package org.recast.DetourCrowd.Include;

public abstract class dtObstacleAvoidanceQuery
{
//    public:
//    dtObstacleAvoidanceQuery();
//    ~dtObstacleAvoidanceQuery();

	public abstract boolean init(int maxCircles, int maxSegments);

	//
	public abstract void reset();

	//
	public abstract void addCircle(float[] pos, float rad,
								   float[] vel, float[] dvel);

	//
	public abstract void addSegment(float[] p, float[] q);

	public abstract void addSegment(float[] p, float[] q, int qIndex);

	//
	public abstract int sampleVelocityGrid(float[] pos, float rad, float vmax,
										   float[] vel, float[] dvel, float[] nvel,
										   dtObstacleAvoidanceParams params,
										   dtObstacleAvoidanceDebugData debug/* = 0*/);

	//
	public abstract int sampleVelocityAdaptive(float[] pos, float rad, float vmax,
											   float[] vel, float[] dvel, float[] nvel,
											   dtObstacleAvoidanceParams params,
											   dtObstacleAvoidanceDebugData debug/* = 0*/);

	//
//    public abstract inline int getObstacleCircleCount() const { return m_ncircles; }
//    public abstract const dtObstacleCircle* getObstacleCircle(const int i) { return &m_circles[i]; }
//
//    public abstract inline int getObstacleSegmentCount() const { return m_nsegments; }
//    public abstract const dtObstacleSegment* getObstacleSegment(const int i) { return &m_segments[i]; }
//
////    private:
//
	public abstract void prepare(float[] pos, float[] dvel);

	//
	public abstract float processSample(float[] vcand, float cs,
										float[] pos, float rad,
										float[] vel, float[] dvel,
										dtObstacleAvoidanceDebugData debug);
//
//    public abstract dtObstacleCircle* insertCircle(const float dist);
//    public abstract dtObstacleSegment* insertSegment(const float dist);

	public dtObstacleAvoidanceParams m_params;
	public float m_invHorizTime;
	public float m_vmax;
	public float m_invVmax;

	public int m_maxCircles;
	public dtObstacleCircle[] m_circles;
	public int m_ncircles;

	public int m_maxSegments;
	public dtObstacleSegment[] m_segments;
	public int m_nsegments;
}
