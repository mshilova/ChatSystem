package edu.ucsd.cse110.client;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GuiInputBox extends JPanel {

	private ChatClientGUI frame;
	private JTextField field;
	private ArrayList<String> sendList;
	
	public GuiInputBox(ChatClientGUI gui) {
		this.frame = gui;
		
		final JLabel output = new JLabel("Please enter a message");
		output.setForeground(Color.RED);
		output.setVisible(false);
		field = new JTextField(50);
		
		JButton send = new JButton("Send");
		send.setSize(100, 20);
		send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if("".equals(field.getText())) {
					output.setVisible(true);
				} else {
					sendList = frame.getEastPanel().getOnlineUsersList().getSelectedUsers();
					// printing the list of users, testing
					for(String s: sendList) {
						System.out.println(s);
						// sending a message to a user
						frame.getPanelWest().updateTextSend(s, field.getText());
						frame.getClient().send(s, field.getText());
					}
					output.setVisible(false);
					field.setText("");
					
					System.out.println("Sending message");
				}
			}
		});
		
		JButton broadcast = new JButton("Broadcast");
		broadcast.setSize(100, 20);
		broadcast.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {			
				if("".equals(field.getText())) {
					output.setVisible(true);
				} else {
					output.setVisible(false);
					frame.getClient().getChatCommander().broadcast(field.getText());
					field.setText("");
					System.out.println("Broadcasting message");
				}
			}
		});
		
		
		// creating layout to align labels with text fields
		GroupLayout groupLayout = new GroupLayout(this);
		this.setLayout(groupLayout);
		groupLayout.setAutoCreateGaps(true);
		groupLayout.setAutoCreateContainerGaps(true);
		       
		// aligning along horizontal axis
		GroupLayout.SequentialGroup hGroup = groupLayout.createSequentialGroup();
		hGroup.addGroup(groupLayout.createParallelGroup().addComponent(output).addComponent(field));
		hGroup.addGroup(groupLayout.createParallelGroup().addComponent(send));
		hGroup.addGroup(groupLayout.createParallelGroup().addComponent(broadcast));
		groupLayout.setHorizontalGroup(hGroup);
		// aligning along vertical axis (read documentation for more detail)   
		GroupLayout.SequentialGroup vGroup = groupLayout.createSequentialGroup();
		vGroup.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(output));
		vGroup.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(field).addComponent(send).addComponent(broadcast));
		groupLayout.setVerticalGroup(vGroup);
	}
	
	
	/**
	 * 
	 * @return	the text in the input box
	 */
	public String getText() {
		return field.getText();
	}

}
