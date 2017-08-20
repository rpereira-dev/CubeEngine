package com.grillecube.common.resources;

import java.util.ArrayList;
import java.util.Collection;

import com.grillecube.common.VoxelEngine;

/**
 * a generic manager class, T is the type to register, I is the id variable type
 */
public abstract class GenericManager<T> {

	/** the default id for an object that couldnt get registered */
	public static final int ERROR_OBJECT_ID = -1;

	/** pointer to the main manager */
	private final ResourceManager _resource_manager;

	/** objects */
	private ArrayList<T> objects;

	public GenericManager(final ResourceManager resource_manager) {
		this._resource_manager = resource_manager;
	}

	public final ResourceManager getResourceManager() {
		return (this._resource_manager);
	}

	public final VoxelEngine.Side getSide() {
		return (VoxelEngine.instance().getSide());
	}

	/** register an object to the manager and return it id */
	protected int registerObject(T object) {
		int id = this.objects.size();
		this.objects.add(object);
		this.onObjectRegistered(object);
		return (id);
	}

	/** return the next ID available for this manager */
	public int getNextID() {
		return (this.objects.size());
	}

	/** a callback when an object is registered */
	protected abstract void onObjectRegistered(T object);

	/** called once when program is started */
	public final void initialize() {
		this.objects = new ArrayList<T>();
		this.onInitialized();
	}

	/** called once when program is stopped */
	public final void deinitialize() {
		this.objects.clear();
		this.objects = null;
		this.onDeinitialized();
	}

	/** called every time the resource has to be loaded */
	public final void load() {
		this.onLoaded();
	}

	/** called every time the resource has to be unloaded */
	public final void unload() {
		this.objects.clear();
		this.onUnloaded();
	}

	protected abstract void onInitialized();

	protected abstract void onDeinitialized();

	protected abstract void onLoaded();

	protected abstract void onUnloaded();

	/** return the filepath for the given assets */
	protected String getResource(String modid, String path) {
		return (this.getResourceManager().getResourcePath(modid, path));
	}

	/** return the number of object registered */
	public int getObjectCount() {
		return (this.objects.size());
	}

	/** return the object with the given id */
	public T getObjectByID(int id) {
		if (id < 0 || id >= this.objects.size()) {
			return (null);
		}
		return (this.objects.get(id));
	}

	/** return true if the manager already have registered the given object */
	public boolean hasObject(T object) {
		return (this.objects.contains(object));
	}

	/** return every registered objects */
	public Collection<T> getObjects() {
		return (this.objects);
	}
}
