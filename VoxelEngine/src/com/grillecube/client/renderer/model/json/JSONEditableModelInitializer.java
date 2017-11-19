package com.grillecube.client.renderer.model.json;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.grillecube.client.renderer.model.Model;
import com.grillecube.client.renderer.model.editor.mesher.EditableModel;
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

		model.setBlockSizeUnit((float) (blocks.getDouble("sizeUnit")));
		JSONArray blocksData = blocks.getJSONArray("blocks");

		ArrayList<ModelBlockData> blockDatas = new ArrayList<ModelBlockData>();
		int minx = 0, miny = 0, minz = 0;
		int maxx = 0, maxy = 0, maxz = 0;
		for (int i = 0; i < blocksData.length();) {
			int x = blocksData.getInt(i++);
			int y = blocksData.getInt(i++);
			int z = blocksData.getInt(i++);
			String b1 = blocksData.getString(i++);
			String b2 = blocksData.getString(i++);
			String b3 = blocksData.getString(i++);
			float w1 = (float) blocksData.getDouble(i++);
			float w2 = (float) blocksData.getDouble(i++);
			float w3 = (float) blocksData.getDouble(i++);

			ModelBlockData blockData = new ModelBlockData(x, y, z);
			blockData.setBone(0, b1, w1);
			blockData.setBone(1, b2, w2);
			blockData.setBone(2, b3, w3);
			blockDatas.add(blockData);
			if (x < minx) {
				minx = x;
			} else if (x > maxx) {
				maxx = x;
			}

			if (y < miny) {
				miny = y;
			} else if (y > maxy) {
				maxy = y;
			}

			if (z < minz) {
				minz = z;
			} else if (z > maxz) {
				maxz = z;
			}

		}
		model.allocate(minx, miny, minz, maxx, maxy, maxz);
		for (ModelBlockData blockData : blockDatas) {
			model.setBlockDataUncheck(blockData);
		}
	}
}
