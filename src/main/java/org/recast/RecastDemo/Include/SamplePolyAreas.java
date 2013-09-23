package org.recast.RecastDemo.Include;

/**
 * @author igozha
 * @since 22.09.13 22:02
 */
public enum SamplePolyAreas
{
	SAMPLE_POLYAREA_GROUND(0),
	SAMPLE_POLYAREA_WATER(1),
	SAMPLE_POLYAREA_ROAD(2),
	SAMPLE_POLYAREA_DOOR(3),
	SAMPLE_POLYAREA_GRASS(4),
	SAMPLE_POLYAREA_JUMP(5);

	public char v;

	SamplePolyAreas(int v) {
		this.v = (char)v;
	}
}
