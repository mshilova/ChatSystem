package edu.ucsd.cse110.client;

import javax.swing.JFrame;

public class ChatClientGUI extends JFrame {

	private ChatClient client;  // ChatClient reference

	private LoginPage loginPage;
	
	public ChatClientGUI(ChatClient chatClient) {
		client = chatClient; // need for the reference to the clients methods
		// configuring the frame
		this.setVisible(true);
		this.setTitle("Welcome to Chat");
		this.setSize(600, 400);
		// exit on close
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		loginPage = new LoginPage(this, client); // making login page panel
		this.add(loginPage);
	}
	
}
