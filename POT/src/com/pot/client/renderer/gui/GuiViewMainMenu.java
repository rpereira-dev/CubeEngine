package com.pot.client.renderer.gui;

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.client.opengl.object.GLProgramPostProcessing;
import com.grillecube.client.renderer.camera.CameraPerspectiveWorldCentered;
import com.grillecube.client.renderer.gui.Gui;
import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.animations.GuiAnimationTextAutoScale;
import com.grillecube.client.renderer.gui.animations.GuiAnimationTextHoverScale;
import com.grillecube.client.renderer.gui.components.GuiLabel;
import com.grillecube.client.renderer.gui.components.GuiView;
import com.grillecube.client.renderer.world.terrain.TerrainMesh;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.defaultmod.Blocks;
import com.grillecube.common.faces.Face;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.resources.R;
import com.grillecube.common.world.SimplexNoiseOctave;
import com.grillecube.common.world.World;
import com.grillecube.common.world.terrain.Terrain;

public class GuiViewMainMenu extends GuiView {

	/** the background world */
	private World _world;

	/** the blur program effect */
	private GLProgramPostProcessing _program;

	private GuiLabel _lbl_version;

	private GuiLabel _lbl_play;
	// private GuiButton _button_play;
	private GuiLabel _lbl_options;

	public GuiViewMainMenu() {
		super();
	}

	@Override
	public void onAdded(GuiRenderer renderer) {
		this._lbl_version = new GuiLabel();
		this._lbl_version.setFontColor(Gui.COLOR_BLUE);
		this._lbl_version.setFontSize(0.5f, 0.5f);
		this._lbl_version.setPosition(-1, 1);
		this._lbl_version.setText("Version: " + VoxelEngine.VERSION);
		this._lbl_version.addParameters(GuiLabel.PARAM_AUTO_ADJUST_RECT);
		super.addGui(this._lbl_version);

		// this._button_play = new GuiButton("button_default");
		// this._button_play.addParameter(GuiLabel.PARAM_CENTER);
		// this._button_play.addParameter(GuiButton.PARAM_TEXT_FILL_BUTTON);
		// this._button_play.addParameter(GuiButton.PARAM_TEXT_CENTER_BUTTON);
		// this._button_play.setCenter(0, 0.4f);
		// this._button_play.setSize(0.5f, 0.2f);
		// this._button_play.setText("Ima button");
		// super.addGui(this._button_play);

		this._lbl_play = new GuiLabel();
		this._lbl_play.setFontColor(Gui.COLOR_BLACK);
		this._lbl_play.setFontSize(3.0f, 3.0f);
		this._lbl_play.setText(R.getWord("play"));
		this._lbl_play.setCenter(0.0f, 0.4f);
		this._lbl_play.addParameters(GuiLabel.PARAM_AUTO_ADJUST_RECT);
		this._lbl_play.addParameters(GuiLabel.PARAM_CENTER);
		this._lbl_play.startAnimation(new GuiAnimationTextHoverScale<GuiLabel>(1.1f));
		this._lbl_play.startAnimation(new GuiAnimationTextAutoScale<GuiLabel>(0.01f));
		super.addGui(this._lbl_play);

		this._lbl_options = new GuiLabel();
		this._lbl_options.setFontColor(Gui.COLOR_BLACK);
		this._lbl_options.setFontSize(3.0f, 3.0f);
		this._lbl_options.setCenter(0.0f, -0.1f);
		this._lbl_options.setText(R.getWord("options"));
		this._lbl_options.addParameters(GuiLabel.PARAM_AUTO_ADJUST_RECT);
		this._lbl_options.addParameters(GuiLabel.PARAM_CENTER);
		this._lbl_options.startAnimation(new GuiAnimationTextHoverScale<GuiLabel>(1.1f));
		super.addGui(this._lbl_options);

		renderer.getParent().getGLFWWindow().setCursor(true);
		this.setBackgroundWorld();
	}

	private void setBackgroundWorld() {
		VoxelEngineClient engine = VoxelEngineClient.instance();
		CameraPerspectiveWorldCentered cam = new CameraPerspectiveWorldCentered(engine.getGLFWWindow()) {
			@Override
			public void update() {
				super.update();
				super.increaseAngleAroundCenter(0.1f);

			}
		};
		cam.setPitch(0.0f);
		cam.setDistanceFromCenter(90.0f);

		this._world = new ViewMainMenuWorld();
		engine.getRenderer().setCamera(cam);
		engine.setWorld(this._world);
		this._program = new GLProgramPostProcessing(R.getResPath("shaders/post_process/blurhv.fs"));
		engine.getRenderer().setPostProcessingProgram(this._program);
	}

	@Override
	public void onRemoved(GuiRenderer renderer) {
		this._world.delete();
		this._program.delete();
	}

}

class ViewMainMenuWorld extends World {

	private float rotspeedx = 0.005f;
	private float rotspeedy = 0.005f;
	private float rotspeedz = 0.005f;
	private float scalespeed = 0.001f;
	private int step = 1;
	private float maxscale = 1.5f;

	protected void update() {
		TerrainMesh[] meshes = VoxelEngineClient.instance().getRenderer().getWorldRenderer().getTerrainRenderer()
				.getTerrainFactory().getMeshes();
		for (TerrainMesh mesh : meshes) {
			if (mesh == null) {
				continue;
			}
			Vector3f rot = mesh.getRotation();
			mesh.setRotation(rot.x + 1 * rotspeedx, rot.y + 1 * rotspeedy, rot.z + 1 * rotspeedz);

			Vector3f scale = mesh.getScale();
			if (scale.x > maxscale) {
				step = -1;
			} else if (scale.x < 1.0f / maxscale) {
				step = 1;
			}
			scale.x += scalespeed * step;
			scale.y += scalespeed * step;
			scale.z += scalespeed * step;
		}
	}

	@Override
	public String getName() {
		return ("Main menu world");
	}

	@Override
	public void onSet() {
		this.generate();
	}

	private void generate() {
		for (int x = -1; x < 1; x++) {
			for (int y = -1; y < 1; y++) {
				for (int z = -1; z < 1; z++) {
					this.spawnTerrain(new Terrain(x, y, z) {

						@Override
						public void onGenerated() {

							final SimplexNoiseOctave octave = new SimplexNoiseOctave();

							for (int x = 0; x < Terrain.DIM; x++) {
								for (int y = 0; y < Terrain.DIM; y++) {
									for (int z = 0; z < Terrain.DIM; z++) {

										double noisex = this.getWorldPosition().x;
										double noisey = this.getWorldPosition().y + 1024;
										double noisez = this.getWorldPosition().z;

										noisex += x * Terrain.BLOCK_SIZE;
										noisey += y * Terrain.BLOCK_SIZE;
										noisez += z * Terrain.BLOCK_SIZE;

										float weight = 16.0f;

										double d = octave.noise(noisex / (weight * Terrain.BLOCK_SIZE),
												noisey / (weight * Terrain.BLOCK_SIZE),
												noisez / (weight * Terrain.BLOCK_SIZE));

										if (d < 0.2f) {
											this.setBlockAt(Blocks.STONE, x, y, z);
										} else {
											this.setBlockAt(Blocks.AIR, x, y, z);
										}
									}
								}
							}

							for (int x = 0; x < Terrain.DIM; x++) {
								for (int y = 0; y < Terrain.DIM; y++) {
									for (int z = 0; z < Terrain.DIM; z++) {
										if (this.getBlockAt(x, y, z) != Blocks.AIR && ((y < Terrain.DIM - 1
												&& this.getBlockAt(x, y + 1, z) == Blocks.AIR)
												|| (y == Terrain.DIM - 1 && this.getNeighbor(Face.TOP) == null))) {
											this.setBlock(Blocks.GRASS, x, y, z);
										}
									}
								}
							}
						}
					});
				}
			}
		}
	}

	@Override
	public void onUnset() {
	}
}
