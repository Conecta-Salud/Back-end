package com.itesm.domain.models.Uploader.Establecimiento;



public class State {

    private Integer id;
    private String name;
    private String inegiCode;

    public State(Integer id, String name, String inegiCode) {
        this.id = id;
        this.name = name;
        this.inegiCode = inegiCode;
    }
    public State() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInegiCode() {
        return inegiCode;
    }

    public void setInegiCode(String inegiCode) {
        this.inegiCode = inegiCode;
    }
}
