package org.recast.DetourCrowd.Include;

public abstract class dtProximityGrid
{
	public int m_maxItems;
	public float m_cellSize;
	public float m_invCellSize;

	public static class Item
	{
		public short id;
		public short x, y;
		public short next;
	}

	public Item[] m_pool;
	public int m_poolHead;
	public int m_poolSize;

	public int[] m_buckets;
	public int m_bucketsSize;

	public int m_bounds[] = new int[4];

//    public:
//    dtProximityGrid();
//    ~dtProximityGrid();

	public abstract boolean init(int maxItems, float cellSize);

	public abstract void clear();

	public abstract void addItem(int id,
								 float minx, float miny,
								 float maxx, float maxy);

	public abstract int queryItems(float minx, float miny,
								   float maxx, float maxy,
								   int[] ids, int maxIds);

	public abstract int getItemCountAt(int x, int y);

	public int[] getBounds()
	{
		return m_bounds;
	}

	public float getCellSize()
	{
		return m_cellSize;
	}
}
