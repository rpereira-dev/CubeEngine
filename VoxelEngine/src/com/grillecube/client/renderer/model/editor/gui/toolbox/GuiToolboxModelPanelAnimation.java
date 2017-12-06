package com.grillecube.client.renderer.model.editor.gui.toolbox;

import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.components.GuiButton;
import com.grillecube.client.renderer.gui.components.GuiSpinner;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextCenterBox;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextFillBox;
import com.grillecube.client.renderer.gui.event.GuiEventClick;
import com.grillecube.client.renderer.gui.event.GuiListener;
import com.grillecube.client.renderer.gui.event.GuiSpinnerEventPick;
import com.grillecube.client.renderer.model.animation.Bone;
import com.grillecube.client.renderer.model.animation.BoneTransform;
import com.grillecube.client.renderer.model.animation.KeyFrame;
import com.grillecube.client.renderer.model.animation.ModelSkeletonAnimation;
import com.grillecube.client.renderer.model.editor.gui.GuiSliderBarEditor;
import com.grillecube.client.renderer.model.editor.gui.GuiSpinnerEditor;

public class GuiToolboxModelPanelAnimation extends GuiToolboxModelPanel {

	private final GuiButton addAnimation;
	private final GuiSpinnerEditor animations;
	private final GuiButton removeAnimation;

	private final GuiButton addFrame;
	private final GuiSpinnerEditor frames;
	private final GuiButton removeFrame;

	private final GuiSpinnerEditor poses;

	private final GuiSliderBarEditor timer;

	public GuiToolboxModelPanelAnimation() {
		super();
		this.addAnimation = new GuiButton();
		this.animations = new GuiSpinnerEditor();
		this.removeAnimation = new GuiButton();

		this.addFrame = new GuiButton();
		this.frames = new GuiSpinnerEditor();
		this.removeFrame = new GuiButton();

		this.poses = new GuiSpinnerEditor();

		this.timer = new GuiSliderBarEditor();
	}

	@Override
	public final void onInitialized(GuiRenderer guiRenderer) {
		this.addChild(this.addAnimation);
		this.addChild(this.animations);
		this.addChild(this.removeAnimation);

		this.addChild(this.addFrame);
		this.addChild(this.frames);
		this.addChild(this.removeFrame);

		this.addChild(this.poses);

		this.addChild(this.timer);

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
				refresh();
			}
		});

		this.animations.setHint("Animations...");
		this.animations.setBox(1 / 3.0f, 0.70f, 1 / 3.0f, 0.05f, 0);
		this.animations.addListener(new GuiListener<GuiSpinnerEventPick<GuiSpinner>>() {
			@Override
			public void invoke(GuiSpinnerEventPick<GuiSpinner> event) {
				onAnimationPicked();
				refresh();
			}
		});
		for (ModelSkeletonAnimation modelAnimation : this.getSelectedModel().getAnimations()) {
			this.animations.add(modelAnimation, modelAnimation.getName());
		}

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
					refresh();
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
				keyFrame.setTime(getSelectedAnimation().getDuration() + 1000);
				getSelectedAnimation().addKeyFrame(keyFrame);
				frames.add(keyFrame, String.valueOf(keyFrame.getTime()));
				frames.pick(frames.count() - 1);
				refresh();
			}
		});

		this.frames.setHint("Key frames...");
		this.frames.setBox(1 / 3.0f, 0.65f, 1 / 3.0f, 0.05f, 0);
		this.frames.addListener(new GuiListener<GuiSpinnerEventPick<GuiSpinner>>() {
			@Override
			public void invoke(GuiSpinnerEventPick<GuiSpinner> event) {
				onFramePicked();
				refresh();
			}
		});

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
					refresh();
				}
			}
		});

		this.poses.setHint("Poses...");
		this.poses.setBox(0.0f, 0.60f, 1.0f, 0.05f, 0);
		this.poses.addListener(new GuiListener<GuiSpinnerEventPick<GuiSpinner>>() {
			@Override
			public void invoke(GuiSpinnerEventPick<GuiSpinner> event) {
				onPosePicked();
				refresh();
			}
		});

		this.timer.setBox(0.2f, 0.1f, 0.6f, 0.05f, 0.0f);
		this.timer.setPrefix("time: ");
		this.timer.select(0.5f);
	}

	private final void onAnimationPicked() {
		this.frames.removeAll();
		if (this.getSelectedAnimation() != null) {
			for (KeyFrame keyFrame : this.getSelectedAnimation().getKeyFrames()) {
				this.frames.add(keyFrame, String.valueOf(keyFrame.getTime()));
			}
		}
		this.frames.pick(-1);
	}

	private final void onFramePicked() {
		this.poses.removeAll();
		KeyFrame frame = this.getSelectedKeyFrame();
		if (frame != null) {
			for (Bone bone : this.getSelectedModel().getSkeleton().getBones()) {
				BoneTransform boneTransform = frame.getBoneKeyFrames().get(bone.getName());
				if (boneTransform == null) {
					boneTransform = new BoneTransform();
					frame.getBoneKeyFrames().put(bone.getName(), boneTransform);
				}
				this.poses.add(boneTransform, bone.getName());
			}
		}
		this.poses.pick(-1);
	}

	private final void onPosePicked() {
		// TODO Auto-generated method stub

	}

	public final ModelSkeletonAnimation getSelectedAnimation() {
		return ((ModelSkeletonAnimation) this.animations.getPickedObject());
	}

	public final KeyFrame getSelectedKeyFrame() {
		return ((KeyFrame) this.frames.getPickedObject());
	}

	@Override
	public final void refresh() {
		this.removeAnimation.setEnabled(this.animations.count() > 0);
		this.removeFrame.setEnabled(this.frames.count() > 0);
		this.addFrame.setVisible(this.animations.count() > 0);
		this.frames.setVisible(this.animations.count() > 0);
		this.removeFrame.setVisible(this.animations.count() > 0);
		this.poses.setVisible(this.frames.count() > 0);
	}

	@Override
	public final String getTitle() {
		return ("Animation");
	}
}
