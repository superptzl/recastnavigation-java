package org.recast.Detour.Include;

/**
 * @author igozha
 * @since 22.09.13 21:02
 */
public abstract class DetourCommon
{
	public static float dtMin(float a, float b)
	{
		return a < b ? a : b;
	}

	public static int dtMin(int a, int b)
	{
		return a < b ? a : b;
	}

	public static int dtMax(int a, int b)
	{
		return a > b ? a : b;
	}

	public static float dtMax(float a, float b)
	{
		return a > b ? a : b;
	}

	public static void dtVcopy(float[] dest, float[] a)
	{
		dtVcopy(dest, 0, a, 0);
//		dest[0] = a[0];
//		dest[1] = a[1];
//		dest[2] = a[2];
	}

	public static void dtVcopy(float[] dest, int destIndex, float[] a, int aIndex)
	{
		dest[destIndex + 0] = a[aIndex + 0];
		dest[destIndex + 1] = a[aIndex + 1];
		dest[destIndex + 2] = a[aIndex + 2];
	}

	public static float dtSqr(float a)
	{
		return a * a;
	}

	/// Performs a vector copy.
///  @param[out]	dest	The result. [(x, y, z)]
///  @param[in]		a		The vector to copy. [(x, y, z)]
//    public  static void dtVcopy(float[] dest, float[] a)
//{
//    dest[0] = a[0];
//    dest[1] = a[1];
//    dest[2] = a[2];
//}
	public static int dtNextPow2(int v)
	{
		v--;
		v |= v >> 1;
		v |= v >> 2;
		v |= v >> 4;
		v |= v >> 8;
		v |= v >> 16;
		v++;
		return v;
	}

	public static int dtIlog2(int v)
	{
		int r;
		int shift;
		r = ((v > 0xffff) ? 1 : 0) << 4;
		v >>= r;
		shift = ((v > 0xff) ? 1 : 0) << 3;
		v >>= shift;
		r |= shift;
		shift = ((v > 0xf) ? 1 : 0) << 2;
		v >>= shift;
		r |= shift;
		shift = ((v > 0x3) ? 1 : 0) << 1;
		v >>= shift;
		r |= shift;
		r |= (v >> 1);
		return r;
	}

	public abstract boolean dtDistancePtPolyEdgesSqr(float[] pt, float[] verts, int nverts,
													 float[] ed, float[] et);

	public static int dtOppositeTile(int side)
	{
		return (side + 4) & 0x7;
	}

	/// Extracts a tile's salt value from the specified polygon reference.
	///  @note This function is generally meant for internal use only.
	///  @param[in]	ref		The polygon reference.
	///  @see #encodePolyId
//    public static int decodePolyIdSalt(dtPoly ref)
//    {
//        ref.
//        dtPolyRef saltMask = ((dtPoly)1<<m_saltBits)-1;
//        return (int)((ref >> (m_polyBits+m_tileBits)) & saltMask);
//    }

	/// Returns the distance between two points.
///  @param[in]		v1	A point. [(x, y, z)]
///  @param[in]		v2	A point. [(x, y, z)]
/// @return The distance between the two points.
	public static float dtVdist(float[] v1, float[] v2)
	{
		return dtVdist(v1, 0, v2, 0);
	}

	public static float dtVdist(float[] v1, int v1Index, float[] v2, int v2Index)
	{
		float dx = v2[v2Index + 0] - v1[v1Index + 0];
		float dy = v2[v2Index + 1] - v1[v1Index + 1];
		float dz = v2[v2Index + 2] - v1[v1Index + 2];
		return dtSqrt(dx * dx + dy * dy + dz * dz);
	}

	/// Returns the square root of the value.
///  @param[in]		x	The value.
///  @return The square root of the vlaue.
	public static float dtSqrt(float x)
	{
		return (float)Math.sqrt(x);
	}

	public static float dtAbs(float a)
	{
		return a < 0 ? -a : a;
	}

	/// Sets the vector elements to the specified values.
///  @param[out]	dest	The result vector. [(x, y, z)]
///  @param[in]		x		The x-value of the vector.
///  @param[in]		y		The y-value of the vector.
///  @param[in]		z		The z-value of the vector.
	public static void dtVset(float[] dest, float x, float y, float z)
	{
		dest[0] = x;
		dest[1] = y;
		dest[2] = z;
	}

	/// Performs a vector addition. (@p v1 + @p v2)
///  @param[out]	dest	The result vector. [(x, y, z)]
///  @param[in]		v1		The base vector. [(x, y, z)]
///  @param[in]		v2		The vector to add to @p v1. [(x, y, z)]
	public static void dtVadd(float[] dest, float[] v1, float[] v2)
	{
		dtVadd(dest, v1, 0, v2, 0);
	}

	public static void dtVadd(float[] dest, float[] v1, int v1Index, float[] v2, int v2Index)
	{
		dest[0] = v1[0] + v2[0];
		dest[1] = v1[1] + v2[1];
		dest[2] = v1[2] + v2[2];
	}

	/// Performs a vector subtraction. (@p v1 - @p v2)
///  @param[out]	dest	The result vector. [(x, y, z)]
///  @param[in]		v1		The base vector. [(x, y, z)]
///  @param[in]		v2		The vector to subtract from @p v1. [(x, y, z)]
	public static void dtVsub(float[] dest, float[] v1, float[] v2)
	{
		dtVsub(dest, v1, 0, v2, 0);
	}

	public static void dtVsub(float[] dest, float[] v1, int v1Index, float[] v2, int v2Index)
	{
		dest[0] = v1[v1Index + 0] - v2[v2Index + 0];
		dest[1] = v1[v1Index + 1] - v2[v2Index + 1];
		dest[2] = v1[v1Index + 2] - v2[v2Index + 2];
	}

	/// Clamps the value to the specified range.
///  @param[in]		v	The value to clamp.
///  @param[in]		mn	The minimum permitted return value.
///  @param[in]		mx	The maximum permitted return value.
///  @return The value, clamped to the specified range.
	public static float dtClamp(float v, float mn, float mx)
	{
		return v < mn ? mn : (v > mx ? mx : v);
	}

	public static int dtClamp(int v, int mn, int mx)
	{
		return v < mn ? mn : (v > mx ? mx : v);
	}

	/// Determines if two axis-aligned bounding boxes overlap.
///  @param[in]		amin	Minimum bounds of box A. [(x, y, z)]
///  @param[in]		amax	Maximum bounds of box A. [(x, y, z)]
///  @param[in]		bmin	Minimum bounds of box B. [(x, y, z)]
///  @param[in]		bmax	Maximum bounds of box B. [(x, y, z)]
/// @return True if the two AABB's overlap.
/// @see dtOverlapQuantBounds
	public static boolean dtOverlapBounds(float[] amin, float[] amax,
										  float[] bmin, float[] bmax)
	{
		boolean overlap = true;
		overlap = (amin[0] > bmax[0] || amax[0] < bmin[0]) ? false : overlap;
		overlap = (amin[1] > bmax[1] || amax[1] < bmin[1]) ? false : overlap;
		overlap = (amin[2] > bmax[2] || amax[2] < bmin[2]) ? false : overlap;
		return overlap;
	}

	/// Determines if two axis-aligned bounding boxes overlap.
///  @param[in]		amin	Minimum bounds of box A. [(x, y, z)]
///  @param[in]		amax	Maximum bounds of box A. [(x, y, z)]
///  @param[in]		bmin	Minimum bounds of box B. [(x, y, z)]
///  @param[in]		bmax	Maximum bounds of box B. [(x, y, z)]
/// @return True if the two AABB's overlap.
/// @see dtOverlapBounds
	public static boolean dtOverlapQuantBounds(int amin[], int amax[],
											   int bmin[], int bmax[])
	{
		boolean overlap = true;
		overlap = (amin[0] > bmax[0] || amax[0] < bmin[0]) ? false : overlap;
		overlap = (amin[1] > bmax[1] || amax[1] < bmin[1]) ? false : overlap;
		overlap = (amin[2] > bmax[2] || amax[2] < bmin[2]) ? false : overlap;
		return overlap;
	}

	/// Selects the minimum value of each element from the specified vectors.
///  @param[in,out]	mn	A vector.  (Will be updated with the result.) [(x, y, z)]
///  @param[in]	v	A vector. [(x, y, z)]
	public static void dtVmin(float[] mn, float[] v)
	{
		dtVmin(mn, 0, v, 0);
	}

	public static void dtVmin(float[] mn, int mnIndex, float[] v, int vIndex)
	{
		mn[mnIndex + 0] = dtMin(mn[mnIndex + 0], v[vIndex + 0]);
		mn[mnIndex + 1] = dtMin(mn[mnIndex + 1], v[vIndex + 1]);
		mn[mnIndex + 2] = dtMin(mn[mnIndex + 2], v[vIndex + 2]);
	}

	/// Selects the maximum value of each element from the specified vectors.
///  @param[in,out]	mx	A vector.  (Will be updated with the result.) [(x, y, z)]
///  @param[in]		v	A vector. [(x, y, z)]
	public static void dtVmax(float[] mx, float[] v)
	{
		dtVmax(mx, 0, v, 0);
	}

	public static void dtVmax(float[] mx, int mxIndex, float[] v, int vIdex)
	{
		mx[mxIndex + 0] = dtMax(mx[mxIndex + 0], v[vIdex + 0]);
		mx[mxIndex + 1] = dtMax(mx[mxIndex + 1], v[vIdex + 1]);
		mx[mxIndex + 2] = dtMax(mx[mxIndex + 2], v[vIdex + 2]);
	}

	/// Returns the square of the distance between two points.
///  @param[in]		v1	A point. [(x, y, z)]
///  @param[in]		v2	A point. [(x, y, z)]
/// @return The square of the distance between the two points.
	public static float dtVdistSqr(float[] v1, float[] v2)
	{
		return dtVdistSqr(v1, 0, v2, 0);
	}

	public static float dtVdistSqr(float[] v1, int v1Index, float[] v2, int v2Index)
	{
		float dx = v2[v2Index + 0] - v1[v1Index + 0];
		float dy = v2[v2Index + 1] - v1[v1Index + 1];
		float dz = v2[v2Index + 2] - v1[v1Index + 2];
		return dx * dx + dy * dy + dz * dz;
	}

	/// Performs a linear interpolation between two vectors. (@p v1 toward @p v2)
	///  @param[out]	dest	The result vector. [(x, y, x)]
	///  @param[in]		v1		The starting vector.
	///  @param[in]		v2		The destination vector.
	///	 @param[in]		t		The interpolation factor. [Limits: 0 <= value <= 1.0]
	public static void dtVlerp(float[] dest, float[] v1, int v1Index, float[] v2, int v2Index, float t)
	{
		dest[0] = v1[v1Index + 0] + (v2[v2Index + 0] - v1[v1Index + 0]) * t;
		dest[1] = v1[v1Index + 1] + (v2[v2Index + 1] - v1[v1Index + 1]) * t;
		dest[2] = v1[v1Index + 2] + (v2[v2Index + 2] - v1[v1Index + 2]) * t;
	}

	/// Derives the y-axis height of the closest point on the triangle from the specified reference point.
	///  @param[in]		p		The reference point from which to test. [(x, y, z)]
	///  @param[in]		a		Vertex A of triangle ABC. [(x, y, z)]
	///  @param[in]		b		Vertex B of triangle ABC. [(x, y, z)]
	///  @param[in]		c		Vertex C of triangle ABC. [(x, y, z)]
	///  @param[out]	h		The resulting height.
	public abstract boolean dtClosestHeightPointTriangle(float[] p, float[] a, int aIndex, float[] b, int bIndex, float[] c, int cIndex, float[] h);

	public static float dtVdot2D(float[] u, float[] v)
	{
		return u[0] * v[0] + u[2] * v[2];
	}

	public static void dtVnormalize(float[] v)
	{
		float d = 1.0f / dtSqrt(dtSqr(v[0]) + dtSqr(v[1]) + dtSqr(v[2]));
		v[0] *= d;
		v[1] *= d;
		v[2] *= d;
	}

	/// Scales the vector by the specified value. (@p v * @p t)
///  @param[out]	dest	The result vector. [(x, y, z)]
///  @param[in]		v		The vector to scale. [(x, y, z)]
///  @param[in]		t		The scaling factor.
	public static void dtVscale(float[] dest, float[] v, float t)
	{
		dest[0] = v[0] * t;
		dest[1] = v[1] * t;
		dest[2] = v[2] * t;
	}

	/// Derives the square of the distance between the specified points on the xz-plane.
///  @param[in]		v1	A point. [(x, y, z)]
///  @param[in]		v2	A point. [(x, y, z)]
/// @return The square of the distance between the point on the xz-plane.
	public static float dtVdist2DSqr(float[] v1, float[] v2)
	{
		return dtVdist2DSqr(v1, 0, v2, 0);
	}

	public static float dtVdist2DSqr(float[] v1, int viIndex, float[] v2, int v2Index)
	{
		float dx = v2[v2Index + 0] - v1[viIndex + 0];
		float dz = v2[v2Index + 2] - v1[viIndex + 2];
		return dx * dx + dz * dz;
	}

	/// Derives the distance between the specified points on the xz-plane.
///  @param[in]		v1	A point. [(x, y, z)]
///  @param[in]		v2	A point. [(x, y, z)]
/// @return The distance between the point on the xz-plane.
///
/// The vectors are projected onto the xz-plane, so the y-values are ignored.
	public static float dtVdist2D(float[] v1, float[] v2)
	{
		return dtVdist2D(v1, 0, v2, 0);
	}

	public static float dtVdist2D(float[] v1, int v1Index, float[] v2, int v2Index)
	{
		float dx = v2[v2Index + 0] - v1[v1Index + 0];
		float dz = v2[v2Index + 2] - v1[v1Index + 2];
		return dtSqrt(dx * dx + dz * dz);
	}

	/// Derives the scalar length of the vector.
///  @param[in]		v The vector. [(x, y, z)]
/// @return The scalar length of the vector.
	public static float dtVlen(float[] v)
	{
		return dtSqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
	}

	/// Derives the square of the scalar length of the vector. (len * len)
///  @param[in]		v The vector. [(x, y, z)]
/// @return The square of the scalar length of the vector.
	public static float sqrtf(float v)
	{
		return (float)Math.sqrt(v);
	}

	public static float dtVlenSqr(float[] v)
	{
		return v[0] * v[0] + v[1] * v[1] + v[2] * v[2];
	}

	/// Performs a scaled vector addition. (@p v1 + (@p v2 * @p s))
///  @param[out]	dest	The result vector. [(x, y, z)]
///  @param[in]		v1		The base vector. [(x, y, z)]
///  @param[in]		v2		The vector to scale and add to @p v1. [(x, y, z)]
///  @param[in]		s		The amount to scale @p v2 by before adding to @p v1.
	public static void dtVmad(float[] dest, float[] v1, float[] v2, float s)
	{
		dest[0] = v1[0] + v2[0] * s;
		dest[1] = v1[1] + v2[1] * s;
		dest[2] = v1[2] + v2[2] * s;
	}

	/// Derives the signed xz-plane area of the triangle ABC, or the relationship of line AB to point C.
///  @param[in]		a		Vertex A. [(x, y, z)]
///  @param[in]		b		Vertex B. [(x, y, z)]
///  @param[in]		c		Vertex C. [(x, y, z)]
/// @return The signed xz-plane area of the triangle.
	public static float dtTriArea2D(float[] a, float[] b, float[] c)
	{
		return dtTriArea2D(a, b, c, 0);
	}

	public static float dtTriArea2D(float[] a, float[] b, float[] c, int cIndex)
	{
		float abx = b[0] - a[0];
		float abz = b[2] - a[2];
		float acx = c[cIndex + 0] - a[0];
		float acz = c[cIndex + 2] - a[2];
		return acx * abz - abx * acz;
	}

	public abstract float dtDistancePtSegSqr2D(float[] pt, float[] p, float[] q, float[] t);

	/// Derives the xz-plane 2D perp product of the two vectors. (uz*vx - ux*vz)
///  @param[in]		u		The LHV vector [(x, y, z)]
///  @param[in]		v		The RHV vector [(x, y, z)]
/// @return The dot product on the xz-plane.
///
/// The vectors are projected onto the xz-plane, so the y-values are ignored.
	public static float dtVperp2D(float[] u, float[] v)
	{
		return u[2] * v[0] - u[0] * v[2];
	}

	public abstract boolean dtIntersectSegmentPoly2D(float[] p0, float[] p1,
													 float[] verts, int nverts,
													 float[] tmin, float[] tmax,
													 int[] segMin, int[] segMax);

	/// Performs a 'sloppy' colocation check of the specified points.
	///  @param[in]		p0	A point. [(x, y, z)]
	///  @param[in]		p1	A point. [(x, y, z)]
	/// @return True if the points are considered to be at the same location.
	///
	/// Basically, this function will return true if the specified points are
	/// close enough to eachother to be considered colocated.
	public static boolean dtVequal(float[] p0, float[] p1)
	{
		return dtVequal(p0, 0, p1, 0);
	}

	public static boolean dtVequal(float[] p0, int p0Index, float[] p1, int p1Index)
	{
		float thr = dtSqr(1.0f / 16384.0f);
		float d = dtVdistSqr(p0, p0Index, p1, p1Index);
		return d < thr;
	}

	public abstract boolean dtIntersectSegSeg2D(float[] ap, int apIndex, float[] aq,
												float[] bp, float[] bq,
												float[] s, float[] t);

	public abstract boolean dtPointInPolygon(float[] pt, float[] verts, int nverts);
}
