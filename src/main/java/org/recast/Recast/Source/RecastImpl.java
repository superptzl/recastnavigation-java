package org.recast.Recast.Source;

import org.recast.Recast.Include.*;
import org.recast.RecastDemo.Include.InputGeom;

public class RecastImpl extends Recast {
    public void rcCalcGridSize(float[] bmin, float[] bmax, float cs, int[] w, int[] h) {
        w[0] = (int) ((bmax[0] - bmin[0]) / cs + 0.5f);
        h[0] = (int) ((bmax[2] - bmin[2]) / cs + 0.5f);
    }

    public boolean rcCreateHeightfield(rcContext ctx, rcHeightfield hf, int width, int height,
                                       float[] bmin, float[] bmax,
                                       float cs, float ch) {
        // TODO: VC complains about unref formal variable, figure out a way to handle this better.
//	rcAssert(ctx);

        hf.width = width;
        hf.height = height;
        InputGeom.rcVcopy(hf.bmin, bmin);
        InputGeom.rcVcopy(hf.bmax, bmax);
        hf.cs = cs;
        hf.ch = ch;
        hf.spans = new rcSpan[hf.width * hf.height];//(rcSpan**)rcAlloc(sizeof(rcSpan*)*hf.width*hf.height, RC_ALLOC_PERM);
//        if (!hf.spans)
//            return false;
//        memset(hf.spans, 0, sizeof(rcSpan*)*hf.width*hf.height);
        return true;
    }

    public void rcMarkWalkableTriangles(rcContext ctx, float walkableSlopeAngle,
                                        float[] verts, int nv,
                                        int[] tris, int nt,
                                        char[] areas) {
        // TODO: VC complains about unref formal variable, figure out a way to handle this better.
//	rcAssert(ctx);

        double walkableThr = Math.cos(walkableSlopeAngle / 180.0f * Math.PI);

        float norm[] = new float[3];

        for (int i = 0; i < nt; ++i) {
//            int[] tri = tris[i*3];
            calcTriNormal(create3(verts, tris[i * 3 + 0] * 3), create3(verts, tris[i * 3 + 1] * 3), create3(verts, tris[i * 3 + 2] * 3), norm);
            // Check if the face is walkable.
            if (norm[1] > walkableThr)
                areas[i] = RC_WALKABLE_AREA;
        }
    }

    public boolean rcBuildContours(rcContext ctx, rcCompactHeightfield chf,
                                   float maxError, int maxEdgeLen,
                                   rcContourSet cset, int buildFlags) {
        return new RecastContour().rcBuildContours(ctx, chf, maxError, maxEdgeLen, cset, buildFlags);
    }

    public static void calcTriNormal(float[] v0, float[] v1, float[] v2, float[] norm) {
        float e0[] = new float[3], e1[] = new float[3];
        rcVsub(e0, v1, v0);
        rcVsub(e1, v2, v0);
        rcVcross(norm, e0, e1);
        rcVnormalize(norm);
    }

    public boolean rcBuildPolyMesh(rcContext ctx, rcContourSet cset, int nvp, rcPolyMesh mesh) {
        return new RecastMeh().rcBuildPolyMesh(ctx, cset, nvp, mesh);
    }

    public void rcRasterizeTriangles(rcContext ctx, float[] verts, int nv,
                                     int[] tris, char[] areas, int nt,
                                     rcHeightfield solid, int flagMergeThr) {
        new RecastRasterization().rcRasterizeTriangles(ctx, verts, nv, tris, areas, nt, solid, flagMergeThr);
    }

    public void rcRasterizeTriangles(rcContext ctx, float[] verts, int nv,
                                     short[] tris, char[] areas, int nt,
                                     rcHeightfield solid, int flagMergeThr) {
        new RecastRasterization().rcRasterizeTriangles(ctx, verts, nv, tris, areas, nt, solid, flagMergeThr);
    }

    public void rcRasterizeTriangles(rcContext ctx, float[] verts, char[] areas, int nt,
                                     rcHeightfield solid, int flagMergeThr) {
        new RecastRasterization().rcRasterizeTriangles(ctx, verts, areas, nt, solid, flagMergeThr);
    }


    /// @par
///
/// This is just the beginning of the process of fully building a compact heightfield.
/// Various filters may be applied applied, then the distance field and regions built.
/// E.g: #rcBuildDistanceField and #rcBuildRegions
///
/// See the #rcConfig documentation for more information on the configuration parameters.
///
/// @see rcAllocCompactHeightfield, rcHeightfield, rcCompactHeightfield, rcConfig
    public boolean rcBuildCompactHeightfield(rcContext ctx, int walkableHeight, int walkableClimb,
                                             rcHeightfield hf, rcCompactHeightfield chf) {
//        rcAssert(ctx);

        ctx.startTimer(rcTimerLabel.RC_TIMER_BUILD_COMPACTHEIGHTFIELD);

        int w = hf.width;
        int h = hf.height;
        int spanCount = rcGetHeightFieldSpanCount(ctx, hf);

        // Fill in header.
        chf.width = w;
        chf.height = h;
        chf.spanCount = spanCount;
        chf.walkableHeight = walkableHeight;
        chf.walkableClimb = walkableClimb;
        chf.maxRegions = 0;
        rcVcopy(chf.bmin, hf.bmin);
        rcVcopy(chf.bmax, hf.bmax);
        chf.bmax[1] += walkableHeight * hf.ch;
        chf.cs = hf.cs;
        chf.ch = hf.ch;
        chf.cells = new rcCompactCell[w * h];//(rcCompactCell*)rcAlloc(sizeof(rcCompactCell)*w*h, RC_ALLOC_PERM);
        for (int i = 0; i < chf.cells.length; i++) {
            chf.cells[i] = new rcCompactCell();
        }
        /*if (!chf.cells)
        {
            ctx.log(RC_LOG_ERROR, "rcBuildCompactHeightfield: Out of memory 'chf.cells' (%d)", w*h);
            return false;
        }
        memset(chf.cells, 0, sizeof(rcCompactCell)*w*h);*/
        chf.spans = new rcCompactSpan[spanCount];//(rcCompactSpan*)rcAlloc(sizeof(rcCompactSpan)*spanCount, RC_ALLOC_PERM);
        for (int i = 0; i < chf.spans.length; i++) {
            chf.spans[i] = new rcCompactSpan();
        }
        /*if (!chf.spans)
        {
            ctx.log(RC_LOG_ERROR, "rcBuildCompactHeightfield: Out of memory 'chf.spans' (%d)", spanCount);
            return false;
        }
        memset(chf.spans, 0, sizeof(rcCompactSpan)*spanCount);*/
        chf.areas = new char[spanCount];//(unsigned char*)rcAlloc(sizeof(unsigned char)*spanCount, RC_ALLOC_PERM);
        /*if (!chf.areas)
        {
            ctx.log(RC_LOG_ERROR, "rcBuildCompactHeightfield: Out of memory 'chf.areas' (%d)", spanCount);
            return false;
        }
        memset(chf.areas, RC_NULL_AREA, sizeof(unsigned char)*spanCount);*/

        int MAX_HEIGHT = 0xffff;

        // Fill in cells and spans.
        int idx = 0;
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                rcSpan s = hf.spans[x + y * w];
                // If there are no spans at this cell, just leave the data to index=0, count=0.
                if (s == null) continue;
                rcCompactCell c = chf.cells[x + y * w];
                c.setIndex(idx);
                c.setCount(0);
                while (s != null) {
                    if (s.getArea() != RC_NULL_AREA) {
                        int bot = (int) s.getSmax();
                        int top = s.next != null ? (int) s.next.getSmin() : MAX_HEIGHT;
                        chf.spans[idx].y = (short) rcClamp(bot, 0, 0xffff).shortValue();
                        chf.spans[idx].setH((char) rcClamp(top - bot, 0, 0xff).shortValue());
                        chf.areas[idx] = (char) s.getArea();//area;
                        idx++;
                        c.setCount(c.getCount()+1);//count++;
                    }
                    s = s.next;
                }
            }
        }
		//debug
//		for (rcCompactCell c : chf.cells) {
//			if (c.getIndex() != 0 || c.getCount() != 0) {
//				System.out.println(c);
//			}
//		}
		//-----

        // Find neighbour connections.
        int MAX_LAYERS = RC_NOT_CONNECTED - 1;
        int tooHighNeighbour = 0;
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                rcCompactCell c = chf.cells[x + y * w];
                for (int i = (int) c.getIndex(), ni = (int) (c.getIndex() + c.getCount()); i < ni; ++i) {
                    rcCompactSpan s = chf.spans[i];

                    for (int dir = 0; dir < 4; ++dir) {
                        rcSetCon(s, dir, RC_NOT_CONNECTED);
                        int nx = x + rcGetDirOffsetX(dir);
                        int ny = y + rcGetDirOffsetY(dir);
                        // First check that the neighbour cell is in bounds.
                        if (nx < 0 || ny < 0 || nx >= w || ny >= h)
                            continue;

                        // Iterate over all neighbour spans and check if any of the is
                        // accessible from current cell.
                        rcCompactCell nc = chf.cells[nx + ny * w];
                        for (int k = (int) nc.getIndex(), nk = (int) (nc.getIndex() + nc.getCount()); k < nk; ++k) {
                            rcCompactSpan ns = chf.spans[k];
                            int bot = rcMax(s.y, ns.y);
                            int top = rcMin(s.y + s.getH(), ns.y + ns.getH());

                            // Check that the gap between the spans is walkable,
                            // and that the climb height between the gaps is not too high.
                            if ((top - bot) >= walkableHeight && rcAbs((int) ns.y - (int) s.y) <= walkableClimb) {
                                // Mark direction as walkable.
                                int lidx = k - (int) nc.getIndex();
                                if (lidx < 0 || lidx > MAX_LAYERS) {
                                    tooHighNeighbour = rcMax(tooHighNeighbour, lidx);
                                    continue;
                                }
                                rcSetCon(s, dir, lidx);
                                break;
                            }
                        }

                    }
                }
            }
        }

        if (tooHighNeighbour > MAX_LAYERS) {
            ctx.log(rcLogCategory.RC_LOG_ERROR, "rcBuildCompactHeightfield: Heightfield has too many layers %d (max: %d)",
                    tooHighNeighbour, MAX_LAYERS);
        }

        ctx.stopTimer(rcTimerLabel.RC_TIMER_BUILD_COMPACTHEIGHTFIELD);

        return true;
    }


    public void rcFilterLowHangingWalkableObstacles(rcContext ctx, int walkableClimb, rcHeightfield solid) {
        new RecastFilter().rcFilterLowHangingWalkableObstacles(ctx, walkableClimb, solid);
    }

    public void rcFilterLedgeSpans(rcContext ctx, int walkableHeight, int walkableClimb, rcHeightfield solid) {
        new RecastFilter().rcFilterLedgeSpans(ctx, walkableHeight, walkableClimb, solid);
    }

    public void rcFilterWalkableLowHeightSpans(rcContext ctx, int walkableHeight, rcHeightfield solid) {
        new RecastFilter().rcFilterWalkableLowHeightSpans(ctx, walkableHeight, solid);
    }

    public int rcGetHeightFieldSpanCount(rcContext ctx, rcHeightfield hf) {
        // TODO: VC complains about unref formal variable, figure out a way to handle this better.
//	rcAssert(ctx);

        int w = hf.width;
        int h = hf.height;
        int spanCount = 0;
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                for (rcSpan s = hf.spans[x + y * w]; s != null; s = s.next) {
                    if (s.getArea() != RC_NULL_AREA)
                        spanCount++;
                }
            }
        }
        return spanCount;
    }

    public boolean rcErodeWalkableArea(rcContext ctx, int radius, rcCompactHeightfield chf) {
        return new RecastArea().rcErodeWalkableArea(ctx, radius, chf);
    }

    public void rcMarkConvexPolyArea(rcContext ctx, float[] verts, int nverts,
                                     float hmin, float hmax, char areaId,
                                     rcCompactHeightfield chf) {
        new RecastArea().rcMarkConvexPolyArea(ctx, verts, nverts, hmin, hmax, areaId, chf);
    }

    public boolean rcBuildRegionsMonotone(rcContext ctx, rcCompactHeightfield chf,
                                          int borderSize, int minRegionArea, int mergeRegionArea) {
        return new RecastRegion().rcBuildRegionsMonotone(ctx, chf, borderSize, minRegionArea, mergeRegionArea);
    }

    public boolean rcBuildDistanceField(rcContext ctx, rcCompactHeightfield chf) {
        return new RecastRegion().rcBuildDistanceField(ctx, chf);
    }

    public boolean rcBuildRegions(rcContext ctx, rcCompactHeightfield chf,
                                  int borderSize, int minRegionArea, int mergeRegionArea) {
        return new RecastRegion().rcBuildRegions(ctx, chf, borderSize, minRegionArea, mergeRegionArea);
    }

    public boolean rcBuildPolyMeshDetail(rcContext ctx, rcPolyMesh mesh, rcCompactHeightfield chf,
                                                  float sampleDist, float sampleMaxError,
                                                  rcPolyMeshDetail dmesh) {
        return new RecastMeshDetail().rcBuildPolyMeshDetail(ctx, mesh, chf, sampleDist, sampleMaxError, dmesh);
    }
}