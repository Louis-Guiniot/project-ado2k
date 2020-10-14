package com.example.application.data.entity;

import javax.persistence.Entity;
import javax.validation.constraints.Size;

import com.example.application.data.AbstractEntity;

import java.time.LocalDate;

@Entity
public class Person extends AbstractEntity {

	private int id;
	
	@Size(min = 10, max = 30, message 
		      = "First Name must be between 10 and 30 characters")
	private String firstName;
	
	private LocalDate dateOfBirth;
	
	@Size(min = 10, max = 10, message 
		      = "Mobile phone must be 10 numbers")
	private String mobile;
	
	
	private String gender;
	
	private String address;
	
	private String city;
	
	private String cap;
	
	private String road;
	
	private String province;
	
	private String lastName;
	
	private String email;
	
	private String phone;
	
	private String occupation;

		
	public Person(int id, String firstName, LocalDate dateOfBirth, String mobile, String gender, String address,
			String city, String cap, String road, String province, String lastName, String email, String phone,
			String occupation) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.dateOfBirth = dateOfBirth;
		this.mobile = mobile;
		this.gender = gender;
		this.address = address;
		this.city = city;
		this.cap = cap;
		this.road = road;
		this.province = province;
		this.lastName = lastName;
		this.email = email;
		this.phone = phone;
		this.occupation = occupation;
	}
	
	public Person() {
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCap() {
		return cap;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}

	public String getRoad() {
		return road;
	}

	public void setRoad(String road) {
		this.road = road;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

}
