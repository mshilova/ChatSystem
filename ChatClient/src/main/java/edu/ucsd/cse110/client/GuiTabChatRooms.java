package edu.ucsd.cse110.client;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GuiTabChatRooms extends JPanel {

	private static final long serialVersionUID = 1L;
	private ChatClientGUI frame;
	private JComboBox<String> chatRoomList;
	private HashMap<String,GuiTextArea> chatRoomTextAreas;
	private HashMap<String,JComboBox<String>> membersBoxes;

	public GuiTabChatRooms(ChatClientGUI gui) {
		this.frame = gui;
		this.setPreferredSize(new Dimension(500, 450));
		this.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		chatRoomTextAreas = new HashMap<String,GuiTextArea>();
		
		chatRoomList = new JComboBox<String>();
		chatRoomList.setPreferredSize(new Dimension(150, 25));
		chatRoomList.setEditable(false);
		chatRoomList.addActionListener(roomSelectAction);
		chatRoomList.setName("roomList");
		this.add(chatRoomList);
		
		JButton leaveButton = new JButton("Leave Chat Room");
		leaveButton.addActionListener(leaveRoomAction);
		leaveButton.setToolTipText("Leave this Chat Room.");
		this.add(leaveButton);
		
		membersBoxes = new HashMap<String,JComboBox<String>>();
	}
	
	/**
	 * Adding chat room name to the drop down menu in the ChatRooms tab.
	 * Create a new text area for that chat room
	 * @param roomName	the name of the chat room to be added
	 */
	public void addChatRoom(String roomName, String creator)  {
		membersBoxes.put(roomName, new JComboBox<String>());
		membersBoxes.get(roomName).addItem(creator);
		System.out.println("added combobox");
		chatRoomTextAreas.put(roomName, new GuiTextArea());
		chatRoomList.addItem(roomName);
		frame.getClient().getChatCommander().setupChatRoomTopic(roomName);
	}
	
	
	/**
	 * Add chat room name to the drop down menu in the ChatRooms tab and create
	 * a new text area for that chat room if the user accepts the chat room
	 * invitation
	 * @param roomName	the name of the chat room to be added if invitation is 
	 * 					accepted
	 */
	public void addChatRoomInvite(String roomName)  {		
	    int reply = JOptionPane.showConfirmDialog(null, "Would you like to join the chat room '" + roomName + "'?", "Invitation", JOptionPane.YES_NO_OPTION);
	    
        if (reply == JOptionPane.YES_OPTION) {
        	System.out.println("Yes was clicked");
        	membersBoxes.put(roomName, new JComboBox<String>());
        	chatRoomTextAreas.put(roomName, new GuiTextArea());
        	chatRoomList.addItem(roomName);
        	// accepting the invite
        	try {
        		frame.getClient().getChatCommander().addPendingInvitation( roomName );
				frame.getClient().getChatCommander().acceptInvite("accept "+roomName);
			} catch (JMSException e) {
				e.printStackTrace();
			}
        	frame.getPanelWest().setSelectedTab("chatrooms");
        } else {
        	System.out.println("No was clicked");
        }
		
	}
	
	/**
	 * 
	 * @return	the name of the currently selected chat room
	 */
	public String getSelectedRoom()  {
		return (String)chatRoomList.getSelectedItem();
	}
	
	
	
	public JComboBox<String> getChatRooms() {
		return chatRoomList;
	}
	
	
	/**
	 * Removes a chat room from the drop down menu in the ChatRooms tab
	 * @param the name of the chat room to be removed from the drop down menu
	 */
	public void removeRoom(String roomName) {
		chatRoomList.removeItem(roomName);
		chatRoomTextAreas.remove(roomName);
	}
	
	
	/**
	 * Set the text area in the ChatRooms tab to a specific chat room
	 * @param room	the name of the chat room whose text area should be shown
	 */
	public void setRoomTextArea(String s) {
		for(Component c: (Component[])this.getComponents())  {
			if(c instanceof GuiTextArea) {
				this.remove(c);
			} else if(c instanceof JComboBox) {
				if(!"roomList".equals(c.getName())) {
					this.remove(c);
				}
			}
		}
		this.add(membersBoxes.get(s));
		this.add(chatRoomTextAreas.get(s));
		System.out.println("box size: "+membersBoxes.size());
		this.revalidate();
		this.repaint();
	}
	
	
	public void setRoomMembers(String room, Map<String, Destination> userMap) {
		membersBoxes.get(room).removeAllItems();
		membersBoxes.get(room).revalidate();
		membersBoxes.get(room).repaint();
		for(Map.Entry<String, Destination> entry : userMap.entrySet()) {
			membersBoxes.get(room).addItem(entry.getKey());
		}
		membersBoxes.get(room).revalidate();
		membersBoxes.get(room).repaint();
	}
	
	public void updateRoomTextSend(String text) {
		String currentRoom = getSelectedRoom();
		chatRoomTextAreas.get(currentRoom).append("\nMe: " + text);
	}
	
	
	public void updateRoomTextReceive(String room, String sender, String text) {
		System.out.println("updating");
		chatRoomTextAreas.get(room).append("\n" + sender + ": " + text);
		frame.getPanelWest().setSelectedTab("chatrooms");
		this.revalidate();
		this.repaint();
	}
	
	
	/*
	 * When a chat room is selected in the chatRoomList menu, switch to the text
	 * area of that chat room
	 */
	private ActionListener roomSelectAction = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			String room = (String)chatRoomList.getSelectedItem();
			if(room != null) {
				setRoomTextArea(room);
				System.out.println("Entered room: " + room);
			}	
		}
		
	};
	
	private ActionListener leaveRoomAction = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("inside leave action.");
			String room = ((GuiTabChatRooms)((JButton)e.getSource()).getParent()).getSelectedRoom();
			if(room != null) {
				try {
					frame.getClient().getChatCommander().leaveChatRoom(room);
					((GuiTabChatRooms)((JButton)e.getSource()).getParent()).removeRoom(room);
				} catch (JMSException e1) {
					e1.printStackTrace();
				}
			}
			
		}
	};

}
