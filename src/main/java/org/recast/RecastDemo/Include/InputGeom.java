package org.recast.RecastDemo.Include;

/**
 * @author igozha
 * @since 18.09.13 20:23
 */
public abstract class InputGeom
{
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

//	#ifndef INPUTGEOM_H
//	#define INPUTGEOM_H
//
//	#include "ChunkyTriMesh.h"
//	#include "MeshLoaderObj.h"

	static int MAX_CONVEXVOL_PTS = 12;
	public static class ConvexVolume
	{
		public float verts[] = new float[MAX_CONVEXVOL_PTS*3];
		public float hmin, hmax;
		public int nverts;
		public int area;
	}

//	class InputGeom
//	{
	public rcChunkyTriMesh m_chunkyMesh;
	public rcMeshLoaderObj m_mesh;
	public float m_meshBMin[] = new float[3], m_meshBMax[] = new float[3];

		/// @name Off-Mesh connections.
		///@{
	public static final int MAX_OFFMESH_CONNECTIONS = 256;
	public float m_offMeshConVerts[] = new float[MAX_OFFMESH_CONNECTIONS*3*2];
	public float m_offMeshConRads[] = new float[MAX_OFFMESH_CONNECTIONS];
	public char m_offMeshConDirs[] = new char[MAX_OFFMESH_CONNECTIONS];
	public char m_offMeshConAreas[] = new char[MAX_OFFMESH_CONNECTIONS];
	public short m_offMeshConFlags[] = new short[MAX_OFFMESH_CONNECTIONS];
	public int m_offMeshConId[] = new int[MAX_OFFMESH_CONNECTIONS];
	public int m_offMeshConCount;
		///@}

		/// @name Convex Volumes.
		///@{
	public static final int MAX_VOLUMES = 256;
	public ConvexVolume m_volumes[] = new ConvexVolume[MAX_VOLUMES];
	public int m_volumeCount;
		///@}

//	public:
//		InputGeom();
//		~InputGeom();

	public abstract boolean loadMesh(/*class rcContext* ctx, */String filepath);

	public abstract boolean load(/*rcContext* ctx, */String filepath);
	public abstract boolean save(String filepath);

		/// Method to return static mesh data.
	public rcMeshLoaderObj getMesh() { return m_mesh; }
	public float[] getMeshBoundsMin() { return m_meshBMin; }
	public float[] getMeshBoundsMax() { return m_meshBMax; }
	public rcChunkyTriMesh getChunkyMesh() { return m_chunkyMesh; }
	public abstract boolean raycastMesh(float[] src, float[] dst, float[] tmin);

		/// @name Off-Mesh connections.
		///@{
		public int getOffMeshConnectionCount() { return m_offMeshConCount; }
	public float[] getOffMeshConnectionVerts() { return m_offMeshConVerts; }
	public float[] getOffMeshConnectionRads() { return m_offMeshConRads; }
	public char[] getOffMeshConnectionDirs() { return m_offMeshConDirs; }
	public char[] getOffMeshConnectionAreas() { return m_offMeshConAreas; }
	public short[] getOffMeshConnectionFlags() { return m_offMeshConFlags; }
	public int[] getOffMeshConnectionId() { return m_offMeshConId; }
	public abstract void addOffMeshConnection(float[] spos, float[] epos, float rad,
								  char bidir, char area, short flags);
	public abstract void deleteOffMeshConnection(int i);
//	public abstract void drawOffMeshConnections(struct duDebugDraw* dd, bool hilight = false);
		///@}

		/// @name Box Volumes.
		///@{
	public int getConvexVolumeCount() { return m_volumeCount; }
	public ConvexVolume[] getConvexVolumes() { return m_volumes; }
	public abstract void addConvexVolume(float[] verts, int nverts,
							 float minh, float maxh, char area);
	public abstract void deleteConvexVolume(int i);
//	public abstract void drawConvexVolumes(struct duDebugDraw* dd, boolean hilight = false);
		///@}
//	};

	public abstract void rcCalcBounds(float[] verts, int nv, float[] bmin, float[] bmax);

	public static void rcVcopy(float[] dest, float[] v)
	{
		dest[0] = v[0];
		dest[1] = v[1];
		dest[2] = v[2];
	}

	public static void rcVmin(float[] mn, float[] v)
	{
		mn[0] = rcMin(mn[0], v[0]);
		mn[1] = rcMin(mn[1], v[1]);
		mn[2] = rcMin(mn[2], v[2]);
	}

	public static void rcVmax(float[] mx,  float[] v)
	{
		mx[0] = rcMax(mx[0], v[0]);
		mx[1] = rcMax(mx[1], v[1]);
		mx[2] = rcMax(mx[2], v[2]);
	}

	public static  float rcMin(float a, float b) { return a < b ? a : b; }

	public static  float rcMax(float a, float b) { return a > b ? a : b; }
//	#endif // INPUTGEOM_H

}
