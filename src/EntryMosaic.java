import processing.core.PApplet;
import processing.core.PImage;

public class EntryMosaic implements Entry {
	public boolean resize = true;
	public boolean fade = false;
	public boolean circle = true;
	/** fill is similiar to {@link EntryHalftone#clip} */
	public boolean fill = true;

	public boolean posterize = true;
	/**
	 * delayed, represents whether the current image is posterized.
	 * As the posterization and de-posterization requires the image to be loaded again,
	 * we want it to load it in draw() ( as not to stuck UI thread )
	 */
	public boolean posterized = false;

	/** magic numbers */
	public int diamt = 8, spacing = 8;

	public String fileName;
	public PImage image = null;

	public UIManager ui;

	public EntryMosaic(String fileName, UIManager ui) {
		this.fileName = fileName;
		this.ui = ui;
	}

	@Override
	public void setup() {
		this.addControls();

		this.image = this.ui.applet.loadImage(this.fileName);

		boolean fill = this.fill;
		int spacing = this.spacing;
		this.ui.setCanvasSize(this.image.width - (fill ? spacing : 0),
				this.image.height - (fill ? spacing : 0));
	}

	private void addControls() {
		this.ui.addCheck("resize", value -> this.resize = value, this.resize);
		this.ui.addCheck("fade", value -> this.fade = value, this.fade);
		this.ui.addCheck("circle", value -> this.circle = value, this.circle);
		this.ui.addCheck("posterize", value -> this.posterize = value, this.posterize);
	}

	@Override
	public void draw() {
		PApplet applet = this.ui.applet;
		applet.background(255);

		if (this.posterize ^ this.posterized) {
			PImage image = this.ui.applet.loadImage(this.fileName);
			if (this.posterize)
				image.filter(PApplet.POSTERIZE, 8);
			this.posterized = this.posterize;
			this.image = image;
		}

		boolean fill = this.fill, resize = this.resize, fade = this.fade, circle = this.circle;
		int spacing = this.spacing, diamt = this.diamt;
		int width = this.image.width, height = this.image.height;

		int dx = fill ? -spacing / 2 : 0, dy = fill ? -spacing / 2 : 0;

		// cover all the surface
		int i0 = width % spacing / 2;
		int j0 = height % spacing / 2;
		for (int i = i0; i < width; i += spacing) {
			for (int j = j0; j < height; j += spacing) {
				int c = this.image.get(i, j);

				// set color
				applet.fill(c);
				applet.noStroke();

				// effects
				float diamt2 = diamt;
				int r = (c & 0xFF0000) >> 16, g = (c & 0xFF00) >> 8, b = c & 0xFF;
				float avg = (r + g + b) / 3.0f;

				if (resize) {
					// calc illumination
					float lum = (float) (1 - avg / 255.0f * 0.5);
					diamt2 *= lum;
				}
				if (fade) {
					float dist = (float) Math.sqrt(
							PApplet.sq(i - width / 2) + PApplet.sq(j - height / 2)) * .7f;
					dist = 1 - dist / Math.min(width, height) * 2;
					if (dist < 0) continue;
					float size = (float) Math.pow(dist, .4);
					diamt2 *= size;
				}

				// draw element
				if (circle)
					applet.ellipse(i + dx, j + dy, diamt2, diamt2);
				else applet.rect(i - diamt2 / 2.0f + dx, j - diamt2 / 2.0f + dy, diamt2,
						diamt2);
			}
		}
	}
}