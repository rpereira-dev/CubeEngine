package com.grillecube.client.renderer.model.editor.gui.components;

import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.components.GuiColoredQuad;
import com.grillecube.client.renderer.gui.components.GuiLabel;
import com.grillecube.client.renderer.gui.components.GuiSliderBar;
import com.grillecube.client.renderer.gui.components.GuiSliderBarRenderer;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextCenterBox;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextFillBox;

public class GuiSliderBarEditor<T> extends GuiSliderBar<T> {

	public GuiSliderBarEditor() {
		super();
		this.setRenderer(new GuiSliderBarRenderer<T>() {

			private GuiColoredQuad total;
			private GuiColoredQuad selected;
			private GuiLabel guiLabel;

			@Override
			public void onValueChanged(GuiSliderBar<T> guiSliderBar) {
				float width = guiSliderBar.getValues().getPercent();
				this.selected.setBox(0, 0, width, 1, 0);

				this.guiLabel.setText(guiSliderBar.getValues().getSelectedValue().toString());
			}

			@Override
			public void onInitialized(GuiRenderer renderer, GuiSliderBar<T> guiSliderBar) {
				this.total = new GuiColoredQuad();
				this.total.setColor(0, 0, 0, 1);
				guiSliderBar.addChild(this.total);

				this.selected = new GuiColoredQuad();
				this.selected.setColor(1, 0, 0, 1);
				guiSliderBar.addChild(this.selected);

				this.guiLabel = new GuiLabel();
				this.guiLabel.setPosition(0.0f, 0.0f);
				this.guiLabel.setText("Hello world");
				this.guiLabel.setFontColor(1, 1, 1, 1.0f);
				this.guiLabel.addParameter(new GuiTextParameterTextFillBox(0.75f));
				this.guiLabel.addParameter(new GuiTextParameterTextCenterBox());
				guiSliderBar.addChild(this.guiLabel);
			}

			@Override
			public void onDeinitialized(GuiRenderer renderer, GuiSliderBar<T> guiSliderBar) {
			}

			@Override
			public void onUpdate(float x, float y, boolean pressed, GuiSliderBar<T> guiSliderBar) {
			}

			@Override
			public void onAttachedTo(GuiSliderBar<T> guiSliderBar) {
			}

			@Override
			public void onDetachedFrom(GuiSliderBar<T> guiSliderBar) {
			}

			@Override
			public void onRender(GuiRenderer guiRenderer, GuiSliderBar<T> guiSliderBar) {
			}
		});
	}
}
