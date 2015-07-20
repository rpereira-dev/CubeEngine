package com.grillecube.renderer;

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
	
	

}
