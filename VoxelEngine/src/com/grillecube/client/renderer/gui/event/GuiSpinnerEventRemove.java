package com.grillecube.client.renderer.gui.event;

import com.grillecube.client.renderer.gui.components.GuiSpinner;

public class GuiSpinnerEventRemove<T extends GuiSpinner> extends GuiSpinnerEvent<T> {

	private final int removedIndex;
	private final Object removedObject;
	private final String removedName;

	public GuiSpinnerEventRemove(T gui, int removedIndex, Object removedObject, String removedName) {
		super(gui);
		this.removedIndex = removedIndex;
		this.removedObject = removedObject;
		this.removedName = removedName;
	}

	public final int getRemovedIndex() {
		return (this.removedIndex);
	}

	public final Object getRemovedObject() {
		return (this.removedObject);
	}

	public final String getRemovedName() {
		return (this.removedName);
	}
}
