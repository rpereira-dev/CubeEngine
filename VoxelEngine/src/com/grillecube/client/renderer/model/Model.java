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

package com.grillecube.client.renderer.model;

import java.awt.Color;
import java.util.ArrayList;

import com.grillecube.client.renderer.model.animation.ModelAnimation;
import com.grillecube.client.renderer.model.animation.ModelPartAnimation;
import com.grillecube.client.renderer.model.builder.ModelPartBuilder;
import com.grillecube.client.renderer.model.builder.ModelPartSkinBuilder;
import com.grillecube.client.renderer.model.items.ModelItem;
import com.grillecube.common.Logger;
import com.grillecube.common.maths.BoundingBox;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.resources.GenericManager;
import com.grillecube.common.resources.R;

public class Model {

	/** constants */
	public static final Class<?>[] _MODELS_CLASSES = { ModelStatic.class, ModelLiving.class, ModelLivingBiped.class,
			ModelItem.class };
	public static final String[] MODELS_INFO = { "STATIC : chair, table, boat ...", "LIVING : animal",
			"LIVING BIPED : biped animal living, human, zombie...", "ITEM : sword, equipments, tool, food" };

	@SuppressWarnings("unchecked")
	public static final Class<? extends Model> getModelClassByInfo(String info) {
		for (int i = 0; i < MODELS_INFO.length; i++) {
			if (MODELS_INFO[i].equals(info)) {
				return (Class<? extends Model>) (_MODELS_CLASSES[i]);
			}
		}
		return (null);
	}

	/** model name */
	private String _name;

	/** the axis origin */
	private Vector3f _origin;

	/**
	 * axis.x is the rotation around X axis, axis.y around Y axis, axis.z around
	 * Z axis
	 */
	private Vector3f _axis;

	/** model parts */
	private ArrayList<ModelPart> _parts;
	private ArrayList<ModelSkin> _skins;
	private ArrayList<ModelAnimation> _animations;
	private ArrayList<ModelAttachmentPoint> _attach_points;

	/** building colors (model builder) */
	private ArrayList<Color> _colors;

	/** bounding box which merge every boxes of this model as a single one */
	private BoundingBox _global_box;

	/** the bounding box with the greatest area */
	private BoundingBox _max_box;

	/** model id */
	private final int _id;

	/**
	 * file path (so the model can be unloaded if not used and realoaded when
	 * used)
	 */
	private String _filepath;

	public Model() {
		this(null);
	}

	public Model(int id) {
		this.init();
		this._id = id;
	}

	public Model(String name) {
		this.init();
		this._name = name;
		this._id = GenericManager.ERROR_OBJECT_ID;
	}

	private final void init() {
		this._axis = new Vector3f();
		this.setAxis(Vector3f.NULL_VEC);
		this._origin = new Vector3f();
		this.setOrigin(Vector3f.NULL_VEC);
		this._parts = new ArrayList<ModelPart>();
		this._skins = new ArrayList<ModelSkin>();
		this._animations = new ArrayList<ModelAnimation>();
		this._attach_points = new ArrayList<ModelAttachmentPoint>();
		this._global_box = new BoundingBox();
		this._max_box = new BoundingBox();
		this._name = null;
	}

	public void setFilepath(String filepath) {
		this._filepath = filepath;
	}

	public String getFilepath() {
		return (this._filepath);
	}

	/** return model parts */
	public ArrayList<ModelPart> getParts() {
		return (this._parts);
	}

	public ModelAttachmentPoint getAttachmentPoint(int id) {
		if (id < 0 || id >= this._attach_points.size()) {
			return (null);
		}
		return (this._attach_points.get(id));
	}

	public ModelAttachmentPoint getAttachmentPoint(String name) {
		for (ModelAttachmentPoint point : this._attach_points) {
			if (point.getName().equals(name)) {
				return (point);
			}
		}
		return (null);
	}

	public boolean addAttachmentPoint(ModelAttachmentPoint point) {
		this._attach_points.add(point);
		return (true);
	}

	public boolean removeAttachmentPoint(ModelAttachmentPoint point) {
		return (this._attach_points.remove(point));
	}

	public ArrayList<ModelSkin> getSkins() {
		return (this._skins);
	}

	/** add an animation to the modelpart */
	public void addAnimation(ModelAnimation animation) {
		this._animations.add(animation);
		for (int i = 0; i < this._parts.size(); i++) {
			animation.addPart(new ModelPartAnimation());
		}
	}

	public void removeAnimation(ModelAnimation animation) {
		this._animations.remove(animation);
	}

	/** delete the model */
	public void delete() {
		for (ModelPart part : this._parts) {
			part.delete();
		}
	}

	/** return number of model parts */
	public int getPartsCount() {
		return (this._parts.size());
	}

	/** set model name */
	public void setName(String str) {
		this._name = str;
	}

	/** set a model part to this model */
	public void addPart(ModelPart part) {
		this._parts.add(part);
		for (ModelSkin skin : this._skins) {
			skin.addPart(new ModelPartSkin());
		}
		for (ModelAnimation animation : this._animations) {
			animation.addPart(new ModelPartAnimation());
		}
	}

	/** set a model part to this model */
	public void addPartBuilder(ModelPartBuilder part) {
		this._parts.add(part);
		for (ModelSkin skin : this._skins) {
			skin.addPart(new ModelPartSkinBuilder());
		}
		for (ModelAnimation animation : this._animations) {
			animation.addPart(new ModelPartAnimation());
		}
	}

	/** remove a model part from this model */
	public void removePart(ModelPart part) {
		int index = this._parts.indexOf(part);
		if (index == -1) {
			return;
		}
		this._parts.remove(index);
		for (ModelSkin skin : this._skins) {
			skin.removePart(index);
		}
		for (ModelAnimation animation : this._animations) {
			animation.removePart(index);
		}
	}

	@Override
	public String toString() {
		return ("Model(" + this._name + ")");
	}

	/** return the part at the given index */
	public ModelPart getPartAt(int i) {
		if (i < 0 || i >= this._parts.size()) {
			return (null);
		}
		return (this._parts.get(i));
	}

	/** return the model name */
	public String getName() {
		return (this._name);
	}

	/** recalculate bounding boxes */
	public void updateBox() {

		// set the global box to 0
		if (this._parts == null) {
			this._global_box.set(BoundingBox.EMPTY_BOX);
			this._max_box.set(BoundingBox.EMPTY_BOX);
		} else if (this._parts.size() > 0) {
			BoundingBox maxbox = null;
			float maxarea = Float.NEGATIVE_INFINITY;
			this._global_box.set(this._parts.get(0).getBoundingBox());

			int i;
			for (i = 1; i < this._parts.size(); i++) {
				ModelPart part = this._parts.get(i);
				BoundingBox box = part.getBoundingBox();
				float boxarea = box.getArea();
				if (boxarea >= maxarea) {
					maxbox = box;
					maxarea = boxarea;
				}
				if (boxarea != 0) {
					this._global_box.join(box);
				}
			}

			if (maxbox != null) {
				this._max_box.set(maxbox);
			}
		}
	}

	/** return a bounding box which merged every model part bouding boxes */
	public BoundingBox getGlobalBoundingBox() {
		return (this._global_box);
	}

	/**
	 * return the bounding box of the model part with the greatest bounding box
	 * (in term of area)
	 */
	public BoundingBox getMaxBoundingBox() {
		return (this._max_box);
	}

	public int getID() {
		return (this._id);
	}

	/** add a skin to the model */
	public int newSkin(String name) {
		int skinID = this._skins.size();
		ModelSkin skin = new ModelSkin(name);
		this._skins.add(skin);
		for (ModelPart part : this._parts) {
			skin.addPart(new ModelPartSkin());
		}
		return (skinID);
	}

	public int newSkinBuilder(String name) {
		int skinID = this._skins.size();
		ModelSkin skin = new ModelSkin(name);
		this._skins.add(skin);
		for (ModelPart part : this._parts) {
			skin.addPart(new ModelPartSkinBuilder());
		}
		return (skinID);
	}

	/** remove a skin from the model */
	public void removeSkin(ModelSkin skin) {
		this._skins.remove(skin);
	}

	public int getAnimationCount() {
		return (this._animations.size());
	}

	public ModelAnimation getAnimation(int animationID) {
		return (this._animations.get(animationID));
	}

	public ArrayList<ModelAnimation> getAnimations() {
		return (this._animations);
	}

	public int getAnimationID(ModelAnimation animation) {
		return (this._animations.indexOf(animation));
	}

	/** return the id for the given model part */
	public int getPartID(ModelPart part) {
		for (int i = 0; i < this._parts.size(); i++) {
			if (this._parts.get(i) == part) {
				return (i);
			}
		}
		return (-1);
	}

	/** prepare the model when initialized as a model builder */
	public void prepareModelBuilder() {
		this._colors = new ArrayList<Color>();
		this.newSkinBuilder(R.getWord("skin_default"));
	}

	public ArrayList<Color> getColors() {
		return (this._colors);
	}

	public void removeColor(Color color) {
		this._colors.remove(color);
	}

	public void addColor(Color color) {
		if (this._colors.contains(color)) {
			return;
		}
		this._colors.add(color);
	}

	public void addColor(int color) {
		this.addColor(new Color(color));
	}

	/** this model referencial (axis) */
	public void setAxis(Vector3f axis) {
		this.setAxis(axis.x, axis.y, axis.z);
	}

	/** this model global offset (origin) */
	public void setOrigin(Vector3f axis) {
		this.setOrigin(axis.x, axis.y, axis.z);
	}

	public void setAxis(float x, float y, float z) {
		this._axis.set(x, y, z);
	}

	public void setOrigin(float x, float y, float z) {
		this._origin.set(x, y, z);
	}

	public Vector3f getAxis() {
		return (this._axis);
	}

	public Vector3f getOrigin() {
		return (this._origin);
	}

	public ModelSkin getSkin(int skinID) {
		if (skinID < 0 || skinID >= this._skins.size()) {
			return (null);
		}
		return (this._skins.get(skinID));
	}

	private static final ModelMesher mesher = new ModelMesherCull();
	// private static final ModelMesher mesher = new ModelMesherGreedy();

	/** should only be called on editor */
	public void refreshVertices() {

		Logger.get().log(Logger.Level.DEBUG, "refreshing model vertices");

		int partID;
		for (partID = 0; partID < this._parts.size(); partID++) {
			ModelPartBuilder part = (ModelPartBuilder) this._parts.get(partID);

			// update mesher vertices
			mesher.updateVertexStack(part);

			// set model vertices
			part.setVertices(mesher.generateModelPartVertices());

			// for each skin
			for (ModelSkin skin : this._skins) {
				// update vertices
				ModelPartSkinBuilder skinpart = (ModelPartSkinBuilder) skin.getPart(partID);
				skinpart.setVertices(mesher.generateSkinPartVertices(skinpart));
			}
		}
	}
}
