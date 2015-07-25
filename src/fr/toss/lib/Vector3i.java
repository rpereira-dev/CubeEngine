package fr.toss.lib;

public class Vector3i
{
	public int	_x;
	public int	_y;
	public int	_z;
	
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
		this._x = x;
		this._y = y;
		this._z = z;
	}
	
	public int	getX()
	{
		return (this._x);
	}
	
	public int	getY()
	{
		return (this._y);
	}
	
	public int	getZ()
	{
		return (this._z);
	}
}
