package edu.ucsd.cse110.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.jms.JMSException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GuiPanelEast extends JPanel {

	final private GuiChatPage page;
	private GuiOnlineUsersList onlineUsersList;
	private JButton logoff, createChatRoom, invite;
	
	
	public GuiPanelEast(GuiChatPage c_page) {
		this.page = c_page;
		this.setPreferredSize(new Dimension(200,500));
		this.setLayout(new BorderLayout());

		onlineUsersList = new GuiOnlineUsersList(page.getFrame());
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
				String response = JOptionPane.showInputDialog("Enter the name of your ChatRoom");
				System.out.println(response);
				// ChatRoom and ChatRoomManager logic handled in server, but topic is actually made in ChatClient
				// TODO have Kacy to change this
				// client should have a method createChatRoom() and the commander should do everything
				// getting the user name is strange: client is passed in the constructor to the commander, then the commender is getting the client,
				// then the user name. If we just call on the client to get user name, there is an IOB exception, but if we get the client 
				// through the commander, it works. Consider refactoring this, why passing the client to commander then getting it to get the name?
		  		page.getFrame().getClient().sendServer( Constants.CREATECHATROOM, page.getFrame().getClient().getChatCommander().getClient().getUser().getUsername()+" " + response );
		  		page.getFrame().getClient().getChatCommander().add(response);
				
			}
			
		});
		
		invite = new JButton("Invite to ChatRoom");
		invite.setPreferredSize(new Dimension(200,25));
		eastSouthPanel.add(invite);
		invite.addActionListener(new ActionListener()  {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				ArrayList<String> users = page.getEastPanel().getOnlineUsersList().getSelectedUsers();
				String room = page.getWestPanel().getSelectedRoom();
				if(room != null)  {
					for(String user: users)
					{
						try {
							page.getFrame().getClient().getChatCommander().sendInvitation(user, room);
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
				page.getFrame().getClient().sendServer( Constants.LOGOFF, page.getFrame().getClient().getUser().getUsername() );
				System.exit(0);
				page.getFrame().dispose(); // closing gui 
				
			}
			
		});

	}
	
	
	public GuiOnlineUsersList getOnlineUsersList() {
		return onlineUsersList;
	}

}
