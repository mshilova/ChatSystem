package edu.ucsd.cse110.server;

import javax.jms.Message;

public interface Manager {
	
	public abstract boolean removeItem(Message message);
	public abstract boolean addItem(Message message);
	public abstract boolean containsItem(String item);
	public abstract void removeAllItems();

}