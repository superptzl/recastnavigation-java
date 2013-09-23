package org.recast.RecastDemo.Source;

import org.recast.RecastDemo.Include.CrowdTool;

public class CrowdToolImpl extends CrowdTool {
    CrowdTool::CrowdTool() :
    m_sample(0),
    m_state(0),
    m_mode(TOOLMODE_CREATE)
    {
    }

    CrowdTool::~CrowdTool()
    {
    }

    void CrowdTool::init(Sample* sample)
    {
        if (m_sample != sample)
        {
            m_sample = sample;
        }

        if (!sample)
            return;

        m_state = (CrowdToolState*)sample->getToolState(type());
        if (!m_state)
        {
            m_state = new CrowdToolState();
            sample->setToolState(type(), m_state);
        }
        m_state->init(sample);
    }

    void CrowdTool::reset()
    {
    }

    void CrowdTool::handleMenu()
    {
        if (!m_state)
            return;
        CrowdToolParams* params = m_state->getToolParams();

        if (imguiCheck("Create Agents", m_mode == TOOLMODE_CREATE))
            m_mode = TOOLMODE_CREATE;
        if (imguiCheck("Move Target", m_mode == TOOLMODE_MOVE_TARGET))
            m_mode = TOOLMODE_MOVE_TARGET;
        if (imguiCheck("Select Agent", m_mode == TOOLMODE_SELECT))
            m_mode = TOOLMODE_SELECT;
        if (imguiCheck("Toggle Polys", m_mode == TOOLMODE_TOGGLE_POLYS))
            m_mode = TOOLMODE_TOGGLE_POLYS;

        imguiSeparatorLine();

        if (imguiCollapse("Options", 0, params->m_expandOptions))
            params->m_expandOptions = !params->m_expandOptions;

        if (params->m_expandOptions)
        {
            imguiIndent();
            if (imguiCheck("Optimize Visibility", params->m_optimizeVis))
            {
                params->m_optimizeVis = !params->m_optimizeVis;
                m_state->updateAgentParams();
            }
            if (imguiCheck("Optimize Topology", params->m_optimizeTopo))
            {
                params->m_optimizeTopo = !params->m_optimizeTopo;
                m_state->updateAgentParams();
            }
            if (imguiCheck("Anticipate Turns", params->m_anticipateTurns))
            {
                params->m_anticipateTurns = !params->m_anticipateTurns;
                m_state->updateAgentParams();
            }
            if (imguiCheck("Obstacle Avoidance", params->m_obstacleAvoidance))
            {
                params->m_obstacleAvoidance = !params->m_obstacleAvoidance;
                m_state->updateAgentParams();
            }
            if (imguiSlider("Avoidance Quality", &params->m_obstacleAvoidanceType, 0.0f, 3.0f, 1.0f))
            {
                m_state->updateAgentParams();
            }
            if (imguiCheck("Separation", params->m_separation))
            {
                params->m_separation = !params->m_separation;
                m_state->updateAgentParams();
            }
            if (imguiSlider("Separation Weight", &params->m_separationWeight, 0.0f, 20.0f, 0.01f))
            {
                m_state->updateAgentParams();
            }

            imguiUnindent();
        }

        if (imguiCollapse("Selected Debug Draw", 0, params->m_expandSelectedDebugDraw))
            params->m_expandSelectedDebugDraw = !params->m_expandSelectedDebugDraw;

        if (params->m_expandSelectedDebugDraw)
        {
            imguiIndent();
            if (imguiCheck("Show Corners", params->m_showCorners))
                params->m_showCorners = !params->m_showCorners;
            if (imguiCheck("Show Collision Segs", params->m_showCollisionSegments))
                params->m_showCollisionSegments = !params->m_showCollisionSegments;
            if (imguiCheck("Show Path", params->m_showPath))
                params->m_showPath = !params->m_showPath;
            if (imguiCheck("Show VO", params->m_showVO))
                params->m_showVO = !params->m_showVO;
            if (imguiCheck("Show Path Optimization", params->m_showOpt))
                params->m_showOpt = !params->m_showOpt;
            if (imguiCheck("Show Neighbours", params->m_showNeis))
                params->m_showNeis = !params->m_showNeis;
            imguiUnindent();
        }

        if (imguiCollapse("Debug Draw", 0, params->m_expandDebugDraw))
            params->m_expandDebugDraw = !params->m_expandDebugDraw;

        if (params->m_expandDebugDraw)
        {
            imguiIndent();
            if (imguiCheck("Show Labels", params->m_showLabels))
                params->m_showLabels = !params->m_showLabels;
            if (imguiCheck("Show Prox Grid", params->m_showGrid))
                params->m_showGrid = !params->m_showGrid;
            if (imguiCheck("Show Nodes", params->m_showNodes))
                params->m_showNodes = !params->m_showNodes;
            if (imguiCheck("Show Perf Graph", params->m_showPerfGraph))
                params->m_showPerfGraph = !params->m_showPerfGraph;
            if (imguiCheck("Show Detail All", params->m_showDetailAll))
                params->m_showDetailAll = !params->m_showDetailAll;
            imguiUnindent();
        }
    }

    void CrowdTool::handleClick(const float* s, const float* p, bool shift)
    {
        if (!m_sample) return;
        if (!m_state) return;
        InputGeom* geom = m_sample->getInputGeom();
        if (!geom) return;
        dtCrowd* crowd = m_sample->getCrowd();
        if (!crowd) return;

        if (m_mode == TOOLMODE_CREATE)
        {
            if (shift)
            {
                // Delete
                int ahit = m_state->hitTestAgents(s,p);
                if (ahit != -1)
                    m_state->removeAgent(ahit);
            }
            else
            {
                // Add
                m_state->addAgent(p);
            }
        }
        else if (m_mode == TOOLMODE_MOVE_TARGET)
        {
            m_state->setMoveTarget(p, shift);
        }
        else if (m_mode == TOOLMODE_SELECT)
        {
            // Highlight
            int ahit = m_state->hitTestAgents(s,p);
            m_state->hilightAgent(ahit);
        }
        else if (m_mode == TOOLMODE_TOGGLE_POLYS)
        {
            dtNavMesh* nav = m_sample->getNavMesh();
            dtNavMeshQuery* navquery = m_sample->getNavMeshQuery();
            if (nav && navquery)
            {
                dtQueryFilter filter;
                const float* ext = crowd->getQueryExtents();
                float tgt[3];
                dtPolyRef ref;
                navquery->findNearestPoly(p, ext, &filter, &ref, tgt);
                if (ref)
                {
                    unsigned short flags = 0;
                    if (dtStatusSucceed(nav->getPolyFlags(ref, &flags)))
                    {
                        flags ^= SAMPLE_POLYFLAGS_DISABLED;
                        nav->setPolyFlags(ref, flags);
                    }
                }
            }
        }

    }

    void CrowdTool::handleStep()
    {
        if (!m_state) return;

        const float dt = 1.0f/20.0f;
        m_state->updateTick(dt);

        m_state->setRunning(false);
    }

    void CrowdTool::handleToggle()
    {
        if (!m_state) return;
        m_state->setRunning(!m_state->isRunning());
    }

    void CrowdTool::handleUpdate(const float dt)
    {
    }

    void CrowdTool::handleRender()
    {
    }

    void CrowdTool::handleRenderOverlay(double* proj, double* model, int* view)
    {
        // Tool help
        const int h = view[3];
        int ty = h-40;

        if (m_mode == TOOLMODE_CREATE)
        {
            imguiDrawText(280, ty, IMGUI_ALIGN_LEFT, "LMB: add agent.  Shift+LMB: remove agent.", imguiRGBA(255,255,255,192));
        }
        else if (m_mode == TOOLMODE_MOVE_TARGET)
        {
            imguiDrawText(280, ty, IMGUI_ALIGN_LEFT, "LMB: set move target.  Shift+LMB: adjust set velocity.", imguiRGBA(255,255,255,192));
            ty -= 20;
            imguiDrawText(280, ty, IMGUI_ALIGN_LEFT, "Setting velocity will move the agents without pathfinder.", imguiRGBA(255,255,255,192));
        }
        else if (m_mode == TOOLMODE_SELECT)
        {
            imguiDrawText(280, ty, IMGUI_ALIGN_LEFT, "LMB: select agent.", imguiRGBA(255,255,255,192));
        }
        ty -= 20;
        imguiDrawText(280, ty, IMGUI_ALIGN_LEFT, "SPACE: Run/Pause simulation.  1: Step simulation.", imguiRGBA(255,255,255,192));
        ty -= 20;

        if (m_state && m_state->isRunning())
            imguiDrawText(280, ty, IMGUI_ALIGN_LEFT, "- RUNNING -", imguiRGBA(255,32,16,255));
        else
            imguiDrawText(280, ty, IMGUI_ALIGN_LEFT, "- PAUSED -", imguiRGBA(255,255,255,128));
    }

}
