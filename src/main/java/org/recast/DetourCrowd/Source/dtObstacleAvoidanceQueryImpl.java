package org.recast.DetourCrowd.Source;

import org.recast.Detour.Include.DetourCommon;
import org.recast.Detour.Source.DetourCommonImpl;
import org.recast.DetourCrowd.Include.*;

public class dtObstacleAvoidanceQueryImpl extends dtObstacleAvoidanceQuery {
    public dtObstacleAvoidanceQueryImpl()
    {
//        :
//        m_maxCircles(0),
//                m_circles(0),
//                m_ncircles(0),
//                m_maxSegments(0),
//                m_segments(0),
//                m_nsegments(0)
    }

//    ~dtObstacleAvoidanceQuery()
//    {
//        dtFree(m_circles);
//        dtFree(m_segments);
//    }

    public boolean init(int maxCircles, int maxSegments)
    {
        m_maxCircles = maxCircles;
        m_ncircles = 0;
        m_circles = new dtObstacleCircle[m_maxCircles];//)dtAlloc(sizeof(dtObstacleCircle)*m_maxCircles, DT_ALLOC_PERM);
        for (int i = 0; i < m_circles.length; i++) {
            m_circles[i] = new dtObstacleCircle();
        }
//        if (!m_circles)
//            return false;
//        memset(m_circles, 0, sizeof(dtObstacleCircle)*m_maxCircles);

        m_maxSegments = maxSegments;
        m_nsegments = 0;
        m_segments = new dtObstacleSegment[m_maxSegments];//*)dtAlloc(sizeof(dtObstacleSegment)*m_maxSegments, DT_ALLOC_PERM);
//        if (!m_segments)
//            return false;
//        memset(m_segments, 0, sizeof(dtObstacleSegment)*m_maxSegments);
        for (int i = 0; i < m_segments.length; i++) {
            m_segments[i] = new dtObstacleSegment();
        }

        return true;
    }

    public void reset()
    {
        m_ncircles = 0;
        m_nsegments = 0;
    }

    public void addCircle(float[] pos, float rad,
                                             float[] vel, float[] dvel)
    {
        if (m_ncircles >= m_maxCircles)
            return;

        dtObstacleCircle cir = m_circles[m_ncircles++];
        DetourCommon.dtVcopy(cir.p, pos);
        cir.rad = rad;
        DetourCommon.dtVcopy(cir.vel, vel);
        DetourCommon.dtVcopy(cir.dvel, dvel);
    }
//
    public void addSegment(float[] p, float[] q)
    {
        addSegment(p, q, 0);
    }

    public void addSegment(float[] p, float[] q, int qIndex)
    {
        if (m_nsegments > m_maxSegments)
            return;

        dtObstacleSegment seg = m_segments[m_nsegments++];
        DetourCommon.dtVcopy(seg.p, p);
        DetourCommon.dtVcopy(seg.q, 0, q, qIndex);
    }

    public void prepare(float[] pos, float[] dvel)
    {
        // Prepare obstacles
        for (int i = 0; i < m_ncircles; ++i)
        {
            dtObstacleCircle cir = m_circles[i];

            // Side
            float[]  pa = pos;
            float[] pb = cir.p;

            float orig[] = {0,0,0};
            float dv[] = new float[3];
            DetourCommon.dtVsub(cir.dp, pb, pa);
            DetourCommon.dtVnormalize(cir.dp);
            DetourCommon.dtVsub(dv, cir.dvel, dvel);

            float a = DetourCommon.dtTriArea2D(orig, cir.dp, dv);
            if (a < 0.01f)
            {
                cir.np[0] = -cir.dp[2];
                cir.np[2] = cir.dp[0];
            }
            else
            {
                cir.np[0] = cir.dp[2];
                cir.np[2] = -cir.dp[0];
            }
        }

        for (int i = 0; i < m_nsegments; ++i)
        {
            dtObstacleSegment seg = m_segments[i];

            // Precalc if the agent is really close to the segment.
            float r = 0.01f;
            float t[] = new float[1];
            seg.touch = new DetourCommonImpl().dtDistancePtSegSqr2D(pos, seg.p, seg.q, t) < DetourCommon.dtSqr(r);
        }
    }

    public float processSample( float[] vcand,  float cs,
                                                   float[] pos,  float rad,
                                                   float[] vel,  float[] dvel,
                                                  dtObstacleAvoidanceDebugData debug)
    {
        // Find min time of impact and exit amongst all obstacles.
        float tmin = m_params.horizTime;
        float side = 0;
        int nside = 0;

        for (int i = 0; i < m_ncircles; ++i)
        {
             dtObstacleCircle cir = m_circles[i];

            // RVO
            float vab[] = new float[3];
            DetourCommon.dtVscale(vab, vcand, 2);
            DetourCommon.dtVsub(vab, vab, vel);
            DetourCommon.dtVsub(vab, vab, cir.vel);

            // Side
            side += DetourCommon.dtClamp(DetourCommon.dtMin(DetourCommon.dtVdot2D(cir.dp,vab)*0.5f+0.5f, DetourCommon.dtVdot2D(cir.np,vab)*2), 0.0f, 1.0f);
            nside++;

            float htmin[] = new float[1], htmax[] = new float[1];
            if (sweepCircleCircle(pos,rad, vab, cir.p,cir.rad, htmin, htmax) == 0)
                continue;

            // Handle overlapping obstacles.
            if (htmin[0] < 0.0f && htmax[0] > 0.0f)
            {
                // Avoid more when overlapped.
                htmin[0] = -htmin[0] * 0.5f;
            }

            if (htmin[0] >= 0.0f)
            {
                // The closest obstacle is somewhere ahead of us, keep track of nearest obstacle.
                if (htmin[0] < tmin)
                    tmin = htmin[0];
            }
        }

        for (int i = 0; i < m_nsegments; ++i)
        {
             dtObstacleSegment seg = m_segments[i];
            float htmin[] = new float[1];

            if (seg.touch)
            {
                // Special case when the agent is very close to the segment.
                float sdir[] = new float[3], snorm[] = new float[3] ;
                DetourCommon.dtVsub(sdir, seg.q, seg.p);
                snorm[0] = -sdir[2];
                snorm[2] = sdir[0];
                // If the velocity is pointing towards the segment, no collision.
                if (DetourCommon.dtVdot2D(snorm, vcand) < 0.0f)
                    continue;
                // Else immediate collision.
                htmin[0] = 0.0f;
            }
            else
            {
                if (isectRaySeg(pos, vcand, seg.p, seg.q, htmin) == 0)
                    continue;
            }

            // Avoid less when facing walls.
            htmin[0] *= 2.0f;

            // The closest obstacle is somewhere ahead of us, keep track of nearest obstacle.
            if (htmin[0] < tmin)
                tmin = htmin[0];
        }

        // Normalize side bias, to prevent it dominating too much.
        if (nside != 0)
            side /= nside;

         float vpen = m_params.weightDesVel * (DetourCommon.dtVdist2D(vcand, dvel) * m_invVmax);
         float vcpen = m_params.weightCurVel * (DetourCommon.dtVdist2D(vcand, vel) * m_invVmax);
         float spen = m_params.weightSide * side;
         float tpen = m_params.weightToi * (1.0f/(0.1f+tmin*m_invHorizTime));

         float penalty = vpen + vcpen + spen + tpen;

        // Store different penalties for debug viewing
        if (debug != null)
            debug.addSample(vcand, cs, penalty, vpen, vcpen, spen, tpen);

        return penalty;
    }

    public int sampleVelocityGrid(float[] pos, float rad, float vmax,
                                                     float[] vel, float[] dvel, float[] nvel,
                                                     dtObstacleAvoidanceParams params,
                                                     dtObstacleAvoidanceDebugData debug)
    {
        prepare(pos, dvel);

//        memcpy(&m_params, params, sizeof(dtObstacleAvoidanceParams));
//        System.arraycopy(params.);
        m_params = params.clone();
        m_invHorizTime = 1.0f / m_params.horizTime;
        m_vmax = vmax;
        m_invVmax = 1.0f / vmax;

        DetourCommon.dtVset(nvel, 0,0,0);

        if (debug != null)
            debug.reset();

         float cvx = dvel[0] * m_params.velBias;
         float cvz = dvel[2] * m_params.velBias;
         float cs = vmax * 2 * (1 - m_params.velBias) / (float)(m_params.gridSize-1);
         float half = (m_params.gridSize-1)*cs*0.5f;

        float minPenalty = Float.MAX_VALUE;
        int ns = 0;

        for (int y = 0; y < m_params.gridSize; ++y)
        {
            for (int x = 0; x < m_params.gridSize; ++x)
            {
                float vcand[] = new float[3];
                vcand[0] = cvx + x*cs - half;
                vcand[1] = 0;
                vcand[2] = cvz + y*cs - half;

                if (DetourCommon.dtSqr(vcand[0])+DetourCommon.dtSqr(vcand[2]) > DetourCommon.dtSqr(vmax+cs/2)) continue;

                 float penalty = processSample(vcand, cs, pos,rad,vel,dvel, debug);
                ns++;
                if (penalty < minPenalty)
                {
                    minPenalty = penalty;
                    DetourCommon.dtVcopy(nvel, vcand);
                }
            }
        }

        return ns;
    }


public     int sampleVelocityAdaptive(float[] pos, float rad, float vmax,
                                                         float[] vel, float[] dvel, float[] nvel,
                                                         dtObstacleAvoidanceParams params,
                                                         dtObstacleAvoidanceDebugData debug)
    {
        prepare(pos, dvel);

//        memcpy(&m_params, params, sizeof(dtObstacleAvoidanceParams));
        m_params = params.clone();
        m_invHorizTime = 1.0f / m_params.horizTime;
        m_vmax = vmax;
        m_invVmax = 1.0f / vmax;

        DetourCommon.dtVset(nvel, 0,0,0);

        if (debug != null)
            debug.reset();

        // Build sampling pattern aligned to desired velocity.
        float pat[] = new float[(dtObstacleAvoidanceDebugData.DT_MAX_PATTERN_DIVS*dtObstacleAvoidanceDebugData.DT_MAX_PATTERN_RINGS+1)*2];
        int npat = 0;

         int ndivs = (int)m_params.adaptiveDivs;
         int nrings= (int)m_params.adaptiveRings;
         int depth = (int)m_params.adaptiveDepth;

         int nd = DetourCommon.dtClamp(ndivs, 1, dtObstacleAvoidanceDebugData.DT_MAX_PATTERN_DIVS);
         int nr = DetourCommon.dtClamp(nrings, 1, dtObstacleAvoidanceDebugData.DT_MAX_PATTERN_RINGS);
         float da = (float)((1.0f/nd) * Math.PI*2);
         float dang = (float)Math.atan2(dvel[2], dvel[0]);

        // Always add sample at zero
        pat[npat*2+0] = 0;
        pat[npat*2+1] = 0;
        npat++;

        for (int j = 0; j < nr; ++j)
        {
             float r = (float)(nr-j)/(float)nr;
            float a = dang + (j&1)*0.5f*da;
            for (int i = 0; i < nd; ++i)
            {
                pat[npat*2+0] = (float)Math.cos(a)*r;
                pat[npat*2+1] = (float)Math.sin(a)*r;
                npat++;
                a += da;
            }
        }

        // Start sampling.
        float cr = vmax * (1.0f - m_params.velBias);
        float res[] = new float[3];
        DetourCommon.dtVset(res, dvel[0] * m_params.velBias, 0, dvel[2] * m_params.velBias);
        int ns = 0;

        for (int k = 0; k < depth; ++k)
        {
            float minPenalty = Float.MAX_VALUE;
            float bvel[] = new float[3];
            DetourCommon.dtVset(bvel, 0,0,0);

            for (int i = 0; i < npat; ++i)
            {
                float vcand[] = new float[3];
                vcand[0] = res[0] + pat[i*2+0]*cr;
                vcand[1] = 0;
                vcand[2] = res[2] + pat[i*2+1]*cr;

                if (DetourCommon.dtSqr(vcand[0])+DetourCommon.dtSqr(vcand[2]) > DetourCommon.dtSqr(vmax+0.001f)) continue;

                 float penalty = processSample(vcand,cr/10, pos,rad,vel,dvel, debug);
                ns++;
                if (penalty < minPenalty)
                {
                    minPenalty = penalty;
                    DetourCommon.dtVcopy(bvel, vcand);
                }
            }

            DetourCommon.dtVcopy(res, bvel);

            cr *= 0.5f;
        }

        DetourCommon.dtVcopy(nvel, res);

        return ns;
    }



    public static int sweepCircleCircle(float[] c0, float r0, float[] v,
                                 float[] c1, float r1,
                                 float[] tmin, float[] tmax)
    {
        float EPS = 0.0001f;
        float s[] = new float[3];
        DetourCommon.dtVsub(s,c1,c0);
        float r = r0+r1;
        float c = DetourCommon.dtVdot2D(s,s) - r*r;
        float a = DetourCommon.dtVdot2D(v,v);
        if (a < EPS) return 0;	// not moving

        // Overlap, calc time to exit.
        float b = DetourCommon.dtVdot2D(v,s);
        float d = b*b - a*c;
        if (d < 0.0f) return 0; // no intersection.
        a = 1.0f / a;
        float rd = DetourCommon.dtSqrt(d);
        tmin[0] = (b - rd) * a;
        tmax[0] = (b + rd) * a;
        return 1;
    }

    public static int isectRaySeg(float[] ap, float[] u,
                           float[] bp, float[] bq,
                           float[] t)
    {
        float v[] = new float[3], w[] = new float[3];
        DetourCommon.dtVsub(v,bq,bp);
        DetourCommon.dtVsub(w,ap,bp);
        float d = DetourCommon.dtVperp2D(u,v);
        if (Math.abs(d) < 1e-6f) return 0;
        d = 1.0f/d;
        t[0] = DetourCommon.dtVperp2D(v,w) * d;
        if (t[0] < 0 || t[0] > 1) return 0;
        float s = DetourCommon.dtVperp2D(u,w) * d;
        if (s < 0 || s > 1) return 0;
        return 1;
    }

}
