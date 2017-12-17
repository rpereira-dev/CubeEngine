package com.grillecube.common.event;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;

public abstract class Listener<T extends Event> {
	/** the event class */
	private Class<? extends Event> eventClass;
	/** the raised events */
	private final ArrayList<T> events;

	@SuppressWarnings("unchecked")
	public Listener() {
		this.eventClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
		this.events = new ArrayList<T>();
	}

	public Listener(Class<? extends Event> eventclass) {
		this.eventClass = eventclass;
		this.events = new ArrayList<T>();
	}

	public final void stackEvent(T event) {
		this.events.add(event);
	}

	public Class<? extends Event> getEventClass() {
		return (this.eventClass);
	}

	public final void unstackEvents() {
		this.events.clear();
		this.events.trimToSize();
	}

	/** called before the event is raised, cancel should be done here if possible */
	public abstract void pre(T event);

	/** called after the event was raised, and if it wasn't cancelled */
	public abstract void post(T event);
}
