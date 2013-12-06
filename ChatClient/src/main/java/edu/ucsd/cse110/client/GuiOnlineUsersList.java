package edu.ucsd.cse110.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Destination;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class GuiOnlineUsersList extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private ChatClientGUI frame;
	private JList<String> onlineUsersList;
	private Map<String,Destination> onlineUsers;
	private Map<String,GuiTextArea> userText;

	static DefaultListModel<String> listModel;
			
	public GuiOnlineUsersList (ChatClientGUI frame) {
		this.frame = frame;
		this.setVisible(true);
		this.setLayout(new BorderLayout());
		userText = new HashMap<String,GuiTextArea>();
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
        			if (!onlineUsers.containsKey(listModel.get(i))) {
        				listModel.remove(i);
        				userText.remove(i);
        			}
           		}
            }
        };
		
        Timer timer = new Timer( 500, actionListener );
        timer.start();
		
	}
		
	private void initComponents() {
		JLabel label = new JLabel("Online Users");
		label.setPreferredSize(new Dimension(200,25));
		this.add(label,BorderLayout.NORTH);
		
		listModel = new DefaultListModel<String>();
		onlineUsersList = new javax.swing.JList<String>(listModel);
		
		JScrollPane scrollPane = new JScrollPane(onlineUsersList);
		this.add(scrollPane);
		
		onlineUsersList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// Set the text area to the user just selected
				if(e.getValueIsAdjusting()) {
					frame.getPanelWest().getGeneralTab().setTextArea(onlineUsersList.getSelectedValue());
				}
			}
			
		});
	}
	
	
	/**
	 * 
	 * @param name	name of the user whose text area is to be received
	 * @return	the text area of the user
	 */
	public GuiTextArea getTextArea(String name)  {
		return userText.get(name);
	}
	
	
	/**
	 * Append the user text area for messages received
	 * @param user	the user whose text area is to appended
	 * @param message	the message to append
	 */
	public void appendTextReceive(String user, String message)  {
		userText.get(user).append("\n" + user + ": " + message);
	}
	
	
	/**
	 * Append the user text area for messages sent
	 * @param user	the user whose text area is to be appended
	 * @param message	the message to append
	 */
	public void appendTextSend(String user, String message) {
		userText.get(user).append("\nMe: " + message);
	}
	
	
	/**
	 * 
	 * @return	list of users selected in the online users list
	 */
	public ArrayList<String> getSelectedUsers() {
		if(!onlineUsersList.getSelectedValuesList().isEmpty()) {
			return (ArrayList<String>)onlineUsersList.getSelectedValuesList();
		}
		return new ArrayList<String>();
	}
	
	
	/**
	 * Select a user in the online users list
	 * @param name
	 */
	public void setReplyUser(String name)  {
		onlineUsersList.setSelectedValue(name, true);
	}
	
	
	/**
	 * Clear selection
	 */
	public void deselectAll() {
		onlineUsersList.clearSelection();
	}
}
