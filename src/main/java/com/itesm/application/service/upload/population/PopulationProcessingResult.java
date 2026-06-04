package com.itesm.application.service.upload.population;

public record PopulationProcessingResult(
        int filesProcessed,
        int dataRows,
        int skippedRows,
        int unsupportedPeriodRows,
        int valuesUpserted,
        int errorRecords
) {
    public PopulationProcessingResult add(PopulationProcessingResult other) {
        return new PopulationProcessingResult(
                filesProcessed + other.filesProcessed(),
                dataRows + other.dataRows(),
                skippedRows + other.skippedRows(),
                unsupportedPeriodRows + other.unsupportedPeriodRows(),
                valuesUpserted + other.valuesUpserted(),
                errorRecords + other.errorRecords()
        );
    }
}
