package com.grillecube.editor.toolbox;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.grillecube.client.renderer.model.Model;
import com.grillecube.client.renderer.model.ModelPart;
import com.grillecube.client.renderer.model.animation.ModelAnimation;
import com.grillecube.client.renderer.model.animation.ModelAnimationFrame;
import com.grillecube.client.renderer.model.animation.ModelPartAnimation;

public class ToolboxPanelAnimationPart extends ToolboxPanel {
	private static final long serialVersionUID = 1L;

	private JLabel _label_frames_panels;
	private JButton _add_frame_button;
	private JComboBox<ModelAnimationFrame> _frames;
	private JButton _remove_frame_button;

	private ToolboxPanelModelAnimationFrame _panel_frame;

	public ToolboxPanelAnimationPart(Toolbox toolbox) {
		this(toolbox, null, null, null);
	}

	public ToolboxPanelAnimationPart(Toolbox toolbox, Model model, ModelPart part, ModelAnimation animation) {
		super(toolbox);

		this._panel_frame = new ToolboxPanelModelAnimationFrame(this);
		this.add(this._panel_frame);

		this._label_frames_panels = new JLabel();
		this._label_frames_panels.setFont(new Font(this._label_frames_panels.getName(), Font.PLAIN, 14));
		this.add(this._label_frames_panels);

		this._frames = new JComboBox<ModelAnimationFrame>(new SortedComboBoxModel<ModelAnimationFrame>());
		this._frames.setToolTipText("Animation Frames");
		((JLabel) this._frames.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		this._frames.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshVisibility();
			}
		});
		this.add(this._frames);

		if (model != null && part != null && animation != null) {
			int partID = model.getPartID(part);
			ModelPartAnimation animpart = animation.getAnimationPart(partID);
			if (animpart != null) {
				for (ModelAnimationFrame frame : animpart.getFrames()) {
					this._frames.addItem(frame);
				}
			}
		}

		this._add_frame_button = new JButton("Add");
		this._add_frame_button.setToolTipText("Create a new animation");
		this._add_frame_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addModelAnimationFrame();
			}
		});
		this.add(this._add_frame_button);

		this._remove_frame_button = new JButton("Remove");
		this._remove_frame_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeModelAnimationFrame();
			}
		});
		this._remove_frame_button.setToolTipText("Remove the frame from the animation");
		this.add(this._remove_frame_button);

		this._label_frames_panels.setText("<html><B>Frames</B></html>");
		this._label_frames_panels.setHorizontalAlignment(SwingConstants.CENTER);
		this._label_frames_panels.updateUI();

		ToolboxPanelModelAnimationFrame animpanel = this.getModelAnimationFrameValuePanel();
		if (animpanel != null) {
			animpanel.setVisible(true);
		}
	}

	private ToolboxPanelModelAnimationFrame getModelAnimationFrameValuePanel() {
		return (this._panel_frame);
	}

	private void addModelAnimationFrame() {
		ModelAnimationFrame prev = this.getModelAnimationFrame();
		ModelAnimationFrame frame = new ModelAnimationFrame();

		// if there was a previous frame, copy it
		if (prev != null) {
			frame.set(prev);
		}

		// add the frame to containers
		this.getAnimationPart().addFrame(frame);
		this._frames.addItem(frame);
		this._frames.setSelectedItem(frame);

		// update visibility
		this.refreshVisibility();
	}

	public ModelPartAnimation getAnimationPart() {
		return (this.getToolbox().getAnimationPart());
	}

	public ModelAnimation getAnimation() {
		return (this.getToolbox().getAnimation());
	}

	private void removeModelAnimationFrame() {
		ModelAnimationFrame theframe = this.getModelAnimationFrame();
		this._frames.removeItem(theframe);
		this.getAnimationPart().removeFrame(theframe);
	}

	public ModelAnimationFrame getModelAnimationFrame() {
		ModelAnimationFrame theframe = (ModelAnimationFrame) this._frames.getSelectedItem();
		return (theframe);
	}

	@Override
	protected void updateVisibility() {

		ToolboxPanelModelAnimationFrame panel = this.getModelAnimationFrameValuePanel();
		panel.setFrame(this.getModelAnimationFrame());
		panel.updateVisibility();

		this._remove_frame_button.setEnabled(this._frames.getItemCount() > 0);
	}

	@Override
	public void resize() {
		int w = this.getWidth() / 3;
		int h = this.getHeight() / 27;

		this._label_frames_panels.setLocation(0, 0);
		this._add_frame_button.setLocation(0, h);
		this._frames.setLocation(w, h);
		this._remove_frame_button.setLocation(w * 2, h);

		this._label_frames_panels.setSize(this.getWidth(), h);
		this._add_frame_button.setSize(w, h);
		this._frames.setSize(w, h);
		this._remove_frame_button.setSize(w, h);

		int posy = h * 2;
		this._panel_frame.setSize(this.getWidth(), this.getHeight() - posy);
		this._panel_frame.setLocation(0, posy);
		this._panel_frame.resize();
	}

	public void repaintFramesSlider() {
		this._frames.repaint();
	}

	public JComboBox<ModelAnimationFrame> getComboBox() {
		return (this._frames);
	}
}
