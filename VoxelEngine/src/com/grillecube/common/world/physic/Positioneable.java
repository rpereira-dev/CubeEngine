package com.grillecube.common.world.physic;

public interface Positioneable {

	/** positions */
	public float getPositionX();

	public float getPositionY();

	public float getPositionZ();

	/** position velocity */
	public float getPositionVelocityX();

	public float getPositionVelocityY();

	public float getPositionVelocityZ();

	/** position acceleration */
	public float getPositionAccelerationX();

	public float getPositionAccelerationY();

	public float getPositionAccelerationZ();

	/** positions */
	public void setPositionX(float x);

	public void setPositionY(float y);

	public void setPositionZ(float z);

	/** position velocity */
	public void setPositionVelocityX(float vx);

	public void setPositionVelocityY(float vy);

	public void setPositionVelocityZ(float vz);

	/** position acceleration */
	public void setPositionAccelerationX(float ax);

	public void setPositionAccelerationY(float ay);

	public void setPositionAccelerationZ(float az);

	/**
	 * update the velocity of the positioneable objects depending on the elasped
	 * time 'dt' and it current acceleration
	 * 
	 * @param positioneable
	 *            : the positioneable object
	 * @param dt
	 *            : elasped time
	 */
	public static void velocity(Positioneable positioneable, double dt) {
		positioneable.setPositionVelocityX(
				(float) (positioneable.getPositionVelocityX() + positioneable.getPositionAccelerationX() * dt));
		positioneable.setPositionVelocityY(
				(float) (positioneable.getPositionVelocityY() + positioneable.getPositionAccelerationY() * dt));
		positioneable.setPositionVelocityZ(
				(float) (positioneable.getPositionVelocityZ() + positioneable.getPositionAccelerationZ() * dt));
	}

	/**
	 * update the position of the positioneable objects depending on the elasped
	 * time 'dt' and it current velocity
	 * 
	 * @param positioneable
	 *            : the positioneable object
	 * @param dt
	 */
	public static void position(Positioneable positioneable, double dt) {
		float vx = positioneable.getPositionVelocityX();
		float vy = positioneable.getPositionVelocityY();
		float vz = positioneable.getPositionVelocityZ();
		position(positioneable, vx, vy, vz, dt);
	}

	public static void position(Positioneable positioneable, float vx, float vy, float vz, double dt) {
		positioneable.setPositionX((float) (positioneable.getPositionX() + vx * dt));
		positioneable.setPositionY((float) (positioneable.getPositionY() + vy * dt));
		positioneable.setPositionZ((float) (positioneable.getPositionZ() + vz * dt));
	}

	/** @return true if this positioneable objects is rotating */
	public static boolean isMoving(Positioneable positioneable) {
		return (positioneable.getPositionVelocityX() != 0 || positioneable.getPositionVelocityY() != 0
				|| positioneable.getPositionVelocityZ() != 0);
	}

}
