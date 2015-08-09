package com.grillecube.client;

/** this call contains every definition of game events */
public class GameEvent
{
	/** called before the game started */
	public static final int PRE_START = 0;
	
	/** called after the game started */
	public static final int POST_START = 1;
	
	/** called when the game stop: free ressources? */
	public static final int STOP = 2;
	
	/** called after the renderer is called (use it for opengl initialization!) */
	public static int POST_RENDERER_START = 3;
	
	/** unused */
	public static final int MAX_ID = 4;
}
