package org.pbccrc.platform.model;

import org.springframework.stereotype.Component;

@Component
public class UserModel {
	
	private String name;
	private String password;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public String toString() {
		return "UserModel [name=" + name + ", password=" + password + "]";
	}
	
}
