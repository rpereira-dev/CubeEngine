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

package com.grillecube.engine.renderer.model.json;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.grillecube.engine.Logger;
import com.grillecube.engine.Logger.Level;
import com.grillecube.engine.maths.Vector3f;
import com.grillecube.engine.renderer.model.Model;
import com.grillecube.engine.renderer.model.ModelPart;
import com.grillecube.engine.renderer.model.ModelPartSkin;
import com.grillecube.engine.renderer.model.ModelSkin;
import com.grillecube.engine.renderer.model.animation.ModelAnimationFrame;
import com.grillecube.engine.renderer.model.animation.ModelAnimation;
import com.grillecube.engine.renderer.model.animation.ModelPartAnimation;
import com.grillecube.engine.world.Terrain;

//TODO : an optimization algorythm which lower the number of polygons on exported (final) model

public class ModelExporter {
	/** export vertices */
	public static void exportModel(Model model, String filepath) {
		ModelExporter.exportModel(model, new File(filepath));
	}

	/** export vertices */
	public static void exportModel(Model model, File file) {
		if (model == null) {
			Logger.get().log(Level.WARNING, "Tried to export a null model");
			return;
		}

		if (file == null) {
			Logger.get().log(Level.WARNING, "Tried to export a model on a null file");
			return;
		}

		JSONObject json = new JSONObject();
		json.put(ModelImporter.KEY_MODEL_NAME, model.getName());
		json.put(ModelImporter.KEY_MODEL_SKINS, modelSkinsToJSONArray(model.getSkins()));
		json.put(ModelImporter.KEY_MODEL_ANIMATIONS, modelAnimationsToJSONArray(model.getAnimations()));
		json.put(ModelImporter.KEY_MODEL_PARTS, modelPartsToJSONArray(model.getParts()));
		JSONHelper.writeJSONObjectToFile(file, json);
	}

	public static JSONArray modelAnimationsToJSONArray(ArrayList<ModelAnimation> animations) {
		JSONArray array = new JSONArray();
		for (ModelAnimation animation : animations) {
			array.put(modelAnimationToJSONObject(animation));
		}
		return (array);
	}

	private static JSONObject modelAnimationToJSONObject(ModelAnimation animation) {
		JSONObject object = new JSONObject();
		object.put(ModelImporter.KEY_MODEL_ANIMATION_NAME, animation.getName());
		object.put(ModelImporter.KEY_MODEL_ANIMATION_PARTS, modelAnimationPartToJSONArray(animation));

		return (object);
	}

	private static JSONArray modelAnimationPartToJSONArray(ModelAnimation animation) {
		JSONArray array = new JSONArray();

		for (ModelPartAnimation part : animation.getAnimationParts()) {
			array.put(modelPartAnimationToJSONObject(part));
		}

		return (array);
	}

	private static JSONObject modelPartAnimationToJSONObject(ModelPartAnimation part) {
		JSONObject object = new JSONObject();
		object.put(ModelImporter.KEY_MODEL_ANIMATION_PART_FRAMES, modelPartModelAnimationFramesToJSONArray(part));
		return (object);
	}

	private static JSONArray modelPartModelAnimationFramesToJSONArray(ModelPartAnimation part) {
		JSONArray array = new JSONArray();

		for (ModelAnimationFrame frame : part.getFrames()) {
			array.put(modelPartModelAnimationFrameToJSONObject(frame));
		}

		return (array);
	}
	
	private static JSONObject modelPartModelAnimationFrameToJSONObject(ModelAnimationFrame frame) {

		float s = 1.0f / (float) Terrain.BLOCK_SIZE;
		int time = frame.getTime();
		JSONArray translation = JSONHelper.vector3fToJSONArray(new Vector3f(frame.getTranslation()).scale(s));
		JSONArray rotation = JSONHelper.vector3fToJSONArray(frame.getRotation());
		JSONArray scale = JSONHelper.vector3fToJSONArray(frame.getScaling());
		JSONArray offset = JSONHelper.vector3fToJSONArray(new Vector3f(frame.getOffset()).scale(s));

		JSONObject json = new JSONObject();
		json.put(ModelImporter.KEY_MODEL_PART_ANIMATION_FRAME_TIME, time);
		json.put(ModelImporter.KEY_MODEL_PART_ANIMATION_FRAME_TIME, translation);
		json.put(ModelImporter.KEY_MODEL_PART_ANIMATION_FRAME_TIME, rotation);
		json.put(ModelImporter.KEY_MODEL_PART_ANIMATION_FRAME_TIME, scale);
		json.put(ModelImporter.KEY_MODEL_PART_ANIMATION_FRAME_TIME, offset);

		return (json);
	}

	private static JSONArray modelSkinsToJSONArray(ArrayList<ModelSkin> skins) {
		JSONArray array = new JSONArray();
		for (ModelSkin skin : skins) {
			array.put(modelSkinToJSONObject(skin));
		}
		return (array);
	}

	private static JSONObject modelSkinToJSONObject(ModelSkin skin) {
		JSONObject json = new JSONObject();
		json.put(ModelImporter.KEY_MODEL_SKIN_NAME, skin.getName());
		json.put(ModelImporter.KEY_MODEL_SKIN_PARTS, modelSkinPartToJSONArray(skin));
		return (json);
	}

	private static JSONArray modelSkinPartToJSONArray(ModelSkin skin) {
		JSONArray json = new JSONArray();

		for (ModelPartSkin partskin : skin.getPartsSkin()) {
			json.put(skinToJSONObject(partskin));
		}

		return (json);
	}

	private static JSONObject skinToJSONObject(ModelPartSkin skin) {
		JSONObject json = new JSONObject();
		String key = ModelImporter.KEY_MODEL_SKINS_VERTICES;
		json.put(key, JSONHelper.arrayToJSONArray(skin.getVBO().getContent(0), 100000.0d));
		return (json);
	}

	private static JSONArray modelPartsToJSONArray(ArrayList<ModelPart> parts) {
		JSONArray array = new JSONArray();
		for (ModelPart part : parts) {
			array.put(modelPartToJSONObject(part));
		}
		return (array);
	}

	private static JSONObject modelPartToJSONObject(ModelPart part) {
		JSONObject json = new JSONObject();
		json.put(ModelImporter.KEY_MODEL_PART_NAME, part.getName());
		json.put(ModelImporter.KEY_MODEL_PART_BOUNDING_BOX, JSONHelper.boundingBoxToJSONObject(part.getBoundingBox()));
		json.put(ModelImporter.KEY_MODEL_PART_VERTICES, modelVerticesToJSONArray(part));
		return (json);
	}

	private static JSONArray modelVerticesToJSONArray(ModelPart part) {
		float[] vertices = part.getPositionVertices();
		double scale = 1 / (double) Terrain.BLOCK_SIZE;

		for (int i = 0; i < vertices.length; i++) {
			vertices[i] *= scale;
		}

		return (JSONHelper.arrayToJSONArray(vertices, 1000.0d));
	}
}
