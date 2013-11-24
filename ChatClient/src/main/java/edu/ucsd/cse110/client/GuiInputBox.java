package edu.ucsd.cse110.client;

import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class GuiInputBox extends JPanel {

	private ChatClientGUI frame;
	
	public GuiInputBox(ChatClientGUI frame) {
		this.frame = frame;
		this.setLayout(new FlowLayout());
		
		JTextField field = new JTextField(50);
		this.add(field);
		this.setVisible(true);
	}

}
