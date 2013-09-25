package org.recast.RecastDemo.Include;

public abstract class ValueHistory
{
	public final static int MAX_HISTORY = 256;
	public float m_samples[] = new float[MAX_HISTORY];
	public int m_hsamples;
//    :
//    ValueHistory();
//    ~ValueHistory();

	public void addSample(float val)
	{
		m_hsamples = (m_hsamples + MAX_HISTORY - 1) % MAX_HISTORY;
		m_samples[m_hsamples] = val;
	}

	public int getSampleCount()
	{
		return MAX_HISTORY;
	}

	public float getSample(int i)
	{
		return m_samples[(m_hsamples + i) % MAX_HISTORY];
	}

	public abstract float getSampleMin();

	public abstract float getSampleMax();

	public abstract float getAverage();
}
