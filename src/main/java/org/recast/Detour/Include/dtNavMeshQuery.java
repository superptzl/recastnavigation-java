package org.recast.Detour.Include;

/**
 * @author igozha
 * @since 22.09.13 22:01
 */
public abstract class dtNavMeshQuery
{
	public dtNavMeshQuery()
	{
	}

	//
	// Copyright (c) 2009-2010 Mikko Mononen memon@inside.org
	//
	// This software is provided 'as-is', without any express or implied
	// warranty.  In no event will the authors be held liable for any damages
	// arising from the use of this software.
	// Permission is granted to anyone to use this software for any purpose,
	// including commercial applications, and to alter it and redistribute it
	// freely, subject to the following restrictions:
	// 1. The origin of this software must not be misrepresented; you must not
	//    claim that you wrote the original software. If you use this software
	//    in a product, an acknowledgment in the product documentation would be
	//    appreciated but is not required.
	// 2. Altered source versions must be plainly marked as such, and must not be
	//    misrepresented as being the original software.
	// 3. This notice may not be removed or altered from any source distribution.
	//

//	#ifndef DETOURNAVMESHQUERY_H
//	#define DETOURNAVMESHQUERY_H
//
//	#include "DetourNavMesh.h"
//	#include "DetourStatus.h"

	// Define DT_VIRTUAL_QUERYFILTER if you wish to derive a custom filter from dtQueryFilter.
	// On certain platforms indirect or virtual function call is expensive. The default
	// setting is to use non-virtual functions, the actual implementations of the functions
	// are declared as inline for maximum speed.

	//#define DT_VIRTUAL_QUERYFILTER 1

	;

	/// Provides the ability to perform pathfinding related queries against
	/// a navigation mesh.
	/// @ingroup detour
//	class dtNavMeshQuery
//	{
//	public:
//		dtNavMeshQuery();
//		~dtNavMeshQuery();

	/// Initializes the query object.
	///  @param[in]		nav			Pointer to the dtNavMesh object to use for all queries.
	///  @param[in]		maxNodes	Maximum number of search nodes. [Limits: 0 < value <= 65536]
	/// @returns The status flags for the query.
	public abstract dtStatus init(dtNavMesh nav, int maxNodes);

	/// @name Standard Pathfinding Functions
	// /@{

	/// Finds a path from the start polygon to the end polygon.
	///  @param[in]		startRef	The refrence id of the start polygon.
	///  @param[in]		endRef		The reference id of the end polygon.
	///  @param[in]		startPos	A position within the start polygon. [(x, y, z)]
	///  @param[in]		endPos		A position within the end polygon. [(x, y, z)]
	///  @param[in]		filter		The polygon filter to apply to the query.
	///  @param[out]	path		An ordered list of polygon references representing the path. (Start to end.)
	///  							[(polyRef) * @p pathCount]
	///  @param[out]	pathCount	The number of polygons returned in the @p path array.
	///  @param[in]		maxPath		The maximum number of polygons the @p path array can hold. [Limit: >= 1]
//		public abstract dtStatus findPath(dtPoly startRef, dtPoly endRef,
//						  float[] startPos, float[] endPos,
//						  dtQueryFilter filter,
//						  dtPoly path, int[]pathCount, int maxPath);

	/// Finds the straight path from the start to the end position within the polygon corridor.
	///  @param[in]		startPos			Path start position. [(x, y, z)]
	///  @param[in]		endPos				Path end position. [(x, y, z)]
	///  @param[in]		path				An array of polygon references that represent the path corridor.
	///  @param[in]		pathSize			The number of polygons in the @p path array.
	///  @param[out]	straightPath		Points describing the straight path. [(x, y, z) * @p straightPathCount].
	///  @param[out]	straightPathFlags	Flags describing each point. (See: #dtStraightPathFlags) [opt]
	///  @param[out]	straightPathRefs	The reference id of the polygon that is being entered at each point. [opt]
	///  @param[out]	straightPathCount	The number of points in the straight path.
	///  @param[in]		maxStraightPath		The maximum number of points the straight path arrays can hold.  [Limit: > 0]
	///  @param[in]		options				Query options. (see: #dtStraightPathOptions)
	/// @returns The status flags for the query.
	public abstract dtStatus findStraightPath(float[] startPos, float[] endPos,
											  dtPoly[] path, int pathSize,
											  float[] straightPath, char[] straightPathFlags, dtPoly[] straightPathRefs,
											  int[] straightPathCount, int maxStraightPath, int options/* = 0*/);

	///@}
	/// @name Sliced Pathfinding Functions
	/// Common use case:
	///	-# Call initSlicedFindPath() to initialize the sliced path query.
	///	-# Call updateSlicedFindPath() until it returns complete.
	///	-# Call finalizeSlicedFindPath() to get the path.
	///@{

	/// Intializes a sliced path query.
	///  @param[in]		startRef	The refrence id of the start polygon.
	///  @param[in]		endRef		The reference id of the end polygon.
	///  @param[in]		startPos	A position within the start polygon. [(x, y, z)]
	///  @param[in]		endPos		A position within the end polygon. [(x, y, z)]
	///  @param[in]		filter		The polygon filter to apply to the query.
	/// @returns The status flags for the query.
	public abstract dtStatus initSlicedFindPath(dtPoly startRef, dtPoly endRef,
												float[] startPos, float[] endPos,
												dtQueryFilter filter);

	/// Updates an in-progress sliced path query.
	///  @param[in]		maxIter		The maximum number of iterations to perform.
	///  @param[out]	doneIters	The actual number of iterations completed. [opt]
	/// @returns The status flags for the query.
	public abstract dtStatus updateSlicedFindPath(int maxIter, int[] doneIters);

	/// Finalizes and returns the results of a sliced path query.
	///  @param[out]	path		An ordered list of polygon references representing the path. (Start to end.)
	///  							[(polyRef) * @p pathCount]
	///  @param[out]	pathCount	The number of polygons returned in the @p path array.
	///  @param[in]		maxPath		The max number of polygons the path array can hold. [Limit: >= 1]
	/// @returns The status flags for the query.
	public abstract dtStatus finalizeSlicedFindPath(dtPoly[] path, int[] pathCount, int maxPath);

	/// Finalizes and returns the results of an incomplete sliced path query, returning the path to the furthest
	/// polygon on the existing path that was visited during the search.
	///  @param[out]	existing		An array of polygon references for the existing path.
	///  @param[out]	existingSize	The number of polygon in the @p existing array.
	///  @param[out]	path			An ordered list of polygon references representing the path. (Start to end.)
	///  								[(polyRef) * @p pathCount]
	///  @param[out]	pathCount		The number of polygons returned in the @p path array.
	///  @param[in]		maxPath			The max number of polygons the @p path array can hold. [Limit: >= 1]
	/// @returns The status flags for the query.
	public abstract dtStatus finalizeSlicedFindPathPartial(dtPoly[] existing, int existingSize,
														   dtPoly[] path, int[] pathCount, int maxPath);

	///@}
	/// @name Dijkstra Search Functions
	/// @{

	/// Finds the polygons along the navigation graph that touch the specified circle.
	///  @param[in]		startRef		The reference id of the polygon where the search starts.
	///  @param[in]		centerPos		The center of the search circle. [(x, y, z)]
	///  @param[in]		radius			The radius of the search circle.
	///  @param[in]		filter			The polygon filter to apply to the query.
	///  @param[out]	resultRef		The reference ids of the polygons touched by the circle. [opt]
	///  @param[out]	resultParent	The reference ids of the parent polygons for each result.
	///  								Zero if a result polygon has no parent. [opt]
	///  @param[out]	resultCost		The search cost from @p centerPos to the polygon. [opt]
	///  @param[out]	resultCount		The number of polygons found. [opt]
	///  @param[in]		maxResult		The maximum number of polygons the result arrays can hold.
	/// @returns The status flags for the query.
//		public abstract dtStatus findPolysAroundCircle(dtPoly startRef, float[] centerPos, float radius,
//									   dtQueryFilter filter,
//									   dtPoly resultRef, dtPoly resultParent, float[] resultCost,
//									   int[] resultCount, int maxResult) ;

	/// Finds the polygons along the naviation graph that touch the specified convex polygon.
	///  @param[in]		startRef		The reference id of the polygon where the search starts.
	///  @param[in]		verts			The vertices describing the convex polygon. (CCW)
	///  								[(x, y, z) * @p nverts]
	///  @param[in]		nverts			The number of vertices in the polygon.
	///  @param[in]		filter			The polygon filter to apply to the query.
	///  @param[out]	resultRef		The reference ids of the polygons touched by the search polygon. [opt]
	///  @param[out]	resultParent	The reference ids of the parent polygons for each result. Zero if a
	///  								result polygon has no parent. [opt]
	///  @param[out]	resultCost		The search cost from the centroid point to the polygon. [opt]
	///  @param[out]	resultCount		The number of polygons found.
	///  @param[in]		maxResult		The maximum number of polygons the result arrays can hold.
	/// @returns The status flags for the query.
		/*public abstract dtStatus findPolysAroundShape(dtPoly startRef, const float* verts, const int nverts,
									  const dtQueryFilter* filter,
									  dtPoly* resultRef, dtPoly* resultParent, float* resultCost,
									  int* resultCount, const int maxResult) const;*/

	/// @}
	/// @name Local Query Functions
	///@{

	/// Finds the polygon nearest to the specified center point.
	///  @param[in]		center		The center of the search box. [(x, y, z)]
	///  @param[in]		extents		The search distance along each axis. [(x, y, z)]
	///  @param[in]		filter		The polygon filter to apply to the query.
	///  @param[out]	nearestRef	The reference id of the nearest polygon.
	///  @param[out]	nearestPt	The nearest point on the polygon. [opt] [(x, y, z)]
	/// @returns The status flags for the query.
	public abstract dtStatus findNearestPoly(float[] center, float[] extents,
											 dtQueryFilter filter,
											 dtPoly[] nearestRef, float[] nearestPt);

	/// Finds polygons that overlap the search box.
	///  @param[in]		center		The center of the search box. [(x, y, z)]
	///  @param[in]		extents		The search distance along each axis. [(x, y, z)]
	///  @param[in]		filter		The polygon filter to apply to the query.
	///  @param[out]	polys		The reference ids of the polygons that overlap the query box.
	///  @param[out]	polyCount	The number of polygons in the search result.
	///  @param[in]		maxPolys	The maximum number of polygons the search result can hold.
	/// @returns The status flags for the query.
	public abstract dtStatus queryPolygons(float[] center, float[] extents,
										   dtQueryFilter filter,
										   dtPoly polys[], int[] polyCount, int maxPolys);

	/// Finds the non-overlapping navigation polygons in the local neighbourhood around the center position.
	///  @param[in]		startRef		The reference id of the polygon where the search starts.
	///  @param[in]		centerPos		The center of the query circle. [(x, y, z)]
	///  @param[in]		radius			The radius of the query circle.
	///  @param[in]		filter			The polygon filter to apply to the query.
	///  @param[out]	resultRef		The reference ids of the polygons touched by the circle.
	///  @param[out]	resultParent	The reference ids of the parent polygons for each result.
	///  								Zero if a result polygon has no parent. [opt]
	///  @param[out]	resultCount		The number of polygons found.
	///  @param[in]		maxResult		The maximum number of polygons the result arrays can hold.
	/// @returns The status flags for the query.
//		public abstract dtStatus findLocalNeighbourhood(dtPoly startRef, const float* centerPos, const float radius,
//										const dtQueryFilter* filter,
//										dtPoly* resultRef, dtPoly* resultParent,
//										int* resultCount, const int maxResult) const;

	/// Moves from the start to the end position constrained to the navigation mesh.
	///  @param[in]		startRef		The reference id of the start polygon.
	///  @param[in]		startPos		A position of the mover within the start polygon. [(x, y, x)]
	///  @param[in]		endPos			The desired end position of the mover. [(x, y, z)]
	///  @param[in]		filter			The polygon filter to apply to the query.
	///  @param[out]	resultPos		The result position of the mover. [(x, y, z)]
	///  @param[out]	visited			The reference ids of the polygons visited during the move.
	///  @param[out]	visitedCount	The number of polygons visited during the move.
	///  @param[in]		maxVisitedSize	The maximum number of polygons the @p visited array can hold.
	/// @returns The status flags for the query.
	public abstract dtStatus moveAlongSurface(dtPoly startRef, float[] startPos, float[] endPos,
											  dtQueryFilter filter,
											  float[] resultPos, dtPoly[] visited, int[] visitedCount, int maxVisitedSize);

	/// Casts a 'walkability' ray along the surface of the navigation mesh from
	/// the start position toward the end position.
	///  @param[in]		startRef	The reference id of the start polygon.
	///  @param[in]		startPos	A position within the start polygon representing
	///  							the start of the ray. [(x, y, z)]
	///  @param[in]		endPos		The position to cast the ray toward. [(x, y, z)]
	///  @param[out]	t			The hit parameter. (FLT_MAX if no wall hit.)
	///  @param[out]	hitNormal	The normal of the nearest wall hit. [(x, y, z)]
	///  @param[in]		filter		The polygon filter to apply to the query.
	///  @param[out]	path		The reference ids of the visited polygons. [opt]
	///  @param[out]	pathCount	The number of visited polygons. [opt]
	///  @param[in]		maxPath		The maximum number of polygons the @p path array can hold.
	/// @returns The status flags for the query.
	public abstract dtStatus raycast(dtPoly startRef, float[] startPos, float[] endPos,
									 dtQueryFilter filter,
									 float[] t, float[] hitNormal, dtPoly[] path, int[] pathCount, int maxPath);

	/// Finds the distance from the specified position to the nearest polygon wall.
	///  @param[in]		startRef		The reference id of the polygon containing @p centerPos.
	///  @param[in]		centerPos		The center of the search circle. [(x, y, z)]
	///  @param[in]		maxRadius		The radius of the search circle.
	///  @param[in]		filter			The polygon filter to apply to the query.
	///  @param[out]	hitDist			The distance to the nearest wall from @p centerPos.
	///  @param[out]	hitPos			The nearest position on the wall that was hit. [(x, y, z)]
	///  @param[out]	hitNormal		The normalized ray formed from the wall point to the
	///  								source point. [(x, y, z)]
	/// @returns The status flags for the query.
		/*public abstract dtStatus findDistanceToWall(dtPoly startRef, const float* centerPos, const float maxRadius,
									const dtQueryFilter* filter,
									float* hitDist, float* hitPos, float* hitNormal) const;*/

	/// Returns the segments for the specified polygon, optionally including portals.
	///  @param[in]		ref				The reference id of the polygon.
	///  @param[in]		filter			The polygon filter to apply to the query.
	///  @param[out]	segmentVerts	The segments. [(ax, ay, az, bx, by, bz) * segmentCount]
	///  @param[out]	segmentRefs		The reference ids of each segment's neighbor polygon.
	///  								Or zero if the segment is a wall. [opt] [(parentRef) * @p segmentCount]
	///  @param[out]	segmentCount	The number of segments returned.
	///  @param[in]		maxSegments		The maximum number of segments the result arrays can hold.
	/// @returns The status flags for the query.
//		public abstract dtStatus getPolyWallSegments(dtPoly ref, const dtQueryFilter* filter,
//									 float* segmentVerts, dtPoly* segmentRefs, int* segmentCount,
//									 const int maxSegments) const;

	/// Returns random location on navmesh.
	/// Polygons are chosen weighted by area. The search runs in linear related to number of polygon.
	///  @param[in]		filter			The polygon filter to apply to the query.
	///  @param[in]		frand			Function returning a random number [0..1).
	///  @param[out]	randomRef		The reference id of the random location.
	///  @param[out]	randomPt		The random location.
	/// @returns The status flags for the query.
//		public abstract dtStatus findRandomPoint(const dtQueryFilter* filter, float (*frand)(),
//								 dtPoly* randomRef, float* randomPt) const;

	/// Returns random location on navmesh within the reach of specified location.
	/// Polygons are chosen weighted by area. The search runs in linear related to number of polygon.
	/// The location is not exactly constrained by the circle, but it limits the visited polygons.
	///  @param[in]		startRef		The reference id of the polygon where the search starts.
	///  @param[in]		centerPos		The center of the search circle. [(x, y, z)]
	///  @param[in]		filter			The polygon filter to apply to the query.
	///  @param[in]		frand			Function returning a random number [0..1).
	///  @param[out]	randomRef		The reference id of the random location.
	///  @param[out]	randomPt		The random location. [(x, y, z)]
	/// @returns The status flags for the query.
		/*public abstract dtStatus findRandomPointAroundCircle(dtPoly startRef, const float* centerPos, const float maxRadius,
											 const dtQueryFilter* filter, float (*frand)(),
											 dtPoly* randomRef, float* randomPt) const;*/

	/// Finds the closest point on the specified polygon.
	///  @param[in]		ref			The reference id of the polygon.
	///  @param[in]		pos			The position to check. [(x, y, z)]
	///  @param[out]	closest		The closest point on the polygon. [(x, y, z)]
	/// @returns The status flags for the query.
	public abstract dtStatus closestPointOnPoly(dtPoly ref, float[] pos, float[] closest);

	/// Returns a point on the boundary closest to the source point if the source point is outside the
	/// polygon's xz-bounds.
	///  @param[in]		ref			The reference id to the polygon.
	///  @param[in]		pos			The position to check. [(x, y, z)]
	///  @param[out]	closest		The closest point. [(x, y, z)]
	/// @returns The status flags for the query.
	public abstract dtStatus closestPointOnPolyBoundary(dtPoly ref, float[] pos, float[] closest);

	/// Gets the height of the polygon at the provided position using the height detail. (Most accurate.)
	///  @param[in]		ref			The reference id of the polygon.
	///  @param[in]		pos			A position within the xz-bounds of the polygon. [(x, y, z)]
	///  @param[out]	height		The height at the surface of the polygon.
	/// @returns The status flags for the query.
	public abstract dtStatus getPolyHeight(dtPoly ref, float[] pos, float[] height);

	/// @}
	/// @name Miscellaneous Functions
	/// @{

	/// Returns true if the polygon reference is valid and passes the filter restrictions.
	///  @param[in]		ref			The polygon reference to check.
	///  @param[in]		filter		The filter to apply.
	public abstract boolean isValidPolyRef(dtPoly ref, dtQueryFilter filter);

	/// Returns true if the polygon reference is in the closed list.
	///  @param[in]		ref		The reference id of the polygon to check.
	/// @returns True if the polygon is in closed list.
//		public abstract boolean isInClosedList(dtPoly ref) const;

	/// Gets the node pool.
	/// @returns The node pool.
//		public class dtNodePool* getNodePool() const { return m_nodePool; }

	/// Gets the navigation mesh the query object is using.
	/// @return The navigation mesh the query object is using.
	public dtNavMesh getAttachedNavMesh()
	{
		return m_nav;
	}

	/// @}

//	private:

	/// Returns neighbour tile based on side.
//	public abstract dtMeshTile* getNeighbourTileAt(int x, int y, int side) const;
//
//		/// Queries polygons within a tile.
	public abstract int queryPolygonsInTile(dtMeshTile tile, float[] qmin, float[] qmax, dtQueryFilter filter,
											dtPoly[] polys, int polysIndex, int maxPolys);

	//		/// Find nearest polygon within a tile.
//		public abstract dtPoly findNearestPolyInTile(const dtMeshTile* tile, const float* center, const float* extents,
//										const dtQueryFilter* filter, float* nearestPt) const;
//		/// Returns closest point on polygon.
	public abstract void closestPointOnPolyInTile(dtMeshTile tile, dtPoly poly, float[] pos, float[] closest);

	//
//		/// Returns portal points between two polygons.
	public abstract dtStatus getPortalPoints(dtPoly from, dtPoly to, float[] left, float[] right,
											 char[] fromType, char[] toType);

	public abstract dtStatus getPortalPoints(dtPoly from, dtPoly fromPoly, dtMeshTile fromTile,
											 dtPoly to, dtPoly toPoly, dtMeshTile toTile,
											 float[] left, float[] right);

	//
//		/// Returns edge mid point between two polygons.
	public abstract dtStatus getEdgeMidPoint(dtPoly from, dtPoly to, float[] mid);

	public abstract dtStatus getEdgeMidPoint(dtPoly from, dtPoly fromPoly, dtMeshTile fromTile,
											 dtPoly to, dtPoly toPoly, dtMeshTile toTile,
											 float[] mid);

	//
//		// Appends vertex to a straight path
	public abstract dtStatus appendVertex(float[] pos, char flags, dtPoly ref,
										  float[] straightPath, char[] straightPathFlags, dtPoly[] straightPathRefs,
										  int[] straightPathCount, int maxStraightPath);

	//
//		// Appends intermediate portal points to a straight path.
	public abstract dtStatus appendPortals(int startIdx, int endIdx, float[] endPos, dtPoly[] path,
										   float[] straightPath, char[] straightPathFlags, dtPoly[] straightPathRefs,
										   int[] straightPathCount, int maxStraightPath, int options);

	public dtNavMesh m_nav;                ///< Pointer to navmesh data.

	public static class dtQueryData
	{
		public dtStatus status;
		public dtNode lastBestNode;
		public float lastBestNodeCost;
		public dtPoly startRef, endRef;
		public float startPos[] = new float[3], endPos[] = new float[3];
		public dtQueryFilter filter;
	}

	public dtQueryData m_query;                ///< Sliced query state.

	public dtNodePool m_tinyNodePool;    ///< Pointer to small node pool.
	public dtNodePool m_nodePool;        ///< Pointer to node pool.
	public dtNodeQueue m_openList;        ///< Pointer to open list queue.
//	};

	/// Allocates a query object using the Detour allocator.
	/// @return An allocated query object, or null on failure.
	/// @ingroup detour
//	dtNavMeshQuery* dtAllocNavMeshQuery();

	/// Frees the specified query object using the Detour allocator.
	///  @param[in]		query		A query object allocated using #dtAllocNavMeshQuery
	/// @ingroup detour
//	void dtFreeNavMeshQuery(dtNavMeshQuery* query);

//	#endif // DETOURNAVMESHQUERY_H

}
