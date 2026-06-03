package com.itesm.infrastructure.persistence.repository;

import com.itesm.application.dto.Uploader.Auxiliar.CsvIndicatorData;
import com.itesm.domain.models.Uploader.Auxiliar.AvailabilityStatus;
import com.itesm.domain.models.Uploader.Auxiliar.TerritoryLevel;
import com.itesm.domain.models.Uploader.indicator.TerritoryIndicatorValues;
import com.itesm.domain.repository.IndicatorCsvParserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@ApplicationScoped
public class IndicatorCsvParserServiceImpl implements IndicatorCsvParserService {

    @Inject
    EntityManager em;

    @Override
    public CsvIndicatorData parse(String fileOrContent) {
        List<TerritoryIndicatorValues> indicatorValues = new ArrayList<>();

        try (Reader reader = createReader(fileOrContent);
             CSVParser csvParser = new CSVParser(
                     reader,
                     CSVFormat.DEFAULT
                             .builder()
                             .setHeader()
                             .setSkipHeaderRecord(true)
                             .build()
             )
        ) {

            List<String> headerNames = csvParser.getHeaderNames();
            Map<Integer, Integer> indicatorColumnIndexes = new HashMap<>();
            Map<Integer, Integer> indicatorIdsByColumn = new HashMap<>();
            Map<Integer, Integer> dataSourceIdsByColumn = new HashMap<>();

            for (int columnIndex = 0; columnIndex < headerNames.size(); columnIndex++) {
                if (columnIndex < 2) {
                    continue;
                }

                String header = headerNames.get(columnIndex);
                if (header == null || header.isBlank()) {
                    continue;
                }

                String normalizedHeader = normalizeHeader(header);
                String indicatorCode = mapHeaderToIndicatorCode(normalizedHeader);
                if (indicatorCode == null) {
                    throw new RuntimeException("Unrecognized indicator header: " + header);
                }

                Integer indicatorId = findIndicatorIdByCode(indicatorCode);
                Integer dataSourceId = findDataSourceIdByIndicatorCode(indicatorCode);

                indicatorColumnIndexes.put(columnIndex, columnIndex);
                indicatorIdsByColumn.put(columnIndex, indicatorId);
                dataSourceIdsByColumn.put(columnIndex, dataSourceId);
            }

            for (CSVRecord record : csvParser) {
                String periodValue = record.get(0).trim();
                String territoryValue = record.get(1).trim();

                if (periodValue.isBlank() || territoryValue.isBlank()) {
                    continue;
                }

                Short analysisYear = parseYear(periodValue);
                TerritoryLocation location = parseTerritory(territoryValue);
                String sourceFile = determineSourceFileName(fileOrContent);

                for (Map.Entry<Integer, Integer> entry : indicatorColumnIndexes.entrySet()) {
                    int columnIndex = entry.getKey();
                    String rawValue = record.get(columnIndex).trim();
                    if (rawValue.isBlank()) {
                        continue;
                    }

                    TerritoryIndicatorValues indicatorValue = new TerritoryIndicatorValues();
                    indicatorValue.setTerritoryLevel(location.getTerritoryLevel());
                    indicatorValue.setStateId(location.getStateId());
                    indicatorValue.setMunicipalityId(location.getMunicipalityId());
                    indicatorValue.setIndicatorId(indicatorIdsByColumn.get(columnIndex));
                    indicatorValue.setValue(parseBigDecimal(rawValue));
                    indicatorValue.setAnalysisYear(analysisYear);
                    indicatorValue.setSourceYear(analysisYear);
                    indicatorValue.setDataSourceId(dataSourceIdsByColumn.get(columnIndex));
                    indicatorValue.setSourceFile(sourceFile);
                    indicatorValue.setAvailabilityStatus(AvailabilityStatus.available);
                    indicatorValue.setMethodologyNote(null);

                    indicatorValues.add(indicatorValue);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Error parsing indicator CSV", e);
        }

        return new CsvIndicatorData(indicatorValues);
    }

    private Reader createReader(String fileOrContent) throws IOException {
        Path candidatePath = Path.of(fileOrContent);
        if (Files.exists(candidatePath)) {
            return Files.newBufferedReader(candidatePath, StandardCharsets.UTF_16LE);
        }

        return new StringReader(fileOrContent);
    }

    private String determineSourceFileName(String fileOrContent) {
        Path candidatePath = Path.of(fileOrContent);
        if (Files.exists(candidatePath)) {
            return candidatePath.getFileName().toString();
        }
        return null;
    }

    private Short parseYear(String periodValue) {
        String safe = periodValue.replaceAll("[^0-9]", "");
        if (safe.isBlank()) {
            throw new RuntimeException("Invalid period value: " + periodValue);
        }
        return Short.valueOf(safe);
    }

    private TerritoryLocation parseTerritory(String territoryValue) {
        String trimmed = territoryValue.trim();
        int firstSpace = trimmed.indexOf(' ');
        String codePart = firstSpace > 0 ? trimmed.substring(0, firstSpace) : trimmed;
        String numericCode = codePart.replaceAll("[^0-9]", "");

        if (numericCode.isBlank()) {
            throw new RuntimeException("Unable to parse territory code: " + territoryValue);
        }

        if (numericCode.equals("00")) {
            return new TerritoryLocation(TerritoryLevel.country, null, null);
        }

        if (numericCode.length() == 2) {
            return new TerritoryLocation(TerritoryLevel.state, Integer.valueOf(numericCode), null);
        }

        if (numericCode.length() >= 3) {
            Integer stateId = Integer.valueOf(numericCode.substring(0, 2));
            Integer municipalityId = Integer.valueOf(numericCode);
            return new TerritoryLocation(TerritoryLevel.municipality, stateId, municipalityId);
        }

        throw new RuntimeException("Unsupported territory code length: " + territoryValue);
    }

    private String mapHeaderToIndicatorCode(String normalizedHeader) {
        if (normalizedHeader.contains("poblacion total")) {
            return "total_population";
        }

        if (normalizedHeader.contains("porcentaje") && normalizedHeader.contains("60")) {
            return "percentage_over_60";
        }

        if (normalizedHeader.contains("carencia") && normalizedHeader.contains("servicios de salud")) {
            return "healthcare_access_deficiency";
        }

        if (normalizedHeader.contains("poblacion en situacion de pobreza") || normalizedHeader.contains("poblacion en situacion de pobreza")) {
            return "total_poverty_population";
        }

        return null;
    }

    private Integer findIndicatorIdByCode(String indicatorCode) {
        List<Integer> result = em.createQuery(
                        "SELECT i.id FROM IndicatorsEntity i WHERE i.code = :code",
                        Integer.class
                )
                .setParameter("code", indicatorCode)
                .getResultList();

        if (result.isEmpty()) {
            throw new RuntimeException("No indicator found for code: " + indicatorCode);
        }

        return result.get(0);
    }

    private Integer findDataSourceIdByIndicatorCode(String indicatorCode) {
        String dataSourceCode;

        return switch (indicatorCode) {
            case "total_population", "percentage_over_60" -> findDataSourceIdByCode("inegi_population");
            case "healthcare_access_deficiency" -> findDataSourceIdByCode("coneval_healthcare_deficiency");
            case "total_poverty_population" -> findDataSourceIdByCode("coneval_poverty");
            default -> throw new RuntimeException("No data source mapping for indicator code: " + indicatorCode);
        };
    }

    private Integer findDataSourceIdByCode(String dataSourceCode) {
        List<Integer> result = em.createQuery(
                        "SELECT d.id FROM DataSourceEntity d WHERE d.code = :code",
                        Integer.class
                )
                .setParameter("code", dataSourceCode)
                .getResultList();

        if (result.isEmpty()) {
            throw new RuntimeException("No data source found for code: " + dataSourceCode);
        }

        return result.get(0);
    }

    private BigDecimal parseBigDecimal(String rawValue) {
        String safeValue = rawValue.replaceAll("[^0-9.,-]", "").replace(',', '.');
        if (safeValue.isBlank()) {
            return null;
        }

        return new BigDecimal(safeValue);
    }

    private String normalizeHeader(String header) {
        String normalized = header.toLowerCase(Locale.ROOT);
        normalized = normalized.replace("á", "a");
        normalized = normalized.replace("é", "e");
        normalized = normalized.replace("í", "i");
        normalized = normalized.replace("ó", "o");
        normalized = normalized.replace("ú", "u");
        normalized = normalized.replace("ñ", "n");
        normalized = normalized.replace("ü", "u");
        normalized = normalized.replaceAll("[^a-z0-9 ]+", " ");
        normalized = normalized.replaceAll("\\s+", " ");
        return normalized.trim();
    }

    private static class TerritoryLocation {
        private final TerritoryLevel territoryLevel;
        private final Integer stateId;
        private final Integer municipalityId;

        public TerritoryLocation(TerritoryLevel territoryLevel, Integer stateId, Integer municipalityId) {
            this.territoryLevel = territoryLevel;
            this.stateId = stateId;
            this.municipalityId = municipalityId;
        }

        public TerritoryLevel getTerritoryLevel() {
            return territoryLevel;
        }

        public Integer getStateId() {
            return stateId;
        }

        public Integer getMunicipalityId() {
            return municipalityId;
        }
    }
}
