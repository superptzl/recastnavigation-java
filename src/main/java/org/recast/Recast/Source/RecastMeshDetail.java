package org.recast.Recast.Source;

import org.recast.Recast.Include.*;

import java.util.Arrays;

public class RecastMeshDetail extends RecastImpl {
    /// @par
///
/// See the #rcConfig documentation for more information on the configuration parameters.
///
/// @see rcAllocPolyMeshDetail, rcPolyMesh, rcCompactHeightfield, rcPolyMeshDetail, rcConfig
    public boolean rcBuildPolyMeshDetail(rcContext ctx, rcPolyMesh mesh, rcCompactHeightfield chf,
                                         float sampleDist, float sampleMaxError,
                                         rcPolyMeshDetail dmesh) {
    {
//        rcAssert(ctx);

        ctx.startTimer(rcTimerLabel.RC_TIMER_BUILD_POLYMESHDETAIL);

        if (mesh.nverts == 0 || mesh.npolys == 0)
            return true;

        int nvp = mesh.nvp;
        float cs = mesh.cs;
        float ch = mesh.ch;
        float[] orig = mesh.bmin;
        int borderSize = mesh.borderSize;

		rcIntArray edges = new rcIntArrayImpl (64);
		rcIntArray tris = new rcIntArrayImpl(512);
		rcIntArray stack = new rcIntArrayImpl(512);
		rcIntArray samples = new rcIntArrayImpl(512);
        float verts[] = new float[256*3];
        rcHeightPatch hp = new rcHeightPatch();
        int nPolyVerts = 0;
        int maxhw = 0, maxhh = 0;

//        rcScopedDelete<int> bounds = (int*)rcAlloc(sizeof(int)*mesh.npolys*4, RC_ALLOC_TEMP);
        int[] bounds = new int[mesh.npolys*4];
        /*if (!bounds)
        {
            ctx.log(RC_LOG_ERROR, "rcBuildPolyMeshDetail: Out of memory 'bounds' (%d).", mesh.npolys*4);
            return false;
        }*/
//        rcScopedDelete<float> poly = (float*)rcAlloc(sizeof(float)*nvp*3, RC_ALLOC_TEMP);
        float[] poly = new float[nvp*3];
        /*if (!poly)
        {
            ctx.log(RC_LOG_ERROR, "rcBuildPolyMeshDetail: Out of memory 'poly' (%d).", nvp*3);
            return false;
        }*/

        // Find max size for a polygon area.
        for (int i = 0; i < mesh.npolys; ++i)
        {
//            int* p = &mesh.polys[i*nvp*2];
//            /*int xmin = */bounds[i*4+0];
//            /*int xmax = */bounds[i*4+1];
//            /*int ymin = */bounds[i*4+2];
//            /*int ymax = */bounds[i*4+3];
			bounds[i*4+0] = chf.width;
			bounds[i*4+1] = 0;
			bounds[i*4+2] = chf.height;
			bounds[i*4+3] = 0;
            for (int j = 0; j < nvp; ++j)
            {
                if(mesh.polys[i*nvp*2 + j] == RC_MESH_NULL_IDX) break;
				bounds[i*4+0] = rcMin(bounds[i*4+0], mesh.verts[mesh.polys[i*nvp*2 + j]*3+0]);
				bounds[i*4+1] = rcMax(bounds[i*4+1], mesh.verts[mesh.polys[i*nvp*2 + j]*3+0]);
				bounds[i*4+2] = rcMin(bounds[i*4+2], mesh.verts[mesh.polys[i*nvp*2 + j]*3+2]);
				bounds[i*4+3] = rcMax(bounds[i*4+3], mesh.verts[mesh.polys[i*nvp*2 + j]*3+2]);
                nPolyVerts++;
            }
			bounds[i*4+0] = rcMax(0,bounds[i*4+0]-1);
			bounds[i*4+1] = rcMin(chf.width,bounds[i*4+1]+1);
			bounds[i*4+2] = rcMax(0,bounds[i*4+2]-1);
			bounds[i*4+3] = rcMin(chf.height,bounds[i*4+3]+1);
            if (bounds[i*4+0] >= bounds[i*4+1] || bounds[i*4+2] >= bounds[i*4+3]) continue;
            maxhw = rcMax(maxhw, bounds[i*4+1]-bounds[i*4+0]);
            maxhh = rcMax(maxhh, bounds[i*4+3]-bounds[i*4+1]);
        }

//        hp.data = (unsigned int*)rcAlloc(sizeof(unsigned int)*maxhw*maxhh, RC_ALLOC_TEMP);
        hp.data = new int[maxhw*maxhh];//(unsigned int*)rcAlloc(sizeof(unsigned int)*maxhw*maxhh, RC_ALLOC_TEMP);
        /*if (!hp.data)
        {
            ctx.log(RC_LOG_ERROR, "rcBuildPolyMeshDetail: Out of memory 'hp.data' (%d).", maxhw*maxhh);
            return false;
        }*/

        dmesh.nmeshes = mesh.npolys;
        dmesh.nverts = 0;
        dmesh.ntris = 0;
        dmesh.meshes = new int[dmesh.nmeshes*4];//(unsigned int*)rcAlloc(sizeof(unsigned int)*dmesh.nmeshes*4, RC_ALLOC_PERM);
        /*if (!dmesh.meshes)
        {
            ctx.log(RC_LOG_ERROR, "rcBuildPolyMeshDetail: Out of memory 'dmesh.meshes' (%d).", dmesh.nmeshes*4);
            return false;
        }*/

        int vcap = nPolyVerts+nPolyVerts/2;
        int tcap = vcap*2;

        dmesh.nverts = 0;
        dmesh.verts = new float[vcap*3];//(float*)rcAlloc(sizeof(float)*vcap*3, RC_ALLOC_PERM);
        /*if (!dmesh.verts)
        {
            ctx.log(RC_LOG_ERROR, "rcBuildPolyMeshDetail: Out of memory 'dmesh.verts' (%d).", vcap*3);
            return false;
        }*/
        dmesh.ntris = 0;
        dmesh.tris = new char[tcap*4];//(unsigned char*)rcAlloc(sizeof(unsigned char*)*tcap*4, RC_ALLOC_PERM);
        /*if (!dmesh.tris)
        {
            ctx.log(RC_LOG_ERROR, "rcBuildPolyMeshDetail: Out of memory 'dmesh.tris' (%d).", tcap*4);
            return false;
        }*/

        for (int i = 0; i < mesh.npolys; ++i)
        {
			int[] p = createN(mesh.polys, i*nvp*2, nvp);

            // Store polygon vertices for processing.
            int npoly = 0;
            for (int j = 0; j < nvp; ++j)
            {
                if(mesh.polys[i*nvp*2+j] == RC_MESH_NULL_IDX) break;
//				int[] v = create3(mesh.verts, mesh.polys[i*nvp*2+j]*3);
                poly[j*3+0] = mesh.verts[mesh.polys[i*nvp*2+j]*3+0]*cs;
                poly[j*3+1] = mesh.verts[mesh.polys[i*nvp*2+j]*3+1]*ch;
                poly[j*3+2] = mesh.verts[mesh.polys[i*nvp*2+j]*3+2]*cs;
                npoly++;
            }

            // Get the height data from the area of the polygon.
            hp.xmin = bounds[i*4+0];
            hp.ymin = bounds[i*4+2];
            hp.width = bounds[i*4+1]-bounds[i*4+0];
            hp.height = bounds[i*4+3]-bounds[i*4+2];
            getHeightData(chf, p, npoly, mesh.verts, borderSize, hp, stack);

            // Build detail mesh.
            int nverts[] = new int[]{0};
            if (!buildPolyDetail(ctx, poly, npoly,
                    sampleDist, sampleMaxError,
                    chf, hp, verts, nverts, tris,
                    edges, samples))
            {
                return false;
            }

            // Move detail verts to world space.
            for (int j = 0; j < nverts[0]; ++j)
            {
                verts[j*3+0] += orig[0];
                verts[j*3+1] += orig[1] + chf.ch; // Is this offset necessary?
                verts[j*3+2] += orig[2];
            }
            // Offset poly too, will be used to flag checking.
            for (int j = 0; j < npoly; ++j)
            {
                poly[j*3+0] += orig[0];
                poly[j*3+1] += orig[1];
                poly[j*3+2] += orig[2];
            }

            // Store detail submesh.
            int ntris = tris.size()/4;

            dmesh.meshes[i*4+0] = (int)dmesh.nverts;
            dmesh.meshes[i*4+1] = (int)nverts[0];
            dmesh.meshes[i*4+2] = (int)dmesh.ntris;
            dmesh.meshes[i*4+3] = (int)ntris;

            // Store vertices, allocate more memory if necessary.
            if (dmesh.nverts+nverts[0] > vcap)
            {
                while (dmesh.nverts+nverts[0] > vcap)
                    vcap += 256;

//                float* newv = (float*)rcAlloc(sizeof(float)*vcap*3, RC_ALLOC_PERM);
                float[] newv = new float[vcap*3];//(float*)rcAlloc(sizeof(float)*vcap*3, RC_ALLOC_PERM);
                /*if (!newv)
                {
                    ctx.log(RC_LOG_ERROR, "rcBuildPolyMeshDetail: Out of memory 'newv' (%d).", vcap*3);
                    return false;
                }*/
                if (dmesh.nverts != 0)        {
//                    memcpy(newv, dmesh.verts, sizeof(float)*3*dmesh.nverts);
                    System.arraycopy(dmesh.verts, 0, newv, 0, 3*dmesh.nverts);
                }
                dmesh.verts = null;
//                rcFree(dmesh.verts);
                dmesh.verts = newv;
            }
            for (int j = 0; j < nverts[0]; ++j)
            {
                dmesh.verts[dmesh.nverts*3+0] = verts[j*3+0];
                dmesh.verts[dmesh.nverts*3+1] = verts[j*3+1];
                dmesh.verts[dmesh.nverts*3+2] = verts[j*3+2];
                dmesh.nverts++;
            }

            // Store triangles, allocate more memory if necessary.
            if (dmesh.ntris+ntris > tcap)
            {
                while (dmesh.ntris+ntris > tcap)
                    tcap += 256;
                char[] newt = new char[tcap*4];//(unsigned char*)rcAlloc(sizeof(unsigned char)*tcap*4, RC_ALLOC_PERM);
                /*if (!newt)
                {
                    ctx.log(RC_LOG_ERROR, "rcBuildPolyMeshDetail: Out of memory 'newt' (%d).", tcap*4);
                    return false;
                }*/
                if (dmesh.ntris != 0) {
//                    memcpy(newt, dmesh.tris, sizeof(unsigned char)*4*dmesh.ntris);
                    System.arraycopy(dmesh.tris, 0, newt, 0, 4*dmesh.ntris);
                }
                dmesh.tris = null;
//                rcFree(dmesh.tris);
                dmesh.tris = newt;
            }
            for (int j = 0; j < ntris; ++j)
            {
//                int[] t = create3(tris, j*4);
                dmesh.tris[dmesh.ntris*4+0] = (char)tris.m_data[j*4+0];
                dmesh.tris[dmesh.ntris*4+1] = (char)tris.m_data[j*4+1];
                dmesh.tris[dmesh.ntris*4+2] = (char)tris.m_data[j*4+2];
                dmesh.tris[dmesh.ntris*4+3] = getTriFlags(create3(verts, tris.m_data[j*4+0]*3), create3(verts, tris.m_data[j*4+1]*3), create3(verts, tris.m_data[j*4+2]*3), poly, npoly);
                dmesh.ntris++;
            }
        }

        ctx.stopTimer(rcTimerLabel.RC_TIMER_BUILD_POLYMESHDETAIL);

        return true;
    }



    }

    static void getHeightData(rcCompactHeightfield chf,
							  int[] poly, int npoly,
							  int[] verts, int bs,
                              rcHeightPatch hp, rcIntArray stack)
    {
        // Floodfill the heightfield to get 2D height data,
        // starting at vertex locations as seeds.

        // Note: Reads to the compact heightfield are offset by border size (bs)
        // since border size offset is already removed from the polymesh vertices.

        Arrays.fill(hp.data, 0, hp.width*hp.height, (int)0);
//        memset(hp.data, 0, sizeof(unsignedint)*hp.width*hp.height);

        stack.resize(0);

        int offset[] = new int[]
        {
            0,0, -1,-1, 0,-1, 1,-1, 1,0, 1,1, 0,1, -1,1, -1,0
        };

        // Use poly vertices as seed points for the flood fill.
        for (int j = 0; j < npoly; ++j)
        {
            int cx = 0, cz = 0, ci =-1;
            int dmin = RC_UNSET_HEIGHT;
            for (int k = 0; k < 9; ++k)
            {
                int ax = (int)verts[poly[j]*3+0] + offset[k*2+0];
                int ay = (int)verts[poly[j]*3+1];
                int az = (int)verts[poly[j]*3+2] + offset[k*2+1];
                if (ax < hp.xmin || ax >= hp.xmin+hp.width ||
                        az < hp.ymin || az >= hp.ymin+hp.height)
                    continue;

                rcCompactCell c = chf.cells[(ax+bs)+(az+bs)*chf.width];
                for (int i = (int)c.getIndex(), ni = (int)(c.getIndex()+c.getCount()); i < ni; ++i)
                {
                    rcCompactSpan s = chf.spans[i];
                    int d = (int)rcAbs(ay - (int)s.y);
                    if (d < dmin)
                    {
                        cx = ax;
                        cz = az;
                        ci = i;
                        dmin = d;
                    }
                }
            }
            if (ci != -1)
            {
                stack.push(cx);
                stack.push(cz);
                stack.push(ci);
            }
        }

        // Find center of the polygon using flood fill.
        int pcx = 0, pcz = 0;
        for (int j = 0; j < npoly; ++j)
        {
            pcx += (int)verts[poly[j]*3+0];
            pcz += (int)verts[poly[j]*3+2];
        }
        pcx /= npoly;
        pcz /= npoly;

        for (int i = 0; i < stack.size(); i += 3)
        {
            int cx = stack.m_data[i+0];
            int cy = stack.m_data[i+1];
            int idx = cx-hp.xmin+(cy-hp.ymin)*hp.width;
            hp.data[idx] = 1;
        }

        while (stack.size() > 0)
        {
            int ci = stack.pop();
            int cy = stack.pop();
            int cx = stack.pop();

            // Check if close to center of the polygon.
            if (rcAbs(cx-pcx) <= 1 && rcAbs(cy-pcz) <= 1)
            {
                stack.resize(0);
                stack.push(cx);
                stack.push(cy);
                stack.push(ci);
                break;
            }

            rcCompactSpan cs = chf.spans[ci];

            for (int dir = 0; dir < 4; ++dir)
            {
                if (rcGetCon(cs, dir) == RC_NOT_CONNECTED) continue;

                int ax = cx + rcGetDirOffsetX(dir);
                int ay = cy + rcGetDirOffsetY(dir);

                if (ax < hp.xmin || ax >= (hp.xmin+hp.width) ||
                        ay < hp.ymin || ay >= (hp.ymin+hp.height))
                    continue;

                if (hp.data[ax-hp.xmin+(ay-hp.ymin)*hp.width] != 0)
                    continue;

                int ai = (int)chf.cells[(ax+bs)+(ay+bs)*chf.width].getIndex() + rcGetCon(cs, dir);

                int idx = ax-hp.xmin+(ay-hp.ymin)*hp.width;
                hp.data[idx] = 1;

                stack.push(ax);
                stack.push(ay);
                stack.push(ai);
            }
        }

        Arrays.fill(hp.data, 0, hp.width*hp.height, (int)0xff);
//        memset(hp.data, 0xff, sizeof(unsigned int)*hp.width*hp.height);

        // Mark start locations.
        for (int i = 0; i < stack.size(); i += 3)
        {
            int cx = stack.m_data[i+0];
            int cy = stack.m_data[i+1];
            int ci = stack.m_data[i+2];
            int idx = cx-hp.xmin+(cy-hp.ymin)*hp.width;
            rcCompactSpan cs = chf.spans[ci];
            hp.data[idx] = cs.y;
        }

        int RETRACT_SIZE = 256;
        int head = 0;

        while (head*3 < stack.size())
        {
            int cx = stack.m_data[head*3+0];
            int cy = stack.m_data[head*3+1];
            int ci = stack.m_data[head*3+2];
            head++;
            if (head >= RETRACT_SIZE)
            {
                head = 0;
				if (stack.size() > RETRACT_SIZE*3) {
//					memmove(&stack[0], &stack[RETRACT_SIZE*3], sizeof(int)*(stack.size()-RETRACT_SIZE*3));
					System.arraycopy(stack.m_data, RETRACT_SIZE*3, stack.m_data, 0, stack.size()-RETRACT_SIZE*3);
				}
				stack.resize(stack.size()-RETRACT_SIZE*3);
                /*if (stack.size() > RETRACT_SIZE*3) {
                    for (int i = 0; i < RETRACT_SIZE*3; i++) {
                        stack.pollFirst();
                    }
                }*/
                /*
                    memmove(&stack[0], &stack[RETRACT_SIZE*3], sizeof(int)*(stack.size()-RETRACT_SIZE*3));
                }
                stack = new LinkedList<>(stack.subList(0, stack.size()-RETRACT_SIZE*3));*/
            }

            rcCompactSpan cs = chf.spans[ci];
            for (int dir = 0; dir < 4; ++dir)
            {
                if (rcGetCon(cs, dir) == RC_NOT_CONNECTED) continue;

                int ax = cx + rcGetDirOffsetX(dir);
                int ay = cy + rcGetDirOffsetY(dir);

                if (ax < hp.xmin || ax >= (hp.xmin+hp.width) ||
                        ay < hp.ymin || ay >= (hp.ymin+hp.height))
                    continue;

                if (hp.data[ax-hp.xmin+(ay-hp.ymin)*hp.width] != RC_UNSET_HEIGHT)
                    continue;

                int ai = (int)chf.cells[(ax+bs)+(ay+bs)*chf.width].getIndex() + rcGetCon(cs, dir);

                rcCompactSpan as = chf.spans[ai];
                int idx = ax-hp.xmin+(ay-hp.ymin)*hp.width;
                hp.data[idx] = as.y;

                stack.push(ax);
                stack.push(ay);
                stack.push(ai);
            }
        }

    }

    public static class rcHeightPatch
    {
        //            inline rcHeightPatch() : data(0), xmin(0), ymin(0), width(0), height(0) {}
//            inline ~rcHeightPatch() { rcFree(data); }
        public int[] data;
        int xmin, ymin, width, height;
    }

    public static boolean buildPolyDetail(rcContext ctx, float[] in, int nin,
                                float sampleDist, float sampleMaxError,
                                rcCompactHeightfield chf, rcHeightPatch hp,
                                float[] verts, int[] nverts, rcIntArray tris,
								rcIntArray edges, rcIntArray samples)
    {
        int MAX_VERTS = 127;
        int MAX_TRIS = 255;	// Max tris for delaunay is 2n-2-k (n=num verts, k=num hull verts).
        int MAX_VERTS_PER_EDGE = 32;
        float edge[] = new float[(MAX_VERTS_PER_EDGE+1)*3];
        int hull[] = new int[MAX_VERTS];
        int nhull = 0;

        nverts[0] = 0;

        for (int i = 0; i < nin; ++i) {
//            rcVcopy(&verts[i*3], &in[i*3]);
            verts[i*3+0] = in[i*3+0];
            verts[i*3+1] = in[i*3+1];
            verts[i*3+2] = in[i*3+2];
        }
        nverts[0] = nin;

        float cs = chf.cs;
        float ics = 1.0f/cs;

        // Tessellate outlines.
        // This is done in separate pass in order to ensure
        // seamless height values across the ply boundaries.
        if (sampleDist > 0)
        {
            for (int i = 0, j = nin-1; i < nin; j=i++)
            {
                float[] vj = create3(in, j*3);
                float[] vi = create3(in, i*3);
                boolean swapped = false;
                // Make sure the segments are always handled in same order
                // using lexological sort or else there will be seams.
                if (Math.abs(vj[0]-vi[0]) < 1e-6f)
                {
                    if (vj[2] > vi[2])
                    {
//                        rcSwap(vj,vi);
                        float[] tmp = vj;
                        vi = vj;
                        vj = tmp;
                        swapped = true;
                    }
                }
                else
                {
                    if (vj[0] > vi[0])
                    {
//                        rcSwap(vj,vi);
                        float[] tmp = vj;
                        vi = vj;
                        vj = tmp;
                        swapped = true;
                    }
                }
                // Create samples along the edge.
                float dx = vi[0] - vj[0];
                float dy = vi[1] - vj[1];
                float dz = vi[2] - vj[2];
                float d = (float)Math.sqrt(dx*dx + dz*dz);
                int nn = 1 + (int)Math.floor(d/sampleDist);
                if (nn >= MAX_VERTS_PER_EDGE) nn = MAX_VERTS_PER_EDGE-1;
                if (nverts[0]+nn >= MAX_VERTS)
                    nn = MAX_VERTS-1-nverts[0];

                for (int k = 0; k <= nn; ++k)
                {
                    float u = (float)k/(float)nn;
                    float[] pos = create3(edge, k*3);
                    pos[0] = vj[0] + dx*u;
                    pos[1] = vj[1] + dy*u;
                    pos[2] = vj[2] + dz*u;
                    pos[1] = getHeight(pos[0],pos[1],pos[2], cs, ics, chf.ch, hp)*chf.ch;
                }
                // Simplify samples.
                int idx[] = new int[MAX_VERTS_PER_EDGE];
                idx[0] = 0;
                idx[1] = nn;
                int nidx = 2;
                for (int k = 0; k < nidx-1; )
                {
                    int a = idx[k];
                    int b = idx[k+1];
                    float[] va = create3(edge, a*3);
                    float[] vb = create3(edge, b*3);
                    // Find maximum deviation along the segment.
                    float maxd = 0;
                    int maxi = -1;
                    for (int m = a+1; m < b; ++m)
                    {
                        float dev = distancePtSeg(create3(edge, m*3),va,vb);
                        if (dev > maxd)
                        {
                            maxd = dev;
                            maxi = m;
                        }
                    }
                    // If the max deviation is larger than accepted error,
                    // add new point, else continue to next segment.
                    if (maxi != -1 && maxd > rcSqr(sampleMaxError))
                    {
                        for (int m = nidx; m > k; --m)
                            idx[m] = idx[m-1];
                        idx[k+1] = maxi;
                        nidx++;
                    }
                    else
                    {
                        ++k;
                    }
                }

                hull[nhull++] = j;
                // Add new vertices.
                if (swapped)
                {
                    for (int k = nidx-2; k > 0; --k)
                    {
//                        rcVcopy(&verts[nverts*3], &edge[idx[k]*3]);
                        verts[nverts[0]*3+0] = edge[idx[k]*3+0];
                        verts[nverts[0]*3+1] = edge[idx[k]*3+1];
                        verts[nverts[0]*3+2] = edge[idx[k]*3+2];
                        hull[nhull++] = nverts[0];
                        nverts[0]++;
                    }
                }
                else
                {
                    for (int k = 1; k < nidx-1; ++k)
                    {
//                        rcVcopy(&verts[nverts[0]*3], &edge[idx[k]*3]);
                        verts[nverts[0]*3+0] = edge[idx[k]*3+0];
                        verts[nverts[0]*3+1] = edge[idx[k]*3+1];
                        verts[nverts[0]*3+2] = edge[idx[k]*3+2];
                        hull[nhull++] = nverts[0];
                        nverts[0]++;
                    }
                }
            }
        }


        // Tessellate the base mesh.
        edges.resize(0);
        tris.resize(0);

        delaunayHull(ctx, nverts[0], verts, nhull, hull, tris, edges);

        if (tris.size() == 0)
        {
            // Could not triangulate the poly, make sure there is some valid data there.
            ctx.log(rcLogCategory.RC_LOG_WARNING, "buildPolyDetail: Could not triangulate polygon, adding default data.");
            for (int i = 2; i < nverts[0]; ++i)
            {
                tris.push(0);
                tris.push(i-1);
                tris.push(i);
                tris.push(0);
            }
            return true;
        }

        if (sampleDist > 0)
        {
            // Create sample locations in a grid.
            float bmin[] = new float[3], bmax[] = new float[3];
            rcVcopy(bmin, in);
            rcVcopy(bmax, in);
            for (int i = 1; i < nin; ++i)
            {
                rcVmin(bmin, create3(in, i*3));
                rcVmax(bmax, create3(in, i*3));
            }
            int x0 = (int)Math.floor(bmin[0] / sampleDist);
            int x1 = (int)Math.ceil(bmax[0] / sampleDist);
            int z0 = (int)Math.floor(bmin[2] / sampleDist);
            int z1 = (int)Math.ceil(bmax[2] / sampleDist);
            samples.resize(0);
            for (int z = z0; z < z1; ++z)
            {
                for (int x = x0; x < x1; ++x)
                {
                    float pt[] = new float[3];
                    pt[0] = x*sampleDist;
                    pt[1] = (bmax[1]+bmin[1])*0.5f;
                    pt[2] = z*sampleDist;
                    // Make sure the samples are not too close to the edges.
                    if (distToPoly(nin,in,pt) > -sampleDist/2) continue;
                    samples.push(x);
                    samples.push((int)(getHeight(pt[0], pt[1], pt[2], cs, ics, chf.ch, hp)));
                    samples.push(z);
                    samples.push(0); // Not added
                }
            }

            // Add the samples starting from the one that has the most
            // error. The procedure stops when all samples are added
            // or when the max error is within treshold.
            int nsamples = samples.size()/4;
            for (int iter = 0; iter < nsamples; ++iter)
            {
                if (nverts[0] >= MAX_VERTS)
                    break;

                // Find sample with most error.
                float bestpt[] = new float[]{0,0,0};
                float bestd = 0;
                int besti = -1;
                for (int i = 0; i < nsamples; ++i)
                {
//                    int[] s = &samples[i*4];
                    if (samples.m_data[i*4+3] != 0) continue; // skip added.
                    float pt[] = new float[3];
                    // The sample location is jittered to get rid of some bad triangulations
                    // which are cause by symmetrical data from the grid structure.
                    pt[0] = samples.m_data[i*4+0]*sampleDist + getJitterX(i)*cs*0.1f;
                    pt[1] = samples.m_data[i*4+1]*chf.ch;
                    pt[2] = samples.m_data[i*4+2]*sampleDist + getJitterY(i)*cs*0.1f;
                    float d = distToTriMesh(pt, verts, nverts[0], tris, tris.size()/4);
                    if (d < 0) continue; // did not hit the mesh.
                    if (d > bestd)
                    {
                        bestd = d;
                        besti = i;
                        rcVcopy(bestpt,pt);
                    }
                }
                // If the max error is within accepted threshold, stop tesselating.
                if (bestd <= sampleMaxError || besti == -1)
                    break;
                // Mark sample as added.
                samples.m_data[besti*4+3] = 1;
                // Add the new sample point.
//                rcVcopy(&verts[nverts[0]*3],bestpt);
                verts[nverts[0]*3+0] = bestpt[0];
                verts[nverts[0]*3+1] = bestpt[1];
                verts[nverts[0]*3+2] = bestpt[2];
                nverts[0]++;

                // Create new triangulation.
                // TODO: Incremental add instead of full rebuild.
                edges.resize(0);
                tris.resize(0);
                delaunayHull(ctx, nverts[0], verts, nhull, hull, tris, edges);
            }
        }

        int ntris = tris.size()/4;
        if (ntris > MAX_TRIS)
        {
//            tris.resize(MAX_TRIS*4);
            ctx.log(rcLogCategory.RC_LOG_ERROR, "rcBuildPolyMeshDetail: Shrinking triangle count from %d to max %d.", ntris, MAX_TRIS);
        }

        return true;
    }

    public static char getTriFlags(float[] va, float[] vb, float[] vc,
                                     float[] vpoly, int npoly)
    {
        char flags = 0;
        flags |= getEdgeFlags(va,vb,vpoly,npoly) << 0;
        flags |= getEdgeFlags(vb,vc,vpoly,npoly) << 2;
        flags |= getEdgeFlags(vc,va,vpoly,npoly) << 4;
        return flags;
    }

    public static char getEdgeFlags(float[] va, float[] vb,
                                      float[] vpoly, int npoly)
    {
        // Return true if edge (va,vb) is part of the polygon.
        float thrSqr = rcSqr(0.001f);
        for (int i = 0, j = npoly-1; i < npoly; j=i++)
        {
            if (distancePtSeg2d(va, create3(vpoly, j*3), create3(vpoly, i*3)) < thrSqr &&
                distancePtSeg2d(vb, create3(vpoly, j*3), create3(vpoly, i*3)) < thrSqr)
            return 1;
        }
        return 0;
    }

    public static float distancePtSeg2d(float[] pt, float[] p, float[] q)
    {
        float pqx = q[0] - p[0];
        float pqz = q[2] - p[2];
        float dx = pt[0] - p[0];
        float dz = pt[2] - p[2];
        float d = pqx*pqx + pqz*pqz;
        float t = pqx*dx + pqz*dz;
        if (d > 0)
            t /= d;
        if (t < 0)
            t = 0;
        else if (t > 1)
            t = 1;

        dx = p[0] + t*pqx - pt[0];
        dz = p[2] + t*pqz - pt[2];

        return dx*dx + dz*dz;
    }

    static float distancePtSeg(float[] pt, float[] p, float[] q)
    {
        float pqx = q[0] - p[0];
        float pqy = q[1] - p[1];
        float pqz = q[2] - p[2];
        float dx = pt[0] - p[0];
        float dy = pt[1] - p[1];
        float dz = pt[2] - p[2];
        float d = pqx*pqx + pqy*pqy + pqz*pqz;
        float t = pqx*dx + pqy*dy + pqz*dz;
        if (d > 0)
            t /= d;
        if (t < 0)
            t = 0;
        else if (t > 1)
            t = 1;

        dx = p[0] + t*pqx - pt[0];
        dy = p[1] + t*pqy - pt[1];
        dz = p[2] + t*pqz - pt[2];

        return dx*dx + dy*dy + dz*dz;
    }

    public static void delaunayHull(rcContext ctx, int npts, float[] pts,
                             int nhull, int[] hull,
							 rcIntArray tris, rcIntArray edges)
    {
//        int[] edges = new int[listEdges.size()];
//        int index = 0;
//        for (Integer edge1 : listEdges) {
//            edges[index] = edge1;
//            index++;
//        }
//        int[] tris = new int[listTris.size()];
//        index = 0;
//        for (Integer tris1 : listTris) {
//            tris[index] = tris1;
//            index++;
//        }
//        listEdges.toArray(new int[listEdges.size()]);
        int nfaces[] = new int[]{0};
        int nedges[] = new int[]{0};
//        int maxEdges = npts*10;
//        edges.resize(maxEdges*4);
//		int nfaces = 0;
//		int nedges = 0;
		int maxEdges = npts*10;
		edges.resize(maxEdges*4);

        for (int i = 0, j = nhull-1; i < nhull; j=i++)
            addEdge(ctx, edges.m_data, nedges, maxEdges, hull[j],hull[i], EdgeValues.HULL.v, EdgeValues.UNDEF.v);

        int currentEdge = 0;
        while (currentEdge < nedges[0])
        {
            if (edges.m_data[currentEdge*4+2] == EdgeValues.UNDEF.v)
                completeFacet(ctx, pts, npts, edges.m_data, nedges, maxEdges, nfaces, currentEdge);
            if (edges.m_data[currentEdge*4+3] == EdgeValues.UNDEF.v)
                completeFacet(ctx, pts, npts, edges.m_data, nedges, maxEdges, nfaces, currentEdge);
            currentEdge++;
        }

        // Create tris
		tris.resize(nfaces[0]*4);
			for (int i = 0; i < nfaces[0]*4; ++i)
				tris.m_data[i] = -1;

        for (int i = 0; i < nedges[0]; ++i)
        {
//            int* e = &edges[i*4];
            if (edges.m_data[i*4+3] >= 0)
            {
                // Left face
//                int[] t = &tris[edges[i*4+3]*4];
                if (tris.get(edges.m_data[i*4+3]*4+0) == -1)
                {
                    tris.set(edges.m_data[i*4+3]*4+0, edges.m_data[i*4+0]);
                    tris.set(edges.m_data[i*4+3]*4+1, edges.m_data[i*4+1]);
                }
                else if (tris.get(edges.m_data[i*4+3]*4+0) == edges.m_data[i*4+1])
                    tris.set(edges.m_data[i*4+3]*4+2, edges.m_data[i*4+0]);
                else if (tris.get(edges.m_data[i*4+3]*4+1) == edges.m_data[i*4+0])
                    tris.set(edges.m_data[i*4+3]*4+2, edges.m_data[i*4+1]);
            }
            if (edges.m_data[i*4+2] >= 0)
            {
                // Right
//                int[] t = &tris[edges[i*4+2]*4];
                if (tris.get(edges.m_data[i*4+2]*4+0) == -1)
                {
                    tris.set(edges.m_data[i*4+2]*4+0, edges.m_data[i*4+1]);
                    tris.set(edges.m_data[i*4+2]*4+1, edges.m_data[i*4+0]);
                }
                else if (tris.get(edges.m_data[i*4+2]*4+0) == edges.m_data[i*4+0])
                    tris.set(edges.m_data[i * 4 + 2] * 4 + 2, edges.m_data[i * 4 + 1]);
                else if (tris.get(edges.m_data[i*4+2]*4+1) == edges.m_data[i*4+1])
                    tris.set(edges.m_data[i*4+2]*4+2,  edges.m_data[i*4+0]);
            }
        }

        for (int i = 0; i < tris.size()/4; ++i)
        {
//            int* t = &tris.get(i*4);
            if (tris.get(i*4+0) == -1 || tris.get(i*4+1) == -1 || tris.get(i*4+2) == -1)
            {
                ctx.log(rcLogCategory.RC_LOG_WARNING, "delaunayHull: Removing dangling face %d [%d,%d,%d].", i, tris.get(i*4+0),tris.get(i*4+1),tris.get(i*4+2));
                tris.set(i * 4 + 0, tris.get(tris.size() - 4));
                tris.set(i * 4 + 1, tris.get(tris.size() - 3));
                tris.set(i * 4 + 2, tris.get(tris.size() - 2));
                tris.set(i * 4 + 3, tris.get(tris.size() - 1));
				tris.resize(tris.size()-4);
                --i;
            }
        }
    }

    public static int addEdge(rcContext ctx, int[] edges, int[] nedges, int maxEdges, int s, int t, int l, int r)
    {
        if (nedges[0] >= maxEdges)
        {
            ctx.log(rcLogCategory.RC_LOG_ERROR, "addEdge: Too many edges (%d/%d).", nedges, maxEdges);
            return EdgeValues.UNDEF.v;
        }

        // Add edge if not already in the triangulation.
        int e = findEdge(edges, nedges[0], s, t);
        if (e == EdgeValues.UNDEF.v)
        {
//            int* edge = &edges[nedges*4];
            edges[nedges[0]*4+0] = s;
            edges[nedges[0]*4+1] = t;
            edges[nedges[0]*4+2] = l;
            edges[nedges[0]*4+3] = r;
            return nedges[0]++;
        }
        else
        {
            return EdgeValues.UNDEF.v;
        }
    }

    public static enum EdgeValues
    {
        UNDEF(-1),
        HULL(-2);
        int v;

        EdgeValues(int v) {
            this.v = v;
        }
    }

    static int findEdge(int[] edges, int nedges, int s, int t)
    {
        for (int i = 0; i < nedges; i++)
        {
//            int* e = &edges[i*4];
            if ((edges[i*4+0] == s && edges[i*4+1] == t) || (edges[i*4+0] == t && edges[i*4+1] == s))
                return i;
        }
        return EdgeValues.UNDEF.v;
    }

    static void completeFacet(rcContext ctx, float[] pts, int npts, int[] edges, int[] nedges, int maxEdges, int[] nfaces, int e)
    {
        float EPS = 1e-5f;

        int[] edge = createN(edges, e*4, 4);

        // Cache s and t.
        int s,t;
        if (edge[2] == EdgeValues.UNDEF.v)
        {
            s = edge[0];
            t = edge[1];
        }
        else if (edge[3] == EdgeValues.UNDEF.v)
        {
            s = edge[1];
            t = edge[0];
        }
        else
        {
            // Edge already completed.
            return;
        }

        // Find best point on left of edge.
        int pt = npts;
        float c[] = {0,0,0};
        float r[] = new float[]{-1};
        for (int u = 0; u < npts; ++u)
        {
            if (u == s || u == t) continue;
            if (vcross2(create3(pts, s*3), create3(pts, t*3), create3(pts, u*3)) > EPS)
            {
                if (r[0] < 0)
                {
                    // The circle is not updated yet, do it now.
                    pt = u;
                    circumCircle(create3(pts, s*3), create3(pts, t*3), create3(pts, u*3), c, r);
                    continue;
                }
                float d = vdist2(c, create3(pts, u*3));
                float tol = 0.001f;
                if (d > r[0]*(1+tol))
                {
                    // Outside current circumcircle, skip.
                    continue;
                }
                else if (d < r[0]*(1-tol))
                {
                    // Inside safe circumcircle, update circle.
                    pt = u;
                    circumCircle(create3(pts, s*3), create3(pts, t*3), create3(pts, u*3), c, r);
                }
                else
                {
                    // Inside epsilon circum circle, do extra tests to make sure the edge is valid.
                    // s-u and t-u cannot overlap with s-pt nor t-pt if they exists.
                    if (overlapEdges(pts, edges, nedges[0], s,u))
                        continue;
                    if (overlapEdges(pts, edges, nedges[0], t,u))
                        continue;
                    // Edge is valid.
                    pt = u;
                    circumCircle(create3(pts, s*3), create3(pts, t*3), create3(pts, u*3), c, r);
                }
            }
        }

        // Add new triangle or update edge info if s-t is on hull.
        if (pt < npts)
        {
            // Update face information of edge being completed.
            updateLeftFace(edges, e*4, s, t, nfaces[0]);

            // Add new edge or update face info of old edge.
            e = findEdge(edges, nedges[0], pt, s);
            if (e == EdgeValues.UNDEF.v)
                addEdge(ctx, edges, nedges, maxEdges, pt, s, nfaces[0], EdgeValues.UNDEF.v);
            else {
                updateLeftFace(edges, e*4, pt, s, nfaces[0]);
            }

            // Add new edge or update face info of old edge.
            e = findEdge(edges, nedges[0], t, pt);
            if (e == EdgeValues.UNDEF.v)
                addEdge(ctx, edges, nedges, maxEdges, t, pt, nfaces[0], EdgeValues.UNDEF.v);
            else
                updateLeftFace(edges, e*4, t, pt, nfaces[0]);

            nfaces[0]++;
        }
        else
        {
            updateLeftFace(edges, e*4, s, t, EdgeValues.HULL.v);
        }
    }


    static boolean circumCircle(float[] p1, float[] p2, float[] p3,
                             float[] c, float[] r)
    {
        float EPS = 1e-6f;

        float cp = vcross2(p1, p2, p3);
        if (Math.abs(cp) > EPS)
        {
            float p1Sq = vdot2(p1,p1);
            float p2Sq = vdot2(p2,p2);
            float p3Sq = vdot2(p3,p3);
            c[0] = (p1Sq*(p2[2]-p3[2]) + p2Sq*(p3[2]-p1[2]) + p3Sq*(p1[2]-p2[2])) / (2*cp);
            c[2] = (p1Sq*(p3[0]-p2[0]) + p2Sq*(p1[0]-p3[0]) + p3Sq*(p2[0]-p1[0])) / (2*cp);
            r[0] = vdist2(c, p1);
            return true;
        }

        c[0] = p1[0];
        c[2] = p1[2];
        r[0] = 0;
        return false;
    }

    static void updateLeftFace(int[] edges, int index, int s, int t, int f)
    {
        if (edges[index+0] == s && edges[index+1] == t && edges[index+2] == EdgeValues.UNDEF.v)
            edges[index+2] = f;
        else if (edges[index+1] == s && edges[index+0] == t && edges[index+3] == EdgeValues.UNDEF.v)
            edges[index+3] = f;
    }


    public static float vdot2(float[] a, float[] b)
    {
        return a[0]*b[0] + a[2]*b[2];
    }

    public static float vdistSq2(float[] p, float[] q)
    {
        float dx = q[0] - p[0];
        float dy = q[2] - p[2];
        return dx*dx + dy*dy;
    }

    public static float vdist2(float[] p, float[]q)
    {
        return (float)Math.sqrt(vdistSq2(p,q));
    }

    public static float vcross2(float[] p1, float[] p2, float[] p3)
    {
        float u1 = p2[0] - p1[0];
        float v1 = p2[2] - p1[2];
        float u2 = p3[0] - p1[0];
        float v2 = p3[2] - p1[2];
        return u1 * v2 - v1 * u2;
    }

    public static boolean overlapEdges(float[] pts, int[] edges, int nedges, int s1, int t1)
    {
        for (int i = 0; i < nedges; ++i)
        {
            int s0 = edges[i*4+0];
            int t0 = edges[i*4+1];
            // Same or connected edges do not overlap.
            if (s0 == s1 || s0 == t1 || t0 == s1 || t0 == t1)
                continue;
            if (overlapSegSeg2d(create3(pts, s0*3), create3(pts, t0*3), create3(pts, s1*3), create3(pts, t1*3)) != 0)
            return true;
        }
        return false;
    }

    static int overlapSegSeg2d(float[] a, float[] b, float[] c, float[] d)
    {
        float a1 = vcross2(a, b, d);
        float a2 = vcross2(a, b, c);
        if (a1*a2 < 0.0f)
        {
            float a3 = vcross2(c, d, a);
            float a4 = a3 + a2 - a1;
            if (a3 * a4 < 0.0f)
                return 1;
        }
        return 0;
    }

    static float distToTriMesh(float[] p, float[] verts, int nverts, rcIntArray tris, int ntris)
    {
        float dmin = Float.MAX_VALUE;
        for (int i = 0; i < ntris; ++i)
        {
            float[] va = create3(verts, tris.m_data[i*4+0]*3);
            float[] vb = create3(verts, tris.m_data[i*4+1]*3);
            float[] vc = create3(verts, tris.m_data[i*4+2]*3);
            float d = distPtTri(p, va,vb,vc);
            if (d < dmin)
                dmin = d;
        }
        if (dmin == Float.MAX_VALUE) return -1;
        return dmin;
    }

    static float distPtTri(float[] p, float[] a, float[] b, float[] c)
    {
        float v0[] = new float[3], v1[] = new float[3], v2[] = new float[3];
        rcVsub(v0, c,a);
        rcVsub(v1, b,a);
        rcVsub(v2, p,a);

        float dot00 = vdot2(v0, v0);
        float dot01 = vdot2(v0, v1);
        float dot02 = vdot2(v0, v2);
        float dot11 = vdot2(v1, v1);
        float dot12 = vdot2(v1, v2);

        // Compute barycentric coordinates
        float invDenom = 1.0f / (dot00 * dot11 - dot01 * dot01);
        float u = (dot11 * dot02 - dot01 * dot12) * invDenom;
        float v = (dot00 * dot12 - dot01 * dot02) * invDenom;

        // If point lies inside the triangle, return interpolated y-coord.
        float EPS = 1e-4f;
        if (u >= -EPS && v >= -EPS && (u+v) <= 1+EPS)
        {
            float y = a[1] + v0[1]*u + v1[1]*v;
            return Math.abs(y - p[1]);
        }
        return Float.MAX_VALUE;
    }


    static int getHeight(float fx, float fy, float fz,
                                    float cs, float ics, float ch,
                                    rcHeightPatch hp)
    {
        int ix = (int)Math.floor(fx * ics + 0.01f);
        int iz = (int)Math.floor(fz * ics + 0.01f);
        ix = rcClamp(ix-hp.xmin, 0, hp.width);
        iz = rcClamp(iz-hp.ymin, 0, hp.height);
        int h = hp.data[ix+iz*hp.width];
        if (h == RC_UNSET_HEIGHT)
        {
            // Special case when data might be bad.
            // Find nearest neighbour pixel which has valid height.
            int[] off = new int[]{ -1,0, -1,-1, 0,-1, 1,-1, 1,0, 1,1, 0,1, -1,1};
//            int[] off = new int[8*2]{1};
            float dmin = Float.MAX_VALUE;
            for (int i = 0; i < 8; ++i)
            {
                int nx = ix+off[i*2+0];
                int nz = iz+off[i*2+1];
                if (nx < 0 || nz < 0 || nx >= hp.width || nz >= hp.height) continue;
                int nh = hp.data[nx+nz*hp.width];
                if (nh == RC_UNSET_HEIGHT) continue;

                float d = Math.abs(nh * ch - fy);
                if (d < dmin)
                {
                    h = nh;
                    dmin = d;
                }

/*			const float dx = (nx+0.5f)*cs - fx;
			const float dz = (nz+0.5f)*cs - fz;
			const float d = dx*dx+dz*dz;
			if (d < dmin)
			{
				h = nh;
				dmin = d;
			} */
            }
        }
        return h;
    }

    static float distToPoly(int nvert, float[] verts, float[] p)
    {

        float dmin = Float.MAX_VALUE;
        int i, j;
        boolean c = false;
        for (i = 0, j = nvert-1; i < nvert; j = i++)
        {
            float[] vi = create3(verts, i*3);
            float[] vj = create3(verts, j*3);
            if (((vi[2] > p[2]) != (vj[2] > p[2])) &&
                    (p[0] < (vj[0]-vi[0]) * (p[2]-vi[2]) / (vj[2]-vi[2]) + vi[0]) )
                c = !c;
            dmin = rcMin(dmin, distancePtSeg2d(p, vj, vi));
        }
        return c ? -dmin : dmin;
    }


    public static float getJitterX(int i)
    {
        return (((i * 0x8da6b343) & 0xffff) / 65535.0f * 2.0f) - 1.0f;
    }

    public static float getJitterY(int i)
    {
        return (((i * 0xd8163841) & 0xffff) / 65535.0f * 2.0f) - 1.0f;
    }

    public static final int RC_UNSET_HEIGHT = 0xffff;
}