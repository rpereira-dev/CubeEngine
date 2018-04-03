package com.grillecube.common.world;

import java.util.Collection;
import java.util.Iterator;

import com.grillecube.common.Logger;
import com.grillecube.common.Taskable;
import com.grillecube.common.event.Event;
import com.grillecube.common.resources.ResourceManager;

public abstract class WorldStorage<T> implements Taskable, Iterable<T> {

	/** the world */
	private final World world;

	public WorldStorage(World world) {
		this.world = world;
	}

	public World getWorld() {
		return (this.world);
	}

	public abstract void delete();

	protected void invokeEvent(Event event) {
		if (ResourceManager.instance() != null && ResourceManager.instance().getEventManager() != null) {
			ResourceManager.instance().getEventManager().invokeEvent(event);
		} else {
			Logger.get().log(Logger.Level.WARNING, "Tried to invoke an event before EventManager initialization");
		}
	}

	public final Iterator<T> iterator() {
		return (this.get().iterator());
	}

	public abstract Collection<T> get();

	/**
	 * add an object to the storage
	 * 
	 * @param object
	 *            : the object
	 * @return : the object if added, null else way
	 */
	public abstract T add(T object);

	/**
	 * remove an object from the storage
	 * 
	 * @param object
	 *            : the object to remove
	 * @return : the object removed, or null
	 */
	public abstract T remove(T object);

	/**
	 * @return : the size (number of object) of the storage
	 */
	public abstract int size();

}
