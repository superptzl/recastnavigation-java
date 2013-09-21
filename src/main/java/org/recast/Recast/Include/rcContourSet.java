package org.recast.Recast.Include;

public class rcContourSet {
    public rcContour[] conts;	///< An array of the contours in the set. [Size: #nconts]
    public int nconts;			///< The number of contours in the set.
    public float bmin[] = new float[3];  	///< The minimum bounds in world space. [(x, y, z)]
    public float bmax[] = new float[3];		///< The maximum bounds in world space. [(x, y, z)]
    public float cs;			///< The size of each cell. (On the xz-plane.)
    public float ch;			///< The height of each cell. (The minimum increment along the y-axis.)
    public int width;			///< The width of the set. (Along the x-axis in cell units.)
    public int height;			///< The height of the set. (Along the z-axis in cell units.)
    public int borderSize;		///< The AABB border size used to generate the source data from which the contours were derived.
}
