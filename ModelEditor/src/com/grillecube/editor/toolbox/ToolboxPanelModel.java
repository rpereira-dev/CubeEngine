package com.grillecube.editor.toolbox;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import com.grillecube.editor.ModelEditor;
import com.grillecube.editor.toolbox.JComboBoxColor.IconColor;
import com.grillecube.editor.window.camera.CameraToolNewAttachmentPointModel;
import com.grillecube.engine.Logger;
import com.grillecube.engine.renderer.MainRenderer.GLTask;
import com.grillecube.engine.renderer.model.Model;
import com.grillecube.engine.renderer.model.ModelAttachmentPoint;
import com.grillecube.engine.renderer.model.ModelPart;
import com.grillecube.engine.renderer.model.ModelSkin;
import com.grillecube.engine.renderer.model.animation.ModelAnimation;
import com.grillecube.engine.renderer.model.animation.ModelAnimationFrame;
import com.grillecube.engine.renderer.model.animation.ModelAnimationInstance;
import com.grillecube.engine.renderer.model.builder.ModelPartBuilder;
import com.grillecube.engine.world.entity.EntityModeled;

public class ToolboxPanelModel extends ToolboxPanel {
	private static final long serialVersionUID = 1L;

	private JLabel _label_model_type;
	private JTextField _textfield_model_name;

	private JLabel _label_part;
	private JButton _add_part_button;
	private JComboBox<ModelPart> _model_parts;
	private JButton _remove_part_button;

	private JLabel _label_skin;
	private JComboBox<ModelSkin> _model_skins;
	private JButton _add_skin_button;
	private JButton _remove_skin_button;

	private JLabel _label_animation;
	private JComboBox<ModelAnimation> _model_animations;
	private JButton _add_animation_button;
	private JButton _remove_animation_button;

	private JButton _anim_button_play;
	private JToggleButton _anim_button_loop;
	private JToggleButton _anim_button_freeze;

	private JLabel _label_attachment_point;
	private JButton _add_attachment_point_button;
	private JComboBox<ModelAttachmentPoint> _model_attachment_points;
	private JButton _remove_attachment_point_button;

	public static final int BUILD = 0;
	public static final int ANIMATIONS = 1;
	public static final int EQUIPMENTS = 2;
	private JToggleButton[] _buttons;

	private HashMap<ModelPart, ToolboxPanelBuildPart> _build_panels;
	private HashMap<ModelPart, HashMap<ModelAnimation, ToolboxPanelAnimationPart>> _animations_panels;
	private HashMap<ModelPart, ToolboxPanelEquipmentPart> _equipment_panels;

	private EntityModeled _entity;

	public static final Color[] DEFAULT_COLORS = { new Color(0xFFFFFFFF), new Color(0xFF7EC0EE), new Color(0xFFCD5555),
			new Color(0xFF90EE90) };
	private JLabel _label_color;
	private JButton _add_color_button;
	private JComboBoxColor _colors;
	private JButton _remove_color_button;
	private Color _prev_color;

	private Border _border;

	public ToolboxPanelModel(Toolbox toolbox, EntityModeled entity) {
		super(toolbox);

		this._border = BorderFactory.createLineBorder(Color.BLACK, 2);

		this._entity = entity;

		this._label_model_type = new JLabel("", SwingConstants.CENTER);
		this._label_model_type.setBorder(this._border);
		this._textfield_model_name = new JTextField();

		// PARTS PARTS PARTS PARTS PARTS PARTS PARTS PARTS PARTS PARTS PARTS
		this._label_part = new JLabel("<html><B>Model Parts:</B></html>", SwingConstants.CENTER);
		this._label_part.setBorder(this._border);
		this._model_parts = new JComboBox<ModelPart>();
		this._model_parts.setToolTipText("Model parts");
		this._model_parts.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					refreshVisibility();
					resize();
				}
			}

		});
		((JLabel) this._model_parts.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

		this._add_part_button = new JButton("Add");
		this._add_part_button.setToolTipText("Add a new Model Part to the Model");
		this._add_part_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addModelPart();
				refreshVisibility();
				resize();
			}
		});

		this._remove_part_button = new JButton("Remove");
		this._remove_part_button.setToolTipText("Remove the current ModelPart from the Model");
		this._remove_part_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeModelPart();
				refreshVisibility();
			}
		});

		// SKINS SKINS SKINS SKINS SKINS SKINS SKINS SKINS SKINS SKINS SKINS
		this._label_skin = new JLabel("<html><B>Skins:</B></html>", SwingConstants.CENTER);
		this._label_skin.setBorder(this._border);

		this._model_skins = new JComboBox<ModelSkin>();
		this._model_skins.setToolTipText("Model skins");
		this._model_skins.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					refreshVisibility();
				}
			}

		});
		((JLabel) this._model_skins.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

		this._add_skin_button = new JButton("Add");
		this._add_skin_button.setToolTipText("Add a new Model skin to the Model");
		this._add_skin_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addSkin();
				refreshVisibility();
			}
		});

		this._remove_skin_button = new JButton("Remove");
		this._remove_skin_button.setToolTipText("Remove the current Model skin from the Model");
		this._remove_skin_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeSkin();
				refreshVisibility();
			}
		});

		// ANIMATIONS ANIMATIONS ANIMATIONS ANIMATIONS ANIMATIONS ANIMATIONS
		this._label_animation = new JLabel("<html><B>Animations:</B></html>", SwingConstants.CENTER);
		this._label_animation.setBorder(this._border);

		this._model_animations = new JComboBox<ModelAnimation>();
		this._model_animations.setToolTipText("Model animations");
		this._model_animations.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					refreshVisibility();
					resize();
				}
			}

		});
		((JLabel) this._model_animations.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

		this._add_animation_button = new JButton("Add");
		this._add_animation_button.setToolTipText("Add a new Model animation to the Model");
		this._add_animation_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addAnimation();
				refreshVisibility();
			}
		});

		this._remove_animation_button = new JButton("Remove");
		this._remove_animation_button.setToolTipText("Remove the current Model animation from the Model");
		this._remove_animation_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeAnimation();
				refreshVisibility();
			}
		});

		this._anim_button_play = new JButton("Play");
		this._anim_button_play.setToolTipText("Play the current animation once");
		this._anim_button_play.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				playAnimation();
			}
		});

		this._anim_button_loop = new JToggleButton("Loop");
		this._anim_button_loop.setToolTipText("Loop on the current animation");
		this._anim_button_loop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loopAnimation(_anim_button_loop.isSelected());
			}
		});

		this._anim_button_freeze = new JToggleButton("Freeze");
		this._anim_button_freeze.setToolTipText("Freeze the animation to the current frame timer");
		this._anim_button_freeze.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				freezeAnimation(_anim_button_freeze.isSelected());
			}
		});

		// ATTACHMENT POINTS
		this._label_attachment_point = new JLabel("<html><B>Attachment points:</B></html>", SwingConstants.CENTER);
		this._label_attachment_point.setBorder(this._border);

		this._model_attachment_points = new JComboBox<ModelAttachmentPoint>();
		this._model_attachment_points.setToolTipText("Attachment points");
		this._model_attachment_points.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					refreshVisibility();
				}
			}

		});
		((JLabel) this._model_attachment_points.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

		this._add_attachment_point_button = new JButton("Add");
		this._add_attachment_point_button.setToolTipText("Add a new Attachment point to the Model");
		this._add_attachment_point_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addAttachmentPoint();
			}
		});

		this._remove_attachment_point_button = new JButton("Remove");
		this._remove_attachment_point_button.setToolTipText("Remove the current Attachment point from the Model");
		this._remove_attachment_point_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeAttachmentPoint(getAttachmentPoint());
			}
		});

		/** colors */
		this._colors = new JComboBoxColor();

		for (Color color : DEFAULT_COLORS) {
			this._colors.addItem(new JComboBoxColor.IconColor(color));
		}

		this._label_color = new JLabel("<html><B>Colors:</B></html>", SwingConstants.CENTER);
		this._label_color.setBorder(this._border);

		this._remove_color_button = new JButton("Remove");
		this._remove_color_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeColor();
			}
		});
		this._remove_color_button.setToolTipText("Remove the selected color");

		this._add_color_button = new JButton("Add");
		this._add_color_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addColor();
			}
		});
		this._add_color_button.setToolTipText("Add a color");

		/** build / animate / equipments buttons */
		this._buttons = new JToggleButton[3];
		ActionListener toggle_listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JToggleButton button = (JToggleButton) e.getSource();

				boolean value = button.isSelected();
				for (JToggleButton b : _buttons) {
					b.setSelected(false);
				}

				button.setSelected(value);
				refreshVisibility();
				resize();
			}

		};

		this._buttons[BUILD] = new JToggleButton("Build");
		this._buttons[ANIMATIONS] = new JToggleButton("Animate");
		this._buttons[EQUIPMENTS] = new JToggleButton("Equipments");

		for (JToggleButton button : this._buttons) {
			button.addActionListener(toggle_listener);
		}

		this._build_panels = new HashMap<ModelPart, ToolboxPanelBuildPart>();
		this._animations_panels = new HashMap<ModelPart, HashMap<ModelAnimation, ToolboxPanelAnimationPart>>();
		this._equipment_panels = new HashMap<ModelPart, ToolboxPanelEquipmentPart>();

		// add components
		this.add(this._label_model_type);
		this.add(this._textfield_model_name);

		this.add(this._label_animation);
		this.add(this._add_animation_button);
		this.add(this._model_animations);
		this.add(this._remove_animation_button);

		this.add(this._anim_button_play);
		this.add(this._anim_button_loop);
		this.add(this._anim_button_freeze);

		this.add(this._label_skin);
		this.add(this._add_skin_button);
		this.add(this._model_skins);
		this.add(this._remove_skin_button);

		this.add(this._label_part);
		this.add(this._add_part_button);
		this.add(this._model_parts);
		this.add(this._remove_part_button);

		this.add(this._label_attachment_point);
		this.add(this._add_attachment_point_button);
		this.add(this._model_attachment_points);
		this.add(this._remove_attachment_point_button);

		this.add(_colors);
		this.add(this._label_color);
		this.add(this._add_color_button);
		this.add(this._remove_color_button);

		for (JToggleButton button : this._buttons) {
			this.add(button);
		}

		// add default parts
		for (ModelPart part : this.getModel().getParts()) {
			this._model_parts.addItem(part);
		}

		// add default animations
		for (ModelAnimation animation : this.getModel().getAnimations()) {
			this._model_animations.addItem(animation);
		}

		// add default skins
		for (ModelSkin skin : this.getModel().getSkins()) {
			this._model_skins.addItem(skin);
		}

		this.refreshVisibility();
	}

	public void playAnimation() {
		ModelAnimationInstance instance = this.getEntity().getModelInstance().getAnimationInstance(this.getAnimation());
		if (instance != null) {
			instance.startPlay();
		}
		this._anim_button_loop.setSelected(false);
		this._anim_button_freeze.setSelected(false);
	}

	public void loopAnimation(boolean selected) {

		ModelAnimationInstance instance = this.getEntity().getModelInstance().getAnimationInstance(this.getAnimation());
		if (instance == null) {
			return;
		}

		if (selected) {
			instance.startLoop();
			this._anim_button_freeze.setSelected(false);
		} else {
			instance.stop();
		}
	}

	public void freezeAnimation(boolean selected) {

		ModelAnimationInstance instance = this.getEntity().getModelInstance().getAnimationInstance(this.getAnimation());
		if (instance == null) {
			return;
		}

		if (selected) {
			instance.startFreeze();
			this._anim_button_loop.setSelected(false);
		} else {
			instance.stopFreeze();
		}
	}

	public Model getModel() {
		return (this._entity.getModelInstance().getModel());
	}

	public EntityModeled getEntity() {
		return (this._entity);
	}

	private void addModelPart() {

		String name = JOptionPane.showInputDialog(_add_part_button, "Enter a name: ");
		if (name == null) {
			return;
		}
		if (name.length() == 0) {
			JOptionPane.showMessageDialog(_add_part_button, new JLabel("Empty name"), "Empty name",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		ModelEditor.instance().getEngine().getRenderer().addGLTask(new GLTask() {
			@Override
			public void run() {
				Model model = getModel();
				ModelPartBuilder part = new ModelPartBuilder(name);

				// add the part and create it skin
				model.addPartBuilder(part);

				// add the mode part to the combo list
				_model_parts.addItem(part);
				_model_parts.setSelectedIndex(_model_parts.getItemCount() - 1);

				getEntity().getModelInstance().resetModel();

				ModelEditor.instance().toast("Part added: " + part.getName(), true);
			}
		});
	}

	private void addSkin() {

		String name = JOptionPane.showInputDialog(_add_skin_button, "Enter a name: ");
		if (name == null || name.length() == 0) {
			return;
		}

		ModelEditor.instance().getEngine().getRenderer().addGLTask(new GLTask() {
			@Override
			public void run() {
				Model model = getModel();
				ModelSkin skin = model.getSkin(model.newSkinBuilder(name));

				// add the mode part to the combo list
				_model_skins.addItem(skin);
				_model_skins.setSelectedIndex(_model_skins.getItemCount() - 1);

				refreshVisibility();
				resize();

				ModelEditor.instance().toast("Skin added: " + skin.getName(), true);
			}
		});
	}

	private void removeSkin() {
		ModelSkin skin = this.getSkin();
		if (skin != null) {
			this._model_skins.removeItem(skin);
			this.getModel().removeSkin(skin);
			ModelEditor.instance().toast("Skin removed: " + skin.getName(), false);
		}
	}

	public ModelSkin getSkin() {
		return (ModelSkin) (this._model_skins.getSelectedItem());
	}

	private void addAnimation() {
		String name = JOptionPane.showInputDialog(_add_part_button, "Enter an animation name: ");
		if (name == null || name.length() == 0) {
			return;
		}
		ModelAnimation animation = new ModelAnimation(name);
		this.getModel().addAnimation(animation);
		this._model_animations.addItem(animation);
		this._model_animations.setSelectedIndex(this._model_animations.getItemCount() - 1);
		ModelEditor.instance().toast("Animation added: " + animation.getName(), true);
	}

	private void removeAnimation() {
		ModelAnimation animation = this.getModelAnimation();
		if (animation != null) {
			this._model_animations.removeItem(animation);
			this.getModel().removeAnimation(animation);
			ModelEditor.instance().toast("Animation removed: " + animation.getName(), true);
		}
	}

	private void addAttachmentPoint() {
		ModelEditor editor = this.getToolbox().getModelEditor();
		editor.getCamera().getTools().useTool(CameraToolNewAttachmentPointModel.class);
		editor.getEngine().getRenderer().getGuiRenderer().toast("Please select an attachment point.", 90);
	}

	public void addAttachmentPoint(ModelAttachmentPoint point) {
		this._model_attachment_points.addItem(point);
		this.getModel().addAttachmentPoint(point);
		this._model_attachment_points.setSelectedIndex(this._model_attachment_points.getItemCount() - 1);
		ModelEditor.instance().toast("Attachment point added: " + point.getName(), true);

		this.refreshVisibility();
	}

	public void removeAttachmentPoint(ModelAttachmentPoint point) {
		this._model_attachment_points.removeItem(point);
		this.getModel().removeAttachmentPoint(point);

		this.refreshVisibility();
		ModelEditor.instance().toast("Attachment point removed: " + point.getName(), false);

	}

	private void removeModelPart() {
		ModelEditor.instance().getEngine().getRenderer().addGLTask(new GLTask() {
			@Override
			public void run() {

				Model model = getModel();
				ModelPart part = getModelPart();
				if (part != null) {
					_model_parts.removeItem(part);
					model.removePart(part);
					part.delete();

					model.updateBox();

					// reset the model
					getEntity().getModelInstance().resetModel();
				}
			}
		});
	}

	@Override
	protected void updateVisibility() {

		this._remove_part_button.setEnabled(this._model_parts.getItemCount() > 0);
		this._model_parts.setEnabled(this._model_parts.getItemCount() > 0);

		this._remove_animation_button.setEnabled(this._model_animations.getItemCount() > 0);
		this._model_animations.setEnabled(this._model_animations.getItemCount() > 0);

		this._remove_skin_button.setEnabled(this._model_skins.getSelectedIndex() > 0);
		this._model_skins.setEnabled(this._model_skins.getItemCount() > 0);

		this._remove_attachment_point_button.setEnabled(this._model_attachment_points.getItemCount() > 0);
		this._model_attachment_points.setEnabled(this._model_attachment_points.getItemCount() > 0);

		this._add_attachment_point_button.setEnabled(this.getModelPart() != null);

		this._anim_button_play.setEnabled(this.getAnimation() != null);
		this._anim_button_loop.setEnabled(this.getAnimation() != null);
		this._anim_button_freeze.setEnabled(this.getAnimation() != null);

		this.updateAnimationButtonVisibility();

		for (JToggleButton button : this._buttons) {
			button.setEnabled(this.getModelPart() != null);
		}
		this._buttons[BUILD].setEnabled(this.getModelPart() != null);
		this._buttons[ANIMATIONS].setEnabled(this.getAnimation() != null);
		this._buttons[EQUIPMENTS].setEnabled(this.getModelPart() != null);

		this._remove_color_button.setEnabled(this._colors.getItemCount() > DEFAULT_COLORS.length);

		// remove every panels and add only the one of the current part
		ToolboxPanel[] panels = new ToolboxPanel[3];
		panels[BUILD] = this.getBuildPanel();
		panels[ANIMATIONS] = this.getAnimationPanel();
		panels[EQUIPMENTS] = this.getEquipmentPanel();

		for (Component component : this.getComponents()) {
			if (component instanceof ToolboxPanelBuildPart || component instanceof ToolboxPanelAnimationPart
					|| component instanceof ToolboxPanelEquipmentPart) {
				this.remove(component);
			}
		}

		int index;
		for (index = 0; index < this._buttons.length; index++) {
			if (this._buttons[index].isSelected()) {
				break;
			}
		}

		if (index != this._buttons.length) {
			ToolboxPanel panel = panels[index];
			panel.setVisible(true);
			panel.refreshVisibility();

			Logger.get().log(Logger.Level.DEBUG, "set: " + panel.getClass().getSimpleName());
			this.add(panel);
		}
	}

	public void updateAnimationButtonVisibility() {

		ModelAnimationInstance animinstance = this.getAnimationInstance();
		if (animinstance != null) {
			this._anim_button_loop.setSelected(animinstance.isLooping());
			this._anim_button_freeze.setSelected(animinstance.isFreezing());
		}
	}

	@Override
	public void resize() {

		int w = this.getWidth() / 4;
		int h = this.getHeight() / 32;

		//
		this._label_part.setSize(w, h);
		this._add_part_button.setSize(w, h);
		this._model_parts.setSize(w, h);
		this._model_parts.setPrototypeDisplayValue(null);
		this._remove_part_button.setSize(w, h);

		this._label_part.setLocation(0, 0);
		this._add_part_button.setLocation(w, 0);
		this._model_parts.setLocation(w * 2, 0);
		this._remove_part_button.setLocation(w * 3, 0);

		// skin
		this._label_skin.setSize(w, h);
		this._add_skin_button.setSize(w, h);
		this._model_skins.setSize(w, h);
		this._model_skins.setPrototypeDisplayValue(null);
		this._remove_skin_button.setSize(w, h);

		this._label_skin.setLocation(0, h);
		this._add_skin_button.setLocation(w, h);
		this._model_skins.setLocation(w * 2, h);
		this._remove_skin_button.setLocation(w * 3, h);

		// animation
		this._label_animation.setSize(w, h * 2);
		this._add_animation_button.setSize(w, h);
		this._model_animations.setSize(w, h);
		this._model_animations.setPrototypeDisplayValue(null);
		this._remove_animation_button.setSize(w, h);

		this._label_animation.setLocation(0, (int) (h * 2));
		this._add_animation_button.setLocation(w, h * 2);
		this._model_animations.setLocation(w * 2, h * 2);
		this._remove_animation_button.setLocation(w * 3, h * 2);

		this._anim_button_play.setLocation(w, h * 3);
		this._anim_button_loop.setLocation(w * 2, h * 3);
		this._anim_button_freeze.setLocation(w * 3, h * 3);

		this._anim_button_play.setSize(w, h);
		this._anim_button_loop.setSize(w, h);
		this._anim_button_freeze.setSize(w, h);

		// atachment points
		this._label_attachment_point.setSize(w, h);
		this._add_attachment_point_button.setSize(w, h);
		this._model_attachment_points.setSize(w, h);
		this._model_attachment_points.setPrototypeDisplayValue(null);
		this._remove_attachment_point_button.setSize(w, h);

		this._label_attachment_point.setLocation(0, h * 4);
		this._add_attachment_point_button.setLocation(w, h * 4);
		this._model_attachment_points.setLocation(w * 2, h * 4);
		this._remove_attachment_point_button.setLocation(w * 3, h * 4);

		// colors
		this._label_color.setSize(w, h);
		this._add_color_button.setSize(w, h);
		this._colors.setSize(w, h);
		this._colors.setPrototypeDisplayValue(null);
		this._remove_color_button.setSize(w, h);

		this._label_color.setLocation(0, h * 5);
		this._add_color_button.setLocation(w, h * 5);
		this._colors.setLocation(w * 2, h * 5);
		this._remove_color_button.setLocation(w * 3, h * 5);

		// buttons
		int bw = this.getWidth() / this._buttons.length;
		for (int i = 0; i < this._buttons.length; i++) {
			this._buttons[i].setSize(bw, h);
			this._buttons[i].setLocation(bw * i, h * 6);
		}

		// panels
		ToolboxPanel panels[] = new ToolboxPanel[3];
		panels[BUILD] = this.getBuildPanel();
		panels[ANIMATIONS] = this.getAnimationPanel();
		panels[EQUIPMENTS] = this.getEquipmentPanel();

		for (ToolboxPanel panel : panels) {
			if (panel == null) {
				continue;
			}

			panel.setLocation(0, h * 7);
			panel.setSize(this.getWidth(), this.getHeight() - h * 7);
			panel.resize();
		}
	}

	public ModelPartBuilder getModelPart() {

		if (this._model_parts.getItemCount() == 0) {
			return (null);
		}
		return ((ModelPartBuilder) this._model_parts.getSelectedItem());
	}

	public ModelAnimation getModelAnimation() {
		return (ModelAnimation) (this._model_animations.getSelectedItem());
	}

	public ModelAnimationFrame getModelAnimationFrame() {
		ToolboxPanelAnimationPart panel = this.getAnimationPanel();
		if (panel == null) {
			return (null);
		}
		return (panel.getModelAnimationFrame());
	}

	public ToolboxPanelBuildPart getBuildPanel() {
		ModelPart part = this.getModelPart();
		if (part == null) {
			return (null);
		}
		ToolboxPanelBuildPart panel = this._build_panels.get(part);
		if (panel == null) {
			panel = new ToolboxPanelBuildPart(this.getToolbox());
			this._build_panels.put(part, panel);
		}
		return (panel);
	}

	public ToolboxPanelAnimationPart getAnimationPanel() {

		ModelPart part = this.getModelPart();
		ModelAnimation animation = this.getAnimation();
		if (part == null || animation == null) {
			return (null);
		}

		HashMap<ModelAnimation, ToolboxPanelAnimationPart> animations = this._animations_panels.get(part);
		if (animations == null) {
			animations = new HashMap<ModelAnimation, ToolboxPanelAnimationPart>();
			this._animations_panels.put(part, animations);
		}

		ToolboxPanelAnimationPart panel = animations.get(animation);
		if (panel == null) {
			panel = new ToolboxPanelAnimationPart(this.getToolbox());
			animations.put(animation, panel);
		}
		return (panel);
	}

	public ToolboxPanelEquipmentPart getEquipmentPanel() {

		ModelPart part = this.getModelPart();
		if (part == null) {
			return (null);
		}
		ToolboxPanelEquipmentPart panel = this._equipment_panels.get(part);
		if (panel == null) {
			panel = new ToolboxPanelEquipmentPart(this.getToolbox());
			this._equipment_panels.put(part, panel);
		}
		return (panel);
	}

	public Color getBuildingColor() {
		IconColor color = (IconColor) this._colors.getSelectedItem();
		if (color == null) {
			return (Color.WHITE);
		}
		return (color.getColor());
	}

	public String getModelName() {
		return (this._textfield_model_name.getText());
	}

	@Override
	public String toString() {
		return (this.getModel().getName());
	}

	public ModelAttachmentPoint getAttachmentPoint() {
		return (ModelAttachmentPoint) (this._model_attachment_points.getSelectedItem());
	}

	public ModelAnimation getAnimation() {
		return (ModelAnimation) (this._model_animations.getSelectedItem());
	}

	private void removeColor() {
		IconColor color = (IconColor) this._colors.getSelectedItem();
		this._colors.removeItem(color);
		this.getModel().removeColor(color.getColor());
		this.refreshVisibility();
	}

	private void addColor() {
		Color color = JColorChooser.showDialog(this._add_color_button, "Choose a Color", _prev_color);
		if (color == null) {
			return;
		}
		_colors.addItem(new IconColor(color));
		_colors.setSelectedIndex(_colors.getItemCount() - 1);
		_prev_color = color;
		this.getModel().addColor(color);
		this.refreshVisibility();
	}
}
