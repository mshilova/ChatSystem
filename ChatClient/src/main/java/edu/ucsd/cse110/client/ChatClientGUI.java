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
		this.setCloseOperation();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/*
	 * Verifying login
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
			System.out.println("verified: " + client.verified);
			System.out.println("registered: " + client.registered);
		}while(!(client.verified || client.registered));
		doChat();
		
	}
	
	public void doChat()  {
		
		System.out.println("inside doChat");
		this.remove(loginPage); // clear the frame
		
		this.validate();
		this.repaint();
		
        chatPage = new ChatPageGUI(this, client);
        this.add(chatPage); // adding main chat page to the frame
        this.validate();
        this.repaint();

		
	}
	/*
	 * Log out the user when the window is closed.
	 * Called after user verification.
	 */
	private void setCloseOperation() {
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        client.sendServer(Constants.LOGOFF, client.getUser());
		        System.out.println("closing behavior set");
		    }
		});
	}
}
