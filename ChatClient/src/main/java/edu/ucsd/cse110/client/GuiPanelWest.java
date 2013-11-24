package edu.ucsd.cse110.client;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class GuiPanelWest extends JPanel {

	private GuiChatPage page;
	private JTextArea textArea;
	
	public GuiPanelWest(GuiChatPage page) {
		this.page = page;
		
		textArea = new JTextArea(30,45);
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea); 
		this.add(scrollPane);
		scrollPane.setVisible(true);
	}
	
	public void updateText(String message) {
		textArea.append(message);
	}

}
