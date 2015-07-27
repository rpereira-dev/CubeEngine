package fr.toss.lib;

import org.lwjgl.util.vector.Vector3f;

public class Vector3i
{
	public int	x;
	public int	y;
	public int	z;
	
	public Vector3i(int x, int y, int z)
	{
		this.set(x, y, z);
	}
	
	public Vector3i()
	{
		this(0, 0, 0);
	}
	
	public void	set(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public int	getX()
	{
		return (this.x);
	}
	
	public int	getY()
	{
		return (this.y);
	}
	
	public int	getZ()
	{
		return (this.z);
	}

	public Vector3f toVector3f()
	{
		return (new Vector3f(this.x, this.y, this.z));
	}
}
