package com.grillecube.common.maths;

import java.nio.ByteBuffer;

/**
 * 
 * @author Romain
 *
 *         3d vector made of integer, following LWJGL util vector library
 *         implementations
 */

public class Vector3i extends Vector {
	private static final long serialVersionUID = 1L;

	public int x;
	public int y;
	public int z;

	public Vector3i(int x, int y, int z) {
		this.set(x, y, z);
	}

	public Vector3i(Vector3f vec) {
		this((int) vec.x, (int) vec.y, (int) vec.z);
	}

	public Vector3i() {
		this(0, 0, 0);
	}

	public Vector3i set(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return (this);
	}

	public Vector3i clone() {
		return (new Vector3i(this.x, this.y, this.z));
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

	public Vector3f toVector3f() {
		return (new Vector3f(this.x, this.y, this.z));
	}

	@Override
	public int hashCode() {
		int x = (this.x < 0) ? (this.x ^ Integer.MIN_VALUE) : this.x;
		int y = (this.y < 0) ? (this.y ^ Integer.MIN_VALUE) : this.y;
		int z = (this.z < 0) ? (this.z ^ Integer.MIN_VALUE) : this.z;
		int hash = ((x & 0xFF) << 16) | ((y & 0xFF) << 8) | (z & 0xFF);
		return (hash);
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == this) {
			return (true);
		}

		if (!(obj instanceof Vector3i)) {
			return (false);
		}

		Vector3i vec = (Vector3i) obj;
		return (this.x == vec.x && this.y == vec.y && this.z == vec.z);
	}

	public static Vector3i add(Vector3i a, Vector3i b) {
		return (add(new Vector3i(), a, b));
	}

	public static Vector3i add(Vector3i dst, Vector3i a, Vector3i b) {
		return (dst.set(a.x + b.x, a.y + b.y, a.z + b.z));
	}

	public static Vector3i sub(Vector3i a, Vector3i b) {
		return (sub(new Vector3i(), a, b));
	}

	public static Vector3i sub(Vector3i dst, Vector3i a, Vector3i b) {
		return (dst.set(a.x - b.x, a.y - b.y, a.z - b.z));
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

	public void set(Vector3i pos) {
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

	public Vector2i xy() {
		return (new Vector2i(x, y));
	}

	public Vector2i xz() {
		return (new Vector2i(x, z));
	}

	public Vector2i yz() {
		return (new Vector2i(y, z));
	}
}
