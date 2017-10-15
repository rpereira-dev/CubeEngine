/**
**	This file is part of the project https://github.com/toss-dev/VoxelEngine
**
**	License is available here: https://raw.githubusercontent.com/toss-dev/VoxelEngine/master/LICENSE.md
**
**	PEREIRA Romain
**                                       4-----7          
**                                      /|    /|
**                                     0-----3 |
**                                     | 5___|_6
**                                     |/    | /
**                                     1-----2
*/

package com.grillecube.common.world;

import java.util.ArrayList;
import java.util.Collection;

import com.grillecube.common.Taskable;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.maths.BoundingBox;
import com.grillecube.common.maths.Maths;
import com.grillecube.common.maths.Vector3i;
import com.grillecube.common.world.block.Block;
import com.grillecube.common.world.entity.Entity;
import com.grillecube.common.world.terrain.Terrain;
import com.grillecube.common.world.terrain.WorldFlatTerrainStorage;

public abstract class WorldFlat extends World {

	/** world weather */
	private Sky sky;

	public WorldFlat() {
		super();
		this.sky = new Sky();
	}

	protected final WorldTerrainStorage instanciateTerrainStorage() {
		return (new WorldFlatTerrainStorage(this));
	}

	/** tasks to be run to update the world */
	@Override
	protected void onTasksGet(VoxelEngine engine, ArrayList<VoxelEngine.Callable<Taskable>> tasks) {
		this.sky.getTasks(engine, tasks);
	}

	/** return world weather */
	public Sky getSky() {
		return (this.sky);
	}

	@Override
	public String toString() {
		return ("WorldFlat: " + this.getName());
	}

	/** return world name */
	public abstract String getName();

	/** return every blocks which collides with the given bounding box */
	public final ArrayList<Block> getCollidingBlocks(BoundingBox box) {
		ArrayList<Block> lst = new ArrayList<Block>();

		int minx = Maths.floor(box.getMin().x);
		int maxx = Maths.floor(box.getMax().x + 1.0D);
		int miny = Maths.floor(box.getMin().y);
		int maxy = Maths.floor(box.getMax().y + 1.0D);
		int minz = Maths.floor(box.getMin().z);
		int maxz = Maths.floor(box.getMax().z + 1.0D);

		Vector3i pos = new Vector3i();

		// iterate though each blocks
		for (pos.x = minx; pos.x < maxx; ++pos.x) {
			for (pos.z = minz; pos.z < maxz; ++pos.z) {
				for (pos.y = miny; pos.y < maxy; ++pos.y) {
					Block block = this.getBlock(pos.x, pos.y, pos.z);
					if (block.influenceCollisions()) {
						lst.add(block);
					}
				}
			}
		}
		return (lst);
	}

	/** return every blocks which collides with the given bounding box */
	public final ArrayList<Entity> getCollidingEntities(BoundingBox box) {
		ArrayList<Entity> lst = new ArrayList<Entity>();
		Collection<Entity> entities = this.getEntityStorage().getEntities();
		for (Entity entity : entities) {
			if (entity.getBoundingBox().intersect(box)) {
				lst.add(entity);
			}
		}

		return (lst);
	}

	/**
	 * return every bounding box which collides with the given bounding box,
	 * excluding the given entity
	 */
	public Collection<BoundingBox> getCollidingBoundingBox(Entity entity, BoundingBox box) {
		return (this.getCollidingBoundingBox(entity, box, new ArrayList<BoundingBox>()));
	}

	/**
	 * @param entity
	 *            : the entity to exclude from the collision test
	 * @param box
	 *            : the box to test
	 * @param lst
	 *            : the list to push boxes
	 * @return
	 */
	public Collection<BoundingBox> getCollidingBoundingBox(Entity entity, BoundingBox box,
			Collection<BoundingBox> lst) {
		lst.clear();

		int minx = Maths.floor(box.getMin().x);
		int maxx = Maths.floor(box.getMax().x + 1.0D);
		int miny = Maths.floor(box.getMin().y);
		int maxy = Maths.floor(box.getMax().y + 1.0D);
		int minz = Maths.floor(box.getMin().z);
		int maxz = Maths.floor(box.getMax().z + 1.0D);

		// iterate though each block
		for (int x = minx; x < maxx; ++x) {
			for (int z = minz; z < maxz; ++z) {
				for (int y = miny; y < maxy; ++y) {
					Block block = this.getBlock(x, y, z);
					if (block.influenceCollisions()) {
						// generate the AABB for this block
						BoundingBox blockbox = new BoundingBox();
						blockbox.setMinSize(blockbox.getMin().set(x, y, z), Terrain.BLOCK_SIZE_VEC);
						if (box.intersect(blockbox)) {
							lst.add(blockbox);
						}
					}
				}
			}
		}

		// for (Entity e : this.getEntities()) {
		// if (!(e instanceof Entity) || e == entity) {
		// continue;
		// }
		//
		// BoundingBox entity_box = ((Entity)
		// e).getModelInstance().getMaxBoundingBox();
		// if (box.intersect(entity_box)) {
		// lst.add(entity_box);
		// }
		// }

		return (lst);
	}
}
