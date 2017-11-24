package com.grillecube.client.renderer.model.editor.gui;

import java.util.ArrayList;

import com.grillecube.client.renderer.gui.components.Gui;
import com.grillecube.client.renderer.gui.components.GuiButton;
import com.grillecube.client.renderer.gui.components.GuiSpinner;
import com.grillecube.client.renderer.gui.event.GuiEventClick;
import com.grillecube.client.renderer.gui.event.GuiEventPress;
import com.grillecube.client.renderer.gui.event.GuiListener;
import com.grillecube.common.utils.Color;

public class GuiSpinnerColor extends GuiSpinner {

	private static final String ATTR_BUTTON_INDEX = "spinnerIndex";

	private static final GuiListener<GuiEventClick<GuiButton>> BUTTONS_LEFT_PRESSED_LISTENER = new GuiListener<GuiEventClick<GuiButton>>() {
		@Override
		public void invoke(GuiEventClick<GuiButton> event) {
			GuiSpinnerColor spinner = ((GuiSpinnerColor) event.getGui().getParent());
			spinner.pick((Integer) event.getGui().getAttribute(ATTR_BUTTON_INDEX));
		}
	};

	private static final GuiListener<GuiEventPress<GuiButton>> PRESS_EXPAND_LISTENER = new GuiListener<GuiEventPress<GuiButton>>() {
		@Override
		public void invoke(GuiEventPress<GuiButton> event) {
			((GuiSpinner) event.getGui().getParent()).expand();
		}
	};

	private static final Color HINT_COLOR = new Color(0.5f, 0.5f, 0.5f, 1.0f);

	private final GuiButton title;
	private final ArrayList<GuiButton> guiColors;

	public GuiSpinnerColor() {
		super();

		this.guiColors = new ArrayList<GuiButton>();

		this.title = new GuiButton();
		this.title.addListener(PRESS_EXPAND_LISTENER);
		this.addChild(this.title);

		this.refreshButtons();
	}

	@Override
	protected void onIndexPicked(int pickedIndex) {
		if (this.isExpanded()) {
			this.expand();
		}
		this.refreshButtons();
	}

	@Override
	protected void onExpansionChanged() {
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
			Color color = (Color) super.getObject(i);
			if (color != null) {
				guiButton.setOutColor(color);
				guiButton.setHoveredColor(Color.mix(Gui.COLOR_WHITE, color, 0.5f, null));
				guiButton.setPressedColor(Color.mix(Gui.COLOR_LIGHT_BLUE, color, 0.5f, null));
				guiButton.setDisabledColor(Color.mix(Gui.COLOR_BLACK, color, 0.5f, null));
			}
			guiButton.setVisible(this.isExpanded());
			guiButton.setBox(0.2f, -i - 1, 0.8f, 1, 0);
		}
	}

	@Override
	protected void onObjectAdded(int index) {
		// new gui button
		GuiButton guiButton = new GuiButton();
		guiButton.setVisible(false);
		guiButton.addListener(BUTTONS_LEFT_PRESSED_LISTENER);
		guiButton.setOutColor(0.8f, 0.8f, 0.8f, 1.0f);
		guiButton.setTransition(0.05f);
		this.guiColors.add(guiButton);
		this.addChild(guiButton);
		guiButton.setLayer(this.getTopestLayer() + 1);
		this.refreshButtons();
	}

	@Override
	protected void onObjectsSorted() {
		this.refreshButtons();
	}

	@Override
	protected void onObjectRemoved(int index) {
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