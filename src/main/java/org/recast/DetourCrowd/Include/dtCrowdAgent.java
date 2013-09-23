package org.recast.DetourCrowd.Include;

import org.recast.Detour.Include.dtPoly;

public class dtCrowdAgent {
    /// 1 if the agent is active, or 0 if the agent is in an unused slot in the agent pool.
    public char active;

    /// The type of mesh polygon the agent is traversing. (See: #CrowdAgentState)
    public CrowdAgentState state;

    /// The path corridor the agent is using.
    public dtPathCorridor corridor;

    /// The local boundary data for the agent.
    public dtLocalBoundary boundary;

    /// Time since the agent's path corridor was optimized.
    public float topologyOptTime;

    /// The known neighbors of the agent.
    public dtCrowdNeighbour[] neis = new dtCrowdNeighbour[dtCrowd.DT_CROWDAGENT_MAX_NEIGHBOURS];

    /// The number of neighbors.
    public int nneis;

    /// The desired speed.
    public float desiredSpeed;

    public float npos[] = new float[3];		///< The current agent position. [(x, y, z)]
    public float disp[] = new float[3];
    public float dvel[] = new float[3];		///< The desired velocity of the agent. [(x, y, z)]
    public float nvel[] = new float[3];
    public float vel[] = new float[3];		///< The actual velocity of the agent. [(x, y, z)]

    /// The agent's configuration parameters.
    public dtCrowdAgentParams params;

    /// The local path corridor corners for the agent. (Staight path.) [(x, y, z) * #ncorners]
    public  float cornerVerts[] = new float[dtCrowd.DT_CROWDAGENT_MAX_CORNERS*3];

    /// The local path corridor corner flags. (See: #dtStraightPathFlags) [(flags) * #ncorners]
    public char cornerFlags[] = new char[dtCrowd.DT_CROWDAGENT_MAX_CORNERS];

    /// The reference id of the polygon being entered at the corner. [(polyRef) * #ncorners]
    public  dtPoly[] cornerPolys = new dtPoly[dtCrowd.DT_CROWDAGENT_MAX_CORNERS];

    /// The number of corners.
    public  int ncorners;

    public MoveRequestState targetState;			///< State of the movement request.
    public dtPoly targetRef;				///< Target polyref of the movement request.
    public float targetPos[] = new float[3];					///< Target position of the movement request (or velocity in case of DT_CROWDAGENT_TARGET_VELOCITY).
//    public dtPathQueue targetPathqRef;		///< Path finder ref.
    public boolean targetReplan;					///< Flag indicating that the current path is being replanned.
    public float targetReplanTime;				/// <Time since the agent's target was replanned.
}
