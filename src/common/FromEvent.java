package common;

import java.awt.Container;
import java.awt.event.MouseEvent;

import hu.akarnokd.reactive4java.base.Func1;
import hu.akarnokd.reactive4java.query.ObservableBuilder;
import hu.akarnokd.reactive4java.swing.ObservableMouseListener;
import static hu.akarnokd.reactive4java.query.ObservableBuilder.from;

@SuppressWarnings("UnusedDeclaration")
public class FromEvent {

	// add .select(debugger) on any of the events to print when it happens
	private static final Func1<MouseEvent,MouseEvent> debugger = new Func1<MouseEvent, MouseEvent>() {
		@Override
		public MouseEvent invoke(final MouseEvent mouseEvent) {
			System.out.println(mouseEvent);
			return mouseEvent;
		}
	};

	public static ObservableBuilder<MouseEvent> mouseMoved(final Container container) {
		return filterMouseEvents(container, MouseEvent.MOUSE_MOVED);
	}

	public static ObservableBuilder<MouseEvent> mouseReleased(final Container container) {
		return filterMouseEvents(container, MouseEvent.MOUSE_RELEASED);
	}

	public static ObservableBuilder<MouseEvent> mousePressed(final Container container) {
		return filterMouseEvents(container, MouseEvent.MOUSE_PRESSED);
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
}
