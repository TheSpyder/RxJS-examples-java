package reactive;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import hu.akarnokd.reactive4java.base.Func1;
import hu.akarnokd.reactive4java.query.ObservableBuilder;
import hu.akarnokd.reactive4java.reactive.Observable;
import hu.akarnokd.reactive4java.reactive.Observer;
import static common.FromEvent.mouseMoved;
import static common.FromEvent.mousePressed;
import static common.FromEvent.mouseReleased;

public class DragDrop {

	public static void main(String[] args) {
        final JFrame f = makeFrame();

		final JLabel text = new JLabel("<insert logo here>");
		text.setFont(text.getFont().deriveFont(24f));
		final Container contentPane = f.getContentPane();
		contentPane.add(text);

		final ObservableBuilder<MouseEvent> mouseReleased = mouseReleased(contentPane);
		final ObservableBuilder<MouseEvent> mouseMove = mouseMoved(contentPane);

		//Java doesn't bubble when you listen, so we have to listen on the parent and filter by clicks on the target
		final ObservableBuilder<MouseEvent> mousePressed = mousePressed(contentPane).where(new Func1<MouseEvent, Boolean>() {
			@Override
			public Boolean invoke(final MouseEvent mouseEvent) {
				Point point = SwingUtilities.convertMouseEvent(contentPane, mouseEvent, text).getPoint();
				return text.contains(point.x, point.y);
			}
		});

		final ObservableBuilder<Point> mouseDrag = mousePressed.selectMany(new Func1<MouseEvent, Observable<Point>>() {
			@Override
			public Observable<Point> invoke(final MouseEvent mouseEvent) {
				System.out.println("pressed");
				Point location = text.getLocation();
				Point downPoint = mouseEvent.getLocationOnScreen();
				final int startX = downPoint.x - location.x;
				final int startY = downPoint.y - location.y;

				return mouseMove.select(new Func1<MouseEvent, Point>() {
					@Override
					public Point invoke(final MouseEvent moveEvent) {
						System.out.println("moved");
						Point movePoint = moveEvent.getLocationOnScreen();
						return new Point(movePoint.x - startX, movePoint.y - startY);
					}
				}).takeUntil(mouseReleased);
			}
		});

		mouseDrag.register(new Observer<Point>() {
			@Override
			public void next(final Point value) {
				text.setLocation(value);
			}

			@Override
			public void error(final Throwable ex) {
			}

			@Override
			public void finish() {
			}
		});

        f.setVisible(true);
    }

	private static JFrame makeFrame() {
        final JFrame f = new JFrame();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		f.setSize(screenSize.width, screenSize.height / 2);
		f.getContentPane().setLayout(new FlowLayout());
        return f;
    }
}
