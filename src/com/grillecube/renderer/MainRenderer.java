package com.grillecube.renderer;

import org.lwjgl.opengl.GL11;

import com.grillecube.renderer.program.Program;

public class MainRenderer
{
	public static final int	PROGRAM_TERRAIN = 0;
	
	public Program	_programs[];	//gl program
	
	
	public MainRenderer()
	{		
		this._programs = new Program[1];
	}
	
	/** call on initialization */
	public void	start()
	{
		this._programs[PROGRAM_TERRAIN] = new ProgramTerrain();
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public void	stop()
	{
		int	i;
		
		for (i = 0 ; i < this._programs.length ; i++)
		{
			this._programs[i].delete();
			this._programs[i] = null;
		}
	}

	/** main rendering function (screen is already cleared, and frame buffer will be swapped after this render */
	public void render()
	{
		
	}
	
	

}
