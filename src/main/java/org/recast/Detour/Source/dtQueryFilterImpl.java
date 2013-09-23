package org.recast.Detour.Source;

import org.recast.Detour.Include.dtMeshTile;
import org.recast.Detour.Include.dtPoly;
import org.recast.Detour.Include.dtQueryFilter;

/**
 * @author igozha
 * @since 22.09.13 22:18
 */
public class dtQueryFilterImpl extends dtQueryFilter	
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
	
//	#include <math.h>
//	#include <float.h>
//	#include <string.h>
//	#include "DetourNavMeshQuery.h"
//	#include "DetourNavMesh.h"
//	#include "DetourNode.h"
//	#include "DetourCommon.h"
//	#include "DetourAlloc.h"
//	#include "DetourAssert.h"
//	#include <new>
	
	/// @class dtQueryFilter
	///
	/// <b>The Default Implementation</b>
	/// 
	/// At construction: All area costs default to 1.0.  All flags are included
	/// and none are excluded.
	/// 
	/// If a polygon has both an include and an exclude flag, it will be excluded.
	/// 
	/// The way filtering works, a navigation mesh polygon must have at least one flag 
	/// set to ever be considered by a query. So a polygon with no flags will never
	/// be considered.
	///
	/// Setting the include flags to 0 will result in all polygons being excluded.
	///
	/// <b>Custom Implementations</b>
	/// 
	/// DT_VIRTUAL_QUERYFILTER must be defined in order to extend this class.
	/// 
	/// Implement a custom query filter by overriding the virtual passFilter() 
	/// and getCost() functions. If this is done, both functions should be as 
	/// fast as possible. Use cached local copies of data rather than accessing 
	/// your own objects where possible.
	/// 
	/// Custom implementations do not need to adhere to the flags or cost logic 
	/// used by the default implementation.  
	/// 
	/// In order for A* searches to work properly, the cost should be proportional to
	/// the travel distance. Implementing a cost modifier less than 1.0 is likely 
	/// to lead to problems during pathfinding.
	///
	/// @see dtNavMeshQuery
	
	public dtQueryFilterImpl()
	{
//		:
				m_includeFlags(0xffff),
				m_excludeFlags(0)
		for (int i = 0; i < DT_MAX_AREAS; ++i)
			m_areaCost[i] = 1.0f;
	}
	
//	#ifdef DT_VIRTUAL_QUERYFILTER
//	bool passFilter(const dtPolyRef /*ref*/,
//								   const dtMeshTile* /*tile*/,
//								   const dtPoly* poly) const
//	{
//		return (poly->flags & m_includeFlags) != 0 && (poly->flags & m_excludeFlags) == 0;
//	}
//
//	float getCost(const float* pa, const float* pb,
//								 const dtPolyRef /*prevRef*/, const dtMeshTile* /*prevTile*/, const dtPoly* /*prevPoly*/,
//								 const dtPolyRef /*curRef*/, const dtMeshTile* /*curTile*/, const dtPoly* curPoly,
//								 const dtPolyRef /*nextRef*/, const dtMeshTile* /*nextTile*/, const dtPoly* /*nextPoly*/) const
//	{
//		return dtVdist(pa, pb) * m_areaCost[curPoly->getArea()];
//	}
//	#else
	public boolean passFilter(dtPolyRef ref,
										  dtMeshTile tile,
										  dtPoly poly)
	{
		return (poly.flags & m_includeFlags) != 0 && (poly->flags & m_excludeFlags) == 0;
	}
	
	public float getCost(float[] pa, float[] pb,
										dtPolyRef /*prevRef*/, dtMeshTile prevTile, dtPoly prevPoly,
										dtPolyRef /*curRef*/, dtMeshTile curTile, dtPoly curPoly,
										dtPolyRef /*nextRef*/, dtMeshTile nextTile, dtPoly nextPoly)
	{
		return dtVdist(pa, pb) * m_areaCost[curPoly->getArea()];
	}
//	#endif	
}		
