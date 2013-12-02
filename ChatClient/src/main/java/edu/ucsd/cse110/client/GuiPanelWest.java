package edu.ucsd.cse110.client;

import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GuiPanelWest extends JPanel {

	private static final long serialVersionUID = 1L;
	private ChatClientGUI frame;
	private GuiTabGeneral generalTab;
	private GuiTabChatRooms chatRoomsTab;
	private final JTabbedPane tabbedPane;
	
	public GuiPanelWest(ChatClientGUI gui) {
		this.frame = gui;
		

		tabbedPane = new JTabbedPane();
		//adding tabs
		tabbedPane.addTab("General", generalTab = new GuiTabGeneral(frame));
		tabbedPane.addTab("ChatRooms", chatRoomsTab = new GuiTabChatRooms(frame));
		
		tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if(tabbedPane.getSelectedComponent() instanceof GuiTabChatRooms) {
					frame.getEastPanel().getOnlineUsersList().deselectAll();
					frame.getPanelSouth().getInputBox().setLabel("Sending messages to Chat Rooms.");
				}else if(tabbedPane.getSelectedComponent() instanceof GuiTabGeneral) {
					frame.getEastPanel().getOnlineUsersList().deselectAll();
					frame.getPanelSouth().getInputBox().setLabel("Sending messages to Users.");
				}
			}
		});
		
		this.add(tabbedPane);
		this.setVisible(true);
	}
	
	public GuiTabGeneral getGeneralTab() {
		return this.generalTab;
	}

	public GuiTabChatRooms getChatRoomsTab() {
		return this.chatRoomsTab;
	}
	
	public Component getSelectedTab() {
		return tabbedPane.getSelectedComponent();
	}
	
	public void setSelectedTab(String type)  {
		if(type.equals("general"))  {
			tabbedPane.setSelectedComponent(generalTab);
		} else if(type.equals("chatrooms")) {
			tabbedPane.setSelectedComponent(chatRoomsTab);
		}
		
	}
}
