package com.grillecube.engine.renderer.camera;

import com.grillecube.engine.Logger;
import com.grillecube.engine.maths.Vector3f;
import com.grillecube.engine.maths.Vector3i;
import com.grillecube.engine.maths.Vector4f;
import com.grillecube.engine.opengl.GLFWWindow;
import com.grillecube.engine.renderer.model.BoundingBox;
import com.grillecube.engine.world.Terrain;
import com.grillecube.engine.world.World;
import com.grillecube.engine.world.block.Block;

public abstract class CameraProjectiveWorld extends CameraProjective implements RaycastingCallback {

	/** the world */
	private World _world;

	/** looking block informations */
	private Vector3f _look_cube;
	private Vector3f _look_face;
	private BoundingBox _look_box;

	/** the terrain index the camera is inside */
	private Vector3i _world_index;

	public CameraProjectiveWorld(GLFWWindow window) {
		super(window);

		this._look_cube = new Vector3f();
		this._look_face = new Vector3f();
		this._look_box = new BoundingBox();
		this._look_box.setMinSize(new Vector3f(), new Vector3f(1, 1, 1));
		this._look_box.setColor(new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));

		this._world_index = new Vector3i();
	}

	public Vector3i getWorldIndex() {
		return (this._world_index);
	}

	@Override
	public void update() {
		super.update();
		if (this.getWorld() == null) {
			return ;
		}
		this.getWorld().getTerrainIndex(this.getPosition(), this._world_index);
		Raycasting.raycast(this.getPosition(), this.getViewVector(), 128.0f, this);
	}

	@Override
	public boolean onRaycastCoordinates(float x, float y, float z, Vector3f face) {
		if (this._world == null) {
			return (true);
		}
		Block block = this._world.getBlock(x, y, z);
		if (block.isVisible()) {
			this._look_cube.set(x + face.x, y + face.y, z + face.z);
			this._look_face.set(face);
			this._look_box.setMin(this._look_cube);
			return (true);
		}
		return (false);
	}

	public void setWorld(World world) {
		this._world = world;
	}

	public World getWorld() {
		return (this._world);
	}

	public void setBlock(Block block, Vector3f pos) {

		if (this.getWorld() == null) {
			return;
		}

		Terrain terrain = this.getWorld().setBlock(block, pos.x, pos.y, pos.z);

		if (terrain == null) {
			return;
		}

		terrain.requestUpdateAll();
	}

	/** return the height of the last liquid seen */
	public Vector3f getLookFace() {
		return (this._look_face);
	}

	public Vector3f getLookCoords() {
		return (this._look_cube);
	}

	public BoundingBox getLookBoundingBox() {
		return (this._look_box);
	}
}
