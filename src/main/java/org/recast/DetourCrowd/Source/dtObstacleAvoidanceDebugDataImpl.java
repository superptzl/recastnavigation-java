package org.recast.DetourCrowd.Source;

import org.recast.Detour.Include.DetourCommon;
import org.recast.DetourCrowd.Include.dtObstacleAvoidanceDebugData;

public class dtObstacleAvoidanceDebugDataImpl extends dtObstacleAvoidanceDebugData
{
//    dtObstacleAvoidanceDebugData* dtAllocObstacleAvoidanceDebugData()
//    {
//        void* mem = dtAlloc(sizeof(dtObstacleAvoidanceDebugData), DT_ALLOC_PERM);
//        if (!mem) return 0;
//        return new(mem) dtObstacleAvoidanceDebugData;
//    }
//
//    void dtFreeObstacleAvoidanceDebugData(dtObstacleAvoidanceDebugData* ptr)
//    {
//        if (!ptr) return;
//        ptr->~dtObstacleAvoidanceDebugData();
//        dtFree(ptr);
//    }

	public dtObstacleAvoidanceDebugDataImpl()
	{
//		:
//	    m_nsamples(0),
//	    m_maxSamples(0),
//	    m_vel(0),
//	    m_ssize(0),
//	    m_pen(0),
//	    m_vpen(0),
//	    m_vcpen(0),
//	    m_spen(0),
//	    m_tpen(0)
	}

//    ~dtObstacleAvoidanceDebugData()
//    {
//        dtFree(m_vel);
//        dtFree(m_ssize);
//        dtFree(m_pen);
//        dtFree(m_vpen);
//        dtFree(m_vcpen);
//        dtFree(m_spen);
//        dtFree(m_tpen);
//    }

	public boolean init(int maxSamples)
	{
//        dtAssert(maxSamples);
		m_maxSamples = maxSamples;

		m_vel = new float[3 * m_maxSamples];//(float*)dtAlloc(sizeof(float)*3*m_maxSamples, DT_ALLOC_PERM);
//        if (!m_vel)
//            return false;
		m_pen = new float[m_maxSamples];//(float*)dtAlloc(sizeof(float)*m_maxSamples, DT_ALLOC_PERM);
//        if (!m_pen)
//            return false;
		m_ssize = new float[m_maxSamples];//(float*)dtAlloc(sizeof(float)*m_maxSamples, DT_ALLOC_PERM);
//        if (!m_ssize)
//            return false;
		m_vpen = new float[m_maxSamples];//(float*)dtAlloc(sizeof(float)*m_maxSamples, DT_ALLOC_PERM);
//        if (!m_vpen)
//            return false;
		m_vcpen = new float[m_maxSamples];//(float*)dtAlloc(sizeof(float)*m_maxSamples, DT_ALLOC_PERM);
//        if (!m_vcpen)
//            return false;
		m_spen = new float[m_maxSamples];//(float*)dtAlloc(sizeof(float)*m_maxSamples, DT_ALLOC_PERM);
//        if (!m_spen)
//            return false;
		m_tpen = new float[m_maxSamples];//(float*)dtAlloc(sizeof(float)*m_maxSamples, DT_ALLOC_PERM);
//        if (!m_tpen)
//            return false;

		return true;
	}

	public void reset()
	{
		m_nsamples = 0;
	}

	public void addSample(float[] vel, float ssize, float pen,
						  float vpen, float vcpen, float spen, float tpen)
	{
		if (m_nsamples >= m_maxSamples)
			return;
//        dtAssert(m_vel);
//        dtAssert(m_ssize);
//        dtAssert(m_pen);
//        dtAssert(m_vpen);
//        dtAssert(m_vcpen);
//        dtAssert(m_spen);
//        dtAssert(m_tpen);
		DetourCommon.dtVcopy(m_vel, m_nsamples * 3, vel, 0);
		m_ssize[m_nsamples] = ssize;
		m_pen[m_nsamples] = pen;
		m_vpen[m_nsamples] = vpen;
		m_vcpen[m_nsamples] = vcpen;
		m_spen[m_nsamples] = spen;
		m_tpen[m_nsamples] = tpen;
		m_nsamples++;
	}

	public static void normalizeArray(float[] arr, int n)
	{
		// Normalize penaly range.
		float minPen = Float.MAX_VALUE;
		float maxPen = -Float.MAX_VALUE;
		for (int i = 0; i < n; ++i)
		{
			minPen = DetourCommon.dtMin(minPen, arr[i]);
			maxPen = DetourCommon.dtMax(maxPen, arr[i]);
		}
		float penRange = maxPen - minPen;
		float s = penRange > 0.001f ? (1.0f / penRange) : 1;
		for (int i = 0; i < n; ++i)
			arr[i] = DetourCommon.dtClamp((arr[i] - minPen) * s, 0.0f, 1.0f);
	}

	public void normalizeSamples()
	{
		normalizeArray(m_pen, m_nsamples);
		normalizeArray(m_vpen, m_nsamples);
		normalizeArray(m_vcpen, m_nsamples);
		normalizeArray(m_spen, m_nsamples);
		normalizeArray(m_tpen, m_nsamples);
	}


}
