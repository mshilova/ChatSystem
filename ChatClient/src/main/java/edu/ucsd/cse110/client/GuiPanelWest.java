package edu.ucsd.cse110.client;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class GuiPanelWest extends JPanel {

	private ChatClientGUI frame;
	private JPanel general;
	private JPanel chatRooms;
	private JComboBox<String> chatRoomList;
	private HashMap<String,GuiTextArea> chatRoomTextAreas;
	
	public GuiPanelWest(ChatClientGUI gui) {
		this.frame = gui;
		chatRoomTextAreas = new HashMap<String,GuiTextArea>();
//        this.setBorder(BorderFactory.createLineBorder(Color.black));

		JTabbedPane tabbedPane = new JTabbedPane();
		general = new JPanel();
		general.setPreferredSize(new Dimension(500, 440));
		chatRooms = new JPanel();
		chatRooms.setPreferredSize(new Dimension(500, 440));
		chatRooms.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		//adding tabs
		tabbedPane.addTab("General", general);
		//TODO add drop down to have multiple chat rooms
		tabbedPane.addTab("ChatRooms", chatRooms);
		// making an empty drop down list for chatrooms
		chatRoomList = new JComboBox<String>();
		chatRoomList.setPreferredSize(new Dimension(200, 25));
		chatRoomList.setEditable(false);
		chatRoomList.addActionListener(chatRoomSelect);
		
		chatRooms.add(chatRoomList);
		this.add(tabbedPane);
		this.setVisible(true);
	}
	/*
	 * Adding chat room name to the drop down menu in the chatroom tab.
	 */
	public void addChatRoom(String s)  {
		chatRoomTextAreas.put(s, new GuiTextArea());
		chatRoomList.addItem(s);
		
	}
	
	
	public void addChatRoomInvite(String s)  {
		//TODO make it stand out from 
		// the created rooms
		// idea: connect to chat room when selected
		
	    int reply = JOptionPane.showConfirmDialog(null, "Would you like to join the chat room '" + s + "'", "Invitaion", JOptionPane.YES_NO_OPTION);
	    
        if (reply == JOptionPane.YES_OPTION) {
//          JOptionPane.showMessageDialog(null, "HELLO");
        	System.out.println("yes was clicked");
        	chatRoomList.addItem(s);
        	chatRoomTextAreas.put(s, new GuiTextArea());
        }
        else {
//           JOptionPane.showMessageDialog(null, "GOODBYE");
        	System.out.println("no was clicked");
           System.exit(0);
        }
		
		
	}
	
	//TODO when user accept chat room invite, add that chat room text area
	
	public String getSelectedRoom()  {
		return (String)chatRoomList.getSelectedItem();
	}
	public void removeRoom(String s) {
		chatRoomList.removeItem(s);
		// might have to repaint, not sure yet
	}
	
	/*
	 * Update your text area from a sender
	 */
	public void updateTextReceive(String sender, String message) {
		frame.getEastPanel().getOnlineUsersList().appendTextReceive(sender, message);
		setTextArea(sender);
		// select the senders text area for reply
		frame.getEastPanel().getOnlineUsersList().setReplyUser(sender);
	}
	
	/*
	 * Update your text area for messages you send
	 */
	public void updateTextSend(String receiver, String message) {
		frame.getEastPanel().getOnlineUsersList().appendTextSend(receiver, message);
		if(general.getComponentCount()==0) {
			setTextArea(receiver);
		}
	}
	
	public void setTextArea(String s) {
		general.removeAll();
		general.add(frame.getEastPanel().getOnlineUsersList().getTextArea(s));
		general.revalidate();
		general.repaint();
	}
	
	public void setRoomTextArea(String s) {
//		for(JComponent c: (JComponent[])chatRooms.getComponents())  {
//			if(c instanceof GuiTextArea)  {
//				chatRooms.remove(c);
//			}			
//		}
		chatRooms.add(chatRoomTextAreas.get(s));
		chatRooms.revalidate();
		chatRooms.repaint();
	}
	
	/*
	 * Update text area in chatbox
	 */
	
	private ActionListener chatRoomSelect = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			String room =	(String)((JComboBox)e.getSource()).getSelectedItem();
			setRoomTextArea(room);
			System.out.println("inside room action listener, room is : " + room);
		}
		
		
		
	};

}
