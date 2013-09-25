package org.recast.Recast.Include;

public class rcHeightfield
{
	public int width;            ///< The width of the heightfield. (Along the x-axis in cell units.)
	public int height;            ///< The height of the heightfield. (Along the z-axis in cell units.)
	public float bmin[] = new float[3];    ///< The minimum bounds in world space. [(x, y, z)]
	public float bmax[] = new float[3];        ///< The maximum bounds in world space. [(x, y, z)]
	public float cs;            ///< The size of each cell. (On the xz-plane.)
	public float ch;            ///< The height of each cell. (The minimum increment along the y-axis.)
	public rcSpan[] spans;        ///< Heightfield of spans (width*height).
	public rcSpanPool pools;    ///< Linked list of span pools.
	public rcSpan freelist;    ///< The next free span.
}
