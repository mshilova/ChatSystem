package edu.ucsd.cse110.client;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;

public class GuiTabGeneral extends JPanel {

	private static final long serialVersionUID = 1L;
	private ChatClientGUI frame;

	public GuiTabGeneral(ChatClientGUI gui) {
		this.frame = gui;
		this.setPreferredSize(new Dimension(500, 450));
		this.setLayout(new FlowLayout(FlowLayout.LEADING));
	}

	
	/**
	 * Update your text area from a sender
	 * @param sender	the sender
	 * @param message	the message to be appended to the text area
	 */
	public void updateTextReceive(String sender, String message) {
		frame.getEastPanel().getOnlineUsersList().appendTextReceive(sender, message);
		setTextArea(sender);
		// select the senders text area for reply
		frame.getEastPanel().getOnlineUsersList().setReplyUser(sender);
		frame.getPanelWest().setSelectedTab("general");
	}
	

	/**
	 * Update your text area for messages you send
	 * @param receiver	the receiver of your message
	 * @param message	the message to be appended to the text area
	 */
	public void updateTextSend(String receiver, String message) {
		frame.getEastPanel().getOnlineUsersList().appendTextSend(receiver, message);
		if(this.getComponentCount()==0) {
			setTextArea(receiver);
		}
	}
	
	
	/**
	 * Set the text area in the General tab to a specific user
	 * @param user	the name of the user whose text area should be shown
	 */
	public void setTextArea(String user) {
		this.removeAll();
		this.add(frame.getEastPanel().getOnlineUsersList().getTextArea(user));
		this.revalidate();
		this.repaint();
	}
}
