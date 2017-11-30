package com.grillecube.common.world.items;

import com.grillecube.common.resources.R;
import com.grillecube.common.world.entity.EntityLiving;

public abstract class Item {

	/** rarity */
	public enum Rarity {
		VALUELESS("valueless"), COMMON("common"), RARE("rare");

		/** rarity name */
		String name;

		Rarity(String str) {
			this.name = R.getWord("rarity." + str);
		}

		@Override
		public String toString() {
			return (this.name);
		}
	}

	/** unique item id, set when the item is registered */
	private final short id;

	/** item rarity */
	private Rarity rarity;

	public Item(short id) {
		this.id = id;
		this.setRarity(Item.Rarity.COMMON);
	}

	/** set the item rarity */
	public void setRarity(Rarity rarity) {
		this.rarity = rarity;
	}

	/** get the item rarity */
	public Rarity getRarity() {
		return (this.rarity);
	}

	/** item id */
	public final int getID() {
		return (this.id);
	}

	/**
	 * called when this item is unequipped on a living entity at the given
	 * equipment id
	 */
	public void onUnequipped(EntityLiving entity, int equipmentID) {
	}

	/**
	 * called when this item is equipped on a living entity at the given
	 * equipment id
	 */
	public void onEquipped(EntityLiving entity, int equipmentID) {
	}
}
