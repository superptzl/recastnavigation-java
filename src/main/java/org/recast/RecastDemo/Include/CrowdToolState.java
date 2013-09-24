package org.recast.RecastDemo.Include;

import org.recast.Detour.Include.dtNavMesh;
import org.recast.Detour.Include.dtPoly;
import org.recast.DetourCrowd.Include.dtCrowd;
import org.recast.DetourCrowd.Include.dtCrowdAgentDebugInfo;
import org.recast.DetourCrowd.Include.dtObstacleAvoidanceDebugData;

public abstract class CrowdToolState extends SampleToolState {
    public Sample m_sample;
    public dtNavMesh m_nav;
    public dtCrowd m_crowd;

    public float m_targetPos[] = new float[3];
    public dtPoly m_targetRef;

    public dtCrowdAgentDebugInfo m_agentDebug;
    public dtObstacleAvoidanceDebugData m_vod;

    public final static int AGENT_MAX_TRAIL = 64;
    public final static int MAX_AGENTS = 128;
    public static class AgentTrail
    {
		public float trail[] = new float[AGENT_MAX_TRAIL*3];
        public int htrail;
    }
    public AgentTrail[] m_trails = new AgentTrail[MAX_AGENTS];

    public ValueHistory m_crowdTotalTime;
    public ValueHistory m_crowdSampleCount;

    public CrowdToolParams m_toolParams;

    public boolean m_run;

//    CrowdToolState();
//    virtual ~CrowdToolState();

    public abstract void init(Sample sample);
//    public abstract void reset();
//    public abstract void handleRender();
//    public abstract void handleRenderOverlay(double* proj, double* model, int* view);
//    public abstract void handleUpdate(const float dt);

    public boolean isRunning() { return m_run; }
    public void setRunning(boolean s) { m_run = s; }

    public abstract void addAgent(float[] pos);
//    void removeAgent(const int idx);
//    void hilightAgent(const int idx);
//    void updateAgentParams();
//    int hitTestAgents(const float* s, const float* p);
    public abstract void setMoveTarget(float[] p, boolean adjust);
    public abstract void updateTick(float dt);

    public CrowdToolParams getToolParams() { return m_toolParams; }
}
