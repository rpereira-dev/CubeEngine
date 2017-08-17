package com.grillecube.client.renderer.gui.components;

import java.util.ArrayList;

import com.grillecube.client.renderer.gui.listeners.GuiListenerMouseHover;

/**
 * The GuiSliderBar values holder
 */
public class GuiSliderBarValues<T> {

	/** the listener */
	private static final GuiListenerMouseHover<GuiSliderBar<?>> LISTENER = new GuiListenerMouseHover<GuiSliderBar<?>>() {
		@Override
		public void invokeMouseHover(GuiSliderBar<?> guiSliderBar, boolean leftPressed, double mousex, double mousey) {
			if (!leftPressed) {
				return;
			}
			GuiSliderBarValues<?> holder = guiSliderBar.getValues();
			if (holder.getValues() == null || holder.getValues().length == 0) {
				return;
			}
			holder.select(mousex);
		}
	};

	/** the objects hold */
	private T[] values;

	/** selected index */
	private int selectedIndex;

	/** the guiSliderBar */
	private ArrayList<GuiSliderBar<T>> guiSliderBars;

	public GuiSliderBarValues(T[] values) {
		this.selectedIndex = 0;
		this.guiSliderBars = new ArrayList<GuiSliderBar<T>>();
		this.values = values;
	}

	public GuiSliderBarValues() {
		this(null);
	}

	public final T[] getValues() {
		return (this.values);
	}

	public final void onAttachedTo(GuiSliderBar<T> guiSliderBar) {
		guiSliderBar.addListener(LISTENER);
		this.guiSliderBars.add(guiSliderBar);
	}

	public final void onDetachedFrom(GuiSliderBar<T> guiSliderBar) {
		guiSliderBar.removeListener(LISTENER);
		this.guiSliderBars.remove(guiSliderBar);
	}

	/** select the value at given index */
	public final T select(int selectedIndex) {
		if (selectedIndex < 0 || selectedIndex >= this.values.length) {
			return (null);
		}
		this.selectedIndex = selectedIndex;
		for (GuiSliderBar<T> guiSliderBar : this.guiSliderBars) {
			guiSliderBar.onValueChanged();
		}
		return (this.values[this.selectedIndex]);
	}

	/** select the value at given index */
	public final T select(float percent) {
		return (this.select((int) (this.values.length * percent)));
	}

	/** @see GuiSliderBarValues#select(float) */
	public final T select(double percent) {
		return (this.select((float) percent));
	}

	/** get the selected value */
	public final T getSelectedValue() {
		if (this.selectedIndex < 0 || this.selectedIndex >= this.values.length) {
			return (null);
		}
		return (this.values[this.selectedIndex]);
	}

	/** get the percent progression of the selected value */
	public final float getPercent() {
		if (this.values == null || this.values.length == 0) {
			return (0);
		}
		return ((this.selectedIndex + 1) / (float) this.values.length);
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
