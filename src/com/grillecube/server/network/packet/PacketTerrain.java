package com.grillecube.server.network.packet;

import com.grillecube.common.network.Packet;
import com.grillecube.common.network.Packets;
import com.grillecube.common.world.Terrain;

import io.netty.channel.Channel;

//implementation example:
public class PacketTerrain extends Packet
{
	private Terrain	_terrain;
	
	public PacketTerrain(Channel channel, Terrain terrain)
	{
		super(channel);
		this._terrain = terrain;
	}

	@Override
	public void writeData()
	{
		super.writeInt(this._terrain.getLocation().getX());
		super.writeInt(this._terrain.getLocation().getY());
		super.writeInt(this._terrain.getLocation().getZ());
		
		for (int x = 0 ; x < Terrain.SIZE_X  ; x++)
		{
			for (int y = 0 ; y < Terrain.SIZE_Y  ; y++)
			{
				for (int z = 0 ; z < Terrain.SIZE_Z  ; z++)
				{
					super.writeByte(this._terrain.getBlock(x, y, z).getID());
				}
			}
		}
	}

	@Override
	public int getPacketSize()
	{
		int	size;
		
		size = 4 * 3;	//terrain coordinates (x, y, z)
		size += Terrain.SIZE_X * Terrain.SIZE_Y * Terrain.SIZE_Z; //terrain blocks
		return (size);
	}

	@Override
	public int getPacketID()
	{
		return (Packets.TERRAIN_FULL);
	}
}