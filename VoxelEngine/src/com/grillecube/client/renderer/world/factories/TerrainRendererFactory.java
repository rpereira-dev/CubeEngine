package com.grillecube.client.renderer.world.factories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.client.event.renderer.EventTerrainList;
import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.renderer.MainRenderer.GLTask;
import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.client.renderer.terrain.TerrainMesh;
import com.grillecube.client.renderer.terrain.TerrainMesher;
import com.grillecube.client.renderer.terrain.TerrainMesherGreedy;
import com.grillecube.client.renderer.terrain.TerrainRenderer;
import com.grillecube.client.renderer.world.WorldRenderer;
import com.grillecube.common.event.EventCallback;
import com.grillecube.common.event.world.EventWorldDespawnTerrain;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.resources.EventManager;
import com.grillecube.common.resources.ResourceManager;
import com.grillecube.common.world.terrain.Terrain;

/** a factory class which create terrain renderer lists */
public class TerrainRendererFactory extends WorldRendererFactory {

	/** array list of terrain to render */
	private HashMap<Terrain, TerrainMesh> meshes;

	/** the mesher */
	private TerrainMesher mesher;

	/** next rendering list */
	private ArrayList<TerrainMesh> renderingList;

	/** post rendernig list creation callback */
	private EventTerrainList eventTerrainList;

	public TerrainRendererFactory(WorldRenderer worldRenderer) {
		super(worldRenderer);

		this.meshes = new HashMap<Terrain, TerrainMesh>(8192);
		this.mesher = new TerrainMesherGreedy();
		// this.mesher = new TerrainMesherCull();
		this.renderingList = new ArrayList<TerrainMesh>();
		this.eventTerrainList = new EventTerrainList(this);

		EventManager eventManager = ResourceManager.instance().getEventManager();
		eventManager.registerEventCallback(new EventCallback<EventWorldDespawnTerrain>() {
			@Override
			public void invoke(EventWorldDespawnTerrain event) {
				Terrain terrain = event.getTerrain();
				TerrainMesh terrainMesh = getMesh(terrain);
				if (terrainMesh != null) {
					terrainMesh.deinitialize();
					meshes.remove(terrain);
				}
			}
		});
	}

	@Override
	public final void deinitialize() {
		/** destroy every currently set meshes */
		Collection<TerrainMesh> meshes = this.meshes.values();
		VoxelEngineClient.instance().addGLTask(new GLTask() {
			@Override
			public void run() {
				for (TerrainMesh mesh : meshes) {
					if (mesh != null) {
						mesh.deinitialize();
					}
				}
			}
		});
		this.meshes.clear();

	}

	/** get the mesh for the gven terrain */
	public final TerrainMesh getMesh(Terrain terrain) {
		return (this.meshes.get(terrain));
	}

	@Override
	public final void update() {
		this.updateLoadedMeshes();
		this.updateRenderingList();
		this.updateRenderingListMeshes();
		super.getResourceManager().getEventManager().invokeEvent(this.eventTerrainList);
	}

	/**
	 * update the meshes in the rendering list (so they can generate their mesh
	 * floats
	 */
	private final void updateRenderingListMeshes() {
		int i = 0;
		for (TerrainMesh mesh : this.meshes.values()) {
			if (mesh != null && mesh.update(this.mesher, this.getCamera())) {
				if (++i == 8) {
					break;
				}
			}
		}
	}

	private final void updateRenderingList() {
		this.renderingList.clear();
		for (TerrainMesh mesh : this.meshes.values()) {
			if (mesh == null) {
				continue;
			}

			if (isMeshInFrustum(mesh)) {
				this.renderingList.add(mesh);
			}
		}
	}

	private final boolean isMeshInFrustum(TerrainMesh terrainMesh) {
		// calculate square distance from camera
		Terrain terrain = terrainMesh.getTerrain();
		Vector3f center = terrainMesh.getTerrain().getWorldPosCenter();
		CameraProjective camera = this.getCamera();
		float distance = (float) Vector3f.distanceSquare(center, camera.getPosition());
		return (distance < camera.getSquaredRenderDistance()
				&& camera.isBoxInFrustum(terrain.getWorldPos(), Terrain.DIM_SIZE));
	}

	/** unload the meshes */
	private final void updateLoadedMeshes() {

		// for each terrain in the factory
		for (Terrain terrain : this.meshes.keySet()) {
			// if this terrain isnt loaded anymore
			if (!this.getWorld().isTerrainLoaded(terrain)) {
				// then remove it from the factory
				this.meshes.remove(terrain);
			}
		}

		// for every loaded terrains
		Terrain[] terrains = this.getWorld().getLoadedTerrains();
		final ArrayList<TerrainMesh> newMeshes = new ArrayList<TerrainMesh>();
		for (Terrain terrain : terrains) {
			// add it to the factory if it hasnt already been added
			if (!this.meshes.containsKey(terrain)) {
				TerrainMesh mesh = new TerrainMesh(terrain);
				newMeshes.add(mesh);
				this.meshes.put(terrain, mesh);
			}
		}

		VoxelEngineClient.instance().addGLTask(new GLTask() {
			@Override
			public void run() {
				for (TerrainMesh mesh : newMeshes) {
					mesh.initialize();
				}
			}
		});
	}

	@Override
	public void render() {
		MainRenderer mainRenderer = this.getMainRenderer();
		TerrainRenderer terrainRenderer = mainRenderer.getTerrainRenderer();
		terrainRenderer.render(this.getCamera(), this.getWorld(), this.renderingList);
	}
}
