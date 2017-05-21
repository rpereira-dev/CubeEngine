package com.grillecube.common.world.entity;

import com.grillecube.client.renderer.model.Model;
import com.grillecube.common.world.World;
import com.grillecube.common.world.items.Item;

public abstract class EntityModeledLiving extends EntityModeled {

	/** default inventory size */
	private static final int DEFAULT_EQUIPMENT_COUNT = 1;
	
	/** the items held by this entity */
	private Item[] _equipments;

	public EntityModeledLiving(World world) {
		super(world);
		if (this.getEquipmentCount() > 0) {
			this._equipments = new Item[this.getEquipmentCount()];
		} else {
			this._equipments = null;
		}
	}

	public EntityModeledLiving(World world, Model model) {
		super(world, model);
	}

	/** equip an item to the given biped part */
	public void equip(Item item, int equipmentID) {
		if (equipmentID < 0 || equipmentID >= this.getEquipmentCount()) {
			return;
		}

		if (this._equipments[equipmentID] != null) {
			this._equipments[equipmentID].onUnequipped(this, equipmentID);
		}

		this._equipments[equipmentID] = item;

		if (this._equipments[equipmentID] != null) {
			this._equipments[equipmentID].onEquipped(this, equipmentID);
		}
	}

	public void unequip(int equipmentID) {
		this.equip(null, equipmentID);
	}
	
	/** return the number of equipment which can be equipped */
	public int getEquipmentCount() {
		return (EntityModeledLiving.DEFAULT_EQUIPMENT_COUNT);
	}
	
	public Item[] getEquipments() {
		return (this._equipments == null ? null : this._equipments.clone());
	}
	
}
