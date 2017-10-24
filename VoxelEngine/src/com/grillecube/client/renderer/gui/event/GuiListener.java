package com.grillecube.client.renderer.gui.event;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

public abstract class GuiListener<T extends GuiEvent<?>> {

	/** the GuiEvent class bound to this event */
	private Class<? extends GuiEvent<?>> guiEventClass;

	/** the raised events */
	private final ArrayList<T> events;

	@SuppressWarnings("unchecked")
	public GuiListener() {
		// thats what we call "black magic" ;)
		/**
		 * it basically extract the raw Event class from this class
		 * 
		 * e.g:
		 * 
		 * GuiListener<GuiEventMouseEnter<GuiText>> and
		 * GuiListener<GuiEventMouseEnter<GuiOtherText>>
		 * 
		 * will return the raw class type 'GuiEventMouseEnter', which is what we
		 * want here
		 */
		this.guiEventClass = (Class<T>) ((ParameterizedType) ((Type) ((ParameterizedType) this.getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0])).getRawType();
		this.events = new ArrayList<T>();
	}

	public GuiListener(Class<? extends GuiEvent<?>> eventclass) {
		this.guiEventClass = eventclass;
		this.events = new ArrayList<T>();
	}

	@SuppressWarnings("unchecked")
	public final void stackEvent(GuiEvent<?> guiEvent) {
		this.events.add((T) guiEvent);
	}

	public Class<? extends GuiEvent<?>> getEventClass() {
		return (this.guiEventClass);
	}

	public final void invokeEvents() {
		for (T event : this.events) {
			this.invoke(event);
		}
	}

	public final void unstackEvents() {
		this.events.clear();
		this.events.trimToSize();
	}

	public abstract void invoke(T event);
}
