package edu.ucsd.cse110.client;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class GuiTextArea extends JPanel{
	
	private static final long serialVersionUID = 1L;
	private JTextArea textArea;
	
	public GuiTextArea() {
		textArea = new JTextArea(25,40);
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		
		JScrollPane scrollPane = new JScrollPane(textArea);
		this.add(scrollPane);
		scrollPane.setVisible(true);
	}
	
	public void append(String s)  {
		textArea.append(s);
	}

}
