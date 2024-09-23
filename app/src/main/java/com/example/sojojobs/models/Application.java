package com.example.sojojobs.models;

public class Application {
    private String name;
    private String email;
    private String phone;
    private String cvUrl;

    // Default constructor required for Firestore
    public Application() {}

    // Constructor
    public Application(String name, String email, String phone, String cvUrl) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.cvUrl = cvUrl;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCvUrl() { return cvUrl; }
    public void setCvUrl(String cvUrl) { this.cvUrl = cvUrl; }
}
