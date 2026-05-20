package com.itesm.application.usecase.admin.activity;

import com.itesm.application.dto.admin.activity.ActivityLogResponseDto;
import com.itesm.application.dto.common.PageResponseDto;
import com.itesm.domain.models.admin.activity.SystemActivityLog;
import com.itesm.domain.models.common.PageResult;
import com.itesm.domain.repository.SystemActivityLogRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ApplicationScoped
public class FindActivityLogsUseCase {

    private final SystemActivityLogRepository systemActivityLogRepository;

    public FindActivityLogsUseCase(SystemActivityLogRepository systemActivityLogRepository) {
        this.systemActivityLogRepository = systemActivityLogRepository;
    }

    public PageResponseDto<ActivityLogResponseDto> execute(
            String query,
            String action,
            String module,
            String result,
            LocalDateTime from,
            LocalDateTime to,
            int page,
            int size
    ) {
        PageResult<SystemActivityLog> logs = systemActivityLogRepository.findActivityLogs(
                query,
                action,
                module,
                result,
                from,
                to,
                page,
                size
        );

        return new PageResponseDto<>(
                logs.getItems()
                        .stream()
                        .map(this::toDto)
                        .collect(Collectors.toList()),
                logs.getTotalItems(),
                logs.getPage(),
                logs.getSize(),
                logs.getTotalPages()
        );
    }

    private ActivityLogResponseDto toDto(SystemActivityLog log) {
        return new ActivityLogResponseDto(
                log.getId(),
                log.getUserId(),
                log.getUserEmail(),
                log.getUserFullName(),
                log.getAction(),
                log.getModule(),
                log.getResult(),
                log.getDetail(),
                log.getCreatedAt()
        );
    }
}