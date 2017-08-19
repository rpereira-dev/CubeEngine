package com.grillecube.client.renderer.model.editor.gui;

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.components.Gui;
import com.grillecube.client.renderer.gui.components.GuiButton;
import com.grillecube.client.renderer.gui.components.GuiLabel;
import com.grillecube.client.renderer.gui.components.GuiPopUp;
import com.grillecube.client.renderer.gui.components.GuiPrompt;
import com.grillecube.client.renderer.gui.components.GuiSliderBar;
import com.grillecube.client.renderer.gui.components.GuiSpinner;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextAlignLeft;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextCenterBox;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextFillBox;
import com.grillecube.client.renderer.gui.listeners.GuiListenerMouseLeftPress;
import com.grillecube.client.renderer.gui.listeners.GuiSliderBarListenerValueChanged;
import com.grillecube.client.renderer.gui.listeners.GuiSpinnerListenerAdd;
import com.grillecube.client.renderer.gui.listeners.GuiSpinnerListenerPick;
import com.grillecube.client.renderer.model.ModelInitializer;
import com.grillecube.client.renderer.model.editor.mesher.EditableModel;
import com.grillecube.client.renderer.model.editor.mesher.ModelMesher;
import com.grillecube.client.renderer.model.editor.mesher.ModelMesherCull;
import com.grillecube.client.renderer.model.instance.ModelInstance;
import com.grillecube.client.renderer.model.json.JSONEditableModelInitializer;
import com.grillecube.client.resources.ModelManager;
import com.grillecube.client.resources.ResourceManagerClient;
import com.grillecube.common.Logger;
import com.grillecube.common.resources.R;
import com.grillecube.common.world.World;
import com.grillecube.common.world.entity.Entity;

/** a view which handles model creation */
public class GuiToolboxViewModels extends GuiToolboxView {

	/** new model button */
	private final GuiLabel title;
	private final GuiButton newModelButton;
	private final GuiSpinner modelList;
	private final GuiPrompt modelName;
	private final GuiSliderBarEditor modelBlockSizeUnit;

	public GuiToolboxViewModels() {
		super();

		// the title
		this.title = new GuiLabel();
		this.title.setBox(0, 0.9f, 1, 0.1f, 0);
		this.title.setText("Model Editor");
		this.title.setFontColor(0, 0, 0, 1.0f);
		this.title.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.title.addTextParameter(new GuiTextParameterTextCenterBox());
		this.addChild(this.title);

		// new model button
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

		// spinner list
		this.modelList = new GuiSpinnerEditor();
		this.modelList.addListener(new GuiSpinnerListenerAdd() {
			@Override
			public void invokeSpinnerAddObject(GuiSpinner guiSpinner, int index) {
				modelList.pick(index);
			}
		});
		this.modelList.addListener(new GuiSpinnerListenerPick() {
			@Override
			public void invokeSpinnerIndexPick(GuiSpinner guiSpinner, int index) {
				onModelInstanceChanged();
			}
		});
		this.modelList.setBox(0, 0.80f, 1.0f, 0.05f, 0.0f);

		ModelInstance modelInstance = newModel();
		this.modelList.add(modelInstance, modelInstance.getModel().getName());
		this.addChild(this.modelList);

		// model name
		this.modelName = new GuiPrompt();
		this.modelName.setBox(0, 0.4f, 1, 0.1f, 0);
		this.modelName.setHeldTextColor(0, 0, 0, 1.0f);
		this.modelName.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.modelName.addTextParameter(new GuiTextParameterTextAlignLeft(0.1f));
		this.addChild(this.modelName);

		// block size unit slider bar
		this.modelBlockSizeUnit = new GuiSliderBarEditor();
		this.modelBlockSizeUnit.setBox(0, 0.7f, 1.0f, 0.05f, 0);
		this.modelBlockSizeUnit.addValues(0.125f, 0.250f, 0.5f, 1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f, 9.0f,
				10.0f, 11.0f, 12.0f, 13.0f, 14.0f, 15.0f, 16.0f);
		this.modelBlockSizeUnit.addListener(new GuiSliderBarListenerValueChanged() {
			@Override
			public void invokeSliderBarValueChanged(GuiSliderBar guiSliderBar, int selectedIndex, Object value) {
				onBlockSizeUnitChanged();
			}
		});
		this.modelBlockSizeUnit.select((Object) 1.0f);
		this.addChild(this.modelBlockSizeUnit);
	}

	private final void onBlockSizeUnitChanged() {
		EditableModel model = this.getSelectedModel();
		model.setBlockSizeUnit((float) this.modelBlockSizeUnit.getSelectedValue());
		model.generate();
	}

	private final void onModelInstanceChanged() {
		ModelManager manager = VoxelEngineClient.instance().getResourceManager().getModelManager();
		// TODO : set every other entities invisible
	}

	private final ModelInstance getSelectedModelInstance() {
		return ((ModelInstance) (this.modelList.getSelectedObject()));
	}

	private final EditableModel getSelectedModel() {
		return ((EditableModel) (this.getSelectedModelInstance().getModel()));
	}

	private final ModelInstance newModel() {
		VoxelEngineClient engine = VoxelEngineClient.instance();
		World world = engine.getWorld();
		ResourceManagerClient manager = engine.getResourceManager();
		Entity entity = new Entity() {
			@Override
			protected void onUpdate() {
			}
		};
		world.spawnEntity(entity);

		entity.setPosition(0.0f, 16.0f, 0.0f);

		// String path = GuiRenderer.dialogSelectFolder("Import an existing
		// model", R.getResPath("models/"));
		String path = R.getResPath("models/bipedJSON");
		ModelInitializer initializer = new JSONEditableModelInitializer(path);
		EditableModel editableModel = new EditableModel(initializer);
		try {
			editableModel.initialize();
		} catch (Exception e) {
			Logger.get().log(Logger.Level.ERROR, "Error when parsing model", path);
			e.printStackTrace(Logger.get().getPrintStream());
			editableModel.deinitialize();
			return (null);
		}

		ModelMesher modelMesher = new ModelMesherCull();
		modelMesher.generate(editableModel);

		ModelInstance modelInstance = new ModelInstance(editableModel, entity);
		return (modelInstance);
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
