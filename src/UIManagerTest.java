import processing.core.PApplet;

/**
 *
 */
public class UIManagerTest implements Entry {
	private UIManager ui;

	public UIManagerTest(UIManager ui) {
		this.ui = ui;
	}

	@Override
	public void setup() {
		this.ui.addCheck("someLongLabel1", null, true);
		this.ui.addCheck("someLongLabel2", null, true);
		this.ui.addRadio(new String[]{"one", "two", "three"}, null, 0);
		this.ui.addCheck("short", null, true);
		this.ui.addCheck("someLongLabel3", null, true);
		this.ui.addCheck("someLongLabel4", null, true);

		this.ui.setCanvasSize(300, 300);
	}

	@Override
	public void draw() {
		PApplet applet = this.ui.applet;
		applet.background(255);
		applet.noStroke();
		applet.fill(0);
		applet.ellipse(150, 150, 100, 100);
	}
}