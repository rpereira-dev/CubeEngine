package com.grillecube.common.world.physic;

import com.grillecube.common.world.entity.WorldEntity;

public class ForceAirFriction extends ForceFriction {

	@Override
	public final float getFluidDensity() {
		// air density at 30°C
		return (1.1644f);
	}

	@Override
	public final float getDragCoefficient(WorldEntity entity) {
		// cube drag coefficient
		// https://www.engineeringtoolbox.com/drag-coefficient-d_627.html
		return (1.0f);
	}
}
