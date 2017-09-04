package com.grillecube.client.renderer.gui.event;

import com.grillecube.client.renderer.gui.components.GuiSpinner;

public class GuiSpinnerEventPick<T extends GuiSpinner> extends GuiSpinnerEvent<T> {

	private final int prevIndex;
	private final Object prevObject;
	private final String prevName;

	public GuiSpinnerEventPick(T gui, int prevIndex) {
		super(gui);
		this.prevIndex = prevIndex;
		this.prevObject = gui.getObject(prevIndex);
		this.prevName = gui.getName(prevIndex);
	}
	
	public final int getPrevPickedIndex() {
		return (this.prevIndex);
	}

	public final Object getPrevPickedObject() {
		return (this.prevObject);
	}

	public final String getPrevPickedName() {
		return (this.prevName);
	}
}
