
package com.grillecube.client.renderer.gui.event;

import com.grillecube.client.renderer.gui.components.GuiSpinner;

/**
 * a listener called when the element at given index is newly added to the
 * spinner
 */
public class GuiSpinnerEventAdd<T extends GuiSpinner> extends GuiSpinnerEvent<T> {

	private final int addedIndex;
	private final Object addedObject;
	private final String addedName;

	public GuiSpinnerEventAdd(T gui, int addedIndex, Object addedObject, String addedName) {
		super(gui);
		this.addedIndex = addedIndex;
		this.addedObject = addedObject;
		this.addedName = addedName;
	}

	public final int getAddedIndex() {
		return (this.addedIndex);
	}

	public final Object getAddedObject() {
		return (this.addedObject);
	}

	public final String getAddedName() {
		return (this.addedName);
	}
}
