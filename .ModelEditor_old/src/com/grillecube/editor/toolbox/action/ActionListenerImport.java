package com.grillecube.editor.toolbox.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.grillecube.client.renderer.MainRenderer.GLTask;
import com.grillecube.client.renderer.model.Model;
import com.grillecube.client.renderer.model.json.ModelBuilderImporter;
import com.grillecube.editor.ModelEditor;

public class ActionListenerImport implements ActionListener {
	private ModelEditor _editor;

	public ActionListenerImport(ModelEditor window) {
		this._editor = window;
	}

	/** called whent he user click on "export" */
	public void actionPerformed(ActionEvent event) {
		JFileChooser chooser = new JFileChooser();

		chooser.setCurrentDirectory(new File(ModelEditor.RUNNING_DIRECTORY.getPath()));
		chooser.setFileFilter(new FileNameExtensionFilter("Model file (*.json)", "json"));

		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			String filepath = chooser.getSelectedFile().getAbsolutePath();
			ModelEditor.instance().getEngine().getRenderer().addGLTask(new GLTask() {
				@Override
				public void run() {
					Model model = ModelBuilderImporter.importModelBuilder(filepath);
					model.refreshVertices();
					if (model != null) {
						_editor.addModel(model);
					}
				}
			});
		}
	}
}