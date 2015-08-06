package com.grillecube.client.mod.renderer.font;

import com.grillecube.client.Game;
import com.grillecube.common.mod.IMod;
import com.grillecube.common.mod.ModInfo;

import fr.toss.lib.Logger;

/**
 * Modding test class
 */
@ModInfo(author = "toss", version = "1.0")
public class ModFontRenderer implements IMod
{
	private static ModFontRenderer _instance;
	
	private FontRenderer _font_renderer;

	public ModFontRenderer()
	{
		_instance = this;
	}
	
	@Override
	public void initialize(Game game)
	{
		Logger.get().log(Logger.Level.DEBUG, "Loading particle mod!");
		
		this._font_renderer = new FontRenderer(game);
		game.getRenderer().registerRenderer(this._font_renderer);
	}

	@Override
	public void deinitialize(Game game)
	{
		Logger.get().log(Logger.Level.DEBUG, "Unloading particle mod!");
	}
	
	public static ModFontRenderer instance()
	{
		return (_instance);
	}
	
	public FontRenderer getFontRenderer()
	{
		return (this._font_renderer);
	}
}
