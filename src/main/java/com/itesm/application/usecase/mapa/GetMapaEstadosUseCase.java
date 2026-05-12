package com.itesm.application.usecase.mapa;

import com.itesm.application.dto.mapa.MapaIndicadorResponseDto;
import com.itesm.domain.models.mapa.IndicadorMapaTipo;
import com.itesm.domain.repository.MapaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class GetMapaEstadosUseCase {

    private final MapaRepository mapaRepository;

    @Inject
    public GetMapaEstadosUseCase(MapaRepository mapaRepository) {
        this.mapaRepository = mapaRepository;
    }

    public List<MapaIndicadorResponseDto> execute(String indicador, Integer anio) {
        if (anio == null) {
            throw new BadRequestException("El anio es obligatorio");
        }

        IndicadorMapaTipo indicadorTipo = IndicadorMapaTipo.fromString(indicador);

        if (!mapaRepository.existsPeriodoByAnio(anio)) {
            throw new NotFoundException("No existe periodo para el año solicitado: " + anio);
        }

        return mapaRepository.findEstadosIndicador(indicadorTipo, anio)
                .stream()
                .map(item -> new MapaIndicadorResponseDto(
                        item.getCode(),
                        item.getName(),
                        item.getValue(),
                        item.getLevel().getValue(),
                        item.getColorToken().getValue()
                ))
                .collect(Collectors.toList());
    }
}
