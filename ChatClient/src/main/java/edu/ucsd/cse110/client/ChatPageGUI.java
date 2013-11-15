package edu.ucsd.cse110.client;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ChatPageGUI extends JPanel {
	
	private JFrame frame;
	private ChatClient client;
	
	public ChatPageGUI(JFrame frame, ChatClient client) {
		
		frame.removeAll();
		frame.revalidate();
		frame.repaint();
		this.frame = frame;
		this.client = client;
	}

}
