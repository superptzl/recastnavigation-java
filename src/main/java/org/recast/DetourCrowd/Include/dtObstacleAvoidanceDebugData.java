package org.recast.DetourCrowd.Include;

public abstract class dtObstacleAvoidanceDebugData {
    public:
    dtObstacleAvoidanceDebugData();
    ~dtObstacleAvoidanceDebugData();

    bool init(const int maxSamples);
    void reset();
    void addSample(const float* vel, const float ssize, const float pen,
                   const float vpen, const float vcpen, const float spen, const float tpen);

    void normalizeSamples();

    inline int getSampleCount() const { return m_nsamples; }
    inline const float* getSampleVelocity(const int i) const { return &m_vel[i*3]; }
    inline float getSampleSize(const int i) const { return m_ssize[i]; }
    inline float getSamplePenalty(const int i) const { return m_pen[i]; }
    inline float getSampleDesiredVelocityPenalty(const int i) const { return m_vpen[i]; }
    inline float getSampleCurrentVelocityPenalty(const int i) const { return m_vcpen[i]; }
    inline float getSamplePreferredSidePenalty(const int i) const { return m_spen[i]; }
    inline float getSampleCollisionTimePenalty(const int i) const { return m_tpen[i]; }

    private:
    int m_nsamples;
    int m_maxSamples;
    float* m_vel;
    float* m_ssize;
    float* m_pen;
    float* m_vpen;
    float* m_vcpen;
    float* m_spen;
    float* m_tpen;
};

dtObstacleAvoidanceDebugData* dtAllocObstacleAvoidanceDebugData();
void dtFreeObstacleAvoidanceDebugData(dtObstacleAvoidanceDebugData* ptr);


static const int DT_MAX_PATTERN_DIVS = 32;	///< Max numver of adaptive divs.
static const int DT_MAX_PATTERN_RINGS = 4;	///< Max number of adaptive rings.

struct dtObstacleAvoidanceParams
        {
        float velBias;
float weightDesVel;
float weightCurVel;
float weightSide;
float weightToi;
float horizTime;
unsigned char gridSize;	///< grid
unsigned char adaptiveDivs;	///< adaptive
unsigned char adaptiveRings;	///< adaptive
unsigned char adaptiveDepth;	///< adaptive
}
