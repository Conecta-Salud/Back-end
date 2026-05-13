package com.itesm.application.dto.dashboard.summary;

import java.util.List;

public class DashboardRankingDto {

    private String title;
    private List<DashboardRankingColumnDto> columns;
    private List<DashboardRankingRowDto> rows;

    public DashboardRankingDto(
            String title,
            List<DashboardRankingColumnDto> columns,
            List<DashboardRankingRowDto> rows
    ) {
        this.title = title;
        this.columns = columns;
        this.rows = rows;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<DashboardRankingColumnDto> getColumns() {
        return columns;
    }

    public void setColumns(List<DashboardRankingColumnDto> columns) {
        this.columns = columns;
    }

    public List<DashboardRankingRowDto> getRows() {
        return rows;
    }

    public void setRows(List<DashboardRankingRowDto> rows) {
        this.rows = rows;
    }
}
