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
				new EntryFill("images/faster-than-anyone.png", this.ui));
		this.ui.setContent(Registry.instance.query("fill"));
	}

	@Override
	public void setup() {
		ui.onSetup();
	}

	@Override
	public void draw() {
		ui.onDraw();
	}

	@Override
	public void mouseMoved() {
		ui.onMouseMoved();
	}

	@Override
	public void mousePressed() {
		ui.onMouseDown();
	}

	@Override
	public void mouseReleased() {
		ui.onMouseUp();
	}
}