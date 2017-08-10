package com.pot.common.world;

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.client.renderer.world.terrain.TerrainMesh;
import com.grillecube.common.defaultmod.Blocks;
import com.grillecube.common.faces.Face;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.SimplexNoiseOctave;
import com.grillecube.common.world.World;
import com.grillecube.common.world.terrain.Terrain;

public class ViewMainMenuWorld extends World {

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
						public void preGenerated() {

							final SimplexNoiseOctave octave = new SimplexNoiseOctave();

							for (int x = 0; x < Terrain.DIM; x++) {
								for (int y = 0; y < Terrain.DIM; y++) {
									for (int z = 0; z < Terrain.DIM; z++) {

										double noisex = this.getWorldPos().x;
										double noisey = this.getWorldPos().y + 1024;
										double noisez = this.getWorldPos().z;

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
