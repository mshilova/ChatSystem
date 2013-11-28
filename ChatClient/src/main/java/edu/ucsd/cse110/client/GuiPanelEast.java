package edu.ucsd.cse110.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.jms.JMSException;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GuiPanelEast extends JPanel {

	private static final long serialVersionUID = 1L;
	final private ChatClientGUI frame;
	private GuiOnlineUsersList onlineUsersList;
	private JButton logoff, createChatRoom, invite;
	
	
	public GuiPanelEast(ChatClientGUI gui) {
		this.frame = gui;
		this.setPreferredSize(new Dimension(200,500));
		this.setLayout(new BorderLayout());

		onlineUsersList = new GuiOnlineUsersList(frame);
		onlineUsersList.setPreferredSize(new Dimension(200,380));
		this.add(onlineUsersList, BorderLayout.NORTH);
		
		JPanel eastSouthPanel = new JPanel();
		
		this.add(onlineUsersList, BorderLayout.NORTH);
		this.add(eastSouthPanel, BorderLayout.CENTER);
		
		createChatRoom = new JButton("Create ChatRoom");
		createChatRoom.setPreferredSize(new Dimension(200,25));
		eastSouthPanel.add(createChatRoom);
		createChatRoom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String response = JOptionPane.showInputDialog(createChatRoom, "Enter the name of your ChatRoom", "Create ChatRoom", JOptionPane.PLAIN_MESSAGE);
				response = response.trim();
				if(response != null) {
					if(!response.contains(" ")) {	// room name cannot be more than 1 word
						frame.getClient().getChatCommander().createChatRoom(response);
				  		frame.getClient().getChatCommander().add(response);
					}
				}
			}
		});
		
		invite = new JButton("Invite to ChatRoom");
		invite.setPreferredSize(new Dimension(200,25));
		eastSouthPanel.add(invite);
		
		invite.addActionListener(new ActionListener()  {
			@Override
			public void actionPerformed(ActionEvent e) {	
				List<String> users = frame.getEastPanel().getOnlineUsersList().getSelectedUsers();
				String room = frame.getPanelWest().getSelectedRoom();
				if(room != null)  {
					for(String user: users) {
						System.out.println(user);
						try {
							frame.getClient().getChatCommander().sendInvitation(user, room);
						} catch (JMSException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});
		
		logoff = new JButton("Log out");
		logoff.setPreferredSize(new Dimension(200,25));
		eastSouthPanel.add(logoff);
		
		logoff.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.getClient().sendServer( Constants.LOGOFF, frame.getClient().getUser().getUsername() );
				System.exit(0);
				frame.dispose(); // closing GUI 	
			}
		});

	}
	
	
	/**
	 * 
	 * @return	the GuiOnlineUserList JPanel
	 */
	public GuiOnlineUsersList getOnlineUsersList() {
		return onlineUsersList;
	}

}
