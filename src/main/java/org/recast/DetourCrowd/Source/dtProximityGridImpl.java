package org.recast.DetourCrowd.Source;

import org.recast.Detour.Include.DetourCommon;
import org.recast.DetourCrowd.Include.dtProximityGrid;

public class dtProximityGridImpl extends dtProximityGrid {
    public dtProximityGridImpl()
    {
//        :
//        m_maxItems(0),
//                m_cellSize(0),
//                m_pool(0),
//                m_poolHead(0),
//                m_poolSize(0),
//                m_buckets(0),
//                m_bucketsSize(0)
    }

//    ~dtProximityGrid()
//    {
//        dtFree(m_buckets);
//        dtFree(m_pool);
//    }

    public boolean init(int poolSize, float cellSize)
    {
//        dtAssert(poolSize > 0);
//        dtAssert(cellSize > 0.0f);

        m_cellSize = cellSize;
        m_invCellSize = 1.0f / m_cellSize;

        // Allocate hashs buckets
        m_bucketsSize = DetourCommon.dtNextPow2(poolSize);
        m_buckets = new int[m_bucketsSize];//(unsigned short*)dtAlloc(sizeof(unsigned short)*m_bucketsSize, DT_ALLOC_PERM);
//        if (!m_buckets)
//            return false;

        // Allocate pool of items.
        m_poolSize = poolSize;
        m_poolHead = 0;
        m_pool = new Item[m_poolSize];//)dtAlloc(sizeof(Item)*m_poolSize, DT_ALLOC_PERM);
//        if (!m_pool)
//            return false;

        clear();

        return true;
    }

    public void clear()
    {
//        memset(m_buckets, 0xff, sizeof(unsigned short)*m_bucketsSize);
        m_buckets = null;
        m_poolHead = 0;
        m_bounds[0] = 0xffff;
        m_bounds[1] = 0xffff;
        m_bounds[2] = -0xffff;
        m_bounds[3] = -0xffff;
    }

    public void addItem( int id,
                                   float minx,  float miny,
                                   float maxx,  float maxy)
    {
//         int iminx = (int)floorf(minx * m_invCellSize);
//         int iminy = (int)floorf(miny * m_invCellSize);
//         int imaxx = (int)floorf(maxx * m_invCellSize);
//         int imaxy = (int)floorf(maxy * m_invCellSize);
//
//        m_bounds[0] = dtMin(m_bounds[0], iminx);
//        m_bounds[1] = dtMin(m_bounds[1], iminy);
//        m_bounds[2] = dtMax(m_bounds[2], imaxx);
//        m_bounds[3] = dtMax(m_bounds[3], imaxy);
//
//        for (int y = iminy; y <= imaxy; ++y)
//        {
//            for (int x = iminx; x <= imaxx; ++x)
//            {
//                if (m_poolHead < m_poolSize)
//                {
//                     int h = hashPos2(x, y, m_bucketsSize);
//                     unsigned short idx = (unsigned short)m_poolHead;
//                    m_poolHead++;
//                    Item& item = m_pool[idx];
//                    item.x = (short)x;
//                    item.y = (short)y;
//                    item.id = id;
//                    item.next = m_buckets[h];
//                    m_buckets[h] = idx;
//                }
//            }
//        }
    }

    public int queryItems( float minx,  float miny,
                                     float maxx,  float maxy,
                                    int[] ids,  int maxIds)
    {
//         int iminx = (int)floorf(minx * m_invCellSize);
//         int iminy = (int)floorf(miny * m_invCellSize);
//         int imaxx = (int)floorf(maxx * m_invCellSize);
//         int imaxy = (int)floorf(maxy * m_invCellSize);
//
//        int n = 0;
//
//        for (int y = iminy; y <= imaxy; ++y)
//        {
//            for (int x = iminx; x <= imaxx; ++x)
//            {
//                 int h = hashPos2(x, y, m_bucketsSize);
//                unsigned short idx = m_buckets[h];
//                while (idx != 0xffff)
//                {
//                    Item& item = m_pool[idx];
//                    if ((int)item.x == x && (int)item.y == y)
//                    {
//                        // Check if the id exists already.
//                         unsigned short* end = ids + n;
//                        unsigned short* i = ids;
//                        while (i != end && *i != item.id)
//                        ++i;
//                        // Item not found, add it.
//                        if (i == end)
//                        {
//                            if (n >= maxIds)
//                                return n;
//                            ids[n++] = item.id;
//                        }
//                    }
//                    idx = item.next;
//                }
//            }
//        }
//
//        return n;
        return -1;
    }

    public int getItemCountAt( int x,  int y) 
    {
//        int n = 0;
//
//         int h = hashPos2(x, y, m_bucketsSize);
//        unsigned short idx = m_buckets[h];
//        while (idx != 0xffff)
//        {
//            Item& item = m_pool[idx];
//            if ((int)item.x == x && (int)item.y == y)
//                n++;
//            idx = item.next;
//        }
//
//        return n;
        return -1;
    }

}
