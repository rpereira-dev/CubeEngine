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
 * Holds a 4-tuple vector.
 * 
 * @author cix_foo <cix_foo@users.sourceforge.net>
 * @version $Revision$ $Id$
 */

public class Vector4f extends Vector implements Serializable {

	private static final long serialVersionUID = 1L;

	public float x, y, z, w;

	/**
	 * Constructor for Vector4f.
	 */
	public Vector4f() {
		super();
	}

	/**
	 * Constructor
	 */
	public Vector4f(Vector3f src, float w) {
		this.set(src.x, src.y, src.z, w);
	}

	/**
	 * Constructor
	 */
	public Vector4f(float x, float y, float z, float w) {
		set(x, y, z, w);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lwjgl.util.vector.WritableVector4f#set(float, float, float, float)
	 */
	public Vector4f set(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		return (this);
	}

	/**
	 * Load from another Vector4f
	 * 
	 * @param src
	 *            The source vector
	 * @return this
	 */
	public Vector4f set(Vector4f src) {
		return (this.set(src.x, src.y, src.z, src.w));
	}

	/**
	 * @return the length squared of the vector
	 */
	public float lengthSquared() {
		return x * x + y * y + z * z + w * w;
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
	public Vector4f translate(float x, float y, float z, float w) {
		this.x += x;
		this.y += y;
		this.z += z;
		this.w += w;
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
	public static Vector4f add(Vector4f left, Vector4f right, Vector4f dest) {
		if (dest == null)
			return new Vector4f(left.x + right.x, left.y + right.y, left.z + right.z, left.w + right.w);
		else {
			dest.set(left.x + right.x, left.y + right.y, left.z + right.z, left.w + right.w);
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
	public static Vector4f sub(Vector4f left, Vector4f right, Vector4f dest) {
		if (dest == null)
			return new Vector4f(left.x - right.x, left.y - right.y, left.z - right.z, left.w - right.w);
		else {
			dest.set(left.x - right.x, left.y - right.y, left.z - right.z, left.w - right.w);
			return dest;
		}
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
		w = -w;
		return this;
	}

	/**
	 * Negate a vector and place the result in a destination vector.
	 * 
	 * @param dest
	 *            The destination vector or null if a new vector is to be created
	 * @return the negated vector
	 */
	public Vector4f negate(Vector4f dest) {
		if (dest == null)
			dest = new Vector4f();
		dest.x = -x;
		dest.y = -y;
		dest.z = -z;
		dest.w = -w;
		return dest;
	}

	/**
	 * Normalise this vector and place the result in another vector.
	 * 
	 * @param dest
	 *            The destination vector, or null if a new vector is to be created
	 * @return the normalised vector
	 */
	public Vector4f normalise(Vector4f dest) {
		float l = length();

		if (dest == null)
			dest = new Vector4f(x / l, y / l, z / l, w / l);
		else
			dest.set(x / l, y / l, z / l, w / l);

		return dest;
	}

	/**
	 * The dot product of two vectors is calculated as v1.x * v2.x + v1.y * v2.y +
	 * v1.z * v2.z + v1.w * v2.w
	 * 
	 * @param left
	 *            The LHS vector
	 * @param right
	 *            The RHS vector
	 * @return left dot right
	 */
	public static float dot(Vector4f left, Vector4f right) {
		return left.x * right.x + left.y * right.y + left.z * right.z + left.w * right.w;
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
	public static float angle(Vector4f a, Vector4f b) {
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
	 * @see org.lwjgl.vector.Vector#load(ByteBuffer)
	 */
	public Vector load(ByteBuffer buf) {
		x = buf.getFloat();
		y = buf.getFloat();
		z = buf.getFloat();
		w = buf.getFloat();
		return this;
	}

	@Override
	public Vector scale(float scale) {
		this.x *= scale;
		this.y *= scale;
		this.z *= scale;
		this.w *= scale;
		return this;
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
		buf.putFloat(w);

		return this;
	}

	public String toString() {
		return "Vector4f: " + x + " " + y + " " + z + " " + w;
	}

	/**
	 * Set W
	 * 
	 * @param w
	 */
	public final void setW(float w) {
		this.w = w;
	}

	/*
	 * (Overrides)
	 * 
	 * @see org.lwjgl.vector.ReadableVector3f#getZ()
	 */
	public final float getW() {
		return w;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector4f other = (Vector4f) obj;

		if (x == other.x && y == other.y && z == other.z && w == other.w)
			return true;

		return false;
	}

	public static Vector4f rand() {
		float r = System.currentTimeMillis() % 486 / 486.0f;
		float g = System.currentTimeMillis() % 789 / 789.0f;
		float b = System.currentTimeMillis() % 695 / 695.0f;
		return (new Vector4f(r, g, b, 1.0f));
	}

	public Vector3f xyz() {
		return (new Vector3f(this.x, this.y, this.z));
	}

	public static Vector4f scale(Vector4f dst, Vector4f color, float f) {
		if (dst == null) {
			dst = new Vector4f();
		}
		dst.x = color.x * f;
		dst.y = color.y * f;
		dst.z = color.z * f;
		dst.w = color.w * f;
		return (dst);
	}

	/** dst = (1 - ratio) * left + ratio * right */
	public static final Vector4f mix(Vector4f left, Vector4f right, float ratio, Vector4f dst) {
		if (dst == null) {
			dst = new Vector4f();
		}
		dst.x = left.x * (1 - ratio) + right.x * ratio;
		dst.y = left.y * (1 - ratio) + right.y * ratio;
		dst.z = left.z * (1 - ratio) + right.z * ratio;
		dst.w = left.w * (1 - ratio) + right.w * ratio;
		return (dst);
	}
}