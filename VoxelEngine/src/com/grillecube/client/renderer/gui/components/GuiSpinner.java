package com.grillecube.client.renderer.gui.components;

import com.grillecube.client.renderer.gui.GuiRenderer;

/** a slider bar */
public abstract class GuiSpinner<T> extends Gui {

	/** the values and renderer for this slider */
	private GuiSpinnerValues<T> values;
	private GuiSpinnerRenderer<T> renderer;

	public GuiSpinner() {
		this(null, null);
	}

	public GuiSpinner(GuiSpinnerValues<T> values, GuiSpinnerRenderer<T> renderer) {
		super();
		this.setHolder(values);
		this.setRenderer(renderer);
	}

	/**
	 * set the value values
	 * 
	 * @param values
	 */
	public final void setHolder(GuiSpinnerValues<T> values) {
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
	public final void setRenderer(GuiSpinnerRenderer<T> renderer) {
		if (this.renderer != null) {
			this.renderer.onDetachedFrom(this);
		}
		this.renderer = renderer;
		if (this.renderer != null) {
			this.renderer.onAttachedTo(this);
		}
	}

	public final GuiSpinnerValues<T> getValues() {
		return (this.values);
	}

	public final GuiSpinnerRenderer<T> getRenderer() {
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

	/**
	 * a callback when a value is added (and is now the last item of the
	 * GuiSpinnerValues)
	 */
	protected void onValueAdded() {
	}

	/**
	 * a callback when values are sorted
	 */
	protected void onValuesSorted() {
	}
}