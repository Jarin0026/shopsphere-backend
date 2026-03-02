package com.shopsphere.dto;

public class AuthResponse {

	private String token;
	private Long id;
	private String email;
	private String role;
	private String name;
	
	
	public AuthResponse(String token, Long id, String email, String role, String name) {
		super();
		this.token = token;
		this.id = id;
		this.email = email;
		this.role = role;
		this.name = name;
	}


	public String getToken() {
		return token;
	}


	public void setToken(String token) {
		this.token = token;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getRole() {
		return role;
	}


	public void setRole(String role) {
		this.role = role;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}
	
	

}
