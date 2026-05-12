package com.itesm.application.usecase.estado;

import com.itesm.application.dto.estado.EstadoResponseDto;
import com.itesm.domain.repository.EstadoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class FindAllEstadosUseCase {

    private final EstadoRepository estadoRepository;

    @Inject
    public FindAllEstadosUseCase(EstadoRepository estadoRepository) {
        this.estadoRepository = estadoRepository;
    }

    public List<EstadoResponseDto> execute() {
        return estadoRepository.getAllEstados()
                .stream()
                .map(estado -> new EstadoResponseDto(
                        estado.getId(),
                        estado.getNombre(),
                        estado.getClaveIngei(),
                        estado.getLat(),
                        estado.getLng()
                ))
                .collect(Collectors.toList());

    }
}
