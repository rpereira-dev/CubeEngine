package com.grillecube.client.renderer.factories;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Stack;

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.renderer.MainRenderer.GLTask;
import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.client.renderer.world.TerrainMesh;
import com.grillecube.client.renderer.world.TerrainMeshTriangle;
import com.grillecube.client.renderer.world.TerrainMeshVertex;
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
import com.grillecube.common.world.Timer;
import com.grillecube.common.world.WorldFlat;
import com.grillecube.common.world.terrain.Terrain;

/** a factory class which create terrain renderer lists */
public class FlatTerrainRendererFactory extends RendererFactory {

	class TerrainRenderingData {

		// TODO : optimize this by setting null when ununsed
		final Terrain terrain;
		boolean meshUpToDate;
		final TerrainMesh opaqueMesh; // mesh holding opaque blocks
		final TerrainMesh transparentMesh; // mesh holding transparent blocks
		final Stack<TerrainMeshTriangle> opaqueTriangles;
		final Stack<TerrainMeshTriangle> transparentTriangles;
		ByteBuffer vertices;
		boolean isInFrustrum;
		Vector3f lastCameraPos;
		Timer timer;

		TerrainRenderingData(Terrain terrain) {
			this.terrain = terrain;
			this.opaqueMesh = new TerrainMesh(terrain);
			this.transparentMesh = new TerrainMesh(terrain);
			this.opaqueTriangles = new Stack<TerrainMeshTriangle>();
			this.transparentTriangles = new Stack<TerrainMeshTriangle>();
			this.meshUpToDate = false;
			this.lastCameraPos = new Vector3f();
			this.timer = new Timer();
		}

		void requestUpdate() {
			this.meshUpToDate = false;
		}

		void deinitialize() {
			this.opaqueMesh.deinitialize();
			this.transparentMesh.deinitialize();
		}

		void update(CameraProjective camera) {
			this.timer.update();
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
			// if mesh need update, regenerate them
			if (!this.meshUpToDate) {
				this.updateVertices(mesher);
			}

			// TODO : can this be done more properly?
			// if there is transparent triangles, and camera moved
			if (!camera.getPosition().equals(this.lastCameraPos) && this.transparentTriangles.size() > 0
					&& this.timer.getTime() > 0.5d) {
				this.timer.restart();
				// sort them back to front of the camera
				this.transparentTriangles.sort(TRIANGLE_SORT);
				// update vbo
				mesher.setMeshVertices(this.transparentMesh, this.transparentTriangles);
				this.lastCameraPos.set(camera.getPosition());
			}
		}

		Comparator<TerrainMeshTriangle> TRIANGLE_SORT = new Comparator<TerrainMeshTriangle>() {
			@Override
			public int compare(TerrainMeshTriangle t1, TerrainMeshTriangle t2) {
				float cx = camera.getPosition().x;
				float cy = camera.getPosition().y;
				float cz = camera.getPosition().z;
				TerrainMeshVertex v1 = t1.v0;
				TerrainMeshVertex v2 = t2.v0;
				double d1 = Vector3f.distanceSquare(cx, cy, cz, v1.posx, v1.posy, v1.posz);
				double d2 = Vector3f.distanceSquare(cx, cy, cz, v2.posx, v2.posy, v2.posz);
				return (int) ((d2 - d1) * 10.0f);
			}
		};

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
			mesher.pushVerticesToStacks(this.terrain, this.opaqueMesh, this.transparentMesh, this.opaqueTriangles,
					this.transparentTriangles);
			mesher.setMeshVertices(this.opaqueMesh, this.opaqueTriangles);
			mesher.setMeshVertices(this.transparentMesh, this.transparentTriangles);

			this.timer.restart();
			this.lastCameraPos.set(camera.getPosition());
		}

	}

	/** array list of terrain to render */
	private HashMap<Terrain, TerrainRenderingData> terrainsRenderingData;

	/** the mesher */
	private TerrainMesher mesher;

	/** next rendering list */
	private ArrayList<TerrainMesh> opaqueRenderingList;
	private ArrayList<TerrainMesh> transparentRenderingList;

	/** the world on which terrain should be considered */
	private WorldFlat world;
	private CameraProjective camera;

	public FlatTerrainRendererFactory(MainRenderer mainRenderer) {
		super(mainRenderer);

		this.terrainsRenderingData = new HashMap<Terrain, TerrainRenderingData>(4096);
		this.mesher = new FlatTerrainMesherGreedy();
		// this.mesher = new FlatTerrainMesherCull();
		this.opaqueRenderingList = new ArrayList<TerrainMesh>();
		this.transparentRenderingList = new ArrayList<TerrainMesh>();

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

	private final Comparator<? super TerrainMesh> DISTANCE_DESC_SORT = new Comparator<TerrainMesh>() {
		@Override
		public int compare(TerrainMesh o1, TerrainMesh o2) {
			return ((int) (Vector3f.distanceSquare(o2.getPosition(), camera.getPosition())
					- Vector3f.distanceSquare(o1.getPosition(), camera.getPosition())));
		}
	};

	private final void updateRenderingList() {
		this.opaqueRenderingList.clear();
		this.transparentRenderingList.clear();
		Collection<TerrainRenderingData> collection = this.terrainsRenderingData.values();
		TerrainRenderingData[] terrainsRenderingData = collection.toArray(new TerrainRenderingData[collection.size()]);

		// update meshes and add opaques one first (to be rendered first)
		for (TerrainRenderingData terrainRenderingData : terrainsRenderingData) {
			terrainRenderingData.update(this.getCamera());
			// if (terrainRenderingData.isInFrustrum() &&
			// terrainRenderingData.opaqueMesh.getVertexCount() > 0) {
			this.opaqueRenderingList.add(terrainRenderingData.opaqueMesh);
			// terrainRenderingData.requestUpdate();
			// }
		}
		this.opaqueRenderingList.sort(DISTANCE_DESC_SORT);

		// add the transparent meshes (to be rendered after opaque ones)
		for (TerrainRenderingData terrainRenderingData : terrainsRenderingData) {
			if (terrainRenderingData.isInFrustrum() && terrainRenderingData.transparentMesh.getVertexCount() > 0) {
				this.transparentRenderingList.add(terrainRenderingData.transparentMesh);
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
		terrainRenderer.render(this.getCamera(), this.getWorld(), this.opaqueRenderingList,
				this.transparentRenderingList);
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
