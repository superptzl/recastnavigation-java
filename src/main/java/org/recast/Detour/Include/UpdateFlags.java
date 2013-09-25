package org.recast.Detour.Include;

/**
 * @author igozha
 * @since 23.09.13 21:54
 */
public class UpdateFlags
{
	public final static int DT_CROWD_ANTICIPATE_TURNS = 1;
	public final static int DT_CROWD_OBSTACLE_AVOIDANCE = 2;
	public final static int DT_CROWD_SEPARATION = 4;
	public final static int DT_CROWD_OPTIMIZE_VIS = 8;            ///< Use #dtPathCorridor::optimizePathVisibility() to optimize the agent path.
	public final static int DT_CROWD_OPTIMIZE_TOPO = 16;        ///< Use dtPathCorridor::optimizePathTopology() to optimize the agent path.
}
