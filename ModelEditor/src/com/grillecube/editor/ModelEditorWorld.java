/**
**	This file is part of the project https://github.com/toss-dev/VoxelEngine
**
**	License is available here: https://raw.githubusercontent.com/toss-dev/VoxelEngine/master/LICENSE.md
**
**	PEREIRA Romain
**                                       4-----7          
**                                      /|    /|
**                                     0-----3 |
**                                     | 5___|_6
**                                     |/    | /
**                                     1-----2
*/

package com.grillecube.editor;

import java.util.ArrayList;

import com.grillecube.editor.model.ModelGrid;
import com.grillecube.editor.model.ModelPoint;
import com.grillecube.editor.toolbox.Toolbox;
import com.grillecube.editor.toolbox.ToolboxPanelModel;
import com.grillecube.engine.Taskable;
import com.grillecube.engine.VoxelEngine;
import com.grillecube.engine.VoxelEngineClient;
import com.grillecube.engine.maths.Vector3f;
import com.grillecube.engine.maths.Vector4f;
import com.grillecube.engine.mod.ModInfo;
import com.grillecube.engine.renderer.model.builder.ModelPartBuilder;
import com.grillecube.engine.renderer.world.MVPRenderer;
import com.grillecube.engine.world.Terrain;
import com.grillecube.engine.world.World;
import com.grillecube.engine.world.block.Blocks;

@ModInfo(name = "Model Editor", author = "toss-dev", version = "1.0.0")
public class ModelEditorWorld extends World {

	/** window view */
	private ModelGrid _grid;
	private ModelPoint _rotation_point;
	private ModelPoint _attachment_point;

	@Override
	public void onSet() {

		// create the default entity
		// String file = ModelEditor.Config.TMP_MODEL_BUILDER_FILE;
		// Model model = ModelBuilderImporter.importModelBuilder(file);
		// if (model == null) {
		// model = new ModelBuilder();
		// }
		// this._entity = new EntityModeled(this, model) {
		// @Override
		// public boolean move(float x, float y, float z) {
		// return (false);
		// }
		//
		// @Override
		// protected void onUpdate() {
		// }
		// };
		// this.spawnEntity(this._entity, 0, 0, 0);

		// create the grid and the rotation point
		this._grid = new ModelGrid(ModelPartBuilder.DIM_X, ModelPartBuilder.DIM_Z);
		this._rotation_point = new ModelPoint();
		this._attachment_point = new ModelPoint();
		this._rotation_point.setColor(new Vector4f(1.0f, 0.1f, 0.1f, 1.0f));
		this._attachment_point.setColor(new Vector4f(0.1f, 0.1f, 1.0f, 1.0f));
		MVPRenderer renderer = VoxelEngineClient.instance().getRenderer().getWorldRenderer().getMVPRenderer();
		renderer.addMVPObject(this._grid);
		renderer.addMVPObject(this._attachment_point);
		renderer.addMVPObject(this._rotation_point);

		this.setWeather(new WeatherEditor());

		// generate terrains
		for (int i = -3; i < 3; i++) {
			for (int j = -1; j < 1; j++) {
				for (int k = -3; k < 3; k++) {
					Terrain terrain = new Terrain(i, j, k) {

						@Override
						public void onGenerated(World world) {

							if (this.getLocation().getWorldIndex().y == -1) {
								for (int x = 0; x < Terrain.DIM; x++) {
									for (int z = 0; z < Terrain.DIM; z++) {
										this.setBlock(Blocks.GRASS, x, 15, z);
									}
								}
							} else if (VoxelEngine.instance().getRNG().nextInt(5) == 0) {
								this.setBlock(Blocks.STONE, 5, 0, 6);

								this.setBlock(Blocks.STONE, 8, 0, 5);

								this.setBlock(Blocks.LOG, 8, 0, 6);
								this.setBlock(Blocks.LOG, 8, 1, 6);
								this.setBlock(Blocks.LOG, 8, 2, 6);
								this.setBlock(Blocks.LOG, 8, 3, 6);
								this.setBlock(Blocks.LOG, 8, 4, 6);

								this.setBlock(Blocks.LEAVES, 8, 5, 6);

								this.setBlock(Blocks.LEAVES, 7, 4, 6);
								this.setBlock(Blocks.LEAVES, 9, 4, 6);
								this.setBlock(Blocks.LEAVES, 8, 4, 5);
								this.setBlock(Blocks.LEAVES, 8, 4, 7);
							}

							//
							// this.spawnTerrain(new Terrain(new Vector3i(-1, 0,
							// 0)) {
							// @Override
							// public void generate() {
							// this.setBlock(Blocks.STONE, 5, 0, 4);
							//
							// this.setBlock(Blocks.STONE, 8, 0, 1);
							//
							// this.setBlock(Blocks.LOG, 4, 0, 2);
							// this.setBlock(Blocks.LOG, 4, 1, 2);
							// this.setBlock(Blocks.LOG, 4, 2, 2);
							// this.setBlock(Blocks.LOG, 4, 3, 2);
							// this.setBlock(Blocks.LOG, 4, 4, 2);
							//
							// this.setBlock(Blocks.LEAVES, 4, 5, 2);
							//
							// this.setBlock(Blocks.LEAVES, 3, 4, 2);
							// this.setBlock(Blocks.LEAVES, 5, 4, 2);
							// this.setBlock(Blocks.LEAVES, 4, 4, 1);
							// this.setBlock(Blocks.LEAVES, 4, 4, 3);
							//
							// this.requestMeshUpdate();
							// }
							// });
							//
							// this.spawnTerrain(new Terrain(new Vector3i(0, 0,
							// -1)) {
							// @Override
							// public void generate() {
							// this.setBlock(Blocks.STONE, 5, 0, 4);
							//
							// this.setBlock(Blocks.STONE, 8, 0, 1);
							//
							// this.setBlock(Blocks.LOG, 4, 0, 2);
							// this.setBlock(Blocks.LOG, 4, 1, 2);
							// this.setBlock(Blocks.LOG, 4, 2, 2);
							// this.setBlock(Blocks.LOG, 4, 3, 2);
							// this.setBlock(Blocks.LOG, 4, 4, 2);
							//
							// this.setBlock(Blocks.LEAVES, 4, 5, 2);
							//
							// this.setBlock(Blocks.LEAVES, 3, 4, 2);
							// this.setBlock(Blocks.LEAVES, 5, 4, 2);
							// this.setBlock(Blocks.LEAVES, 4, 4, 1);
							// this.setBlock(Blocks.LEAVES, 4, 4, 3);
							// this.requestMeshUpdate();
							// }
							// });
						}
					};
					this.spawnTerrain(terrain);
				}
			}
		}
	}

	@Override
	public void onUnset() {

	}

	@Override
	protected void onTasksGet(VoxelEngine engine, ArrayList<VoxelEngine.Callable<Taskable>> tasks) {
		tasks.add(engine.new Callable<Taskable>() {

			@Override
			public ModelEditorWorld call() throws Exception {
				onUpdate();
				return (ModelEditorWorld.this);
			}

			@Override
			public String getName() {
				return ("ModelEditorWorld update");
			}

		});
	}

	protected void onUpdate() {
		this.updateGrid();
		this.updatePoints();
	}

	private void updateGrid() {
		ToolboxPanelModel panel = this.getToolbox().getModelPanel();
		if (panel != null && panel.getModelPart() != null) {
			this._grid.setScale(panel.getModelPart().getBlockScale());
		} else {
			this._grid.setScale(Vector3f.ONE_VEC);
		}
	}

	private void updatePoints() {
		ToolboxPanelModel panel = this.getToolbox().getModelPanel();

		if (panel == null || panel.getModelPart() == null) {
			this._attachment_point.set(0, 10000000, 0);
			this._rotation_point.set(0, 10000000, 0);
		} else {
			Vector3f scale = panel.getModelPart().getBlockScale();
			this._attachment_point.setScale(scale);
			this._rotation_point.setScale(scale);

			if (panel.getAttachmentPoint() != null) {
				this._attachment_point.set(panel.getAttachmentPoint().getPoint());
			}

			if (panel.getModelAnimationFrame() != null) {
				this._rotation_point.set(panel.getModelAnimationFrame().getOffset());
				this._rotation_point.getPosition().scale(panel.getModelPart().getBlockScale());
			}
		}
	}

	public Toolbox getToolbox() {
		return (ModelEditor.instance().getToolbox());
	}

	public ModelGrid getGrid() {
		return (this._grid);
	}

	@Override
	public String getName() {
		return ("Model Editor World");
	}

	public ModelPoint getRotationPoint() {
		return (this._rotation_point);
	}

	public ModelPoint getAttachmentPoint() {
		return (this._attachment_point);
	}
}