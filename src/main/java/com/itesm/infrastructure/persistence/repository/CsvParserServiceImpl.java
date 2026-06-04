package com.itesm.infrastructure.persistence.repository;

import com.itesm.application.dto.Uploader.Auxiliar.CsvData;
import com.itesm.domain.models.Uploader.Establecimiento.*;
import com.itesm.domain.models.healthunit.CareLevel;
import com.itesm.domain.models.healthunit.HealthUnitSummary;
import com.itesm.domain.repository.CsvParserService;
import jakarta.enterprise.context.ApplicationScoped;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.InvalidPathException;

import java.util.*;

@ApplicationScoped
public class CsvParserServiceImpl implements CsvParserService {

    @Override
    public CsvData parse(String file) {

        Map<String, State> statesMap = new HashMap<>();
        Map<String, Municipality> municipalitiesMap = new HashMap<>();
        Map<String, Institution> institutionsMap = new HashMap<>();
        Map<String, Establishment> establishmentsMap = new HashMap<>();
        Map<String, MedicalUnitTypes> medicalUnitTypesMap = new HashMap<>();

        List<HealthUnitSummary> healthUnitSummaries = new ArrayList<>();

        try (
                Reader reader = createReader(file);
                CSVParser csvParser = new CSVParser(
                        reader,
                        CSVFormat.DEFAULT
                                .builder()
                                .setHeader()
                                .setSkipHeaderRecord(true)
                                .setTrim(true)
                                .setIgnoreSurroundingSpaces(true)
                                .setIgnoreEmptyLines(true)
                                .build()
                )
        ) {

            for (CSVRecord record : csvParser) {
                if (isEmptyRecord(record)) {
                    continue;
                }
                // ===== STATE =====

                String stateName = record.get("ENTIDAD").trim();
                String stateCode = normalizeNumericCode(
                        record.get("CLAVE DE LA ENTIDAD"),
                        2,
                        "CLAVE DE LA ENTIDAD",
                        record.getRecordNumber()
                );

                if (stateCode.isBlank()) {
                    throw new IllegalArgumentException("CLAVE DE LA ENTIDAD está vacía en la fila " + record.getRecordNumber());
                }

                State state = statesMap.computeIfAbsent(
                        stateCode,
                        key -> {
                            State s = new State();
                            s.setName(stateName);
                            s.setInegiCode(stateCode);
                            return s;
                        }
                );

                // ===== MUNICIPALITY =====

                String municipalityName = record.get("MUNICIPIO").trim();
                String municipalityCode = normalizeNumericCode(
                        record.get("CLAVE DEL MUNICIPIO"),
                        3,
                        "CLAVE DEL MUNICIPIO",
                        record.getRecordNumber()
                );

                if (municipalityCode.isBlank()) {
                    throw new IllegalArgumentException("CLAVE DEL MUNICIPIO está vacía en la fila " + record.getRecordNumber());
                }
                String municipalityKey = stateCode + "-" + municipalityCode;
                String municipalityNationalCode = stateCode + municipalityCode;

                Municipality municipality = municipalitiesMap.computeIfAbsent(
                        municipalityKey,
                        key -> {
                            Municipality m = new Municipality();
                            m.setName(municipalityName);
                            m.setInegiCode(municipalityNationalCode);
                            m.setStateInegiCode(stateCode);
                            return m;
                        }
                );


                // ===== INSTITUTION =====

                String institutionName =
                        record.get("NOMBRE DE LA INSTITUCION").trim();

                Institution institution = institutionsMap.computeIfAbsent(
                        institutionName,
                        key -> {
                            Institution i = new Institution();
                            i.setName(institutionName);
                            return i;
                        }
                );

                // ===== ESTABLISHMENT =====

                String establishmentName =
                        record.get("NOMBRE TIPO ESTABLECIMIENTO").trim();

                Establishment establishment = establishmentsMap.computeIfAbsent(
                        establishmentName,
                        key -> {
                            Establishment e = new Establishment();
                            e.setName(establishmentName);
                            return e;
                        }
                );

                // ===== MEDICAL UNIT TYPE =====

                String medicalUnitTypeName =
                        record.get("NOMBRE DE TIPOLOGIA").trim();

                MedicalUnitTypes medicalUnitType =
                        medicalUnitTypesMap.computeIfAbsent(
                                medicalUnitTypeName,
                                key -> {
                                    MedicalUnitTypes m = new MedicalUnitTypes();
                                    m.setName(medicalUnitTypeName);
                                    return m;
                                }
                        );

                // ===== HEALTH UNIT =====

                HealthUnitSummary healthUnit = new HealthUnitSummary();

                healthUnit.setClues(
                        record.get("CLUES").trim()
                );

                healthUnit.setName(
                        record.get("NOMBRE DE LA UNIDAD").trim()
                );

                healthUnit.setCareLevel(
                        mapCareLevel(
                                record.get("NIVEL ATENCION")
                        )
                );

                // relaciones

                healthUnit.setMunicipalityId(
                        Integer.valueOf(municipalityNationalCode)
                );

                healthUnit.setMunicipalityName(
                        municipality.getName()
                );

                healthUnit.setStateId(
                        Integer.valueOf(stateCode)
                );

                healthUnit.setStateName(
                        state.getName()
                );

                healthUnit.setInstitutionName(
                        institution.getName()
                );

                healthUnit.setEstablishmentTypeName(
                        establishment.getName()
                );

                healthUnit.setMedicalUnitTypeName(
                        medicalUnitType.getName()
                );

                healthUnitSummaries.add(healthUnit);
            }

        } catch (IOException e) {
            throw new RuntimeException("Error parsing CSV", e);
        }

        return new CsvData(
                new ArrayList<>(statesMap.values()),
                new ArrayList<>(municipalitiesMap.values()),
                new ArrayList<>(institutionsMap.values()),
                new ArrayList<>(establishmentsMap.values()),
                new ArrayList<>(medicalUnitTypesMap.values()),
                healthUnitSummaries
        );
    }

    private CareLevel mapCareLevel(String csvValue) {
        if (csvValue == null) {
            return CareLevel.not_specified;
        }

        return switch (csvValue.trim().toUpperCase()) {

            case "PRIMER NIVEL" -> CareLevel.primary;

            case "SEGUNDO NIVEL" -> CareLevel.secondary;

            case "TERCER NIVEL" -> CareLevel.tertiary;

            case "NO APLICA" -> CareLevel.not_specified;

            default -> CareLevel.not_specified;
        };
    }

    private String removeBom(String content) {
        if (content != null && content.startsWith("\uFEFF")) {
            return content.substring(1);
        }

        return content;
    }

    private String normalizeNumericCode(String value, int length, String columnName, long recordNumber) {
        if (value == null || value.trim().isBlank()) {
            throw new IllegalArgumentException(columnName + " está vacía en la fila " + recordNumber);
        }

        String digits = value.trim().replaceAll("[^0-9]", "");
        if (digits.isBlank()) {
            throw new IllegalArgumentException(columnName + " no contiene dígitos válidos en la fila " + recordNumber);
        }

        if (digits.length() > length) {
            return digits;
        }

        return String.format("%0" + length + "d", Integer.parseInt(digits));
    }

    private boolean isEmptyRecord(CSVRecord record) {
        for (String value : record) {
            if (value != null && !value.trim().isEmpty()) {
                return false;
            }
        }

        return true;
    }
    private Reader createReader(String fileOrContent) throws IOException {
        if (fileOrContent == null || fileOrContent.isBlank()) {
            throw new IllegalArgumentException("El contenido CSV está vacío.");
        }

        if (fileOrContent.contains("\n") || fileOrContent.contains(",") || fileOrContent.contains(";")) {
            return new StringReader(removeBom(fileOrContent));
        }

        try {
            Path candidatePath = Path.of(fileOrContent);

            if (Files.exists(candidatePath)) {
                return Files.newBufferedReader(candidatePath, StandardCharsets.UTF_8);
            }
        } catch (InvalidPathException ignored) {
            return new StringReader(removeBom(fileOrContent));
        }

        return new StringReader(removeBom(fileOrContent));
    }
}
