import processing.core.PApplet;

import java.awt.*;
import java.awt.event.MouseAdapter;

public class CheckWidget extends MouseAdapter implements Widget {
	// View-related fields
	private Point position;
	private boolean mouseOver, mouseDown;
	private boolean enabled = true;

	// Model-related fields
	private boolean checked;
	private String label;
	private CheckListener listener;

	private UIManager ui;

	public CheckWidget(UIManager ui, String label, CheckListener listener,
					   boolean initial) {
		this.ui = ui;

		this.label = label;
		this.listener = listener;
		this.checked = initial;
	}

	public void draw() {
		PApplet applet = this.ui.applet;

		int x = this.position.x, y = this.position.y;
		applet.noFill();
		applet.stroke(this.enabled ? 0 : 100);
		applet.rect(x, y, 14, 14);

		if (this.checked) {
			applet.line(x + 3, y + 7, x + 6, y + 10);
			applet.line(x + 6, y + 10, x + 11, y + 4);
		}

		if (!this.enabled) {
			applet.noStroke();
			applet.fill(0, 30);
			applet.rect(x, y, 14, 14);
		} else if (this.mouseOver || this.mouseDown) {
			applet.noStroke();
			applet.fill(0, this.mouseDown ? 100 : 50);
			applet.rect(x, y, 14, 14);
		}

		applet.noStroke();
		applet.fill(this.enabled ? 0 : 100);
		applet.textSize(12);
		applet.textAlign(PApplet.LEFT, PApplet.BOTTOM);
		applet.text(this.label, x + 18, y + 13);
	}

	private void onClick() {
		if (!this.enabled)
			return;

		this.checked = !this.checked;
		if (this.listener != null)
			this.listener.onChange(this.checked);
		this.ui.requestRedraw();
	}

	public void setEnabled(boolean enabled, Object... params) {
		this.enabled = enabled;
	}

	public Dimension getSize() {
		PApplet applet = this.ui.applet;
		applet.textSize(12);
		float textWidth = applet.textWidth(this.label);
		return new Dimension(14 + 4 + (int) textWidth, 14);
	}

	public void setLocation(Point position) {
		this.position = new Point(position);

		this.ui.listen(new Rectangle(position, this.getSize()), this);
	}

	@Override
	public void mouseEntered(java.awt.event.MouseEvent unused) {
		this.mouseOver = true;
		this.ui.requestRedrawWidgets();
	}

	@Override
	public void mouseExited(java.awt.event.MouseEvent unused) {
		this.mouseOver = false;
		this.mouseDown = false;
		this.ui.requestRedrawWidgets();
	}

	@Override
	public void mousePressed(java.awt.event.MouseEvent unused) {
		this.mouseDown = true;
		this.ui.requestRedrawWidgets();
	}

	@Override
	public void mouseReleased(java.awt.event.MouseEvent event) {
		if (this.mouseDown && event.getButton() == Mouse.LEFT_BUTTON)
			this.onClick();
		this.mouseDown = false;
		this.ui.requestRedrawWidgets();
	}
}
