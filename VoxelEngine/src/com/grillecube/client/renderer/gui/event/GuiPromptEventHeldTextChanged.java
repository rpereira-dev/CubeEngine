package com.grillecube.client.renderer.gui.event;

import com.grillecube.client.renderer.gui.components.GuiPrompt;

public class GuiPromptEventHeldTextChanged<T extends GuiPrompt> extends GuiEvent<T> {
	public GuiPromptEventHeldTextChanged(T gui) {
		super(gui);
	}
}
