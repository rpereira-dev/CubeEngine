package com.grillecube.client.renderer.gui.components;

import java.util.ArrayList;
import java.util.Comparator;

import com.grillecube.client.renderer.gui.event.GuiSliderBarEventValueChanged;

/** a slider bar */
public class GuiSliderBar extends Gui {
	/** the objects hold */
	private final ArrayList<Object> values;

	/** selected index */
	private int selectedIndex;

	public GuiSliderBar() {
		super();
		this.selectedIndex = 0;
		this.values = new ArrayList<Object>();
	}

	/** add all values to the list */
	public final void addValues(Object... objects) {
		for (Object object : objects) {
			this.addValue(object);
		}
	}

	/** add a value to the list */
	public final void addValue(Object object) {
		if (this.values.size() == 0) {
			this.select(0);
		}
		this.values.add(object);
	}

	/** remove all values from the list */
	public final void removeValues(Object... objects) {
		for (Object object : objects) {
			this.removeValue(object);
		}
	}

	/** remove a value from the list */
	public final void removeValue(Object object) {
		this.values.remove(object);
	}

	/** remove all values from the list */
	public final void removeValues() {
		this.values.clear();
	}

	/** sort the values */
	public final void sort(Comparator<Object> cmp) {
		this.values.sort(cmp);
	}

	/** get all values from the list */
	public final ArrayList<Object> getValues() {
		return (this.values);
	}

	public final Object getValue(int index) {
		return (this.values.get(index));
	}

	/** select the value at given index */
	public final Object select(int selectedIndex) {
		if (this.values.size() == 0) {
			return (null);
		}
		if (selectedIndex < 0) {
			selectedIndex = 0;
		} else if (selectedIndex >= this.values.size()) {
			selectedIndex = this.values.size() - 1;
		}
		int prevIndex = this.selectedIndex;
		this.selectedIndex = selectedIndex;
		super.stackEvent(new GuiSliderBarEventValueChanged<GuiSliderBar>(this, prevIndex, this.getValue(prevIndex)));
		return (this.getSelectedValue());
	}

	public final Object select(Object object) {
		return (this.select(this.values.indexOf(object)));
	}

	/** select the value at given index */
	public final Object select(float percent) {
		return (this.select((int) (this.values.size() * percent)));
	}

	/** @see GuiSliderBarValues#select(float) */
	public final Object select(double percent) {
		return (this.select((float) percent));
	}

	/** get the selected value */
	public final Object getSelectedValue() {
		if (this.selectedIndex < 0 || this.selectedIndex >= this.values.size()) {
			return (null);
		}
		return (this.values.get(this.selectedIndex));
	}

	/** get the selected value */
	public final int getSelectedIndex() {
		return (this.selectedIndex);
	}

	/** get the percent progression of the selected value */
	public final float getPercent() {
		if (this.values.size() == 0) {
			return (0);
		}
		return ((this.selectedIndex + 1) / (float) this.values.size());
	}

	@Override
	protected void onInputUpdate() {
		// TODO : mouse press
		if (super.isPressed()) {
			this.select(this.getMouseX());
		}
	}

	/** VALUES HELPER */

	public static final Integer[] intRange(int min, int max, int n) {
		int step = (max - min) / (n - 1);
		Integer[] values = new Integer[n];
		for (int i = 0; i < n; i++) {
			values[i] = new Integer(min + i * step);
		}
		return (values);
	}

	public static final Float[] floatRange(float min, float max, float step) {
		return (floatRange(min, max, (max - min) / step));
	}

	public static final Float[] floatRange(float min, float max, int n) {
		float step = (max - min) / (float) (n - 1);
		Float[] values = new Float[n];
		for (int i = 0; i < n; i++) {
			values[i] = new Float(min + i * step);
		}
		return (values);
	}
}