package com.grillecube.client.mod.renderer.particles;

import java.util.Comparator;

import com.grillecube.client.world.WorldClient;

public abstract class AParticle
{
	private WorldClient	_world;
	
	public float	posX;
	public float	posY;
	public float	posZ;
	
	public float	velX;
	public float	velY;
	public float	velZ;
	
	public float	r;
	public float	g;
	public float	b;
	
	private int			_particleID;
	
	public AParticle(WorldClient world)
	{
		this._world = world;
		
		this.posX = 0;
		this.posY = 0;
		this.posZ = 0;
		
		this.velX = 0;
		this.velY = 0;
		this.velZ = 0;
		
		this.r = 1.0f;
		this.g = 1.0f;
		this.b = 1.0f;
	}
	
	public int	getParticleID()
	{
		return (this._particleID);
	}
	
	public abstract void update();
}

class ParticleComparator implements Comparator<AParticle>
{
	@Override
	public int compare(AParticle p1, AParticle p2)
	{
		if (p1.getParticleID() < p2.getParticleID())
		{
			return (-1);
		}
		if (p1.getParticleID() > p2.getParticleID())
		{
			return (1);
		}
		return (0);
	}
	
}
