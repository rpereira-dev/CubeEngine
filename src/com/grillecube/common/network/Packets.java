package com.grillecube.common.network;

import java.util.HashMap;

import com.grillecube.server.network.packet.PacketString;

import io.netty.buffer.ByteBuf;

public class Packets
{
	/** map of packets */
	private static final HashMap<Integer, Class<? extends Packet>>	_packets = new HashMap<Integer, Class<? extends Packet>>();
	
	/** every packets ID should be listed here */
	public static final int STRING = 1;	//TO REMOVE: only a small example

	/** add a packet class to the map 
	 * @throws WrongPacketFormatException */
	public static void	registerPacket(Class<? extends Packet> packet_class, int id) throws WrongPacketFormatException
	{
		try
		{
			packet_class.getDeclaredConstructor(ByteBuf.class);
			_packets.put(id, packet_class);
		}
		catch (NoSuchMethodException e)
		{
			throw new WrongPacketFormatException("Byte buffer constructor not found!", packet_class);
		}
		catch (SecurityException e)
		{
			throw new WrongPacketFormatException(e.getMessage(), packet_class);
		}
	}
	
	/** called on initialization 
	 * 	@throws WrongPacketFormatException : if a packet is wrongly formatted*/
	public static void	loadPackets() throws WrongPacketFormatException
	{
		registerPacket(PacketString.class, STRING);
	}

	public static Packet getFromPacketID(int packetID, ByteBuf byteBuffer) throws NoSuchPacketException, WrongPacketFormatException
	{
		Class<? extends Packet>	packet_class;
		
		packet_class = _packets.get(packetID);
		if (packet_class == null)
		{
			throw new NoSuchPacketException(packetID);
		}
		try
		{
			return (packet_class.getDeclaredConstructor(ByteBuf.class).newInstance(byteBuffer));
		}
		catch (Exception exception)	//should never occured because inserted packets are checked on initialiaztion
		{
			System.err.println("Packets.getFromPacketID()");
			throw new WrongPacketFormatException(exception.getMessage(), packet_class);
		}
	}
}
