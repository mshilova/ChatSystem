package edu.ucsd.cse110.server;

public interface Constants {
	public static String ACTIVEMQ_URL = "tcp://localhost:61616";
	public static String USERNAME = "max";	
	public static String PASSWORD = "pwd";	
	public static String QUEUENAME = "test";
	// adding a server queue from which the server reads its received messages
	public static String SERVERQUEUE = "server";
	public static String BROADCAST = "broadcastTopic";
}