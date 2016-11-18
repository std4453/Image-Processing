import processing.core.PApplet;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.util.Arrays;

/**
 * Implementation of radio widget
 */
public class RadioWidget implements Widget {
	// View-related fields
	private Point position;
	/**
	 * which radio button is hovered / pressed
	 */
	private int mouseOver = -1, mouseDown = -1;
	/**
	 * whether each radio is enabled
	 */
	private boolean[] enabled;

	// Model-related fields
	private int choice;
	private String[] labels;
	private RadioListener listener;
	private int length;

	private UIManager ui;

	public RadioWidget(UIManager ui, String[] labels, RadioListener listener,
					   int initial) {
		this.ui = ui;

		this.labels = labels;
		this.listener = listener;
		if (initial < 0 || initial >= labels.length)
			initial = 0;
		this.choice = initial;
		this.length = this.labels.length;
		this.enabled = new boolean[this.length];
		Arrays.fill(this.enabled, true);
	}

	public void draw() {
		PApplet applet = this.ui.applet;

		int x = this.position.x, y = this.position.y;

		for (int i = 0; i < this.length; ++i) {
			// draw circle
			applet.noFill();
			applet.stroke(this.enabled[i] ? 0 : 100);
			applet.ellipse(x + 7, y + 7, 14, 14);

			if (this.choice == i) {    // if chosen
				// draw center circle
				applet.noStroke();
				applet.fill(this.enabled[i] ? 0 : 100);
				applet.ellipse(x + 7.5f, y + 7.5f, 6, 6);
			}

			// draw overlay
			if (!this.enabled[i]) {    // if disabled
				applet.noStroke();
				applet.fill(0, 30);
				applet.ellipse(x + 7, y + 7, 14, 14);
			} else if (this.mouseOver == i || this.mouseDown == i) { // hovered / pressed
				applet.noStroke();
				applet.fill(0, this.mouseDown == i ? 100 : 50);
				applet.ellipse(x + 7, y + 7, 14, 14);
			}

			// draw label text
			applet.noStroke();
			applet.fill(this.enabled[i] ? 0 : 100);
			applet.textAlign(PApplet.LEFT, PApplet.BOTTOM);
			applet.textSize(12);
			applet.text(this.labels[i], x + 18, y + 13);

			// increase x
			x += 14 + 4 + applet.textWidth(this.labels[i]) + 10;
		}
	}

	private void onClick(int i) {
		if (!this.enabled[i])
			return;
		if (this.choice == i)
			return;

		this.choice = i;
		if (this.listener != null)
			this.listener.onChange(this.choice);
		this.ui.requestRedraw();
	}

	/**
	 * @param params
	 * 		params[0] = index of item to be enabled / disabled
	 */
	public void setEnabled(boolean enabled, Object... params) {
		if (params.length == 0 || !(params[0] instanceof Integer))
			return;
		int index = (Integer) params[0];
		if (index >= 0 && index < this.length)
			this.enabled[index] = enabled;
	}

	public Dimension getSize() {
		PApplet applet = this.ui.applet;
		applet.textSize(12);
		int textWidth = 18 * this.length + 10 * (this.length - 1);
		for (int i = 0; i < this.length; ++i)
			textWidth += applet.textWidth(this.labels[i]);
		return new Dimension(textWidth, 14);
	}

	public void setLocation(Point position) {
		// update items layout

		this.position = new Point(position);
		PApplet applet = this.ui.applet;
		applet.textSize(12);
		int x = position.x;
		for (int i = 0; i < this.length; ++i) {
			int width = 18 + (int) applet.textWidth(this.labels[i]);
			final int ii = i;

			// listen pointer event
			this.ui.listen(new Rectangle(x, position.y, width, 14), new MouseAdapter() {
				@Override
				public void mouseEntered(java.awt.event.MouseEvent unused) {
					RadioWidget.this.mouseOver = ii;
					RadioWidget.this.ui.requestRedrawWidgets();
				}

				@Override
				public void mouseExited(java.awt.event.MouseEvent unused) {
					if (RadioWidget.this.mouseOver == ii)
						RadioWidget.this.mouseOver = -1;
					if (RadioWidget.this.mouseDown == ii)
						RadioWidget.this.mouseDown = -1;
					RadioWidget.this.ui.requestRedrawWidgets();
				}

				@Override
				public void mousePressed(java.awt.event.MouseEvent unused) {
					RadioWidget.this.mouseDown = ii;
					RadioWidget.this.ui.requestRedrawWidgets();
				}

				@Override
				public void mouseReleased(java.awt.event.MouseEvent event) {
					if (RadioWidget.this.mouseDown == ii && event.getButton() == Mouse.LEFT_BUTTON) {
						RadioWidget.this.onClick(ii);
					}
					if (RadioWidget.this.mouseDown == ii)
						RadioWidget.this.mouseDown = -1;
					RadioWidget.this.ui.requestRedrawWidgets();
				}
			});
			x += width + 10;
		}
	}
}