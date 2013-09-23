package org.recast.Detour.Source;

import org.recast.Detour.Include.*;

public class dtNodePoolImpl extends dtNodePool {
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

//    #include "DetourNode.h"
//            #include "DetourAlloc.h"
//            #include "DetourAssert.h"
//            #include "DetourCommon.h"
//            #include <string.h>
//
//    inline unsigned int dtHashRef(dtPolyRef a)
//    {
//        a += ~(a<<15);
//        a ^=  (a>>10);
//        a +=  (a<<3);
//        a ^=  (a>>6);
//        a += ~(a<<11);
//        a ^=  (a>>16);
//        return (unsigned int)a;
//    }

//////////////////////////////////////////////////////////////////////////////////////////
    public dtNodePoolImpl(int maxNodes, int hashSize)
    {
        super(maxNodes, hashSize);
//        :
//        m_nodes(0),
//                m_first(0),
//                m_next(0),
//                m_maxNodes = maxNodes;
//                m_hashSize = hashSize;
//                m_nodeCount(0)
//        dtAssert(dtNextPow2(m_hashSize) == (unsigned int)m_hashSize);
//        dtAssert(m_maxNodes > 0);

        m_nodes = new dtNode[m_maxNodes];//(dtNode*)dtAlloc(sizeof(dtNode)*m_maxNodes, DT_ALLOC_PERM);
        m_next = new dtNodeIndex[m_maxNodes];//(dtNodeIndex*)dtAlloc(sizeof(dtNodeIndex)*m_maxNodes, DT_ALLOC_PERM);
        m_first = new dtNodeIndex[hashSize];//(dtNodeIndex*)dtAlloc(sizeof(dtNodeIndex)*hashSize, DT_ALLOC_PERM);

//        dtAssert(m_nodes);
//        dtAssert(m_next);
//        dtAssert(m_first);
//
//        memset(m_first, 0xff, sizeof(dtNodeIndex)*m_hashSize);
//        memset(m_next, 0xff, sizeof(dtNodeIndex)*m_maxNodes);
    }
//
//    ~dtNodePool()
//    {
//        dtFree(m_nodes);
//        dtFree(m_next);
//        dtFree(m_first);
//    }
//
    public void clear()
    {
//        memset(m_first, 0xff, sizeof(dtNodeIndex)*m_hashSize);
        m_nodeCount = 0;
    }

    public dtNode findNode(dtPoly id)
    {
//        int bucket = dtHashRef(id) & (m_hashSize-1);
//        dtNodeIndex i = m_first[bucket];
//        while (i.v != DetourNavMesh.DT_NULL_IDX)
//        {
//            if (m_nodes[i.v].id == id)
//                return m_nodes[i.v];
//            i = m_next[i.v];
//        }
        return null;
    }

    public dtNode getNode(dtPoly id)
    {
//        unsigned int bucket = dtHashRef(id) & (m_hashSize-1);
//        dtNodeIndex i = m_first[bucket];
//        dtNode* node = 0;
//        while (i != DT_NULL_IDX)
//        {
//            if (m_nodes[i].id == id)
//                return &m_nodes[i];
//            i = m_next[i];
//        }
//
//        if (m_nodeCount >= m_maxNodes)
//            return 0;
//
//        i = (dtNodeIndex)m_nodeCount;
//        m_nodeCount++;
//
//        // Init node
//        node = &m_nodes[i];
//        node->pidx = 0;
//        node->cost = 0;
//        node->total = 0;
//        node->id = id;
//        node->flags = 0;
//
//        m_next[i] = m_first[bucket];
//        m_first[bucket] = i;
//
//        return node;
        return null;
    }


}
