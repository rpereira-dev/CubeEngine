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

package com.grillecube.engine.mod;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.grillecube.engine.Logger;
import com.grillecube.engine.resources.ResourceManager;

public class ModLoader {
	/** every mods */
	private ArrayList<Mod> _mods;

	public ModLoader() {
		this._mods = new ArrayList<Mod>();
	}

	/** find mods into the given folder and try to load it */
	public void injectMods(String filepath) {
		File folder = new File(filepath);

		if (!folder.exists()) {
			Logger.get().log(Logger.Level.WARNING, "Mod folder doesnt exists: " + filepath);
			return;
		}

		if (!folder.isDirectory()) {
			Logger.get().log(Logger.Level.WARNING, "Mod folder ... isnt a folder? " + filepath);
			return;
		}

		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				this.injectMods(file.getAbsolutePath());
				continue;
			}

			try {
				this.loadMod(file);
			} catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException exception) {
				exception.printStackTrace();
			}
		}
	}

	/** load a mod from the given file (which should be a JarFile) */
	@SuppressWarnings({ "resource" })
	private void loadMod(File file)
			throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
		JarFile jar = new JarFile(file.getAbsolutePath());
		Enumeration<JarEntry> entries = jar.entries();
		URL[] urls = { new URL("jar:file:" + file.getAbsolutePath() + "!/") };
		URLClassLoader cl = URLClassLoader.newInstance(urls);

		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();

			if (entry.getName().endsWith(".class")) {
				String clazzname = entry.getName().replace('/', '.').substring(0, entry.getName().length() - 6);
				Class<?> modclass = cl.loadClass(clazzname);

				if (this.isClassMod(modclass)) {
					IMod mod = (IMod) modclass.newInstance();
					this.injectMod(new Mod(mod, file.getAbsolutePath()));
				}
			}
		}
	}

	/** return true if the class implements IMod interface */
	private boolean isClassMod(Class<?> clazz) {
		Class<?>[] interfaces = clazz.getInterfaces();

		for (Class<?> inter : interfaces) {
			if (inter == IMod.class) {
				return (true);
			}
		}
		return (false);
	}

	/** add the given mod to the mods */
	public void injectMod(Mod mod) {
		Class<?> clazz = mod.getMod().getClass();
		if (clazz.isAnnotationPresent(ModInfo.class)) {
			this._mods.add(mod);
			Logger.get().log(Logger.Level.FINE, "Adding mod: " + clazz);
		} else {
			Logger.get().log(Logger.Level.WARNING,
					"Missing ModInfo annotations in mod: " + clazz + " . Mod wasnt injected.");
		}
	}

	/** initialize every mods */
	public void initializeAll(ResourceManager manager) {
		for (Mod mod : this._mods) {
			mod.initialize();
		}
	}

	/** deinitialize every mods and clean the mod list */
	public void deinitializeAll(ResourceManager manager) {
		for (Mod mod : this._mods) {
			mod.deinitialize();
		}
		this._mods.clear();
	}

	/** load every mod resources */
	public void loadAll(ResourceManager manager) {
		for (Mod mod : this._mods) {
			mod.loadResources(manager);
		}
	}

	public void unloadAll(ResourceManager manager) {
		for (Mod mod : this._mods) {
			mod.unloadResources(manager);
		}
	}

	public void stop(ResourceManager manager) {
		this.deinitializeAll(manager);
	}
}
