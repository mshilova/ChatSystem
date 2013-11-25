package edu.ucsd.cse110.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;

public class GuiInputBox extends JPanel {

	private GuiChatPage page;
	private JTextField field;
	private ArrayList<String> sendList;
	
	public GuiInputBox(GuiChatPage chatPage) {
		
		this.page = chatPage;
		this.setLayout(new FlowLayout());
		
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
					sendList = page.getEastPanel().getOnlineUsersList().getSelectedUsers();
					// printing the list of users, testing
					for(String s: sendList) {
						System.out.println(s);
						// sending a message to a user
						page.getWestPanel().updateTextSend(s, field.getText());
						page.getFrame().getClient().send(s, field.getText());
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
					page.getFrame().getClient().getChatCommander().broadcast(field.getText());
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
	       // create a sequential group for the horizontal axis
		       
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
	
	
	public String getText() {
		return field.getText();
	}

}
