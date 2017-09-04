package com.grillecube.common.resources;

import java.util.ArrayList;
import java.util.HashMap;

import com.grillecube.common.Logger;
import com.grillecube.common.Logger.Level;
import com.grillecube.common.event.Event;
import com.grillecube.common.event.EventListener;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class EventManager extends GenericManager<EventHandler> {

	private HashMap<Class<? extends Event>, ArrayList<EventListener>> events;

	public EventManager(ResourceManager resource_manager) {
		super(resource_manager);
	}

	/** raise an event */
	public void invokeEvent(Event event) {
		ArrayList<EventListener> callbacks = this.events.get(event.getClass());
		if (callbacks == null) {
			Logger.get().log(Level.WARNING,
					"Tried to invoke an un-existing event! " + event.getClass().getSimpleName());
			return;
		}

		for (EventListener callback : callbacks) {
			callback.invoke(event);
		}
	}

	/** register an event */
	public void registerEvent(Class<? extends Event> eventclass) {
		if (this.events.get(eventclass) != null) {
			Logger.get().log(Level.WARNING, "Tried to add an already registered event! " + eventclass);
			return;
		}
		ArrayList lst = new ArrayList<EventListener>();
		this.events.put(eventclass, lst);
		super.registerObject(new EventHandler(eventclass, lst));
		Logger.get().log(Level.FINE, "Registered event: " + eventclass.getSimpleName());
	}

	public <T extends Event> void addListener(EventListener<T> callback) {
		ArrayList<EventListener> event = this.events.get(callback.getEventClass());
		if (event == null) {
			Logger.get().log(Level.ERROR, "Tried to add an event listener on an un-existing event! "
					+ callback.getEventClass().getSimpleName());
		}
		event.add(callback);
		Logger.get().log(Level.FINE, "Added event callback : " + callback.getClass().getSimpleName() + " on : "
				+ callback.getEventClass().getSimpleName());
	}
	
	public <T extends Event> void removeListener(EventListener<T> callback) {
		ArrayList<EventListener> event = this.events.get(callback.getEventClass());
		if (event == null) {
			Logger.get().log(Level.ERROR, "Tried to remove an event callback on an un-existing event! "
					+ callback.getEventClass().getSimpleName());
			return;
		}
		event.remove(callback);
		Logger.get().log(Level.FINE, "Removed event callback : " + callback.getClass().getSimpleName() + " on : "
				+ callback.getEventClass().getSimpleName());
	}

	@Override
	protected void onObjectRegistered(EventHandler object) {
	}
	
	@Override
	public void onInitialized() {
		this.events = new HashMap<Class<? extends Event>, ArrayList<EventListener>>();
	}

	@Override
	public void onLoaded() {
	}

	@Override
	protected void onDeinitialized() {
		this.events.clear();
		this.events = null;
	}

	@Override
	protected void onUnloaded() {
		this.events.clear();
	}
}

@SuppressWarnings({ "rawtypes" })
class EventHandler {

	private final ArrayList<EventListener> callbacks;
	private final Class<? extends Event> event;

	public EventHandler(Class<? extends Event> event, ArrayList<EventListener> lst) {
		this.event = event;
		this.callbacks = lst;
	}

	@Override
	public boolean equals(Object handler) {
		return (((EventHandler) handler).getEvent() == this.getEvent());
	}

	public Class<? extends Event> getEvent() {
		return (this.event);
	}

	public ArrayList<EventListener> getCallbacks() {
		return (this.callbacks);
	}

}
