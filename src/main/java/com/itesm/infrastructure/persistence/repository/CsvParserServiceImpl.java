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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

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
                                .build()
                )
        ) {

            for (CSVRecord record : csvParser) {

                // ===== STATE =====

                String stateName = record.get("ENTIDAD").trim();
                String stateCode = record.get("CLAVE DE LA ENTIDAD").trim();

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
                String municipalityCode = record.get("CLAVE DEL MUNICIPIO").trim();

                Municipality municipality = municipalitiesMap.computeIfAbsent(
                        municipalityCode,
                        key -> {
                            Municipality m = new Municipality();
                            m.setName(municipalityName);
                            m.setInegiCode(municipalityCode);

                            // relación
                            m.setInegiCode(state.getInegiCode());

                            return m;
                        }
                );

                // ===== INSTITUTION =====

                String institutionName =
                        record.get("NOMBRE DE LA INSTITUCIÓN").trim();

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
                        record.get("NOMBRE DE TIPOLOGÍA").trim();

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
                                record.get("NIVEL ATENCIÓN")
                        )
                );

                // relaciones

                healthUnit.setMunicipalityId(
                        Integer.valueOf(municipality.getInegiCode())
                );

                healthUnit.setMunicipalityName(
                        municipality.getName()
                );

                healthUnit.setStateId(
                        Integer.valueOf(state.getInegiCode())
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

    private Reader createReader(String fileOrContent) throws IOException {
        Path candidatePath = Path.of(fileOrContent);
        if (Files.exists(candidatePath)) {
            return Files.newBufferedReader(candidatePath, StandardCharsets.UTF_8);
        }

        return new StringReader(fileOrContent);
    }
}