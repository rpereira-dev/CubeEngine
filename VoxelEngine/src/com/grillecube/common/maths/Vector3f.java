/*
 * Copyright (c) 2002-2008 LWJGL Project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'LWJGL' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.grillecube.common.maths;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 *
 * Holds a 3-tuple vector.
 *
 * @author cix_foo <cix_foo@users.sourceforge.net>
 * @version $Revision$ $Id$
 */

public class Vector3f extends Vector implements Serializable, ReadableVector3f {

	public static final Vector3f AXIS_X = new Vector3f(1, 0, 0);
	public static final Vector3f AXIS_Y = new Vector3f(0, 1, 0);
	public static final Vector3f AXIS_Z = new Vector3f(0, 0, 1);

	public static final Vector3f NULL_VEC = new Vector3f(0, 0, 0);
	public static final Vector3f ONE_VEC = new Vector3f(1, 1, 1);

	private static final long serialVersionUID = 1L;

	public float x, y, z;

	/**
	 * Constructor for Vector3f.
	 */
	public Vector3f() {
		super();
	}

	/**
	 * Constructor
	 */
	public Vector3f(ReadableVector3f src) {
		set(src);
	}

	/**
	 * Constructor
	 */
	public Vector3f(float x, float y, float z) {
		set(x, y, z);
	}

	public Vector3f(Vector3i vec) {
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
	}

	public Vector3f(float value) {
		this(value, value, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lwjgl.util.vector.WritableVector2f#set(float, float)
	 */
	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void set(float value) {
		this.x = value;
		this.y = value;
		this.z = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lwjgl.util.vector.WritableVector3f#set(float, float, float)
	 */
	public Vector3f set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return (this);
	}

	/**
	 * Load from another Vector3f
	 * 
	 * @param src
	 *            The source vector
	 * @return this
	 */
	public Vector3f set(ReadableVector3f src) {
		x = src.getX();
		y = src.getY();
		z = src.getZ();
		return this;
	}

	public Vector3f set(Vector3i src) {
		x = src.getX();
		y = src.getY();
		z = src.getZ();
		return this;
	}

	/**
	 * @return the length squared of the vector
	 */
	public float lengthSquared() {
		return (lengthSquared(x, y, z));
	}

	public static float lengthSquared(float x, float y, float z) {
		return x * x + y * y + z * z;
	}

	/**
	 * Translate a vector
	 * 
	 * @param x
	 *            The translation in x
	 * @param y
	 *            the translation in y
	 * @return this
	 */
	public Vector3f translate(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	/**
	 * Add a vector to another vector and place the result in a destination vector.
	 * 
	 * @param left
	 *            The LHS vector
	 * @param right
	 *            The RHS vector
	 * @param dest
	 *            The destination vector, or null if a new vector is to be created
	 * @return the sum of left and right in dest
	 */
	public static Vector3f add(Vector3f left, Vector3f right, Vector3f dest) {
		if (dest == null)
			return new Vector3f(left.x + right.x, left.y + right.y, left.z + right.z);
		else {
			dest.set(left.x + right.x, left.y + right.y, left.z + right.z);
			return dest;
		}
	}

	/**
	 * Subtract a vector from another vector and place the result in a destination
	 * vector.
	 * 
	 * @param left
	 *            The LHS vector
	 * @param right
	 *            The RHS vector
	 * @param dest
	 *            The destination vector, or null if a new vector is to be created
	 * @return left minus right in dest
	 */
	public static Vector3f sub(Vector3f left, Vector3f right, Vector3f dest) {
		if (dest == null)
			return new Vector3f(left.x - right.x, left.y - right.y, left.z - right.z);
		else {
			dest.set(left.x - right.x, left.y - right.y, left.z - right.z);
			return dest;
		}
	}

	/**
	 * The cross product of two vectors.
	 *
	 * @param left
	 *            The LHS vector
	 * @param right
	 *            The RHS vector
	 * @param dest
	 *            The destination result, or null if a new vector is to be created
	 * @return left cross right
	 */
	public static Vector3f cross(Vector3f left, Vector3f right, Vector3f dest) {

		if (dest == null)
			dest = new Vector3f();

		dest.set(left.y * right.z - left.z * right.y, right.x * left.z - right.z * left.x,
				left.x * right.y - left.y * right.x);

		return dest;
	}

	/**
	 * Negate a vector
	 * 
	 * @return this
	 */
	public Vector negate() {
		x = -x;
		y = -y;
		z = -z;
		return this;
	}

	/**
	 * Negate a vector and place the result in a destination vector.
	 * 
	 * @param dest
	 *            The destination vector or null if a new vector is to be created
	 * @return the negated vector
	 */
	public Vector3f negate(Vector3f dest) {
		if (dest == null)
			dest = new Vector3f();
		dest.x = -x;
		dest.y = -y;
		dest.z = -z;
		return dest;
	}

	/**
	 * Normalise this vector and place the result in another vector.
	 * 
	 * @param dest
	 *            The destination vector, or null if a new vector is to be created
	 * @return the normalised vector
	 */
	public Vector3f normalise(Vector3f dest) {
		float l = length();

		if (dest == null)
			dest = new Vector3f(x / l, y / l, z / l);
		else
			dest.set(x / l, y / l, z / l);

		return dest;
	}

	/**
	 * The dot product of two vectors is calculated as v1.x * v2.x + v1.y * v2.y +
	 * v1.z * v2.z
	 * 
	 * @param left
	 *            The LHS vector
	 * @param right
	 *            The RHS vector
	 * @return left dot right
	 */
	public static float dot(Vector3f left, Vector3f right) {
		return left.x * right.x + left.y * right.y + left.z * right.z;
	}

	public static float dot(float x1, float y1, float z1, float x2, float y2, float z2) {
		return x1 * x2 + y1 * y2 + z1 * z2;
	}

	public static float dot(Vector3f l, float rx, float ry, float rz) {
		return (l.x * rx + l.y * ry + l.z * rz);
	}

	/**
	 * Calculate the angle between two vectors, in radians
	 * 
	 * @param a
	 *            A vector
	 * @param b
	 *            The other vector
	 * @return the angle between the two vectors, in radians
	 */
	public static float angle(Vector3f a, Vector3f b) {
		float dls = dot(a, b) / (a.length() * b.length());
		if (dls < -1f)
			dls = -1f;
		else if (dls > 1.0f)
			dls = 1.0f;
		return (float) Math.acos(dls);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lwjgl.vector.Vector#load(FloatBuffer)
	 */
	public Vector load(ByteBuffer buf) {
		x = buf.getFloat();
		y = buf.getFloat();
		z = buf.getFloat();
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lwjgl.vector.Vector#scale(float)
	 */
	public Vector3f scale(float scale) {

		x *= scale;
		y *= scale;
		z *= scale;

		return this;

	}

	public static Vector3f scale(Vector3f vec, float scale, Vector3f dst) {
		if (dst == null) {
			dst = new Vector3f();
		}
		return (dst.set(vec.x * scale, vec.y * scale, vec.z * scale));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lwjgl.vector.Vector#store(FloatBuffer)
	 */
	public Vector store(ByteBuffer buf) {

		buf.putFloat(x);
		buf.putFloat(y);
		buf.putFloat(z);

		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder(64);

		sb.append("Vector3f[");
		sb.append(x);
		sb.append(", ");
		sb.append(y);
		sb.append(", ");
		sb.append(z);
		sb.append(']');
		return sb.toString();
	}

	/**
	 * @return x
	 */
	public final float getX() {
		return x;
	}

	/**
	 * @return y
	 */
	public final float getY() {
		return y;
	}

	/**
	 * Set X
	 * 
	 * @param x
	 */
	public final void setX(float x) {
		this.x = x;
	}

	/**
	 * Set Y
	 * 
	 * @param y
	 */
	public final void setY(float y) {
		this.y = y;
	}

	/**
	 * Set Z
	 * 
	 * @param z
	 */
	public void setZ(float z) {
		this.z = z;
	}

	/*
	 * (Overrides)
	 * 
	 * @see org.lwjgl.vector.ReadableVector3f#getZ()
	 */
	public float getZ() {
		return z;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector3f other = (Vector3f) obj;

		if (x == other.x && y == other.y && z == other.z)
			return true;

		return false;
	}

	public void add(Vector3f vec) {
		this.x += vec.x;
		this.y += vec.y;
		this.z += vec.z;
	}

	public static double distance(float ax, float ay, float az, float bx, float by, float bz) {
		return (Math.sqrt(distanceSquare(ax, ay, az, bx, by, bz)));
	}

	public static double distance(Vector3f a, Vector3f b) {
		return (distance(a.x, a.y, a.z, b.x, b.y, b.z));
	}

	public static double distanceSquare(Vector3f a, Vector3f b) {
		return (distanceSquare(a.x, a.y, a.z, b.x, b.y, b.z));
	}

	public static double distanceSquare(float ax, float ay, float az, float bx, float by, float bz) {
		float dx = ax - bx;
		float dy = ay - by;
		float dz = az - bz;

		return (dx * dx + dy * dy + dz * dz);
	}

	public Vector3f translate(Vector3f position) {
		return (this.translate(position.x, position.y, position.z));
	}

	public static Vector3f mix(Vector3f dst, Vector3f left, Vector3f right, float ratio) {
		if (dst == null) {
			dst = new Vector3f();
		}
		dst.x = left.x * ratio + right.x * (1 - ratio);
		dst.y = left.y * ratio + right.y * (1 - ratio);
		dst.z = left.z * ratio + right.z * (1 - ratio);
		return (dst);
	}

	public Vector3f add(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return (this);
	}

	public Vector3f add(Vector3f lookVec, float strength) {
		this.x += x * strength;
		this.y += y * strength;
		this.z += z * strength;
		return (this);
	}

	public Vector3f scale(Vector3f scale) {
		this.x *= scale.x;
		this.y *= scale.y;
		this.z *= scale.z;
		return (this);
	}

	/**
	 * rotate the vector by 90 degrees around the Y axis (right-handed coordinate
	 * system)
	 */
	public void rotateY90() {
		float x = this.x;
		this.x = this.z;
		this.z = -x;
	}

	/**
	 * rotate the vector by 270 degrees around the Y axis (right-handed coordinate
	 * system)
	 */
	public void rotateY270() {
		float x = this.x;
		this.x = -this.z;
		this.z = x;
	}

	public void sub(Vector3f normal) {
		this.x -= normal.x;
		this.y -= normal.y;
		this.z -= normal.z;
	}

	public void clamp(float m, float M) {
		this.x = Maths.clamp(this.x, m, M);
		this.y = Maths.clamp(this.y, m, M);
		this.z = Maths.clamp(this.z, m, M);
	}

	public void round(float decimal) {
		this.x = Maths.approximatate(this.x, decimal);
		this.y = Maths.approximatate(this.y, decimal);
		this.z = Maths.approximatate(this.z, decimal);

	}

	public static float length(float x, float y, float z) {
		return (float) Math.sqrt(lengthSquared(x, y, z));
	}

	public static Vector3f interpolate(Vector3f a, Vector3f b, float ratio, Vector3f dst) {
		if (dst == null) {
			dst = new Vector3f();
		}
		dst.x = a.x * (1.0f - ratio) + b.x * ratio;
		dst.y = a.y * (1.0f - ratio) + b.y * ratio;
		dst.z = a.z * (1.0f - ratio) + b.z * ratio;
		return (dst);

	}

	public void add(javax.vecmath.Vector3f v) {
		this.add(v.x, v.y, v.z);
	}
}