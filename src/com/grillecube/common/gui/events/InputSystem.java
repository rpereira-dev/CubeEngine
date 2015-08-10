package com.grillecube.common.gui.events;

import com.grillecube.common.gui.events.listeners.CursorPosListener;

public abstract class InputSystem {
	public void addCursorPosListener(CursorPosListener cpListener) {
		//Add listener
	}
	
	public void notifyCursorPosListeners(double xpos, double ypos) {
		
	}
}
