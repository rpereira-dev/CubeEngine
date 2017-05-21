package com.grillecube.client.renderer.event;

import com.grillecube.client.renderer.MainRenderer;

public class EventPostRender extends EventRender
{
	public EventPostRender(MainRenderer renderer)
	{
		super(renderer);
	}
	
	@Override
	public String getName()
	{
		return ("Post Render");
	}
}
