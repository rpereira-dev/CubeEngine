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
import java.nio.FloatBuffer;

/**
 * Holds a 4x4 float matrix.
 *
 * @author foo
 */
public class Matrix4f extends Matrix implements Serializable {
	private static final long serialVersionUID = 1L;

	public float m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33;

	/**
	 * Construct a new matrix, initialized to the identity.
	 */
	public Matrix4f() {
		super();
		setIdentity();
	}

	public Matrix4f(final Matrix4f src) {
		super();
		load(src);
	}

	public Matrix4f(float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13, float m20,
			float m21, float m22, float m23, float m30, float m31, float m32, float m33) {
		super();
		this.m00 = m00;
		this.m01 = m01;
		this.m02 = m02;
		this.m03 = m03;

		this.m10 = m10;
		this.m11 = m11;
		this.m12 = m12;
		this.m13 = m13;

		this.m20 = m20;
		this.m21 = m21;
		this.m22 = m22;
		this.m23 = m23;

		this.m30 = m30;
		this.m31 = m31;
		this.m32 = m32;
		this.m33 = m33;
	}

	/**
	 * Returns a string representation of this matrix
	 */
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(m00).append(' ').append(m10).append(' ').append(m20).append(' ').append(m30).append('\n');
		buf.append(m01).append(' ').append(m11).append(' ').append(m21).append(' ').append(m31).append('\n');
		buf.append(m02).append(' ').append(m12).append(' ').append(m22).append(' ').append(m32).append('\n');
		buf.append(m03).append(' ').append(m13).append(' ').append(m23).append(' ').append(m33).append('\n');
		return buf.toString();
	}

	/**
	 * Set this matrix to be the identity matrix.
	 * 
	 * @return this
	 */
	public Matrix setIdentity() {
		return setIdentity(this);
	}

	/**
	 * Set the given matrix to be the identity matrix.
	 * 
	 * @param m
	 *            The matrix to set to the identity
	 * @return m
	 */
	public static Matrix4f setIdentity(Matrix4f m) {
		m.m00 = 1.0f;
		m.m01 = 0.0f;
		m.m02 = 0.0f;
		m.m03 = 0.0f;
		m.m10 = 0.0f;
		m.m11 = 1.0f;
		m.m12 = 0.0f;
		m.m13 = 0.0f;
		m.m20 = 0.0f;
		m.m21 = 0.0f;
		m.m22 = 1.0f;
		m.m23 = 0.0f;
		m.m30 = 0.0f;
		m.m31 = 0.0f;
		m.m32 = 0.0f;
		m.m33 = 1.0f;

		return m;
	}

	/**
	 * Set this matrix to 0.
	 * 
	 * @return this
	 */
	public Matrix setZero() {
		return setZero(this);
	}

	/**
	 * Set the given matrix to 0.
	 * 
	 * @param m
	 *            The matrix to set to 0
	 * @return m
	 */
	public static Matrix4f setZero(Matrix4f m) {
		m.m00 = 0.0f;
		m.m01 = 0.0f;
		m.m02 = 0.0f;
		m.m03 = 0.0f;
		m.m10 = 0.0f;
		m.m11 = 0.0f;
		m.m12 = 0.0f;
		m.m13 = 0.0f;
		m.m20 = 0.0f;
		m.m21 = 0.0f;
		m.m22 = 0.0f;
		m.m23 = 0.0f;
		m.m30 = 0.0f;
		m.m31 = 0.0f;
		m.m32 = 0.0f;
		m.m33 = 0.0f;

		return m;
	}

	/**
	 * Load from another matrix4f
	 * 
	 * @param src
	 *            The source matrix
	 * @return this
	 */
	public Matrix4f load(Matrix4f src) {
		return load(src, this);
	}

	/**
	 * Copy the source matrix to the destination matrix
	 * 
	 * @param src
	 *            The source matrix
	 * @param dest
	 *            The destination matrix, or null of a new one is to be created
	 * @return The copied matrix
	 */
	public static Matrix4f load(Matrix4f src, Matrix4f dest) {
		if (dest == null)
			dest = new Matrix4f();
		dest.m00 = src.m00;
		dest.m01 = src.m01;
		dest.m02 = src.m02;
		dest.m03 = src.m03;
		dest.m10 = src.m10;
		dest.m11 = src.m11;
		dest.m12 = src.m12;
		dest.m13 = src.m13;
		dest.m20 = src.m20;
		dest.m21 = src.m21;
		dest.m22 = src.m22;
		dest.m23 = src.m23;
		dest.m30 = src.m30;
		dest.m31 = src.m31;
		dest.m32 = src.m32;
		dest.m33 = src.m33;

		return dest;
	}

	/**
	 * Load from a float buffer. The buffer stores the matrix in column major
	 * (OpenGL) order.
	 *
	 * @param buf
	 *            A float buffer to read from
	 * @return this
	 */
	public Matrix load(FloatBuffer buf) {

		m00 = buf.get();
		m01 = buf.get();
		m02 = buf.get();
		m03 = buf.get();
		m10 = buf.get();
		m11 = buf.get();
		m12 = buf.get();
		m13 = buf.get();
		m20 = buf.get();
		m21 = buf.get();
		m22 = buf.get();
		m23 = buf.get();
		m30 = buf.get();
		m31 = buf.get();
		m32 = buf.get();
		m33 = buf.get();

		return this;
	}

	/**
	 * Load from a float buffer. The buffer stores the matrix in row major
	 * (maths) order.
	 *
	 * @param buf
	 *            A float buffer to read from
	 * @return this
	 */
	public Matrix loadTranspose(FloatBuffer buf) {

		m00 = buf.get();
		m10 = buf.get();
		m20 = buf.get();
		m30 = buf.get();
		m01 = buf.get();
		m11 = buf.get();
		m21 = buf.get();
		m31 = buf.get();
		m02 = buf.get();
		m12 = buf.get();
		m22 = buf.get();
		m32 = buf.get();
		m03 = buf.get();
		m13 = buf.get();
		m23 = buf.get();
		m33 = buf.get();

		return this;
	}

	/**
	 * Store this matrix in a float buffer. The matrix is stored in column major
	 * (openGL) order.
	 * 
	 * @param buf
	 *            The buffer to store this matrix in
	 */
	public Matrix store(FloatBuffer buf) {
		buf.put(m00);
		buf.put(m01);
		buf.put(m02);
		buf.put(m03);
		buf.put(m10);
		buf.put(m11);
		buf.put(m12);
		buf.put(m13);
		buf.put(m20);
		buf.put(m21);
		buf.put(m22);
		buf.put(m23);
		buf.put(m30);
		buf.put(m31);
		buf.put(m32);
		buf.put(m33);
		return this;
	}

	/**
	 * Store this matrix in a float buffer. The matrix is stored in row major
	 * (maths) order.
	 * 
	 * @param buf
	 *            The buffer to store this matrix in
	 */
	public Matrix storeTranspose(FloatBuffer buf) {
		buf.put(m00);
		buf.put(m10);
		buf.put(m20);
		buf.put(m30);
		buf.put(m01);
		buf.put(m11);
		buf.put(m21);
		buf.put(m31);
		buf.put(m02);
		buf.put(m12);
		buf.put(m22);
		buf.put(m32);
		buf.put(m03);
		buf.put(m13);
		buf.put(m23);
		buf.put(m33);
		return this;
	}

	/**
	 * Store the rotation portion of this matrix in a float buffer. The matrix
	 * is stored in column major (openGL) order.
	 * 
	 * @param buf
	 *            The buffer to store this matrix in
	 */
	public Matrix store3f(FloatBuffer buf) {
		buf.put(m00);
		buf.put(m01);
		buf.put(m02);
		buf.put(m10);
		buf.put(m11);
		buf.put(m12);
		buf.put(m20);
		buf.put(m21);
		buf.put(m22);
		return this;
	}

	/**
	 * Add two matrices together and place the result in a third matrix.
	 * 
	 * @param left
	 *            The left source matrix
	 * @param right
	 *            The right source matrix
	 * @param dest
	 *            The destination matrix, or null if a new one is to be created
	 * @return the destination matrix
	 */
	public static Matrix4f add(Matrix4f left, Matrix4f right, Matrix4f dest) {
		if (dest == null)
			dest = new Matrix4f();

		dest.m00 = left.m00 + right.m00;
		dest.m01 = left.m01 + right.m01;
		dest.m02 = left.m02 + right.m02;
		dest.m03 = left.m03 + right.m03;
		dest.m10 = left.m10 + right.m10;
		dest.m11 = left.m11 + right.m11;
		dest.m12 = left.m12 + right.m12;
		dest.m13 = left.m13 + right.m13;
		dest.m20 = left.m20 + right.m20;
		dest.m21 = left.m21 + right.m21;
		dest.m22 = left.m22 + right.m22;
		dest.m23 = left.m23 + right.m23;
		dest.m30 = left.m30 + right.m30;
		dest.m31 = left.m31 + right.m31;
		dest.m32 = left.m32 + right.m32;
		dest.m33 = left.m33 + right.m33;

		return dest;
	}

	/**
	 * Subtract the right matrix from the left and place the result in a third
	 * matrix.
	 * 
	 * @param left
	 *            The left source matrix
	 * @param right
	 *            The right source matrix
	 * @param dest
	 *            The destination matrix, or null if a new one is to be created
	 * @return the destination matrix
	 */
	public static Matrix4f sub(Matrix4f left, Matrix4f right, Matrix4f dest) {
		if (dest == null)
			dest = new Matrix4f();

		dest.m00 = left.m00 - right.m00;
		dest.m01 = left.m01 - right.m01;
		dest.m02 = left.m02 - right.m02;
		dest.m03 = left.m03 - right.m03;
		dest.m10 = left.m10 - right.m10;
		dest.m11 = left.m11 - right.m11;
		dest.m12 = left.m12 - right.m12;
		dest.m13 = left.m13 - right.m13;
		dest.m20 = left.m20 - right.m20;
		dest.m21 = left.m21 - right.m21;
		dest.m22 = left.m22 - right.m22;
		dest.m23 = left.m23 - right.m23;
		dest.m30 = left.m30 - right.m30;
		dest.m31 = left.m31 - right.m31;
		dest.m32 = left.m32 - right.m32;
		dest.m33 = left.m33 - right.m33;

		return dest;
	}

	/**
	 * Multiply the right matrix by the left and place the result in a third
	 * matrix.
	 * 
	 * @param left
	 *            The left source matrix
	 * @param right
	 *            The right source matrix
	 * @param dest
	 *            The destination matrix, or null if a new one is to be created
	 * @return the destination matrix
	 */
	public static Matrix4f mul(Matrix4f left, Matrix4f right, Matrix4f dest) {
		if (dest == null)
			dest = new Matrix4f();

		float m00 = left.m00 * right.m00 + left.m10 * right.m01 + left.m20 * right.m02 + left.m30 * right.m03;
		float m01 = left.m01 * right.m00 + left.m11 * right.m01 + left.m21 * right.m02 + left.m31 * right.m03;
		float m02 = left.m02 * right.m00 + left.m12 * right.m01 + left.m22 * right.m02 + left.m32 * right.m03;
		float m03 = left.m03 * right.m00 + left.m13 * right.m01 + left.m23 * right.m02 + left.m33 * right.m03;
		float m10 = left.m00 * right.m10 + left.m10 * right.m11 + left.m20 * right.m12 + left.m30 * right.m13;
		float m11 = left.m01 * right.m10 + left.m11 * right.m11 + left.m21 * right.m12 + left.m31 * right.m13;
		float m12 = left.m02 * right.m10 + left.m12 * right.m11 + left.m22 * right.m12 + left.m32 * right.m13;
		float m13 = left.m03 * right.m10 + left.m13 * right.m11 + left.m23 * right.m12 + left.m33 * right.m13;
		float m20 = left.m00 * right.m20 + left.m10 * right.m21 + left.m20 * right.m22 + left.m30 * right.m23;
		float m21 = left.m01 * right.m20 + left.m11 * right.m21 + left.m21 * right.m22 + left.m31 * right.m23;
		float m22 = left.m02 * right.m20 + left.m12 * right.m21 + left.m22 * right.m22 + left.m32 * right.m23;
		float m23 = left.m03 * right.m20 + left.m13 * right.m21 + left.m23 * right.m22 + left.m33 * right.m23;
		float m30 = left.m00 * right.m30 + left.m10 * right.m31 + left.m20 * right.m32 + left.m30 * right.m33;
		float m31 = left.m01 * right.m30 + left.m11 * right.m31 + left.m21 * right.m32 + left.m31 * right.m33;
		float m32 = left.m02 * right.m30 + left.m12 * right.m31 + left.m22 * right.m32 + left.m32 * right.m33;
		float m33 = left.m03 * right.m30 + left.m13 * right.m31 + left.m23 * right.m32 + left.m33 * right.m33;

		dest.m00 = m00;
		dest.m01 = m01;
		dest.m02 = m02;
		dest.m03 = m03;
		dest.m10 = m10;
		dest.m11 = m11;
		dest.m12 = m12;
		dest.m13 = m13;
		dest.m20 = m20;
		dest.m21 = m21;
		dest.m22 = m22;
		dest.m23 = m23;
		dest.m30 = m30;
		dest.m31 = m31;
		dest.m32 = m32;
		dest.m33 = m33;

		return dest;
	}

	/**
	 * Transform a Vector by a matrix and return the result in a destination
	 * vector.
	 * 
	 * @param left
	 *            The left matrix
	 * @param right
	 *            The right vector
	 * @param dest
	 *            The destination vector, or null if a new one is to be created
	 * @return the destination vector
	 */
	public static Vector4f transform(Matrix4f left, Vector4f right, Vector4f dest) {
		if (dest == null)
			dest = new Vector4f();

		float x = left.m00 * right.x + left.m10 * right.y + left.m20 * right.z + left.m30 * right.w;
		float y = left.m01 * right.x + left.m11 * right.y + left.m21 * right.z + left.m31 * right.w;
		float z = left.m02 * right.x + left.m12 * right.y + left.m22 * right.z + left.m32 * right.w;
		float w = left.m03 * right.x + left.m13 * right.y + left.m23 * right.z + left.m33 * right.w;

		dest.x = x;
		dest.y = y;
		dest.z = z;
		dest.w = w;

		return dest;
	}

	/**
	 * Transpose this matrix
	 * 
	 * @return this
	 */
	public Matrix transpose() {
		return transpose(this);
	}

	/**
	 * Translate this matrix
	 * 
	 * @param vec
	 *            The vector to translate by
	 * @return this
	 */
	public Matrix4f translate(Vector2f vec) {
		return translate(vec, this);
	}

	/**
	 * Translate this matrix
	 * 
	 * @param vec
	 *            The vector to translate by
	 * @return this
	 */
	public Matrix4f translate(Vector3f vec) {
		return translate(vec, this);
	}

	/**
	 * Scales this matrix
	 * 
	 * @param vec
	 *            The vector to scale by
	 * @return this
	 */
	public Matrix4f scale(Vector3f vec) {
		return scale(vec, this, this);
	}

	/**
	 * Scales the source matrix and put the result in the destination matrix
	 * 
	 * @param vec
	 *            The vector to scale by
	 * @param src
	 *            The source matrix
	 * @param dest
	 *            The destination matrix, or null if a new matrix is to be
	 *            created
	 * @return The scaled matrix
	 */
	public static Matrix4f scale(Vector3f vec, Matrix4f src, Matrix4f dest) {
		if (dest == null)
			dest = new Matrix4f();
		dest.m00 = src.m00 * vec.x;
		dest.m01 = src.m01 * vec.x;
		dest.m02 = src.m02 * vec.x;
		dest.m03 = src.m03 * vec.x;
		dest.m10 = src.m10 * vec.y;
		dest.m11 = src.m11 * vec.y;
		dest.m12 = src.m12 * vec.y;
		dest.m13 = src.m13 * vec.y;
		dest.m20 = src.m20 * vec.z;
		dest.m21 = src.m21 * vec.z;
		dest.m22 = src.m22 * vec.z;
		dest.m23 = src.m23 * vec.z;
		return dest;
	}

	public void scale(float scale) {
		this.m00 = this.m00 * scale;
		this.m01 = this.m01 * scale;
		this.m02 = this.m02 * scale;
		this.m03 = this.m03 * scale;
		this.m10 = this.m10 * scale;
		this.m11 = this.m11 * scale;
		this.m12 = this.m12 * scale;
		this.m13 = this.m13 * scale;
		this.m20 = this.m20 * scale;
		this.m21 = this.m21 * scale;
		this.m22 = this.m22 * scale;
		this.m23 = this.m23 * scale;
	}

	/**
	 * Rotates the matrix around the given axis the specified angle
	 * 
	 * @param angle
	 *            the angle, in radians.
	 * @param axis
	 *            The vector representing the rotation axis. Must be normalized.
	 * @return this
	 */
	public Matrix4f rotate(float angle, Vector3f axis) {
		return rotate(angle, axis, this);
	}

	/**
	 * Rotates the matrix around the given axis the specified angle
	 * 
	 * @param angle
	 *            the angle, in radians.
	 * @param axis
	 *            The vector representing the rotation axis. Must be normalized.
	 * @param dest
	 *            The matrix to put the result, or null if a new matrix is to be
	 *            created
	 * @return The rotated matrix
	 */
	public Matrix4f rotate(float angle, Vector3f axis, Matrix4f dest) {
		return rotate(angle, axis, this, dest);
	}

	/**
	 * Rotates the source matrix around the given axis the specified angle and
	 * put the result in the destination matrix.
	 * 
	 * @param angle
	 *            the angle, in radians.
	 * @param axis
	 *            The vector representing the rotation axis. Must be normalized.
	 * @param src
	 *            The matrix to rotate
	 * @param dest
	 *            The matrix to put the result, or null if a new matrix is to be
	 *            created
	 * @return The rotated matrix
	 */
	public static Matrix4f rotate(float angle, Vector3f axis, Matrix4f src, Matrix4f dest) {
		if (dest == null)
			dest = new Matrix4f();
		float c = (float) Math.cos(angle);
		float s = (float) Math.sin(angle);
		float oneminusc = 1.0f - c;
		float xy = axis.x * axis.y;
		float yz = axis.y * axis.z;
		float xz = axis.x * axis.z;
		float xs = axis.x * s;
		float ys = axis.y * s;
		float zs = axis.z * s;

		float f00 = axis.x * axis.x * oneminusc + c;
		float f01 = xy * oneminusc + zs;
		float f02 = xz * oneminusc - ys;
		// n[3] not used
		float f10 = xy * oneminusc - zs;
		float f11 = axis.y * axis.y * oneminusc + c;
		float f12 = yz * oneminusc + xs;
		// n[7] not used
		float f20 = xz * oneminusc + ys;
		float f21 = yz * oneminusc - xs;
		float f22 = axis.z * axis.z * oneminusc + c;

		float t00 = src.m00 * f00 + src.m10 * f01 + src.m20 * f02;
		float t01 = src.m01 * f00 + src.m11 * f01 + src.m21 * f02;
		float t02 = src.m02 * f00 + src.m12 * f01 + src.m22 * f02;
		float t03 = src.m03 * f00 + src.m13 * f01 + src.m23 * f02;
		float t10 = src.m00 * f10 + src.m10 * f11 + src.m20 * f12;
		float t11 = src.m01 * f10 + src.m11 * f11 + src.m21 * f12;
		float t12 = src.m02 * f10 + src.m12 * f11 + src.m22 * f12;
		float t13 = src.m03 * f10 + src.m13 * f11 + src.m23 * f12;
		dest.m20 = src.m00 * f20 + src.m10 * f21 + src.m20 * f22;
		dest.m21 = src.m01 * f20 + src.m11 * f21 + src.m21 * f22;
		dest.m22 = src.m02 * f20 + src.m12 * f21 + src.m22 * f22;
		dest.m23 = src.m03 * f20 + src.m13 * f21 + src.m23 * f22;
		dest.m00 = t00;
		dest.m01 = t01;
		dest.m02 = t02;
		dest.m03 = t03;
		dest.m10 = t10;
		dest.m11 = t11;
		dest.m12 = t12;
		dest.m13 = t13;
		return dest;
	}

	public void rotateX(float angle) {
		Matrix4f.rotateX(angle, this, this);
	}

	public void rotateY(float angle) {
		Matrix4f.rotateY(angle, this, this);
	}

	public void rotateZ(float angle) {
		Matrix4f.rotateZ(angle, this, this);
	}

	public void rotateXYZ(Vector3f rot) {
		this.rotateXYZ(rot.x, rot.y, rot.z);
	}

	public void rotateXYZ(float rx, float ry, float rz) {
		rotateXYZ(rx, ry, rz, this, this);
	}

	public static Matrix4f rotateX(float angle, Matrix4f src, Matrix4f dest) {

		if (dest == null) {
			dest = new Matrix4f();
		}
		float c = (float) Math.cos(angle);
		float s = (float) Math.sin(angle);

		float t00 = src.m00;
		float t01 = src.m01;
		float t02 = src.m02;
		float t03 = src.m03;
		float t10 = src.m10 * c + src.m20 * s;
		float t11 = src.m11 * c + src.m21 * s;
		float t12 = src.m12 * c + src.m22 * s;
		float t13 = src.m13 * c + src.m23 * s;
		dest.m20 = src.m10 * -s + src.m20 * c;
		dest.m21 = src.m11 * -s + src.m21 * c;
		dest.m22 = src.m12 * -s + src.m22 * c;
		dest.m23 = src.m13 * -s + src.m23 * c;
		dest.m00 = t00;
		dest.m01 = t01;
		dest.m02 = t02;
		dest.m03 = t03;
		dest.m10 = t10;
		dest.m11 = t11;
		dest.m12 = t12;
		dest.m13 = t13;
		return dest;

	}

	public static Matrix4f rotateY(float angle, Matrix4f src, Matrix4f dest) {
		if (dest == null)
			dest = new Matrix4f();
		float c = (float) Math.cos(angle);
		float s = (float) Math.sin(angle);

		float t00 = src.m00 * c + src.m20 * -s;
		float t01 = src.m01 * c + src.m21 * -s;
		float t02 = src.m02 * c + src.m22 * -s;
		float t03 = src.m03 * c + src.m23 * -s;
		float t10 = src.m10;
		float t11 = src.m11;
		float t12 = src.m12;
		float t13 = src.m13;
		dest.m20 = src.m00 * s + src.m20 * c;
		dest.m21 = src.m01 * s + src.m21 * c;
		dest.m22 = src.m02 * s + src.m22 * c;
		dest.m23 = src.m03 * s + src.m23 * c;
		dest.m00 = t00;
		dest.m01 = t01;
		dest.m02 = t02;
		dest.m03 = t03;
		dest.m10 = t10;
		dest.m11 = t11;
		dest.m12 = t12;
		dest.m13 = t13;
		return dest;
	}

	public static Matrix4f rotateZ(float angle, Matrix4f src, Matrix4f dest) {
		if (dest == null)
			dest = new Matrix4f();
		float c = (float) Math.cos(angle);
		float s = (float) Math.sin(angle);

		float t00 = src.m00 * c + src.m10 * s;
		float t01 = src.m01 * c + src.m11 * s;
		float t02 = src.m02 * c + src.m12 * s;
		float t03 = src.m03 * c + src.m13 * s;
		float t10 = src.m00 * -s + src.m10 * c;
		float t11 = src.m01 * -s + src.m11 * c;
		float t12 = src.m02 * -s + src.m12 * c;
		float t13 = src.m03 * -s + src.m13 * c;
		dest.m20 = src.m20;
		dest.m21 = src.m21;
		dest.m22 = src.m22;
		dest.m23 = src.m23;
		dest.m00 = t00;
		dest.m01 = t01;
		dest.m02 = t02;
		dest.m03 = t03;
		dest.m10 = t10;
		dest.m11 = t11;
		dest.m12 = t12;
		dest.m13 = t13;
		return dest;
	}

	public static Matrix4f rotateXYZ(Vector3f rot, Matrix4f src, Matrix4f dest) {
		return (rotateXYZ(rot.x, rot.y, rot.z, src, dest));
	}

	private static Matrix4f rotateXYZ(float rx, float ry, float rz, Matrix4f src, Matrix4f dest) {
		rotateX(rx, src, dest);
		rotateY(ry, src, dest);
		rotateZ(rz, src, dest);
		return (dest);
	}

	/**
	 * Translate this matrix and stash the result in another matrix
	 * 
	 * @param vec
	 *            The vector to translate by
	 * @param dest
	 *            The destination matrix or null if a new matrix is to be
	 *            created
	 * @return the translated matrix
	 */
	public Matrix4f translate(Vector3f vec, Matrix4f dest) {
		return translate(vec, this, dest);
	}

	/**
	 * Translate the source matrix and stash the result in the destination
	 * matrix
	 * 
	 * @param vec
	 *            The vector to translate by
	 * @param src
	 *            The source matrix
	 * @param dest
	 *            The destination matrix or null if a new matrix is to be
	 *            created
	 * @return The translated matrix
	 */
	public static Matrix4f translate(Vector3f vec, Matrix4f src, Matrix4f dest) {
		if (dest == null)
			dest = new Matrix4f();

		dest.m30 += src.m00 * vec.x + src.m10 * vec.y + src.m20 * vec.z;
		dest.m31 += src.m01 * vec.x + src.m11 * vec.y + src.m21 * vec.z;
		dest.m32 += src.m02 * vec.x + src.m12 * vec.y + src.m22 * vec.z;
		dest.m33 += src.m03 * vec.x + src.m13 * vec.y + src.m23 * vec.z;

		return dest;
	}

	public void translate(float x, float y, float z) {
		this.m30 += this.m00 * x + this.m10 * y + this.m20 * z;
		this.m31 += this.m01 * x + this.m11 * y + this.m21 * z;
		this.m32 += this.m02 * x + this.m12 * y + this.m22 * z;
		this.m33 += this.m03 * x + this.m13 * y + this.m23 * z;
	}

	/**
	 * Translate this matrix and stash the result in another matrix
	 * 
	 * @param vec
	 *            The vector to translate by
	 * @param dest
	 *            The destination matrix or null if a new matrix is to be
	 *            created
	 * @return the translated matrix
	 */
	public Matrix4f translate(Vector2f vec, Matrix4f dest) {
		return translate(vec, this, dest);
	}

	/**
	 * Translate the source matrix and stash the result in the destination
	 * matrix
	 * 
	 * @param vec
	 *            The vector to translate by
	 * @param src
	 *            The source matrix
	 * @param dest
	 *            The destination matrix or null if a new matrix is to be
	 *            created
	 * @return The translated matrix
	 */
	public static Matrix4f translate(Vector2f vec, Matrix4f src, Matrix4f dest) {
		if (dest == null)
			dest = new Matrix4f();

		dest.m30 += src.m00 * vec.x + src.m10 * vec.y;
		dest.m31 += src.m01 * vec.x + src.m11 * vec.y;
		dest.m32 += src.m02 * vec.x + src.m12 * vec.y;
		dest.m33 += src.m03 * vec.x + src.m13 * vec.y;

		return dest;
	}

	/**
	 * Transpose this matrix and place the result in another matrix
	 * 
	 * @param dest
	 *            The destination matrix or null if a new matrix is to be
	 *            created
	 * @return the transposed matrix
	 */
	public Matrix4f transpose(Matrix4f dest) {
		return transpose(this, dest);
	}

	/**
	 * Transpose the source matrix and place the result in the destination
	 * matrix
	 * 
	 * @param src
	 *            The source matrix
	 * @param dest
	 *            The destination matrix or null if a new matrix is to be
	 *            created
	 * @return the transposed matrix
	 */
	public static Matrix4f transpose(Matrix4f src, Matrix4f dest) {
		if (dest == null)
			dest = new Matrix4f();
		float m00 = src.m00;
		float m01 = src.m10;
		float m02 = src.m20;
		float m03 = src.m30;
		float m10 = src.m01;
		float m11 = src.m11;
		float m12 = src.m21;
		float m13 = src.m31;
		float m20 = src.m02;
		float m21 = src.m12;
		float m22 = src.m22;
		float m23 = src.m32;
		float m30 = src.m03;
		float m31 = src.m13;
		float m32 = src.m23;
		float m33 = src.m33;

		dest.m00 = m00;
		dest.m01 = m01;
		dest.m02 = m02;
		dest.m03 = m03;
		dest.m10 = m10;
		dest.m11 = m11;
		dest.m12 = m12;
		dest.m13 = m13;
		dest.m20 = m20;
		dest.m21 = m21;
		dest.m22 = m22;
		dest.m23 = m23;
		dest.m30 = m30;
		dest.m31 = m31;
		dest.m32 = m32;
		dest.m33 = m33;

		return dest;
	}

	/**
	 * @return the determinant of the matrix
	 */
	public float determinant() {
		float f = m00 * ((m11 * m22 * m33 + m12 * m23 * m31 + m13 * m21 * m32) - m13 * m22 * m31 - m11 * m23 * m32
				- m12 * m21 * m33);
		f -= m01 * ((m10 * m22 * m33 + m12 * m23 * m30 + m13 * m20 * m32) - m13 * m22 * m30 - m10 * m23 * m32
				- m12 * m20 * m33);
		f += m02 * ((m10 * m21 * m33 + m11 * m23 * m30 + m13 * m20 * m31) - m13 * m21 * m30 - m10 * m23 * m31
				- m11 * m20 * m33);
		f -= m03 * ((m10 * m21 * m32 + m11 * m22 * m30 + m12 * m20 * m31) - m12 * m21 * m30 - m10 * m22 * m31
				- m11 * m20 * m32);
		return f;
	}

	/**
	 * Calculate the determinant of a 3x3 matrix
	 * 
	 * @return result
	 */

	private static float determinant3x3(float t00, float t01, float t02, float t10, float t11, float t12, float t20,
			float t21, float t22) {
		return t00 * (t11 * t22 - t12 * t21) + t01 * (t12 * t20 - t10 * t22) + t02 * (t10 * t21 - t11 * t20);
	}

	/**
	 * Invert this matrix
	 * 
	 * @return this if successful, null otherwise
	 */
	public Matrix invert() {
		return invert(this, this);
	}

	/**
	 * Invert the source matrix and put the result in the destination
	 * 
	 * @param src
	 *            The source matrix
	 * @param dest
	 *            The destination matrix, or null if a new matrix is to be
	 *            created
	 * @return The inverted matrix if successful, null otherwise
	 */
	public static Matrix4f invert(Matrix4f src, Matrix4f dest) {
		float determinant = src.determinant();

		if (determinant != 0) {
			/*
			 * m00 m01 m02 m03 m10 m11 m12 m13 m20 m21 m22 m23 m30 m31 m32 m33
			 */
			if (dest == null)
				dest = new Matrix4f();
			float determinant_inv = 1f / determinant;

			// first row
			float t00 = determinant3x3(src.m11, src.m12, src.m13, src.m21, src.m22, src.m23, src.m31, src.m32, src.m33);
			float t01 = -determinant3x3(src.m10, src.m12, src.m13, src.m20, src.m22, src.m23, src.m30, src.m32,
					src.m33);
			float t02 = determinant3x3(src.m10, src.m11, src.m13, src.m20, src.m21, src.m23, src.m30, src.m31, src.m33);
			float t03 = -determinant3x3(src.m10, src.m11, src.m12, src.m20, src.m21, src.m22, src.m30, src.m31,
					src.m32);
			// second row
			float t10 = -determinant3x3(src.m01, src.m02, src.m03, src.m21, src.m22, src.m23, src.m31, src.m32,
					src.m33);
			float t11 = determinant3x3(src.m00, src.m02, src.m03, src.m20, src.m22, src.m23, src.m30, src.m32, src.m33);
			float t12 = -determinant3x3(src.m00, src.m01, src.m03, src.m20, src.m21, src.m23, src.m30, src.m31,
					src.m33);
			float t13 = determinant3x3(src.m00, src.m01, src.m02, src.m20, src.m21, src.m22, src.m30, src.m31, src.m32);
			// third row
			float t20 = determinant3x3(src.m01, src.m02, src.m03, src.m11, src.m12, src.m13, src.m31, src.m32, src.m33);
			float t21 = -determinant3x3(src.m00, src.m02, src.m03, src.m10, src.m12, src.m13, src.m30, src.m32,
					src.m33);
			float t22 = determinant3x3(src.m00, src.m01, src.m03, src.m10, src.m11, src.m13, src.m30, src.m31, src.m33);
			float t23 = -determinant3x3(src.m00, src.m01, src.m02, src.m10, src.m11, src.m12, src.m30, src.m31,
					src.m32);
			// fourth row
			float t30 = -determinant3x3(src.m01, src.m02, src.m03, src.m11, src.m12, src.m13, src.m21, src.m22,
					src.m23);
			float t31 = determinant3x3(src.m00, src.m02, src.m03, src.m10, src.m12, src.m13, src.m20, src.m22, src.m23);
			float t32 = -determinant3x3(src.m00, src.m01, src.m03, src.m10, src.m11, src.m13, src.m20, src.m21,
					src.m23);
			float t33 = determinant3x3(src.m00, src.m01, src.m02, src.m10, src.m11, src.m12, src.m20, src.m21, src.m22);

			// transpose and divide by the determinant
			dest.m00 = t00 * determinant_inv;
			dest.m11 = t11 * determinant_inv;
			dest.m22 = t22 * determinant_inv;
			dest.m33 = t33 * determinant_inv;
			dest.m01 = t10 * determinant_inv;
			dest.m10 = t01 * determinant_inv;
			dest.m20 = t02 * determinant_inv;
			dest.m02 = t20 * determinant_inv;
			dest.m12 = t21 * determinant_inv;
			dest.m21 = t12 * determinant_inv;
			dest.m03 = t30 * determinant_inv;
			dest.m30 = t03 * determinant_inv;
			dest.m13 = t31 * determinant_inv;
			dest.m31 = t13 * determinant_inv;
			dest.m32 = t23 * determinant_inv;
			dest.m23 = t32 * determinant_inv;
			return dest;
		} else
			return null;
	}

	/**
	 * Negate this matrix
	 * 
	 * @return this
	 */
	public Matrix negate() {
		return negate(this);
	}

	/**
	 * Negate this matrix and place the result in a destination matrix.
	 * 
	 * @param dest
	 *            The destination matrix, or null if a new matrix is to be
	 *            created
	 * @return the negated matrix
	 */
	public Matrix4f negate(Matrix4f dest) {
		return negate(this, dest);
	}

	/**
	 * Negate this matrix and place the result in a destination matrix.
	 * 
	 * @param src
	 *            The source matrix
	 * @param dest
	 *            The destination matrix, or null if a new matrix is to be
	 *            created
	 * @return The negated matrix
	 */
	public static Matrix4f negate(Matrix4f src, Matrix4f dest) {
		if (dest == null)
			dest = new Matrix4f();

		dest.m00 = -src.m00;
		dest.m01 = -src.m01;
		dest.m02 = -src.m02;
		dest.m03 = -src.m03;
		dest.m10 = -src.m10;
		dest.m11 = -src.m11;
		dest.m12 = -src.m12;
		dest.m13 = -src.m13;
		dest.m20 = -src.m20;
		dest.m21 = -src.m21;
		dest.m22 = -src.m22;
		dest.m23 = -src.m23;
		dest.m30 = -src.m30;
		dest.m31 = -src.m31;
		dest.m32 = -src.m32;
		dest.m33 = -src.m33;

		return dest;
	}

	public static final Vector3f DEFAULT_POS = new Vector3f(0, 0, 0);
	public static final Vector3f DEFAULT_ROT = new Vector3f(0, 0, 0);
	public static final Vector3f DEFAULT_SCALE = new Vector3f(1, 1, 1);
	public static final Vector3f DEFAULT_OFFSET = new Vector3f(0.5f, 0.5f, 0.5f);

	public static final Matrix4f IDENTITY = new Matrix4f();

	public static Matrix4f createTransformationMatrix(Matrix4f dst, Vector3f pos, Vector3f rot, Vector3f scale) {
		if (dst == null) {
			dst = new Matrix4f();
		}
		if (pos == null) {
			pos = Matrix4f.DEFAULT_POS;
		}
		if (rot == null) {
			rot = Matrix4f.DEFAULT_ROT;
		}
		if (scale == null) {
			scale = Matrix4f.DEFAULT_SCALE;
		}

		dst.setIdentity();
		dst.translate(pos);
		dst.rotateX((float) Math.toRadians(rot.x));
		dst.rotateY((float) Math.toRadians(rot.y));
		dst.rotateZ((float) Math.toRadians(rot.z));
		dst.scale(scale);
		return (dst);
	}

	public static Matrix4f createTransformationMatrixWithOffset(Matrix4f dst, Vector3f pos, Vector3f rot,
			Vector3f offset, Vector3f scale) {

		if (dst == null) {
			dst = new Matrix4f();
		}

		if (pos == null) {
			pos = Matrix4f.DEFAULT_POS;
		}
		if (rot == null) {
			rot = Matrix4f.DEFAULT_ROT;
		}
		if (scale == null) {
			scale = Matrix4f.DEFAULT_SCALE;
		}
		if (offset == null) {
			return (Matrix4f.createTransformationMatrix(dst, pos, rot, scale));
		}

		dst.setIdentity();

		dst.scale(scale);

		dst.translate(offset);
		dst.rotateX((float) Math.toRadians(rot.x));
		dst.rotateY((float) Math.toRadians(rot.y));
		dst.rotateZ((float) Math.toRadians(rot.z));
		dst.translate(offset.negate(null));

		dst.translate(pos);

		return (dst);
	}

	public static Matrix4f lookat(Vector3f pos, Vector3f lookpoint) {
		Matrix4f view = new Matrix4f();

		Vector3f Z = Vector3f.sub(pos, lookpoint, null).normalise(null);
		Vector3f X = Vector3f.cross(new Vector3f(0, 1, 0), Z, null).normalise(null);
		Vector3f Y = Vector3f.cross(Z, X, null);

		view.m00 = X.x;
		view.m10 = X.y;
		view.m20 = X.z;
		view.m30 = -Vector3f.dot(X, pos);

		view.m01 = Y.x;
		view.m11 = Y.y;
		view.m21 = Y.z;
		view.m31 = -Vector3f.dot(Y, pos);

		view.m02 = Z.x;
		view.m12 = Z.y;
		view.m22 = Z.z;
		view.m32 = -Vector3f.dot(Z, pos);

		view.m03 = 0;
		view.m13 = 0;
		view.m23 = 0;
		view.m33 = 1;

		return (view);
	}

	/**
	 * Creates an orthographic projection matrix. This method is analogous to
	 * the now deprecated {@code glOrtho} method.
	 * 
	 * @param left
	 *            left vertical clipping plane
	 * @param right
	 *            right vertical clipping plane
	 * @param bottom
	 *            bottom horizontal clipping plane
	 * @param top
	 *            top horizontal clipping plane
	 * @param zNear
	 *            distance to nearer depth clipping plane (negative if the plane
	 *            is to be behind the viewer)
	 * @param zFar
	 *            distance to farther depth clipping plane (negative if the
	 *            plane is to be behind the viewer)
	 * @return
	 */
	// public static final Matrix4f orthographic(Matrix4f dst, final float left,
	// final float right, final float bottom,
	// final float top, final float zNear, final float zFar) {
	// if (dst == null) {
	// dst = new Matrix4f();
	// }
	//
	// final float m00 = 2f / (right - left);
	// final float m11 = 2f / (top - bottom);
	// final float m22 = -2f / (zFar - zNear);
	// final float m30 = -(right + left) / (right - left);
	// final float m31 = -(top + bottom) / (top - bottom);
	// final float m32 = -(zFar + zNear) / (zFar - zNear);
	//
	// dst.m00 = m00;
	// dst.m10 = 0f;
	// dst.m20 = 0f;
	// dst.m30 = m30;
	//
	// dst.m01 = 0f;
	// dst.m11 = m11;
	// dst.m21 = 0f;
	// dst.m31 = m31;
	//
	// dst.m02 = 0f;
	// dst.m12 = 0f;
	// dst.m22 = m22;
	// dst.m32 = m32;
	//
	// dst.m03 = 0f;
	// dst.m13 = 0f;
	// dst.m23 = 0f;
	// dst.m33 = 1f;
	//
	// return (dst);
	// }

	public void set(Matrix4f matrix) {
		this.m00 = matrix.m00;
		this.m01 = matrix.m01;
		this.m02 = matrix.m02;
		this.m03 = matrix.m03;

		this.m10 = matrix.m10;
		this.m11 = matrix.m11;
		this.m12 = matrix.m12;
		this.m13 = matrix.m13;

		this.m20 = matrix.m20;
		this.m21 = matrix.m21;
		this.m22 = matrix.m22;
		this.m23 = matrix.m23;

		this.m30 = matrix.m30;
		this.m31 = matrix.m31;
		this.m32 = matrix.m32;
		this.m33 = matrix.m33;
	}

	public static Matrix4f perspective(Matrix4f dst, float aspect, float fov, float near, float far) {
		if (dst == null) {
			dst = new Matrix4f();
		}

		float y_scale = (float) (1.0f / Math.tan(fov / 2.0f) * aspect);
		float x_scale = y_scale / aspect;
		float frustrum_length = far - near;

		dst.setIdentity();
		dst.m00 = x_scale;
		dst.m11 = y_scale;
		dst.m22 = -((far + near) / frustrum_length);
		dst.m23 = -1;
		dst.m32 = -((2 * near * far) / frustrum_length);
		dst.m33 = 0;

		return (dst);
	}

	public static Matrix4f perspective_glm(Matrix4f dst, float aspect, float fov, float near, float far) {
		if (dst == null) {
			dst = new Matrix4f();
		}

		final float halfFovyRadians = (float) Math.toRadians((fov / 2.0f));
		final float range = (float) Math.tan(halfFovyRadians) * near;
		final float left = -range * aspect;
		final float right = range * aspect;
		final float bottom = -range;
		final float top = range;

		dst.setIdentity();

		dst.m00 = (2f * near) / (right - left);
		dst.m10 = 0;
		dst.m20 = 0;
		dst.m30 = 0;

		dst.m01 = 0;
		dst.m11 = (2f * near) / (top - bottom);
		dst.m21 = 0;
		dst.m31 = 0;

		dst.m02 = 0;
		dst.m12 = 0;
		dst.m22 = -(far + near) / (far - near);
		dst.m32 = -(2f * far * near);

		dst.m03 = 0;
		dst.m13 = 0;
		dst.m23 = -1.0f;
		dst.m33 = 0;

		return (dst);
	}

	public static Matrix4f orthographic(Matrix4f dst, float left, float right, float bot, float top, float near,
			float far) {
		if (dst == null) {
			dst = new Matrix4f();
		}

		dst.m00 = 2 / (right - left);
		dst.m01 = 0;
		dst.m02 = 0;
		dst.m03 = (right + left) / (left - right);

		dst.m10 = 0;
		dst.m11 = 2 / (top - bot);
		dst.m12 = 0;
		dst.m13 = (top + bot) / (bot - top);

		dst.m20 = 0;
		dst.m21 = 0;
		dst.m22 = 2 / (near - far);
		dst.m23 = (far + near) / (near - far);

		dst.m30 = 0f;
		dst.m31 = 0f;
		dst.m32 = 0f;
		dst.m33 = 1f;

		return (dst);
	}
}