package com.grillecube.server.network.packet;

import com.grillecube.common.network.Packet;
import com.grillecube.common.network.Packets;

import io.netty.buffer.ByteBuf;

/**
 * 
 * Simple exception of a packet definition:
 *
 *	• WHEN A PACKET IS SENT:
 *	e.g:	new PacketString("Hello world").send(channel);	<- will send the packet to the given channel
 *	'Packet.send()' will create a new ByteBuf of size '4 + Packet.getPacketSize()'
 *	the 4 first bytes is an int that contains the packet ID ('Packet.getPacketID')
 *	'Packet.getPacketSize()' correspond to the number of bytes written in 'Packet.writeData()' function
 *	When this byteBuffer is created, it is filled by calling 'Packet.writeData()' function implementation
 *
 *	• WHEN A PACKET IS RECEIVED:
 *	'Packet.fromByteBuffer(ByteBuf buff)' will return a new instance of this Packet, which contains the received data
 *	the 'PacketString(ByteBuf buff)' has to be implemented so when the packet is received,
 *	java can call this constructor. Else way an exception is called on initialiaztion
 *
 *	When this constructor is called, the 'readData()' function is called,
 *	so the packet attributes can be set
 *	
 *	e.g:	new PacketString(byteBuffer).getString(); <- will return the String attribute
 */


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
