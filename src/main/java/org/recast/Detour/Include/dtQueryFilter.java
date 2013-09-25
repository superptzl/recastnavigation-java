package org.recast.Detour.Include;

/**
 * @author igozha
 * @since 22.09.13 22:12
 */ /// Defines polygon filtering and traversal costs for navigation mesh query operations.
/// @ingroup detour
public abstract class dtQueryFilter
{
	public float m_areaCost[] = new float[DetourNavMesh.dtTileFlags.DT_MAX_AREAS];        ///< Cost per area type. (Used by default implementation.)
	public int m_includeFlags;        ///< Flags for polygons that can be visited. (Used by default implementation.)
	public int m_excludeFlags;        ///< Flags for polygons that should not be visted. (Used by default implementation.)

//public:
//	dtQueryFilter();

	/// Returns true if the polygon can be visited.  (I.e. Is traversable.)
	///  @param[in]		ref		The reference id of the polygon test.
	///  @param[in]		tile	The tile containing the polygon.
	///  @param[in]		poly  The polygon to test.
//#ifdef DT_VIRTUAL_QUERYFILTER
	public abstract boolean passFilter(dtPoly ref,
									   dtMeshTile tile,
									   dtPoly poly);

	//#else
	public abstract boolean passFilter(dtPoly ref,
									   dtMeshTile[] tile,
									   dtPoly[] poly);
//#endif

	/// Returns cost to move from the beginning to the end of a line segment
	/// that is fully contained within a polygon.
	///  @param[in]		pa			The start position on the edge of the previous and current polygon. [(x, y, z)]
	///  @param[in]		pb			The end position on the edge of the current and next polygon. [(x, y, z)]
	///  @param[in]		prevRef		The reference id of the previous polygon. [opt]
	///  @param[in]		prevTile	The tile containing the previous polygon. [opt]
	///  @param[in]		prevPoly	The previous polygon. [opt]
	///  @param[in]		curRef		The reference id of the current polygon.
	///  @param[in]		curTile		The tile containing the current polygon.
	///  @param[in]		curPoly		The current polygon.
	///  @param[in]		nextRef		The refernece id of the next polygon. [opt]
	///  @param[in]		nextTile	The tile containing the next polygon. [opt]
	///  @param[in]		nextPoly	The next polygon. [opt]
//#ifdef DT_VIRTUAL_QUERYFILTER
//	virtual float getCost(float* pa, float* pb,
//						  dtPoly prevRef, dtMeshTile* prevTile, dtPoly* prevPoly,
//						  dtPoly curRef, dtMeshTile* curTile, dtPoly* curPoly,
//						  dtPoly nextRef, dtMeshTile* nextTile, dtPoly* nextPoly) ;
//#else
	public abstract float getCost(float[] pa, float[] pb,
								  dtPoly prevRef, dtMeshTile prevTile, dtPoly prevPoly,
								  dtPoly curRef, dtMeshTile curTile, dtPoly curPoly,
								  dtPoly nextRef, dtMeshTile nextTile, dtPoly nextPoly);
//#endif

	/// @name Getters and setters for the default implementation data.
	///@{

	/// Returns the traversal cost of the area.
	///  @param[in]		i		The id of the area.
	/// @returns The traversal cost of the area.
	public float getAreaCost(int i)
	{
		return m_areaCost[i];
	}

	/// Sets the traversal cost of the area.
	///  @param[in]		i		The id of the area.
	///  @param[in]		cost	The new cost of traversing the area.
	public void setAreaCost(int i, float cost)
	{
		m_areaCost[i] = cost;
	}

	/// Returns the include flags for the filter.
	/// Any polygons that include one or more of these flags will be
	/// included in the operation.
	public int getIncludeFlags()
	{
		return m_includeFlags;
	}

	/// Sets the include flags for the filter.
	/// @param[in]		flags	The new flags.
	public void setIncludeFlags(int flags)
	{
		m_includeFlags = flags;
	}

	/// Returns the exclude flags for the filter.
	/// Any polygons that include one ore more of these flags will be
	/// excluded from the operation.
	public int getExcludeFlags()
	{
		return m_excludeFlags;
	}

	/// Sets the exclude flags for the filter.
	/// @param[in]		flags		The new flags.
	public void setExcludeFlags(int flags)
	{
		m_excludeFlags = flags;
	}

	///@}

}
