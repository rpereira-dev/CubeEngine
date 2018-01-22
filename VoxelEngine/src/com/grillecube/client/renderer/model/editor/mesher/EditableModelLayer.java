package com.grillecube.client.renderer.model.editor.mesher;

import java.util.ArrayList;
import java.util.HashMap;

import com.grillecube.common.maths.Vector3i;

public class EditableModelLayer {

	private final String layerName;
	private HashMap<Vector3i, ModelBlockData> blocksData;
	private ArrayList<ModelPlane> planes;

	/**
	 * the size of a single block of this model (N.B: a terrain block size is 1.0f)
	 */
	private float blockSizeUnit;

	private int minx;
	private int miny;
	private int minz;

	private int maxx;
	private int maxy;
	private int maxz;

	private boolean planesUpToDate;
	private boolean isVisible;

	public EditableModelLayer(String layerName) {
		this.layerName = layerName;
		this.blocksData = new HashMap<Vector3i, ModelBlockData>();
		this.planes = new ArrayList<ModelPlane>();
		this.blockSizeUnit = 1.0f;
		this.isVisible = true;
		this.planesUpToDate = false;
		this.updateMinMax();
	}

	/**
	 * set the block data for this model, may cause crash if capacity isnt
	 * enough, @see {@link #setBlockData(ModelBlockData, int, int, int)} to ensure
	 * the capacity automatically
	 */
	public final boolean setBlockData(ModelBlockData blockData) {

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
		return (true);
	}

	private final void updateMinMax() {
		this.minx = Integer.MAX_VALUE;
		this.miny = Integer.MAX_VALUE;
		this.minz = Integer.MAX_VALUE;
		this.maxx = Integer.MIN_VALUE;
		this.maxy = Integer.MIN_VALUE;
		this.maxz = Integer.MIN_VALUE;

		for (ModelBlockData blockData : this.blocksData.values()) {
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

	/**
	 * @param index
	 * @return the block data at given index
	 */
	public final ModelBlockData getBlockData(Vector3i index) {
		return (this.blocksData.get(index));
	}

	public final EditableModelLayer clone() {
		EditableModelLayer copy = new EditableModelLayer(this.layerName);
		for (ModelBlockData blockData : this.blocksData.values()) {
			blockData = blockData.clone();
			copy.blocksData.put(blockData.getPos(), blockData);
		}

		copy.minx = this.minx;
		copy.miny = this.miny;
		copy.minz = this.minz;

		copy.maxx = this.maxx;
		copy.maxy = this.maxy;
		copy.maxz = this.maxz;

		copy.blockSizeUnit = this.blockSizeUnit;
		copy.isVisible = this.isVisible;

		copy.planes.addAll(this.planes);

		return (copy);
	}

	/**
	 * unset the block at given position
	 * 
	 * @param pos
	 * @return : true if a block was actually removed
	 */
	public final boolean unsetBlockData(Vector3i pos) {
		ModelBlockData data = this.blocksData.remove(pos);
		if (data == null) {
			return (false);
		}
		if (data.getX() == this.minx || data.getX() == this.maxx || data.getY() == this.miny || data.getY() == this.maxy
				|| data.getZ() == this.minz || data.getZ() == this.maxz) {
			this.updateMinMax();
		}
		return (true);
	}

	/** rotate blocks by 90 degrees around x axis */
	public final void rotateX() {
		HashMap<Vector3i, ModelBlockData> oldBlockDatas = this.blocksData;
		this.blocksData = new HashMap<Vector3i, ModelBlockData>();
		this.updateMinMax();

		for (ModelBlockData blockData : oldBlockDatas.values()) {
			int y = blockData.getZ();
			int z = -blockData.getY();
			blockData.setY(y);
			blockData.setZ(z);
			this.setBlockData(blockData);
		}
	}

	/** rotate blocks by 90 degrees around y axis */
	public final void rotateY() {
		HashMap<Vector3i, ModelBlockData> oldBlockDatas = this.blocksData;
		this.blocksData = new HashMap<Vector3i, ModelBlockData>();
		this.updateMinMax();

		for (ModelBlockData blockData : oldBlockDatas.values()) {
			int x = blockData.getZ();
			int z = -blockData.getX();
			blockData.setX(x);
			blockData.setZ(z);
			this.setBlockData(blockData);
		}
	}

	/** rotate blocks by 90 degrees around z axis */
	public final void rotateZ() {
		HashMap<Vector3i, ModelBlockData> oldBlockDatas = this.blocksData;
		this.blocksData = new HashMap<Vector3i, ModelBlockData>();
		this.updateMinMax();

		for (ModelBlockData blockData : oldBlockDatas.values()) {
			int x = blockData.getY();
			int y = -blockData.getX();
			blockData.setX(x);
			blockData.setY(y);
			this.setBlockData(blockData);
		}
	}

	/** translate the whole model blocks */
	public final void translate(int dx, int dy, int dz) {
		HashMap<Vector3i, ModelBlockData> oldBlockDatas = this.blocksData;
		this.blocksData = new HashMap<Vector3i, ModelBlockData>();
		this.updateMinMax();

		for (ModelBlockData blockData : oldBlockDatas.values()) {
			int x = blockData.getX() + dx;
			int y = blockData.getY() + dy;
			int z = blockData.getZ() + dz;
			blockData.setX(x);
			blockData.setY(y);
			blockData.setZ(z);
			this.setBlockData(blockData);
		}
	}

	/** min x block coordinates */
	public final int getMinx() {
		return (this.minx);
	}

	/** min y block coordinates */
	public final int getMiny() {
		return (this.miny);
	}

	/** min z block coordinates */
	public final int getMinz() {
		return (this.minz);
	}

	/** max x block coordinates */
	public final int getMaxx() {
		return (this.maxx);
	}

	/** max y block coordinates */
	public final int getMaxy() {
		return (this.maxy);
	}

	/** max z block coordinates */
	public final int getMaxz() {
		return (this.maxz);
	}

	/**
	 * @return : return number of set blocks
	 */
	public int getBlockDataCount() {
		return (this.blocksData.size());
	}

	/**
	 * the size of a single block of this model (N.B: a terrain block size is 1.0f).
	 * 
	 * Notice that each block of a model has the same size, they are "uniforms" to
	 * make things easier for now
	 */
	public final void setBlockSizeUnit(float size) {
		this.blockSizeUnit = size;
	}

	public final float getBlockSizeUnit() {
		return (this.blockSizeUnit);
	}

	public final void setPlanesUpToDate() {
		this.planesUpToDate = true;
	}

	public final boolean arePlanesUpToDate() {
		return (this.planesUpToDate);
	}

	/**
	 * request a mesh update
	 */
	public final void requestPlanesUpdate() {
		this.planesUpToDate = false;
	}

	public final HashMap<Vector3i, ModelBlockData> getRawBlockDatas() {
		return (this.blocksData);
	}

	public ArrayList<ModelPlane> getPlanes() {
		return (this.planes);
	}

	public final String getName() {
		return (this.layerName);
	}

	public final boolean isVisible() {
		return (this.isVisible);
	}

	public final void setVisible(boolean b) {
		this.isVisible = b;
	}
}
