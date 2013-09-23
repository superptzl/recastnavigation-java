package org.recast.DetourCrowd.Source;

import org.recast.DetourCrowd.Include.dtObstacleAvoidanceQuery;

public class dtObstacleAvoidanceQueryImpl extends dtObstacleAvoidanceQuery {
    public dtObstacleAvoidanceQueryImpl()
    {
        :
        m_maxCircles(0),
                m_circles(0),
                m_ncircles(0),
                m_maxSegments(0),
                m_segments(0),
                m_nsegments(0)
    }

//    ~dtObstacleAvoidanceQuery()
//    {
//        dtFree(m_circles);
//        dtFree(m_segments);
//    }

    bool init(const int maxCircles, const int maxSegments)
    {
        m_maxCircles = maxCircles;
        m_ncircles = 0;
        m_circles = (dtObstacleCircle*)dtAlloc(sizeof(dtObstacleCircle)*m_maxCircles, DT_ALLOC_PERM);
        if (!m_circles)
            return false;
        memset(m_circles, 0, sizeof(dtObstacleCircle)*m_maxCircles);

        m_maxSegments = maxSegments;
        m_nsegments = 0;
        m_segments = (dtObstacleSegment*)dtAlloc(sizeof(dtObstacleSegment)*m_maxSegments, DT_ALLOC_PERM);
        if (!m_segments)
            return false;
        memset(m_segments, 0, sizeof(dtObstacleSegment)*m_maxSegments);

        return true;
    }

    void reset()
    {
        m_ncircles = 0;
        m_nsegments = 0;
    }

    void addCircle(const float* pos, const float rad,
                                             const float* vel, const float* dvel)
    {
        if (m_ncircles >= m_maxCircles)
            return;

        dtObstacleCircle* cir = &m_circles[m_ncircles++];
        dtVcopy(cir->p, pos);
        cir->rad = rad;
        dtVcopy(cir->vel, vel);
        dtVcopy(cir->dvel, dvel);
    }

    void addSegment(const float* p, const float* q)
    {
        if (m_nsegments > m_maxSegments)
            return;

        dtObstacleSegment* seg = &m_segments[m_nsegments++];
        dtVcopy(seg->p, p);
        dtVcopy(seg->q, q);
    }

    void prepare(const float* pos, const float* dvel)
    {
        // Prepare obstacles
        for (int i = 0; i < m_ncircles; ++i)
        {
            dtObstacleCircle* cir = &m_circles[i];

            // Side
            const float* pa = pos;
            const float* pb = cir->p;

            const float orig[3] = {0,0};
            float dv[3];
            dtVsub(cir->dp,pb,pa);
            dtVnormalize(cir->dp);
            dtVsub(dv, cir->dvel, dvel);

            const float a = dtTriArea2D(orig, cir->dp,dv);
            if (a < 0.01f)
            {
                cir->np[0] = -cir->dp[2];
                cir->np[2] = cir->dp[0];
            }
            else
            {
                cir->np[0] = cir->dp[2];
                cir->np[2] = -cir->dp[0];
            }
        }

        for (int i = 0; i < m_nsegments; ++i)
        {
            dtObstacleSegment* seg = &m_segments[i];

            // Precalc if the agent is really close to the segment.
            const float r = 0.01f;
            float t;
            seg->touch = dtDistancePtSegSqr2D(pos, seg->p, seg->q, t) < dtSqr(r);
        }
    }

    float processSample(const float* vcand, const float cs,
                                                  const float* pos, const float rad,
                                                  const float* vel, const float* dvel,
                                                  dtObstacleAvoidanceDebugData* debug)
    {
        // Find min time of impact and exit amongst all obstacles.
        float tmin = m_params.horizTime;
        float side = 0;
        int nside = 0;

        for (int i = 0; i < m_ncircles; ++i)
        {
            const dtObstacleCircle* cir = &m_circles[i];

            // RVO
            float vab[3];
            dtVscale(vab, vcand, 2);
            dtVsub(vab, vab, vel);
            dtVsub(vab, vab, cir->vel);

            // Side
            side += dtClamp(dtMin(dtVdot2D(cir->dp,vab)*0.5f+0.5f, dtVdot2D(cir->np,vab)*2), 0.0f, 1.0f);
            nside++;

            float htmin = 0, htmax = 0;
            if (!sweepCircleCircle(pos,rad, vab, cir->p,cir->rad, htmin, htmax))
                continue;

            // Handle overlapping obstacles.
            if (htmin < 0.0f && htmax > 0.0f)
            {
                // Avoid more when overlapped.
                htmin = -htmin * 0.5f;
            }

            if (htmin >= 0.0f)
            {
                // The closest obstacle is somewhere ahead of us, keep track of nearest obstacle.
                if (htmin < tmin)
                    tmin = htmin;
            }
        }

        for (int i = 0; i < m_nsegments; ++i)
        {
            const dtObstacleSegment* seg = &m_segments[i];
            float htmin = 0;

            if (seg->touch)
            {
                // Special case when the agent is very close to the segment.
                float sdir[3], snorm[3];
                dtVsub(sdir, seg->q, seg->p);
                snorm[0] = -sdir[2];
                snorm[2] = sdir[0];
                // If the velocity is pointing towards the segment, no collision.
                if (dtVdot2D(snorm, vcand) < 0.0f)
                    continue;
                // Else immediate collision.
                htmin = 0.0f;
            }
            else
            {
                if (!isectRaySeg(pos, vcand, seg->p, seg->q, htmin))
                    continue;
            }

            // Avoid less when facing walls.
            htmin *= 2.0f;

            // The closest obstacle is somewhere ahead of us, keep track of nearest obstacle.
            if (htmin < tmin)
                tmin = htmin;
        }

        // Normalize side bias, to prevent it dominating too much.
        if (nside)
            side /= nside;

        const float vpen = m_params.weightDesVel * (dtVdist2D(vcand, dvel) * m_invVmax);
        const float vcpen = m_params.weightCurVel * (dtVdist2D(vcand, vel) * m_invVmax);
        const float spen = m_params.weightSide * side;
        const float tpen = m_params.weightToi * (1.0f/(0.1f+tmin*m_invHorizTime));

        const float penalty = vpen + vcpen + spen + tpen;

        // Store different penalties for debug viewing
        if (debug)
            debug->addSample(vcand, cs, penalty, vpen, vcpen, spen, tpen);

        return penalty;
    }

    int sampleVelocityGrid(const float* pos, const float rad, const float vmax,
                                                     const float* vel, const float* dvel, float* nvel,
                                                     const dtObstacleAvoidanceParams* params,
                                                     dtObstacleAvoidanceDebugData* debug)
    {
        prepare(pos, dvel);

        memcpy(&m_params, params, sizeof(dtObstacleAvoidanceParams));
        m_invHorizTime = 1.0f / m_params.horizTime;
        m_vmax = vmax;
        m_invVmax = 1.0f / vmax;

        dtVset(nvel, 0,0,0);

        if (debug)
            debug->reset();

        const float cvx = dvel[0] * m_params.velBias;
        const float cvz = dvel[2] * m_params.velBias;
        const float cs = vmax * 2 * (1 - m_params.velBias) / (float)(m_params.gridSize-1);
        const float half = (m_params.gridSize-1)*cs*0.5f;

        float minPenalty = FLT_MAX;
        int ns = 0;

        for (int y = 0; y < m_params.gridSize; ++y)
        {
            for (int x = 0; x < m_params.gridSize; ++x)
            {
                float vcand[3];
                vcand[0] = cvx + x*cs - half;
                vcand[1] = 0;
                vcand[2] = cvz + y*cs - half;

                if (dtSqr(vcand[0])+dtSqr(vcand[2]) > dtSqr(vmax+cs/2)) continue;

                const float penalty = processSample(vcand, cs, pos,rad,vel,dvel, debug);
                ns++;
                if (penalty < minPenalty)
                {
                    minPenalty = penalty;
                    dtVcopy(nvel, vcand);
                }
            }
        }

        return ns;
    }


    int sampleVelocityAdaptive(const float* pos, const float rad, const float vmax,
                                                         const float* vel, const float* dvel, float* nvel,
                                                         const dtObstacleAvoidanceParams* params,
                                                         dtObstacleAvoidanceDebugData* debug)
    {
        prepare(pos, dvel);

        memcpy(&m_params, params, sizeof(dtObstacleAvoidanceParams));
        m_invHorizTime = 1.0f / m_params.horizTime;
        m_vmax = vmax;
        m_invVmax = 1.0f / vmax;

        dtVset(nvel, 0,0,0);

        if (debug)
            debug->reset();

        // Build sampling pattern aligned to desired velocity.
        float pat[(DT_MAX_PATTERN_DIVS*DT_MAX_PATTERN_RINGS+1)*2];
        int npat = 0;

        const int ndivs = (int)m_params.adaptiveDivs;
        const int nrings= (int)m_params.adaptiveRings;
        const int depth = (int)m_params.adaptiveDepth;

        const int nd = dtClamp(ndivs, 1, DT_MAX_PATTERN_DIVS);
        const int nr = dtClamp(nrings, 1, DT_MAX_PATTERN_RINGS);
        const float da = (1.0f/nd) * DT_PI*2;
        const float dang = atan2f(dvel[2], dvel[0]);

        // Always add sample at zero
        pat[npat*2+0] = 0;
        pat[npat*2+1] = 0;
        npat++;

        for (int j = 0; j < nr; ++j)
        {
            const float r = (float)(nr-j)/(float)nr;
            float a = dang + (j&1)*0.5f*da;
            for (int i = 0; i < nd; ++i)
            {
                pat[npat*2+0] = cosf(a)*r;
                pat[npat*2+1] = sinf(a)*r;
                npat++;
                a += da;
            }
        }

        // Start sampling.
        float cr = vmax * (1.0f - m_params.velBias);
        float res[3];
        dtVset(res, dvel[0] * m_params.velBias, 0, dvel[2] * m_params.velBias);
        int ns = 0;

        for (int k = 0; k < depth; ++k)
        {
            float minPenalty = FLT_MAX;
            float bvel[3];
            dtVset(bvel, 0,0,0);

            for (int i = 0; i < npat; ++i)
            {
                float vcand[3];
                vcand[0] = res[0] + pat[i*2+0]*cr;
                vcand[1] = 0;
                vcand[2] = res[2] + pat[i*2+1]*cr;

                if (dtSqr(vcand[0])+dtSqr(vcand[2]) > dtSqr(vmax+0.001f)) continue;

                const float penalty = processSample(vcand,cr/10, pos,rad,vel,dvel, debug);
                ns++;
                if (penalty < minPenalty)
                {
                    minPenalty = penalty;
                    dtVcopy(bvel, vcand);
                }
            }

            dtVcopy(res, bvel);

            cr *= 0.5f;
        }

        dtVcopy(nvel, res);

        return ns;
    }


}
