package com.grillecube.common.event;

import java.util.ArrayList;

import com.grillecube.common.Taskable;
import com.grillecube.common.VoxelEngine.Callable;

/** an event which is called right after the main loop ends */
public class EventGetTasks extends Event {

	private ArrayList<Callable<Taskable>> tasksList;

	public EventGetTasks(ArrayList<Callable<Taskable>> tasksList) {
		super();
		this.tasksList = tasksList;
	}

	public final ArrayList<Callable<Taskable>> getTasksList() {
		return (this.tasksList);
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
