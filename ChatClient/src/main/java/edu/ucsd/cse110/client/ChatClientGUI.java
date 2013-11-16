package edu.ucsd.cse110.client;

import javax.swing.JFrame;

public class ChatClientGUI extends JFrame {

	private ChatClient client;  // ChatClient reference

	private LoginPageGUI loginPage;
	private ChatPageGUI chatPage;
	
	public ChatClientGUI(ChatClient chatClient) {
		client = chatClient; // need for the reference to the clients methods
		// configuring the frame
		
		this.setVisible(true);
		this.setTitle("Welcome to Chat");
		this.setSize(600, 400);
		// exit on close
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		
		// adding the main chat panel
	}
	
	/*
	 * 
	 */
	public void start()  {
		loginPage = new LoginPageGUI(this, client); // making login page panel
		this.add(loginPage);
		loginPage.log();
		// continue verifying that login was successful
		 do {
			try {
				System.out.println("verifying login");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}while(!(client.verified || client.registered));
		doChat();
		
	}
	
	public void doChat()  {
		
		this.remove(loginPage);
		this.validate();
		this.repaint();
		
	}
}
