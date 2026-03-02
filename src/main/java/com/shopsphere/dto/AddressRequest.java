package com.shopsphere.dto;

import jakarta.validation.constraints.NotBlank;

public class AddressRequest {
	
	@NotBlank 
	private String fullName;
	
    @NotBlank 
    private String phone;
    
    @NotBlank 
    private String street;
    
    @NotBlank 
    private String city;
    
    @NotBlank 
    private String state;
    
    @NotBlank 
    private String zipCode;
    
    private boolean isDefault;

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
    
    
	
}
