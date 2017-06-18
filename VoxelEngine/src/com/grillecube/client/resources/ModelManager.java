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
public class ModelManager extends GenericManager<String> {

	/** the filepath to model map */
	private final HashMap<String, Model> models;

	/** the model hashmap */
	private final HashMap<Class<? extends Entity>, Integer> entitiesModels;

	/** the model instances using the given model */
	private final HashMap<Model, ArrayList<ModelInstance>> modelsModelInstances;

	/** the model instances of the entities */
	private final HashMap<Entity, ModelInstance> entitiesModelInstance;

	/** the model manager */
	public ModelManager(ResourceManager resourceManager) {
		super(resourceManager);
		this.models = new HashMap<String, Model>();
		this.entitiesModels = new HashMap<Class<? extends Entity>, Integer>();
		this.modelsModelInstances = new HashMap<Model, ArrayList<ModelInstance>>();
		this.entitiesModelInstance = new HashMap<Entity, ModelInstance>();
	}

	@Override
	protected void onInitialized() {
	}

	@Override
	protected void onStopped() {
	}

	@Override
	protected void onCleaned() {
		this.entitiesModels.clear();
	}

	@Override
	protected void onLoaded() {

		this.getResourceManager().getEventManager().registerEventCallback(new EventCallback<EventEntitySpawn>() {
			@Override
			public void invoke(EventEntitySpawn event) {

				Entity entity = event.getEntity();
				String dirpath = getModelFilepathForEntity(entity);
				if (dirpath == null) {
					Logger.get().log(Logger.Level.ERROR, "no model for entity class", entity.getClass());
					return;
				}

				Model model = getModelByFilepath(dirpath);
				if (!model.isInitialized()) {
					model.initialize();
					try {
						model.set(dirpath);
					} catch (Exception exception) {
						model.deinitialize();
						Logger.get().log(Logger.Level.ERROR, "error when initializing model...");
						Logger.get().log(Logger.Level.ERROR, exception.getLocalizedMessage());
					}
				}
				ModelInstance modelInstance = new ModelInstance(model, entity);
				ArrayList<ModelInstance> modelInstances = modelsModelInstances.get(model);
				if (modelInstances == null) {
					modelInstances = new ArrayList<ModelInstance>(1);
					modelsModelInstances.put(model, modelInstances);
				}
				modelInstances.add(modelInstance);

				entitiesModelInstance.put(entity, modelInstance);
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
	 * register a new model, link it with the entity class, and return the model
	 * ID
	 */
	public int registerModel(String filepath) {
		this.models.put(filepath, new Model());
		return (super.registerObject(filepath));
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
	public void attachEntityToModel(Class<? extends Entity> entityClass, Integer modelID) {
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
	public Model getModelForEntity(Class<? extends Entity> entityClass) {
		return (this.getModelByID(this.entitiesModels.get(entityClass)));
	}

	public Model getModelForEntity(Entity entity) {
		return (this.getModelForEntity(entity.getClass()));
	}

	public String getModelFilepathForEntity(Class<? extends Entity> entityClass) {
		return (this.getModelFilepath(this.entitiesModels.get(entityClass)));
	}

	public String getModelFilepathForEntity(Entity entity) {
		return (this.getModelFilepathForEntity(entity.getClass()));
	}

	/**
	 * get a model by it id
	 * 
	 * @param modelID
	 *            : the modelID of the model we want
	 * @return : the model
	 */
	public Model getModelByID(int modelID) {
		return (this.getModelByFilepath(this.getModelFilepath(modelID)));
	}

	/** get a model by it filepath */
	public Model getModelByFilepath(String filepath) {
		return (this.models.get(filepath));
	}

	/** get a model filepath by it id */
	public String getModelFilepath(int modelID) {
		return (super.getObjectByID(modelID));
	}

	/** get the model instance for the given entity */
	public ModelInstance getModelInstance(Entity entity) {
		return (this.entitiesModelInstance.get(entity));
	}

	/** get the list of the model instances using the given model */
	public ArrayList<ModelInstance> getModelInstances(Model model) {
		return (this.modelsModelInstances.get(model));
	}

	@Override
	protected void onObjectRegistered(String filepath) {
		Logger.get().log(Logger.Level.FINE, "new model registered", filepath);
	}

}
