package com.grillecube.client.renderer.model;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.lwjgl.util.vector.Vector3f;

import com.grillecube.server.Game;

import fr.toss.lib.Logger;

public class ModelLoader
{
	private static final float UNIT = 1 / 16.0f;
	public static Model	loadModel(String filename)
	{
		FileInputStream	fstream;
		DataInputStream	dis;
		Model			model;
		File			file;
		
		filename = "./assets/models/" + filename + ".mdl";
		Game.getLogger().log(Logger.Level.DEBUG, "Loading new model: " + filename);
		file = new File(filename);
		try
		{
			fstream = new FileInputStream(file);
			dis = new DataInputStream(fstream);
			model = new Model(readChars(dis));
			loadModelParts(dis, model);
			return (model);
		}
		catch (IOException exception)
		{
			exception.printStackTrace();
		}
		return (null);
	}

	private static void	loadModelParts(DataInputStream dis, Model model) throws IOException
	{
		ModelPart[]	parts;
		int	nb_parts;
		int	i;
		
		nb_parts = dis.readInt();
		parts = new ModelPart[nb_parts];
		for (i = 0 ; i < nb_parts ; i++)
		{
			parts[i] = loadModelPart(dis);
		}
		model.setParts(parts);
	}
	
	private static ModelPart	loadModelPart(DataInputStream dis) throws IOException
	{
		ModelPart	part;
		int			float_count;
		float[]		vertices;
		int			i;
		
		part = new ModelPart(readChars(dis));
		float_count = dis.readInt();
		vertices = new float[float_count];
		i = 0;
		while (i < float_count)
		{
			if (i % 7 < 4)
			{
				vertices[i++] = dis.readFloat() * UNIT;	//coordinates
			}
			else
			{
				vertices[i++] = dis.readFloat();	//color
			}
		}
		part.setVertices(vertices);
		
		//animations loading
		Animation[]	animations;
		int			animation_count;
		
		animation_count = dis.readInt();
		animations = new Animation[animation_count];
		for (i = 0 ; i < animation_count ; i++)
		{
			animations[i] = readAnimation(dis);
		}
		part.setAnimations(animations);
		
		return (part);
	}
	
	private static Animation	readAnimation(DataInputStream dis) throws IOException
	{
		Animation			animation;
		AnimationFrame[]	frames;
		
		float			time;
		Vector3f		translate;
		Vector3f		rot;
		Vector3f		scale;
		Vector3f		offset;
		int				frames_count;
		int				i;
		
		animation = new Animation(readChars(dis), dis.readInt());
		frames_count = dis.readInt();
		frames = new AnimationFrame[frames_count];
		for (i = 0 ; i < frames_count ; i++)
		{
			time = dis.readFloat();
			translate = new Vector3f(dis.readFloat() * UNIT, dis.readFloat() * UNIT, dis.readFloat() * UNIT);
			rot = new Vector3f(dis.readFloat(), dis.readFloat(), dis.readFloat());
			scale = new Vector3f(dis.readFloat(), dis.readFloat(), dis.readFloat());
			offset = new Vector3f(dis.readFloat() * UNIT, dis.readFloat() * UNIT, dis.readFloat() * UNIT);
			frames[i] = new AnimationFrame(time, translate, rot, scale, offset);
		}
		animation.setFrames(frames);
		return (animation);
	}
	
	private static String	readChars(DataInputStream dis) throws IOException
	{
		String	name;
		byte	buffer[];
		int		namelen;
		
		namelen = dis.readInt();
		buffer = new byte[namelen];
		dis.readFully(buffer, 0, namelen);
		name = new String(buffer);
		return (name);
	}
}
