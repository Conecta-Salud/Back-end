package com.itesm.domain.models.Uploader.Establecimiento;

public class MedicalUnitTypes {
    private int id;
    private String name;

    public MedicalUnitTypes() {}
    public MedicalUnitTypes(int id, String name) {
        this.id = id;
        this.name = name;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
