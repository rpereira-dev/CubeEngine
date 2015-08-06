package com.grillecube.client.world;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector3i;

public class TerrainLocation
{
	private Vector3i _index;
	private Vector3f _world_location;
	private Vector3f _center;
	
	public TerrainLocation()
	{
		this(0, 0, 0);
	}
	
	public TerrainLocation(int x, int y, int z)
	{
		this._index = new Vector3i(x, y, z);
		this._world_location = new Vector3f(x * Terrain.SIZE_X,
											y * Terrain.SIZE_Y,
											z * Terrain.SIZE_Z);
		this._center = new Vector3f(this._world_location.x + Terrain.SIZE_X / 2,
										this._world_location.y + Terrain.SIZE_Y / 2,
										this._world_location.z + Terrain.SIZE_Z / 2);
	}
	
	/** World index */
	public Vector3i toWorldIndex()
	{
		return (this._index);
	}

	/** World position */
	public Vector3f toWorldLocation()
	{
		return (this._world_location);
	}
	
	/** World position */
	public Vector3f getCenter()
	{
		return (this._center);
	}
	
	@Override
	public boolean	equals(Object obj)
	{
		return (this._index.equals(((TerrainLocation)obj).toWorldIndex()));
	}

	public TerrainLocation set(int x, int y, int z)
	{
		this._index.set(x, y, z);
		this._world_location.set(x * Terrain.SIZE_X,
									y * Terrain.SIZE_Y,
									z * Terrain.SIZE_Z);
		this._world_location.set(this._world_location.x + Terrain.SIZE_X / 2,
									this._world_location.y + Terrain.SIZE_Y / 2,
									this._world_location.z + Terrain.SIZE_Z / 2);
		return (this);
	}
	
	@Override
	public String	toString()
	{
		return ("TerrainLocation: " + this._index.toString() + " : " + this._world_location.toString());
	}

	/** world position relative */
	public double dist(Vector3f position)
	{
		float x = this._world_location.x - position.x;
		float y = this._world_location.y - position.y;
		float z = this._world_location.z - position.z;
		return (Math.sqrt(x * x + y * y + z * z));
	}
}