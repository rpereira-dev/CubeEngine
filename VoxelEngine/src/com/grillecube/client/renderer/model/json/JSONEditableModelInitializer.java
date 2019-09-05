package com.grillecube.client.renderer.model.json;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.grillecube.client.renderer.model.Model;
import com.grillecube.client.renderer.model.editor.mesher.EditableModel;
import com.grillecube.client.renderer.model.editor.mesher.EditableModelLayer;
import com.grillecube.client.renderer.model.editor.mesher.ModelBlockData;
import com.grillecube.common.Logger;
import com.grillecube.common.utils.JSONHelper;

public class JSONEditableModelInitializer extends JSONModelInitializer {

	public JSONEditableModelInitializer(String dirpath) {
		super(dirpath);
	}

	@Override
	protected void parseJSON(Model model, JSONObject jsonInfo) throws JSONException, IOException {
		super.parseJSON(model, jsonInfo);
		// get the blocks
		this.parseJSONBlocks((EditableModel) model, jsonInfo);
	}

	private void parseJSONBlocks(EditableModel model, JSONObject jsonInfo) {
		String blockspath = super.dirpath + jsonInfo.getString("blocks");
		JSONObject blocks;
		try {
			blocks = new JSONObject(JSONHelper.readFile(blockspath));
		} catch (Exception e) {
			e.printStackTrace(Logger.get().getPrintStream());
			return;
		}

		JSONObject origin = blocks.getJSONObject("origin");
		float x = (float) origin.getDouble("x");
		float y = (float) origin.getDouble("y");
		float z = (float) origin.getDouble("z");
		model.setOrigin(x, y, z);

		JSONArray layers = blocks.getJSONArray("layers");
		for (int i = 0; i < layers.length(); i++) {
			JSONObject layer = layers.getJSONObject(i);
			String name = layer.getString("name");
			float sizeUnit = (float) layer.getDouble("sizeUnit");
			EditableModelLayer editableModelLayer = new EditableModelLayer(name);
			editableModelLayer.setBlockSizeUnit(sizeUnit);
			model.setLayer(editableModelLayer);
		}

		JSONArray layersData = blocks.getJSONArray("layersData");
		for (int i = 0; i < layersData.length(); i++) {
			JSONObject layerData = layersData.getJSONObject(i);
			String layerName = layerData.getString("layer");

			EditableModelLayer editableModelLayer = model.getLayer(layerName);
			if (editableModelLayer == null) {
				continue;
			}
			JSONArray layerBlocksData = layerData.getJSONArray("blocks");

			for (int j = 0; j < layerBlocksData.length();) {
				int ix = layerBlocksData.getInt(j++);
				int iy = layerBlocksData.getInt(j++);
				int iz = layerBlocksData.getInt(j++);
				String b1 = layerBlocksData.getString(j++);
				String b2 = layerBlocksData.getString(j++);
				String b3 = layerBlocksData.getString(j++);
				float w1 = (float) layerBlocksData.getDouble(j++);
				float w2 = (float) layerBlocksData.getDouble(j++);
				float w3 = (float) layerBlocksData.getDouble(j++);

				ModelBlockData blockData = new ModelBlockData(ix, iy, iz);
				blockData.setBone(0, b1, w1);
				blockData.setBone(1, b2, w2);
				blockData.setBone(2, b3, w3);
				editableModelLayer.setBlockData(blockData);
			}
		}
	}
}
