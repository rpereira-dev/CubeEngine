package com.grillecube.server.network.packet;

import com.grillecube.common.packet.APacket;
import com.grillecube.server.Game;

public abstract class AClientPacket extends APacket
{
	private int	_clientID;
	
	public AClientPacket(APacket.PacketID id, int clientID)
	{
		super(id);
		this._clientID = clientID;
	}
	
	public int	getClientID()
	{
		return (this._clientID);
	}

	/** handle the packet server side */
	public abstract void handle(Game game);
}
