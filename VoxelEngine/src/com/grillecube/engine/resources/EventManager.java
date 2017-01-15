package com.grillecube.engine.resources;

import java.util.ArrayList;
import java.util.HashMap;

import com.grillecube.engine.Logger;
import com.grillecube.engine.Logger.Level;
import com.grillecube.engine.VoxelEngine;
import com.grillecube.engine.VoxelEngine.Side;
import com.grillecube.engine.event.Event;
import com.grillecube.engine.event.EventCallback;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class EventManager extends GenericManager<EventHandler> {
	
	private final HashMap<Class<? extends Event>, ArrayList<EventCallback>> _events;

	public EventManager(ResourceManager resource_manager) {
		super(resource_manager);
		this._events = new HashMap<Class<? extends Event>, ArrayList<EventCallback>>();
	}

	/** raise an event */
	public void invokeEvent(Event event) {
		ArrayList<EventCallback> callbacks = this._events.get(event.getClass());
		if (callbacks == null) {
			Logger.get().log(Level.WARNING,
					"Tried to invoke an un-existing event! " + event.getClass().getSimpleName());
			return;
		}

		for (EventCallback callback : callbacks) {
			callback.invoke(event);
		}
	}

	/** register an event */
	public void registerEvent(Class<? extends Event> eventclass, Side side) {
		if (VoxelEngine.instance().getSide().match(side)) {
			if (this._events.get(eventclass) != null) {
				Logger.get().log(Level.WARNING, "Tried to add an already registered event! " + eventclass);
				return;
			}
			ArrayList lst = new ArrayList<EventCallback>();
			this._events.put(eventclass, lst);
			super.registerObject(new EventHandler(eventclass, lst));
			Logger.get().log(Level.FINE, "Registered event: " + eventclass.getSimpleName());
		}
	}

	public <T extends Event> void registerEventCallback(EventCallback<T> callback) {
		ArrayList<EventCallback> event = this._events.get(callback.getEventClass());
		if (event == null) {
			Logger.get().log(Level.ERROR, "Tried to register an event callback on an un-existing event! "
					+ callback.getEventClass().getSimpleName());
			return;
		}
		event.add(callback);
		Logger.get().log(Level.FINE, "Registered event callback : " + callback.getClass().getSimpleName() + " , on : "
				+ callback.getEventClass().getSimpleName());
	}
	
	@Override
	protected void onObjectRegistered(EventHandler object) {}

	@Override
	protected void onInitialized() {}

	@Override
	protected void onStopped() {}
	
	@Override
	protected void onLoaded() {}

	@Override
	protected void onCleaned() {
		this._events.clear();
	}
}

@SuppressWarnings({ "rawtypes"})
class EventHandler {
	
	private final ArrayList<EventCallback> _callbacks;
	private final Class<? extends Event> _event;
	
	public EventHandler(Class<? extends Event> event, ArrayList<EventCallback> lst) {
		this._event = event;
		this._callbacks = lst;
	}
	
	@Override
	public boolean equals(Object handler) {
		return (((EventHandler)handler).getEvent() == this.getEvent());
	}
	
	public Class<? extends Event> getEvent() {
		return (this._event);
	}
	
	public ArrayList<EventCallback> getCallbacks() {
		return (this._callbacks);
	}
	
}
