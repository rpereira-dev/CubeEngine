package com.grillecube.client.renderer.model.editor.mesher;

import java.util.HashMap;

import com.grillecube.client.renderer.model.Model;
import com.grillecube.client.renderer.model.ModelInitializer;
import com.grillecube.common.maths.Vector3i;

public class EditableModel extends Model {

	/**
	 * the size of a single block of this model (N.B: a terrain block size is
	 * 1.0f)
	 */
	private float blockSizeUnit;

	/** the mesher to be used for this model */
	private final ModelMesher modelMesher;
	private boolean meshUpToDate;

	/** blocks data */
	private HashMap<Vector3i, ModelBlockData> blocksData;

	/**
	 * the model origin (the origin coordinates, in the model referential), so
	 * when and entity is located at (x, y, z), the concrete model's block at
	 * coordinate (x, y, z) is the one at this origin
	 */
	private final Vector3i origin;

	private int minx;
	private int miny;
	private int minz;

	private int maxx;
	private int maxy;
	private int maxz;

	public EditableModel() {
		this(null);
	}

	public EditableModel(ModelInitializer modelInitializer) {
		super(modelInitializer);
		this.blockSizeUnit = 1.0f;
		this.blocksData = new HashMap<Vector3i, ModelBlockData>();
		this.origin = new Vector3i(0, 0, 0);
		this.modelMesher = new ModelMesherCull();
		this.meshUpToDate = true;
		this.resetMinMax();
	}

	/** @see ModelBuildingData#origin */
	public final Vector3i getOrigin() {
		return (this.origin);
	}

	/** @see ModelBuildingData#origin */
	public final void setOrigin(Vector3i origin) {
		this.setOrigin(origin.x, origin.y, origin.z);
	}

	/** @see ModelBuildingData#origin */
	public final void setOrigin(int x, int y, int z) {
		this.origin.set(x, y, z);
	}

	/**
	 * the size of a single block of this model (N.B: a terrain block size is
	 * 1.0f).
	 * 
	 * Notice that each block of a model has the same size, they are "uniforms"
	 * to make things easier for now
	 */
	public final void setBlockSizeUnit(float size) {
		this.blockSizeUnit = size;
	}

	public final float getBlockSizeUnit() {
		return (this.blockSizeUnit);
	}

	/**
	 * return the block data at given coordinates relatively to the model basis
	 * (0 == model center)
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public final ModelBlockData getBlockData(Vector3i index) {
		return (this.blocksData.get(index));
	}

	public final ModelBlockData getBlockData(int x, int y, int z) {
		return (this.getBlockData(new Vector3i(x, y, z)));
	}

	/** */
	public final HashMap<Vector3i, ModelBlockData> getRawBlockDatas() {
		return (this.blocksData);
	}

	private final void resetMinMax() {
		this.minx = Integer.MAX_VALUE;
		this.miny = Integer.MAX_VALUE;
		this.minz = Integer.MAX_VALUE;
		this.maxx = Integer.MIN_VALUE;
		this.maxy = Integer.MIN_VALUE;
		this.maxz = Integer.MIN_VALUE;

		for (ModelBlockData blockData : blocksData.values()) {
			if (blockData.getPos().x < this.minx) {
				this.minx = blockData.getPos().x;
			}

			if (blockData.getPos().y < this.miny) {
				this.miny = blockData.getPos().y;
			}

			if (blockData.getPos().z < this.minz) {
				this.minz = blockData.getPos().z;
			}

			if (blockData.getPos().x > this.maxx) {
				this.maxx = blockData.getPos().x;
			}

			if (blockData.getPos().y > this.maxy) {
				this.maxy = blockData.getPos().y;
			}

			if (blockData.getPos().z > this.maxz) {
				this.maxz = blockData.getPos().z;
			}
		}
	}

	/** */
	public final void setRawBlockDatas(HashMap<Vector3i, ModelBlockData> blocksData) {
		this.blocksData = blocksData;
		this.resetMinMax();
	}

	/**
	 * set the block data for this model, may cause crash if capacity isnt
	 * enough, @see {@link #setBlockData(ModelBlockData, int, int, int)} to
	 * ensure the capacity automatically
	 */
	public final void setBlockData(ModelBlockData blockData) {
		this.blocksData.put(blockData.getPos(), blockData);
		if (blockData.getPos().x < this.minx) {
			this.minx = blockData.getPos().x;
		}

		if (blockData.getPos().y < this.miny) {
			this.miny = blockData.getPos().y;
		}

		if (blockData.getPos().z < this.minz) {
			this.minz = blockData.getPos().z;
		}

		if (blockData.getPos().x > this.maxx) {
			this.maxx = blockData.getPos().x;
		}

		if (blockData.getPos().y > this.maxy) {
			this.maxy = blockData.getPos().y;
		}

		if (blockData.getPos().z > this.maxz) {
			this.maxz = blockData.getPos().z;
		}
	}

	public final boolean unsetBlockData(Vector3i pos) {
		ModelBlockData data = this.blocksData.remove(pos);
		if (data == null) {
			return (false);
		}
		if (data.getX() == this.minx || data.getX() == this.maxx || data.getY() == this.miny || data.getY() == this.maxy
				|| data.getZ() == this.minz || data.getZ() == this.maxz) {
			this.resetMinMax();
		}
		return (true);
	}

	/**
	 * request a mesh update
	 */
	public final void requestMeshUpdate() {
		this.meshUpToDate = false;
	}

	@Override
	protected void onBound() {
		if (!this.meshUpToDate) {
			this.generateMesh();
		}
	}

	public final void generateMesh() {
		this.modelMesher.generate(this);
		this.meshUpToDate = true;
	}

	public final int getMinx() {
		return (this.minx);
	}

	public final int getMiny() {
		return (this.miny);
	}

	public final int getMinz() {
		return (this.minz);
	}

	public final int getMaxx() {
		return (this.maxx);
	}

	public final int getMaxy() {
		return (this.maxy);
	}

	public final int getMaxz() {
		return (this.maxz);
	}

	public int getBlockDataCount() {
		return (this.blocksData.size());
	}
}