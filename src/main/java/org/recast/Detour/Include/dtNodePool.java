package org.recast.Detour.Include;

public abstract class dtNodePool
{
	//    public:
	public dtNodePool(int maxNodes, int hashSize)
	{
		this.m_maxNodes = maxNodes;
		this.m_hashSize = hashSize;
	}

	//    ~dtNodePool();
//    inline void operator=( dtNodePool&) {}
	public abstract void clear();

	public abstract dtNode getNode(dtPoly id);

	public abstract dtNode findNode(dtPoly id);

	public int getNodeIdx(dtNode node)
	{
		if (node == null) return 0;
		for (int i = 0; i < m_nodes.length; i++)
		{
			if (m_nodes[i] == node)
			{
				return i;
			}
		}
		return -1;
	}

    /*public dtNode getNodeAtIdx(int idx)
	{
        if (idx == 0) return null;
        return m_nodes[idx-1];
    }*/

	public dtNode getNodeAtIdx(int idx)
	{
		if (idx == 0) return null;
		return m_nodes[idx - 1];
	}

	/*inline int getMemUsed()
	{
		return sizeof(*this) +
			sizeof(dtNode)*m_maxNodes +
			sizeof(dtNodeIndex)*m_maxNodes +
			sizeof(dtNodeIndex)*m_hashSize;
	}
*/
	public int getMaxNodes()
	{
		return m_maxNodes;
	}

	public int getHashSize()
	{
		return m_hashSize;
	}

	public dtNodeIndex getFirst(int bucket)
	{
		return m_first[bucket];
	}

	public dtNodeIndex getNext(int i)
	{
		return m_next[i];
	}

//    private:

	public dtNode[] m_nodes;
	public dtNodeIndex[] m_first;
	public dtNodeIndex[] m_next;
	public int m_maxNodes;
	public int m_hashSize;
	public int m_nodeCount;
}
