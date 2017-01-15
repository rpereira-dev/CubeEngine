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
import com.grillecube.engine.renderer.model.Model;
import com.grillecube.engine.renderer.model.ModelPart;
import com.grillecube.engine.renderer.model.ModelPartSkin;
import com.grillecube.engine.renderer.model.ModelSkin;
import com.grillecube.engine.renderer.model.builder.ModelPartBuilder;
import com.grillecube.engine.renderer.model.builder.ModelPartSkinBuilder;

public class ModelBuilderExporter {

	/** export blocks */
	public static void exportModelBuilder(Model builder, String filepath) {
		Logger.get().log(Logger.Level.FINE, "Exporting Model to : " + filepath);
		if (builder == null) {
			Logger.get().log(Level.WARNING, "Tried to export a null model");
			return;
		}

		JSONObject json = new JSONObject();
		json.put(ModelImporter.KEY_MODEL_NAME, builder.getName());
		json.put(ModelImporter.KEY_MODEL_PARTS, modelPartsBuilderToJSONArray(builder.getParts()));
		json.put(ModelImporter.KEY_MODEL_SKINS, modelSkinsToJSONArray(builder.getSkins()));
		json.put(ModelImporter.KEY_MODEL_ANIMATIONS, ModelExporter.modelAnimationsToJSONArray(builder.getAnimations()));

		JSONHelper.writeJSONObjectToFile(new File(filepath), json);
		Logger.get().log(Logger.Level.FINE, "Model exported.");
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
		json.put(ModelImporter.KEY_MODEL_SKIN_PARTS, modelSkinPartToJSONArray(skin.getPartsSkin()));

		return (json);
	}

	private static JSONArray modelSkinPartToJSONArray(ArrayList<ModelPartSkin> partskins) {

		JSONArray array = new JSONArray();

		for (ModelPartSkin partskin : partskins) {
			array.put(skinBuilderToJSONObject((ModelPartSkinBuilder) partskin));
		}
		return (array);
	}

	private static JSONObject skinBuilderToJSONObject(ModelPartSkinBuilder skin) {

		JSONObject json = new JSONObject();
		json.put(ModelImporter.KEY_MODEL_SKIN_CUBES, skin.getBlocks());
		return (json);
	}

	private static JSONArray modelPartsBuilderToJSONArray(ArrayList<ModelPart> parts) {
		JSONArray array = new JSONArray();
		for (ModelPart part : parts) {
			array.put(modelPartBuilderToJSONObject((ModelPartBuilder) part));
		}
		return (array);
	}

	private static JSONObject modelPartBuilderToJSONObject(ModelPartBuilder part) {
		JSONObject json = new JSONObject();
		json.put(ModelImporter.KEY_MODEL_PART_NAME, part.getName());
		json.put(ModelImporter.KEY_MODEL_PART_CUBES, JSONHelper.arrayToJSONArray(part.getBlocksCoordinates()));
		json.put(ModelImporter.KEY_MODEL_PART_BUILD_COLORS, buildColorsToJSONArray(part));
		return (json);
	}

	private static JSONArray buildColorsToJSONArray(ModelPartBuilder part) {
		JSONArray array = new JSONArray();

		// TODO
		// for (Color color : part.getColors()) {
		// array.put(color.getRGB());
		// }

		return (array);
	}
}
