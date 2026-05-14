package com.itesm.application.dto.comparison.summary;

import java.math.BigDecimal;
import java.util.List;

public class ComparisonPriorityResultDto {

    private String territoryCode;
    private String name;
    private String parentName;
    private BigDecimal score;
    private String level;
    private String label;
    private String colorToken;
    private List<ComparisonPriorityFactorDto> factors;

    public ComparisonPriorityResultDto(
            String territoryCode,
            String name,
            String parentName,
            BigDecimal score,
            String level,
            String label,
            String colorToken,
            List<ComparisonPriorityFactorDto> factors
    ) {
        this.territoryCode = territoryCode;
        this.name = name;
        this.parentName = parentName;
        this.score = score;
        this.level = level;
        this.label = label;
        this.colorToken = colorToken;
        this.factors = factors;
    }

    public String getTerritoryCode() {
        return territoryCode;
    }

    public void setTerritoryCode(String territoryCode) {
        this.territoryCode = territoryCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getColorToken() {
        return colorToken;
    }

    public void setColorToken(String colorToken) {
        this.colorToken = colorToken;
    }

    public List<ComparisonPriorityFactorDto> getFactors() {
        return factors;
    }

    public void setFactors(List<ComparisonPriorityFactorDto> factors) {
        this.factors = factors;
    }
}
