package com.grillecube.client.renderer.model.collada;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.lwjgl.BufferUtils;

import com.grillecube.client.renderer.model.Model;
import com.grillecube.client.renderer.model.ModelInitializer;
import com.grillecube.client.renderer.model.ModelSkeleton;
import com.grillecube.client.renderer.model.ModelSkin;
import com.grillecube.client.renderer.model.animation.BoneTransform;
import com.grillecube.client.renderer.model.animation.KeyFrame;
import com.grillecube.client.renderer.model.animation.ModelAnimation;
import com.grillecube.client.renderer.model.collada.xml.XmlNode;
import com.grillecube.client.renderer.model.collada.xml.XmlParser;
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.maths.Quaternion;
import com.grillecube.common.maths.Vector3f;

public class ColladaModelInitializer implements ModelInitializer {

	private String dirpath;

	public ColladaModelInitializer(String dirpath) {
		this.dirpath = dirpath;
		if (!this.dirpath.endsWith(File.separator)) {
			this.dirpath += File.separator;
		}
	}

	/** initializer override */
	@Override
	public void onInitialized(Model model) {

		try {
			this.parseCollada(model);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private final void parseCollada(Model model) throws JSONException, IOException {

		String path = this.dirpath + "model.dae";
		XmlNode node = XmlParser.loadXmlFile(path);
		SkinLoader skinLoader = new SkinLoader(node.getChild("library_controllers"), 3);
		SkinningData skinningData = skinLoader.extractSkinData();
		SkeletonLoader jointsLoader = new SkeletonLoader(node.getChild("library_visual_scenes"),
				skinningData.jointOrder);
		SkeletonData jointsData = jointsLoader.extractBoneData();

		GeometryLoader g = new GeometryLoader(node.getChild("library_geometries"), skinningData.verticesSkinData);
		MeshData meshData = g.extractModelData();

		ByteBuffer vertices = BufferUtils.createByteBuffer(meshData.getVertices().length * 4);
		for (float f : meshData.getVertices()) {
			vertices.putFloat(f);
		}
		vertices.flip();
		model.getMesh().setVertices(vertices);

		ByteBuffer indices = BufferUtils.createByteBuffer(meshData.getIndices().length * 2);
		for (short indice : meshData.getIndices()) {
			indices.putShort(indice);
		}
		indices.flip();
		model.getMesh().setIndices(indices);

		this.addBone(model.getSkeleton(), jointsData.headJoint, null);
		model.getSkeleton().setRootBone(jointsData.headJoint.nameId);

		String skinName = "Default Skin";
		String skinTexture = this.dirpath + "model.png";
		model.addSkin(new ModelSkin(skinName, skinTexture));

		// animation
		ModelAnimation modelAnimation = loadAnimation(path);
		model.addAnimation(modelAnimation);
	}

	private void addBone(ModelSkeleton modelSkeleton, JointData bone, String parentName) {
		modelSkeleton.addBone(bone.nameId, parentName, bone.bindLocalTransform).setID(bone.index);
		for (JointData child : bone.children) {
			this.addBone(modelSkeleton, child, bone.nameId);
		}
	}

	/**
	 * Loads up a collada animation file, and returns and animation created from
	 * the extracted animation data from the file.
	 * 
	 * @param colladaFile
	 *            - the collada file containing data about the desired
	 *            animation.
	 * @return The animation made from the data in the file.
	 */
	public static ModelAnimation loadAnimation(String colladaFile) {
		AnimationData animationData = loadColladaAnimation(colladaFile);
		ArrayList<KeyFrame> frames = new ArrayList<KeyFrame>(animationData.keyFrames.length);
		for (int i = 0; i < animationData.keyFrames.length; i++) {
			frames.add(createKeyFrame(animationData.keyFrames[i]));
		}
		return new ModelAnimation(colladaFile, (long) (animationData.lengthSeconds * 1000.0f), frames);
	}

	public static AnimationData loadColladaAnimation(String colladaFile) {
		XmlNode node = XmlParser.loadXmlFile(colladaFile);
		XmlNode animNode = node.getChild("library_animations");
		XmlNode jointsNode = node.getChild("library_visual_scenes");
		AnimationLoader loader = new AnimationLoader(animNode, jointsNode);
		AnimationData animData = loader.extractAnimation();
		return animData;
	}

	/**
	 * Creates a keyframe from the data extracted from the collada file.
	 * 
	 * @param data
	 *            - the data about the keyframe that was extracted from the
	 *            collada file.
	 * @return The keyframe.
	 */
	private static KeyFrame createKeyFrame(KeyFrameData data) {
		HashMap<String, BoneTransform> map = new HashMap<String, BoneTransform>();
		for (JointTransformData jointData : data.jointTransforms) {
			BoneTransform jointTransform = createTransform(jointData);
			map.put(jointData.jointNameId, jointTransform);
		}
		return new KeyFrame((long) (data.time * 1000.0f), map);
	}

	/**
	 * Creates a joint transform from the data extracted from the collada file.
	 * 
	 * @param data
	 *            - the data from the collada file.
	 * @return The joint transform.
	 */
	private static BoneTransform createTransform(JointTransformData data) {
		Matrix4f mat = data.jointLocalTransform;
		Vector3f translation = new Vector3f(mat.m30, mat.m31, mat.m32);
		Quaternion rotation = Quaternion.fromMatrix(mat);
		return new BoneTransform(translation, rotation);
	}

}
