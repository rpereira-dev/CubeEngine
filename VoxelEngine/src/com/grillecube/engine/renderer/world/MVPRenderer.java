package com.grillecube.engine.renderer.world;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.grillecube.engine.Taskable;
import com.grillecube.engine.VoxelEngine;
import com.grillecube.engine.opengl.GLH;
import com.grillecube.engine.renderer.MainRenderer;
import com.grillecube.engine.renderer.camera.CameraProjective;
import com.grillecube.engine.renderer.camera.CameraProjectiveWorld;
import com.grillecube.engine.world.World;

public class MVPRenderer extends RendererWorld {
	private ProgramMVP _program;
	private ArrayList<MVPObject> _objects;

	public MVPRenderer(MainRenderer main_renderer) {
		super(main_renderer);
	}

	@Override
	public void initialize() {
		this._objects = new ArrayList<MVPObject>();
		this._program = new ProgramMVP();
	}

	@Override
	public void deinitialize() {

		this._objects = null;

		GLH.glhDeleteObject(this._program);
		this._program = null;
	}

	@Override
	public void onWorldSet(World world) {
	}

	@Override
	public void onWorldUnset(World world) {
	}

	/** add an object to the mvp renderer */
	public void addMVPObject(MVPObject object) {
		this._objects.add(object);
	}

	/** remove an object to the mvp renderer */
	public void removeMVPObject(MVPObject object) {
		this._objects.remove(object);
	}

	@Override
	public void render() {
		this.render(super.getCamera());
	}

	private void render(CameraProjective camera) {

		if (this._objects.size() == 0) {
			return;
		}

		GL11.glEnable(GL11.GL_DEPTH_TEST);

		this._program.useStart();
		this._program.loadUniforms(this.getWorld(), camera);

		for (MVPObject object : this._objects) {
			this._program.loadUniforms(object);
			object.render();
		}
		this._program.useStop();

		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}

	@Override
	public void renderShadow(ShadowCamera shadow_camera) {
		// TODO
	}

	@Override
	public void getTasks(VoxelEngine engine, ArrayList<VoxelEngine.Callable<Taskable>> tasks, World world,
			CameraProjectiveWorld camera) {
	}

}
