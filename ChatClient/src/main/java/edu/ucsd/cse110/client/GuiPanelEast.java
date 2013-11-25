package edu.ucsd.cse110.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class GuiPanelEast extends JPanel {

	private GuiChatPage page;
	private GuiOnlineUsersList onlineUsersList;
	private JButton logoff, createChatRoom;
	
	public GuiPanelEast(GuiChatPage page) {
		this.page = page;
		this.setPreferredSize(new Dimension(200,500));
		this.setLayout(new BorderLayout());

		onlineUsersList = new GuiOnlineUsersList(page.getFrame());
		onlineUsersList.setPreferredSize(new Dimension(200,420));
		this.add(onlineUsersList, BorderLayout.NORTH);
		
		JPanel eastSouthPanel = new JPanel();
		
		this.add(onlineUsersList, BorderLayout.NORTH);
		this.add(eastSouthPanel, BorderLayout.CENTER);
		
		createChatRoom = new JButton("Create ChatRoom");
		createChatRoom.setPreferredSize(new Dimension(200,25));
		eastSouthPanel.add(createChatRoom);
		
		logoff = new JButton("Log out");
		logoff.setPreferredSize(new Dimension(200,25));
		eastSouthPanel.add(logoff);

	}
	
	
	public GuiOnlineUsersList getOnlineUsersList() {
		return onlineUsersList;
	}

}
