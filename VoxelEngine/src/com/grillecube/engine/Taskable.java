package com.grillecube.engine;

import java.util.ArrayList;

public interface Taskable {

	public void getTasks(VoxelEngine engine, ArrayList<VoxelEngine.Callable<Taskable>> tasks);
}
