package edu.ucsd.cse110.client;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class GuiPanelWest extends JPanel {

	private static final long serialVersionUID = 1L;
	private ChatClientGUI frame;
	private JPanel generalTab, chatRoomsTab;
	private JComboBox<String> chatRoomList;
	private HashMap<String,GuiTextArea> chatRoomTextAreas;
	
	public GuiPanelWest(ChatClientGUI gui) {
		this.frame = gui;
		chatRoomTextAreas = new HashMap<String,GuiTextArea>();

		JTabbedPane tabbedPane = new JTabbedPane();
		generalTab = new JPanel();
		generalTab.setPreferredSize(new Dimension(500, 450));
		generalTab.setLayout(new FlowLayout(FlowLayout.LEADING));
		chatRoomsTab = new JPanel();
		chatRoomsTab.setPreferredSize(new Dimension(500, 450));
		chatRoomsTab.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		//adding tabs
		tabbedPane.addTab("General", generalTab);
		//TODO add drop down to have multiple chat rooms
		tabbedPane.addTab("ChatRooms", chatRoomsTab);
		// making an empty drop down list for chat rooms
		chatRoomList = new JComboBox<String>();
		chatRoomList.setPreferredSize(new Dimension(200, 25));
		chatRoomList.setEditable(false);
		chatRoomList.addActionListener(chatRoomSelect);
		
		chatRoomsTab.add(chatRoomList);
		this.add(tabbedPane);
		this.setVisible(true);
	}
	
	
	/**
	 * Adding chat room name to the drop down menu in the ChatRooms tab.
	 * Create a new text area for that chat room
	 * @param roomName	the name of the chat room to be added
	 */
	public void addChatRoom(String roomName)  {
		chatRoomTextAreas.put(roomName, new GuiTextArea());
		chatRoomList.addItem(roomName);
		
	}
	
	
	/**
	 * Add chat room name to the drop down menu in the ChatRooms tab and create
	 * a new text area for that chat room if the user accepts the chat room
	 * invitation
	 * @param roomName	the name of the chat room to be added if invitation is 
	 * 					accepted
	 */
	public void addChatRoomInvite(String roomName)  {
		//TODO make it stand out from 
		// the created rooms
		// idea: connect to chat room when selected
		
	    int reply = JOptionPane.showConfirmDialog(null, "Would you like to join the chat room '" + roomName + "'?", "Invitation", JOptionPane.YES_NO_OPTION);
	    
        if (reply == JOptionPane.YES_OPTION) {
        	System.out.println("Yes was clicked");
        	chatRoomList.addItem(roomName);
        	chatRoomTextAreas.put(roomName, new GuiTextArea());
        } else {
        	System.out.println("No was clicked");
        }
		
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
	}
	

	/**
	 * Update your text area for messages you send
	 * @param receiver	the receiver of your message
	 * @param message	the message to be appended to the text area
	 */
	public void updateTextSend(String receiver, String message) {
		frame.getEastPanel().getOnlineUsersList().appendTextSend(receiver, message);
		if(generalTab.getComponentCount()==0) {
			setTextArea(receiver);
		}
	}
	
	
	/**
	 * Set the text area in the General tab to a specific user
	 * @param user	the name of the user whose text area should be shown
	 */
	public void setTextArea(String user) {
		generalTab.removeAll();
		generalTab.add(frame.getEastPanel().getOnlineUsersList().getTextArea(user));
		generalTab.revalidate();
		generalTab.repaint();
	}
	
	
	/**
	 * 
	 * @return	the name of the currently selected chat room
	 */
	public String getSelectedRoom()  {
		return (String)chatRoomList.getSelectedItem();
	}
	
	
	/**
	 * Removes a chat room from the drop down menu in the ChatRooms tab
	 * @param the name of the chat room to be removed from the drop down menu
	 */
	public void removeRoom(String roomName) {
		chatRoomList.removeItem(roomName);
	}
	
	
	/**
	 * Set the text area in the ChatRooms tab to a specific chat room
	 * @param room	the name of the chat room whose text area should be shown
	 */
	public void setRoomTextArea(String s) {
		for(Component c: (Component[])chatRoomsTab.getComponents())  {
			if(c instanceof GuiTextArea)  {
				chatRoomsTab.remove(c);
			}
		}
		chatRoomsTab.add(chatRoomTextAreas.get(s));
		chatRoomsTab.revalidate();
		chatRoomsTab.repaint();
	}
	
	
	/*
	 * When a chat room is selected in the chatRoomList menu, switch to the text
	 * area of that chat room
	 */
	private ActionListener chatRoomSelect = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			String room = (String)chatRoomList.getSelectedItem();
			setRoomTextArea(room);
			System.out.println("Inside room action listener, room is : " + room);
		}
		
	};

}
