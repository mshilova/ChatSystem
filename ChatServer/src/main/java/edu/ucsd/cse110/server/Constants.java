package edu.ucsd.cse110.server;

public interface Constants {
	public static final String ACTIVEMQ_URL = "tcp://localhost:61616";
	
	// name of server queue from which the server reads its received messages
	public static final String SERVERQUEUE = "server";
	// name of broadcast topic from which broadcasts are published and subscribed
	public static final String BROADCAST = "broadcast";

	/*
	 * The following constants are used between the client and the server to
	 * communicate in messages. Set a Message's JMSType as one of these
	 * to let the server know how to process the message.
	 */
	// used between client and server to identify the type of request and response
	public static final String LISTCHATROOMS = "listChatRooms";
	public static final String ONLINEUSERS = "onlineUsers";
	public static final String CREATECHATROOM = "createChatRoom";
	
	public static final String VERIFYUSER = "verifyUser";
	public static final String REGISTERUSER = "registerUser";

	public static final String LOGOFF = "setUserOffline";
	public static final String RESPONSE = "success";
	
	public static final int MINFIELDLENGTH = 4;
}