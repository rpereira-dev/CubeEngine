package com.grillecube.client.renderer.font;

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
	private static Font DEFAULT_FONT;
	
	/** font models */
	private ArrayList<FontModel>	_fonts_model;
	
	@Override
	public void start()
	{
		this._program = new ProgramFont();
		this._fonts_model = new ArrayList<FontModel>();
		
		FontRenderer.DEFAULT_FONT = new Font("./assets/textures/font/font.png", 0);
		
		FontModel model = new FontModel(FontRenderer.DEFAULT_FONT, "Hello world");
		model.setPosition(-1, 1, 0);
		this._fonts_model.add(model);
	}

	@Override
	public void stop()
	{
		this._program.stop();
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
