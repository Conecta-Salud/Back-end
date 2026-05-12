package com.itesm.domain.repository;

import com.itesm.domain.models.mapa.IndicadorMapaTipo;
import com.itesm.domain.models.mapa.MapaIndicador;

import java.util.List;

public interface MapaRepository {

    List<MapaIndicador> findEstadosIndicador(IndicadorMapaTipo indicador, Integer anio);

    List<MapaIndicador> findMunicipiosIndicador(String claveEstado, IndicadorMapaTipo indicador, Integer anio);

    boolean existsPeriodoByAnio(Integer anio);

    boolean existsEstadoByClave(String claveEstado);
}
