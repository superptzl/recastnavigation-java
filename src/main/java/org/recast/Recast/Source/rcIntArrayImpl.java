package org.recast.Recast.Source;

import org.recast.Recast.Include.rcIntArray;

/**
 * @author igozha
 * @since 20.09.13 22:21
 */
public class rcIntArrayImpl extends rcIntArray
{
	public rcIntArrayImpl()
	{
	}

	public rcIntArrayImpl(int n)
	{
		super(n);
	}

	@Override
	public void resize(int n)
	{
		if (n > m_cap)
		{
			if (m_cap == 0) m_cap = n;
			while (m_cap < n) m_cap *= 2;
			int[] newData = new int[m_cap];//(int*)rcAlloc(m_cap*sizeof(int), RC_ALLOC_TEMP);
			if (m_size != 0 /*&& newData*/)
			{
//				memcpy(newData, m_data, m_size*sizeof(int));
				System.arraycopy(m_data, 0, newData, 0, m_size);
			}
			m_data = null;
//			rcFree(m_data);
			m_data = newData;
		}
		m_size = n;
	}
}
