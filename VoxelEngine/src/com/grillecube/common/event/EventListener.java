package com.grillecube.common.event;

import java.lang.reflect.ParameterizedType;

public abstract class EventListener<T extends Event> {
	private Class<? extends Event> _class;

	@SuppressWarnings("unchecked")
	public EventListener() {
		this._class = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public EventListener(Class<? extends Event> eventclass) {
		this._class = eventclass;
	}

	public Class<? extends Event> getEventClass() {
		return (this._class);
	}

	public abstract void invoke(T event);
}
