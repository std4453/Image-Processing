import java.awt.*;

/**
 * Widget interface
 */
public interface Widget {
	/**
	 * draw the widget
	 */
	void draw();

	/**
	 * @return the size of the widget
	 */
	Dimension getSize();

	/**
	 * Set the location of the widget
	 *
	 * @param position
	 * 		the location of the widget
	 */
	void setLocation(Point position);

	/**
	 * Enable / disable the widget. For compatibility reasons, some additional
	 * parameters are passed.
	 */
	void setEnabled(boolean enabled, Object... params);
}