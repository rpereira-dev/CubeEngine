package com.grillecube.client.event;

import com.grillecube.client.Game;

public interface IEvent
{
	/** event name ID, for debug */
	public String getName();
	
	/** invocation of this event */
	public void invoke(Game game);
}
