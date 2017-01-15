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

package com.grillecube.editor;

import com.grillecube.engine.Logger;
import com.grillecube.engine.Logger.Level;
import com.grillecube.engine.renderer.model.json.ModelBuilderExporter;

public class Main {
	public static final void main(String[] args) {
		ModelEditor editor = new ModelEditor();

		try {
			editor.loop();
		} catch (Exception exception) {

			Logger.get().log(Level.ERROR, "Hum... The Model Editor crashed");
			Logger.get().log(Level.ERROR, "Please send me the following error message:");
			exception.printStackTrace(Logger.get().getPrintStream());

			String filepath = System.currentTimeMillis() + "-model_saved.json";
			ModelBuilderExporter.exportModelBuilder(editor.getModel(), filepath);
			Logger.get().log(Level.ERROR, "Hum... The Model Editor crashed. Your model has been saved to: " + filepath);
		}
		Logger.get().log(Level.FINE, "stopped");
	}
}

// TODO : FIX NEW MODEL (NON LIVING BIPED) BUILDNIG ISSUES!