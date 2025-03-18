package com.luv2code.jobportal.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "recruiter_profile")
public class RecruiterProfile {

    @Id
    private int userAccountId;
    @OneToOne
    @JoinColumn(name = "user_account_id")
    @MapsId
    private Users userId;

    private String firstName;

    private String lastName;

    private String city;
    private String state;
    private String country;
    private String company;
    @Column(nullable = true,length = 64)
    private String profilePhoto;

    public RecruiterProfile() {
    }

    public RecruiterProfile(int userAccountId, Users userId, String firstName, String lastName, String city, String state, String country, String company, String profilePhoto) {
        this.userAccountId = userAccountId;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.state = state;
        this.country = country;
        this.company = company;
        this.profilePhoto = profilePhoto;
    }

    public RecruiterProfile(Users users){
        this.userId = users;
    }

    public int getUserAccountId() {
        return userAccountId;
    }

    public RecruiterProfile setUserAccountId(int userAccountId) {
        this.userAccountId = userAccountId;
        return this;
    }

    public Users getUserId() {
        return userId;
    }

    public RecruiterProfile setUserId(Users userId) {
        this.userId = userId;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public RecruiterProfile setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public RecruiterProfile setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getCity() {
        return city;
    }

    public RecruiterProfile setCity(String city) {
        this.city = city;
        return this;
    }

    public String getState() {
        return state;
    }

    public RecruiterProfile setState(String state) {
        this.state = state;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public RecruiterProfile setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getCompany() {
        return company;
    }

    public RecruiterProfile setCompany(String company) {
        this.company = company;
        return this;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public RecruiterProfile setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
        return this;
    }

    @Transient
    public String getPhotosImagePath() {
        if (this.profilePhoto == null) {
            return null;
        }else {
            return "/photos/recruiter/" + this.userAccountId + "/" + this.profilePhoto;
        }
    }

    @Override
    public String toString() {
        return "RecruiterProfile{" +
                "userAccountId=" + userAccountId +
                ", userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", company='" + company + '\'' +
                ", profilePhoto='" + profilePhoto + '\'' +
                '}';
    }
}
