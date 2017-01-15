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

package com.grillecube.engine.opengl.object;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import com.grillecube.engine.Logger;
import com.grillecube.engine.maths.Matrix4f;
import com.grillecube.engine.maths.Vector2f;
import com.grillecube.engine.maths.Vector3f;
import com.grillecube.engine.maths.Vector4f;
import com.grillecube.engine.opengl.GLH;

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
	private FloatBuffer _matrix_buffer;
	private ArrayList<GLShader> _shaders;
	private int _programID;

	/** standart program class with a vertex + fragment shader */
	public GLProgram() {
		this._matrix_buffer = BufferUtils.createFloatBuffer(4 * 4);
		this._shaders = new ArrayList<GLShader>();
	}

	public void addShader(GLShader shader) {
		this._shaders.add(shader);
	}

	public void link() {
		this._programID = GL20.glCreateProgram();
		for (GLShader shader : this._shaders) {
			GL20.glAttachShader(this._programID, shader.getID());
		}
		this.bindAttributes();
		GL20.glLinkProgram(this._programID);
		String message = GL20.glGetProgramInfoLog(this._programID);
		if (message.length() > 0) {
			Logger.get().log(Logger.Level.WARNING, "Linking shader message: " + message);
		}

		GL20.glValidateProgram(this._programID);

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
		GL20.glDeleteProgram(this._programID);
	}

	public void useStart() {
		GL20.glUseProgram(this._programID);
	}

	public void useStop() {
		GL20.glUseProgram(0);
	}

	protected void bindAttribute(int attribute, String name) {
		GL20.glBindAttribLocation(this._programID, attribute, name);
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
		matrix.store(_matrix_buffer);
		_matrix_buffer.flip();
		GL20.glUniformMatrix4fv(location, false, _matrix_buffer);
	}

	public int getUniform(String name) {
		return (GL20.glGetUniformLocation(this._programID, name));
	}
}
