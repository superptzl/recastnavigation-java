package com.pf.util;

/**
 * @author igozha
 * @since 07.09.13 08:48
 */
public class Point
{
	public Point(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public double distanceTo(Point other) {
		double dx = x - other.x;
		double dy = y - other.y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	public double x;
	public double y;
}
