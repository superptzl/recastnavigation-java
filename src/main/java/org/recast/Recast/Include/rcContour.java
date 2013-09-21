package org.recast.Recast.Include;

/// Represents a simple, non-overlapping contour in field space.
public class rcContour {
    public int[] verts;			///< Simplified contour vertex and connection data. [Size: 4 * #nverts]
    public int nverts;			///< The number of vertices in the simplified contour.
    public int[] rverts;		///< Raw contour vertex and connection data. [Size: 4 * #nrverts]
    public int nrverts;		///< The number of vertices in the raw contour.
    public short reg;	///< The region id of the contour.
    public char area;	///< The area id of the contour.
}
