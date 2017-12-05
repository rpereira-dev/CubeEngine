package com.grillecube.client.renderer.model.editor.gui.toolbox;

import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.components.GuiButton;
import com.grillecube.client.renderer.gui.components.GuiSpinner;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextCenterBox;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextFillBox;
import com.grillecube.client.renderer.gui.event.GuiEventClick;
import com.grillecube.client.renderer.gui.event.GuiListener;
import com.grillecube.client.renderer.gui.event.GuiSpinnerEventPick;
import com.grillecube.client.renderer.model.animation.KeyFrame;
import com.grillecube.client.renderer.model.animation.ModelSkeletonAnimation;
import com.grillecube.client.renderer.model.editor.gui.GuiSpinnerEditor;

public class GuiToolboxModelPanelAnimation extends GuiToolboxModelPanel {

	private final GuiButton addAnimation;
	private final GuiSpinnerEditor animations;
	private final GuiButton removeAnimation;

	private final GuiButton addFrame;
	private final GuiSpinnerEditor frames;
	private final GuiButton removeFrame;

	public GuiToolboxModelPanelAnimation() {
		super();
		this.addAnimation = new GuiButton();
		this.animations = new GuiSpinnerEditor();
		this.removeAnimation = new GuiButton();

		this.addFrame = new GuiButton();
		this.frames = new GuiSpinnerEditor();
		this.removeFrame = new GuiButton();
	}

	@Override
	public final void onInitialized(GuiRenderer guiRenderer) {
		this.addChild(this.addAnimation);
		this.addChild(this.animations);
		this.addChild(this.removeAnimation);

		this.addChild(this.addFrame);
		this.addChild(this.frames);
		this.addChild(this.removeFrame);

		this.addAnimation.setText("Add");
		this.addAnimation.setBox(0.0f, 0.70f, 1 / 3.0f, 0.05f, 0.0f);
		this.addAnimation.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.addAnimation.addTextParameter(new GuiTextParameterTextCenterBox());
		this.addAnimation.addListener(new GuiListener<GuiEventClick<GuiButton>>() {
			@Override
			public void invoke(GuiEventClick<GuiButton> event) {
				// TODO : animation name
				String animationName = String.valueOf(System.currentTimeMillis());
				ModelSkeletonAnimation animation = new ModelSkeletonAnimation(animationName);
				getSelectedModel().addAnimation(animation);
				animations.add(animation);
				animations.pick(animations.count() - 1);
				addAnimation.setEnabled(true);
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
		for (ModelSkeletonAnimation modelAnimation : this.getSelectedModel().getAnimations()) {
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
				if (animations.count() > 0) {
					int idx = animations.getPickedIndex();
					ModelSkeletonAnimation animation = (ModelSkeletonAnimation) animations.remove(idx);
					getSelectedModel().removeAnimation(animation);
					if (animations.count() == 0) {
						removeAnimation.setEnabled(false);
					}
				}
			}
		});

		this.addFrame.setText("Add");
		this.addFrame.setBox(0.0f, 0.65f, 1 / 3.0f, 0.05f, 0.0f);
		this.addFrame.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.addFrame.addTextParameter(new GuiTextParameterTextCenterBox());
		this.addFrame.addListener(new GuiListener<GuiEventClick<GuiButton>>() {
			@Override
			public void invoke(GuiEventClick<GuiButton> event) {
				KeyFrame keyFrame = new KeyFrame();
				getSelectedAnimation().addKeyFrame(keyFrame);
				frames.add(keyFrame, String.valueOf(keyFrame.getTime()));
				frames.pick(frames.count() - 1);
				addFrame.setEnabled(true);
			}
		});

		this.frames.setHint("Key frames...");
		this.frames.setBox(1 / 3.0f, 0.65f, 1 / 3.0f, 0.05f, 0);
		this.frames.addListener(new GuiListener<GuiSpinnerEventPick<GuiSpinner>>() {
			@Override
			public void invoke(GuiSpinnerEventPick<GuiSpinner> event) {
				onFramePicked();
			}
		});
		this.frames.pick(0);

		this.removeFrame.setText("Remove");
		this.removeFrame.setBox(2 * 1 / 3.0f, 0.65f, 1 / 3.0f, 0.05f, 0.0f);
		this.removeFrame.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.removeFrame.addTextParameter(new GuiTextParameterTextCenterBox());
		this.removeFrame.addListener(new GuiListener<GuiEventClick<GuiButton>>() {
			@Override
			public void invoke(GuiEventClick<GuiButton> event) {
				if (frames.count() > 0) {
					int idx = frames.getPickedIndex();
					KeyFrame keyFrame = (KeyFrame) frames.remove(idx);
					getSelectedAnimation().removeKeyFrame(keyFrame);
					if (frames.count() == 0) {
						removeFrame.setEnabled(false);
					}
				}
			}
		});

		refresh();
	}

	protected final void onFramePicked() {
		// TODO Auto-generated method stub
	}

	public final ModelSkeletonAnimation getSelectedAnimation() {
		return ((ModelSkeletonAnimation) this.animations.getPickedObject());
	}

	@Override
	public final void refresh() {
		this.removeAnimation.setEnabled(this.animations.count() > 0);
		this.removeFrame.setEnabled(this.frames.count() > 0);
		this.refreshFrames();
	}

	@Override
	public final String getTitle() {
		return ("Animation");
	}

	private final void onAnimationPicked() {
		this.refreshFrames();
	}

	private void refreshFrames() {
		this.frames.removeAll();
		if (this.getSelectedAnimation() != null) {
			for (KeyFrame keyFrame : this.getSelectedAnimation().getKeyFrames()) {
				this.frames.add(keyFrame, String.valueOf(keyFrame.getTime()));
			}
		}
	}
}
