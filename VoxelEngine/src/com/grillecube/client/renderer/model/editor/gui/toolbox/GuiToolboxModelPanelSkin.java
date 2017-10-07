package com.grillecube.client.renderer.model.editor.gui.toolbox;

import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.components.Gui;
import com.grillecube.client.renderer.gui.components.GuiButton;
import com.grillecube.client.renderer.gui.components.GuiTexture;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextCenterBox;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextFillBox;
import com.grillecube.client.renderer.gui.event.GuiEventClick;
import com.grillecube.client.renderer.gui.event.GuiListener;
import com.grillecube.client.renderer.gui.event.GuiSliderBarEventValueChanged;
import com.grillecube.client.renderer.model.editor.gui.GuiSliderBarEditor;
import com.grillecube.client.renderer.model.editor.gui.GuiSpinnerColor;
import com.grillecube.client.renderer.model.editor.gui.GuiSpinnerEditor;
import com.grillecube.common.maths.Vector4f;

public class GuiToolboxModelPanelSkin extends GuiToolboxModelPanel {

	/** the model block size unit slider bar */
	private final GuiSliderBarEditor pixelPerFace;

	private final GuiButton addSkin;
	private final GuiSpinnerEditor skins;
	private final GuiButton removeSkin;

	private final GuiSpinnerEditor tools;
	
	private final GuiButton addColor;
	private final GuiSpinnerColor colors;
	private final GuiButton removeColor;

	private final GuiTexture skinPreview;

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
		this.addColor.addListener(new GuiListener<GuiEventClick<GuiButton>>() {
			@Override
			public void invoke(GuiEventClick<GuiButton> event) {
				colors.add(GuiRenderer.dialogPickColor());
				colors.pick(colors.count() - 1);
				refresh();
			}
		});
		this.addChild(this.addColor);

		this.colors = new GuiSpinnerColor();
		this.colors.setHint("Colors...");
		this.colors.setBox(1 / 3.0f, 0.60f, 1 / 3.0f, 0.05f, 0);
		this.colors.add(Gui.COLOR_BLUE);
		this.colors.add(Gui.COLOR_RED);
		this.colors.add(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
		this.colors.pick(0);
		this.addChild(this.colors);

		this.removeColor = new GuiButton();
		this.removeColor.setText("Remove");
		this.removeColor.setBox(2 * 1 / 3.0f, 0.60f, 1 / 3.0f, 0.05f, 0.0f);
		this.removeColor.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.removeColor.addTextParameter(new GuiTextParameterTextCenterBox());
		this.removeColor.addListener(new GuiListener<GuiEventClick<GuiButton>>() {
			@Override
			public void invoke(GuiEventClick<GuiButton> event) {
				int index = colors.getPickedIndex();
				colors.remove(index);
				colors.pick(colors.count() - 1);
				refresh();
			}
		});
		this.addChild(this.removeColor);
<<<<<<< HEAD

		// skin preview
		this.skinPreview = new GuiTexture();
		this.skinPreview.setBox(0.2f, 0.50f, 0.6f, 0.2f, 0.0f);
		this.addChild(this.skinPreview);
=======
		

		//tools
		this.tools = new GuiSpinnerEditor();
		this.tools.setHint("Tools...");
		this.tools.setBox(0, 0.55f, 1, 0.05f, 0);
		this.tools.add("Paint");
		this.tools.add("Fill surface");
		this.addChild(this.tools);
>>>>>>> 856eee9be42fb9b08ffd540060ce2285952a9054
	}

	private final void onPixelPerFaceChanged() {
		// TODO
	}

	@Override
	public void refresh() {
		this.removeColor.setEnabled(this.colors.count() > 0);
		this.removeSkin.setEnabled(this.skins.count() > 0);
	}

	@Override
	public String getTitle() {
		return ("Skinning");
	}

}
