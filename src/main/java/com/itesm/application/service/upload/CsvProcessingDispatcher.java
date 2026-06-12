package com.itesm.application.service.upload;

import com.itesm.application.service.upload.establishments.HealthEstablishmentProcessingResult;
import com.itesm.application.service.upload.establishments.HealthEstablishmentsCsvProcessor;
import com.itesm.application.service.upload.population.PopulationIndicatorsCsvProcessor;
import com.itesm.application.service.upload.population.PopulationProcessingResult;
import com.itesm.application.service.upload.sectorial.HealthSectorialCsvProcessor;
import com.itesm.application.service.upload.sectorial.HealthSectorialProcessingResult;
import com.itesm.domain.models.upload.UploadProcessingMode;
import com.itesm.domain.models.upload.UploadStatus;
import com.itesm.domain.repository.DataUploadRepository;
import com.itesm.infrastructure.persistence.entity.DataUploadEntity;
import com.itesm.infrastructure.persistence.entity.UploadBatchEntity;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class CsvProcessingDispatcher {

    private static final String FILE_COUNT_LABEL = " archivo(s), ";
    private static final String ERRORS_LABEL = " errores.";

    // Punto unico de enrutamiento: el sourceType del lote decide que procesador CSV
    // interpreta los archivos y que tablas/indicadores se actualizan.
    private final DataUploadRepository dataUploadRepository;
    private final PopulationIndicatorsCsvProcessor populationIndicatorsCsvProcessor;
    private final HealthEstablishmentsCsvProcessor healthEstablishmentsCsvProcessor;
    private final HealthSectorialCsvProcessor healthSectorialCsvProcessor;

    public CsvProcessingDispatcher(
            DataUploadRepository dataUploadRepository,
            PopulationIndicatorsCsvProcessor populationIndicatorsCsvProcessor,
            HealthEstablishmentsCsvProcessor healthEstablishmentsCsvProcessor,
            HealthSectorialCsvProcessor healthSectorialCsvProcessor
    ) {
        this.dataUploadRepository = dataUploadRepository;
        this.populationIndicatorsCsvProcessor = populationIndicatorsCsvProcessor;
        this.healthEstablishmentsCsvProcessor = healthEstablishmentsCsvProcessor;
        this.healthSectorialCsvProcessor = healthSectorialCsvProcessor;
    }

    public CsvProcessingResult dispatch(
            UploadBatchEntity batch,
            UploadProcessingMode mode,
            boolean replaceExistingForYear
    ) {
        List<DataUploadEntity> uploads = dataUploadRepository.findByBatchId(batch.getId());

        CsvProcessingResult result = switch (batch.getSourceType()) {
            case population -> processPopulation(batch, uploads, mode, replaceExistingForYear);
            case health_sectorial -> processHealthSectorial(batch, uploads, mode, replaceExistingForYear);
            case health_establishments -> processHealthEstablishments(batch, uploads, mode, replaceExistingForYear);
        };

        return new CsvProcessingResult(
                result.status(),
                result.message()
                        + " Modo=" + processingModeLabel(mode)
                        + ", reemplazar registros del año=" + yesNo(replaceExistingForYear)
        );
    }

    private CsvProcessingResult processPopulation(
            UploadBatchEntity batch,
            List<DataUploadEntity> uploads,
            UploadProcessingMode mode,
            boolean replaceExistingForYear
    ) {
        PopulationProcessingResult result = populationIndicatorsCsvProcessor.process(
                batch,
                uploads,
                mode,
                replaceExistingForYear
        );

        return new CsvProcessingResult(
                statusFor(result),
            "Datos poblacionales procesados: "
                + result.filesProcessed()
                + FILE_COUNT_LABEL
                + result.dataRows()
                + " filas de datos, "
                + result.skippedRows()
                + " filas de metadatos omitidas, "
                + result.unsupportedPeriodRows()
                + " filas con periodo no soportado omitidas, "
                + result.valuesUpserted()
                + " valores insertados/actualizados, "
                + result.errorRecords()
                + ERRORS_LABEL
        );
    }

    private CsvProcessingResult processHealthEstablishments(
            UploadBatchEntity batch,
            List<DataUploadEntity> uploads,
            UploadProcessingMode mode,
            boolean replaceExistingForYear
    ) {
        HealthEstablishmentProcessingResult result = healthEstablishmentsCsvProcessor.process(
                batch,
                uploads,
                mode,
                replaceExistingForYear
        );

        return new CsvProcessingResult(
                statusFor(result),
            "Establecimientos procesados: "
                + result.filesProcessed()
                + FILE_COUNT_LABEL
                + result.dataRows()
                + " filas, "
                + result.skippedRows()
                + " filas omitidas, "
                + result.healthUnitsUpserted()
                + " unidades de salud insertadas/actualizadas, "
                + result.catalogValuesChanged()
                + " valores de catálogo creados/actualizados, "
                + result.territorialIndicatorsUpserted()
                + " indicadores territoriales actualizados, "
                + result.coordinateWarnings()
                + " advertencias de coordenadas, "
                + result.warningRecords()
                + " advertencias no bloqueantes, "
                + result.errorRecords()
                + ERRORS_LABEL
        );
    }

    private CsvProcessingResult processHealthSectorial(
            UploadBatchEntity batch,
            List<DataUploadEntity> uploads,
            UploadProcessingMode mode,
            boolean replaceExistingForYear
    ) {
        HealthSectorialProcessingResult result = healthSectorialCsvProcessor.process(
                batch,
                uploads,
                mode,
                replaceExistingForYear
        );

        return new CsvProcessingResult(
                statusFor(result),
            "Datos sectoriales procesados: "
                + result.filesProcessed()
                + FILE_COUNT_LABEL
                + result.dataRows()
                + " filas, "
                + result.skippedRows()
                + " filas omitidas, "
                + result.staffRowsUpserted()
                + " registros de personal insertados/actualizados, "
                + result.specialtyRowsUpserted()
                + " registros de especialidades insertados/actualizados, "
                + result.infrastructureRowsUpserted()
                + " registros de infraestructura insertados/actualizados, "
                + result.infrastructureDetailRowsUpserted()
                + " detalles de infraestructura insertados/actualizados, "
                + result.minimalHealthUnitsCreated()
                + " unidades mínimas de salud creadas, "
                + result.territorialIndicatorsUpserted()
                + " indicadores territoriales actualizados, "
                + result.errorRecords()
                + ERRORS_LABEL
        );
    }

    private String processingModeLabel(UploadProcessingMode mode) {
        return switch (mode) {
            case validate_only -> "solo validar";
            case upsert -> "insertar/actualizar";
            case replace -> "reemplazar";
        };
    }

    private String yesNo(boolean value) {
        return value ? "sí" : "no";
    }

    private UploadStatus statusFor(PopulationProcessingResult result) {
        if (result.errorRecords() == 0) {
            return UploadStatus.completed;
        }

        // Si hubo errores pero tambien se persistieron datos, el lote queda en warning:
        // el admin puede revisar errores sin perder los registros validos.
        return result.valuesUpserted() > 0 ? UploadStatus.warning : UploadStatus.error;
    }

    private UploadStatus statusFor(HealthEstablishmentProcessingResult result) {
        if (result.errorRecords() == 0 && result.warningRecords() == 0) {
            return UploadStatus.completed;
        }

        if (result.errorRecords() == 0) {
            return UploadStatus.warning;
        }

        return result.healthUnitsUpserted() > 0 ? UploadStatus.warning : UploadStatus.error;
    }

    private UploadStatus statusFor(HealthSectorialProcessingResult result) {
        if (result.errorRecords() == 0) {
            return UploadStatus.completed;
        }

        return result.staffRowsUpserted() > 0 ? UploadStatus.warning : UploadStatus.error;
    }
}
