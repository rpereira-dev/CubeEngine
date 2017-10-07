package com.grillecube.client.renderer.model.editor.gui;

import java.util.ArrayList;

import com.grillecube.client.renderer.gui.components.GuiButton;
import com.grillecube.client.renderer.gui.components.GuiSpinner;
import com.grillecube.client.renderer.gui.event.GuiEventClick;
import com.grillecube.client.renderer.gui.event.GuiEventPress;
import com.grillecube.client.renderer.gui.event.GuiListener;
import com.grillecube.client.renderer.gui.event.GuiSpinnerEventAdd;
import com.grillecube.client.renderer.gui.event.GuiSpinnerEventExpanded;
import com.grillecube.client.renderer.gui.event.GuiSpinnerEventPick;
import com.grillecube.client.renderer.gui.event.GuiSpinnerEventRemove;
import com.grillecube.client.renderer.gui.event.GuiSpinnerEventSorted;
import com.grillecube.common.maths.Vector4f;

public class GuiSpinnerEditor extends GuiSpinner {

	private static final String ATTR_BUTTON_INDEX = "spinnerIndex";

	private static final GuiListener<GuiSpinnerEventAdd<GuiSpinnerEditor>> ADD_LISTENER = new GuiListener<GuiSpinnerEventAdd<GuiSpinnerEditor>>() {
		@Override
		public void invoke(GuiSpinnerEventAdd<GuiSpinnerEditor> event) {
			event.getGui().onObjectAdded(event.getAddedIndex());
		}
	};

	private static final GuiListener<GuiSpinnerEventExpanded<GuiSpinnerEditor>> EXPAND_LISTENER = new GuiListener<GuiSpinnerEventExpanded<GuiSpinnerEditor>>() {

		@Override
		public void invoke(GuiSpinnerEventExpanded<GuiSpinnerEditor> event) {
			event.getGui().onExpansionChanged();
		}
	};

	private static final GuiListener<GuiSpinnerEventSorted<GuiSpinnerEditor>> SORTED_LISTENER = new GuiListener<GuiSpinnerEventSorted<GuiSpinnerEditor>>() {
		@Override
		public void invoke(GuiSpinnerEventSorted<GuiSpinnerEditor> event) {
			event.getGui().onObjectsSorted();
		}
	};

	private static final GuiListener<GuiSpinnerEventRemove<GuiSpinnerEditor>> REMOVE_LISTENER = new GuiListener<GuiSpinnerEventRemove<GuiSpinnerEditor>>() {
		@Override
		public void invoke(GuiSpinnerEventRemove<GuiSpinnerEditor> event) {
			event.getGui().onObjectRemoved(event.getRemovedIndex());
		}
	};

	private static final GuiListener<GuiSpinnerEventPick<GuiSpinnerEditor>> PICKED_LISTENER = new GuiListener<GuiSpinnerEventPick<GuiSpinnerEditor>>() {

		@Override
		public void invoke(GuiSpinnerEventPick<GuiSpinnerEditor> event) {
			event.getGui().onIndexPicked(event.getPickedIndex());
		}
	};

	private static final GuiListener<GuiEventClick<GuiButton>> BUTTONS_LEFT_PRESSED_LISTENER = new GuiListener<GuiEventClick<GuiButton>>() {

		@Override
		public void invoke(GuiEventClick<GuiButton> event) {
			GuiSpinnerEditor spinner = ((GuiSpinnerEditor) event.getGui().getParent());
			spinner.pick((Integer) event.getGui().getAttribute(ATTR_BUTTON_INDEX));
		}
	};

	private static final GuiListener<GuiEventPress<GuiButton>> PRESS_EXPAND_LISTENER = new GuiListener<GuiEventPress<GuiButton>>() {
		@Override
		public void invoke(GuiEventPress<GuiButton> event) {
			((GuiSpinner) event.getGui().getParent()).expand();
		}
	};

	private static final Vector4f TX_COLOR = new Vector4f(0, 0, 0, 1.0f);
	private static final Vector4f HINT_COLOR = new Vector4f(0.5f, 0.5f, 0.5f, 1.0f);

	private final GuiButton title;
	private final ArrayList<GuiButton> guiButtons;

	public GuiSpinnerEditor() {
		super();

		this.guiButtons = new ArrayList<GuiButton>();

		this.title = new GuiButton();
		this.title.addListener(PRESS_EXPAND_LISTENER);
		this.addChild(this.title);

		this.refreshButtons();

		super.addListener(ADD_LISTENER);
		super.addListener(EXPAND_LISTENER);
		super.addListener(SORTED_LISTENER);
		super.addListener(PICKED_LISTENER);
		super.addListener(REMOVE_LISTENER);
	}

	private final void onIndexPicked(int pickedIndex) {
		this.refreshButtons();
	}

	private final void onExpansionChanged() {
		this.refreshButtons();
	}

	private void refreshButtons() {

		// update title
		this.title.setSelectable(this.count() != 0);
		this.title.setSelected(this.isExpanded() && this.title.isSelectable());
		this.title.setText(this.getPickedIndex() == -1 ? this.getHint() : "-   " + this.getPickedName() + " ...");
		this.title.setFontColor(this.getPickedIndex() == -1 ? HINT_COLOR : TX_COLOR);

		// update buttons
		for (int i = 0; i < this.guiButtons.size(); i++) {
			GuiButton guiButton = this.guiButtons.get(i);
			guiButton.setAttribute(ATTR_BUTTON_INDEX, i);
			guiButton.setText(">   " + super.getName(i));
			guiButton.setVisible(this.isExpanded());
			guiButton.setBox(0.05f, -i - 1, 0.95f, 1, 0);
		}
	}

	private final void onObjectAdded(int index) {
		// new gui button
		GuiButton guiButton = new GuiButton();
		guiButton.setVisible(false);
		guiButton.addListener(BUTTONS_LEFT_PRESSED_LISTENER);
		guiButton.setOutColor(0.8f, 0.8f, 0.8f, 1.0f);
		this.guiButtons.add(guiButton);
		this.addChild(guiButton);
		guiButton.increaseLayer(64);
		this.refreshButtons();
	}

	private final void onObjectsSorted() {
		this.refreshButtons();
	}

	private final void onObjectRemoved(int index) {
		if (this.guiButtons.size() == 0) {
			return;
		}

		GuiButton guiButton = this.guiButtons.remove(this.guiButtons.size() - 1);
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
