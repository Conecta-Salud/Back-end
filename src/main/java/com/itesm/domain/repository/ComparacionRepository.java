package com.itesm.domain.repository;

import com.itesm.domain.models.comparacion.ComparacionTerritorio;

import java.util.List;

public interface ComparacionRepository {
    List<ComparacionTerritorio> compararEstados(Integer periodoId, List<Integer> idsEstados);

    List<ComparacionTerritorio> compararMunicipios(Integer periodoId, List<Integer> idsMunicipios);
}
