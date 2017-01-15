package com.grillecube.engine.world.entity.physic;

import com.grillecube.engine.maths.Vector3f;
import com.grillecube.engine.world.entity.Entity;

public class EntityPhysicStrafeLeft extends EntityPhysic {
	private Vector3f _move;

	public EntityPhysicStrafeLeft() {
		this._move = new Vector3f();
	}

	@Override
	public void onEnable(Entity entity) {
	}

	@Override
	public void onDisable(Entity entity) {
	}

	@Override
	public void onUpdate(Entity entity) {
		this._move.set(entity.getViewVector());
		this._move.y = 0;
		this._move.normalise();
		this._move.scale(entity.getSpeed());
		this._move.rotateY90();
		entity.move(this._move);
	}
}
