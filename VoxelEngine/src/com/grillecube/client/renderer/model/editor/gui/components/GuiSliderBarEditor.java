package com.grillecube.client.renderer.model.editor.gui.components;

import com.grillecube.client.renderer.gui.components.GuiColoredQuad;
import com.grillecube.client.renderer.gui.components.GuiLabel;
import com.grillecube.client.renderer.gui.components.GuiSliderBar;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextCenterBox;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextFillBox;
import com.grillecube.client.renderer.gui.listeners.GuiSliderBarListenerValueChanged;

public class GuiSliderBarEditor extends GuiSliderBar implements GuiSliderBarListenerValueChanged {

	private GuiColoredQuad total;
	private GuiColoredQuad selected;
	private GuiLabel guiLabel;

	public GuiSliderBarEditor() {
		super();
		this.total = new GuiColoredQuad();
		this.total.setColor(0, 0, 0, 1);
		this.addChild(this.total);

		this.selected = new GuiColoredQuad();
		this.selected.setColor(1, 0, 0, 1);
		this.addChild(this.selected);

		this.guiLabel = new GuiLabel();
		this.guiLabel.setPosition(0.0f, 0.0f);
		this.guiLabel.setText("Hello world");
		this.guiLabel.setFontColor(1, 1, 1, 1.0f);
		this.guiLabel.getGuiText().addParameter(new GuiTextParameterTextFillBox(0.75f));
		this.guiLabel.getGuiText().addParameter(new GuiTextParameterTextCenterBox());
		this.addChild(this.guiLabel);

		this.addListener(this);
	}

	@Override
	public void invokeSliderBarValueChanged(GuiSliderBar guiSliderBar, int selectedIndex, Object value) {
		float width = this.getPercent();
		this.selected.setBox(0, 0, width, 1, 0);
		this.guiLabel.setText(this.getSelectedValue().toString());
	}
}
