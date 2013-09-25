package org.recast.Detour.Include;

/**
 * @author igozha
 * @since 22.09.13 20:56
 */
/// Defines the location of detail sub-mesh data within a dtMeshTile.
public class dtPolyDetail
{
	public int vertBase;            ///< The offset of the vertices in the dtMeshTile::detailVerts array.
	public int triBase;            ///< The offset of the triangles in the dtMeshTile::detailTris array.
	public char vertCount;        ///< The number of vertices in the sub-mesh.
	public char triCount;            ///< The number of triangles in the sub-mesh.
}
