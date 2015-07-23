package com.grillecube.common.packet;

import java.net.DatagramPacket;

public abstract class APacket
{
	/** unique packet id */
	public enum PacketID
	{
		PING(0);	//PacketPing.java
		
		private int	_id;
		
		PacketID(int i)
		{
			this._id = i;
		}

		public int getID()
		{
			return (this._id);
		}
	}
	private PacketID	_id;

	
	public APacket(PacketID id)
	{
		this._id = id;
	}
	
	/** return packet unique ID */
	public PacketID	getID()
	{
		return (this._id);
	}
}

