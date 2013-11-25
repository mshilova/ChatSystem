package edu.ucsd.cse110.client;

import javax.swing.JPanel;

public class GuiPanelWest extends JPanel {

	private GuiChatPage page;
	
	public GuiPanelWest(GuiChatPage page) {
		this.page = page;
		
//		JTabbedPane tabbedPane = new JTabbedPane();
		
//		textArea = new JTextArea(30,45);
//		textArea.setLineWrap(true);
//		textArea.setEditable(false);
//		JScrollPane scrollPane = new JScrollPane(textArea); 
//		this.add(scrollPane);
//		scrollPane.setVisible(true);
//		this.add(page.getEastPanel().getOnlineUsersList().getTextArea("General"));
		this.setVisible(true);
	}
	
	
	/*
	 * Update your text area from a sender
	 */
	public void updateTextReceive(String sender, String message) {
		page.getEastPanel().getOnlineUsersList().appendTextReceive(sender, message);
		if(this.getComponentCount()==0) {
			setTextArea(sender);
		}
	}
	
	/*
	 * Update your text area for messages you send
	 */
	public void updateTextSend(String receiver, String message) {
		page.getEastPanel().getOnlineUsersList().appendTextSend(receiver, message);
		if(this.getComponentCount()==0) {
			setTextArea(receiver);
		}
	}
	
	public void setTextArea(String s) {
		this.removeAll();
		this.add(page.getEastPanel().getOnlineUsersList().getTextArea(s));
		this.revalidate();
		this.repaint();
	}

}
