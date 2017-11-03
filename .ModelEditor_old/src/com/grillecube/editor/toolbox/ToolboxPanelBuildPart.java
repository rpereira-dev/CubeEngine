package com.grillecube.editor.toolbox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.grillecube.client.renderer.model.ModelPart;
import com.grillecube.client.renderer.model.builder.ModelPartBuilder;
import com.grillecube.editor.window.camera.CameraTool;
import com.grillecube.editor.window.camera.CameraTools;

public class ToolboxPanelBuildPart extends ToolboxPanel {
	private static final long serialVersionUID = 1L;

	private JLabel _tools_text;
	private JComboBox<CameraTool> _tools_list;

	private JLabel _model_part_label;

	private JCustomSlider _slider_scale;

	private JCustomSlider _slider_model_center_x;
	private JCustomSlider _slider_model_center_y;
	private JCustomSlider _slider_model_center_z;

	private JCustomSlider _slider_model_axis_x;
	private JCustomSlider _slider_model_axis_y;
	private JCustomSlider _slider_model_axis_z;

	public ToolboxPanelBuildPart(Toolbox toolbox) {
		super(toolbox);
		/** tools */
		this._tools_text = new JLabel("<html><B>Tools:</B></html>", SwingConstants.CENTER);
		this.add(this._tools_text);

		this._tools_list = new JComboBox<CameraTool>();
		for (CameraTool tool : CameraTools.TOOLS) {
			this._tools_list.addItem(tool);
		}
		this._tools_list.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getToolbox().getModelEditor().getCamera().getTools()
						.useTool((CameraTool) _tools_list.getSelectedItem());
			}
		});
		((JLabel) this._tools_list.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		this.add(this._tools_list);

		/** label */
		this._model_part_label = new JLabel("<html><B>ModelPart mesh transformation</B></html>", SwingConstants.CENTER);
		this.add(this._model_part_label);

		/** scale */
		this._slider_scale = new JCustomSlider(-5, 5) {
			@Override
			public void onValueChanged(float value) {
				value = ModelPart.unitToBlockScale(value);
				getModelPart().setBlockScale(value, value, value);
				getToolbox().getModelEditor().getWorld().getGrid().getScale().set(value);
				((ModelPartBuilder) getModelPart()).updateBox();
				getModel().updateBox();
			}
		};
		this._slider_scale.setLabel("Block size unit");
		this._slider_scale.addTo(this);
		this._slider_scale.updateValue(ModelPart.blockScaleToUnit(ModelPart.DEFAULT_SCALE));

		/** rotation x */
		this._slider_model_axis_x = new JCustomSlider(-180, 180) {
			@Override
			public void onValueChanged(float value) {
				getModel().getAxis().setX(value);
				getToolbox().getModelEditor().getWorld().getGrid().getRotation().setX(value);
			}
		};
		this._slider_model_axis_x.setLabel("Axis X");
		this._slider_model_axis_x.addTo(this);
		this._slider_model_axis_x.updateValue(0);

		/** rotation y */
		this._slider_model_axis_y = new JCustomSlider(-180, 180) {
			@Override
			public void onValueChanged(float value) {
				getModel().getAxis().setY(value);
				getToolbox().getModelEditor().getWorld().getGrid().getRotation().setY(value);
			}
		};
		this._slider_model_axis_y.setLabel("Axis Y");
		this._slider_model_axis_y.addTo(this);
		this._slider_model_axis_y.updateValue(0);

		/** rotation z */
		this._slider_model_axis_z = new JCustomSlider(-180, 180) {
			@Override
			public void onValueChanged(float value) {
				getModel().getAxis().setZ(value);
				getToolbox().getModelEditor().getWorld().getGrid().getRotation().setZ(value);
			}
		};
		this._slider_model_axis_z.setLabel("Axis Z");
		this._slider_model_axis_z.addTo(this);
		this._slider_model_axis_z.updateValue(0);

		/** rotation x */
		this._slider_model_center_x = new JCustomSlider(ModelPartBuilder.MIN_X, ModelPartBuilder.MAX_X) {
			@Override
			public void onValueChanged(float value) {
				getModel().getOrigin().setX(value);
				getToolbox().getModelEditor().getWorld().getGrid().getPosition().setX(value);
			}
		};
		this._slider_model_center_x.setLabel("Center X");
		this._slider_model_center_x.addTo(this);
		this._slider_model_center_x.updateValue(0);

		/** rotation y */
		this._slider_model_center_y = new JCustomSlider(ModelPartBuilder.MIN_Y, ModelPartBuilder.MAX_Y) {
			@Override
			public void onValueChanged(float value) {
				getModel().getOrigin().setY(value);
				getToolbox().getModelEditor().getWorld().getGrid().getPosition().setY(value);
			}
		};
		this._slider_model_center_y.setLabel("Center Y");
		this._slider_model_center_y.addTo(this);
		this._slider_model_center_y.updateValue(0);

		/** rotation z */
		this._slider_model_center_z = new JCustomSlider(ModelPartBuilder.MIN_Z, ModelPartBuilder.MAX_Z) {
			@Override
			public void onValueChanged(float value) {
				getModel().getOrigin().setZ(value);
				getToolbox().getModelEditor().getWorld().getGrid().getPosition().setZ(value);
			}
		};
		this._slider_model_center_z.setLabel("Center Z");
		this._slider_model_center_z.addTo(this);
		this._slider_model_center_z.updateValue(0);
	}

	@Override
	protected void updateVisibility() {

	}

	@Override
	public void resize() {
		int w = this.getWidth() / 8;
		int h = this.getHeight() / 32;

		this._tools_text.setSize(w, h);
		this._tools_text.setLocation(0, h);

		this._tools_list.setSize(this.getWidth() - w, h);
		this._tools_list.setLocation(w, h);

		this._model_part_label.setLocation(0, h * 3);
		this._model_part_label.setSize(this.getWidth(), h);

		this._slider_scale.setLocationAndSize(0, h * 4, w * 2, w * 5, w - 4, h);

		this._slider_model_center_x.setLocationAndSize(0, h * 5, w * 2, w * 5, w - 4, h);
		this._slider_model_center_y.setLocationAndSize(0, h * 6, w * 2, w * 5, w - 4, h);
		this._slider_model_center_z.setLocationAndSize(0, h * 7, w * 2, w * 5, w - 4, h);

		this._slider_model_axis_x.setLocationAndSize(0, h * 8, w * 2, w * 5, w - 4, h);
		this._slider_model_axis_y.setLocationAndSize(0, h * 9, w * 2, w * 5, w - 4, h);
		this._slider_model_axis_z.setLocationAndSize(0, h * 10, w * 2, w * 5, w - 4, h);
	}

	public void setTool(CameraTool tool) {
		int index;
		for (index = 0; index < this._tools_list.getItemCount(); index++) {
			if (this._tools_list.getItemAt(index).equals(tool)) {
				break;
			}
		}
		this._tools_list.setSelectedIndex(index);
	}
}
