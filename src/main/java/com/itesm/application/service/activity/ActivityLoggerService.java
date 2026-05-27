package com.itesm.application.service.activity;

import com.itesm.domain.repository.SystemActivityLogRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class ActivityLoggerService {

    private final SystemActivityLogRepository systemActivityLogRepository;

    public ActivityLoggerService(SystemActivityLogRepository systemActivityLogRepository) {
        this.systemActivityLogRepository = systemActivityLogRepository;
    }

    public void logSuccess(
            UUID userId,
            String action,
            String module,
            String detail
    ) {
        systemActivityLogRepository.create(
                userId,
                action,
                module,
                ActivityResults.SUCCESS,
                detail
        );
    }

    public void logWarning(
            UUID userId,
            String action,
            String module,
            String detail
    ) {
        systemActivityLogRepository.create(
                userId,
                action,
                module,
                ActivityResults.WARNING,
                detail
        );
    }

    public void logError(
            UUID userId,
            String action,
            String module,
            String detail
    ) {
        systemActivityLogRepository.create(
                userId,
                action,
                module,
                ActivityResults.ERROR,
                detail
        );
    }
}