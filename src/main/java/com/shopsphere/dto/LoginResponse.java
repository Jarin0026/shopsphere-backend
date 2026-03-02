package com.shopsphere.dto;

public class LoginResponse {

    private String token;
    private long id;
    private String role;
    private String email;
    private String name;
    
    
	public LoginResponse(String token, long id, String role, String email, String name) {
		super();
		this.token = token;
		this.id = id;
		this.role = role;
		this.email = email;
		this.name = name;
	}


	public String getToken() {
		return token;
	}


	public void setToken(String token) {
		this.token = token;
	}


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public String getRole() {
		return role;
	}


	public void setRole(String role) {
		this.role = role;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

 
}
