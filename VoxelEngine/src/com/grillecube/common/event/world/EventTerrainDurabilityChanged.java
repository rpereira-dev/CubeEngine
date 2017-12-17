package com.grillecube.common.event.world;

import com.grillecube.common.world.Terrain;

public class EventTerrainDurabilityChanged extends EventTerrain {

	private final byte old;
	private final int index;

	public EventTerrainDurabilityChanged(Terrain terrain, byte old, int index) {
		super(terrain);
		this.old = old;
		this.index = index;
	}

	public final int getIndex() {
		return (this.index);
	}

	public final byte getOldDurability() {
		return (this.old);
	}

	@Override
	protected void process() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void unprocess() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onReset() {
		// TODO Auto-generated method stub
		
	}

}
