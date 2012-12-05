package reactive;

import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JLabel;

import hu.akarnokd.reactive4java.query.ObservableBuilder;
import hu.akarnokd.reactive4java.reactive.Observer;
import static common.FromEvent.mouseMoved;

public class TimeFliesLikeAnArrow {
    public static void main(String[] args) {
        final JFrame f = makeFrame();

		final ObservableBuilder<MouseEvent> mouseMove = mouseMoved(f.getContentPane());

		final String time = "TIME FLIES LIKE AN ARROW";
        for (int i = 0; i < time.length(); i++) {
			final int lol_closure_i = i;

			final JLabel character = new JLabel(time.substring(i, i + 1));
			f.getContentPane().add(character);

			//reactive java doesn't have subscribe
            mouseMove.delay(i * 100, TimeUnit.MILLISECONDS).register(new Observer<MouseEvent>() {
				@Override
				public void next(final MouseEvent e) {
					Point loc = e.getPoint();
					int xLoc = loc.x + lol_closure_i * 10 + 15;
					character.setLocation(xLoc, loc.y);
				}

				@Override
				public void error(final Throwable ex) {
				}

				@Override
				public void finish() {
				}
			});
        }
        f.setVisible(true);
    }

	private static JFrame makeFrame() {
        final JFrame f = new JFrame();
        f.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		f.getContentPane().setLayout(new FlowLayout());
        return f;
    }
}
