import processing.core.PApplet;

import java.awt.*;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UIManager {
	public static UIManager instance;

	// View
	protected boolean redrawRequested, redrawWidgetsRequested;
	protected int canvasWidth, canvasHeight;
	protected int windowWidth, windowHeight;

	// Model
	protected Entry content;

	// widgets
	protected java.util.List<Widget> widgets = new ArrayList<>();
	protected Map<Widget, Rectangle> bounds = new HashMap<>();
	protected int lastX = 0, lastY = 0;

	public PApplet applet;

	public UIManager(PApplet applet) {
		this.applet = applet;
		UIManager.instance = this;
	}

	public void setContent(Entry content) {
		this.content = content;
	}

	// interface
	public void onSetup() {
		this.applet.smooth();
		if (this.content != null) this.content.setup();
		this.redrawRequested = true;
	}

	public void onDraw() {
		if (this.redrawRequested) {
			if (this.content != null) this.content.draw();
			this.drawWidgets();
			this.redrawRequested = false;
			this.redrawWidgetsRequested = false;
		} else if (this.redrawWidgetsRequested) {
			this.drawWidgets();
			this.redrawWidgetsRequested = false;
		}
	}

	// public methods
	public void setCanvasSize(int width, int height) {
		this.canvasWidth = width;
		this.canvasHeight = height;
		this.windowWidth = width;
		this.windowHeight = height + 34;
		this.applet.getSurface().setSize(this.windowWidth, this.windowHeight);

		this.updateWidgetsLayout();
	}

	public void requestRedraw() {
		this.redrawRequested = true;
	}

	public void requestRedrawWidgets() {
		this.redrawWidgetsRequested = true;
	}

	public Widget addCheck(String label, CheckListener listener, boolean initial) {
		Widget widget = new CheckWidget(this, label, listener, initial);
		this.widgets.add(widget);
		this.redrawRequested = true;
		return widget;
	}

	public Widget addRadio(String[] labels, RadioListener listener, int initial) {
		Widget widget = new RadioWidget(this, labels, listener, initial);
		this.widgets.add(widget);
		this.redrawRequested = true;
		return widget;
	}

	private void updateWidgetsLayout() {
		int padding = 10, spacing = 10;
		int x = padding, y = this.canvasHeight + padding;
		for (Widget widget : this.widgets) {
			Dimension size = widget.getSize();
			Point p = new Point(x, y);
			widget.setLocation(p);
			this.bounds.put(widget, new Rectangle(p, size));

			x += size.width + spacing;
		}
	}

	private void drawWidgets() {
		this.applet.fill(255);
		this.applet.noStroke();
		this.applet.rect(0, this.canvasHeight, this.canvasWidth,
				this.windowHeight - this.canvasHeight);

		this.widgets.forEach(Widget::draw);
	}

	protected java.util.List<PointerArea> areas = new ArrayList<>();

	public PointerArea listen(Rectangle rectangle, MouseListener listener) {
		PointerArea area = new PointerArea(rectangle, listener);
		this.areas.add(area);
		return area;
	}

	public void onMouseMoved() {
		int x = this.applet.mouseX, y = this.applet.mouseY;
		for (PointerArea area : this.areas) area.mouseMove(this.lastX, this.lastY, x, y);
		this.lastX = x;
		this.lastY = y;
	}

	public void onMouseDown() {
		int button = Mouse.translate(this.applet.mouseButton);
		for (PointerArea area : this.areas)
			area.mouseDown(this.applet.mouseX, this.applet.mouseY, button);
	}

	public void onMouseUp() {
		int button = Mouse.translate(this.applet.mouseButton);
		for (PointerArea area : this.areas)
			area.mouseUp(this.applet.mouseX, this.applet.mouseY, button);
	}

	class PointerArea {
		public Rectangle range;
		public java.util.List<MouseListener> listeners = new ArrayList<>();

		public PointerArea(Rectangle range) {
			this.range = new Rectangle(range);
		}

		public PointerArea(Rectangle range, MouseListener... listeners) {
			this(range);
			Collections.addAll(this.listeners, listeners);
		}

		public void mouseMove(int lastX, int lastY, int x, int y) {
			boolean lastIn = this.range.contains(lastX, lastY);
			boolean thisIn = this.range.contains(x, y);
			if (lastIn && !thisIn)
				for (MouseListener listener : this.listeners)
					listener.mouseExited(this.createMouseEvent(x, y));
			if (thisIn && !lastIn)
				for (MouseListener listener : this.listeners)
					listener.mouseEntered(this.createMouseEvent(x, y));
		}

		public void mouseDown(int x, int y, int button) {
			if (this.range.contains(x, y))
				for (MouseListener listener : this.listeners)
					listener.mousePressed(this.createMouseEvent(x, y, button));
		}

		public void mouseUp(int x, int y, int button) {
			if (this.range.contains(x, y))
				for (MouseListener listener : this.listeners)
					listener.mouseReleased(this.createMouseEvent(x, y, button));
		}

		private final Component PLACEHOLDER_COMPONENT = new Frame();

		private java.awt.event.MouseEvent createMouseEvent(int x, int y) {
			return new java.awt.event.MouseEvent(this.PLACEHOLDER_COMPONENT,
					0, 0L, 0, x, y, 0, false);
		}

		private java.awt.event.MouseEvent createMouseEvent(int x, int y, int button) {
			return new java.awt.event.MouseEvent(this.PLACEHOLDER_COMPONENT,
					0, 0L, 0, x, y, 0, false, button);
		}
	}
}