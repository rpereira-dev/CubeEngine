package com.grillecube.server.network.packet;

import com.grillecube.common.network.Packet;
import com.grillecube.common.network.Packets;

import io.netty.buffer.ByteBuf;

public class PacketString extends Packet
{
	private String	_str;
	
	public PacketString(ByteBuf buff)
	{
		super(buff);
	}
	
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
		return (this._str.length() + 4);	//strlen + sizeof(int)
	}

	@Override
	public int getPacketID()
	{
		return (Packets.STRING);
	}

	@Override
	public void readData()
	{
		byte[]	chars;
		int		length;
		
		length = super.readInt();
		chars = super.readBytes(length);
		this._str = new String(chars);
	}
	
	public String	getString()
	{
		return (this._str);
	}

}
