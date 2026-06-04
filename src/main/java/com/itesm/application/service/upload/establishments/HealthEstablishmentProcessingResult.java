package com.itesm.application.service.upload.establishments;

public record HealthEstablishmentProcessingResult(
        int filesProcessed,
        int dataRows,
        int skippedRows,
        int healthUnitsUpserted,
        int catalogValuesChanged,
        int territorialIndicatorsUpserted,
        int errorRecords,
        int warningRecords,
        int coordinateWarnings
) {
    public HealthEstablishmentProcessingResult add(HealthEstablishmentProcessingResult other) {
        return new HealthEstablishmentProcessingResult(
                filesProcessed + other.filesProcessed(),
                dataRows + other.dataRows(),
                skippedRows + other.skippedRows(),
                healthUnitsUpserted + other.healthUnitsUpserted(),
                catalogValuesChanged + other.catalogValuesChanged(),
                territorialIndicatorsUpserted + other.territorialIndicatorsUpserted(),
                errorRecords + other.errorRecords(),
                warningRecords + other.warningRecords(),
                coordinateWarnings + other.coordinateWarnings()
        );
    }
}
