package edu.ucsd.cse110.client;

import javax.swing.JPanel;

public class GuiPanelSouth extends JPanel {

	private GuiChatPage page;
	
	public GuiPanelSouth(GuiChatPage page) {
		this.page = page;
		this.setSize(800, 100);
		this.add(new GuiInputBox(page));
	}

}
