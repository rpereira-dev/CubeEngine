package com.grillecube.client.renderer.model.dae;

import com.grillecube.client.renderer.model.dae.datastructures.AnimationData;
import com.grillecube.client.renderer.model.dae.datastructures.MeshData;
import com.grillecube.client.renderer.model.dae.datastructures.ModelData;
import com.grillecube.client.renderer.model.dae.datastructures.SkeletonData;
import com.grillecube.client.renderer.model.dae.datastructures.SkinningData;
import com.grillecube.client.renderer.model.parser.xml.XMLNode;
import com.grillecube.client.renderer.model.parser.xml.XMLParser;

public class ColladaLoader {

	public static ModelData loadColladaModel(String colladaFile, int maxWeights) {
		XMLNode node = XMLParser.loadXmlFile(colladaFile);

		SkinLoader skinLoader = new SkinLoader(node.getChild("library_controllers"), maxWeights);
		SkinningData skinningData = skinLoader.extractSkinData();

		SkeletonLoader jointsLoader = new SkeletonLoader(node.getChild("library_visual_scenes"),
				skinningData.jointOrder);
		SkeletonData jointsData = jointsLoader.extractBoneData();

		GeometryLoader g = new GeometryLoader(node.getChild("library_geometries"), skinningData.verticesSkinData);
		MeshData meshData = g.extractModelData();

		return new ModelData(meshData, jointsData);
	}

	public static AnimationData loadColladaAnimation(String filepath) {
		XMLNode node = XMLParser.loadXmlFile(filepath);
		XMLNode animNode = node.getChild("library_animations");
		XMLNode jointsNode = node.getChild("library_visual_scenes");
		AnimationLoader loader = new AnimationLoader(animNode, jointsNode);
		AnimationData animData = loader.extractAnimation();
		return animData;
	}

}
