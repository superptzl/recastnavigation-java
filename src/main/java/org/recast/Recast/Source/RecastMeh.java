package org.recast.Recast.Source;

import org.recast.Recast.Include.*;

import java.util.Arrays;

public class RecastMeh {
    public boolean rcBuildPolyMesh(rcContext ctx, rcContourSet cset, int nvp, rcPolyMesh mesh)
    {
//            rcAssert(ctx);

        ctx.startTimer(rcTimerLabel.RC_TIMER_BUILD_POLYMESH);

        Recast.rcVcopy(mesh.bmin, cset.bmin);
        Recast.rcVcopy(mesh.bmax, cset.bmax);
        mesh.cs = cset.cs;
        mesh.ch = cset.ch;
        mesh.borderSize = cset.borderSize;

        int maxVertices = 0;
        int maxTris = 0;
        int maxVertsPerCont = 0;
        for (int i = 0; i < cset.nconts; ++i)
        {
            // Skip null contours.
            if (cset.conts[i].nverts < 3) continue;
            maxVertices += cset.conts[i].nverts;
            maxTris += cset.conts[i].nverts - 2;
            maxVertsPerCont = Recast.rcMax(maxVertsPerCont, cset.conts[i].nverts);
        }

        if (maxVertices >= 0xfffe)
        {
            ctx.log(rcLogCategory.RC_LOG_ERROR, "rcBuildPolyMesh: Too many vertices %d.", maxVertices);
            return false;
        }

//            rcScopedDelete<unsigned char> vflags = (unsigned char*)rcAlloc(sizeof(unsigned char)*maxVertices, RC_ALLOC_TEMP);
        char[] vflags = new char[maxVertices];
        /*if (!vflags)
        {
            ctx.log(RC_LOG_ERROR, "rcBuildPolyMesh: Out of memory 'vflags' (%d).", maxVertices);
            return false;
        }*/
//            memset(vflags, 0, maxVertices);

//            mesh.verts = (unsigned short*)rcAlloc(sizeof(unsigned short)*maxVertices*3, RC_ALLOC_PERM);
        mesh.verts = new short[maxVertices*3];//(unsigned short*)rcAlloc(sizeof(unsigned short)*maxVertices*3, RC_ALLOC_PERM);
        /*if (!mesh.verts)
        {
            ctx.log(RC_LOG_ERROR, "rcBuildPolyMesh: Out of memory 'mesh.verts' (%d).", maxVertices);
            return false;
        }*/
//            mesh.polys = (unsigned short*)rcAlloc(sizeof(unsigned short)*maxTris*nvp*2, RC_ALLOC_PERM);
        mesh.polys = new short[maxTris*nvp*2];//(unsigned short*)rcAlloc(sizeof(unsigned short)*maxTris*nvp*2, RC_ALLOC_PERM);
        /*if (!mesh.polys)
        {
            ctx.log(RC_LOG_ERROR, "rcBuildPolyMesh: Out of memory 'mesh.polys' (%d).", maxTris*nvp*2);
            return false;
        }*/
//            mesh.regs = (unsigned short*)rcAlloc(sizeof(unsigned short)*maxTris, RC_ALLOC_PERM);
        mesh.regs = new short[maxTris];//(unsigned short*)rcAlloc(sizeof(unsigned short)*maxTris, RC_ALLOC_PERM);
        /*if (!mesh.regs)
        {
            ctx.log(RC_LOG_ERROR, "rcBuildPolyMesh: Out of memory 'mesh.regs' (%d).", maxTris);
            return false;
        }*/
//            mesh.areas = (unsigned char*)rcAlloc(sizeof(unsigned char)*maxTris, RC_ALLOC_PERM);
        mesh.areas = new char[maxTris];//(unsigned char*)rcAlloc(sizeof(unsigned char)*maxTris, RC_ALLOC_PERM);
        /*if (!mesh.areas)
        {
            ctx.log(RC_LOG_ERROR, "rcBuildPolyMesh: Out of memory 'mesh.areas' (%d).", maxTris);
            return false;
        }*/

        mesh.nverts = 0;
        mesh.npolys = 0;
        mesh.nvp = nvp;
        mesh.maxpolys = maxTris;

//            memset(mesh.verts, 0, sizeof(unsigned short)*maxVertices*3);
//            memset(mesh.polys, 0xff, sizeof(unsigned short)*maxTris*nvp*2);
//            memset(mesh.regs, 0, sizeof(unsigned short)*maxTris);
//            memset(mesh.areas, 0, sizeof(unsigned char)*maxTris);

//            rcScopedDelete<int> nextVert = (int*)rcAlloc(sizeof(int)*maxVertices, RC_ALLOC_TEMP);
        int[] nextVert = new int[maxVertices];
        /*if (!nextVert)
        {
            ctx.log(RC_LOG_ERROR, "rcBuildPolyMesh: Out of memory 'nextVert' (%d).", maxVertices);
            return false;
        }*/
//            memset(nextVert, 0, sizeof(int)*maxVertices);

//            rcScopedDelete<int> firstVert = (int*)rcAlloc(sizeof(int)*VERTEX_BUCKET_COUNT, RC_ALLOC_TEMP);
        int[] firstVert = new int[VERTEX_BUCKET_COUNT];
        /*if (!firstVert)
        {
            ctx.log(RC_LOG_ERROR, "rcBuildPolyMesh: Out of memory 'firstVert' (%d).", VERTEX_BUCKET_COUNT);
            return false;
        }*/
        for (int i = 0; i < VERTEX_BUCKET_COUNT; ++i)
            firstVert[i] = -1;

//            rcScopedDelete<int> indices = (int*)rcAlloc(sizeof(int)*maxVertsPerCont, RC_ALLOC_TEMP);
        int[]indices = new int[maxVertsPerCont];
        /*if (!indices)
        {
            ctx.log(RC_LOG_ERROR, "rcBuildPolyMesh: Out of memory 'indices' (%d).", maxVertsPerCont);
            return false;
        }*/
//            rcScopedDelete<int> tris = (int*)rcAlloc(sizeof(int)*maxVertsPerCont*3, RC_ALLOC_TEMP);
        int[] tris = new int[maxVertsPerCont*3];
        /*if (!tris)
        {
            ctx.log(RC_LOG_ERROR, "rcBuildPolyMesh: Out of memory 'tris' (%d).", maxVertsPerCont*3);
            return false;
        }*/
//            rcScopedDelete<unsigned short> polys = (unsigned short*)rcAlloc(sizeof(unsigned short)*(maxVertsPerCont+1)*nvp, RC_ALLOC_TEMP);
        short[] polys = new short[(maxVertsPerCont+1)*nvp];
        /*if (!polys)
        {
            ctx.log(RC_LOG_ERROR, "rcBuildPolyMesh: Out of memory 'polys' (%d).", maxVertsPerCont*nvp);
            return false;
        }*/
        short[] tmpPoly = Recast.createN(polys, maxVertsPerCont * nvp, nvp);

        for (int i = 0; i < cset.nconts; ++i)
        {
            rcContour cont = cset.conts[i];

            // Skip null contours.
            if (cont.nverts < 3)
                continue;

            // Triangulate contour
            for (int j = 0; j < cont.nverts; ++j)
                indices[j] = j;

            int ntris = triangulate(cont.nverts, cont.verts, indices, tris);
            if (ntris <= 0)
            {
                // Bad triangulation, should not happen.
/*			printf("\tconst float bmin[3] = {%ff,%ff,%ff};\n", cset.bmin[0], cset.bmin[1], cset.bmin[2]);
        printf("\tconst float cs = %ff;\n", cset.cs);
        printf("\tconst float ch = %ff;\n", cset.ch);
        printf("\tconst int verts[] = {\n");
        for (int k = 0; k < cont.nverts; ++k)
        {
            const int* v = &cont.verts[k*4];
            printf("\t\t%d,%d,%d,%d,\n", v[0], v[1], v[2], v[3]);
        }
        printf("\t};\n\tconst int nverts = sizeof(verts)/(sizeof(int)*4);\n");*/
                ctx.log(rcLogCategory.RC_LOG_WARNING, "rcBuildPolyMesh: Bad triangulation Contour %d.", i);
                ntris = -ntris;
            }

            // Add and merge vertices.
            for (int j = 0; j < cont.nverts; ++j)
            {
//                    int[] v = cont.verts[j*4];

                int tmp[] = new int[]{mesh.nverts};

                indices[j] = addVertex((short)cont.verts[j*4+0], (short)cont.verts[j*4+1], (short)cont.verts[j*4+2],
                    mesh.verts, firstVert, nextVert, tmp);
                mesh.nverts = tmp[0];
                if ((cont.verts[j*4+3] & Recast.RC_BORDER_VERTEX) != 0)
                {
                    // This vertex should be removed.
                    vflags[indices[j]] = 1;
                }
            }

            // Build initial polygons.
            int npolys = 0;
//                memset(polys, 0xff, maxVertsPerCont*nvp*sizeof(unsigned short));
            for (int zz = 0; zz < maxVertsPerCont*nvp; zz++) {
                polys[zz] = 0xff;
            }
            for (int j = 0; j < ntris; ++j)
            {
//                    int* t = tris[j*3];
                if (tris[j*3+0] != tris[j*3+1] && tris[j*3+0] != tris[j*3+2] && tris[j*3+1] != tris[j*3+2])
                {
                    polys[npolys*nvp+0] = (short)indices[tris[j*3+0]];
                    polys[npolys*nvp+1] = (short)indices[tris[j*3+1]];
                    polys[npolys*nvp+2] = (short)indices[tris[j*3+2]];
                    npolys++;
                }
            }
            if (npolys == 0)
                continue;

            // Merge polygons.
            if (nvp > 3)
            {
                for(;;)
                {
                    // Find best polygons to merge.
                    int bestMergeVal = 0;
                    int bestPa = 0, bestPb = 0, bestEa = 0, bestEb = 0;

                    for (int j = 0; j < npolys-1; ++j)
                    {
                        short[] pj = Recast.create3(polys, j * nvp);
                        for (int k = j+1; k < npolys; ++k)
                        {
                            short[] pk = Recast.create3(polys, k * nvp);
                            int ea[] = new int[0], eb[] = new int[0];
                            int v = getPolyMergeValue(pj, pk, mesh.verts, ea, eb, nvp);
                            if (v > bestMergeVal)
                            {
                                bestMergeVal = v;
                                bestPa = j;
                                bestPb = k;
                                bestEa = ea[0];
                                bestEb = eb[0];
                            }
                        }
                    }

                    if (bestMergeVal > 0)
                    {
                        // Found best, merge.
                        short[] pa = Recast.createN(polys, bestPa * nvp, nvp);
                        short[] pb = Recast.createN(polys, bestPb * nvp, nvp);
                        mergePolys(pa, pb, bestEa, bestEb, tmpPoly, nvp);
                        System.arraycopy(pa, 0, polys, bestPa*nvp*nvp, nvp);
                        System.arraycopy(pb, 0, polys, bestPb*nvp*nvp, nvp);
//                            short[] tmpPoly = createN(polys, maxVertsPerCont*nvp, nvp);
                        System.arraycopy(tmpPoly, 0, polys, maxVertsPerCont*nvp, nvp);

//                            memcpy(pb, &polys[(npolys-1)*nvp], sizeof(unsigned short)*nvp);
                        System.arraycopy(polys, (npolys-1)*nvp, pb, 0, nvp);
                        npolys--;
                    }
                    else
                    {
                        // Could not merge any polygons, stop.
                        break;
                    }
                }
            }

            // Store polygons.
            for (int j = 0; j < npolys; ++j)
            {
//                    short[] p = createN(mesh.polys, mesh.npolys*nvp*2, nvp);
//                    short[] q = createN(polys[j*nvp];
                for (int k = 0; k < nvp; ++k)
                    mesh.polys[mesh.npolys*nvp*2 + k] = polys[j*nvp + k];
                mesh.regs[mesh.npolys] = cont.reg;
                mesh.areas[mesh.npolys] = cont.area;
                mesh.npolys++;
                if (mesh.npolys > maxTris)
                {
                    ctx.log(rcLogCategory.RC_LOG_ERROR, "rcBuildPolyMesh: Too many polygons %d (max:%d).", mesh.npolys, maxTris);
                    return false;
                }
            }
        }


        // Remove edge vertices.
        for (int i = 0; i < mesh.nverts; ++i)
        {
            if (vflags[i] != 0)
            {
                if (!canRemoveVertex(ctx, mesh, (short)i))
                continue;
                if (!removeVertex(ctx, mesh, (short)i, maxTris))
                {
                    // Failed to remove vertex
                    ctx.log(rcLogCategory.RC_LOG_ERROR, "rcBuildPolyMesh: Failed to remove edge vertex %d.", i);
                    return false;
                }
                // Remove vertex
                // Note: mesh.nverts is already decremented inside removeVertex()!
                // Fixup vertex flags
                for (int j = i; j < mesh.nverts; ++j)
                    vflags[j] = vflags[j+1];
                --i;
            }
        }

        // Calculate adjacency.
        if (!buildMeshAdjacency(mesh.polys, mesh.npolys, mesh.nverts, nvp))
        {
            ctx.log(rcLogCategory.RC_LOG_ERROR, "rcBuildPolyMesh: Adjacency failed.");
            return false;
        }

        // Find portal edges
        if (mesh.borderSize > 0)
        {
            int w = cset.width;
            int h = cset.height;
            for (int i = 0; i < mesh.npolys; ++i)
            {
//                    short* p = &mesh.polys[i*2*nvp];
                for (int j = 0; j < nvp; ++j)
                {
                    if (mesh.polys[i*2*nvp+j] == Recast.RC_MESH_NULL_IDX) break;
                    // Skip connected edges.
                    if (mesh.polys[i*2*nvp+nvp+j] != Recast.RC_MESH_NULL_IDX)
                        continue;
                    int nj = j+1;
                    if (nj >= nvp || mesh.polys[i*2*nvp+nj] == Recast.RC_MESH_NULL_IDX) nj = 0;
//                        short* va = mesh.verts[mesh.polys[i*2*nvp+j]*3];
//                        short* vb = mesh.verts[mesh.polys[i*2*nvp+nj]*3];

                    if ((int)mesh.verts[mesh.polys[i*2*nvp+j]*3+0] == 0 && (int)mesh.verts[mesh.polys[i*2*nvp+nj]*3+0] == 0)
                        mesh.polys[i*2*nvp+nvp+j] = (short)0x8000 | 0;
                    else if ((int)mesh.verts[mesh.polys[i*2*nvp+j]*3+2] == h && (int)mesh.verts[mesh.polys[i*2*nvp+nj]*3+2] == h)
                        mesh.polys[i*2*nvp+nvp+j] = (short)0x8000 | 1;
                    else if ((int)mesh.verts[mesh.polys[i*2*nvp+j]*3+0] == w && (int)mesh.verts[mesh.polys[i*2*nvp+nj]*3+0] == w)
                        mesh.polys[i*2*nvp+nvp+j] = (short)0x8000 | 2;
                    else if ((int)mesh.verts[mesh.polys[i*2*nvp+j]*3+2] == 0 && (int)mesh.verts[mesh.polys[i*2*nvp+nj]*3+2] == 0)
                        mesh.polys[i*2*nvp+nvp+j] = (short)0x8000 | 3;
                }
            }
        }

        // Just allocate the mesh flags array. The user is resposible to fill it.
//            mesh.flags = (unsigned short*)rcAlloc(sizeof(unsigned short)*mesh.npolys, RC_ALLOC_PERM);
        mesh.flags = new short[mesh.npolys];//(unsigned short*)rcAlloc(sizeof(unsigned short)*mesh.npolys, RC_ALLOC_PERM);
        /*if (!mesh.flags)
        {
            ctx.log(RC_LOG_ERROR, "rcBuildPolyMesh: Out of memory 'mesh.flags' (%d).", mesh.npolys);
            return false;
        }*/
//            memset(mesh.flags, 0, sizeof(unsigned short) * mesh.npolys);

        if (mesh.nverts > 0xffff)
        {
            ctx.log(rcLogCategory.RC_LOG_ERROR, "rcBuildPolyMesh: The resulting mesh has too many vertices %d (max %d). Data can be corrupted.", mesh.nverts, 0xffff);
        }
        if (mesh.npolys > 0xffff)
        {
            ctx.log(rcLogCategory.RC_LOG_ERROR, "rcBuildPolyMesh: The resulting mesh has too many polygons %d (max %d). Data can be corrupted.", mesh.npolys, 0xffff);
        }

        ctx.stopTimer(rcTimerLabel.RC_TIMER_BUILD_POLYMESH);

        return true;
    }

    public static boolean buildMeshAdjacency(short[] polys, int npolys,
                                   int nverts, int vertsPerPoly)
    {
        // Based on code by Eric Lengyel from:
        // http://www.terathon.com/code/edges.php

        int maxEdgeCount = npolys*vertsPerPoly;
//            short* firstEdge = (unsigned short*)rcAlloc(sizeof(unsigned short)*(nverts + maxEdgeCount), RC_ALLOC_TEMP);
        short[] firstEdge = new short[nverts + maxEdgeCount];
//            if (!firstEdge)
//                return false;
        short[] nextEdge = firstEdge;
        int edgeCount = 0;

        rcEdge[] edges =new rcEdge[maxEdgeCount];// (rcEdge*)rcAlloc(sizeof(rcEdge)*maxEdgeCount, RC_ALLOC_TEMP);
        /*if (!edges)
        {
            rcFree(firstEdge);
            return false;
        }*/

        for (int i = 0; i < nverts; i++)
            firstEdge[i] = Recast.RC_MESH_NULL_IDX;

        for (int i = 0; i < npolys; ++i)
        {
//                short* t = polys[i*vertsPerPoly*2];
            for (int j = 0; j < vertsPerPoly; ++j)
            {
                if (polys[i*vertsPerPoly*2+j] == Recast.RC_MESH_NULL_IDX) break;
                short v0 = polys[i*vertsPerPoly*2+j];
                short v1 = (j+1 >= vertsPerPoly || polys[i*vertsPerPoly*2+j+1] == Recast.RC_MESH_NULL_IDX) ? polys[i*vertsPerPoly*2+0] : polys[i*vertsPerPoly*2+j+1];
                if (v0 < v1)
                {
                    if (edges[edgeCount] == null) {
                        edges[edgeCount] = new rcEdge();
                    }
                    rcEdge edge = edges[edgeCount];
                    edge.vert[0] = v0;
                    edge.vert[1] = v1;
                    edge.poly[0] = (short)i;
                    edge.polyEdge[0] = (short)j;
                    edge.poly[1] = (short)i;
                    edge.polyEdge[1] = 0;
                    // Insert edge
                    nextEdge[nverts + edgeCount] = firstEdge[v0];
                    firstEdge[v0] = (short)edgeCount;
                    edgeCount++;
                }
            }
        }

        for (int i = 0; i < npolys; ++i)
        {
//                short* t = polys[i*vertsPerPoly*2];
            for (int j = 0; j < vertsPerPoly; ++j)
            {
                if (polys[i*vertsPerPoly*2+j] == Recast.RC_MESH_NULL_IDX) break;
                short v0 = polys[i*vertsPerPoly*2+j];
                short v1 = (j+1 >= vertsPerPoly || polys[i*vertsPerPoly*2+j+1] == Recast.RC_MESH_NULL_IDX) ? polys[i*vertsPerPoly*2+0] : polys[i*vertsPerPoly*2+j+1];
                if (v0 > v1)
                {
                    for (short e = firstEdge[v1]; e != Recast.RC_MESH_NULL_IDX; e = nextEdge[nverts + e])
                    {
                        if (edges[edgeCount] == null) {
                            edges[edgeCount] = new rcEdge();
                        }
                        rcEdge edge = edges[e];
                        if (edge.vert[1] == v0 && edge.poly[0] == edge.poly[1])
                        {
                            edge.poly[1] = (short)i;
                            edge.polyEdge[1] = (short)j;
                            break;
                        }
                    }
                }
            }
        }

        // Store adjacency
        for (int i = 0; i < edgeCount; ++i)
        {
            rcEdge e = edges[i];
            if (e.poly[0] != e.poly[1])
            {
//                    short* p0 = &polys[e.poly[0]*vertsPerPoly*2];
//                    short* p1 = polys[e.poly[1]*vertsPerPoly*2];
                polys[e.poly[0]*vertsPerPoly*2+vertsPerPoly + e.polyEdge[0]] = e.poly[1];
                polys[e.poly[1]*vertsPerPoly*2+vertsPerPoly + e.polyEdge[1]] = e.poly[0];
            }
        }

//            rcFree(firstEdge);
//            rcFree(edges);

        return true;
    }


    public static void pushFront(int v, int[] arr, int[] an)
    {
        an[0]++;
        for (int i = an[0]-1; i > 0; --i) arr[i] = arr[i-1];
        arr[0] = v;
    }

    public static void pushBack(int v, int[] arr, int[] an)
    {
        arr[an[0]] = v;
        an[0]++;
    }

    public static boolean removeVertex(rcContext ctx, rcPolyMesh mesh, short rem, int maxTris)
    {
        int nvp = mesh.nvp;

        // Count number of polygons to remove.
        int numRemovedVerts = 0;
        for (int i = 0; i < mesh.npolys; ++i)
        {
            short[] p = Recast.createN(mesh.polys, i * nvp * 2, nvp);
            int nv = countPolyVerts(p, nvp);
            for (int j = 0; j < nv; ++j)
            {
                if (p[j] == rem)
                    numRemovedVerts++;
            }
        }

        int nedges = 0;
//            rcScopedDelete<int> edges = (int*)rcAlloc(sizeof(int)*numRemovedVerts*nvp*4, RC_ALLOC_TEMP);
        int[] edges = new int[numRemovedVerts*nvp*3];
        /*if (!edges)
        {
            ctx.log(RC_LOG_WARNING, "removeVertex: Out of memory 'edges' (%d).", numRemovedVerts*nvp*4);
            return false;
        }*/

        int nhole[] = new int[]{0};
//            rcScopedDelete<int> hole = (int*)rcAlloc(sizeof(int)*numRemovedVerts*nvp, RC_ALLOC_TEMP);
        int[] hole = new int[numRemovedVerts*nvp];//(int*)rcAlloc(sizeof(int)*numRemovedVerts*nvp, RC_ALLOC_TEMP);
        /*if (!hole)
        {
            ctx.log(RC_LOG_WARNING, "removeVertex: Out of memory 'hole' (%d).", numRemovedVerts*nvp);
            return false;
        }*/

        int nhreg[] = new int[]{0};
//            rcScopedDelete<int> hreg = (int*)rcAlloc(sizeof(int)*numRemovedVerts*nvp, RC_ALLOC_TEMP);
        int[] hreg = new int[numRemovedVerts*nvp];
//            if (!hreg)
//            {
//                ctx.log(RC_LOG_WARNING, "removeVertex: Out of memory 'hreg' (%d).", numRemovedVerts*nvp);
//                return false;
//            }

        int nharea[] = new int[]{0};
//            rcScopedDelete<int> harea = (int*)rcAlloc(sizeof(int)*numRemovedVerts*nvp, RC_ALLOC_TEMP);
        int[] harea = new int[numRemovedVerts*nvp];//(int*)rcAlloc(sizeof(int)*numRemovedVerts*nvp, RC_ALLOC_TEMP);
//            if (!harea)
//            {
//                ctx.log(RC_LOG_WARNING, "removeVertex: Out of memory 'harea' (%d).", numRemovedVerts*nvp);
//                return false;
//            }

        for (int i = 0; i < mesh.npolys; ++i)
        {
            short[] p = Recast.createN(mesh.polys, i * nvp * 2, nvp);
            int nv = countPolyVerts(p, nvp);
            boolean hasRem = false;
            for (int j = 0; j < nv; ++j)
                if (p[j] == rem) hasRem = true;
            if (hasRem)
            {
                // Collect edges which does not touch the removed vertex.
                for (int j = 0, k = nv-1; j < nv; k = j++)
                {
                    if (p[j] != rem && p[k] != rem)
                    {
//                            int* e = edges[nedges*4];
                        edges[nedges*4+0] = p[k];
                        edges[nedges*4+1] = p[j];
                        edges[nedges*4+2] = mesh.regs[i];
                        edges[nedges*4+3] = mesh.areas[i];
                        nedges++;
                    }
                }
                // Remove the polygon.
                short[] p2 = Recast.createN(mesh.polys, (mesh.npolys - 1) * nvp * 2, nvp);
//                    memcpy(p,p2,sizeof(unsigned short)*nvp);
                System.arraycopy(p2, 0, p, 0, nvp);
//                    memset(p+nvp,0xff,sizeof(unsigned short)*nvp);
                Arrays.fill(p, nvp, nvp + nv, (short) 0xff);
                mesh.regs[i] = mesh.regs[mesh.npolys-1];
                mesh.areas[i] = mesh.areas[mesh.npolys-1];
                mesh.npolys--;
                --i;
            }
            System.arraycopy(p, 0, mesh.polys, i*nvp*2, nvp);
        }

        // Remove vertex.
        for (int i = (int)rem; i < mesh.nverts; ++i)
        {
            mesh.verts[i*3+0] = mesh.verts[(i+1)*3+0];
            mesh.verts[i*3+1] = mesh.verts[(i+1)*3+1];
            mesh.verts[i*3+2] = mesh.verts[(i+1)*3+2];
        }
        mesh.nverts--;

        // Adjust indices to match the removed vertex layout.
        for (int i = 0; i < mesh.npolys; ++i)
        {
            short[] p = Recast.createN(mesh.polys, i * nvp * 2, nvp);
            int nv = countPolyVerts(p, nvp);
            for (int j = 0; j < nv; ++j)
                if (p[j] > rem) p[j]--;
        }
        for (int i = 0; i < nedges; ++i)
        {
            if (edges[i*4+0] > rem) edges[i*4+0]--;
            if (edges[i*4+1] > rem) edges[i*4+1]--;
        }

        if (nedges == 0)
            return true;

        // Start with one vertex, keep appending connected
        // segments to the start and end of the hole.
        pushBack(edges[0], hole, nhole);
        pushBack(edges[2], hreg, nhreg);
        pushBack(edges[3], harea, nharea);

        while (nedges != 0)
        {
            boolean match = false;

            for (int i = 0; i < nedges; ++i)
            {
                int ea = edges[i*4+0];
                int eb = edges[i*4+1];
                int r = edges[i*4+2];
                int a = edges[i*4+3];
                boolean add = false;
                if (hole[0] == eb)
                {
                    // The segment matches the beginning of the hole boundary.
                    pushFront(ea, hole, nhole);
                    pushFront(r, hreg, nhreg);
                    pushFront(a, harea, nharea);
                    add = true;
                }
                else if (hole[nhole[0]-1] == ea)
                {
                    // The segment matches the end of the hole boundary.
                    pushBack(eb, hole, nhole);
                    pushBack(r, hreg, nhreg);
                    pushBack(a, harea, nharea);
                    add = true;
                }
                if (add)
                {
                    // The edge segment was added, remove it.
                    edges[i*4+0] = edges[(nedges-1)*4+0];
                    edges[i*4+1] = edges[(nedges-1)*4+1];
                    edges[i*4+2] = edges[(nedges-1)*4+2];
                    edges[i*4+3] = edges[(nedges-1)*4+3];
                    --nedges;
                    match = true;
                    --i;
                }
            }

            if (!match)
                break;
        }

//            rcScopedDelete<int> tris = (int*)rcAlloc(sizeof(int)*nhole*3, RC_ALLOC_TEMP);
        int[] tris = new int[nhole[0]*3];//(int*)rcAlloc(sizeof(int)*nhole*3, RC_ALLOC_TEMP);
//            if (!tris)
//            {
//                ctx.log(RC_LOG_WARNING, "removeVertex: Out of memory 'tris' (%d).", nhole*3);
//                return false;
//            }

//            rcScopedDelete<int> tverts = (int*)rcAlloc(sizeof(int)*nhole*4, RC_ALLOC_TEMP);
        int[] tverts = new int[nhole[0]*4];//(int*)rcAlloc(sizeof(int)*nhole*4, RC_ALLOC_TEMP);
//            if (!tverts)
//            {
//                ctx.log(RC_LOG_WARNING, "removeVertex: Out of memory 'tverts' (%d).", nhole*4);
//                return false;
//            }

//            rcScopedDelete<int> thole = (int*)rcAlloc(sizeof(int)*nhole, RC_ALLOC_TEMP);
        int[] thole = new int[nhole[0]];//(int*)rcAlloc(sizeof(int)*nhole, RC_ALLOC_TEMP);
//            if (!tverts)
//            {
//                ctx.log(RC_LOG_WARNING, "removeVertex: Out of memory 'thole' (%d).", nhole);
//                return false;
//            }

        // Generate temp vertex array for triangulation.
        for (int i = 0; i < nhole[0]; ++i)
        {
            int pi = hole[i];
            tverts[i*4+0] = mesh.verts[pi*3+0];
            tverts[i*4+1] = mesh.verts[pi*3+1];
            tverts[i*4+2] = mesh.verts[pi*3+2];
            tverts[i*4+3] = 0;
            thole[i] = i;
        }

        // Triangulate the hole.
        int ntris = triangulate(nhole[0], tverts, thole, tris);
        if (ntris < 0)
        {
            ntris = -ntris;
            ctx.log(rcLogCategory.RC_LOG_WARNING, "removeVertex: triangulate() returned bad results.");
        }

        // Merge the hole triangles back to polygons.
//            rcScopedDelete<unsigned short> polys = (unsigned short*)rcAlloc(sizeof(unsigned short)*(ntris+1)*nvp, RC_ALLOC_TEMP);
        short[] polys = new short[(ntris+1)*nvp];//(unsigned short*)rcAlloc(sizeof(unsigned short)*(ntris+1)*nvp, RC_ALLOC_TEMP);
//            if (!polys)
//            {
//                ctx.log(RC_LOG_ERROR, "removeVertex: Out of memory 'polys' (%d).", (ntris+1)*nvp);
//                return false;
//            }
//            rcScopedDelete<unsigned short> pregs = (unsigned short*)rcAlloc(sizeof(unsigned short)*ntris, RC_ALLOC_TEMP);
        short[] pregs = new short[ntris];//(unsigned short*)rcAlloc(sizeof(unsigned short)*ntris, RC_ALLOC_TEMP);
//            if (!pregs)
//            {
//                ctx.log(RC_LOG_ERROR, "removeVertex: Out of memory 'pregs' (%d).", ntris);
//                return false;
//            }
//            rcScopedDelete<unsigned char> pareas = (unsigned char*)rcAlloc(sizeof(unsigned char)*ntris, RC_ALLOC_TEMP);
        char[] pareas = new char[ntris];//(unsigned char*)rcAlloc(sizeof(unsigned char)*ntris, RC_ALLOC_TEMP);
//            if (!pregs)
//            {
//                ctx.log(RC_LOG_ERROR, "removeVertex: Out of memory 'pareas' (%d).", ntris);
//                return false;
//            }

        short[] tmpPoly = Recast.createN(polys, ntris * nvp, nvp);

        // Build initial polygons.
        int npolys = 0;
//            memset(polys, 0xff, ntris*nvp*sizeof(unsigned short));
        Arrays.fill(polys, 0, ntris*nvp, (short)0xff);
        for (int j = 0; j < ntris; ++j)
        {
//                int* t = create3(tris[j*3];
            if (tris[j*3+0] != tris[j*3+1] && tris[j*3+0] != tris[j*3+2] && tris[j*3+1] != tris[j*3+2])
            {
                polys[npolys*nvp+0] = (short)hole[tris[j*3+0]];
                polys[npolys*nvp+1] = (short)hole[tris[j*3+1]];
                polys[npolys*nvp+2] = (short)hole[tris[j*3+2]];
                pregs[npolys] = (short)hreg[tris[j*3+0]];
                pareas[npolys] = (char)harea[tris[j*3+0]];
                npolys++;
            }
        }
        if (npolys == 0)
            return true;

        // Merge polygons.
        if (nvp > 3)
        {
            for (;;)
            {
                // Find best polygons to merge.
                int bestMergeVal = 0;
                int bestPa = 0, bestPb = 0, bestEa = 0, bestEb = 0;

                for (int j = 0; j < npolys-1; ++j)
                {
                    short[] pj = Recast.createN(polys, j * nvp, nvp);
                    for (int k = j+1; k < npolys; ++k)
                    {
                        short[] pk = Recast.createN(polys, k * nvp, nvp);
                        int ea[] = new int[]{0}, eb[] = new int[]{0};
                        int v = getPolyMergeValue(pj, pk, mesh.verts, ea, eb, nvp);
                        System.arraycopy(pj, 0, polys, j*nvp, nvp);
                        System.arraycopy(pk, 0, polys, k*nvp, nvp);
                        if (v > bestMergeVal)
                        {
                            bestMergeVal = v;
                            bestPa = j;
                            bestPb = k;
                            bestEa = ea[0];
                            bestEb = eb[0];
                        }
                    }
                }

                if (bestMergeVal > 0)
                {
                    // Found best, merge.
                    short[] pa = Recast.createN(polys, bestPa * nvp, nvp);
                    short[] pb = Recast.createN(polys, bestPb * nvp, nvp);
                    mergePolys(pa, pb, bestEa, bestEb, tmpPoly, nvp);
//                        short[] tmpPoly = createN(polys, ntris*nvp, nvp);
                    System.arraycopy(tmpPoly, 0, polys, ntris*nvp, nvp);
                    System.arraycopy(pa, 0, polys, bestPa*nvp, nvp);
                    System.arraycopy(pb, 0, polys, bestPb*nvp, nvp);

//                        memcpy(pb, &polys[(npolys-1)*nvp], sizeof(unsigned short)*nvp);
                    System.arraycopy(polys, (npolys-1)*nvp, pb, 0, nvp);
                    pregs[bestPb] = pregs[npolys-1];
                    pareas[bestPb] = pareas[npolys-1];
                    npolys--;
                }
                else
                {
                    // Could not merge any polygons, stop.
                    break;
                }
            }
        }

        // Store polygons.
        for (int i = 0; i < npolys; ++i)
        {
            if (mesh.npolys >= maxTris) break;
//                short* p = &mesh.polys[mesh.npolys*nvp*2];
//                memset(p,0xff,sizeof(unsigned short)*nvp*2);
            Arrays.fill(mesh.polys, mesh.npolys*nvp*2, mesh.npolys*nvp*2 + nvp*2, (short)0xff);
            for (int j = 0; j < nvp; ++j)
                mesh.polys[mesh.npolys*nvp*2+j] = polys[i*nvp+j];
            mesh.regs[mesh.npolys] = pregs[i];
            mesh.areas[mesh.npolys] = pareas[i];
            mesh.npolys++;
            if (mesh.npolys > maxTris)
            {
                ctx.log(rcLogCategory.RC_LOG_ERROR, "removeVertex: Too many polygons %d (max:%d).", mesh.npolys, maxTris);
                return false;
            }
        }

        return true;
    }

    public static boolean canRemoveVertex(rcContext ctx, rcPolyMesh mesh, short rem)
    {
        int nvp = mesh.nvp;

        // Count number of polygons to remove.
        int numRemovedVerts = 0;
        int numTouchedVerts = 0;
        int numRemainingEdges = 0;
        for (int i = 0; i < mesh.npolys; ++i)
        {
            short[] p = Recast.createN(mesh.polys, i * nvp * 2, nvp);
            int nv = countPolyVerts(p, nvp);
            int numRemoved = 0;
            int numVerts = 0;
            for (int j = 0; j < nv; ++j)
            {
                if (p[j] == rem)
                {
                    numTouchedVerts++;
                    numRemoved++;
                }
                numVerts++;
            }
            if (numRemoved != 0)
            {
                numRemovedVerts += numRemoved;
                numRemainingEdges += numVerts-(numRemoved+1);
            }
        }

        // There would be too few edges remaining to create a polygon.
        // This can happen for example when a tip of a triangle is marked
        // as deletion, but there are no other polys that share the vertex.
        // In this case, the vertex should not be removed.
        if (numRemainingEdges <= 2)
            return false;

        // Find edges which share the removed vertex.
        int maxEdges = numTouchedVerts*2;
        int nedges = 0;
//            rcScopedDelete<int> edges = (int*)rcAlloc(sizeof(int)*maxEdges*3, RC_ALLOC_TEMP);
        int[] edges = new int[maxEdges*3];
        /*if (!edges)
        {
            ctx.log(RC_LOG_WARNING, "canRemoveVertex: Out of memory 'edges' (%d).", maxEdges*3);
            return false;
        }*/

        for (int i = 0; i < mesh.npolys; ++i)
        {
            short[] p = Recast.createN(mesh.polys, i * nvp * 2, nvp);
//                createN
            int nv = countPolyVerts(p, nvp);

            // Collect edges which touches the removed vertex.
            for (int j = 0, k = nv-1; j < nv; k = j++)
            {
                if (p[j] == rem || p[k] == rem)
                {
                    // Arrange edge so that a=rem.
                    int a = p[j], b = p[k];
                    if (b == rem)  {
                        int t = a;
                        a = b;
                        b = t;
//                            rcSwap(a,b);
                    }

                    // Check if the edge exists
                    boolean exists = false;
                    for (int m = 0; m < nedges; ++m)
                    {
//                            int[] e = edges[m*3];
                        if (edges[m*3+1] == b)
                        {
                            // Exists, increment vertex share count.
                            edges[m*3+2]++;
                            exists = true;
                        }
                    }
                    // Add new edge.
                    if (!exists)
                    {
//                            int[] e = edges[nedges*3];
                        edges[nedges*3+0] = a;
                        edges[nedges*3+1] = b;
                        edges[nedges*3+2] = 1;
                        nedges++;
                    }
                }
            }
        }

        // There should be no more than 2 open edges.
        // This catches the case that two non-adjacent polygons
        // share the removed vertex. In that case, do not remove the vertex.
        int numOpenEdges = 0;
        for (int i = 0; i < nedges; ++i)
        {
            if (edges[i*3+2] < 2)
                numOpenEdges++;
        }
        if (numOpenEdges > 2)
            return false;

        return true;
    }

    static void mergePolys(short[] pa, short[] pb, int ea, int eb,
                           short[] tmp, int nvp)
    {
        int na = countPolyVerts(pa, nvp);
        int nb = countPolyVerts(pb, nvp);

        // Merge polygons.
//            memset(tmp, 0xff, sizeof(unsigned short)*nvp);
        Arrays.fill(tmp, 0, nvp, (short)0xff);
        /*for (int i = 0; i < nvp; i ++) {
            tmp[i] = 0xff;
        }*/
        int n = 0;
        // Add pa
        for (int i = 0; i < na-1; ++i)
            tmp[n++] = pa[(ea+1+i) % na];
        // Add pb
        for (int i = 0; i < nb-1; ++i)
            tmp[n++] = pb[(eb+1+i) % nb];

//            memcpy(pa, tmp, sizeof(unsigned short)*nvp);
        System.arraycopy(tmp, 0, pa, 0, nvp);
    }

    public static int countPolyVerts(short[] p, int nvp)
    {
        for (int i = 0; i < nvp; ++i)
            if (p[i] == Recast.RC_MESH_NULL_IDX)
                return i;
        return nvp;
    }

    public static int getPolyMergeValue(short[] pa, short[] pb,
                                 short[] verts, int[] ea, int[] eb,
                                 int nvp)
    {
        int na = countPolyVerts(pa, nvp);
        int nb = countPolyVerts(pb, nvp);

        // If the merged polygon would be too big, do not merge.
        if (na+nb-2 > nvp)
            return -1;

        // Check if the polygons share an edge.
        ea[0] = -1;
        eb[0] = -1;

        for (int i = 0; i < na; ++i)
        {
            short va0 = pa[i];
            short va1 = pa[(i+1) % na];
            if (va0 > va1) {
                short t = va0;
                va0 = va1;
                va1 = t;
//                    rcSwap(va0, va1);
            }
            for (int j = 0; j < nb; ++j)
            {
                short vb0 = pb[j];
                short vb1 = pb[(j+1) % nb];
                if (vb0 > vb1) {
                    short t = vb0;
                    vb0 = vb1;
                    vb1 = t;
//                        rcSwap(vb0, vb1);
                }
                if (va0 == vb0 && va1 == vb1)
                {
                    ea[0] = i;
                    eb[0] = j;
                    break;
                }
            }
        }

        // No common edge, cannot merge.
        if (ea[0] == -1 || eb[0] == -1)
            return -1;

        // Check to see if the merged polygon would be convex.
        short va, vb, vc;

        va = pa[(ea[0]+na-1) % na];
        vb = pa[ea[0]];
        vc = pb[(eb[0]+2) % nb];
        if (!uleft(Recast.create3(verts, va * 3), Recast.create3(verts, vb * 3), Recast.create3(verts, vc * 3)))
        return -1;

        va = pb[(eb[0]+nb-1) % nb];
        vb = pb[eb[0]];
        vc = pa[(ea[0]+2) % na];
        if (!uleft(Recast.create3(verts, va * 3), Recast.create3(verts, vb * 3), Recast.create3(verts, vc * 3)))
        return -1;

        va = pa[ea[0]];
        vb = pa[(ea[0]+1)%na];

        int dx = (int)verts[va*3+0] - (int)verts[vb*3+0];
        int dy = (int)verts[va*3+2] - (int)verts[vb*3+2];

        return dx*dx + dy*dy;
    }

    public static boolean uleft(short[] a, short[] b, short[] c)
    {
        return ((int)b[0] - (int)a[0]) * ((int)c[2] - (int)a[2]) -
                ((int)c[0] - (int)a[0]) * ((int)b[2] - (int)a[2]) < 0;
    }

    public static int triangulate(int n, int[] verts, int[] indices, int[] tris)
    {
        int ntris = 0;
        int[] dst = tris;
        int dstIndex = 0;

        // The last bit of the index is used to indicate if the vertex can be removed.
        for (int i = 0; i < n; i++)
        {
            int i1 = next(i, n);
            int i2 = next(i1, n);
            if (diagonal(i, i2, n, verts, indices))
                indices[i1] |= 0x80000000;
        }

        while (n > 3)
        {
            int minLen = -1;
            int mini = -1;
            for (int i = 0; i < n; i++)
            {
                int i1 = next(i, n);
                if ((indices[i1] & 0x80000000) != 0)
                {
                    int[] p0 = Recast.create3(verts, (indices[i] & 0x0fffffff) * 4);
                    int[] p2 = Recast.create3(verts, (indices[next(i1, n)] & 0x0fffffff) * 4);

                    int dx = p2[0] - p0[0];
                    int dy = p2[2] - p0[2];
                    int len = dx*dx + dy*dy;

                    if (minLen < 0 || len < minLen)
                    {
                        minLen = len;
                        mini = i;
                    }
                }
            }

            if (mini == -1)
            {
                // Should not happen.
/*			printf("mini == -1 ntris=%d n=%d\n", ntris, n);
        for (int i = 0; i < n; i++)
        {
            printf("%d ", indices[i] & 0x0fffffff);
        }
        printf("\n");*/
                return -ntris;
            }

            int i = mini;
            int i1 = next(i, n);
            int i2 = next(i1, n);

            dst[dstIndex] = indices[i] & 0x0fffffff;
            dstIndex++;
            dst[dstIndex] = indices[i1] & 0x0fffffff;
            dstIndex++;
            dst[dstIndex] = indices[i2] & 0x0fffffff;
            dstIndex++;
            ntris++;

            // Removes P[i1] by copying P[i+1]...P[n-1] left one index.
            n--;
            for (int k = i1; k < n; k++)
                indices[k] = indices[k+1];

            if (i1 >= n) i1 = 0;
            i = prev(i1,n);
            // Update diagonal flags.
            if (diagonal(prev(i, n), i1, n, verts, indices))
                indices[i] |= 0x80000000;
            else
                indices[i] &= 0x0fffffff;

            if (diagonal(i, next(i1, n), n, verts, indices))
                indices[i1] |= 0x80000000;
            else
                indices[i1] &= 0x0fffffff;
        }

        // Append the remaining triangle.
        dst[dstIndex] = indices[0] & 0x0fffffff;
        dstIndex++;
        dst[dstIndex] = indices[1] & 0x0fffffff;
        dstIndex++;
        dst[dstIndex] = indices[2] & 0x0fffffff;
        dstIndex++;
        ntris++;

        return ntris;
    }

    public static int prev(int i, int n) { return i-1 >= 0 ? i-1 : n-1; }
    public static int next(int i, int n) { return i+1 < n ? i+1 : 0; }

    public static final int VERTEX_BUCKET_COUNT = (1<<12);

    public static int computeVertexHash(int x, int y, int z)
    {
        int h1 = 0x8da6b343; // Large multiplicative constants;
        int h2 = 0xd8163841; // here arbitrarily chosen primes
        int h3 = 0xcb1ab31f;
        int n = h1 * x + h2 * y + h3 * z;
        return (int)(n & (VERTEX_BUCKET_COUNT-1));
    }

    public static short addVertex(short x, short y, short z,
                                    short[] verts, int[] firstVert, int[] nextVert, int[] nv)
    {
        int bucket = computeVertexHash(x, 0, z);
        int i = firstVert[bucket];

        while (i != -1)
        {
//                short[] v = verts[i*3];
            if (verts[i*3+0] == x && (Recast.rcAbs(verts[i * 3 + 1] - y) <= 2) && verts[i*3+2] == z)
                return (short)i;
            i = nextVert[i]; // next
        }

        // Could not find, create new.
        i = nv[0]; nv[0]++;
//            short[] v = verts[i*3];
        verts[i*3+0] = x;
        verts[i*3+1] = y;
        verts[i*3+2] = z;
        nextVert[i] = firstVert[bucket];
        firstVert[bucket] = i;

        return (short)i;
    }

    // Returns T iff (v_i, v_j) is a proper internal
// diagonal of P.
    public static boolean diagonal(int i, int j, int n, int[] verts, int[] indices)
    {
        return inCone(i, j, n, verts, indices) && diagonalie(i, j, n, verts, indices);
    }

    // Returns true iff the diagonal (i,j) is strictly internal to the
// polygon P in the neighborhood of the i endpoint.
    public static boolean	inCone(int i, int j, int n, int[] verts, int[] indices)
    {
        int[] pi = Recast.create3(verts, (indices[i] & 0x0fffffff) * 4);
        int[] pj = Recast.create3(verts, (indices[j] & 0x0fffffff) * 4);
        int[] pi1 = Recast.create3(verts, (indices[next(i, n)] & 0x0fffffff) * 4);
        int[] pin1 = Recast.create3(verts, (indices[prev(i, n)] & 0x0fffffff) * 4);

        // If P[i] is a convex vertex [ i+1 left or on (i-1,i) ].
        if (leftOn(pin1, pi, pi1))
            return left(pi, pj, pin1) && left(pj, pi, pi1);
        // Assume (i-1,i,i+1) not collinear.
        // else P[i] is reflex.
        return !(leftOn(pi, pj, pi1) && leftOn(pj, pi, pin1));
    }

    // Returns T iff (v_i, v_j) is a proper internal *or* external
// diagonal of P, *ignoring edges incident to v_i and v_j*.
    public static boolean diagonalie(int i, int j, int n, int[] verts, int[] indices)
    {
        int[] d0 = Recast.create3(verts, (indices[i] & 0x0fffffff) * 4);
        int[] d1 = Recast.create3(verts, (indices[j] & 0x0fffffff) * 4);

        // For each edge (k,k+1) of P
        for (int k = 0; k < n; k++)
        {
            int k1 = next(k, n);
            // Skip edges incident to i or j
            if (!((k == i) || (k1 == i) || (k == j) || (k1 == j)))
            {
                int[] p0 = Recast.create3(verts, (indices[k] & 0x0fffffff) * 4);
                int[] p1 = Recast.create3(verts, (indices[k1] & 0x0fffffff) * 4);

                if (vequal(d0, p0) || vequal(d1, p0) || vequal(d0, p1) || vequal(d1, p1))
                    continue;

                if (intersect(d0, d1, p0, p1))
                    return false;
            }
        }
        return true;
    }

    public static boolean vequal(int[] a, int[] b)
    {
        return a[0] == b[0] && a[2] == b[2];
    }

    // Returns true iff segments ab and cd intersect, properly or improperly.
    public static boolean intersect(int[] a, int[] b, int[] c, int[] d)
    {
        if (intersectProp(a, b, c, d))
            return true;
        else if (between(a, b, c) || between(a, b, d) ||
                between(c, d, a) || between(c, d, b))
            return true;
        else
            return false;
    }

    //	Returns true iff ab properly intersects cd: they share
//	a point interior to both segments.  The properness of the
//	intersection is ensured by using strict leftness.
    public static boolean intersectProp(int[] a, int[] b, int[] c, int[] d)
    {
        // Eliminate improper cases.
        if (collinear(a,b,c) || collinear(a,b,d) ||
                collinear(c,d,a) || collinear(c,d,b))
            return false;

        return xorb(left(a,b,c), left(a,b,d)) && xorb(left(c,d,a), left(c,d,b));
    }

    //	Exclusive or: true iff exactly one argument is true.
//	The arguments are negated to ensure that they are 0/1
//	values.  Then the bitwise Xor operator may apply.
//	(This idea is due to Michael Baldwin.)
    public static boolean xorb(boolean x, boolean y)
    {
        return !x ^ !y;
    }

    // Returns T iff (a,b,c) are collinear and point c lies
// on the closed segement ab.
    public static boolean between(int[] a, int[] b, int[] c)
    {
        if (!collinear(a, b, c))
            return false;
        // If ab not vertical, check betweenness on x; else on y.
        if (a[0] != b[0])
            return	((a[0] <= c[0]) && (c[0] <= b[0])) || ((a[0] >= c[0]) && (c[0] >= b[0]));
        else
            return	((a[2] <= c[2]) && (c[2] <= b[2])) || ((a[2] >= c[2]) && (c[2] >= b[2]));
    }

    // Returns true iff c is strictly to the left of the directed
// line through a to b.
    public static boolean left(int[] a, int[] b, int[] c)
    {
        return area2(a, b, c) < 0;
    }

    public static boolean leftOn(int[] a, int[] b, int[] c)
    {
        return area2(a, b, c) <= 0;
    }

    public static boolean collinear(int[] a, int[] b, int[] c)
    {
        return area2(a, b, c) == 0;
    }


    public static int area2(int[] a, int[] b, int[] c)
    {
        return (b[0] - a[0]) * (c[2] - a[2]) - (c[0] - a[0]) * (b[2] - a[2]);
    }
}
