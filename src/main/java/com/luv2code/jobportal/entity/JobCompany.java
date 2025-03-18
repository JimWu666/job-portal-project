package com.luv2code.jobportal.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "job_company")
public class JobCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    private String name;
    private String logo;

    public JobCompany() {
    }

    public JobCompany(Integer Id, String name, String logo) {
        Id = Id;
        this.name = name;
        this.logo = logo;
    }

    public Integer getId() {
        return Id;
    }

    public JobCompany setId(Integer Id) {
        Id = Id;
        return this;
    }

    public String getName() {
        return name;
    }

    public JobCompany setName(String name) {
        this.name = name;
        return this;
    }

    public String getLogo() {
        return logo;
    }

    public JobCompany setLogo(String logo) {
        this.logo = logo;
        return this;
    }

    @Override
    public String toString() {
        return "JobCompany{" +
                "Id=" + Id +
                ", name='" + name + '\'' +
                ", logo='" + logo + '\'' +
                '}';
    }
}
