package org.recast.DetourCrowd.Include;

import org.recast.Detour.Include.dtNavMeshQuery;
import org.recast.Detour.Include.dtPoly;
import org.recast.Detour.Include.dtQueryFilter;

public abstract class dtPathCorridor {
    public float m_pos[] = new float[3];
    public float m_target[] = new float[3];

    public dtPoly[] m_path;
    public int m_npath;
    public int m_maxPath;

//    public:
//    dtPathCorridor();
//    ~dtPathCorridor();

    /// Allocates the corridor's path buffer.
    ///  @param[in]		maxPath		The maximum path size the corridor can handle.
    /// @return True if the initialization succeeded.
    public abstract boolean init(int maxPath);

    /// Resets the path corridor to the specified position.
    ///  @param[in]		ref		The polygon reference containing the position.
    ///  @param[in]		pos		The new position in the corridor. [(x, y, z)]
    public abstract void reset(dtPoly ref,  float[] pos);

    /// Finds the corners in the corridor from the position toward the target. (The straightened path.)
    ///  @param[out]	cornerVerts		The corner vertices. [(x, y, z) * cornerCount] [Size: <= maxCorners]
    ///  @param[out]	cornerFlags		The flag for each corner. [(flag) * cornerCount] [Size: <= maxCorners]
    ///  @param[out]	cornerPolys		The polygon reference for each corner. [(polyRef) * cornerCount]
    ///  								[Size: <= @p maxCorners]
    ///  @param[in]		maxCorners		The maximum number of corners the buffers can hold.
    ///  @param[in]		navquery		The query object used to build the corridor.
    ///  @param[in]		filter			The filter to apply to the operation.
    /// @return The number of corners returned in the corner buffers. [0 <= value <= @p maxCorners]
    public abstract int findCorners(float[] cornerVerts, char[] cornerFlags,
                    dtPoly[] cornerPolys,  int maxCorners,
                    dtNavMeshQuery navquery,  dtQueryFilter filter);
//
//    /// Attempts to optimize the path if the specified point is visible from the current position.
//    ///  @param[in]		next					The point to search toward. [(x, y, z])
//    ///  @param[in]		pathOptimizationRange	The maximum range to search. [Limit: > 0]
//    ///  @param[in]		navquery				The query object used to build the corridor.
//    ///  @param[in]		filter					The filter to apply to the operation.
    public abstract void optimizePathVisibility( float[] next,  int nextIndex, float pathOptimizationRange,
                                dtNavMeshQuery navquery,  dtQueryFilter filter);
//
//    /// Attempts to optimize the path using a local area search. (Partial replanning.)
//    ///  @param[in]		navquery	The query object used to build the corridor.
//    ///  @param[in]		filter		The filter to apply to the operation.
    public abstract  boolean optimizePathTopology(dtNavMeshQuery navquery,  dtQueryFilter filter);
//
    public abstract boolean moveOverOffmeshConnection(dtPoly offMeshConRef, dtPoly refs,
                                   float[] startPos, float[] endPos,
                                   dtNavMeshQuery navquery);
//
    public abstract  boolean fixPathStart(dtPoly safeRef,  float[] safePos);
//
//    public abstract boolean trimInvalidPath(dtPoly safeRef,  float[] safePos,
//                         dtNavMeshQuery navquery,  dtQueryFilter filter);
//
//    /// Checks the current corridor path to see if its polygon references remain valid.
//    ///  @param[in]		maxLookAhead	The number of polygons from the beginning of the corridor to search.
//    ///  @param[in]		navquery		The query object used to build the corridor.
//    ///  @param[in]		filter			The filter to apply to the operation.
    public abstract boolean isValid( int maxLookAhead, dtNavMeshQuery navquery,  dtQueryFilter filter);
//
//    /// Moves the position from the current location to the desired location, adjusting the corridor
//    /// as needed to reflect the change.
//    ///  @param[in]		npos		The desired new position. [(x, y, z)]
//    ///  @param[in]		navquery	The query object used to build the corridor.
//    ///  @param[in]		filter		The filter to apply to the operation.
    public abstract void movePosition( float[] npos, dtNavMeshQuery navquery,  dtQueryFilter filter);
//
//    /// Moves the target from the curent location to the desired location, adjusting the corridor
//    /// as needed to reflect the change.
//    ///  @param[in]		npos		The desired new target position. [(x, y, z)]
//    ///  @param[in]		navquery	The query object used to build the corridor.
//    ///  @param[in]		filter		The filter to apply to the operation.
//    public abstract void moveTargetPosition( float[] npos, dtNavMeshQuery navquery,  dtQueryFilter filter);
//
//    /// Loads a new path and target into the corridor.
//    ///  @param[in]		target		The target location within the last polygon of the path. [(x, y, z)]
//    ///  @param[in]		path		The path corridor. [(polyRef) * @p npolys]
//    ///  @param[in]		npath		The number of polygons in the path.
    public abstract void setCorridor( float[] target,  dtPoly[] polys,  int npath);
//
//    /// Gets the current position within the corridor. (In the first polygon.)
//    /// @return The current position within the corridor.
    public float[] getPos()  { return m_pos; }
//
//    /// Gets the current target within the corridor. (In the last polygon.)
//    /// @return The current target within the corridor.
    public float[] getTarget()  { return m_target; }
//
//    /// The polygon reference id of the first polygon in the corridor, the polygon containing the position.
//    /// @return The polygon reference id of the first polygon in the corridor. (Or zero if there is no path.)
    public dtPoly getFirstPoly()  { return m_npath != 0? m_path[0] : null; }
//
//    /// The polygon reference id of the last polygon in the corridor, the polygon containing the target.
//    /// @return The polygon reference id of the last polygon in the corridor. (Or zero if there is no path.)
    public dtPoly getLastPoly()  { return m_npath != 0 ? m_path[m_npath-1] : null; }
//
//    /// The corridor's path.
//    /// @return The corridor's path. [(polyRef) * #getPathCount()]
    public  dtPoly[] getPath()  { return m_path; }
//
//    /// The number of polygons in the current corridor path.
//    /// @return The number of polygons in the current corridor path.
    public int getPathCount()  { return m_npath; }

	public abstract int dtMergeCorridorStartMoved(dtPoly[] path, int npath, int maxPath,
								  dtPoly[] visited, int nvisited);

//	int dtMergeCorridorEndMoved(dtPolyRef* path, const int npath, const int maxPath,
//								const dtPolyRef* visited, const int nvisited);
//
//	int dtMergeCorridorStartShortcut(dtPolyRef* path, const int npath, const int maxPath,
//									 const dtPolyRef* visited, const int nvisited);
}
