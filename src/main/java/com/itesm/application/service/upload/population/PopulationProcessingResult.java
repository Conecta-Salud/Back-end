package com.itesm.application.service.upload.population;

public record PopulationProcessingResult(
        int records,
        int valuesUpserted,
        int errors
) {
    public PopulationProcessingResult add(PopulationProcessingResult other) {
        return new PopulationProcessingResult(
                records + other.records(),
                valuesUpserted + other.valuesUpserted(),
                errors + other.errors()
        );
    }
}
