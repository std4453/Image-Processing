UIManager ui;

public ImageProcessing() {
  this.ui = new UIManager(this);
  Registry.instance.register("mosaic", new EntryMosaic("flowers3.png", this.ui));
  Registry.instance.register("halftone", new EntryHalftone("flowers3.png", this.ui));
  Registry.instance.register("circles", new EntryCircles("faster-than-anyone.png", this.ui));
  Registry.instance.register("fill", new EntryFill("faster-than-anyone.png", this.ui));
  this.ui.setContent(Registry.instance.query("fill"));
}

void setup() { ui.onSetup(); }
void draw() { ui.onDraw(); }
void mouseMoved() { ui.onMouseMoved(); }
void mousePressed() { ui.onMouseDown(); }
void mouseReleased() { ui.onMouseUp(); }