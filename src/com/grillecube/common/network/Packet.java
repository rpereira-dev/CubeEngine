package com.grillecube.common.network;

import com.grillecube.client.world.blocks.Block;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public abstract class Packet
{
	private ByteBuf	_buffer;
	
	//send the packetID, the packet data
	public void send(Channel channel)
	{
		this._buffer = channel.alloc().buffer(this.getPacketSize() + 4);
		this.writeInt(this.getPacketID());
		this.writeData();
		channel.writeAndFlush(this._buffer);
	}
 
	//write every packet data
	public abstract void writeData();
	
	// return the sum of every packet's data size
	public abstract int	getPacketSize();
	
	//return packet unique ID (so client and server knows how to handle it)
	public abstract int	getPacketID();

	public void writeInt(int value)
	{
		this._buffer.writeInt(value);
	}
	
	public void writeBytes(byte[] bytes)
	{
		this._buffer.writeBytes(bytes);
	}

	public void writeByte(byte value)
	{
		this._buffer.writeByte(value);
	}
}
