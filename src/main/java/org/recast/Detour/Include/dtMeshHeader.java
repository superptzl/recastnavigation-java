package org.recast.Detour.Include;

/**
 * @author igozha
 * @since 22.09.13 20:58
 */
/// Provides high level information related to a dtMeshTile object.
/// @ingroup detour
public class dtMeshHeader
{
	public int magic;                ///< Tile magic number. (Used to identify the data format.)
	public int version;            ///< Tile data format version number.
	public int x;                    ///< The x-position of the tile within the dtNavMesh tile grid. (x, y, layer)
	public int y;                    ///< The y-position of the tile within the dtNavMesh tile grid. (x, y, layer)
	public int layer;                ///< The layer of the tile within the dtNavMesh tile grid. (x, y, layer)
	public int userId;    ///< The user defined id of the tile.
	public int polyCount;            ///< The number of polygons in the tile.
	public int vertCount;            ///< The number of vertices in the tile.
	public int maxLinkCount;        ///< The number of allocated links.
	public int detailMeshCount;    ///< The number of sub-meshes in the detail mesh.

	/// The number of unique vertices in the detail mesh. (In addition to the polygon vertices.)
	public int detailVertCount;

	public int detailTriCount;            ///< The number of triangles in the detail mesh.
	public int bvNodeCount;            ///< The number of bounding volume nodes. (Zero if bounding volumes are disabled.)
	public int offMeshConCount;        ///< The number of off-mesh connections.
	public int offMeshBase;            ///< The index of the first polygon which is an off-mesh connection.
	public float walkableHeight;        ///< The height of the agents using the tile.
	public float walkableRadius;        ///< The radius of the agents using the tile.
	public float walkableClimb;        ///< The maximum climb height of the agents using the tile.
	public float bmin[] = new float[3];                ///< The minimum bounds of the tile's AABB. [(x, y, z)]
	public float bmax[] = new float[3];                ///< The maximum bounds of the tile's AABB. [(x, y, z)]

	/// The bounding volume quantization factor.
	public float bvQuantFactor;
}
