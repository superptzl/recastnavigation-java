package org.recast.RecastDemo.Include;

import org.draw.DrawingApplet;
import org.recast.Recast.Include.*;
import org.recast.RecastDemo.Source.SampleImpl;

public abstract class Sample_SoloMesh extends SampleImpl
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
//    #ifndef RECASTSAMPLESOLOMESH_H
//    #define RECASTSAMPLESOLOMESH_H
//
//    #include "Sample.h"
//            #include "DetourNavMesh.h"
//            #include "Recast.h"

	//    class Sample_SoloMesh : public Sample
//    {
//        protected:
	public boolean m_keepInterResults;
	public float m_totalBuildTimeMs;

	public char[] m_triareas;
	public rcHeightfield m_solid;
	public rcCompactHeightfield m_chf;
	public rcContourSet m_cset;
	public rcPolyMesh m_pmesh;
	public rcConfig m_cfg;
	public rcPolyMeshDetail m_dmesh;

    /*public static enum DrawMode {
		DRAWMODE_NAVMESH,
        DRAWMODE_NAVMESH_TRANS,
        DRAWMODE_NAVMESH_BVTREE,
        DRAWMODE_NAVMESH_NODES,
        DRAWMODE_NAVMESH_INVIS,
        DRAWMODE_MESH,
        DRAWMODE_VOXELS,
        DRAWMODE_VOXELS_WALKABLE,
        DRAWMODE_COMPACT,
        DRAWMODE_COMPACT_DISTANCE,
        DRAWMODE_COMPACT_REGIONS,
        DRAWMODE_REGION_CONNECTIONS,
        DRAWMODE_RAW_CONTOURS,
        DRAWMODE_BOTH_CONTOURS,
        DRAWMODE_CONTOURS,
        DRAWMODE_POLYMESH,
        DRAWMODE_POLYMESH_DETAIL,
        MAX_DRAWMODE
    }*/

	public DrawMode m_drawMode;

	public abstract void cleanup();

//    public:
//    Sample_SoloMesh();
//    virtual ~Sample_SoloMesh();
/*
    public abstract void handleSettings();

    public abstract void handleTools();

    public abstract void handleDebugMode();



    public abstract void handleRenderOverlay(double[] proj, double[] model, int[] view);*/

//    public abstract void handleMeshChanged(InputGeom geom);

	public abstract void handleRender(DrawingApplet drawing);

	public abstract boolean handleBuild();
//};

//#endif // RECASTSAMPLESOLOMESHSIMPLE_H

}
