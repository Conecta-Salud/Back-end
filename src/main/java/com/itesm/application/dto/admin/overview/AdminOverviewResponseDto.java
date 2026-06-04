package com.itesm.application.dto.admin.overview;

public class AdminOverviewResponseDto {

    private Long registeredUsers;
    private Long activeUsersLast7Days;
    private Long comparisonsPerformed;
    private Long completedUploadBatches;

    public AdminOverviewResponseDto(
            Long registeredUsers,
            Long activeUsersLast7Days,
            Long comparisonsPerformed,
            Long completedUploadBatches
    ) {
        this.registeredUsers = registeredUsers;
        this.activeUsersLast7Days = activeUsersLast7Days;
        this.comparisonsPerformed = comparisonsPerformed;
        this.completedUploadBatches = completedUploadBatches;
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

    public Long getCompletedUploadBatches() {
        return completedUploadBatches;
    }
}