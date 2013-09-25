package org.recast.Detour.Include;

/**
 * @author igozha
 * @since 22.09.13 21:18
 */
public class dtMeshTile
{
	public int salt;                    ///< Counter describing modifications to the tile.

	public int linksFreeList;            ///< Index to the next free link.
	public dtMeshHeader header;                ///< The tile header.
	public dtPoly[] polys;                        ///< The tile polygons. [Size: dtMeshHeader::polyCount]
	public float[] verts;                        ///< The tile vertices. [Size: dtMeshHeader::vertCount]
	public dtLink[] links;                        ///< The tile links. [Size: dtMeshHeader::maxLinkCount]
	public dtPolyDetail[] detailMeshes;            ///< The tile's detail sub-meshes. [Size: dtMeshHeader::detailMeshCount]

	/// The detail mesh's unique vertices. [(x, y, z) * dtMeshHeader::detailVertCount]
	public float[] detailVerts;

	/// The detail mesh's triangles. [(vertA, vertB, vertC) * dtMeshHeader::detailTriCount]
	public char[] detailTris;

	/// The tile bounding volume nodes. [Size: dtMeshHeader::bvNodeCount]
	/// (Will be null if bounding volumes are disabled.)
	public dtBVNode[] bvTree;

	public dtOffMeshConnection[] offMeshCons;        ///< The tile off-mesh connections. [Size: dtMeshHeader::offMeshConCount]

	//	public char[] data;                    ///< The tile data. (Not directly accessed under normal situations.)
//	public int dataSize;                            ///< Size of the tile data.
	public int flags;                                ///< Tile flags. (See: #dtTileFlags)
	public dtMeshTile next;                        ///< The next free tile, or the next tile in the spatial grid.
}
