package com.grillecube.client.renderer.factories;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.renderer.MainRenderer.GLTask;
import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.client.renderer.terrain.TerrainMesh;
import com.grillecube.client.renderer.terrain.TerrainMesher;
import com.grillecube.client.renderer.terrain.TerrainMesherGreedy;
import com.grillecube.client.renderer.terrain.TerrainRenderer;
import com.grillecube.common.event.EventListener;
import com.grillecube.common.event.world.EventTerrainBlocklightUpdate;
import com.grillecube.common.event.world.EventTerrainDespawn;
import com.grillecube.common.event.world.EventTerrainSetBlock;
import com.grillecube.common.event.world.EventTerrainSunlightUpdate;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.resources.EventManager;
import com.grillecube.common.resources.ResourceManager;
import com.grillecube.common.world.World;
import com.grillecube.common.world.terrain.Terrain;

/** a factory class which create terrain renderer lists */
public class TerrainRendererFactory extends RendererFactory {

	class TerrainMeshData {
		byte state;
		TerrainMesh mesh;
		ByteBuffer vertices;

		TerrainMeshData(TerrainMesh mesh) {
			this.mesh = mesh;
			this.state = 1;
		}

		void requestUpdate() {
			this.state = 1;
		}

		boolean verticesNeedUpdate() {
			return (this.state == 1);
		}

		void updateVertices(TerrainMesher mesher) {
			this.mesh.setVertices(mesher.generateVertices(this.mesh.getTerrain()));
			this.state = 0;
		}
	}

	/** array list of terrain to render */
	private HashMap<Terrain, TerrainMeshData> meshesData;

	/** the mesher */
	private TerrainMesher mesher;

	/** next rendering list */
	private ArrayList<TerrainMesh> renderingList;

	/** the world on which terrain should be considered */
	private World world;
	private CameraProjective camera;

	// TODO : Terrain.STATE_VERTICES_UP_TO_DATE , remove this (server has
	// nothing to deal with vertices, and it creates conflicts with multiple
	// world renderer on the same world
	// to do so, add events to Terrains, and listener to the factory
	public TerrainRendererFactory(MainRenderer mainRenderer) {
		super(mainRenderer);

		this.meshesData = new HashMap<Terrain, TerrainMeshData>(8192);
		this.mesher = new TerrainMesherGreedy();
		// this.mesher = new TerrainMesherCull();
		this.renderingList = new ArrayList<TerrainMesh>();

		EventManager eventManager = ResourceManager.instance().getEventManager();
		eventManager.addListener(new EventListener<EventTerrainDespawn>() {
			@Override
			public void invoke(EventTerrainDespawn event) {
				Terrain terrain = event.getTerrain();
				if (terrain.getWorld() != world) {
					return;
				}
				TerrainMeshData terrainMeshData = meshesData.get(terrain);
				if (terrainMeshData != null) {
					terrainMeshData.mesh.deinitialize();
					meshesData.remove(terrain);
				}
			}
		});

		eventManager.addListener(new EventListener<EventTerrainSetBlock>() {
			@Override
			public void invoke(EventTerrainSetBlock event) {
				if (event.getTerrain().getWorld() != world) {
					return;
				}
				requestMeshUpdate(event.getTerrain());
			}
		});

		eventManager.addListener(new EventListener<EventTerrainBlocklightUpdate>() {
			@Override
			public void invoke(EventTerrainBlocklightUpdate event) {
				if (event.getTerrain().getWorld() != world) {
					return;
				}
				requestMeshUpdate(event.getTerrain());
			}
		});

		eventManager.addListener(new EventListener<EventTerrainSunlightUpdate>() {
			@Override
			public void invoke(EventTerrainSunlightUpdate event) {
				if (event.getTerrain().getWorld() != world) {
					return;
				}
				requestMeshUpdate(event.getTerrain());
			}
		});
	}

	@Override
	public final void deinitialize() {
		/** destroy every currently set meshes */
		Collection<TerrainMeshData> meshesData = this.meshesData.values();
		VoxelEngineClient.instance().addGLTask(new GLTask() {
			@Override
			public void run() {
				for (TerrainMeshData meshData : meshesData) {
					if (meshData != null) {
						meshData.mesh.deinitialize();
					}
				}
			}
		});
		this.meshesData.clear();

	}

	@Override
	public final void update() {
		this.updateLoadedMeshes();
		this.updateRenderingList();
	}

	private final void updateRenderingList() {
		this.renderingList.clear();
		for (TerrainMeshData meshData : this.meshesData.values()) {
			if (meshData == null) {
				continue;
			}

			if (this.isMeshInFrustum(meshData.mesh)) {
				this.renderingList.add(meshData.mesh);
			}
		}

		VoxelEngineClient.instance().addGLTask(new GLTask() {
			@Override
			public void run() {
				for (TerrainMesh mesh : renderingList) {
					TerrainMeshData meshData = meshesData.get(mesh.getTerrain());
					if (meshData.verticesNeedUpdate()) {
						meshData.updateVertices(mesher);
					}
				}
			}
		});
	}

	private final void requestMeshUpdate(Terrain terrain) {
		TerrainMeshData meshData = this.meshesData.get(terrain);
		if (meshData == null) {
			return;
		}
		meshData.requestUpdate();
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
		ArrayList<TerrainMesh> oldMeshes = new ArrayList<TerrainMesh>();
		for (Terrain terrain : this.meshesData.keySet()) {
			// if this terrain isnt loaded anymore
			if (!this.getWorld().isTerrainLoaded(terrain)) {
				// then remove it from the factory
				TerrainMeshData meshData = this.meshesData.remove(terrain);
				oldMeshes.add(meshData.mesh);
			}
		}

		// for every loaded terrains
		Terrain[] terrains = this.getWorld().getLoadedTerrains();
		ArrayList<TerrainMesh> newMeshes = new ArrayList<TerrainMesh>();
		for (Terrain terrain : terrains) {
			// add it to the factory if it hasnt already been added
			if (!this.meshesData.containsKey(terrain)) {
				TerrainMesh mesh = new TerrainMesh(terrain);
				newMeshes.add(mesh);
				this.meshesData.put(terrain, new TerrainMeshData(mesh));
			}
		}

		VoxelEngineClient.instance().addGLTask(new GLTask() {
			@Override
			public void run() {
				for (TerrainMesh mesh : oldMeshes) {
					mesh.deinitialize();
				}
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

	public final World getWorld() {
		return (this.world);
	}

	public final CameraProjective getCamera() {
		return (this.camera);
	}

	public final void setWorld(World world) {
		this.world = world;
	}

	public final void setCamera(CameraProjective camera) {
		this.camera = camera;
	}

}
