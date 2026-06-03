package com.itesm.application.usecase.availability;

import com.itesm.application.dto.availability.DataAvailabilityResponseDto;
import com.itesm.domain.repository.DataAvailabilityRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GetDataAvailabilityUseCase {

    private final DataAvailabilityRepository dataAvailabilityRepository;

    public GetDataAvailabilityUseCase(DataAvailabilityRepository dataAvailabilityRepository) {
        this.dataAvailabilityRepository = dataAvailabilityRepository;
    }

    public DataAvailabilityResponseDto execute(
            String territoryLevel,
            Integer analysisYear,
            String categoryCode
    ) {
        return new DataAvailabilityResponseDto(
                dataAvailabilityRepository.findAvailableAnalysisYears(),
                dataAvailabilityRepository.findAvailability(
                        territoryLevel,
                        analysisYear,
                        categoryCode
                )
        );
    }
}
