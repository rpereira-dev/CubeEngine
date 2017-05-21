package com.grillecube.editor.toolbox;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.grillecube.common.Logger;
import com.grillecube.common.Logger.Level;
import com.grillecube.editor.ModelEditor;
import com.grillecube.editor.toolbox.action.ActionListenerExport;
import com.grillecube.editor.toolbox.action.ActionListenerImport;
import com.grillecube.editor.toolbox.action.ActionListenerNew;

public class ToolboxMenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;
	private Toolbox _toolbox;

	public ToolboxMenuBar(Toolbox toolbox) {
		this._toolbox = toolbox;
		this.addFileMenu();
		this.addAboutMenu();
		this.addExitMenu();
	}

	private void addFileMenu() {
		JMenu menu = new JMenu("File");
		{
			JMenu new_menu = new JMenu("New...");
			{
				JMenuItem item;

				item = new JMenuItem("ModelBuilder");
				item.setToolTipText("ModelEditor default file format for any models");
				item.addActionListener(new ActionListenerNew(this._toolbox.getModelEditor()));
				new_menu.add(item);

				// item = new JMenuItem("ModelBuilderStatic");
				// item.setToolTipText("ModelEditor file format for static game
				// objects");
				// item.addActionListener(new
				// ActionListenerNew(this._toolbox.getModelEditor(),
				// ModelBuilderStatic.class));
				// new_menu.add(item);
				//
				// item = new JMenuItem("ModelBuilderLiving");
				// item.setToolTipText("ModelEditor file format for living game
				// entities");
				// item.addActionListener(new
				// ActionListenerNew(this._toolbox.getModelEditor(),
				// ModelBuilderLiving.class));
				// new_menu.add(item);
				//
				// item = new JMenuItem("ModelBuilderBiped");
				// item.setToolTipText("ModelEditor file format for living game
				// entities");
				// item.addActionListener(new
				// ActionListenerNew(this._toolbox.getModelEditor(),
				// ModelBuilderBiped.class));
				// new_menu.add(item);
			}
			menu.add(new_menu);

			JMenu import_menu = new JMenu("Load...");
			{
				JMenuItem item_model = new JMenuItem("Model");
				item_model.setToolTipText("Load the given Model");
				item_model.addActionListener(new ActionListenerImport(this._toolbox.getModelEditor()));
				import_menu.add(item_model);
			}
			menu.add(import_menu);

			JMenu export_menu = new JMenu("Save...");
			{
				JMenuItem item_model = new JMenuItem("Model");
				item_model.setToolTipText("Save the current model");
				item_model.addActionListener(new ActionListenerExport(this._toolbox.getModelEditor()));
				export_menu.add(item_model);
			}
			menu.add(export_menu);
		}
		this.add(menu);
	}

	private void addAboutMenu() {
		JMenu menu = new JMenu("About");
		{

			JMenuItem item_repo = new JMenuItem("Source code");
			item_repo.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						Desktop.getDesktop().browse(new URL("https://github.com/rpereira-dev/VoxelEngine").toURI());
					} catch (Exception exception) {
						Logger.get().log(Level.ERROR, "Exception occured: " + exception.getMessage());
					}
				}
			});
			menu.add(item_repo);

			JMenuItem item_version = new JMenuItem("Version: " + ModelEditor.VERSION);
			menu.add(item_version);
		}
		this.add(menu);
	}

	private void addExitMenu() {
		JMenu menu = new JMenu("Exit");
		{
			JMenuItem exit_menu_item = new JMenuItem("Exit");
			exit_menu_item.setToolTipText("Exit application");

			exit_menu_item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					_toolbox.close();
				}
			});
			menu.add(exit_menu_item);
		}
		this.add(menu);
	}
}
