package org.recast.Recast.Source;

import org.recast.Recast.Include.*;
import org.recast.RecastDemo.Include.InputGeom;


public class RecastContour {
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

//        #define _USE_MATH_DEFINES
//        #include <math.h>
//        #include <string.h>
//        #include <stdio.h>
//        #include "Recast.h"
//                #include "RecastAlloc.h"
//                #include "RecastAssert.h"


    static int getCornerHeight(int x, int y, int i, int dir,
                               rcCompactHeightfield chf,
                               boolean[] isBorderVertex)
    {
        rcCompactSpan s = chf.spans[i];
        int ch = (int)s.y;
        int dirp = (dir+1) & 0x3;

        int regs[] = {0,0,0,0};

        // Combine region and area codes in order to prevent
        // border vertices which are in between two areas to be removed.
        regs[0] = chf.spans[i].reg | (chf.areas[i] << 16);

        if (Recast.rcGetCon(s, dir) != Recast.RC_NOT_CONNECTED)
        {
            int ax = x + Recast.rcGetDirOffsetX(dir);
            int ay = y + Recast.rcGetDirOffsetY(dir);
            int ai = (int)chf.cells[ax+ay*chf.width].getIndex() + Recast.rcGetCon(s, dir);
            rcCompactSpan as = chf.spans[ai];
            ch = (int) Recast.rcMax(ch, (int) as.y);
            regs[1] = chf.spans[ai].reg | (chf.areas[ai] << 16);
            if (Recast.rcGetCon(as, dirp) != Recast.RC_NOT_CONNECTED)
            {
                int ax2 = ax + Recast.rcGetDirOffsetX(dirp);
                int ay2 = ay + Recast.rcGetDirOffsetY(dirp);
                int ai2 = (int)chf.cells[ax2+ay2*chf.width].getIndex() + Recast.rcGetCon(as, dirp);
                rcCompactSpan as2 = chf.spans[ai2];
                ch = (int) Recast.rcMax(ch, (int) as2.y);
                regs[2] = chf.spans[ai2].reg | (chf.areas[ai2] << 16);
            }
        }
        if (Recast.rcGetCon(s, dirp) != Recast.RC_NOT_CONNECTED)
        {
            int ax = x + Recast.rcGetDirOffsetX(dirp);
            int ay = y + Recast.rcGetDirOffsetY(dirp);
            int ai = (int)chf.cells[ax+ay*chf.width].getIndex() + Recast.rcGetCon(s, dirp);
            rcCompactSpan as = chf.spans[ai];
            ch = (int) Recast.rcMax(ch, (int) as.y);
            regs[3] = chf.spans[ai].reg | (chf.areas[ai] << 16);
            if (Recast.rcGetCon(as, dir) != Recast.RC_NOT_CONNECTED)
            {
                int ax2 = ax + Recast.rcGetDirOffsetX(dir);
                int ay2 = ay + Recast.rcGetDirOffsetY(dir);
                int ai2 = (int)chf.cells[ax2+ay2*chf.width].getIndex() + Recast.rcGetCon(as, dir);
                rcCompactSpan as2 = chf.spans[ai2];
                ch = (int) Recast.rcMax(ch, (int) as2.y);
                regs[2] = chf.spans[ai2].reg | (chf.areas[ai2] << 16);
            }
        }

        // Check if the vertex is special edge vertex, these vertices will be removed later.
        for (int j = 0; j < 4; ++j)
        {
            int a = j;
            int b = (j+1) & 0x3;
            int c = (j+2) & 0x3;
            int d = (j+3) & 0x3;

            // The vertex is a border vertex there are two same exterior cells in a row,
            // followed by two interior cells and none of the regions are out of bounds.
            boolean twoSameExts = (regs[a] & regs[b] & Recast.RC_BORDER_REG) != 0 && regs[a] == regs[b];
            boolean twoInts = ((regs[c] | regs[d]) & Recast.RC_BORDER_REG) == 0;
            boolean intsSameArea = (regs[c]>>16) == (regs[d]>>16);
            boolean noZeros = regs[a] != 0 && regs[b] != 0 && regs[c] != 0 && regs[d] != 0;
            if (twoSameExts && twoInts && intsSameArea && noZeros)
            {
                isBorderVertex[0] = true;
                break;
            }
        }

        return ch;
    }

    static void walkContour(int x, int y, int i,
                            rcCompactHeightfield chf,
                            char[] flags, rcIntArray points)
    {
        // Choose the first non-connected edge
        char dir = 0;
        while ((flags[i] & (1 << dir)) == 0)
            dir++;

        char startDir = dir;
        int starti = i;

        char area = chf.areas[i];

        int iter = 0;
        while (++iter < 40000)
        {
            if ((flags[i] & (1 << dir)) != 0)
            {
                // Choose the edge corner
                boolean isBorderVertex[] = new boolean[]{false};
                boolean isAreaBorder = false;
                int px = x;
                int py = getCornerHeight(x, y, i, dir, chf, isBorderVertex);
                int pz = y;
                switch(dir)
                {
                    case 0: pz++; break;
                    case 1: px++; pz++; break;
                    case 2: px++; break;
                }
                int r = 0;
                rcCompactSpan s = chf.spans[i];
                if (Recast.rcGetCon(s, dir) != Recast.RC_NOT_CONNECTED)
                {
                    int ax = x + Recast.rcGetDirOffsetX(dir);
                    int ay = y + Recast.rcGetDirOffsetY(dir);
                    int ai = (int)chf.cells[ax+ay*chf.width].getIndex() + Recast.rcGetCon(s, dir);
                    r = (int)chf.spans[ai].reg;
                    if (area != chf.areas[ai])
                        isAreaBorder = true;
                }
                if (isBorderVertex[0])
                    r |= Recast.RC_BORDER_VERTEX;
                if (isAreaBorder)
                    r |= Recast.RC_AREA_BORDER;
                points.push(px);
                points.push(py);
                points.push(pz);
                points.push(r);

                flags[i] &= ~(1 << dir); // Remove visited edges
                dir = (char)((dir+1) & 0x3);  // Rotate CW
            }
            else
            {
                int ni = -1;
                int nx = x + Recast.rcGetDirOffsetX(dir);
                int ny = y + Recast.rcGetDirOffsetY(dir);
                rcCompactSpan s = chf.spans[i];
                if (Recast.rcGetCon(s, dir) != Recast.RC_NOT_CONNECTED)
                {
                    rcCompactCell nc = chf.cells[nx+ny*chf.width];
                    ni = (int)nc.getIndex() + Recast.rcGetCon(s, dir);
                }
                if (ni == -1)
                {
                    // Should not happen.
                    return;
                }
                x = nx;
                y = ny;
                i = ni;
                dir = (char)((dir+3) & 0x3);	// Rotate CCW
            }

            if (starti == i && startDir == dir)
            {
                break;
            }
        }
    }

    static float distancePtSeg(int x, int z,
                               int px, int pz,
                               int qx, int qz)
    {
        float pqx = (float)(qx - px);
        float pqz = (float)(qz - pz);
        float dx = (float)(x - px);
        float dz = (float)(z - pz);
        float d = pqx*pqx + pqz*pqz;
        float t = pqx*dx + pqz*dz;
        if (d > 0)
            t /= d;
        if (t < 0)
            t = 0;
        else if (t > 1)
            t = 1;

        dx = px + t*pqx - x;
        dz = pz + t*pqz - z;

        return dx*dx + dz*dz;
    }

    static double distancePtSegDouble(int x, int z,
                               int px, int pz,
                               int qx, int qz)
    {
		double pqx = (double)(qx - px);
		double pqz = (double)(qz - pz);
		double dx = (double)(x - px);
		double dz = (double)(z - pz);
		double d = pqx*pqx + pqz*pqz;
		double t = pqx*dx + pqz*dz;
        if (d > 0)
            t /= d;
        if (t < 0)
            t = 0;
        else if (t > 1)
            t = 1;

        dx = px + t*pqx - x;
        dz = pz + t*pqz - z;

        return dx*dx + dz*dz;
    }

	public static void simplifyContour(rcIntArray points, rcIntArray simplified,
								float maxError, int maxEdgeLen, int buildFlags)
	{
		// Add initial points.
		boolean hasConnections = false;
		for (int i = 0; i < points.size(); i += 4)
		{
			if ((points.m_data[i+3] & Recast.RC_CONTOUR_REG_MASK) != 0)
			{
				hasConnections = true;
				break;
			}
		}

		if (hasConnections)
		{
			// The contour has some portals to other regions.
			// Add a new point to every location where the region changes.
			for (int i = 0, ni = points.size()/4; i < ni; ++i)
			{
				int ii = (i+1) % ni;
				boolean differentRegs = (points.m_data[i*4+3] & Recast.RC_CONTOUR_REG_MASK) != (points.m_data[ii*4+3] & Recast.RC_CONTOUR_REG_MASK);
				boolean areaBorders = (points.m_data[i*4+3] & Recast.RC_AREA_BORDER) != (points.m_data[ii*4+3] & Recast.RC_AREA_BORDER);
				if (differentRegs || areaBorders)
				{
					simplified.push(points.m_data[i*4+0]);
					simplified.push(points.m_data[i*4+1]);
					simplified.push(points.m_data[i*4+2]);
					simplified.push(i);
				}
			}
		}

		if (simplified.size() == 0)
		{
			// If there is no connections at all,
			// create some initial points for the simplification process.
			// Find lower-left and upper-right vertices of the contour.
			int llx = points.m_data[0];
			int lly = points.m_data[1];
			int llz = points.m_data[2];
			int lli = 0;
			int urx = points.m_data[0];
			int ury = points.m_data[1];
			int urz = points.m_data[2];
			int uri = 0;
			for (int i = 0; i < points.size(); i += 4)
			{
				int x = points.m_data[i+0];
				int y = points.m_data[i+1];
				int z = points.m_data[i+2];
				if (x < llx || (x == llx && z < llz))
				{
					llx = x;
					lly = y;
					llz = z;
					lli = i/4;
				}
				if (x > urx || (x == urx && z > urz))
				{
					urx = x;
					ury = y;
					urz = z;
					uri = i/4;
				}
			}
			simplified.push(llx);
			simplified.push(lly);
			simplified.push(llz);
			simplified.push(lli);

			simplified.push(urx);
			simplified.push(ury);
			simplified.push(urz);
			simplified.push(uri);
		}

		// Add points until all raw points are within
		// error tolerance to the simplified shape.
		 int pn = points.size()/4;
		for (int i = 0; i < simplified.size()/4; )
		{
			int ii = (i+1) % (simplified.size()/4);

			 int ax = simplified.m_data[i*4+0];
			 int az = simplified.m_data[i*4+2];
			 int ai = simplified.m_data[i*4+3];

			 int bx = simplified.m_data[ii*4+0];
			 int bz = simplified.m_data[ii*4+2];
			 int bi = simplified.m_data[ii*4+3];

			// Find maximum deviation from the segment.
			double maxd = 0;
			int maxi = -1;
			int ci, cinc, endi;

			// Traverse the segment in lexilogical order so that the
			// max deviation is calculated similarly when traversing
			// opposite segments.
			if (bx > ax || (bx == ax && bz > az))
			{
				cinc = 1;
				ci = (ai+cinc) % pn;
				endi = bi;
			}
			else
			{
				cinc = pn-1;
				ci = (bi+cinc) % pn;
				endi = ai;
			}

			// Tessellate only outer edges or edges between areas.
			if ((points.m_data[ci*4+3] & Recast.RC_CONTOUR_REG_MASK) == 0 ||
				(points.m_data[ci*4+3] & Recast.RC_AREA_BORDER) != 0)
			{
				while (ci != endi)
				{
					double d = distancePtSegDouble(points.m_data[ci*4+0], points.m_data[ci*4+2], ax, az, bx, bz);
					if (d > maxd)
					{
						maxd = d;
						maxi = ci;
					}
					ci = (ci+cinc) % pn;
				}
			}


			// If the max deviation is larger than accepted error,
			// add new point, else continue to next segment.
			if (maxi != -1 && maxd > (maxError*maxError))
			{
				// Add space for the new point.
				simplified.resize(simplified.size()+4);
				 int n = simplified.size()/4;
				for (int j = n-1; j > i; --j)
				{
					simplified.m_data[j*4+0] = simplified.m_data[(j-1)*4+0];
					simplified.m_data[j*4+1] = simplified.m_data[(j-1)*4+1];
					simplified.m_data[j*4+2] = simplified.m_data[(j-1)*4+2];
					simplified.m_data[j*4+3] = simplified.m_data[(j-1)*4+3];
				}
				// Add the point.
				simplified.m_data[(i+1)*4+0] = points.m_data[maxi*4+0];
				simplified.m_data[(i+1)*4+1] = points.m_data[maxi*4+1];
				simplified.m_data[(i+1)*4+2] = points.m_data[maxi*4+2];
				simplified.m_data[(i+1)*4+3] = maxi;
			}
			else
			{
				++i;
			}
		}

		// Split too long edges.
		if (maxEdgeLen > 0 && (buildFlags & (rcBuildContoursFlags.RC_CONTOUR_TESS_WALL_EDGES.v|rcBuildContoursFlags.RC_CONTOUR_TESS_AREA_EDGES.v)) != 0)
		{
			for (int i = 0; i < simplified.size()/4; )
			{
				 int ii = (i+1) % (simplified.size()/4);

				 int ax = simplified.m_data[i*4+0];
				 int az = simplified.m_data[i*4+2];
				 int ai = simplified.m_data[i*4+3];

				 int bx = simplified.m_data[ii*4+0];
				 int bz = simplified.m_data[ii*4+2];
				 int bi = simplified.m_data[ii*4+3];

				// Find maximum deviation from the segment.
				int maxi = -1;
				int ci = (ai+1) % pn;

				// Tessellate only outer edges or edges between areas.
				boolean tess = false;
				// Wall edges.
				if ((buildFlags & rcBuildContoursFlags.RC_CONTOUR_TESS_WALL_EDGES.v) != 0 && (points.m_data[ci*4+3] & Recast.RC_CONTOUR_REG_MASK) == 0)
					tess = true;
				// Edges between areas.
				if ((buildFlags & rcBuildContoursFlags.RC_CONTOUR_TESS_AREA_EDGES.v) !=0 && (points.m_data[ci*4+3] & Recast.RC_AREA_BORDER) != 0)
					tess = true;

				if (tess)
				{
					int dx = bx - ax;
					int dz = bz - az;
					if (dx*dx + dz*dz > maxEdgeLen*maxEdgeLen)
					{
						// Round based on the segments in lexilogical order so that the
						// max tesselation is consistent regardles in which direction
						// segments are traversed.
						 int n = bi < ai ? (bi+pn - ai) : (bi - ai);
						if (n > 1)
						{
							if (bx > ax || (bx == ax && bz > az))
								maxi = (ai + n/2) % pn;
							else
								maxi = (ai + (n+1)/2) % pn;
						}
					}
				}

				// If the max deviation is larger than accepted error,
				// add new point, else continue to next segment.
				if (maxi != -1)
				{
					// Add space for the new point.
					simplified.resize(simplified.size()+4);
					 int n = simplified.size()/4;
					for (int j = n-1; j > i; --j)
					{
						simplified.m_data[j*4+0] = simplified.m_data[(j-1)*4+0];
						simplified.m_data[j*4+1] = simplified.m_data[(j-1)*4+1];
						simplified.m_data[j*4+2] = simplified.m_data[(j-1)*4+2];
						simplified.m_data[j*4+3] = simplified.m_data[(j-1)*4+3];
					}
					// Add the point.
					simplified.m_data[(i+1)*4+0] = points.m_data[maxi*4+0];
					simplified.m_data[(i+1)*4+1] = points.m_data[maxi*4+1];
					simplified.m_data[(i+1)*4+2] = points.m_data[maxi*4+2];
					simplified.m_data[(i+1)*4+3] = maxi;
				}
				else
				{
					++i;
				}
			}
		}

		for (int i = 0; i < simplified.size()/4; ++i)
		{
			// The edge vertex flag is take from the current raw point,
			// and the neighbour region is take from the next raw point.
			 int ai = (simplified.m_data[i*4+3]+1) % pn;
			 int bi = simplified.m_data[i*4+3];
			simplified.m_data[i*4+3] = (points.m_data[ai*4+3] & (Recast.RC_CONTOUR_REG_MASK|Recast.RC_AREA_BORDER)) | (points.m_data[bi*4+3] & Recast.RC_BORDER_VERTEX);
		}

	}

   /* static void simplifyContour(rcIntArray points, rcIntArray simplified,
                                float maxError, int maxEdgeLen, int buildFlags)
    {
        // Add initial points.
        boolean hasConnections = false;
        for (int i = 0; i < points.size(); i += 4)
        {
            if ((points.get(i+3) & Recast.RC_CONTOUR_REG_MASK) != 0)
            {
                hasConnections = true;
                break;
            }
        }

        if (hasConnections)
        {
            // The contour has some portals to other regions.
            // Add a new point to every location where the region changes.
            for (int i = 0, ni = points.size()/4; i < ni; ++i)
            {
                int ii = (i+1) % ni;
                boolean differentRegs = (points.get(i*4+3) & Recast.RC_CONTOUR_REG_MASK) != (points.get(ii*4+3) & Recast.RC_CONTOUR_REG_MASK);
                boolean areaBorders = (points.get(i*4+3) & Recast.RC_AREA_BORDER) != (points.get(ii*4+3) & Recast.RC_AREA_BORDER);
                if (differentRegs || areaBorders)
                {
                    simplified.push(points.get(i * 4 + 0));
                    simplified.push(points.get(i * 4 + 1));
                    simplified.push(points.get(i * 4 + 2));
                    simplified.push(i);
                }
            }
        }

        if (simplified.size() == 0)
        {
            // If there is no connections at all,
            // create some initial points for the simplification process.
            // Find lower-left and upper-right vertices of the contour.
            int llx = points.get(0);
            int lly = points.get(1);
            int llz = points.get(2);
            int lli = 0;
            int urx = points.get(0);
            int ury = points.get(1);
            int urz = points.get(2);
            int uri = 0;
            for (int i = 0; i < points.size(); i += 4)
            {
                int x = points.get(i+0);
                int y = points.get(i+1);
                int z = points.get(i+2);
                if (x < llx || (x == llx && z < llz))
                {
                    llx = x;
                    lly = y;
                    llz = z;
                    lli = i/4;
                }
                if (x > urx || (x == urx && z > urz))
                {
                    urx = x;
                    ury = y;
                    urz = z;
                    uri = i/4;
                }
            }
            simplified.push(llx);
            simplified.push(lly);
            simplified.push(llz);
            simplified.push(lli);

            simplified.push(urx);
            simplified.push(ury);
            simplified.push(urz);
            simplified.push(uri);
        }

        // Add points until all raw points are within
        // error tolerance to the simplified shape.
        int pn = points.size()/4;
        for (int i = 0; i < simplified.size()/4; )
        {
            int ii = (i+1) % (simplified.size()/4);

            int ax = simplified.get(i*4+0);
            int az = simplified.get(i*4+2);
            int ai = simplified.get(i*4+3);

            int bx = simplified.get(ii*4+0);
            int bz = simplified.get(ii*4+2);
            int bi = simplified.get(ii*4+3);

            // Find maximum deviation from the segment.
            float maxd = 0;
            int maxi = -1;
            int ci, cinc, endi;

            // Traverse the segment in lexilogical order so that the
            // max deviation is calculated similarly when traversing
            // opposite segments.
            if (bx > ax || (bx == ax && bz > az))
            {
                cinc = 1;
                ci = (ai+cinc) % pn;
                endi = bi;
            }
            else
            {
                cinc = pn-1;
                ci = (bi+cinc) % pn;
                endi = ai;
            }

            // Tessellate only outer edges or edges between areas.
            if ((points.get(ci*4+3) & Recast.RC_CONTOUR_REG_MASK) == 0 ||
                    (points.get(ci*4+3) & Recast.RC_AREA_BORDER) != 0)
            {
                while (ci != endi)
                {
                    float d = distancePtSeg(points.get(ci*4+0), points.get(ci*4+2), ax, az, bx, bz);
                    if (d > maxd)
                    {
                        maxd = d;
                        maxi = ci;
                    }
                    ci = (ci+cinc) % pn;
                }
            }


            // If the max deviation is larger than accepted error,
            // add new point, else continue to next segment.
            if (maxi != -1 && maxd > (maxError*maxError))
            {
                // Add space for the new point.
				simplified.resize(simplified.size()+4);
                int n = simplified.size()/4;
                for (int j = n-1; j > i; --j)
                {
                    simplified.set(j*4+0, simplified.get((j-1)*4+0));
                    simplified.set(j * 4 + 1, simplified.get((j - 1) * 4 + 1));
                    simplified.set(j * 4 + 2, simplified.get((j - 1) * 4 + 2));
                    simplified.set(j * 4 + 3, simplified.get((j - 1) * 4 + 3));
                }
                // Add the point.
                simplified.set((i + 1) * 4 + 0, points.get(maxi * 4 + 0));
                simplified.set((i + 1) * 4 + 1, points.get(maxi * 4 + 1));
                simplified.set((i + 1) * 4 + 2, points.get(maxi * 4 + 2));
                simplified.set((i + 1) * 4 + 3, maxi);
            }
            else
            {
                ++i;
            }
        }

        // Split too long edges.
        if (maxEdgeLen > 0 && (buildFlags & (rcBuildContoursFlags.RC_CONTOUR_TESS_WALL_EDGES.v|rcBuildContoursFlags.RC_CONTOUR_TESS_AREA_EDGES.v)) != 0)
        {
            for (int i = 0; i < simplified.size()/4; )
            {
                int ii = (i+1) % (simplified.size()/4);

                int ax = simplified.get(i*4+0);
                int az = simplified.get(i*4+2);
                int ai = simplified.get(i*4+3);

                int bx = simplified.get(ii*4+0);
                int bz = simplified.get(ii*4+2);
                int bi = simplified.get(ii*4+3);

                // Find maximum deviation from the segment.
                int maxi = -1;
                int ci = (ai+1) % pn;

                // Tessellate only outer edges or edges between areas.
                boolean tess = false;
                // Wall edges.
                if ((buildFlags & rcBuildContoursFlags.RC_CONTOUR_TESS_WALL_EDGES.v) != 0 && (points.get(ci*4+3) & Recast.RC_CONTOUR_REG_MASK) == 0)
                    tess = true;
                // Edges between areas.
                if ((buildFlags & rcBuildContoursFlags.RC_CONTOUR_TESS_AREA_EDGES.v) != 0 && (points.get(ci*4+3) & Recast.RC_AREA_BORDER) != 0)
                    tess = true;

                if (tess)
                {
                    int dx = bx - ax;
                    int dz = bz - az;
                    if (dx*dx + dz*dz > maxEdgeLen*maxEdgeLen)
                    {
                        // Round based on the segments in lexilogical order so that the
                        // max tesselation is consistent regardles in which direction
                        // segments are traversed.
                        int n = bi < ai ? (bi+pn - ai) : (bi - ai);
                        if (n > 1)
                        {
                            if (bx > ax || (bx == ax && bz > az))
                                maxi = (ai + n/2) % pn;
                            else
                                maxi = (ai + (n+1)/2) % pn;
                        }
                    }
                }

                // If the max deviation is larger than accepted error,
                // add new point, else continue to next segment.
                if (maxi != -1)
                {
                    // Add space for the new point.
					simplified.resize(simplified.size()+4);
                    int n = simplified.size()/4;
                    for (int j = n-1; j > i; --j)
                    {
                        simplified.set(j * 4 + 0, simplified.get((j - 1) * 4 + 0));
                        simplified.set(j * 4 + 1, simplified.get((j - 1) * 4 + 1));
                        simplified.set(j * 4 + 2, simplified.get((j - 1) * 4 + 2));
                        simplified.set(j * 4 + 3, simplified.get((j - 1) * 4 + 3));
                    }
                    // Add the point.
                    simplified.set((i + 1) * 4 + 0, points.get(maxi * 4 + 0));
                    simplified.set((i + 1) * 4 + 1, points.get(maxi * 4 + 1));
                    simplified.set((i + 1) * 4 + 2, points.get(maxi * 4 + 2));
                    simplified.set((i+1)*4+3, maxi);
                }
                else
                {
                    ++i;
                }
            }
        }

        for (int i = 0; i < simplified.size()/4; ++i)
        {
            // The edge vertex flag is take from the current raw point,
            // and the neighbour region is take from the next raw point.
            int ai = (simplified.get(i*4+3)+1) % pn;
            int bi = simplified.get(i*4+3);
            simplified.set(i*4+3, (points.get(ai*4+3) & (Recast.RC_CONTOUR_REG_MASK| Recast.RC_AREA_BORDER)) | (points.get(bi*4+3) & Recast.RC_BORDER_VERTEX));
        }
    }
*/
    static void removeDegenerateSegments(rcIntArray simplified)
    {
        // Remove adjacent vertices which are equal on xz-plane,
        // or else the triangulator will get confused.
        for (int i = 0; i < simplified.size()/4; ++i)
        {
            int ni = i+1;
            if (ni >= (simplified.size()/4))
                ni = 0;

            if (simplified.get(i * 4 + 0) == simplified.get(ni * 4 + 0) &&
                    simplified.get(i * 4 + 2) == simplified.get(ni * 4 + 2))
            {
                // Degenerate segment, remove.
                for (int j = i; j < simplified.size()/4-1; ++j)
                {
                    simplified.set(j * 4 + 0, simplified.get((j + 1) * 4 + 0));
                    simplified.set(j * 4 + 1, simplified.get((j + 1) * 4 + 1));
                    simplified.set(j*4+2, simplified.get((j+1)*4+2));
                    simplified.set(j * 4 + 3, simplified.get((j + 1) * 4 + 3));
                }
//                    simplified.resize(simplified.size()-4);
				simplified.resize(simplified.size()-4);
            }
        }
    }

    public static int calcAreaOfPolygon2D(int[] verts, int nverts)
    {
        int area = 0;
        for (int i = 0, j = nverts-1; i < nverts; j=i++)
        {
//                int[] vi = verts[i*4];
//                int[] vj = verts[j*4];
            area += verts[i*4 + 0] * verts[j*4 + 2] - verts[j*4 + 0] * verts[i*4 + 2];
        }
        return (area+1) / 2;
    }

    public static boolean ileft(int[] a, int[] b, int[] c)
    {
        return (b[0] - a[0]) * (c[2] - a[2]) - (c[0] - a[0]) * (b[2] - a[2]) <= 0;
    }

    public static void getClosestIndices(int[] vertsa, int nvertsa,
                                  int[] vertsb, int nvertsb,
                                  int[] ia, int[] ib)
    {
        int closestDist = 0xfffffff;
        ia[0] = -1;
        ib[0] = -1;
        for (int i = 0; i < nvertsa; ++i)
        {
            int in = (i+1) % nvertsa;
            int ip = (i+nvertsa-1) % nvertsa;
            int[] va = new int[3];
            va[0] = vertsa[i*4+0];
            va[1] = vertsa[i*4+1];
            va[2] = vertsa[i*4+2];

            int[] van = new int[3];
            van[0] = vertsa[in*4+0];
            van[1] = vertsa[in*4+1];
            van[2] = vertsa[in*4+2];

            int[] vap = new int[3];
            vap[0] = vertsa[ip*4+0];
            vap[1] = vertsa[ip*4+1];
            vap[2] = vertsa[ip*4+2];

            for (int j = 0; j < nvertsb; ++j)
            {
                int[] vb = new int[3];
                vb[0] = vertsb[j*4+0];
                vb[1] = vertsb[j*4+1];
                vb[2] = vertsb[j*4+2];
                // vb must be "infront" of va.
                if (ileft(vap,va,vb) && ileft(va,van,vb))
                {
                    int dx = vb[0] - va[0];
                    int dz = vb[2] - va[2];
                    int d = dx*dx + dz*dz;
                    if (d < closestDist)
                    {
                        ia[0] = i;
                        ib[0] = j;
                        closestDist = d;
                    }
                }
            }
        }
    }

    public static boolean mergeContours(rcContour ca, rcContour cb, int ia, int ib)
    {
        int maxVerts = ca.nverts + cb.nverts + 2;
        int[] verts = new int[maxVerts*4];//(int*)rcAlloc(sizeof(int)*maxVerts*4, RC_ALLOC_PERM);
//            if (!verts)
//                return false;

        int nv = 0;

        // Copy contour A.
        for (int i = 0; i <= ca.nverts; ++i)
        {
//                int[] dst = verts[nv*4];
//                int[] src = ca.verts[((ia+i)%ca.nverts)*4];
            verts[nv*4+0] = ca.verts[((ia+i)%ca.nverts)*4+0];
            verts[nv*4+1] = ca.verts[((ia+i)%ca.nverts)*4+1];
            verts[nv*4+2] = ca.verts[((ia+i)%ca.nverts)*4+2];
            verts[nv*4+3] = ca.verts[((ia+i)%ca.nverts)*4+3];
            nv++;
        }

        // Copy contour B
        for (int i = 0; i <= cb.nverts; ++i)
        {
//                int* dst = verts[nv*4];
//                const int* src = cb.verts[((ib+i)%cb.nverts)*4];
            verts[nv*4+0] = cb.verts[((ib+i)%cb.nverts)*4+0];
            verts[nv*4+1] = cb.verts[((ib+i)%cb.nverts)*4+1];
            verts[nv*4+2] = cb.verts[((ib+i)%cb.nverts)*4+2];
            verts[nv*4+3] = cb.verts[((ib+i)%cb.nverts)*4+3];
            nv++;
        }

//            rcFree(ca.verts);
        ca.verts = verts;
        ca.nverts = nv;

//            rcFree(cb.verts);
//            cb.verts = 0;
        cb.nverts = 0;

        return true;
    }

    /// @par
///
/// The raw contours will match the region outlines exactly. The @p maxError and @p maxEdgeLen
/// parameters control how closely the simplified contours will match the raw contours.
///
/// Simplified contours are generated such that the vertices for portals between areas match up.
/// (They are considered mandatory vertices.)
///
/// Setting @p maxEdgeLength to zero will disabled the edge length feature.
///
/// See the #rcConfig documentation for more information on the configuration parameters.
///
/// @see rcAllocContourSet, rcCompactHeightfield, rcContourSet, rcConfig
    public boolean rcBuildContours(rcContext ctx, rcCompactHeightfield chf,
                                   float maxError, int maxEdgeLen,
                                   rcContourSet cset, int buildFlags) {
    {
//            rcAssert(ctx);

        int w = chf.width;
        int h = chf.height;
        int borderSize = chf.borderSize;

        ctx.startTimer(rcTimerLabel.RC_TIMER_BUILD_CONTOURS);

        InputGeom.rcVcopy(cset.bmin, chf.bmin);
        InputGeom.rcVcopy(cset.bmax, chf.bmax);
        if (borderSize > 0)
        {
            // If the heightfield was build with bordersize, remove the offset.
            float pad = borderSize*chf.cs;
            cset.bmin[0] += pad;
            cset.bmin[2] += pad;
            cset.bmax[0] -= pad;
            cset.bmax[2] -= pad;
        }
        cset.cs = chf.cs;
        cset.ch = chf.ch;
        cset.width = chf.width - chf.borderSize*2;
        cset.height = chf.height - chf.borderSize*2;
        cset.borderSize = chf.borderSize;

        int maxContours = Math.max((int) chf.maxRegions, 8);
        cset.conts = new rcContour[maxContours];//(rcContour*)rcAlloc(sizeof(rcContour)*maxContours, RC_ALLOC_PERM);
		for (int i = 0; i < maxContours; i++) {
			cset.conts[i] = new rcContour();
		}
        /*if (!cset.conts)
            return false;*/
        cset.nconts = 0;

//            rcScopedDelete<unsigned char> flags = (unsigned char*)rcAlloc(sizeof(unsigned char)*chf.spanCount, RC_ALLOC_TEMP);
        char flags[] = new char[chf.spanCount];
        /*if (!flags)
        {
            ctx.log(RC_LOG_ERROR, "rcBuildContours: Out of memory 'flags' (%d).", chf.spanCount);
            return false;
        }*/

        ctx.startTimer(rcTimerLabel.RC_TIMER_BUILD_CONTOURS_TRACE);

        // Mark boundaries.
        for (int y = 0; y < h; ++y)
        {
            for (int x = 0; x < w; ++x)
            {
                rcCompactCell c = chf.cells[x+y*w];
                for (int i = (int)c.getIndex(), ni = (int)(c.getIndex()+c.getCount()); i < ni; ++i)
                {
                    char res = 0;
                    rcCompactSpan s = chf.spans[i];
//					if (!chf.spans[i].reg || (chf.spans[i].reg & RC_BORDER_REG))
                    if (chf.spans[i].reg == 0|| (chf.spans[i].reg & Recast.RC_BORDER_REG) != 0)
                    {
                        flags[i] = 0;
                        continue;
                    }
                    for (int dir = 0; dir < 4; ++dir)
                    {
						int r = 0;
                        if (Recast.rcGetCon(s, dir) != Recast.RC_NOT_CONNECTED)
                        {
                            int ax = x + Recast.rcGetDirOffsetX(dir);
                            int ay = y + Recast.rcGetDirOffsetY(dir);
                            int ai = (int)chf.cells[ax+ay*w].getIndex() + Recast.rcGetCon(s, dir);
                            r = chf.spans[ai].reg;
                        }
                        if (r == chf.spans[i].reg)
                            res |= (1 << dir);
                    }
                    flags[i] = (char)(res ^ 0xf); // Inverse, mark non connected edges.
                }
            }
        }

        ctx.stopTimer(rcTimerLabel.RC_TIMER_BUILD_CONTOURS_TRACE);

		rcIntArray verts = new rcIntArrayImpl(256);
		rcIntArray simplified = new rcIntArrayImpl(64);

        for (int y = 0; y < h; ++y)
        {
            for (int x = 0; x < w; ++x)
            {
                rcCompactCell c = chf.cells[x+y*w];
                for (int i = (int)c.getIndex(), ni = (int)(c.getIndex()+c.getCount()); i < ni; ++i)
                {
                    if (flags[i] == 0 || flags[i] == 0xf)
                    {
                        flags[i] = 0;
                        continue;
                    }
					int reg = chf.spans[i].reg;
                    if (reg == 0 || (reg & Recast.RC_BORDER_REG) != 0)
                        continue;
                    char area = chf.areas[i];

                    verts.resize(0);
                    simplified.resize(0);

                    ctx.startTimer(rcTimerLabel.RC_TIMER_BUILD_CONTOURS_TRACE);
                    walkContour(x, y, i, chf, flags, verts);
                    ctx.stopTimer(rcTimerLabel.RC_TIMER_BUILD_CONTOURS_TRACE);

                    ctx.startTimer(rcTimerLabel.RC_TIMER_BUILD_CONTOURS_SIMPLIFY);
                    simplifyContour(verts, simplified, maxError, maxEdgeLen, buildFlags);
                    removeDegenerateSegments(simplified);
                    ctx.stopTimer(rcTimerLabel.RC_TIMER_BUILD_CONTOURS_SIMPLIFY);


                    // Store region.contour remap info.
                    // Create contour.
                    if (simplified.size()/4 >= 3)
                    {
                        if (cset.nconts >= maxContours)
                        {
                            // Allocate more contours.
                            // This can happen when there are tiny holes in the heightfield.
                            int oldMax = maxContours;
                            maxContours *= 2;
//                                rcContour[] newConts = (rcContour*)rcAlloc(sizeof(rcContour)*maxContours, RC_ALLOC_PERM);
                            rcContour[] newConts = new rcContour[maxContours];//(rcContour*)rcAlloc(sizeof(rcContour)*maxContours, RC_ALLOC_PERM);
                            for (int j = 0; j < cset.nconts; ++j)
                            {
                                newConts[j] = cset.conts[j];
                                // Reset source pointers to prevent data deletion.
                                cset.conts[j].verts = null;
                                cset.conts[j].rverts = null;
                            }
//                                rcFree(cset.conts);
                            cset.conts = newConts;

                            ctx.log(rcLogCategory.RC_LOG_WARNING, "rcBuildContours: Expanding max contours from %d to %d.", oldMax, maxContours);
                        }

                        rcContour cont = cset.conts[cset.nconts++];

                        cont.nverts = simplified.size()/4;
//                            cont.verts = (int*)rcAlloc(sizeof(int)*cont.nverts*4, RC_ALLOC_PERM);
                        cont.verts = new int[cont.nverts*4];//(int*)rcAlloc(sizeof(int)*cont.nverts*4, RC_ALLOC_PERM);
                        /*if (!cont.verts)
                        {
                            ctx.log(RC_LOG_ERROR, "rcBuildContours: Out of memory 'verts' (%d).", cont.nverts);
                            return false;
                        }*/
//                            memcpy(cont.verts, &simplified[0], sizeof(int)*cont.nverts*4);

                        System.arraycopy(simplified.m_data, 0, cont.verts, 0, cont.nverts*4);
                        if (borderSize > 0)
                        {
                            // If the heightfield was build with bordersize, remove the offset.
                            for (int j = 0; j < cont.nverts; ++j)
                            {
//                                    int* v = cont.verts[j*4];
                                cont.verts[j*4 + 0] -= borderSize;
                                cont.verts[j*4 + 2] -= borderSize;
                            }
                        }

                        cont.nrverts = verts.size()/4;
//						assert cont.nrverts == 98;
                        cont.rverts = new int[cont.nrverts*4];//(int*)rcAlloc(sizeof(int)*cont.nrverts*4, RC_ALLOC_PERM);
                        /*if (!cont.rverts)
                        {
                            ctx.log(RC_LOG_ERROR, "rcBuildContours: Out of memory 'rverts' (%d).", cont.nrverts);
                            return false;
                        }*/
//                            memcpy(cont.rverts, &verts[0], sizeof(int)*cont.nrverts*4);
                        System.arraycopy(verts.m_data, 0, cont.rverts, 0, cont.nrverts*4);
                        if (borderSize > 0)
                        {
                            // If the heightfield was build with bordersize, remove the offset.
                            for (int j = 0; j < cont.nrverts; ++j)
                            {
//                                    int* v = cont.rverts[j*4];
                                cont.rverts[j*4 + 0] -= borderSize;
                                cont.rverts[j*4 + 2] -= borderSize;
                            }
                        }

/*					cont.cx = cont.cy = cont.cz = 0;
                for (int i = 0; i < cont.nverts; ++i)
                {
                    cont.cx += cont.verts[i*4+0];
                    cont.cy += cont.verts[i*4+1];
                    cont.cz += cont.verts[i*4+2];
                }
                cont.cx /= cont.nverts;
                cont.cy /= cont.nverts;
                cont.cz /= cont.nverts;*/

                        cont.reg = reg;
                        cont.area = area;
                    }
                }
            }
        }

        // Check and merge droppings.
        // Sometimes the previous algorithms can fail and create several contours
        // per area. This pass will try to merge the holes into the main region.
        for (int i = 0; i < cset.nconts; ++i)
        {
            rcContour cont = cset.conts[i];
            // Check if the contour is would backwards.
            if (calcAreaOfPolygon2D(cont.verts, cont.nverts) < 0)
            {
                // Find another contour which has the same region ID.
                int mergeIdx = -1;
                for (int j = 0; j < cset.nconts; ++j)
                {
                    if (i == j) continue;
                    if (cset.conts[j].nverts != 0&& cset.conts[j].reg == cont.reg)
                    {
                        // Make sure the polygon is correctly oriented.
                        if (calcAreaOfPolygon2D(cset.conts[j].verts, cset.conts[j].nverts) != 0)
                        {
                            mergeIdx = j;
                            break;
                        }
                    }
                }
                if (mergeIdx == -1)
                {
                    ctx.log(rcLogCategory.RC_LOG_WARNING, "rcBuildContours: Could not find merge target for bad contour %d.", i);
                }
                else
                {
                    rcContour mcont = cset.conts[mergeIdx];
                    // Merge by closest points.
                    int ia[] = new int[]{0}, ib[] = new int[]{0};
                    getClosestIndices(mcont.verts, mcont.nverts, cont.verts, cont.nverts, ia, ib);
                    if (ia[0] == -1 || ib[0] == -1)
                    {
                        ctx.log(rcLogCategory.RC_LOG_WARNING, "rcBuildContours: Failed to find merge points for %d and %d.", i, mergeIdx);
                        continue;
                    }
                    if (!mergeContours(mcont, cont, ia[0], ib[0]))
                    {
                        ctx.log(rcLogCategory.RC_LOG_WARNING, "rcBuildContours: Failed to merge contours %d and %d.", i, mergeIdx);
                        continue;
                    }
                }
            }
        }

        ctx.stopTimer(rcTimerLabel.RC_TIMER_BUILD_CONTOURS);

        return true;
    }

}
}
