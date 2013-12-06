package edu.ucsd.cse110.client;

import java.awt.BorderLayout;

import javax.jms.JMSException;
import javax.swing.JComboBox;
import javax.swing.JFrame;

public class ChatClientGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private ChatClient client;  // ChatClient reference
	private GuiPanelEast guiPanelEast;
	private GuiPanelSouth guiPanelSouth;
	private GuiPanelWest guiPanelWest;
	
	public ChatClientGUI(ChatClient chatClient) {
		client = chatClient; // need for the reference to the clients methods
		
		// configuring the frame
		this.setTitle("Welcome to Chat");
		this.setSize(800, 600);
		// exit on close
		this.setCloseOperation();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());

		this.setVisible(true);
	}
	
	
	/*
	 * Start using the GUI
	 */
	public void start()  {
		setLoginPage();
	}
	
	
	/*
	 * Verifying login
	 */
	public void setLoginPage() {
		// making login page panel
		GuiLoginPage loginPage = new GuiLoginPage(this);
		this.add(loginPage,BorderLayout.CENTER);
		loginPage.log();
	}
	
	
	public void setChatPage()  {
        this.add(guiPanelWest = new GuiPanelWest(this), BorderLayout.WEST);
        this.add(guiPanelEast = new GuiPanelEast(this), BorderLayout.EAST);
        this.add(guiPanelSouth = new GuiPanelSouth(this), BorderLayout.SOUTH);
        this.setTitle("Welcome " + client.getUser().getUsername());
        this.revalidate();
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
		    	try {
			    	JComboBox<String> rooms = guiPanelWest.getChatRoomsTab().getChatRooms();
			    	for(int i=0; i<rooms.getItemCount(); i++) {
			    		try {
							client.getChatCommander().leaveChatRoom(rooms.getItemAt(i));
						} catch (JMSException e) {
							e.printStackTrace();
						}
			    	}
		    	}
		    	catch(NullPointerException e)  { }
		    	
		    	
		        client.sendServer(Constants.LOGOFF, client.getUser().getUsername());
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
	
	public void updateTextArea(String sender, String message) {
		this.getPanelWest().getGeneralTab().updateTextReceive(sender, message);
	}
	
	public void updateRoomTextArea(String room, String sender, String message) {
		this.getPanelWest().getChatRoomsTab().updateRoomTextReceive(room, sender, message);
	}
	
	public GuiPanelEast getEastPanel() {
		return guiPanelEast;
	}
	
	public GuiPanelSouth getPanelSouth() {
		return guiPanelSouth;
	}
	
	public GuiPanelWest getPanelWest() {
		return guiPanelWest;
	}
}
