package org.recast.Detour.Source;

import org.recast.Detour.Include.*;
import org.recast.Recast.Include.Recast;

import java.util.Arrays;
import java.util.Comparator;

/**
 * @author igozha
 * @since 22.09.13 20:44
 */
public class DetourNavMeshBuilderImpl extends DetourNavMeshBuilder
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
//	#include <stdio.h>
//	#include <stdlib.h>
//	#include <string.h>
//	#include <float.h>
//	#include "DetourNavMesh.h"
//	#include "DetourCommon.h"
//	#include "DetourNavMeshBuilder.h"
//	#include "DetourAlloc.h"
//	#include "DetourAssert.h"
	
	public final static int MESH_NULL_IDX = 0xffff;
	public final static char XP = 1<<0;
	public final static 			char ZP = 1<<1;
	public final static char XM = 1<<2;
	public final static char ZM = 1<<3;
	
	
	public static class BVItem
	{
		int bmin[] = new int[3];
		int bmax[] = new int[3];
		int i;
	}
	
	public static int compareItemX(BVItem a, BVItem b)
	{
//		BVItem* a = (BVItem*)va;
//		BVItem* b = (BVItem*)vb;
		if (a.bmin[0] < b.bmin[0])
			return -1;
		if (a.bmin[0] > b.bmin[0])
			return 1;
		return 0;
	}
	
	static int compareItemY(BVItem a, BVItem b)
	{
//		BVItem* a = (BVItem*)va;
//		BVItem* b = (BVItem*)vb;
		if (a.bmin[1] < b.bmin[1])
			return -1;
		if (a.bmin[1] > b.bmin[1])
			return 1;
		return 0;
	}
	
	static int compareItemZ(BVItem a, BVItem b)
	{
//		BVItem* a = (BVItem*)va;
//		BVItem* b = (BVItem*)vb;
		if (a.bmin[2] < b.bmin[2])
			return -1;
		if (a.bmin[2] > b.bmin[2])
			return 1;
		return 0;
	}
	
	static void calcExtends(BVItem[] items, int nitems, int imin, int imax,
							int[] bmin, int[] bmax)
	{
		bmin[0] = items[imin].bmin[0];
		bmin[1] = items[imin].bmin[1];
		bmin[2] = items[imin].bmin[2];
		
		bmax[0] = items[imin].bmax[0];
		bmax[1] = items[imin].bmax[1];
		bmax[2] = items[imin].bmax[2];
		
		for (int i = imin+1; i < imax; ++i)
		{
			BVItem it = items[i];
			if (it.bmin[0] < bmin[0]) bmin[0] = it.bmin[0];
			if (it.bmin[1] < bmin[1]) bmin[1] = it.bmin[1];
			if (it.bmin[2] < bmin[2]) bmin[2] = it.bmin[2];
			
			if (it.bmax[0] > bmax[0]) bmax[0] = it.bmax[0];
			if (it.bmax[1] > bmax[1]) bmax[1] = it.bmax[1];
			if (it.bmax[2] > bmax[2]) bmax[2] = it.bmax[2];
		}
	}
	
	public static int longestAxis(int x, int y, int z)
	{
		int	axis = 0;
		int maxVal = x;
		if (y > maxVal)
		{
			axis = 1;
			maxVal = y;
		}
		if (z > maxVal)
		{
			axis = 2;
			maxVal = z;
		}
		return axis;
	}
	
	static void subdivide(BVItem[] items, int nitems, int imin, int imax, int[] curNode, dtBVNode[] nodes)
	{
		int inum = imax - imin;
		int icur = curNode[0];
		
		dtBVNode node = nodes[curNode[0]++];
		
		if (inum == 1)
		{
			// Leaf
			node.bmin[0] = items[imin].bmin[0];
			node.bmin[1] = items[imin].bmin[1];
			node.bmin[2] = items[imin].bmin[2];
			
			node.bmax[0] = items[imin].bmax[0];
			node.bmax[1] = items[imin].bmax[1];
			node.bmax[2] = items[imin].bmax[2];
			
			node.i = items[imin].i;
		}
		else
		{
			// Split
			calcExtends(items, nitems, imin, imax, node.bmin, node.bmax);
			
			int	axis = longestAxis(node.bmax[0] - node.bmin[0],
								   node.bmax[1] - node.bmin[1],
								   node.bmax[2] - node.bmin[2]);
			
			if (axis == 0)
			{
				// Sort along x-axis
//				qsort(items+imin, inum, sizeof(BVItem), compareItemX);
				Arrays.sort(items, imin, imin + inum, new Comparator<BVItem>()
				{
					@Override
					public int compare(BVItem o1, BVItem o2)
					{
						return compareItemX(o1, o2);
					}
				});
			}
			else if (axis == 1)
			{
				// Sort along y-axis
//				qsort(items+imin, inum, sizeof(BVItem), compareItemY);
				Arrays.sort(items, imin, imin + inum, new Comparator<BVItem>()
								{
									@Override
									public int compare(BVItem o1, BVItem o2)
									{
										return compareItemY(o1, o2);
									}
								});
			}
			else
			{
				// Sort along z-axis
//				qsort(items+imin, inum, sizeof(BVItem), compareItemZ);
				Arrays.sort(items, imin, imin + inum, new Comparator<BVItem>()
								{
									@Override
									public int compare(BVItem o1, BVItem o2)
									{
										return compareItemZ(o1, o2);
									}
								});
			}
			
			int isplit = imin+inum/2;
			
			// Left
			subdivide(items, nitems, imin, isplit, curNode, nodes);
			// Right
			subdivide(items, nitems, isplit, imax, curNode, nodes);
			
			int iescape = curNode[0] - icur;
			// Negative index means escape.
			node.i = -iescape;
		}
	}
	
	static int createBVTree(int[] verts, int nverts,
							int[] polys, int npolys, int nvp,
							float cs, float ch,
							int nnodes, dtBVNode[] nodes)
	{
		// Build tree
//		BVItem* items = (BVItem*)dtAlloc(sizeof(BVItem)*npolys, DT_ALLOC_TEMP);
		BVItem[] items = new BVItem[npolys];//(BVItem*)dtAlloc(sizeof(BVItem)*npolys, DT_ALLOC_TEMP);
		for (int i = 0; i < npolys; i++)
		{
			BVItem it = items[i];
			it.i = i;
			// Calc polygon bounds.
//			int* p = &polys[i*nvp*2];
			int[] p = polys;//[i*nvp*2];
			int pIndex = i*nvp*2; 
			it.bmin[0] = it.bmax[0] = verts[p[pIndex+0]*3+0];
			it.bmin[1] = it.bmax[1] = verts[p[pIndex+0]*3+1];
			it.bmin[2] = it.bmax[2] = verts[p[pIndex+0]*3+2];
			
			for (int j = 1; j < nvp; ++j)
			{
				if (p[pIndex+j] == MESH_NULL_IDX) break;
				int x = verts[p[pIndex+j]*3+0];
				int y = verts[p[pIndex+j]*3+1];
				int z = verts[p[pIndex+j]*3+2];
				
				if (x < it.bmin[0]) it.bmin[0] = x;
				if (y < it.bmin[1]) it.bmin[1] = y;
				if (z < it.bmin[2]) it.bmin[2] = z;
				
				if (x > it.bmax[0]) it.bmax[0] = x;
				if (y > it.bmax[1]) it.bmax[1] = y;
				if (z > it.bmax[2]) it.bmax[2] = z;
			}
			// Remap y
			it.bmin[1] = (int)Math.floor((float)it.bmin[1] * ch / cs);
			it.bmax[1] = (int)Math.ceil((float)it.bmax[1] * ch / cs);
		}
		
		int[] curNode = new int[]{0};
		subdivide(items, npolys, 0, npolys, curNode, nodes);
		
		items = null;
//		dtFree(items);
		
		return curNode[0];
	}
	
	static char classifyOffMeshPoint(float[] pt, int ptIndex, float[] bmin, float[] bmax)
	{
			
	
		char outcode = 0; 
		outcode |= (pt[ptIndex+0] >= bmax[0]) ? XP : 0;
		outcode |= (pt[ptIndex+2] >= bmax[2]) ? ZP : 0;
		outcode |= (pt[ptIndex+0] < bmin[0])  ? XM : 0;
		outcode |= (pt[ptIndex+2] < bmin[2])  ? ZM : 0;
	
		switch (outcode)
		{
		case XP: return 0;
		case XP|ZP: return 1;
		case ZP: return 2;
		case XM|ZP: return 3;
		case XM: return 4;
		case XM|ZM: return 5;
		case ZM: return 6;
		case XP|ZM: return 7;
		};
	
		return 0xff;	
	}
	
	// TODO: Better error handling.
	
	/// @par
	/// 
	/// The output data array is allocated using the detour allocator (dtAlloc()).  The method
	/// used to free the memory will be determined by how the tile is added to the navigation
	/// mesh.
	///
	/// @see dtNavMesh, dtNavMesh::addTile()
	public boolean dtCreateNavMeshData(dtNavMeshCreateParams params, dtMeshHeader header, dtMeshTile meshTile)
	{
		if (params.nvp > DetourNavMesh.DT_VERTS_PER_POLYGON)
			return false;
		if (params.vertCount >= 0xffff)
			return false;
		if (params.vertCount == 0 || params.verts == null)
			return false;
		if (params.polyCount == 0|| params.polys == null)
			return false;
	
		int nvp = params.nvp;
		
		// Classify off-mesh connection points. We store only the connections
		// whose start point is inside the tile.
		char[] offMeshConClass;
		int storedOffMeshConCount = 0;
		int offMeshConLinkCount = 0;
		
		if (params.offMeshConCount > 0)
		{
//			offMeshConClass = (char*)dtAlloc(sizeof(char)*params.offMeshConCount*2, DT_ALLOC_TEMP);
			offMeshConClass = new char[params.offMeshConCount*2];
//			if (!offMeshConClass)
//				return false;
	
			// Find tight heigh bounds, used for culling out off-mesh start locations.
			float hmin = Float.MAX_VALUE;
			float hmax = -Float.MAX_VALUE;
			
			if (params.detailVerts != null && params.detailVertsCount != 0)
			{
				for (int i = 0; i < params.detailVertsCount; ++i)
				{
					float h = params.detailVerts[i*3+1];
					hmin = dtMin(hmin, h);
					hmax = dtMax(hmax,h);
				}
			}
			else
			{
				for (int i = 0; i < params.vertCount; ++i)
				{
//					int* iv = &params.verts[i*3];
					float h = params.bmin[1] + params.verts[i*3+1] * params.ch;
					hmin = dtMin(hmin,h);
					hmax = dtMax(hmax,h);
				}
			}
			hmin -= params.walkableClimb;
			hmax += params.walkableClimb;
			float bmin[] = new float[3], bmax[] = new float[3];
			dtVcopy(bmin, params.bmin);
			dtVcopy(bmax, params.bmax);
			bmin[1] = hmin;
			bmax[1] = hmax;
	
			for (int i = 0; i < params.offMeshConCount; ++i)
			{
//				float* p0 = &params.offMeshConVerts[(i*2+0)*3];
				float[] p0 = params.offMeshConVerts;//[(i*2+0)*3];
				int p0Index = (i*2+0)*3;
//				float* p1 = &params.offMeshConVerts[(i*2+1)*3];
				float[] p1 = params.offMeshConVerts;//[(i*2+1)*3];
				int p1Index = (i*2+1)*3;
				offMeshConClass[i*2+0] = classifyOffMeshPoint(p0, p0Index, bmin, bmax);
				offMeshConClass[i*2+1] = classifyOffMeshPoint(p1, p1Index, bmin, bmax);
	
				// Zero out off-mesh start positions which are not even potentially touching the mesh.
				if (offMeshConClass[i*2+0] == 0xff)
				{
					if (p0[p0Index+1] < bmin[1] || p0[p0Index+1] > bmax[1])
						offMeshConClass[i*2+0] = 0;
				}
	
				// Cound how many links should be allocated for off-mesh connections.
				if (offMeshConClass[i*2+0] == 0xff)
					offMeshConLinkCount++;
				if (offMeshConClass[i*2+1] == 0xff)
					offMeshConLinkCount++;
	
				if (offMeshConClass[i*2+0] == 0xff)
					storedOffMeshConCount++;
			}
		}
		
		// Off-mesh connectionss are stored as polygons, adjust values.
		int totPolyCount = params.polyCount + storedOffMeshConCount;
		int totVertCount = params.vertCount + storedOffMeshConCount*2;
		
		// Find portal edges which are at tile borders.
		int edgeCount = 0;
		int portalCount = 0;
		for (int i = 0; i < params.polyCount; ++i)
		{
//			int* p = &params.polys[i*2*nvp];
			int[] p = params.polys;//.polys[i*2*nvp];
			int pIndex = i*2*nvp; 
			for (int j = 0; j < nvp; ++j)
			{
				if (p[pIndex+j] == MESH_NULL_IDX) break;
				edgeCount++;
				
				if ((p[pIndex+nvp+j] & 0x8000) != 0)
				{
					int dir = p[pIndex+nvp+j] & 0xf;
					if (dir != 0xf)
						portalCount++;
				}
			}
		}
	
		int maxLinkCount = edgeCount + portalCount*2 + offMeshConLinkCount*2;
		
		// Find unique detail vertices.
		int uniqueDetailVertCount = 0;
		int detailTriCount = 0;
		if (params.detailMeshes != null)
		{
			// Has detail mesh, count unique detail vertex count and use input detail tri count.
			detailTriCount = params.detailTriCount;
			for (int i = 0; i < params.polyCount; ++i)
			{
//				int* p = &params.polys[i*nvp*2];
				int ndv = params.detailMeshes[i*4+1];
				int nv = 0;
				for (int j = 0; j < nvp; ++j)
				{
					if (params.polys[i*nvp*2+j] == MESH_NULL_IDX) break;
					nv++;
				}
				ndv -= nv;
				uniqueDetailVertCount += ndv;
			}
		}
		else
		{
			// No input detail mesh, build detail mesh from nav polys.
			uniqueDetailVertCount = 0; // No extra detail verts.
			detailTriCount = 0;
			for (int i = 0; i < params.polyCount; ++i)
			{
//				int* p = &params.polys[i*nvp*2];
				int nv = 0;
				for (int j = 0; j < nvp; ++j)
				{
					if (params.polys[i*nvp*2+j] == MESH_NULL_IDX) break;
					nv++;
				}
				detailTriCount += nv-2;
			}
		}
		
		// Calculate data size
		meshTile.verts = new float[3*totVertCount];
		meshTile.links = new dtLink[maxLinkCount];
		for (int i = 0; i < maxLinkCount; i++) {
			meshTile.links[i] = new dtLink();
		}
		meshTile.polys = new dtPoly[totPolyCount];
		for (int i = 0; i < totPolyCount; i++) {
			meshTile.polys[i] = new dtPoly();
		}
		meshTile.detailVerts = new float[3*uniqueDetailVertCount];//navDVerts
		meshTile.detailMeshes = new dtPolyDetail[params.polyCount];
		for (int i = 0; i < params.polyCount; i++) {
			meshTile.detailMeshes[i] = new dtPolyDetail();
		}
		meshTile.detailTris = new char[4*detailTriCount];
		meshTile.bvTree = new dtBVNode[params.buildBvTree ? params.polyCount*2 : 0];
		meshTile.offMeshCons = new dtOffMeshConnection[storedOffMeshConCount];

		// Store header
		header.magic = DetourNavMesh.DT_NAVMESH_MAGIC;
		header.version = DetourNavMesh.DT_NAVMESH_VERSION;
		header.x = params.tileX;
		header.y = params.tileY;
		header.layer = params.tileLayer;
		header.userId = params.userId;
		header.polyCount = totPolyCount;
		header.vertCount = totVertCount;
		header.maxLinkCount = maxLinkCount;
		dtVcopy(header.bmin, params.bmin);
		dtVcopy(header.bmax, params.bmax);
		header.detailMeshCount = params.polyCount;
		header.detailVertCount = uniqueDetailVertCount;
		header.detailTriCount = detailTriCount;
		header.bvQuantFactor = 1.0f / params.cs;
		header.offMeshBase = params.polyCount;
		header.walkableHeight = params.walkableHeight;
		header.walkableRadius = params.walkableRadius;
		header.walkableClimb = params.walkableClimb;
		header.offMeshConCount = storedOffMeshConCount;
		header.bvNodeCount = params.buildBvTree ? params.polyCount*2 : 0;
		
		int offMeshVertsBase = params.vertCount;
		int offMeshPolyBase = params.polyCount;
		
		// Store vertices
		// Mesh vertices
		for (int i = 0; i < params.vertCount; ++i)
		{
//			int* iv = &params.verts[i*3];
			meshTile.verts[i*3+0] = params.bmin[0] + params.verts[i*3+0] * params.cs;
			meshTile.verts[i*3+1] = params.bmin[1] + params.verts[i*3+1] * params.ch;
			meshTile.verts[i*3+2] = params.bmin[2] + params.verts[i*3+2] * params.cs;
		}
		// Off-mesh link vertices.
		int n = 0;
		for (int i = 0; i < params.offMeshConCount; ++i)
		{
			// Only store connections which start from this tile.
			if (offMeshConClass[i*2+0] == 0xff)
			{
//				float linkv = &params.offMeshConVerts[i*2*3];
//				float v = &meshTile.verts[(offMeshVertsBase + n*2)*3];
				dtVcopy(meshTile.verts, (offMeshVertsBase + n*2)*3 + 0, params.offMeshConVerts, i*2*3+0);
				dtVcopy(meshTile.verts, (offMeshVertsBase + n*2)*3 + 3, params.offMeshConVerts, i*2*3+3);
				n++;
			}
		}
		
		// Store polygons
		// Mesh polys

		int[] src = params.polys;
		int srcIndex = 0;
		for (int i = 0; i < params.polyCount; ++i)
		{
			dtPoly p = meshTile.polys[i];
			p.vertCount = 0;
			p.flags = params.polyFlags[i];
			p.setArea(params.polyAreas[i]);
			p.setType(dtPolyTypes.DT_POLYTYPE_GROUND);
			for (int j = 0; j < nvp; ++j)
			{
				if (src[srcIndex+j] == MESH_NULL_IDX) break;
				p.verts[j] = src[srcIndex+j];
				if ((src[srcIndex+nvp+j] & 0x8000) != 0)
				{
					// Border or portal edge.
					int dir = src[srcIndex+nvp+j] & 0xf;
					if (dir == 0xf) // Border
						p.neis[j] = 0;
					else if (dir == 0) // Portal x-
						p.neis[j] = DetourNavMesh.DT_EXT_LINK | 4;
					else if (dir == 1) // Portal z+
						p.neis[j] = DetourNavMesh.DT_EXT_LINK | 2;
					else if (dir == 2) // Portal x+
						p.neis[j] = DetourNavMesh.DT_EXT_LINK | 0;
					else if (dir == 3) // Portal z-
						p.neis[j] = DetourNavMesh.DT_EXT_LINK | 6;
				}
				else
				{
					// Normal connection
					p.neis[j] = src[srcIndex+nvp+j]+1;
				}
				
				p.vertCount++;
			}
//			src += nvp*2;
			srcIndex += nvp*2;
		}
		// Off-mesh connection vertices.
		n = 0;
		for (int i = 0; i < params.offMeshConCount; ++i)
		{
			// Only store connections which start from this tile.
			if (offMeshConClass[i*2+0] == 0xff)
			{
				dtPoly p = meshTile.polys[offMeshPolyBase+n];
				p.vertCount = 2;
				p.verts[0] = (int)(offMeshVertsBase + n*2+0);
				p.verts[1] = (int)(offMeshVertsBase + n*2+1);
				p.flags = params.offMeshConFlags[i];
				p.setArea(params.offMeshConAreas[i]);
				p.setType(dtPolyTypes.DT_POLYTYPE_OFFMESH_CONNECTION);
				n++;
			}
		}
	
		// Store detail meshes and vertices.
		// The nav polygon vertices are stored as the first vertices on each mesh.
		// We compress the mesh data by skipping them and using the navmesh coordinates.
		if (params.detailMeshes != null)
		{
			int vbase = 0;
			for (int i = 0; i < params.polyCount; ++i)
			{
				dtPolyDetail dtl = meshTile.detailMeshes[i];
				int vb = (int)params.detailMeshes[i*4+0];
				int ndv = (int)params.detailMeshes[i*4+1];
//				int nv = navPolys[i].vertCount;
				int nv = meshTile.polys[i].vertCount;
				dtl.vertBase = (int)vbase;
				dtl.vertCount = (char)(ndv-nv);
				dtl.triBase = (int)params.detailMeshes[i*4+2];
				dtl.triCount = (char)params.detailMeshes[i*4+3];
				// Copy vertices except the first 'nv' verts which are equal to nav poly verts.
				if ((ndv-nv) != 0)
				{
					memcpy(&navDVerts[vbase*3], &params.detailVerts[(vb+nv)*3], sizeof(float)*3*(ndv-nv));
					vbase += (int)(ndv-nv);
				}
			}
			// Store triangles.
			memcpy(navDTris, params.detailTris, sizeof(char)*4*params.detailTriCount);
		}
		else
		{
			// Create dummy detail mesh by triangulating polys.
			int tbase = 0;
			for (int i = 0; i < params.polyCount; ++i)
			{
//				dtPolyDetail dtl = navDMeshes[i];
				dtPolyDetail dtl = meshTile.detailMeshes[i];
//				int nv = navPolys[i].vertCount;
				int nv = meshTile.polys[i].vertCount;
				dtl.vertBase = 0;
				dtl.vertCount = 0;
				dtl.triBase = (int)tbase;
				dtl.triCount = (char)(nv-2);
				// Triangulate polygon (local indices).
				for (int j = 2; j < nv; ++j)
				{
//					char* t = &navDTris[tbase*4];
//					char* t = &meshTile.detailTris[tbase*4];
					meshTile.detailTris[tbase*4+0] = 0;
					meshTile.detailTris[tbase*4+1] = (char)(j-1);
					meshTile.detailTris[tbase*4+2] = (char)j;
					// Bit for each edge that belongs to poly boundary.
					meshTile.detailTris[tbase*4+3] = (1<<2);
					if (j == 2) meshTile.detailTris[tbase*4+3] |= (1<<0);
					if (j == nv-1) meshTile.detailTris[tbase*4+3] |= (1<<4);
					tbase++;
				}
			}
		}
	
		// Store and create BVtree.
		// TODO: take detail mesh into account! use byte per bbox extent?
		if (params.buildBvTree)
		{
			createBVTree(params.verts, params.vertCount, params.polys, params.polyCount,
						 nvp, params.cs, params.ch, params.polyCount*2, meshTile.bvTree);
		}
		
		// Store Off-Mesh connections.
		n = 0;
		for (int i = 0; i < params.offMeshConCount; ++i)
		{
			// Only store connections which start from this tile.
			if (offMeshConClass[i*2+0] == 0xff)
			{
//				dtOffMeshConnection con = &offMeshCons[n];
				dtOffMeshConnection con = meshTile.offMeshCons[n];
				con.poly = (int)(offMeshPolyBase + n);
				// Copy connection end-points.
//				float* endPts = &params.offMeshConVerts[i*2*3];
				dtVcopy(con.pos, 0, params.offMeshConVerts, i*2*3+0);
				dtVcopy(con.pos, 3, params.offMeshConVerts, i*2*3+3);
				con.rad = params.offMeshConRad[i];
				con.flags = params.offMeshConDir[i]!=0 ? DetourNavMesh.DT_OFFMESH_CON_BIDIR : 0;
				con.side = offMeshConClass[i*2+1];
				if (params.offMeshConUserID != null)
					con.userId = params.offMeshConUserID[i];
				n++;
			}
		}

		offMeshConClass = null;
//		dtFree(offMeshConClass);
		
//		*outData = data;
//		*outDataSize = dataSize;
		
		return true;
	}
/*
	public boolean dtNavMeshHeaderSwapEndian(dtMeshHeader header, dtMeshTile tile)
	{
//		dtMeshHeader* header = (dtMeshHeader*)data;
		
		int swappedMagic = DetourNavMesh.DT_NAVMESH_MAGIC;
		int swappedVersion = DetourNavMesh.DT_NAVMESH_VERSION;
		dtSwapEndian(&swappedMagic);
		dtSwapEndian(&swappedVersion);
		
		if ((header.magic != DetourNavMesh.DT_NAVMESH_MAGIC || header.version != DetourNavMesh.DT_NAVMESH_VERSION) &&
			(header.magic != swappedMagic || header.version != swappedVersion))
		{
			return false;
		}
			
		dtSwapEndian(&header.magic);
		dtSwapEndian(&header.version);
		dtSwapEndian(&header.x);
		dtSwapEndian(&header.y);
		dtSwapEndian(&header.layer);
		dtSwapEndian(&header.userId);
		dtSwapEndian(&header.polyCount);
		dtSwapEndian(&header.vertCount);
		dtSwapEndian(&header.maxLinkCount);
		dtSwapEndian(&header.detailMeshCount);
		dtSwapEndian(&header.detailVertCount);
		dtSwapEndian(&header.detailTriCount);
		dtSwapEndian(&header.bvNodeCount);
		dtSwapEndian(&header.offMeshConCount);
		dtSwapEndian(&header.offMeshBase);
		dtSwapEndian(&header.walkableHeight);
		dtSwapEndian(&header.walkableRadius);
		dtSwapEndian(&header.walkableClimb);
		dtSwapEndian(&header.bmin[0]);
		dtSwapEndian(&header.bmin[1]);
		dtSwapEndian(&header.bmin[2]);
		dtSwapEndian(&header.bmax[0]);
		dtSwapEndian(&header.bmax[1]);
		dtSwapEndian(&header.bmax[2]);
		dtSwapEndian(&header.bvQuantFactor);
	
		// Freelist index and pointers are updated when tile is added, no need to swap.
	
		return true;
	}
	
	/// @par
	///
	/// @warning This function assumes that the header is in the correct endianess already. 
	/// Call #dtNavMeshHeaderSwapEndian() first on the data if the data is expected to be in wrong endianess 
	/// to start with. Call #dtNavMeshHeaderSwapEndian() after the data has been swapped if converting from 
	/// native to foreign endianess.
	public boolean dtNavMeshDataSwapEndian(dtMeshHeader header, dtMeshTile tile)
	{
		// Make sure the data is in right format.
//		dtMeshHeader* header = (dtMeshHeader*)data;
		if (header.magic != DetourNavMesh.DT_NAVMESH_MAGIC)
			return false;
		if (header.version != DetourNavMesh.DT_NAVMESH_VERSION)
			return false;
		
		// Patch header pointers.
		int headerSize = dtAlign4(sizeof(dtMeshHeader));
		int vertsSize = dtAlign4(sizeof(float)*3*header.vertCount);
		int polysSize = dtAlign4(sizeof(dtPoly)*header.polyCount);
		int linksSize = dtAlign4(sizeof(dtLink)*(header.maxLinkCount));
		int detailMeshesSize = dtAlign4(sizeof(dtPolyDetail)*header.detailMeshCount);
		int detailVertsSize = dtAlign4(sizeof(float)*3*header.detailVertCount);
		int detailTrisSize = dtAlign4(sizeof(char)*4*header.detailTriCount);
		int bvtreeSize = dtAlign4(sizeof(dtBVNode)*header.bvNodeCount);
		int offMeshLinksSize = dtAlign4(sizeof(dtOffMeshConnection)*header.offMeshConCount);
		
		char* d = data + headerSize;
		float* verts = (float*)d; d += vertsSize;
		dtPoly* polys = (dtPoly*)d; d += polysSize;
		*//*dtLink* links = (dtLink*)d;*//* d += linksSize;
		dtPolyDetail* detailMeshes = (dtPolyDetail*)d; d += detailMeshesSize;
		float* detailVerts = (float*)d; d += detailVertsSize;
		*//*char* detailTris = (char*)d;*//* d += detailTrisSize;
		dtBVNode* bvTree = (dtBVNode*)d; d += bvtreeSize;
		dtOffMeshConnection* offMeshCons = (dtOffMeshConnection*)d; d += offMeshLinksSize;
		
		// Vertices
		for (int i = 0; i < header.vertCount*3; ++i)
		{
			dtSwapEndian(&verts[i]);
		}
	
		// Polys
		for (int i = 0; i < header.polyCount; ++i)
		{
			dtPoly* p = &polys[i];
			// poly.firstLink is update when tile is added, no need to swap.
			for (int j = 0; j < DetourNavMesh.DT_VERTS_PER_POLYGON; ++j)
			{
				dtSwapEndian(&p.verts[j]);
				dtSwapEndian(&p.neis[j]);
			}
			dtSwapEndian(&p.flags);
		}
	
		// Links are rebuild when tile is added, no need to swap.
	
		// Detail meshes
		for (int i = 0; i < header.detailMeshCount; ++i)
		{
			dtPolyDetail* pd = &detailMeshes[i];
			dtSwapEndian(&pd.vertBase);
			dtSwapEndian(&pd.triBase);
		}
		
		// Detail verts
		for (int i = 0; i < header.detailVertCount*3; ++i)
		{
			dtSwapEndian(&detailVerts[i]);
		}
	
		// BV-tree
		for (int i = 0; i < header.bvNodeCount; ++i)
		{
			dtBVNode* node = &bvTree[i];
			for (int j = 0; j < 3; ++j)
			{
				dtSwapEndian(&node.bmin[j]);
				dtSwapEndian(&node.bmax[j]);
			}
			dtSwapEndian(&node.i);
		}
	
		// Off-mesh Connections.
		for (int i = 0; i < header.offMeshConCount; ++i)
		{
			dtOffMeshConnection* con = &offMeshCons[i];
			for (int j = 0; j < 6; ++j)
				dtSwapEndian(&con.pos[j]);
			dtSwapEndian(&con.rad);
			dtSwapEndian(&con.poly);
		}
		
		return true;
	}
	*/
}
