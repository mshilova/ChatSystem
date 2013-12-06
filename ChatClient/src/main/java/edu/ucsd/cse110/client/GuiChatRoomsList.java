package edu.ucsd.cse110.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.jms.JMSException;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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
		chatRoomsList.addListSelectionListener(selectionListener);
		
	}

	
	/**
	 * Update the list as changes are made
	 */
	public void updateList(List<String> list) {
		listModel.removeAllElements();
		for(String s : list) {
			listModel.addElement(s);
		}
		this.revalidate();
		this.repaint();
	}
	
	
	/**
	 * Get the selected chat room if one is selected
	 * @return	the name of the selected chat room
	 */
	public String getSelection() {
		return chatRoomsList.getSelectedValue();
	}
	
	
	/**
	 * Deselect all elements in the list
	 */
	public void deselectAll() {
		chatRoomsList.clearSelection();
	}
	
	private ListSelectionListener selectionListener = new ListSelectionListener() {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if(getSelection() != null) {
				String room = getSelection();
				deselectAll();
				if(!frame.getPanelWest().getChatRoomsTab().myChatRooms().contains(room)) {
					int reply = JOptionPane.showConfirmDialog(
							null,
							"Would you like to join the chat room '" + room + "'?", "Request", JOptionPane.YES_NO_OPTION);
				    
			        if (reply == JOptionPane.YES_OPTION) {
			        	try {
							frame.getClient().getChatCommander().requestInvite(room);
						} catch (JMSException e1) {
							e1.printStackTrace();
						}
			        }
				}
			}
		}
	};
}
