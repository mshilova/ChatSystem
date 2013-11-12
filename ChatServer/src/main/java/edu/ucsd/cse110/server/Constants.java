package edu.ucsd.cse110.server;

public interface Constants {
	public static String ACTIVEMQ_URL = "tcp://localhost:61616";
	public static String USERNAME = "max";	
	public static String PASSWORD = "pwd";
	public static String QUEUENAME = "test";
	
	// name of server queue from which the server reads its received messages
	public static String SERVERQUEUE = "server";
	// name of broadcast topic from which broadcasts are published and subscribed
	public static String BROADCAST = "broadcast";

	/*
	 * The following constants are used between the client and the server to
	 * communicate in messages. Set a Message's JMSType as one of these
	 * to let the server know how to process the message.
	 */
	// used between client and server to identify the type of request and response
	public static String LISTCHATROOMS = "listChatRooms";
	public static String ONLINEUSERS = "onlineUsers";
	public static String CREATECHATROOM = "createChatRoom";
	
	public static String VERIFYUSER = "verifyUser";
	public static String REGISTERUSER = "registerUser";

	public static String SETUSEROFFLINE = "setUserOffline";
	
	public static final int MINFIELDLENGTH = 4;
}