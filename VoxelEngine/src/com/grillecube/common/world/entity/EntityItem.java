package com.grillecube.common.world.entity;

import com.grillecube.client.renderer.model.Model;
import com.grillecube.common.world.World;

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
