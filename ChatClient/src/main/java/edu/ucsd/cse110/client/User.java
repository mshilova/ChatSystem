package edu.ucsd.cse110.client;
import java.util.Scanner;

public class User {
	public String userName;
	public String password;
	
	public void setInfo() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Username: ");
		userName = sc.nextLine();
		System.out.println("Password: ");
		password = sc.nextLine();
	}
}
