package com.grillecube.client.renderer.font;

import java.util.ArrayList;

import com.grillecube.client.Game;
import com.grillecube.client.renderer.Renderer;

public class FontRenderer extends Renderer
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
	private ArrayList<FontModel> _fonts_model;
	
	private FontModel _debug_font_model;
	
	@Override
	public void start()
	{
		FontRenderer.DEFAULT_FONT = new Font("./assets/textures/font/font.png", -0.2f);

		this._program = new ProgramFont();
		this._fonts_model = new ArrayList<FontModel>();
		_debug_font_model = new FontModel(DEFAULT_FONT, "", FontModel.INFINITE_TIMER);
		_debug_font_model.setPosition(-1, 1, 0);
		this.addFontModel(_debug_font_model);
	}
	
	/** add the font model to the renderer */
	public void addFontModel(FontModel model)
	{
		this._fonts_model.add(model);
	}
	
	/** add a string to the renderer:
	 *
	 * - coordinate system (for posx, posy, posz) is gl one (-1 ; 1)
	 * - time is in MS, and is how long the string should be rendered (FontModel.INFINITE_TIMER if infinite)
	 **/
	public FontModel addString(Font font, String str, float posx, float posy, float posz, long timer)
	{
		FontModel model = new FontModel(font, str, timer);
		model.setPosition(posx, posy, posz);
		this.addFontModel(model);
		return (model);
	}
	
	public FontModel addString(String str, float posx, float posy, float posz, long timer)
	{
		return (this.addString(FontRenderer.DEFAULT_FONT, str, posx, posy, posz, timer));
	}
	
	public FontModel addString(String str, float posx, float posy, float posz)
	{
		return (this.addString(FontRenderer.DEFAULT_FONT, str, posx, posy, posz, FontModel.INFINITE_TIMER));
	}
	
	public FontModel addString(Font font, String str, int posx, int posy, int posz)
	{
		return (this.addString(font, str, posx, posy, posz));
	}

	public FontModel addString(Font font, String str, int time)
	{
		return (this.addString(font, str, -1, 1, 0, time));
	}
	
	public FontModel addString(Font font, String str)
	{
		return (this.addString(font, str, -1, 1, 0, FontModel.INFINITE_TIMER));
	}
	
	public FontModel addString(String str, long timer)
	{
		return (this.addString(FontRenderer.DEFAULT_FONT, str, -1, 1, 0, timer));
	}
	
	public FontModel addString(String str)
	{
		return (this.addString(FontRenderer.DEFAULT_FONT, str, -1, 1, 0, FontModel.INFINITE_TIMER));
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
		this._program.useStart();
		this._debug_font_model.setText("pos: " + this.getCamera().getPosition() + "\n" + "look: " + this.getCamera().getLookVec());
		for (int i = 0 ; i < this._fonts_model.size() ; i++)
		{
			FontModel model = this._fonts_model.get(i);
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
