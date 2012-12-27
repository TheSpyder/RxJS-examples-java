package common;

import java.awt.Container;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import hu.akarnokd.reactive4java.base.Func1;
import hu.akarnokd.reactive4java.query.ObservableBuilder;
import hu.akarnokd.reactive4java.swing.ObservableKeyListener;
import hu.akarnokd.reactive4java.swing.ObservableMouseListener;
import static hu.akarnokd.reactive4java.query.ObservableBuilder.from;

@SuppressWarnings("UnusedDeclaration")
public class FromEvent {

	//helper function to extract points from mouse events
	public static final Func1<MouseEvent, Point> eventPoint = new Func1<MouseEvent, Point>() {
		@Override
		public Point invoke(final MouseEvent mouseEvent) {
			return mouseEvent.getPoint();
		}
	};

	// add .select(debugger) on any of the events to print when it happens
	private static final Func1<MouseEvent, MouseEvent> debugger = new Func1<MouseEvent, MouseEvent>() {
		@Override
		public MouseEvent invoke(final MouseEvent mouseEvent) {
			System.out.println(mouseEvent);
			return mouseEvent;
		}
	};

	// Java separates moves from drags, combining them is generally more useful for Rx style
	public static ObservableBuilder<MouseEvent> mouseMovedAndDragged(final Container container) {
		return mouseMoved(container).merge(mouseDragged(container));
	}

	public static ObservableBuilder<MouseEvent> mouseMoved(final Container container) {
		return filterMouseEvents(container, MouseEvent.MOUSE_MOVED);
	}

	public static ObservableBuilder<MouseEvent> mouseDragged(final Container container) {
		return filterMouseEvents(container, MouseEvent.MOUSE_DRAGGED);
	}

	public static ObservableBuilder<MouseEvent> mouseReleased(final Container container) {
		return filterMouseEvents(container, MouseEvent.MOUSE_RELEASED);
	}

	public static ObservableBuilder<MouseEvent> mousePressed(final Container container) {
		return filterMouseEvents(container, MouseEvent.MOUSE_PRESSED);
	}

	public static ObservableBuilder<KeyEvent> keyReleased(final Container container) {
		return filterKeyEvents(container, KeyEvent.KEY_RELEASED);
	}

	private static ObservableBuilder<MouseEvent> filterMouseEvents(final Container container, final int event) {
		return from(ObservableMouseListener.register(container))
				.where(new Func1<MouseEvent, Boolean>() {
					@Override
					public Boolean invoke(MouseEvent mouseEvent) {
						return mouseEvent.getID() == event;
					}
				});
	}

	private static ObservableBuilder<KeyEvent> filterKeyEvents(final Container container, final int event) {
		return from(ObservableKeyListener.register(container))
			.where(new Func1<KeyEvent, Boolean>() {
				@Override
				public Boolean invoke(final KeyEvent keyEvent) {
					return keyEvent.getID() == event;
				}
			});
	}

	public static ObservableBuilder<MouseEvent> detectMouseDownOn(final Container child, final Container parent) {
		// Java doesn't bubble mouse events, so we have to listen on the parent and filter by clicks on the target
		return mousePressed(parent).where(new Func1<MouseEvent, Boolean>() {
			@Override
			public Boolean invoke(final MouseEvent mouseEvent) {
				Point point = SwingUtilities.convertMouseEvent(parent, mouseEvent, child).getPoint();
				return child.contains(point.x, point.y);
			}
		});
	}
}
