/**
 * The Entry of a render program that actually get executed. The reason why Entry is
 * used is mixed:<br>
 * a) I wanted to place image processing codes into separate modules and decouple.
 * b) I wanted to implement several widgets and I have to extract a common interface of
 * different image processing effects.
 * c) I may want to provide the warm changing between different effects so that effects
 * should be able to be instantiated separately.
 */
public interface Entry {
	/**
	 * Setup everything. Called only once during the setup process.
	 */
	void setup();

	/**
	 * Draw the content. Called only when required ( when redraw is manually requested
	 * when something changes or the program runs by the first time )
	 */
	void draw();
}