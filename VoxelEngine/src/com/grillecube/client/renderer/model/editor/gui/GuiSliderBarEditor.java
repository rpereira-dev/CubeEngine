package com.grillecube.client.renderer.model.editor.gui;

import com.grillecube.client.renderer.gui.components.GuiColoredQuad;
import com.grillecube.client.renderer.gui.components.GuiLabel;
import com.grillecube.client.renderer.gui.components.GuiSliderBar;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextCenterBox;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextFillBox;
import com.grillecube.client.renderer.gui.event.GuiListener;
import com.grillecube.client.renderer.gui.event.GuiSliderBarEventValueChanged;
import com.grillecube.common.maths.Vector4f;

public class GuiSliderBarEditor extends GuiSliderBar {

	private static final GuiListener<GuiSliderBarEventValueChanged<GuiSliderBarEditor>> LISTENER = new GuiListener<GuiSliderBarEventValueChanged<GuiSliderBarEditor>>() {
		@Override
		public void invoke(GuiSliderBarEventValueChanged<GuiSliderBarEditor> event) {
			event.getGui().onValueChanged();
		}
	};

	private static final Vector4f FILL_COLOR = new Vector4f(0.6f, 0.6f, 1.0f, 1.0f);
	private static final Vector4f BG_COLOR = new Vector4f(0.87f, 0.87f, 0.87f, 1.0f);

	private GuiColoredQuad total;
	private GuiColoredQuad selected;
	private GuiLabel guiLabel;

	public GuiSliderBarEditor() {
		super();
		this.total = new GuiColoredQuad();
		this.total.setColor(BG_COLOR);
		this.addChild(this.total);

		this.selected = new GuiColoredQuad();
		this.selected.setColor(FILL_COLOR);
		this.addChild(this.selected);

		this.guiLabel = new GuiLabel();
		this.guiLabel.setFontColor(0, 0, 0, 1.0f);
		this.guiLabel.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.guiLabel.addTextParameter(new GuiTextParameterTextCenterBox());
		this.addChild(this.guiLabel);

		this.addListener(LISTENER);
	}

	protected void onValueChanged() {
		float width = this.getPercent();
		this.selected.setBox(0, 0, width, 1, 0);
		this.guiLabel.setText(this.getPrefix() + this.getSelectedValue().toString() + this.getSuffix());
	}

	public void setText(String title) {
		this.guiLabel.setText(title);
	}
}
