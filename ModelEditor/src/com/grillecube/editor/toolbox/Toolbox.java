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

package com.grillecube.editor.toolbox;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.grillecube.editor.ModelEditor;
import com.grillecube.editor.model.ModelGrid;
import com.grillecube.editor.toolbox.action.ActionListenerNew;
import com.grillecube.engine.Logger;
import com.grillecube.engine.Logger.Level;
import com.grillecube.engine.maths.Vector3f;
import com.grillecube.engine.renderer.MainRenderer.GLTask;
import com.grillecube.engine.renderer.model.Model;
import com.grillecube.engine.renderer.model.ModelPart;
import com.grillecube.engine.renderer.model.ModelSkin;
import com.grillecube.engine.renderer.model.animation.ModelAnimation;
import com.grillecube.engine.renderer.model.animation.ModelAnimationFrame;
import com.grillecube.engine.renderer.model.animation.ModelAnimationInstance;
import com.grillecube.engine.renderer.model.animation.ModelPartAnimation;
import com.grillecube.engine.renderer.model.builder.ModelPartBuilder;
import com.grillecube.engine.renderer.model.instance.ModelInstance;
import com.grillecube.engine.resources.R;
import com.grillecube.engine.world.World;
import com.grillecube.engine.world.entity.EntityModeled;

public class Toolbox extends JFrame implements MouseListener {
	public static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();

	private static final int TOOLBOX_WIDTH = SCREEN_SIZE.width / 4;
	private static final int TOOLBOX_HEIGHT = SCREEN_SIZE.height;
	private static final int TOOLBOX_POS_X = SCREEN_SIZE.width - TOOLBOX_WIDTH;
	private static final int TOOLBOX_POS_Y = SCREEN_SIZE.height - TOOLBOX_HEIGHT;
	private static final long serialVersionUID = 1L;

	/** model editor instance */
	private ModelEditor _editor;

	/** exit key listener */
	private KeyListener _key_listener;

	/** model list */
	private JButton _add_model;
	private HashMap<Model, ToolboxPanelModel> _panels;
	private JComboBox<Model> _models;
	private JLabel _label_models;
	private JButton _remove_model;

	private ToolboxMenuBar _menu_bar;

	public Toolbox(ModelEditor editor) {
		super();
		this._editor = editor;
		this._panels = new HashMap<Model, ToolboxPanelModel>();
		this.prepareToolbox();
		this.addDefaultComponents();
		this.resize();
		this.refreshVisibility();
	}

	private void addDefaultComponents() {

		this._add_model = new JButton("New");
		this._add_model.setToolTipText("Add a new Model");
		this._add_model.addActionListener(new ActionListenerNew(this.getModelEditor()));

		this._label_models = new JLabel("<html><B>Models:</B></html>", SwingConstants.CENTER);

		this._models = new JComboBox<Model>();
		this._models.setToolTipText("Models");
		this._models.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					refreshVisibility();
				}
			}
		});
		((JLabel) this._models.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

		this._remove_model = new JButton("Remove");
		this._remove_model.setToolTipText("Remove the current Model from the editor");
		this._remove_model.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeModel();
				refreshVisibility();
			}
		});

		this.add(this._models);
		this.add(this._add_model);
		this.add(this._label_models);
		this.add(this._remove_model);
	}

	private void prepareToolbox() {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			this.setIconImage(new ImageIcon(R.getResPath("textures/blocks/grass_top.png")).getImage());
			this._menu_bar = new ToolboxMenuBar(this);
			this.setJMenuBar(this._menu_bar);
		} catch (Exception e) {
			Logger.get().log(Level.WARNING, "No system look and feel found, Java default one expected");
		}
		this._key_listener = new ToolboxListenerKey();

		super.setSize(TOOLBOX_WIDTH, TOOLBOX_HEIGHT);
		super.setLocation(TOOLBOX_POS_X, TOOLBOX_POS_Y);
		super.setTitle("ModelEditor Toolbox");
		super.setDefaultCloseOperation(EXIT_ON_CLOSE);
		super.setVisible(true);
		super.setLayout(null);

		super.addComponentListener(new ToolboxListenerResize(this));
		super.addKeyListener(this._key_listener);

		super.addMouseListener(this);
	}

	public void addModel(EntityModeled entity) {

		ToolboxPanelModel panel = new ToolboxPanelModel(this, entity);
		Model model = entity.getModelInstance().getModel();
		this._panels.put(model, panel);
		this._models.addItem(model);
		this._models.setSelectedItem(model);

		this.resize();
		this.refreshVisibility();
	}

	private void removeModel() {

		Model model = this.getModel();
		ToolboxPanel panel = this.getModelPanel();
		if (model == null || panel == null) {
			return;
		}

		this._models.removeItem(model);
		this.remove(panel);
		this._panels.remove(model);

		this.getModelEditor().getEngine().getRenderer().addGLTask(new GLTask() {

			@Override
			public void run() {
				getWorld().getEntityStorage().removeEntitiesByModel(model);
				model.delete();
			}

		});
	}

	public Component add(Component comp) {
		comp.addKeyListener(this._key_listener);
		return (super.add(comp));
	}

	public void resize() {

		if (this._label_models == null) {
			return;
		}

		Logger.get().log(Level.FINE, "Recalculating components size");

		int w = this.getWidth() / 4;
		int h = this.getHeight() / 32;

		this._label_models.setSize(w, h);
		this._label_models.setLocation(0, 0);

		this._add_model.setSize(w, h);
		this._add_model.setLocation(w, 0);

		this._models.setSize(w, h);
		this._models.setPrototypeDisplayValue(null);
		this._models.setLocation(w * 2, 0);

		this._remove_model.setSize(w, h);
		this._remove_model.setLocation(w * 3, 0);

		for (ToolboxPanelModel panel : this._panels.values()) {
			panel.setSize(this.getWidth(), this.getHeight() - h);
			panel.setLocation(0, h);
			panel.resize();
		}
	}

	public World getWorld() {
		return (this._editor.getWorld());
	}

	public void refreshVisibility() {

		for (ToolboxPanel panel : this._panels.values()) {
			this.remove(panel);
		}

		Model model = this.getModel();
		ToolboxPanelModel panel = this.getModelPanel();
		if (model != null && panel != null) {
			panel.setVisible(true);
			panel.refreshVisibility();
			this.add(panel);

			ModelPartBuilder part = panel.getModelPart();

			// reset grid
			ModelGrid grid = this.getModelEditor().getGrid();
			grid.set(model.getOrigin(), model.getAxis(), part == null ? Vector3f.ONE_VEC : part.getBlockScale());

			// reset rotation point
			this.getModelEditor().getWorld().getRotationPoint().reset();

			// reset entities
			this.getWorld().getEntityStorage().add(panel.getEntity());
		}

		this._remove_model.setEnabled(this._models.getItemCount() > 0);
		this._models.setEnabled(this._models.getItemCount() > 0);

		// repaint
		this.repaint();
	}

	public KeyListener getKeyListener() {
		return (this._key_listener);
	}

	public ModelEditor getModelEditor() {
		return (this._editor);
	}

	public void close() {
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	public ModelPartBuilder getModelPart() {
		ToolboxPanelModel panel = this.getModelPanel();
		return (panel == null ? null : panel.getModelPart());
	}

	public ToolboxPanelModel getModelPanel() {
		return ((ToolboxPanelModel) (this._panels.get(this.getModel())));
	}

	public Color getBuildingColor() {
		return (this.getModelPanel().getBuildingColor());
	}

	public String getModelName() {
		return (this.getModelPanel().getModelName());
	}

	public EntityModeled getEntity() {

		if (this.getModelPanel() == null) {
			return (null);
		}

		return (this.getModelPanel().getEntity());
	}

	public Model getModel() {
		return ((Model) this._models.getSelectedItem());
	}

	public ModelSkin getModelSkin() {
		return (this.getModelPanel().getSkin());
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

	public ModelAnimation getAnimation() {
		return (this.getModelPanel().getAnimation());
	}

	public ModelPartAnimation getAnimationPart() {

		if (this.getModel() == null || this.getModelPart() == null || this.getAnimation() == null) {
			return (null);
		}

		int partID = this.getModel().getPartID(this.getModelPart());
		ModelPartAnimation animationpart = this.getAnimation().getAnimationPart(partID);
		return (animationpart);
	}

	public ModelAnimationFrame getModelAnimationFrame() {
		return (this.getModelPanel().getModelAnimationFrame());
	}
}

abstract class ToolboxPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	protected int _width;
	protected int _height;
	protected int _x;
	protected int _y;

	private Toolbox _toolbox;

	public ToolboxPanel(Toolbox toolbox) {
		super();
		this._toolbox = toolbox;
		this.setLayout(null);
	}

	public int getMaxX() {
		return (this._x + this._width);
	}

	public int getMaxY() {
		return (this._y + this._height);
	}

	@Override
	public void setLocation(int x, int y) {
		this._x = x;
		this._y = y;
		super.setLocation(x, y);
	}

	@Override
	public void setSize(int w, int h) {
		this._width = w;
		this._height = h;
		super.setSize(w, h);
	}

	public int getWidth() {
		return (this._width);
	}

	public int getHeight() {
		return (this._height);
	}

	@Override
	public void addKeyListener(KeyListener listener) {
		super.addKeyListener(listener);
	}

	public Model getModel() {
		return (this.getToolbox().getModel());
	}

	public ModelPart getModelPart() {
		return (this.getToolbox().getModelPart());
	}

	public ModelAnimation getModelAnimation() {
		return (this.getToolbox().getModelPanel().getModelAnimation());
	}

	public EntityModeled getEntity() {
		return (this.getToolbox().getEntity());
	}

	public Toolbox getToolbox() {
		return (this._toolbox);
	}

	public ModelAnimationInstance getAnimationInstance() {

		ModelAnimation animation = this.getModelAnimation();
		EntityModeled entity = this.getEntity();
		if (animation != null && entity != null) {
			ModelInstance instance = entity.getModelInstance();
			if (instance != null) {
				ModelAnimationInstance animinstance = instance.getAnimationInstance(animation);
				return (animinstance);
			}
		}
		return (null);
	}

	@Override
	public Component add(Component comp) {
		comp.addKeyListener(this.getToolbox().getKeyListener());
		return (super.add(comp));
	}

	/** instance all components */
	protected abstract void updateVisibility();

	/** update visibility */
	public void refreshVisibility() {
		Logger.get().log(Level.DEBUG, "Updating visibility: " + this.getClass().getSimpleName());
		this.updateVisibility();
		this.repaint();
	}

	/** resize components */
	public abstract void resize();
}