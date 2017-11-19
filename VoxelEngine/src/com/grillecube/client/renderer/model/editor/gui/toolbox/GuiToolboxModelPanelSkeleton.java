package com.grillecube.client.renderer.model.editor.gui.toolbox;

import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.components.GuiButton;
import com.grillecube.client.renderer.gui.components.GuiLabel;
import com.grillecube.client.renderer.gui.components.GuiSliderBar;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextCenterBox;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextFillBox;
import com.grillecube.client.renderer.gui.event.GuiEventClick;
import com.grillecube.client.renderer.gui.event.GuiListener;
import com.grillecube.client.renderer.gui.event.GuiSliderBarEventValueChanged;
import com.grillecube.client.renderer.model.ModelSkeleton;
import com.grillecube.client.renderer.model.animation.Bone;
import com.grillecube.client.renderer.model.editor.gui.GuiPromptEditor;
import com.grillecube.client.renderer.model.editor.gui.GuiSpinnerEditor;
import com.grillecube.common.maths.Matrix4f;

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

			Object[] pos = (Object[]) GuiSliderBar.intRange(-16, 16);
			this.posX.addValuesArray(pos);
			this.posY.addValuesArray(pos);
			this.posZ.addValuesArray(pos);
			this.posX.setPrefix("Pos. X: ");
			this.posY.setPrefix("Pos. Y: ");
			this.posZ.setPrefix("Pos. Z: ");

			this.posX.select((Object) 0);
			this.posY.select((Object) 0);
			this.posZ.select((Object) 0);
		}

		{
			this.addChild(this.rotX);
			this.addChild(this.rotY);
			this.addChild(this.rotZ);

			this.rotX.setBox(0, 0.45f, 1.0f, 0.05f, 0);
			this.rotY.setBox(0, 0.40f, 1.0f, 0.05f, 0);
			this.rotZ.setBox(0, 0.35f, 1.0f, 0.05f, 0);

			Object[] rot = (Object[]) GuiSliderBar.intRange(-180, 180);
			this.rotX.addValuesArray(rot);
			this.rotY.addValuesArray(rot);
			this.rotZ.addValuesArray(rot);
			this.rotX.setPrefix("Rot. X: ");
			this.rotY.setPrefix("Rot. Y: ");
			this.rotZ.setPrefix("Rot. Z: ");

			this.rotX.select((Object) 0);
			this.rotY.select((Object) 0);
			this.rotZ.select((Object) 0);
		}

		GuiListener<GuiSliderBarEventValueChanged<GuiSliderBar>> listener = new GuiListener<GuiSliderBarEventValueChanged<GuiSliderBar>>() {
			@Override
			public void invoke(GuiSliderBarEventValueChanged<GuiSliderBar> event) {
				Matrix4f localBindTransform = new Matrix4f();
				localBindTransform.rotateXYZ((float) ((Integer) rotX.getSelectedValue() / 180.0f * Math.PI),
						(float) ((Integer) rotY.getSelectedValue() / 180.0f * Math.PI),
						(float) ((Integer) rotZ.getSelectedValue() / 180.0f * Math.PI));
				localBindTransform.translate(-(Integer) posX.getSelectedValue(), -(Integer) posY.getSelectedValue(),
						-(Integer) posZ.getSelectedValue());
				getSelectedBone().setLocalBindTransform(localBindTransform);
				getSelectedBone().calcInverseBindTransform();
			}
		};
		this.posX.addListener(listener);
		this.posY.addListener(listener);
		this.posZ.addListener(listener);
		this.rotX.addListener(listener);
		this.rotY.addListener(listener);
		this.rotZ.addListener(listener);
	}

	@Override
	public void refresh() {
		this.bones.removeAll();
		for (Bone bone : this.getModelSkeleton().getBones()) {
			this.bones.add(bone, bone.getName());
		}

		if (this.bones.count() == 0) {
			this.boneTransformLabel.setVisible(false);
			this.posX.setVisible(false);
			this.posY.setVisible(false);
			this.posZ.setVisible(false);
			this.rotX.setVisible(false);
			this.rotY.setVisible(false);
			this.rotZ.setVisible(false);
		} else {
			this.bones.pick(0);
		}
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
