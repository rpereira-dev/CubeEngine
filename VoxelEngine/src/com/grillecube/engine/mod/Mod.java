package com.grillecube.engine.mod;

import java.util.ArrayList;

import com.grillecube.engine.Logger;
import com.grillecube.engine.Logger.Level;
import com.grillecube.engine.resources.IModResource;
import com.grillecube.engine.resources.ResourceManager;

public class Mod {
	private IMod _mod;
	private ModInfo _modinfo;
	private String _filepath;
	private ArrayList<IModResource> _resources;

	public Mod(IMod mod, String filepath) {
		this._mod = mod;
		this._modinfo = mod.getClass().getAnnotation(ModInfo.class);
		this._filepath = filepath;
		this._resources = new ArrayList<IModResource>();
	}

	public IMod getMod() {
		return (this._mod);
	}

	/** return location of the mod jar file (usefull for assets loadings) */
	public String getJarPath() {
		return (this._filepath);
	}

	public ModInfo getModInfo() {
		return (this._modinfo);
	}

	public IModResource addRessource(IModResource resource) {
		this._resources.add(resource);
		return (resource);
	}

	/** called when resources of this mod should be loaded */
	public void loadResources(ResourceManager manager) {
		Logger.get().log(Level.FINE, "Loading resources: " + this.toString());

		Logger.get().indent(1);
		for (IModResource resource : this._resources) {
			Logger.get().log(Level.FINE, "Resource: " + resource.getClass().getSimpleName());
			Logger.get().indent(1);
			resource.load(this, manager);
			Logger.get().indent(-1);
		}
		Logger.get().indent(-1);
	}

	/** called when resources of this mod should be loaded */
	public void unloadResources(ResourceManager manager) {
		Logger.get().log(Level.FINE, "Unloading resources: " + this.toString());

		for (IModResource resource : this._resources) {
			Logger.get().log(Level.FINE, "Resource: " + resource.getClass().getSimpleName());
			resource.load(this, manager);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Mod(");
		builder.append(this._modinfo.name());
		builder.append(";");
		builder.append(this._modinfo.author());
		builder.append(";");
		builder.append(this._modinfo.version());
		builder.append(")");
		return (builder.toString());
	}

	public void initialize() {
		Logger.get().log(Level.FINE, "Initializing: " + this.toString());
		this._mod.initialize(this);
	}

	public void deinitialize() {
		Logger.get().log(Level.FINE, "Deinitializing: " + this.toString());
		this._mod.deinitialize(this);
	}
}
