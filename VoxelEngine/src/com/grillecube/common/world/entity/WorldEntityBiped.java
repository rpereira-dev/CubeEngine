package com.grillecube.common.world.entity;

import com.grillecube.common.world.World;

public abstract class WorldEntityBiped extends WorldEntityLiving {
	/** equipment ids */
	public static final int EQUIPMENT_BOOTS = 0;
	public static final int EQUIPMENT_LEGGINGS = 1;
	public static final int EQUIPMENT_BELT = 2;
	public static final int EQUIPMENT_GLOVES = 3;
	public static final int EQUIPMENT_PLASTRON = 4;
	public static final int EQUIPMENT_HELM = 5;
	public static final int EQUIPMENT_LEFT_HAND = 6;
	public static final int EQUIPMENT_RIGHT_HAND = 7;
	public static final int EQUIPMENT_COUNT = 8;

	public WorldEntityBiped(World world, float mass, float width, float height, float depth) {
		super(world, mass, width, height, depth);
	}

	@Override
	protected void onUpdate(double dt) {
	}

	@Override
	public int getEquipmentCount() {
		return (WorldEntityBiped.EQUIPMENT_COUNT);
	}
}
