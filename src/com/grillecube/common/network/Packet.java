package com.grillecube.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public abstract class Packet
{
	private static final int maxSize = 1024;
	
	private Channel _channel;
	private ByteBuf _byteBuffer;
	
	public Packet(Channel channel)
	{
		this._channel = channel;
		this._byteBuffer = channel.alloc().buffer(this.getPacketSize());
	}
	
	//send the packetID, the packet data
	public void send()
	{
		this.writeInt(this.getPacketID());
		this.writeData();
		this._channel.writeAndFlush(_byteBuffer);
	}
 
	//write every packet data
	public abstract void writeData();
	
	// return the sum of every packet's data size
	public abstract int	getPacketSize();
	
	//return packet unique ID (so client and server knows how to handle it)
	public abstract int	getPacketID();

	public void writeInt(int value)
	{
		this._byteBuffer.writeInt(value);
	}
}
