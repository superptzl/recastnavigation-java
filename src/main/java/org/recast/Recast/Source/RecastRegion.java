package org.recast.Recast.Source;

import org.recast.Recast.Include.*;


public class RecastRegion extends RecastImpl {
    /// @par
/// 
/// Non-null regions will consist of connected, non-overlapping walkable spans that form a single contour.
/// Contours will form simple polygons.
/// 
/// If multiple regions form an area that is smaller than @p minRegionArea, then all spans will be
/// re-assigned to the zero (null) region.
/// 
/// Partitioning can result in smaller than necessary regions. @p mergeRegionArea helps 
/// reduce unecessarily small regions.
/// 
/// See the #rcConfig documentation for more information on the configuration parameters.
/// 
/// The region data will be available via the rcCompactHeightfield::maxRegions
/// and rcCompactSpan::reg fields.
/// 
/// @warning The distance field must be created using #rcBuildDistanceField before attempting to build regions.
/// 
/// @see rcCompactHeightfield, rcCompactSpan, rcBuildDistanceField, rcBuildRegionsMonotone, rcConfig
    public boolean rcBuildRegionsMonotone(rcContext ctx, rcCompactHeightfield chf,
                                          int borderSize, int minRegionArea, int mergeRegionArea) {
//        rcAssert(ctx);

        ctx.startTimer(rcTimerLabel.RC_TIMER_BUILD_REGIONS);

        int w = chf.width;
        int h = chf.height;
        short id = 1;

//        rcScopedDelete<unsigned short> srcReg = (unsigned short*)rcAlloc(sizeof(unsigned short)*chf.spanCount, RC_ALLOC_TEMP);
        short[] srcReg = new short[chf.spanCount];
        /*if (!srcReg)
        {
            ctx.log(RC_LOG_ERROR, "rcBuildRegionsMonotone: Out of memory 'src' (%d).", chf.spanCount);
            return false;
        }
        memset(srcReg,0,sizeof(unsigned short)*chf.spanCount);*/

        int nsweeps = rcMax(chf.width,chf.height);
//        rcScopedDelete<rcSweepSpan> sweeps = (rcSweepSpan*)rcAlloc(sizeof(rcSweepSpan)*nsweeps, RC_ALLOC_TEMP);
        rcSweepSpan[] sweeps = new rcSweepSpan[nsweeps];
        /*if (!sweeps)
        {
            ctx.log(RC_LOG_ERROR, "rcBuildRegionsMonotone: Out of memory 'sweeps' (%d).", nsweeps);
            return false;
        }*/


        // Mark border regions.
        if (borderSize > 0)
        {
            // Make sure border will not overflow.
            int bw = rcMin(w, borderSize);
            int bh = rcMin(h, borderSize);
            // Paint regions
            paintRectRegion(0, bw, 0, h, (short)(id|RC_BORDER_REG), chf, srcReg); id++;
            paintRectRegion(w-bw, w, 0, h, (short)(id|RC_BORDER_REG), chf, srcReg); id++;
            paintRectRegion(0, w, 0, bh, (short)(id|RC_BORDER_REG), chf, srcReg); id++;
            paintRectRegion(0, w, h-bh, h, (short)(id|RC_BORDER_REG), chf, srcReg); id++;

            chf.borderSize = borderSize;
        }

//        rcIntArray prev(256);
        rcIntArray prev = new rcIntArrayImpl(256);

        // Sweep one line at a time.
        for (int y = borderSize; y < h-borderSize; ++y)
        {
            // Collect spans from this row.
//            prev.resize(id+1);
//            memset(&prev[0],0,sizeof(int)*id);
            for (int i = 0; i < id; i++) {
                prev.set(i, 0);
            }
//            prev
            short rid = 1;

            for (int x = borderSize; x < w-borderSize; ++x)
            {
                rcCompactCell c = chf.cells[x+y*w];

                for (int i = (int)c.index, ni = (int)(c.index+c.count); i < ni; ++i)
                {
                    rcCompactSpan s = chf.spans[i];
                    if (chf.areas[i] == RC_NULL_AREA) continue;

                    // -x
                    short previd = 0;
                    if (rcGetCon(s, 0) != RC_NOT_CONNECTED)
                    {
                        int ax = x + rcGetDirOffsetX(0);
                        int ay = y + rcGetDirOffsetY(0);
                        int ai = (int)chf.cells[ax+ay*w].index + rcGetCon(s, 0);
                        if ((srcReg[ai] & RC_BORDER_REG) == 0 && chf.areas[i] == chf.areas[ai])
                            previd = srcReg[ai];
                    }

                    if (previd == 0)
                    {
                        previd = rid++;
                        sweeps[previd].rid = previd;
                        sweeps[previd].ns = 0;
                        sweeps[previd].nei = 0;
                    }

                    // -y
                    if (rcGetCon(s,3) != RC_NOT_CONNECTED)
                    {
                        int ax = x + rcGetDirOffsetX(3);
                        int ay = y + rcGetDirOffsetY(3);
                        int ai = (int)chf.cells[ax+ay*w].index + rcGetCon(s, 3);
                        if (srcReg[ai] != 0 && (srcReg[ai] & RC_BORDER_REG) == 0 && chf.areas[i] == chf.areas[ai])
                        {
                            short nr = srcReg[ai];
                            if (sweeps[previd].nei == 0 || sweeps[previd].nei == nr)
                            {
                                sweeps[previd].nei = nr;
                                sweeps[previd].ns++;
                                prev.set(nr, prev.get(nr)+1);
                            }
                            else
                            {
                                sweeps[previd].nei = RC_NULL_NEI;
                            }
                        }
                    }

                    srcReg[i] = previd;
                }
            }

            // Create unique ID.
            for (int i = 1; i < rid; ++i)
            {
                if (sweeps[i].nei != RC_NULL_NEI && sweeps[i].nei != 0 &&
                        prev.get(sweeps[i].nei) == (int)sweeps[i].ns)
                {
                    sweeps[i].id = sweeps[i].nei;
                }
                else
                {
                    sweeps[i].id = id++;
                }
            }

            // Remap IDs
            for (int x = borderSize; x < w-borderSize; ++x)
            {
                rcCompactCell c = chf.cells[x+y*w];

                for (int i = (int)c.index, ni = (int)(c.index+c.count); i < ni; ++i)
                {
                    if (srcReg[i] > 0 && srcReg[i] < rid)
                        srcReg[i] = sweeps[srcReg[i]].id;
                }
            }
        }

        ctx.startTimer(rcTimerLabel.RC_TIMER_BUILD_REGIONS_FILTER);

        // Filter out small regions.
        chf.maxRegions = id;
        if (!filterSmallRegions(ctx, minRegionArea, mergeRegionArea, chf.maxRegions, chf, srcReg))
            return false;

        ctx.stopTimer(rcTimerLabel.RC_TIMER_BUILD_REGIONS_FILTER);

        // Store the result out.
        for (int i = 0; i < chf.spanCount; ++i)
            chf.spans[i].reg = srcReg[i];

        ctx.stopTimer(rcTimerLabel.RC_TIMER_BUILD_REGIONS);

        return true;
    }

    static void paintRectRegion(int minx, int maxx, int miny, int maxy, short regId,
                                rcCompactHeightfield chf, short[] srcReg)
    {
        int w = chf.width;
        for (int y = miny; y < maxy; ++y)
        {
            for (int x = minx; x < maxx; ++x)
            {
                rcCompactCell c = chf.cells[x+y*w];
                for (int i = (int)c.index, ni = (int)(c.index+c.count); i < ni; ++i)
                {
                    if (chf.areas[i] != RC_NULL_AREA)
                        srcReg[i] = regId;
                }
            }
        }
    }

    public static boolean filterSmallRegions(rcContext ctx, int minRegionArea, int mergeRegionSize,
                                   short maxRegionId,
                                   rcCompactHeightfield chf,
                                   short[] srcReg)
    {
        int w = chf.width;
        int h = chf.height;

        int nreg = maxRegionId+1;
//        rcRegion* regions = (rcRegion*)rcAlloc(sizeof(rcRegion)*nreg, RC_ALLOC_TEMP);
        rcRegion[] regions = new rcRegion[nreg];
        /*if (!regions)
        {
            ctx.log(RC_LOG_ERROR, "filterSmallRegions: Out of memory 'regions' (%d).", nreg);
            return false;
        }*/

        // Construct regions
        for (int i = 0; i < nreg; ++i) {
            regions[i] = new rcRegion((short)i);
//            new(&regions[i]) rcRegion((unsigned short)i);
        }

        // Find edge of a region and find connections around the contour.
        for (int y = 0; y < h; ++y)
        {
            for (int x = 0; x < w; ++x)
            {
                rcCompactCell c = chf.cells[x+y*w];
                for (int i = (int)c.index, ni = (int)(c.index+c.count); i < ni; ++i)
                {
                    short r = srcReg[i];
                    if (r == 0 || r >= nreg)
                        continue;

                    rcRegion reg = regions[r];
                    reg.spanCount++;


                    // Update floors.
                    for (int j = (int)c.index; j < ni; ++j)
                    {
                        if (i == j) continue;
                        short floorId = srcReg[j];
                        if (floorId == 0 || floorId >= nreg)
                            continue;
                        addUniqueFloorRegion(reg, floorId);
                    }

                    // Have found contour
                    if (reg.connections.size() > 0)
                        continue;

                    reg.areaType = chf.areas[i];

                    // Check if this cell is next to a border.
                    int ndir = -1;
                    for (int dir = 0; dir < 4; ++dir)
                    {
                        if (isSolidEdge(chf, srcReg, x, y, i, dir))
                        {
                            ndir = dir;
                            break;
                        }
                    }

                    if (ndir != -1)
                    {
                        // The cell is at border.
                        // Walk around the contour to find all the neighbours.
                        walkContour(x, y, i, ndir, chf, srcReg, reg.connections);
                    }
                }
            }
        }

        // Remove too small regions.
		rcIntArray stack = new rcIntArrayImpl(32);
		rcIntArray trace = new rcIntArrayImpl(32);
        for (int i = 0; i < nreg; ++i)
        {
            rcRegion reg = regions[i];
            if (reg.id == 0 || (reg.id & RC_BORDER_REG) != 0)
                continue;
            if (reg.spanCount == 0)
                continue;
            if (reg.visited)
                continue;

            // Count the total size of all the connected regions.
            // Also keep track of the regions connects to a tile border.
            boolean connectsToBorder = false;
            int spanCount = 0;
            stack.resize(0);
            trace.resize(0);

            reg.visited = true;
            stack.push(i);

            while (stack.size() !=0)
            {
                // Pop
                int ri = stack.pop();

                rcRegion creg = regions[ri];

                spanCount += creg.spanCount;
                trace.push(ri);

                for (int j = 0; j < creg.connections.size(); ++j)
                {
                    if ((creg.connections.m_data[j] & RC_BORDER_REG) != 0)
                    {
                        connectsToBorder = true;
                        continue;
                    }
                    rcRegion neireg = regions[creg.connections.m_data[j]];
                    if (neireg.visited)
                        continue;
                    if (neireg.id == 0 || (neireg.id & RC_BORDER_REG) != 0)
                        continue;
                    // Visit
                    stack.push((int)neireg.id);
                    neireg.visited = true;
                }
            }

            // If the accumulated regions size is too small, remove it.
            // Do not remove areas which connect to tile borders
            // as their size cannot be estimated correctly and removing them
            // can potentially remove necessary areas.
            if (spanCount < minRegionArea && !connectsToBorder)
            {
                // Kill all visited regions.
                for (int j = 0; j < trace.size(); ++j)
                {
                    regions[trace.m_data[j]].spanCount = 0;
                    regions[trace.m_data[j]].id = 0;
                }
            }
        }

        // Merge too small regions to neighbour regions.
        int mergeCount = 0 ;
        do
        {
            mergeCount = 0;
            for (int i = 0; i < nreg; ++i)
            {
                rcRegion reg = regions[i];
                if (reg.id == 0 || (reg.id & RC_BORDER_REG) != 0)
                    continue;
                if (reg.spanCount == 0)
                    continue;

                // Check to see if the region should be merged.
                if (reg.spanCount > mergeRegionSize && isRegionConnectedToBorder(reg))
                    continue;

                // Small region with more than 1 connection.
                // Or region which is not connected to a border at all.
                // Find smallest neighbour region that connects to this one.
                int smallest = 0xfffffff;
                short mergeId = reg.id;
                for (int j = 0; j < reg.connections.size(); ++j)
                {
                    if ((reg.connections.m_data[j] & RC_BORDER_REG) != 0) continue;
                    rcRegion mreg = regions[reg.connections.m_data[j]];
                    if (mreg.id == 0 || (mreg.id & RC_BORDER_REG) != 0) continue;
                    if (mreg.spanCount < smallest &&
                            canMergeWithRegion(reg, mreg) &&
                            canMergeWithRegion(mreg, reg))
                    {
                        smallest = mreg.spanCount;
                        mergeId = mreg.id;
                    }
                }
                // Found new id.
                if (mergeId != reg.id)
                {
                    short oldId = reg.id;
                    rcRegion target = regions[mergeId];

                    // Merge neighbours.
                    if (mergeRegions(target, reg))
                    {
                        // Fixup regions pointing to current region.
                        for (int j = 0; j < nreg; ++j)
                        {
                            if (regions[j].id == 0 || (regions[j].id & RC_BORDER_REG) != 0) continue;
                            // If another region was already merged into current region
                            // change the nid of the previous region too.
                            if (regions[j].id == oldId)
                                regions[j].id = mergeId;
                            // Replace the current region with the new one if the
                            // current regions is neighbour.
                            replaceNeighbour(regions[j], oldId, mergeId);
                        }
                        mergeCount++;
                    }
                }
            }
        }
        while (mergeCount > 0);

        // Compress region Ids.
        for (int i = 0; i < nreg; ++i)
        {
            regions[i].remap = false;
            if (regions[i].id == 0) continue;       // Skip nil regions.
            if ((regions[i].id & RC_BORDER_REG )!= 0) continue;    // Skip external regions.
            regions[i].remap = true;
        }

        short regIdGen = 0;
        for (int i = 0; i < nreg; ++i)
        {
            if (!regions[i].remap)
                continue;
            short oldId = regions[i].id;
            short newId = ++regIdGen;
            for (int j = i; j < nreg; ++j)
            {
                if (regions[j].id == oldId)
                {
                    regions[j].id = newId;
                    regions[j].remap = false;
                }
            }
        }
        maxRegionId = regIdGen;

        // Remap regions.
        for (int i = 0; i < chf.spanCount; ++i)
        {
            if ((srcReg[i] & RC_BORDER_REG) == 0)
                srcReg[i] = regions[srcReg[i]].id;
        }

//        for (int i = 0; i < nreg; ++i)
//            regions[i].~rcRegion();
        regions = null;
//        rcFree(regions);

        return true;
    }

    public static boolean mergeRegions(rcRegion rega, rcRegion regb)
    {
        short aid = rega.id;
        short bid = regb.id;

        // Duplicate current neighbourhood.
        rcIntArray acon = new rcIntArrayImpl();
		acon.resize(rega.connections.size());
//        acon.resize(rega.connections.size());
//        for (int i = 0; i < rega.connections.size(); ++i)
//            acon.set(i, rega.connections.get(i));
//        LinkedList<Integer> bcon = regb.connections;
		for (int i = 0; i < rega.connections.size(); ++i)
				acon.m_data[i] = rega.connections.m_data[i];
		rcIntArray bcon = regb.connections;

        // Find insertion point on A.
        int insa = -1;
        for (int i = 0; i < acon.size(); ++i)
        {
            if (acon.m_data[i] == bid)
            {
                insa = i;
                break;
            }
        }
        if (insa == -1)
            return false;

        // Find insertion point on B.
        int insb = -1;
        for (int i = 0; i < bcon.size(); ++i)
        {
            if (bcon.m_data[i] == aid)
            {
                insb = i;
                break;
            }
        }
        if (insb == -1)
            return false;

        // Merge neighbours.
        rega.connections.resize(0);
        for (int i = 0, ni = acon.size(); i < ni-1; ++i)
            rega.connections.push(acon.m_data[(insa+1+i) % ni]);

        for (int i = 0, ni = bcon.size(); i < ni-1; ++i)
            rega.connections.push(bcon.m_data[(insb+1+i) % ni]);

        removeAdjacentNeighbours(rega);

        for (int j = 0; j < regb.floors.size(); ++j)
            addUniqueFloorRegion(rega, regb.floors.m_data[j]);
        rega.spanCount += regb.spanCount;
        regb.spanCount = 0;
        regb.connections.resize(0);

        return true;
    }

    static void replaceNeighbour(rcRegion reg, short oldId, short newId)
    {
        boolean neiChanged = false;
        for (int i = 0; i < reg.connections.size(); ++i)
        {
            if (reg.connections.m_data[i] == oldId)
            {
                reg.connections.m_data[i] = (int)newId;
                neiChanged = true;
            }
        }
        for (int i = 0; i < reg.floors.size(); ++i)
        {
            if (reg.floors.m_data[i] == oldId)
                reg.floors.m_data[i] = (int)newId;
        }
        if (neiChanged)
            removeAdjacentNeighbours(reg);
    }

    static void walkContour(int x, int y, int i, int dir,
                            rcCompactHeightfield chf,
                            short[] srcReg,
                            rcIntArray cont)
    {
        int startDir = dir;
        int starti = i;

        rcCompactSpan ss = chf.spans[i];
        short curReg = 0;
        if (rcGetCon(ss, dir) != RC_NOT_CONNECTED)
        {
            int ax = x + rcGetDirOffsetX(dir);
            int ay = y + rcGetDirOffsetY(dir);
            int ai = (int)chf.cells[ax+ay*chf.width].index + rcGetCon(ss, dir);
            curReg = srcReg[ai];
        }
        cont.push((int)curReg);

        int iter = 0;
        while (++iter < 40000)
        {
            rcCompactSpan s = chf.spans[i];

            if (isSolidEdge(chf, srcReg, x, y, i, dir))
            {
                // Choose the edge corner
                short r = 0;
                if (rcGetCon(s, dir) != RC_NOT_CONNECTED)
                {
                    int ax = x + rcGetDirOffsetX(dir);
                    int ay = y + rcGetDirOffsetY(dir);
                    int ai = (int)chf.cells[ax+ay*chf.width].index + rcGetCon(s, dir);
                    r = srcReg[ai];
                }
                if (r != curReg)
                {
                    curReg = r;
                    cont.push((int)curReg);
                }

                dir = (dir+1) & 0x3;  // Rotate CW
            }
            else
            {
                int ni = -1;
                int nx = x + rcGetDirOffsetX(dir);
                int ny = y + rcGetDirOffsetY(dir);
                if (rcGetCon(s, dir) != RC_NOT_CONNECTED)
                {
                    rcCompactCell nc = chf.cells[nx+ny*chf.width];
                    ni = (int)nc.index + rcGetCon(s, dir);
                }
                if (ni == -1)
                {
                    // Should not happen.
                    return;
                }
                x = nx;
                y = ny;
                i = ni;
                dir = (dir+3) & 0x3;	// Rotate CCW
            }

            if (starti == i && startDir == dir)
            {
                break;
            }
        }

        // Remove adjacent duplicates.
        if (cont.size() > 1)
        {
            for (int j = 0; j < cont.size(); )
            {
                int nj = (j+1) % cont.size();
                if (cont.m_data[j] == cont.m_data[nj])
                {
                    for (int k = j; k < cont.size()-1; ++k)
                        cont.m_data[k] = cont.m_data[k+1];
                    cont.pop();
                }
                else
                    ++j;
            }
        }
    }



    public static final short RC_NULL_NEI = (short)0xffff;

    public static class rcRegion
    {
        public rcRegion(short id) {
            this.id = id;
        }

        //        inline rcRegion(unsigned short i) :
//        spanCount(0),
//                id(i),
//                areaType(0),
//                remap(false),
//                visited(false)
//        {}

        public int spanCount;					// Number of spans belonging to this region
        public short id;				// ID of the region
        public  char areaType = 0;			// Are type.
        public boolean remap;
        public boolean visited;
        public rcIntArray connections;
        public rcIntArray floors;
    }

    static void addUniqueFloorRegion(rcRegion reg, int n)
    {
        for (int i = 0; i < reg.floors.size(); ++i)
            if (reg.floors.m_data[i] == n)
                return;
        reg.floors.push(n);
    }

    static boolean isSolidEdge(rcCompactHeightfield chf, short[] srcReg,
                            int x, int y, int i, int dir)
    {
        rcCompactSpan s = chf.spans[i];
        short r = 0;
        if (rcGetCon(s, dir) != RC_NOT_CONNECTED)
        {
            int ax = x + rcGetDirOffsetX(dir);
            int ay = y + rcGetDirOffsetY(dir);
            int ai = (int)chf.cells[ax+ay*chf.width].index + rcGetCon(s, dir);
            r = srcReg[ai];
        }
        if (r == srcReg[i])
            return false;
        return true;
    }

    public static boolean isRegionConnectedToBorder(rcRegion reg)
    {
        // Region is connected to border if
        // one of the neighbours is null id.
        for (int i = 0; i < reg.connections.size(); ++i)
        {
            if (reg.connections.m_data[i] == 0)
                return true;
        }
        return false;
    }

    public static void removeAdjacentNeighbours(rcRegion reg)
    {
        // Remove adjacent duplicates.
        for (int i = 0; i < reg.connections.size() && reg.connections.size() > 1; )
        {
            int ni = (i+1) % reg.connections.size();
            if (reg.connections.m_data[i] == reg.connections.m_data[ni])
            {
                // Remove duplicate
                for (int j = i; j < reg.connections.size()-1; ++j)
                    reg.connections.m_data[j] = reg.connections.m_data[j+1];
                reg.connections.pop();
            }
            else
                ++i;
        }
    }

    public static boolean canMergeWithRegion(rcRegion rega, rcRegion regb)
    {
        if (rega.areaType != regb.areaType)
            return false;
        int n = 0;
        for (int i = 0; i < rega.connections.size(); ++i)
        {
            if (rega.connections.m_data[i] == regb.id)
                n++;
        }
        if (n > 1)
            return false;
        for (int i = 0; i < rega.floors.size(); ++i)
        {
            if (rega.floors.m_data[i] == regb.id)
                return false;
        }
        return true;
    }


    /// @par
/// 
/// This is usually the second to the last step in creating a fully built
/// compact heightfield.  This step is required before regions are built
/// using #rcBuildRegions or #rcBuildRegionsMonotone.
/// 
/// After this step, the distance data is available via the rcCompactHeightfield::maxDistance
/// and rcCompactHeightfield::dist fields.
///
/// @see rcCompactHeightfield, rcBuildRegions, rcBuildRegionsMonotone
    public boolean rcBuildDistanceField(rcContext ctx, rcCompactHeightfield chf)
    {
//        rcAssert(ctx);

        ctx.startTimer(rcTimerLabel.RC_TIMER_BUILD_DISTANCEFIELD);

        /*if (chf.dist)
        {
            rcFree(chf.dist);
            chf.dist = 0;
        }*/

//        unsigned short* src = (unsigned short*)rcAlloc(sizeof(unsigned short)*chf.spanCount, RC_ALLOC_TEMP);
        short[] src = new short[chf.spanCount];
        /*if (!src)
        {
            ctx.log(RC_LOG_ERROR, "rcBuildDistanceField: Out of memory 'src' (%d).", chf.spanCount);
            return false;
        }*/
//        unsigned short* dst = (unsigned short*)rcAlloc(sizeof(unsigned short)*chf.spanCount, RC_ALLOC_TEMP);
        short[] dst = new short[chf.spanCount];
        /*if (!dst)
        {
            ctx.log(RC_LOG_ERROR, "rcBuildDistanceField: Out of memory 'dst' (%d).", chf.spanCount);
            rcFree(src);
            return false;
        }*/

        short[] maxDist = new short[]{0};

        ctx.startTimer(rcTimerLabel.RC_TIMER_BUILD_DISTANCEFIELD_DIST);

        calculateDistanceField(chf, src, maxDist);
        chf.maxDistance = maxDist[0];

        ctx.stopTimer(rcTimerLabel.RC_TIMER_BUILD_DISTANCEFIELD_DIST);

        ctx.startTimer(rcTimerLabel.RC_TIMER_BUILD_DISTANCEFIELD_BLUR);

        // Blur
        if (boxBlur(chf, 1, src, dst) != src) {
//            rcSwap(src, dst);
            short[] tmp = src;
            src = dst;
            dst = tmp;
        }

        // Store distance.
        chf.dist = src;

        ctx.stopTimer(rcTimerLabel.RC_TIMER_BUILD_DISTANCEFIELD_BLUR);

        ctx.stopTimer(rcTimerLabel.RC_TIMER_BUILD_DISTANCEFIELD);

        dst = null;
//        rcFree(dst);

        return true;
    }


    /// @par
/// 
/// Non-null regions will consist of connected, non-overlapping walkable spans that form a single contour.
/// Contours will form simple polygons.
/// 
/// If multiple regions form an area that is smaller than @p minRegionArea, then all spans will be
/// re-assigned to the zero (null) region.
/// 
/// Watershed partitioning can result in smaller than necessary regions, especially in diagonal corridors. 
/// @p mergeRegionArea helps reduce unecessarily small regions.
/// 
/// See the #rcConfig documentation for more information on the configuration parameters.
/// 
/// The region data will be available via the rcCompactHeightfield::maxRegions
/// and rcCompactSpan::reg fields.
/// 
/// @warning The distance field must be created using #rcBuildDistanceField before attempting to build regions.
/// 
/// @see rcCompactHeightfield, rcCompactSpan, rcBuildDistanceField, rcBuildRegionsMonotone, rcConfig
public     boolean rcBuildRegions(rcContext ctx, rcCompactHeightfield chf,
                        int borderSize, int minRegionArea, int mergeRegionArea)
    {
//        rcAssert(ctx);

        ctx.startTimer(rcTimerLabel.RC_TIMER_BUILD_REGIONS);

        int w = chf.width;
        int h = chf.height;

//        rcScopedDelete<unsigned short> buf = (unsigned short*)rcAlloc(sizeof(unsigned short)*chf.spanCount*4, RC_ALLOC_TEMP);
        short[] buf = new short[chf.spanCount*4];
        /*if (!buf)
        {
            ctx.log(RC_LOG_ERROR, "rcBuildRegions: Out of memory 'tmp' (%d).", chf.spanCount*4);
            return false;
        }*/

        ctx.startTimer(rcTimerLabel.RC_TIMER_BUILD_REGIONS_WATERSHED);

		rcIntArray stack = new rcIntArrayImpl(1024);
		rcIntArray visited = new rcIntArrayImpl(1024);

        short[] srcReg = buf;
        short[] srcDist = createN(buf, chf.spanCount, chf.spanCount);//buf + chf.spanCount;
        short[] dstReg = createN(buf, chf.spanCount*2, chf.spanCount);//buf + chf.spanCount * 2;
        short[] dstDist = createN(buf, chf.spanCount*3, chf.spanCount);//buf + chf.spanCount * 3;

//        memset(srcReg, 0, sizeof(unsigned short)*chf.spanCount);
//        memset(srcDist, 0, sizeof(unsigned short)*chf.spanCount);

        short regionId = 1;
        short level = (short)((chf.maxDistance+1) & ~1);

        // TODO: Figure better formula, expandIters defines how much the 
        // watershed "overflows" and simplifies the regions. Tying it to
        // agent radius was usually good indication how greedy it could be.
//	const int expandIters = 4 + walkableRadius * 2;
        int expandIters = 8;

        if (borderSize > 0)
        {
            // Make sure border will not overflow.
            int bw = rcMin(w, borderSize);
            int bh = rcMin(h, borderSize);
            // Paint regions
            paintRectRegion(0, bw, 0, h, (short)(regionId|RC_BORDER_REG), chf, srcReg); regionId++;
            paintRectRegion(w-bw, w, 0, h, (short)(regionId|RC_BORDER_REG), chf, srcReg); regionId++;
            paintRectRegion(0, w, 0, bh, (short)(regionId|RC_BORDER_REG), chf, srcReg); regionId++;
            paintRectRegion(0, w, h-bh, h, (short)(regionId|RC_BORDER_REG), chf, srcReg); regionId++;

            chf.borderSize = borderSize;
        }

        while (level > 0)
        {
            level = (short)(level >= 2 ? level-2 : 0);

            ctx.startTimer(rcTimerLabel.RC_TIMER_BUILD_REGIONS_EXPAND);

            // Expand current regions until no empty connected cells found.
            if (expandRegions(expandIters, level, chf, srcReg, srcDist, dstReg, dstDist, stack) != srcReg)
            {
//                rcSwap(srcReg, dstReg);
                short[] tmp = srcReg;
                srcReg = dstReg;
                dstReg = tmp;
//                rcSwap(srcDist, dstDist);
                tmp = srcDist;
                srcDist = dstDist;
                dstDist = tmp;
            }

            ctx.stopTimer(rcTimerLabel.RC_TIMER_BUILD_REGIONS_EXPAND);

            ctx.startTimer(rcTimerLabel.RC_TIMER_BUILD_REGIONS_FLOOD);

            // Mark new regions with IDs.
            for (int y = 0; y < h; ++y)
            {
                for (int x = 0; x < w; ++x)
                {
                    rcCompactCell c = chf.cells[x+y*w];
                    for (int i = (int)c.index, ni = (int)(c.index+c.count); i < ni; ++i)
                    {
                        if (chf.dist[i] < level || srcReg[i] != 0 || chf.areas[i] == RC_NULL_AREA)
                            continue;
                        if (floodRegion(x, y, i, level, regionId, chf, srcReg, srcDist, stack))
                            regionId++;
                    }
                }
            }

            ctx.stopTimer(rcTimerLabel.RC_TIMER_BUILD_REGIONS_FLOOD);
        }

        // Expand current regions until no empty connected cells found.
        if (expandRegions(expandIters*8, (short)0, chf, srcReg, srcDist, dstReg, dstDist, stack) != srcReg)
        {
//            rcSwap(srcReg, dstReg);
            short[] tmp = srcReg;
            srcReg = dstReg;
            dstReg = tmp;
//            rcSwap(srcDist, dstDist);
            tmp = srcDist;
            srcDist = dstDist;
            dstDist = tmp;
        }

        ctx.stopTimer(rcTimerLabel.RC_TIMER_BUILD_REGIONS_WATERSHED);

        ctx.startTimer(rcTimerLabel.RC_TIMER_BUILD_REGIONS_FILTER);

        // Filter out small regions.
        chf.maxRegions = regionId;
        if (!filterSmallRegions(ctx, minRegionArea, mergeRegionArea, chf.maxRegions, chf, srcReg))
            return false;

        ctx.stopTimer(rcTimerLabel.RC_TIMER_BUILD_REGIONS_FILTER);

        // Write the result out.
        for (int i = 0; i < chf.spanCount; ++i)
            chf.spans[i].reg = srcReg[i];

        ctx.stopTimer(rcTimerLabel.RC_TIMER_BUILD_REGIONS);

        return true;
    }

    static boolean floodRegion(int x, int y, int i,
                            short level, short r,
                            rcCompactHeightfield chf,
                            short[] srcReg, short[] srcDist,
                            rcIntArray stack)
    {
        int w = chf.width;

        char area = chf.areas[i];

        // Flood fill mark region.
        stack.resize(0);
        stack.push((int)x);
        stack.push((int)y);
        stack.push((int)i);
        srcReg[i] = r;
        srcDist[i] = 0;

        short lev = (short)(level >= 2 ? level-2 : 0);
        int count = 0;

        while (stack.size() > 0)
        {
            int ci = stack.pop();
            int cy = stack.pop();
            int cx = stack.pop();

            rcCompactSpan cs = chf.spans[ci];

            // Check if any of the neighbours already have a valid region set.
            short ar = 0;
            for (int dir = 0; dir < 4; ++dir)
            {
                // 8 connected
                if (rcGetCon(cs, dir) != RC_NOT_CONNECTED)
                {
                    int ax = cx + rcGetDirOffsetX(dir);
                    int ay = cy + rcGetDirOffsetY(dir);
                    int ai = (int)chf.cells[ax+ay*w].index + rcGetCon(cs, dir);
                    if (chf.areas[ai] != area)
                        continue;
                    short nr = srcReg[ai];
                    if ((nr & RC_BORDER_REG) != 0) // Do not take borders into account.
                        continue;
                    if (nr != 0 && nr != r)
                        ar = nr;

                    rcCompactSpan as = chf.spans[ai];

                    int dir2 = (dir+1) & 0x3;
                    if (rcGetCon(as, dir2) != RC_NOT_CONNECTED)
                    {
                        int ax2 = ax + rcGetDirOffsetX(dir2);
                        int ay2 = ay + rcGetDirOffsetY(dir2);
                        int ai2 = (int)chf.cells[ax2+ay2*w].index + rcGetCon(as, dir2);
                        if (chf.areas[ai2] != area)
                            continue;
                        short nr2 = srcReg[ai2];
                        if (nr2 != 0 && nr2 != r)
                            ar = nr2;
                    }
                }
            }
            if (ar != 0)
            {
                srcReg[ci] = 0;
                continue;
            }
            count++;

            // Expand neighbours.
            for (int dir = 0; dir < 4; ++dir)
            {
                if (rcGetCon(cs, dir) != RC_NOT_CONNECTED)
                {
                    int ax = cx + rcGetDirOffsetX(dir);
                    int ay = cy + rcGetDirOffsetY(dir);
                    int ai = (int)chf.cells[ax+ay*w].index + rcGetCon(cs, dir);
                    if (chf.areas[ai] != area)
                        continue;
                    if (chf.dist[ai] >= lev && srcReg[ai] == 0)
                    {
                        srcReg[ai] = r;
                        srcDist[ai] = 0;
                        stack.push(ax);
                        stack.push(ay);
                        stack.push(ai);
                    }
                }
            }
        }

        return count > 0;
    }

    public static short[] expandRegions(int maxIter, short level,
                                         rcCompactHeightfield chf,
                                         short[] srcReg, short[] srcDist,
                                         short[] dstReg, short[] dstDist,
										 rcIntArray stack)
    {
        int w = chf.width;
        int h = chf.height;

        // Find cells revealed by the raised level.
        stack.resize(0);
        for (int y = 0; y < h; ++y)
        {
            for (int x = 0; x < w; ++x)
            {
                rcCompactCell c = chf.cells[x+y*w];
                for (int i = (int)c.index, ni = (int)(c.index+c.count); i < ni; ++i)
                {
                    if (chf.dist[i] >= level && srcReg[i] == 0 && chf.areas[i] != RC_NULL_AREA)
                    {
                        stack.push(x);
                        stack.push(y);
                        stack.push(i);
                    }
                }
            }
        }

        int iter = 0;
        while (stack.size() > 0)
        {
            int failed = 0;

//            memcpy(dstReg, srcReg, sizeof(unsigned short)*chf.spanCount);
            System.arraycopy(srcReg, 0, dstReg, 0, chf.spanCount);
//            memcpy(dstDist, srcDist, sizeof(unsigned short)*chf.spanCount);
            System.arraycopy(srcDist, 0, dstDist, 0, chf.spanCount);

            for (int j = 0; j < stack.size(); j += 3)
            {
                int x = stack.m_data[j+0];
                int y = stack.m_data[j+1];
                int i = stack.m_data[j+2];
                if (i < 0)
                {
                    failed++;
                    continue;
                }

                short r = srcReg[i];
                short d2 = (short)0xffff;
                char area = chf.areas[i];
                rcCompactSpan s = chf.spans[i];
                for (int dir = 0; dir < 4; ++dir)
                {
                    if (rcGetCon(s, dir) == RC_NOT_CONNECTED) continue;
                    int ax = x + rcGetDirOffsetX(dir);
                    int ay = y + rcGetDirOffsetY(dir);
                    int ai = (int)chf.cells[ax+ay*w].index + rcGetCon(s, dir);
                    if (chf.areas[ai] != area) continue;
                    if (srcReg[ai] > 0 && (srcReg[ai] & RC_BORDER_REG) == 0)
                    {
                        if ((int)srcDist[ai]+2 < (int)d2)
                        {
                            r = srcReg[ai];
                            d2 = (short)(srcDist[ai]+2);
                        }
                    }
                }
                if (r != 0)
                {
                    stack.m_data[j+2] = -1; // mark as used
                    dstReg[i] = r;
                    dstDist[i] = d2;
                }
                else
                {
                    failed++;
                }
            }

            // rcSwap source and dest.
            short[] tmp = srcReg;
            srcReg = dstReg;
            dstReg = tmp;
//            rcSwap(srcReg, dstReg);
//            rcSwap(srcDist, dstDist);
            tmp = srcDist;
            srcDist = dstDist;
            dstDist = tmp;

            if (failed*3 == stack.size())
                break;

            if (level > 0)
            {
                ++iter;
                if (iter >= maxIter)
                    break;
            }
        }

        return srcReg;
    }



    public static void calculateDistanceField(rcCompactHeightfield chf, short[] src, short[] maxDist)
    {
        int w = chf.width;
        int h = chf.height;

        // Init distance and points.
        for (int i = 0; i < chf.spanCount; ++i)
            src[i] = (short)0xffff;

        // Mark boundary cells.
        for (int y = 0; y < h; ++y)
        {
            for (int x = 0; x < w; ++x)
            {
                rcCompactCell c = chf.cells[x+y*w];
                for (int i = (int)c.index, ni = (int)(c.index+c.count); i < ni; ++i)
                {
                    rcCompactSpan s = chf.spans[i];
                    char area = chf.areas[i];

                    int nc = 0;
                    for (int dir = 0; dir < 4; ++dir)
                    {
                        if (rcGetCon(s, dir) != RC_NOT_CONNECTED)
                        {
                            int ax = x + rcGetDirOffsetX(dir);
                            int ay = y + rcGetDirOffsetY(dir);
                            int ai = (int)chf.cells[ax+ay*w].index + rcGetCon(s, dir);
                            if (area == chf.areas[ai])
                                nc++;
                        }
                    }
                    if (nc != 4)
                        src[i] = 0;
                }
            }
        }


        // Pass 1
        for (int y = 0; y < h; ++y)
        {
            for (int x = 0; x < w; ++x)
            {
                rcCompactCell c = chf.cells[x+y*w];
                for (int i = (int)c.index, ni = (int)(c.index+c.count); i < ni; ++i)
                {
                    rcCompactSpan s = chf.spans[i];

                    if (rcGetCon(s, 0) != RC_NOT_CONNECTED)
                    {
                        // (-1,0)
                        int ax = x + rcGetDirOffsetX(0);
                        int ay = y + rcGetDirOffsetY(0);
                        int ai = (int)chf.cells[ax+ay*w].index + rcGetCon(s, 0);
                        rcCompactSpan as = chf.spans[ai];
                        if (src[ai]+2 < src[i])
                            src[i] = (short)(src[ai]+2);

                        // (-1,-1)
                        if (rcGetCon(as, 3) != RC_NOT_CONNECTED)
                        {
                            int aax = ax + rcGetDirOffsetX(3);
                            int aay = ay + rcGetDirOffsetY(3);
                            int aai = (int)chf.cells[aax+aay*w].index + rcGetCon(as, 3);
                            if (src[aai]+3 < src[i])
                                src[i] = (short)(src[aai]+3);
                        }
                    }
                    if (rcGetCon(s, 3) != RC_NOT_CONNECTED)
                    {
                        // (0,-1)
                        int ax = x + rcGetDirOffsetX(3);
                        int ay = y + rcGetDirOffsetY(3);
                        int ai = (int)chf.cells[ax+ay*w].index + rcGetCon(s, 3);
                        rcCompactSpan as = chf.spans[ai];
                        if (src[ai]+2 < src[i])
                            src[i] = (short)(src[ai]+2);

                        // (1,-1)
                        if (rcGetCon(as, 2) != RC_NOT_CONNECTED)
                        {
                            int aax = ax + rcGetDirOffsetX(2);
                            int aay = ay + rcGetDirOffsetY(2);
                            int aai = (int)chf.cells[aax+aay*w].index + rcGetCon(as, 2);
                            if (src[aai]+3 < src[i])
                                src[i] = (short)(src[aai]+3);
                        }
                    }
                }
            }
        }

        // Pass 2
        for (int y = h-1; y >= 0; --y)
        {
            for (int x = w-1; x >= 0; --x)
            {
                rcCompactCell c = chf.cells[x+y*w];
                for (int i = (int)c.index, ni = (int)(c.index+c.count); i < ni; ++i)
                {
                    rcCompactSpan s = chf.spans[i];

                    if (rcGetCon(s, 2) != RC_NOT_CONNECTED)
                    {
                        // (1,0)
                        int ax = x + rcGetDirOffsetX(2);
                        int ay = y + rcGetDirOffsetY(2);
                        int ai = (int)chf.cells[ax+ay*w].index + rcGetCon(s, 2);
                        rcCompactSpan as = chf.spans[ai];
                        if (src[ai]+2 < src[i])
                            src[i] = (short)(src[ai]+2);

                        // (1,1)
                        if (rcGetCon(as, 1) != RC_NOT_CONNECTED)
                        {
                            int aax = ax + rcGetDirOffsetX(1);
                            int aay = ay + rcGetDirOffsetY(1);
                            int aai = (int)chf.cells[aax+aay*w].index + rcGetCon(as, 1);
                            if (src[aai]+3 < src[i])
                                src[i] = (short)(src[aai]+3);
                        }
                    }
                    if (rcGetCon(s, 1) != RC_NOT_CONNECTED)
                    {
                        // (0,1)
                        int ax = x + rcGetDirOffsetX(1);
                        int ay = y + rcGetDirOffsetY(1);
                        int ai = (int)chf.cells[ax+ay*w].index + rcGetCon(s, 1);
                        rcCompactSpan as = chf.spans[ai];
                        if (src[ai]+2 < src[i])
                            src[i] = (short)(src[ai]+2);

                        // (-1,1)
                        if (rcGetCon(as, 0) != RC_NOT_CONNECTED)
                        {
                            int aax = ax + rcGetDirOffsetX(0);
                            int aay = ay + rcGetDirOffsetY(0);
                            int aai = (int)chf.cells[aax+aay*w].index + rcGetCon(as, 0);
                            if (src[aai]+3 < src[i])
                                src[i] = (short)(src[aai]+3);
                        }
                    }
                }
            }
        }

        maxDist[0] = 0;
        for (int i = 0; i < chf.spanCount; ++i)
            maxDist[0] = (short)rcMax(src[i], maxDist[0]);

    }

    static short[] boxBlur(rcCompactHeightfield chf, int thr,
                                   short[] src, short[] dst)
    {
        int w = chf.width;
        int h = chf.height;

        thr *= 2;

        for (int y = 0; y < h; ++y)
        {
            for (int x = 0; x < w; ++x)
            {
                rcCompactCell c = chf.cells[x+y*w];
                for (int i = (int)c.index, ni = (int)(c.index+c.count); i < ni; ++i)
                {
                    rcCompactSpan s = chf.spans[i];
                    short cd = src[i];
                    if (cd <= thr)
                    {
                        dst[i] = cd;
                        continue;
                    }

                    int d = (int)cd;
                    for (int dir = 0; dir < 4; ++dir)
                    {
                        if (rcGetCon(s, dir) != RC_NOT_CONNECTED)
                        {
                            int ax = x + rcGetDirOffsetX(dir);
                            int ay = y + rcGetDirOffsetY(dir);
                            int ai = (int)chf.cells[ax+ay*w].index + rcGetCon(s, dir);
                            d += (int)src[ai];

                            rcCompactSpan as = chf.spans[ai];
                            int dir2 = (dir+1) & 0x3;
                            if (rcGetCon(as, dir2) != RC_NOT_CONNECTED)
                            {
                                int ax2 = ax + rcGetDirOffsetX(dir2);
                                int ay2 = ay + rcGetDirOffsetY(dir2);
                                int ai2 = (int)chf.cells[ax2+ay2*w].index + rcGetCon(as, dir2);
                                d += (int)src[ai2];
                            }
                            else
                            {
                                d += cd;
                            }
                        }
                        else
                        {
                            d += cd*2;
                        }
                    }
                    dst[i] = (short)((d+5)/9);
                }
            }
        }
        return dst;
    }
}
