package com.itesm.domain.models.Uploader.Establecimiento;

public class Establishment {
    private int id;
    private String name;


    public Establishment() {}
    public Establishment(int id, String name) {
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
