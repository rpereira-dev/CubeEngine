package com.grillecube.client.renderer.world;

import java.util.ArrayList;

import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.renderer.Renderer;
import com.grillecube.client.renderer.factories.RendererFactory;
import com.grillecube.common.Logger;
import com.grillecube.common.Taskable;
import com.grillecube.common.VoxelEngine;

public abstract class RendererFactorized extends Renderer {

	private ArrayList<RendererFactory> factories;

	public RendererFactorized(MainRenderer mainRenderer) {
		super(mainRenderer);
		this.factories = new ArrayList<RendererFactory>();
	}

	@Override
	public final void initialize() {
		Logger.get().log(Logger.Level.DEBUG, "Initializing " + this.getClass().getSimpleName());
		this.onInitialized();
		for (RendererFactory factory : this.factories) {
			factory.initialize();
		}
	}

	protected abstract void onInitialized();

	@Override
	public final void deinitialize() {
		this.onDeinitialized();
		for (RendererFactory factory : this.factories) {
			factory.deinitialize();
		}
		this.factories = null;
	}

	protected abstract void onDeinitialized();

	public final void addFactory(RendererFactory factory) {
		this.factories.add(factory);
	}

	public final void addFactory(RendererFactory factory, int index) {
		this.factories.add(index, factory);
	}

	@Override
	public void render() {
		for (RendererFactory factory : this.factories) {
			factory.render();
		}
	}

	@Override
	public void getTasks(VoxelEngine engine, ArrayList<VoxelEngine.Callable<Taskable>> tasks) {
		for (RendererFactory factory : this.factories) {
			tasks.add(engine.new Callable<Taskable>() {
				@Override
				public Taskable call() throws Exception {
					factory.update();
					return (RendererFactorized.this);
				}

				@Override
				public String getName() {
					return (factory.getClass().getSimpleName() + " update");
				}
			});
		}
	}
}
