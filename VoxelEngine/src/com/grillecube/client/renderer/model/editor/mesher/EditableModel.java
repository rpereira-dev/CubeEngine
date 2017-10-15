package com.grillecube.client.renderer.model.editor.mesher;

import com.grillecube.client.renderer.model.Model;
import com.grillecube.client.renderer.model.ModelInitializer;
import com.grillecube.common.maths.Maths;
import com.grillecube.common.maths.Vector3i;

public class EditableModel extends Model {

	/**
	 * the size of a single block of this model (N.B: a terrain block size is
	 * 1.0f)
	 */
	private float blockSizeUnit;

	/** the mesher to be used for this model */
	private ModelMesher modelMesher;

	/** blocks data */
	private BlockData[][][] blocksData;

	/** minimum coordinates */
	private int minx;
	private int miny;
	private int minz;
	private int maxx;
	private int maxy;
	private int maxz;

	/**
	 * the model origin (the origin coordinates, in the model referential), so
	 * when and entity is located at (x, y, z), the concrete model's block at
	 * coordinate (x, y, z) is the one at this origin
	 */
	private final Vector3i origin;

	public EditableModel() {
		this(null);
	}

	public EditableModel(ModelInitializer modelInitializer) {
		super(modelInitializer);
		this.blockSizeUnit = 1.0f;
		this.blocksData = null;
		this.origin = new Vector3i(0, 0, 0);
		this.modelMesher = null;
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
	public final BlockData getBlockData(int x, int y, int z) {
		if (x < this.minx || y < this.miny || z < this.minz || x > this.maxx || y > this.maxy || z > this.maxz) {
			return (null);
		}
		int ix = x - this.minx;
		int iy = y - this.miny;
		int iz = z - this.minz;
		return (this.blocksData[ix][iy][iz]);
	}

	/** set the block data for this model, and ensure the container capacity */
	public final void setBlockData(BlockData blockData, int x, int y, int z) {
		this.ensureCapacity(x, y, z);
		this.setBlockDataUncheck(blockData, x, y, z);
	}

	/**
	 * set the block data for this model, may cause crash if capacity isnt
	 * enough, @see {@link #setBlockData(BlockData, int, int, int)} to ensure
	 * the capacity automatically
	 */
	public final void setBlockDataUncheck(BlockData blockData, int x, int y, int z) {
		int ix = x - this.minx;
		int iy = y - this.miny;
		int iz = z - this.minz;
		this.blocksData[ix][iy][iz] = blockData;
	}

	/**
	 * ensure that this model can hold block at coordinates (x, y, z)
	 */
	public final void ensureCapacity(int x, int y, int z) {
		int mx = Maths.min(x, this.minx);
		int my = Maths.min(y, this.miny);
		int mz = Maths.min(z, this.minz);
		int Mx = Maths.max(x, this.maxx);
		int My = Maths.max(y, this.maxy);
		int Mz = Maths.max(z, this.maxz);
		if (this.blocksData != null && mx == this.minx && my == this.miny && mz == this.minz && Mx == this.maxx
				&& My == this.maxy && Mz == this.maxz) {
			return;
		}

		int oldminx = this.minx;
		int oldminy = this.miny;
		int oldminz = this.minz;
		int oldsizex = this.getSizeX();
		int oldsizey = this.getSizeY();
		int oldsizez = this.getSizeZ();
		BlockData[][][] oldBlocksData = this.blocksData;

		this.minx = mx;
		this.miny = my;
		this.minz = mz;
		this.maxx = Mx;
		this.maxy = My;
		this.maxz = Mz;
		this.blocksData = new BlockData[this.getSizeX()][this.getSizeY()][this.getSizeZ()];

		if (oldBlocksData != null) {
			int dx = oldminx - mx;
			int dy = oldminy - my;
			int dz = oldminz - mz;

			// copy and offset previously set blocks
			for (int i = 0; i < oldsizex; i++) {
				for (int j = 0; j < oldsizey; j++) {
					for (int k = 0; k < oldsizez; k++) {
						this.blocksData[i + dx][j + dy][k + dz] = oldBlocksData[i][j][k];
					}
				}
			}
		}
	}

	/**
	 * allocate the block data containers, and delete every previously set block
	 * data
	 */
	public final void allocate(int minx, int miny, int minz, int maxx, int maxy, int maxz) {
		this.minx = minx;
		this.miny = miny;
		this.minz = minz;
		this.maxx = maxx;
		this.maxy = maxy;
		this.maxz = maxz;
		this.blocksData = new BlockData[this.getSizeX()][this.getSizeY()][this.getSizeZ()];
	}

	/** unset the block data at the given coordinates */
	public final void unsetBlockData(int x, int y, int z) {
		this.blocksData[x][y][z] = null;
	}

	public final void generate() {
		if (this.modelMesher != null) {
			return;
		}
		this.modelMesher = new ModelMesherCull();
	}

	@Override
	public void preRender() {
		super.preRender();
		if (this.modelMesher != null) {
			this.modelMesher.generate(this);
			this.getMesh().setIndices(this.modelMesher.getIndices());
			this.getMesh().setVertices(this.modelMesher.getVertices());
			this.modelMesher = null;
		}
	}

	public final int getMinX() {
		return (this.minx);
	}

	public final int getMinY() {
		return (this.miny);
	}

	public final int getMinZ() {
		return (this.minz);
	}

	public final int getMaxX() {
		return (this.maxx);
	}

	public final int getMaxY() {
		return (this.maxy);
	}

	public final int getMaxZ() {
		return (this.maxz);
	}

	public final int getSizeX() {
		return (this.maxx - this.minx + 1);
	}

	public final int getSizeY() {
		return (this.maxy - this.miny + 1);
	}

	public final int getSizeZ() {
		return (this.maxz - this.minz + 1);
	}
}
