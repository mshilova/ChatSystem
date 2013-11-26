package edu.ucsd.cse110.client;

import javax.swing.JPanel;

public class GuiPanelSouth extends JPanel {

	private ChatClientGUI frame;
	
	public GuiPanelSouth(ChatClientGUI gui) {
		this.frame = gui;
		this.setSize(800, 100);
		this.add(new GuiInputBox(frame));
	}

}
