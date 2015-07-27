package com.grillecube.common.network;

import io.netty.buffer.ByteBuf;

import com.grillecube.server.network.packet.PacketTerrain;

public class Packets
{
	/**
	 * There should be some sort of PacketID Manager (in order to add mods or so)
	 */
	public static final int TERRAIN_FULL = 1;
	
	public static Packet getFromPacketId(int packetId, ByteBuf byteBuffer) {
		/**
		 * TODO : Someful like a for loop into a map of id <=> class
		 */
		Packet packet = null;
		switch(packetId) {
		case TERRAIN_FULL:
			packet = new PacketTerrain(null, byteBuffer); //TODO : GetChannel somewhere
			packet.readData();
			break;
		}
		return packet;
	}
	
}
