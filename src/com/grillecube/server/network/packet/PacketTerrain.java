package com.grillecube.server.network.packet;

import com.grillecube.common.network.Packet;
import com.grillecube.common.network.Packets;
import com.grillecube.common.world.Terrain;
import com.grillecube.common.world.TerrainLocation;

import io.netty.buffer.ByteBuf;

//implementation example:
public class PacketTerrain extends Packet
{
	private TerrainLocation	_location;
	private byte[][][]		_blocks;

	public PacketTerrain(ByteBuf buffer)
	{
		super(buffer);
	}
	
	public PacketTerrain(Terrain terrain)
	{
		this._location = terrain.getLocation();
		this._blocks = terrain.getBlocks();
	}
	
	/**
	 * Read every data and create terrain
	 * this function is called when the packet is cosntructed with it 'Packet(ByteBuf buffer)' constructor
	 * (so the current ByteBuf is the one received
	 */
	@Override
	public void readData()
	{		
		this._location = new TerrainLocation(super.readInt(), super.readInt(), super.readInt());
		this._blocks = new byte[Terrain.SIZE_X][Terrain.SIZE_Y][Terrain.SIZE_Z];
		for (int x = 0 ; x < Terrain.SIZE_X  ; x++)
		{
			for (int y = 0 ; y < Terrain.SIZE_Y  ; y++)
			{
				for (int z = 0 ; z < Terrain.SIZE_Z  ; z++)
				{
					this._blocks[x][y][z] = super.readByte();
				}
			}
		}
	}

	@Override
	public void writeData()
	{
		super.writeInt(this._location.getX());
		super.writeInt(this._location.getY());
		super.writeInt(this._location.getZ());
		
		for (int x = 0 ; x < Terrain.SIZE_X  ; x++)
		{
			for (int y = 0 ; y < Terrain.SIZE_Y  ; y++)
			{
				for (int z = 0 ; z < Terrain.SIZE_Z  ; z++)
				{
					super.writeByte(this._blocks[x][y][z]);
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

	public byte[][][]	getBlocks()
	{
		return (this._blocks);
	}
	
	public TerrainLocation	getTerrainLocation()
	{
		return (this._location);
	}
}