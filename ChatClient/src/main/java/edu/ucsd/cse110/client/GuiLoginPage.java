package edu.ucsd.cse110.client;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.EtchedBorder;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class GuiLoginPage extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField userField;
	private JPasswordField passField; // text fields to hold user name and password
	private JLabel failLabel;
	
	private ChatClientGUI frame;
	private boolean newUserFlag; // default existing user
	
	public GuiLoginPage(ChatClientGUI frame)  {
		this.frame = frame;
		this.setSize(800, 600);
		this.setLayout(null);
	}
	
	/*
	 * Showing options: new or existing user
	 */
	public void log()  {
		
		/*
		 * BONUS
		 */
		JLabel picture = new JLabel();
		picture.setIcon(new ImageIcon("src/main/resources/michael.gif"));
		picture.setLocation(300, 100);
		picture.setSize(200, 200);
		picture.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		this.add(picture);
		
		JButton newUser = new JButton("New User");
		newUser.setLocation(230, 320);
		newUser.setSize(150, 50);
		this.add(newUser);
		newUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newUserFlag = true;	// setting the flag for the new user
				inputPage();	// going to the next page
			}
		});	
				
		JButton existingUser = new JButton("Existing User");
		existingUser.setLocation(420, 320);
		existingUser.setSize(150, 50);
		this.add(existingUser);
		existingUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// new User flag is false by default
				inputPage();	// going to the next page
			}	
		});
		
		frame.revalidate();
		frame.repaint();
	}
	
	
	public void inputPage() {
		
		// changing the title of the frame
		frame.setTitle("Type name and password");
		this.removeAll(); // clear the panels
		this.revalidate();	
		this.repaint();
		
		JLabel userLabel = new JLabel("Username: ");
		userField = new JTextField();
		userField.addKeyListener(enterOnEnter);
		
		JLabel passLabel = new JLabel("Password: ");
		passField = new JPasswordField();
		passField.addKeyListener(enterOnEnter);
		
		failLabel = new JLabel("Failed, please try again");
		failLabel.setForeground(Color.RED);
		failLabel.setVisible(false);
		
		JButton nextAction; // next action to take
		if(newUserFlag)  {
			nextAction = new JButton("Register");
		} else {
			nextAction = new JButton("Log in");
		}
		nextAction.setLocation(300, 300);
		nextAction.setSize(150, 50);
       
		// creating layout to align labels with text fields
		GroupLayout groupLayout = new GroupLayout(this);
		this.setLayout(groupLayout);
		groupLayout.setAutoCreateGaps(true);
		groupLayout.setAutoCreateContainerGaps(true);
		// create a sequential group for the horizontal axis
		
		// aligning along horizontal axis
		GroupLayout.SequentialGroup hGroup = groupLayout.createSequentialGroup();
		hGroup.addGroup(groupLayout.createParallelGroup().addComponent(userLabel).addComponent(passLabel).addComponent(nextAction));
		hGroup.addGroup(groupLayout.createParallelGroup().addComponent(userField).addComponent(passField).addComponent(failLabel));
		groupLayout.setHorizontalGroup(hGroup);
		// aligning along vertical axis (read documentation for more detail)   
		GroupLayout.SequentialGroup vGroup = groupLayout.createSequentialGroup();
		vGroup.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(userLabel).addComponent(userField));
		vGroup.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(passLabel).addComponent(passField));
		vGroup.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(nextAction).addComponent(failLabel));
		groupLayout.setVerticalGroup(vGroup);
		
		// action to take after login or register buttons were pressed
		nextAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a){
				attemptLogin();
				} 
		});
		
	}
	
	
	/**
	 * Attempt to register or log in using the input user name and password
	 */
	public void attemptLogin() {
		if(newUserFlag)  {    		    	             
			frame.getClient().registerUser(userField.getText(), new String(passField.getPassword()));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(frame.getClient().getUser().getVerified()) {
				failLabel.setVisible(false);
				frame.getClient().setUser(new User(userField.getText(), passField.getPassword().toString(),true));
				frame.remove(this);
				frame.setChatPage();
			} else {
				failLabel.setVisible(true);
			} 
		} else{
			frame.getClient().verifyUser(userField.getText(), new String(passField.getPassword()));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(frame.getClient().getUser().getVerified()) {
				failLabel.setVisible(false);
				frame.getClient().setUser(new User(userField.getText(), passField.getPassword().toString(),true));
				frame.remove(this);
				frame.setChatPage();
			} else {
				failLabel.setVisible(true);
			} 
		}
	}
	
	
	/*
	 * Attempt to register or log in when the enter key is pressed
	 */
	private KeyAdapter enterOnEnter = new KeyAdapter() {
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER) {
				attemptLogin();
			}
		}
	};
	
}
