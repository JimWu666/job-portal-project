package com.luv2code.jobportal.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "skills")
public class Skills {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String experienceLevel;
    private String yearsOfExperience;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "job_seeker_profile")
    private JobSeekerProfile jobSeekerProfile;

    public Skills() {
    }

    public Skills(Integer id, String name, String experienceLevel, String yearsOfExperience, JobSeekerProfile jobSeekerProfile) {
        this.id = id;
        this.name = name;
        this.experienceLevel = experienceLevel;
        this.yearsOfExperience = yearsOfExperience;
        this.jobSeekerProfile = jobSeekerProfile;
    }

    public Integer getId() {
        return id;
    }

    public Skills setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Skills setName(String name) {
        this.name = name;
        return this;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public Skills setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
        return this;
    }

    public String getYearsOfExperience() {
        return yearsOfExperience;
    }

    public Skills setYearsOfExperience(String yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
        return this;
    }

    public JobSeekerProfile getJobSeekerProfile() {
        return jobSeekerProfile;
    }

    public Skills setJobSeekerProfile(JobSeekerProfile jobSeekerProfile) {
        this.jobSeekerProfile = jobSeekerProfile;
        return this;
    }

    @Override
    public String toString() {
        return "Skills{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", experienceLevel='" + experienceLevel + '\'' +
                ", yearsOfExperience='" + yearsOfExperience + '\'' +
                ", jobSeekerProfile=" + jobSeekerProfile +
                '}';
    }
}
