package krow.guis;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

public final class ShapeFactory {
	public static Circle buildCircle() {
		return buildCircle(50);
	}

	public static Circle buildCircle(final double size) {
		return new Circle(size / 2);
	}

	public static Shape buildRegularShape(double size, int sides) {
		sides = Math.abs(sides);
		size = size == 0 ? 100 : Math.abs(size);
		if (sides == Integer.MAX_VALUE)
			return buildCircle(size);

		final double[] points = new double[sides * 2];

		points[0] = 0;
		points[1] = -size / 2;

		final double turnInc = 180 - (sides - 2) * 180 / sides;
		double turningDeg = 0 + (sides & 1) * (turnInc / 2);

		final double distance =

				// Modified size code for shapes with even side count

				// (sides & 1) == 0 ? (2 * Math.tan(Math.toRadians(180 / sides))
				// * (size / 2)):
				2 * size / (1 / Math.tan(Math.toRadians(180 / sides)) + 1 / Math.sin(Math.toRadians(180 / sides)));

		for (int i = 2; i < points.length; i++) {
			points[i] = points[i++ - 2] + distance * Math.cos(Math.toRadians(turningDeg));
			points[i] = points[i - 2] + distance * Math.sin(Math.toRadians(turningDeg));
			turningDeg += turnInc;
		}

		final Polygon p = new Polygon(points);
		return p;
	}

	public static Polygon buildTriangle() {
		return buildTriangle(100);
	}

	public static Polygon buildTriangle(final double size) {
		return new Polygon(0, 0, -size / 2, size, size / 2, size);
	}

	private ShapeFactory() {
	}

}
