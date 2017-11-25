package com.grillecube.client.renderer.model.editor.gui;

import com.grillecube.client.renderer.gui.components.GuiButton;
import com.grillecube.client.renderer.gui.components.GuiText;
import com.grillecube.client.renderer.gui.components.GuiWindow;
import com.grillecube.client.renderer.gui.components.parameters.GuiParameter;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextCenterBox;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextFillBox;
import com.grillecube.client.renderer.gui.event.GuiEventPress;
import com.grillecube.client.renderer.gui.event.GuiListener;
import com.grillecube.client.renderer.model.editor.camera.CameraToolRigging;
import com.grillecube.client.renderer.model.editor.mesher.ModelBlockData;

public class GuiWindowRigging extends GuiWindow {

	private final GuiButton confirm;
	private final GuiButton cancel;

	public GuiWindowRigging(CameraToolRigging cameraToolRigging) {
		super();
		
		GuiParameter<GuiText> txtSize = new GuiTextParameterTextFillBox(0.75f);
		GuiParameter<GuiText> txtCenter = new GuiTextParameterTextCenterBox();

		float w = 0.2f;
		float h = w / 1.6f;
		
		this.confirm = new GuiButton();
		this.confirm.setText("Confirm");
		this.confirm.addTextParameter(txtSize);
		this.confirm.addTextParameter(txtCenter);
		this.confirm.setBox(0.2f, 0.1f, w, h, 0.0f);
		this.addChild(this.confirm);
		this.confirm.addListener(new GuiListener<GuiEventPress<GuiButton>>() {
			@Override
			public void invoke(GuiEventPress<GuiButton> event) {
				this.setBoneWeight();
				close();
			}

			private final void setBoneWeight() {
				int x0 = cameraToolRigging.getX();
				int y0 = cameraToolRigging.getY();
				int z0 = cameraToolRigging.getZ();
				for (int dx = 0; dx < cameraToolRigging.getWidth(); dx++) {
					for (int dy = 0; dy < cameraToolRigging.getHeight(); dy++) {
						for (int dz = 0; dz < cameraToolRigging.getDepth(); dz++) {
							int x = x0 + dx;
							int y = y0 + dy;
							int z = z0 + dz;
							ModelBlockData blockData = cameraToolRigging.getModel().getBlockData(x, y, z);
							if (blockData != null) {
								int i = 0;
								String bone = cameraToolRigging.getModel().getSkeleton().getBones().get(0).getName();
								float weight = 0.5f;
								blockData.setBone(i, bone, weight);
							}
						}
					}
				}
			}
		});
		this.cancel = new GuiButton();
		this.cancel.setText("Decline");
		this.cancel.setBox(0.6f, 0.1f, w, h, 0.0f);
		this.cancel.addTextParameter(txtSize);
		this.cancel.addTextParameter(txtCenter);
		this.addChild(this.cancel);
	}
}
