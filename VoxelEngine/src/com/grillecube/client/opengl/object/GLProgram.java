/**
**	This file is part of the project https://github.com/toss-dev/VoxelEngine
**
**	License is available here: https://raw.githubusercontent.com/toss-dev/VoxelEngine/master/LICENSE.md
**
**	PEREIRA Romain
**                                       4-----7          
**                                      /|    /|
**                                     0-----3 |
**                                     | 5___|_6
**                                     |/    | /
**                                     1-----2
*/

package com.grillecube.client.opengl.object;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import com.grillecube.client.opengl.GLH;
import com.grillecube.common.Logger;
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.maths.Vector2f;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector4f;

/**
 * How to use: 
 * Program p = new Program();
 * 
 * p.addShader(GLH.loadShader("..."));
 * p.link();
 * 
 * p.useStart();
 * render();
 * p.useStop();
 */

/** openGL program */
public abstract class GLProgram implements GLObject {
	private FloatBuffer matrixBuffer;
	private ArrayList<GLShader> shaders;
	private int progID;

	/** standart program class with a vertex + fragment shader */
	public GLProgram() {
		this.matrixBuffer = BufferUtils.createFloatBuffer(4 * 4);
		this.shaders = new ArrayList<GLShader>();
	}

	public void addShader(GLShader shader) {
		this.shaders.add(shader);
	}

	public void link() {
		this.progID = GL20.glCreateProgram();
		for (GLShader shader : this.shaders) {
			GL20.glAttachShader(this.progID, shader.getID());
		}
		this.bindAttributes();
		GL20.glLinkProgram(this.progID);
		String message = GL20.glGetProgramInfoLog(this.progID);
		if (message.length() > 0) {
			Logger.get().log(Logger.Level.WARNING, "Linking shader message: " + message);
		}

		GL20.glValidateProgram(this.progID);

		this.linkUniforms();

		GLH.glhAddObject(this);
	}

	public abstract void bindAttributes();

	/** set every uniforms variables */
	public abstract void linkUniforms();

	/**
	 * GLObject implementation, do not call it, this will be called
	 * automatically when program exit
	 */
	@Override
	public void delete() {
		GL20.glDeleteProgram(this.progID);
	}

	public void useStart() {
		GL20.glUseProgram(this.progID);
	}

	public void useStop() {
		GL20.glUseProgram(0);
	}

	protected void bindAttribute(int attribute, String name) {
		GL20.glBindAttribLocation(this.progID, attribute, name);
	}

	protected void loadUniformInteger(int location, int value) {
		GL20.glUniform1i(location, value);
	}

	protected void loadUniformFloat(int location, float value) {
		GL20.glUniform1f(location, value);
	}

	protected void loadUniformVec(int location, Vector2f vec) {
		this.loadUniformVec(location, vec.x, vec.y);
	}

	protected void loadUniformVec(int location, Vector3f vec) {
		this.loadUniformVec(location, vec.x, vec.y, vec.z);
	}

	protected void loadUniformVec(int location, Vector4f vec) {
		this.loadUniformVec(location, vec.x, vec.y, vec.z, vec.w);
	}

	protected void loadUniformVec(int location, float x, float y) {
		GL20.glUniform2f(location, x, y);
	}

	protected void loadUniformVec(int location, float x, float y, float z) {
		GL20.glUniform3f(location, x, y, z);
	}

	protected void loadUniformVec(int location, float x, float y, float z, float w) {
		GL20.glUniform4f(location, x, y, z, w);
	}

	protected void loadUniformMatrix(int location, Matrix4f matrix) {
		matrix.store(matrixBuffer);
		matrixBuffer.flip();
		GL20.glUniformMatrix4fv(location, false, matrixBuffer);
	}

	public int getUniform(String name) {
		return (GL20.glGetUniformLocation(this.progID, name));
	}
}
