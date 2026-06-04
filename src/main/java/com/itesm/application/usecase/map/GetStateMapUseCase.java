package com.itesm.application.usecase.map;

import com.itesm.application.dto.map.MapIndicatorResponseDto;
import com.itesm.domain.models.map.MapIndicatorType;
import com.itesm.domain.repository.MapRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class GetStateMapUseCase {

    private final MapRepository mapRepository;

    @Inject
    public GetStateMapUseCase(MapRepository mapRepository) {
        this.mapRepository = mapRepository;
    }

    public List<MapIndicatorResponseDto> execute(String indicator, Integer year) {
        if (year == null) {
            throw new BadRequestException("El periodo es obligatorio");
        }

        MapIndicatorType indicatorType = MapIndicatorType.fromString(indicator);

        if (!mapRepository.existsPeriodByYear(year)) {
            throw new NotFoundException("No existe periodo para el año solicitado: " + year);
        }

        return mapRepository.findStateIndicators(indicatorType, year)
                .stream()
                .map(item -> new MapIndicatorResponseDto(
                        item.getCode(),
                        item.getName(),
                        item.getValue(),
                        item.getLevel().name().toLowerCase(),
                        item.getColorToken().name().toLowerCase(),
                        item.getSourceYear(),
                        item.getUnit(),
                        item.getAvailabilityStatus(),
                        item.getMethodologyNote(),
                        item.getDataSourceName()
                ))
                .collect(Collectors.toList());
    }
}
