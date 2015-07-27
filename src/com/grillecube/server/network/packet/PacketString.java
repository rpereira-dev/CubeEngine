package com.grillecube.server.network.packet;

import com.grillecube.common.network.Packet;
import com.grillecube.common.network.Packets;

public class PacketString extends Packet
{
	private String	_str;
	
	public PacketString(String str)
	{
		this._str = str;
	}
	
	@Override
	public void writeData()
	{
		super.writeInt(this._str.length());
		super.writeBytes(this._str.getBytes());
	}

	@Override
	public int getPacketSize()
	{
		return (this._str.length() + 4);
	}

	@Override
	public int getPacketID()
	{
		return (Packets.STRING);
	}

}
