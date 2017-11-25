package com.grillecube.client.renderer.lines;

import java.util.ArrayList;

import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.renderer.RendererFactory;
import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.utils.Color;
import com.grillecube.common.world.entity.collision.Positioneable;
import com.grillecube.common.world.entity.collision.Sizeable;

public class LineRendererFactory extends RendererFactory {

	private final ArrayList<Line> renderingList;
	private CameraProjective camera;

	public LineRendererFactory(MainRenderer mainRenderer) {
		this(mainRenderer, null);
	}

	public LineRendererFactory(MainRenderer mainRenderer, CameraProjective camera) {
		super(mainRenderer);
		this.renderingList = new ArrayList<Line>();
		this.setCamera(camera);
	}

	public final void setCamera(CameraProjective camera) {
		this.camera = camera;
	}

	public final CameraProjective getCamera() {
		return (this.camera);
	}

	public final ArrayList<Line> getRenderingList() {
		return (this.renderingList);
	}

	@Override
	public void update(double dt) {
	}

	/** add a line to the renderer factory */
	public final Line addLine(Line line) {
		return (this.renderingList.add(line) ? line : null);
	}

	// bounding box corners
	static Vector3f[] corners = new Vector3f[8];

	static {
		corners[0] = new Vector3f(0, 0, 0);
		corners[1] = new Vector3f(1, 0, 0);
		corners[2] = new Vector3f(1, 0, 1);
		corners[3] = new Vector3f(0, 0, 1);
		corners[4] = new Vector3f(0, 1, 0);
		corners[5] = new Vector3f(1, 1, 0);
		corners[6] = new Vector3f(1, 1, 1);
		corners[7] = new Vector3f(0, 1, 1);
	}

	public final int setBox(Positioneable position, Sizeable sizeable, int index, Color color) {

		Vector3f boxorigin = new Vector3f(position.getPositionX(), position.getPositionY(), position.getPositionZ());
		Vector3f boxsize = new Vector3f(sizeable.getSizeX(), sizeable.getSizeY(), sizeable.getSizeZ());
		Line[] lines = new Line[12];

		lines[0] = this.getBoxLine(color, boxorigin, boxsize, 0, 1);
		lines[1] = this.getBoxLine(color, boxorigin, boxsize, 1, 2);
		lines[2] = this.getBoxLine(color, boxorigin, boxsize, 2, 3);
		lines[3] = this.getBoxLine(color, boxorigin, boxsize, 3, 0);

		lines[4] = this.getBoxLine(color, boxorigin, boxsize, 4, 5);
		lines[5] = this.getBoxLine(color, boxorigin, boxsize, 5, 6);
		lines[6] = this.getBoxLine(color, boxorigin, boxsize, 6, 7);
		lines[7] = this.getBoxLine(color, boxorigin, boxsize, 7, 4);

		lines[8] = this.getBoxLine(color, boxorigin, boxsize, 0, 4);
		lines[9] = this.getBoxLine(color, boxorigin, boxsize, 1, 5);

		lines[10] = this.getBoxLine(color, boxorigin, boxsize, 2, 6);
		lines[11] = this.getBoxLine(color, boxorigin, boxsize, 3, 7);

		if (index >= this.renderingList.size()) {
			index = this.renderingList.size();
			for (Line line : lines) {
				this.renderingList.add(line);
			}
			return (index);
		}

		int i = index;
		for (Line line : lines) {
			this.renderingList.set(i++, line);
		}

		return (index);
	}

	public final int addBox(Positioneable positionable, Sizeable sizeable) {
		return (this.addBox(positionable, sizeable, Color.BLUE));
	}

	public final int addBox(Positioneable positionable, Sizeable sizeable, Color color) {
		return (this.setBox(positionable, sizeable, this.renderingList.size(), color));
	}

	private Line getBoxLine(Color color, Vector3f boxorigin, Vector3f boxsize, int cornera, int cornerb) {

		Vector3f a = new Vector3f(corners[cornera]);
		Vector3f b = new Vector3f(corners[cornerb]);

		a.scale(boxsize);
		b.scale(boxsize);

		a.add(boxorigin);
		b.add(boxorigin);

		// Logger.get().log(Logger.Level.DEBUG, "added line: " + a, b);
		return (new Line(a, color, b, color));
	}

	@Override
	public void render() {
		this.getMainRenderer().getLineRenderer().render(this);
	}

	public final void removeAllLines() {
		this.renderingList.clear();
	}

}
