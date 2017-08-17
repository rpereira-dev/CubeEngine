package com.grillecube.client.renderer.gui.components;

import com.grillecube.client.renderer.gui.GuiRenderer;

/** a slider bar */
public abstract class GuiSliderBar<T> extends Gui {

	/** the values and renderer for this slider */
	private GuiSliderBarValues<T> values;
	private GuiSliderBarRenderer<T> renderer;

	public GuiSliderBar() {
		this(null, null);
	}

	public GuiSliderBar(GuiSliderBarValues<T> values, GuiSliderBarRenderer<T> renderer) {
		super();
		this.setHolder(values);
		this.setRenderer(renderer);
	}

	/**
	 * set the value values
	 * 
	 * @param values
	 */
	public final void setHolder(GuiSliderBarValues<T> values) {
		if (this.values != null) {
			this.values.onDetachedFrom(this);
		}
		this.values = values;
		if (this.values != null) {
			this.values.onAttachedTo(this);
		}
	}

	/**
	 * set the renderer
	 * 
	 * @param renderer
	 */
	public final void setRenderer(GuiSliderBarRenderer<T> renderer) {
		if (this.renderer != null) {
			this.renderer.onDetachedFrom(this);
		}
		this.renderer = renderer;
		if (this.renderer != null) {
			this.renderer.onAttachedTo(this);
		}
	}

	public final GuiSliderBarValues<T> getValues() {
		return (this.values);
	}

	public final GuiSliderBarRenderer<T> getRenderer() {
		return (this.renderer);
	}

	@Override
	protected void onInitialized(GuiRenderer renderer) {
		this.renderer.onInitialized(renderer, this);
	}

	@Override
	protected void onDeinitialized(GuiRenderer renderer) {
		this.renderer.onDeinitialized(renderer, this);
	}

	@Override
	protected void onUpdate(float x, float y, boolean pressed) {
		this.renderer.onUpdate(x, y, pressed, this);
	}

	/**
	 * a callback when a value is selected
	 */
	protected void onValueChanged() {
		this.renderer.onValueChanged(this);
	}

	/** do the rendering of this gui */
	protected void onRender(GuiRenderer guiRenderer) {
		this.renderer.onRender(guiRenderer, this);
	}

	@Override
	public void onAddedTo(Gui gui) {
	}

	@Override
	public void onRemovedFrom(Gui gui) {
	}
}