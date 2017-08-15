package com.grillecube.client.renderer.model.editor.gui;

import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.components.Gui;
import com.grillecube.client.renderer.gui.components.GuiColoredQuad;
import com.grillecube.client.renderer.gui.components.GuiLabel;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextCenterYBox;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextFillBox;
import com.grillecube.client.renderer.gui.listeners.GuiListenerMouseEnter;

public class GuiLabelEditor extends Gui {

	private final GuiLabel guiLabel;
	private final GuiColoredQuad guiColoredQuad;

	public GuiLabelEditor() {
		super();
		this.guiColoredQuad = new GuiColoredQuad();
		this.guiColoredQuad.setBox(0, 0, 1, 1, 0);
		this.guiColoredQuad.setColor(0.2f, 0.2f, 0.2f, 0.5f);
		this.addChild(this.guiColoredQuad);

		this.guiLabel = new GuiLabel();
		this.guiLabel.setBox(0.05f, 0.05f, 0.90f, 0.90f, 0);
		this.guiLabel.setPosition(0.0f, 0.0f);
		this.guiLabel.setText("Hello world");
		this.guiLabel.addParameter(new GuiTextParameterTextFillBox());
		this.guiLabel.addParameter(new GuiTextParameterTextCenterYBox());
		this.guiLabel.addListener(new GuiListenerMouseEnter<GuiLabel>() {

			@Override
			public void invokeMouseEnter(GuiLabel gui, double mousex, double mousey) {
				System.out.println("in");
			}

		});
		this.addChild(this.guiLabel);
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
	public void onAddedTo(GuiRenderer guiRenderer) {
	}

	@Override
	public void onRemovedFrom(GuiRenderer guiRenderer) {
	}

	@Override
	public void onAddedTo(Gui gui) {
	}

	@Override
	public void onRemovedFrom(Gui gui) {
	}

}
