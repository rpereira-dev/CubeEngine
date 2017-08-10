package com.grillecube.common.world.entity;

import com.grillecube.common.world.World;

public abstract class EntityBiped extends EntityLiving {

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

	public EntityBiped(World world) {
		super(world);
	}

	@Override
	protected void onUpdate() {
	}

	@Override
	public int getEquipmentCount() {
		return (EntityBiped.EQUIPMENT_COUNT);
	}
}