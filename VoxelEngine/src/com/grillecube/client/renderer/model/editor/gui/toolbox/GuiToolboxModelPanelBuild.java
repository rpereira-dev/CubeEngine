package com.grillecube.client.renderer.model.editor.gui.toolbox;

import com.grillecube.client.renderer.gui.components.GuiSpinner;
import com.grillecube.client.renderer.gui.event.GuiListener;
import com.grillecube.client.renderer.gui.event.GuiSliderBarEventValueChanged;
import com.grillecube.client.renderer.model.editor.gui.GuiSliderBarEditor;
import com.grillecube.client.renderer.model.editor.gui.GuiSpinnerEditor;

public class GuiToolboxModelPanelBuild extends GuiToolboxModelPanel {

	/** the building tools list */
	private final GuiSpinner tools;

	/** the model block size unit slider bar */
	private final GuiSliderBarEditor modelBlockSizeUnit;

	public GuiToolboxModelPanelBuild() {
		super();
		this.tools = new GuiSpinnerEditor();
		this.tools.setHint("Tools...");
		this.tools.setBox(0, 0.70f, 1.0f, 0.05f, 0);
		this.addChild(this.tools);

		// // block size unit slider bar
		this.modelBlockSizeUnit = new GuiSliderBarEditor();
		this.modelBlockSizeUnit.setBox(0, 0.65f, 1.0f, 0.05f, 0);
		this.modelBlockSizeUnit.addValues(0.03125f, 0.0625f, 0.125f, 0.250f, 0.5f, 1.0f, 2.0f, 4.0f, 8.0f, 16.0f, 32.0f, 64.0f);
		this.modelBlockSizeUnit.select((Object) 1.0f);
		this.modelBlockSizeUnit.addListener(new GuiListener<GuiSliderBarEventValueChanged<GuiSliderBarEditor>>() {
			@Override
			public void invoke(GuiSliderBarEventValueChanged<GuiSliderBarEditor> event) {
				onBlockSizeUnitChanged();
			}
		});
		this.modelBlockSizeUnit.setPrefix("Size: ");
		this.addChild(this.modelBlockSizeUnit);
	}

	@Override
	public void refresh() {

	}

	private final void onBlockSizeUnitChanged() {
		this.getModel().setBlockSizeUnit((float) this.modelBlockSizeUnit.getSelectedValue());
		this.getModel().requestMeshUpdate();
	}

	@Override
	public String getTitle() {
		return ("Build");
	}

}
