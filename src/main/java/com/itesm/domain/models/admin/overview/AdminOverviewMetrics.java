package com.itesm.domain.models.admin.overview;

public class AdminOverviewMetrics {

    private Long registeredUsers;
    private Long activeUsersLast7Days;
    private Long comparisonsPerformed;
    private Long exportedReports;

    public AdminOverviewMetrics(
            Long registeredUsers,
            Long activeUsersLast7Days,
            Long comparisonsPerformed,
            Long exportedReports
    ) {
        this.registeredUsers = registeredUsers;
        this.activeUsersLast7Days = activeUsersLast7Days;
        this.comparisonsPerformed = comparisonsPerformed;
        this.exportedReports = exportedReports;
    }

    public Long getRegisteredUsers() {
        return registeredUsers;
    }

    public Long getActiveUsersLast7Days() {
        return activeUsersLast7Days;
    }

    public Long getComparisonsPerformed() {
        return comparisonsPerformed;
    }

    public Long getExportedReports() {
        return exportedReports;
    }
}