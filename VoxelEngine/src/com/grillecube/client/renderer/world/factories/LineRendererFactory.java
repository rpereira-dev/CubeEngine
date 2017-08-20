package com.grillecube.client.renderer.world.factories;

import java.util.ArrayList;

import com.grillecube.client.renderer.lines.Line;
import com.grillecube.client.renderer.lines.LineRenderer;
import com.grillecube.client.renderer.world.WorldRenderer;
import com.grillecube.common.maths.BoundingBox;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector4f;

public class LineRendererFactory extends WorldRendererFactory {

	private ArrayList<Line> renderingList;

	public LineRendererFactory(WorldRenderer worldRenderer) {
		super(worldRenderer);
		this.renderingList = new ArrayList<Line>();
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
	}

	@Override
	public void render() {
		LineRenderer lineRenderer = super.getMainRenderer().getLineRenderer();
		lineRenderer.render(super.getCamera(), this.renderingList);
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

	public Line[] addBox(BoundingBox box) {
		Line[] lines = new Line[12];
		Vector4f color = box.getColor();
		Vector3f boxorigin = box.getMin();
		Vector3f boxsize = box.getSize();

		lines[0] = this.addLine(this.getBoxLine(color, boxorigin, boxsize, 0, 1));
		lines[1] = this.addLine(this.getBoxLine(color, boxorigin, boxsize, 1, 2));
		lines[2] = this.addLine(this.getBoxLine(color, boxorigin, boxsize, 2, 3));
		lines[3] = this.addLine(this.getBoxLine(color, boxorigin, boxsize, 3, 0));

		lines[4] = this.addLine(this.getBoxLine(color, boxorigin, boxsize, 4, 5));
		lines[5] = this.addLine(this.getBoxLine(color, boxorigin, boxsize, 5, 6));
		lines[6] = this.addLine(this.getBoxLine(color, boxorigin, boxsize, 6, 7));
		lines[7] = this.addLine(this.getBoxLine(color, boxorigin, boxsize, 7, 4));

		lines[8] = this.addLine(this.getBoxLine(color, boxorigin, boxsize, 0, 4));
		lines[9] = this.addLine(this.getBoxLine(color, boxorigin, boxsize, 1, 5));

		lines[10] = this.addLine(this.getBoxLine(color, boxorigin, boxsize, 2, 6));
		lines[11] = this.addLine(this.getBoxLine(color, boxorigin, boxsize, 3, 7));

		return (lines);
	}

	private Line getBoxLine(Vector4f color, Vector3f boxorigin, Vector3f boxsize, int cornera, int cornerb) {

		Vector3f a = new Vector3f(corners[cornera]);
		Vector3f b = new Vector3f(corners[cornerb]);

		a.scale(boxsize);
		b.scale(boxsize);

		a.add(boxorigin);
		b.add(boxorigin);

		// Logger.get().log(Logger.Level.DEBUG, "added line: " + a, b);
		return (new Line(a, color, b, color));
	}

}
