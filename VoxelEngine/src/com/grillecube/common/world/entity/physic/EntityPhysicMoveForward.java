package com.grillecube.common.world.entity.physic;

import com.grillecube.client.renderer.model.instance.ModelInstance;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.entity.Entity;
import com.grillecube.common.world.entity.EntityModeled;

public class EntityPhysicMoveForward extends EntityPhysic {
	private Vector3f _move;

	public EntityPhysicMoveForward() {
		this._move = new Vector3f();
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
				instance.getAnimationInstance(0).startPlay();
			}
		}
	}

	@Override
	public void onUpdate(Entity entity) {
		this._move.set(entity.getViewVector());
		this._move.y = 0;
		this._move.normalise();
		this._move.scale(entity.getSpeed());
		entity.move(this._move);

		if (entity instanceof EntityModeled) {
			EntityModeled model = (EntityModeled) entity;
			ModelInstance instance = model.getModelInstance();
			if (instance != null && !instance.isPlayingAnyAnimations()) {
				instance.getAnimationInstance(0).startPlay();
			}
		}
	}
}
