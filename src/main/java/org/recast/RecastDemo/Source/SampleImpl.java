package org.recast.RecastDemo.Source;

import org.recast.Detour.Include.DrawNavMeshFlags;
import org.recast.RecastDemo.Include.InputGeom;
import org.recast.RecastDemo.Include.Sample;
import org.recast.RecastDemo.Include.SampleTool;
import org.recast.RecastDemo.Include.SampleToolType;

public abstract class SampleImpl extends Sample
{
	//
// Copyright (c) 2009-2010 Mikko Mononen memon@inside.org
//
// This software is provided 'as-is', without any express or implied
// warranty.  In no event will the authors be held liable for any damages
// arising from the use of this software.
// Permission is granted to anyone to use this software for any purpose,
// including commercial applications, and to alter it and redistribute it
// freely, subject to the following restrictions:
// 1. The origin of this software must not be misrepresented; you must not
//    claim that you wrote the original software. If you use this software
//    in a product, an acknowledgment in the product documentation would be
//    appreciated but is not required.
// 2. Altered source versions must be plainly marked as such, and must not be
//    misrepresented as being the original software.
// 3. This notice may not be removed or altered from any source distribution.
//
//
//    #define _USE_MATH_DEFINES
//    #include <math.h>
//    #include <stdio.h>
//    #include "Sample.h"
//            #include "InputGeom.h"
//            #include "Recast.h"
//            #include "RecastDebugDraw.h"
//            #include "DetourDebugDraw.h"
//            #include "DetourNavMesh.h"
//            #include "DetourNavMeshQuery.h"
//            #include "DetourCrowd.h"
//            #include "imgui.h"
//            #include "SDL.h"
//            #include "SDL_opengl.h"
//
//            #ifdef WIN32
//    #	define snprintf _snprintf
//    #endif

	public SampleImpl()
	{
//        m_geom(0),
//                m_navMesh(0),
//                m_navQuery(0),
//                m_crowd(0),
		m_navMeshDrawFlags = (char)(DrawNavMeshFlags.DU_DRAWNAVMESH_OFFMESHCONS.v | DrawNavMeshFlags.DU_DRAWNAVMESH_CLOSEDLIST.v);
//                m_tool(0),
//                m_ctx(0)
		resetCommonSettings();
//        m_navQuery = dtAllocNavMeshQuery();
//        m_crowd = dtAllocCrowd();

//        for (int i = 0; i < MAX_TOOLS; i++)
//            m_toolStates[i] = 0;
	}

	/* ~Sample()
		{
			dtFreeNavMeshQuery(m_navQuery);
			dtFreeNavMesh(m_navMesh);
			dtFreeCrowd(m_crowd);
			delete m_tool;
			for (int i = 0; i < MAX_TOOLS; i++)
				delete m_toolStates[i];
		}*/
	public void setTool(SampleTool tool)
	{
//        delete m_tool;
		m_tool = tool;
		if (m_tool != null)
			m_tool.init(this);
	}

	/*  public void handleSettings()
		{
		}

		public void handleTools()
		{
		}

		public void handleDebugMode()
		{
		}

		public void handleRender()
		{
			if (!m_geom)
				return;

			DebugDrawGL dd;

			// Draw mesh
			duDebugDrawTriMesh(&dd, m_geom.getMesh().getVerts(), m_geom.getMesh().getVertCount(),
				m_geom.getMesh().getTris(), m_geom.getMesh().getNormals(), m_geom.getMesh().getTriCount(), 0, 1.0f);
			// Draw bounds
			const float* bmin = m_geom.getMeshBoundsMin();
			const float* bmax = m_geom.getMeshBoundsMax();
			duDebugDrawBoxWire(&dd, bmin[0],bmin[1],bmin[2], bmax[0],bmax[1],bmax[2], duRGBA(255,255,255,128), 1.0f);
		}

		public void handleRenderOverlay(double[] proj, double[] model, int[] view)
		{
		}

	*/
	public void handleMeshChanged(InputGeom geom)
	{
		m_geom = geom;
	}

	public float[] getBoundsMin()
	{
		if (m_geom == null) return null;
		return m_geom.getMeshBoundsMin();
	}

	public float[] getBoundsMax()
	{
		if (m_geom == null) return null;
		return m_geom.getMeshBoundsMax();
	}

	public void resetCommonSettings()
	{
		m_cellSize = 0.3f;
		m_cellHeight = 0.2f;
		m_agentHeight = 2.0f;
		m_agentRadius = 0.6f;
		m_agentMaxClimb = 0.9f;
		m_agentMaxSlope = 45.0f;
		m_regionMinSize = 8;
		m_regionMergeSize = 20;
		m_monotonePartitioning = false;
		m_edgeMaxLen = 12.0f;
		m_edgeMaxError = 1.3f;
		m_vertsPerPoly = 6.0f;
		m_detailSampleDist = 6.0f;
		m_detailSampleMaxError = 1.0f;
	}

	/* public void handleCommonSettings()
		{
			imguiLabel("Rasterization");
			imguiSlider("Cell Size", &m_cellSize, 0.1f, 1.0f, 0.01f);
			imguiSlider("Cell Height", &m_cellHeight, 0.1f, 1.0f, 0.01f);

			if (m_geom)
			{
				const float* bmin = m_geom.getMeshBoundsMin();
				const float* bmax = m_geom.getMeshBoundsMax();
				int gw = 0, gh = 0;
				rcCalcGridSize(bmin, bmax, m_cellSize, &gw, &gh);
				char text[64];
				snprintf(text, 64, "Voxels  %d x %d", gw, gh);
				imguiValue(text);
			}

			imguiSeparator();
			imguiLabel("Agent");
			imguiSlider("Height", &m_agentHeight, 0.1f, 5.0f, 0.1f);
			imguiSlider("Radius", &m_agentRadius, 0.0f, 5.0f, 0.1f);
			imguiSlider("Max Climb", &m_agentMaxClimb, 0.1f, 5.0f, 0.1f);
			imguiSlider("Max Slope", &m_agentMaxSlope, 0.0f, 90.0f, 1.0f);

			imguiSeparator();
			imguiLabel("Region");
			imguiSlider("Min Region Size", &m_regionMinSize, 0.0f, 150.0f, 1.0f);
			imguiSlider("Merged Region Size", &m_regionMergeSize, 0.0f, 150.0f, 1.0f);
			if (imguiCheck("Monotore Partitioning", m_monotonePartitioning))
				m_monotonePartitioning = !m_monotonePartitioning;

			imguiSeparator();
			imguiLabel("Polygonization");
			imguiSlider("Max Edge Length", &m_edgeMaxLen, 0.0f, 50.0f, 1.0f);
			imguiSlider("Max Edge Error", &m_edgeMaxError, 0.1f, 3.0f, 0.1f);
			imguiSlider("Verts Per Poly", &m_vertsPerPoly, 3.0f, 12.0f, 1.0f);

			imguiSeparator();
			imguiLabel("Detail Mesh");
			imguiSlider("Sample Distance", &m_detailSampleDist, 0.0f, 16.0f, 1.0f);
			imguiSlider("Max Sample Error", &m_detailSampleMaxError, 0.0f, 16.0f, 1.0f);

			imguiSeparator();
		}
	*/
	public void handleClick(float[] s, float[] p, boolean shift)
	{
		if (m_tool != null)
			m_tool.handleClick(s, p, shift);
	}

	/* public void handleToggle()
		{
			if (m_tool)
				m_tool.handleToggle();
		}

		*/
	public void handleStep()
	{
		if (m_tool != null)
			m_tool.handleStep();
	}

	public boolean handleBuild()
	{
		return true;
	}

	public void handleUpdate(float dt)
	{
		if (m_tool != null)
			m_tool.handleUpdate(dt);
		updateToolStates(dt);
	}

	public void updateToolStates(float dt)
	{
		for (SampleToolType i : SampleToolType.values())
		{
			if (m_toolStates.get(i) != null)
				m_toolStates.get(i).handleUpdate(dt);
		}
	}

   /* public void initToolStates(Sample sample)
	{
        for (int i = 0; i < MAX_TOOLS; i++)
        {
            if (m_toolStates[i])
                m_toolStates[i].init(sample);
        }
    }

    public void resetToolStates()
    {
        for (int i = 0; i < MAX_TOOLS; i++)
        {
            if (m_toolStates[i])
                m_toolStates[i].reset();
        }
    }

    public void renderToolStates()
    {
        for (int i = 0; i < MAX_TOOLS; i++)
        {
            if (m_toolStates[i])
                m_toolStates[i].handleRender();
        }
    }

    public void renderOverlayToolStates(double[] proj, double[] model, int[] view)
    {
        for (int i = 0; i < MAX_TOOLS; i++)
        {
            if (m_toolStates[i])
                m_toolStates[i].handleRenderOverlay(proj, model, view);
        }
    }*/
}
