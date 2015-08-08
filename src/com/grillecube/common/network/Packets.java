package com.grillecube.common.network;

import java.util.HashMap;
import com.grillecube.server.network.packet.PacketString;

import io.netty.buffer.ByteBuf;

public class Packets
{
	/** map of packets */
	private static final HashMap<Integer, PacketData>	_packets = new HashMap<Integer, PacketData>();
	
	/** every packets ID should be listed here */
	public static final int STRING = 1;	//TO REMOVE: only a small example

	/** add a packet class to the map 
	 * @throws WrongPacketFormatException */
	public static void	registerPacket(Class<? extends Packet> packet_class, int id) throws WrongPacketFormatException 
	{
		try
		{
			packet_class.getDeclaredConstructor(ByteBuf.class);
			_packets.put(id, new PacketData(packet_class));
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
	
	public static void addListenerToPacket(int id, PacketListener<? extends Packet> listener) {
		//public static final int MON_ID_UNIQUE = Packets.getUniqueId()
		PacketData data = _packets.get(id);
		if(listener == null || data == null) 
			return;
		data.get_listeners().add(listener);
	}
	

	public static void onReceive(Packet packet) {
		PacketData data = _packets.get(packet.getPacketID());
		if(data == null) 
			return;
		data.onReceive(packet);
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
		PacketData data = _packets.get(packetID);
		if(data == null) 
		{
			throw new NoSuchPacketException(packetID);
		}
		packet_class = data.get_class();
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
