package com.grillecube.common.world.entity.physic;

import com.grillecube.common.world.entity.Entity;

public class EntityPhysicJumping extends EntityPhysic {
	
	private int _timer;
	
	@Override
	public void onEnable(Entity entity) {
		this._timer = 0;
		entity.getPositionVelocity().setY(1.0f);
//		VoxelEngineClient.instance().getRenderer().getGuiRenderer().toast("jump starts", 0, 1, 0, 1);
	}

	@Override
	public void onDisable(Entity entity) {
		// VoxelEngineClient.instance().getRenderer().getGuiRenderer().toast("jump
		// stop", 1, 0, 0, 1);
	}

	@Override
	public void onUpdate(Entity entity) {
		++this._timer;
		if (this._timer > 60 || (this._timer > 20 && !(entity.isInAir()))) {
			this.disable(entity);
		}
	}
}
