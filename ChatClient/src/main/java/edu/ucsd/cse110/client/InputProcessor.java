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
	      
	      if(inputMessage.startsWith("help")) {
	        // display the help message
	        ChatClientApplication.printHelp();
	        
	      } else if(inputMessage.startsWith("exit")) {
	        // go off-line
				client.sendServer( Constants.LOGOFF, client.getUser().getUsername() );
				input.close();
				System.exit(0);
	        
	      } else if(inputMessage.startsWith("listOnlineUsers")) {
	        // list all online users
	        client.listOnlineUsers();
	        
	      } else if(inputMessage.startsWith("listChatRooms")) {
	        // list all chat rooms
	        chatCommander.listChatRooms();
	        
	      } else if(inputMessage.startsWith("broadcast")) {
	        // broadcast the message
	        inputMessage = inputMessage.substring("broadcast".length()+1);
	        chatCommander.broadcast(inputMessage);
	        
	      } else if(inputMessage.startsWith("createChatRoom")) {
	    	  
	    	  if ( inputMessage.length() <= "createChatRoom ".length() ||
	    		   inputMessage.substring( "createChatRoom ".length() ).contains( " " ) ) {
	    		  System.err.println( "Invalid room name. Please enter another name for your chat room."
	    		  		              + "The name may not contain spaces." );
	    		  continue;
	    	  }
	        // create a chat-room
	    	  String room = "";
	    	  room = inputMessage.substring("createChatRoom".length()+1);
	    	  if ( room.length() < 1) {
	        	System.err.println( "Please give the chat room a name, for example: createChatRoom BossRoom" );
	        	continue;
	    	  }
	    	
	  
	    	  // ChatRoom and ChatRoomManager logic handled in server, but topic is actually made in ChatClient
	  		  client.sendServer( Constants.CREATECHATROOM, chatCommander.getClient().getUser().getUsername()+" " +room ); 
	  		  chatCommander.add( room );  	  //TODO this was the line changed in ChatClient
	      
	      } else if(inputMessage.startsWith("send")) {
	        // send a message to a specific user
	        inputMessage = inputMessage.substring("send".length()+1);
	        String userList = inputMessage.substring(0,inputMessage.indexOf(" "));
	        String[] mailingList = userList.split(",");
	        for(String recipient : mailingList) {
	          client.send(recipient,
	              inputMessage.substring(inputMessage.indexOf(" ")+1));
	        }
	        
	        //TODO Causing problems when  inviting offline users. not sure why.
	      } else if ( chatCommander.chatRoomEntered(inputMessage) )  {
	    	  String roomName = inputMessage.substring( 0, inputMessage.indexOf(" ") );
	    	  chatCommander.publishMessageToChatRoom( roomName, inputMessage.substring( roomName.length() + 1 ) ); 
	      }
	      else if ( inputMessage.startsWith( "invite " ) ) {
	    	 System.out.println("invite ::: " + inputMessage); 
	    	  String invitation[] = inputMessage.split( " " );
	    	  if ( invitation.length != 3 ) {
	    		  System.err.println( "Wrong number of arguments. Must be: invite chatRoom username" );
	    		  continue;
	    	  }
	    	  if ( ! chatCommander.chatRoomEntered( invitation[1] ) ) {
	    		  System.err.println( "You entered a chat room name that does not exist or that you are not subscribed to." );
	    		  continue;
	    	  }
	    	  if ( ! client.userOnline( invitation[2] )  ) {
	    		  System.out.println( "That user is not online or does not exist." );
	    		  continue;
	    	  }
	    	  
	    	  chatCommander.sendInvitation( invitation[2], invitation[1] ); // passing in the username and the room name
	    	  
	      }else if( inputMessage.startsWith("accept" ) )
	    	 chatCommander.acceptInvite( inputMessage );    
	      else {	    	  System.out.println("else..");

	        // invalid input, display input instructions again
	        System.out.println("Client did not recognize your input. Please try again.");
	        System.out.println("# Type 'help' for the list of commands");
	      }
	    }
	  }

}
	
