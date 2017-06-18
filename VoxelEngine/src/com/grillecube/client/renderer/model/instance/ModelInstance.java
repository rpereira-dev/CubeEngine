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

import com.grillecube.client.renderer.model.Model;
import com.grillecube.common.world.entity.Entity;

public class ModelInstance {
	/** entity reference */
	private Entity entity;

	/** model reference */
	private Model model;

	/** the skeleton instance */
	private ModelSkeletonInstance skeleton;

	/** curent skin id */
	private int skinID;

	public ModelInstance(Model model, Entity entity) {
		this.entity = entity;
		this.skinID = 0;
		this.model = model;
		this.skeleton = new ModelSkeletonInstance(this);
	}

	/** get model from this model instance */
	public Model getModel() {
		return (this.model);
	}

	public void toggleSkin(int skinID) {
		this.skinID = skinID;
	}

	public int getSkinID() {
		return (this.skinID);
	}

	/** update the model */
	public void update() {
		this.skeleton.update();
	}

	/** @return : get tthis model instance entity */
	public Entity getEntity() {
		return (this.entity);
	}

	public ModelSkeletonInstance getSkeletonInstance() {
		return (this.skeleton);
	}
}
