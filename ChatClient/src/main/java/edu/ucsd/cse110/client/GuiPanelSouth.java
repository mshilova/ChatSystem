package edu.ucsd.cse110.client;

import javax.swing.JPanel;

public class GuiPanelSouth extends JPanel {

	private ChatClientGUI frame;
	
	public GuiPanelSouth(ChatClientGUI gui) {
		this.frame = gui;
		this.add(new GuiInputBox(frame));
	}

}
