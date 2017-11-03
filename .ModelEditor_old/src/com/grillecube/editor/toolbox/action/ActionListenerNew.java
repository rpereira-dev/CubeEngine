package com.grillecube.editor.toolbox.action;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.grillecube.client.renderer.MainRenderer.GLTask;
import com.grillecube.client.renderer.model.Model;
import com.grillecube.common.Logger;
import com.grillecube.common.Logger.Level;
import com.grillecube.editor.ModelEditor;

public class ActionListenerNew implements ActionListener {

	private ModelEditor _editor;

	public ActionListenerNew(ModelEditor window) {
		this._editor = window;
	}

	/** called when he user click on "export" */
	@Override
	public void actionPerformed(ActionEvent event) {

		JTextField modelnamefield = new JTextField(64);
		JComboBox<String> models = new JComboBox<String>();
		for (String str : Model.MODELS_INFO) {
			models.addItem(str);
		}

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1));
		panel.add(new JLabel("Enter new model name: "));
		panel.add(modelnamefield);
		panel.add(new JLabel("Select new model type: "));
		panel.add(models);

		int result = JOptionPane.showConfirmDialog(null, panel, "Creating a new model...",
				JOptionPane.OK_CANCEL_OPTION);

		if (result == JOptionPane.OK_OPTION) {

			Class<? extends Model> modelclass = Model.getModelClassByInfo((String) models.getSelectedItem());
			String modelname = isModelNameValid(modelnamefield.getText()) ? modelnamefield.getText() : "DefaultName";
			if (modelclass != null) {

				ModelEditor.instance().getEngine().getRenderer().addGLTask(new GLTask() {
					@Override
					public void run() {
						try {
							Logger.get().log(Level.FINE, "Reseting model...");

							Model model = (Model) modelclass.newInstance();
							model.prepareModelBuilder();
							model.setName(modelname + " (" + modelclass.getSimpleName() + ")");
							_editor.addModel(model);
						} catch (InstantiationException | IllegalAccessException e) {
							e.printStackTrace();
						}
					}
				});
			}
		}
	}

	static final String char_allowed = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ 123456789";

	private boolean isModelNameValid(String text) {
		if (text == null || text.isEmpty()) {
			return (false);
		}
		int i;
		for (i = 0; i < text.length(); i++) {
			if (char_allowed.indexOf(text.charAt(i)) < 0) {
				return (false);
			}
		}
		return (true);
	}
}
