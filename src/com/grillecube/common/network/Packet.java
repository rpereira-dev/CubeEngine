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
	
	public Packet(Channel channel, ByteBuf byteBuffer)
	{
		this._channel = channel;
		this._byteBuffer = byteBuffer;
	}
	
	//send the packetID, the packet data
	public void send()
	{
		this.writeInt(this.getPacketID());
		this.writeData();
		this._channel.writeAndFlush(_byteBuffer);
	}
 
	//read every packet data (when received)
	public abstract void readData();
	
	//write every packet data
	public abstract void writeData();
	
	// return the sum of every packet's data size
	public abstract int	getPacketSize();
	
	//return packet unique ID (so client and server knows how to handle it)
	public abstract int	getPacketID();
	
	public static Packet fromByteBuffer(ByteBuf byteBuffer) {
		//Returns implementation of packet from byteBuffer
		return Packets.getFromPacketId(byteBuffer.readInt(), byteBuffer);
	}
	

	/**
	 * Protected 'cause it should only be used by children
	 * @param value
	 */
	protected void writeInt(int value)
	{
		this._byteBuffer.writeInt(value);
	}

	protected void writeByte(byte value)
	{
		this._byteBuffer.writeByte(value);
	}
	
	protected void writeString(String value) {
		this._byteBuffer.writeBytes(value.getBytes());
	}
	
	/**
	 * Need to read also...
	 */
	/**
	 * Protected 'cause it should only be used by children
	 * @param value
	 */
	protected int readInt()
	{
		return this._byteBuffer.readInt();
	}

	protected byte readByte()
	{
		return this._byteBuffer.readByte();
	}
}
