package com.grillecube.common.world.entity.physic;

import com.grillecube.client.renderer.model.instance.ModelInstance;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.entity.Entity;
import com.grillecube.common.world.entity.EntityModeled;

public class EntityPhysicMoveBackward extends EntityPhysic {
	private Vector3f move;

	public EntityPhysicMoveBackward() {
		this.move = new Vector3f();
	}

	@Override
	public void onEnable(Entity entity) {

	}

	@Override
	public void onDisable(Entity entity) {
		if (entity instanceof EntityModeled) {
			EntityModeled model = (EntityModeled) entity;
			ModelInstance instance = model.getModelInstance();
			if (instance != null && instance.isPlayingAnyAnimations()) {
				instance.getAnimationInstance(0).stopPlay();
			}
		}
	}

	@Override
	public void onUpdate(Entity entity) {
		this.move.set(entity.getViewVector());
		this.move.y = 0;
		this.move.normalise();
		this.move.scale(entity.getSpeed() / 3.0f);
		this.move.negate(this.move);
		entity.move(this.move);

		if (entity instanceof EntityModeled) {
			EntityModeled model = (EntityModeled) entity;
			ModelInstance instance = model.getModelInstance();
			if (instance != null && !instance.isPlayingAnyAnimations()) {
				instance.getAnimationInstance(0).startPlay();
			}
		}
	}
}
