package org.recast.Detour.Include;

/// Defines a navigation mesh tile.
/// @ingroup detour
public abstract class DetourNavMesh
{
	//    public static final int DT_VERTS_PER_POLYGON = 6;
//	public final static int DT_VERTS_PER_POLYGON = 6;
	/// A magic number used to detect compatibility of navigation tile data.
	public final static int DT_NAVMESH_MAGIC = 'D' << 24 | 'N' << 16 | 'A' << 8 | 'V';
	/// A version number used to detect compatibility of navigation tile data.
	public final static int DT_NAVMESH_VERSION = 7;

	/// A flag that indicates that an entity links to an external entity.
	/// (E.g. A polygon edge is a portal that links to another polygon.)
	public final static int DT_EXT_LINK = 0x8000;

	/// A value that indicates the entity does not link to anything.
	public final static int DT_NULL_LINK = 0xffffffff;

//	/ A flag that indicates that an off-mesh connection can be traversed in both directions. (Is bidirectional.)
//	public final static int DT_OFFMESH_CON_BIDIR = 1;

	/// A flag that indicates that an off-mesh connection can be traversed in both directions. (Is bidirectional.)
	public final static int DT_OFFMESH_CON_BIDIR = 1;

	/// The maximum number of vertices per navigation polygon.
	/// @ingroup detour
	public final static int DT_VERTS_PER_POLYGON = 6;

	/// Tile flags used for various functions and fields.
	/// For an example, see dtNavMesh::addTile().
	public static class dtTileFlags
	{
		/// The navigation mesh owns the tile memory and is responsible for freeing it.
		public final static int DT_TILE_FREE_DATA = 0x01;

		/// The maximum number of user defined area ids.
		/// @ingroup detour
		public final static int DT_MAX_AREAS = 64;
	}
}
