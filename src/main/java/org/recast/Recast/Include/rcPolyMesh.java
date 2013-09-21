package org.recast.Recast.Include;

/// Represents a polygon mesh suitable for use in building a navigation mesh.
/// @ingroup recast
public class rcPolyMesh {
    public int[] verts;	///< The mesh vertices. [Form: (x, y, z) * #nverts]
    public int[] polys;	///< Polygon and neighbor data. [Length: #maxpolys * 2 * #nvp]
    public int[] regs;	///< The region id assigned to each polygon. [Length: #maxpolys]
    public int[] flags;	///< The user defined flags for each polygon. [Length: #maxpolys]
    public char[] areas;	///< The area id assigned to each polygon. [Length: #maxpolys]
    public int nverts;				///< The number of vertices.
    public int npolys;				///< The number of polygons.
    public int maxpolys;			///< The number of allocated polygons.
    public int nvp;				///< The maximum number of vertices per polygon.
    public float bmin[] = new float[3];			///< The minimum bounds in world space. [(x, y, z)]
    public float bmax[] = new float[3];			///< The maximum bounds in world space. [(x, y, z)]
    public float cs;				///< The size of each cell. (On the xz-plane.)
    public float ch;				///< The height of each cell. (The minimum increment along the y-axis.)
    public int borderSize;			///< The AABB border size used to generate the source data from which the mesh was derived.
}
