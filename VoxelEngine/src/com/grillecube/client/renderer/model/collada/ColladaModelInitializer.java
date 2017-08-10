package com.grillecube.client.renderer.model.collada;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.json.JSONException;
import org.lwjgl.BufferUtils;

import com.grillecube.client.renderer.model.Model;
import com.grillecube.client.renderer.model.ModelInitializer;
import com.grillecube.client.renderer.model.ModelSkeleton;
import com.grillecube.client.renderer.model.ModelSkin;
import com.grillecube.client.renderer.model.collada.xml.XmlNode;
import com.grillecube.client.renderer.model.collada.xml.XmlParser;

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

		XmlNode node = XmlParser.loadXmlFile(this.dirpath + "model.dae");
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
	}

	private void addBone(ModelSkeleton modelSkeleton, JointData bone, String parentName) {
		modelSkeleton.addBone(bone.nameId, parentName, bone.bindLocalTransform).setID(bone.index);
		for (JointData child : bone.children) {
			this.addBone(modelSkeleton, child, bone.nameId);
		}
	}

}
