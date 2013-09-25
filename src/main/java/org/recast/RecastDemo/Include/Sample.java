package org.recast.RecastDemo.Include;

import org.recast.Detour.Include.dtNavMesh;
import org.recast.Detour.Include.dtNavMeshQuery;
import org.recast.DetourCrowd.Include.dtCrowd;
import org.recast.Recast.Include.BuildContext;

import java.util.HashMap;
import java.util.Map;

public abstract class Sample {
    public InputGeom m_geom;
    public dtNavMesh m_navMesh;
    public dtNavMeshQuery m_navQuery;
    public dtCrowd m_crowd;

    public char m_navMeshDrawFlags;

    public float m_cellSize;
    public float m_cellHeight;
    public float m_agentHeight;
    public float m_agentRadius;
    public float m_agentMaxClimb;
    public float m_agentMaxSlope;
    public float m_regionMinSize;
    public float m_regionMergeSize;
    public boolean m_monotonePartitioning;
    public float m_edgeMaxLen;
    public float m_edgeMaxError;
    public float m_vertsPerPoly;
    public float m_detailSampleDist;
    public float m_detailSampleMaxError;

    public SampleTool m_tool;
//    public SampleToolState[] m_toolStates = new SampleToolState[SampleToolType.values().length];
    public Map<SampleToolType, SampleToolState> m_toolStates = new HashMap<>();

    public BuildContext m_ctx;

//    public:
//    Sample();
//    virtual ~Sample();

    public void setContext(BuildContext ctx) { m_ctx = ctx; }

    public abstract void setTool(SampleTool tool);
    public SampleToolState getToolState(SampleToolType type) { return m_toolStates.get(type); }
    public void setToolState(SampleToolType type, SampleToolState s) { m_toolStates.put(type, s); }

//    public abstract void handleSettings();
//    public abstract void handleTools();
//    public abstract void handleDebugMode();
    public abstract void handleClick(float[] s, float[] p, boolean shift);
//    public abstract void handleToggle();
    public abstract void handleStep();
//    public abstract void handleRender();
//    public abstract void handleRenderOverlay(double[] proj, double[] model, int[] view);
    public abstract void handleMeshChanged(InputGeom geom);
    public abstract boolean handleBuild();
    public abstract void handleUpdate(float dt);

    public InputGeom getInputGeom() { return m_geom; }
    public dtNavMesh getNavMesh() { return m_navMesh; }
    public dtNavMeshQuery getNavMeshQuery() { return m_navQuery; }
    public dtCrowd getCrowd() { return m_crowd; }
    public float getAgentRadius() { return m_agentRadius; }
    public float getAgentHeight() { return m_agentHeight; }
    public float getAgentClimb() { return m_agentMaxClimb; }
    public abstract float[] getBoundsMin();
    public abstract float[] getBoundsMax();

    public char getNavMeshDrawFlags() { return m_navMeshDrawFlags; }
    public void setNavMeshDrawFlags(char flags) { m_navMeshDrawFlags = flags; }

    public abstract void updateToolStates(float dt);
//    public abstract void initToolStates(Sample sample);
//    public abstract void resetToolStates();
//    public abstract void renderToolStates();
//    public abstract void renderOverlayToolStates(double[] proj, double[] model, int[] view);

//    public abstract void resetCommonSettings();
//    public abstract void handleCommonSettings();
}
