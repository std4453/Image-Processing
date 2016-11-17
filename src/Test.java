import processing.core.PApplet;

/**
 * Testing of Processing
 */
public class Test extends PApplet {
	@Override
	public void settings() {
		this.size(600, 600);
	}

	@Override
	public void setup() {
		super.setup();

		this.background(255);
		this.noStroke();
		this.fill(0);
	}

	@Override
	public void draw() {
		super.draw();

		this.ellipse(300, 300, 200, 200);
	}

	public static void main(String[] args) {
		Test test = new Test();
		PApplet.main("Test");
	}
}
