package com.grillecube.client;

public class GameState
{
	/** default game state */
	public static final GameState NONE		= new GameState(0);
	public static final GameState RUNNING	= new GameState(1);
	public static final int DEFAULT_GAME_STATE_MAX_ID = 2;
	
	private int _id;
	private long _value;
	
	public GameState(int id)
	{
		this._id = id;
		this._value = (long) Math.pow(2, id);
	}
	
	public int getID()
	{
		return (this._id);
	}
	
	public long getValue()
	{
		return (this._value);
	}
	
	/** return true if the game has the given state */
	public boolean has(GameState state)
	{
		return ((this._value & state.getValue()) == state.getValue());
	}
	
	/** set the given state */
	public void	set(GameState state)
	{
		this._value = this._value | state.getValue();
	}
	
	/** unset the given state */
	public void	unset(GameState state)
	{
		this._value = this._value & ~(state.getValue());
	}

	/** set a state if it was unset, or unset it if it was set */
	public void	switchh(GameState state)
	{
		this._id = this._id ^ state.getID();
	}
}
