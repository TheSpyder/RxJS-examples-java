package reactive;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import hu.akarnokd.reactive4java.base.Effect1;
import hu.akarnokd.reactive4java.base.Func1;
import hu.akarnokd.reactive4java.base.Pair;
import hu.akarnokd.reactive4java.query.ObservableBuilder;
import hu.akarnokd.reactive4java.reactive.Observable;
import static common.FromEvent.mouseDragged;
import static common.FromEvent.mouseMoved;
import static common.FromEvent.mousePressed;
import static common.FromEvent.mouseReleased;
import static hu.akarnokd.reactive4java.query.ObservableBuilder.from;
import static hu.akarnokd.reactive4java.reactive.Reactive.switchToNext;

public class CanvasPaint {

	private static Pair<Point, Point> getOffset(final List<MouseEvent> mouseEvents, final Container contentPane, final Container panel) {
		//just a little bit messier than the JS version
		MouseEvent first = SwingUtilities.convertMouseEvent(contentPane, mouseEvents.get(0), panel);
		MouseEvent second = SwingUtilities.convertMouseEvent(contentPane, mouseEvents.get(1), panel);
		return new Pair<Point, Point>(first.getPoint(), second.getPoint());
	}

	public static void main(String[] args) {
        final JFrame f = makeFrame();
		final Container contentPane = f.getContentPane();

		//instead of a JS canvas, we're using a BufferedImage painted onto a JPanel
		final Dimension size = f.getSize();
		final BufferedImage canvas = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D ctx = canvas.createGraphics();
		ctx.setColor(Color.black);
		ctx.setStroke(new BasicStroke(5));

		final Container panel = addImageToFrame(canvas, contentPane);

		// Java separates moves from drags
		ObservableBuilder<MouseEvent> mouseMove = mouseMoved(panel).merge(mouseDragged(panel));

		// Calculate difference between two mouse moves
		final ObservableBuilder<Pair<Point, Point>> mouseDiffs = mouseMove.bufferWithCount(2, 1).select(new Func1<List<MouseEvent>, Pair<Point, Point>>() {
			@Override
			public Pair<Point, Point> invoke(final List<MouseEvent> mouseEvents) {
				return getOffset(mouseEvents, contentPane, panel);
			}
		});

		// Button clicks == merge together both mouse up and mouse down
		final ObservableBuilder<Boolean> mouseButton = mousePressed(panel).select(new Func1<MouseEvent, Boolean>() {
			@Override
			public Boolean invoke(final MouseEvent _) {
				return true;
			}
		})
			.merge(mouseReleased(panel).select(new Func1<MouseEvent, Boolean>() {
			@Override
			public Boolean invoke(final MouseEvent _) {
					return false;
			}
		}));

		// Paint if the mouse is down
		final ObservableBuilder<Pair<Point, Point>> paint = from(switchToNext(mouseButton.select(new Func1<Boolean, Observable<Pair<Point, Point>>>() {
			@Override
			public Observable<Pair<Point, Point>> invoke(final Boolean down) {
				return down
					   ? mouseDiffs
					   : mouseDiffs.take(0);
			}
		})));
		// instead of switchLatest, we have to do from(switchToNext()). Not sure if this is Java's fault or the library

		// Update the canvas
		paint.subscribe(new Effect1<Pair<Point, Point>>() {
			@Override
			public void invoke(final Pair<Point, Point> points) {
				ctx.drawLine(points.first.x, points.first.y, points.second.x, points.second.y);
				contentPane.repaint();
			}
		});

        f.setVisible(true);
    }

	private static Container addImageToFrame(final BufferedImage canvas, final Container contentPane) {
		contentPane.setLayout(new BorderLayout());
		contentPane.add(new JPanel() {
			@Override
			public void paint(final Graphics g) {
				g.drawImage(canvas, 0, 0, this);
			}
		}, BorderLayout.CENTER);
		return contentPane;
	}

	private static JFrame makeFrame() {
        final JFrame f = new JFrame();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		f.setSize(screenSize.width, screenSize.height);
		f.getContentPane().setLayout(new FlowLayout());
        return f;
    }
}
