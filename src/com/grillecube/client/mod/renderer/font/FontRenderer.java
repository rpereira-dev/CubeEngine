package com.grillecube.client.mod.renderer.font;

import java.util.ArrayList;

import com.grillecube.client.Game;
import com.grillecube.client.renderer.ARenderer;

public class FontRenderer extends ARenderer
{
	public FontRenderer(Game game)
	{
		super(game);
	}

	/** rendering program */
	private ProgramFont	_program;
	
	/** fonts */
	public static Font DEFAULT_FONT;
	
	/** font models */
	private ArrayList<FontModel>	_fonts_model;
	
	@Override
	public void start()
	{
		this._program = new ProgramFont();
		this._fonts_model = new ArrayList<FontModel>();
		
		FontRenderer.DEFAULT_FONT = new Font("./assets/textures/font/font.png", -0.2f);
	}

	@Override
	public void stop()
	{
		this._program.stop();
	}
	
	/** add a string to the renderer:
	 *
	 * - coordinate system (for posx, posy, posz) is gl one (-1 ; 1)
	 * - time is in MS, and is how long the string should be rendered (FontModel.INFINITE_TIMER if infinite)
	 **/
	public void addString(Font font, String str, float posx, float posy, float posz, long timer)
	{
		FontModel model = new FontModel(font, str, timer);
		model.setPosition(posx, posy, posz);
		this._fonts_model.add(model);
	}
	
	public void addString(Font font, String str, int posx, int posy, int posz)
	{
		this.addString(font, str, posx, posy, posz);
	}

	public void addString(Font font, String str, int time)
	{
		this.addString(font, str, -1, 1, 0, time);
	}
	
	public void addString(Font font, String str)
	{
		this.addString(font, str, -1, 1, 0, FontModel.INFINITE_TIMER);
	}
	
	public void addString(String str)
	{
		this.addString(FontRenderer.DEFAULT_FONT, str, -1, 1, 0, FontModel.INFINITE_TIMER);
	}
	
	/** remove every string from the renderer */
	public void clearStringRenderer()
	{
		for (FontModel model : this._fonts_model)
		{
			model.destroy();
		}
		this._fonts_model.clear();
	}
	
	@Override
	public void render()
	{
		FontModel	model;
		int 		i;
		
		this._program.useStart();
		
		i = 0;
		while (i < this._fonts_model.size())
		{
			model = this._fonts_model.get(i);
			model.update();
			if (model.hasTimerEnded())
			{
				this._fonts_model.remove(i);
				continue ;
			}
			this._program.bindFontModel(model);
			model.render();
			i++;
		}
		
		this._program.useStop();
	}

}
