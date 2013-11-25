package edu.ucsd.cse110.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class GuiPanelWest extends JPanel {

	private GuiChatPage page;
	private JPanel general;
	private JPanel chatRooms;
	private JComboBox chatRoomList;
	
	public GuiPanelWest(GuiChatPage page) {
		this.page = page;
//        this.setBorder(BorderFactory.createLineBorder(Color.black));

		JTabbedPane tabbedPane = new JTabbedPane();
		general = new JPanel();
		general.setPreferredSize(new Dimension(500, 440));
		chatRooms = new JPanel();
		chatRooms.setPreferredSize(new Dimension(500, 440));
		chatRooms.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		//adding tabs
		tabbedPane.addTab("General", general);
		//TODO add drop down to have multiple chat rooms
		tabbedPane.addTab("ChatRooms", chatRooms);
		// making an empty drop down list for chatrooms
		chatRoomList = new JComboBox();
		chatRoomList.setPreferredSize(new Dimension(200, 25));
		chatRoomList.setEditable(false);
		
		chatRooms.add(chatRoomList);
		this.add(tabbedPane);
		this.setVisible(true);
	}
	/*
	 * Adding chat room name to the drop down menu in the chatroom tab.
	 */
	public void addChatRoom(String s)  {	
		chatRoomList.addItem(s);
	}
	
	
	public void addChatRoomInvite(String s)  {
		//TODO make it stand out from 
		// the created rooms
		// idea: connect to chat room when selected
		chatRoomList.addItem(s);
		
	}
	
	public String getSelectedRoom()  {
		return (String)chatRoomList.getSelectedItem();
	}
	public void removeRoom(String s) {
		chatRoomList.removeItem(s);
		// might have to repaint, not sure yet
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
