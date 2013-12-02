package edu.ucsd.cse110.client;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.jms.JMSException;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GuiInputBox extends JPanel {

	private static final long serialVersionUID = 1L;
	private ChatClientGUI frame;
	private JTextField field;
	private ArrayList<String> sendList;
	private final JLabel output;
	
	public GuiInputBox(ChatClientGUI gui) {
		this.frame = gui;
		
		output = new JLabel();
		
		
		field = new JTextField(50);
		
		JButton send = new JButton("Send");
		send.addActionListener(sendAction);
		send.setToolTipText("Select user to send message.");
		
		JButton broadcast = new JButton("Broadcast");
		broadcast.addActionListener(broadcastAction);
		broadcast.setToolTipText("Broadcast message to all users.");
		
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

	
	private ActionListener sendAction = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(frame.getPanelWest().getSelectedTab() instanceof GuiTabChatRooms)  {
				// inside ChatRoom tab
				try {
					// getting the room name
					String room = frame.getPanelWest().getChatRoomsTab().getSelectedRoom();
					System.out.println("Room: " + room);
					if(room != null)  {
						String message = field.getText();
						if("".equals(field.getText()))  {
							output.setText("Please enter a message.");
							output.setForeground(Color.RED);
							output.setVisible(true);
						} else {
							frame.getClient().getChatCommander().publishMessageToChatRoom(room, message);
							frame.getPanelWest().getChatRoomsTab().updateRoomTextSend(message);
						}	
					}	else {
						output.setText("Please select a Chat Room.");
						output.setForeground(Color.RED);
						output.setVisible(true);
					}
				} catch (JMSException e1) {
					e1.printStackTrace();
				}
			} else {
				// general tab sending message
				if("".equals(field.getText())) {
					output.setText("Please enter a message.");
					output.setForeground(Color.RED);
					output.setVisible(true);
				} else {
					sendList = frame.getEastPanel().getOnlineUsersList().getSelectedUsers();
					if(sendList.isEmpty()) {
						output.setText("Select a user to send message");
						output.setForeground(Color.RED);
						output.setVisible(true);
					} else {
						// printing the list of users, testing
						for(String s: sendList) {
							System.out.println(s);
							// sending a message to a user
							frame.getPanelWest().getGeneralTab().updateTextSend(s, field.getText());
							frame.getClient().send(s, field.getText());
						}
						output.setText("Sending messages to Users.");
						output.setForeground(Color.black);
						field.setText("");
						
						System.out.println("Sending message");
					}
					
				}
			}
		}
	};
	
	
	private ActionListener broadcastAction = new ActionListener() {
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
	};
	
	public void setLabel(String s)  {
		output.setText(s);
		output.setForeground(Color.black);
		output.setVisible(true);
	}
}
