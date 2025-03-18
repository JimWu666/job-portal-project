package com.luv2code.jobportal.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "job_seeker_profile")
public class JobSeekerProfile {

    @Id
    private Integer userAccountId;

    @OneToOne
    @JoinColumn(name = "user_account_id")
    @MapsId
    private Users userId;

    private String firstName;
    private String lastName;
    private String city;
    private String state;
    private String country;
    private String workAuthorization;
    private String employmentType;
    private String resume;
    @Column(nullable = true, length = 64)
    private String profilePhoto;

    @OneToMany(targetEntity = Skills.class, cascade = CascadeType.ALL, mappedBy = "jobSeekerProfile")
    private List<Skills> skills;

    public JobSeekerProfile() {
    }

    public JobSeekerProfile(Users userId) {
        this.userId = userId;
    }

    public JobSeekerProfile(Integer userAccountId, Users userId, String firstName, String lastName, String city, String state, String country, String workAuthorization, String employmentType, String resume, String profilePhoto, List<Skills> skills) {
        this.userAccountId = userAccountId;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.state = state;
        this.country = country;
        this.workAuthorization = workAuthorization;
        this.employmentType = employmentType;
        this.resume = resume;
        this.profilePhoto = profilePhoto;
        this.skills = skills;
    }

    public Integer getUserAccountId() {
        return userAccountId;
    }

    public JobSeekerProfile setUserAccountId(Integer userAccountId) {
        this.userAccountId = userAccountId;
        return this;
    }

    public Users getUserId() {
        return userId;
    }

    public JobSeekerProfile setUserId(Users userId) {
        this.userId = userId;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public JobSeekerProfile setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public JobSeekerProfile setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getCity() {
        return city;
    }

    public JobSeekerProfile setCity(String city) {
        this.city = city;
        return this;
    }

    public String getState() {
        return state;
    }

    public JobSeekerProfile setState(String state) {
        this.state = state;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public JobSeekerProfile setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getWorkAuthorization() {
        return workAuthorization;
    }

    public JobSeekerProfile setWorkAuthorization(String workAuthorization) {
        this.workAuthorization = workAuthorization;
        return this;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public JobSeekerProfile setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
        return this;
    }

    public String getResume() {
        return resume;
    }

    public JobSeekerProfile setResume(String resume) {
        this.resume = resume;
        return this;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public JobSeekerProfile setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
        return this;
    }

    public List<Skills> getSkills() {
        return skills;
    }

    public JobSeekerProfile setSkills(List<Skills> skills) {
        this.skills = skills;
        return this;
    }

    @Transient
    public String getPhotosImagePath() {

        if (this.profilePhoto == null || this.userAccountId == null) return null;

        return "/photos/candidate/" + this.userAccountId + "/" + this.profilePhoto;
    }

    @Override
    public String toString() {
        return "JobSeekerProfile{" +
                "userAccountId=" + userAccountId +
                ", userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", workAuthorization='" + workAuthorization + '\'' +
                ", employmentType='" + employmentType + '\'' +
                ", resume='" + resume + '\'' +
                ", profilePhoto='" + profilePhoto + '\'' +
                '}';
    }
}
