package com.grillecube.common.world.items;

import com.grillecube.client.renderer.model.Model;
import com.grillecube.common.resources.R;
import com.grillecube.common.world.entity.EntityLiving;
//TODO : CLIENT SIDE ONLY

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

	/** the item texture for inventories */
	private final int textureID;

	/** the model when this item is rendered */
	private final Model model;
	// TODO : CLIENT SIDE ONLY

	/** the skin id to use (relative to the given model) */
	private final int skinID;

	/** the model string id (to get unlocalized strings) */
	private final String textID;

	/** item rarity */
	private Rarity _rarity;

	public Item(short id, int textureID, Model model, int skinID, String textID) {
		this.id = id;
		this.textureID = textureID;
		this.model = model;
		this.skinID = skinID;
		this.textID = textID;
		this.setRarity(Item.Rarity.COMMON);
	}

	/** set the item rarity */
	public void setRarity(Rarity rarity) {
		this._rarity = rarity;
	}

	/** get the item rarity */
	public Rarity getRarity() {
		return (this._rarity);
	}

	/** item id */
	public int getID() {
		return (this.id);
	}

	/** texture id */
	public int getTextureID() {
		return (this.textureID);
	}

	/** model id */
	public Model getModel() {
		return (this.model);
	}

	/** skin id */
	public int getSkinID() {
		return (this.skinID);
	}

	/** return the text id for this item */
	public String getTextID() {
		return (this.textID);
	}

	/** return the name of the item */
	public String getName() {
		return (R.getWord("item." + this.textID + ".name"));
	}

	/** return the description of the item */
	public String getDescription() {
		return (R.getWord("item." + this.textID + ".description"));
	}

	/** return the comment of an item */
	public String getComment() {
		return (R.getWord("item." + this.textID + ".comment"));
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
