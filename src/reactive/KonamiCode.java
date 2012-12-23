package reactive;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

import hu.akarnokd.reactive4java.base.Func1;
import hu.akarnokd.reactive4java.base.Functions;
import hu.akarnokd.reactive4java.query.ObservableBuilder;
import hu.akarnokd.reactive4java.reactive.Observable;
import hu.akarnokd.reactive4java.reactive.Observer;
import static common.FromEvent.keyReleased;
import static hu.akarnokd.reactive4java.query.ObservableBuilder.from;

public class KonamiCode {

	private static final ObservableBuilder<Integer> codes = from(
		38, // up
		38, // up
		40, // down
		40, // down
		37, // left
		39, // right
		37, // left
		39, // right
		66, // b
		65  // a
	);

	public static void main(String[] args) {
		final JFrame f = makeFrame();

		keyReleased(f)
			.select(new Func1<KeyEvent, Integer>() {
				@Override
				public Integer invoke(final KeyEvent keyEvent) {
					return keyEvent.getKeyCode();
				}
			})
			.window(10)
			.selectMany(new Func1<Observable<Integer>, Observable<Boolean>>() {
				@Override
				public Observable<Boolean> invoke(final Observable<Integer> keys) {
					return codes.sequenceEqual(keys);
				}
			})
			.where(Functions.<Boolean>identity())
			.register(new Observer<Boolean>() {
				@Override
				public void next(final Boolean value) {
					System.out.println("CODE");
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
