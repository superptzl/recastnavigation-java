package org.recast.DetourCrowd.Include;

import org.recast.Detour.Include.*;

public abstract class dtCrowd {
    public final static int MAX_PATHQUEUE_NODES = 4096;
    public final static int MAX_COMMON_NODES = 512;
    public final static int MAX_ITERS_PER_UPDATE = 100;

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
    public dtQueryFilter m_filter;

    public float m_maxAgentRadius;

    public int m_velocitySampleCount;

    public dtNavMeshQuery m_navquery;

    public abstract void updateTopologyOptimization(dtCrowdAgent[] agents, int nagents, float dt);
    public abstract void updateMoveRequest(float dt);
    public abstract void checkPathValidity(dtCrowdAgent[] agents, int nagents, float dt);

    public int getAgentIndex(dtCrowdAgent agent) {
        for (int i = 0; i < m_agents.length; i++) {
            if (m_agents[i] == agent) {
                return i;
            }
        }
        return -1;
    }

    public abstract boolean requestMoveTargetReplan(int idx, dtPoly ref, float[] pos);
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
    public abstract void setObstacleAvoidanceParams(int idx, dtObstacleAvoidanceParams params);

    /// Gets the shared avoidance configuration for the specified index.
    ///  @param[in]		idx		The index of the configuration to retreive.
    ///							[Limits:  0 <= value < #DT_CROWD_MAX_OBSTAVOIDANCE_PARAMS]
    /// @return The requested configuration.
    public abstract dtObstacleAvoidanceParams getObstacleAvoidanceParams(int idx);

    /// Gets the specified agent from the pool.
    ///	 @param[in]		idx		The agent index. [Limits: 0 <= value < #getAgentCount()]
    /// @return The requested agent.
    public abstract dtCrowdAgent getAgent(int idx);

    /// The maximum number of agents that can be managed by the object.
//    / @return The maximum number of agents.
    public abstract int getAgentCount();

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
    public abstract boolean requestMoveTarget(int idx, dtPoly ref, float[] pos);

    /// Submits a new move request for the specified agent.
    ///  @param[in]		idx		The agent index. [Limits: 0 <= value < #getAgentCount()]
    ///  @param[in]		vel		The movement velocity. [(x, y, z)]
    /// @return True if the request was successfully submitted.
    public abstract boolean requestMoveVelocity(int idx, float[] vel);

    /// Resets any request for the specified agent.
    ///  @param[in]		idx		The agent index. [Limits: 0 <= value < #getAgentCount()]
    /// @return True if the request was successfully reseted.
//    public bool resetMoveTarget(const int idx);

    /// Gets the active agents int the agent pool.
    ///  @param[out]	agents		An array of agent pointers. [(#dtCrowdAgent *) * maxAgents]
    ///  @param[in]		maxAgents	The size of the crowd agent array.
    /// @return The number of agents returned in @p agents.
    public abstract int getActiveAgents(dtCrowdAgent[] agents, int maxAgents);

    /// Updates the steering and positions of all agents.
    ///  @param[in]		dt		The time, in seconds, to update the simulation. [Limit: > 0]
    ///  @param[out]	debug	A debug object to load with debug information. [Opt]
    public abstract void update(float dt, dtCrowdAgentDebugInfo debug);

    /// Gets the filter used by the crowd.
    /// @return The filter used by the crowd.
    public dtQueryFilter getFilter() { return m_filter; }

    /// Gets the filter used by the crowd.
    /// @return The filter used by the crowd.
    public dtQueryFilter getEditableFilter() { return m_filter; }

    /// Gets the search extents [(x, y, z)] used by the crowd for query operations.
    /// @return The search extents used by the crowd. [(x, y, z)]
    public float[] getQueryExtents() { return m_ext; }

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

    public static int addToPathQueue(dtCrowdAgent newag, dtCrowdAgent[] agents, int nagents, int maxAgents)
    {
        // Insert neighbour based on greatest time.
        int slot = 0;
        if (!nagents)
        {
            slot = nagents;
        }
        else if (newag->targetReplanTime <= agents[nagents-1]->targetReplanTime)
        {
            if (nagents >= maxAgents)
                return nagents;
            slot = nagents;
        }
        else
        {
            int i;
            for (i = 0; i < nagents; ++i)
                if (newag->targetReplanTime >= agents[i]->targetReplanTime)
            break;

            const int tgt = i+1;
            const int n = dtMin(nagents-i, maxAgents-tgt);

            dtAssert(tgt+n <= maxAgents);

            if (n > 0)
                memmove(&agents[tgt], &agents[i], sizeof(dtCrowdAgent*)*n);
            slot = i;
        }

        agents[slot] = newag;

        return DetourCommon.dtMin(nagents+1, maxAgents);
    }

    public static int addToOptQueue(dtCrowdAgent newag, dtCrowdAgent[] agents, int nagents, int maxAgents)
    {
        // Insert neighbour based on greatest time.
        int slot = 0;
        if (!nagents)
        {
            slot = nagents;
        }
        else if (newag->topologyOptTime <= agents[nagents-1]->topologyOptTime)
        {
            if (nagents >= maxAgents)
                return nagents;
            slot = nagents;
        }
        else
        {
            int i;
            for (i = 0; i < nagents; ++i)
                if (newag->topologyOptTime >= agents[i]->topologyOptTime)
            break;

            const int tgt = i+1;
            const int n = dtMin(nagents-i, maxAgents-tgt);

//            dtAssert(tgt+n <= maxAgents);

            if (n > 0)
                memmove(&agents[tgt], &agents[i], sizeof(dtCrowdAgent*)*n);
            slot = i;
        }

        agents[slot] = newag;

        return DetourCommon.dtMin(nagents+1, maxAgents);
    }

    public static int getNeighbours(float[] pos, float height, float range,
                             dtCrowdAgent skip, dtCrowdNeighbour[] result, int maxResult,
                             dtCrowdAgent[] agents, int nagents, dtProximityGrid grid)
    {
        int n = 0;

        int MAX_NEIS = 32;
        int ids[] = new int[MAX_NEIS];
        int nids = grid.queryItems(pos[0]-range, pos[2]-range,
                pos[0]+range, pos[2]+range,
                ids, MAX_NEIS);

        for (int i = 0; i < nids; ++i)
        {
            dtCrowdAgent ag = agents[ids[i]];

            if (ag == skip) continue;

            // Check for overlap.
            float diff[3];
            DetourCommon.dtVsub(diff, pos, ag.npos);
            if (fabsf(diff[1]) >= (height+ag.params.height)/2.0f)
            continue;
            diff[1] = 0;
            const float distSqr = dtVlenSqr(diff);
            if (distSqr > dtSqr(range))
                continue;

            n = addNeighbour(ids[i], distSqr, result, n, maxResult);
        }
        return n;
    }

    public static int addNeighbour(int idx, float dist,
                            dtCrowdNeighbour[] neis, int nneis, int maxNeis)
    {
        // Insert neighbour based on the distance.
        dtCrowdNeighbour* nei = 0;
        if (!nneis)
        {
            nei = &neis[nneis];
        }
        else if (dist >= neis[nneis-1].dist)
        {
            if (nneis >= maxNeis)
                return nneis;
            nei = &neis[nneis];
        }
        else
        {
            int i;
            for (i = 0; i < nneis; ++i)
                if (dist <= neis[i].dist)
                    break;

            const int tgt = i+1;
            const int n = dtMin(nneis-i, maxNeis-tgt);

            dtAssert(tgt+n <= maxNeis);

            if (n > 0)
                memmove(&neis[tgt], &neis[i], sizeof(dtCrowdNeighbour)*n);
            nei = &neis[i];
        }

        memset(nei, 0, sizeof(dtCrowdNeighbour));

        nei->idx = idx;
        nei->dist = dist;

        return DetourCommon.dtMin(nneis+1, maxNeis);
    }

    public static boolean overOffmeshConnection(dtCrowdAgent ag, float radius)
    {
        if (!ag->ncorners)
            return false;

        boolean offMeshConnection = (ag->cornerFlags[ag->ncorners-1] & DT_STRAIGHTPATH_OFFMESH_CONNECTION) ? true : false;
        if (offMeshConnection)
        {
            const float distSq = DetourCommon.dtVdist2DSqr(ag->npos, &ag->cornerVerts[(ag->ncorners-1)*3]);
            if (distSq < radius*radius)
                return true;
        }

        return false;
    }

    public static void calcSmoothSteerDirection(dtCrowdAgent ag, float[] dir)
    {
        if (!ag->ncorners)
        {
            dtVset(dir, 0,0,0);
            return;
        }

        const int ip0 = 0;
        const int ip1 = dtMin(1, ag->ncorners-1);
        const float* p0 = &ag->cornerVerts[ip0*3];
        const float* p1 = &ag->cornerVerts[ip1*3];

        float dir0[3], dir1[3];
        dtVsub(dir0, p0, ag->npos);
        dtVsub(dir1, p1, ag->npos);
        dir0[1] = 0;
        dir1[1] = 0;

        float len0 = dtVlen(dir0);
        float len1 = dtVlen(dir1);
        if (len1 > 0.001f)
            dtVscale(dir1,dir1,1.0f/len1);

        dir[0] = dir0[0] - dir1[0]*len0*0.5f;
        dir[1] = 0;
        dir[2] = dir0[2] - dir1[2]*len0*0.5f;

        dtVnormalize(dir);
    }

    public static void calcStraightSteerDirection(dtCrowdAgent ag, float[] dir)
    {
        if (!ag->ncorners)
        {
            dtVset(dir, 0,0,0);
            return;
        }
        dtVsub(dir, &ag->cornerVerts[0], ag->npos);
        dir[1] = 0;
        dtVnormalize(dir);
    }

    public static float getDistanceToGoal(dtCrowdAgent ag, float range)
    {
        if (!ag->ncorners)
            return range;

        const bool endOfPath = (ag->cornerFlags[ag->ncorners-1] & DT_STRAIGHTPATH_END) ? true : false;
        if (endOfPath)
            return dtMin(dtVdist2D(ag->npos, &ag->cornerVerts[(ag->ncorners-1)*3]), range);

        return range;
    }

    public static float tween(float t, float t0, float t1)
    {
        return DetourCommon.dtClamp((t-t0) / (t1-t0), 0.0f, 1.0f);
    }

    public static void integrate(dtCrowdAgent ag, float dt)
    {
        // Fake dynamic constraint.
        const float maxDelta = ag->params.maxAcceleration * dt;
        float dv[3];
        dtVsub(dv, ag->nvel, ag->vel);
        float ds = dtVlen(dv);
        if (ds > maxDelta)
            dtVscale(dv, dv, maxDelta/ds);
        dtVadd(ag->vel, ag->vel, dv);

        // Integrate
        if (dtVlen(ag->vel) > 0.0001f)
            dtVmad(ag->npos, ag->npos, ag->vel, dt);
        else
            dtVset(ag->vel,0,0,0);
    }

}
