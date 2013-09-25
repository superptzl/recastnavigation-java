package org.recast.DetourCrowd.Include;

import org.recast.Detour.Include.dtNavMeshQuery;
import org.recast.Detour.Include.dtPoly;
import org.recast.Detour.Include.dtQueryFilter;

public abstract class dtLocalBoundary
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
//    #ifndef DETOURLOCALBOUNDARY_H
//    #define DETOURLOCALBOUNDARY_H
//
//    #include "DetourNavMeshQuery.h"
//
//
//    class dtLocalBoundary
//    {
	public final static int MAX_LOCAL_SEGS = 8;
	public final static int MAX_LOCAL_POLYS = 16;

	public static class Segment
	{
		float s[] = new float[6];    ///< Segment start/end
		float d;    ///< Distance for pruning.
	}

	public float m_center[] = new float[3];
	public Segment m_segs[] = new Segment[MAX_LOCAL_SEGS];
	public int m_nsegs;

	public dtPoly[] m_polys = new dtPoly[MAX_LOCAL_POLYS];
	public int m_npolys;

	public abstract void addSegment(float dist, float[] seg);

//        public:
//        dtLocalBoundary();
//        ~dtLocalBoundary();

	public abstract void reset();

	public abstract void update(dtPoly ref, float[] pos, float collisionQueryRange,
								dtNavMeshQuery navquery, dtQueryFilter filter);

	public abstract boolean isValid(dtNavMeshQuery navquery, dtQueryFilter filter);

	public float[] getCenter()
	{
		return m_center;
	}

	public int getSegmentCount()
	{
		return m_nsegs;
	}

	public float[] getSegment(int i)
	{
		return m_segs[i].s;
	}
//    };
//
//    #endif // DETOURLOCALBOUNDARY_H

}
