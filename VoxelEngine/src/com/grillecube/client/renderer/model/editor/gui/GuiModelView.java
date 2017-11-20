package com.grillecube.client.renderer.model.editor.gui;

import java.util.ArrayList;

import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.components.Gui;
import com.grillecube.client.renderer.gui.components.GuiViewDebug;
import com.grillecube.client.renderer.gui.components.GuiViewWorld;
import com.grillecube.client.renderer.gui.event.GuiEventKeyPress;
import com.grillecube.client.renderer.gui.event.GuiEventMouseLeftPress;
import com.grillecube.client.renderer.gui.event.GuiEventMouseLeftRelease;
import com.grillecube.client.renderer.gui.event.GuiEventMouseMove;
import com.grillecube.client.renderer.gui.event.GuiEventMouseRightPress;
import com.grillecube.client.renderer.gui.event.GuiEventMouseRightRelease;
import com.grillecube.client.renderer.gui.event.GuiEventMouseScroll;
import com.grillecube.client.renderer.gui.event.GuiListener;
import com.grillecube.client.renderer.model.EditableModel;
import com.grillecube.client.renderer.model.editor.ModelEditorCamera;
import com.grillecube.client.renderer.model.editor.ModelEditorMod;
import com.grillecube.client.renderer.model.editor.camera.CameraController;
import com.grillecube.client.renderer.model.editor.camera.CameraControllerDefault;
import com.grillecube.client.renderer.model.editor.gui.toolbox.GuiToolbox;
import com.grillecube.client.renderer.model.instance.ModelInstance;
import com.grillecube.client.renderer.world.WorldRenderer;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.world.World;
import com.grillecube.common.world.WorldFlat;

/** the gui which displays the model */
public class GuiModelView extends Gui {

	private ArrayList<ModelInstance> modelInstances;
	private GuiViewWorld guiViewWorld;
	private CameraController cameraController;

	public GuiModelView() {
		super();
		this.modelInstances = new ArrayList<ModelInstance>();
	}

	@Override
	protected void onInitialized(GuiRenderer renderer) {
		int worldID = ModelEditorMod.WORLD_ID;
		CameraProjective camera = new ModelEditorCamera(renderer.getMainRenderer().getGLFWWindow());

		this.guiViewWorld = new GuiViewWorld();
		this.guiViewWorld.setHoverable(false);
		this.guiViewWorld.set(camera, worldID);
		this.guiViewWorld.initialize(renderer);

		this.cameraController = new CameraControllerDefault(this);

		this.addChild(this.guiViewWorld);
		this.addChild(new GuiViewDebug(camera));

		this.addListener(Gui.ON_HOVERED_FOCUS_LISTENER);
		this.addListener(new GuiListener<GuiEventKeyPress<GuiModelView>>() {
			@Override
			public void invoke(GuiEventKeyPress<GuiModelView> event) {
				cameraController.onKeyPress(event);
			}
		});
		this.addListener(new GuiListener<GuiEventMouseLeftPress<GuiModelView>>() {
			@Override
			public void invoke(GuiEventMouseLeftPress<GuiModelView> event) {
				cameraController.onLeftPressed();
			}
		});

		this.addListener(new GuiListener<GuiEventMouseLeftRelease<GuiModelView>>() {
			@Override
			public void invoke(GuiEventMouseLeftRelease<GuiModelView> event) {
				cameraController.onLeftReleased();
			}
		});
		this.addListener(new GuiListener<GuiEventMouseRightPress<GuiModelView>>() {
			@Override
			public void invoke(GuiEventMouseRightPress<GuiModelView> event) {
				cameraController.onRightPressed();
			}
		});

		this.addListener(new GuiListener<GuiEventMouseRightRelease<GuiModelView>>() {
			@Override
			public void invoke(GuiEventMouseRightRelease<GuiModelView> event) {
				cameraController.onRightReleased();
			}
		});

		this.addListener(new GuiListener<GuiEventMouseMove<GuiModelView>>() {
			@Override
			public void invoke(GuiEventMouseMove<GuiModelView> event) {
				cameraController.onMouseMove();
			}
		});

		this.addListener(new GuiListener<GuiEventMouseScroll<GuiModelView>>() {
			@Override
			public void invoke(GuiEventMouseScroll<GuiModelView> event) {
				cameraController.onMouseScroll(event);
			}
		});
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.updateModelInstances();
		this.cameraController.update();
		if (this.cameraController.requestedPanelRefresh()) {
			this.getToolbox().refresh();
		}
	}

	private final void updateModelInstances() {
		for (ModelInstance modelInstance : this.modelInstances) {
			modelInstance.getEntity().update(VoxelEngine.instance().getTimer().getDt());
			modelInstance.update();
		}
	}

	public final GuiToolbox getToolbox() {
		return (((GuiModelEditor) this.getParent()).getToolbox());
	}

	public final ModelInstance getSelectedModelInstance() {
		return (((GuiModelEditor) this.getParent()).getSelectedModelInstance());
	}

	public final EditableModel getSelectedModel() {
		return (((GuiModelEditor) this.getParent()).getSelectedModel());
	}

	public final GuiViewWorld getGuiViewWorld() {
		return (this.guiViewWorld);
	}

	public final ModelEditorCamera getCamera() {
		return ((ModelEditorCamera) this.guiViewWorld.getWorldRenderer().getCamera());
	}

	public final World getWorld() {
		return (this.guiViewWorld.getWorldRenderer().getWorld());
	}

	public final void addModelInstance(ModelInstance modelInstance) {
		this.modelInstances.add(modelInstance);
		this.guiViewWorld.getWorldRenderer().getModelRendererFactory().addModelInstance(modelInstance);
	}

	public final void removeModelInstance(ModelInstance modelInstance) {
		this.modelInstances.remove(modelInstance);
		this.guiViewWorld.getWorldRenderer().getModelRendererFactory().removeModelInstance(modelInstance);
	}

	public final WorldRenderer<WorldFlat> getWorldRenderer() {
		return (this.guiViewWorld.getWorldRenderer());
	}
}
