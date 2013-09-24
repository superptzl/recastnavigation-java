package org.recast.Detour.Source;

import org.recast.Detour.Include.DetourCommon;

public class DetourCommonImpl extends DetourCommon {
    public boolean dtDistancePtPolyEdgesSqr(float[] pt, float[] verts, int nverts,
                                  float[]ed, float[] et)
    {
        // TODO: Replace pnpoly with triArea2D tests?
        int i, j;
        boolean c = false;
        for (i = 0, j = nverts-1; i < nverts; j = i++)
        {
            float[] vi = verts;//[i*3];
            int viIndex = i*3;
            float[] vj = verts;//[j*3];
            int vjIndex = j*3;
            if (((vi[viIndex+2] > pt[2]) != (vj[vjIndex+2] > pt[2])) &&
                    (pt[0] < (vj[vjIndex+0]-vi[0]) * (pt[2]-vi[viIndex+2]) / (vj[vjIndex+2]-vi[viIndex+2]) + vi[viIndex+0]) )
                c = !c;
            ed[j] = dtDistancePtSegSqr2D(pt, vj, vjIndex, vi, viIndex, et, j);
        }
        return c;
    }

    public float dtDistancePtSegSqr2D(float[] pt, float[] p, int pIndex, float[] q, int qIndex, float[] t, int tIndex)
    {
        float pqx = q[qIndex+0] - p[pIndex+0];
        float pqz = q[qIndex+2] - p[pIndex+2];
        float dx = pt[0] - p[pIndex+0];
        float dz = pt[2] - p[pIndex+2];
        float d = pqx*pqx + pqz*pqz;
        t[tIndex] = pqx*dx + pqz*dz;
        if (d > 0) t[tIndex] /= d;
        if (t[tIndex] < 0) t[tIndex] = 0;
        else if (t[tIndex] > 1) t[tIndex] = 1;
        dx = p[pIndex+0] + t[tIndex]*pqx - pt[0];
        dz = p[pIndex+2] + t[tIndex]*pqz - pt[2];
        return dx*dx + dz*dz;
    }


	public boolean dtClosestHeightPointTriangle(float[] p, float[] a, int aIndex, float[] b, int bIndex, float[] c, int cIndex, float[] h)
	{
		float v0[] = new float[3], v1[] = new float[3], v2[] = new float[3];
		dtVsub(v0, c, cIndex, a, aIndex);
		dtVsub(v1, b, bIndex, a, aIndex);
		dtVsub(v2, p, 0, a, aIndex);

		float dot00 = dtVdot2D(v0, v0);
		float dot01 = dtVdot2D(v0, v1);
		float dot02 = dtVdot2D(v0, v2);
		float dot11 = dtVdot2D(v1, v1);
		float dot12 = dtVdot2D(v1, v2);

		// Compute barycentric coordinates
		float invDenom = 1.0f / (dot00 * dot11 - dot01 * dot01);
		float u = (dot11 * dot02 - dot01 * dot12) * invDenom;
		float v = (dot00 * dot12 - dot01 * dot02) * invDenom;

		// The (sloppy) epsilon is needed to allow to get height of points which
		// are interpolated along the edges of the triangles.
		float EPS = 1e-4f;

		// If point lies inside the triangle, return interpolated ycoord.
		if (u >= -EPS && v >= -EPS && (u+v) <= 1+EPS)
		{
			h[0] = a[aIndex+1] + v0[1]*u + v1[1]*v;
			return true;
		}

		return false;
	}

    public float dtDistancePtSegSqr2D(float[] pt, float[] p, float[] q, float[] t)
    {
        float pqx = q[0] - p[0];
        float pqz = q[2] - p[2];
        float dx = pt[0] - p[0];
        float dz = pt[2] - p[2];
        float d = pqx*pqx + pqz*pqz;
        t[0] = pqx*dx + pqz*dz;
        if (d > 0) t[0] /= d;
        if (t[0] < 0) t[0] = 0;
        else if (t[0] > 1) t[0] = 1;
        dx = p[0] + t[0]*pqx - pt[0];
        dz = p[2] + t[0]*pqz - pt[2];
        return dx*dx + dz*dz;
    }

	public boolean dtIntersectSegmentPoly2D(float[] p0, float[] p1,
								  float[] verts, int nverts,
								  float[] tmin, float[] tmax,
								  int[] segMin, int[] segMax)
	{
		float EPS = 0.00000001f;

		tmin[0] = 0;
		tmax[0] = 1;
		segMin[0] = -1;
		segMax[0] = -1;

		float dir[] = new float[3];
		dtVsub(dir, p1, p0);

		for (int i = 0, j = nverts-1; i < nverts; j=i++)
		{
			float edge[] = new float[3], diff[] = new float[3];
			dtVsub(edge, verts, i*3, verts, j*3);
			dtVsub(diff, p0, 0, verts, j*3);
			float n = dtVperp2D(edge, diff);
			float d = dtVperp2D(dir, edge);
			if (Math.abs(d) < EPS)
			{
				// S is nearly parallel to this edge
				if (n < 0)
					return false;
				else
					continue;
			}
			float t = n / d;
			if (d < 0)
			{
				// segment S is entering across this edge
				if (t > tmin[0])
				{
					tmin[0] = t;
					segMin[0] = j;
					// S enters after leaving polygon
					if (tmin[0] > tmax[0])
						return false;
				}
			}
			else
			{
				// segment S is leaving across this edge
				if (t < tmax[0])
				{
					tmax[0] = t;
					segMax[0] = j;
					// S leaves before entering polygon
					if (tmax[0] < tmin[0])
						return false;
				}
			}
		}

		return true;
	}

	public boolean dtIntersectSegSeg2D(float[] ap, int apIndex, float[] aq,
							 float[] bp, float[] bq,
							 float[] s, float[] t)
	{
		float u[] = new float[3], v[] = new float[3], w[] = new float[3];
		dtVsub(u,aq,0,ap,apIndex);
		dtVsub(v,bq,bp);
		dtVsub(w,ap,apIndex,bp,0);
		float d = vperpXZ(u,v);
		if (Math.abs(d) < 1e-6f) return false;
		s[0] = vperpXZ(v,w) / d;
		t[0] = vperpXZ(u,w) / d;
		return true;
	}

	public static float vperpXZ(float[] a, float[] b) { return a[0]*b[2] - a[2]*b[0]; }

	public boolean dtPointInPolygon(float[] pt, float[] verts, int nverts)
	{
		// TODO: Replace pnpoly with triArea2D tests?
		int i, j;
		boolean c = false;
		for (i = 0, j = nverts-1; i < nverts; j = i++)
		{
			float[] vi = verts;//[i*3];
			float[] vj = verts;//[j*3];
			if (((vi[i*3+2] > pt[2]) != (vj[j*3+2] > pt[2])) &&
				(pt[0] < (vj[j*3+0]-vi[i*3+0]) * (pt[2]-vi[i*3+2]) / (vj[j*3+2]-vi[i*3+2]) + vi[i*3+0]) )
				c = !c;
		}
		return c;
	}
}
