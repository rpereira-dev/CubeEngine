package com.grillecube.client.renderer.model.editor.gui;

import java.util.ArrayList;

import com.grillecube.client.renderer.gui.components.GuiButton;
import com.grillecube.client.renderer.gui.components.GuiSpinner;
import com.grillecube.client.renderer.gui.event.GuiEventClick;
import com.grillecube.client.renderer.gui.event.GuiListener;
import com.grillecube.client.renderer.gui.event.GuiSpinnerEventAdd;
import com.grillecube.client.renderer.gui.event.GuiSpinnerEventExpanded;
import com.grillecube.client.renderer.gui.event.GuiSpinnerEventPick;
import com.grillecube.client.renderer.gui.event.GuiSpinnerEventRemove;
import com.grillecube.client.renderer.gui.event.GuiSpinnerEventSorted;
import com.grillecube.common.maths.Vector4f;

public class GuiSpinnerColor extends GuiSpinner {

	private static final String ATTR_BUTTON_INDEX = "spinnerIndex";

	private static final GuiListener<GuiSpinnerEventAdd<GuiSpinnerColor>> ADD_LISTENER = new GuiListener<GuiSpinnerEventAdd<GuiSpinnerColor>>() {
		@Override
		public void invoke(GuiSpinnerEventAdd<GuiSpinnerColor> event) {
			event.getGui().onObjectAdded(event.getAddedIndex());
		}
	};

	private static final GuiListener<GuiSpinnerEventExpanded<GuiSpinnerColor>> EXPAND_LISTENER = new GuiListener<GuiSpinnerEventExpanded<GuiSpinnerColor>>() {

		@Override
		public void invoke(GuiSpinnerEventExpanded<GuiSpinnerColor> event) {
			event.getGui().onExpansionChanged();
		}
	};

	private static final GuiListener<GuiSpinnerEventSorted<GuiSpinnerColor>> SORTED_LISTENER = new GuiListener<GuiSpinnerEventSorted<GuiSpinnerColor>>() {
		@Override
		public void invoke(GuiSpinnerEventSorted<GuiSpinnerColor> event) {
			event.getGui().onObjectsSorted();
		}
	};

	private static final GuiListener<GuiSpinnerEventRemove<GuiSpinnerColor>> REMOVE_LISTENER = new GuiListener<GuiSpinnerEventRemove<GuiSpinnerColor>>() {
		@Override
		public void invoke(GuiSpinnerEventRemove<GuiSpinnerColor> event) {
			event.getGui().onObjectRemoved(event.getRemovedIndex());
		}
	};

	private static final GuiListener<GuiSpinnerEventPick<GuiSpinnerColor>> PICKED_LISTENER = new GuiListener<GuiSpinnerEventPick<GuiSpinnerColor>>() {

		@Override
		public void invoke(GuiSpinnerEventPick<GuiSpinnerColor> event) {
			event.getGui().onIndexPicked(event.getPickedIndex());
		}
	};

	private static final GuiListener<GuiEventClick<GuiButton>> BUTTONS_LEFT_PRESSED_LISTENER = new GuiListener<GuiEventClick<GuiButton>>() {

		@Override
		public void invoke(GuiEventClick<GuiButton> event) {
			GuiSpinnerColor spinner = ((GuiSpinnerColor) event.getGui().getParent());
			spinner.pick((Integer) event.getGui().getAttribute(ATTR_BUTTON_INDEX));
		}
	};

	private static final Vector4f HINT_COLOR = new Vector4f(0.5f, 0.5f, 0.5f, 1.0f);

	private final GuiButton title;
	private final ArrayList<GuiButton> guiColors;

	public GuiSpinnerColor() {
		super();

		this.guiColors = new ArrayList<GuiButton>();

		this.title = new GuiButton();
		this.addChild(this.title);

		this.refreshButtons();

		super.addListener(ADD_LISTENER);
		super.addListener(EXPAND_LISTENER);
		super.addListener(SORTED_LISTENER);
		super.addListener(PICKED_LISTENER);
		super.addListener(REMOVE_LISTENER);
	}

	private final void onIndexPicked(int pickedIndex) {
		if (this.isExpanded()) {
			this.expand();
		}
		this.refreshButtons();
	}

	private final void onExpansionChanged() {
		this.refreshButtons();
	}

	private void refreshButtons() {

		// update title
		this.title.setSelectable(this.count() != 0);
		this.title.setSelected(this.isExpanded() && this.isSelectable());

		if (this.getPickedIndex() == -1) {
			this.title.setText(this.getHint());
			this.title.setDefaultColors();
			this.title.setFontColor(HINT_COLOR);
		} else {
			this.title.setText("");
			this.title.setColors(this.guiColors.get(this.getPickedIndex()));
		}

		// update buttons
		for (int i = 0; i < this.guiColors.size(); i++) {
			GuiButton guiButton = this.guiColors.get(i);
			guiButton.setAttribute(ATTR_BUTTON_INDEX, i);
			Vector4f color = (Vector4f) super.getObject(i);
			if (color != null) {
				guiButton.setOutColor(color);
				guiButton.setHoveredColor(Vector4f.scale(null, color, 1.4f));
				guiButton.setPressedColor(Vector4f.scale(null, color, 2.0f));
				guiButton.setDisabledColor(Vector4f.scale(null, color, 0.2f));
			}
			guiButton.setVisible(this.isExpanded());
			guiButton.setBox(0.2f, -i - 1, 0.8f, 1, 0);
		}
	}

	private final void onObjectAdded(int index) {
		// new gui button
		GuiButton guiButton = new GuiButton();
		guiButton.setVisible(false);
		guiButton.addListener(BUTTONS_LEFT_PRESSED_LISTENER);
		guiButton.setOutColor(0.8f, 0.8f, 0.8f, 1.0f);
		this.guiColors.add(guiButton);
		this.addChild(guiButton);
		guiButton.increaseLayer(10); // TODO make it proper
		this.refreshButtons();
	}

	private final void onObjectsSorted() {
		this.refreshButtons();
	}

	private final void onObjectRemoved(int index) {
		if (this.guiColors.size() == 0) {
			return;
		}

		GuiButton guiButton = this.guiColors.remove(this.guiColors.size() - 1);
		this.removeChild(guiButton);
		guiButton.deinitialize();
		this.refreshButtons();
	}

	/** called whenever the hint changes */
	@Override
	protected void onHintChanged() {
		if (this.getPickedIndex() == -1) {
			this.title.setText(super.getHint());
		}
	}
}
