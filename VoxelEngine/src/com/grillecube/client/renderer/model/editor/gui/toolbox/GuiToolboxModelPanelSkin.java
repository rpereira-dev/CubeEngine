package com.grillecube.client.renderer.model.editor.gui.toolbox;

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.components.Gui;
import com.grillecube.client.renderer.gui.components.GuiButton;
import com.grillecube.client.renderer.gui.components.GuiPrompt;
import com.grillecube.client.renderer.gui.components.GuiTexture;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextCenterBox;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextFillBox;
import com.grillecube.client.renderer.gui.event.GuiEventClick;
import com.grillecube.client.renderer.gui.event.GuiListener;
import com.grillecube.client.renderer.gui.event.GuiPromptEventHeldTextChanged;
import com.grillecube.client.renderer.gui.event.GuiSpinnerEventPick;
import com.grillecube.client.renderer.gui.event.GuiTextureEventTextureChanged;
import com.grillecube.client.renderer.model.ModelSkin;
import com.grillecube.client.renderer.model.editor.gui.GuiPopUpCallback;
import com.grillecube.client.renderer.model.editor.gui.GuiSpinnerColor;
import com.grillecube.client.renderer.model.editor.gui.GuiSpinnerEditor;
import com.grillecube.client.renderer.model.editor.gui.GuiWindowNewSkin;
import com.grillecube.common.utils.Color;

public class GuiToolboxModelPanelSkin extends GuiToolboxModelPanel {

	private final GuiButton addSkin;
	private final GuiSpinnerEditor skins;
	private final GuiButton removeSkin;

	private final GuiButton addColor;
	private final GuiSpinnerColor colors;
	private final GuiButton removeColor;

	private final GuiTexture skinPreview;
	private final GuiPrompt skinName;

	private float windowAspectRatio;

	public GuiToolboxModelPanelSkin() {
		super();

		this.addSkin = new GuiButton();
		this.skins = new GuiSpinnerEditor();
		this.removeSkin = new GuiButton();

		this.addColor = new GuiButton();
		this.colors = new GuiSpinnerColor();
		this.removeColor = new GuiButton();

		this.skinPreview = new GuiTexture();
		this.skinName = new GuiPrompt();
	}

	@Override
	public void onInitialized(GuiRenderer guiRenderer) {

		// skins
		this.addSkin.setText("Add");
		this.addSkin.setBox(0.0f, 0.70f, 1 / 3.0f, 0.05f, 0.0f);
		this.addSkin.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.addSkin.addTextParameter(new GuiTextParameterTextCenterBox());
		this.addChild(this.addSkin);
		this.addSkin.addListener(new GuiListener<GuiEventClick<GuiButton>>() {
			@Override
			public void invoke(GuiEventClick<GuiButton> event) {

				new GuiWindowNewSkin(new GuiPopUpCallback<GuiWindowNewSkin>() {

					@Override
					public void onConfirm(GuiWindowNewSkin popUp) {
						String skinName = popUp.name.asString();
						ModelSkin skin = new ModelSkin(skinName);
						getSelectedModel().addSkin(skin);
						getSelectedModel().requestMeshUpdate();
						skins.add(skin, skin.getName());
						skins.pick(skins.count() - 1);
						VoxelEngineClient.instance().getRenderer().getGuiRenderer().toast("Skin added");
					}

					@Override
					public void onCancel(GuiWindowNewSkin popUp) {
					}

				}).open(getOldestParent());

				refresh();
			}
		});

		this.skins.setHint("Skins...");
		this.skins.setBox(1 / 3.0f, 0.70f, 1 / 3.0f, 0.05f, 0);
		for (ModelSkin skin : this.getSelectedModel().getSkins()) {
			this.skins.add(skin);
		}
		this.skins.pick(0);
		this.skins.addListener(new GuiListener<GuiSpinnerEventPick<GuiSpinnerEditor>>() {
			@Override
			public void invoke(GuiSpinnerEventPick<GuiSpinnerEditor> event) {
				getSelectedModelInstance().toggleSkin(event.getPickedIndex());
			}

		});
		this.addChild(this.skins);

		this.removeSkin.setText("Remove");
		this.removeSkin.setBox(2 * 1 / 3.0f, 0.70f, 1 / 3.0f, 0.05f, 0.0f);
		this.removeSkin.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.removeSkin.addTextParameter(new GuiTextParameterTextCenterBox());
		this.addChild(this.removeSkin);

		// colors
		this.addColor.setText("Add");
		this.addColor.setBox(0.0f, 0.65f, 1 / 3.0f, 0.05f, 0.0f);
		this.addColor.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.addColor.addTextParameter(new GuiTextParameterTextCenterBox());
		this.addColor.addListener(new GuiListener<GuiEventClick<GuiButton>>() {
			@Override
			public void invoke(GuiEventClick<GuiButton> event) {
				Color color = GuiRenderer.dialogPickColor();
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
		this.colors.setBox(1 / 3.0f, 0.65f, 1 / 3.0f, 0.05f, 0);
		this.colors.add(Gui.COLOR_BLUE);
		this.colors.add(Gui.COLOR_RED);
		this.colors.add(Gui.COLOR_WHITE);
		this.colors.pick(0);
		this.addChild(this.colors);

		this.removeColor.setText("Remove");
		this.removeColor.setBox(2 * 1 / 3.0f, 0.65f, 1 / 3.0f, 0.05f, 0.0f);
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

		// skin preview
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
		this.skinName.addListener(new GuiListener<GuiPromptEventHeldTextChanged<GuiPrompt>>() {
			@Override
			public void invoke(GuiPromptEventHeldTextChanged<GuiPrompt> event) {
				ModelSkin skin = getSelectedSkin();
				if (skin == null) {
					return;
				}
				skin.setName(event.getGui().getHeldText());
				skins.setName(skin, skin.getName());
			}
		});

		this.windowAspectRatio = guiRenderer.getMainRenderer().getGLFWWindow().getAspectRatio();
		this.refresh();
		this.resize();

	}

	public final ModelSkin getSelectedSkin() {
		return ((ModelSkin) this.skins.getPickedObject());
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
			this.skinName.setHeldText(this.getSelectedSkin().getName());
			this.skinPreview.setVisible(true);
			this.skinName.setVisible(true);
		} else {
			this.skinPreview.setVisible(false);
			this.skinName.setVisible(false);
		}
	}

	@Override
	public String getTitle() {
		return ("Skinning");
	}

	public final Color getSelectedColor() {
		return ((Color) (this.colors.getPickedObject()));
	}

}
