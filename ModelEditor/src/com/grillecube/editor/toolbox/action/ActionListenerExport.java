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

package com.grillecube.editor.toolbox.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.grillecube.editor.ModelEditor;
import com.grillecube.engine.renderer.MainRenderer.GLTask;
import com.grillecube.engine.renderer.model.json.ModelExporter;

public class ActionListenerExport implements ActionListener {
	private ModelEditor _editor;

	public ActionListenerExport(ModelEditor window) {
		this._editor = window;
	}

	/** called whent he user click on "export" */
	public void actionPerformed(ActionEvent event) {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File(ModelEditor.RUNNING_DIRECTORY.getPath()));
		chooser.setFileFilter(new FileNameExtensionFilter("Model file (*.json)", "json"));
		if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			final String filepath;
			String path = chooser.getSelectedFile().getAbsolutePath();
			if (!path.endsWith(".json")) {
				path = path + ".json";
			}
			filepath = path;
			ModelEditor.instance().getEngine().getRenderer().addGLTask(new GLTask() {
				@Override
				public void run() {
					_editor.getModel().setName(_editor.getToolbox().getModelName());
					ModelExporter.exportModel(_editor.getModel(), filepath);
					ModelEditor.instance().getEngine().getRenderer().getGuiRenderer().toast("Model was exported!", 0.0f,
							1.0f, 0.0f, 1.0f);
				}
			});
		}
	}
}