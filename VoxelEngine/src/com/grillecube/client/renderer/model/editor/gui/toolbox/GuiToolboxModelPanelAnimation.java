package com.grillecube.client.renderer.model.editor.gui.toolbox;

import java.util.Comparator;

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.components.GuiButton;
import com.grillecube.client.renderer.gui.components.GuiPrompt;
import com.grillecube.client.renderer.gui.components.GuiSliderBar;
import com.grillecube.client.renderer.gui.components.GuiSpinner;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextCenterBox;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextFillBox;
import com.grillecube.client.renderer.gui.event.GuiEventClick;
import com.grillecube.client.renderer.gui.event.GuiEventPress;
import com.grillecube.client.renderer.gui.event.GuiListener;
import com.grillecube.client.renderer.gui.event.GuiPromptEventHeldTextChanged;
import com.grillecube.client.renderer.gui.event.GuiSliderBarEventSelect;
import com.grillecube.client.renderer.gui.event.GuiSpinnerEventPick;
import com.grillecube.client.renderer.model.animation.Bone;
import com.grillecube.client.renderer.model.animation.BoneTransform;
import com.grillecube.client.renderer.model.animation.KeyFrame;
import com.grillecube.client.renderer.model.animation.ModelSkeletonAnimation;
import com.grillecube.client.renderer.model.editor.gui.GuiPopUpCallback;
import com.grillecube.client.renderer.model.editor.gui.GuiPromptEditor;
import com.grillecube.client.renderer.model.editor.gui.GuiSliderBarEditor;
import com.grillecube.client.renderer.model.editor.gui.GuiSpinnerEditor;
import com.grillecube.client.renderer.model.editor.gui.GuiWindowNewAnimation;
import com.grillecube.client.renderer.model.instance.AnimationInstance;
import com.grillecube.client.renderer.model.instance.ModelInstance;
import com.grillecube.common.maths.Quaternion;
import com.grillecube.common.maths.Vector3f;

public class GuiToolboxModelPanelAnimation extends GuiToolboxModelPanel {

	private final GuiButton addAnimation;
	private final GuiSpinnerEditor animations;
	private final GuiButton removeAnimation;

	private final GuiButton addFrame;
	private final GuiSpinnerEditor frames;
	private final GuiButton removeFrame;

	private final GuiSpinnerEditor poses;

	private final GuiPromptEditor time;

	private final GuiPromptEditor posX;
	private final GuiPromptEditor posY;
	private final GuiPromptEditor posZ;

	private final GuiPromptEditor rotX;
	private final GuiPromptEditor rotY;
	private final GuiPromptEditor rotZ;

	private final GuiButton run;
	private final GuiButton loop;
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

		this.time = new GuiPromptEditor("time", "time (ms)");

		this.posX = new GuiPromptEditor("X", "pos. x");
		this.posY = new GuiPromptEditor("Y", "pos. y");
		this.posZ = new GuiPromptEditor("Z", "pos. z");

		this.rotX = new GuiPromptEditor("rX", "rot. x");
		this.rotY = new GuiPromptEditor("rY", "rot. y");
		this.rotZ = new GuiPromptEditor("rZ", "rot. z");

		this.run = new GuiButton();
		this.loop = new GuiButton();
		this.timer = new GuiSliderBarEditor();

	}

	@Override
	public final void onInitialized(GuiRenderer guiRenderer) {
		this.addChild(this.addAnimation);
		this.addChild(this.animations);
		this.addChild(this.removeAnimation);

		this.addChild(this.time);

		this.addChild(this.poses);

		this.addChild(this.posX);
		this.addChild(this.posY);
		this.addChild(this.posZ);

		this.addChild(this.rotX);
		this.addChild(this.rotY);
		this.addChild(this.rotZ);

		this.addChild(this.addFrame);
		this.addChild(this.frames);
		this.addChild(this.removeFrame);

		this.addChild(this.run);
		this.addChild(this.loop);
		this.addChild(this.timer);

		this.addAnimation.setText("Add");
		this.addAnimation.setBox(0.0f, 0.70f, 1 / 3.0f, 0.05f, 0.0f);
		this.addAnimation.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.addAnimation.addTextParameter(new GuiTextParameterTextCenterBox());
		this.addAnimation.addListener(new GuiListener<GuiEventClick<GuiButton>>() {
			@Override
			public void invoke(GuiEventClick<GuiButton> event) {

				new GuiWindowNewAnimation(new GuiPopUpCallback<GuiWindowNewAnimation>() {

					@Override
					public void onConfirm(GuiWindowNewAnimation popUp) {
						String animationName = popUp.name.asString();
						ModelSkeletonAnimation animation = new ModelSkeletonAnimation(animationName);
						getSelectedModel().addAnimation(animation);
						animations.add(animation, animation.getName());
						animations.pick(animations.count() - 1);
						VoxelEngineClient.instance().getRenderer().getGuiRenderer().toast("Animation added");

					}

					@Override
					public void onCancel(GuiWindowNewAnimation popUp) {
					}

				}).open(getOldestParent());

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
				if (getSelectedAnimation() == null) {
					// TODO : toast
					return;
				}
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

		this.time.setBox(0, 0.60f, 1.0f, 0.05f, 0);
		this.time.getPrompt().addListener(new GuiListener<GuiPromptEventHeldTextChanged<GuiPrompt>>() {
			@Override
			public void invoke(GuiPromptEventHeldTextChanged<GuiPrompt> event) {
				KeyFrame frame = getSelectedKeyFrame();
				if (frame == null) {
					return;
				}

				frame.setTime(time.getPrompt().asLong(0));
				frames.setName(frame, String.valueOf(frame.getTime()));
				frames.sort(new Comparator<Object>() {
					@Override
					public int compare(Object o1, Object o2) {
						KeyFrame f1 = (KeyFrame) o1;
						KeyFrame f2 = (KeyFrame) o2;
						return ((int) (f1.getTime() - f2.getTime()));
					}
				});
			}
		});
		this.poses.setHint("Poses...");
		this.poses.setBox(0.0f, 0.55f, 1.0f, 0.05f, 0);
		this.poses.addListener(new GuiListener<GuiSpinnerEventPick<GuiSpinner>>() {
			@Override
			public void invoke(GuiSpinnerEventPick<GuiSpinner> event) {
				onPosePicked();
				refresh();
			}
		});

		this.posX.setBox(0, 0.50f, 1.0f, 0.05f, 0);
		this.posY.setBox(0, 0.45f, 1.0f, 0.05f, 0);
		this.posZ.setBox(0, 0.40f, 1.0f, 0.05f, 0);

		this.rotX.setBox(0, 0.35f, 1.0f, 0.05f, 0);
		this.rotY.setBox(0, 0.30f, 1.0f, 0.05f, 0);
		this.rotZ.setBox(0, 0.25f, 1.0f, 0.05f, 0);

		this.run.setBox(0.2f, 0.15f, 0.3f, 0.05f, 0.0f);
		this.run.setText("Run");
		this.run.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.run.addTextParameter(new GuiTextParameterTextCenterBox());
		this.run.addListener(new GuiListener<GuiEventPress<GuiButton>>() {
			@Override
			public void invoke(GuiEventPress<GuiButton> event) {
				ModelInstance modelInstance = getSelectedModelInstance();
				ModelSkeletonAnimation animation = getSelectedAnimation();
				AnimationInstance instance = modelInstance.startAnimation(animation);
				instance.setTime((long) (timer.getPercent() * animation.getDuration()));
				instance.restart();
			}
		});

		this.loop.setBox(0.5f, 0.15f, 0.3f, 0.05f, 0.0f);
		this.loop.setText("Loop");
		this.loop.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.loop.addTextParameter(new GuiTextParameterTextCenterBox());
		this.loop.addListener(new GuiListener<GuiEventPress<GuiButton>>() {
			@Override
			public void invoke(GuiEventPress<GuiButton> event) {
				ModelInstance modelInstance = getSelectedModelInstance();
				ModelSkeletonAnimation animation = getSelectedAnimation();
				AnimationInstance instance = modelInstance.startAnimation(animation);
				instance.setTime((long) (timer.getPercent() * animation.getDuration()));
				instance.loop();
			}
		});

		this.timer.setBox(0.2f, 0.075f, 0.6f, 0.05f, 0.0f);
		this.timer.setPrefix("time: ");
		this.timer.select(0.5f);

		this.timer.addListener(new GuiListener<GuiSliderBarEventSelect<GuiSliderBar>>() {
			@Override
			public void invoke(GuiSliderBarEventSelect<GuiSliderBar> event) {
				ModelInstance modelInstance = getSelectedModelInstance();
				ModelSkeletonAnimation animation = getSelectedAnimation();
				AnimationInstance instance = modelInstance.startAnimation(animation);
				instance.setTime((long) (timer.getPercent() * animation.getDuration()));
				instance.freeze();
			}
		});

		GuiListener<GuiPromptEventHeldTextChanged<GuiPrompt>> listener = new GuiListener<GuiPromptEventHeldTextChanged<GuiPrompt>>() {
			@Override
			public void invoke(GuiPromptEventHeldTextChanged<GuiPrompt> event) {
				BoneTransform bt = getSelectedPose();
				Vector3f pos = bt.getTranslation();
				Quaternion quat = bt.getRotation();
				Vector3f rot = Quaternion.toEulerAngle(quat);

				rot.setX(rotX.getPrompt().asFloat(rot.x));
				rot.setY(rotY.getPrompt().asFloat(rot.y));
				rot.setZ(rotZ.getPrompt().asFloat(rot.z));
				Quaternion.toQuaternion(quat, rot);

				pos.setX(posX.getPrompt().asFloat(bt.getTranslation().x));
				pos.setY(posY.getPrompt().asFloat(bt.getTranslation().y));
				pos.setZ(posZ.getPrompt().asFloat(bt.getTranslation().z));
				bt.set(pos, quat);
			}
		};
		this.posX.getPrompt().addListener(listener);
		this.posY.getPrompt().addListener(listener);
		this.posZ.getPrompt().addListener(listener);
		this.rotX.getPrompt().addListener(listener);
		this.rotY.getPrompt().addListener(listener);
		this.rotZ.getPrompt().addListener(listener);

		this.refresh();
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
		int idx = this.poses.getPickedIndex();
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
			this.timer.select(frame.getTime() / (float) this.getSelectedAnimation().getDuration());
		} else {
			this.timer.select(0);
		}
		this.poses.pick(idx);
	}

	private final void onPosePicked() {
		BoneTransform bt = this.getSelectedPose();
		if (bt == null) {
			return;
		}

		this.posX.setValue(bt.getTranslation().x);
		this.posY.setValue(bt.getTranslation().y);
		this.posZ.setValue(bt.getTranslation().z);

		Vector3f rot = Quaternion.toEulerAngle(bt.getRotation());
		this.rotX.setValue(rot.x);
		this.rotY.setValue(rot.y);
		this.rotZ.setValue(rot.z);
	}

	public final ModelSkeletonAnimation getSelectedAnimation() {
		return ((ModelSkeletonAnimation) this.animations.getPickedObject());
	}

	public final KeyFrame getSelectedKeyFrame() {
		return ((KeyFrame) this.frames.getPickedObject());
	}

	public final BoneTransform getSelectedPose() {
		return ((BoneTransform) this.poses.getPickedObject());
	}

	@Override
	public final void refresh() {
		this.removeAnimation.setEnabled(this.animations.count() > 0);
		this.removeFrame.setEnabled(this.getSelectedKeyFrame() != null);

		this.addFrame.setVisible(this.getSelectedAnimation() != null);
		this.frames.setVisible(this.getSelectedAnimation() != null);
		this.removeFrame.setVisible(this.getSelectedAnimation() != null);

		this.time.setVisible(this.getSelectedKeyFrame() != null);
		this.time.getPrompt().setHeldText(
				this.getSelectedKeyFrame() == null ? "" : String.valueOf(this.getSelectedKeyFrame().getTime()));

		this.poses.setVisible(this.getSelectedKeyFrame() != null);

		this.posX.setVisible(this.getSelectedPose() != null);
		this.posY.setVisible(this.getSelectedPose() != null);
		this.posZ.setVisible(this.getSelectedPose() != null);
		this.rotX.setVisible(this.getSelectedPose() != null);
		this.rotY.setVisible(this.getSelectedPose() != null);
		this.rotZ.setVisible(this.getSelectedPose() != null);

		this.run.setVisible(this.getSelectedAnimation() != null);
		this.loop.setVisible(this.getSelectedAnimation() != null);
		this.timer.setVisible(this.getSelectedAnimation() != null);
	}

	@Override
	public final String getTitle() {
		return ("Animation");
	}
}
