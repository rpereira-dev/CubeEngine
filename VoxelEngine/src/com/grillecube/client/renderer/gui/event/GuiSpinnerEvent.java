package com.grillecube.client.renderer.gui.event;

import com.grillecube.client.renderer.gui.components.GuiSpinner;

public abstract class GuiSpinnerEvent<T extends GuiSpinner> extends GuiEvent<T> {

	private final boolean expanded;

	private final int pickedIndex;
	private final Object pickedObject;
	private final String pickedName;

	public GuiSpinnerEvent(T gui) {
		super(gui);
		this.expanded = gui.isExpanded();
		this.pickedIndex = gui.getPickedIndex();
		this.pickedName = gui.getPickedName();
		this.pickedObject = gui.getPickedObject();
	}

	public final boolean isExpanded() {
		return (this.expanded);
	}

	public final int getPickedIndex() {
		return (this.pickedIndex);
	}

	public final Object getPickedObject() {
		return (this.pickedObject);
	}

	public final String getPickedName() {
		return (this.pickedName);
	}

}
