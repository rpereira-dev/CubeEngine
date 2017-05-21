package com.grillecube.common.resources;

import com.grillecube.common.Logger;
import com.grillecube.common.world.items.Item;

public class ItemManager extends GenericManager<Item> {

	public ItemManager(ResourceManager resource_manager) {
		super(resource_manager);
	}

	/** register an item to the engine */
	public Item registerItem(Item item) {
		Logger.get().log(Logger.Level.FINE, "Registering an item: " + item.toString());
		super.registerObject(item);
		return (item);
	}
	
	@Override
	protected void onObjectRegistered(Item item) {
	}

	@Override
	protected void onInitialized() {		
	}

	@Override
	protected void onStopped() {		
	}

	@Override
	protected void onCleaned() {		
	}

	@Override
	protected void onLoaded() {		
	}

	/** get an item by it id */
	public Item getItemByID(short itemID) {
		return (super.getObjectByID(itemID));
	}
}
