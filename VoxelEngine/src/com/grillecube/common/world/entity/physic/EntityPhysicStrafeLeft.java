package com.grillecube.common.world.entity.physic;

import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.entity.Entity;

public class EntityPhysicStrafeLeft extends EntityPhysic {
	private Vector3f move;

	public EntityPhysicStrafeLeft() {
		this.move = new Vector3f();
	}

	@Override
	public void onEnable(Entity entity) {
	}

	@Override
	public void onDisable(Entity entity) {
	}

	@Override
	public void onUpdate(Entity entity) {
		this.move.set(entity.getViewVector());
		this.move.y = 0;
		this.move.normalise();
		this.move.scale(entity.getSpeed());
		this.move.rotateY90();
		entity.move(this.move);
	}
}
