package com.grillecube.common.world.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.grillecube.common.Logger;
import com.grillecube.common.Logger.Level;
import com.grillecube.common.Taskable;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.world.World;
import com.grillecube.common.world.WorldStorage;
import com.grillecube.common.world.events.EventEntityDespawn;
import com.grillecube.common.world.events.EventEntitySpawn;

/**
 * data structure which contains every entities
 * 
 * It aims is to have every entities stored in a continuous memory space and to
 * be able to get sorted entities (by type, so model) fastly, to optimize
 * rendering Insertion and deletion are also optimized
 **/
public class EntityStorage extends WorldStorage {

	/** the entities by their UUID */
	private HashMap<Integer, Entity> entities;

	/** the list of the entities sharing the class */
	private HashMap<Class<? extends Entity>, ArrayList<Entity>> entitiesByClass;

	public EntityStorage(World world) {
		super(world);
		this.entities = new HashMap<Integer, Entity>();
		this.entitiesByClass = new HashMap<Class<? extends Entity>, ArrayList<Entity>>();
	}

	/** get all entities */
	public Collection<Entity> getEntities() {
		return (this.entities.values());
	}

	public Entity getEntity(Integer worldID) {
		return (this.entities.get(worldID));
	}

	/** return a list of entities using the same class */
	public ArrayList<Entity> getEntitiesByClass(Class<? extends Entity> clazz) {
		return (this.entitiesByClass.get(clazz));
	}

	public ArrayList<Entity> getEntitiesByClass(Entity entity) {
		return (this.getEntitiesByClass(entity.getClass()));
	}

	/**
	 * return a collection of array list of entities, where each array list
	 * holds entities of the same class
	 */
	public Collection<ArrayList<Entity>> getEntitiesByClass() {
		return (this.entitiesByClass.values());
	}

	/** return the entity with the given world id */
	public Entity getEntityByID(Integer id) {
		return (this.entities.get(id));
	}

	/** return true if the entity is already stored, false else way */
	public boolean contains(Entity entity) {
		return (this.entities.containsValue(entity));
	}

	public boolean contains(Integer entityID) {
		return (this.entities.containsKey(entityID));
	}

	/** add an entity to the storage */
	public Entity add(Entity entity) {

		if (this.contains(entity)) {
			Logger.get().log(Logger.Level.WARNING, "Tried to spawn an already spawned entity", entity);
			return (entity);
		}

		// generate entity unique id
		Integer id = this.generateNextEntityID(entity);

		// prepare the entity
		entity.setWorldID(id);

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

	private void addEntityToTypeList(Entity entity) {
		ArrayList<Entity> type_list = this.getEntitiesByClass(entity.getClass());
		if (type_list == null) {
			type_list = new ArrayList<Entity>(1);
			this.entitiesByClass.put(entity.getClass(), type_list);
		}
		type_list.add(entity);
	}

	/**
	 * generate unique id for the given entity (assuming the entity isnt already
	 * stored
	 */
	private Integer generateNextEntityID(Entity entity) {

		// get the entity id if it already has one
		if (entity.getWorldID() != Entity.DEFAULT_WORLD_ID && !(this.contains(entity.getWorldID()))) {
			return (entity.getWorldID());
		}

		// else generate one
		Integer id = entity.hashCode();
		while (this.contains(id)) {
			++id;
		}
		return (id);
	}

	/** remove the given entity */
	public Entity remove(Entity entity) {
		if (!(this.contains(entity))) {
			Logger.get().log(Level.WARNING, "Tried to remove an entity which wasnt in the world");
			return (null);
		}

		// remove from global list
		this.entities.remove(entity.getWorldID());

		// remove from type list
		ArrayList<Entity> type_list = this.getEntitiesByClass(entity);
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

		Collection<Entity> entities_collection = this.getEntities();
		int size = entities_collection.size();
		Entity[] entities = entities_collection.toArray(new Entity[size]);

		tasks.add(engine.new Callable<Taskable>() {

			@Override
			public EntityStorage call() throws Exception {

				for (int i = 0; i < entities.length; i++) {
					Entity entity = entities[i];
					entity.update();
				}
				return (EntityStorage.this);
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
}
