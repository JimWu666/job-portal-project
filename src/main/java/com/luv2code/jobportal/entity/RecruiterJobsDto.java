package com.luv2code.jobportal.entity;

import jakarta.persistence.Entity;


public class RecruiterJobsDto {

    private Long totalCandidates;
    private Integer jobPostId;
    private String jobTitle;
    private JobLocation jobLocationId;
    private JobCompany jobCompanyId;

    public RecruiterJobsDto(Long totalCandidates, Integer jobPostId, String jobTitle, JobLocation jobLocationId, JobCompany jobCompanyId) {
        this.totalCandidates = totalCandidates;
        this.jobPostId = jobPostId;
        this.jobTitle = jobTitle;
        this.jobLocationId = jobLocationId;
        this.jobCompanyId = jobCompanyId;
    }

    public Long getTotalCandidates() {
        return totalCandidates;
    }

    public RecruiterJobsDto setTotalCandidates(Long totalCandidates) {
        this.totalCandidates = totalCandidates;
        return this;
    }

    public Integer getJobPostId() {
        return jobPostId;
    }

    public RecruiterJobsDto setJobPostId(Integer jobPostId) {
        this.jobPostId = jobPostId;
        return this;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public RecruiterJobsDto setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
        return this;
    }

    public JobLocation getJobLocationId() {
        return jobLocationId;
    }

    public RecruiterJobsDto setJobLocationId(JobLocation jobLocationId) {
        this.jobLocationId = jobLocationId;
        return this;
    }

    public JobCompany getJobCompanyId() {
        return jobCompanyId;
    }

    public RecruiterJobsDto setJobCompanyId(JobCompany jobCompanyId) {
        this.jobCompanyId = jobCompanyId;
        return this;
    }


}
