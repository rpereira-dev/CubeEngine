package com.grillecube.client.renderer.model.editor.gui.toolbox;

import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.components.GuiButton;
import com.grillecube.client.renderer.gui.components.GuiSpinner;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextCenterBox;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextFillBox;
import com.grillecube.client.renderer.gui.event.GuiEventClick;
import com.grillecube.client.renderer.gui.event.GuiListener;
import com.grillecube.client.renderer.gui.event.GuiSpinnerEventPick;
import com.grillecube.client.renderer.model.animation.ModelSkeletonAnimation;
import com.grillecube.client.renderer.model.editor.gui.GuiSpinnerEditor;

public class GuiToolboxModelPanelAnimation extends GuiToolboxModelPanel {

	private final GuiButton addAnimation;
	private final GuiSpinnerEditor animations;
	private final GuiButton removeAnimation;

	public GuiToolboxModelPanelAnimation() {
		super();
		this.addAnimation = new GuiButton();
		this.animations = new GuiSpinnerEditor();
		this.removeAnimation = new GuiButton();
	}

	@Override
	public final void onInitialized(GuiRenderer guiRenderer) {
		this.addChild(this.addAnimation);
		this.addChild(this.animations);
		this.addChild(this.removeAnimation);

		this.addAnimation.setText("Add");
		this.addAnimation.setBox(0.0f, 0.70f, 1 / 3.0f, 0.05f, 0.0f);
		this.addAnimation.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.addAnimation.addTextParameter(new GuiTextParameterTextCenterBox());
		this.addAnimation.addListener(new GuiListener<GuiEventClick<GuiButton>>() {
			@Override
			public void invoke(GuiEventClick<GuiButton> event) {
				// TODO
				refresh();
			}
		});

		this.animations.setHint("Animations...");
		this.animations.setBox(1 / 3.0f, 0.70f, 1 / 3.0f, 0.05f, 0);
		this.animations.addListener(new GuiListener<GuiSpinnerEventPick<GuiSpinner>>() {
			@Override
			public void invoke(GuiSpinnerEventPick<GuiSpinner> event) {
				onAnimationPicked();
			}
		});
		for (ModelSkeletonAnimation modelAnimation : this.getModel().getAnimations()) {
			this.animations.add(modelAnimation, modelAnimation.getName());
		}
		this.animations.pick(0);

		this.removeAnimation.setText("Remove");
		this.removeAnimation.setBox(2 * 1 / 3.0f, 0.70f, 1 / 3.0f, 0.05f, 0.0f);
		this.removeAnimation.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.removeAnimation.addTextParameter(new GuiTextParameterTextCenterBox());
		this.removeAnimation.addListener(new GuiListener<GuiEventClick<GuiButton>>() {
			@Override
			public void invoke(GuiEventClick<GuiButton> event) {
				// TODO
				refresh();
			}
		});
	}

	@Override
	public void refresh() {

	}

	@Override
	public String getTitle() {
		return ("Animation");
	}

	private final void onAnimationPicked() {

	}

}
