package com.grillecube.common.world.physic;

import com.grillecube.common.world.entity.WorldEntity;

public class ControlRotateLeft extends Control<WorldEntity> {
	@Override
	public void run(WorldEntity entity, double dt) {
		entity.setRotationY(entity.getRotationY() - 2.0f);
	}
}
