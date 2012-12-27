package common;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class FakeCanvas extends JPanel {

	final BufferedImage canvas;

	final Graphics2D ctx;

	//instead of a JS canvas, we're using a BufferedImage painted onto a JPanel
	public FakeCanvas(final Dimension size) {
		canvas = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
		ctx = canvas.createGraphics();
	}

	public Graphics2D getContext() {
		return ctx;
	}

	@Override
	public void paint(final Graphics g) {
		g.drawImage(canvas, 0, 0, this);
	}
}
