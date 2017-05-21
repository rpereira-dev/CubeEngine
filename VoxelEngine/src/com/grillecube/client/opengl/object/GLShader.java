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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.grillecube.common.Logger;

public class GLShader implements GLObject
{
	private int _id;
	
	public GLShader(int id)
	{
		this._id = id;
	}

	public static GLShader load(String filepath, int type)
	{
		GLShader shader = new GLShader(ShaderLoader.loadShader(filepath, type));
		return (shader);
	}
	
	public int getID()
	{
		return (this._id);
	}

	@Override
	public void delete()
	{
		GL20.glDeleteShader(this._id);
	}
}

class ShaderLoader
{
	public static int loadShader(String filepath, int type)
	{		
		Logger.get().log(Logger.Level.FINE, "Loading shader: " + filepath);

		try {
			String source = readFile(filepath);
			int shader_id = GL20.glCreateShader(type);
			GL20.glShaderSource(shader_id, source);
			GL20.glCompileShader(shader_id);
			if (GL20.glGetShaderi(shader_id, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE)
			{
				System.out.println(GL20.glGetShaderInfoLog(shader_id, 512));
				System.err.println("Couldnt compile shader: " + filepath);
				return (-1);
			}
			return (shader_id);
		} catch (IOException e) {
			System.out.println("couldnt read file: " + filepath);
			e.printStackTrace();
			return (-1);
		}		
	}

	private static String readFile(String filepath) throws IOException
	{
		StringBuilder source = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(filepath));
		String line;
		while ((line = reader.readLine()) != null)
		{
			if (line.trim().startsWith("#extern"))
			{
				String curdir = filepath.substring(0, filepath.lastIndexOf(File.separatorChar) + 1);
				String filename = line.substring(7).replaceAll(" ", "").replaceAll("'", "").replaceAll("\"", "");
				line = readFile(curdir + filename);
			}
			source.append(line).append("\n");
		}
		reader.close();
		return (source.toString());
	}
}