package com.grillecube.editor.toolbox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import com.grillecube.engine.maths.Vector3f;
import com.grillecube.engine.renderer.model.animation.ModelAnimationFrame;
import com.grillecube.engine.renderer.model.animation.ModelAnimationInstance;

public class ToolboxPanelModelAnimationFrame extends ToolboxPanel {
	private static final long serialVersionUID = 1L;

	private ToolboxPanelAnimationPart _parent;

	private JCustomSlider _slider_time;

	private JCustomSlider _slider_rot_x;
	private JCustomSlider _slider_rot_y;
	private JCustomSlider _slider_rot_z;

	private JCustomSlider _slider_offset_x;
	private JCustomSlider _slider_offset_y;
	private JCustomSlider _slider_offset_z;

	private JCustomSlider _slider_scale_x;
	private JCustomSlider _slider_scale_y;
	private JCustomSlider _slider_scale_z;

	private JCustomSlider _slider_translate_x;
	private JCustomSlider _slider_translate_y;
	private JCustomSlider _slider_translate_z;

	private JButton _random_button;

	private ModelAnimationFrame _frame;

	public ToolboxPanelModelAnimationFrame(ToolboxPanelAnimationPart parent) {
		super(parent.getToolbox());
		this._parent = parent;

		this._random_button = new JButton("Randomize");
		this._random_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_slider_rot_x.randomize();
				_slider_rot_y.randomize();
				_slider_rot_z.randomize();

				_slider_offset_x.randomize();
				_slider_offset_y.randomize();
				_slider_offset_z.randomize();

				_slider_scale_x.randomize();
				_slider_scale_y.randomize();
				_slider_scale_z.randomize();

				_slider_translate_x.randomize();
				_slider_translate_y.randomize();
				_slider_translate_z.randomize();

				_slider_time.randomize();
			}
		});
		this.add(this._random_button);

		this._slider_time = new JCustomSlider(0, 10.0f) {

			@Override
			public void onValueChanged(float value) {
				_frame.setTime((int) (value * 1000));
				_parent.getAnimation().updateDuration();
				freezeAnimation();
				_parent.repaintFramesSlider();
			}
		};
		this._slider_time.setLabel("Time:");
		this._slider_time.addTo(this);

		/** rotation x */
		this._slider_rot_x = new JCustomSlider(-180, 180) {
			@Override
			public void onValueChanged(float value) {
				Vector3f rot = _frame.getRotation();
				rot.setX(value);
				freezeAnimation();
			}
		};
		this._slider_rot_x.setLabel("Rotation X:");
		this._slider_rot_x.addTo(this);

		/** rotation y */
		this._slider_rot_y = new JCustomSlider(-180, 180) {
			@Override
			public void onValueChanged(float value) {
				Vector3f rot = _frame.getRotation();
				rot.setY(value);
				freezeAnimation();
			}
		};
		this._slider_rot_y.setLabel("Rotation Y:");
		this._slider_rot_y.addTo(this);

		/** rotation y */
		this._slider_rot_z = new JCustomSlider(-180, 180) {
			@Override
			public void onValueChanged(float value) {
				Vector3f rot = _frame.getRotation();
				rot.setZ(value);
				freezeAnimation();
			}
		};
		this._slider_rot_z.setLabel("Rotation Z:");
		this._slider_rot_z.addTo(this);

		/** offset x */
		this._slider_offset_x = new JCustomSlider(-32.0f, 32.0f) {
			@Override
			public void onValueChanged(float value) {
				Vector3f offset = _frame.getOffset();
				offset.setX(value);
				freezeAnimation();
			}
		};
		this._slider_offset_x.setLabel("Offset X:");
		this._slider_offset_x.updateValue(0.5f);
		this._slider_offset_x.addTo(this);

		/** offset y */
		this._slider_offset_y = new JCustomSlider(-32.0f, 32.0f) {
			@Override
			public void onValueChanged(float value) {
				Vector3f offset = _frame.getOffset();
				offset.setY(value);
				freezeAnimation();
			}
		};
		this._slider_offset_y.setLabel("Offset Y:");
		this._slider_offset_y.updateValue(0.5f);
		this._slider_offset_y.addTo(this);

		/** offset z */
		this._slider_offset_z = new JCustomSlider(-32.0f, 32.0f) {
			@Override
			public void onValueChanged(float value) {
				Vector3f offset = _frame.getOffset();
				offset.setZ(value);
				freezeAnimation();
			}
		};
		this._slider_offset_z.setLabel("Offset Z:");
		this._slider_offset_z.updateValue(0.5f);
		this._slider_offset_z.addTo(this);

		/** offset x */
		this._slider_translate_x = new JCustomSlider(-32.0f, 32.0f) {
			@Override
			public void onValueChanged(float value) {
				Vector3f translate = _frame.getTranslation();
				translate.setX(value);
				freezeAnimation();
			}
		};
		this._slider_translate_x.setLabel("Translate X:");
		this._slider_translate_x.addTo(this);

		/** offset y */
		this._slider_translate_y = new JCustomSlider(-32.0f, 32.0f) {
			@Override
			public void onValueChanged(float value) {
				Vector3f translate = _frame.getTranslation();
				translate.setY(value);
				freezeAnimation();
			}
		};
		this._slider_translate_y.setLabel("Translate Y:");
		this._slider_translate_y.addTo(this);

		/** offset z */
		this._slider_translate_z = new JCustomSlider(-32.0f, 32.0f) {
			@Override
			public void onValueChanged(float value) {
				Vector3f translate = _frame.getTranslation();
				translate.setZ(value);
				freezeAnimation();
			}
		};
		this._slider_translate_z.setLabel("Translate Z:");
		this._slider_translate_z.addTo(this);

		/** offset x */
		this._slider_scale_x = new JCustomSlider(0.0f, 16.0f) {
			@Override
			public void onValueChanged(float value) {
				Vector3f scale = _frame.getScaling();
				scale.setX(value);
				freezeAnimation();
			}
		};
		this._slider_scale_x.setLabel("Scale X:");
		this._slider_scale_x.addTo(this);

		/** offset y */
		this._slider_scale_y = new JCustomSlider(0.0f, 16.0f) {
			@Override
			public void onValueChanged(float value) {
				Vector3f scale = _frame.getScaling();
				scale.setY(value);
				freezeAnimation();
			}
		};
		this._slider_scale_y.setLabel("Scale Y:");
		this._slider_scale_y.addTo(this);

		/** offset z */
		this._slider_scale_z = new JCustomSlider(0.0f, 16.0f) {
			@Override
			public void onValueChanged(float value) {
				Vector3f scale = _frame.getScaling();
				scale.setZ(value);
				freezeAnimation();
			}
		};
		this._slider_scale_z.setLabel("Scale Z:");
		this._slider_scale_z.addTo(this);

		this.setFrame(null);
	}

	public ModelAnimationFrame getModelAnimationFrame() {
		return (this._frame);
	}

	public void setFrame(ModelAnimationFrame frame) {
		if (frame == null) {
			frame = new ModelAnimationFrame();
		}
		this._frame = frame;
	}

	@Override
	protected void updateVisibility() {
		ModelAnimationFrame frame = this._frame;

		Vector3f translate = frame.getTranslation();
		Vector3f rot = frame.getRotation();
		Vector3f scale = frame.getScaling();
		Vector3f offset = frame.getOffset();

		this._slider_time.setValue(frame.getTime() / 1000.0f);

		this._slider_translate_x.updateValue(translate.x);
		this._slider_translate_y.updateValue(translate.y);
		this._slider_translate_z.updateValue(translate.z);

		this._slider_scale_x.updateValue(scale.x);
		this._slider_scale_y.updateValue(scale.y);
		this._slider_scale_z.updateValue(scale.z);

		this._slider_rot_x.updateValue(rot.x);
		this._slider_rot_y.updateValue(rot.y);
		this._slider_rot_z.updateValue(rot.z);

		this._slider_offset_x.updateValue(offset.x);
		this._slider_offset_y.updateValue(offset.y);
		this._slider_offset_z.updateValue(offset.z);
	}

	@Override
	public void resize() {
		int w = this.getWidth() / 8;
		int h = this.getHeight() / 32;

		this._slider_time.setLocationAndSize(0, h, w * 2, w * 5, w - 4, h);

		this._slider_rot_x.setLocationAndSize(0, h * 3, w * 2, w * 5, w - 4, h);
		this._slider_rot_y.setLocationAndSize(0, h * 4, w * 2, w * 5, w - 4, h);
		this._slider_rot_z.setLocationAndSize(0, h * 5, w * 2, w * 5, w - 4, h);

		this._slider_offset_x.setLocationAndSize(0, h * 7, w * 2, w * 5, w - 4, h);
		this._slider_offset_y.setLocationAndSize(0, h * 8, w * 2, w * 5, w - 4, h);
		this._slider_offset_z.setLocationAndSize(0, h * 9, w * 2, w * 5, w - 4, h);

		this._slider_translate_x.setLocationAndSize(0, h * 11, w * 2, w * 5, w - 4, h);
		this._slider_translate_y.setLocationAndSize(0, h * 12, w * 2, w * 5, w - 4, h);
		this._slider_translate_z.setLocationAndSize(0, h * 13, w * 2, w * 5, w - 4, h);

		this._slider_scale_x.setLocationAndSize(0, h * 15, w * 2, w * 5, w - 4, h);
		this._slider_scale_y.setLocationAndSize(0, h * 16, w * 2, w * 5, w - 4, h);
		this._slider_scale_z.setLocationAndSize(0, h * 17, w * 2, w * 5, w - 4, h);

		this._random_button.setLocation(this.getWidth() / 2 - w, h * 19);
		this._random_button.setSize(w * 2, h);
	}

	@Override
	public String toString() {
		return (this._frame.toString());
	}

	public void freezeAnimation() {
		ModelAnimationInstance instance = this.getAnimationInstance();
		if (instance == null) {
			return;
		}
		ModelAnimationFrame frame = this.getModelAnimationFrame();
		instance.startFreeze(frame == null ? 0 : frame.getTime());
		this.getToolbox().getModelPanel().updateAnimationButtonVisibility();
	}
}
