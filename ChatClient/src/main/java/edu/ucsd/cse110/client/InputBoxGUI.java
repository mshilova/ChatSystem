package edu.ucsd.cse110.client;

import javax.swing.JPanel;

public class InputBoxGUI extends JPanel {

	ChatClient client;
	
	public InputBoxGUI(ChatClient client) {
		this.client = client;
		this.setSize(500, 100);
		this.setLayout(null);
	}

}
