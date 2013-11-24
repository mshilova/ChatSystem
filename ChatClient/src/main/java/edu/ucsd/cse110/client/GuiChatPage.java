package edu.ucsd.cse110.client;

import java.awt.BorderLayout;

import javax.swing.JPanel;

public class GuiChatPage extends JPanel {

	private ChatClientGUI frame;
	private GuiPanelEast guiPanelEast;
	private GuiPanelWest guiPanelWest;
	
	public GuiChatPage(ChatClientGUI frame) {
		this.frame = frame;
        this.setSize(800, 600);
        this.setLayout(new BorderLayout());
        
        this.add(guiPanelEast = new GuiPanelEast(this), BorderLayout.EAST);
        this.add(new GuiPanelNorth(this), BorderLayout.NORTH);
        this.add(new GuiPanelSouth(this), BorderLayout.SOUTH);
        this.add(guiPanelWest = new GuiPanelWest(this), BorderLayout.WEST);
	}

	public ChatClientGUI getFrame() {
		return frame;
	}

	public void setFrame(ChatClientGUI frame) {
		this.frame = frame;
	}
	
	public GuiPanelEast getEastPanel() {
		return guiPanelEast;
	}
	
	public GuiPanelWest getWestPanel() {
		return guiPanelWest;
	}
}
