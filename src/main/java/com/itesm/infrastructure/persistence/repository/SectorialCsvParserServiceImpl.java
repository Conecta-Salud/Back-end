package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.repository.SectorialCsvParserService;
import com.itesm.infrastructure.persistence.entity.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;

@ApplicationScoped
public class SectorialCsvParserServiceImpl implements SectorialCsvParserService {

    @Inject
    EntityManager em;

    @Override
    @Transactional
    public void parse(String fileOrContent) {
        try {
            Reader reader = createReader(fileOrContent);
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());

            for (CSVRecord record : csvParser) {
                processRecord(record);
            }

            csvParser.close();
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException("Error parsing sectorial CSV: " + e.getMessage(), e);
        }
    }

    private void processRecord(CSVRecord record) throws IOException {
        String clues = record.get("CLUES");
        String ano = record.get("AÑO");

        // Find health unit and period
        HealthUnitEntity healthUnit = findHealthUnitByClues(clues);
        PeriodEntity period = findPeriodByYear(Integer.parseInt(ano));

        if (healthUnit == null || period == null) {
            System.out.println("Skipping record: CLUES=" + clues + ", AÑO=" + ano);
            return;
        }

        // Process staff data
        processStaffData(record, healthUnit, period);

        // Process infrastructure data
        processInfrastructureData(record, healthUnit, period);
    }

    private void processStaffData(CSVRecord record, HealthUnitEntity healthUnit, PeriodEntity period) throws IOException {
        Integer totalDoctors = 0;
        Integer totalNurses = 0;

        // Get total doctors from "Total médicos generales especialistas y odonólogos" (col 139)
        try {
            String docValue = record.get("Total médicos generales especialistas y odonólogos");
            if (docValue != null && !docValue.isEmpty()) {
                totalDoctors = Integer.parseInt(docValue);
            }
        } catch (Exception e) {
            // Continue if value not found
        }

        // Get total nurses from "Total enfermeras en contacto con el paciente" (col 187)
        try {
            String nurseValue = record.get("Total enfermeras en contacto con el paciente");
            if (nurseValue != null && !nurseValue.isEmpty()) {
                totalNurses = Integer.parseInt(nurseValue);
            }
        } catch (Exception e) {
            // Continue if value not found
        }

        // Find or create HealthUnitStaff
        HealthUnitStaffEntity staffEntity = em.createQuery(
                "SELECT s FROM HealthUnitStaffEntity s WHERE s.healthUnit.id = :healthUnitId AND s.period.id = :periodId",
                HealthUnitStaffEntity.class)
                .setParameter("healthUnitId", healthUnit.getId())
                .setParameter("periodId", period.getId())
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);

        if (staffEntity == null) {
            staffEntity = new HealthUnitStaffEntity();
            staffEntity.setHealthUnit(healthUnit);
            staffEntity.setPeriod(period);
        }

        staffEntity.setTotalDoctors(totalDoctors);
        staffEntity.setTotalNurses(totalNurses);
        staffEntity.setSourceFile("BD ABIERTOS SECTORIAL 2024.csv");

        em.persist(staffEntity);
    }

    private void processInfrastructureData(CSVRecord record, HealthUnitEntity healthUnit, PeriodEntity period) throws IOException {
        // Find or create HealthUnitInfrastructure record
        HealthUnitInfrastructureEntity infraEntity = em.createQuery(
                "SELECT i FROM HealthUnitInfrastructureEntity i WHERE i.healthUnit.id = :healthUnitId AND i.period.id = :periodId",
                HealthUnitInfrastructureEntity.class)
                .setParameter("healthUnitId", healthUnit.getId())
                .setParameter("periodId", period.getId())
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);

        if (infraEntity == null) {
            infraEntity = new HealthUnitInfrastructureEntity();
            infraEntity.setHealthUnit(healthUnit);
            infraEntity.setPeriod(period);
            infraEntity.setSourceFile("BD ABIERTOS SECTORIAL 2024.csv");

            em.persist(infraEntity);
        }

        // Process infrastructure detail columns
        processInfrastructureDetails(record, infraEntity);
    }

    private void processInfrastructureDetails(CSVRecord record, HealthUnitInfrastructureEntity infraEntity) throws IOException {
        // Map of CSV column names to InfrastructureType codes
        Map<String, String> infrastructureMapping = new HashMap<>();
        infrastructureMapping.put("Quirófanos", "quirofanos");
        infrastructureMapping.put("salas de expulsión", "salas_expulsion");
        infrastructureMapping.put("cunas para recién nacido sano (incluye las ubicadas en tocología como en neonatología y cuneros)", "cunas_recien_nacido");
        infrastructureMapping.put("ambulancias", "ambulancias");
        infrastructureMapping.put("TOTAL DE CONSULTORIOS", "total_consultorios");
        infrastructureMapping.put("TOTAL CAMAS AREA HOSPITALIZACIÓN", "total_camas_hospitalizacion");
        infrastructureMapping.put("Aceleradores lineales", "aceleradores_lineales");
        infrastructureMapping.put("Cunas de calor radiante", "cunas_calor_radiante");
        infrastructureMapping.put("Incubadoras", "incubadoras");
        infrastructureMapping.put("Equipos de resonancia magnética", "equipos_resonancia");
        infrastructureMapping.put("Tomógrafos computados", "tomografos");
        infrastructureMapping.put("Ultrasonido", "ultrasonido");

        for (Map.Entry<String, String> entry : infrastructureMapping.entrySet()) {
            try {
                String columnName = entry.getKey();
                String code = entry.getValue();
                String value = record.get(columnName);

                if (value != null && !value.isEmpty()) {
                    Integer quantity = Integer.parseInt(value);

                    InfrastructureTypeEntity infraType = em.createQuery(
                            "SELECT t FROM InfrastructureTypeEntity t WHERE t.code = :code",
                            InfrastructureTypeEntity.class)
                            .setParameter("code", code)
                            .getResultList()
                            .stream()
                            .findFirst()
                            .orElse(null);

                    if (infraType != null) {
                        HealthUnitInfrastructureDetailEntity detail = em.createQuery(
                                "SELECT d FROM HealthUnitInfrastructureDetailEntity d WHERE d.healthUnitInfrastructure.id = :infraId AND d.infrastructureType.id = :typeId",
                                HealthUnitInfrastructureDetailEntity.class)
                                .setParameter("infraId", infraEntity.getId())
                                .setParameter("typeId", infraType.getId())
                                .getResultList()
                                .stream()
                                .findFirst()
                                .orElse(null);

                        if (detail == null) {
                            detail = new HealthUnitInfrastructureDetailEntity();
                            detail.setHealthUnitInfrastructure(infraEntity);
                            detail.setInfrastructureType(infraType);
                        }

                        detail.setQuantity(quantity);
                        em.persist(detail);
                    }
                }
            } catch (Exception e) {
                // Skip if column or parsing fails
            }
        }
    }

    private HealthUnitEntity findHealthUnitByClues(String clues) {
        return em.createQuery("SELECT h FROM HealthUnitEntity h WHERE h.clave = :clave", HealthUnitEntity.class)
                .setParameter("clave", clues)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
    }

    private PeriodEntity findPeriodByYear(Integer year) {
        return em.createQuery("SELECT p FROM PeriodEntity p WHERE p.ano = :ano", PeriodEntity.class)
                .setParameter("ano", year)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
    }

    private Reader createReader(String fileOrContent) throws IOException {
        File file = new File(fileOrContent);
        if (file.exists()) {
            return new InputStreamReader(Files.newInputStream(file.toPath()), Charset.forName("latin-1"));
        } else {
            return new StringReader(fileOrContent);
        }
    }
}
