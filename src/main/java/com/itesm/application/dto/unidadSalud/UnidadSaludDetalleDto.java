package com.itesm.application.dto.unidadSalud;

public class UnidadSaludDetalleDto {

    private Integer id;
    private String clues;
    private String nombre;
    private TerritorioUnidadDto territorio;
    private ClasificacionUnidadDto clasificacion;
    private PersonalUnidadDto personal;
    private InfraestructuraUnidadDto infraestructura;

    public UnidadSaludDetalleDto(
            Integer id,
            String clues,
            String nombre,
            TerritorioUnidadDto territorio,
            ClasificacionUnidadDto clasificacion,
            PersonalUnidadDto personal,
            InfraestructuraUnidadDto infraestructura
    ) {
        this.id = id;
        this.clues = clues;
        this.nombre = nombre;
        this.territorio = territorio;
        this.clasificacion = clasificacion;
        this.personal = personal;
        this.infraestructura = infraestructura;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClues() {
        return clues;
    }

    public void setClues(String clues) {
        this.clues = clues;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TerritorioUnidadDto getTerritorio() {
        return territorio;
    }

    public void setTerritorio(TerritorioUnidadDto territorio) {
        this.territorio = territorio;
    }

    public ClasificacionUnidadDto getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(ClasificacionUnidadDto clasificacion) {
        this.clasificacion = clasificacion;
    }

    public PersonalUnidadDto getPersonal() {
        return personal;
    }

    public void setPersonal(PersonalUnidadDto personal) {
        this.personal = personal;
    }

    public InfraestructuraUnidadDto getInfraestructura() {
        return infraestructura;
    }

    public void setInfraestructura(InfraestructuraUnidadDto infraestructura) {
        this.infraestructura = infraestructura;
    }
}
