package com.grillecube.client.renderer.model;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lwjgl.BufferUtils;

import com.grillecube.common.JSONHelper;

public class JSONModelInitializer implements ModelInitializer {

	private String dirpath;

	public JSONModelInitializer(String dirpath) {
		this.dirpath = dirpath;
	}

	/** initializer override */
	@Override
	public void onInitialized(Model model) {

		try {
			this.parseJSON(model);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private final void parseJSON(Model model) throws JSONException, IOException {

		// get the info file
		JSONObject json = new JSONObject(JSONHelper.readFile(this.dirpath + "info.json"));

		// get the mesh path
		String meshpath = this.dirpath + json.getString("mesh");
		this.parseMesh(model, new JSONObject(JSONHelper.readFile(meshpath)));

		// get skeleton path
		String skeletonPath = this.dirpath + json.getString("skeleton");
		this.parseSkeleton(model, new JSONObject(JSONHelper.readFile(skeletonPath)));

		// get skins
		JSONArray skins = json.getJSONArray("skins");
		for (int i = 0; i < skins.length(); i++) {
			String skinpath = this.dirpath + skins.getString(i);
			this.parseSkin(model, new JSONObject(JSONHelper.readFile(skinpath)));
		}

		// get animations
		JSONArray animations = json.getJSONArray("animations");
		for (int i = 0; i < animations.length(); i++) {
			String animpath = this.dirpath + animations.getString(i);
			this.parseAnimation(new JSONObject(JSONHelper.readFile(animpath)));
		}
	}

	private void parseMesh(Model model, JSONObject mesh) {
		JSONArray vertices = mesh.getJSONArray("vertices");
		ByteBuffer verticesBuffer = BufferUtils.createByteBuffer(vertices.length() * 4);
		int i = 0;
		while (i < vertices.length()) {

			float x = (float) vertices.getDouble(i++);
			float y = (float) vertices.getDouble(i++);
			float z = (float) vertices.getDouble(i++);

			float uvx = (float) vertices.getDouble(i++);
			float uvy = (float) vertices.getDouble(i++);

			int j1 = vertices.getInt(i++);
			int j2 = vertices.getInt(i++);
			int j3 = vertices.getInt(i++);

			float w1 = (float) vertices.getDouble(i++);
			float w2 = (float) vertices.getDouble(i++);
			float w3 = (float) vertices.getDouble(i++);

			verticesBuffer.putFloat(x);
			verticesBuffer.putFloat(y);
			verticesBuffer.putFloat(z);

			verticesBuffer.putFloat(uvx);
			verticesBuffer.putFloat(uvy);

			verticesBuffer.putInt(j1);
			verticesBuffer.putInt(j2);
			verticesBuffer.putInt(j3);

			verticesBuffer.putFloat(w1);
			verticesBuffer.putFloat(w2);
			verticesBuffer.putFloat(w3);
		}

		verticesBuffer.flip();
		model.getMesh().setVertices(verticesBuffer);

		// TODO : indices?
		// JSONArray indices = mesh.getJSONArray("indices");

	}

	private void parseSkeleton(Model model, JSONObject skeleton) {
		// TODO Auto-generated method stub

	}

	private void parseSkin(Model model, JSONObject skin) {
		String name = this.dirpath + skin.getString("name");
		String texture = this.dirpath + skin.getString("texture");
		model.addSkin(new ModelSkin(name, texture));
	}

	private void parseAnimation(JSONObject skin) {
		// TODO
	}

}
