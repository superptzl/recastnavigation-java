package org.recast.Recast.Include;

public class rcCompactHeightfield {
    public int width;					///< The width of the heightfield. (Along the x-axis in cell units.)
    public int height;					///< The height of the heightfield. (Along the z-axis in cell units.)
    public int spanCount;				///< The number of spans in the heightfield.
    public int walkableHeight;			///< The walkable height used during the build of the field.  (See: rcConfig::walkableHeight)
    public int walkableClimb;			///< The walkable climb used during the build of the field. (See: rcConfig::walkableClimb)
    public int borderSize;				///< The AABB border size used during the build of the field. (See: rcConfig::borderSize)
    public int maxDistance;	///< The maximum distance value of any span within the field.
    public int maxRegions;	///< The maximum region id of any span within the field.
    public float bmin[] = new float[3];				///< The minimum bounds in world space. [(x, y, z)]
    public float bmax[] = new float[3];				///< The maximum bounds in world space. [(x, y, z)]
    public float cs;					///< The size of each cell. (On the xz-plane.)
    public float ch;					///< The height of each cell. (The minimum increment along the y-axis.)
    public rcCompactCell[] cells;		///< Array of cells. [Size: #width*#height]
    public rcCompactSpan[] spans;		///< Array of spans. [Size: #spanCount]
    public int[] dist;		///< Array containing border distance data. [Size: #spanCount]
    public char[] areas;		///< Array containing area id data. [Size: #spanCount]
}
