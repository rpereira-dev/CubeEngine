package com.grillecube.client.renderer.world.lines;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.GLVertexArray;
import com.grillecube.client.opengl.object.GLVertexBuffer;
import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.renderer.camera.CameraProjectiveWorld;
import com.grillecube.client.renderer.world.RendererWorld;
import com.grillecube.common.Logger;
import com.grillecube.common.Taskable;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.maths.BoundingBox;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector4f;
import com.grillecube.common.world.World;

public class LineRenderer extends RendererWorld {

	public static final int MAX_LINE_NUMBER = 1000;
	private static final int FLOAT_PER_LINE_VERTEX = 3 + 4;
	private static final int BYTES_PER_LINES = FLOAT_PER_LINE_VERTEX * 4;

	/** the lines */
	private ArrayList<Line> lines;

	/** the vbo which contains every lines instances data */
	private GLVertexArray vao;
	private GLVertexBuffer vbo;

	/** the rendering program */
	private ProgramLines program;

	private boolean upToDate;

	private ByteBuffer buffer;

	public LineRenderer(MainRenderer renderer) {
		super(renderer);
	}

	public Line addLine(Line line) {
		int size = this.lines.size();
		if (size >= MAX_LINE_NUMBER) {
			Logger.get().log(Logger.Level.WARNING, "already rendering too much line: " + size + "/" + MAX_LINE_NUMBER);
			return (null);
		}

		if (this.lines.add(line)) {
			this.requestUpdate();
			return (line);
		}
		return (null);
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

		if (this.isUpToDate()) {
			return;
		}
		this.setUpToDate();

		// else update the buffer
		this.updateFloatBuffer();
		this.updateVertexBuffer();
	}

	@Override
	public void postRender() {
		this.removeAllLines();
	}

	public void removeAllLines() {
		this.lines.clear();
		this.requestUpdate();
	}

	private void updateVertexBuffer() {

		// update vertex buffer object
		this.vbo.bind(GL15.GL_ARRAY_BUFFER);
		this.vbo.bufferData(GL15.GL_ARRAY_BUFFER, this.buffer, GL15.GL_STATIC_DRAW);
	}

	public void updateFloatBuffer() {

		// this.getParent().getWorldRenderer().getShadowBox().box.setColor(1.0f,
		// 0.0f, 0.0f, 1.0f);
		// this.addBox(this.getParent().getWorldRenderer().getShadowBox().box);

		int size_required = this.lines.size() * 2 * LineRenderer.FLOAT_PER_LINE_VERTEX * 4;
		if (this.buffer != null && this.buffer.capacity() == size_required) {
			this.buffer.rewind();
		} else {
			this.buffer = BufferUtils.createByteBuffer(size_required);
		}

		for (Line line : this.lines) {
			this.buffer.putFloat(line.posa.x);
			this.buffer.putFloat(line.posa.y);
			this.buffer.putFloat(line.posa.z);
			this.buffer.putFloat(line.colora.x);
			this.buffer.putFloat(line.colora.y);
			this.buffer.putFloat(line.colora.z);
			this.buffer.putFloat(line.colora.w);

			this.buffer.putFloat(line.posb.x);
			this.buffer.putFloat(line.posb.y);
			this.buffer.putFloat(line.posb.z);
			this.buffer.putFloat(line.colorb.x);
			this.buffer.putFloat(line.colorb.y);
			this.buffer.putFloat(line.colorb.z);
			this.buffer.putFloat(line.colorb.w);
		}
		this.buffer.flip();
	}

	public void render(CameraProjectiveWorld camera) {

		GL11.glEnable(GL11.GL_DEPTH_TEST);

		this.program.useStart();
		{
			this.program.loadGlobalUniforms(camera);
			this.vao.bind();
			this.vao.draw(GL11.GL_LINES, 0, this.vbo.getByteCount() / BYTES_PER_LINES);
		}
		this.program.useStop();

		GL11.glDisable(GL11.GL_DEPTH_TEST);

	}

	@Override
	public void render() {
		this.render(super.getCamera());
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
		this.program = new ProgramLines();
		this.lines = new ArrayList<Line>();

		this.vao = GLH.glhGenVAO();
		this.vbo = GLH.glhGenVBO();
		this.vao.bind();
		this.vbo.bind(GL15.GL_ARRAY_BUFFER);
		this.vao.setAttribute(0, 3, GL11.GL_FLOAT, false, FLOAT_PER_LINE_VERTEX * 4, 0); // pos
		this.vao.setAttribute(1, 4, GL11.GL_FLOAT, false, FLOAT_PER_LINE_VERTEX * 4, 3 * 4); // color
		this.vao.enableAttribute(0);
		this.vao.enableAttribute(1);
		this.vbo.unbind(GL15.GL_ARRAY_BUFFER);
		this.vao.unbind();
	}

	@Override
	public void deinitialize() {
		this.vao.delete();
		this.vbo.delete();
	}

	public void requestUpdate() {
		this.upToDate = false;
	}

	public void setUpToDate() {
		this.upToDate = true;
	}

	public boolean isUpToDate() {
		return (this.upToDate);
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
