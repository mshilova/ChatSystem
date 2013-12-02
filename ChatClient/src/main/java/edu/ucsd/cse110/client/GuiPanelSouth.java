package edu.ucsd.cse110.client;

import javax.swing.JPanel;

public class GuiPanelSouth extends JPanel {

	private static final long serialVersionUID = 1L;
	private ChatClientGUI frame;
	private GuiInputBox inputBox;
	
	public GuiPanelSouth(ChatClientGUI gui) {
		this.frame = gui;
		this.add(inputBox = new GuiInputBox(frame));
		inputBox.setLabel("Sending messages to Users.");
	}

	public GuiInputBox getInputBox()  {
		 return this.inputBox;
	}
}
