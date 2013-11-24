package edu.ucsd.cse110.client;

import java.awt.BorderLayout;

import javax.swing.JPanel;

public class GuiChatPage extends JPanel {

	private ChatClientGUI frame;
	
	public GuiChatPage(ChatClientGUI frame) {
		this.frame = frame;
        this.setSize(600, 400);
        this.setLayout(new BorderLayout());
        
        this.add(new GuiPanelEast(this), BorderLayout.EAST);
        this.add(new GuiPanelNorth(this), BorderLayout.NORTH);
        this.add(new GuiPanelSouth(this), BorderLayout.SOUTH);
        this.add(new GuiPanelWest(this), BorderLayout.WEST);
	}

	public ChatClientGUI getFrame() {
		return frame;
	}

	public void setFrame(ChatClientGUI frame) {
		this.frame = frame;
	}
	
}
