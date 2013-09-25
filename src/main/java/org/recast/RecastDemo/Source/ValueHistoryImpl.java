package org.recast.RecastDemo.Source;

import org.recast.RecastDemo.Include.ValueHistory;

public class ValueHistoryImpl extends ValueHistory
{
//    #include "ValueHistory.h"
//            #include "imgui.h"
//            #include <string.h>
//    #include <stdio.h>
//
//    #ifdef WIN32
//    #	define snprintf _snprintf
//    #endif

	public ValueHistoryImpl()
	{
//        :
//        m_hsamples  0)
		for (int i = 0; i < MAX_HISTORY; ++i)
			m_samples[i] = 0;
	}

//    ~ValueHistory()
//    {
//    }

	public float getSampleMin()
	{
		float val = m_samples[0];
		for (int i = 1; i < MAX_HISTORY; ++i)
			if (m_samples[i] < val)
				val = m_samples[i];
		return val;
	}

	public float getSampleMax()
	{
		float val = m_samples[0];
		for (int i = 1; i < MAX_HISTORY; ++i)
			if (m_samples[i] > val)
				val = m_samples[i];
		return val;
	}

	public float getAverage()
	{
		float val = 0;
		for (int i = 0; i < MAX_HISTORY; ++i)
			val += m_samples[i];
		return val / (float)MAX_HISTORY;
	}

}
