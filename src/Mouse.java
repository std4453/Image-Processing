public class Mouse {
	public static final int LEFT_BUTTON = 0;
	public static final int MIDDLE_BUTTON = 1;
	public static final int RIGHT_BUTTON = 2;

	public static int translate(int rawButton) {
		// raw button codes are get within Processing 3.0.2, they may be
		// different in other versions and other operating systems.
		switch (rawButton) {
			case 37:
				return LEFT_BUTTON;
			case 3:
				return MIDDLE_BUTTON;
			case 39:
				return RIGHT_BUTTON;
			default:
				return -1;
		}
	}
}