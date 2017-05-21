package com.grillecube.client.renderer.world.terrain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Stack;

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.client.renderer.MainRenderer.GLTask;
import com.grillecube.client.renderer.camera.Camera;
import com.grillecube.client.renderer.camera.CameraProjectiveWorld;
import com.grillecube.client.renderer.world.particles.ParticleCube;
import com.grillecube.common.Logger;
import com.grillecube.common.faces.Face;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector3i;
import com.grillecube.common.world.World;
import com.grillecube.common.world.terrain.Terrain;

/**
 * HOW THE FACTORY WORKS:
 * 
 * it get the world in which the camera is.
 * 
 * For every loaded terrains in this world, it checks which one should be
 * rendered (frustum / occlusion culling)
 * 
 * For every terrain that should be rendered, it update it mesh if needed
 * 
 * If the current camera world is different from a terrain mesh world, then the
 * mesh is destroyed
 *
 * If a terrain is farer than (1.4f * the camera render distance), then it mesh
 * is deleted.
 *
 * If a mesh hasnt been rendered for more than TERRAIN_MESH_MAX_REMAINING_TIME
 * seconds, then it is deleted (meaning that this terrain is occluded, or that
 * the user isnt looking at it for a long time
 * 
 * 
 * 
 * @author Romain
 *
 */
public class TerrainRendererFactory {

	public static final double TERRAIN_MESH_MAX_REMAINING_TIME = 30.0d;

	/** array list of terrain to render */
	private HashMap<Terrain, TerrainMesh> _meshes;
	private TerrainMesher _mesher;
	private ArrayList<TerrainMesh> _rendering_list_camera;

	public void initialize() {
		this._meshes = new HashMap<Terrain, TerrainMesh>(8192);
		this._mesher = new TerrainMesherGreedy();
		//		this._mesher = new TerrainMesherCull();
		this._rendering_list_camera = new ArrayList<TerrainMesh>();
	}

	public void deinitialize() {

		this.destroyMeshes();
		this._meshes = null;
		this._mesher = null;
		this._rendering_list_camera = null;
	}

	public void requestMeshRebuild() {
		// ask for every terrain's mesh rebuild
		Collection<Terrain> terrains = this._meshes.keySet();
		for (Terrain terrain : terrains) {
			terrain.requestMeshUpdate();
		}
	}

	/** destroy every currently set meshes */
	public void destroyMeshes() {

		// destroy meshes
		Collection<TerrainMesh> meshes = this._meshes.values();
		VoxelEngineClient.instance().getRenderer().addGLTask(new GLTask() {

			@Override
			public void run() {
				for (TerrainMesh mesh : meshes) {
					if (mesh != null) {
						mesh.destroy();
					}
				}
			}
		});

		this._meshes.clear();
	}

	public void onWorldSet(World world) {
		// TODO
	}

	public void onWorldUnset(World world) {
		// TODO
	}

	public void update(TerrainRenderer renderer) {

		World world = renderer.getWorld();
		CameraProjectiveWorld camera = (CameraProjectiveWorld) renderer.getCamera();

		this.updateLoadedTerrains(world);
		this.updateRenderingList(camera);
		this.updateRenderingListMeshes(camera);
	}

	/** this function register the terrain meshes */
	private void updateLoadedTerrains(World world) {

		// for each terrain in the factory
		for (Terrain terrain : this._meshes.keySet()) {
			// if this terrain isnt loaded anymore
			if (!world.isTerrainLoaded(terrain)) {
				// then remove it from the factory
				this._meshes.remove(terrain);
			}
		}

		// for every loaded terrains
		Terrain[] terrains = world.getLoadedTerrains();
		for (Terrain terrain : terrains) {
			// add it to the factory if it hasnt already been added
			if (this._meshes.get(terrain) == null) {
				this._meshes.put(terrain, new TerrainMesh(terrain));
				// PS: the new TerrainMesh instanciation doesnt allocate any GL
				// stuff
			}
		}
	}

	/**
	 * update the meshes in the rendering list (so they can generate their mesh
	 * floats
	 */
	private void updateRenderingListMeshes(Camera camera) {
		int i = 0;
		for (TerrainMesh mesh : this._meshes.values()) {
			if (mesh != null && mesh.update(this._mesher, camera)) {
				if (++i == 8) {
					break;
				}
			}
		}
	}

	/** return true if the given terrain is in the camera frustum */
	public boolean isTerrainInFrustum(CameraProjectiveWorld camera, Terrain terrain) {

		float distance = (float) Vector3f.distanceSquare(terrain.getCenter(), camera.getPosition());
		if (distance >= camera.getSquaredRenderDistance()) {
			return (false);
		}

		return (camera.isBoxInFrustum(terrain.getLocation().getWorldPosition(), Terrain.DIM_SIZE));
	}

	/** CULLING ALGORYTHM STOPS HERE */
	public TerrainMesh getMesh(Terrain terrain) {
		return (this._meshes.get(terrain));
	}

	private void updateRenderingList(CameraProjectiveWorld camera) {
		// if (GLH.glhGetContext().getWindow().isKeyPressed(GLFW.GLFW_KEY_L)) {
		// this._rendering_list_camera.clear();
		// this._rendering_list_shadow.clear();
		// this.getRenderingListFrustumOcclusionCull(camera);
		// } else if
		// (GLH.glhGetContext().getWindow().isKeyPressed(GLFW.GLFW_KEY_X)) {
		// this._rendering_list_camera.clear();
		// this._rendering_list_shadow.clear();
		// this.getRenderingListFrustumCull(camera, shadow_camera);
		// }

		this._rendering_list_camera.clear();
		this.getRenderingListFrustumCull(camera);
	}

	class TerrainVisit {
		Terrain terrain;
		Vector3i index;
		Face fromface;

		TerrainVisit(Terrain terrain, Vector3i index, Face fromface) {
			this.terrain = terrain;
			this.index = index;
			this.fromface = fromface;
		}
	}

	private void addBox(Vector3i index, float r, float g, float b) {

		ParticleCube particle = new ParticleCube();
		float size = Terrain.DIM_SIZE * 0.9f;
		particle.setScale(size, size, size);
		particle.setHealth(50000);
		particle.setColor(r, g, b, 0.2f);

		particle.setPosition(index.x * Terrain.DIM_SIZE, index.y * Terrain.DIM_SIZE, index.z * Terrain.DIM_SIZE);

		VoxelEngineClient.instance().getRenderer().getWorldRenderer().getParticleRenderer().spawnParticle(particle);

	}

	private void getRenderingListFrustumOcclusionCull(CameraProjectiveWorld camera) {
		VoxelEngineClient.instance().getRenderer().getWorldRenderer().getParticleRenderer().removeAllParticles();

		World world = camera.getWorld();
		HashMap<Vector3i, Boolean[]> visited = new HashMap<Vector3i, Boolean[]>();
		Vector3f viewvec = camera.getViewVector();
		Stack<TerrainVisit> stack = new Stack<TerrainVisit>();
		Vector3i camindex = camera.getWorldIndex();

		// add the six terrain next to the camera + the terrain the camera is
		// inside
		for (Face face : Face.faces) {
			Vector3i index = Vector3i.add(camindex, face.getVector());
			stack.push(new TerrainVisit(world.getTerrain(index), camindex, face));
		}

		Vector3i nextindex = new Vector3i();
		int i = 0;
		// while the stack isnt empty
		while (!stack.isEmpty() && i < 300) {

			++i;

			// get the terrain to visit
			TerrainVisit visit = stack.pop();

			// index we are in
			Vector3i inindex = visit.index;

			// if we already processed it, continue to next face
			Boolean[] set = visited.get(inindex);
			if (set == null) {
				set = new Boolean[Face.faces.length];
				int j;
				for (j = 0; j < set.length; j++) {
					set[j] = false;
				}
			} else if (set[visit.fromface.getID()] != null) {
				continue;
			}

			// set the terrain as visited
			set[visit.fromface.getID()] = true;

			// get the terrain
			Terrain interrain = visit.terrain;
			// if non null (full of air), and in frustum, then add it to the
			// rendering list
			if (interrain != null && camera.isBoxInFrustum(interrain.getWorldPosition(), Terrain.DIM_SIZE)) {
				TerrainMesh mesh = this.getMesh(interrain);
				if (this._rendering_list_camera.contains(mesh) == false) {
					this._rendering_list_camera.add(mesh);
					this.addBox(inindex, 0, 1, 0);
				}
			}

			// get the face we came from
			Face fromface = visit.fromface;

			// for each faces of the terrain
			for (Face toface : Face.faces) {

				// get the neighboor terrain
				Vector3i.add(nextindex, visit.index, toface.getVector());

				// if we are going backward, continue to other faces

				// get the next terrain
				Terrain nextterrain = (interrain == null) ? world.getTerrain(nextindex)
						: interrain.getNeighbor(toface.getID());

				// if null, it means it is empty (full of air), so not occluded
				// obviously
				// check if 'nextterrain' is visible from 'fromterrain'
				// though 'interrain'
				if (interrain != null && interrain.canFaceBeSeenFrom(toface, fromface.getOpposite())) {
					this.addBox(nextindex, 1, 0, 0);
					continue;
				}

				// if in frustum and not occluded, add it to the stack
				stack.push(new TerrainVisit(nextterrain, nextindex, toface));
			}
		}

		Logger.get().log(Logger.Level.DEBUG, i + "/300", "stack size: " + stack.size(),
				"rendering list: " + this._rendering_list_camera.size());

		i = 0;
		while (i < 16) {
			Logger.get().log(Logger.Level.DEBUG, stack.get(i).fromface.getName(), stack.get(i).index);
			++i;
		}

	}

	private void getRenderingListFrustumCull(CameraProjectiveWorld camera) {

		for (TerrainMesh mesh : this._meshes.values()) {

			if (mesh == null) {
				continue;
			}

			// calculate square distance from camera
			float distance = (float) Vector3f.distanceSquare(mesh.getTerrain().getCenter(), camera.getPosition());
			if (distance < camera.getSquaredRenderDistance()
					&& camera.isBoxInFrustum(mesh.getTerrain().getLocation().getWorldPosition(), Terrain.DIM_SIZE)) {
				this._rendering_list_camera.add(mesh);
			}
		}
	}

	/**
	 * return the list of the terrain that should be rendered (non null terrain)
	 */
	public ArrayList<TerrainMesh> getCameraRenderingList() {
		return (this._rendering_list_camera);
	}

	/**
	 * return an array which contains every current meshes (may contains null
	 * meshes)
	 */
	public TerrainMesh[] getMeshes() {
		return (this._meshes.values().toArray(new TerrainMesh[this._meshes.size()]));
	}
}
