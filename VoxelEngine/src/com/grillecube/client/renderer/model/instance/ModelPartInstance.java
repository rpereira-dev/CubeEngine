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
import com.grillecube.client.renderer.model.ModelPart;
import com.grillecube.client.renderer.model.animation.ModelAnimationInstance;
import com.grillecube.client.renderer.model.animation.ModelPartAnimationInstance;
import com.grillecube.common.Logger;
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.entity.EntityModeled;

public class ModelPartInstance {

	/** the model part */
	private ModelPart _model_part;

	/** bounding box */
	private BoundingBox _box;

	/** transf matrix */
	private Matrix4f _transf_matrix;

	/** attached models */
	private ModelInstance[] _attached_models;

	public ModelPartInstance(Model model, ModelPart part) {
		this._model_part = part;
		this._box = new BoundingBox();
		this._transf_matrix = new Matrix4f();
		this._attached_models = new ModelInstance[part.getAttachmentPoints().size()];
	}

	public boolean attachModel(ModelInstance model, int pointID) {
		if (pointID < 0 || pointID >= this._attached_models.length) {
			return (false);
		}
		this._attached_models[pointID] = model;
		return (true);
	}

	public void detachModel(int pointID) {
		if (pointID < 0 || pointID >= this._attached_models.length) {
			return;
		}
		this._attached_models[pointID] = null;
	}

	/** return model part which is used by this instance */
	public ModelPart getModelPart() {
		return (this._model_part);
	}

	public BoundingBox getBoundingBox() {
		return (this._box);
	}

	/** update the model part instance for the given entity */
	public void update(EntityModeled entity, ModelAnimationInstance[] animation_instance, int partID) {

		// update bounding box
		this._box.set(this.getModelPart().getBoundingBox());
		this._box.translate(entity.getPosition());
		this._box.rotate(entity.getRotation(), entity.getPosition());

		this.updateTransformation(entity, animation_instance, partID);
	}

	private Vector3f vb_pos = new Vector3f();
	private Vector3f vb_rot = new Vector3f();
	private Vector3f vb_scale = new Vector3f();
	private Vector3f vb_offset = new Vector3f();

	private Vector3f vb_a_pos = new Vector3f();
	private Vector3f vb_a_rot = new Vector3f();
	private Vector3f vb_a_scale = new Vector3f();
	private Vector3f vb_a_offset = new Vector3f();

	private Vector3f vecbuffer = new Vector3f();

	private void updateTransformation(EntityModeled ent, ModelAnimationInstance[] animations, int partID) {

		// set identity
		this._transf_matrix.setIdentity();

		// get the model
		Model model = ent.getModelInstance().getModel();
		ModelPart part = this.getModelPart();

		if (model == null || part == null) {
			return;
		}

		// place the model part relatively to the model
		this.vb_pos.set(model.getOrigin());
		this.vb_rot.set(model.getAxis());
		this.vb_scale.set(part.getBlockScale());
		this.vb_offset.set(0);

		// place the model part relatively to the entity
		this.vb_pos.add(ent.getPosition());
		this.vb_rot.add(ent.getRotation());
		 this.vb_scale.scale(ent.getScale());

		// place the model part relatively to the animation it is playing
		if (animations != null && animations.length > 0) {

			this.vb_a_pos.set(model.getOrigin());
			this.vb_a_rot.set(model.getAxis());
			this.vb_a_scale.set(1);
			this.vb_a_offset.set(0);

			int anim_count = 0;
			for (ModelAnimationInstance animation : animations) {

				if (animation.isStopped()) {
					continue;
				}

				++anim_count;

				// get the animation for this part instance
				ModelPartAnimationInstance part_animation = animation.getPart(partID);

				// get it transformation value
				Vector3f translation = part_animation.getInterpolatedTranslation();
				Vector3f offset = part_animation.getInterpolatedOffset();
				Vector3f rot = part_animation.getInterpolatedRotation();
				Vector3f scale = part_animation.getInterpolatedScaling();

				this.vb_a_pos.add(translation);
				this.vb_a_rot.add(rot);
				this.vb_a_scale.scale(scale);
				this.vb_a_offset.add(offset);
			}

			// Logger.get().log(Logger.Level.DEBUG, anim_count);

			if (anim_count > 0) {
				// Logger.get().log(Logger.Level.DEBUG, anim_count);
				float factor = 1 / (float) anim_count;

				this.vb_a_pos.scale(factor);
				this.vb_a_rot.scale(factor);
				this.vb_a_offset.scale(factor);

				// Logger.get().log(Logger.Level.DEBUG, this.vb_a_pos);
				// Logger.get().log(Logger.Level.DEBUG, this.vb_a_rot);
				// Logger.get().log(Logger.Level.DEBUG, this.vb_a_scale);
				// Logger.get().log(Logger.Level.DEBUG, this.vb_a_offset);
				// Logger.get().log(Logger.Level.DEBUG, "");

				this.vb_pos.add(this.vb_a_pos);
				this.vb_rot.add(this.vb_a_rot);
				this.vb_scale.scale(this.vb_a_scale);
				this.vb_offset.add(this.vb_a_offset);
			}
		}

		Matrix4f.createTransformationMatrixWithOffset(this._transf_matrix, this.vb_pos, this.vb_rot, this.vb_offset,
				this.vb_scale);
	}

	// private Matrix4f createAnimationMatrix(Vector3f translation, Vector3f
	// offset, Vector3f rotation, Vector3f scaling) {
	// animation_matrix.setIdentity();
	//
	// animation_matrix.translate(translation);
	//
	// animation_matrix.translate(offset); // set rotation point
	// animation_matrix.rotate(rotation.x, Vector3f.AXIS_X);
	// animation_matrix.rotate(rotation.y, Vector3f.AXIS_Y);
	// animation_matrix.rotate(rotation.z, Vector3f.AXIS_Z);
	// animation_matrix.translate(offset.negate(vecbuffer));
	//
	// animation_matrix.scale(scaling);
	// return (animation_matrix);
	// }

	public Matrix4f getTransformationMatrix() {
		return (this._transf_matrix);
	}
}
