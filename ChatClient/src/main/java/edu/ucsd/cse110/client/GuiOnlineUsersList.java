package edu.ucsd.cse110.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.jms.Destination;
import javax.swing.Timer;

public class GuiOnlineUsersList extends JPanel {
	
	private ChatClientGUI frame;
	private JList<String> onlineUsersList;
	private Map<String,Destination> onlineUsers;

	static DefaultListModel<String> listModel;
			
	public GuiOnlineUsersList (ChatClientGUI frame) {
		this.frame = frame;
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
           		}
        		
        		//Remove off-line users
        		for(int i = 0; i < listModel.size();i++)
        		{
        			if (!onlineUsers.containsKey(listModel.get(i)))
        				listModel.remove(i);
           		}
            }
        };
		
        Timer timer = new Timer( 500, actionListener );
        timer.start();
		
	}
		
	private void initComponents() {
		listModel = new DefaultListModel<String>();
		onlineUsersList = new javax.swing.JList<String>(listModel);
		this.add(onlineUsersList);
	}
	
	
	public List<String> getSelectedUsers() {
		return onlineUsersList.getSelectedValuesList();
	}
	
}
