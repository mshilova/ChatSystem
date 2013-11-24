package edu.ucsd.cse110.client;

import javax.swing.JFrame;

public class ChatClientGUI extends JFrame {

	private ChatClient client;  // ChatClient reference
	
	public ChatClientGUI(ChatClient chatClient) {
		client = chatClient; // need for the reference to the clients methods
		
		// configuring the frame
		this.setTitle("Welcome to Chat");
		this.setSize(800, 600);
		// exit on close
		this.setCloseOperation();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.setVisible(true);
	}
	
	
	/*
	 * Start using the GUI
	 */
	public void start()  {
		loginPage();
		chatPage();
	}
	
	
	/*
	 * Verifying login
	 */
	public void loginPage() {
		// making login page panel
		GuiLoginPage loginPage = new GuiLoginPage(this);
		this.add(loginPage);
		loginPage.log();
		
		// continue verifying until login is successful
		do {
			try {
				System.out.println("Verifying login");
				Thread.sleep(1000);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Verification: " + client.getUser().getVerified());
		} while(!client.getUser().getVerified());
		 
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		this.remove(loginPage);	// clear the frame
	}
	
	
	public void chatPage()  {
		GuiChatPage chatPage = new GuiChatPage(this);
        this.add(chatPage); // adding main chat page to the frame
        this.setTitle("Welcome " + client.getUser().getUsername());
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
		        client.sendServer(Constants.LOGOFF, client.getUser().getUsername());
		        System.out.println("Closing behavior set");
		    }
		});
	}
	
	
	public void setFrameTitle(String title) {
		this.setTitle(title);
	}


	public ChatClient getClient() {
		return client;
	}


	public void setClient(ChatClient client) {
		this.client = client;
	}
}
