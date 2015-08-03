package com.grillecube.client.renderer.font;

import java.util.ArrayList;

import com.grillecube.client.renderer.Camera;
import com.grillecube.client.renderer.IRenderer;
import com.grillecube.client.world.WorldClient;

public class FontRenderer implements IRenderer
{
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
	public void render(WorldClient world, Camera camera)
	{
		this._program.useStart();
		for (FontModel model : this._fonts_model)
		{
			this._program.bindFontModel(model);
			model.render();
		}
		this._program.useStop();
	}

}
