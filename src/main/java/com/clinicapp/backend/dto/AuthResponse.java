package com.clinicapp.backend.dto;

public class AuthResponse {
	private String token;
	private String role;
	private Long clinicId;

	public AuthResponse() {
	}

	public AuthResponse(String token, String role, Long clinicId) {
		this.token = token;
		this.role = role;
		this.clinicId = clinicId;
	}

	// getters / setters
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
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
}
