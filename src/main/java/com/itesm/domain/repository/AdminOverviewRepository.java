package com.itesm.domain.repository;

import com.itesm.domain.models.admin.overview.AdminOverviewMetrics;

public interface AdminOverviewRepository {
    AdminOverviewMetrics getOverviewMetrics();
}
