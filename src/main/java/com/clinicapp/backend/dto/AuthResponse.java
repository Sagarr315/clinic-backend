package com.clinicapp.backend.dto;

public class AuthResponse {
    private String token;
    private String role;
    private Long clinicId;
    private Long Id;
    private String clinicSubdomain;

    public AuthResponse(String token, String role, Long clinicId, Long Id, String clinicSubdomain) {
        this.token = token;
        this.role = role;
        this.clinicId = clinicId;
        this.Id = Id;
        this.clinicSubdomain = clinicSubdomain;
    }

    public String getToken() {
        return token;
    }

    public String getRole() {
        return role;
    }

    public Long getClinicId() {
        return clinicId;
    }

    public Long getId() {
        return Id;
    }

    public String getClinicSubdomain() {
        return clinicSubdomain;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setClinicId(Long clinicId) {
        this.clinicId = clinicId;
    }

    public void setUserId(Long Id) {
        this.Id = Id;
    }

    public void setClinicSubdomain(String clinicSubdomain) {
        this.clinicSubdomain = clinicSubdomain;
    }
}
