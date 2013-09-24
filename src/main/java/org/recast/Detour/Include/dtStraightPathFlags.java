package org.recast.Detour.Include;

/// Vertex flags returned by dtNavMeshQuery::findStraightPath.
public class dtStraightPathFlags {
    public static final int DT_STRAIGHTPATH_START = 0x01;				///< The vertex is the start position in the path.
    public static final int DT_STRAIGHTPATH_END = 0x02;					///< The vertex is the end position in the path.
    public static final int DT_STRAIGHTPATH_OFFMESH_CONNECTION = 0x04;	///< The vertex is the start of an off-mesh connection.
}
