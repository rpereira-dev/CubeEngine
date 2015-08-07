package com.grillecube.client.mod.renderer.font;

import com.grillecube.client.Game;
import com.grillecube.client.event.IEvent;

public class EventFontPostStart implements IEvent
{
	@Override
	public void invoke(Game game)
	{		
		FontRenderer renderer = ModFontRenderer.instance().getFontRenderer();
		String str = new String("this is a test to check post init event!");
		renderer.addString(str, 5000);
	}
}
