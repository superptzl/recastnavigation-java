package org.recast.Recast.Include;

public abstract class Recast {
    public static final char RC_WALKABLE_AREA = 63;
    public static final int RC_BORDER_REG = 0x8000;
    public static final int RC_NOT_CONNECTED = 0x3f;
    /// Applied to the region id field of contour vertices in order to extract the region id.
/// The region id field of a vertex may have several flags applied to it.  So the
/// fields value can't be used directly.
/// @see rcContour::verts, rcContour::rverts
    public static final int RC_CONTOUR_REG_MASK = 0xffff;
    public static final int RC_AREA_BORDER = 0x20000;
    /// Border vertex flag.
/// If a region ID has this bit set, then the associated element lies on
/// a tile border. If a contour vertex's region ID has this bit set, the
/// vertex will later be removed in order to match the segments and vertices
/// at tile boundaries.
/// (Used during the build process.)
/// @see rcCompactSpan::reg, #rcContour::verts, #rcContour::rverts
    public static final int RC_BORDER_VERTEX = 0x10000;

    /// The number of spans allocated per span spool.
/// @see rcSpanPool
    public static final int RC_SPANS_PER_POOL = 2048;

    public static final int VERTEX_BUCKET_COUNT = (1<<12);

    /// Represents the null area.
/// When a data element is given this value it is considered to no longer be
/// assigned to a usable area.  (E.g. It is unwalkable.)
    public final static char RC_NULL_AREA = 0;

    /// An value which indicates an invalid index within a mesh.
/// @note This does not necessarily indicate an error.
/// @see rcPolyMesh::polys
    public final static short RC_MESH_NULL_IDX = (short)(0xffff);

    /// Defines the number of bits allocated to rcSpan::smin and rcSpan::smax.
    public final static int RC_SPAN_HEIGHT_BITS = 13;
    /// Defines the maximum value for rcSpan::smin and rcSpan::smax.
    public final static  int RC_SPAN_MAX_HEIGHT = (1<<RC_SPAN_HEIGHT_BITS)-1;

    public static float rcSqr(float a) { return a*a; }

    public abstract void rcCalcGridSize(float[] bmin, float[] bmax, float cs, int[] w, int[] h);

    public abstract boolean rcCreateHeightfield(rcContext ctx, rcHeightfield hf, int width, int height,
                             float[] bmin, float[] bmax,
                             float cs, float ch);

    public abstract void rcMarkWalkableTriangles(rcContext ctx, float walkableSlopeAngle, float[] verts, int nv,
                                 int[] tris, int nt, char[] areas);

    /// Rasterizes an indexed triangle mesh into the specified heightfield.
///  @ingroup recast
///  @param[in,out]	ctx				The build context to use during the operation.
///  @param[in]		verts			The vertices. [(x, y, z) * @p nv]
///  @param[in]		nv				The number of vertices.
///  @param[in]		tris			The triangle indices. [(vertA, vertB, vertC) * @p nt]
///  @param[in]		areas			The area id's of the triangles. [Limit: <= #RC_WALKABLE_AREA] [Size: @p nt]
///  @param[in]		nt				The number of triangles.
///  @param[in,out]	solid			An initialized heightfield.
///  @param[in]		flagMergeThr	The distance where the walkable flag is favored over the non-walkable flag.
///  								[Limit: >= 0] [Units: vx]
    public void rcRasterizeTriangles(rcContext ctx, float[] verts, int nv,
                              int[] tris, char[] areas, int nt,
                              rcHeightfield solid) {
        rcRasterizeTriangles(ctx, verts, nv, tris, areas, nt, solid, 1);
    }

    public abstract void rcRasterizeTriangles(rcContext ctx, float[] verts, int nv,
                              int[] tris, char[] areas, int nt,
                              rcHeightfield solid, int flagMergeThr);

    /// Rasterizes an indexed triangle mesh into the specified heightfield.
///  @ingroup recast
///  @param[in,out]	ctx			The build context to use during the operation.
///  @param[in]		verts		The vertices. [(x, y, z) * @p nv]
///  @param[in]		nv			The number of vertices.
///  @param[in]		tris		The triangle indices. [(vertA, vertB, vertC) * @p nt]
///  @param[in]		areas		The area id's of the triangles. [Limit: <= #RC_WALKABLE_AREA] [Size: @p nt]
///  @param[in]		nt			The number of triangles.
///  @param[in,out]	solid		An initialized heightfield.
///  @param[in]		flagMergeThr	The distance where the walkable flag is favored over the non-walkable flag.
///  							[Limit: >= 0] [Units: vx]
    public void rcRasterizeTriangles(rcContext ctx, float[] verts, int nv,
                              short[] tris, char[] areas, int nt,
                              rcHeightfield solid) {
        rcRasterizeTriangles(ctx, verts, nv, tris, areas, nt, solid, 1);
    }

    public abstract void rcRasterizeTriangles(rcContext ctx, float[] verts, int nv,
                              short[] tris, char[] areas, int nt,
                              rcHeightfield solid, int flagMergeThr);

    /// Rasterizes triangles into the specified heightfield.
///  @ingroup recast
///  @param[in,out]	ctx				The build context to use during the operation.
///  @param[in]		verts			The triangle vertices. [(ax, ay, az, bx, by, bz, cx, by, cx) * @p nt]
///  @param[in]		areas			The area id's of the triangles. [Limit: <= #RC_WALKABLE_AREA] [Size: @p nt]
///  @param[in]		nt				The number of triangles.
///  @param[in,out]	solid			An initialized heightfield.
///  @param[in]		flagMergeThr	The distance where the walkable flag is favored over the non-walkable flag.
///  								[Limit: >= 0] [Units: vx]
    public void rcRasterizeTriangles(rcContext ctx, float[] verts, char[] areas, int nt,
                              rcHeightfield solid)
    {
        rcRasterizeTriangles(ctx, verts, areas, nt, solid, 1);
    }

    public abstract void rcRasterizeTriangles(rcContext ctx, float[] verts, char[] areas, int nt,
                              rcHeightfield solid, int flagMergeThr);

    /// Marks non-walkable spans as walkable if their maximum is within @p walkableClimp of a walkable neihbor.
///  @ingroup recast
///  @param[in,out]	ctx				The build context to use during the operation.
///  @param[in]		walkableClimb	Maximum ledge height that is considered to still be traversable.
///  								[Limit: >=0] [Units: vx]
///  @param[in,out]	solid			A fully built heightfield.  (All spans have been added.)
    public abstract void rcFilterLowHangingWalkableObstacles(rcContext ctx, int walkableClimb, rcHeightfield solid);

    /// Marks spans that are ledges as not-walkable.
///  @ingroup recast
///  @param[in,out]	ctx				The build context to use during the operation.
///  @param[in]		walkableHeight	Minimum floor to 'ceiling' height that will still allow the floor area to
///  								be considered walkable. [Limit: >= 3] [Units: vx]
///  @param[in]		walkableClimb	Maximum ledge height that is considered to still be traversable.
///  								[Limit: >=0] [Units: vx]
///  @param[in,out]	solid			A fully built heightfield.  (All spans have been added.)
    public abstract void rcFilterLedgeSpans(rcContext ctx, int walkableHeight,
                            int walkableClimb, rcHeightfield solid);

    /// Marks walkable spans as not walkable if the clearence above the span is less than the specified height.
///  @ingroup recast
///  @param[in,out]	ctx				The build context to use during the operation.
///  @param[in]		walkableHeight	Minimum floor to 'ceiling' height that will still allow the floor area to
///  								be considered walkable. [Limit: >= 3] [Units: vx]
///  @param[in,out]	solid			A fully built heightfield.  (All spans have been added.)
    public abstract void rcFilterWalkableLowHeightSpans(rcContext ctx, int walkableHeight, rcHeightfield solid);

    /// Builds a compact heightfield representing open space, from a heightfield representing solid space.
///  @ingroup recast
///  @param[in,out]	ctx				The build context to use during the operation.
///  @param[in]		walkableHeight	Minimum floor to 'ceiling' height that will still allow the floor area
///  								to be considered walkable. [Limit: >= 3] [Units: vx]
///  @param[in]		walkableClimb	Maximum ledge height that is considered to still be traversable.
///  								[Limit: >=0] [Units: vx]
///  @param[in]		hf				The heightfield to be compacted.
///  @param[out]	chf				The resulting compact heightfield. (Must be pre-allocated.)
///  @returns True if the operation completed successfully.
    public abstract boolean rcBuildCompactHeightfield(rcContext ctx, int walkableHeight, int walkableClimb,
                                   rcHeightfield hf, rcCompactHeightfield chf);

    /// Frees the specified heightfield object using the Recast allocator.
///  @param[in]		hf	A heightfield allocated using #rcAllocHeightfield
///  @ingroup recast
///  @see rcAllocHeightfield
//    public abstract void rcFreeHeightField(rcHeightfield hf);

    /// Erodes the walkable area within the heightfield by the specified radius.
///  @ingroup recast
///  @param[in,out]	ctx		The build context to use during the operation.
///  @param[in]		radius	The radius of erosion. [Limits: 0 < value < 255] [Units: vx]
///  @param[in,out]	chf		The populated compact heightfield to erode.
///  @returns True if the operation completed successfully.
    public abstract boolean rcErodeWalkableArea(rcContext ctx, int radius, rcCompactHeightfield chf);

    /// Applies the area id to the all spans within the specified convex polygon.
///  @ingroup recast
///  @param[in,out]	ctx		The build context to use during the operation.
///  @param[in]		verts	The vertices of the polygon [Fomr: (x, y, z) * @p nverts]
///  @param[in]		nverts	The number of vertices in the polygon.
///  @param[in]		hmin	The height of the base of the polygon.
///  @param[in]		hmax	The height of the top of the polygon.
///  @param[in]		areaId	The area id to apply. [Limit: <= #RC_WALKABLE_AREA]
///  @param[in,out]	chf		A populated compact heightfield.
    public abstract void rcMarkConvexPolyArea(rcContext ctx, float[] verts, int nverts,
                              float hmin, float hmax, char areaId,
                              rcCompactHeightfield chf);

    /// Builds region data for the heightfield using simple monotone partitioning.
///  @ingroup recast
///  @param[in,out]	ctx				The build context to use during the operation.
///  @param[in,out]	chf				A populated compact heightfield.
///  @param[in]		borderSize		The size of the non-navigable border around the heightfield.
///  								[Limit: >=0] [Units: vx]
///  @param[in]		minRegionArea	The minimum number of cells allowed to form isolated island areas.
///  								[Limit: >=0] [Units: vx].
///  @param[in]		mergeRegionArea	Any regions with a span count smaller than this value will, if possible,
///  								be merged with larger regions. [Limit: >=0] [Units: vx]
///  @returns True if the operation completed successfully.
    public abstract boolean rcBuildRegionsMonotone(rcContext ctx, rcCompactHeightfield chf,
                                int borderSize, int minRegionArea, int mergeRegionArea);

    /// Builds the distance field for the specified compact heightfield.
///  @ingroup recast
///  @param[in,out]	ctx		The build context to use during the operation.
///  @param[in,out]	chf		A populated compact heightfield.
///  @returns True if the operation completed successfully.
    public abstract boolean rcBuildDistanceField(rcContext ctx, rcCompactHeightfield chf);

    /// Builds region data for the heightfield using watershed partitioning.
///  @ingroup recast
///  @param[in,out]	ctx				The build context to use during the operation.
///  @param[in,out]	chf				A populated compact heightfield.
///  @param[in]		borderSize		The size of the non-navigable border around the heightfield.
///  								[Limit: >=0] [Units: vx]
///  @param[in]		minRegionArea	The minimum number of cells allowed to form isolated island areas.
///  								[Limit: >=0] [Units: vx].
///  @param[in]		mergeRegionArea		Any regions with a span count smaller than this value will, if possible,
///  								be merged with larger regions. [Limit: >=0] [Units: vx]
///  @returns True if the operation completed successfully.
    public abstract boolean rcBuildRegions(rcContext ctx, rcCompactHeightfield chf,
                        int borderSize, int minRegionArea, int mergeRegionArea);

    /// Builds a contour set from the region outlines in the provided compact heightfield.
///  @ingroup recast
///  @param[in,out]	ctx			The build context to use during the operation.
///  @param[in]		chf			A fully built compact heightfield.
///  @param[in]		maxError	The maximum distance a simplfied contour's border edges should deviate
///  							the original raw contour. [Limit: >=0] [Units: wu]
///  @param[in]		maxEdgeLen	The maximum allowed length for contour edges along the border of the mesh.
///  							[Limit: >=0] [Units: vx]
///  @param[out]	cset		The resulting contour set. (Must be pre-allocated.)
///  @param[in]		buildFlags	The build flags. (See: #rcBuildContoursFlags)
///  @returns True if the operation completed successfully.
    public boolean rcBuildContours(rcContext ctx, rcCompactHeightfield chf,
                         float maxError, int maxEdgeLen,
                         rcContourSet cset) {
        return rcBuildContours(ctx, chf, maxError, maxEdgeLen, cset, rcBuildContoursFlags.RC_CONTOUR_TESS_WALL_EDGES.v);
    }

    public abstract boolean rcBuildContours(rcContext ctx, rcCompactHeightfield chf,
                         float maxError, int maxEdgeLen,
                         rcContourSet cset, int flags);

    /// Builds a polygon mesh from the provided contours.
///  @ingroup recast
///  @param[in,out]	ctx		The build context to use during the operation.
///  @param[in]		cset	A fully built contour set.
///  @param[in]		nvp		The maximum number of vertices allowed for polygons generated during the
///  						contour to polygon conversion process. [Limit: >= 3]
///  @param[out]	mesh	The resulting polygon mesh. (Must be re-allocated.)
///  @returns True if the operation completed successfully.
    public abstract boolean rcBuildPolyMesh(rcContext ctx, rcContourSet cset, int nvp, rcPolyMesh mesh);

    /// Builds a detail mesh from the provided polygon mesh.
///  @ingroup recast
///  @param[in,out]	ctx				The build context to use during the operation.
///  @param[in]		mesh			A fully built polygon mesh.
///  @param[in]		chf				The compact heightfield used to build the polygon mesh.
///  @param[in]		sampleDist		Sets the distance to use when samping the heightfield. [Limit: >=0] [Units: wu]
///  @param[in]		sampleMaxError	The maximum distance the detail mesh surface should deviate from
///  								heightfield data. [Limit: >=0] [Units: wu]
///  @param[out]	dmesh			The resulting detail mesh.  (Must be pre-allocated.)
///  @returns True if the operation completed successfully.
    public abstract boolean rcBuildPolyMeshDetail(rcContext ctx, rcPolyMesh mesh, rcCompactHeightfield chf,
                               float sampleDist, float sampleMaxError,
                               rcPolyMeshDetail dmesh);

//    public static int rcGetCon(rcCompactSpan s, int dir)
//    {
//        int shift = (int)dir*6;
//        return (s.con >> shift) & 0x3f;
//    }
//
    public static int rcGetDirOffsetX(int dir)
    {
        int offset[] = { -1, 0, 1, 0, };
        return offset[dir&0x03];
    }

    /// Gets the standard height (z-axis) offset for the specified direction.
///  @param[in]		dir		The direction. [Limits: 0 <= value < 4]
///  @return The height offset to apply to the current cell position to move
///  	in the direction.
    public static int rcGetDirOffsetY(int dir)
    {
        int offset[] = { 0, 1, 0, -1 };
        return offset[dir&0x03];
    }

    public static void rcVsub(float[]dest, float[] v1, float[] v2)
    {
        dest[0] = v1[0]-v2[0];
        dest[1] = v1[1]-v2[1];
        dest[2] = v1[2]-v2[2];
    }

    public static void rcVcross(float[] dest, float[] v1, float[] v2)
    {
        dest[0] = v1[1]*v2[2] - v1[2]*v2[1];
        dest[1] = v1[2]*v2[0] - v1[0]*v2[2];
        dest[2] = v1[0]*v2[1] - v1[1]*v2[0];
    }

    public static void rcVnormalize(float[] v)
    {
        float d = 1.0f / rcSqrt(rcSqr(v[0]) + rcSqr(v[1]) + rcSqr(v[2]));
        v[0] *= d;
        v[1] *= d;
        v[2] *= d;
    }

    public static float rcSqrt(float x) {
        return (float)Math.sqrt(x);
    }

    public static int[] create3(int[] arr, int fromIndex) {
        int[] r = new int[3];
        r[0] = arr[fromIndex+0];
        r[0] = arr[fromIndex+1];
        r[0] = arr[fromIndex+2];
        return r;
    }

    public static short[] create3(short[] arr, int fromIndex) {
        return createN(arr, fromIndex, 3);
    }

    public static int[] createN(int[] arr, int fromIndex, int n) {
        int[] r = new int[n];
        System.arraycopy(arr, fromIndex, r, 0, n);
        return r;
    }

    public static short[] createN(short[] arr, int fromIndex, int n) {
        short[] r = new short[n];
        System.arraycopy(arr, fromIndex, r, 0, n);
        return r;
    }

    public static float[] createN(float[] arr, int fromIndex, int n) {
        float[] r = new float[n];
        System.arraycopy(arr, fromIndex, r, 0, n);
        /*for (int i = 0; i < n; i++) {
            r[i] = arr[fromIndex + i];
        }*/
        return r;
    }

    public static float[] create3(float[] arr, int fromIndex) {
        return createN(arr, fromIndex, 3);
        /*float[] r = new float[3];
        r[0] = arr[fromIndex+0];
        r[0] = arr[fromIndex+1];
        r[0] = arr[fromIndex+2];
        return r;*/
    }

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
    public static  int rcMin(int a, int b) { return a < b ? a : b; }

//    public static  float rcMax(float a, float b) { return a > b ? a : b; }

    public static float rcMax(float a, float b) { return a > b ? a : b; }
    public static int rcMax(int a, int b) { return a > b ? a : b; }
    public static float rcAbs(float a) { return a < 0 ? -a : a; }

    public static <T> void rcSwap(T[] a, T[] b) { T t = a[0]; a[0] = b[0]; b[0] = t; }

    public static <T extends Number> T rcClamp(T v, T mn, T mx) { return v.doubleValue() < mn.doubleValue() ? mn : (v.doubleValue() > mx.doubleValue() ? mx : v); }
//    public static float rcSwap(float a, float b) { float t = a; a = b; b = t; }


/// Sets the neighbor connection data for the specified direction.
///  @param[in]		s		The span to update.
///  @param[in]		dir		The direction to set. [Limits: 0 <= value < 4]
///  @param[in]		i		The index of the neighbor span.
    public static void rcSetCon(rcCompactSpan s, int dir, int i)
    {
        int shift = (int)dir*6;
        int con = s.con;
        s.con = (con & ~(0x3f << shift)) | (((int)i & 0x3f) << shift);
    }

/// Gets neighbor connection data for the specified direction.
///  @param[in]		s		The span to check.
///  @param[in]		dir		The direction to check. [Limits: 0 <= value < 4]
///  @return The neighbor connection data for the specified direction,
///  	or #RC_NOT_CONNECTED if there is no connection.
    public static int rcGetCon(rcCompactSpan s, int dir)
    {
        int shift = (int)dir*6;
        return (s.con >> shift) & 0x3f;
    }
}
