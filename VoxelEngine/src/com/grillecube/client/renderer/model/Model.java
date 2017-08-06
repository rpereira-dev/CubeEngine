package com.grillecube.client.renderer.model;

import java.util.ArrayList;

import com.grillecube.client.renderer.model.animation.ModelAnimation;

public class Model {

	/** the maximum number of joints transformations which affects a vertex */
	public static final int MAX_WEIGHTS = 3;

	/** the model name */
	private String name;

	/** the model mesh */
	private ModelMesh mesh;

	/** the model skeleton */
	private ModelSkeleton skeleton;

	/** the model animations */
	private ArrayList<ModelAnimation> animations;

	/** the skin list for this model */
	private ArrayList<ModelSkin> skins;

	/** model initialized callback */
	private final ModelInitializer modelInitializer;

	public Model() {
		this(null);
	}

	public Model(ModelInitializer modelInitializer) {
		this.modelInitializer = modelInitializer;
	}

	/** initialize this model */
	public void initialize() {
		this.mesh = new ModelMesh();
		this.skeleton = new ModelSkeleton();
		this.skins = new ArrayList<ModelSkin>();
		this.animations = new ArrayList<ModelAnimation>();

		this.mesh.initialize();

		if (this.modelInitializer != null) {
			this.modelInitializer.onInitialized(this);
		}
	}

	/** deinitialize this model */
	public void deinitialize() {
		this.mesh.deinitialize();

		this.skeleton = null;
		this.mesh = null;
		this.skins = null;
		this.animations = null;
	}

	/**
	 * @return : true if this model is already initialized
	 */
	public final boolean isInitialized() {
		return (this.mesh != null);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return (this.name);
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/** get the skin for this model */
	public ArrayList<ModelSkin> getSkins() {
		return (this.skins);
	}

	/** get this model mesh */
	public ModelMesh getMesh() {
		return (this.mesh);
	}

	/** get this model skeleton */
	public ModelSkeleton getSkeleton() {
		return (this.skeleton);
	}

	/** add a skin to this model and return it id */
	public int addSkin(ModelSkin modelSkin) {
		int ID = this.skins.size();
		this.skins.add(modelSkin);
		return (ID);
	}

	/**
	 * 
	 * @param the
	 *            skin ID
	 * @return : the skin
	 */
	public ModelSkin getSkin(int skinID) {
		if (skinID < 0 || skinID >= this.skins.size()) {
			return (null);
		}
		return (this.skins.get(skinID));
	}

	/** add an animation to the model */
	public void addAnimation(ModelAnimation animation) {
		this.animations.add(animation);
	}

	/** get the animations this model can play */
	public ModelAnimation getAnimation(int id) {
		if (id < 0 || id >= this.animations.size()) {
			return (null);
		}
		return (this.animations.get(id));
	}

	/** clone this model so it has it own mesh (destructible then) */
	public final Model clone() {
		Model model = new Model();
		model.initialize();
		model.getMesh().setVertices(model.getMesh().getVertices());
		return (model);
	}
}
