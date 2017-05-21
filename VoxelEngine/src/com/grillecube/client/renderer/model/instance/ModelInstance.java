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

package com.grillecube.client.renderer.model.instance;

import com.grillecube.client.renderer.model.BoundingBox;
import com.grillecube.client.renderer.model.Model;
import com.grillecube.client.renderer.model.animation.ModelAnimation;
import com.grillecube.client.renderer.model.animation.ModelAnimationInstance;
import com.grillecube.common.Logger;
import com.grillecube.common.maths.Vector4f;
import com.grillecube.common.world.entity.Entity;
import com.grillecube.common.world.entity.EntityModeled;

public class ModelInstance {
	/** entit reference */
	private EntityModeled _entity;

	/** model reference */
	private Model _model;

	/** model instance boxes */
	private BoundingBox _global_box;
	private BoundingBox _max_box;

	/** model parts instances */
	private ModelPartInstance[] _parts_instances;
	private ModelAnimationInstance[] _animation_instance;

	/**
	 * the model instance on which 'this' is attached to (can be null is not
	 * attached to any other models)
	 */
	private ModelPartInstance _attached_to;
	private int _attachment_id;

	/** curent skin id */
	private int _skinID;

	public ModelInstance(EntityModeled entity) {
		this._global_box = new BoundingBox();
		this._max_box = new BoundingBox();
		this.setEntity(entity);
		this.setModel(new Model());
	}

	public ModelInstance(Model model) {
		this.setModel(model);
	}

	public void attachTo(ModelPartInstance other, int attachmentID) {
		if (other.attachModel(this, attachmentID)) {
			this._attached_to = other;
			this._attachment_id = attachmentID;
		}
	}

	public void detach() {
		if (this._attached_to == null) {
			return;
		}
		this._attached_to.detachModel(this._attachment_id);
		this._attached_to = null;
	}

	/** return the model instance of which 'this' is attached */
	public ModelPartInstance getAttachedTo() {
		return (this._attached_to);
	}

	private void setEntity(EntityModeled entity) {
		this._entity = entity;
	}

	public void setModel(Model model) {

		this._model = model;
		this.resetModel();
	}

	public void resetModel() {
		this.resetModelPartInstances();
		this.resetAnimationInstances();
		this.resetBoundingBoxes();
	}

	private void resetBoundingBoxes() {

		Model model = this.getModel();
		if (model == null) {
			this._global_box.set(BoundingBox.EMPTY_BOX);
			this._max_box.set(BoundingBox.EMPTY_BOX);
		} else {
			this._global_box.set(model.getGlobalBoundingBox());
			this._max_box.set(model.getMaxBoundingBox());
		}

	}

	private void resetAnimationInstances() {

		Model model = this.getModel();
		if (model == null) {
			this._animation_instance = new ModelAnimationInstance[0];
		} else {
			// prepare animation instances
			int animationcount = model.getAnimationCount();
			this._animation_instance = new ModelAnimationInstance[animationcount];
			for (int i = 0; i < animationcount; i++) {
				this._animation_instance[i] = new ModelAnimationInstance(model.getAnimation(i));
			}
		}
	}

	private void resetModelPartInstances() {
		Model model = this.getModel();
		if (model == null) {
			this._parts_instances = new ModelPartInstance[0];
		} else {
			int partcount = model.getPartsCount();
			this._parts_instances = new ModelPartInstance[partcount];
			for (int i = 0; i < partcount; i++) {
				this._parts_instances[i] = new ModelPartInstance(model, model.getPartAt(i));
			}
		}
	}

	/** get model from this model instance */
	public Model getModel() {
		return (this._model);
	}

	public ModelAnimationInstance getAnimationInstance(int animationID) {

		if (animationID < 0 || animationID >= this._animation_instance.length) {
			Logger.get().log(Logger.Level.DEBUG, "Tried to start an unknown animation id", this, animationID);
			return (null);
		}

		return (this._animation_instance[animationID]);
	}

	public ModelAnimationInstance getAnimationInstance(String name) {
		for (ModelAnimationInstance instance : this._animation_instance) {
			if (instance.getAnimation().getName().toLowerCase().equals(name.toLowerCase())) {
				return (instance);
			}
		}
		return (null);
	}

	public ModelAnimationInstance getAnimationInstance(ModelAnimation animation) {
		for (ModelAnimationInstance instance : this._animation_instance) {
			if (instance.getAnimation().equals(animation)) {
				return (instance);
			}
		}
		return (null);
	}

	/** return true if the model part is playing an animation */
	public boolean isPlayingAnyAnimations() {
		for (ModelAnimationInstance instance : this._animation_instance) {
			if (instance.isPlaying()) {
				return (true);
			}
		}
		return (false);
	}

	public boolean isLoopingAnyAnimations(ModelAnimation animation) {
		for (ModelAnimationInstance instance : this._animation_instance) {
			if (instance.getAnimation().equals(animation)) {
				return (instance.isLooping());
			}
		}
		return (false);
	}

	public ModelPartInstance[] getPartInstances() {
		return (this._parts_instances);
	}

	public void toggleSkin(int skinID) {
		this._skinID = skinID;
	}

	public int getSkinID() {
		return (this._skinID);
	}

	/** update the model */
	public void update() {

		if (this.getModel() == null) {
			return;
		}

		// update bounding box
		this.updateBoundingBoxes();

		// update animations
		for (ModelAnimationInstance instance : this._animation_instance) {
			instance.update();
		}

		// update model parts
		int partID = 0;
		for (ModelPartInstance instance : this._parts_instances) {
			instance.update(this._entity, this._animation_instance, partID);
			++partID;
		}
	}

	private void updateBoundingBoxes() {
		this._max_box.set(this.getModel().getMaxBoundingBox());
		this._max_box.translate(this._entity.getPosition());
		this._max_box.rotate(this._entity.getRotation(), this._entity.getPosition());
		this._max_box.setColor(new Vector4f(0.0f, 0.0f, 1.0f, 1.0f));

		this._global_box.set(this.getModel().getGlobalBoundingBox());
		this._global_box.translate(this._entity.getPosition());
		this._global_box.rotate(this._entity.getRotation(), this._entity.getPosition());
		this._global_box.setColor(new Vector4f(0.0f, 1.0f, 0.0f, 1.0f));

	}

	/** get tthis model instance entity */
	public Entity getEntity() {
		return (this._entity);
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

	public ModelPartInstance getPart(int partID) {
		if (partID < 0 || partID >= this._parts_instances.length) {
			return (null);
		}
		return (this._parts_instances[partID]);
	}

	public void playAnimation(ModelAnimation animation) {
	}
}
