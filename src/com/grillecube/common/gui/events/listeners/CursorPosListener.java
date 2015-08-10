package com.grillecube.common.gui.events.listeners;

public abstract class CursorPosListener implements Listener {

	@Override
	public void onEvent(Object... args) {
		onEvent((double) args[0], (double)args[1]);
	}
	
	public abstract void onEvent(double xpos, double ypos);
}
