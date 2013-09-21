package com.pf.domain;

import com.pf.util.Point;

import java.awt.geom.Rectangle2D;

/**
 * @author igozha
 * @since 07.09.13 08:39
 */
public class Obstacle
{
	public Obstacle(Point connerOne, Point connerTwo)
	{
		this.connerOne = connerOne;
		this.connerTwo = connerTwo;
	}

	public Point connerOne;
	public Point connerTwo;

	public boolean containsPoint(Point point)
	{
		return new Rectangle2D.Double(connerOne.x, connerOne.y, Math.abs(connerTwo.x - connerOne.x), Math.abs(connerTwo.y - connerOne.y)).
			contains(point.x, point.y);
	}
}
