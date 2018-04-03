package com.grillecube.common.maths;

import java.nio.ByteBuffer;

/**
 * 
 * @author Romain
 *
 *         3d vector made of integer, following LWJGL util vector library
 *         implementations
 */

public class Vector4i extends Vector3i {
	private static final long serialVersionUID = 1L;

	public int w;

	public Vector4i(int x, int y, int z, int w) {
		this.set(x, y, z, w);
	}

	public Vector4i() {
		this(0, 0, 0, 0);
	}

	public Vector4i set(int x, int y, int z, int w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		return (this);
	}

	public Vector4i clone() {
		return (new Vector4i(this.x, this.y, this.z, this.w));
	}

	public int getX() {
		return (this.x);
	}

	public int getY() {
		return (this.y);
	}

	public int getZ() {
		return (this.z);
	}

	public int getW() {
		return (this.w);
	}

	public Vector3f toVector3f() {
		return (new Vector3f(this.x, this.y, this.z));
	}

	@Override
	public int hashCode() {
		int x = (this.x < 0) ? (this.x ^ Integer.MIN_VALUE) : this.x;
		int y = (this.y < 0) ? (this.y ^ Integer.MIN_VALUE) : this.y;
		int z = (this.z < 0) ? (this.z ^ Integer.MIN_VALUE) : this.z;
		int w = (this.w < 0) ? (this.w ^ Integer.MIN_VALUE) : this.w;
		int hash = ((w & 0xFF) << 24) | ((x & 0xFF) << 16) | ((y & 0xFF) << 8) | (z & 0xFF);
		return (hash);
	}

	@Override
	public boolean equals(Object obj) {
		Vector4i vec = (Vector4i) obj;
		return (this.x == vec.x && this.y == vec.y && this.z == vec.z && this.w == vec.w);
	}

	public static Vector4i add(Vector4i a, Vector4i b) {
		return (new Vector4i(a.x + b.x, a.y + b.y, a.z + b.z, a.w + b.w));
	}

	public static Vector4i sub(Vector4i a, Vector4i b) {
		return (new Vector4i(a.x - b.x, a.y - b.y, a.z - b.z, a.w - b.w));
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(64);

		sb.append("Vector3i[");
		sb.append(x);
		sb.append(", ");
		sb.append(y);
		sb.append(", ");
		sb.append(z);
		sb.append(", ");
		sb.append(w);
		sb.append(']');
		return (sb.toString());
	}

	@Override
	public float lengthSquared() {
		return (this.x * this.x + this.y * this.y + this.z * this.z);
	}

	@Override
	public Vector load(ByteBuffer buf) {
		this.x = buf.getInt();
		this.y = buf.getInt();
		this.z = buf.getInt();
		return (this);
	}

	@Override
	public Vector store(ByteBuffer buf) {
		buf.putInt(this.x);
		buf.putInt(this.y);
		buf.putInt(this.z);
		return (this);
	}

	@Override
	public Vector negate() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
		return (this);
	}

	@Override
	public Vector scale(float scale) {
		this.x *= scale;
		this.y *= scale;
		this.z *= scale;

		return (this);
	}

	public void set(Vector4i pos) {
		this.x = pos.x;
		this.y = pos.y;
		this.z = pos.z;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setZ(int z) {
		this.z = z;
	}
}
