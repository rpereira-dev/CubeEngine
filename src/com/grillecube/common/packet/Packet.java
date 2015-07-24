package com.grillecube.common.packet;

public abstract class Packet
{
	public static final int	MAX_SIZE = 65536;	//64 ko

	private int	_id;
	
	public Packet(int id)
	{
		this._id = id;
	}
	
	public int	getID()
	{
		return (this._id);
	}
	
	public abstract void	handler();	
}
