package com.grillecube.common.mod;

import java.util.ArrayList;

import com.grillecube.common.Logger;
import com.grillecube.common.Logger.Level;
import com.grillecube.common.Taskable;
import com.grillecube.common.VoxelEngine.Callable;
import com.grillecube.common.resources.ResourceManager;

public class Mod {
	private IMod mod;
	private ModInfo modInfo;
	private ArrayList<IModResource> modResources;

	public Mod(IMod mod, ModInfo modInfo) {
		this.mod = mod;
		this.modInfo = modInfo;
		this.modResources = new ArrayList<IModResource>();
	}

	public IMod getMod() {
		return (this.mod);
	}

	public ModInfo getModInfo() {
		return (this.modInfo);
	}

	public IModResource addResource(IModResource resource) {
		this.modResources.add(resource);
		return (resource);
	}

	/** called when resources of this mod should be loaded */
	public void loadResources(ResourceManager manager) {
		Logger.get().log(Level.FINE, "Loading resources: " + this.toString());

		Logger.get().indent(1);
		for (IModResource resource : this.modResources) {
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

		for (IModResource resource : this.modResources) {
			Logger.get().log(Level.FINE, "Resource: " + resource.getClass().getSimpleName());
			resource.load(this, manager);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Mod(");
		builder.append(this.modInfo.name());
		builder.append(";");
		builder.append(this.modInfo.author());
		builder.append(";");
		builder.append(this.modInfo.version());
		builder.append(";");
		builder.append(this.modInfo.clientProxy());
		builder.append(";");
		builder.append(this.modInfo.serverProxy());
		builder.append(")");
		return (builder.toString());
	}

	public void initialize() {
		Logger.get().log(Level.FINE, "Initializing: " + this.toString());
		this.mod.initialize(this);
	}

	public void deinitialize() {
		Logger.get().log(Level.FINE, "Deinitializing: " + this.toString());
		this.mod.deinitialize(this);
	}

	public void getTasks(ArrayList<Callable<Taskable>> tasks) {
	}
}
