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

package com.grillecube.client.renderer.world.terrain;

import java.util.ArrayList;
import java.util.Comparator;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.GLTexture;
import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.renderer.camera.CameraProjectiveWorld;
import com.grillecube.client.renderer.camera.CameraView;
import com.grillecube.client.renderer.world.RendererWorld;
import com.grillecube.client.resources.BlockRendererManager;
import com.grillecube.common.Taskable;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.World;

public class TerrainRenderer extends RendererWorld {
	/** rendering program */
	private ProgramTerrain terrainProgram;

	/** terrains */
	private TerrainRendererFactory factory;

	/** meshes to render */
	private ArrayList<TerrainMesh> meshes;

	/** texture atlas (blocks) */
	private boolean canRender;

	public TerrainRenderer(MainRenderer main_renderer) {
		super(main_renderer);
	}

	@Override
	public void initialize() {
		this.terrainProgram = new ProgramTerrain();
		this.factory = new TerrainRendererFactory();
		this.factory.initialize();
	}

	@Override
	public void deinitialize() {

		GLH.glhDeleteObject(this.terrainProgram);
		this.terrainProgram = null;

		this.factory.deinitialize();
	}

	@Override
	public void onWorldSet(World world) {
		this.factory.onWorldSet(world);
	}

	@Override
	public void onWorldUnset(World world) {
		this.factory.onWorldUnset(world);
	}

	@Override
	public void preRender() {
		this.meshes = this.factory.getCameraRenderingList();
		this.meshes.sort(new Comparator<TerrainMesh>() {

			@Override
			public int compare(TerrainMesh t1, TerrainMesh t2) {
				double d1 = Vector3f.distanceSquare(getCamera().getPosition(), t1.getTerrain().getWorldPosCenter());
				double d2 = Vector3f.distanceSquare(getCamera().getPosition(), t2.getTerrain().getWorldPosCenter());
				return ((int) (d2 - d1));
			}

		});
		this.canRender = this.meshes != null && this.meshes.size() > 0;
	}

	@Override
	public void render() {

		if (!this.canRender) {
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

		float distance = (float) Vector3f.distanceSquare(camera.getPosition(), mesh.getTerrain().getWorldPosCenter());
		BlockRendererManager manager = this.getParent().getResourceManager().getBlockTextureManager();
		GLTexture texture = null;

		if (distance < (1 << 10)) {
			texture = manager.getTextureAtlas(BlockRendererManager.RESOLUTION_16x16);
		} else if (distance < (1 << 12)) {
			texture = manager.getTextureAtlas(BlockRendererManager.RESOLUTION_8x8);
		} else if (distance < (1 << 14)) {
			texture = manager.getTextureAtlas(BlockRendererManager.RESOLUTION_4x4);
		} else if (distance < (1 << 16)) {
			texture = manager.getTextureAtlas(BlockRendererManager.RESOLUTION_2x2);
		} else {
			texture = manager.getTextureAtlas(BlockRendererManager.RESOLUTION_1x1);
		}

		if (texture != null) {
			texture.bind(GL13.GL_TEXTURE0, GL11.GL_TEXTURE_2D);
		}

	}

	public void render(CameraView camera) {

		if (!this.canRender) {
			return;
		}

		GL11.glEnable(GL11.GL_DEPTH_TEST);

		this.terrainProgram.useStart();
		{
			// render with the current camera, the current world weather, no
			// clipping, using reflection and refraction fbos as texture
			this.terrainProgram.loadUniforms(this.getCamera(), this.getWorld());

			// bind textures
			for (TerrainMesh mesh : this.meshes) {
				mesh.preRender();

				if (mesh.getVertexCount() <= 0) {
					continue;
				}
				this.bindTextureAtlas(mesh, camera);
				this.terrainProgram.loadInstanceUniforms(mesh);
				mesh.bind();
				mesh.draw();
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
				factory.update(TerrainRenderer.this);
				return (TerrainRenderer.this);
			}

			@Override
			public String getName() {
				return ("TerrainRenderer factory update");
			}
		});
	}

	public TerrainRendererFactory getTerrainFactory() {
		return (this.factory);
	}
}