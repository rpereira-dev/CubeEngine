package com.grillecube.engine.renderer.model.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.grillecube.engine.Logger;
import com.grillecube.engine.Logger.Level;
import com.grillecube.engine.renderer.model.Model;
import com.grillecube.engine.renderer.model.ModelPart;
import com.grillecube.engine.renderer.model.ModelSkin;
import com.grillecube.engine.renderer.model.animation.ModelAnimation;
import com.grillecube.engine.renderer.model.builder.ModelPartBuilder;
import com.grillecube.engine.renderer.model.builder.ModelPartSkinBuilder;

public class ModelBuilderImporter {

	public static Model importModelBuilder(String filepath) {
		return (importModelBuilder(null, filepath));
	}

	/** load a model with it blocks */
	public static Model importModelBuilder(Model dst, String filepath) {

		Logger.get().log(Logger.Level.FINE, "Importing model builder: " + filepath);

		try {
			JSONObject object = new JSONObject(JSONHelper.readFile(filepath));

			if (dst == null) {
				dst = new Model(object.getString(ModelImporter.KEY_MODEL_NAME));
			}

			// parts
			JSONArray parts = object.getJSONArray(ModelImporter.KEY_MODEL_PARTS);
			for (int i = 0; i < parts.length(); i++) {
				ModelPartBuilder part = jsonObjectToModelPartBuilder(parts.getJSONObject(i));
				part.updateBox();
				dst.addPart(part);
			}

			// skins
			JSONArray skins = object.getJSONArray(ModelImporter.KEY_MODEL_SKINS);
			for (int i = 0; i < skins.length(); i++) {
				ModelSkin skin = jsonObjectToModelSkin(dst.getParts(), parts.getJSONObject(i));
				// dst.addSkin(skin);
			}

			// get model animations
			JSONArray animations = object.getJSONArray(ModelImporter.KEY_MODEL_ANIMATIONS);
			for (int i = 0; i < animations.length(); i++) {
				ModelAnimation animation = ModelImporter.jsonObjectToModelAnimation(animations.getJSONObject(i));
				dst.addAnimation(animation);
			}

			// update the model box
			dst.updateBox();
			return (dst);
		} catch (Exception e) {
			Logger.get().log(Logger.Level.ERROR, "Exception occured: " + e.getLocalizedMessage());
		}
		return (null);
	}

	private static ModelSkin jsonObjectToModelSkin(ArrayList<ModelPart> parts, JSONObject object) {

		ModelSkin skin = new ModelSkin(object.getString(ModelImporter.KEY_MODEL_SKIN_NAME));

		JSONArray skins = object.getJSONArray(ModelImporter.KEY_MODEL_SKIN_PARTS);
		for (int i = 0; i < skins.length(); i++) {
			JSONObject json = skins.getJSONObject(i);

			ModelPartSkinBuilder skinpart = new ModelPartSkinBuilder();
			int[] rgba = JSONHelper.jsonArrayToIntArray(json.getJSONArray(ModelImporter.KEY_MODEL_SKIN_CUBES));
			skinpart.setBlocks(rgba);
			skin.addPart(skinpart);
		}
		return (null);

	}

	private static ModelPartBuilder jsonObjectToModelPartBuilder(JSONObject object) {

		ModelPartBuilder part = new ModelPartBuilder(object.getString(ModelImporter.KEY_MODEL_PART_NAME));

		Logger.get().log(Level.DEBUG, "Adding part: " + part);

		// load set cubes
		JSONArray cubes = object.getJSONArray(ModelImporter.KEY_MODEL_PART_CUBES);
		int[] cubes_coordinates = JSONHelper.jsonArrayToIntArray(cubes);
		part.setBlocks(cubes_coordinates);

		// load colors used for this model part
		JSONArray colors = object.getJSONArray(ModelImporter.KEY_MODEL_PART_BUILD_COLORS);
		for (int i = 0; i < colors.length(); i++) {
			int value = (int) colors.get(i);
//			part.addColor(value);
		}
//TODO
		return (part);
	}

}
