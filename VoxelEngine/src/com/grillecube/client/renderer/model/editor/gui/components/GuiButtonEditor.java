package com.grillecube.client.renderer.model.editor.gui.components;

import com.grillecube.client.renderer.gui.components.GuiColoredQuad;
import com.grillecube.client.renderer.gui.listeners.GuiListenerMouseEnter;
import com.grillecube.client.renderer.gui.listeners.GuiListenerMouseExit;
import com.grillecube.common.maths.Vector4f;

public class GuiButtonEditor extends GuiLabelEditor {

	private static final Vector4f IN_COLOR = new Vector4f(0.6f, 0.6f, 1.0f, 1.0f);
	private static final Vector4f OUT_COLOR = new Vector4f(0.87f, 0.87f, 0.87f, 1.0f);

	public GuiButtonEditor() {
		super();
		super.getBackground().addListener(new GuiListenerMouseEnter<GuiColoredQuad>() {
			@Override
			public void invokeMouseEnter(GuiColoredQuad gui, double mousex, double mousey) {
				gui.setColor(IN_COLOR);
			}
		});
		super.getBackground().addListener(new GuiListenerMouseExit<GuiColoredQuad>() {
			@Override
			public void invokeMouseExit(GuiColoredQuad gui, double mousex, double mousey) {
				gui.setColor(OUT_COLOR);
			}
		});
	}
}
