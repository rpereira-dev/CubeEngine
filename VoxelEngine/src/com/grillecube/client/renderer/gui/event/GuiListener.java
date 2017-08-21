package com.grillecube.client.renderer.gui.event;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class GuiListener<T extends GuiEvent<?>> {

	private Class<? extends GuiEvent<?>> guiEventClass;

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
	}

	public GuiListener(Class<? extends GuiEvent<?>> eventclass) {
		this.guiEventClass = eventclass;
	}

	public Class<? extends GuiEvent<?>> getEventClass() {
		return (this.guiEventClass);
	}

	public abstract void invoke(T event);
}
