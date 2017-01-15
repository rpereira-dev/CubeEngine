package com.grillecube.engine.event;

import java.lang.reflect.ParameterizedType;

public abstract class EventCallback<T extends Event> {
	private Class<? extends Event> _class;

	@SuppressWarnings("unchecked")
	public EventCallback() {
		this._class = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public EventCallback(Class<? extends Event> eventclass) {
		this._class = eventclass;
	}

	public Class<? extends Event> getEventClass() {
		return (this._class);
	}

	public abstract void invoke(T event);
}
