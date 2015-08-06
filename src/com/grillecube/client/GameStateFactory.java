package com.grillecube.client;

public class GameStateFactory
{
	private int _next_id;
	
	public GameStateFactory()
	{
		this._next_id = GameState.DEFAULT_GAME_STATE_MAX_ID;
	}

	public GameState registerNewState()
	{
		GameState new_state = new GameState(this._next_id);
		++this._next_id;
		return (new_state);
	}
}
