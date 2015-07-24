package com.grillecube.client.network.packet;

import com.grillecube.common.packet.Packet;
import com.grillecube.common.packet.PacketID;

public class PacketPing extends Packet
{
	public PacketPing()
	{
		super(PacketID.PING);
	}

	@Override
	public void handler()
	{
		
	}

}
