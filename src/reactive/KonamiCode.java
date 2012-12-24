package reactive;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JLabel;

import hu.akarnokd.reactive4java.base.Effect1;
import hu.akarnokd.reactive4java.base.Func1;
import hu.akarnokd.reactive4java.base.Functions;
import hu.akarnokd.reactive4java.query.ObservableBuilder;
import hu.akarnokd.reactive4java.reactive.DefaultObservable;
import hu.akarnokd.reactive4java.reactive.Observable;
import static common.FromEvent.keyReleased;
import static hu.akarnokd.reactive4java.query.ObservableBuilder.from;
import static hu.akarnokd.reactive4java.query.ObservableBuilder.tick;

public class KonamiCode {

	private static final Integer[] codes = {
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
	};

	private static final ObservableBuilder<Integer> konami = from(codes);

	public static void main(String[] args) {
		final JFrame f = makeFrame();

		keyReleased(f)
			// get the key code
			.select(new Func1<KeyEvent, Integer>() {
				@Override
				public Integer invoke(final KeyEvent keyEvent) {
					return keyEvent.getKeyCode();
				}
			})
			// get the last 10 keys
			.window(codes.length)
			// compare to known konmai code sequence
			.selectMany(new Func1<Observable<Integer>, Observable<Boolean>>() {
				@Override
				public Observable<Boolean> invoke(final Observable<Integer> keys) {
					return konami.sequenceEqual(keys);
				}
			})
			// where we match
			.where(Functions.<Boolean>identity())
			// print the result
			.subscribe(new Effect1<Boolean>() {
				@Override
				public void invoke(final Boolean _) {
					showCode(f.getContentPane());
				}
			});

		f.setVisible(true);
	}

	private static void showCode(final Container contentPane) {
		final JLabel text = new JLabel("KONAMI!");
		text.setFont(text.getFont().deriveFont(36f));
		contentPane.add(text);
		contentPane.validate();
		tick(0, 2000, 1, TimeUnit.MILLISECONDS).register(new DefaultObservable<Long>() {
			@Override
			public void finish() {
				contentPane.remove(text);
				contentPane.revalidate();
				contentPane.repaint();
			}

			@Override
			public void next(final Long value) {
				//TODO: Fade out. Is that a jQuery thing or Rx jQuery extension?
			}
		});
	}

	private static JFrame makeFrame() {
		final JFrame f = new JFrame();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		f.setSize(screenSize.width, screenSize.height);
		f.getContentPane().setLayout(new FlowLayout());
		return f;
	}
}
