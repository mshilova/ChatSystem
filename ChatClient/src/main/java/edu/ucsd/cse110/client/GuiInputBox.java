package edu.ucsd.cse110.client;

import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class GuiInputBox extends JPanel {

	private ChatClientGUI frame;
	private JTextField field;
	
	public GuiInputBox(ChatClientGUI frame) {
		this.frame = frame;
		this.setLayout(new FlowLayout());
		
		field = new JTextField(50);
		this.add(field);
	}
	
	
	public String getText() {
		return field.getText();
	}

}
