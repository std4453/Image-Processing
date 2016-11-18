import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.Random;

public class EntryFill implements Entry {
	protected int attempts = 10000;
	protected float maxSize = 100;

	protected float iterScale = 0.9f;
	protected float minScale = 0.05f;

	private static final int SHAPETYPE_CIRCLE = 0;
	private static final int SHAPETYPE_RECT = 1;
	private static final int SHAPETYPE_HEXAGON = 2;
	protected int shapeType = 0;

	protected String fileName;
	protected PImage image;
	protected UIManager ui;

	protected Random random;
	protected PGraphics overlap, canvas;

	protected boolean shapesNeedRedraw = true;
	protected java.util.List<PGraphics> shapes = new ArrayList<>();

	public EntryFill(String fileName, UIManager ui) {
		this.fileName = fileName;
		this.ui = ui;
	}

	@Override
	public void setup() {
		this.addControls();

		PApplet applet = this.ui.applet;
		this.image = applet.loadImage(this.fileName);
		// default: 0.5f
		this.image.filter(PApplet.THRESHOLD, 0.5f);
		this.image.filter(PApplet.INVERT);
		this.ui.setCanvasSize(this.image.width, this.image.height);

		this.canvas = applet.getGraphics();
		// create overlap canvas
		this.overlap = applet.createGraphics(this.image.width, this.image.height);
		this.overlap.beginDraw();

		this.random = new Random();
	}

	private void addControls() {
		this.ui.addRadio(new String[]{"circles", "quads", "hexagons"},
				value -> {
					EntryFill.this.shapeType = value;
					EntryFill.this.shapesNeedRedraw = true;
				}, this.shapeType);
	}

	@Override
	public void draw() {
		this.canvas.background(255);
		// draw image to overlap
		this.overlap.image(this.image, 0, 0);

		if (this.shapesNeedRedraw) {
			this.redrawShapes();
			this.shapesNeedRedraw = false;
		}

		float x, y;
		int choice = 0;
		int shapesCount = this.shapes.size();
		for (int i = 0; i < this.attempts; ++i) {
			// choose random position
			x = this.random.nextFloat() * this.image.width;
			y = this.random.nextFloat() * this.image.height;
			if (shapesCount > 0)
				choice = this.random.nextInt(shapesCount);

			this.tryPosition(x, y, this.shapes.get(choice));
		}
	}

	/**
	 * Some common operations
	 */
	private void initCanvas(PGraphics canvas, boolean text) {
		canvas.beginDraw();
		canvas.background(255, 0);
		canvas.noStroke();
		canvas.fill(0);

		// text
		if (text) {
			canvas.textAlign(PApplet.CENTER, PApplet.CENTER);
			// use default font for now
		}
	}

	private void initCanvas(PGraphics canvas) {
		this.initCanvas(canvas, false);
	}

	private void redrawShapes() {
		float size = this.maxSize;
		int isize = (int) Math.ceil(size);
		PApplet applet = this.ui.applet;
		PGraphics canvas;

		this.shapes.clear();
		switch (this.shapeType) {
			case SHAPETYPE_CIRCLE:
				canvas = applet.createGraphics(isize, isize);
				this.initCanvas(canvas);
				canvas.ellipse(isize / 2f, isize / 2f, size, size);
				canvas.endDraw();
				this.shapes.add(canvas);
				break;

			case SHAPETYPE_RECT:
				canvas = applet.createGraphics(isize, isize);
				this.initCanvas(canvas);
				canvas.rect(isize / 2f - size / 2, isize / 2f - size / 2, size, size);
				canvas.endDraw();
				this.shapes.add(canvas);
				break;

			case SHAPETYPE_HEXAGON:
				// calc precise height
				float height = (float) (size * Math.sqrt(3) / 2);
				int iheight = (int) Math.ceil(height);
				canvas = applet.createGraphics(isize, iheight);
				this.initCanvas(canvas);
				float dx = isize / 2f, dy = iheight / 2f;
				canvas.beginShape();
				// use a loop to generate all vertices
				for (int i = 0; i < 6; ++i)
					canvas.vertex((float) (dx + size / 2 * Math.cos(Math.PI / 3 * i)),
							(float) (dy + size / 2 * Math.sin(Math.PI / 3 * i)));
				canvas.endShape(PApplet.CLOSE);
				canvas.endDraw();
				this.shapes.add(canvas);
				break;
		}
	}

	private void tryPosition(float x, float y, PGraphics shape) {
		for (float scale = 1; scale > this.minScale; scale *= this.iterScale)
			if (this.checkSpace(x, y, shape, scale)) {
				// draw scaled shapes

				this.overlap.pushMatrix();
				this.overlap.translate(x, y);
				this.overlap.scale(scale);
				this.overlap.image(shape, 0, 0);
				this.overlap.popMatrix();

				this.canvas.pushMatrix();
				this.canvas.translate(x, y);
				this.canvas.scale(scale);
				this.canvas.image(shape, 0, 0);
				this.canvas.popMatrix();

				break;
			}
	}

	private boolean checkSpace(float x, float y, PGraphics shape, float scale) {
		int width = shape.width, height = shape.height;

		// go through all the pixels in the shape

		for (int i = 0; i < width; ++i)
			for (int j = 0; j < height; ++j)
				if (shape.get(i, j) != 0x00FFFFFF)
					if (this.overlap.get((int) (x + i * scale),
							(int) (y + j * scale)) != 0xFFFFFFFF)
						return false;
		return true;
	}
}