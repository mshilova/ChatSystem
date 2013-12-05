package edu.ucsd.cse110.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.jms.JMSException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GuiPanelEast extends JPanel {

	private static final long serialVersionUID = 1L;
	final private ChatClientGUI frame;
	private GuiOnlineUsersList onlineUsersList;
	private GuiChatRoomsList chatRoomsList;
	private JButton logoff, createChatRoom, invite;
	
	
	public GuiPanelEast(ChatClientGUI gui) {
		this.frame = gui;
		this.setPreferredSize(new Dimension(200,500));
		this.setLayout(new BorderLayout());

		JPanel lists = new JPanel();
		lists.setLayout(new BoxLayout(lists,BoxLayout.Y_AXIS));
		lists.setPreferredSize(new Dimension(200, 400));
		this.add(lists, BorderLayout.NORTH);
		
		onlineUsersList = new GuiOnlineUsersList(frame);
		onlineUsersList.setPreferredSize(new Dimension(200,200));
		lists.add(onlineUsersList);
		
		chatRoomsList = new GuiChatRoomsList(frame);
		chatRoomsList.setPreferredSize(new Dimension(200,100));
		lists.add(chatRoomsList);
		
		JPanel buttonsPanel = new JPanel();
		this.add(buttonsPanel, BorderLayout.CENTER);
		
		createChatRoom = new JButton("Create ChatRoom");
		createChatRoom.setPreferredSize(new Dimension(200,25));
		createChatRoom.addActionListener(createChatRoomAction);
		createChatRoom.setToolTipText("Create a new Chat Room.");
		buttonsPanel.add(createChatRoom);
		
		invite = new JButton("Invite to ChatRoom");
		invite.setPreferredSize(new Dimension(200,25));
		invite.addActionListener(inviteAction);
		invite.setToolTipText("Select a user to invite to your Chat Room.");
		buttonsPanel.add(invite);
		
		logoff = new JButton("Log out");
		logoff.setPreferredSize(new Dimension(200,25));
		logoff.addActionListener(logOffAction);
		logoff.setToolTipText("Exit the Program.");
		buttonsPanel.add(logoff);

	}
	
	private ActionListener createChatRoomAction = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			String response = JOptionPane.showInputDialog(createChatRoom, "Enter the name of your ChatRoom", "Create ChatRoom", JOptionPane.PLAIN_MESSAGE);
			
			if(response != null) {
				response = response.trim();
				if(!response.contains(" ")) {	// room name cannot be more than 1 word
					frame.getClient().getChatCommander().createChatRoom(response);
			  		frame.getClient().getChatCommander().add(response);
			  		frame.getPanelWest().setSelectedTab("chatrooms");
				}
			}
		}
	};
	
	private ActionListener inviteAction = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {	
			List<String> users = frame.getEastPanel().getOnlineUsersList().getSelectedUsers();
			String room = frame.getPanelWest().getChatRoomsTab().getSelectedRoom();
			if(room != null)  {
				List<String> members = frame.getPanelWest().getChatRoomsTab().getRoomMembers(room);
				for(String user: users) {
					if(!members.contains(user)) {
						try {
							frame.getClient().getChatCommander().sendInvitation(user, room);
						} catch (JMSException e1) {
							e1.printStackTrace();
						}
					} else {
						JOptionPane.showMessageDialog(
								invite,
								"This User is already in the ChatRoom.",
								"Cannot Invite",
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
			onlineUsersList.deselectAll();
		}
	};
	
	private ActionListener logOffAction = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
	    	JComboBox<String> rooms = frame.getPanelWest().getChatRoomsTab().getChatRooms();
	    	for(int i=0; i<rooms.getItemCount(); i++) {
	    		try {
					frame.getClient().getChatCommander().leaveChatRoom(rooms.getItemAt(i));
				} catch (JMSException e1) {
					e1.printStackTrace();
				}
	    	}
			
			frame.getClient().sendServer( Constants.LOGOFF, frame.getClient().getUser().getUsername() );
			System.exit(0);
			frame.dispose(); // closing GUI 	
		}
	};
	
	/**
	 * 
	 * @return	the GuiOnlineUserList JPanel
	 */
	public GuiOnlineUsersList getOnlineUsersList() {
		return onlineUsersList;
	}

	public void errorPopUp()  {
		JOptionPane.showMessageDialog(createChatRoom, "Invalid Input.");
	}
	
	public GuiChatRoomsList getChatRoomsList() {
		return chatRoomsList;
	}
}
