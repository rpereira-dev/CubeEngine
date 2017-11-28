package com.grillecube.client.renderer.model.editor.gui.toolbox;

import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.components.Gui;
import com.grillecube.client.renderer.gui.components.GuiButton;
import com.grillecube.client.renderer.gui.components.GuiColoredQuad;
import com.grillecube.client.renderer.gui.components.GuiLabel;
import com.grillecube.client.renderer.gui.components.GuiSpinner;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextCenterBox;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextCenterXBox;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextFillBox;
import com.grillecube.client.renderer.gui.event.GuiEventClick;
import com.grillecube.client.renderer.gui.event.GuiListener;
import com.grillecube.client.renderer.gui.event.GuiSpinnerEventPick;
import com.grillecube.client.renderer.model.ModelSkin;
import com.grillecube.client.renderer.model.editor.gui.GuiModelEditor;
import com.grillecube.client.renderer.model.editor.gui.GuiSpinnerEditor;
import com.grillecube.client.renderer.model.editor.mesher.EditableModel;
import com.grillecube.client.renderer.model.instance.ModelInstance;
import com.grillecube.client.renderer.model.json.JSONEditableModelInitializer;
import com.grillecube.client.renderer.model.json.JSONModelExporter;
import com.grillecube.client.renderer.model.json.JSONModelInitializer;
import com.grillecube.common.Logger;
import com.grillecube.common.resources.R;
import com.grillecube.common.utils.Color;
import com.grillecube.common.world.entity.Entity;

public class GuiToolbox extends Gui {

	/** the color back of the toolbox */
	private GuiColoredQuad bg;

	/** "Model Editor" */
	private final GuiLabel title;

	/** 3 buttons */
	private final GuiButton newModelButton;
	private final GuiButton importModelButton;
	private final GuiButton saveModelButton;

	/** the model list */
	private final GuiSpinner modelList;

	public GuiToolbox() {
		super();

		this.addListener(Gui.ON_HOVERED_FOCUS_LISTENER);

		// background
		this.bg = new GuiColoredQuad();
		this.bg.setBox(0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
		this.bg.setColor(0.95f, 0.95f, 0.95f, 1.0f);
		this.addChild(this.bg);

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
		this.newModelButton.setBox(0, 0.85f, 1 / 3.0f, 0.05f, 0);
		this.newModelButton.setText("New");
		this.newModelButton.addTextParameter(new GuiTextParameterTextCenterXBox());
		this.addChild(this.newModelButton);
		this.newModelButton.addListener(new GuiListener<GuiEventClick<GuiButton>>() {
			@Override
			public void invoke(GuiEventClick<GuiButton> event) {
				createNewModel();
			}
		});

		// import model button
		this.importModelButton = new GuiButton();
		this.importModelButton.setBox(1 / 3.0f, 0.85f, 1 / 3.0f, 0.05f, 0);
		this.importModelButton.setText("Import");
		this.importModelButton.addTextParameter(new GuiTextParameterTextCenterXBox());
		this.addChild(this.importModelButton);
		this.importModelButton.addListener(new GuiListener<GuiEventClick<GuiButton>>() {
			@Override
			public void invoke(GuiEventClick<GuiButton> event) {
				importModel();
			}
		});

		// save model button
		this.saveModelButton = new GuiButton();
		this.saveModelButton.setBox(2 * 1 / 3.0f, 0.85f, 1 / 3.0f, 0.05f, 0);
		this.saveModelButton.setText("Save");
		this.saveModelButton.addTextParameter(new GuiTextParameterTextCenterXBox());
		this.addChild(this.saveModelButton);
		this.saveModelButton.addListener(new GuiListener<GuiEventClick<GuiButton>>() {
			@Override
			public void invoke(GuiEventClick<GuiButton> event) {
				saveCurrentModel();
			}
		});

		// spinner list
		this.modelList = new GuiSpinnerEditor();
		this.modelList.setBox(0, 0.80f, 1.0f, 0.05f, 0.0f);
		this.modelList.setHint("Model list ...");
		this.modelList.addListener(new GuiListener<GuiSpinnerEventPick<GuiSpinnerEditor>>() {
			@Override
			public void invoke(GuiSpinnerEventPick<GuiSpinnerEditor> event) {
				GuiToolboxModel prevPanel = (GuiToolboxModel) event.getPrevPickedObject();
				GuiToolboxModel newPanel = (GuiToolboxModel) event.getPickedObject();
				if (prevPanel != newPanel) {
					onPickedModelChanged(prevPanel, newPanel);
				}
			}
		});
		this.addChild(this.modelList);
	}

	private final void saveCurrentModel() {
		try {
			EditableModel model = this.getSelectedModel();
			String defaultPath;
			if (model.getInitializer() instanceof JSONModelInitializer) {
				defaultPath = ((JSONModelInitializer) model.getInitializer()).getDirpath();
			} else {
				defaultPath = R.getResPath("models/");
			}
			String path = GuiRenderer.dialogSelectFolder("Save model", defaultPath);
			if (path != null) {
				JSONModelExporter.export(model, path);
			}
			Logger.get().log(Logger.Level.FINE, "model saved properly at", path);
		} catch (Exception e) {
			Logger.get().log(Logger.Level.ERROR, "error when exporting model", e.getMessage());
		}
	}

	private final void createNewModel() {
		this.importModel(R.getResPath("models/defaultJSON"));
	}

	private final void importModel() {
		String path = GuiRenderer.dialogSelectFolder("EditableModel path", R.getResPath("models/"));
		if (path == null) {
			return;
		}
		this.importModel(path);
	}

	private final void importModel(String absolutePath) {
		Entity entity = new Entity() {
			@Override
			public void update(double dt) {
			}

			@Override
			protected void onUpdate(double dt) {
			}
		};

		((GuiModelEditor) this.getParent()).getModelView().getWorld().spawnEntity(entity);
		EditableModel editableModel = new EditableModel(new JSONEditableModelInitializer(absolutePath));
		try {
			editableModel.initialize();
			editableModel.requestMeshUpdate();
		} catch (Exception e) {
			Logger.get().log(Logger.Level.ERROR, "Error when parsing model", absolutePath);
			e.printStackTrace(Logger.get().getPrintStream());
			editableModel.deinitialize();
			return;
		}

		ModelInstance modelInstance = new ModelInstance(editableModel, entity);
		this.addModelInstance(modelInstance);

	}

	private final void addModelInstance(ModelInstance modelInstance) {
		GuiToolboxModel guiToolboxModel = new GuiToolboxModel(modelInstance);
		guiToolboxModel.setBox(0, 0.0f, 1.0f, 1.0f, 0);
		this.modelList.add(guiToolboxModel, modelInstance.getModel().getName());
		this.addTask(new GuiTask() {
			@Override
			public final boolean run() {
				modelList.pick(modelList.count() - 1);
				return (true);
			}
		});
		((GuiModelEditor) this.getParent()).onModelInstanceAdded(modelInstance);
	}

	private final void onPickedModelChanged(GuiToolboxModel prevPanel, GuiToolboxModel newPanel) {
		this.addTask(new GuiTask() {
			@Override
			public final boolean run() {
				if (prevPanel != null) {
					removeChild(prevPanel);
				}

				if (newPanel != null) {
					addChild(newPanel);
				}
				return (true);
			}
		});

		GuiModelEditor parent = (GuiModelEditor) this.getParent();
		ModelInstance prevModelInstance = prevPanel != null ? prevPanel.getModelInstance() : null;
		ModelInstance newModelInstance = newPanel != null ? newPanel.getModelInstance() : null;
		parent.onPickedModelChanged(prevModelInstance, newModelInstance);
	}

	public final ModelInstance getSelectedModelInstance() {
		return (this.modelList.getPickedObject() == null ? null
				: ((GuiToolboxModel) (this.modelList.getPickedObject())).getModelInstance());
	}

	public final EditableModel getSelectedModel() {
		ModelInstance modelInstance = this.getSelectedModelInstance();
		if (modelInstance == null) {
			return (null);
		}
		return ((EditableModel) (modelInstance.getModel()));
	}

	public final Color getSelectedColor() {
		return (this.modelList == null || this.modelList.getPickedObject() == null ? Color.BLACK
				: ((GuiToolboxModel) (this.modelList.getPickedObject())).getSelectedColor());
	}

	public final int getSelectedTool() {
		return (this.modelList == null || this.modelList.getPickedObject() == null ? 0
				: ((GuiToolboxModel) (this.modelList.getPickedObject())).getSelectedTool());
	}

	public final ModelSkin getSelectedSkin() {
		return (this.modelList == null || this.modelList.getPickedObject() == null ? null
				: ((GuiToolboxModel) (this.modelList.getPickedObject())).getSelectedSkin());
	}

	public GuiToolboxModel getSelectedModelPanels() {
		return (this.modelList != null ? (GuiToolboxModel) this.modelList.getPickedObject() : null);
	}

	public final void refresh() {
		GuiToolboxModel modelPanels = (GuiToolboxModel) this.modelList.getPickedObject();
		if (modelPanels == null) {
			return;
		}
		modelPanels.refresh();
	}
}
