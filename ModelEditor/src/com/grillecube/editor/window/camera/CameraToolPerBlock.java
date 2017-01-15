package com.grillecube.editor.window.camera;

import java.util.Arrays;

import com.grillecube.engine.Logger;
import com.grillecube.engine.maths.Vector3i;
import com.grillecube.engine.maths.Vector4f;
import com.grillecube.engine.renderer.model.Model;
import com.grillecube.engine.renderer.model.builder.ModelPartBuilder;
import com.grillecube.engine.renderer.model.builder.ModelPartSkinBuilder;
import com.grillecube.engine.resources.Compress;

public abstract class CameraToolPerBlock extends CameraTool {

	public CameraToolPerBlock(Vector4f color) {
		super(color);
	}

	@Override
	public Action newAction(Model model, ModelPartBuilder part, ModelPartSkinBuilder skin, CameraEditor camera) {
		return (new Action(model, part, camera) {
			private byte[] _blocks;
			private int _uncompressed_blocks_length;
			private int[] _colors;
			private int _uncompressed_color_length;

			@Override
			protected boolean doo() {

				this._blocks = Compress.compressBooleanArray(part.getRawBlock());
				this._uncompressed_blocks_length = part.getRawBlock().length;

				this._colors = Compress.compressIntArray(skin.getRawColors());
				this._uncompressed_color_length = skin.getRawColors().length;

				Vector3i one = camera.getBlockOne();
				Vector3i two = camera.getBlockTwo();

				Logger.get().log(Logger.Level.DEBUG, "one: " + one, "two: " + two);

				int minx, miny, minz;
				int maxx, maxy, maxz;
				int x, y, z;

				if (one.x < two.x) {
					minx = one.x;
					maxx = two.x;
				} else {
					minx = two.x;
					maxx = one.x;
				}

				if (one.y < two.y) {
					miny = one.y;
					maxy = two.y;
				} else {
					miny = two.y;
					maxy = one.y;
				}

				if (one.z < two.z) {
					minz = one.z;
					maxz = two.z;
				} else {
					minz = two.z;
					maxz = one.z;
				}

				boolean updated = false;

				for (x = minx; x <= maxx; x++) {
					for (y = miny; y <= maxy; y++) {
						for (z = minz; z <= maxz; z++) {
							if (applyToolOnBlock(model, part, skin, camera, x, y, z)) {
								updated = true;
							}
						}
					}
				}

				return (updated);
			}

			@Override
			protected boolean undo() {
				part.setRawBlocks(Compress.decompressBooleanArray(this._blocks, this._uncompressed_blocks_length));
				skin.setRawColors(Compress.decompressIntArray(this._colors, this._uncompressed_color_length));
				return (true);
			}

		});
	}

	protected abstract boolean applyToolOnBlock(Model model, ModelPartBuilder part, ModelPartSkinBuilder skin,
			CameraEditor camera, int x, int y, int z);
}
