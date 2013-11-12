package edu.ucsd.cse110.client;
import java.io.Serializable;
import java.util.Scanner;

public class User implements Serializable {
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
	private String userName;
	private String password;
	
	public void setInfo() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Username: ");
		userName = sc.nextLine();
		System.out.println("Password: ");
		password = sc.nextLine();
		
		sc.close();
	}
	

}
