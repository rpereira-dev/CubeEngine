package com.grillecube.engine.renderer.world;

import java.util.ArrayList;

import com.grillecube.engine.Taskable;
import com.grillecube.engine.VoxelEngine;
import com.grillecube.engine.renderer.MainRenderer;
import com.grillecube.engine.renderer.Renderer;
import com.grillecube.engine.renderer.camera.CameraProjectiveWorld;
import com.grillecube.engine.world.World;

/** a world renderer utilitaries abstract class */
public abstract class RendererWorld extends Renderer {

	private int _state = 0;

	public RendererWorld(MainRenderer renderer) {
		super(renderer);
	}

	public WorldRenderer getWorldRenderer() {
		return (this.getParent().getWorldRenderer());
	}

	@Override
	public void preRender() {
	}

	@Override
	public void postRender() {
	}

	/** render to the final image */
	public abstract void render();

	public abstract void renderShadow(ShadowCamera shadow_camera);

	public World getWorld() {
		return (this.getParent().getWorldRenderer().getWorld());
	}

	public CameraProjectiveWorld getCamera() {
		return (this.getParent().getCamera());
	}

	public boolean hasState(int state) {
		return ((this._state & state) == state);
	}

	public void setState(int state) {
		this._state = this._state | state;
	}

	public void unsetState(int state) {
		this._state = this._state & ~(state);
	}

	public void switchState(int state) {
		this._state = this._state ^ state;
	}

	/** called when the given world is set (non null) */
	public abstract void onWorldSet(World world);

	/** called when the given world is unset (non null) */
	public abstract void onWorldUnset(World world);

	public abstract void getTasks(VoxelEngine engine, ArrayList<VoxelEngine.Callable<Taskable>> tasks, World world,
			CameraProjectiveWorld camera);

	@Override
	public void getTasks(VoxelEngine engine, ArrayList<VoxelEngine.Callable<Taskable>> tasks) {
		this.getTasks(engine, tasks, this.getWorld(), this.getCamera());
	}
}
