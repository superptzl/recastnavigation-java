package org.recast.RecastDemo.Include;

/**
 * @author igozha
 * @since 19.09.13 09:08
 */
public abstract class rcChunkyTriMesh
{
	public rcChunkyTriMesh()
	{

	}
//	inline ~rcChunkyTriMesh() { delete [] nodes; delete [] tris; }

	public rcChunkyTriMeshNode[] nodes;
	public int nnodes;
	public int[] tris;
	public int ntris;
	public int maxTrisPerChunk;

	public static class rcChunkyTriMeshNode
	{
		public float bmin[] = new float[2], bmax[] = new float[2];
		public int i, n;
	}

	public abstract boolean rcCreateChunkyTriMesh(float[] verts, int[] tris, int ntris,
												  int trisPerChunk, rcChunkyTriMesh cm);

}


