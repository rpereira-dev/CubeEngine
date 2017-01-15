package com.grillecube.engine.resources;

import java.util.ArrayList;
import java.util.Collection;

import com.grillecube.engine.VoxelEngine;

/**
 * a generic manager class, T is the type to register, I is the id variable type
 */
public abstract class GenericManager<T> {

	/** the default id for an object that couldnt get registered */
	public static final int ERROR_OBJECT_ID = -1;

	/** pointer to the main manager */
	private final ResourceManager _resource_manager;

	/** objects */
	private final ArrayList<T> _objects;

	public GenericManager(final ResourceManager resource_manager) {
		this._resource_manager = resource_manager;
		this._objects = new ArrayList<T>();
	}

	public final ResourceManager getResourceManager() {
		return (this._resource_manager);
	}

	public final VoxelEngine.Side getSide() {
		return (VoxelEngine.instance().getSide());
	}

	/** register an object to the manager and return it id */
	protected int registerObject(T object) {
		int id = this._objects.size();
		this._objects.add(object);
		this.onObjectRegistered(object);
		return (id);
	}

	/** return the next ID available for this manager */
	public int getNextID() {
		return (this._objects.size());
	}

	/** a callback when an object is registered */
	protected abstract void onObjectRegistered(T object);

	/** called once when program is started */
	public void initialize() {
		this.onInitialized();
	}

	/** called once when program is stopped */
	public void stop() {
		this._objects.clear();
		this.onStopped();
	}

	/** called when resources should be cleaned */
	public void clean() {
		this._objects.clear();
		this.onCleaned();
	}

	/** called when resources should be loaded */
	public void load() {
		this.onLoaded();
	}

	protected abstract void onInitialized();

	protected abstract void onStopped();

	protected abstract void onCleaned();

	protected abstract void onLoaded();

	/** return the filepath for the given assets */
	protected String getResource(String modid, String path) {
		return (this.getResourceManager().getResourcePath(modid, path));
	}

	/** return the number of object registered */
	public int getObjectCount() {
		return (this._objects.size());
	}

	/** return the object with the given id */
	public T getObjectByID(int id) {
		if (id < 0 || id >= this._objects.size()) {
			return (null);
		}
		return (this._objects.get(id));
	}

	/** return true if the manager already have registered the given object */
	public boolean hasObject(T object) {
		return (this._objects.contains(object));
	}

	/** return every registered objects */
	public Collection<T> getObjects() {
		return (this._objects);
	}

	public void updateServerSide() {
	}

	public void updateClientSide() {
	}
}
