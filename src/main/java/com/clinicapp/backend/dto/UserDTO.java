package com.clinicapp.backend.dto;

public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String password;  // ADD THIS FIELD
    private String role;
    private Long clinicId;
    private String specialization;

    // Add constructors, getters, and setters
    public UserDTO() {
    }

    public UserDTO(Long id, String name, String email, String password, String role, Long clinicId, String specialization) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;  // ADD IN CONSTRUCTOR
        this.role = role;
        this.clinicId = clinicId;
        this.specialization = specialization;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // ADD PASSWORD GETTER AND SETTER
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getClinicId() {
        return clinicId;
    }

    public void setClinicId(Long clinicId) {
        this.clinicId = clinicId;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
}