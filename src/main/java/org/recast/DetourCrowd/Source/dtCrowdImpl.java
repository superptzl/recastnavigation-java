package org.recast.DetourCrowd.Source;

import org.recast.Detour.Include.*;
import org.recast.Detour.Source.dtNavMeshQueryImpl;
import org.recast.DetourCrowd.Include.*;

public class dtCrowdImpl extends dtCrowd {
    public dtCrowdImpl()
    {
//        :
//        m_maxAgents(0),
//                m_agents(0),
//                m_activeAgents(0),
//                m_agentAnims(0),
//                m_obstacleQuery(0),
//                m_grid(0),
//                m_pathResult(0),
//                m_maxPathResult(0),
//                m_maxAgentRadius(0),
//                m_velocitySampleCount(0),
//                m_navquery(0)
    }

//    ~dtCrowd()
//    {
//        purge();
//    }

    public void purge()
    {
//        for (int i = 0; i < m_maxAgents; ++i)
//            m_agents[i].~dtCrowdAgent();
//        dtFree(m_agents);
//        m_agents = 0;
//        m_maxAgents = 0;
//
//        dtFree(m_activeAgents);
//        m_activeAgents = 0;
//
//        dtFree(m_agentAnims);
//        m_agentAnims = 0;
//
//        dtFree(m_pathResult);
//        m_pathResult = 0;
//
//        dtFreeProximityGrid(m_grid);
//        m_grid = 0;
//
//        dtFreeObstacleAvoidanceQuery(m_obstacleQuery);
//        m_obstacleQuery = 0;
//
//        dtFreeNavMeshQuery(m_navquery);
//        m_navquery = 0;
    }

    /// @par
///
/// May be called more than once to purge and re-initialize the crowd.
    public boolean init(int maxAgents, float maxAgentRadius, dtNavMesh nav)
    {
        purge();

        m_maxAgents = maxAgents;
        m_maxAgentRadius = maxAgentRadius;

        DetourCommon.dtVset(m_ext, m_maxAgentRadius * 2.0f, m_maxAgentRadius * 1.5f, m_maxAgentRadius * 2.0f);

        m_grid = new dtProximityGridImpl();//dtAllocProximityGrid();
//        if (!m_grid)
//            return false;
        if (!m_grid.init(m_maxAgents*4, maxAgentRadius*3))
            return false;

        m_obstacleQuery = new dtObstacleAvoidanceQueryImpl();//dtAllocObstacleAvoidanceQuery();
//        if (!m_obstacleQuery)
//            return false;
        if (!m_obstacleQuery.init(6, 8))
            return false;

        // Init obstacle query params.
//        memset(m_obstacleQueryParams, 0, sizeof(m_obstacleQueryParams));
        for (int i = 0; i < m_obstacleQueryParams.length; i++) {
            m_obstacleQueryParams[i] = new dtObstacleAvoidanceParams();
        }
        for (int i = 0; i < DT_CROWD_MAX_OBSTAVOIDANCE_PARAMS; ++i)
        {
            dtObstacleAvoidanceParams params = m_obstacleQueryParams[i];
            params.velBias = 0.4f;
            params.weightDesVel = 2.0f;
            params.weightCurVel = 0.75f;
            params.weightSide = 0.75f;
            params.weightToi = 2.5f;
            params.horizTime = 2.5f;
            params.gridSize = 33;
            params.adaptiveDivs = 7;
            params.adaptiveRings = 2;
            params.adaptiveDepth = 5;
        }

        // Allocate temp buffer for merging paths.
        m_maxPathResult = 256;
        m_pathResult = new dtPoly[m_maxPathResult];//(dtPolyRef*)dtAlloc(sizeof(dtPolyRef)*m_maxPathResult, DT_ALLOC_PERM);
//        if (!m_pathResult)
//            return false;
        m_pathq = new dtPathQueueImpl();
        if (!m_pathq.init(m_maxPathResult, dtCrowd.MAX_PATHQUEUE_NODES, nav))
            return false;

        m_agents = new dtCrowdAgent[maxAgents];//*)dtAlloc(sizeof(dtCrowdAgent)*m_maxAgents, DT_ALLOC_PERM);
//        if (!m_agents)
//            return false;

        m_activeAgents = new dtCrowdAgent[m_maxAgents];
//        if (!m_activeAgents)
//            return false;

        m_agentAnims = new dtCrowdAgentAnimation[m_maxAgents];// *)dtAlloc(sizeof(dtCrowdAgentAnimation)*m_maxAgents, DT_ALLOC_PERM);
//        if (!m_agentAnims)
//            return false;

        for (int i = 0; i < m_maxAgents; ++i)
        {
            m_agents[i] = new dtCrowdAgent();
            m_agents[i].active = 0;
            if (!m_agents[i].corridor.init(m_maxPathResult))
                return false;
        }

        for (int i = 0; i < m_maxAgents; ++i)
        {
            m_agentAnims[i] = new dtCrowdAgentAnimation();
            m_agentAnims[i].active = 0;
        }

        // The navquery is mostly used for local searches, no need for large node pool.
        m_navquery = new dtNavMeshQueryImpl();//dtAllocNavMeshQuery();
//        if (!m_navquery)
//            return false;
        if (dtStatus.dtStatusFailed(m_navquery.init(nav, MAX_COMMON_NODES)))
            return false;

        return true;
    }

    public void setObstacleAvoidanceParams(int idx, dtObstacleAvoidanceParams params)
    {
        if (idx >= 0 && idx < DT_CROWD_MAX_OBSTAVOIDANCE_PARAMS)
			m_obstacleQueryParams[idx] = params.clone();
//            memcpy(&, params, sizeof(dtObstacleAvoidanceParams));
    }
//
    public dtObstacleAvoidanceParams getObstacleAvoidanceParams(int idx)
    {
        if (idx >= 0 && idx < DT_CROWD_MAX_OBSTAVOIDANCE_PARAMS)
            return m_obstacleQueryParams[idx];
        return null;
    }
//
public     int getAgentCount()
    {
        return m_maxAgents;
    }
//
///// @par
/////
///// Agents in the pool may not be in use.  Check #dtCrowdAgent.active before using the returned object.
    public dtCrowdAgent getAgent(int idx)
    {
        return m_agents[idx];
    }

    public void updateAgentParameters(int idx, dtCrowdAgentParams params)
    {
        if (idx < 0 || idx > m_maxAgents)
            return;
        m_agents[idx].params = params;
//        memcpy(&m_agents[idx].params, params, sizeof(dtCrowdAgentParams));
//        System.arraycopy(params, 0, , 0, 1);
    }
//
//    /// @par
/////
/// The agent's position will be constrained to the surface of the navigation mesh.
    public int addAgent(float[] pos, dtCrowdAgentParams params)
    {
        // Find empty slot.
        int idx = -1;
        for (int i = 0; i < m_maxAgents; ++i)
        {
            if (m_agents[i].active == 0)
            {
                idx = i;
                break;
            }
        }
        if (idx == -1)
            return -1;

        dtCrowdAgent ag = m_agents[idx];

        // Find nearest position on navmesh and place the agent there.
        float nearest[] = new float[3];
        dtPoly[] ref = new dtPoly[1];
        m_navquery.findNearestPoly(pos, m_ext, m_filter, ref, nearest);

        ag.corridor.reset(ref[0], nearest);
        ag.boundary.reset();

        updateAgentParameters(idx, params);

        ag.topologyOptTime = 0;
        ag.targetReplanTime = 0;
        ag.nneis = 0;

        DetourCommon.dtVset(ag.dvel, 0,0,0);
        DetourCommon.dtVset(ag.nvel, 0,0,0);
        DetourCommon.dtVset(ag.vel, 0,0,0);
        DetourCommon.dtVcopy(ag.npos, nearest);

        ag.desiredSpeed = 0;

        if (ref[0] != null)
            ag.state = CrowdAgentState.DT_CROWDAGENT_STATE_WALKING;
        else
            ag.state = CrowdAgentState.DT_CROWDAGENT_STATE_INVALID;

        ag.targetState = MoveRequestState.DT_CROWDAGENT_TARGET_NONE;

        ag.active = 1;

        return idx;
    }

    /// @par
///
/// The agent is deactivated and will no longer be processed.  Its #dtCrowdAgent object
/// is not removed from the pool.  It is marked as inactive so that it is available for reuse.
//    void removeAgent(const int idx)
//    {
//        if (idx >= 0 && idx < m_maxAgents)
//        {
//            m_agents[idx].active = 0;
//        }
//    }
//
    public boolean requestMoveTargetReplan(int idx, dtPoly ref, float[] pos)
    {
        if (idx < 0 || idx > m_maxAgents)
            return false;

        dtCrowdAgent ag = m_agents[idx];

        // Initialize request.
        ag.targetRef = ref;
        DetourCommon.dtVcopy(ag.targetPos, pos);
        ag.targetPathqRef = null;
        ag.targetReplan = true;
        if (ag.targetRef != null)
            ag.targetState = MoveRequestState.DT_CROWDAGENT_TARGET_REQUESTING;
        else
            ag.targetState = MoveRequestState.DT_CROWDAGENT_TARGET_FAILED;

        return true;
    }
//
//    /// @par
/////
///// This method is used when a new target is set.
/////
///// The position will be constrained to the surface of the navigation mesh.
/////
///// The request will be processed during the next #update().
    public boolean requestMoveTarget(int idx, dtPoly ref, float[] pos)
    {
        if (idx < 0 || idx > m_maxAgents)
            return false;
        if (ref == null)
            return false;

        dtCrowdAgent ag = m_agents[idx];

        // Initialize request.
        ag.targetRef = ref;
        DetourCommon.dtVcopy(ag.targetPos, pos);
        ag.targetPathqRef = null;
        ag.targetReplan = false;
        if (ag.targetRef != null)
            ag.targetState = MoveRequestState.DT_CROWDAGENT_TARGET_REQUESTING;
        else
            ag.targetState = MoveRequestState.DT_CROWDAGENT_TARGET_FAILED;

        return true;
    }

    public boolean requestMoveVelocity(int idx, float[] vel)
    {
        if (idx < 0 || idx > m_maxAgents)
            return false;

        dtCrowdAgent ag = m_agents[idx];

        // Initialize request.
        ag.targetRef = null;
        DetourCommon.dtVcopy(ag.targetPos, vel);
        ag.targetPathqRef = null;
        ag.targetReplan = false;
        ag.targetState = MoveRequestState.DT_CROWDAGENT_TARGET_VELOCITY;

        return true;
    }
//
//    bool resetMoveTarget(const int idx)
//    {
//        if (idx < 0 || idx > m_maxAgents)
//            return false;
//
//        dtCrowdAgent* ag = &m_agents[idx];
//
//        // Initialize request.
//        ag.targetRef = 0;
//        dtVset(ag.targetPos, 0,0,0);
//        ag.targetPathqRef = DT_PATHQ_INVALID;
//        ag.targetReplan = false;
//        ag.targetState = DT_CROWDAGENT_TARGET_NONE;
//
//        return true;
//    }
//
    public int getActiveAgents(dtCrowdAgent[] agents, int maxAgents)
    {
        int n = 0;
        for (int i = 0; i < m_maxAgents; ++i)
        {
            if (m_agents[i].active == 0) continue;
            if (n < maxAgents)
                agents[n++] = m_agents[i];
        }
        return n;
    }
//
//
    public void updateMoveRequest(float dt)
    {
        int PATH_MAX_AGENTS = 8;
        dtCrowdAgent queue[] = new dtCrowdAgent[PATH_MAX_AGENTS];
        int nqueue = 0;

        // Fire off new requests.
        for (int i = 0; i < m_maxAgents; ++i)
        {
            dtCrowdAgent ag = m_agents[i];
            if (ag.active == 0)
                continue;
            if (ag.state == CrowdAgentState.DT_CROWDAGENT_STATE_INVALID)
                continue;
            if (ag.targetState == MoveRequestState.DT_CROWDAGENT_TARGET_NONE || ag.targetState == MoveRequestState.DT_CROWDAGENT_TARGET_VELOCITY)
                continue;

            if (ag.targetState == MoveRequestState.DT_CROWDAGENT_TARGET_REQUESTING)
            {
                dtPoly[] path = ag.corridor.getPath();
                int npath = ag.corridor.getPathCount();
//                dtAssert(npath);

                int MAX_RES = 32;
                float reqPos[] = new float[3];
                dtPoly reqPath[] = new dtPoly[MAX_RES];	// The path to the request location
                int[] reqPathCount = new int[1];

                // Quick seach towards the goal.
                int MAX_ITER = 20;
                m_navquery.initSlicedFindPath(path[0], ag.targetRef, ag.npos, ag.targetPos, m_filter);
                m_navquery.updateSlicedFindPath(MAX_ITER, null);
                dtStatus status = new dtStatus(0);
                if (ag.targetReplan) // && npath > 10)
                {
                    // Try to use existing steady path during replan if possible.
                    status = m_navquery.finalizeSlicedFindPathPartial(path, npath, reqPath, reqPathCount, MAX_RES);
                }
                else
                {
                    // Try to move towards target when goal changes.
                    status = m_navquery.finalizeSlicedFindPath(reqPath, reqPathCount, MAX_RES);
                }

                if (!dtStatus.dtStatusFailed(status) && reqPathCount[0] > 0)
                {
                    // In progress or succeed.
                    if (reqPath[reqPathCount[0]-1] != ag.targetRef)
                    {
                        // Partial path, constrain target position inside the last polygon.
                        status = m_navquery.closestPointOnPoly(reqPath[reqPathCount[0]-1], ag.targetPos, reqPos);
                        if (dtStatus.dtStatusFailed(status))
                            reqPathCount[0] = 0;
                    }
                    else
                    {
                        DetourCommon.dtVcopy(reqPos, ag.targetPos);
                    }
                }
                else
                {
                    reqPathCount[0] = 0;
                }

                if (reqPathCount[0] == 0)
                {
                    // Could not find path, start the request from current location.
                    DetourCommon.dtVcopy(reqPos, ag.npos);
                    reqPath[0] = path[0];
                    reqPathCount[0] = 1;
                }

                ag.corridor.setCorridor(reqPos, reqPath, reqPathCount[0]);
                ag.boundary.reset();

                if (reqPath[reqPathCount[0]-1] == ag.targetRef)
                {
                    ag.targetState = MoveRequestState.DT_CROWDAGENT_TARGET_VALID;
                    ag.targetReplanTime = 0.0f;
                }
                else
                {
                    // The path is longer or potentially unreachable, full plan.
                    ag.targetState = MoveRequestState.DT_CROWDAGENT_TARGET_WAITING_FOR_QUEUE;
                }
            }

            if (ag.targetState == MoveRequestState.DT_CROWDAGENT_TARGET_WAITING_FOR_QUEUE)
            {
                nqueue = addToPathQueue(ag, queue, nqueue, PATH_MAX_AGENTS);
            }
        }

        for (int i = 0; i < nqueue; ++i)
        {
            dtCrowdAgent ag = queue[i];
            ag.targetPathqRef = m_pathq.request(ag.corridor.getLastPoly(), ag.targetRef,
                    ag.corridor.getTarget(), ag.targetPos, m_filter);
            if (ag.targetPathqRef != null)
                ag.targetState = MoveRequestState.DT_CROWDAGENT_TARGET_WAITING_FOR_PATH;
        }


        // Update requests.
        m_pathq.update(MAX_ITERS_PER_UPDATE);

        dtStatus status;

        // Process path results.
        for (int i = 0; i < m_maxAgents; ++i)
        {
            dtCrowdAgent ag = m_agents[i];
            if (ag.active == 0)
                continue;
            if (ag.targetState == MoveRequestState.DT_CROWDAGENT_TARGET_NONE || ag.targetState == MoveRequestState.DT_CROWDAGENT_TARGET_VELOCITY)
                continue;

            if (ag.targetState == MoveRequestState.DT_CROWDAGENT_TARGET_WAITING_FOR_PATH)
            {
                // Poll path queue.
                status = m_pathq.getRequestStatus(ag.targetPathqRef);
                if (dtStatus.dtStatusFailed(status))
                {
                    // Path find failed, retry if the target location is still valid.
                    ag.targetPathqRef = null;
                    if (ag.targetRef != null)
                        ag.targetState = MoveRequestState.DT_CROWDAGENT_TARGET_REQUESTING;
                    else
                        ag.targetState = MoveRequestState.DT_CROWDAGENT_TARGET_FAILED;
                    ag.targetReplanTime = 0.0f;
                }
                else if (dtStatus.dtStatusSucceed(status))
                {
                    dtPoly[] path = ag.corridor.getPath();
                    int npath = ag.corridor.getPathCount();
//                    dtAssert(npath);

                    // Apply results.
                    float targetPos[] = new float[3];
                    DetourCommon.dtVcopy(targetPos, ag.targetPos);

                    dtPoly[] res = m_pathResult;
                    boolean valid = true;
                    int[] nres = new int[1];
                    status = m_pathq.getPathResult(ag.targetPathqRef, res, nres, m_maxPathResult);
                    if (dtStatus.dtStatusFailed(status) || nres[0] == 0)
                        valid = false;

                    // Merge result and existing path.
                    // The agent might have moved whilst the request is
                    // being processed, so the path may have changed.
                    // We assume that the end of the path is at the same location
                    // where the request was issued.

                    // The last ref in the old path should be the same as
                    // the location where the request was issued..
                    if (valid && path[npath-1] != res[0])
                        valid = false;

                    if (valid)
                    {
                        // Put the old path infront of the old path.
                        if (npath > 1)
                        {
                            // Make space for the old path.
                            if ((npath-1)+nres[0] > m_maxPathResult)
                                nres[0] = m_maxPathResult - (npath-1);

//                            memmove(res+npath-1, res, sizeof(dtPolyRef)*nres);
                            System.arraycopy(res, 0, res, npath+1, nres[0]);
                            // Copy old path in the beginning.
//                            memcpy(res, path, sizeof(dtPolyRef)*(npath-1));
                            System.arraycopy(path, 0, res, 0, (npath-1));
                            nres[0] += npath-1;

                            // Remove trackbacks
                            for (int j = 0; j < nres[0]; ++j)
                            {
                                if (j-1 >= 0 && j+1 < nres[0])
                                {
                                    if (res[j-1] == res[j+1])
                                    {
//                                        memmove(res+(j-1), res+(j+1), sizeof(dtPolyRef)*(nres-(j+1)));
                                        System.arraycopy(res, j+1, res, j-1, (nres[0]-(j+1)));
                                        nres[0] -= 2;
                                        j -= 2;
                                    }
                                }
                            }

                        }

                        // Check for partial path.
                        if (res[nres[0]-1] != ag.targetRef)
                        {
                            // Partial path, constrain target position inside the last polygon.
                            float nearest[] = new float[3];
                            status = m_navquery.closestPointOnPoly(res[nres[0]-1], targetPos, nearest);
                            if (dtStatus.dtStatusSucceed(status))
                                DetourCommon.dtVcopy(targetPos, nearest);
                            else
                                valid = false;
                        }
                    }

                    if (valid)
                    {
                        // Set current corridor.
                        ag.corridor.setCorridor(targetPos, res, nres[0]);
                        // Force to update boundary.
                        ag.boundary.reset();
                        ag.targetState = MoveRequestState.DT_CROWDAGENT_TARGET_VALID;
                    }
                    else
                    {
                        // Something went wrong.
                        ag.targetState = MoveRequestState.DT_CROWDAGENT_TARGET_FAILED;
                    }

                    ag.targetReplanTime = 0.0f;
                }
            }
        }

    }


    public void updateTopologyOptimization(dtCrowdAgent[] agents, int nagents, float dt)
    {
        if (nagents == 0)
            return;

        float OPT_TIME_THR = 0.5f; // seconds
        int OPT_MAX_AGENTS = 1;
        dtCrowdAgent queue[] = new dtCrowdAgent[OPT_MAX_AGENTS];
        int nqueue = 0;

        for (int i = 0; i < nagents; ++i)
        {
            dtCrowdAgent ag = agents[i];
            if (ag.state != CrowdAgentState.DT_CROWDAGENT_STATE_WALKING)
                continue;
            if (ag.targetState == MoveRequestState.DT_CROWDAGENT_TARGET_NONE || ag.targetState == MoveRequestState.DT_CROWDAGENT_TARGET_VELOCITY)
                continue;
            if ((ag.params.updateFlags & UpdateFlags.DT_CROWD_OPTIMIZE_TOPO) == 0)
                continue;
            ag.topologyOptTime += dt;
            if (ag.topologyOptTime >= OPT_TIME_THR)
                nqueue = addToOptQueue(ag, queue, nqueue, OPT_MAX_AGENTS);
        }

        for (int i = 0; i < nqueue; ++i)
        {
            dtCrowdAgent ag = queue[i];
            ag.corridor.optimizePathTopology(m_navquery, m_filter);
            ag.topologyOptTime = 0;
        }

    }

    public void checkPathValidity(dtCrowdAgent[] agents, int nagents, float dt)
    {
        int CHECK_LOOKAHEAD = 10;
        float TARGET_REPLAN_DELAY = 1.0f; // seconds

        for (int i = 0; i < nagents; ++i)
        {
            dtCrowdAgent ag = agents[i];

            if (ag.state != CrowdAgentState.DT_CROWDAGENT_STATE_WALKING)
                continue;

            if (ag.targetState == MoveRequestState.DT_CROWDAGENT_TARGET_NONE || ag.targetState == MoveRequestState.DT_CROWDAGENT_TARGET_VELOCITY)
                continue;

            ag.targetReplanTime += dt;

            boolean replan = false;

            // First check that the current location is valid.
            int idx = getAgentIndex(ag);
            float agentPos[] = new float[3];
            dtPoly agentRef = ag.corridor.getFirstPoly();
            DetourCommon.dtVcopy(agentPos, ag.npos);
            if (!m_navquery.isValidPolyRef(agentRef, m_filter))
            {
                // Current location is not valid, try to reposition.
                // TODO: this can snap agents, how to handle that?
                float nearest[] = new float[3];
                agentRef = null;
                dtPoly[] tmp = new dtPoly[]{agentRef};
                m_navquery.findNearestPoly(ag.npos, m_ext, m_filter, tmp, nearest);
                agentRef = tmp[0];
                DetourCommon.dtVcopy(agentPos, nearest);

                if (agentRef == null)
                {
                    // Could not find location in navmesh, set state to invalid.
                    ag.corridor.reset(null, agentPos);
                    ag.boundary.reset();
                    ag.state = CrowdAgentState.DT_CROWDAGENT_STATE_INVALID;
                    continue;
                }

                // Make sure the first polygon is valid, but leave other valid
                // polygons in the path so that replanner can adjust the path better.
                ag.corridor.fixPathStart(agentRef, agentPos);
//			ag.corridor.trimInvalidPath(agentRef, agentPos, m_navquery, &m_filter);
                ag.boundary.reset();
                DetourCommon.dtVcopy(ag.npos, agentPos);

                replan = true;
            }

            // Try to recover move request position.
            if (ag.targetState != MoveRequestState.DT_CROWDAGENT_TARGET_NONE && ag.targetState != MoveRequestState.DT_CROWDAGENT_TARGET_FAILED)
            {
                if (!m_navquery.isValidPolyRef(ag.targetRef, m_filter))
                {
                    // Current target is not valid, try to reposition.
                    float nearest[] = new float[3];
                    dtPoly[] tmp = new dtPoly[]{ag.targetRef};
                    m_navquery.findNearestPoly(ag.targetPos, m_ext, m_filter, tmp, nearest);
                    ag.targetRef = tmp[0];
                    DetourCommon.dtVcopy(ag.targetPos, nearest);
                    replan = true;
                }
                if (ag.targetRef == null)
                {
                    // Failed to reposition target, fail moverequest.
                    ag.corridor.reset(agentRef, agentPos);
                    ag.targetState = MoveRequestState.DT_CROWDAGENT_TARGET_NONE;
                }
            }

            // If nearby corridor is not valid, replan.
            if (!ag.corridor.isValid(CHECK_LOOKAHEAD, m_navquery, m_filter))
            {
                // Fix current path.
//			ag.corridor.trimInvalidPath(agentRef, agentPos, m_navquery, &m_filter);
//			ag.boundary.reset();
                replan = true;
            }

            // If the end of the path is near and it is not the requested location, replan.
            if (ag.targetState == MoveRequestState.DT_CROWDAGENT_TARGET_VALID)
            {
                if (ag.targetReplanTime > TARGET_REPLAN_DELAY &&
                        ag.corridor.getPathCount() < CHECK_LOOKAHEAD &&
                                ag.corridor.getLastPoly() != ag.targetRef)
                    replan = true;
            }

            // Try to replan path to goal.
            if (replan)
            {
                if (ag.targetState != MoveRequestState.DT_CROWDAGENT_TARGET_NONE)
                {
                    requestMoveTargetReplan(idx, ag.targetRef, ag.targetPos);
                }
            }
        }
    }

    public void update(float dt, dtCrowdAgentDebugInfo debug)
    {
        m_velocitySampleCount = 0;

        int debugIdx = debug != null ? debug.idx : -1;

        dtCrowdAgent[] agents = m_activeAgents;
        int nagents = getActiveAgents(agents, m_maxAgents);

        // Check that all agents still have valid paths.
        checkPathValidity(agents, nagents, dt);

        // Update async move request and path finder.
        updateMoveRequest(dt);

        // Optimize path topology.
        updateTopologyOptimization(agents, nagents, dt);

        // Register agents to proximity grid.
        m_grid.clear();
        for (int i = 0; i < nagents; ++i)
        {
            dtCrowdAgent ag = agents[i];
            float[] p = ag.npos;
            float r = ag.params.radius;
            m_grid.addItem(i, p[0]-r, p[2]-r, p[0]+r, p[2]+r);
        }

        // Get nearby navmesh segments and agents to collide with.
        for (int i = 0; i < nagents; ++i)
        {
            dtCrowdAgent ag = agents[i];
            if (ag.state != CrowdAgentState.DT_CROWDAGENT_STATE_WALKING)
                continue;

            // Update the collision boundary after certain distance has been passed or
            // if it has become invalid.
            float updateThr = ag.params.collisionQueryRange*0.25f;
            if (DetourCommon.dtVdist2DSqr(ag.npos, ag.boundary.getCenter()) > DetourCommon.dtSqr(updateThr) ||
                    !ag.boundary.isValid(m_navquery, m_filter))
            {
                ag.boundary.update(ag.corridor.getFirstPoly(), ag.npos, ag.params.collisionQueryRange,
                        m_navquery, m_filter);
            }
            // Query neighbour agents
            ag.nneis = getNeighbours(ag.npos, ag.params.height, ag.params.collisionQueryRange,
                    ag, ag.neis, DT_CROWDAGENT_MAX_NEIGHBOURS,
                    agents, nagents, m_grid);
            for (int j = 0; j < ag.nneis; j++)
                ag.neis[j].idx = getAgentIndex(agents[ag.neis[j].idx]);
        }

        // Find next corner to steer to.
        for (int i = 0; i < nagents; ++i)
        {
            dtCrowdAgent ag = agents[i];

            if (ag.state != CrowdAgentState.DT_CROWDAGENT_STATE_WALKING)
                continue;
            if (ag.targetState == MoveRequestState.DT_CROWDAGENT_TARGET_NONE || ag.targetState == MoveRequestState.DT_CROWDAGENT_TARGET_VELOCITY)
                continue;

            // Find corners for steering
            ag.ncorners = ag.corridor.findCorners(ag.cornerVerts, ag.cornerFlags, ag.cornerPolys,
                    DT_CROWDAGENT_MAX_CORNERS, m_navquery, m_filter);

            // Check to see if the corner after the next corner is directly visible,
            // and short cut to there.
            if ((ag.params.updateFlags & UpdateFlags.DT_CROWD_OPTIMIZE_VIS) != 0 && ag.ncorners > 0)
            {
                float[] target = ag.cornerVerts;
                int targetIndex = DetourCommon.dtMin(1,ag.ncorners-1)*3;
                ag.corridor.optimizePathVisibility(target, targetIndex, ag.params.pathOptimizationRange, m_navquery, m_filter);

                // Copy data for debug purposes.
                if (debugIdx == i)
                {
                    DetourCommon.dtVcopy(debug.optStart, ag.corridor.getPos());
                    DetourCommon.dtVcopy(debug.optEnd, target);
                }
            }
            else
            {
                // Copy data for debug purposes.
                if (debugIdx == i)
                {
                    DetourCommon.dtVset(debug.optStart, 0, 0, 0);
                    DetourCommon.dtVset(debug.optEnd, 0, 0, 0);
                }
            }
        }

        // Trigger off-mesh connections (depends on corners).
        for (int i = 0; i < nagents; ++i)
        {
            dtCrowdAgent ag = agents[i];

            if (ag.state != CrowdAgentState.DT_CROWDAGENT_STATE_WALKING)
                continue;
            if (ag.targetState == MoveRequestState.DT_CROWDAGENT_TARGET_NONE || ag.targetState == MoveRequestState.DT_CROWDAGENT_TARGET_VELOCITY)
                continue;

            // Check
            float triggerRadius = ag.params.radius*2.25f;
            if (overOffmeshConnection(ag, triggerRadius))
            {
                // Prepare to off-mesh connection.
                int idx = -1;//ag - m_agents;
                for (int ii = 0; ii < m_agents.length; ii++) {
                    if (m_agents[ii] == ag) {
                        idx = ii;
                    }
                }
                dtCrowdAgentAnimation anim = m_agentAnims[idx];

                // Adjust the path over the off-mesh connection.
                dtPoly refs[] = new dtPoly[2];
                if (ag.corridor.moveOverOffmeshConnection(ag.cornerPolys[ag.ncorners-1], refs[0],
                        anim.startPos, anim.endPos, m_navquery))
                {
                    DetourCommon.dtVcopy(anim.initPos, ag.npos);
                    anim.polyRef = refs[1];
                    anim.active = 1;
                    anim.t = 0.0f;
                    anim.tmax = (DetourCommon.dtVdist2D(anim.startPos, anim.endPos) / ag.params.maxSpeed) * 0.5f;

                    ag.state = CrowdAgentState.DT_CROWDAGENT_STATE_OFFMESH;
                    ag.ncorners = 0;
                    ag.nneis = 0;
                    continue;
                }
                else
                {
                    // Path validity check will ensure that bad/blocked connections will be replanned.
                }
            }
        }

        // Calculate steering.
        for (int i = 0; i < nagents; ++i)
        {
            dtCrowdAgent ag = agents[i];

            if (ag.state != CrowdAgentState.DT_CROWDAGENT_STATE_WALKING)
                continue;
            if (ag.targetState == MoveRequestState.DT_CROWDAGENT_TARGET_NONE)
                continue;

            float dvel[] = {0,0,0};

            if (ag.targetState == MoveRequestState.DT_CROWDAGENT_TARGET_VELOCITY)
            {
                DetourCommon.dtVcopy(dvel, ag.targetPos);
                ag.desiredSpeed = DetourCommon.dtVlen(ag.targetPos);
            }
            else
            {
                // Calculate steering direction.
                if ((ag.params.updateFlags & UpdateFlags.DT_CROWD_ANTICIPATE_TURNS) != 0)
                    calcSmoothSteerDirection(ag, dvel);
                else
                    calcStraightSteerDirection(ag, dvel);

                // Calculate speed scale, which tells the agent to slowdown at the end of the path.
                float slowDownRadius = ag.params.radius*2;	// TODO: make less hacky.
                float speedScale = getDistanceToGoal(ag, slowDownRadius) / slowDownRadius;

                ag.desiredSpeed = ag.params.maxSpeed;
                DetourCommon.dtVscale(dvel, dvel, ag.desiredSpeed * speedScale);
            }

            // Separation
            if ((ag.params.updateFlags & UpdateFlags.DT_CROWD_SEPARATION) != 0)
            {
                float separationDist = ag.params.collisionQueryRange;
                float invSeparationDist = 1.0f / separationDist;
                float separationWeight = ag.params.separationWeight;

                float w = 0;
                float disp[] = {0,0,0};

                for (int j = 0; j < ag.nneis; ++j)
                {
                    dtCrowdAgent nei = m_agents[ag.neis[j].idx];

                    float diff[] = new float[3];
                    DetourCommon.dtVsub(diff, ag.npos, nei.npos);
                    diff[1] = 0;

                    float distSqr = DetourCommon.dtVlenSqr(diff);
                    if (distSqr < 0.00001f)
                        continue;
                    if (distSqr > DetourCommon.dtSqr(separationDist))
                        continue;
                    float dist = DetourCommon.sqrtf(distSqr);
                    float weight = separationWeight * (1.0f - DetourCommon.dtSqr(dist*invSeparationDist));

                    DetourCommon.dtVmad(disp, disp, diff, weight / dist);
                    w += 1.0f;
                }

                if (w > 0.0001f)
                {
                    // Adjust desired velocity.
                    DetourCommon.dtVmad(dvel, dvel, disp, 1.0f/w);
                    // Clamp desired velocity to desired speed.
                    float speedSqr = DetourCommon.dtVlenSqr(dvel);
                    float desiredSqr = DetourCommon.dtSqr(ag.desiredSpeed);
                    if (speedSqr > desiredSqr)
                        DetourCommon.dtVscale(dvel, dvel, desiredSqr / speedSqr);
                }
            }

            // Set the desired velocity.
            DetourCommon.dtVcopy(ag.dvel, dvel);
        }

        // Velocity planning.
        for (int i = 0; i < nagents; ++i)
        {
            dtCrowdAgent ag = agents[i];

            if (ag.state != CrowdAgentState.DT_CROWDAGENT_STATE_WALKING)
                continue;

            if ((ag.params.updateFlags & UpdateFlags.DT_CROWD_OBSTACLE_AVOIDANCE) != 0)
            {
                m_obstacleQuery.reset();

                // Add neighbours as obstacles.
                for (int j = 0; j < ag.nneis; ++j)
                {
                    dtCrowdAgent nei = m_agents[ag.neis[j].idx];
                    m_obstacleQuery.addCircle(nei.npos, nei.params.radius, nei.vel, nei.dvel);
                }

                // Append neighbour segments as obstacles.
                for (int j = 0; j < ag.boundary.getSegmentCount(); ++j)
                {
                    float[] s = ag.boundary.getSegment(j);
                    if (DetourCommon.dtTriArea2D(ag.npos, s, s, 3) < 0.0f)
                        continue;
                    m_obstacleQuery.addSegment(s, s, 3);
                }

                dtObstacleAvoidanceDebugData vod = null;
                if (debugIdx == i)
                    vod = debug.vod;

                // Sample new safe velocity.
                boolean adaptive = true;
                int ns = 0;

                dtObstacleAvoidanceParams params = m_obstacleQueryParams[ag.params.obstacleAvoidanceType];

                if (adaptive)
                {
                    ns = m_obstacleQuery.sampleVelocityAdaptive(ag.npos, ag.params.radius, ag.desiredSpeed,
                            ag.vel, ag.dvel, ag.nvel, params, vod);
                }
                else
                {
                    ns = m_obstacleQuery.sampleVelocityGrid(ag.npos, ag.params.radius, ag.desiredSpeed,
                            ag.vel, ag.dvel, ag.nvel, params, vod);
                }
                m_velocitySampleCount += ns;
            }
            else
            {
                // If not using velocity planning, new velocity is directly the desired velocity.
                DetourCommon.dtVcopy(ag.nvel, ag.dvel);
            }
        }

        // Integrate.
        for (int i = 0; i < nagents; ++i)
        {
            dtCrowdAgent ag = agents[i];
            if (ag.state != CrowdAgentState.DT_CROWDAGENT_STATE_WALKING)
                continue;
            integrate(ag, dt);
        }

        // Handle collisions.
        float COLLISION_RESOLVE_FACTOR = 0.7f;

        for (int iter = 0; iter < 4; ++iter)
        {
            for (int i = 0; i < nagents; ++i)
            {
                dtCrowdAgent ag = agents[i];
                int idx0 = getAgentIndex(ag);

                if (ag.state != CrowdAgentState.DT_CROWDAGENT_STATE_WALKING)
                    continue;

                DetourCommon.dtVset(ag.disp, 0, 0, 0);

                float w = 0;

                for (int j = 0; j < ag.nneis; ++j)
                {
                    dtCrowdAgent nei = m_agents[ag.neis[j].idx];
                    int idx1 = getAgentIndex(nei);

                    float diff[] = new float[3];
                    DetourCommon.dtVsub(diff, ag.npos, nei.npos);
                    diff[1] = 0;

                    float dist = DetourCommon.dtVlenSqr(diff);
                    if (dist > DetourCommon.dtSqr(ag.params.radius + nei.params.radius))
                        continue;
                    dist = DetourCommon.sqrtf(dist);
                    float pen = (ag.params.radius + nei.params.radius) - dist;
                    if (dist < 0.0001f)
                    {
                        // Agents on top of each other, try to choose diverging separation directions.
                        if (idx0 > idx1)
                            DetourCommon.dtVset(diff, -ag.dvel[2], 0, ag.dvel[0]);
                        else
                            DetourCommon.dtVset(diff, ag.dvel[2], 0, -ag.dvel[0]);
                        pen = 0.01f;
                    }
                    else
                    {
                        pen = (1.0f/dist) * (pen*0.5f) * COLLISION_RESOLVE_FACTOR;
                    }

                    DetourCommon.dtVmad(ag.disp, ag.disp, diff, pen);

                    w += 1.0f;
                }

                if (w > 0.0001f)
                {
                    float iw = 1.0f / w;
                    DetourCommon.dtVscale(ag.disp, ag.disp, iw);
                }
            }

            for (int i = 0; i < nagents; ++i)
            {
                dtCrowdAgent ag = agents[i];
                if (ag.state != CrowdAgentState.DT_CROWDAGENT_STATE_WALKING)
                    continue;

                DetourCommon.dtVadd(ag.npos, ag.npos, ag.disp);
            }
        }

        for (int i = 0; i < nagents; ++i)
        {
            dtCrowdAgent ag = agents[i];
            if (ag.state != CrowdAgentState.DT_CROWDAGENT_STATE_WALKING)
                continue;

            // Move along navmesh.
            ag.corridor.movePosition(ag.npos, m_navquery, m_filter);
            // Get valid constrained position back.
            DetourCommon.dtVcopy(ag.npos, ag.corridor.getPos());

            // If not using path, truncate the corridor to just one poly.
            if (ag.targetState == MoveRequestState.DT_CROWDAGENT_TARGET_NONE || ag.targetState == MoveRequestState.DT_CROWDAGENT_TARGET_VELOCITY)
            {
                ag.corridor.reset(ag.corridor.getFirstPoly(), ag.npos);
            }

        }

        // Update agents using off-mesh connection.
        for (int i = 0; i < m_maxAgents; ++i)
        {
            dtCrowdAgentAnimation anim = m_agentAnims[i];
            if (anim.active == 0)
                continue;
            dtCrowdAgent ag = agents[i];

            anim.t += dt;
            if (anim.t > anim.tmax)
            {
                // Reset animation
                anim.active = 0;
                // Prepare agent for walking.
                ag.state = CrowdAgentState.DT_CROWDAGENT_STATE_WALKING;
                continue;
            }

            // Update position
            float ta = anim.tmax*0.15f;
            float tb = anim.tmax;
            if (anim.t < ta)
            {
                float u = tween(anim.t, 0.0f, ta);
                DetourCommon.dtVlerp(ag.npos, anim.initPos, 0, anim.startPos, 0, u);
            }
            else
            {
                float u = tween(anim.t, ta, tb);
                DetourCommon.dtVlerp(ag.npos, anim.startPos, 0, anim.endPos, 0, u);
            }

            // Update velocity.
            DetourCommon.dtVset(ag.vel, 0, 0, 0);
            DetourCommon.dtVset(ag.dvel, 0, 0, 0);
        }

    }
}
