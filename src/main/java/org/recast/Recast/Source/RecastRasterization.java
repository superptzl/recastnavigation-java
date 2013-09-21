package org.recast.Recast.Source;

import org.recast.Recast.Include.*;

public class RecastRasterization extends RecastImpl {

/// @par
///
/// Spans will only be added for triangles that overlap the heightfield grid.
///
/// @see rcHeightfield

    public void rcRasterizeTriangles(rcContext ctx, float[] verts, int nv,
                                     int[] tris, char[] areas, int nt,
                                     rcHeightfield solid, int flagMergeThr) {
//            rcAssert(ctx);

        ctx.startTimer(rcTimerLabel.RC_TIMER_RASTERIZE_TRIANGLES);

        float ics = 1.0f / solid.cs;
        float ich = 1.0f / solid.ch;
        // Rasterize triangles.
        for (int i = 0; i < nt; ++i) {
            float[] v0 = create3(verts, tris[i * 3 + 0] * 3);
            float[] v1 = create3(verts, tris[i * 3 + 1] * 3);
            float[] v2 = create3(verts, tris[i * 3 + 2] * 3);
            // Rasterize.
            rasterizeTri(v0, v1, v2, areas[i], solid, solid.bmin, solid.bmax, solid.cs, ics, ich, flagMergeThr);
            System.arraycopy(v0, 0, verts, tris[i * 3 + 0] * 3, 3);
            System.arraycopy(v1, 0, verts, tris[i * 3 + 1] * 3, 3);
            System.arraycopy(v2, 0, verts, tris[i * 3 + 2] * 3, 3);
        }

        ctx.stopTimer(rcTimerLabel.RC_TIMER_RASTERIZE_TRIANGLES);
    }

    /// @par
///
/// Spans will only be added for triangles that overlap the heightfield grid.
///
/// @see rcHeightfield
    public void rcRasterizeTriangles(rcContext ctx, float[] verts, int nv,
                                     short[] tris, char[] areas, int nt,
                                     rcHeightfield solid, int flagMergeThr) {
//            rcAssert(ctx);

        ctx.startTimer(rcTimerLabel.RC_TIMER_RASTERIZE_TRIANGLES);

        float ics = 1.0f / solid.cs;
        float ich = 1.0f / solid.ch;
        // Rasterize triangles.
        for (int i = 0; i < nt; ++i) {
            float[] v0 = create3(verts, tris[i * 3 + 0] * 3);
            float[] v1 = create3(verts, tris[i * 3 + 1] * 3);
            float[] v2 = create3(verts, tris[i * 3 + 2] * 3);
            // Rasterize.
            rasterizeTri(v0, v1, v2, areas[i], solid, solid.bmin, solid.bmax, solid.cs, ics, ich, flagMergeThr);

            System.arraycopy(v0, 0, verts, tris[i * 3 + 0] * 3, 3);
            System.arraycopy(v1, 0, verts, tris[i * 3 + 1] * 3, 3);
            System.arraycopy(v2, 0, verts, tris[i * 3 + 2] * 3, 3);
        }

        ctx.stopTimer(rcTimerLabel.RC_TIMER_RASTERIZE_TRIANGLES);
    }

    /// @par
///
/// Spans will only be added for triangles that overlap the heightfield grid.
///
/// @see rcHeightfield
    public void rcRasterizeTriangles(rcContext ctx, float[] verts, char[] areas, int nt,
                                     rcHeightfield solid, int flagMergeThr) {
//            rcAssert(ctx);

        ctx.startTimer(rcTimerLabel.RC_TIMER_RASTERIZE_TRIANGLES);

        float ics = 1.0f / solid.cs;
        float ich = 1.0f / solid.ch;
        // Rasterize triangles.
        for (int i = 0; i < nt; ++i) {
            float[] v0 = create3(verts, (i * 3 + 0) * 3);
            float[] v1 = create3(verts, (i * 3 + 1) * 3);
            float[] v2 = create3(verts, (i * 3 + 2) * 3);
            // Rasterize.
            rasterizeTri(v0, v1, v2, areas[i], solid, solid.bmin, solid.bmax, solid.cs, ics, ich, flagMergeThr);

            System.arraycopy(v0, 0, verts, (i * 3 + 0) * 3, 3);
            System.arraycopy(v1, 0, verts, (i * 3 + 1) * 3, 3);
            System.arraycopy(v2, 0, verts, (i * 3 + 2) * 3, 3);
        }

        ctx.stopTimer(rcTimerLabel.RC_TIMER_RASTERIZE_TRIANGLES);
    }

    public static void rasterizeTri(float[] v0, float[] v1, float[] v2,
                             char area, rcHeightfield hf,
                             float[] bmin, float[] bmax,
                             float cs, float ics, float ich,
                             int flagMergeThr)
    {
        int w = hf.width;
        int h = hf.height;
        float tmin[] = new float[3], tmax[] = new float[3];
        float by = bmax[1] - bmin[1];

        // Calculate the bounding box of the triangle.
        rcVcopy(tmin, v0);
        rcVcopy(tmax, v0);
        rcVmin(tmin, v1);
        rcVmin(tmin, v2);
        rcVmax(tmax, v1);
        rcVmax(tmax, v2);

        // If the triangle does not touch the bbox of the heightfield, skip the triagle.
        if (!overlapBounds(bmin, bmax, tmin, tmax))
            return;

        // Calculate the footpring of the triangle on the grid.
        int x0 = (int)((tmin[0] - bmin[0])*ics);
        int y0 = (int)((tmin[2] - bmin[2])*ics);
        int x1 = (int)((tmax[0] - bmin[0])*ics);
        int y1 = (int)((tmax[2] - bmin[2])*ics);
        x0 = rcClamp(x0, 0, w-1);
        y0 = rcClamp(y0, 0, h-1);
        x1 = rcClamp(x1, 0, w-1);
        y1 = rcClamp(y1, 0, h-1);

        // Clip the triangle into all grid cells it touches.
        float in[] = new float[7*3], out[] = new float[7*3], inrow[] = new float[7*3];

        for (int y = y0; y <= y1; ++y)
        {
            // Clip polygon to row.
//            rcVcopy(&in[0], v0);
            in[0] = v0[0];
            in[1] = v0[1];
            in[2] = v0[2];

//            rcVcopy(&in[1*3], v1);
            in[1*3 + 0] = v1[0];
            in[1*3 + 1] = v1[1];
            in[1*3 + 2] = v1[2];

//            rcVcopy(&in[2*3], v2);
            in[2*3 + 0] = v2[0];
            in[2*3 + 1] = v2[1];
            in[2*3 + 2] = v2[2];

            int nvrow = 3;
            float cz = bmin[2] + y*cs;
            nvrow = clipPoly(in, nvrow, out, 0, 1, -cz);
            if (nvrow < 3) continue;
            nvrow = clipPoly(out, nvrow, inrow, 0, -1, cz+cs);
            if (nvrow < 3) continue;

            for (int x = x0; x <= x1; ++x)
            {
                // Clip polygon to column.
                int nv = nvrow;
                float cx = bmin[0] + x*cs;
                nv = clipPoly(inrow, nv, out, 1, 0, -cx);
                if (nv < 3) continue;
                nv = clipPoly(out, nv, in, -1, 0, cx+cs);
                if (nv < 3) continue;

                // Calculate min and max of the span.
                float smin = in[1], smax = in[1];
                for (int i = 1; i < nv; ++i)
                {
                    smin = rcMin(smin, in[i*3+1]);
                    smax = rcMax(smax, in[i*3+1]);
                }
                smin -= bmin[1];
                smax -= bmin[1];
                // Skip the span if it is outside the heightfield bbox
                if (smax < 0.0f) continue;
                if (smin > by) continue;
                // Clamp the span to the heightfield bbox.
                if (smin < 0.0f) smin = 0;
                if (smax > by) smax = by;

                // Snap the span to the heightfield height grid.
                short ismin = (short)rcClamp((int)Math.floor(smin * ich), 0, RC_SPAN_MAX_HEIGHT).shortValue();
                short ismax = (short)rcClamp((int)Math.ceil(smax * ich), (int)ismin+1, RC_SPAN_MAX_HEIGHT).shortValue();

                addSpan(hf, x, y, ismin, ismax, area, flagMergeThr);
            }
        }
    }

    public static boolean overlapBounds(float[] amin, float[] amax, float[] bmin, float[] bmax)
    {
        boolean overlap = true;
        overlap = (amin[0] > bmax[0] || amax[0] < bmin[0]) ? false : overlap;
        overlap = (amin[1] > bmax[1] || amax[1] < bmin[1]) ? false : overlap;
        overlap = (amin[2] > bmax[2] || amax[2] < bmin[2]) ? false : overlap;
        return overlap;
    }

    /*public static boolean overlapInterval(unsigned short amin, unsigned short amax,
                                unsigned short bmin, unsigned short bmax)
    {
        if (amax < bmin) return false;
        if (amin > bmax) return false;
        return true;
    }
*/

    public static rcSpan allocSpan(rcHeightfield hf)
    {
        // If running out of memory, allocate new page and update the freelist.
        if (hf.freelist == null || hf.freelist.next == null)
        {
            // Create new page.
            // Allocate memory for the new pool.
            rcSpanPool pool = new rcSpanPool();//(rcSpanPool*)rcAlloc(sizeof(rcSpanPool), RC_ALLOC_PERM);
//            if (!pool) return 0;
//            pool.next = 0;
            // Add the pool into the list of pools.
            pool.next = hf.pools;
            hf.pools = pool;
            // Add new items to the free list.
            rcSpan freelist = hf.freelist;
            rcSpan head = pool.items[0];
            int indx = RC_SPANS_PER_POOL;
            rcSpan it;// = pool.items[indx];
            do
            {
                --indx;
                it = pool.items[indx];
//                --it;
//                it.
                it.next = freelist;
                freelist = it;
            }
            while (it != head);
            hf.freelist = it;
        }

        // Pop item from in front of the free list.
        rcSpan it = hf.freelist;
        hf.freelist = hf.freelist.next;
        return it;
    }

    public static void freeSpan(rcHeightfield hf, rcSpan ptr)
    {
        if (ptr == null) return;
        // Add the node in front of the free list.
        ptr.next = hf.freelist;
        hf.freelist = ptr;
    }

    public static void addSpan(rcHeightfield hf, int x, int y,
                        short smin, short smax,
                        char area, int flagMergeThr)
    {

        int idx = x + y*hf.width;

        rcSpan s = allocSpan(hf);
        s.smin = smin;
        s.smax = smax;
        s.area = area;
        s.next = null;

        // Empty cell, add he first span.
        if (hf.spans[idx] == null)
        {
            hf.spans[idx] = s;
            return;
        }
        rcSpan prev = null;
        rcSpan cur = hf.spans[idx];

        // Insert and merge spans.
        while (cur != null)
        {
            if (cur.smin > s.smax)
            {
                // Current span is further than the new span, break.
                break;
            }
            else if (cur.smax < s.smin)
            {
                // Current span is before the new span advance.
                prev = cur;
                cur = cur.next;
            }
            else
            {
                // Merge spans.
                if (cur.smin < s.smin)
                    s.smin = cur.smin;
                if (cur.smax > s.smax)
                    s.smax = cur.smax;

                // Merge flags.
                if (rcAbs((int)s.smax - (int)cur.smax) <= flagMergeThr)
                    s.area = rcMax(s.area, cur.area);

                // Remove current span.
                rcSpan next = cur.next;
                freeSpan(hf, cur);
                if (prev != null)
                    prev.next = next;
                else
                    hf.spans[idx] = next;
                cur = next;
            }
        }

        // Insert new span.
        if (prev != null)
        {
            s.next = prev.next;
            prev.next = s;
        }
        else
        {
            s.next = hf.spans[idx];
            hf.spans[idx] = s;
        }
    }

    /// @par
///
/// The span addition can be set to favor flags. If the span is merged to
/// another span and the new @p smax is within @p flagMergeThr units
/// from the existing span, the span flags are merged.
///
/// @see rcHeightfield, rcSpan.
    public void rcAddSpan(rcContext ctx, rcHeightfield hf, int x, int y,
                   short smin, short smax,
                   char area, int flagMergeThr)
    {
//	rcAssert(ctx);
        addSpan(hf, x,y, smin, smax, area, flagMergeThr);
    }

    public static int clipPoly(float[] in, int n, float[] out, float pnx, float pnz, float pd)
    {
        float d[] = new float[12];
        for (int i = 0; i < n; ++i)
            d[i] = pnx*in[i*3+0] + pnz*in[i*3+2] + pd;

        int m = 0;
        for (int i = 0, j = n-1; i < n; j=i, ++i)
        {
            boolean ina = d[j] >= 0;
            boolean inb = d[i] >= 0;
            if (ina != inb)
            {
                float s = d[j] / (d[j] - d[i]);
                out[m*3+0] = in[j*3+0] + (in[i*3+0] - in[j*3+0])*s;
                out[m*3+1] = in[j*3+1] + (in[i*3+1] - in[j*3+1])*s;
                out[m*3+2] = in[j*3+2] + (in[i*3+2] - in[j*3+2])*s;
                m++;
            }
            if (inb)
            {
                out[m*3+0] = in[i*3+0];
                out[m*3+1] = in[i*3+1];
                out[m*3+2] = in[i*3+2];
                m++;
            }
        }
        return m;
    }
}
