package edu.ucsd.cse110.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.jms.Destination;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class GuiOnlineUsersList extends JPanel {
	
	private ChatClientGUI frame;
	private JList<String> onlineUsersList;
	private Map<String,Destination> onlineUsers;
	private Map<String,GuiTextArea> userText;

	static DefaultListModel<String> listModel;
			
	public GuiOnlineUsersList (ChatClientGUI frame) {
		this.frame = frame;
		userText = new HashMap<String,GuiTextArea>();
		this.setVisible(true);
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createLineBorder(Color.black));
		initComponents();
		
		createList();
	}
	
	public void createList() {
			
		ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
            	onlineUsers = frame.getClient().getOnlineUsers();
        		// Add new Online Users
        		for(String user : onlineUsers.keySet())
        		{
        			if (listModel.indexOf(user)== -1)
        				listModel.addElement(user);
        			if(!userText.containsKey(user))  {
        				userText.put(user, new GuiTextArea());
        			}
           		}
        		
        		//Remove off-line users
        		for(int i = 0; i < listModel.size();i++)
        		{
        			if (!onlineUsers.containsKey(listModel.get(i)))
        				listModel.remove(i);
        			// TODO remove text area for logged off user
           		}
            }
        };
		
        Timer timer = new Timer( 500, actionListener );
        timer.start();
		
	}
		
	private void initComponents() {
		listModel = new DefaultListModel<String>();
		onlineUsersList = new javax.swing.JList<String>(listModel);
		
		JScrollPane scrollPane = new JScrollPane(onlineUsersList);
		this.add(scrollPane);
		
		onlineUsersList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// Set the text area to the user just selected
				if(e.getValueIsAdjusting()) {
					frame.getChatPage().getWestPanel().setTextArea(onlineUsersList.getSelectedValue());
				}
			}
			
		});
	}
	
	public GuiTextArea getTextArea(String s)  {
		return userText.get(s); // returning the text area for each user
	}
	
	public void appendTextReceive(String user, String message)  {
		userText.get(user).append("\n" + user + ": " + message);
	}
	
	public void appendTextSend(String user, String message) {
		userText.get(user).append("\nMe: " + message);

	}
	
	public ArrayList<String> getSelectedUsers() {
		return (ArrayList<String>)onlineUsersList.getSelectedValuesList();
	}
	
}
