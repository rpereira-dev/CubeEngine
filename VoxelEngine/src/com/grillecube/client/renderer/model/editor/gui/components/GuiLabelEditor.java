package com.grillecube.client.renderer.model.editor.gui.components;

import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.components.Gui;
import com.grillecube.client.renderer.gui.components.GuiColoredQuad;
import com.grillecube.client.renderer.gui.components.GuiLabel;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextAlignLeft;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextCenterYBox;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextFillBox;

public class GuiLabelEditor extends Gui {

	private final GuiLabel guiLabel;
	private final GuiColoredQuad guiColoredQuad;

	public GuiLabelEditor() {
		super();
		this.guiColoredQuad = new GuiColoredQuad();
		this.guiColoredQuad.setColor(0.0f, 0.0f, 0.0f, 0.0f);
		this.addChild(this.guiColoredQuad);

		this.guiLabel = new GuiLabel();
		this.guiLabel.setPosition(0.0f, 0.0f);
		this.guiLabel.setText("Hello world");
		this.guiLabel.setFontColor(0, 0, 0, 1.0f);
		this.guiLabel.addParameter(new GuiTextParameterTextFillBox(0.75f));
		this.guiLabel.addParameter(new GuiTextParameterTextCenterYBox());
		this.guiLabel.addParameter(new GuiTextParameterTextAlignLeft(0.1f));
		this.addChild(this.guiLabel);
	}

	public final void setText(String value) {
		this.guiLabel.setText(value);
	}

	public final GuiLabel getLabel() {
		return (this.guiLabel);
	}

	public final GuiColoredQuad getBackground() {
		return (this.guiColoredQuad);
	}

	@Override
	protected void onInitialized(GuiRenderer renderer) {
	}

	@Override
	protected void onDeinitialized(GuiRenderer renderer) {
	}

	@Override
	protected void onUpdate(float x, float y, boolean pressed) {
	}

	@Override
	public void onAddedTo(Gui gui) {
	}

	@Override
	public void onRemovedFrom(Gui gui) {
	}

}
