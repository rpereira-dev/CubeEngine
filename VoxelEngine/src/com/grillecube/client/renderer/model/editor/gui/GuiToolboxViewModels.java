package com.grillecube.client.renderer.model.editor.gui;

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.components.Gui;
import com.grillecube.client.renderer.gui.components.GuiButton;
import com.grillecube.client.renderer.gui.components.GuiLabel;
import com.grillecube.client.renderer.gui.components.GuiPopUp;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextCenterBox;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextFillBox;
import com.grillecube.client.renderer.gui.listeners.GuiListenerMouseLeftPress;
import com.grillecube.client.renderer.model.ModelInitializer;
import com.grillecube.client.renderer.model.editor.gui.components.GuiSpinnerEditor;
import com.grillecube.client.renderer.model.editor.mesher.EditableModel;
import com.grillecube.client.renderer.model.editor.mesher.ModelMesher;
import com.grillecube.client.renderer.model.editor.mesher.ModelMesherCull;
import com.grillecube.client.renderer.model.instance.ModelInstance;
import com.grillecube.client.renderer.model.json.JSONEditableModelInitializer;
import com.grillecube.client.resources.ResourceManagerClient;
import com.grillecube.common.resources.R;
import com.grillecube.common.world.World;
import com.grillecube.common.world.entity.Entity;

/** a view which handles model creation */
public class GuiToolboxViewModels extends GuiToolboxView {

	/** new model button */
	private GuiLabel title;
	private GuiButton newModelButton;

	public GuiToolboxViewModels() {
		super();

		this.title = new GuiLabel();
		this.title.setBox(0, 0.9f, 1, 0.1f, 0);
		this.title.setText("Model Editor");
		this.title.setFontColor(0, 0, 0, 1.0f);
		this.title.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.title.addTextParameter(new GuiTextParameterTextCenterBox());
		this.addChild(this.title);

		this.newModelButton = new GuiButton();
		this.newModelButton.setBox(0, 0.85f, 1, 0.05f, 0);
		this.newModelButton.setText("New model");
		this.addChild(this.newModelButton);

		this.newModelButton.addListener(new GuiListenerMouseLeftPress<GuiButton>() {

			@Override
			public void invokeMouseLeftPress(GuiButton gui, double mousex, double mousey) {
				GuiPopUp modelPreview = new GuiPopUp();

				// TODO make this cleaner
				getParent().getParent().getChildren().get(1).addChild(modelPreview);
			}
		});

		GuiSpinnerEditor guiSpinner = new GuiSpinnerEditor();
		guiSpinner.setBox(0, 0.80f, 1.0f, 0.05f, 0.0f);
		guiSpinner.add("a");
		guiSpinner.add("b");
		guiSpinner.add("c");
		guiSpinner.add("d");
		guiSpinner.add("e");
		guiSpinner.add("f");
		guiSpinner.add("g");
		this.addChild(guiSpinner);
	}

	@Override
	protected void onInitialized(GuiRenderer renderer) {
	}

	private final void newModel(VoxelEngineClient engine) {
		World world = engine.getWorld();
		ResourceManagerClient manager = engine.getResourceManager();
		Entity entity = new Entity() {
			@Override
			protected void onUpdate() {
			}
		};
		world.spawnEntity(entity);

		entity.setPosition(0.0f, 16.0f, 0.0f);

		ModelInitializer initializer = new JSONEditableModelInitializer(R.getResPath("models/bipedJSON/"));
		EditableModel editableModel = new EditableModel(initializer);
		editableModel.initialize();

		ModelMesher modelMesher = new ModelMesherCull();
		modelMesher.generate(editableModel);

		ModelInstance modelInstance = new ModelInstance(editableModel, entity);
		manager.getModelManager().addModelInstance(modelInstance);
	}

	@Override
	protected void onDeinitialized(GuiRenderer renderer) {
	}

	@Override
	protected void onUpdate(float x, float y, boolean pressed) {
	}

	@Override
	public void onAddedTo(Gui gui) {
	}

	@Override
	public void onRemovedFrom(Gui gui) {
	}
}
