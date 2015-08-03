package com.grillecube.client.renderer.opengl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.grillecube.client.Game;

import fr.toss.lib.Logger;

/** openGL program */
public abstract class Program
{
	private FloatBuffer	_matrix_buffer;

	private int	_programID;
	private int	_vertexID;
	private int	_fragmentID;
	
	public Program(String vertex, String fragment)
	{
		vertex = "./assets/shaders/" + vertex + ".vertex";
		fragment = "./assets/shaders/" + fragment + ".fragment";
		
		this._matrix_buffer = BufferUtils.createFloatBuffer(4 * 4);
		this._vertexID = Shader.loadShader(vertex, GL20.GL_VERTEX_SHADER);
		this._fragmentID = Shader.loadShader(fragment, GL20.GL_FRAGMENT_SHADER);
		
		this._programID = GL20.glCreateProgram();
		GL20.glAttachShader(this._programID, this._vertexID);
		GL20.glAttachShader(this._programID, this._fragmentID);
		this.bindAttributes();
		GL20.glLinkProgram(this._programID);
		GL20.glValidateProgram(this._programID);
		this.linkUniforms();
	}
	
	public abstract void	bindAttributes();
	
	/** set every uniforms variables */
	public abstract void	linkUniforms();
	
	public void stop()
	{
		GL20.glDetachShader(this._programID, this._vertexID);
		GL20.glDetachShader(this._programID, this._fragmentID);
		GL20.glDeleteShader(this._vertexID);
		GL20.glDeleteShader(this._fragmentID);
		GL20.glDeleteProgram(this._programID);
	}
	
	public void	useStart()
	{
		GL20.glUseProgram(this._programID);
	}
	
	public void	useStop()
	{
		GL20.glUseProgram(0);
	}
	
	protected void	bindAttribute(int attribute, String name)
	{
		GL20.glBindAttribLocation(this._programID, attribute, name);
	}
	
	protected void	loadUniformInteger(int location, int value)
	{
		GL20.glUniform1i(location, value);
	}
	
	protected void	loadUniformFloat(int location, float value)
	{
		GL20.glUniform1f(location, value);
	}
	
	protected void	loadUniformVec(int location, Vector3f vec)
	{
		GL20.glUniform3f(location, vec.x, vec.y, vec.z);
	}
	
	public void loadUniformVec4(int location, Vector4f vec)
	{
		GL20.glUniform4f(location, vec.x, vec.y, vec.z, vec.w);
	}
	
	public void	loadUniformMatrix(int location, Matrix4f matrix)
	{
		matrix.store(_matrix_buffer);
		_matrix_buffer.flip();
		GL20.glUniformMatrix4fv(location, false, _matrix_buffer);
	}
	
	
	public static void loadTransformationMatrix(Program program, int location, Vector3f pos, Vector3f rot, Vector3f scale)
	{
		Matrix4f matrix;
		
		matrix = new Matrix4f();
		
		matrix.translate(pos);
		matrix.rotate(rot.x, new Vector3f(1, 0, 0));
		matrix.rotate(rot.y, new Vector3f(0, 1, 0));
		matrix.rotate(rot.z, new Vector3f(0, 0, 1));
		matrix.scale(scale);
		
		program.loadUniformMatrix(location, matrix);
	}

	public int getUniform(String name)
	{
		return (GL20.glGetUniformLocation(this._programID, name));
	}
}
