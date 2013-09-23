package org.recast.Detour.Include;

/**
 * @author igozha
 * @since 22.09.13 21:02
 */
public abstract class DetourCommon
{
	public static float dtMin(float a, float b) { return a < b ? a : b; }
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
}
