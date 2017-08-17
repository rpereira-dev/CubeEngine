package com.grillecube.client.renderer.model.editor.gui.components;

import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.components.GuiColoredQuad;
import com.grillecube.client.renderer.gui.components.GuiLabel;
import com.grillecube.client.renderer.gui.components.GuiSliderBar;
import com.grillecube.client.renderer.gui.components.GuiSpinner;
import com.grillecube.client.renderer.gui.components.GuiSpinnerRenderer;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextCenterBox;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextFillBox;

public class GuiSpinnerEditor<T> extends GuiSpinner<T> {

	public GuiSpinnerEditor() {
		super();
		this.setRenderer(new GuiSpinnerRenderer<T>() {

			@Override
			public void onValueChanged(GuiSpinner<T> guiSliderBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onInitialized(GuiRenderer renderer, GuiSpinner<T> guiSliderBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onDeinitialized(GuiRenderer renderer, GuiSpinner<T> guiSliderBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onUpdate(float x, float y, boolean pressed, GuiSpinner<T> guiSliderBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAttachedTo(GuiSpinner<T> guiSliderBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onDetachedFrom(GuiSpinner<T> guiSliderBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onRender(GuiRenderer guiRenderer, GuiSpinner<T> guiSliderBar) {
				// TODO Auto-generated method stub
				
			}});
	}
}
