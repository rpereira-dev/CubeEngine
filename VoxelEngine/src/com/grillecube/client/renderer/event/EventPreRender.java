package com.grillecube.client.renderer.event;

import com.grillecube.client.renderer.MainRenderer;

public class EventPreRender extends EventRender
{
	public EventPreRender(MainRenderer renderer)
	{
		super(renderer);
	}

	@Override
	public String getName()
	{
		return ("Pre Render");
	}
}
