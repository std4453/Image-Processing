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

	/**
	 * lumType decides how is the size of shape sampled from the image.<br>
	 * For example, lumType ==  LUMTYPE_BRIGHTNESS means that the size is the
	 * brightness of the sample color, using the {@link PApplet#brightness(int)}
	 * method, while LUMTYPE_AVERAGE represents the average value of three color
	 * components, and LUMTYPE_RED / GREEN / BLUE represent the three color components.
	 */
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

		boolean[] scatterArr = new boolean[]{false, false, true},
				coloredArr = new boolean[]{false, true, false};

		this.radio = this.ui.addRadio(
				new String[]{"grayscale", "colored", "scatter"},
				value -> {
					this.scatter = scatterArr[value];
					this.colored = coloredArr[value];
				}, this.scatter ? 2 : this.colored ? 1 : 0);
	}

	@Override
	public void draw() {
		PApplet applet = this.ui.applet;
		PImage image = this.image;
		applet.background(255);

		applet.noStroke();
		applet.fill(0);

		// calc shape diameter ( to make the standard color density identical )
		double d = this.circle ? this.spacing * Math.sqrt(
				4 / Math.PI) : this.spacing;

		if (this.rounded) this.drawRoundedHalftone(d);
		else if (this.scatter) {
			applet.blendMode(PApplet.SUBTRACT);

			applet.fill(255, 0, 0);
			this.lumType = LUMTYPE_RED;
			this.drawGridHalftone(this.angle, d);

			applet.fill(0, 255, 0);
			this.lumType = LUMTYPE_GREEN;
			this.drawGridHalftone(this.angle + 30, d);

			applet.fill(0, 0, 255);
			this.lumType = LUMTYPE_BLUE;
			this.drawGridHalftone(this.angle + 105, d);

			applet.blendMode(PApplet.BLEND);
		} else this.drawGridHalftone(this.angle, d);
	}

	/**
	 * Draw concentric halftone shapes
	 *
	 * @param d
	 * 		diameter
	 */
	private void drawRoundedHalftone(double d) {
		double maxDist = Math.sqrt(this.image.width * this.image.width +
				this.image.height * this.image.height);
		for (double dist = 0; dist < maxDist; dist += this.spacing) {
			if (dist == 0)
				this.drawElement(0, 0, d);
			else {
				double round = 2 * Math.PI,
						da = round / Math.floor(2 * Math.PI * dist / this.spacing);
				for (double angle = 0; angle < round; angle += da)
					this.drawElement(dist, angle, d);
			}
		}
	}

	/**
	 * Draw rectangular grid shapes.
	 *
	 * @param pAngle
	 * 		rotation angle of shape grid ( in degrees, counterclockwise )
	 * @param d
	 * 		diameter
	 */
	private void drawGridHalftone(double pAngle, double d) {
		double spacing = this.spacing;
		pAngle = Math.toRadians(pAngle);

		double maxDist = Math.sqrt(this.image.width * this.image.width +
				this.image.height * this.image.height);
		for (double i = -maxDist / 2; i < maxDist / 2; i += spacing)
			for (double j = -maxDist / 2; j < maxDist / 2; j += spacing) {
				double dist = Math.sqrt(i * i + j * j);
				if (dist > maxDist) continue;
				double angle = Math.atan2(i, j) + pAngle;

				double x = this.image.width / 2.0 + dist * Math.sin(
						angle), y = this.image.height / 2.0 + dist * Math.cos(angle);

				this.drawElement(x, y, d);
			}
	}

	private void drawElement(double x, double y, double d) {
		if (this.colored && !this.scatter)
			this.ui.applet.fill(this.getColor(this.image, x, y));

		float diameter = (float) this.getDiameter(this.image, x, y, d);
		if (this.circle)
			this.ui.applet.ellipse(this.dx + (float) x,
					this.dy + (float) y, diameter, diameter);
		else this.ui.applet.rect(this.dx + (float) x - diameter / 2,
				this.dy + (float) y - diameter / 2, diameter, diameter);
	}

	/**
	 * Safe get color: accepts two {@code double} coords, return transparent black (
	 * 0x00000000 ) if out of bounds.
	 */
	private int getColor(PImage image, double x, double y) {
		int ix = (int) x, iy = (int) y;
		if (ix < 0 || iy < 0 || ix >= image.width || iy >= image.height)
			return 0;
		return image.get(ix, iy);
	}

	/**
	 * Get scaled diameter = d * lum
	 */
	private double getDiameter(PImage image, double x, double y, double d) {
		int ix = (int) x, iy = (int) y;
		if (ix < 0 || iy < 0 || ix >= image.width || iy >= image.height)
			return 0;
		double lum = this.getLuminance(this.ui.applet, image.get(ix, iy));
		return d * Math.sqrt(1 - lum / 255);
	}

	/**
	 * @param c
	 * 		color
	 *
	 * @return luminance of color, according to lumType
	 */
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