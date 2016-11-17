import processing.core.PApplet;
import processing.core.PImage;

public class EntryHalftone implements Entry {
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

	public EntryHalftone(String fileName, UIManager ui) {
		this.fileName = fileName;
		this.ui = ui;
	}

	@Override
	public void setup() {
		this.addControls();

		this.image = this.ui.applet.loadImage(this.fileName);
		this.ui.setCanvasSize(
				this.image.width - (this.clip ? (int) (this.spacing * 2) : 0),
				this.image.height - (this.clip ? (int) (this.spacing * 2) : 0));
		if (this.clip) {
			this.dx = -(int) this.spacing;
			this.dy = -(int) this.spacing;
		}
	}

	protected Widget radio;

	private void addControls() {
		this.ui.addCheck("circle", value -> this.circle = value, this.circle);
		this.ui.addCheck("rounded", value -> {
			this.rounded = value;
			EntryHalftone.this.radio.setEnabled(!value, 2);
		}, this.rounded);
		this.radio = this.ui.addRadio(
				new String[]{"grayscale", "colored", "scatter"},
				value -> {
					this.scatter = this.colored = false;
					switch (value) {
						case 2:
							this.scatter = true;
							break;
						case 1:
							this.colored = true;
							break;
					}
				}, this.scatter ? 2 : this.colored ? 1 : 0);
	}

	@Override
	public void draw() {
		PApplet applet = this.ui.applet;
		PImage image = this.image;
		applet.background(255);

		applet.noStroke();
		applet.fill(0);

		double d = this.circle ? this.spacing * Math.sqrt(
				4 / Math.PI) : this.spacing;
		if (this.rounded) this.drawRoundedHalftone(image, d);
		else if (this.scatter) {
			applet.background(255);
			applet.blendMode(PApplet.SUBTRACT);

			applet.fill(255, 0, 0);
			this.lumType = LUMTYPE_RED;
			this.drawGridHalftone(image, this.angle, d);

			applet.fill(0, 255, 0);
			this.lumType = LUMTYPE_GREEN;
			this.drawGridHalftone(image, this.angle + 30, d);

			applet.fill(0, 0, 255);
			this.lumType = LUMTYPE_BLUE;
			this.drawGridHalftone(image, this.angle + 105, d);

			applet.blendMode(PApplet.BLEND);
		} else this.drawGridHalftone(image, this.angle, d);
	}

	private void drawRoundedHalftone(PImage image, double d) {
		int width = image.width, height = image.height;
		double spacing = this.spacing;

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
		double spacing = this.spacing;

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
		if (this.colored && !this.scatter)
			this.ui.applet.fill(this.getColor(image, x, y));
		float diameter = (float) this.getDiameter(image, x, y, d);
		if (this.circle)
			this.ui.applet.ellipse(this.dx + (float) x,
					this.dy + (float) y, diameter, diameter);
		else this.ui.applet.rect(this.dx + (float) x - diameter / 2,
				this.dy + (float) y - diameter / 2, diameter, diameter);
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
		double lum = this.getLuminance(this.ui.applet, image.get(ix, iy));
		return d * Math.sqrt(1 - lum / 255);
	}

	private double getLuminance(PApplet applet, int c) {
		switch (this.lumType) {
			case LUMTYPE_BRIGHTNESS:
				return applet.brightness(c);
			case LUMTYPE_AVERAGE:
				return (applet.red(c) + applet.green(c) + applet.blue(c)) / 3.0;
			case LUMTYPE_RED:
				return applet.red(c);
			case LUMTYPE_GREEN:
				return applet.green(c);
			case LUMTYPE_BLUE:
				return applet.blue(c);
			default:
				return 0;
		}
	}
}