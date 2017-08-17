package com.grillecube.client.renderer.gui.components;

import com.grillecube.client.renderer.gui.GuiRenderer;

/**
 * The GuiSliderBar renderer
 */
public interface GuiSliderBarRenderer<T> {

	/**
	 * callback when the GuiSliderHolder value of the linked GuiSliderBar
	 * changed
	 */
	public void onValueChanged(GuiSliderBar<T> guiSliderBar);

	/** a callbak when the GuiSlider is initialized */
	public void onInitialized(GuiRenderer renderer, GuiSliderBar<T> guiSliderBar);

	/** a callbak when the GuiSlider is deinitialized */
	public void onDeinitialized(GuiRenderer renderer, GuiSliderBar<T> guiSliderBar);

	/** a callback when the GuiSlider is updated */
	public void onUpdate(float x, float y, boolean pressed, GuiSliderBar<T> guiSliderBar);

	/** a callback when the GuiSlider is attached to this renderer */
	public void onAttachedTo(GuiSliderBar<T> guiSliderBar);

	/** a callback when the GuiSlider is detached from this renderer */
	public void onDetachedFrom(GuiSliderBar<T> guiSliderBar);

	public void onRender(GuiRenderer guiRenderer, GuiSliderBar<T> guiSliderBar);
}