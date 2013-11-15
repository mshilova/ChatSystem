package edu.ucsd.cse110.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;

public class LoginPage extends JPanel {

	private JButton newUser, existingUser; // buttons for user choice / login
	private JButton Enter; // generic button
	private JTextField userField, passField; // text fields to hold user name and
	                                       // password
	private JFrame frame;
	private ChatClient client;
	private boolean newUserFlag; // default existing user
	
	public LoginPage(JFrame frame, ChatClient client)  {
		this.frame = frame; 
		this.client = client;
		this.setSize(600, 400);
		this.setLayout(null);
		log();
	}
	
	/*
	 * Showing options: new or existing user
	 */
	public void log()  {
		
		newUser = new JButton("New User");
		newUser.setLocation(120, 120);
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
		existingUser.setLocation(300, 120);
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
		passField = new JTextField();
	   
	   JButton nextAction;
	   if(newUserFlag)  {
		   nextAction = new JButton("Register");
	   }else {
		   nextAction = new JButton("Log in");
	   }
	   
       nextAction.setLocation(300, 300);
       nextAction.setSize(150, 50);
	      
	   // creating layout to align labels with textfields
       GroupLayout groupLayout = new GroupLayout(this);
       this.setLayout(groupLayout);
       groupLayout.setAutoCreateGaps(true);
       groupLayout.setAutoCreateContainerGaps(true);
       // create a sequential group for the horizontal axis
	       
       // aligning along horizontal axis
       GroupLayout.SequentialGroup hGroup = groupLayout.createSequentialGroup();
       hGroup.addGroup(groupLayout.createParallelGroup().addComponent(userLabel).addComponent(passLabel));
       hGroup.addGroup(groupLayout.createParallelGroup().addComponent(userField).addComponent(passField).addComponent(nextAction));
       groupLayout.setHorizontalGroup(hGroup);
	    // aligning along vertical axis (read documentation for more detail)   
       GroupLayout.SequentialGroup vGroup = groupLayout.createSequentialGroup();
       vGroup.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(userLabel).addComponent(userField));
       vGroup.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(passLabel).addComponent(passField));
       vGroup.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(nextAction));
       groupLayout.setVerticalGroup(vGroup);
	       
       nextAction.addActionListener(new ActionListener() {
    	   public void actionPerformed(ActionEvent a){
    		   if(newUserFlag)  {    		   
        		   
        		   
  	             
    			 // TODO register new user  
    		   }else{
    			   client.verifyUser(userField.getText(), passField.getText());
	               try {
	            	   Thread.sleep(1000);
	               } catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
	               }
	               
	               if(client.verified) {
	            	   client.setUser(userField.getText());
	            	   System.out.println("login verified");
	            	   setCloseOperation();
	            	   // proceed
	               } else {
	            	   System.out.println("login not verified");
	               } 
    		   }
	           } 
	       });
		
	}
	
	/*
	 * Log out the user when the window is closed.
	 * Called after user verification.
	 */
	private void setCloseOperation() {
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        client.sendServer(Constants.LOGOFF, client.getUser());
		        System.out.println("closing behavior set");
		    }
		});
	}

}
