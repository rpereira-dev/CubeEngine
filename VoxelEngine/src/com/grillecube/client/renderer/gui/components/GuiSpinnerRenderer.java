package com.grillecube.client.renderer.gui.components;

import com.grillecube.client.renderer.gui.GuiRenderer;

/**
 * The GuiSpinner renderer
 */
public interface GuiSpinnerRenderer<T> {

	/**
	 * callback when the GuiSliderHolder value of the linked GuiSpinner
	 * changed
	 */
	public void onValueChanged(GuiSpinner<T> guiSliderBar);

	/** a callbak when the GuiSlider is initialized */
	public void onInitialized(GuiRenderer renderer, GuiSpinner<T> guiSliderBar);

	/** a callbak when the GuiSlider is deinitialized */
	public void onDeinitialized(GuiRenderer renderer, GuiSpinner<T> guiSliderBar);

	/** a callback when the GuiSlider is updated */
	public void onUpdate(float x, float y, boolean pressed, GuiSpinner<T> guiSliderBar);

	/** a callback when the GuiSlider is attached to this renderer */
	public void onAttachedTo(GuiSpinner<T> guiSliderBar);

	/** a callback when the GuiSlider is detached from this renderer */
	public void onDetachedFrom(GuiSpinner<T> guiSliderBar);

	public void onRender(GuiRenderer guiRenderer, GuiSpinner<T> guiSliderBar);
}