package org.recast.RecastDemo.Include;

/**
 * @author igozha
 * @since 22.09.13 22:04
 */
public enum SamplePolyFlags
{
	SAMPLE_POLYFLAGS_WALK(0x01),        // Ability to walk (ground, grass, road)
	SAMPLE_POLYFLAGS_SWIM(0x02),        // Ability to swim (water).
	SAMPLE_POLYFLAGS_DOOR(0x04),        // Ability to move through doors.
	SAMPLE_POLYFLAGS_JUMP(0x08),        // Ability to jump.
	SAMPLE_POLYFLAGS_DISABLED(0x10),        // Disabled polygon
	SAMPLE_POLYFLAGS_ALL(0xffff);    // All abilities.

	public int v;

	SamplePolyFlags(int v)
	{
		this.v = v;
	}
}
