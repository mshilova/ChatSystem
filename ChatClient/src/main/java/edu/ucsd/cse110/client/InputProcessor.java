package edu.ucsd.cse110.client;

import java.util.Scanner;

import javax.jms.JMSException;

public class InputProcessor {

    private Scanner input;
    
    public InputProcessor(){
    	input = new Scanner(System.in);
    }

    /** Prompts someone with the passed in string prompt
     * 	Returns a boolean value based on whether the promptee
     * 	enters yes or no or y or n or any caps variant. 
     *             ^tongue twister^
     * 
     * @return true if stdin == yes or y or any caps variant
     * @return false if stdin == no or n or any caps variant
     */
    public boolean yesNoPrompt(String prompt){
    	String answer;
    	while(true){
			System.out.println(prompt);
			answer = input.nextLine();
			if("yes".equalsIgnoreCase(answer) ||
				 "y".equalsIgnoreCase(answer))
				return true;
			else if("no".equalsIgnoreCase(answer)||
					 "n".equalsIgnoreCase(answer))
				return false;
		}
    }
    
    /* takes in two prompt strings and gets 2 responses from
     * a user. NO ERROR checking here boys.
     */
    public String[] twoPrompt(String prompt1, String prompt2){ 	
    	String[] answers = new String[2];
    	
    	System.out.println(prompt1);
    	answers[0] = input.nextLine();
    	System.out.println(prompt2);
    	answers[1] = input.nextLine();
    	
    	return answers;
    }
    
	public void processUserCommands( ChatClient client ) throws JMSException {
		  
		ChatCommander chatCommander = client.getChatCommander();
		
	    System.out.println("# Type 'help' for the list of available commands.");
	    String inputMessage;

	    while(true) {
	      inputMessage = input.nextLine();
	      
	      if(inputMessage.startsWith("help")) 
	          ChatClientApplication.printHelp();     // display the help message  
	      
	      else if(inputMessage.startsWith("exit")) 
	    	  processLogOff(chatCommander, client);  // go off-line
	        
	      else if(inputMessage.startsWith("listOnlineUsers")) 
	          client.listOnlineUsers();  // list all online users
	      
	      else if(inputMessage.equalsIgnoreCase("listChatRooms")) 
	          chatCommander.listChatRooms(); // list all chat rooms
	        
	      else if(inputMessage.startsWith("broadcast")) 
	          processBroadcast(chatCommander, inputMessage);   // broadcast the message

          else if(inputMessage.startsWith("createChatRoom")) 
	    	  processChatRoomCreation(chatCommander, inputMessage);  	 
	      
	      else if(inputMessage.startsWith("send")) 
	        processMessageToSend(client, inputMessage);	     // send a message to a specific user

	      else if ( chatCommander.chatRoomEntered(inputMessage) )  
	    	  processMessageForChatRoom(chatCommander, inputMessage); 
	      
	      else if ( inputMessage.startsWith( "invite " ) ) 
	    	  processInvitation(client, chatCommander, inputMessage);
	    	  
	      else if( inputMessage.startsWith("accept" ) )
	    	 chatCommander.acceptInvite( inputMessage );  
	      
	      else if ( inputMessage.startsWith( "leave " ) || inputMessage.startsWith( "Leave " ) ) 
	         processLeaveChatRoom(chatCommander, inputMessage);
	  
	      
	      else if ( inputMessage.startsWith( "inChatRoom " ) ) 
	    	 processListUsersInChatRoom(chatCommander, inputMessage);
	    	  
	      
	      else {	    	  
	        // invalid input, display input instructions again
	        System.out.println("Client did not recognize your input. Please try again.");
	        System.out.println("# Type 'help' for the list of commands");
	      }
	    }
	    
	  }

	
	public boolean processListUsersInChatRoom(ChatCommander chatCommander,
			String inputMessage) {
		
		String inChatRoomAndRoomName[] = inputMessage.split( " " );
		
		if ( 2 != inChatRoomAndRoomName.length ) {
			  System.out.println( "Invalid number of arguments.  Type in 'inChatRoom' followed by the room name." );
			  return false;
		}
		  
		return chatCommander.requestUsersInChatRoom( inChatRoomAndRoomName[1] );
		
	}

	
	
	public boolean processLeaveChatRoom(ChatCommander chatCommander,
			String inputMessage) throws JMSException {
		String leaveRoom[] = inputMessage.split( " " );
		  if ( 2 != leaveRoom.length ) {
		    System.out.println( "Please enter 'leave' followed by the name of the room." );
		    return false;
		  }
  
		  return chatCommander.leaveChatRoom( leaveRoom[1] );
	}
	
	

	public boolean processInvitation(ChatClient client,
			ChatCommander chatCommander, String inputMessage)
			throws JMSException {
		
		  System.out.println("invite ::: " + inputMessage);
		  
		  String invitation[] = inputMessage.split( " " );
		  
		  if ( invitation.length != 3 ) {
			  System.err.println( "Wrong number of arguments. Must be: invite chatRoom username" );
			  return false;
		  }
		  
		  if ( ! chatCommander.chatRoomEntered( invitation[1] ) ) {
			  System.err.println( "You entered a chat room name that does not exist or that you are not subscribed to." );
			  return false;
		  }
		  
		  if ( ! client.userOnline( invitation[2] )  ) {
			  System.out.println( "That user is not online or does not exist." );
			  return false;
		  }
		  
		  chatCommander.sendInvitation( invitation[2], invitation[1] ); // passing in the username and the room name
		  
		  return true;
	}

	
	public boolean processMessageForChatRoom(ChatCommander chatCommander,
			String inputMessage) throws JMSException {
	
		String roomName = inputMessage.substring( 0, inputMessage.indexOf(" ") );
		chatCommander.publishMessageToChatRoom( roomName, inputMessage.substring( roomName.length() + 1 ) );
		  
		return true;
	}

	
	public boolean processMessageToSend(ChatClient client, String inputMessage) {
		
		if ( ! inputMessage.contains( " " ) ) {
			System.out.println( "You must specify users and a message to send." );
			return false;
		}
		
		inputMessage = inputMessage.substring("send".length()+1);
		String userList = inputMessage.substring(0,inputMessage.indexOf(" "));
		String[] mailingList = userList.split(",");
		for(String recipient : mailingList) {
		  client.send(recipient,
		      inputMessage.substring(inputMessage.indexOf(" ")+1));
		}
		
		return true;
	}

	
	
	public boolean processChatRoomCreation(ChatCommander chatCommander,
			String inputMessage) {
		if ( inputMessage.length() <= "createChatRoom ".length() ||
			   inputMessage.substring( "createChatRoom ".length() ).contains( " " ) ) {
			  System.err.println( "Invalid room name. Please enter another name for your chat room."
			  		              + "The name may not contain spaces." );
			  return false;
		  }
		// create a chat-room
		  String room = "";
		  room = inputMessage.substring("createChatRoom".length()+1);
		  if ( room.length() < 1) {
			System.err.println( "Please give the chat room a name, for example: createChatRoom BossRoom" );
			return false;
		  }
		
  
		  // ChatRoom and ChatRoomManager logic handled in server, but topic is actually made in ChatClient
		  chatCommander.createChatRoom( room );		 
		  
		  return true;
	}

	public boolean processBroadcast(ChatCommander chatCommander,
			String inputMessage) {
		
		if ( ! inputMessage.contains( " " ) ) {
			System.out.println( "You didn't enter a message to broadcast" );
			return false;
		}
		
		inputMessage = inputMessage.substring("broadcast".length()+1);
		chatCommander.broadcast(inputMessage);
		
		return true;
		
	}

	

	public void processLogOff(ChatCommander commander, ChatClient client) {
		commander.leaveAllChatRooms();
		client.sendServer( Constants.LOGOFF, client.getUser().getUsername() );
		input.close();
		System.exit(0);  //you can probably test this method by testing that the exit status was 0
	}

}
	
