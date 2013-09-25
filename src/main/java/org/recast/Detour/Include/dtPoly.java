package org.recast.Detour.Include;

/**
 * @author igozha
 * @since 22.09.13 21:19
 */
/// Defines a polyogn within a dtMeshTile object.
/// @ingroup detour
public class dtPoly
{
	/// Index to first link in linked list. (Or #DT_NULL_LINK if there is no link.)
	public int firstLink;
	public final dtMeshTile parent;

	public dtPoly(dtMeshTile parent)
	{
		this.parent = parent;
	}

	/// The indices of the polygon's vertices.
	/// The actual vertices are located in dtMeshTile::verts.
	public int verts[] = new int[DetourNavMesh.DT_VERTS_PER_POLYGON];

	/// Packed data representing neighbor polygons references and flags for each edge.
	public int neis[] = new int[DetourNavMesh.DT_VERTS_PER_POLYGON];

	/// The user defined polygon flags.
	public int flags;

	/// The number of vertices in the polygon.
	public char vertCount;

	/// The bit packed area id and polygon type.
	/// @note Use the structure's set and get methods to acess this value.
	public int areaAndtype;

	/// Sets the user defined area id. [Limit: < #DT_MAX_AREAS]
	public void setArea(int a)
	{
		areaAndtype = (areaAndtype & 0xc0) | (a & 0x3f);
	}

	/// Sets the polygon type. (See: #dtPolyTypes.)
	public void setType(int t)
	{
		areaAndtype = (areaAndtype & 0x3f) | (t << 6);
	}

	/// Gets the user defined area id.
	public int getArea()
	{
		return areaAndtype & 0x3f;
	}

	/// Gets the polygon type. (See: #dtPolyTypes)
	public int getType()
	{
		return areaAndtype >> 6;
	}
}
