package com.grillecube.client.renderer.model.editor.gui.components;

import java.util.ArrayList;

import com.grillecube.client.renderer.gui.components.GuiButton;
import com.grillecube.client.renderer.gui.components.GuiSpinner;
import com.grillecube.client.renderer.gui.listeners.GuiSpinnerListenerAdd;
import com.grillecube.client.renderer.gui.listeners.GuiSpinnerListenerExpanded;
import com.grillecube.client.renderer.gui.listeners.GuiSpinnerListenerRemove;
import com.grillecube.client.renderer.gui.listeners.GuiSpinnerListenerSorted;

public class GuiSpinnerEditor extends GuiSpinner implements GuiSpinnerListenerAdd, GuiSpinnerListenerExpanded,
		GuiSpinnerListenerSorted, GuiSpinnerListenerRemove {

	private GuiButton title;
	private ArrayList<GuiButton> guiButtons;

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

	@Override
	public void invokeSpinnerAddObject(GuiSpinner guiSpinner, Object object) {

		// title now is selectable
		this.title.setSelectable(true);

		// new gui button
		GuiButton guiButton = new GuiButton();
		guiButton.setOutColor(0.7f, 0.7f, 0.7f, 0.5f);
		int y = guiSpinner.count();
		guiButton.setBox(0, -y, 1, 1, 0);
		guiButton.setText(object.toString());
		this.guiButtons.add(guiButton);
		this.addChild(guiButton);
		guiButton.setVisible(false);
	}

	@Override
	public void invokeSpinnerSorted(GuiSpinner guiSpinner) {
		// TODO Auto-generated method stub

	}

	@Override
	public void invokeSpinnerRemoveObject(GuiSpinner guiSpinner, Object object, int index) {
		this.title.setSelectable(guiSpinner.count() > 0);
	}
}
