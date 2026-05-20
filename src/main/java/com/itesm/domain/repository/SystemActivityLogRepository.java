package com.itesm.domain.repository;

import com.itesm.domain.models.admin.activity.SystemActivityLog;
import com.itesm.domain.models.common.PageResult;

import java.time.LocalDateTime;
import java.util.UUID;

public interface SystemActivityLogRepository {
    void create(
            UUID userId,
            String action,
            String module,
            String result,
            String detail
    );

    PageResult<SystemActivityLog> findActivityLogs(
            String query,
            String action,
            String module,
            String result,
            LocalDateTime from,
            LocalDateTime to,
            int page,
            int size
    );
}
