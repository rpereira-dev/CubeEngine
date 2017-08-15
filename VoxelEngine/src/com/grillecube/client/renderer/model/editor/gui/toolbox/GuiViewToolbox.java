package com.grillecube.client.renderer.model.editor.gui.toolbox;

import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.components.Gui;
import com.grillecube.client.renderer.gui.components.GuiColoredQuad;
import com.grillecube.client.renderer.gui.components.GuiView;
import com.grillecube.client.renderer.model.editor.gui.GuiLabelEditor;

public class GuiViewToolbox extends GuiView {

	@Override
	protected void onInitialized(GuiRenderer renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDeinitialized(GuiRenderer renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onUpdate(float x, float y, boolean pressed) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAddedTo(GuiRenderer guiRenderer) {
		// background
		GuiColoredQuad bg = new GuiColoredQuad();
		bg.setBox(0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
		bg.setColor(0.8f, 0.8f, 0.8f, 0.5f);
		this.addChild(bg);

		// text
		GuiLabelEditor guiLabelEditor = new GuiLabelEditor();
		guiLabelEditor.setBox(0, 0, 1, 0.05f, 0);
		this.addChild(guiLabelEditor);
	}

	@Override
	public void onRemovedFrom(GuiRenderer guiRenderer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAddedTo(Gui gui) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRemovedFrom(Gui gui) {
		// TODO Auto-generated method stub

	}

}
