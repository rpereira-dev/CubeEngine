package com.grillecube.common.resources;

import java.util.ArrayList;
import java.util.HashMap;

import com.grillecube.common.Logger;
import com.grillecube.common.event.Event;
import com.grillecube.common.event.EventCallback;
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
	public void onInitialized() {
	}

	@Override
	public void onLoaded() {
	}

	@Override
	protected void onDeinitialized() {
	}

	@Override
	protected void onUnloaded() {
	}

	/** get an item by it id */
	public Item getItemByID(short itemID) {
		return (super.getObjectByID(itemID));
	}
}
