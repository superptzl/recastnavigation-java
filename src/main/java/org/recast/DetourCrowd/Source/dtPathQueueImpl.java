package org.recast.DetourCrowd.Source;

import org.recast.Detour.Include.*;
import org.recast.Detour.Source.dtNavMeshQueryImpl;
import org.recast.DetourCrowd.Include.dtPathQueue;

public class dtPathQueueImpl extends dtPathQueue {
    //
// Copyright (c) 2009-2010 Mikko Mononen memon@inside.org
//
// This software is provided 'as-is', without any express or implied
// warranty.  In no event will the authors be held liable for any damages
// arising from the use of this software.
// Permission is granted to anyone to use this software for any purpose,
// including commercial applications, and to alter it and redistribute it
// freely, subject to the following restrictions:
// 1. The origin of this software must not be misrepresented; you must not
//    claim that you wrote the original software. If you use this software
//    in a product, an acknowledgment in the product documentation would be
//    appreciated but is not required.
// 2. Altered source versions must be plainly marked as such, and must not be
//    misrepresented as being the original software.
// 3. This notice may not be removed or altered from any source distribution.
//

//    #include <string.h>
//    #include "DetourPathQueue.h"
//            #include "DetourNavMesh.h"
//            #include "DetourNavMeshQuery.h"
//            #include "DetourAlloc.h"
//            #include "DetourCommon.h"


    public dtPathQueueImpl()
    {
//        m_nextHandle = 1),
//                m_maxPathSize(0),
//                m_queueHead(0),
//                m_navquery(0)
//        for (int i = 0; i < MAX_QUEUE; ++i)
//            m_queue[i].path = 0;
        for (int i = 0; i < m_queue.length; i++) {
            m_queue[i] = new PathQuery();
        }
    }
//
//    ~dtPathQueue()
//    {
//        purge();
//    }

    public void purge()
    {
//        dtFreeNavMeshQuery(m_navquery);
//        m_navquery = 0;
//        for (int i = 0; i < MAX_QUEUE; ++i)
//        {
//            dtFree(m_queue[i].path);
//            m_queue[i].path = 0;
//        }
    }

    public boolean init(int maxPathSize, int maxSearchNodeCount, dtNavMesh nav)
    {
        purge();

        m_navquery = new dtNavMeshQueryImpl();//dtAllocNavMeshQuery();
//        if (!m_navquery)
//            return false;
        if (dtStatus.dtStatusFailed(m_navquery . init(nav, maxSearchNodeCount)))
            return false;

        m_maxPathSize = maxPathSize;
        for (int i = 0; i < MAX_QUEUE; ++i)
        {
            m_queue[i].ref = null;//DT_PATHQ_INVALID;
            m_queue[i].path = new dtPoly[m_maxPathSize];//*)dtAlloc(sizeof(dtPolyRef)*m_maxPathSize, DT_ALLOC_PERM);
//            if (!m_queue[i].path)
//                return false;
        }

        m_queueHead = 0;

        return true;
    }

    public void update(int maxIters)
    {
        int MAX_KEEP_ALIVE = 2; // in update ticks.

        // Update path request until there is nothing to update
        // or upto maxIters pathfinder iterations has been consumed.
        int iterCount = maxIters;

        for (int i = 0; i < MAX_QUEUE; ++i)
        {
            PathQuery q = m_queue[m_queueHead % MAX_QUEUE];

            // Skip inactive requests.
            if (q.ref == null/*DT_PATHQ_INVALID*/)
            {
                m_queueHead++;
                continue;
            }

            // Handle completed request.
            if (dtStatus.dtStatusSucceed(q.status) || dtStatus.dtStatusFailed(q.status))
            {
                // If the path result has not been read in few frames, free the slot.
                q.keepAlive++;
                if (q.keepAlive > MAX_KEEP_ALIVE)
                {
                    q.ref = null;
                    q.status = new dtStatus(0);
                }

                m_queueHead++;
                continue;
            }

            // Handle query start.
            if (q.status.dtStatus == 0)
            {
                q.status = m_navquery.initSlicedFindPath(q.startRef, q.endRef, q.startPos, q.endPos, q.filter);
            }
            // Handle query in progress.
            if (dtStatus.dtStatusInProgress(q.status))
            {
                int[] iters = new int[1];
                q.status = m_navquery.updateSlicedFindPath(iterCount, iters);
                iterCount -= iters[0];
            }
            if (dtStatus.dtStatusSucceed(q.status))
            {
                int[] tmp = new int[]{q.npath};
                q.status = m_navquery.finalizeSlicedFindPath(q.path, tmp, m_maxPathSize);
                q.npath = tmp[0];
            }

            if (iterCount <= 0)
                break;

            m_queueHead++;
        }
    }
//
    public dtPathQueue request(dtPoly startRef, dtPoly endRef,
                                        float[] startPos, float[] endPos,
                                        dtQueryFilter filter)
    {
        // Find empty slot
        int slot = -1;
        for (int i = 0; i < MAX_QUEUE; ++i)
        {
            if (m_queue[i].ref == null/*DT_PATHQ_INVALID*/)
            {
                slot = i;
                break;
            }
        }
        // Could not find slot.
        if (slot == -1)
            return null;//DT_PATHQ_INVALID;

        //todo [IZ]
//        dtPathQueue ref = m_nextHandle++;
//        if (m_nextHandle == null/*DT_PATHQ_INVALID*/) m_nextHandle++;

        PathQuery q = m_queue[slot];
//        q.ref = ref;
        DetourCommon.dtVcopy(q.startPos, startPos);
        q.startRef = startRef;
        DetourCommon.dtVcopy(q.endPos, endPos);
        q.endRef = endRef;

        q.status.dtStatus = 0;
        q.npath = 0;
        q.filter = filter;
        q.keepAlive = 0;

        return null;
    }

    public dtStatus getRequestStatus(dtPathQueue ref)
    {
        for (int i = 0; i < MAX_QUEUE; ++i)
        {
            if (m_queue[i].ref == ref)
                return m_queue[i].status;
        }
        return new dtStatus(dtStatus.DT_FAILURE);
    }

    public dtStatus getPathResult(dtPathQueue ref, dtPoly[] path, int[] pathSize, int maxPath)
    {
        for (int i = 0; i < MAX_QUEUE; ++i)
        {
            if (m_queue[i].ref == ref)
            {
                PathQuery q = m_queue[i];
                // Free request for reuse.
                q.ref = null;//DT_PATHQ_INVALID;
                q.status.dtStatus = 0;
                // Copy path
                int n = DetourCommon.dtMin(q.npath, maxPath);
//                memcpy(path, q.path, sizeof(dtPolyRef)*n);
                System.arraycopy(q.path, 0, path, 0, n);
                pathSize[0] = n;
                return new dtStatus(dtStatus.DT_SUCCESS);
            }
        }
        return new dtStatus(dtStatus.DT_FAILURE);
    }

}
