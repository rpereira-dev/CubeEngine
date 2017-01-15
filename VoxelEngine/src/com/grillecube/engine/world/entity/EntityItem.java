package com.grillecube.engine.world.entity;

import com.grillecube.engine.renderer.model.Model;
import com.grillecube.engine.world.World;

/**
 * represent an item entity (an item on ground basically)
 */
public class EntityItem extends EntityModeled {

	public EntityItem(World world) {
		super(world);
	}

	public EntityItem(World world, Model model) {
		super(world, model);
	}

	@Override
	protected void onUpdate() {
		this.increaseYaw(0.01f);
	}
}
