package com.grillecube.client.renderer.lines;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.GLVertexArray;
import com.grillecube.client.opengl.object.GLVertexBuffer;
import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.renderer.Renderer;
import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.common.Taskable;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.VoxelEngine.Callable;

public class LineRenderer extends Renderer {

	public static final int MAX_LINE_PER_DRAW = 1000;
	private static final int FLOAT_PER_LINE_VERTEX = 3 + 4;
	private static final int BYTES_PER_LINES = FLOAT_PER_LINE_VERTEX * 4;

	/** the vbo which contains every lines instances data */
	private GLVertexArray vao;
	private GLVertexBuffer vbo;

	/** the rendering program */
	private ProgramLines program;

	/** the bytebuffer holding the data to be send to the gpu */
	private ByteBuffer buffer;

	public LineRenderer(MainRenderer renderer) {
		super(renderer);
	}

	@Override
	public void initialize() {
		this.program = new ProgramLines();

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

	private final void updateFloatBuffer(ArrayList<Line> lines) {
		// regenerate buffer
		int size_required = lines.size() * 2 * LineRenderer.FLOAT_PER_LINE_VERTEX * 4;
		if (this.buffer != null && this.buffer.capacity() == size_required) {
			this.buffer.rewind();
		} else {
			this.buffer = BufferUtils.createByteBuffer(size_required);
		}

		for (Line line : lines) {
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

		// update vertex buffer object
		this.vbo.bind(GL15.GL_ARRAY_BUFFER);
		this.vbo.bufferData(GL15.GL_ARRAY_BUFFER, this.buffer, GL15.GL_STATIC_DRAW);
	}

	public void render(CameraProjective camera, ArrayList<Line> lines) {

		// else update the buffer
		this.updateFloatBuffer(lines);

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
	public void getTasks(VoxelEngine engine, ArrayList<Callable<Taskable>> tasks) {
	}
}
