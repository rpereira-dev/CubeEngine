package com.grillecube.client.event;

import com.grillecube.client.Game;

public interface IEvent
{
	/** invocation of this event */
	public void invoke(Game game, int eventID);
}
