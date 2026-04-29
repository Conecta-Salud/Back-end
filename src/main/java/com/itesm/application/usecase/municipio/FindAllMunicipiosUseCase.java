package com.itesm.application.usecase.municipio;

import com.itesm.application.dto.municipio.MunicipioResponseDto;
import com.itesm.domain.repository.MunicipioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class FindAllMunicipiosUseCase {

    private final MunicipioRepository municipioRepository;

    @Inject
    public FindAllMunicipiosUseCase(MunicipioRepository municipioRepository) {
        this.municipioRepository = municipioRepository;
    }

    public List<MunicipioResponseDto> execute() {
        return municipioRepository.getAllMunicipios()
                .stream()
                .map(municipio -> new MunicipioResponseDto(
                        municipio.getId(),
                        municipio.getIdEstado(),
                        municipio.getNombre(),
                        municipio.getClaveInegi(),
                        municipio.getLat(),
                        municipio.getLng()
                ))
                .collect(Collectors.toList());
    }
}
