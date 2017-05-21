package com.grillecube.client.renderer.world;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.client.renderer.camera.CameraProjectiveWorld;
import com.grillecube.common.Taskable;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.world.World;

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
	public void getTasks(VoxelEngine engine, ArrayList<VoxelEngine.Callable<Taskable>> tasks, World world,
			CameraProjectiveWorld camera) {
	}

}
