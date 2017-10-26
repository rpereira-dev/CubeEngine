package com.grillecube.client.opencl;

import java.util.ArrayList;

import com.grillecube.common.Logger;
import com.grillecube.common.Logger.Level;

//TODO , do we need opencl ?

public class CLH {

	/** lists of every allocated gl objects */
	private static ArrayList<CLObject> _cl_objects;

	/** initialize OpenCL */
	public static final void clhInit() {
		Logger.get().log(Level.FINE, "Initializing OpenCL.");
		_cl_objects = new ArrayList<CLObject>();
	}

	/** stop opencl properly */
	public static final void clhStop() {
		for (CLObject object : _cl_objects) {
			object.delete();
		}
		_cl_objects.clear();
	}

	/** check the open al error in the current context : print and returns it */
	public static int clhCheckError(String label, int err) {

		if (err == 0) {
			return (0);
		}

		Logger.get().log(Level.WARNING, label + " : OpenCL error occured : " + err);
		return (err);
	}

}
