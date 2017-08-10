package com.pot.client.renderer.gui;

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.client.opengl.object.GLProgramPostProcessing;
import com.grillecube.client.renderer.camera.CameraPerspectiveWorldCentered;
import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.animations.GuiAnimationTextAutoScale;
import com.grillecube.client.renderer.gui.animations.GuiAnimationTextHoverScale;
import com.grillecube.client.renderer.gui.components.Gui;
import com.grillecube.client.renderer.gui.components.GuiLabel;
import com.grillecube.client.renderer.gui.components.GuiView;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.resources.R;
import com.grillecube.common.world.World;
import com.pot.common.world.POTWorlds;

public class GuiViewMainMenu extends GuiView {

	/** the background world */
	private World _world;

	/** the blur program effect */
	private GLProgramPostProcessing _program;

	private GuiLabel lblVersion;

	private GuiLabel _lbl_play;
	// private GuiButton _button_play;
	private GuiLabel _lbl_options;

	public GuiViewMainMenu() {
		super();
	}

	@Override
	public void onAddedTo(GuiRenderer renderer) {
		this.lblVersion = new GuiLabel();
		this.lblVersion.setFontColor(Gui.COLOR_BLUE);
		this.lblVersion.setFontSize(0.5f, 0.5f);
		this.lblVersion.setPosition(-1, 1);
		this.lblVersion.setText("Version: " + VoxelEngine.VERSION);
		this.lblVersion.addParameters(GuiLabel.PARAM_AUTO_ADJUST_RECT);
		this.addChild(this.lblVersion);

		// this._button_play = new GuiButton("button_default");
		// this._button_play.addParameter(GuiLabel.PARAM_CENTER);
		// this._button_play.addParameter(GuiButton.PARAM_TEXT_FILL_BUTTON);
		// this._button_play.addParameter(GuiButton.PARAM_TEXT_CENTER_BUTTON);
		// this._button_play.setCenter(0, 0.4f);
		// this._button_play.setSize(0.5f, 0.2f);
		// this._button_play.setText("Ima button");
		// super.addGui(this._button_play);

		this._lbl_play = new GuiLabel();
		this._lbl_play.setFontColor(Gui.COLOR_BLACK);
		this._lbl_play.setFontSize(3.0f, 3.0f);
		this._lbl_play.setText(R.getWord("play"));
		this._lbl_play.setCenterPosition(0.0f, 0.4f);
		this._lbl_play.addParameters(GuiLabel.PARAM_AUTO_ADJUST_RECT);
		this._lbl_play.addParameters(GuiLabel.PARAM_CENTER);
		this._lbl_play.startAnimation(new GuiAnimationTextHoverScale<GuiLabel>(1.1f));
		this._lbl_play.startAnimation(new GuiAnimationTextAutoScale<GuiLabel>(0.01f));
		super.addChild(this._lbl_play);

		this._lbl_options = new GuiLabel();
		this._lbl_options.setFontColor(Gui.COLOR_BLACK);
		this._lbl_options.setFontSize(3.0f, 3.0f);
		this._lbl_options.setCenterPosition(0.0f, -0.1f);
		this._lbl_options.setText(R.getWord("options"));
		this._lbl_options.addParameters(GuiLabel.PARAM_AUTO_ADJUST_RECT);
		this._lbl_options.addParameters(GuiLabel.PARAM_CENTER);
		this._lbl_options.startAnimation(new GuiAnimationTextHoverScale<GuiLabel>(1.1f));
		super.addChild(this._lbl_options);

		renderer.getParent().getGLFWWindow().setCursor(true);
		this.setBackgroundWorld();
	}

	private void setBackgroundWorld() {
		VoxelEngineClient engine = VoxelEngineClient.instance();
		CameraPerspectiveWorldCentered cam = new CameraPerspectiveWorldCentered(engine.getGLFWWindow()) {
			@Override
			public void update() {
				super.update();
				super.increaseAngleAroundCenter(0.1f);

			}
		};
		cam.setPitch(0.0f);
		cam.setDistanceFromCenter(90.0f);

		engine.getRenderer().setCamera(cam);
		engine.setWorld(POTWorlds.MAIN_MENU);
		this._program = new GLProgramPostProcessing(R.getResPath("shaders/post_process/blurhv.fs"));
		engine.getRenderer().setPostProcessingProgram(this._program);
	}

	@Override
	public void onRemovedFrom(GuiRenderer renderer) {
		this._world.delete();
		this._program.delete();
	}

	@Override
	protected void onInitialized(GuiRenderer renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDeinitialized(GuiRenderer renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onUpdate(float x, float y, boolean pressed) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAddedTo(Gui gui) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRemovedFrom(Gui gui) {
		// TODO Auto-generated method stub

	}

}