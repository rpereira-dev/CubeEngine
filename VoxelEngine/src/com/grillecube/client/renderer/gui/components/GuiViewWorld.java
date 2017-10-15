package com.grillecube.client.renderer.gui.components;

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.client.opengl.GLH;
import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.renderer.MainRenderer.GLTask;
import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.event.GuiEventAspectRatio;
import com.grillecube.client.renderer.gui.event.GuiListener;
import com.grillecube.client.renderer.world.WorldRenderer;
import com.grillecube.client.renderer.world.flat.WorldFlatRenderer;
import com.grillecube.common.world.WorldFlat;

public class GuiViewWorld extends GuiView {

	private final GuiTexture txWorld;
	private WorldRenderer<WorldFlat> worldRenderer;
	private CameraProjective camera;
	private int worldID;

	public GuiViewWorld() {
		super();
		this.txWorld = new GuiTexture();
		this.txWorld.setHoverable(false);
		this.txWorld.setBox(0, 0, 1, 1, 0);
	}

	public GuiViewWorld(CameraProjective camera, int worldID) {
		this();
		this.set(camera, worldID);
	}

	public final void set(CameraProjective camera, int worldID) {
		this.camera = camera;
		this.worldID = worldID;
	}

	@Override
	protected void onInitialized(GuiRenderer renderer) {
		MainRenderer mainRenderer = renderer.getMainRenderer();

		this.worldRenderer = new WorldFlatRenderer(mainRenderer);
		this.worldRenderer.initialize();
		this.worldRenderer.setCamera(this.camera);
		this.worldRenderer.setWorld(this.worldID);
		mainRenderer.addRenderer(this.worldRenderer);

		this.txWorld.setTexture(this.worldRenderer.getFBOTexture(), 0.0f, 0.0f, 1.0f, 1.0f);
		this.addChild(this.txWorld);

		this.addListener(new GuiListener<GuiEventAspectRatio<Gui>>() {
			@Override
			public void invoke(GuiEventAspectRatio<Gui> event) {
				VoxelEngineClient.instance().addGLTask(new GLTask() {
					@Override
					public void run() {
						worldRenderer.resizeFbo();
					}
				});
			}
		});
	}

	/** deinitialize the gui: this function is call in opengl main thread */
	protected void onDeinitialized(GuiRenderer renderer) {
		this.worldRenderer.deinitialize();
	}

	@Override
	protected void onUpdate() {
		if (this.worldRenderer == null) {
			return;
		}
		this.worldRenderer.getCamera().update();

		// TODO update this by setting world renderer viewport
		float aspect = GLH.glhGetWindow().getAspectRatio() * super.getTotalAspectRatio();
		this.worldRenderer.getCamera().setAspect(aspect);
	}

	public final WorldRenderer getWorldRenderer() {
		return (this.worldRenderer);
	}
}
