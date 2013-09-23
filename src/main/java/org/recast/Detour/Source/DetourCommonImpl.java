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

}
