package com.itesm.domain.models.department;

public class Department {

    private Integer id;
    private String name;

    public Department() {
    }

    public Department(
            Integer id,
            String name
    ) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}