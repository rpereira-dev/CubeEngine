package com.grillecube.common.event;

import java.util.ArrayList;

import com.grillecube.common.Taskable;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.VoxelEngine.Callable;

/** an event which is called right after the main loop ends */
public class EventGetTasks extends EventEngine {

	private ArrayList<Callable<Taskable>> tasksList;

	public EventGetTasks(VoxelEngine engine, ArrayList<Callable<Taskable>> tasksList) {
		super(engine);
		this.tasksList = tasksList;
	}

	public final ArrayList<Callable<Taskable>> getTasksList() {
		return (this.tasksList);
	}
}
