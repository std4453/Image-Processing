import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;

import java.awt.*;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Random;

public class EntryCircles implements Entry {
	public float iterRatio = 0.8f;
	public float threshold = 0.02f;
	public int startAttempts = 200;
	public int iterAttempts = 50;
	public float size = 120;

	// text strings
	public String[] texts = new String[]{"S", "I", "M", "P", "L", "E"};
	// rotate shapes, this field is respected only when shapeType is SHAPETYPE_TEXTS
	public boolean rotate = true;

	public static final int SHAPETYPE_CIRCLE = 0;
	public static final int SHAPETYPE_RECT = 1;
	public static final int SHAPETYPE_TEXTS = 2;
	public static final int SHAPETYPE_HEX = 3;
	public int shapeType = SHAPETYPE_TEXTS;

	public Random random;

	public String fileName;
	public PImage image;

	public UIManager ui;

	public EntryCircles(String fileName, UIManager ui) {
		this.fileName = fileName;
		this.ui = ui;

		this.random = new Random();
	}

	@Override
	public void setup() {
		this.addControls();

		PApplet applet = this.ui.applet;

		this.image = applet.loadImage(this.fileName);
		// TODO: resolve magic number using a scroll bar
		this.image.filter(PApplet.THRESHOLD, 0.65f);
		this.image.filter(PApplet.INVERT);

		this.ui.setCanvasSize(this.image.width, this.image.height);
	}

	private Widget rotateCheck;

	private void addControls() {
		this.ui.addRadio(new String[]{"circle", "rect", "texts", "hex"},
				value -> {
					this.shapeType = value;
					// rotate is enabled only when shapeType is SHAPETYPE_TEXTS
					this.rotateCheck.setEnabled(value == SHAPETYPE_TEXTS);
				}, this.shapeType);
		this.rotateCheck = this.ui.addCheck("rotate",
				value -> this.rotate = value, this.rotate);
		this.rotateCheck.setEnabled(this.shapeType == SHAPETYPE_TEXTS);
	}

	@Override
	public void draw() {
		PApplet applet = this.ui.applet;
		applet.background(255);
		PGraphics graphics = applet.getGraphics();
		PImage image = this.image;

		int attempts = this.startAttempts;
		for (float size = 1; size > this.threshold; size *= this.iterRatio) {
			// render shapes to offscreen canvases
			Map.Entry[] targets = this.buildTargets(applet, this.size * size);

			for (int i = 0; i < attempts; ++i) {
				// select random shape
				int index = targets.length == 1 ? 0 : this.random.nextInt(
						targets.length);
				Map.Entry entry = targets[index];
				PGraphics target = (PGraphics) entry.getKey();
				if (target == null) continue;
				Rectangle rect = (Rectangle) entry.getValue();

				// place shape randomly
				float x = this.random.nextFloat() * image.width + size, y = this.random
						.nextFloat() * image.height + size;
				float angle = this.shapeType == SHAPETYPE_TEXTS && this.rotate ? this.random
						.nextFloat() * 2 * (float) Math.PI : 0;
				if (this.fits(graphics, target, image, x, y, rect, angle))
					this.drawScaled(graphics, target, x, y, angle);
			}

			// iterate
			attempts += this.iterAttempts;
		}
	}

	private void drawScaled(PGraphics canvas, PImage image, float x, float y,
							float angle) {
		if (angle != 0) {    // rotated text
			canvas.pushMatrix();
			canvas.translate(x, y);
			canvas.rotate(angle);
			canvas.image(image, 0, 0);
			canvas.popMatrix();
		} else    // non-text, save the effort of matrix manipulation
			canvas.image(image, x, y);
	}

	private Map.Entry[] buildTargets(PApplet applet, float size) {
		switch (this.shapeType) {
			case SHAPETYPE_CIRCLE:
				PGraphics canvas = this.initGraphics(
						applet.createGraphics((int) (Math.ceil(size)),
								(int) (Math.ceil(size))), true);
				canvas.ellipse(size / 2, size / 2, size, size);
				canvas.endDraw();
				return new Map.Entry[]{new AbstractMap.SimpleEntry<>(
						canvas,
						new Rectangle(0, 0, this.round(size), this.round(size)))};

			case SHAPETYPE_RECT:
				canvas = this.initGraphics(applet.createGraphics((int) (Math.ceil(size)),
						(int) (Math.ceil(size))), true);
				canvas.rect(0, 0, size, size);
				canvas.endDraw();
				return new Map.Entry[]{new AbstractMap.SimpleEntry<>(
						canvas,
						new Rectangle(0, 0, this.round(size), this.round(size)))};

			case SHAPETYPE_TEXTS:
				PFont font = applet.createFont("DIN Black", size);

				// use a temporary canvas to determine text width
				PGraphics tmp = applet.createGraphics(1, 1);
				tmp.beginDraw();
				tmp.textFont(font);
				tmp.textSize(size);

				Map.Entry[] targets = new Map.Entry[this.texts.length];

				for (int i = 0; i < this.texts.length; ++i) {
					float textWidth = tmp.textWidth(this.texts[i]);
					if ((int) textWidth != 0) {
						// creating a canvas with width 0 will result in an error
						// so in case of very small text size, ignore it completely

						// canvas height is doubled because of ascend and descend part
						canvas = this.initGraphics(applet.createGraphics((int) textWidth,
								(int) (Math.ceil(size * 2))), true);
						canvas.textFont(font);
						canvas.textSize(size);
						canvas.textAlign(PApplet.CENTER, PApplet.CENTER);
						canvas.text(this.texts[i], textWidth / 2, size / 2);
						canvas.endDraw();
					} else canvas = null;
					targets[i] = new AbstractMap.SimpleEntry<>(canvas,
							new Rectangle(0, 0, this.round(textWidth), this.round(size)));
				}
				return targets;

			case SHAPETYPE_HEX:
				float halfSize = size / 2;
				canvas = this.initGraphics(applet.createGraphics((int) (Math.ceil(size)),
						(int) (Math.ceil(size))), true);
				canvas.beginShape();
				// use a loop to generate vertices
				for (int i = 0; i < 6; ++i)
					canvas.vertex(
							halfSize + (float) (halfSize * Math.cos(Math.PI / 3 * i)),
							halfSize + (float) (halfSize * Math.sin(Math.PI / 3 * i)));
				canvas.endShape(PApplet.CLOSE);
				canvas.endDraw();
				return new Map.Entry[]{new AbstractMap.SimpleEntry<>(
						canvas,
						new Rectangle(0, 0, this.round(size), this.round(size)))};

			default:
				return new Map.Entry[0];
		}
	}

	/**
	 * Do some common operations on a newly-generated PGraphics
	 */
	private PGraphics initGraphics(PGraphics graphics, boolean offscreen) {
		if (offscreen)
			graphics.beginDraw();
		graphics.background(255, 0);
		graphics.noStroke();
		graphics.fill(0);
		return graphics;
	}

	/** to shorten code */
	private int round(double n) {
		return (int) Math.round(n);
	}

	private boolean fits(PGraphics range, PGraphics target, PImage image, float x,
						 float y, Rectangle rect, double angle) {
		int x0 = rect.x, x1 = x0 + rect.width, y0 = rect.y, y1 = y0 + rect.height,
				ix = this.round(x), iy = this.round(y);

		// checks all the non-transparent pixels in the shape
		for (int i = x0; i < x1; ++i)
			for (int j = y0; j < y1; ++j)
				if (target.get(i, j) != 0x00FFFFFF) {
					int xx = ix + i, yy = iy + j;
					if (angle != 0) {    // rotate shape
						double length = Math.sqrt(i * i + j * j);
						double da = Math.atan2(j, i);
						xx = ix + (int) Math.round(length * Math.cos(da + angle));
						yy = iy + (int) Math.round(length * Math.sin(da + angle));
					}

					// if another shape already exists or overlaps with the image
					if (image.get(xx, yy) != 0xFFFFFFFF ||
							range.get(xx, yy) != 0xFFFFFFFF)
						return false;
				}

		return true;
	}
}