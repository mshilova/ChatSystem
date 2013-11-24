package edu.ucsd.cse110.client;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

public class GuiPanelEast extends JPanel {

	private GuiChatPage page;
	private JButton logoff, createChatRoom, broadcast;
	
	public GuiPanelEast(GuiChatPage page) {
		this.page = page;
		this.setSize(100, 600);
		this.setLayout(new BorderLayout());
	        
		this.add(new GuiOnlineUsersList(page.getFrame()), BorderLayout.NORTH);
		JPanel eastSouthPanel = new JPanel();
		eastSouthPanel.setLayout(new BorderLayout());
		this.add(eastSouthPanel, BorderLayout.SOUTH);

		createChatRoom = new JButton("Create ChatRoom");
		createChatRoom.setSize(100, 20);
		eastSouthPanel.add(createChatRoom, BorderLayout.NORTH);

		broadcast = new JButton("Broadcast");
		broadcast.setSize(100, 20);
		eastSouthPanel.add(broadcast, BorderLayout.CENTER);
		
		logoff = new JButton("Log out");
		logoff.setSize(100, 20);
		eastSouthPanel.add(logoff, BorderLayout.SOUTH);
	}

}
