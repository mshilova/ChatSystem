package edu.ucsd.cse110.client;
import java.io.Serializable;

public class User implements Serializable {
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
	private String username;
	private String password;
	private boolean verified;
	
	public User(){
		this.verified = false;
	}
	
	public User(String username, String password, boolean verified){
		this.username = username;
		this.password = password;
		this.verified = verified;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setVerified(boolean reg){
		this.verified = reg;
	}
	
	public boolean getVerified(){
		return this.verified;
	}
}
