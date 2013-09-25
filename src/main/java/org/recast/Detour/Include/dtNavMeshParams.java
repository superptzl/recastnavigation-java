package org.recast.Detour.Include;

/**
 * @author igozha
 * @since 22.09.13 22:08
 */
/// Configuration parameters used to define multi-tile navigation meshes.
/// The values are used to allocate space during the initialization of a navigation mesh.
/// @see dtNavMesh::init()
/// @ingroup detour
public class dtNavMeshParams
{
	public float orig[] = new float[3];                    ///< The world space origin of the navigation mesh's tile space. [(x, y, z)]
	public float tileWidth;                ///< The width of each tile. (Along the x-axis.)
	public float tileHeight;                ///< The height of each tile. (Along the z-axis.)
	public int maxTiles;                    ///< The maximum number of tiles the navigation mesh can contain.
	public int maxPolys;                    ///< The maximum number of polygons each tile can contain.
}
