package com.grillecube.client.renderer.model.editor.mesher;

import java.util.ArrayList;
import java.util.Comparator;

class Node {
	int x, y, w, h;
	Node down, right;
	public boolean used;

	Node(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
}

/**
 * http://codeincomplete.com/posts/bin-packing/
 * http://mimoza.marmara.edu.tr/~msakalli/cse706_12/SkienaTheAlgorithmDesignManual.pdf
 * http://www.or.deis.unibo.it/kp/Chapter8.pdf
 */
public class ModelSkinPacker {

	private static Node root;

	public static final void fit(ArrayList<ModelPlane> planes) {
		if (planes == null || planes.size() == 0) {
			return;
		}

		// map vertex to texture
		planes.sort(new Comparator<ModelPlane>() {
			@Override
			public int compare(ModelPlane o1, ModelPlane o2) {
				return (o2.getArea() - o1.getArea());
			}
		});

		ModelPlane rootPlane = planes.get(0);
		root = new Node(0, 0, rootPlane.getTextureWidth(), rootPlane.getTextureHeight());

		for (ModelPlane plane : planes) {
			int w = plane.getTextureWidth();
			int h = plane.getTextureHeight();
			Node node = findNode(root, w, h);
			Node fit;
			if (node != null) {
				fit = splitNode(node, w, h);
			} else {
				fit = growNode(w, h);
			}
			plane.setUV(fit.x, fit.y);
		}
	}

	private static final Node splitNode(Node node, int w, int h) {
		node.used = true;
		node.down = new Node(node.x, node.y + h, node.w, node.h - h);
		node.right = new Node(node.x + w, node.y, node.w - w, h);
		return (node);
	}

	private static final Node growNode(int w, int h) {
		boolean canGrowDown = w < root.w;
		boolean canGrowRight = h < root.h;
		boolean shouldGrowRight = canGrowRight && (root.h >= (root.w + w));
		boolean shouldGrowDown = canGrowDown && (root.w >= (root.h + h));
		if (shouldGrowRight) {
			return (growRight(w, h));
		} else if (shouldGrowDown) {
			return (growDown(w, h));
		} else if (canGrowRight) {
			return (growRight(w, h));
		} else {
			return (growDown(w, h));
		}
	}

	private static final Node growRight(int w, int h) {
		Node child = new Node(0, 0, root.w + w, root.h);
		child.used = true;
		child.down = root;
		child.right = new Node(root.w, 0, w, root.h);
		root = child;

		Node node = findNode(root, w, h);
		if (node != null) {
			return (splitNode(node, w, h));
		} else {
			return (null);
		}
	}

	private static final Node growDown(int w, int h) {

		Node child = new Node(0, 0, root.w, root.h + h);
		child.used = true;
		child.right = root;
		child.down = new Node(0, root.h, root.w, h);
		root = child;

		Node node = findNode(root, w, h);
		if (node != null) {
			return (splitNode(node, w, h));
		} else {
			return (null);
		}
	}

	public static final Node findNode(Node root, int w, int h) {
		if (root.used) {
			Node node = findNode(root.right, w, h);
			return ((node != null) ? node : findNode(root.down, w, h));
		} else if ((w <= root.w) && (h <= root.h)) {
			return (root);
		} else {
			return (null);
		}
	}
}