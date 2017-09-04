package kröw.program.api.graphics.fx.transitions;

import javafx.animation.Transition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

public class RandomColorTransition extends Transition {

	private Color fromColor, toColor;

	private final ObjectProperty<Region> region = new SimpleObjectProperty<>();

	@Override
	protected void interpolate(double frac) {
		region.get()
				.setBackground(new Background(new BackgroundFill(fromColor.interpolate(toColor, frac), null, null)));
	}

	/**
	 * @return the fromColor
	 */
	public final Color getFromColor() {
		return fromColor;
	}

	/**
	 * @return the region
	 */
	public final Region getRegion() {
		return region.get();
	}

	public final ObjectProperty<Region> regionProperty() {
		return region;
	}

	/**
	 * @param region
	 *            the region to set
	 */
	public final void setRegion(Region region) {
		this.region.set(region);
	}

	/**
	 * @param fromColor
	 *            the fromColor to set
	 */
	public final void setFromColor(Color fromColor) {
		this.fromColor = fromColor;
	}

	/**
	 * @return the toColor
	 */
	public final Color getToColor() {
		return toColor;
	}

	/**
	 * @param toColor
	 *            the toColor to set
	 */
	public final void setToColor(Color toColor) {
		this.toColor = toColor;
	}

}
