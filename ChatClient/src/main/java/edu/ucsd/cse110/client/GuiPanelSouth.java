package edu.ucsd.cse110.client;

import javax.swing.JPanel;

public class GuiPanelSouth extends JPanel {

	private static final long serialVersionUID = 1L;
	private ChatClientGUI frame;
	
	public GuiPanelSouth(ChatClientGUI gui) {
		this.frame = gui;
		this.add(new GuiInputBox(frame));
	}

}
