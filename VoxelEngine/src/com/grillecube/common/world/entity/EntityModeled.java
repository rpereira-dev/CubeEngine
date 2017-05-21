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

package com.grillecube.common.world.entity;

import java.util.ArrayList;

import com.grillecube.client.renderer.model.BoundingBox;
import com.grillecube.client.renderer.model.Model;
import com.grillecube.client.renderer.model.instance.ModelInstance;
import com.grillecube.client.sound.ALH;
import com.grillecube.client.sound.ALSound;
import com.grillecube.common.Logger;
import com.grillecube.common.maths.Maths;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.resources.SoundManager;
import com.grillecube.common.world.World;
import com.grillecube.common.world.entity.physic.EntityPhysic;

/** entity that has a model */
public abstract class EntityModeled extends Entity {

	/** model instance of this entity */
	private ModelInstance _model_instance;

	public EntityModeled(World world) {
		super(world);
		this._model_instance = new ModelInstance(this);
		this.enablePhysic(EntityPhysic.AIR_FRICTION);
		this.enablePhysic(EntityPhysic.GRAVITY);
	}

	public EntityModeled(World world, Model model) {
		this(world);
		this.setModel(model);
	}

	@Override
	public void update() {
		super.update();
		this._model_instance.update();
	}

	public void toggleSkin(int skinID) {
		this._model_instance.toggleSkin(skinID);
	}

	public void setModel(Model model) {
		this._model_instance.setModel(model);
		if (model != null) {
			super.setHeight(model.getGlobalBoundingBox().getSize().y);
			super.setWeight(model.getGlobalBoundingBox().getArea());
		} else {
			super.setHeight(0.01f);
			super.setWeight(0.01f);
		}
	}

	/** return model instance for this entity */
	public ModelInstance getModelInstance() {
		return (this._model_instance);
	}

	@Override
	public boolean move(float dx, float dy, float dz) {

		final float length = Maths.sqrt(dx * dx + dy * dy + dz * dz);
		if (length >= 1) { // if the entity has to mov emore than 1 unit, it
							// mean we have to check every block , in order not
							// to miss any collisions

			final int n = Maths.floor(length); // get the number of normalized
												// movement to apply
			final float dux = dx / length; // calculate normalized movements
			final float duy = dy / length;
			final float duz = dz / length;

			boolean moved = false;
			for (int i = 0; i < n; i++) { // iterate and try move the entity n
											// times by a normalized movement
				if (!this.smallMove(dux, duy, duz)) { // if the entity couldnt
														// do a small move, a
														// collision occured,
														// stop here
					return (moved);
				} else { // else the entity has moved, continue moving :)
					moved = true;
				}
			}

			// here the entity has moved n times the normalized movement,
			// but the entity still have to move the remaining length
			float rx = dx - n * dux; // calculate the remaining
			float ry = dy - n * duy;
			float rz = dz - n * duz;
			moved = moved | this.smallMove(rx, ry, rz); // move it
			return (moved); // return if the entity has moved
		}
		return (this.smallMove(dx, dy, dz)); // else the move length i < 1 , so
												// we can just apply 1 single
												// small move (this case should
												// appear most of the time, as
												// an entity shouldnt move too
												// fastly)
	}

	// maybe reduce this precision , to test with a lot of entities...
	private static final int SMALL_MOVE_MAX_TEST = 16; // allow a precision in
														// movement up to 1 / 16
														// = 0.06 in term of
														// dimensions

	/** move the entity with a delta position with a length < 1 */
	private boolean smallMove(float dx, float dy, float dz) {

		ArrayList<BoundingBox> boxes = new ArrayList<BoundingBox>();
		BoundingBox box = new BoundingBox();
		int i = 0;

		while (i < SMALL_MOVE_MAX_TEST) {

			box.set(this.getBoundingBox());
			box.translate(dx, dy, dz);
			this.getWorld().getCollidingBoundingBox(this, box, boxes);
			if (boxes.size() == 0) {
				this.getBoundingBox().translate(dx, dy, dz); // update the
																// bounding box
																// for the next
																// possible
																// iteration
				return (super.move(dx, dy, dz));
			}

			// reduce the movement and try again
			dx *= 0.5f;
			dy *= 0.5f;
			dz *= 0.5f;

			++i;
		}

		return (false);
	}

	@Override
	public BoundingBox getBoundingBox() {
		return (this._model_instance.getGlobalBoundingBox());
	}

	public BoundingBox getRawBoundingBox() {
		return (this._model_instance.getModel().getGlobalBoundingBox());
	}

	public Vector3f getScale() {
		return (Vector3f.ONE_VEC);
	}
}
