package com.grillecube.client.opengl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.grillecube.common.logger.Logger;

public class Shader
{
	public static int 		loadShader(String file, int type)
	{
		StringBuilder	shader_source;
		BufferedReader	reader;
		String			line;
		int				shader_id;
		
		Logger.get().log(Logger.Level.FINE, "Loading shader: " + file);

		shader_id = 0;
		try {
			shader_source = new StringBuilder();
			reader = new BufferedReader(new FileReader(file));
			while ((line = reader.readLine()) != null)
				shader_source.append(line).append("\n");
			reader.close();
			shader_id = GL20.glCreateShader(type);
			GL20.glShaderSource(shader_id, shader_source);
			GL20.glCompileShader(shader_id);
			if (GL20.glGetShaderi(shader_id, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE)
			{
				System.out.println(GL20.glGetShaderInfoLog(shader_id, 512));
				System.err.println("Couldnt compile shader: " + file);
				return (-1);
			}
		} catch (IOException e) {
			System.out.println("couldnt read file: " + file);
			e.printStackTrace();
			return (-1);
		}
		
		return (shader_id);
	}

}
