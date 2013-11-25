package edu.ucsd.cse110.client;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class GuiTextArea extends JPanel{
	
	private JTextArea textArea;
	
	public GuiTextArea() {
		textArea = new JTextArea(30,40);
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea); 
		this.add(scrollPane);
		scrollPane.setVisible(true);
	}
	
	public void append(String s)  {
		textArea.append(s);
	}

}
