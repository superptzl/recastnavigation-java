package org.recast.Detour.Include;

/**
 * @author igozha
 * @since 22.09.13 21:53
 */
/// A navigation mesh based on tiles of convex polygons.
/// @ingroup detour
public abstract class dtNavMesh
{
	public dtNavMesh()
	{
	}
	//	public:
//		dtNavMesh();
//		~dtNavMesh();

	/// @{
	/// @name Initialization and Tile Management

	/// Initializes the navigation mesh for tiled use.
	///  @param[in]	params		Initialization parameters.
	/// @return The status flags for the operation.
	public abstract dtStatus init(dtNavMeshParams params);

	/// Initializes the navigation mesh for single tile use.
	///  @param[in]	data		Data of the new tile. (See: #dtCreateNavMeshData)
	///  @param[in]	dataSize	The data size of the new tile.
	///  @param[in]	flags		The tile flags. (See: #dtTileFlags)
	/// @return The status flags for the operation.
	///  @see dtCreateNavMeshData
	public abstract dtStatus init(dtMeshHeader header, int flags);

	/// The navigation mesh initialization params.
	public abstract dtNavMeshParams getParams();

	/// Adds a tile to the navigation mesh.
	///  @param[in]		data		Data for the new tile mesh. (See: #dtCreateNavMeshData)
	///  @param[in]		dataSize	Data size of the new tile mesh.
	///  @param[in]		flags		Tile flags. (See: #dtTileFlags)
	///  @param[in]		lastRef		The desired reference for the tile. (When reloading a tile.) [opt] [Default: 0]
	///  @param[out]	result		The tile reference. (If the tile was succesfully added.) [opt]
	/// @return The status flags for the operation.
	public abstract dtStatus addTile(dtMeshHeader header, int flags, dtMeshTile lastRef, dtMeshTile[] result);

	/// Removes the specified tile from the navigation mesh.
	///  @param[in]		ref			The reference of the tile to remove.
	///  @param[out]	data		Data associated with deleted tile.
	///  @param[out]	dataSize	Size of the data associated with deleted tile.
	/// @return The status flags for the operation.
//    public abstract dtStatus removeTile(dtMeshTile ref, dtMeshHeader header);

	/// @}

	/// @{
	/// @name Query Functions

	/// Calculates the tile grid location for the specified world position.
	///  @param[in]	pos  The world position for the query. [(x, y, z)]
	///  @param[out]	tx		The tile's x-location. (x, y)
	///  @param[out]	ty		The tile's y-location. (x, y)
	public abstract void calcTileLoc(float[] pos, int[] tx, int[] ty);

	/// Gets the tile at the specified grid location.
	///  @param[in]	x		The tile's x-location. (x, y, layer)
	///  @param[in]	y		The tile's y-location. (x, y, layer)
	///  @param[in]	layer	The tile's layer. (x, y, layer)
	/// @return The tile, or null if the tile does not exist.
	public abstract dtMeshTile getTileAt(int x, int y, int layer);

	/// Gets all tiles at the specified grid location. (All layers.)
	///  @param[in]		x			The tile's x-location. (x, y)
	///  @param[in]		y			The tile's y-location. (x, y)
	///  @param[out]	tiles		A pointer to an array of tiles that will hold the result.
	///  @param[in]		maxTiles	The maximum tiles the tiles parameter can hold.
	/// @return The number of tiles returned in the tiles array.
	public abstract int getTilesAt(int x, int y,
								   dtMeshTile[] tiles, int maxTiles);

	/// Gets the tile reference for the tile at specified grid location.
	///  @param[in]	x		The tile's x-location. (x, y, layer)
	///  @param[in]	y		The tile's y-location. (x, y, layer)
	///  @param[in]	layer	The tile's layer. (x, y, layer)
	/// @return The tile reference of the tile, or 0 if there is none.
	public abstract dtMeshTile getTileRefAt(int x, int y, int layer);

	/// Gets the tile reference for the specified tile.
	///  @param[in]	tile	The tile.
	/// @return The tile reference of the tile.
	public abstract dtMeshTile getTileRef(dtMeshTile tile);

	/// Gets the tile for the specified tile reference.
	///  @param[in]	ref		The tile reference of the tile to retrieve.
	/// @return The tile for the specified reference, or null if the
	///		reference is invalid.
//    public abstract dtMeshTile getTileByRef(dtMeshTile ref);

	/// The maximum number of tiles supported by the navigation mesh.
	/// @return The maximum number of tiles supported by the navigation mesh.
	public abstract int getMaxTiles();

	/// Gets the tile at the specified index.
	///  @param[in]	i		The tile index. [Limit: 0 >= index < #getMaxTiles()]
	/// @return The tile at the specified index.
	public abstract dtMeshTile getTile(int i);

	/// Gets the tile and polygon for the specified polygon reference.
	///  @param[in]		ref		The reference for the a polygon.
	///  @param[out]	tile	The tile containing the polygon.
	///  @param[out]	poly	The polygon.
	/// @return The status flags for the operation.
	public abstract dtStatus getTileAndPolyByRef(dtPoly ref, dtMeshTile[] tile, dtPoly[] poly);

	//
//    /// Returns the tile and polygon for the specified polygon reference.
//    ///  @param[in]		ref		A known valid reference for a polygon.
//    ///  @param[out]	tile	The tile containing the polygon.
//    ///  @param[out]	poly	The polygon.
	public abstract void getTileAndPolyByRefUnsafe(dtPoly ref, dtMeshTile[] tile, dtPoly[] poly);

	//
//    /// Checks the validity of a polygon reference.
//    ///  @param[in]	ref		The polygon reference to check.
//    /// @return True if polygon reference is valid for the navigation mesh.
	public abstract boolean isValidPolyRef(dtPoly ref);

	//
//    /// Gets the polygon reference for the tile's base polygon.
//    ///  @param[in]	tile		The tile.
//    /// @return The polygon reference for the base polygon in the specified tile.
	public abstract dtPoly getPolyRefBase(dtMeshTile tile);

	//
//    /// Gets the endpoints for an off-mesh connection, ordered by "direction of travel".
//    ///  @param[in]		prevRef		The reference of the polygon before the connection.
//    ///  @param[in]		polyRef		The reference of the off-mesh connection polygon.
//    ///  @param[out]	startPos	The start position of the off-mesh connection. [(x, y, z)]
//    ///  @param[out]	endPos		The end position of the off-mesh connection. [(x, y, z)]
//    /// @return The status flags for the operation.
	public abstract dtStatus getOffMeshConnectionPolyEndPoints(dtPoly prevRef, dtPoly polyRef, float[] startPos, float[] endPos);
//
//    /// Gets the specified off-mesh connection.
//    ///  @param[in]	ref		The polygon reference of the off-mesh connection.
//    /// @return The specified off-mesh connection, or null if the polygon reference is not valid.
//    public abstract dtOffMeshConnection getOffMeshConnectionByRef(dtPoly ref);
//
//    /// @}
//
//    /// @{
//    /// @name State Management
//    /// These functions do not effect #dtTileRef or #dtPoly's.
//
//    /// Sets the user defined flags for the specified polygon.
//    ///  @param[in]	ref		The polygon reference.
//    ///  @param[in]	flags	The new flags for the polygon.
//    /// @return The status flags for the operation.
//    public abstract dtStatus setPolyFlags(dtPoly ref, short flags);
//
//    /// Gets the user defined flags for the specified polygon.
//    ///  @param[in]		ref				The polygon reference.
//    ///  @param[out]	resultFlags		The polygon flags.
//    /// @return The status flags for the operation.
//    public abstract dtStatus getPolyFlags(dtPoly ref, int[] resultFlags);
//
//    /// Sets the user defined area for the specified polygon.
//    ///  @param[in]	ref		The polygon reference.
//    ///  @param[in]	area	The new area id for the polygon. [Limit: < #DT_MAX_AREAS]
//    /// @return The status flags for the operation.
//    public abstract dtStatus setPolyArea(dtPoly ref, char area);
//
//    /// Gets the user defined area for the specified polygon.
//    ///  @param[in]		ref			The polygon reference.
//    ///  @param[out]	resultArea	The area id for the polygon.
//    /// @return The status flags for the operation.
//    public abstract dtStatus getPolyArea(dtPoly ref, char[] resultArea);
//
//    /// Gets the size of the buffer required by #storeTileState to store the specified tile's state.
//    ///  @param[in]	tile	The tile.
//    /// @return The size of the buffer required to store the state.
//    public abstract int getTileStateSize(dtMeshTile tile);
//
//    /// Stores the non-structural state of the tile in the specified buffer. (Flags, area ids, etc.)
//    ///  @param[in]		tile			The tile.
//    ///  @param[out]	data			The buffer to store the tile's state in.
//    ///  @param[in]		maxDataSize		The size of the data buffer. [Limit: >= #getTileStateSize]
//    /// @return The status flags for the operation.
//    public abstract dtStatus storeTileState(dtMeshTile tile, char[] data, int maxDataSize);
//
//    /// Restores the state of the tile.
//    ///  @param[in]	tile			The tile.
//    ///  @param[in]	data			The new state. (Obtained from #storeTileState.)
//    ///  @param[in]	maxDataSize		The size of the state within the data buffer.
//    /// @return The status flags for the operation.
//    public abstract dtStatus restoreTileState(dtMeshTile tile, char[] data, int maxDataSize);

	/// @}

	/// @{
	/// @name Encoding and Decoding
	/// These functions are generally meant for internal use only.

	/// Derives a standard polygon reference.
	///  @note This function is generally meant for internal use only.
	///  @param[in]	salt	The tile's salt value.
	///  @param[in]	it		The index of the tile.
	///  @param[in]	ip		The index of the polygon within the tile.
//    public dtPoly encodePolyId(int salt, int it, int ip) {
//        return ((dtPoly) salt << (m_polyBits + m_tileBits)) | ((dtPoly) it << m_polyBits) | (dtPoly) ip;
//    }
//
//    /// Decodes a standard polygon reference.
//    ///  @note This function is generally meant for internal use only.
//    ///  @param[in]	ref   The polygon reference to decode.
//    ///  @param[out]	salt	The tile's salt value.
//    ///  @param[out]	it		The index of the tile.
//    ///  @param[out]	ip		The index of the polygon within the tile.
//    ///  @see #encodePolyId
	public void decodePolyId(dtPoly ref, int[] salt, int[] it, int[] ip)
	{
//        dtPoly saltMask = ((dtPoly) 1 << m_saltBits) - 1;
//        dtPoly tileMask = ((dtPoly) 1 << m_tileBits) - 1;
//        dtPoly polyMask = ((dtPoly) 1 << m_polyBits) - 1;
//        salt[0] = (int) ((ref >> (m_polyBits + m_tileBits)) & saltMask);
//        it[0] = (int) ((ref >> m_polyBits) & tileMask);
//        ip[0] = (int) (ref & polyMask);
	}
//
//    /// Extracts a tile's salt value from the specified polygon reference.
//    ///  @note This function is generally meant for internal use only.
//    ///  @param[in]	ref		The polygon reference.
//    ///  @see #encodePolyId
//    public int decodePolyIdSalt(dtPoly ref) {
//        dtPoly saltMask = ((dtPoly) 1 << m_saltBits) - 1;
//        return (int) ((ref >> (m_polyBits + m_tileBits)) & saltMask);
//    }
//
//    /// Extracts the tile's index from the specified polygon reference.
//    ///  @note This function is generally meant for internal use only.
//    ///  @param[in]	ref		The polygon reference.
//    ///  @see #encodePolyId
//    public int decodePolyIdTile(dtPoly ref) {
//        dtPoly tileMask = ((dtPoly) 1 << m_tileBits) - 1;
//        return (int) ((ref >> m_polyBits) & tileMask);
//    }
//
//    /// Extracts the polygon's index (within its tile) from the specified polygon reference.
//    ///  @note This function is generally meant for internal use only.
//    ///  @param[in]	ref		The polygon reference.
//    ///  @see #encodePolyId
//    public int decodePolyIdPoly(dtPoly ref) {
//        dtPoly polyMask = ((dtPoly) 1 << m_polyBits) - 1;
//        return (int) (ref & polyMask);
//    }

	/// @}

//	private:

	/// Returns pointer to tile in the tile array.
//		public abstract dtMeshTile getTile(int i);

	/// Returns neighbour tile based on side.
//		public abstract int getTilesAt(int x, int y,
//					   dtMeshTile[] tiles, int maxTiles);

	/// Returns neighbour tile based on side.
	public abstract int getNeighbourTilesAt(int x, int y, int side,
											dtMeshTile[] tiles, int maxTiles);

	/// Returns all polygons in neighbour tile based on portal defined by the segment.
//    public abstract int findConnectingPolys(float[] va, float[] vb,
//                                            dtMeshTile tile, int side,
//                                            dtPoly con, float[] conarea, int maxcon);
//
//    /// Builds internal polygons links for a tile.
	public abstract void connectIntLinks(dtMeshTile tile);

	//
//    /// Builds internal polygons links for a tile.
	public abstract void baseOffMeshLinks(dtMeshTile tile);

	//
//    /// Builds external polygon links for a tile.
	public abstract void connectExtLinks(dtMeshTile tile, dtMeshTile target, int side);

	//
//    /// Builds external polygon links for a tile.
	public abstract void connectExtOffMeshLinks(dtMeshTile tile, dtMeshTile target, int side);
//
//    /// Removes external links at specified side.
////    public abstract void unconnectExtLinks(dtMeshTile tile, dtMeshTile target);
//
//
//    // TODO: These methods are duplicates from dtNavMeshQuery, but are needed for off-mesh connection finding.
//
//    /// Queries polygons within a tile.
//    public abstract int queryPolygonsInTile(dtMeshTile tile, float[] qmin, float[] qmax,
//                                            dtPoly[] polys, int maxPolys);
//
//    /// Find nearest polygon within a tile.
//    public abstract dtPoly findNearestPolyInTile(dtMeshTile tile, float[] center,
//                                                 float[] extents, float[] nearestPt);
//
//    /// Returns closest point on polygon.
//    public abstract void closestPointOnPolyInTile(dtMeshTile tile, int ip,
//                                                  float[] pos, float[] closest);

	public dtNavMeshParams m_params;            ///< Current initialization params. TODO: do not store this info twice.
	public float m_orig[] = new float[3];                    ///< Origin of the tile (0,0)
	public float m_tileWidth, m_tileHeight;    ///< Dimensions of each tile.
	public int m_maxTiles;                        ///< Max number of tiles.
	public int m_tileLutSize;                    ///< Tile hash lookup size (must be pot).
	public int m_tileLutMask;                    ///< Tile hash lookup mask.

	public dtMeshTile[] m_posLookup;            ///< Tile hash lookup.
	public dtMeshTile m_nextFree;                ///< Freelist of tiles.
	public dtMeshTile[] m_tiles;                ///< List of tiles.

	public int m_saltBits;            ///< Number of salt bits in the tile ID.
	public int m_tileBits;            ///< Number of tile bits in the tile ID.
	public int m_polyBits;            ///< Number of poly bits in the tile ID.
}
