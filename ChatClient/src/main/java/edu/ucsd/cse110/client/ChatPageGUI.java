package edu.ucsd.cse110.client;

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
        this.setLayout(null);
        // configuring the panel
        logoff = new JButton("Log out");
        logoff.setLocation(20, 300);
        logoff.setSize(100, 30);
        this.add(logoff);

        createChatRoom = new JButton("Create ChatRoom");
        createChatRoom.setLocation(130, 300);
        createChatRoom.setSize(160, 30);
        this.add(createChatRoom);

        broadcast = new JButton("Broadcast");
        broadcast.setLocation(300, 300);
        broadcast.setSize(120, 30);
        this.add(broadcast);

	}

}
