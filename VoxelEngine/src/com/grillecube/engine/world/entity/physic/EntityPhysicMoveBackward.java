package com.grillecube.engine.world.entity.physic;

import com.grillecube.engine.maths.Vector3f;
import com.grillecube.engine.renderer.model.instance.ModelInstance;
import com.grillecube.engine.world.entity.Entity;
import com.grillecube.engine.world.entity.EntityModeled;

public class EntityPhysicMoveBackward extends EntityPhysic {
	private Vector3f _move;

	public EntityPhysicMoveBackward() {
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
				instance.getAnimationInstance(0).stopPlay();
			}
		}
	}

	@Override
	public void onUpdate(Entity entity) {
		this._move.set(entity.getViewVector());
		this._move.y = 0;
		this._move.normalise();
		this._move.scale(entity.getSpeed() / 3.0f);
		this._move.negate(this._move);
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
