package com.grillecube.client.renderer.model;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.grillecube.client.renderer.model.animation.Animation;
import com.grillecube.client.renderer.model.dae.ColladaLoader;
import com.grillecube.client.renderer.model.dae.datastructures.ModelData;
import com.grillecube.common.JSONHelper;

public class Model {

	/** the maximum number of joints transformations which affects a vertex */
	public static final int MAX_WEIGHTS = 3;

	/** the model name */
	private String name;

	/** the model mesh */
	private ModelMesh mesh;

	/** the model skeleton */
	private ModelSkeleton skeleton;

	/** the skin list for this model */
	private ArrayList<ModelSkin> skins;

	/** the animation list for this model */
	private ArrayList<Animation> animations;

	public Model() {
	}

	/** initialize this model */
	public void initialize() {
		this.mesh = new ModelMesh();
		this.skeleton = new ModelSkeleton();
		this.skins = new ArrayList<ModelSkin>();
		this.animations = new ArrayList<Animation>();

		this.mesh.initialize();
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

	/** get the animations this model can play */
	public ArrayList<Animation> getAnimations() {
		return (this.animations);
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

	public void set(String dirpath) throws JSONException, IOException {
		JSONObject json = new JSONObject(JSONHelper.readFile(dirpath + "info.json"));
		String modelpath = dirpath + json.getString("model");
		ModelData modelData = ColladaLoader.loadColladaModel(modelpath, MAX_WEIGHTS);
		this.mesh.set(modelData.getMeshData());
		this.skeleton.set(modelData.getSkeletonData());

		JSONArray skins = json.getJSONArray("skins");
		for (int i = 0; i < skins.length(); i++) {
			JSONObject skin = skins.getJSONObject(i);
			String name = dirpath + skin.getString("name");
			String texture = dirpath + skin.getString("texture");
			this.addSkin(new ModelSkin(name, texture));
		}
	}
}
