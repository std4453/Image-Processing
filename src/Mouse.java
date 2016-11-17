public class Mouse {
	public static final int LEFT_BUTTON = 0;
	public static final int MIDDLE_BUTTON = 1;
	public static final int RIGHT_BUTTON = 2;

	public static int translate(int rawButton) {
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