/**
**	This file is part of the project https://github.com/toss-dev/VoxelEngine
**
**	License is available here: https://raw.githubusercontent.com/toss-dev/VoxelEngine/master/LICENSE.md
**
**	PEREIRA Romain
**                                       4-----7          
**                                      /|    /|
**                                     0-----3 |
**                                     | 5___|_6
**                                     |/    | /
**                                     1-----2
*/

package com.grillecube.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public abstract class Packet {
	private ByteBuf _buffer;

	// default constructor
	public Packet() {
		this._buffer = null;
	}

	// constructor called when a packet is received
	public Packet(ByteBuf buf) {
		this._buffer = buf;
		this.readData();
	}

	// send the packetID, the packet data
	public void send(Channel channel) {
		this._buffer = channel.alloc().buffer(this.getPacketSize() + 4);
		this.writeInt(this.getPacketID());
		this.writeData();
		channel.writeAndFlush(this._buffer);
	}

	// read every packet data (called when a packet is received)
	public abstract void readData();

	// write every packet data, before it is sent
	public abstract void writeData();

	// return the sum of every packet's data size
	public abstract int getPacketSize();

	// return packet unique ID (so client and server knows how to handle it)
	public abstract int getPacketID();

	/**
	 * Protected 'cause it should only be used by children
	 * 
	 * @param value
	 */
	protected void writeInt(int value) {
		this._buffer.writeInt(value);
	}

	public void writeBytes(byte[] bytes) {
		this._buffer.writeBytes(bytes);
	}

	protected void writeByte(byte value) {
		this._buffer.writeByte(value);
	}

	protected void writeShort(short value) {
		this._buffer.writeShort(value);
	}

	protected void writeShorts(short[] values) {
		for (short s : values) {
			this._buffer.writeShort(s);
		}
	}

	/**
	 * Need to read also...
	 */
	/**
	 * Protected 'cause it should only be used by children
	 */
	protected int readInt() {
		return (this._buffer.readInt());
	}

	protected byte readByte() {
		return (this._buffer.readByte());
	}

	public byte[] readBytes(int length) {
		byte[] bytes;

		bytes = new byte[length];
		this._buffer.readBytes(bytes);
		return (bytes);
	}
}
