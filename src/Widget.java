import java.awt.*;

public interface Widget {
	void draw();

	Dimension getSize();

	void setLocation(Point position);

	void setEnabled(boolean enabled, Object... params);
}