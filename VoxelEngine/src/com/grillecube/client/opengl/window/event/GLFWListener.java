package com.grillecube.client.opengl.window.event;

import java.lang.reflect.ParameterizedType;

public abstract class GLFWListener<T extends GLFWEvent> {

	private Class<? extends GLFWEvent> clazz;

	@SuppressWarnings("unchecked")
	public GLFWListener() {
		this.clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public Class<? extends GLFWEvent> getEventClass() {
		return (this.clazz);
	}

	public abstract void invoke(T event);
}
