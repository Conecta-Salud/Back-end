package com.itesm.domain.models.dashboard.summary;

import java.util.List;

public class DashboardRanking {

    private String title;
    private List<DashboardRankingColumn> columns;
    private List<DashboardRankingRow> rows;

    public DashboardRanking(
            String title,
            List<DashboardRankingColumn> columns,
            List<DashboardRankingRow> rows
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

    public List<DashboardRankingColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<DashboardRankingColumn> columns) {
        this.columns = columns;
    }

    public List<DashboardRankingRow> getRows() {
        return rows;
    }

    public void setRows(List<DashboardRankingRow> rows) {
        this.rows = rows;
    }
}
