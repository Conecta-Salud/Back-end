package com.itesm.application.service.upload.population;

public class PopulationIndicatorsCsvRow {

    private final int csvRowNumber;
    private final String periodRaw;
    private final String geographicAreaRaw;
    private final String totalPopulationRaw;
    private final String percentageOver60Raw;
    private final String healthcareAccessDeficiencyRaw;
    private final String totalPovertyPopulationRaw;

    public PopulationIndicatorsCsvRow(
            int csvRowNumber,
            String periodRaw,
            String geographicAreaRaw,
            String totalPopulationRaw,
            String percentageOver60Raw,
            String healthcareAccessDeficiencyRaw,
            String totalPovertyPopulationRaw
    ) {
        this.csvRowNumber = csvRowNumber;
        this.periodRaw = periodRaw;
        this.geographicAreaRaw = geographicAreaRaw;
        this.totalPopulationRaw = totalPopulationRaw;
        this.percentageOver60Raw = percentageOver60Raw;
        this.healthcareAccessDeficiencyRaw = healthcareAccessDeficiencyRaw;
        this.totalPovertyPopulationRaw = totalPovertyPopulationRaw;
    }

    public int getCsvRowNumber() {
        return csvRowNumber;
    }

    public String getPeriodRaw() {
        return periodRaw;
    }

    public String getGeographicAreaRaw() {
        return geographicAreaRaw;
    }

    public String getTotalPopulationRaw() {
        return totalPopulationRaw;
    }

    public String getPercentageOver60Raw() {
        return percentageOver60Raw;
    }

    public String getHealthcareAccessDeficiencyRaw() {
        return healthcareAccessDeficiencyRaw;
    }

    public String getTotalPovertyPopulationRaw() {
        return totalPovertyPopulationRaw;
    }
}
