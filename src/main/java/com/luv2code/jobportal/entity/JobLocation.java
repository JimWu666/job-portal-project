package com.luv2code.jobportal.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "job_location")
public class JobLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    private String city;
    private String state;
    private String country;

    public JobLocation() {
    }

    public JobLocation(Integer id, String city, String state, String country) {
        Id = id;
        this.city = city;
        this.state = state;
        this.country = country;
    }

    public Integer getId() {
        return Id;
    }

    public JobLocation setId(Integer id) {
        Id = id;
        return this;
    }

    public String getCity() {
        return city;
    }

    public JobLocation setCity(String city) {
        this.city = city;
        return this;
    }

    public String getState() {
        return state;
    }

    public JobLocation setState(String state) {
        this.state = state;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public JobLocation setCountry(String country) {
        this.country = country;
        return this;
    }

    @Override
    public String toString() {
        return "JobLocation{" +
                "Id=" + Id +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
