package com.grillecube.engine.event.renderer;

import com.grillecube.engine.renderer.MainRenderer;

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
