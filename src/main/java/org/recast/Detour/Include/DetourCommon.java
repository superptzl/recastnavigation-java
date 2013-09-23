package org.recast.Detour.Include;

/**
 * @author igozha
 * @since 22.09.13 21:02
 */
public abstract class DetourCommon
{
	public static float dtMin(float a, float b) { return a < b ? a : b; }
	public static int dtMin(int a, int b) { return a < b ? a : b; }
	public static float dtMax(float a, float b) { return a > b ? a : b; }

	public static void dtVcopy(float[]dest, float[] a)
	{
		dtVcopy(dest, 0, a, 0);
//		dest[0] = a[0];
//		dest[1] = a[1];
//		dest[2] = a[2];
	}

	public static void dtVcopy(float[]dest, int destIndex, float[] a, int aIndex)
	{
		dest[destIndex+0] = a[aIndex+0];
		dest[destIndex+1] = a[aIndex+1];
		dest[destIndex+2] = a[aIndex+2];
	}

    public static float dtSqr(float a) { return a*a; }

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
    r = ((v > 0xffff) ? 1 : 0) << 4; v >>= r;
    shift = ((v > 0xff) ? 1 : 0) << 3; v >>= shift; r |= shift;
    shift = ((v > 0xf) ? 1 : 0) << 2; v >>= shift; r |= shift;
    shift = ((v > 0x3) ? 1 : 0) << 1; v >>= shift; r |= shift;
    r |= (v >> 1);
    return r;
}

    public abstract boolean dtDistancePtPolyEdgesSqr(float[] pt, float[] verts, int nverts,
                                  float[] ed, float[] et);

    public static int dtOppositeTile(int side) { return (side+4) & 0x7; }

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
    float dx = v2[0] - v1[0];
    float dy = v2[1] - v1[1];
    float dz = v2[2] - v1[2];
    return dtSqrt(dx*dx + dy*dy + dz*dz);
}

    /// Returns the square root of the value.
///  @param[in]		x	The value.
///  @return The square root of the vlaue.
    public static float dtSqrt(float x) {
        return (float)Math.sqrt(x);
    }

    public static float dtAbs(float a) { return a < 0 ? -a : a; }
}
