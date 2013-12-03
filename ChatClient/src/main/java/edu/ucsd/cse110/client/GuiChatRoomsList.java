package edu.ucsd.cse110.client;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class GuiChatRoomsList extends JPanel {

	private static final long serialVersionUID = 1L;
	private ChatClientGUI frame;
	private DefaultListModel<String> listModel;
	private JList<String> chatRoomsList;
	
	public GuiChatRoomsList(ChatClientGUI gui) {
		this.frame = gui;
		
		this.setLayout(new BorderLayout());
		
		JLabel label = new JLabel("Chat Rooms");
		label.setPreferredSize(new Dimension(200,25));
		this.add(label, BorderLayout.NORTH);
		
		listModel = new DefaultListModel<String>();
		chatRoomsList = new JList<String>(listModel);
		this.add(new JScrollPane(chatRoomsList), BorderLayout.CENTER);
		
		updateList();
	}

	
	/**
	 * Update the list as changes are made
	 */
	public void updateList() {
		// TODO implement
		/*
		 * Either update the list as rooms are added/removed from the list in
		 * ChatCommander, or periodically replace the current list with the list
		 * in ChatCommander, which will have the latest ChatRoom information.
		 */
	}
	
	
	/**
	 * Get the selected chat room if one is selected
	 * @return	the name of the selected chat room
	 */
	public String getSelection() {
		return null;	// TODO implement
	}
	
	
	/**
	 * Deselect all elements in the list
	 */
	public void deselectAll() {
		// TODO implement
	}
	
}
