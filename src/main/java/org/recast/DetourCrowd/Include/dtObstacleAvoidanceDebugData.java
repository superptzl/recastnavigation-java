package org.recast.DetourCrowd.Include;

public abstract class dtObstacleAvoidanceDebugData {
	public static final int DT_MAX_PATTERN_DIVS = 32;	///< Max numver of adaptive divs.
	public static final int DT_MAX_PATTERN_RINGS = 4;	///< Max number of adaptive rings.
	
	
	
//    public:
//    dtObstacleAvoidanceDebugData();
//    ~dtObstacleAvoidanceDebugData();

    public abstract boolean init(int maxSamples);
//	public abstract void reset();
//	public abstract void addSample(float[] vel, float ssize, float pen,
//                   float vpen, float vcpen, float spen, float tpen);
//
//	public abstract void normalizeSamples();

    public int getSampleCount() { return m_nsamples; }
//	public float[] getSampleVelocity(int i) { return m_vel[i*3]; }
	public float getSampleSize(int i) { return m_ssize[i]; }
	public float getSamplePenalty(int i) { return m_pen[i]; }
	public float getSampleDesiredVelocityPenalty(int i) { return m_vpen[i]; }
	public float getSampleCurrentVelocityPenalty(int i) { return m_vcpen[i]; }
	public float getSamplePreferredSidePenalty(int i) { return m_spen[i]; }
	public float getSampleCollisionTimePenalty(int i) { return m_tpen[i]; }

//    private:
    public int m_nsamples;
	public int m_maxSamples;
	public float[] m_vel;
	public float[] m_ssize;
	public     float[] m_pen;
	public float[] m_vpen;
	public     float[] m_vcpen;
	public float[] m_spen;
	public float[] m_tpen;
}

//dtObstacleAvoidanceDebugData[] dtAllocObstacleAvoidanceDebugData();
//void dtFreeObstacleAvoidanceDebugData(dtObstacleAvoidanceDebugData[] ptr);


