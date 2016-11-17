import processing.core.PApplet;

public class ImageProcessing extends PApplet {

	UIManager ui;

	public ImageProcessing() {
		this.ui = new UIManager(this);
		Registry.instance.register("mosaic",
				new EntryMosaic("images/flowers3.png", this.ui));
		Registry.instance.register("halftone",
				new EntryHalftone("images/flowers3.png", this.ui));
		Registry.instance.register("circles",
				new EntryCircles("images/faster-than-anyone.png", this.ui));
		Registry.instance.register("fill",
				new EntryFill("images/elder2.png", this.ui));
		this.ui.setContent(Registry.instance.query("fill"));
	}

	@Override
	public void setup() {
		this.ui.onSetup();
	}

	@Override
	public void draw() {
		this.ui.onDraw();
	}

	@Override
	public void mouseMoved() {
		this.ui.onMouseMoved();
	}

	@Override
	public void mousePressed() {
		this.ui.onMouseDown();
	}

	@Override
	public void mouseReleased() {
		this.ui.onMouseUp();
	}
}