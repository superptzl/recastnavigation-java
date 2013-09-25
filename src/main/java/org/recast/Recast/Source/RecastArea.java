package org.recast.Recast.Source;

import org.recast.Recast.Include.*;

import java.util.Arrays;

public class RecastArea extends RecastImpl
{
	public boolean rcErodeWalkableArea(rcContext ctx, int radius, rcCompactHeightfield chf)
	{
//        rcAssert(ctx);

		int w = chf.width;
		int h = chf.height;

		ctx.startTimer(rcTimerLabel.RC_TIMER_ERODE_AREA);

//        unsigned char* dist = (unsigned char*)rcAlloc(sizeof(unsigned char)*chf.spanCount, RC_ALLOC_TEMP);
		char[] dist = new char[chf.spanCount];
//        if (!dist)
//        {
//            ctx.log(RC_LOG_ERROR, "erodeWalkableArea: Out of memory 'dist' (%d).", chf.spanCount);
//            return false;
//        }

		// Init distance.
//        memset(dist, 0xff, sizeof(unsigned char)*chf.spanCount);
		Arrays.fill(dist, Character.MAX_VALUE/*(char)0xff*/);

		// Mark boundary cells.
		for (int y = 0; y < h; ++y)
		{
			for (int x = 0; x < w; ++x)
			{
				rcCompactCell c = chf.cells[x + y * w];
				for (int i = (int)c.getIndex(), ni = (int)(c.getIndex() + c.getCount()); i < ni; ++i)
				{
					if (chf.areas[i] == RC_NULL_AREA)
					{
						dist[i] = 0;
					}
					else
					{
						rcCompactSpan s = chf.spans[i];
						int nc = 0;
						for (int dir = 0; dir < 4; ++dir)
						{
							if (rcGetCon(s, dir) != RC_NOT_CONNECTED)
							{
								int nx = x + rcGetDirOffsetX(dir);
								int ny = y + rcGetDirOffsetY(dir);
								int nidx = (int)chf.cells[nx + ny * w].getIndex() + rcGetCon(s, dir);
								if (chf.areas[nidx] != RC_NULL_AREA)
								{
									nc++;
								}
							}
						}
						// At least one missing neighbour.
						if (nc != 4)
							dist[i] = 0;
					}
				}
			}
		}

		char nd;

		// Pass 1
		for (int y = 0; y < h; ++y)
		{
			for (int x = 0; x < w; ++x)
			{
				rcCompactCell c = chf.cells[x + y * w];
				for (int i = (int)c.getIndex(), ni = (int)(c.getIndex() + c.getCount()); i < ni; ++i)
				{
					rcCompactSpan s = chf.spans[i];

					if (rcGetCon(s, 0) != RC_NOT_CONNECTED)
					{
						// (-1,0)
						int ax = x + rcGetDirOffsetX(0);
						int ay = y + rcGetDirOffsetY(0);
						int ai = (int)chf.cells[ax + ay * w].getIndex() + rcGetCon(s, 0);
						rcCompactSpan as = chf.spans[ai];
						nd = (char)rcMin((int)dist[ai] + 2, 255);
						if (nd < dist[i])
							dist[i] = nd;

						// (-1,-1)
						if (rcGetCon(as, 3) != RC_NOT_CONNECTED)
						{
							int aax = ax + rcGetDirOffsetX(3);
							int aay = ay + rcGetDirOffsetY(3);
							int aai = (int)chf.cells[aax + aay * w].getIndex() + rcGetCon(as, 3);
							nd = (char)rcMin((int)dist[aai] + 3, 255);
							if (nd < dist[i])
								dist[i] = nd;
						}
					}
					if (rcGetCon(s, 3) != RC_NOT_CONNECTED)
					{
						// (0,-1)
						int ax = x + rcGetDirOffsetX(3);
						int ay = y + rcGetDirOffsetY(3);
						int ai = (int)chf.cells[ax + ay * w].getIndex() + rcGetCon(s, 3);
						rcCompactSpan as = chf.spans[ai];
						nd = (char)rcMin((int)dist[ai] + 2, 255);
						if (nd < dist[i])
							dist[i] = nd;

						// (1,-1)
						if (rcGetCon(as, 2) != RC_NOT_CONNECTED)
						{
							int aax = ax + rcGetDirOffsetX(2);
							int aay = ay + rcGetDirOffsetY(2);
							int aai = (int)chf.cells[aax + aay * w].getIndex() + rcGetCon(as, 2);
							nd = (char)rcMin((int)dist[aai] + 3, 255);
							if (nd < dist[i])
								dist[i] = nd;
						}
					}
				}
			}
		}

		// Pass 2
		for (int y = h - 1; y >= 0; --y)
		{
			for (int x = w - 1; x >= 0; --x)
			{
				rcCompactCell c = chf.cells[x + y * w];
				for (int i = (int)c.getIndex(), ni = (int)(c.getIndex() + c.getCount()); i < ni; ++i)
				{
					rcCompactSpan s = chf.spans[i];

					if (rcGetCon(s, 2) != RC_NOT_CONNECTED)
					{
						// (1,0)
						int ax = x + rcGetDirOffsetX(2);
						int ay = y + rcGetDirOffsetY(2);
						int ai = (int)chf.cells[ax + ay * w].getIndex() + rcGetCon(s, 2);
						rcCompactSpan as = chf.spans[ai];
						nd = (char)rcMin((int)dist[ai] + 2, 255);
						if (nd < dist[i])
							dist[i] = nd;

						// (1,1)
						if (rcGetCon(as, 1) != RC_NOT_CONNECTED)
						{
							int aax = ax + rcGetDirOffsetX(1);
							int aay = ay + rcGetDirOffsetY(1);
							int aai = (int)chf.cells[aax + aay * w].getIndex() + rcGetCon(as, 1);
							nd = (char)rcMin((int)dist[aai] + 3, 255);
							if (nd < dist[i])
								dist[i] = nd;
						}
					}
					if (rcGetCon(s, 1) != RC_NOT_CONNECTED)
					{
						// (0,1)
						int ax = x + rcGetDirOffsetX(1);
						int ay = y + rcGetDirOffsetY(1);
						int ai = (int)chf.cells[ax + ay * w].getIndex() + rcGetCon(s, 1);
						rcCompactSpan as = chf.spans[ai];
						nd = (char)rcMin((int)dist[ai] + 2, 255);
						if (nd < dist[i])
							dist[i] = nd;

						// (-1,1)
						if (rcGetCon(as, 0) != RC_NOT_CONNECTED)
						{
							int aax = ax + rcGetDirOffsetX(0);
							int aay = ay + rcGetDirOffsetY(0);
							int aai = (int)chf.cells[aax + aay * w].getIndex() + rcGetCon(as, 0);
							nd = (char)rcMin((int)dist[aai] + 3, 255);
							if (nd < dist[i])
								dist[i] = nd;
						}
					}
				}
			}
		}

		char thr = (char)(radius * 2);
		for (int i = 0; i < chf.spanCount; ++i)
			if (dist[i] < thr)
				chf.areas[i] = RC_NULL_AREA;

//        rcFree(dist);

		ctx.stopTimer(rcTimerLabel.RC_TIMER_ERODE_AREA);

		return true;
	}

	public void rcMarkConvexPolyArea(rcContext ctx, float[] verts, int nverts,
									 float hmin, float hmax, char areaId,
									 rcCompactHeightfield chf)
	{
//        rcAssert(ctx);

		ctx.startTimer(rcTimerLabel.RC_TIMER_MARK_CONVEXPOLY_AREA);

		float bmin[] = new float[3], bmax[] = new float[3];
		rcVcopy(bmin, verts);
		rcVcopy(bmax, verts);
		for (int i = 1; i < nverts; ++i)
		{
			rcVmin(bmin, create3(verts, i * 3));
			rcVmax(bmax, create3(verts, i * 3));
		}
		bmin[1] = hmin;
		bmax[1] = hmax;

		int minx = (int)((bmin[0] - chf.bmin[0]) / chf.cs);
		int miny = (int)((bmin[1] - chf.bmin[1]) / chf.ch);
		int minz = (int)((bmin[2] - chf.bmin[2]) / chf.cs);
		int maxx = (int)((bmax[0] - chf.bmin[0]) / chf.cs);
		int maxy = (int)((bmax[1] - chf.bmin[1]) / chf.ch);
		int maxz = (int)((bmax[2] - chf.bmin[2]) / chf.cs);

		if (maxx < 0) return;
		if (minx >= chf.width) return;
		if (maxz < 0) return;
		if (minz >= chf.height) return;

		if (minx < 0) minx = 0;
		if (maxx >= chf.width) maxx = chf.width - 1;
		if (minz < 0) minz = 0;
		if (maxz >= chf.height) maxz = chf.height - 1;

		// TODO: Optimize.
		for (int z = minz; z <= maxz; ++z)
		{
			for (int x = minx; x <= maxx; ++x)
			{
				rcCompactCell c = chf.cells[x + z * chf.width];
				for (int i = (int)c.getIndex(), ni = (int)(c.getIndex() + c.getCount()); i < ni; ++i)
				{
					rcCompactSpan s = chf.spans[i];
					if (chf.areas[i] == RC_NULL_AREA)
						continue;
					if ((int)s.y >= miny && (int)s.y <= maxy)
					{
						float p[] = new float[3];
						p[0] = chf.bmin[0] + (x + 0.5f) * chf.cs;
						p[1] = 0;
						p[2] = chf.bmin[2] + (z + 0.5f) * chf.cs;

						if (pointInPoly(nverts, verts, p))
						{
							chf.areas[i] = areaId;
						}
					}
				}
			}
		}

		ctx.stopTimer(rcTimerLabel.RC_TIMER_MARK_CONVEXPOLY_AREA);
	}

	public static boolean pointInPoly(int nvert, float[] verts, float[] p)
	{
		int i, j;
		boolean c = false;
		for (i = 0, j = nvert - 1; i < nvert; j = i++)
		{
//            float* vi = verts[i*3];
//            float[] vj = verts[j*3];
			if (((verts[i * 3 + 2] > p[2]) != (verts[j * 32] > p[2])) &&
				(p[0] < (verts[j * 30] - verts[i * 3 + 0]) * (p[2] - verts[i * 3 + 2]) / (verts[j * 32] - verts[i * 3 + 2]) + verts[i * 3 + 0]))
				c = !c;
		}
		return c;
	}
}
