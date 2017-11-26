package com.grillecube.client.renderer.model.json;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONObject;

import com.grillecube.client.renderer.model.ModelSkin;
import com.grillecube.client.renderer.model.animation.BoneTransform;
import com.grillecube.client.renderer.model.animation.KeyFrame;
import com.grillecube.client.renderer.model.animation.ModelSkeletonAnimation;
import com.grillecube.client.renderer.model.editor.mesher.EditableModel;

public class JSONModelExporter {

	/**
	 * initializer override
	 * 
	 * @throws IOException
	 */
	public final void export(EditableModel model, String dirpath) throws IOException {
		JSONObject jsonInfo = new JSONObject();

		// name
		jsonInfo.put("name", model.getName());

		// mesh
		jsonInfo.put("mesh", "mesh/mesh.json");

		// skeleton
		jsonInfo.put("skeleton", "skeleton/skeleton.json");

		// blocks
		String blocksPath = "blocks/blocks.json";
		jsonInfo.put("blocks", blocksPath);
		//TODO
		

		// animations
		JSONArray jsonAnimations = new JSONArray();
		for (ModelSkeletonAnimation animation : model.getAnimations()) {
			String animationPath = "animations/" + animation.getName().toLowerCase() + ".json";
			jsonAnimations.put(animationPath);

			JSONObject jsonAnimation = new JSONObject();
			jsonAnimation.put("name", animation.getName());
			jsonAnimation.put("duration", animation.getDuration());

			JSONArray jsonKeyFrames = new JSONArray();
			for (KeyFrame keyFrame : animation.getKeyFrames()) {
				JSONObject jsonKeyFrame = new JSONObject();
				jsonKeyFrame.put("time", keyFrame.getTime());

				JSONObject jsonPose = new JSONObject();
				for (String boneName : keyFrame.getBoneKeyFrames().keySet()) {
					BoneTransform boneTransform = keyFrame.getBoneKeyFrames().get(boneName);
					JSONObject jsonTransform = new JSONObject();
					JSONObject jsonPosition = new JSONObject();
					jsonPosition.put("x", boneTransform.getTranslation().x);
					jsonPosition.put("y", boneTransform.getTranslation().y);
					jsonPosition.put("z", boneTransform.getTranslation().z);
					jsonTransform.put("position", jsonPosition);

					JSONObject jsonRotation = new JSONObject();
					jsonRotation.put("x", boneTransform.getRotation().getX());
					jsonRotation.put("y", boneTransform.getRotation().getY());
					jsonRotation.put("z", boneTransform.getRotation().getZ());
					jsonRotation.put("w", boneTransform.getRotation().getW());
					jsonTransform.put("rotation", jsonRotation);

					jsonKeyFrame.put("transform", jsonTransform);
				}
				jsonKeyFrames.put(jsonPose);
			}
			writeJSON(jsonAnimation, dirpath, animationPath);
		}
		jsonInfo.put("animations", jsonAnimations);

		// skins
		JSONArray jsonSkins = new JSONArray();
		for (ModelSkin skin : model.getSkins()) {
			String skinPath = "skins/" + skin.getName().toLowerCase() + ".json";
			jsonSkins.put(skinPath);
			
			//TODO
		}
		jsonInfo.put("skins", jsonSkins);

		// finally, write file
		writeJSON(jsonInfo, dirpath, "info.json");
	}

	private static final void writeJSON(JSONObject obj, String dirpath, String... more) throws IOException {
		Path info = Paths.get(dirpath, more);
		File file = info.toFile();
		BufferedWriter output = new BufferedWriter(new FileWriter(file));
		output.write(obj.toString());
		output.close();
	}
}
