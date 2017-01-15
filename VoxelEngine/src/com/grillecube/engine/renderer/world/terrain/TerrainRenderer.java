/**
**	This file is part of the project https://github.com/toss-dev/VoxelEngine
**
**	License is available here: https://raw.githubusercontent.com/toss-dev/VoxelEngine/master/LICENSE.md
**
**	PEREIRA Romain
**                                       4-----7          
**                                      /|    /|
**                                     0-----3 |
**                                     | 5___|_6
**                                     |/    | /
**                                     1-----2
*/

package com.grillecube.engine.renderer.world.terrain;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import com.grillecube.engine.Taskable;
import com.grillecube.engine.VoxelEngine;
import com.grillecube.engine.maths.Vector3f;
import com.grillecube.engine.opengl.GLH;
import com.grillecube.engine.opengl.object.GLTexture;
import com.grillecube.engine.renderer.MainRenderer;
import com.grillecube.engine.renderer.camera.CameraProjectiveWorld;
import com.grillecube.engine.renderer.camera.CameraView;
import com.grillecube.engine.renderer.world.RendererWorld;
import com.grillecube.engine.renderer.world.ShadowCamera;
import com.grillecube.engine.resources.BlockManager;
import com.grillecube.engine.world.World;

public class TerrainRenderer extends RendererWorld {
	/** rendering program */
	private ProgramTerrain _terrain_program;
	private ProgramTerrainReflectionRefraction _terrain_reflection_refraction_program;
	private ProgramTerrainShadow _terrain_shadow_program;

	/** terrains */
	private TerrainRendererFactory _factory;

	private ArrayList<TerrainMesh> _meshes_camera;
	private ArrayList<TerrainMesh> _meshes_shadow;

	/** texture atlas (blocks) */
	private boolean _can_render;

	public TerrainRenderer(MainRenderer main_renderer) {
		super(main_renderer);
	}

	@Override
	public void initialize() {
		this._terrain_program = new ProgramTerrain();
		this._terrain_reflection_refraction_program = new ProgramTerrainReflectionRefraction();
		this._terrain_shadow_program = new ProgramTerrainShadow();
		this._factory = new TerrainRendererFactory();
		this._factory.initialize();
	}

	@Override
	public void deinitialize() {

		GLH.glhDeleteObject(this._terrain_program);
		this._terrain_program = null;

		GLH.glhDeleteObject(this._terrain_reflection_refraction_program);
		this._terrain_reflection_refraction_program = null;

		GLH.glhDeleteObject(this._terrain_shadow_program);
		this._terrain_shadow_program = null;

		this._factory.deinitialize();
	}

	@Override
	public void onWorldSet(World world) {
		this._factory.onWorldSet(world);
	}

	@Override
	public void onWorldUnset(World world) {
		this._factory.onWorldUnset(world);
	}

	@Override
	public void preRender() {
		this._meshes_camera = this._factory.getCameraRenderingList();
		this._meshes_shadow = this._factory.getShadowRenderingList();
		this._can_render = this._meshes_camera != null && this._meshes_camera.size() > 0;
	}

	@Override
	public void render() {

		if (!this._can_render) {
			return;
		}

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		if (this.getParent().getGLFWWindow().isKeyPressed(GLFW.GLFW_KEY_F)) {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		}

		this.render(super.getCamera());

		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_CULL_FACE);
	}

	public void bindTextureAtlas(TerrainMesh mesh, CameraView camera) {

		float distance = (float) Vector3f.distanceSquare(mesh.getTerrain().getCenter(), camera.getPosition());
		BlockManager manager = this.getParent().getResourceManager().getBlockManager();
		GLTexture texture = null;

		if (distance < (1 << 10)) {
			texture = manager.getTextureAtlas(BlockManager.RESOLUTION_16x16);
		} else if (distance < (1 << 12)) {
			texture = manager.getTextureAtlas(BlockManager.RESOLUTION_8x8);
		} else if (distance < (1 << 14)) {
			texture = manager.getTextureAtlas(BlockManager.RESOLUTION_4x4);
		} else if (distance < (1 << 16)) {
			texture = manager.getTextureAtlas(BlockManager.RESOLUTION_2x2);
		} else {
			texture = manager.getTextureAtlas(BlockManager.RESOLUTION_1x1);
		}

		if (texture != null) {
			texture.bind(GL13.GL_TEXTURE0, GL11.GL_TEXTURE_2D);
		}

	}

	public void render(CameraView camera) {

		if (!this._can_render) {
			return;
		}

		GL11.glEnable(GL11.GL_DEPTH_TEST);

		this._terrain_program.useStart();
		{
			// render with the current camera, the current world weather, no
			// clipping, using reflection and refraction fbos as texture
			this._terrain_program.loadUniforms(this.getCamera(), this.getWorld(),
					this.getWorldRenderer().getShadowCamera());

			// bind textures
			this.getParent().getWorldRenderer().getShadowMap().bind(GL13.GL_TEXTURE1, GL11.GL_TEXTURE_2D);

			for (TerrainMesh mesh : this._meshes_camera) {
				mesh.preRender();

				if (mesh.getVertexCount() <= 0) {
					continue;
				}
				this.bindTextureAtlas(mesh, camera);
				this._terrain_program.loadInstanceUniforms(mesh);
				mesh.render();
			}
		}
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}

	@Override
	public void renderShadow(ShadowCamera shadow_camera) {

		// terrain do not cast shadow for now

		if (!this._can_render) {
			return;
		}

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_CULL_FACE);

		this._terrain_shadow_program.useStart();
		{
			this._terrain_shadow_program.loadUniforms(shadow_camera);

			// for (TerrainMesh mesh : this._meshes_shadow) {
			for (TerrainMesh mesh : this._meshes_shadow) {
				this._terrain_shadow_program.loadInstanceUniforms(mesh);
				mesh.render();
			}
		}
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}

	@Override
	public void getTasks(VoxelEngine engine, ArrayList<VoxelEngine.Callable<Taskable>> tasks, World world,
			CameraProjectiveWorld camera) {

		tasks.add(engine.new Callable<Taskable>() {

			@Override
			public Taskable call() {
				_factory.update(TerrainRenderer.this);
				return (TerrainRenderer.this);
			}

			@Override
			public String getName() {
				return ("TerrainRenderer factory update");
			}
		});
	}

	public TerrainRendererFactory getTerrainFactory() {
		return (this._factory);
	}
}
