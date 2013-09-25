package org.recast.RecastDemo.Include;

import java.io.File;

/**
 * @author igozha
 * @since 18.09.13 20:27
 */
public abstract class rcMeshLoaderObj
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

//	#ifndef MESHLOADER_OBJ
//	#define MESHLOADER_OBJ

//	class rcMeshLoaderObj
//	{
//	public:
//		rcMeshLoaderObj();
//		~rcMeshLoaderObj();

	public abstract boolean load(File fileName);

	public float[] getVerts()
	{
		return m_verts;
	}

	public float[] getNormals()
	{
		return m_normals;
	}

	public int[] getTris()
	{
		return m_tris;
	}

	public int getVertCount()
	{
		return m_vertCount;
	}

	public int getTriCount()
	{
		return m_triCount;
	}

//	public String getFileName()
//	{
//		return m_filename;
//	}

//	private:

	public abstract void addVertex(float x, float y, float z, int[] cap);

	public abstract void addTriangle(int a, int b, int c, int[] cap);

	//	public String m_filename;
	public float m_scale;
	public float[] m_verts;
	public int[] m_tris;
	public float[] m_normals;
	public int m_vertCount;
	public int m_triCount;
//	};
//
//	#endif // MESHLOADER_OBJ

}
