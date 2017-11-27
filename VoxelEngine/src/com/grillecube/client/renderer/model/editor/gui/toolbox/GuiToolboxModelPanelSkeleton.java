package com.grillecube.client.renderer.model.editor.gui.toolbox;

import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.components.GuiButton;
import com.grillecube.client.renderer.gui.components.GuiLabel;
import com.grillecube.client.renderer.gui.components.GuiPrompt;
import com.grillecube.client.renderer.gui.components.GuiSpinner;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextCenterBox;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextFillBox;
import com.grillecube.client.renderer.gui.event.GuiEventClick;
import com.grillecube.client.renderer.gui.event.GuiListener;
import com.grillecube.client.renderer.gui.event.GuiPromptEventHeldTextChanged;
import com.grillecube.client.renderer.gui.event.GuiSpinnerEventPick;
import com.grillecube.client.renderer.model.ModelSkeleton;
import com.grillecube.client.renderer.model.animation.Bone;
import com.grillecube.client.renderer.model.editor.gui.GuiPromptEditor;
import com.grillecube.client.renderer.model.editor.gui.GuiSpinnerEditor;
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.maths.Quaternion;
import com.grillecube.common.maths.Vector3f;

public class GuiToolboxModelPanelSkeleton extends GuiToolboxModelPanel {

	private final GuiButton addBone;
	private final GuiSpinnerEditor bones;
	private final GuiButton removeBone;

	private final GuiLabel boneTransformLabel;

	private final GuiPromptEditor posX;
	private final GuiPromptEditor posY;
	private final GuiPromptEditor posZ;

	private final GuiPromptEditor rotX;
	private final GuiPromptEditor rotY;
	private final GuiPromptEditor rotZ;

	public GuiToolboxModelPanelSkeleton() {
		super();
		this.addBone = new GuiButton();
		this.bones = new GuiSpinnerEditor();
		this.removeBone = new GuiButton();

		this.boneTransformLabel = new GuiLabel();

		this.posX = new GuiPromptEditor("X", "pos. x");
		this.posY = new GuiPromptEditor("Y", "pos. y");
		this.posZ = new GuiPromptEditor("Z", "pos. z");

		this.rotX = new GuiPromptEditor("X", "rot. x");
		this.rotY = new GuiPromptEditor("Y", "rot. y");
		this.rotZ = new GuiPromptEditor("Z", "rot. z");
	}

	@Override
	public final void onInitialized(GuiRenderer guiRenderer) {
		{
			this.addChild(this.addBone);
			this.addChild(this.bones);
			this.addChild(this.removeBone);

			this.addBone.setText("Add");
			this.addBone.setBox(0.0f, 0.70f, 1 / 3.0f, 0.05f, 0.0f);
			this.addBone.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
			this.addBone.addTextParameter(new GuiTextParameterTextCenterBox());
			this.addBone.addListener(new GuiListener<GuiEventClick<GuiButton>>() {
				@Override
				public void invoke(GuiEventClick<GuiButton> event) {
					Bone bone = new Bone(getModel().getSkeleton(), (System.currentTimeMillis() % 10000) + "");
					getModelSkeleton().addBone(bone);
					refresh();
				}
			});

			this.bones.setHint("Bones...");
			this.bones.setBox(1 / 3.0f, 0.70f, 1 / 3.0f, 0.05f, 0);
			this.bones.addListener(new GuiListener<GuiSpinnerEventPick<GuiSpinner>>() {
				@Override
				public void invoke(GuiSpinnerEventPick<GuiSpinner> event) {
					onBonePicked();
				}
			});
			for (Bone bone : this.getModelSkeleton().getBones()) {
				this.bones.add(bone, bone.getName());
			}
			this.bones.pick(0);

			this.removeBone.setText("Remove");
			this.removeBone.setBox(2 * 1 / 3.0f, 0.70f, 1 / 3.0f, 0.05f, 0.0f);
			this.removeBone.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
			this.removeBone.addTextParameter(new GuiTextParameterTextCenterBox());
			this.removeBone.addListener(new GuiListener<GuiEventClick<GuiButton>>() {
				@Override
				public void invoke(GuiEventClick<GuiButton> event) {
					getModelSkeleton().removeBone(getSelectedBone());
					refresh();
				}
			});
		}

		{
			this.boneTransformLabel.setBox(0, 0.65f, 1, 0.05f, 0);
			this.boneTransformLabel.setText("Local Bone Transform");
			this.boneTransformLabel.setFontColor(0, 0, 0, 1.0f);
			this.boneTransformLabel.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
			this.boneTransformLabel.addTextParameter(new GuiTextParameterTextCenterBox());
			this.addChild(this.boneTransformLabel);
		}

		{
			this.addChild(this.posX);
			this.addChild(this.posY);
			this.addChild(this.posZ);
			this.posX.setBox(0, 0.60f, 1.0f, 0.05f, 0);
			this.posY.setBox(0, 0.55f, 1.0f, 0.05f, 0);
			this.posZ.setBox(0, 0.50f, 1.0f, 0.05f, 0);
		}

		{
			this.addChild(this.rotX);
			this.addChild(this.rotY);
			this.addChild(this.rotZ);

			this.rotX.setBox(0, 0.45f, 1.0f, 0.05f, 0);
			this.rotY.setBox(0, 0.40f, 1.0f, 0.05f, 0);
			this.rotZ.setBox(0, 0.35f, 1.0f, 0.05f, 0);
		}

		Bone bone = this.getSelectedBone();
		if (bone != null) {
			this.posX.getPrompt().setHeldText(String.valueOf(bone.getLocalTranslation().x));
			this.posY.getPrompt().setHeldText(String.valueOf(bone.getLocalTranslation().y));
			this.posZ.getPrompt().setHeldText(String.valueOf(bone.getLocalTranslation().z));

			Vector3f rot = Quaternion.toEulerAngle(bone.getLocalRotation());
			this.rotX.getPrompt().setHeldText(String.valueOf(rot.x));
			this.rotY.getPrompt().setHeldText(String.valueOf(rot.y));
			this.rotZ.getPrompt().setHeldText(String.valueOf(rot.z));
		}

		GuiListener<GuiPromptEventHeldTextChanged<GuiPrompt>> listener = new GuiListener<GuiPromptEventHeldTextChanged<GuiPrompt>>() {
			@Override
			public void invoke(GuiPromptEventHeldTextChanged<GuiPrompt> event) {
				Matrix4f localBindTransform = new Matrix4f();
				float rx = rotX.getPrompt().asFloat(0.0f);
				float ry = rotY.getPrompt().asFloat(0.0f);
				float rz = rotZ.getPrompt().asFloat(0.0f);
				localBindTransform.rotateXYZ(rx, ry, rz);
				Quaternion q = Quaternion.toQuaternion(rx, ry, rz);
				getSelectedBone().getLocalRotation().set(q);

				float x = posX.getPrompt().asFloat(0.0f);
				float y = posY.getPrompt().asFloat(0.0f);
				float z = posZ.getPrompt().asFloat(0.0f);
				localBindTransform.translate(-x, -y, -z);
				getSelectedBone().getLocalTranslation().set(x, y, z);
				getSelectedBone().setLocalBindTransform(localBindTransform);
				getSelectedBone().calcInverseBindTransform();
			}
		};
		this.posX.getPrompt().addListener(listener);
		this.posY.getPrompt().addListener(listener);
		this.posZ.getPrompt().addListener(listener);
		this.rotX.getPrompt().addListener(listener);
		this.rotY.getPrompt().addListener(listener);
		this.rotZ.getPrompt().addListener(listener);
	}

	@Override
	public void refresh() {
		this.bones.removeAll();
		for (Bone bone : this.getModelSkeleton().getBones()) {
			this.bones.add(bone, bone.getName());
		}
		this.bones.pick(0);

		if (this.bones.count() == 0) {
			this.boneTransformLabel.setVisible(false);
			this.posX.setVisible(false);
			this.posY.setVisible(false);
			this.posZ.setVisible(false);
			this.rotX.setVisible(false);
			this.rotY.setVisible(false);
			this.rotZ.setVisible(false);
		}
	}

	private final void onBonePicked() {

	}

	public final ModelSkeleton getModelSkeleton() {
		return (this.getModel().getSkeleton());
	}

	private final Bone getSelectedBone() {
		return ((Bone) this.bones.getPickedObject());
	}

	@Override
	public String getTitle() {
		return ("Skeleton");
	}
}
