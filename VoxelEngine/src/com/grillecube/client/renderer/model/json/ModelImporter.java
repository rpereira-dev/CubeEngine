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

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.grillecube.client.renderer.model.Model;
import com.grillecube.client.renderer.model.ModelPart;
import com.grillecube.client.renderer.model.ModelPartSkin;
import com.grillecube.client.renderer.model.ModelSkin;
import com.grillecube.client.renderer.model.animation.ModelAnimation;
import com.grillecube.client.renderer.model.animation.ModelAnimationFrame;
import com.grillecube.client.renderer.model.animation.ModelPartAnimation;
import com.grillecube.common.Logger;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.VoxelEngine.Side;

public class ModelImporter {

	/** json key constants */
	public static final String KEY_MODEL_NAME = "Name";

	public static final String KEY_MODEL_SKINS = "ModelSkins";
	public static final String KEY_MODEL_SKIN_NAME = "ModelSkinName";
	public static final String KEY_MODEL_SKIN_PARTS = "ModelPartSkins";
	public static final String KEY_MODEL_SKINS_VERTICES = "ModelPartSkinVertices";
	public static final String KEY_MODEL_SKIN_CUBES = "ModelPartSkinCubes";

	public static final String KEY_MODEL_ANIMATIONS = "ModelAnimations";

	public static final String KEY_MODEL_PARTS = "ModelParts";
	public static final String KEY_MODEL_PART_NAME = "ModelPartName";
	public static final String KEY_MODEL_PART_ANIMATIONS = "ModelPartAnimations";
	public static final String KEY_MODEL_PART_VERTICES = "ModelPartVertices";
	public static final String KEY_MODEL_PART_BOUNDING_BOX = "ModelPartBoundingBox";
	public static final String KEY_MODEL_PART_CUBES = "ModelPartCubes";
	public static final String KEY_MODEL_PART_BUILD_COLORS = "ModelPartBuildColors";
	public static final String KEY_MODEL_PART_SIZE_UNIT = "ModelPartSizeUnit"; // TODO

	public static final String KEY_MODEL_ANIMATION_NAME = "ModelAnimationName";
	public static final String KEY_MODEL_ANIMATION_PARTS = "ModelAnimationParts";
	public static final String KEY_MODEL_ANIMATION_PART_FRAMES = "ModelAnimationPartFrames";

	public static final String KEY_MODEL_PART_ANIMATION_FRAME_TIME = "ModelPartModelAnimationFrameTime";
	public static final String KEY_MODEL_PART_ANIMATION_FRAME_TRANSLATION = "ModelPartModelAnimationFrameTranslation";
	public static final String KEY_MODEL_PART_ANIMATION_FRAME_ROTATION = "ModelPartModelAnimationFrameRotation";
	public static final String KEY_MODEL_PART_ANIMATION_FRAME_SCALE = "ModelPartModelAnimationFrameScale";
	public static final String KEY_MODEL_PART_ANIMATION_FRAME_OFFSET = "ModelPartModelAnimationFrameOffset";

	/** load a raw model from it vertices */
	public static Model loadModel(Model dst, String filepath) throws IOException {
		Logger.get().log(Logger.Level.FINE, "Loading model: " + filepath);
		return (ModelImporter.loadModelfromJsonObject(dst, new JSONObject(JSONHelper.readFile(filepath))));
	}

	/**
	 * load a raw model from it vertices
	 * 
	 * @throws IOException
	 */
	public static Model loadModel(String filepath) throws IOException {
		return (loadModel(null, filepath));
	}

	/** load a model frm a json object */
	public static Model loadModelfromJsonObject(Model dst, JSONObject object) {
		try {

			// create a new model if needed
			if (dst == null) {
				dst = new Model();
			}

			// get model name
			dst.setName(object.getString(ModelImporter.KEY_MODEL_NAME));

			// get model parts
			JSONArray parts = object.getJSONArray(ModelImporter.KEY_MODEL_PARTS);
			for (int i = 0; i < parts.length(); i++) {
				dst.addPart(jsonObjectToModelPart(parts.getJSONObject(i)));
			}

			// get model skins
			JSONArray skins = object.getJSONArray(ModelImporter.KEY_MODEL_SKINS);
			for (int i = 0; i < skins.length(); i++) {
				ModelSkin skin = jsonObjectToModelSkin(skins.getJSONObject(i));
				// dst.addSkin(skin);
			}

			// get model animations
			JSONArray animations = object.getJSONArray(ModelImporter.KEY_MODEL_ANIMATIONS);
			for (int i = 0; i < animations.length(); i++) {
				ModelAnimation animation = jsonObjectToModelAnimation(animations.getJSONObject(i));
				dst.addAnimation(animation);
			}

			// fix boundingbox issues
			dst.updateBox();

			return (dst);
		} catch (Exception e) {
			Logger.get().log(Logger.Level.ERROR, "wrong json file format");
			e.printStackTrace();
			return (null);
		}
	}

	/** return an animation for a json object */
	public static ModelAnimation jsonObjectToModelAnimation(JSONObject object) {

		ModelAnimation animation = new ModelAnimation(object.getString(ModelImporter.KEY_MODEL_ANIMATION_NAME));

		JSONArray animation_parts = object.getJSONArray(ModelImporter.KEY_MODEL_ANIMATION_PARTS);
		for (int i = 0; i < animation_parts.length(); i++) {
			animation.addPart(jsonObjectToModelPartAnimation(animation_parts.getJSONObject(i)));
		}

		return (animation);
	}

	private static ModelPartAnimation jsonObjectToModelPartAnimation(JSONObject object) {

		ModelPartAnimation part = new ModelPartAnimation();
		JSONArray frames = object.getJSONArray(ModelImporter.KEY_MODEL_ANIMATION_PART_FRAMES);
		for (int i = 0; i < frames.length(); i++) {
			part.addFrame(jsonObjectToModelAnimationFrame(frames.getJSONObject(i)));
		}
		part.sortFrames();
		return (part);
	}

	/** return an animation frame from a json object */
	private static ModelAnimationFrame jsonObjectToModelAnimationFrame(JSONObject object) {

		return (new ModelAnimationFrame(object.getInt(ModelImporter.KEY_MODEL_PART_ANIMATION_FRAME_TIME),
				JSONHelper.getJSONVector3f(object, ModelImporter.KEY_MODEL_PART_ANIMATION_FRAME_TRANSLATION),
				JSONHelper.getJSONVector3f(object, ModelImporter.KEY_MODEL_PART_ANIMATION_FRAME_ROTATION),
				JSONHelper.getJSONVector3f(object, ModelImporter.KEY_MODEL_PART_ANIMATION_FRAME_OFFSET),
				JSONHelper.getJSONVector3f(object, ModelImporter.KEY_MODEL_PART_ANIMATION_FRAME_SCALE)));
	}

	private static ModelSkin jsonObjectToModelSkin(JSONObject object) {

		ModelSkin skin = new ModelSkin(object.getString(ModelImporter.KEY_MODEL_SKIN_NAME));
		JSONArray parts = object.getJSONArray(ModelImporter.KEY_MODEL_SKIN_PARTS);
		for (int i = 0; i < parts.length(); i++) {
			skin.addPart(jsonObjectToModelPartSkin(parts.getJSONObject(i)));
		}
		return (skin);
	}

	private static ModelPartSkin jsonObjectToModelPartSkin(JSONObject object) {
		ModelPartSkin skin = new ModelPartSkin();
		skin.setVertices(JSONHelper.jsonArrayToFloatArray(object.getJSONArray(ModelImporter.KEY_MODEL_SKINS_VERTICES)));
		return (skin);
	}

	private static ModelPart jsonObjectToModelPart(JSONObject object) {
		ModelPart part = new ModelPart(object.getString(ModelImporter.KEY_MODEL_PART_NAME));

		JSONObject box = object.getJSONObject(ModelImporter.KEY_MODEL_PART_BOUNDING_BOX);
		part.getBoundingBox().set(JSONHelper.jsonObjectToBoundingBox(box));

		if (!(VoxelEngine.instance().getSide().equals(Side.SERVER))) {
			JSONArray vertices = object.getJSONArray(ModelImporter.KEY_MODEL_PART_VERTICES);
			part.setVertices(JSONHelper.jsonArrayToFloatArray(vertices));
		}

		return (part);
	}
}
