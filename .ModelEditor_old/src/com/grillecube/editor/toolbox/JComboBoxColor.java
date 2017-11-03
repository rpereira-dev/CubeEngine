package com.grillecube.editor.toolbox;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import com.grillecube.editor.toolbox.JComboBoxColor.IconColor;

class JComboBoxColor extends JComboBox<IconColor> {
	/**
	 * A JComboBox which holds colors and render a preview of it
	 */
	private static final long serialVersionUID = 1L;

	private DefaultComboBoxModel<IconColor> _model;

	public JComboBoxColor() {
		this._model = new DefaultComboBoxModel<IconColor>();
		super.setModel(this._model);
		super.setRenderer(new ItemRenderer());
		super.setEditor(new ItemEditor());
	}

	@Override
	public void addItem(IconColor icon) {
		super.addItem(icon);
		icon.setSize(this.getWidth() / 4, this.getHeight());
	}

	@Override
	public void setSize(int width, int height) {
		for (int i = 0; i < super.getItemCount(); i++) {
			super.getItemAt(i).setSize(width / 4, height);
		}
		super.setSize(width, height);
	}

	class ItemRenderer extends JPanel implements ListCellRenderer<IconColor> {
		private static final long serialVersionUID = 1L;

		private JLabel _label;

		public ItemRenderer() {
			super.setLayout(new GridBagLayout());
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.weightx = 1.0;
			constraints.insets = new Insets(2, 2, 2, 2);

			this._label = new JLabel();
			this._label.setOpaque(true);
			this._label.setHorizontalAlignment(JLabel.LEFT);

			super.add(this._label, constraints);
			super.setBackground(Color.LIGHT_GRAY);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends IconColor> list, IconColor color, int index,
				boolean isSelected, boolean cellHasFocus) {
			if (color == null) {
				return (this);
			}
			this._label.setText(color.toString());
			this._label.setIcon(color);

			if (isSelected) {
				this._label.setBackground(new Color(0xFF000077));
				this._label.setForeground(Color.WHITE);
			} else {
				this._label.setForeground(Color.BLACK);
				this._label.setBackground(Color.WHITE);
			}

			return (this);
		}
	}

	class ItemEditor implements ComboBoxEditor {
		private JPanel _panel;
		private JLabel _label;
		private String _selection;

		public ItemEditor() {
			this._panel = new JPanel();
			this._panel.setLayout(new GridBagLayout());
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.weightx = 1.0;
			constraints.insets = new Insets(2, 5, 2, 2);

			this._label = new JLabel();
			this._label.setOpaque(false);
			this._label.setHorizontalAlignment(JLabel.LEFT);
			this._label.setForeground(Color.WHITE);

			this._panel.add(this._label, constraints);
			this._panel.setBackground(Color.BLUE);
		}

		@Override
		public Component getEditorComponent() {
			return (this._panel);
		}

		@Override
		public Object getItem() {
			return (this._label);
		}

		@Override
		public void setItem(Object item) {
			if (item == null) {
				return;
			}
			IconColor color = (IconColor) item;
			this._selection = color.toString();
			this._label.setText(this._selection);
			this._label.setIcon(color);
		}

		@Override
		public void addActionListener(ActionListener l) {
		}

		@Override
		public void removeActionListener(ActionListener l) {
		}

		@Override
		public void selectAll() {
		}
	}

	static class IconColor implements Icon {
		private Color _color;
		private int _width;
		private int _height;

		public IconColor(Color color) {
			this._color = color;
		}

		@Override
		public int getIconWidth() {
			return (this._width);
		}

		@Override
		public int getIconHeight() {
			return (this._height);
		}

		public void setSize(int width, int height) {
			this._width = width;
			this._height = height;
		}

		public Color getColor() {
			return (this._color);
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(this._color);
			g.fillRect(x, y, getIconWidth(), getIconHeight());
		}

		@Override
		public String toString() {
			if (this._color == null) {
				return ("null JComboBoxColor");
			}
			StringBuilder builder = new StringBuilder();
			builder.append("    ");
			builder.append("r:");
			builder.append(this._color.getRed());
			builder.append(" ; ");
			builder.append("g:");
			builder.append(this._color.getGreen());
			builder.append(" ; ");
			builder.append("b:");
			builder.append(this._color.getBlue());
			builder.append(" ; ");
			builder.append("a:");
			builder.append(this._color.getAlpha());
			return (builder.toString());
		}
	}
}
