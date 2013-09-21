package org.recast.Recast.Include;

/// Contains triangle meshes that represent detailed height data associated
/// with the polygons in its associated polygon mesh object.
/// @ingroup recast
public class rcPolyMeshDetail {
    public int[] meshes;	///< The sub-mesh data. [Size: 4*#nmeshes]
    public float[] verts;			///< The mesh vertices. [Size: 3*#nverts]
    public char[] tris;	///< The mesh triangles. [Size: 4*#ntris]
    public int nmeshes;			///< The number of sub-meshes defined by #meshes.
    public int nverts;				///< The number of vertices in #verts.
    public int ntris;				///< The number of triangles in #tris.

}
