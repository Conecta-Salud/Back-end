package com.itesm.application.service.upload.sectorial;

public record HealthSectorialProcessingResult(
        int filesProcessed,
        int dataRows,
        int skippedRows,
        int validRecords,
        int staffRowsUpserted,
        int specialtyRowsUpserted,
        int infrastructureRowsUpserted,
        int infrastructureDetailRowsUpserted,
        int minimalHealthUnitsCreated,
        int territorialIndicatorsUpserted,
        int errorRecords
) {
    public HealthSectorialProcessingResult add(HealthSectorialProcessingResult other) {
        return new HealthSectorialProcessingResult(
                filesProcessed + other.filesProcessed(),
                dataRows + other.dataRows(),
                skippedRows + other.skippedRows(),
                validRecords + other.validRecords(),
                staffRowsUpserted + other.staffRowsUpserted(),
                specialtyRowsUpserted + other.specialtyRowsUpserted(),
                infrastructureRowsUpserted + other.infrastructureRowsUpserted(),
                infrastructureDetailRowsUpserted + other.infrastructureDetailRowsUpserted(),
                minimalHealthUnitsCreated + other.minimalHealthUnitsCreated(),
                territorialIndicatorsUpserted + other.territorialIndicatorsUpserted(),
                errorRecords + other.errorRecords()
        );
    }
}
