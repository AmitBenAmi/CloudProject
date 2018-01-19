package com.cloudproject.users.models;

public class User {
	private String username;
	private String firstName;
	private String lastName;
	private String mail;
	
	public User(String username, String firstName, String lastName, String mail) {
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.mail = mail;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getFirstName() {
		return this.firstName;
	}
	
	public String getLastName() {
		return this.lastName;
	}
	
	public String getMail() {
		return this.mail;
	}
}
