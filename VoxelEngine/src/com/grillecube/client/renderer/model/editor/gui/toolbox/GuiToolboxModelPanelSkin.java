package com.grillecube.client.renderer.model.editor.gui.toolbox;

import com.grillecube.client.renderer.gui.components.GuiButton;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextCenterBox;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextFillBox;
import com.grillecube.client.renderer.gui.event.GuiListener;
import com.grillecube.client.renderer.gui.event.GuiSliderBarEventValueChanged;
import com.grillecube.client.renderer.model.editor.gui.GuiSliderBarEditor;
import com.grillecube.client.renderer.model.editor.gui.GuiSpinnerEditor;

public class GuiToolboxModelPanelSkin extends GuiToolboxModelPanel {

	/** the model block size unit slider bar */
	private final GuiSliderBarEditor pixelPerFace;

	private final GuiButton addSkin;
	private final GuiSpinnerEditor skins;
	private final GuiButton removeSkin;

	private final GuiButton addColor;
	private final GuiSpinnerEditor colors;
	private final GuiButton removeColor;

	public GuiToolboxModelPanelSkin() {
		super();

		// number of pixels per face
		this.pixelPerFace = new GuiSliderBarEditor();
		this.pixelPerFace.setBox(0, 0.70f, 1.0f, 0.05f, 0);
		this.pixelPerFace.addValues(1, 4, 9, 16, 25, 36, 49, 64, 81, 100, 121, 144, 169, 196, 225, 256);
		this.pixelPerFace.addListener(new GuiListener<GuiSliderBarEventValueChanged<GuiSliderBarEditor>>() {
			@Override
			public void invoke(GuiSliderBarEventValueChanged<GuiSliderBarEditor> event) {
				onPixelPerFaceChanged();
			}
		});
		this.pixelPerFace.setPrefix("Pixel per face: ");
		this.pixelPerFace.select((Object) 1.0f);
		this.addChild(this.pixelPerFace);

		// skins
		this.addSkin = new GuiButton();
		this.addSkin.setText("Add");
		this.addSkin.setBox(0.0f, 0.65f, 1 / 3.0f, 0.05f, 0.0f);
		this.addSkin.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.addSkin.addTextParameter(new GuiTextParameterTextCenterBox());
		this.addChild(this.addSkin);

		this.skins = new GuiSpinnerEditor();
		this.skins.setHint("Skins...");
		this.skins.setBox(1 / 3.0f, 0.65f, 1 / 3.0f, 0.05f, 0);
		this.addChild(this.skins);

		this.removeSkin = new GuiButton();
		this.removeSkin.setText("Remove");
		this.removeSkin.setBox(2 * 1 / 3.0f, 0.65f, 1 / 3.0f, 0.05f, 0.0f);
		this.removeSkin.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.removeSkin.addTextParameter(new GuiTextParameterTextCenterBox());
		this.addChild(this.removeSkin);

		// colors
		this.addColor = new GuiButton();
		this.addColor.setText("Add");
		this.addColor.setBox(0.0f, 0.60f, 1 / 3.0f, 0.05f, 0.0f);
		this.addColor.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.addColor.addTextParameter(new GuiTextParameterTextCenterBox());
		this.addChild(this.addColor);

		this.colors = new GuiSpinnerEditor();
		this.colors.setHint("Colors...");
		this.colors.setBox(1 / 3.0f, 0.60f, 1 / 3.0f, 0.05f, 0);
		this.addChild(this.colors);

		this.removeColor = new GuiButton();
		this.removeColor.setText("Remove");
		this.removeColor.setBox(2 * 1 / 3.0f, 0.60f, 1 / 3.0f, 0.05f, 0.0f);
		this.removeColor.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.removeColor.addTextParameter(new GuiTextParameterTextCenterBox());
		this.addChild(this.removeColor);
	}

	private final void onPixelPerFaceChanged() {
		// TODO
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getTitle() {
		return ("Skinning");
	}

}
