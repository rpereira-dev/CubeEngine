package com.grillecube.client.renderer.gui.components;

import java.util.ArrayList;
import java.util.Comparator;

import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.listeners.GuiSliderBarListenerValueChanged;

/** a slider bar */
public class GuiSliderBar extends Gui {

	/** listeners */
	private ArrayList<GuiSliderBarListenerValueChanged> guiSliderBarListenersValueChanged;

	/** the objects hold */
	private final ArrayList<Object> values;

	/** selected index */
	private int selectedIndex;

	public GuiSliderBar() {
		super();
		this.selectedIndex = 0;
		this.values = new ArrayList<Object>();
	}

	public final void addListener(GuiSliderBarListenerValueChanged guiSliderBarListenerValueChanged) {
		if (this.guiSliderBarListenersValueChanged == null) {
			this.guiSliderBarListenersValueChanged = new ArrayList<GuiSliderBarListenerValueChanged>();
		}
		this.guiSliderBarListenersValueChanged.add(guiSliderBarListenerValueChanged);
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
		this.selectedIndex = selectedIndex;
		Object value = this.values.get(this.selectedIndex);
		if (this.guiSliderBarListenersValueChanged != null) {
			for (GuiSliderBarListenerValueChanged listener : this.guiSliderBarListenersValueChanged) {
				listener.invokeSliderBarValueChanged(this, selectedIndex, value);
			}
		}

		return (value);
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

	/** get the percent progression of the selected value */
	public final float getPercent() {
		if (this.values.size() == 0) {
			return (0);
		}
		return ((this.selectedIndex + 1) / (float) this.values.size());
	}

	@Override
	protected void onInitialized(GuiRenderer renderer) {
	}

	@Override
	protected void onDeinitialized(GuiRenderer renderer) {
	}

	@Override
	protected void onUpdate(float x, float y, boolean pressed) {
		if (super.isLeftPressed()) {
			this.select(x);
		}
	}

	/** do the rendering of this gui */
	protected void onRender(GuiRenderer guiRenderer) {
	}

	@Override
	public void onAddedTo(Gui gui) {
	}

	@Override
	public void onRemovedFrom(Gui gui) {
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