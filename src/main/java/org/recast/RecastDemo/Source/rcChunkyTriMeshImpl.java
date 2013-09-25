package org.recast.RecastDemo.Source;

import org.recast.RecastDemo.Include.rcChunkyTriMesh;

import java.util.*;

/**
 * @author igozha
 * @since 19.09.13 09:10
 */
public class rcChunkyTriMeshImpl extends rcChunkyTriMesh
{
	public boolean rcCreateChunkyTriMesh(float[] verts, int[] tris, int ntris,
										 int trisPerChunk, rcChunkyTriMesh cm)
	{
		int nchunks = (ntris + trisPerChunk - 1) / trisPerChunk;

		cm.nodes = new rcChunkyTriMeshNode[nchunks * 4];
		for (int i = 0; i < cm.nodes.length; i++)
		{
			cm.nodes[i] = new rcChunkyTriMeshNode();
		}
		/*if (!cm->nodes)
			return false;
*/
		cm.tris = new int[ntris * 3];
		/*if (!cm->tris)
			return false;*/

		cm.ntris = ntris;

		// Build tree
		BoundsItem[] items = new BoundsItem[ntris];
		for (int i = 0; i < ntris; i++)
		{
			items[i] = new BoundsItem();
		}
//        for ()
		/*if (!items)
			return false;*/

		for (int i = 0; i < ntris; i++)
		{
//			int[] t = tris[i*3];
			BoundsItem it = items[i];
			it.i = i;
			// Calc triangle XZ bounds.
			it.bmin[0] = it.bmax[0] = verts[tris[i * 3 + 0] * 3 + 0];
			it.bmin[1] = it.bmax[1] = verts[tris[i * 3 + 0] * 3 + 2];
			for (int j = 1; j < 3; ++j)
			{
//				float[] v = verts[tris[i*3 + j]*3];
				if (verts[tris[i * 3 + j] * 3 + 0] < it.bmin[0]) it.bmin[0] = verts[tris[i * 3 + j] * 3 + 0];
				if (verts[tris[i * 3 + j] * 3 + 2] < it.bmin[1]) it.bmin[1] = verts[tris[i * 3 + j] * 3 + 2];

				if (verts[tris[i * 3 + j] * 3 + 0] > it.bmax[0]) it.bmax[0] = verts[tris[i * 3 + j] * 3 + 0];
				if (verts[tris[i * 3 + j] * 3 + 2] > it.bmax[1]) it.bmax[1] = verts[tris[i * 3 + j] * 3 + 2];
			}
		}

		int curTri[] = new int[]{0};
		int curNode[] = new int[]{0};
		subdivide(items, ntris, 0, ntris, trisPerChunk, curNode, cm.nodes, nchunks * 4, curTri, cm.tris, tris);

//		delete [] items;

		cm.nnodes = curNode[0];

		// Calc max tris per node.
		cm.maxTrisPerChunk = 0;
		for (int i = 0; i < cm.nnodes; ++i)
		{
			rcChunkyTriMeshNode node = cm.nodes[i];
			boolean isLeaf = node.i >= 0;
			if (!isLeaf) continue;
			if (node.n > cm.maxTrisPerChunk)
				cm.maxTrisPerChunk = node.n;
		}

		return true;
	}

	public static class BoundsItem
	{
		float bmin[] = new float[2];
		float bmax[] = new float[2];
		int i;
	}

	public static void subdivide(BoundsItem[] items, int nitems, int imin, int imax, int trisPerChunk,
								 int[] curNode, rcChunkyTriMeshNode[] nodes, int maxNodes,
								 int[] curTri, int[] outTris, int[] inTris)
	{
		int inum = imax - imin;
		int icur = curNode[0];

		if (curNode[0] > maxNodes)
			return;

		rcChunkyTriMeshNode node = nodes[curNode[0]++];

		if (inum <= trisPerChunk)
		{
			// Leaf
			calcExtends(items, nitems, imin, imax, node.bmin, node.bmax);

			// Copy triangles.
			node.i = curTri[0];
			node.n = inum;

			for (int i = imin; i < imax; ++i)
			{
//				int[] src = inTris[items[i].i*3];
//				int[] dst = outTris[curTri[0]*3];

				outTris[curTri[0] * 3 + 0] = inTris[items[i].i * 3 + 0];
				outTris[curTri[0] * 3 + 1] = inTris[items[i].i * 3 + 1];
				outTris[curTri[0] * 3 + 2] = inTris[items[i].i * 3 + 2];

				curTri[0]++;
				/*int[] src = inTris[items[i].i*3];
                int[] dst = outTris[curTri[0]*3];
                curTri++;
                dst[0] = src[0];
                dst[1] = src[1];
                dst[2] = src[2];*/
			}
		}
		else
		{
			// Split
			calcExtends(items, nitems, imin, imax, node.bmin, node.bmax);

			int axis = longestAxis(node.bmax[0] - node.bmin[0],
								   node.bmax[1] - node.bmin[1]);

			if (axis == 0)
			{
				// Sort along x-axis
				Arrays.sort(items, imin, imin + inum, new Comparator<BoundsItem>()
				{
					@Override
					public int compare(BoundsItem o1, BoundsItem o2)
					{
						return compareItemX(o1, o2);
					}
				});
//				qsort(items + imin, inum, sizeof(BoundsItem), compareItemX);
			}
			else if (axis == 1)
			{
				// Sort along y-axis
				Arrays.sort(items, imin, imin + inum, new Comparator<BoundsItem>()
				{
					@Override
					public int compare(BoundsItem o1, BoundsItem o2)
					{
						return compareItemY(o1, o2);
					}
				});
//				qsort(items+imin, inum, sizeof(BoundsItem), compareItemY);
			}

			int isplit = imin + inum / 2;

			// Left
			subdivide(items, nitems, imin, isplit, trisPerChunk, curNode, nodes, maxNodes, curTri, outTris, inTris);
			// Right
			subdivide(items, nitems, isplit, imax, trisPerChunk, curNode, nodes, maxNodes, curTri, outTris, inTris);

			int iescape = curNode[0] - icur;
			// Negative index means escape.
			node.i = -iescape;
		}
	}

	public static int longestAxis(float x, float y)
	{
		return y > x ? 1 : 0;
	}

	public static void calcExtends(BoundsItem[] items, int nitems,
								   int imin, int imax,
								   float[] bmin, float[] bmax)
	{
		bmin[0] = items[imin].bmin[0];
		bmin[1] = items[imin].bmin[1];

		bmax[0] = items[imin].bmax[0];
		bmax[1] = items[imin].bmax[1];

		for (int i = imin + 1; i < imax; ++i)
		{
			BoundsItem it = items[i];
			if (it.bmin[0] < bmin[0]) bmin[0] = it.bmin[0];
			if (it.bmin[1] < bmin[1]) bmin[1] = it.bmin[1];

			if (it.bmax[0] > bmax[0]) bmax[0] = it.bmax[0];
			if (it.bmax[1] > bmax[1]) bmax[1] = it.bmax[1];
		}
	}

	static int compareItemX(BoundsItem a, BoundsItem b)
	{
//		const BoundsItem* a = (const BoundsItem*)va;
//		const BoundsItem* b = (const BoundsItem*)vb;
		if (a.bmin[0] < b.bmin[0])
			return -1;
		if (a.bmin[0] > b.bmin[0])
			return 1;
		return 0;
	}

	static int compareItemY(BoundsItem a, BoundsItem b)
	{
//		const BoundsItem* a = (const BoundsItem*)va;
//		const BoundsItem* b = (const BoundsItem*)vb;
		if (a.bmin[1] < b.bmin[1])
			return -1;
		if (a.bmin[1] > b.bmin[1])
			return 1;
		return 0;
	}

}
