package com.grillecube.engine.renderer.world.lines;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import com.grillecube.engine.Logger;
import com.grillecube.engine.Taskable;
import com.grillecube.engine.VoxelEngine;
import com.grillecube.engine.maths.Vector3f;
import com.grillecube.engine.maths.Vector4f;
import com.grillecube.engine.opengl.GLH;
import com.grillecube.engine.opengl.object.GLVertexArray;
import com.grillecube.engine.opengl.object.GLVertexBuffer;
import com.grillecube.engine.renderer.MainRenderer;
import com.grillecube.engine.renderer.camera.CameraProjectiveWorld;
import com.grillecube.engine.renderer.model.BoundingBox;
import com.grillecube.engine.renderer.world.RendererWorld;
import com.grillecube.engine.renderer.world.ShadowCamera;
import com.grillecube.engine.world.World;

public class LineRenderer extends RendererWorld {

	public static final int MAX_LINE_NUMBER = 1000;
	private static final int FLOAT_PER_LINE_VERTEX = 3 + 4;

	/** the lines */
	private ArrayList<Line> _lines;

	/** the vbo which contains every lines instances data */
	private GLVertexArray _vao;
	private GLVertexBuffer _vbo;

	/** the rendering program */
	private ProgramLines _program;

	private boolean _up_to_date;

	private FloatBuffer _buffer;

	public LineRenderer(MainRenderer renderer) {
		super(renderer);
	}

	public Line addLine(Line line) {
		int size = this._lines.size();
		if (size >= MAX_LINE_NUMBER) {
			Logger.get().log(Logger.Level.WARNING, "already rendering too much line: " + size + "/" + MAX_LINE_NUMBER);
			return (null);
		}

		if (this._lines.add(line)) {
			this.requestUpdate();
			return (line);
		}
		return (null);
	}

	public void removeLine(Line line) {
		this._lines.remove(this._lines.indexOf(line));
	}

	public void removeLine(int index) {
		if (index < 0 || index >= this._lines.size()) {
			Logger.get().log(Logger.Level.DEBUG,
					"tried to remove a line that wasnt pushed to the renderer: " + index + "/" + this._lines.size());
			return;
		}
		this._lines.remove(index);
		this._up_to_date = false;
	}

	@Override
	public void getTasks(VoxelEngine engine, ArrayList<VoxelEngine.Callable<Taskable>> tasks, World world,
			CameraProjectiveWorld camera) {
		// TODO : fix concurrent modification issues
		// tasks.add(new Callable<Taskable>() {
		//
		// @Override
		// public Taskable call() throws Exception {
		// // if buffer is already up to date
		// if (isUpToDate()) {
		// return (LineRenderer.this);
		// }
		//
		// // else update the buffer
		// updateVertexBuffer();
		// setUpToDate();
		//
		// return (LineRenderer.this);
		// }
		// });
	}

	@Override
	public void preRender() {

		if (isUpToDate()) {
			return;
		}

		// else update the buffer
		this.updateFloatBuffer();
		this.updateVertexBuffer();
		this.setUpToDate();
	}

	@Override
	public void postRender() {
		this.removeAllLines();
	}

	public void removeAllLines() {
		this._lines.clear();
		this.requestUpdate();
	}

	private void updateVertexBuffer() {

		// update vertex buffer object
		this._vbo.bind(GL15.GL_ARRAY_BUFFER);
		this._vbo.bufferData(GL15.GL_ARRAY_BUFFER, this._buffer, GL15.GL_STATIC_DRAW);
	}

	public void updateFloatBuffer() {

		// this.getParent().getWorldRenderer().getShadowBox().box.setColor(1.0f,
		// 0.0f, 0.0f, 1.0f);
		// this.addBox(this.getParent().getWorldRenderer().getShadowBox().box);

		int size_required = this._lines.size() * 2 * LineRenderer.FLOAT_PER_LINE_VERTEX;
		if (this._buffer != null && this._buffer.capacity() == size_required) {
			this._buffer.rewind();
		} else {
			this._buffer = BufferUtils.createFloatBuffer(size_required);
		}

		for (Line line : this._lines) {
			this._buffer.put(line.posa.x);
			this._buffer.put(line.posa.y);
			this._buffer.put(line.posa.z);
			this._buffer.put(line.colora.x);
			this._buffer.put(line.colora.y);
			this._buffer.put(line.colora.z);
			this._buffer.put(line.colora.w);

			this._buffer.put(line.posb.x);
			this._buffer.put(line.posb.y);
			this._buffer.put(line.posb.z);
			this._buffer.put(line.colorb.x);
			this._buffer.put(line.colorb.y);
			this._buffer.put(line.colorb.z);
			this._buffer.put(line.colorb.w);
		}
		this._buffer.flip();
	}

	public void render(CameraProjectiveWorld camera) {

		GL11.glEnable(GL11.GL_DEPTH_TEST);

		this._program.useStart();
		{
			this._program.loadGlobalUniforms(camera);
			this._vao.bind();
			this._vao.draw(GL11.GL_LINES, 0, this._vbo.getFloatCount() / FLOAT_PER_LINE_VERTEX);
		}
		this._program.useStop();

		GL11.glDisable(GL11.GL_DEPTH_TEST);

	}

	@Override
	public void render() {
		this.render(super.getCamera());
	}

	@Override
	public void renderShadow(ShadowCamera shadow_camera) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onWorldSet(World world) {
		// Vector4f red = new Vector4f(1, 0, 0, 1);
		// Vector4f green = new Vector4f(0, 1, 0, 1);
		// this.addLine(new Line(new Vector3f(-100, 0, -100), red, new
		// Vector3f(-100, 0, 100), green));
		// this.addLine(new Line(new Vector3f(-100, 0, 100), red, new
		// Vector3f(100, 0, 100), green));
		// this.addLine(new Line(new Vector3f(100, 0, 100), red, new
		// Vector3f(100, 0, -100), green));
		// this.addLine(new Line(new Vector3f(100, 0, -100), red, new
		// Vector3f(-100, 0, -100), green));
	}

	@Override
	public void onWorldUnset(World world) {
	}

	@Override
	public void initialize() {
		this._program = new ProgramLines();
		this._lines = new ArrayList<Line>();

		this._vao = GLH.glhGenVAO();
		this._vbo = GLH.glhGenVBO();
		this._vao.bind();
		this._vbo.bind(GL15.GL_ARRAY_BUFFER);
		this._vao.setAttribute(0, 3, GL11.GL_FLOAT, false, FLOAT_PER_LINE_VERTEX * 4, 0); // pos
		this._vao.setAttribute(1, 4, GL11.GL_FLOAT, false, FLOAT_PER_LINE_VERTEX * 4, 3 * 4); // color
		this._vao.enableAttribute(0);
		this._vao.enableAttribute(1);
		this._vbo.unbind(GL15.GL_ARRAY_BUFFER);
		this._vao.unbind();
	}

	@Override
	public void deinitialize() {
		this._vao.delete();
		this._vbo.delete();
	}

	public void requestUpdate() {
		this._up_to_date = false;
	}

	public void setUpToDate() {
		this._up_to_date = true;
	}

	public boolean isUpToDate() {
		return (this._up_to_date);
	}

	// bounding box corners
	static Vector4f[] corners = new Vector4f[8];

	static {
		corners[0] = new Vector4f(0, 0, 0, 1);
		corners[1] = new Vector4f(1, 0, 0, 1);
		corners[2] = new Vector4f(1, 0, 1, 1);
		corners[3] = new Vector4f(0, 0, 1, 1);
		corners[4] = new Vector4f(0, 1, 0, 1);
		corners[5] = new Vector4f(1, 1, 0, 1);
		corners[6] = new Vector4f(1, 1, 1, 1);
		corners[7] = new Vector4f(0, 1, 1, 1);
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

	public void removeLines(int[] indices) {

		if (indices == null || indices.length == 0) {
			return;
		}

		// sort the indices
		Arrays.sort(indices);

		// remove them
		int i;
		for (i = indices.length - 1; i >= 0; i--) {
			this.removeLine(indices[i]);
		}
	}

	public void removeLines(Line[] lines) {
		if (lines == null || lines.length == 0) {
			return;
		}

		for (Line line : lines) {
			this.removeLine(line);
		}
	}
}
