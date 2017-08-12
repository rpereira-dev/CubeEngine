package com.grillecube.client.resources;

import java.util.ArrayList;
import java.util.HashMap;

import com.grillecube.client.renderer.model.Model;
import com.grillecube.client.renderer.model.instance.ModelInstance;
import com.grillecube.common.Logger;
import com.grillecube.common.event.EventCallback;
import com.grillecube.common.resources.GenericManager;
import com.grillecube.common.resources.ResourceManager;
import com.grillecube.common.world.entity.Entity;
import com.grillecube.common.world.events.EventEntityDespawn;
import com.grillecube.common.world.events.EventEntitySpawn;

/** handle models */
public class ModelManager extends GenericManager<Model> {

	/** the model hashmap */
	private final HashMap<Class<? extends Entity>, Integer> entitiesModels;

	/** the model instances using the given model */
	private final HashMap<Model, ArrayList<ModelInstance>> modelsModelInstances;

	/** the model instances of the entities */
	private final HashMap<Entity, ModelInstance> entitiesModelInstance;

	/** the model manager */
	public ModelManager(ResourceManager resourceManager) {
		super(resourceManager);
		this.entitiesModels = new HashMap<Class<? extends Entity>, Integer>();
		this.modelsModelInstances = new HashMap<Model, ArrayList<ModelInstance>>();
		this.entitiesModelInstance = new HashMap<Entity, ModelInstance>();
	}

	@Override
	protected final void onInitialized() {
	}

	@Override
	protected final void onStopped() {
	}

	@Override
	protected final void onCleaned() {
		this.entitiesModels.clear();
	}

	@Override
	protected final void onLoaded() {

		this.getResourceManager().getEventManager().registerEventCallback(new EventCallback<EventEntitySpawn>() {
			@Override
			public void invoke(EventEntitySpawn event) {

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

		this.getResourceManager().getEventManager().registerEventCallback(new EventCallback<EventEntityDespawn>() {
			@Override
			public void invoke(EventEntityDespawn event) {
				Entity entity = event.getEntity();
				ModelInstance modelInstance = getModelInstance(entity);
				if (modelInstance == null) {
					return;
				}

				Model model = modelInstance.getModel();
				ArrayList<ModelInstance> modelInstances = modelsModelInstances.get(model);
				modelInstances.remove(modelInstance);

				if (modelInstances.size() == 0) {
					modelsModelInstances.remove(modelInstance.getModel());
					model.deinitialize();
				}

				entitiesModelInstance.remove(entity);
			}
		});
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
	}

	/**
	 * register a new model, link it with the entity class, and return the model
	 * ID
	 */
	public final int registerModel(Model model) {
		return (super.registerObject(model));
	}

	/**
	 * attach an entity to a model, so when the entity spawns, a new model
	 * instance is created
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
