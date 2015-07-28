package com.grillecube.common.mod;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.grillecube.client.Game;

import fr.toss.lib.Logger;

public class ModLoader
{
	private ArrayList<IMod>	_mods;
	
	public ModLoader()
	{
		this._mods = new ArrayList<IMod>();
		this.loadMods("./mods/");
	}
	
	/** find mods into "./mods" folder and add it IMod class to the _mods list */
	private void loadMods(String filepath)
	{
		File folder = new File(filepath);
		
		if (!folder.isDirectory())
		{
			System.err.println("Mods folder isnt a folder?");
			return ;
		}
		
		for (File file : folder.listFiles())
		{
			if (file.isDirectory())
			{
				this.loadMods(file.getAbsolutePath());
				continue ;
			}
			
			try
			{
				this.loadMod(file);
			}
			catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException exception)
			{
				exception.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings({ "resource" })
	private void loadMod(File file) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException
	{
		JarFile jar = new JarFile(file.getAbsolutePath());
		Enumeration<JarEntry> entries = jar.entries();
		URL[] urls = { new URL("jar:file:" + file.getAbsolutePath() + "!/") };
		URLClassLoader cl = URLClassLoader.newInstance(urls);
		
		while (entries.hasMoreElements())
		{
			JarEntry entry = entries.nextElement();
			
			if (entry.getName().endsWith(".class"))
			{
				String clazzname = entry.getName().replace('/', '.').substring(0, entry.getName().length() - 6);
				Class<?> clazz = cl.loadClass(clazzname);
				
				if (this.isClassMod(clazz))
				{
					if (clazz.isAnnotationPresent(ModInfo.class))
					{
						this._mods.add((IMod) clazz.newInstance());
						Game.instance().getLogger().log(Logger.Level.FINE, "Adding mod: " + clazz);
					}
					else
					{
						Game.instance().getLogger().log(Logger.Level.FINE, "Not a mod class! " + clazz);
					}
				}
			}
		}
	}

	private boolean isClassMod(Class<?> clazz)
	{
		Class<?>[] interfaces = clazz.getInterfaces();
		
		for (Class<?> inter : interfaces)
		{
			if (inter == IMod.class)
			{
				return (true);
			}
		}
		return (false);
	}
	
	public void	initializeAll(Game game)
	{
		for (IMod mod : this._mods)
		{
			mod.initialize(game);
		}
	}
	
	public void	deinitializeAll(Game game)
	{
		for (IMod mod : this._mods)
		{
			mod.deinitialize(game);
		}
	}
}
