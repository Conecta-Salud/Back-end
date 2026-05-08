package com.itesm.infrastructure.persistence.entity;

import com.itesm.domain.models.unidadSalud.NivelAtencion;
import jakarta.persistence.*;

@Entity
@Table(name = "unidades_salud")
@NamedEntityGraph(
        name = "UnidadSalud.summary",
        attributeNodes = {
                @NamedAttributeNode(value = "municipio", subgraph = "municipio.estado"),
                @NamedAttributeNode("institucion"),
                @NamedAttributeNode("tipoEstablecimiento"),
                @NamedAttributeNode("tipoTipologia")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "municipio.estado",
                        attributeNodes = {
                                @NamedAttributeNode("estado")
                        }
                )
        }
)
public class UnidadSaludEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String clues;

    @Column(nullable = false, length = 255)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_municipio", nullable = false)
    private MunicipioEntity municipio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_institucion", nullable = false)
    private InstitucionEntity institucion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tipo_establecimiento", nullable = false)
    private TipoEstablecimientoEntity tipoEstablecimiento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tipo_tipologia", nullable = false)
    private TipoTipologiaEntity tipoTipologia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelAtencion nivelAtencion;

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

    public MunicipioEntity getMunicipio() {
        return municipio;
    }

    public void setMunicipio(MunicipioEntity municipio) {
        this.municipio = municipio;
    }

    public InstitucionEntity getInstitucion() {
        return institucion;
    }

    public void setInstitucion(InstitucionEntity institucion) {
        this.institucion = institucion;
    }

    public TipoEstablecimientoEntity getTipoEstablecimiento() {
        return tipoEstablecimiento;
    }

    public void setTipoEstablecimiento(TipoEstablecimientoEntity tipoEstablecimiento) {
        this.tipoEstablecimiento = tipoEstablecimiento;
    }

    public TipoTipologiaEntity getTipoTipologia() {
        return tipoTipologia;
    }

    public void setTipoTipologia(TipoTipologiaEntity tipoTipologia) {
        this.tipoTipologia = tipoTipologia;
    }

    public NivelAtencion getNivelAtencion() {
        return nivelAtencion;
    }

    public void setNivelAtencion(NivelAtencion nivelAtencion) {
        this.nivelAtencion = nivelAtencion;
    }
}
