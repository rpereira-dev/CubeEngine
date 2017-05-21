/**
**	This file is part of the project https://github.com/toss-dev/VoxelEngine
**
**	License is available here: https://raw.githubusercontent.com/toss-dev/VoxelEngine/master/LICENSE.md
**
**	PEREIRA Romain
**                                       4-----7          
**                                      /|    /|
**                                     0-----3 |
**                                     | 5___|_6
**                                     |/    | /
**                                     1-----2
*/

package com.grillecube.client.renderer.model.json;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.grillecube.common.maths.BoundingBox;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.terrain.Terrain;

public class JSONHelper {
	/** load a vector3f from a JSON array of 3 floats */
	public static Vector3f jsonArrayToVector3f(JSONArray array) {
		return (new Vector3f(jsonObjectToFloat(array.get(0)), jsonObjectToFloat(array.get(1)),
				jsonObjectToFloat(array.get(2))));
	}

	/** json array to float array */
	public static float[] jsonArrayToFloatArray(JSONArray array) {
		float[] floats = new float[array.length()];

		for (int i = 0; i < floats.length; i++) {
			floats[i] = jsonObjectToFloat(array.get(i));
		}
		return (floats);
	}

	/** json array to float array */
	public static int[] jsonArrayToIntArray(JSONArray array) {
		int[] integers = new int[array.length()];

		for (int i = 0; i < integers.length; i++) {
			integers[i] = array.getInt(i);
		}
		return (integers);
	}

	/**
	 * parse object to float
	 * 
	 * @see JSONArray getDouble(int index);
	 **/
	public static float jsonObjectToFloat(Object object) {
		try {
			if (object instanceof Number) {
				return (((Number) object).floatValue());
			}
			return (Float.parseFloat((String) object));
		} catch (Exception e) {
			throw new JSONException(object + " is not a number.");
		}
	}

	public static Vector3f getJSONVector3f(JSONObject object, String key) {
		return (jsonArrayToVector3f(object.getJSONArray(key)));
	}

	public static void writeJSONObjectToFile(File file, JSONObject json) {
		try {
			FileWriter writer = new FileWriter(file);
			writer.write(json.toString());
			writer.flush();
			writer.close();
		} catch (Exception e) {
			return;
		}
	}

	public static JSONArray vector3fToJSONArray(Vector3f vec) {
		JSONArray array = new JSONArray();
		array.put(vec.getX());
		array.put(vec.getY());
		array.put(vec.getZ());
		return (array);
	}

	public static <T> JSONArray arrayToJSONArray(T[] array) {
		JSONArray jsonarray = new JSONArray();
		if (array == null) {
			return (null);
		}
		for (T t : array) {
			jsonarray.put(t);
		}
		return (jsonarray);
	}

	public static JSONObject boundingBoxToJSONObject(BoundingBox box) {
		JSONObject json = new JSONObject();
		Vector3f min = new Vector3f(box.getMin());
		Vector3f size = new Vector3f(box.getSize());
		min.scale(1 / (float) Terrain.BLOCK_SIZE);
		size.scale(1 / (float) Terrain.BLOCK_SIZE);
		json.put("min", JSONHelper.vector3fToJSONArray(min));
		json.put("size", JSONHelper.vector3fToJSONArray(size));
		return (json);
	}

	
	/** return a boundingbox from the given jsonobject */
	public static BoundingBox jsonObjectToBoundingBox(JSONObject object) {

		Vector3f min = JSONHelper.getJSONVector3f(object, "min");
		Vector3f size = JSONHelper.getJSONVector3f(object, "size");

		BoundingBox box = new BoundingBox();
		box.setMinSize(min, size);

		// important for later calculation
		box.reequilibrateMinMax();
		return (box);
	}
	
	public static JSONArray arrayToJSONArray(byte[] array) {
		JSONArray jsonarray = new JSONArray();
		if (array == null) {
			return (null);
		}
		for (byte b : array) {
			jsonarray.put(b);
		}
		return (jsonarray);
	}

	public static JSONArray arrayToJSONArray(float[] array, double precision) {
		JSONArray jsonarray = new JSONArray();
		if (array == null) {
			return (null);
		}
		for (float f : array) {
			jsonarray.put((double) Math.round(f * precision) / precision);
			// jsonarray.put(f);
		}
		return (jsonarray);
	}

	public static JSONArray arrayToJSONArray(int[] array) {
		JSONArray jsonarray = new JSONArray();
		if (array == null) {
			return (null);
		}
		for (int i : array) {
			jsonarray.put(i);
		}
		return (jsonarray);
	}

	/**
	 * return a String which contains the full file bytes
	 * 
	 * @throws IOException
	 */
	public static String readFile(String filepath) throws IOException {
		return (readFile(new File(filepath)));
	}

	/**
	 * return a String which contains the full file bytes
	 * 
	 * @throws IOException
	 */
	public static String readFile(File file) throws IOException {
		if (!file.exists()) {
			throw new IOException("Couldnt read model file. (It doesnt exists: " + file.getPath() + ")");
		}
		if (file.isDirectory()) {
			throw new IOException("Couldnt read model file. (It is a directory!!! " + file.getPath() + ")");
		}
		if (!file.canRead() && !file.setReadable(true)) {
			throw new IOException("Couldnt read model file. (Missing read permissions: " + file.getPath() + ")");
		}

		byte[] encoded = Files.readAllBytes(Paths.get(file.getPath()));
		return (new String(encoded, StandardCharsets.UTF_8));
	}
}
