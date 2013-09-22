package org.recast.Recast.Source;

import org.junit.Test;

/**
 * @author igozha
 * @since 22.09.13 11:12
 */
public class RecastContourTest
{
	@Test
	public void testDistancePtSeg()
	{
		float d2 = RecastContour.distancePtSeg(143, 34, 144, 30, 147, 51);//6
		float d1 = RecastContour.distancePtSeg(147, 40, 144, 30, 147, 51);//16
		int x = 2 + 2;

		double d4 = RecastContour.distancePtSegDouble(143, 34, 144, 30, 147, 51);//6
		double d3 = RecastContour.distancePtSegDouble(147, 40, 144, 30, 147, 51);//16

		//cpp
		float c1 = RecastContour.distancePtSeg(143, 34, 144, 30, 147, 51);//ci=6		d	2.4199998	float
		float c2 = RecastContour.distancePtSeg(147, 40, 144, 30, 147, 51);//ci=16				d	2.4200001	float

	}
}
