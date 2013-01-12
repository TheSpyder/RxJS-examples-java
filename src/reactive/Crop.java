package reactive;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import common.FakeCanvas;
import hu.akarnokd.reactive4java.query.ObservableBuilder;
import static common.FromEvent.mouseMovedAndDragged;

public class Crop {

	public static void main(String[] args) throws IOException {
		final JFrame f = makeFrame();

		//loadImage is so much simpler than in JS :)
		BufferedImage image = ImageIO.read(Crop.class.getResource("/images/leaf twirl.jpg"));
		Dimension imageSize = new Dimension(image.getWidth(), image.getHeight());
		f.setSize(imageSize);

		final FakeCanvas buffer = new FakeCanvas(imageSize);
		f.getContentPane().add(buffer, BorderLayout.CENTER);

		buffer.getContext().drawImage(image, 0, 0, null);

		//our "overlay" is just another fake canvas inside the buffer
		final FakeCanvas overlay = new FakeCanvas(imageSize);
		buffer.setLayout(new BorderLayout());
		buffer.add(overlay, BorderLayout.CENTER);


		ObservableBuilder<MouseEvent> mouseMove = mouseMovedAndDragged(overlay);


		f.setVisible(true);
    }

	private static class BoundingBox {
		public int x;
		public int y;
		public int x2;
		public int y2;

		private BoundingBox(final int x, final int y, final int x2, final int y2) {
			this.x = x;
			this.y = y;
			this.x2 = x2;
			this.y2 = y2;
		}

		@Override
		public String toString() {
			return "BoundingBox{" +
				"x=" + x +
				", y=" + y +
				", x2=" + x2 +
				", y2=" + y2 +
				'}';
		}
	}

	private static JFrame makeFrame() {
		final JFrame f = new JFrame();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		f.setSize(screenSize.width, screenSize.height);
		f.getContentPane().setLayout(new BorderLayout());
		return f;
	}
}
