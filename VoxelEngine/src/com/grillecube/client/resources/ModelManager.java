package com.grillecube.client.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.grillecube.client.event.renderer.model.EventModelInstanceAdded;
import com.grillecube.client.event.renderer.model.EventModelInstanceRemoved;
import com.grillecube.client.renderer.model.Model;
import com.grillecube.client.renderer.model.instance.ModelInstance;
import com.grillecube.common.Logger;
import com.grillecube.common.event.Listener;
import com.grillecube.common.event.world.entity.EventEntityDespawn;
import com.grillecube.common.event.world.entity.EventEntitySpawn;
import com.grillecube.common.resources.EventManager;
import com.grillecube.common.resources.GenericManager;
import com.grillecube.common.resources.ResourceManager;
import com.grillecube.common.world.entity.Entity;

/** handle models */
public class ModelManager extends GenericManager<Model> {

	/** the model hashmap */
	private HashMap<Class<? extends Entity>, Integer> entitiesModels;

	/** the model instances using the given model */
	private HashMap<Model, ArrayList<ModelInstance>> modelsModelInstances;

	/** the model instances of the entities */
	private HashMap<Entity, ModelInstance> entitiesModelInstance;

	/** the model manager */
	public ModelManager(ResourceManager resourceManager) {
		super(resourceManager);
	}

	@Override
	protected final void onInitialized() {
		this.entitiesModels = new HashMap<Class<? extends Entity>, Integer>();
		this.modelsModelInstances = new HashMap<Model, ArrayList<ModelInstance>>();
		this.entitiesModelInstance = new HashMap<Entity, ModelInstance>();
	}

	@Override
	protected final void onDeinitialized() {
		this.removeModelInstances();
		this.entitiesModels = null;
		this.modelsModelInstances = null;
		this.entitiesModelInstance = null;
	}

	@Override
	protected final void onUnloaded() {
		this.removeModelInstances();
	}

	@Override
	protected final void onLoaded() {
		EventManager eventManager = this.getResourceManager().getEventManager();
		eventManager.addListener(new Listener<EventEntitySpawn>() {

			@Override
			public void pre(EventEntitySpawn event) {
			}

			@Override
			public void post(EventEntitySpawn event) {
				Entity entity = event.getEntity();
				Model model = getModelForEntity(entity);
				if (model == null) {
					Logger.get().log(Logger.Level.ERROR, "No model for entity class", entity.getClass());
					return;
				}

				if (!model.isInitialized()) {
					Logger.get().log(Logger.Level.FINE, "Initializing model", model.getInitializer());
					model.initialize();
					Logger.get().log(Logger.Level.FINE, "Initialized model", model);
				}
				addModelInstance(new ModelInstance(model, entity));
			}
		});

		eventManager.addListener(new Listener<EventEntityDespawn>() {

			@Override
			public void pre(EventEntityDespawn event) {
			}

			@Override
			public void post(EventEntityDespawn event) {
				Entity entity = event.getEntity();
				ModelInstance modelInstance = getModelInstance(entity);
				if (modelInstance == null) {
					return;
				}
				removeModelInstance(modelInstance);
			}
		});
	}

	/** remove every model instances */
	public final void removeModelInstances() {
		Collection<ModelInstance> collection = this.entitiesModelInstance.values();
		ModelInstance[] modelInstances = collection.toArray(new ModelInstance[collection.size()]);
		for (ModelInstance modelInstance : modelInstances) {
			this.removeModelInstance(modelInstance);
		}
	}

	/** remove the given model instance */
	public final void removeModelInstance(ModelInstance modelInstance) {
		Model model = modelInstance.getModel();
		ArrayList<ModelInstance> modelInstances = this.modelsModelInstances.get(model);
		if (modelInstances == null) {
			Logger.get().log(Logger.Level.WARNING, "tried to despawn an un-spawned model instance: " + modelInstance);
			return;
		}
		modelInstances.remove(modelInstance);

		if (modelInstances.size() == 0) {
			this.modelsModelInstances.remove(modelInstance.getModel());
			model.deinitialize();
		}
		this.entitiesModelInstance.remove(modelInstance.getEntity());
		this.getResourceManager().getEventManager().invokeEvent(new EventModelInstanceRemoved(modelInstance));
	}

	/**
	 * WARNING: do not use it unless you know what you're doing. spawn a model
	 * instance
	 */
	public final void addModelInstance(ModelInstance modelInstance) {
		Entity entity = modelInstance.getEntity();
		Model model = modelInstance.getModel();
		ArrayList<ModelInstance> modelInstances = this.modelsModelInstances.get(model);
		if (modelInstances == null) {
			modelInstances = new ArrayList<ModelInstance>(1);
			this.modelsModelInstances.put(model, modelInstances);
		}
		modelInstances.add(modelInstance);
		this.entitiesModelInstance.put(entity, modelInstance);
		this.getResourceManager().getEventManager().invokeEvent(new EventModelInstanceAdded(modelInstance));
	}

	/**
	 * register a new model, link it with the entity class, and return the model ID
	 */
	public final int registerModel(Model model) {
		return (super.registerObject(model));
	}

	/**
	 * attach an entity to a model, so when the entity spawns, a new model instance
	 * is created
	 * 
	 * @param entityClass
	 *            : the entity class to attach
	 * @param modelID
	 *            : the registered modelID
	 */
	public final void attachEntityToModel(Class<? extends Entity> entityClass, Integer modelID) {
		this.entitiesModels.put(entityClass, modelID);
	}

	/**
	 * get the model for the given entity
	 * 
	 * @param entityClass
	 *            : the entity class we want the model
	 * 
	 * @return : the model
	 */
	public final Model getModelForEntity(Class<? extends Entity> entityClass) {
		if (!this.entitiesModels.containsKey(entityClass)) {
			return (null);
		}
		return (this.getModelByID(this.entitiesModels.get(entityClass)));
	}

	public final Model getModelForEntity(Entity entity) {
		return (this.getModelForEntity(entity.getClass()));
	}

	/** get a model filepath by it id */
	public final Model getModelByID(int modelID) {
		return (super.getObjectByID(modelID));
	}

	/** get the model instance for the given entity */
	public final ModelInstance getModelInstance(Entity entity) {
		return (this.entitiesModelInstance.get(entity));
	}

	/** get the list of the model instances using the given model */
	public final ArrayList<ModelInstance> getModelInstances(Model model) {
		return (this.modelsModelInstances.get(model));
	}

	@Override
	protected void onObjectRegistered(Model model) {
		Logger.get().log(Logger.Level.FINE, "new model registered", model);
	}

}
