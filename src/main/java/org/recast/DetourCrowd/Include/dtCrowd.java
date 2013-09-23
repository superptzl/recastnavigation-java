package org.recast.DetourCrowd.Include;

import org.recast.Detour.Include.dtNavMesh;
import org.recast.Detour.Include.dtNavMeshQuery;
import org.recast.Detour.Include.dtPoly;

public abstract class dtCrowd {
    public final static int MAX_PATHQUEUE_NODES = 4096;
    public final static int MAX_COMMON_NODES = 512;

    public final static int DT_CROWDAGENT_MAX_NEIGHBOURS = 6;

    /// The maximum number of corners a crowd agent will look ahead in the path.
/// This value is used for sizing the crowd agent corner buffers.
/// Due to the behavior of the crowd manager, the actual number of useful
/// corners will be one less than this number.
/// @ingroup crowd
    public final static int DT_CROWDAGENT_MAX_CORNERS = 4;


/// The maximum number of crowd avoidance configurations supported by the
/// crowd manager.
/// @ingroup crowd
/// @see dtObstacleAvoidanceParams, dtCrowd::setObstacleAvoidanceParams(), dtCrowd::getObstacleAvoidanceParams(),
///		 dtCrowdAgentParams::obstacleAvoidanceType
    public final static int DT_CROWD_MAX_OBSTAVOIDANCE_PARAMS = 8;

    public int m_maxAgents;
    public dtCrowdAgent[] m_agents;
    public dtCrowdAgent[] m_activeAgents;
    public dtCrowdAgentAnimation[] m_agentAnims;

    public dtPathQueue m_pathq;

    public dtObstacleAvoidanceParams[] m_obstacleQueryParams = new dtObstacleAvoidanceParams[dtCrowd.DT_CROWD_MAX_OBSTAVOIDANCE_PARAMS];
    public dtObstacleAvoidanceQuery m_obstacleQuery;

    public dtProximityGrid m_grid;

    public dtPoly[] m_pathResult;
    public int m_maxPathResult;

    public float m_ext[] = new float[3];
//    public dtQueryFilter m_filter;

    public float m_maxAgentRadius;

    public int m_velocitySampleCount;

    public dtNavMeshQuery m_navquery;

//    public void updateTopologyOptimization(dtCrowdAgent** agents, const int nagents, const float dt);
//    public void updateMoveRequest(const float dt);
//    public void checkPathValidity(dtCrowdAgent** agents, const int nagents, const float dt);

//    public inline int getAgentIndex(const dtCrowdAgent* agent) const  { return agent - m_agents; }

//    public bool requestMoveTargetReplan(const int idx, dtPolyRef ref, const float* pos);
//
//    public void purge();

//    public:
//    dtCrowd();
//    ~dtCrowd();

    /// Initializes the crowd.
    ///  @param[in]		maxAgents		The maximum number of agents the crowd can manage. [Limit: >= 1]
    ///  @param[in]		maxAgentRadius	The maximum radius of any agent that will be added to the crowd. [Limit: > 0]
    ///  @param[in]		nav				The navigation mesh to use for planning.
    /// @return True if the initialization succeeded.
    public abstract boolean init(int maxAgents, float maxAgentRadius, dtNavMesh nav);

    /// Sets the shared avoidance configuration for the specified index.
    ///  @param[in]		idx		The index. [Limits: 0 <= value < #DT_CROWD_MAX_OBSTAVOIDANCE_PARAMS]
    ///  @param[in]		params	The new configuration.
//    public void setObstacleAvoidanceParams(const int idx, const dtObstacleAvoidanceParams* params);

    /// Gets the shared avoidance configuration for the specified index.
    ///  @param[in]		idx		The index of the configuration to retreive.
    ///							[Limits:  0 <= value < #DT_CROWD_MAX_OBSTAVOIDANCE_PARAMS]
    /// @return The requested configuration.
//    public const dtObstacleAvoidanceParams* getObstacleAvoidanceParams(const int idx) const;

    /// Gets the specified agent from the pool.
    ///	 @param[in]		idx		The agent index. [Limits: 0 <= value < #getAgentCount()]
    /// @return The requested agent.
//    public const dtCrowdAgent* getAgent(const int idx);

    /// The maximum number of agents that can be managed by the object.
//    / @return The maximum number of agents.
//    public const int getAgentCount() const;

    /// Adds a new agent to the crowd.
    ///  @param[in]		pos		The requested position of the agent. [(x, y, z)]
    ///  @param[in]		params	The configutation of the agent.
    /// @return The index of the agent in the agent pool. Or -1 if the agent could not be added.
    public abstract int addAgent(float[] pos, dtCrowdAgentParams params);

    /// Updates the specified agent's configuration.
    ///  @param[in]		idx		The agent index. [Limits: 0 <= value < #getAgentCount()]
    ///  @param[in]		params	The new agent configuration.
    public abstract void updateAgentParameters(int idx, dtCrowdAgentParams params);

    /// Removes the agent from the crowd.
    ///  @param[in]		idx		The agent index. [Limits: 0 <= value < #getAgentCount()]
//    public void removeAgent(const int idx);

    /// Submits a new move request for the specified agent.
    ///  @param[in]		idx		The agent index. [Limits: 0 <= value < #getAgentCount()]
    ///  @param[in]		ref		The position's polygon reference.
    ///  @param[in]		pos		The position within the polygon. [(x, y, z)]
    /// @return True if the request was successfully submitted.
//    public bool requestMoveTarget(const int idx, dtPolyRef ref, const float* pos);

    /// Submits a new move request for the specified agent.
    ///  @param[in]		idx		The agent index. [Limits: 0 <= value < #getAgentCount()]
    ///  @param[in]		vel		The movement velocity. [(x, y, z)]
    /// @return True if the request was successfully submitted.
//    public bool requestMoveVelocity(const int idx, const float* vel);

    /// Resets any request for the specified agent.
    ///  @param[in]		idx		The agent index. [Limits: 0 <= value < #getAgentCount()]
    /// @return True if the request was successfully reseted.
//    public bool resetMoveTarget(const int idx);

    /// Gets the active agents int the agent pool.
    ///  @param[out]	agents		An array of agent pointers. [(#dtCrowdAgent *) * maxAgents]
    ///  @param[in]		maxAgents	The size of the crowd agent array.
    /// @return The number of agents returned in @p agents.
//    public int getActiveAgents(dtCrowdAgent** agents, const int maxAgents);

    /// Updates the steering and positions of all agents.
    ///  @param[in]		dt		The time, in seconds, to update the simulation. [Limit: > 0]
    ///  @param[out]	debug	A debug object to load with debug information. [Opt]
//    public void update(const float dt, dtCrowdAgentDebugInfo* debug);

    /// Gets the filter used by the crowd.
    /// @return The filter used by the crowd.
//    public const dtQueryFilter* getFilter() const { return &m_filter; }

    /// Gets the filter used by the crowd.
    /// @return The filter used by the crowd.
//    public dtQueryFilter* getEditableFilter() { return &m_filter; }

    /// Gets the search extents [(x, y, z)] used by the crowd for query operations.
    /// @return The search extents used by the crowd. [(x, y, z)]
//    public  const float* getQueryExtents() const { return m_ext; }

    /// Gets the velocity sample count.
    /// @return The velocity sample count.
//    public  inline int getVelocitySampleCount() const { return m_velocitySampleCount; }

    /// Gets the crowd's proximity grid.
    /// @return The crowd's proximity grid.
//    public const dtProximityGrid* getGrid() const { return m_grid; }

    /// Gets the crowd's path request queue.
    /// @return The crowd's path request queue.
//    public  const dtPathQueue* getPathQueue() const { return &m_pathq; }

    /// Gets the query object used by the crowd.
//    public const dtNavMeshQuery* getNavMeshQuery() const { return m_navquery; }
}
