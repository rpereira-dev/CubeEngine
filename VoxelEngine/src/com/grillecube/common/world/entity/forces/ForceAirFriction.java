package com.grillecube.common.world.entity.forces;

import com.grillecube.common.world.entity.Entity;

public class ForceAirFriction extends ForceFriction {

	@Override
	public final float getFluidDensity() {
		// air density at 30°C
		return (1.1644f);
	}

	@Override
	public final float getDragCoefficient(Entity entity) {
		// cube drag coefficient
		// https://www.engineeringtoolbox.com/drag-coefficient-d_627.html
		return (1.0f);
	}
}
