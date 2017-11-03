package com.grillecube.editor.toolbox;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public abstract class JCustomSlider {
	private JLabel _label;
	private JSlider _slider;
	private JTextField _textfield;

	private SliderChangeListener _change_listener;

	private float _min_value;
	private float _max_value;
	private float _value;

	public JCustomSlider(float min_value, float max_value) {
		this._min_value = min_value;
		this._max_value = max_value;

		this._label = new JLabel();
		this._slider = new JSlider();
		this._textfield = new JTextField();

		this._change_listener = new SliderChangeListener(this);

		this._label.setHorizontalAlignment(SwingConstants.CENTER);
		this._textfield.setHorizontalAlignment(SwingConstants.CENTER);
		this._textfield.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent event) {
			}

			@Override
			public void keyPressed(KeyEvent event) {
			}

			@Override
			public void keyReleased(KeyEvent event) {
				JCustomSlider.this._slider.removeChangeListener(JCustomSlider.this._change_listener);
				try {
					JCustomSlider.this.setValue(Float.valueOf(JCustomSlider.this._textfield.getText()));
					onValueChanged(_value);
				} catch (Exception e) {
					JCustomSlider.this.setValue(0);
				}
				JCustomSlider.this._slider.addChangeListener(JCustomSlider.this._change_listener);
			}
		});

		this._slider.addChangeListener(this._change_listener);
	}

	public void setLabel(String str) {
		this._label.setText(str);
	}

	public void setLocationAndSize(int x, int y, int sizeX_label, int sizeX_slider, int sizeX_value, int sizeY) {
		this._label.setLocation(x, y);
		this._label.setSize(sizeX_label, sizeY);

		x += sizeX_label;
		this._slider.setLocation(x, y);
		this._slider.setSize(sizeX_slider, sizeY);

		x += sizeX_slider;
		this._textfield.setLocation(x, y);
		this._textfield.setSize(sizeX_value, sizeY);
	}

	public void addTo(JFrame frame) {
		frame.add(this._label);
		frame.add(this._slider);
		frame.add(this._textfield);
	}

	public void addTo(JPanel panel) {
		panel.add(this._label);
		panel.add(this._slider);
		panel.add(this._textfield);
	}

	public JSlider getSlider() {
		return (this._slider);
	}

	public void setValue(float value) {
		if (value > this._max_value) {
			value = this._max_value;
		} else if (value < this._min_value) {
			value = this._min_value;
		}
		int percent = (int) ((value - this._min_value) / (this._max_value - this._min_value) * 100.0f);
		this._slider.setValue(percent);
		this._value = value;
	}

	public abstract void onValueChanged(float value);

	/** should be called when the slider value changed */
	public void updateValueFromSlider(int percent) {
		if (percent < 0) {
			percent = 0;
		} else if (percent > 100) {
			percent = 100;
		}
		this._value = (percent * (this._max_value - this._min_value) / 100.0f + this._min_value);
		String str = String.valueOf(this._value);
		if (str.length() > 5) {
			str = str.substring(0, 5);
		}
		this._textfield.setText(str);
		this.onValueChanged(this._value);
	}

	public void updateValue(float value) {
		this._slider.removeChangeListener(this._change_listener);
		this.setValue(value);
		this._slider.addChangeListener(this._change_listener);
		String str = String.valueOf(this._value);
		if (str.length() > 5) {
			str = str.substring(0, 5);
		}
		this._textfield.setText(str);
	}

	public float getValue() {
		return (this._value);
	}

	public String getText() {
		String str;

		str = String.valueOf(this._value);
		if (str.length() > 4) {
			str.substring(0, 4);
		}
		return (str);
	}

	public float getMaxValue() {
		return (this._max_value);
	}

	public float getMinValue() {
		return (this._min_value);
	}

	public JTextField getTextField() {
		return (this._textfield);
	}

	public float getAverageValue() {
		return ((this._max_value + this._min_value) / 2);
	}

	public void addChangeValueListener(ChangeListener listener) {
		this._slider.addChangeListener(listener);
	}

	public void setVisible(boolean value) {
		this._label.setVisible(value);
		this._slider.setVisible(value);
		this._textfield.setVisible(value);
	}

	public void randomize() {
		long t = (System.nanoTime() * 999) & 0xFFFFFF;
		float f = t / (float) 0xFFFFFF;
		float value = (this.getMaxValue() - this._min_value) * f + this._min_value;
		this.setValue(value);
	}
}

class SliderChangeListener implements ChangeListener {
	private JCustomSlider _slider;

	public SliderChangeListener(JCustomSlider slider) {
		this._slider = slider;
	}

	@Override
	public void stateChanged(ChangeEvent event) {
		JSlider source = (JSlider) event.getSource();
		this._slider.updateValueFromSlider(source.getValue());
	}

}
