package edu.ucsd.cse110.client;

import java.util.HashMap;
import java.util.Scanner;

/**
 * Skip the selection and register/log in portions of the GUI and go directly
 * to the main chat page to save time.
 */
public class SkipGuiLogin {

	public static ChatClientGUI skip(ChatClient client) {
		
		// from UserPass.list
		HashMap<String,String> users = new HashMap<String,String>();
		users.put("Bonnie", "rabbit");
		users.put("Hrach", "turtle");
		users.put("Kyle", "password");
		users.put("Kacy", "password");
		users.put("Masha", "password");
		users.put("Nobel", "password");
		users.put("Savuthy", "password");
		users.put("test",  "test");
		users.put("user", "pass");
		
		System.out.println("Skipping log in GUI. Username:");
		
		Scanner input = new Scanner(System.in);
		String username = input.nextLine();
		
		while(!users.containsKey(username)) {
			System.out.println("Invalid user.");
			username = input.nextLine();
		}
		
		input.close();
		
		String password = users.get(username);
		
		client.verifyUser(username, password);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		ChatClientGUI gui = new ChatClientGUI(client);;
		if(client.getUser().getVerified()) {
			client.setUser(new User(username,password,true));
			gui.setChatPage();
		}
		System.out.println("Ready.");
		return gui;
	}

}
