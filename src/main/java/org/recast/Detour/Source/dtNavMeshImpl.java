package org.recast.Detour.Source;

import org.recast.Detour.Include.*;

import java.util.Arrays;

/**
 * @author igozha
 * @since 22.09.13 21:54
 */
public class dtNavMeshImpl extends dtNavMesh {
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
//	#include <stdio.h>
//	#include "DetourNavMesh.h"
//	#include "DetourNode.h"
//	#include "DetourCommon.h"
//	#include "DetourAlloc.h"
//	#include "DetourAssert.h"
//	#include <new>


    public static boolean overlapSlabs(float[] amin, float[] amax,
                                       float[] bmin, float[] bmax,
                                       float px, float py) {
        // Check for horizontal overlap.
        // The segment is shrunken a little so that slabs which touch
        // at end points are not connected.
        float minx = DetourCommon.dtMax(amin[0] + px, bmin[0] + px);
        float maxx = DetourCommon.dtMin(amax[0] - px, bmax[0] - px);
        if (minx > maxx)
            return false;

        // Check vertical overlap.
        float ad = (amax[1] - amin[1]) / (amax[0] - amin[0]);
        float ak = amin[1] - ad * amin[0];
        float bd = (bmax[1] - bmin[1]) / (bmax[0] - bmin[0]);
        float bk = bmin[1] - bd * bmin[0];
        float aminy = ad * minx + ak;
        float amaxy = ad * maxx + ak;
        float bminy = bd * minx + bk;
        float bmaxy = bd * maxx + bk;
        float dmin = bminy - aminy;
        float dmax = bmaxy - amaxy;

        // Crossing segments always overlap.
        if (dmin * dmax < 0)
            return true;

        // Check for overlap at endpoints.
        float thr = DetourCommon.dtSqr(py * 2);
        if (dmin * dmin <= thr || dmax * dmax <= thr)
            return true;

        return false;
    }

    static float getSlabCoord(float[] va, int vaIndex, int side) {
        if (side == 0 || side == 4)
            return va[vaIndex+0];
        else if (side == 2 || side == 6)
            return va[vaIndex+2];
        return 0;
    }

    static void calcSlabEndPoints(float[] va, int vaIndex, float[] vb, int vbIndex, float[] bmin, float[] bmax, int side) {
        if (side == 0 || side == 4) {
            if (va[vaIndex + 2] < vb[vbIndex + 2]) {
                bmin[0] = va[vaIndex + 2];
                bmin[1] = va[vaIndex + 1];
                bmax[0] = vb[vbIndex + 2];
                bmax[1] = vb[vbIndex + 1];
            } else {
                bmin[0] = vb[vbIndex + 2];
                bmin[1] = vb[vbIndex + 1];
                bmax[0] = va[vaIndex + 2];
                bmax[1] = va[vaIndex + 1];
            }
        } else if (side == 2 || side == 6) {
            if (va[vaIndex + 0] < vb[vbIndex + 0]) {
                bmin[0] = va[vaIndex + 0];
                bmin[1] = va[vaIndex + 1];
                bmax[0] = vb[vbIndex + 0];
                bmax[1] = vb[vbIndex + 1];
            } else {
                bmin[0] = vb[vbIndex + 0];
                bmin[1] = vb[vbIndex + 1];
                bmax[0] = va[vaIndex + 0];
                bmax[1] = va[vaIndex + 1];
            }
        }
    }

    public static int computeTileHash(int x, int y, int mask) {
        long h1 = 0x8da6b343L; // Large multiplicative ants;
        long h2 = 0xd8163841L; // here arbitrarily chosen primes
        long n = h1 * x + h2 * y;
        return (int) (n & mask);
    }

    public static int allocLink(dtMeshTile tile) {
        if (tile.linksFreeList == DetourNavMesh.DT_NULL_LINK)
            return DetourNavMesh.DT_NULL_LINK;
        int link = tile.linksFreeList;
        tile.linksFreeList = tile.links[link].next;
        return link;
    }

    public static void freeLink(dtMeshTile tile, int link) {
        tile.links[link].next = tile.linksFreeList;
        tile.linksFreeList = link;
    }


//	dtNavMesh* dtAllocNavMesh()
//	{
//		void* mem = dtAlloc(sizeof(dtNavMesh), DT_ALLOC_PERM);
//		if (!mem) return 0;
//		return new(mem) dtNavMesh;
//	}
//
//	/// @par
//	///
//	/// This function will only free the memory for tiles with the #DT_TILE_FREE_DATA
//	/// flag set.
//	void dtFreeNavMesh(dtNavMesh* navmesh)
//	{
//		if (!navmesh) return;
//		navmesh.~dtNavMesh();
//		dtFree(navmesh);
//	}

    //////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @class dtNavMesh
     * <p/>
     * The navigation mesh consists of one or more tiles defining three primary types of structural data:
     * <p/>
     * A polygon mesh which defines most of the navigation graph. (See rcPolyMesh for its structure.)
     * A detail mesh used for determining surface height on the polygon mesh. (See rcPolyMeshDetail for its structure.)
     * Off-mesh connections, which define custom point-to-point edges within the navigation graph.
     * <p/>
     * The general build process is as follows:
     * <p/>
     * -# Create rcPolyMesh and rcPolyMeshDetail data using the Recast build pipeline.
     * -# Optionally, create off-mesh connection data.
     * -# Combine the source data into a dtNavMeshCreateParams structure.
     * -# Create a tile data array using dtCreateNavMeshData().
     * -# Allocate at dtNavMesh object and initialize it. (For single tile navigation meshes,
     * the tile data is loaded during this step.)
     * -# For multi-tile navigation meshes, load the tile data using addTile().
     * <p/>
     * Notes:
     * <p/>
     * - This class is usually used in conjunction with the dtNavMeshQuery class for pathfinding.
     * - Technically, all navigation meshes are tiled. A 'solo' mesh is simply a navigation mesh initialized
     * to have only a single tile.
     * - This class does not implement any asynchronous methods. So the ::dtStatus result of all methods will
     * always contain either a success or failure flag.
     * @see dtNavMeshQuery, dtCreateNavMeshData, dtNavMeshCreateParams, #dtAllocNavMesh, #dtFreeNavMesh
     */

    public dtNavMeshImpl() {

//		m_tileWidth(0),
//		m_tileHeight(0),
//		m_maxTiles(0),
//		m_tileLutSize(0),
//		m_tileLutMask(0),
//		m_posLookup(0),
//		m_nextFree(0),
//		m_tiles(0),
//		m_saltBits(0),
//		m_tileBits(0),
//		m_polyBits(0)
//		memset(&m_params, 0, sizeof(dtNavMeshParams));
        m_orig[0] = 0;
        m_orig[1] = 0;
        m_orig[2] = 0;
    }

	/*~dtNavMesh()
    {
		for (int i = 0; i < m_maxTiles; ++i)
		{
			if (m_tiles[i].flags & DT_TILE_FREE_DATA)
			{
				dtFree(m_tiles[i].data);
				m_tiles[i].data = 0;
				m_tiles[i].dataSize = 0;
			}
		}
		dtFree(m_posLookup);
		dtFree(m_tiles);
	}*/

    public dtStatus init(dtNavMeshParams params) {
//		memcpy(&m_params, params, sizeof(dtNavMeshParams));
        m_params = params;
        DetourCommon.dtVcopy(m_orig, params.orig);
        m_tileWidth = params.tileWidth;
        m_tileHeight = params.tileHeight;

        // Init tiles
        m_maxTiles = params.maxTiles;
        m_tileLutSize = DetourCommon.dtNextPow2(params.maxTiles / 4);
        if (m_tileLutSize == 0) m_tileLutSize = 1;
        m_tileLutMask = m_tileLutSize - 1;

//		m_tiles = (dtMeshTile*)dtAlloc(sizeof(dtMeshTile)*m_maxTiles, DT_ALLOC_PERM);
        m_tiles = new dtMeshTile[m_maxTiles];
        for (int i = 0; i < m_maxTiles; i++) {
            m_tiles[i] = new dtMeshTile();
        }
//		if (!m_tiles)
//			return DT_FAILURE | DT_OUT_OF_MEMORY;
//		m_posLookup = (dtMeshTile**)dtAlloc(sizeof(dtMeshTile*)*m_tileLutSize, DT_ALLOC_PERM);
        m_posLookup = new dtMeshTile[m_tileLutSize];
        for (int i = 0; i < m_tileLutSize; i++) {
            m_posLookup[i] = new dtMeshTile();
        }
//		if (!m_posLookup)
//			return DT_FAILURE | DT_OUT_OF_MEMORY;
//		memset(m_tiles, 0, sizeof(dtMeshTile)*m_maxTiles);
//		memset(m_posLookup, 0, sizeof(dtMeshTile*)*m_tileLutSize);
//		m_nextFree = 0;
        for (int i = m_maxTiles - 1; i >= 0; --i) {
            m_tiles[i].salt = 1;
            m_tiles[i].next = m_nextFree;
            m_nextFree = m_tiles[i];
        }

        // Init ID generator values.
        m_tileBits = DetourCommon.dtIlog2(DetourCommon.dtNextPow2((int) params.maxTiles));
        m_polyBits = DetourCommon.dtIlog2(DetourCommon.dtNextPow2((int) params.maxPolys));
        // Only allow 31 salt bits, since the salt mask is calculated using 32bit uint and it will overflow.
        m_saltBits = DetourCommon.dtMin((int) 31, 32 - m_tileBits - m_polyBits);
        if (m_saltBits < 10)
            return new dtStatus(dtStatus.DT_FAILURE | dtStatus.DT_INVALID_PARAM);

        return new dtStatus(dtStatus.DT_SUCCESS);
    }

    public dtStatus init(dtMeshHeader header,int flags) {
        // Make sure the data is in right format.
//		dtMeshHeader* header = (dtMeshHeader*)data;
        if (header.magic != DetourNavMesh.DT_NAVMESH_MAGIC)
            return new dtStatus(dtStatus.DT_FAILURE | dtStatus.DT_WRONG_MAGIC);
        if (header.version != DetourNavMesh.DT_NAVMESH_VERSION)
            return new dtStatus(dtStatus.DT_FAILURE | dtStatus.DT_WRONG_VERSION);

        dtNavMeshParams params = new dtNavMeshParams();
        DetourCommon.dtVcopy(params.orig, header.bmin);
        params.tileWidth = header.bmax[0] - header.bmin[0];
        params.tileHeight = header.bmax[2] - header.bmin[2];
        params.maxTiles = 1;
        params.maxPolys = header.polyCount;

        dtStatus status = init(params);
        if (dtStatus.dtStatusFailed(status))
            return status;

        return addTile(header, flags, null, null);
    }

    /// @par
    ///
    /// @note The parameters are created automatically when the single tile
    /// initialization is performed.
    public dtNavMeshParams getParams() {
        return m_params;
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    public int findConnectingPolys(float[] va, float[] vb,
                                   dtMeshTile tile, int side,
                                   dtPoly[] con, float[] conarea, int maxcon) {
        if (tile == null) return 0;

        float amin[] = new float[2], amax[] = new float[2];
        calcSlabEndPoints(va, 0, vb, 0, amin, amax, side);
        float apos = getSlabCoord(va, 0, side);

        // Remove links pointing to 'side' and compact the links array.
        float bmin[] = new float[2], bmax[] = new float[2];
        int m = DetourNavMesh.DT_EXT_LINK | (int) side;
        int n = 0;

//        dtPoly base = getPolyRefBase(tile);

        for (int i = 0; i < tile.header.polyCount; ++i) {
            dtPoly poly = tile.polys[i];
            int nv = poly.vertCount;
            for (int j = 0; j < nv; ++j) {
                // Skip edges which do not point to the right side.
                if (poly.neis[j] != m) continue;

                float[] vc = tile.verts;//[poly.verts[j] * 3];
                int vcIndex = poly.verts[j] * 3;
                float[] vd = tile.verts;//[poly.verts[(j + 1) % nv] * 3];
                int vdIndex = poly.verts[(j + 1) % nv] * 3;
                float bpos = getSlabCoord(vc, vcIndex, side);

                // Segments are not close enough.
                if (DetourCommon.dtAbs(apos - bpos) > 0.01f)
                    continue;

                // Check if the segments touch.
                calcSlabEndPoints(vc, vcIndex, vd, vcIndex, bmin, bmax, side);

                if (!overlapSlabs(amin, amax, bmin, bmax, 0.01f, tile.header.walkableClimb)) continue;

                // Add return value.
                if (n < maxcon) {
                    conarea[n * 2 + 0] = DetourCommon.dtMax(amin[0], bmin[0]);
                    conarea[n * 2 + 1] = DetourCommon.dtMin(amax[0], bmax[0]);
//                    con[n] = base | (dtPoly) i;
                    n++;
                }
                break;
            }
        }
        return n;
    }

//    public void unconnectExtLinks(dtMeshTile tile, dtMeshTile target) {
//        if (!tile || !target) return;
//
//        int targetNum = decodePolyIdTile(getTileRef(target));
//
//        for (int i = 0; i < tile.header.polyCount; ++i) {
//            dtPoly * poly =&tile.polys[i];
//            unsigned int j = poly.firstLink;
//            unsigned int pj = DT_NULL_LINK;
//            while (j != DT_NULL_LINK) {
//                if (tile.links[j].side != 0xff &&
//                        decodePolyIdTile(tile.links[j].ref) == targetNum) {
//                    // Revove link.
//                    unsigned int nj = tile.links[j].next;
//                    if (pj == DT_NULL_LINK)
//                        poly.firstLink = nj;
//                    else
//                        tile.links[pj].next = nj;
//                    freeLink(tile, j);
//                    j = nj;
//                } else {
//                    // Advance
//                    pj = j;
//                    j = tile.links[j].next;
//                }
//            }
//        }
//    }

    public void connectExtLinks(dtMeshTile tile, dtMeshTile target, int side) {
//        if (tile == null) return;
//
//        // Connect border links.
//        for (int i = 0; i < tile.header.polyCount; ++i) {
//            dtPoly poly = tile.polys[i];
//
//            // Create new links.
//            //		unsigned short m = DT_EXT_LINK | (unsigned short)side;
//
//            int nv = poly.vertCount;
//            for (int j = 0; j < nv; ++j) {
//                // Skip non-portal edges.
//                if ((poly.neis[j] & DetourNavMesh.DT_EXT_LINK) == 0)
//                    continue;
//
//                int dir = (int) (poly.neis[j] & 0xff);
//                if (side != -1 && dir != side)
//                    continue;
//
//                // Create new links
//                float*va =&tile.verts[poly.verts[j] * 3];
//                float*vb =&tile.verts[poly.verts[(j + 1) % nv] * 3];
//                dtPoly nei[] = new dtPoly[4];
//                float neia[] = new float[ 4 * 2];
//                int nnei = findConnectingPolys(va, vb, target, dtOppositeTile(dir), nei, neia, 4);
//                for (int k = 0; k < nnei; ++k) {
//                    int idx = allocLink(tile);
//                    if (idx != DetourNavMesh.DT_NULL_LINK) {
//                        dtLink link = tile.links[idx];
//                        link.ref = nei[k];
//                        link.edge = (char)j;
//                        link.side = (char)dir;
//
//                        link.next = poly.firstLink;
//                        poly.firstLink = idx;
//
//                        // Compress portal limits to a byte value.
//                        if (dir == 0 || dir == 4) {
//                            float tmin = (neia[k * 2 + 0] - va[2]) / (vb[2] - va[2]);
//                            float tmax = (neia[k * 2 + 1] - va[2]) / (vb[2] - va[2]);
//                            if (tmin > tmax)
//                                dtSwap(tmin, tmax);
//                            link.bmin = (unsigned char)(dtClamp(tmin, 0.0f, 1.0f) * 255.0f);
//                            link.bmax = (unsigned char)(dtClamp(tmax, 0.0f, 1.0f) * 255.0f);
//                        } else if (dir == 2 || dir == 6) {
//                            float tmin = (neia[k * 2 + 0] - va[0]) / (vb[0] - va[0]);
//                            float tmax = (neia[k * 2 + 1] - va[0]) / (vb[0] - va[0]);
//                            if (tmin > tmax)
//                                dtSwap(tmin, tmax);
//                            link.bmin = (unsigned char)(dtClamp(tmin, 0.0f, 1.0f) * 255.0f);
//                            link.bmax = (unsigned char)(dtClamp(tmax, 0.0f, 1.0f) * 255.0f);
//                        }
//                    }
//                }
//            }
//        }
    }
//
    public void connectExtOffMeshLinks(dtMeshTile tile, dtMeshTile target, int side) {
//        if (tile == null) return;
//
//        // Connect off-mesh links.
//        // We are interested on links which land from target tile to this tile.
//        char oppositeSide = (side == -1) ? 0xff : (char)dtOppositeTile(side);
//
//        for (int i = 0; i < target.header.offMeshConCount; ++i) {
//            dtOffMeshConnection targetCon = target.offMeshCons[i];
//            if (targetCon.side != oppositeSide)
//                continue;
//
//            dtPoly targetPoly = target.polys[targetCon.poly];
//            // Skip off-mesh connections which start location could not be connected at all.
//            if (targetPoly.firstLink == DetourNavMesh.DT_NULL_LINK)
//                continue;
//
//            float ext[]={
//                targetCon.rad, target.header.walkableClimb, targetCon.rad
//            } ;
//
//            // Find polygon to connect to.
//            float*p =&targetCon.pos[3];
//            float nearestPt[] = new float[3];
//            dtPoly ref = findNearestPolyInTile(tile, p, ext, nearestPt);
//            if (ref == null)
//                continue;
//            // findNearestPoly may return too optimistic results, further check to make sure.
//            if (DetourCommon.dtSqr(nearestPt[0] - p[0]) + DetourCommon.dtSqr(nearestPt[2] - p[2]) > DetourCommon.dtSqr(targetCon.rad))
//                continue;
//            // Make sure the location is on current mesh.
//            float*v =&target.verts[targetPoly.verts[1] * 3];
//            DetourCommon.dtVcopy(v, nearestPt);
//
//            // Link off-mesh connection to target poly.
//            int idx = allocLink(target);
//            if (idx != DetourNavMesh.DT_NULL_LINK) {
//                dtLink link = target.links[idx];
//                link.ref = ref;
//                link.edge = (char)1;
//                link.side = oppositeSide;
//                link.bmin = link.bmax = 0;
//                // Add to linked list.
//                link.next = targetPoly.firstLink;
//                targetPoly.firstLink = idx;
//            }
//
//            // Link target poly to off-mesh connection.
//            if (targetCon.flags & DT_OFFMESH_CON_BIDIR) {
//                int tidx = allocLink(tile);
//                if (tidx != DetourNavMesh.DT_NULL_LINK) {
//                    int landPolyIdx = (int)decodePolyIdPoly(ref);
//                    dtPoly landPoly = tile.polys[landPolyIdx];
//                    dtLink link = tile.links[tidx];
//                    link.ref = getPolyRefBase(target) | (dtPoly) (targetCon.poly);
//                    link.edge = 0xff;
//                    link.side = (char)(side == -1 ? 0xff : side);
//                    link.bmin = link.bmax = 0;
//                    // Add to linked list.
//                    link.next = landPoly.firstLink;
//                    landPoly.firstLink = tidx;
//                }
//            }
//        }
//
    }

    public void connectIntLinks(dtMeshTile tile) {
        if (tile == null) return;

//        dtPoly base = getPolyRefBase(tile);

//        for (int i = 0; i < tile.header.polyCount; ++i) {
//            dtPoly poly = tile.polys[i];
//            poly.firstLink = DetourNavMesh.DT_NULL_LINK;
//
//            if (poly.getType() == DT_POLYTYPE_OFFMESH_CONNECTION)
//                continue;
//
//            // Build edge links backwards so that the links will be
//            // in the linked list from lowest index to highest.
//            for (int j = poly.vertCount - 1; j >= 0; --j) {
//                // Skip hard and non-internal edges.
//                if (poly.neis[j] == 0 || (poly.neis[j] & DetourNavMesh.DT_EXT_LINK)) continue;
//
//                int idx = allocLink(tile);
//                if (idx != DetourNavMesh.DT_NULL_LINK) {
//                    dtLink link = tile.links[idx];
//                    link.ref = base | (dtPoly) (poly.neis[j] - 1);
//                    link.edge = (char)j;
//                    link.side = 0xff;
//                    link.bmin = link.bmax = 0;
//                    // Add to linked list.
//                    link.next = poly.firstLink;
//                    poly.firstLink = idx;
//                }
//            }
//        }
    }

    public void baseOffMeshLinks(dtMeshTile tile) {
//        if (tile == null) return;
//
//        dtPoly base = getPolyRefBase(tile);
//
//        // Base off-mesh connection start points.
//        for (int i = 0; i < tile.header.offMeshConCount; ++i) {
//            dtOffMeshConnection con = tile.offMeshCons[i];
//            dtPoly  poly = tile.polys[con.poly];
//
//            float ext[]={
//                con.rad, tile.header.walkableClimb, con.rad
//            } ;
//
//            // Find polygon to connect to.
//            float*p = con.pos[0]; // First vertex
//            float nearestPt[] = new float[3];
//            dtPoly ref = findNearestPolyInTile(tile, p, ext, nearestPt);
//            if (ref == null) continue;
//            // findNearestPoly may return too optimistic results, further check to make sure.
//            if (DetourCommon.dtSqr(nearestPt[0] - p[0]) + DetourCommon.dtSqr(nearestPt[2] - p[2]) > DetourCommon.dtSqr(con.rad))
//                continue;
//            // Make sure the location is on current mesh.
//            float*v =&tile.verts[poly.verts[0] * 3];
//            DetourCommon.dtVcopy(v, nearestPt);
//
//            // Link off-mesh connection to target poly.
//            int idx = allocLink(tile);
//            if (idx != DetourNavMesh.DT_NULL_LINK) {
//                dtLink link = tile.links[idx];
//                link.ref = ref;
//                link.edge = (char)0;
//                link.side = 0xff;
//                link.bmin = link.bmax = 0;
//                // Add to linked list.
//                link.next = poly.firstLink;
//                poly.firstLink = idx;
//            }
//
//            // Start end-point is always connect back to off-mesh connection.
//            int tidx = allocLink(tile);
//            if (tidx != DetourNavMesh.DT_NULL_LINK) {
//                int landPolyIdx = (int)decodePolyIdPoly(ref);
//                dtPoly landPoly = tile.polys[landPolyIdx];
//                dtLink link = tile.links[tidx];
//                link.ref = base | (dtPoly) (con.poly);
//                link.edge = 0xff;
//                link.side = 0xff;
//                link.bmin = link.bmax = 0;
//                // Add to linked list.
//                link.next = landPoly.firstLink;
//                landPoly.firstLink = tidx;
//            }
//        }
    }

//    public void closestPointOnPolyInTile(dtMeshTile tile, int ip,
//                                         float[] pos, float[] closest) {
//        dtPoly poly = tile.polys[ip];
//        // Off-mesh connections don't have detail polygons.
//        if (poly.getType() == DT_POLYTYPE_OFFMESH_CONNECTION) {
//            float*v0 =&tile.verts[poly.verts[0] * 3];
//            float*v1 =&tile.verts[poly.verts[1] * 3];
//            float d0 = dtVdist(pos, v0);
//            float d1 = dtVdist(pos, v1);
//            float u = d0 / (d0 + d1);
//            dtVlerp(closest, v0, v1, u);
//            return;
//        }
//
//        dtPolyDetail pd = tile.detailMeshes[ip];
//
//        // Clamp point to be inside the polygon.
//        float verts[ ] = new float[DetourNavMesh.DT_VERTS_PER_POLYGON * 3];
//        float edged[ ] = new float[DetourNavMesh.DT_VERTS_PER_POLYGON];
//        float edget[ ] = new float[DetourNavMesh.DT_VERTS_PER_POLYGON];
//        int nv = poly.vertCount;
//        for (int i = 0; i < nv; ++i)
//            DetourCommon.dtVcopy( & verts[i * 3],&tile.verts[poly.verts[i] * 3]);
//
//        DetourCommon.dtVcopy(closest, pos);
//        if (!dtDistancePtPolyEdgesSqr(pos, verts, nv, edged, edget)) {
//            // Point is outside the polygon, dtClamp to nearest edge.
//            float dmin = Float.MAX_VALUE;
//            int imin = -1;
//            for (int i = 0; i < nv; ++i) {
//                if (edged[i] < dmin) {
//                    dmin = edged[i];
//                    imin = i;
//                }
//            }
//            float*va =&verts[imin * 3];
//            float*vb =&verts[((imin + 1) % nv) * 3];
//            dtVlerp(closest, va, vb, edget[imin]);
//        }
//
//        // Find height at the location.
//        for (int j = 0; j < pd.triCount; ++j) {
//            unsigned char*t =&tile.detailTris[(pd.triBase + j) * 4];
//            float*v[3];
//            for (int k = 0; k < 3; ++k) {
//                if (t[k] < poly.vertCount)
//                    v[k] =&tile.verts[poly.verts[t[k]] * 3];
//                else
//                v[k] =&tile.detailVerts[(pd.vertBase + (t[k] - poly.vertCount)) * 3];
//            }
//            float h;
//            if (dtClosestHeightPointTriangle(pos, v[0], v[1], v[2], h)) {
//                closest[1] = h;
//                break;
//            }
//        }
//    }

//    public dtPoly findNearestPolyInTile(dtMeshTile tile,
//                                        float[] center, float[] extents,
//                                        float[] nearestPt) {
//        float bmin[ 3],bmax[3];
//        dtVsub(bmin, center, extents);
//        dtVadd(bmax, center, extents);
//
//        // Get nearby polygons from proximity grid.
//        dtPoly polys[ 128];
//        int polyCount = queryPolygonsInTile(tile, bmin, bmax, polys, 128);
//
//        // Find nearest polygon amongst the nearby polygons.
//        dtPoly nearest = 0;
//        float nearestDistanceSqr = FLT_MAX;
//        for (int i = 0; i < polyCount; ++i) {
//            dtPoly ref = polys[i];
//            float closestPtPoly[ 3];
//            closestPointOnPolyInTile(tile, decodePolyIdPoly(ref), center, closestPtPoly);
//            float d = dtVdistSqr(center, closestPtPoly);
//            if (d < nearestDistanceSqr) {
//                if (nearestPt)
//                    dtVcopy(nearestPt, closestPtPoly);
//                nearestDistanceSqr = d;
//                nearest = ref;
//            }
//        }
//
//        return nearest;
//    }

//    public int queryPolygonsInTile(dtMeshTile tile, float[] qmin, float[] qmax,
//                                   dtPoly[] polys, int maxPolys) {
//        if (tile.bvTree) {
//            dtBVNode * node =&tile.bvTree[0];
//            dtBVNode * end =&tile.bvTree[tile.header.bvNodeCount];
//            float*tbmin = tile.header.bmin;
//            float*tbmax = tile.header.bmax;
//            float qfac = tile.header.bvQuantFactor;
//
//            // Calculate quantized box
//            unsigned short bmin[ 3],bmax[3];
//            // dtClamp query box to world box.
//            float minx = dtClamp(qmin[0], tbmin[0], tbmax[0]) - tbmin[0];
//            float miny = dtClamp(qmin[1], tbmin[1], tbmax[1]) - tbmin[1];
//            float minz = dtClamp(qmin[2], tbmin[2], tbmax[2]) - tbmin[2];
//            float maxx = dtClamp(qmax[0], tbmin[0], tbmax[0]) - tbmin[0];
//            float maxy = dtClamp(qmax[1], tbmin[1], tbmax[1]) - tbmin[1];
//            float maxz = dtClamp(qmax[2], tbmin[2], tbmax[2]) - tbmin[2];
//            // Quantize
//            bmin[0] = (unsigned short)(qfac * minx) & 0xfffe;
//            bmin[1] = (unsigned short)(qfac * miny) & 0xfffe;
//            bmin[2] = (unsigned short)(qfac * minz) & 0xfffe;
//            bmax[0] = (unsigned short)(qfac * maxx + 1) | 1;
//            bmax[1] = (unsigned short)(qfac * maxy + 1) | 1;
//            bmax[2] = (unsigned short)(qfac * maxz + 1) | 1;
//
//            // Traverse tree
//            dtPoly base = getPolyRefBase(tile);
//            int n = 0;
//            while (node < end) {
//                bool overlap = dtOverlapQuantBounds(bmin, bmax, node.bmin, node.bmax);
//                bool isLeafNode = node.i >= 0;
//
//                if (isLeafNode && overlap) {
//                    if (n < maxPolys)
//                        polys[n++] = base | (dtPoly) node.i;
//                }
//
//                if (overlap || isLeafNode)
//                    node++;
//                else {
//                    int escapeIndex = -node.i;
//                    node += escapeIndex;
//                }
//            }
//
//            return n;
//        } else {
//            float bmin[ 3],bmax[3];
//            int n = 0;
//            dtPoly base = getPolyRefBase(tile);
//            for (int i = 0; i < tile.header.polyCount; ++i) {
//                dtPoly * p =&tile.polys[i];
//                // Do not return off-mesh connection polygons.
//                if (p.getType() == DT_POLYTYPE_OFFMESH_CONNECTION)
//                    continue;
//                // Calc polygon bounds.
//                float*v =&tile.verts[p.verts[0] * 3];
//                dtVcopy(bmin, v);
//                dtVcopy(bmax, v);
//                for (int j = 1; j < p.vertCount; ++j) {
//                    v =&tile.verts[p.verts[j] * 3];
//                    dtVmin(bmin, v);
//                    dtVmax(bmax, v);
//                }
//                if (dtOverlapBounds(qmin, qmax, bmin, bmax)) {
//                    if (n < maxPolys)
//                        polys[n++] = base | (dtPoly) i;
//                }
//            }
//            return n;
//        }
//    }

    /// @par
    ///
    /// The add operation will fail if the data is in the wrong format, the allocated tile
    /// space is full, or there is a tile already at the specified reference.
    ///
    /// The lastRef parameter is used to restore a tile with the same tile
    /// reference it had previously used.  In this case the #dtPoly's for the
    /// tile will be restored to the same values they were before the tile was
    /// removed.
    ///
    /// @see dtCreateNavMeshData, #removeTile
    public dtStatus addTile(dtMeshHeader header, int flags,
                            dtMeshTile lastRef, dtMeshTile[] result) {
        // Make sure the data is in right format.
//		dtMeshHeader* header = (dtMeshHeader*)data;
        if (header.magic != DetourNavMesh.DT_NAVMESH_MAGIC)
            return new dtStatus(dtStatus.DT_FAILURE | dtStatus.DT_WRONG_MAGIC);
        if (header.version != DetourNavMesh.DT_NAVMESH_VERSION)
            return new dtStatus(dtStatus.DT_FAILURE | dtStatus.DT_WRONG_VERSION);

        // Make sure the location is free.
        if (getTileAt(header.x, header.y, header.layer) != null)
            return new dtStatus(dtStatus.DT_FAILURE);

        // Allocate a tile.
        dtMeshTile tile = null;
        if (lastRef == null) {
            if (m_nextFree != null) {
                tile = m_nextFree;
                m_nextFree = tile.next;
                tile.next = null;
            }
        } else {
            // Try to relocate the tile to specific index with same salt.
//            int tileIndex = (int) decodePolyIdTile((dtPoly) lastRef);
//            if (tileIndex >= m_maxTiles)
//                return new dtStatus(dtStatus.DT_FAILURE | dtStatus.DT_OUT_OF_MEMORY);
//            // Try to find the specific tile id from the free list.
//            dtMeshTile target = m_tiles[tileIndex];
//            dtMeshTile prev = null;
//            tile = m_nextFree;
//            while (tile != null && tile != target) {
//                prev = tile;
//                tile = tile.next;
//            }
//            // Could not find the correct location.
//            if (tile != target)
//                return new dtStatus(dtStatus.DT_FAILURE | dtStatus.DT_OUT_OF_MEMORY);
//            // Remove from freelist
//            if (prev == null)
//                m_nextFree = tile.next;
//            else
//                prev.next = tile.next;

            // Restore salt.
//            tile.salt = decodePolyIdSalt((dtPoly) lastRef);
        }

        // Make sure we could allocate a tile.
        if (tile == null)
            return new dtStatus(dtStatus.DT_FAILURE | dtStatus.DT_OUT_OF_MEMORY);

        // Insert tile into the position lut.
        int h = computeTileHash(header.x, header.y, m_tileLutMask);
        tile.next = m_posLookup[h];
        m_posLookup[h] = tile;

        // Patch header pointers.

//		unsigned char* d = data + headerSize;
        tile.verts = new float[3 * header.vertCount];
        tile.polys = new dtPoly[header.polyCount];
        tile.links = new dtLink[header.maxLinkCount];
        for (int i = 0; i < tile.links.length; i++) {
            tile.links[i] = new dtLink();
        }
        tile.detailMeshes = new dtPolyDetail[header.detailMeshCount];
        tile.detailVerts = new float[3 * header.detailVertCount];
        tile.detailTris = new char[4 * header.detailTriCount];
        tile.bvTree = new dtBVNode[header.bvNodeCount];
        for (int i = 0; i < tile.bvTree.length; i++) {
            tile.bvTree[i] = new dtBVNode();
        }
        tile.offMeshCons = new dtOffMeshConnection[header.offMeshConCount];

        // If there are no items in the bvtree, reset the tree pointer.
//		if (!bvtreeSize)
//			tile.bvTree = 0;

        // Build links freelist
        tile.linksFreeList = 0;
        tile.links[header.maxLinkCount - 1].next = DetourNavMesh.DT_NULL_LINK;
        for (int i = 0; i < header.maxLinkCount - 1; ++i)
            tile.links[i].next = i + 1;

        // Init tile.
        tile.header = header;
//		tile.data = data;
//		tile.dataSize = dataSize;
        tile.flags = flags;

        connectIntLinks(tile);
        baseOffMeshLinks(tile);

        // Create connections with neighbour tiles.
        int MAX_NEIS = 32;
        dtMeshTile[] neis = new dtMeshTile[MAX_NEIS];
        int nneis;

        // Connect with layers in current tile.
        nneis = getTilesAt(header.x, header.y, neis, MAX_NEIS);
        for (int j = 0; j < nneis; ++j) {
            if (neis[j] != tile) {
                connectExtLinks(tile, neis[j], -1);
                connectExtLinks(neis[j], tile, -1);
            }
            connectExtOffMeshLinks(tile, neis[j], -1);
            connectExtOffMeshLinks(neis[j], tile, -1);
        }

        // Connect with neighbour tiles.
        for (int i = 0; i < 8; ++i) {
            nneis = getNeighbourTilesAt(header.x, header.y, i, neis, MAX_NEIS);
            for (int j = 0; j < nneis; ++j) {
                connectExtLinks(tile, neis[j], i);
                connectExtLinks(neis[j], tile, DetourCommon.dtOppositeTile(i));
                connectExtOffMeshLinks(tile, neis[j], i);
                connectExtOffMeshLinks(neis[j], tile, DetourCommon.dtOppositeTile(i));
            }
        }

        if (result != null) {
            result[0] = getTileRef(tile);
        }

        return new dtStatus(dtStatus.DT_SUCCESS);
    }

    public dtMeshTile getTileAt(int x, int y, int layer) {
        // Find tile based on hash.
        int h = computeTileHash(x, y, m_tileLutMask);
        dtMeshTile tile = m_posLookup[h];
        while (tile != null) {
            if (tile.header != null &&
                    tile.header.x == x &&
                    tile.header.y == y &&
                    tile.header.layer == layer) {
                return tile;
            }
            tile = tile.next;
        }
        return null;
    }

    public int getNeighbourTilesAt(int x, int y, int side, dtMeshTile[] tiles, int maxTiles) {
        int nx = x, ny = y;
        switch (side) {
            case 0:
                nx++;
                break;
            case 1:
                nx++;
                ny++;
                break;
            case 2:
                ny++;
                break;
            case 3:
                nx--;
                ny++;
                break;
            case 4:
                nx--;
                break;
            case 5:
                nx--;
                ny--;
                break;
            case 6:
                ny--;
                break;
            case 7:
                nx++;
                ny--;
                break;
        }

        return getTilesAt(nx, ny, tiles, maxTiles);
    }

    public int getTilesAt(int x, int y, dtMeshTile[] tiles, int maxTiles) {
        int n = 0;

        // Find tile based on hash.
        int h = computeTileHash(x, y, m_tileLutMask);
        dtMeshTile tile = m_posLookup[h];
        while (tile != null) {
            if (tile.header != null &&
                    tile.header.x == x &&
                    tile.header.y == y) {
                if (n < maxTiles)
                    tiles[n++] = tile;
            }
            tile = tile.next;
        }

        return n;
    }

    /// @par
    ///
    /// This function will not fail if the tiles array is too small to hold the
    /// entire result set.  It will simply fill the array to capacity.
//    public int getTilesAt(int x, int y, dtMeshTile[] tiles, int maxTiles) {
//        int n = 0;
//
//        // Find tile based on hash.
//        int h = computeTileHash(x, y, m_tileLutMask);
//        dtMeshTile tile = m_posLookup[h];
//        while (tile) {
//            if (tile.header &&
//                    tile.header.x == x &&
//                    tile.header.y == y) {
//                if (n < maxTiles)
//                    tiles[n++] = tile;
//            }
//            tile = tile.next;
//        }
//
//        return n;
//    }


    public dtMeshTile getTileRefAt(int x, int y, int layer) {
        // Find tile based on hash.
        int h = computeTileHash(x, y, m_tileLutMask);
        dtMeshTile tile = m_posLookup[h];
        while (tile != null) {
            if (tile.header != null &&
                    tile.header.x == x &&
                    tile.header.y == y &&
                    tile.header.layer == layer) {
                return getTileRef(tile);
            }
            tile = tile.next;
        }
        return null;
    }

//    public dtMeshTile getTileByRef(dtMeshTile ref) {
//        if (ref == null)
//            return null;
//        int tileIndex = decodePolyIdTile((dtPoly) ref);
//        int tileSalt = decodePolyIdSalt((dtPoly) ref);
//        if ((int) tileIndex >= m_maxTiles)
//            return null;
//        dtMeshTile tile = m_tiles[tileIndex];
//        if (tile.salt != tileSalt)
//            return null;
//        return tile;
//    }

    public int getMaxTiles() {
        return m_maxTiles;
    }

    public dtMeshTile getTile(int i) {
        return m_tiles[i];
    }

/*    dtMeshTile*

    getTile(int i) {
        return&m_tiles[i];
    }*/

    public void calcTileLoc(float[] pos, int[] tx, int[] ty) {
        tx[0] = (int) Math.floor((pos[0] - m_orig[0]) / m_tileWidth);
        ty[0] = (int) Math.floor((pos[2] - m_orig[2]) / m_tileHeight);
    }

    public dtStatus getTileAndPolyByRef(dtPoly ref, dtMeshTile[] tile, dtPoly[] poly) {
        if (ref == null) return new dtStatus(dtStatus.DT_FAILURE);
        int salt, it, ip;
        decodePolyId(ref, salt, it, ip);
        if (it >= (int)m_maxTiles)return new dtStatus(dtStatus.DT_FAILURE | dtStatus.DT_INVALID_PARAM);
        if (m_tiles[it].salt != salt || m_tiles[it].header == null) return new dtStatus(dtStatus.DT_FAILURE | dtStatus.DT_INVALID_PARAM);
        if (ip >= (int)m_tiles[it].header.polyCount)return new dtStatus(dtStatus.DT_FAILURE | dtStatus.DT_INVALID_PARAM);
        tile[0] = m_tiles[it];
        poly[0] = m_tiles[it].polys[ip];
        return new dtStatus(dtStatus.DT_SUCCESS);
    }

    /// @par
    ///
    /// @warning Only use this function if it is known that the provided polygon
    /// reference is valid. This function is faster than #getTileAndPolyByRef, but
    /// it does not validate the reference.
    public void getTileAndPolyByRefUnsafe(dtPoly ref, dtMeshTile[] tile, dtPoly[] poly) {
        int salt, it, ip;
        decodePolyId(ref, salt, it, ip);
        tile[0] = m_tiles[it];
        poly[0] = m_tiles[it].polys[ip];
    }

    public boolean isValidPolyRef(dtPoly ref) {
        if (ref == null) return false;
        int salt, it, ip;
        decodePolyId(ref, salt, it, ip);
        if (it >= (int)m_maxTiles)return false;
        if (m_tiles[it].salt != salt || m_tiles[it].header == 0) return false;
        if (ip >= (int)m_tiles[it].header.polyCount)return false;
        return true;
    }

    /// @par
    ///
    /// This function returns the data for the tile so that, if desired,
    /// it can be added back to the navigation mesh at a later point.
    ///
    /// @see #addTile
//    public dtStatus removeTile(dtMeshTile ref, dtMeshHeader header) {
//        if (!ref)
//            return DT_FAILURE | DT_INVALID_PARAM;
//        unsigned int tileIndex = decodePolyIdTile((dtPoly) ref);
//        unsigned int tileSalt = decodePolyIdSalt((dtPoly) ref);
//        if ((int) tileIndex >= m_maxTiles)
//            return DT_FAILURE | DT_INVALID_PARAM;
//        dtMeshTile tile = m_tiles[tileIndex];
//        if (tile.salt != tileSalt)
//            return DT_FAILURE | DT_INVALID_PARAM;
//
//        // Remove tile from hash lookup.
//        int h = computeTileHash(tile.header.x, tile.header.y, m_tileLutMask);
//        dtMeshTile prev = null;
//        dtMeshTile cur = m_posLookup[h];
//        while (cur != null) {
//            if (cur == tile) {
//                if (prev != null)
//                    prev.next = cur.next;
//                else
//                    m_posLookup[h] = cur.next;
//                break;
//            }
//            prev = cur;
//            cur = cur.next;
//        }
//
//        // Remove connections to neighbour tiles.
//        // Create connections with neighbour tiles.
//        int MAX_NEIS = 32;
//        dtMeshTile[] neis = new dtMeshTile[MAX_NEIS];
//        int nneis;
//
//        // Connect with layers in current tile.
//        nneis = getTilesAt(tile.header.x, tile.header.y, neis, MAX_NEIS);
//        for (int j = 0; j < nneis; ++j) {
//            if (neis[j] == tile) continue;
//            unconnectExtLinks(neis[j], tile);
//        }
//
//        // Connect with neighbour tiles.
//        for (int i = 0; i < 8; ++i) {
//            nneis = getNeighbourTilesAt(tile.header.x, tile.header.y, i, neis, MAX_NEIS);
//            for (int j = 0; j < nneis; ++j)
//                unconnectExtLinks(neis[j], tile);
//        }
//
//        // Reset tile.
//        if (tile.flags & DT_TILE_FREE_DATA) {
//            // Owns data
////			dtFree(tile.data);
//            tile.header = null;
////			tile.data = 0;
////			tile.dataSize = 0;
////			if (data) *data = 0;
////			if (dataSize) *dataSize = 0;
//        } else {
//            tile.header = header;
////			if (data) *data = tile.data;
////			if (dataSize) *dataSize = tile.dataSize;
//        }
//
////		tile.header = null;
//        tile.flags = 0;
//        tile.linksFreeList = 0;
//        tile.polys = 0;
//        tile.verts = 0;
//        tile.links = 0;
//        tile.detailMeshes = 0;
//        tile.detailVerts = 0;
//        tile.detailTris = 0;
//        tile.bvTree = 0;
//        tile.offMeshCons = 0;
//
//        // Update salt, salt should never be zero.
//        tile.salt = (tile.salt + 1) & ((1 << m_saltBits) - 1);
//        if (tile.salt == 0)
//            tile.salt++;
//
//        // Add to free list.
//        tile.next = m_nextFree;
//        m_nextFree = tile;
//
//        return dtStatus.DT_SUCCESS;
//    }

    public dtMeshTile getTileRef(dtMeshTile tile) {
        return tile;
//        if (tile == null) return null;
//        int it = (int) (tile - m_tiles);
//        return (dtMeshTile) encodePolyId(tile.salt, it, 0);
    }

    /// @par
    ///
    /// Example use case:
    /// @code
    ///
    ///  dtPoly base = navmesh.getPolyRefBase(tile);
    /// for (int i = 0; i < tile.header.polyCount; ++i)
    /// {
    ///      dtPoly* p = &tile.polys[i];
    ///      dtPoly ref = base | (dtPoly)i;
    ///
    ///     // Use the reference to access the polygon data.
    /// }
    /// @endcode
    public dtPoly getPolyRefBase(dtMeshTile tile) {
//        if (!tile) return 0;
//        unsigned int it = (unsigned int)(tile - m_tiles);
//        return encodePolyId(tile.salt, it, 0);
        return null;
    }

    public static class dtTileState {
        public int magic;                                // Magic number, used to identify the data.
        public int version;                            // Data version number.
        public dtMeshTile ref;                            // Tile ref at the time of storing the data.
    }

    public static class dtPolyState {
        public int flags;                        // Flags (see dtPolyFlags).
        public char area;                            // Area ID of the polygon.
    }

    ///  @see #storeTileState
//    public int getTileStateSize(dtMeshTile tile) {
//        if (!tile) return 0;
//        int headerSize = dtAlign4(sizeof(dtTileState));
//        int polyStateSize = dtAlign4(sizeof(dtPolyState) * tile.header.polyCount);
//        return headerSize + polyStateSize;
//    }

    /// @par
    ///
    /// Tile state includes non-structural data such as polygon flags, area ids, etc.
    /// @note The state data is only valid until the tile reference changes.
    /// @see #getTileStateSize, #restoreTileState
//    public dtStatus storeTileState(dtMeshTile tile, char[] data, int maxDataSize) {
//        // Make sure there is enough space to store the state.
//        int sizeReq = getTileStateSize(tile);
//        if (maxDataSize < sizeReq)
//            return DT_FAILURE | DT_BUFFER_TOO_SMALL;
//
//        dtTileState * tileState = (dtTileState *) data;
//        data += dtAlign4(sizeof(dtTileState));
//        dtPolyState * polyStates = (dtPolyState *) data;
//        data += dtAlign4(sizeof(dtPolyState) * tile.header.polyCount);
//
//        // Store tile state.
//        tileState.magic = DT_NAVMESH_STATE_MAGIC;
//        tileState.version = DT_NAVMESH_STATE_VERSION;
//        tileState.ref = getTileRef(tile);
//
//        // Store per poly state.
//        for (int i = 0; i < tile.header.polyCount; ++i) {
//            dtPoly * p =&tile.polys[i];
//            dtPolyState * s =&polyStates[i];
//            s.flags = p.flags;
//            s.area = p.getArea();
//        }
//
//        return DT_SUCCESS;
//    }

    /// @par
    ///
    /// Tile state includes non-structural data such as polygon flags, area ids, etc.
    /// @note This function does not impact the tile's #dtTileRef and #dtPoly's.
    /// @see #storeTileState
//    public dtStatus restoreTileState(dtMeshTile tile, char[] data, int maxDataSize) {
//        // Make sure there is enough space to store the state.
//        int sizeReq = getTileStateSize(tile);
//        if (maxDataSize < sizeReq)
//            return DT_FAILURE | DT_INVALID_PARAM;
//
//        dtTileState * tileState = (dtTileState *) data;
//        data += dtAlign4(sizeof(dtTileState));
//        dtPolyState * polyStates = (dtPolyState *) data;
//        data += dtAlign4(sizeof(dtPolyState) * tile.header.polyCount);
//
//        // Check that the restore is possible.
//        if (tileState.magic != DT_NAVMESH_STATE_MAGIC)
//            return DT_FAILURE | DT_WRONG_MAGIC;
//        if (tileState.version != DT_NAVMESH_STATE_VERSION)
//            return DT_FAILURE | DT_WRONG_VERSION;
//        if (tileState.ref != getTileRef(tile))
//            return DT_FAILURE | DT_INVALID_PARAM;
//
//        // Restore per poly state.
//        for (int i = 0; i < tile.header.polyCount; ++i) {
//            dtPoly * p =&tile.polys[i];
//            dtPolyState * s =&polyStates[i];
//            p.flags = s.flags;
//            p.setArea(s.area);
//        }
//
//        return DT_SUCCESS;
//    }

    /// @par
    ///
    /// Off-mesh connections are stored in the navigation mesh as special 2-vertex
    /// polygons with a single edge. At least one of the vertices is expected to be
    /// inside a normal polygon. So an off-mesh connection is "entered" from a
    /// normal polygon at one of its endpoints. This is the polygon identified by
    /// the prevRef parameter.
    public dtStatus getOffMeshConnectionPolyEndPoints(dtPoly prevRef, dtPoly polyRef, float[] startPos, float[] endPos) {
        int salt, it, ip;

        if (polyRef == null)
            return new dtStatus(dtStatus.DT_FAILURE);

        // Get current polygon
        decodePolyId(polyRef, salt, it, ip);
        if (it >= (int)m_maxTiles)return new dtStatus(dtStatus.DT_FAILURE | dtStatus.DT_INVALID_PARAM);
        if (m_tiles[it].salt != salt || m_tiles[it].header == null) return new dtStatus(dtStatus.DT_FAILURE | dtStatus.DT_INVALID_PARAM);
        dtMeshTile tile = m_tiles[it];
        if (ip >= (int)tile.header.polyCount)return new dtStatus(dtStatus.DT_FAILURE | dtStatus.DT_INVALID_PARAM);
        dtPoly  poly = tile.polys[ip];

        // Make sure that the current poly is indeed off-mesh link.
        if (poly.getType() != dtPolyTypes.DT_POLYTYPE_OFFMESH_CONNECTION)
            return new dtStatus(dtStatus.DT_FAILURE);

        // Figure out which way to hand out the vertices.
        int idx0 = 0, idx1 = 1;

        // Find link that points to first vertex.
        for (int i = poly.firstLink;
        i != DetourNavMesh.DT_NULL_LINK;
        i = tile.links[i].next)
        {
            if (tile.links[i].edge == 0) {
                if (tile.links[i].ref != prevRef) {
                    idx0 = 1;
                    idx1 = 0;
                }
                break;
            }
        }

        DetourCommon.dtVcopy(startPos, 0, tile.verts, poly.verts[idx0] * 3);
        DetourCommon.dtVcopy(endPos, 0, tile.verts, poly.verts[idx1] * 3);

        return new dtStatus(dtStatus.DT_SUCCESS);
    }


//    public dtOffMeshConnection getOffMeshConnectionByRef(dtPoly ref) {
//        unsigned int salt, it, ip;
//
//        if (!ref)
//            return 0;
//
//        // Get current polygon
//        decodePolyId(ref, salt, it, ip);
//        if (it >= (unsigned int)m_maxTiles)return 0;
//        if (m_tiles[it].salt != salt || m_tiles[it].header == 0) return 0;
//        dtMeshTile * tile =&m_tiles[it];
//        if (ip >= (unsigned int)tile.header.polyCount)return 0;
//        dtPoly * poly =&tile.polys[ip];
//
//        // Make sure that the current poly is indeed off-mesh link.
//        if (poly.getType() != DT_POLYTYPE_OFFMESH_CONNECTION)
//            return 0;
//
//        unsigned int idx = ip - tile.header.offMeshBase;
//        dtAssert(idx < (unsignedint)tile.header.offMeshConCount);
//        return&tile.offMeshCons[idx];
//    }


//    public dtStatus setPolyFlags(dtPoly ref, int flags) {
//        if (!ref) return DT_FAILURE;
//        unsigned int salt, it, ip;
//        decodePolyId(ref, salt, it, ip);
//        if (it >= (unsigned int)m_maxTiles)return DT_FAILURE | DT_INVALID_PARAM;
//        if (m_tiles[it].salt != salt || m_tiles[it].header == 0) return DT_FAILURE | DT_INVALID_PARAM;
//        dtMeshTile * tile =&m_tiles[it];
//        if (ip >= (unsigned int)tile.header.polyCount)return DT_FAILURE | DT_INVALID_PARAM;
//        dtPoly * poly =&tile.polys[ip];
//
//        // Change flags.
//        poly.flags = flags;
//
//        return DT_SUCCESS;
//    }

//    public dtStatus getPolyFlags(dtPoly ref, int[] resultFlags) {
//        if (!ref) return DT_FAILURE;
//        unsigned int salt, it, ip;
//        decodePolyId(ref, salt, it, ip);
//        if (it >= (unsigned int)m_maxTiles)return DT_FAILURE | DT_INVALID_PARAM;
//        if (m_tiles[it].salt != salt || m_tiles[it].header == 0) return DT_FAILURE | DT_INVALID_PARAM;
//        dtMeshTile * tile =&m_tiles[it];
//        if (ip >= (unsigned int)tile.header.polyCount)return DT_FAILURE | DT_INVALID_PARAM;
//        dtPoly * poly =&tile.polys[ip];
//
//        *resultFlags = poly.flags;
//
//        return DT_SUCCESS;
//    }

//    public dtStatus setPolyArea(dtPoly ref, char area) {
//        if (!ref) return DT_FAILURE;
//        unsigned int salt, it, ip;
//        decodePolyId(ref, salt, it, ip);
//        if (it >= (unsigned int)m_maxTiles)return DT_FAILURE | DT_INVALID_PARAM;
//        if (m_tiles[it].salt != salt || m_tiles[it].header == 0) return DT_FAILURE | DT_INVALID_PARAM;
//        dtMeshTile * tile =&m_tiles[it];
//        if (ip >= (unsigned int)tile.header.polyCount)return DT_FAILURE | DT_INVALID_PARAM;
//        dtPoly * poly =&tile.polys[ip];
//
//        poly.setArea(area);
//
//        return DT_SUCCESS;
//    }

//    public dtStatus getPolyArea(dtPoly ref, char[] resultArea) {
//        if (ref == null) return new dtStatus(dtStatus.DT_FAILURE);
//        int salt, it, ip;
//        decodePolyId(ref, salt, it, ip);
//        if (it >= (unsigned int)m_maxTiles)return DT_FAILURE | DT_INVALID_PARAM;
//        if (m_tiles[it].salt != salt || m_tiles[it].header == 0) return DT_FAILURE | DT_INVALID_PARAM;
//        dtMeshTile tile = m_tiles[it];
//        if (ip >= (unsigned int)tile.header.polyCount)return DT_FAILURE | DT_INVALID_PARAM;
//        dtPoly poly = tile.polys[ip];
//
//        *resultArea = poly.getArea();
//
//        return DT_SUCCESS;
//    }


}
