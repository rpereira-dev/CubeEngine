package com.grillecube.editor.toolbox;

import com.grillecube.engine.renderer.model.ModelPartAttachmentPoint;

public class ToolboxPanelEquipmentPart extends ToolboxPanel {

	private JCustomSlider _slider_translate_x;
	private JCustomSlider _slider_translate_y;
	private JCustomSlider _slider_translate_z;

	private JCustomSlider _slider_rot_x;
	private JCustomSlider _slider_rot_y;
	private JCustomSlider _slider_rot_z;

	private JCustomSlider _slider_scale_x;
	private JCustomSlider _slider_scale_y;
	private JCustomSlider _slider_scale_z;

	public ToolboxPanelEquipmentPart(Toolbox toolbox) {
		super(toolbox);
	}

	@Override
	protected void updateVisibility() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resize() {
		// TODO Auto-generated method stub

	}
}

class EquipmentPartComponents {

	ModelPartAttachmentPoint point;

	JCustomSlider _slider_translate_x;
	JCustomSlider _slider_translate_y;
	JCustomSlider _slider_translate_z;

	JCustomSlider _slider_rot_x;
	JCustomSlider _slider_rot_y;
	JCustomSlider _slider_rot_z;

	JCustomSlider _slider_scale_x;
	JCustomSlider _slider_scale_y;
	JCustomSlider _slider_scale_z;

	public EquipmentPartComponents() {

	}
}