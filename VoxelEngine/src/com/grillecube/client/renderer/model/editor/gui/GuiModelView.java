package com.grillecube.client.renderer.model.editor.gui;

import java.util.ArrayList;

import com.grillecube.client.renderer.camera.CameraDestinationCenter;
import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.components.Gui;
import com.grillecube.client.renderer.gui.components.GuiButton;
import com.grillecube.client.renderer.gui.components.GuiViewDebug;
import com.grillecube.client.renderer.gui.components.GuiViewWorld;
import com.grillecube.client.renderer.gui.event.GuiEventKeyPress;
import com.grillecube.client.renderer.gui.event.GuiEventMouseLeftPress;
import com.grillecube.client.renderer.gui.event.GuiEventMouseLeftRelease;
import com.grillecube.client.renderer.gui.event.GuiEventMouseMove;
import com.grillecube.client.renderer.gui.event.GuiEventMouseRightPress;
import com.grillecube.client.renderer.gui.event.GuiEventMouseRightRelease;
import com.grillecube.client.renderer.gui.event.GuiEventMouseScroll;
import com.grillecube.client.renderer.gui.event.GuiEventPress;
import com.grillecube.client.renderer.gui.event.GuiListener;
import com.grillecube.client.renderer.model.ModelSkin;
import com.grillecube.client.renderer.model.editor.ModelEditorMod;
import com.grillecube.client.renderer.model.editor.camera.ModelEditorCamera;
import com.grillecube.client.renderer.model.editor.gui.toolbox.GuiToolbox;
import com.grillecube.client.renderer.model.editor.mesher.EditableModel;
import com.grillecube.client.renderer.model.editor.mesher.EditableModelLayer;
import com.grillecube.client.renderer.model.instance.ModelInstance;
import com.grillecube.client.renderer.world.WorldRenderer;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.maths.Maths;
import com.grillecube.common.utils.Color;
import com.grillecube.common.world.World;
import com.grillecube.common.world.WorldFlat;

/** the gui which displays the model */
public class GuiModelView extends Gui {

	private ArrayList<ModelInstance> modelInstances;
	private GuiViewWorld guiViewWorld;
	private ModelEditorCamera camera;
	private GuiButton rotateX;
	private GuiButton rotateY;
	private GuiButton rotateZ;

	public GuiModelView() {
		super();
		this.modelInstances = new ArrayList<ModelInstance>();
	}

	@Override
	protected void onInitialized(GuiRenderer renderer) {
		int worldID = ModelEditorMod.WORLD_ID;

		this.camera = new ModelEditorCamera(renderer.getMainRenderer().getGLFWWindow());
		this.guiViewWorld = new GuiViewWorld();
		this.guiViewWorld.setHoverable(false);
		this.guiViewWorld.set(this.camera, worldID);
		this.guiViewWorld.initialize(renderer);
		this.addChild(this.guiViewWorld);

		this.camera.loadTools(this);
		this.addChild(new GuiViewDebug(camera));

		this.rotateX = new GuiButton();
		this.rotateX.setBox(0.85f, 0.95f, 0.05f, 0.05f, 0.0f);
		this.rotateX.setText("X");
		this.addChild(this.rotateX);
		this.rotateX.addListener(new GuiListener<GuiEventPress<GuiButton>>() {
			@Override
			public void invoke(GuiEventPress<GuiButton> event) {
				getCamera().setCenter(getSelectedModel().getOrigin());
				float phi = getCamera().getPhi() - getCamera().getPhi() % (2.0f * Maths.PI) - Maths.PI_2;
				float theta = getCamera().getTheta() - getCamera().getTheta() % (2.0f * Maths.PI);
				getCamera().addDestination(new CameraDestinationCenter(getCamera().getR(), phi, theta, 1));
			}
		});

		this.rotateY = new GuiButton();
		this.rotateY.setBox(0.90f, 0.95f, 0.05f, 0.05f, 0.0f);
		this.rotateY.setText("Y");
		this.addChild(this.rotateY);
		this.rotateY.addListener(new GuiListener<GuiEventPress<GuiButton>>() {
			@Override
			public void invoke(GuiEventPress<GuiButton> event) {
				getCamera().setCenter(getSelectedModel().getOrigin());
				float phi = getCamera().getPhi() - getCamera().getPhi() % (2.0f * Maths.PI);
				float theta = getCamera().getTheta() - getCamera().getTheta() % (2.0f * Maths.PI);
				getCamera().addDestination(new CameraDestinationCenter(getCamera().getR(), phi, theta, 1));
			}
		});

		this.rotateZ = new GuiButton();
		this.rotateZ.setBox(0.95f, 0.95f, 0.05f, 0.05f, 0.0f);
		this.rotateZ.setText("Z");
		this.addChild(this.rotateZ);
		this.rotateZ.addListener(new GuiListener<GuiEventPress<GuiButton>>() {
			@Override
			public void invoke(GuiEventPress<GuiButton> event) {
				getCamera().setCenter(getSelectedModel().getOrigin());
				float phi = getCamera().getPhi() - getCamera().getPhi() % (2.0f * Maths.PI) - Maths.PI_2;
				float theta = getCamera().getPhi() - getCamera().getPhi() % (2.0f * Maths.PI) + Maths.PI_2;
				getCamera().addDestination(new CameraDestinationCenter(getCamera().getR(), phi, theta, 1));
			}
		});

		this.addListener(Gui.ON_HOVERED_FOCUS_LISTENER);
		this.addListener(new GuiListener<GuiEventKeyPress<GuiModelView>>() {
			@Override
			public void invoke(GuiEventKeyPress<GuiModelView> event) {
				camera.onKeyPress(event);
			}
		});
		this.addListener(new GuiListener<GuiEventMouseLeftPress<GuiModelView>>() {
			@Override
			public void invoke(GuiEventMouseLeftPress<GuiModelView> event) {
				camera.onLeftPressed();
			}
		});

		this.addListener(new GuiListener<GuiEventMouseLeftRelease<GuiModelView>>() {
			@Override
			public void invoke(GuiEventMouseLeftRelease<GuiModelView> event) {
				camera.onLeftReleased();
			}
		});
		this.addListener(new GuiListener<GuiEventMouseRightPress<GuiModelView>>() {
			@Override
			public void invoke(GuiEventMouseRightPress<GuiModelView> event) {
				camera.onRightPressed();
			}
		});

		this.addListener(new GuiListener<GuiEventMouseRightRelease<GuiModelView>>() {
			@Override
			public void invoke(GuiEventMouseRightRelease<GuiModelView> event) {
				camera.onRightReleased();
			}
		});

		this.addListener(new GuiListener<GuiEventMouseMove<GuiModelView>>() {
			@Override
			public void invoke(GuiEventMouseMove<GuiModelView> event) {
				camera.onMouseMove();
			}
		});

		this.addListener(new GuiListener<GuiEventMouseScroll<GuiModelView>>() {
			@Override
			public void invoke(GuiEventMouseScroll<GuiModelView> event) {
				camera.onMouseScroll(event);
			}
		});
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.updateModelInstances();
		this.camera.setTool(this.getSelectedTool());
		this.camera.update();
	}

	private final int getSelectedTool() {
		return (((GuiModelEditor) this.getParent()).getToolbox().getSelectedTool());
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

	public final ModelSkin getSelectedSkin() {
		return (((GuiModelEditor) this.getParent()).getSelectedSkin());
	}

	public final EditableModel getSelectedModel() {
		return (((GuiModelEditor) this.getParent()).getSelectedModel());
	}

	public final Color getSelectedColor() {
		return (((GuiModelEditor) this.getParent()).getSelectedColor());
	}

	public final EditableModelLayer getSelectedModelLayer() {
		return (((GuiModelEditor) this.getParent()).getSelectedModelLayer());
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
