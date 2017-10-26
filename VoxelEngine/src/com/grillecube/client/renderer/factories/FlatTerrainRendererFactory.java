package com.grillecube.client.renderer.factories;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.renderer.MainRenderer.GLTask;
import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.client.renderer.world.TerrainMesh;
import com.grillecube.client.renderer.world.TerrainMesher;
import com.grillecube.client.renderer.world.TerrainRenderer;
import com.grillecube.client.renderer.world.flat.terrain.FlatTerrainMesherGreedy;
import com.grillecube.common.event.EventListener;
import com.grillecube.common.event.world.EventTerrainBlocklightUpdate;
import com.grillecube.common.event.world.EventTerrainDespawn;
import com.grillecube.common.event.world.EventTerrainSetBlock;
import com.grillecube.common.event.world.EventTerrainSunlightUpdate;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.resources.EventManager;
import com.grillecube.common.resources.ResourceManager;
import com.grillecube.common.world.WorldFlat;
import com.grillecube.common.world.terrain.Terrain;

/** a factory class which create terrain renderer lists */
public class FlatTerrainRendererFactory extends RendererFactory {

	class TerrainRenderingData {

		final Terrain terrain;
		boolean meshUpToDate;
		final TerrainMesh opaqueMesh; // mesh holding opaque blocks
		final TerrainMesh transparentMesh; // mesh holding transparent blocks
		ByteBuffer vertices;
		boolean isInFrustrum;

		TerrainRenderingData(Terrain terrain) {
			this.terrain = terrain;
			this.opaqueMesh = new TerrainMesh(terrain);
			this.transparentMesh = new TerrainMesh(terrain);
			this.meshUpToDate = false;
		}

		void requestUpdate() {
			this.meshUpToDate = false;
		}

		void deinitialize() {
			this.opaqueMesh.deinitialize();
			this.transparentMesh.deinitialize();
		}

		void update(CameraProjective camera) {
			// calculate square distance from camera
			Vector3f center = this.terrain.getWorldPosCenter();
			float distance = (float) Vector3f.distanceSquare(center, camera.getPosition());
			this.isInFrustrum = (distance < camera.getSquaredRenderDistance()
					&& camera.isBoxInFrustum(terrain.getWorldPos(), Terrain.TERRAIN_SIZE));
		}

		boolean isInFrustrum() {
			return (this.isInFrustrum);
		}

		void glUpdate(TerrainMesher mesher) {
			if (!this.meshUpToDate) {
				this.updateVertices(mesher);
			}
		}

		void updateVertices(TerrainMesher mesher) {
			this.meshUpToDate = true;

			if (this.terrain.getOpaqueBlockCount() == 0) {
				if (this.opaqueMesh.isInitialized()) {
					this.opaqueMesh.deinitialize();
				}
			} else {
				if (!this.opaqueMesh.isInitialized()) {
					this.opaqueMesh.initialize();
				}
			}

			if (this.terrain.getTransparentBlockCount() == 0) {
				if (this.transparentMesh.isInitialized()) {
					this.transparentMesh.deinitialize();
				}
			} else {
				if (!this.transparentMesh.isInitialized()) {
					this.transparentMesh.initialize();
				}
			}
			mesher.generateVertices(this.terrain, this.opaqueMesh, this.transparentMesh);
		}

	}

	/** array list of terrain to render */
	private HashMap<Terrain, TerrainRenderingData> terrainsRenderingData;

	/** the mesher */
	private TerrainMesher mesher;

	/** next rendering list */
	private ArrayList<TerrainMesh> renderingList;

	/** the world on which terrain should be considered */
	private WorldFlat world;
	private CameraProjective camera;

	public FlatTerrainRendererFactory(MainRenderer mainRenderer) {
		super(mainRenderer);

		this.terrainsRenderingData = new HashMap<Terrain, TerrainRenderingData>(4096);
		this.mesher = new FlatTerrainMesherGreedy();
		// this.mesher = new FlatTerrainMesherCull();
		this.renderingList = new ArrayList<TerrainMesh>();

		EventManager eventManager = ResourceManager.instance().getEventManager();
		eventManager.addListener(new EventListener<EventTerrainDespawn>() {
			@Override
			public void invoke(EventTerrainDespawn event) {
				Terrain terrain = event.getTerrain();
				if (terrain.getWorld() != world) {
					return;
				}
				TerrainRenderingData terrainRenderingData = terrainsRenderingData.get(terrain);
				if (terrainRenderingData != null) {
					terrainRenderingData.deinitialize();
					terrainsRenderingData.remove(terrain);
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
		Collection<TerrainRenderingData> terrainsRenderingData = this.terrainsRenderingData.values();
		VoxelEngineClient.instance().addGLTask(new GLTask() {
			@Override
			public void run() {
				for (TerrainRenderingData terrainRenderingData : terrainsRenderingData) {
					if (terrainRenderingData != null) {
						terrainRenderingData.deinitialize();
					}
				}
			}
		});
		this.terrainsRenderingData.clear();

	}

	@Override
	public final void update() {
		this.updateLoadedMeshes();
		this.updateRenderingList();
	}

	private final void updateRenderingList() {
		this.renderingList.clear();
		Collection<TerrainRenderingData> collection = this.terrainsRenderingData.values();
		TerrainRenderingData[] terrainsRenderingData = collection.toArray(new TerrainRenderingData[collection.size()]);

		// update meshes and add opaques one first (to be rendered first)
		for (TerrainRenderingData terrainRenderingData : terrainsRenderingData) {
			terrainRenderingData.update(this.getCamera());
			// if (terrainRenderingData.isInFrustrum() &&
			// terrainRenderingData.opaqueMesh.getVertexCount() > 0) {
			this.renderingList.add(terrainRenderingData.opaqueMesh);
			// terrainRenderingData.requestUpdate();
			// }
		}

		// add the transparent meshes (to be rendered after opaque ones)
		for (TerrainRenderingData terrainRenderingData : terrainsRenderingData) {
			if (terrainRenderingData.isInFrustrum() && terrainRenderingData.transparentMesh.getVertexCount() > 0) {
				this.renderingList.add(terrainRenderingData.transparentMesh);
			}
		}

		VoxelEngineClient.instance().addGLTask(new GLTask() {
			@Override
			public void run() {
				for (TerrainRenderingData terrainRenderingData : terrainsRenderingData) {
					terrainRenderingData.glUpdate(mesher);
				}
			}
		});
		// System.out.println(this.renderingList.size() + ": " +
		// this.terrainsRenderingData.size());
	}

	private final void requestMeshUpdate(Terrain terrain) {
		TerrainRenderingData terrainRenderingData = this.terrainsRenderingData.get(terrain);
		if (terrainRenderingData == null) {
			return;
		}
		terrainRenderingData.requestUpdate();
	}

	/** unload the meshes */
	private final void updateLoadedMeshes() {

		// for each terrain in the factory
		ArrayList<TerrainRenderingData> oldTerrainsRenderingData = new ArrayList<TerrainRenderingData>();
		for (Terrain terrain : this.terrainsRenderingData.keySet()) {
			// if this terrain isnt loaded anymore
			if (!this.getWorld().isTerrainLoaded(terrain)) {
				// then remove it from the factory
				TerrainRenderingData terrainRenderingData = this.terrainsRenderingData.remove(terrain);
				oldTerrainsRenderingData.add(terrainRenderingData);
			}
		}

		// for every loaded terrains
		Terrain[] terrains = this.getWorld().getLoadedTerrains();
		for (Terrain terrain : terrains) {
			// add it to the factory if it hasnt already been added
			if (!this.terrainsRenderingData.containsKey(terrain)) {
				this.terrainsRenderingData.put(terrain, new TerrainRenderingData(terrain));
			}
		}

		VoxelEngineClient.instance().addGLTask(new GLTask() {
			@Override
			public void run() {
				for (TerrainRenderingData terrainRenderingData : oldTerrainsRenderingData) {
					terrainRenderingData.deinitialize();
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

	public final WorldFlat getWorld() {
		return (this.world);
	}

	public final CameraProjective getCamera() {
		return (this.camera);
	}

	public final void setWorld(WorldFlat world) {
		this.world = world;
	}

	public final void setCamera(CameraProjective camera) {
		this.camera = camera;
	}

}
