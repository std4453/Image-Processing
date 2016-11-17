import processing.core.PApplet;
import processing.core.PImage;

public class EntryHalftone implements Entry {
	private class Context {
		public double spacing = 6;
		public double angle = 0;
		public boolean circle = true;
		public boolean rounded = false;
		public boolean colored = false;
		public boolean scatter = true;

		public boolean clip = true;
		public int dx = 0, dy = 0;

		public int lumType = LUMTYPE_AVERAGE;
		public static final int LUMTYPE_BRIGHTNESS = 0;
		public static final int LUMTYPE_AVERAGE = 1;
		public static final int LUMTYPE_RED = 2;
		public static final int LUMTYPE_GREEN = 3;
		public static final int LUMTYPE_BLUE = 4;

		public String fileName;
		public PImage image;

		public UIManager ui;

		public Context(String fileName, UIManager ui) {
			this.fileName = fileName;
			this.ui = ui;
		}

		public void setImage(PImage image) {
			this.image = image;
		}

		public void setCircle(boolean circle) {
			this.circle = circle;
		}

		public void setRounded(boolean rounded) {
			this.rounded = rounded;
		}

		public void setColored(boolean colored) {
			this.colored = colored;
		}

		public void setScatter(boolean scatter) {
			this.scatter = scatter;
		}

		public void setDelta(int dx, int dy) {
			this.dx = dx;
			this.dy = dy;
		}

		public void setLumType(int lumType) {
			this.lumType = lumType;
		}
	}

	protected Context context;

	public EntryHalftone(String fileName, UIManager ui) {
		this.context = new Context(fileName, ui);
	}

	@Override
	public void setup() {
		this.addControls();

		this.context.setImage(this.context.ui.applet.loadImage(this.context.fileName));
		this.context.ui.setCanvasSize(
				this.context.image.width - (this.context.clip ? (int) (this.context.spacing * 2) : 0),
				this.context.image.height - (this.context.clip ? (int) (this.context.spacing * 2) : 0));
		if (this.context.clip)
			this.context.setDelta(-(int) this.context.spacing,
					-(int) this.context.spacing);
	}

	protected Widget radio;

	private void addControls() {
		this.context.ui.addCheck("circle",
				value -> EntryHalftone.this.context.setCircle(value),
				this.context.circle);
		this.context.ui.addCheck("rounded", value -> {
			EntryHalftone.this.context.setRounded(value);
			EntryHalftone.this.radio.setEnabled(!value, 2);
		}, this.context.rounded);
		this.radio = this.context.ui.addRadio(
				new String[]{"grayscale", "colored", "scatter"},
				value -> {
					EntryHalftone.this.context.setScatter(false);
					EntryHalftone.this.context.setColored(false);
					switch (value) {
						case 2:
							EntryHalftone.this.context.setScatter(true);
							break;
						case 1:
							EntryHalftone.this.context.setColored(true);
							break;
					}
				}, this.context.scatter ? 2 : this.context.colored ? 1 : 0);
	}

	@Override
	public void draw() {
		PApplet applet = this.context.ui.applet;
		PImage image = this.context.image;
		applet.background(255);

		applet.noStroke();
		applet.fill(0);

		double d = this.context.circle ? this.context.spacing * Math.sqrt(
				4 / Math.PI) : this.context.spacing;
		if (this.context.rounded) drawRoundedHalftone(image, d);
		else if (this.context.scatter) {
			applet.background(255);
			applet.blendMode(PApplet.SUBTRACT);

			applet.fill(255, 0, 0);
			this.context.setLumType(Context.LUMTYPE_RED);
			drawGridHalftone(image, this.context.angle, d);

			applet.fill(0, 255, 0);
			this.context.setLumType(Context.LUMTYPE_GREEN);
			drawGridHalftone(image, this.context.angle + 30, d);

			applet.fill(0, 0, 255);
			this.context.setLumType(Context.LUMTYPE_BLUE);
			drawGridHalftone(image, this.context.angle + 105, d);

			applet.blendMode(PApplet.BLEND);
		} else drawGridHalftone(image, this.context.angle, d);
	}

	private void drawRoundedHalftone(PImage image, double d) {
		int width = image.width, height = image.height;
		double spacing = this.context.spacing;

		double maxDist = Math.sqrt(width * width + height * height);
		for (double dist = 0; dist < maxDist; dist += spacing) {
			if (dist == 0)
				this.drawElement(image, 0, 0, d);
			else {
				double round = 2 * Math.PI, da = round / Math.floor(
						2 * Math.PI * dist / spacing);
				for (double angle = 0; angle < round; angle += da)
					this.drawElement(image, dist, angle, d);
			}
		}
	}

	private void drawGridHalftone(PImage image, double pAngle, double d) {
		int width = image.width, height = image.height;
		double spacing = this.context.spacing;

		double maxDist = Math.sqrt(width * width + height * height);
		for (double i = -maxDist / 2; i < maxDist / 2; i += spacing)
			for (double j = -maxDist / 2; j < maxDist / 2; j += spacing) {
				double dist = Math.sqrt(i * i + j * j);
				double angle = Math.atan2(i, j) + Math.toRadians(pAngle);
				this.drawElement(image, dist, angle, d);
			}
	}

	private void drawElement(PImage image, double dist, double angle, double d) {
		double x = image.width / 2.0 + dist * Math.sin(
				angle), y = image.height / 2.0 + dist * Math.cos(angle);
		if (this.context.colored && !this.context.scatter)
			this.context.ui.applet.fill(this.getColor(image, x, y));
		float diameter = (float) this.getDiameter(image, x, y, d);
		if (this.context.circle)
			this.context.ui.applet.ellipse(this.context.dx + (float) x,
					this.context.dy + (float) y, diameter, diameter);
		else this.context.ui.applet.rect(this.context.dx + (float) x - diameter / 2,
				this.context.dy + (float) y - diameter / 2, diameter, diameter);
	}

	private int getColor(PImage image, double x, double y) {
		int ix = (int) x, iy = (int) y;
		if (ix < 0 || iy < 0 || ix >= image.width || iy >= image.height)
			return 0;
		return image.get(ix, iy);
	}

	private double getDiameter(PImage image, double x, double y, double d) {
		int ix = (int) x, iy = (int) y;
		if (ix < 0 || iy < 0 || ix >= image.width || iy >= image.height)
			return 0;
		double lum = this.getLuminance(this.context.ui.applet, image.get(ix, iy));
		return d * Math.sqrt(1 - lum / 255);
	}

	private double getLuminance(PApplet applet, int c) {
		switch (this.context.lumType) {
			case Context.LUMTYPE_BRIGHTNESS:
				return applet.brightness(c);
			case Context.LUMTYPE_AVERAGE:
				return (applet.red(c) + applet.green(c) + applet.blue(c)) / 3.0;
			case Context.LUMTYPE_RED:
				return applet.red(c);
			case Context.LUMTYPE_GREEN:
				return applet.green(c);
			case Context.LUMTYPE_BLUE:
				return applet.blue(c);
			default:
				return 0;
		}
	}
}