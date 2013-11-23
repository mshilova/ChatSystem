package edu.ucsd.cse110.client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ChatPageGUI extends JPanel {
	
	private JFrame frame;
	private ChatClient client;
	private JButton logoff, createChatRoom, broadcast;

	public ChatPageGUI(JFrame frame, ChatClient client) {
		
		this.frame = frame;
		this.client = client;
		
		this.frame = frame;
        this.client = client;
        this.setSize(400, 400);
        this.setLayout(new BorderLayout());
        
        JPanel eastPanel = new JPanel();
        eastPanel.setSize(200, 400);
        eastPanel.setLayout(new BorderLayout());
        this.add(eastPanel, BorderLayout.EAST);
        
        eastPanel.add(new OnlineUsersListGUI(client), BorderLayout.NORTH);
        JPanel eastSouthPanel = new JPanel();
        eastSouthPanel.setLayout(new BorderLayout());
        eastPanel.add(eastSouthPanel, BorderLayout.SOUTH);
        // configuring the panel

        createChatRoom = new JButton("Create ChatRoom");
        createChatRoom.setLocation(130, 300);
        createChatRoom.setSize(160, 30);
        eastSouthPanel.add(createChatRoom, BorderLayout.NORTH);

        broadcast = new JButton("Broadcast");
        broadcast.setLocation(300, 300);
        broadcast.setSize(120, 30);
        eastSouthPanel.add(broadcast, BorderLayout.CENTER);

        logoff = new JButton("Log out");
        logoff.setLocation(20, 300);
        logoff.setSize(100, 30);
        eastSouthPanel.add(logoff, BorderLayout.SOUTH);
	}

}
