package com.grillecube.common.world.entity;

import com.grillecube.common.world.World;
import com.grillecube.common.world.items.Item;

public abstract class EntityLiving extends Entity {

	/** default inventory size */
	private static final int DEFAULT_EQUIPMENT_COUNT = 1;

	/** the items held by this entity */
	private Item[] equipments;

	public EntityLiving(World world) {
		super(world);
		if (this.getEquipmentCount() > 0) {
			this.equipments = new Item[this.getEquipmentCount()];
		} else {
			this.equipments = null;
		}
	}

	/** equip an item to the given biped part */
	public void equip(Item item, int equipmentID) {
		if (equipmentID < 0 || equipmentID >= this.getEquipmentCount()) {
			return;
		}

		if (this.equipments[equipmentID] != null) {
			this.equipments[equipmentID].onUnequipped(this, equipmentID);
		}

		this.equipments[equipmentID] = item;

		if (this.equipments[equipmentID] != null) {
			this.equipments[equipmentID].onEquipped(this, equipmentID);
		}
	}

	public void unequip(int equipmentID) {
		this.equip(null, equipmentID);
	}

	/** return the number of equipment which can be equipped */
	public int getEquipmentCount() {
		return (EntityLiving.DEFAULT_EQUIPMENT_COUNT);
	}

	public Item[] getEquipments() {
		return (this.equipments == null ? null : this.equipments.clone());
	}

}
