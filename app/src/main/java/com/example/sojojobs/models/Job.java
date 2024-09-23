package com.example.sojojobs.models;

import com.google.firebase.Timestamp;

public class Job {
    private String jobId;
    private String jobTitle;
    private String jobDescription;
    private String companyName;
    private String location;
    private String salary;
    private String postedBy;
    private Timestamp postedDate;

    // Default constructor required for calls to DataSnapshot.getValue(Job.class)
    public Job() {}

    // Constructor
    public Job(String jobId, String jobTitle, String jobDescription, String companyName, String location, String salary, String postedBy, Timestamp postedDate) {
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.jobDescription = jobDescription;
        this.companyName = companyName;
        this.location = location;
        this.salary = salary;
        this.postedBy = postedBy;
        this.postedDate = postedDate;
    }

    // Getters and Setters
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getJobDescription() { return jobDescription; }
    public void setJobDescription(String jobDescription) { this.jobDescription = jobDescription; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getSalary() { return salary; }
    public void setSalary(String salary) { this.salary = salary; }

    public String getPostedBy() { return postedBy; }
    public void setPostedBy(String postedBy) { this.postedBy = postedBy; }

    // Return Timestamp directly
    public Timestamp getPostedDate() { return postedDate; }
    public void setPostedDate(Timestamp postedDate) { this.postedDate = postedDate; }
}
