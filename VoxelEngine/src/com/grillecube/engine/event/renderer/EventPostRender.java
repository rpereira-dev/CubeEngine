package com.grillecube.engine.event.renderer;

import com.grillecube.engine.renderer.MainRenderer;

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
