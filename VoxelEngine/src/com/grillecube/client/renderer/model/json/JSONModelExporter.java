package com.grillecube.client.renderer.model.json;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONObject;

import com.grillecube.client.opengl.object.ImageUtils;
import com.grillecube.client.renderer.model.ModelMesh;
import com.grillecube.client.renderer.model.ModelSkin;
import com.grillecube.client.renderer.model.animation.Bone;
import com.grillecube.client.renderer.model.animation.BoneTransform;
import com.grillecube.client.renderer.model.animation.KeyFrame;
import com.grillecube.client.renderer.model.animation.ModelSkeletonAnimation;
import com.grillecube.client.renderer.model.editor.mesher.EditableModel;
import com.grillecube.client.renderer.model.editor.mesher.ModelBlockData;
import com.grillecube.common.maths.Maths;

public class JSONModelExporter {

	/**
	 * export the given model to the given dirpath
	 * 
	 * @throws Exception
	 */
	public static final void export(EditableModel editableModel, String dirPath) throws Exception {

		if (editableModel == null) {
			throw new Exception("null Model in export()");
		}

		JSONObject jsonInfo = new JSONObject();

		// name
		jsonInfo.put("name", editableModel.getName());

		// mesh
		String meshPath = "mesh/mesh.json";
		ModelMesh mesh = editableModel.getMesh();
		JSONObject jsonMesh = new JSONObject();

		ByteBuffer vertices = mesh.getVertices();
		JSONArray jsonVertices = new JSONArray();
		float precision = 1000.0f;
		while (vertices.hasRemaining()) {
			float x = Maths.approximatate(vertices.getFloat(), precision);
			float y = Maths.approximatate(vertices.getFloat(), precision);
			float z = Maths.approximatate(vertices.getFloat(), precision);
			float u = Maths.approximatate(vertices.getFloat(), precision);
			float v = Maths.approximatate(vertices.getFloat(), precision);
			float nx = Maths.approximatate(vertices.getFloat(), precision);
			float ny = Maths.approximatate(vertices.getFloat(), precision);
			float nz = Maths.approximatate(vertices.getFloat(), precision);
			String b1 = editableModel.getSkeleton().getBoneName(vertices.getInt());
			String b2 = editableModel.getSkeleton().getBoneName(vertices.getInt());
			String b3 = editableModel.getSkeleton().getBoneName(vertices.getInt());
			float w1 = Maths.approximatate(vertices.getFloat(), precision);
			float w2 = Maths.approximatate(vertices.getFloat(), precision);
			float w3 = Maths.approximatate(vertices.getFloat(), precision);
			float ao = Maths.approximatate(vertices.getFloat(), precision);

			jsonVertices.put(x);
			jsonVertices.put(y);
			jsonVertices.put(z);
			jsonVertices.put(u);
			jsonVertices.put(v);
			jsonVertices.put(nx);
			jsonVertices.put(ny);
			jsonVertices.put(nz);
			jsonVertices.put(b1);
			jsonVertices.put(b2);
			jsonVertices.put(b3);
			jsonVertices.put(w1);
			jsonVertices.put(w2);
			jsonVertices.put(w3);
			jsonVertices.put(ao);
		}
		jsonMesh.put("vertices", jsonVertices);

		ByteBuffer indices = mesh.getIndices();
		JSONArray jsonIndices = new JSONArray();
		while (indices.hasRemaining()) {
			short i = indices.getShort();
			jsonIndices.put(i);
		}
		jsonMesh.put("indices", jsonIndices);
		writeJSON(jsonMesh, dirPath, meshPath);
		jsonInfo.put("mesh", meshPath);

		// skeleton
		String skeletonPath = "skeleton/skeleton.json";
		jsonInfo.put("skeleton", skeletonPath);
		JSONObject jsonSkeleton = new JSONObject();
		JSONArray jsonBones = new JSONArray();
		jsonSkeleton.put("bones", jsonBones);
		for (Bone bone : editableModel.getSkeleton().getBones()) {
			JSONObject jsonBone = new JSONObject();

			jsonBone.put("name", bone.getName());

			if (bone.hasParent()) {
				jsonBone.put("parentName", bone.getParentName());
			}

			if (bone.hasChildren()) {
				JSONArray jsonChildren = new JSONArray();
				for (String childName : bone.getChildrens()) {
					jsonChildren.put(childName);
				}
				jsonBone.put("childrenNames", jsonChildren);
			}

			JSONObject jsonLocalBindTransform = new JSONObject();

			JSONObject jsonTranslation = new JSONObject();
			jsonTranslation.put("x", bone.getLocalTranslation().x);
			jsonTranslation.put("y", bone.getLocalTranslation().y);
			jsonTranslation.put("z", bone.getLocalTranslation().z);
			jsonLocalBindTransform.put("translation", jsonTranslation);

			JSONObject jsonRotation = new JSONObject();
			jsonRotation.put("x", bone.getLocalRotation().getX());
			jsonRotation.put("y", bone.getLocalRotation().getY());
			jsonRotation.put("z", bone.getLocalRotation().getZ());
			jsonRotation.put("w", bone.getLocalRotation().getW());
			jsonLocalBindTransform.put("rotation", jsonRotation);

			jsonBone.put("localBindTransform", jsonLocalBindTransform);

			jsonBones.put(jsonBone);
		}
		writeJSON(jsonSkeleton, dirPath, skeletonPath);

		// blocks
		String blocksPath = "blocks/blocks.json";
		jsonInfo.put("blocks", blocksPath);
		JSONObject jsonBlocks = new JSONObject();
		jsonBlocks.put("sizeUnit", editableModel.getBlockSizeUnit());
		JSONArray jsonBlocksData = new JSONArray();
		for (ModelBlockData blockData : editableModel.getRawBlockDatas().values()) {
			jsonBlocksData.put(blockData.getX());
			jsonBlocksData.put(blockData.getY());
			jsonBlocksData.put(blockData.getZ());
			jsonBlocksData.put(blockData.getBone(0));
			jsonBlocksData.put(blockData.getBone(1));
			jsonBlocksData.put(blockData.getBone(2));
			jsonBlocksData.put(blockData.getBoneWeight(0));
			jsonBlocksData.put(blockData.getBoneWeight(1));
			jsonBlocksData.put(blockData.getBoneWeight(2));
		}
		jsonBlocks.put("blocks", jsonBlocksData);
		writeJSON(jsonBlocks, dirPath, blocksPath);
		jsonInfo.put("blocks", blocksPath);

		// animations
		JSONArray jsonAnimations = new JSONArray();
		for (ModelSkeletonAnimation animation : editableModel.getAnimations()) {
			String animationPath = "animations/" + animation.getName().toLowerCase() + ".json";
			jsonAnimations.put(animationPath);

			JSONObject jsonAnimation = new JSONObject();
			jsonAnimation.put("name", animation.getName());
			jsonAnimation.put("duration", animation.getDuration());

			JSONArray jsonKeyFrames = new JSONArray();
			for (KeyFrame keyFrame : animation.getKeyFrames()) {
				JSONObject jsonKeyFrame = new JSONObject();
				jsonKeyFrame.put("time", keyFrame.getTime());

				JSONArray jsonPoses = new JSONArray();
				for (String boneName : keyFrame.getBoneKeyFrames().keySet()) {
					JSONObject jsonPose = new JSONObject();

					jsonPose.put("bone", boneName);

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

					jsonPose.put("transform", jsonTransform);

					jsonPoses.put(jsonPose);
				}
				jsonKeyFrame.put("pose", jsonPoses);
				jsonKeyFrames.put(jsonKeyFrame);
			}
			jsonAnimation.put("keyFrames", jsonKeyFrames);
			writeJSON(jsonAnimation, dirPath, animationPath);
		}
		jsonInfo.put("animations", jsonAnimations);

		// skins
		JSONArray jsonSkins = new JSONArray();
		for (ModelSkin skin : editableModel.getSkins()) {
			String rawPath = "skins/" + skin.getName().toLowerCase();
			String skinPath = rawPath + ".json";
			jsonSkins.put(skinPath);
			JSONObject jsonSkin = new JSONObject();
			jsonSkin.put("name", skin.getName());
			String texturePath = rawPath + ".png";
			ImageUtils.exportPNGImage(Paths.get(dirPath, texturePath).toFile(), skin.getGLTexture().getData());
			jsonSkin.put("texture", texturePath);
			writeJSON(jsonSkin, dirPath, skinPath);
		}
		jsonInfo.put("skins", jsonSkins);

		// finally, write file
		writeJSON(jsonInfo, dirPath, "info.json");
	}

	private static final void writeJSON(JSONObject obj, String dirpath, String... more) throws IOException {
		Path info = Paths.get(dirpath, more);
		File file = info.toFile();
		file.getParentFile().mkdirs();
		BufferedWriter output = new BufferedWriter(new FileWriter(file));
		output.write(obj.toString(4));
		output.close();
	}
}
