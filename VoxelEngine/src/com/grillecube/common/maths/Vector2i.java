package com.grillecube.common.maths;

import java.nio.ByteBuffer;

/**
 * 
 * @author Romain
 *
 *	2d vector made of integer, following LWJGL util vector library implementations
 */

public class Vector2i extends Vector
{
	private static final long serialVersionUID = 1L;
	
	public int x;
	public int y;
	
	public Vector2i(int x, int y)
	{
		this.set(x, y);
	}
	
	public Vector2i()
	{
		this(0, 0);
	}
	
	public Vector2i set(int x, int y)
	{
		this.x = x;
		this.y = y;
		return (this);
	}
	
	public Vector2i clone()
	{
		return (new Vector2i(this.x, this.y));
	}
	
	public int getX()
	{
		return (this.x);
	}
	
	public int getY()
	{
		return (this.y);
	}

	public Vector2f toVector2f()
	{
		return (new Vector2f(this.x, this.y));
	}
	
	@Override
	public int hashCode()
	{	
		int x = (this.x < 0) ? (this.x ^ Integer.MIN_VALUE) : this.x;
		int y = (this.y < 0) ? (this.y ^ Integer.MIN_VALUE) : this.y;
		int hash = ((x & 0xFFFF) << 16) | ((y & 0xFFFF) << 0);

		return (hash);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		Vector2i vec = (Vector2i)obj;
		return (this.x == vec.x && this.y == vec.y);
	}

	public static Vector2i add(Vector2i a, Vector2i b)
	{
		return (new Vector2i(a.x + b.x, a.y + b.y));
	}
	
	public static Vector2i sub(Vector2i a, Vector2i b)
	{
		return (new Vector2i(a.x - b.x, a.y - b.y));
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder(64);
		
		sb.append("Vector2i[");
		sb.append(x);
		sb.append(", ");
		sb.append(y);
		sb.append(']');
		return (sb.toString());
	}

	@Override
	public float lengthSquared()
	{
		return (this.x * this.x + this.y * this.y);
	}

	@Override
	public Vector load(ByteBuffer buf)
	{
		this.x = buf.getInt();
		this.y = buf.getInt();
		return (this);
	}

	@Override
	public Vector store(ByteBuffer buf)
	{
		buf.putInt(this.x);
		buf.putInt(this.y);
		return (this);
	}
	
	@Override
	public Vector negate()
	{
		this.x = -this.x;
		this.y = -this.y;
		return (this);
	}
	
	@Override
	public Vector scale(float scale)
	{
		this.x *= scale;
		this.y *= scale;

		return (this);
	}
}
