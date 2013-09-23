package org.recast.Detour.Include;

/**
 * @author igozha
 * @since 22.09.13 22:14
 */
public abstract class dtNodeQueue {
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

//	#ifndef DETOURNODE_H
//	#define DETOURNODE_H
//
//	#include "DetourNavMesh.h"

//	enum dtNodeFlags
//	{
//		DT_NODE_OPEN = 0x01,
//		DT_NODE_CLOSED = 0x02;
//	}
//
////	typedef unsigned short dtNodeIndex;
//	public final static dtNodeIndex DT_NULL_IDX = (dtNodeIndex)~0;


    //	class dtNodeQueue
//	{
//	public:
    public dtNodeQueue(int n) {
        m_capacity = n;
    }
//		~dtNodeQueue();
//		inline void operator=(dtNodeQueue&) {}

    public void clear() {
        m_size = 0;
    }

    public dtNode top() {
        return m_heap[0];
    }

    public dtNode pop() {
        dtNode result = m_heap[0];
        m_size--;
        trickleDown(0, m_heap[m_size]);
        return result;
    }

    public void push(dtNode node) {
        m_size++;
        bubbleUp(m_size - 1, node);
    }

    public void modify(dtNode node) {
        for (int i = 0; i < m_size; ++i) {
            if (m_heap[i] == node) {
                bubbleUp(i, node);
                return;
            }
        }
    }

    public boolean empty() {
        return m_size == 0;
    }

    /*  public int getMemUsed()
          {
              return sizeof(*this) +
              sizeof(dtNode*)*(m_capacity+1);
          }
  */
    public int getCapacity() {
        return m_capacity;
    }

    //	private:
    public abstract void bubbleUp(int i, dtNode node);

    public abstract void trickleDown(int i, dtNode node);

    public dtNode[] m_heap;
    public final int m_capacity;
    public int m_size;
//	};


//	#endif // DETOURNODE_H

}
