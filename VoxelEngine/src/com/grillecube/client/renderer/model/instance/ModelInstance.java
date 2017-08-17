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

import java.util.ArrayList;
import java.util.Iterator;

import com.grillecube.client.renderer.model.Model;
import com.grillecube.client.renderer.model.ModelSkeleton;
import com.grillecube.client.renderer.model.animation.ModelSkeletonAnimation;
import com.grillecube.common.world.entity.Entity;

public class ModelInstance {
	/** entity reference */
	private final Entity entity;

	/** model reference */
	private final Model model;

	/** the skeleton instance */
	private final ModelSkeletonInstance skeleton;

	/** the animation list for this model */
	private final ArrayList<AnimationInstance> animationInstances;

	/** curent skin id */
	private int skinID;

	private long lastUpdate;

	public ModelInstance(Model model, Entity entity) {
		this.entity = entity;
		this.skinID = 0;
		this.model = model;
		this.skeleton = new ModelSkeletonInstance(model.getSkeleton());
		this.animationInstances = new ArrayList<AnimationInstance>();

		// init animations
		ModelSkeletonAnimation modelAnimation = model.getAnimation(0);
		if (modelAnimation != null) {
			AnimationInstance instance = new AnimationInstance(modelAnimation);
			instance.loop();
			this.animationInstances.add(instance);
		}
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
		long curr = System.currentTimeMillis();
		long dt = curr - this.lastUpdate;

		this.updateAnimations(dt);
		this.skeleton.update(this.animationInstances);

		this.lastUpdate = curr;
	}

	private void updateAnimations(long dt) {
		// update animation
		Iterator<AnimationInstance> iterator = this.animationInstances.iterator();
		while (iterator.hasNext()) {
			AnimationInstance animationInstance = iterator.next();
			animationInstance.update(dt);
			if (animationInstance.isStopped()) {
				iterator.remove();
			}
		}
	}

	/** @return : get this model instance entity */
	public Entity getEntity() {
		return (this.entity);
	}

	/** get the skeleton instance */
	public ModelSkeleton getSkeleton() {
		return (this.skeleton);
	}

	/** get the animations this model can play */
	public ArrayList<AnimationInstance> getAnimationInstances() {
		return (this.animationInstances);
	}
}
