package edu.ucsd.cse110.client;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class GuiPanelWest extends JPanel {

	private GuiChatPage page;
	private JPanel general;
	private JPanel chatRooms;
	
	public GuiPanelWest(GuiChatPage page) {
		this.page = page;
        this.setBorder(BorderFactory.createLineBorder(Color.black));

		JTabbedPane tabbedPane = new JTabbedPane();
		general = new JPanel();
		general.setPreferredSize(new Dimension(500, 440));
		chatRooms = new JPanel();
		chatRooms.setPreferredSize(new Dimension(500, 440));
		
		//adding tabs
		tabbedPane.addTab("General", general);
		//TODO add drop down to have multiple chat rooms
		tabbedPane.addTab("ChatRooms", chatRooms);
		this.add(tabbedPane);
		this.setVisible(true);
	}
	
	
	/*
	 * Update your text area from a sender
	 */
	public void updateTextReceive(String sender, String message) {
		page.getEastPanel().getOnlineUsersList().appendTextReceive(sender, message);
		setTextArea(sender);
		// select the senders text area for reply
		page.getEastPanel().getOnlineUsersList().setReplyUser(sender);
	}
	
	/*
	 * Update your text area for messages you send
	 */
	public void updateTextSend(String receiver, String message) {
		page.getEastPanel().getOnlineUsersList().appendTextSend(receiver, message);
		if(general.getComponentCount()==0) {
			setTextArea(receiver);
		}
	}
	
	public void setTextArea(String s) {
		general.removeAll();
		general.add(page.getEastPanel().getOnlineUsersList().getTextArea(s));
		general.revalidate();
		general.repaint();
	}
	
	/*
	 * Update text area in chatbox
	 */
	
	

}
