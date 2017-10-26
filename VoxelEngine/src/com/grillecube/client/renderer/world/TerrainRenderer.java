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

package com.grillecube.client.renderer.world;

import java.util.ArrayList;
import java.util.Comparator;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.GLTexture;
import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.renderer.Renderer;
import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.client.renderer.camera.CameraView;
import com.grillecube.client.resources.BlockRendererManager;
import com.grillecube.common.Taskable;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.VoxelEngine.Callable;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.WorldFlat;
import com.grillecube.common.world.terrain.Terrain;

public class TerrainRenderer extends Renderer {

	/** rendering program */
	private ProgramTerrain terrainProgram;

	public TerrainRenderer(MainRenderer mainRenderer) {
		super(mainRenderer);
	}

	@Override
	public void initialize() {
		this.terrainProgram = new ProgramTerrain();
	}

	@Override
	public void deinitialize() {
		GLH.glhDeleteObject(this.terrainProgram);
		this.terrainProgram = null;
	}

	private final void bindTextureAtlas(TerrainMesh mesh, CameraView camera) {

		float distance = (float) Vector3f.distance(camera.getPosition(), mesh.getTerrain().getWorldPosCenter());
		BlockRendererManager manager = this.getMainRenderer().getResourceManager().getBlockTextureManager();
		GLTexture texture = null;

		if (distance < Terrain.SIZE_DIAGONAL3 * 1) {
			texture = manager.getTextureAtlas(BlockRendererManager.RESOLUTION_16x16);
		} else if (distance < Terrain.SIZE_DIAGONAL3 * 2.0f) {
			texture = manager.getTextureAtlas(BlockRendererManager.RESOLUTION_8x8);
		} else if (distance < Terrain.SIZE_DIAGONAL3 * 3.0f) {
			texture = manager.getTextureAtlas(BlockRendererManager.RESOLUTION_4x4);
		} else if (distance < Terrain.SIZE_DIAGONAL3 * 4.0f) {
			texture = manager.getTextureAtlas(BlockRendererManager.RESOLUTION_2x2);
		} else {
			texture = manager.getTextureAtlas(BlockRendererManager.RESOLUTION_1x1);
		}

		if (texture != null) {
			texture.bind(GL13.GL_TEXTURE0, GL11.GL_TEXTURE_2D);
		}

	}

	public void render(CameraProjective camera, WorldFlat world, ArrayList<TerrainMesh> meshes) {

		if (meshes == null || meshes.size() == 0) {
			return;
		}

		meshes.sort(new Comparator<TerrainMesh>() {
			@Override
			public int compare(TerrainMesh t1, TerrainMesh t2) {
				double d1 = Vector3f.distanceSquare(camera.getPosition(), t1.getTerrain().getWorldPosCenter());
				double d2 = Vector3f.distanceSquare(camera.getPosition(), t2.getTerrain().getWorldPosCenter());
				return ((int) (d2 - d1));
			}
		});

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		if (this.getMainRenderer().getGLFWWindow().isKeyPressed(GLFW.GLFW_KEY_F)) {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		}

		this.terrainProgram.useStart();
		{
			this.terrainProgram.loadUniforms(camera, world);

			// bind textures
			for (TerrainMesh mesh : meshes) {
				if (mesh.getVertexCount() == 0 || !mesh.isInitialized()) {
					continue;
				}
				this.bindTextureAtlas(mesh, camera);
				this.terrainProgram.loadInstanceUniforms(mesh);
				mesh.bind();
				mesh.draw();
			}
		}
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_CULL_FACE);

	}

	@Override
	public void getTasks(VoxelEngine engine, ArrayList<Callable<Taskable>> tasks) {
	}
}