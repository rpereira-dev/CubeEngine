package com.grillecube.client.renderer.model.json;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.grillecube.client.renderer.model.Model;
import com.grillecube.client.renderer.model.editor.mesher.BlockData;
import com.grillecube.client.renderer.model.editor.mesher.EditableModel;
import com.grillecube.common.JSONHelper;
import com.grillecube.common.Logger;

public class JSONEditableModelInitializer extends JSONModelInitializer {

	public JSONEditableModelInitializer(String dirpath) {
		super(dirpath);
	}

	@Override
	protected void parseJSON(Model model, JSONObject jsonInfo) throws JSONException, IOException {

		// model name
		super.parseJSONName(model, jsonInfo);

		// get skeleton
		super.parseJSONSkeleton(model, jsonInfo);

		// get skins
		super.parseJSONSkins(model, jsonInfo);

		// get animations
		super.parseJSONAnimations(model, jsonInfo);

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

		ArrayList<BlockData> blocksDataList = new ArrayList<BlockData>(blocksData.length());
		int xmax = 0;
		int ymax = 0;
		int zmax = 0;
		for (int i = 0; i < blocksData.length();) {
			int x = blocksData.getInt(i++);
			int y = blocksData.getInt(i++);
			int z = blocksData.getInt(i++);
			int b1 = super.getBoneID(model, blocksData.getString(i++));
			int b2 = super.getBoneID(model, blocksData.getString(i++));
			int b3 = super.getBoneID(model, blocksData.getString(i++));
			float w1 = (float) blocksData.getDouble(i++);
			float w2 = (float) blocksData.getDouble(i++);
			float w3 = (float) blocksData.getDouble(i++);

			BlockData blockData = new BlockData(x, y, z);
			blockData.setBoneWeight(0, b1, w1);
			blockData.setBoneWeight(1, b2, w2);
			blockData.setBoneWeight(2, b3, w3);

			blocksDataList.add(blockData);

			if (x > xmax) {
				xmax = x;
			}

			if (y > ymax) {
				ymax = y;
			}

			if (z > zmax) {
				zmax = z;
			}
		}

		model.resize(xmax + 1, ymax + 1, zmax + 1);
		for (BlockData blockData : blocksDataList) {
			model.addBlockData(blockData);
		}

	}

}
