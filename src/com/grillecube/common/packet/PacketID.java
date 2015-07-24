package com.grillecube.common.packet;

import com.grillecube.client.network.packet.PacketPing;

public class PacketID
{
	/** packets id */
	public static final int PING	= 0;
	public static final int MAX		= 1;
	
	/** packets handler */
	public static final Packet[] packets = new Packet[MAX];
	{
		packets[PING] = new PacketPing();
	}
}
