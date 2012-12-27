package reactive;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;

import hu.akarnokd.reactive4java.base.Effect1;
import hu.akarnokd.reactive4java.base.Func1;
import hu.akarnokd.reactive4java.query.ObservableBuilder;
import hu.akarnokd.reactive4java.reactive.Observable;
import static common.FromEvent.detectMouseDownOn;
import static common.FromEvent.mouseMovedAndDragged;
import static common.FromEvent.mouseReleased;

public class DragDrop {

	public static void main(String[] args) {
        final JFrame f = makeFrame();

		final JLabel text = new JLabel("<insert logo here>");
		text.setFont(text.getFont().deriveFont(36f));
		final Container contentPane = f.getContentPane();
		contentPane.add(text);

		final ObservableBuilder<MouseEvent> mouseReleased = mouseReleased(contentPane);

		final ObservableBuilder<MouseEvent> mouseMove = mouseMovedAndDragged(contentPane);
		final ObservableBuilder<MouseEvent> mousePressed = detectMouseDownOn(text, contentPane);


		final ObservableBuilder<Point> mouseDrag = mousePressed.selectMany(new Func1<MouseEvent, Observable<Point>>() {
			@Override
			public Observable<Point> invoke(final MouseEvent mouseEvent) {
				Point location = text.getLocation();
				Point downPoint = mouseEvent.getLocationOnScreen();
				final int startX = downPoint.x - location.x;
				final int startY = downPoint.y - location.y;

				return mouseMove.select(new Func1<MouseEvent, Point>() {
					@Override
					public Point invoke(final MouseEvent moveEvent) {
						Point movePoint = moveEvent.getLocationOnScreen();
						return new Point(movePoint.x - startX, movePoint.y - startY);
					}
				}).takeUntil(mouseReleased);
			}
		});

		mouseDrag.subscribe(new Effect1<Point>() {
			@Override
			public void invoke(final Point value) {
				text.setLocation(value);
			}
		});

        f.setVisible(true);
    }

	private static JFrame makeFrame() {
        final JFrame f = new JFrame();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		f.setSize(screenSize.width, screenSize.height);
		f.getContentPane().setLayout(new FlowLayout());
        return f;
    }
}
