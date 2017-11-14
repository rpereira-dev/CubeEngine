package com.grillecube.common.world.entity.collision;

public class CollisionResponse {

	/** normal x between the face and the direction */
	public final float nx;

	/** normal y between the face and the direction */
	public final float ny;

	/** normal z between the face and the direction */
	public final float nz;

	/** time until the collision will happens */
	public final float dt;

	public CollisionResponse(float nx, float ny, float nz, float dt) {
		this.nx = nx;
		this.ny = ny;
		this.nz = nz;
		this.dt = dt;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof CollisionResponse)) {
			return (false);
		}
		CollisionResponse o = (CollisionResponse) other;
		return (this.nx == o.nx && this.ny == o.ny && this.nz == o.nz && this.dt == o.dt);
	}

	@Override
	public String toString() {
		return ("CollisionResponse{nx=" + this.nx + ";ny=" + this.ny + ";nz=" + this.nz + ";dt=" + this.dt + "}");
	}
}
