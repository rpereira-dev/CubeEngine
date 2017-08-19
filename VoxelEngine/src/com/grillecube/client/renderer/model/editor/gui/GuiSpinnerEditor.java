package com.grillecube.client.renderer.model.editor.gui;

import java.util.ArrayList;

import com.grillecube.client.renderer.gui.components.GuiButton;
import com.grillecube.client.renderer.gui.components.GuiSpinner;
import com.grillecube.client.renderer.gui.listeners.GuiListenerMouseLeftRelease;
import com.grillecube.client.renderer.gui.listeners.GuiSpinnerListenerAdd;
import com.grillecube.client.renderer.gui.listeners.GuiSpinnerListenerExpanded;
import com.grillecube.client.renderer.gui.listeners.GuiSpinnerListenerRemove;
import com.grillecube.client.renderer.gui.listeners.GuiSpinnerListenerSorted;

public class GuiSpinnerEditor extends GuiSpinner implements GuiSpinnerListenerAdd, GuiSpinnerListenerExpanded,
		GuiSpinnerListenerSorted, GuiSpinnerListenerRemove {

	private static final String ATTR_BUTTON_INDEX = "spinnerIndex";

	private static final GuiListenerMouseLeftRelease<GuiButton> PICK_LISTENER = new GuiListenerMouseLeftRelease<GuiButton>() {
		@Override
		public void invokeMouseLeftRelease(GuiButton guiButton, double mousex, double mousey) {
			GuiSpinnerEditor spinner = ((GuiSpinnerEditor) guiButton.getParent());
			spinner.pick((Integer) guiButton.getAttribute(ATTR_BUTTON_INDEX));
		}
	};

	private final GuiButton title;
	private final ArrayList<GuiButton> guiButtons;

	public GuiSpinnerEditor() {
		super();

		this.guiButtons = new ArrayList<GuiButton>();

		this.title = new GuiButton();
		this.title.setText("...");
		this.title.setFontColor(0, 0, 0, 1.0f);
		this.addChild(this.title);

		super.addListener((GuiSpinnerListenerAdd) this);
		super.addListener((GuiSpinnerListenerRemove) this);
		super.addListener((GuiSpinnerListenerExpanded) this);
		super.addListener((GuiSpinnerListenerSorted) this);
	}

	@Override
	public void invokeSpinnerExpanded(GuiSpinner guiSpinner, boolean expanded) {
		for (GuiButton guiButton : this.guiButtons) {
			guiButton.setVisible(expanded);
		}
	}

	private void refreshButtons() {
		for (int i = 0; i < this.count(); i++) {
			GuiButton guiButton = this.guiButtons.get(i);
			guiButton.setAttribute(ATTR_BUTTON_INDEX, i);
			guiButton.setText(super.getName(i));
		}
	}

	@Override
	public void invokeSpinnerAddObject(GuiSpinner guiSpinner, int index) {

		String name = super.getName(index);

		// title now is selectable
		this.title.setSelectable(true);
		if (this.count() == 1) {
			this.title.setText(name + " ...");
		}

		// new gui button
		GuiButton guiButton = new GuiButton();
		guiButton.setOutColor(0.7f, 0.7f, 0.7f, 0.5f);
		guiButton.setBox(0.2f, -guiSpinner.count(), 0.8f, 1, 0);
		guiButton.setVisible(false);
		guiButton.addListener(PICK_LISTENER);

		this.guiButtons.add(guiButton);
		this.addChild(guiButton);
		this.refreshButtons();
	}

	@Override
	public void invokeSpinnerSorted(GuiSpinner guiSpinner) {
		this.refreshButtons();
	}

	@Override
	public void invokeSpinnerRemoveObject(GuiSpinner guiSpinner, int index) {
		if (this.guiButtons.size() == 0) {
			return;
		}

		GuiButton guiButton = this.guiButtons.remove(this.guiButtons.size() - 1);
		this.removeChild(guiButton);
		guiButton.deinitialize();
		this.refreshButtons();

		if (this.guiButtons.size() == 0) {
			this.title.setSelectable(false);
			this.title.setText("...");
		}
	}
}
