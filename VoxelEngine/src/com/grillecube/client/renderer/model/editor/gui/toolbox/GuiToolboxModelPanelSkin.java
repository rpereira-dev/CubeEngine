package com.grillecube.client.renderer.model.editor.gui.toolbox;

import java.awt.image.BufferedImage;

import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.components.Gui;
import com.grillecube.client.renderer.gui.components.GuiButton;
import com.grillecube.client.renderer.gui.components.GuiPrompt;
import com.grillecube.client.renderer.gui.components.GuiSliderBar;
import com.grillecube.client.renderer.gui.components.GuiTexture;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextCenterBox;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextFillBox;
import com.grillecube.client.renderer.gui.event.GuiEventClick;
import com.grillecube.client.renderer.gui.event.GuiEventMouseHover;
import com.grillecube.client.renderer.gui.event.GuiListener;
import com.grillecube.client.renderer.gui.event.GuiSliderBarEventValueChanged;
import com.grillecube.client.renderer.gui.event.GuiTextureEventTextureChanged;
import com.grillecube.client.renderer.model.ModelSkin;
import com.grillecube.client.renderer.model.editor.gui.GuiSliderBarEditor;
import com.grillecube.client.renderer.model.editor.gui.GuiSpinnerColor;
import com.grillecube.client.renderer.model.editor.gui.GuiSpinnerEditor;
import com.grillecube.common.maths.Vector4f;

public class GuiToolboxModelPanelSkin extends GuiToolboxModelPanel {

	/** the model block size unit slider bar */
	private final GuiSliderBarEditor pixelPerLine;

	private final GuiButton addSkin;
	private final GuiSpinnerEditor skins;
	private final GuiButton removeSkin;

	private final GuiSpinnerEditor tools;

	private final GuiButton addColor;
	private final GuiSpinnerColor colors;
	private final GuiButton removeColor;

	private final GuiTexture skinPreview;

	private final GuiPrompt skinName;

	private float windowAspectRatio;

	public GuiToolboxModelPanelSkin() {
		super();

		this.pixelPerLine = new GuiSliderBarEditor();

		this.addSkin = new GuiButton();
		this.skins = new GuiSpinnerEditor();
		this.removeSkin = new GuiButton();

		this.addColor = new GuiButton();
		this.colors = new GuiSpinnerColor();
		this.removeColor = new GuiButton();

		this.tools = new GuiSpinnerEditor();

		this.skinPreview = new GuiTexture();
		this.skinName = new GuiPrompt();
	}

	@Override
	public void onInitialized(GuiRenderer guiRenderer) {

		// number of pixels per face
		this.pixelPerLine.setBox(0, 0.70f, 1.0f, 0.05f, 0);
		this.pixelPerLine.addValuesArray(GuiSliderBar.intRange(1, 64));
		this.pixelPerLine.setPrefix("Pixel per face line: ");
		this.pixelPerLine.select((Object) ModelSkin.DEFAULT_PIXELS_PER_LINE);
		this.pixelPerLine.addListener(new GuiListener<GuiSliderBarEventValueChanged<GuiSliderBarEditor>>() {
			@Override
			public void invoke(GuiSliderBarEventValueChanged<GuiSliderBarEditor> event) {
				onPixelPerFaceChanged();
			}
		});
		this.addChild(this.pixelPerLine);

		// skins
		this.addSkin.setText("Add");
		this.addSkin.setBox(0.0f, 0.65f, 1 / 3.0f, 0.05f, 0.0f);
		this.addSkin.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.addSkin.addTextParameter(new GuiTextParameterTextCenterBox());
		this.addChild(this.addSkin);

		this.skins.setHint("Skins...");
		this.skins.setBox(1 / 3.0f, 0.65f, 1 / 3.0f, 0.05f, 0);
		for (ModelSkin skin : this.getModel().getSkins()) {
			this.skins.add(skin, skin.getName());
		}
		this.skins.pick(0);
		this.addChild(this.skins);

		this.removeSkin.setText("Remove");
		this.removeSkin.setBox(2 * 1 / 3.0f, 0.65f, 1 / 3.0f, 0.05f, 0.0f);
		this.removeSkin.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.removeSkin.addTextParameter(new GuiTextParameterTextCenterBox());
		this.addChild(this.removeSkin);

		// colors
		this.addColor.setText("Add");
		this.addColor.setBox(0.0f, 0.60f, 1 / 3.0f, 0.05f, 0.0f);
		this.addColor.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.addColor.addTextParameter(new GuiTextParameterTextCenterBox());
		this.addColor.addListener(new GuiListener<GuiEventClick<GuiButton>>() {
			@Override
			public void invoke(GuiEventClick<GuiButton> event) {
				Vector4f color = GuiRenderer.dialogPickColor();
				if (color == null) {
					return;
				}
				colors.add(color);
				colors.pick(colors.count() - 1);
				refresh();
			}
		});
		this.addChild(this.addColor);

		this.colors.setHint("Colors...");
		this.colors.setBox(1 / 3.0f, 0.60f, 1 / 3.0f, 0.05f, 0);
		this.colors.add(Gui.COLOR_BLUE);
		this.colors.add(Gui.COLOR_RED);
		this.colors.add(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
		this.colors.pick(0);
		this.addChild(this.colors);

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

		// tools
		this.tools.setHint("Tools...");
		this.tools.setBox(0, 0.55f, 1, 0.05f, 0);
		this.tools.add("Paint");
		this.tools.add("Fill surface");
		this.addChild(this.tools);

		// skin preview
		this.skinPreview.addListener(new GuiListener<GuiEventMouseHover<GuiTexture>>() {
			@Override
			public void invoke(GuiEventMouseHover<GuiTexture> event) {
				if (!event.getGui().isPressed()) {
					return;
				}
				BufferedImage data = skinPreview.getGLTexture().getData();
				Vector4f color = getSelectedColor();
				int r = (int) (color.x * 255.0f);
				int g = (int) (color.y * 255.0f);
				int b = (int) (color.z * 255.0f);
				int a = (int) (color.w * 255.0f);
				int rgb = (a << 24 | r << 16 | g << 8 | b << 0);
				int px = (int) (data.getWidth() * event.getGui().getMouseX());
				int py = (int) (data.getHeight() * event.getGui().getMouseY());
				if (px < 0 || py < 0 || px >= data.getWidth() || py >= data.getHeight() || data.getRGB(px, py) == rgb) {
					return;
				}
				data.setRGB(px, py, rgb);
				skinPreview.getGLTexture().setData(data);
			}
		});
		this.skinPreview.addListener(new GuiListener<GuiTextureEventTextureChanged<GuiTexture>>() {
			@Override
			public void invoke(GuiTextureEventTextureChanged<GuiTexture> event) {
				resize();
			}
		});
		this.addChild(this.skinPreview);

		this.skinName.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.skinName.addTextParameter(new GuiTextParameterTextCenterBox());
		this.skinName.setHeldTextColor(0, 0, 0, 1.0f);
		this.skinName.setHintColor(0.5f, 0.5f, 0.5f, 1.0f);
		this.skinName.setHint("Enter skin name...");
		this.addChild(this.skinName);

		this.windowAspectRatio = guiRenderer.getMainRenderer().getGLFWWindow().getAspectRatio();
		this.refresh();
		this.resize();

	}

	public final ModelSkin getSelectedSkin() {
		return ((ModelSkin) this.skins.getPickedObject());
	}

	private final void onPixelPerFaceChanged() {
		this.getSelectedSkin().setPixelsPerLine((int) this.pixelPerLine.getSelectedValue());
	}

	@Override
	public void onWindowResized(int width, int height, float windowAspectRatio) {
		this.windowAspectRatio = windowAspectRatio;
		this.resize();
	}

	private void resize() {
		float imgRatio = this.skinPreview.getGLTexture() == null ? 1.0f
				: this.skinPreview.getGLTexture().getWidth() / (float) this.skinPreview.getGLTexture().getHeight();
		float width, height;
		float ratio = super.getTotalAspectRatio() * this.windowAspectRatio / imgRatio;
		height = 0.4f;
		width = height / ratio;
		if (width >= 0.8f) {
			width = 0.8f;
			height = width * ratio;
		}
		float marginX = (1.0f - width) / 2.0f;
		float marginY = 0.55f - 0.05f - height;
		this.skinPreview.setBox(marginX, marginY, width, height, 0);
		this.skinName.setBox(marginX, marginY - 0.07f, width, 0.05f, 0);
	}

	@Override
	public void refresh() {
		this.removeColor.setEnabled(this.colors.count() > 0);
		this.removeSkin.setEnabled(this.skins.count() > 1);
		if (this.getSelectedSkin() != null) {
			this.skinPreview.setTexture(this.getSelectedSkin().getGLTexture(), 0, 0, 1, 1);
		}
	}

	@Override
	public String getTitle() {
		return ("Skinning");
	}

	public final Vector4f getSelectedColor() {
		return ((Vector4f) (this.colors.getPickedObject()));
	}

}
