package edu.ucsd.cse110.client;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class GuiLoginPage extends JPanel {

	private JButton newUser, existingUser; // buttons for user choice / login
	private JTextField userField;
	private JPasswordField passField; // text fields to hold user name and
	                                       // password
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
		
		newUser = new JButton("New User");
		newUser.setLocation(250, 120);
		newUser.setSize(150, 50);
		this.add(newUser);
		// setting the flag for the new user
		newUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newUserFlag = true;
				// going to the next page
				inputPage();
			}
		});	
				
		existingUser = new JButton("Existing User");
		existingUser.setLocation(400, 120);
		existingUser.setSize(150, 50);
		this.add(existingUser);
		existingUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// new User flag is false by default
				// going to the next page
				inputPage();
			}	
		});
	}
	
	
	public void inputPage() {
		
		// changing the title of the frame
		frame.setTitle("Type name and password");
		this.removeAll(); // clear the panels
		this.revalidate();	
		this.repaint();
		
		JLabel userLabel = new JLabel("Username: ");
		userField = new JTextField();
		JLabel passLabel = new JLabel("Password: ");
		passField = new JPasswordField();
		final JLabel output = new JLabel("Failed, please try again");
		output.setForeground(Color.RED);
		output.setVisible(false);
	   
	   JButton nextAction; // next action to take
	   if(newUserFlag)  {
		   nextAction = new JButton("Register");
	   }else {
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
       hGroup.addGroup(groupLayout.createParallelGroup().addComponent(userField).addComponent(passField).addComponent(output));
       groupLayout.setHorizontalGroup(hGroup);
	    // aligning along vertical axis (read documentation for more detail)   
       GroupLayout.SequentialGroup vGroup = groupLayout.createSequentialGroup();
       vGroup.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(userLabel).addComponent(userField));
       vGroup.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(passLabel).addComponent(passField));
       vGroup.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(nextAction).addComponent(output));
       groupLayout.setVerticalGroup(vGroup);
	     
       // action to take after login or register buttons were pressed
       nextAction.addActionListener(new ActionListener() {
    	   public void actionPerformed(ActionEvent a){
    		   if(newUserFlag)  {    		    	             
       			   frame.getClient().registerUser(userField.getText(), new String(passField.getPassword()));
	               try {
	            	   Thread.sleep(1000);
	               } catch (InterruptedException e) {
					e.printStackTrace();
	               }
	               if(frame.getClient().getUser().getVerified()) {
	            	   output.setVisible(false);
	            	   frame.getClient().setUser(new User(userField.getText(), passField.getPassword().toString(),true));
	            	   System.out.println("registration verified");
	               } else {
	            	   System.out.println("registration not verified");
	            	   output.setVisible(true);
	            	   
	               } 
    		   }else{
    			   frame.getClient().verifyUser(userField.getText(), new String(passField.getPassword()));
	               try {
	            	   Thread.sleep(1000);
	               } catch (InterruptedException e) {
					e.printStackTrace();
	               }
	               
	               if(frame.getClient().getUser().getVerified()) {
	            	   output.setVisible(false);
	            	   frame.getClient().setUser(new User(userField.getText(), passField.getPassword().toString(),true));
	            	   System.out.println("User set: " + frame.getClient().getUser().getUsername());
	            	   System.out.println("login verified");
	               } else {
	            	   System.out.println("login not verified");
	            	   output.setVisible(true);
	               } 
    		   }
	        } 
       });
		
	}
	
}
