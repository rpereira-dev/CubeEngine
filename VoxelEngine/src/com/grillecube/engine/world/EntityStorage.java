package com.grillecube.engine.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.grillecube.engine.Logger;
import com.grillecube.engine.Logger.Level;
import com.grillecube.engine.Taskable;
import com.grillecube.engine.VoxelEngine;
import com.grillecube.engine.event.world.EventEntitySpawn;
import com.grillecube.engine.maths.Maths;
import com.grillecube.engine.renderer.model.Model;
import com.grillecube.engine.resources.ResourceManager;
import com.grillecube.engine.world.entity.Entity;
import com.grillecube.engine.world.entity.EntityModeled;

/**
 * data structure which contains every entities
 * 
 * It aims is to have every entities stored in a continuous memory space and to
 * be able to get sorted entities (by type, so model) fastly, to optimize
 * rendering Insertion and deletion are also optimized
 **/
public class EntityStorage extends WorldStorage {

	private HashMap<Integer, Entity> _entities;
	private HashMap<Class<? extends Entity>, ArrayList<Entity>> _entities_by_type;
	private HashMap<Model, ArrayList<Entity>> _entities_by_model;

	public EntityStorage(World world) {
		super(world);
		this._entities = new HashMap<Integer, Entity>();
		this._entities_by_type = new HashMap<Class<? extends Entity>, ArrayList<Entity>>();
		this._entities_by_model = new HashMap<Model, ArrayList<Entity>>();
	}

	/** get all entities */
	public Collection<Entity> getEntities() {
		return (this._entities.values());
	}

	public Entity getEntity(Integer worldID) {
		return (this._entities.get(worldID));
	}

	/** return a list of entities using the same class */
	public ArrayList<Entity> getEntitiesByType(Class<? extends Entity> clazz) {
		return (this._entities_by_type.get(clazz));
	}

	public ArrayList<Entity> getEntitiesByType(Entity entity) {
		return (this.getEntitiesByType(entity.getClass()));
	}

	/**
	 * return a collection of array list of entities, where each array list
	 * holds entities of the same class
	 */
	public Collection<ArrayList<Entity>> getEntitiesByType() {
		return (this._entities_by_type.values());
	}

	/**
	 * return a list of entities using the same model. IMPORTANT: if the model
	 * is non null, then the entities all inherite from class "EntityModeled"
	 * (you can cast them safely)
	 */
	public ArrayList<Entity> getEntitiesByModel(Model model) {
		return (this._entities_by_model.get(model));
	}

	public ArrayList<Entity> getEntitiesByModel(Entity entity) {
		return (this.getEntitiesByModel(this.getEntityModel(entity)));
	}

	/**
	 * return a collection of array list of entitymodeled, where each array list
	 * contains all entities using the same model
	 */
	public Collection<ArrayList<Entity>> getEntitiesByModel() {
		return (this._entities_by_model.values());
	}

	/** return the entity with the given world id */
	public Entity getEntityByID(Integer id) {
		return (this._entities.get(id));
	}

	/** return true if the entity is already stored, false else way */
	public boolean contains(Entity entity) {
		return (this._entities.containsValue(entity));
	}

	public boolean contains(Integer entityID) {
		return (this._entities.containsKey(entityID));
	}

	/** add an entity to the storage */
	public Entity add(Entity entity) {

		if (this.contains(entity)) {
			return (entity);
		}

		// generate entity unique id
		Integer id = this.generateNextEntityID(entity);

		// prepare the entity
		entity.setWorldID(id);

		// add it to the list
		this._entities.put(id, entity);

		// spawn the entity
		entity.setWorld(this.getWorld());
		entity.onSpawn(this.getWorld());

		// add it to the type list
		this.addEntityToTypeList(entity);

		// add it to the model list
		this.addEntityToModelList(entity);

		// invoke events
		this.invokeEvent(new EventEntitySpawn(entity));

		return (entity);
	}

	private void addEntityToModelList(Entity entity) {
		Model model = this.getEntityModel(entity);
		ArrayList<Entity> model_list = this.getEntitiesByModel(model);

		if (model_list == null) {
			model_list = new ArrayList<Entity>(1);
			this._entities_by_model.put(model, model_list);
		}
		model_list.add(entity);
	}

	private Model getEntityModel(Entity entity) {
		return ((entity instanceof EntityModeled) ? ((EntityModeled) entity).getModelInstance().getModel() : null);
	}

	private void addEntityToTypeList(Entity entity) {
		ArrayList<Entity> type_list = this.getEntitiesByType(entity.getClass());
		if (type_list == null) {
			type_list = new ArrayList<Entity>(1);
			this._entities_by_type.put(entity.getClass(), type_list);
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

	public Entity add(Entity entity, float x, float y, float z) {
		entity.setPosition(x, y, z);
		return (this.add(entity));
	}

	@Deprecated
	public Entity add(int entityID, float x, float y, float z) {
		Entity entity = ResourceManager.instance().getEntityManager().newInstance(entityID);
		return (this.add(entity, x, y, z));
	}

	/** remove the given entity */
	public Entity remove(Entity entity) {
		if (!(this.contains(entity))) {
			Logger.get().log(Level.WARNING, "Tried to remove an entity which wasnt in the world");
			return (null);
		}

		// remove from global list
		this._entities.remove(entity.getWorldID());

		// remove from type list
		ArrayList<Entity> type_list = this.getEntitiesByType(entity);
		if (type_list != null) {
			type_list.remove(entity);
			if (type_list.size() == 0) {
				this._entities_by_type.remove(entity.getClass());
			}
		}

		// remove from model list
		Model model = this.getEntityModel(entity);
		ArrayList<Entity> model_list = this.getEntitiesByModel(model);
		if (model_list != null) {
			model_list.remove(entity);
			if (model_list.size() == 0) {
				this._entities_by_model.remove(model);
			}
		}

		return (entity);
	}

	/** remove every entities which uses the given model (can be null) */
	public Collection<Entity> removeEntitiesByModel(Model model) {

		ArrayList<Entity> entities = this.getEntitiesByModel(model);

		if (entities == null || entities.size() == 0) {
			Logger.get().log(Logger.Level.DEBUG,
					"Tried to remove entity with model: " + model + " but no entities with this model where found.");
			return (null);
		}

		for (Entity entity : entities) {
			this._entities.remove(entity.getWorldID());
			this._entities_by_type.remove(entity.getClass());
		}

		this._entities_by_model.remove(model);

		return (entities);
	}

	/** clean the entity storage, remove every entities */
	public void removeAll() {
		this._entities.clear();
		this._entities_by_model.clear();
		this._entities_by_type.clear();
	}

	/** the number of entity to be updated per tasks */
	public static final int ENTITY_PER_TASK = 8;

	@Override
	public void getTasks(VoxelEngine engine, ArrayList<com.grillecube.engine.VoxelEngine.Callable<Taskable>> tasks) {

		Collection<Entity> entities_collection = this.getEntities();
		int size = entities_collection.size();
		Entity[] entities = entities_collection.toArray(new Entity[size]);

		int i;
		for (i = 0; i < size; i += ENTITY_PER_TASK) {

			final int begin = i;
			final int end = Maths.min(i + ENTITY_PER_TASK, size);

			tasks.add(engine.new Callable<Taskable>() {

				@Override
				public EntityStorage call() throws Exception {

					for (int j = begin; j < end; j++) {
						Entity entity = entities[j];
						entity.update();
					}
					return (EntityStorage.this);
				}

				@Override
				public String getName() {
					return ("EntityStorage update n°" + begin + " to n°" + end + " on a total of " + size);
				}
			});
		}
	}

	@Override
	public void delete() {
		this.removeAll();
	}
}
