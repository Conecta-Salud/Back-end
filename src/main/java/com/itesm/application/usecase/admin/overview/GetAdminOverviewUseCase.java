package com.itesm.application.usecase.admin.overview;

import com.itesm.application.dto.admin.overview.AdminOverviewResponseDto;
import com.itesm.domain.models.admin.overview.AdminOverviewMetrics;
import com.itesm.domain.repository.AdminOverviewRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GetAdminOverviewUseCase {

    private final AdminOverviewRepository adminOverviewRepository;

    public GetAdminOverviewUseCase(AdminOverviewRepository adminOverviewRepository) {
        this.adminOverviewRepository = adminOverviewRepository;
    }

    public AdminOverviewResponseDto execute() {
        AdminOverviewMetrics metrics = adminOverviewRepository.getOverviewMetrics();

        return new AdminOverviewResponseDto(
                metrics.getRegisteredUsers(),
                metrics.getActiveUsersLast7Days(),
                metrics.getComparisonsPerformed(),
                metrics.getCompletedUploadBatches()
        );
    }
}