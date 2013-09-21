package org.recast.RecastDemo.Source;

import org.recast.RecastDemo.Include.rcMeshLoaderObj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * @author igozha
 * @since 18.09.13 20:28
 */
public class rcMeshLoaderObjImpl extends rcMeshLoaderObj
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
	
//	#include "MeshLoaderObj.h"
//	#include <stdio.h>
//	#include <stdlib.h>
//	#include <string.h>
//	#define _USE_MATH_DEFINES
//	#include <math.h>
	
	public rcMeshLoaderObjImpl()
	{
//		:
				m_scale = 1.0f;
//				m_verts(0),
//				m_tris(0),
//				m_normals(0),
//				m_vertCount(0),
//				m_triCount(0)
	}
	
	/*~rcMeshLoaderObj()
	{
		delete [] m_verts;
		delete [] m_normals;
		delete [] m_tris;
	}
*/
	public void addVertex(float x, float y, float z, int[] cap)
	{
		if (m_vertCount+1 > cap[0])
		{
			cap[0] = cap[0] == 0 ? 8 : cap[0]*2;
			float[] nv = new float[cap[0]*3];
//			if (m_verts)
			if (m_verts != null) {
			System.arraycopy(m_verts, 0, nv, 0, m_vertCount*3);
			}
//			if (m_vertCount)
//				memcpy(nv, m_verts, m_vertCount*3*sizeof(float));
//			delete [] m_verts;
			m_verts = nv;
		}
//		float[] dst = m_verts[m_vertCount*3];
		m_verts[m_vertCount*3 + 0] = x*m_scale;
		m_verts[m_vertCount*3 + 1] = y*m_scale;
		m_verts[m_vertCount*3 + 2] = z*m_scale;
//		*dst++
//		*dst++
//		*dst++
		m_vertCount++;
	}
	
	public void addTriangle(int a, int b, int c, int[] cap)
	{
		if (m_triCount+1 > cap[0])
		{
			cap[0] = cap[0] == 0 ? 8 : cap[0]*2;
			int[] nv = new int[cap[0]*3];
			if (m_tris != null) {
			System.arraycopy(m_tris, 0, nv, 0, m_triCount*3);
			}
//			if (m_triCount)
//				memcpy(nv, m_tris, m_triCount*3*sizeof(int));
//			delete [] m_tris;
			m_tris = nv;
		}
//		int* dst = m_tris[m_triCount*3];
		m_tris[m_triCount*3 + 0] = a;
		m_tris[m_triCount*3 + 1] = b;
		m_tris[m_triCount*3 + 2] = c;
		m_triCount++;
	}
	
	public static String parseRow(String buf, String bufEnd, String row, int len)
	{
		boolean cont = false;
		boolean start = true;
		boolean done = false;
//		int n = 0;
//		while (!done && buf < bufEnd)
//		{
//			char c = *buf;
//			buf++;
//			// multirow
//			switch (c)
//			{
//				case '\\':
//					cont = true; // multirow
//					break;
//				case '\n':
//					if (start) break;
//					done = true;
//					break;
//				case '\r':
//					break;
//				case '\t':
//				case ' ':
//					if (start) break;
//				default:
//					start = false;
//					cont = false;
//					row[n++] = c;
//					if (n >= len-1)
//						done = true;
//					break;
//			}
//		}
//		row[n] = '\0';
		return buf;
	}
	
	public static int parseFace(String row, int[] data, int n, int vcnt)
	{
		String[] numbers = row.trim().split("[\\s]");
		int i = 0;
		for (String number : numbers) {
			int vi = Integer.valueOf(number);
			data[i++] = vi < 0 ? vi+vcnt : vi-1;
			if (i >= n) return i;
		}
		/*int j = 0;
		while (row.length() > 0 && row.charAt(0) != '\0')
		{
			// Skip initial white space
			while (row.length() > 0 && row.charAt(0) != '\0' && (row.charAt(0) == ' ' || row.charAt(0) == '\t')) {
//				row++;
				row = row.substring(1);
			}
			char s = row.charAt(0);
			// Find vertex delimiter and terminated the string there for conversion.
			while (row.length() > 0 && row.charAt(0) != '\0' && row.charAt(0) != ' ' && row.charAt(0) != '\t')
			{
				if (row.charAt(0) == '/') {
					row = "\0";
				}
				row = row.substring(1);
			}
			if (s == '\0')
				continue;
			int vi = Integer.valueOf(""+s);

			if (j >= n) return j;
		}*/
		return i;
	}
	
	public boolean load(File file)
	{
//		char* buf = 0;
//
////		FILE* fp = fopen(filename, "rb");
////		if (!fp)
////			return false;
//		fseek(fp, 0, SEEK_END);
//		int bufSize = ftell(fp);
//		fseek(fp, 0, SEEK_SET);
//		buf = new char[bufSize];
//		if (!buf)
//		{
//			fclose(fp);
//			return false;
//		}
//		fread(buf, bufSize, 1, fp);
//		fclose(fp);
		int face[] = new int[32];
				float x,y,z;
				int nv;
				int vcap[] = new int[]{0};
				int tcap[] = new int[]{0};
//		File fp = new File(filename);
		try (BufferedReader br = new BufferedReader(new FileReader(file)))
		{

			String line;
			while ((line = br.readLine()) != null)
			{
				// Parse one row
//				row[0] = '\0';
//				src = parseRow(src, srcEnd, row, sizeof(row)/sizeof(char));
				// Skip comments
				if (line.charAt(0) == '#') continue;
				if (line.charAt(0) == 'v' && line.charAt(1) != 'n' && line.charAt(1) != 't')
				{
					// Vertex pos
//					sscanf(row+1, "%f %f %f", x, y, z);
					String[] numbers = line.substring(1).trim().split(" ");
					x = Float.valueOf(numbers[0]);
					y = Float.valueOf(numbers[1]);
					z = Float.valueOf(numbers[2]);
					addVertex(x, y, z, vcap);
				}
				if (line.charAt(0) == 'f')
				{
					// Faces
					nv = parseFace(line.substring(1), face, 32, m_vertCount);
					for (int i = 2; i < nv; ++i)
					{
						int a = face[0];
						int b = face[i-1];
						int c = face[i];
						if (a < 0 || a >= m_vertCount || b < 0 || b >= m_vertCount || c < 0 || c >= m_vertCount)
							continue;
						addTriangle(a, b, c, tcap);
					}
				}
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}

//		char* src = buf;
//		char* srcEnd = buf + bufSize;
//		char row[512];

//
//		while (src < srcEnd)
//		{
//
//		}
	
//		delete [] buf;
	
		// Calculate normals.
		m_normals = new float[m_triCount*3];
		for (int i = 0; i < m_triCount*3; i += 3)
		{
//			float v0 = m_verts[m_tris[i] * 3];
//			float v1 = m_verts[m_tris[i + 1] * 3];
//			float v2 = m_verts[m_tris[i + 2] * 3];
			float e0[] = new float[3], e1[] = new float[3];
			for (int j = 0; j < 3; ++j)
			{
				e0[j] = m_verts[m_tris[i + 1] * 3 + j] - m_verts[m_tris[i] * 3 + j];
				e1[j] = m_verts[m_tris[i + 2] * 3 + j] - m_verts[m_tris[i] * 3 + j];
			}
//			float[] n = m_normals[i];
			m_normals[i + 0] = e0[1]*e1[2] - e0[2]*e1[1];
			m_normals[i + 1] = e0[2]*e1[0] - e0[0]*e1[2];
			m_normals[i + 2] = e0[0]*e1[1] - e0[1]*e1[0];
			double d = Math.sqrt(m_normals[i + 0]*m_normals[i + 0] + m_normals[i + 1]*m_normals[i + 1] + m_normals[i + 2]*m_normals[i + 2]);
			if (d > 0)
			{
				d = 1.0f/d;
				m_normals[i + 0] *= d;
				m_normals[i + 1] *= d;
				m_normals[i + 2] *= d;
			}
		}
//		m_filename = filename;
//		strncpy(m_filename, filename, sizeof(m_filename));
//		m_filename[sizeof(m_filename)-1] = '\0';
		
		return true;
	}
	
}
