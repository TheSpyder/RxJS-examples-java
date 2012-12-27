package reactive;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JFrame;

import common.FakeCanvas;
import hu.akarnokd.reactive4java.base.Effect1;
import hu.akarnokd.reactive4java.base.Func1;
import hu.akarnokd.reactive4java.base.Pair;
import hu.akarnokd.reactive4java.query.ObservableBuilder;
import hu.akarnokd.reactive4java.reactive.Observable;
import static common.FromEvent.mouseMovedAndDragged;
import static common.FromEvent.mousePressed;
import static common.FromEvent.mouseReleased;
import static hu.akarnokd.reactive4java.query.ObservableBuilder.from;
import static hu.akarnokd.reactive4java.reactive.Reactive.switchToNext;

public class CanvasPaint {

	private static Pair<Point, Point> getOffset(final List<MouseEvent> mouseEvents) {
		return new Pair<Point, Point>(mouseEvents.get(0).getPoint(), mouseEvents.get(1).getPoint());
	}

	public static void main(String[] args) {
        final JFrame f = makeFrame();

		final FakeCanvas canvas = new FakeCanvas(f.getSize());
		f.getContentPane().add(canvas, BorderLayout.CENTER);
		final Graphics2D ctx = canvas.getContext();

		ctx.setColor(Color.black);
		ctx.setStroke(new BasicStroke(5));

		// Java separates moves from drags
		ObservableBuilder<MouseEvent> mouseMove = mouseMovedAndDragged(canvas);

		// Calculate difference between two mouse moves
		final ObservableBuilder<Pair<Point, Point>> mouseDiffs = mouseMove.bufferWithCount(2, 1).select(new Func1<List<MouseEvent>, Pair<Point, Point>>() {
			@Override
			public Pair<Point, Point> invoke(final List<MouseEvent> mouseEvents) {
				return getOffset(mouseEvents);
			}
		});

		// Button clicks == merge together both mouse up and mouse down
		final ObservableBuilder<Boolean> mouseButton = mousePressed(canvas).select(new Func1<MouseEvent, Boolean>() {
			@Override
			public Boolean invoke(final MouseEvent _) {
				return true;
			}
		})
			.merge(mouseReleased(canvas).select(new Func1<MouseEvent, Boolean>() {
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
				f.getContentPane().repaint();
			}
		});

        f.setVisible(true);
    }

	private static JFrame makeFrame() {
        final JFrame f = new JFrame();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		f.setSize(screenSize.width, screenSize.height);
		f.getContentPane().setLayout(new BorderLayout());
		f.setResizable(false);
        return f;
    }
}
