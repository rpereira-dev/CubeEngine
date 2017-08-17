package com.grillecube.client.renderer.gui.components;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * The GuiSpinner values holder
 */
public class GuiSpinnerValues<T> {

	/** the objects hold */
	private final ArrayList<T> values;

	/** the guiSliderBar */
	private ArrayList<GuiSpinner<T>> guiSpinners;

	public GuiSpinnerValues(T[] values) {
		this.values = new ArrayList<T>();
		this.guiSpinners = new ArrayList<GuiSpinner<T>>();
	}

	public GuiSpinnerValues() {
		this(null);
	}

	public final ArrayList<T> getValues() {
		return (this.values);
	}

	public final void onAttachedTo(GuiSpinner<T> guiSliderBar) {
		this.guiSpinners.add(guiSliderBar);
	}

	public final void onDetachedFrom(GuiSpinner<T> guiSliderBar) {
		this.guiSpinners.remove(guiSliderBar);
	}

	/** get the value at given index */
	public final T get(int index) {
		return (this.values.get(index));
	}

	public final void add(T value) {
		this.values.add(value);
		for (GuiSpinner<T> guiSpinner : this.guiSpinners) {
			guiSpinner.onValueAdded();
		}
	}

	public final void sort(Comparator<T> comparator) {
		this.values.sort(comparator);
		for (GuiSpinner<T> guiSpinner : this.guiSpinners) {
			guiSpinner.onValuesSorted();
		}
	}
}
