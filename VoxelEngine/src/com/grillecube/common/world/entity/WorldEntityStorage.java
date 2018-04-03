package com.grillecube.common.world.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.grillecube.common.Logger;
import com.grillecube.common.Logger.Level;
import com.grillecube.common.Taskable;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.event.world.entity.EventEntityDespawn;
import com.grillecube.common.event.world.entity.EventEntitySpawn;
import com.grillecube.common.world.World;
import com.grillecube.common.world.WorldStorage;

/**
 * data structure which contains every entities
 * 
 * It aims is to have every entities stored in a continuous memory space and to
 * be able to get sorted entities (by type, so model) fastly, to optimize
 * rendering Insertion and deletion are also optimized
 **/
public class WorldEntityStorage extends WorldStorage<WorldEntity> implements Iterable<WorldEntity> {

	/** the entities by their UUID */
	private HashMap<Integer, WorldEntity> entities;

	/** the list of the entities sharing the class */
	private HashMap<Class<? extends WorldEntity>, ArrayList<WorldEntity>> entitiesByClass;

	public WorldEntityStorage(World world) {
		super(world);
		this.entities = new HashMap<Integer, WorldEntity>();
		this.entitiesByClass = new HashMap<Class<? extends WorldEntity>, ArrayList<WorldEntity>>();
	}

	/** get all entities */
	public Collection<WorldEntity> getEntities() {
		return (this.entities.values());
	}

	@Override
	public Collection<WorldEntity> get() {
		return (this.getEntities());
	}

	public WorldEntity getEntity(Integer worldID) {
		return (this.entities.get(worldID));
	}

	/** return a list of entities using the same class */
	public ArrayList<WorldEntity> getEntitiesByClass(Class<? extends WorldEntity> clazz) {
		return (this.entitiesByClass.get(clazz));
	}

	public ArrayList<WorldEntity> getEntitiesByClass(WorldEntity entity) {
		return (this.getEntitiesByClass(entity.getClass()));
	}

	/**
	 * return a collection of array list of entities, where each array list holds
	 * entities of the same class
	 */
	public Collection<ArrayList<WorldEntity>> getEntitiesByClass() {
		return (this.entitiesByClass.values());
	}

	/** return the entity with the given world id */
	public WorldEntity getEntityByID(Integer id) {
		return (this.entities.get(id));
	}

	/** return true if the entity is already stored, false else way */
	public boolean contains(WorldEntity entity) {
		return (this.entities.containsValue(entity));
	}

	public boolean contains(Integer entityID) {
		return (this.entities.containsKey(entityID));
	}

	@Override
	public WorldEntity add(WorldEntity entity) {

		if (this.contains(entity)) {
			Logger.get().log(Logger.Level.WARNING, "Tried to spawn an already spawned entity", entity);
			return (entity);
		}

		// generate entity unique id
		Integer id = this.generateNextEntityID(entity);

		// prepare the entity
		entity.setEntityID(id);

		// add it to the list
		this.entities.put(id, entity);

		// spawn the entity
		entity.setWorld(this.getWorld());
		entity.onSpawn(this.getWorld());

		// add it to the type list
		this.addEntityToTypeList(entity);

		// invoke events
		this.invokeEvent(new EventEntitySpawn(entity));

		return (entity);
	}

	private void addEntityToTypeList(WorldEntity entity) {
		ArrayList<WorldEntity> type_list = this.getEntitiesByClass(entity.getClass());
		if (type_list == null) {
			type_list = new ArrayList<WorldEntity>(1);
			this.entitiesByClass.put(entity.getClass(), type_list);
		}
		type_list.add(entity);
	}

	/**
	 * generate unique id for the given entity (assuming the entity isnt already
	 * stored
	 */
	private Integer generateNextEntityID(WorldEntity entity) {

		// get the entity id if it already has one
		if (entity.getEntityID() != WorldEntity.DEFAULT_ENTITY_ID && !(this.contains(entity.getEntityID()))) {
			return (entity.getEntityID());
		}

		// else generate one
		Integer id = entity.hashCode();
		while (this.contains(id)) {
			++id;
		}
		return (id);
	}

	@Override
	public WorldEntity remove(WorldEntity entity) {
		if (!(this.contains(entity))) {
			Logger.get().log(Level.WARNING, "Tried to remove an entity which wasnt in the world");
			return (null);
		}

		// remove from global list
		this.entities.remove(entity.getEntityID());

		// remove from type list
		ArrayList<WorldEntity> type_list = this.getEntitiesByClass(entity);
		if (type_list != null) {
			type_list.remove(entity);
			if (type_list.size() == 0) {
				this.entitiesByClass.remove(entity.getClass());
			}
		}

		// invoke events
		this.invokeEvent(new EventEntityDespawn(entity));

		return (entity);
	}

	/** clean the entity storage, remove every entities */
	public void removeAll() {
		this.entities.clear();
		this.entitiesByClass.clear();
	}

	@Override
	public void getTasks(VoxelEngine engine, ArrayList<com.grillecube.common.VoxelEngine.Callable<Taskable>> tasks) {

		tasks.add(engine.new Callable<Taskable>() {
			@Override
			public WorldEntityStorage call() throws Exception {
				double dt = engine.getTimer().getDt();
				for (WorldEntity entity : WorldEntityStorage.this) {
					entity.update(dt);
				}
				return (WorldEntityStorage.this);
			}

			@Override
			public String getName() {
				return ("EntityStorage update");
			}
		});
	}

	@Override
	public void delete() {
		this.removeAll();
	}

	@Override
	public int size() {
		return (this.entities.size());
	}
}
