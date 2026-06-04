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
public class GetMunicipalityMapUseCase {

    private final MapRepository mapRepository;

    @Inject
    public GetMunicipalityMapUseCase(MapRepository mapRepository) {
        this.mapRepository = mapRepository;
    }

    public List<MapIndicatorResponseDto> execute(String stateCode, String indicator, Integer year) {
        if (stateCode == null || stateCode.isBlank()) {
            throw new BadRequestException("El stateCode es obligatoria");
        }

        if (year  == null) {
            throw new BadRequestException("El periodo es obligatorio");
        }

        MapIndicatorType indicatorType = MapIndicatorType.fromString(indicator);

        if (!mapRepository.existsPeriodByYear(year)) {
            throw new NotFoundException("No existe periodo para el año solicitado: " + year);
        }

        if (!mapRepository.existsStateByCode(stateCode)) {
            throw new NotFoundException("No existe estado con clave INEGI: " + stateCode);
        }

        return mapRepository.findMunicipalityIndicators(stateCode, indicatorType, year)
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
