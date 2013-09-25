package org.recast.Detour.Include;

/**
 * @author igozha
 * @since 22.09.13 21:22
 */
/// Defines a link between polygons.
/// @note This structure is rarely if ever used by the end user.
/// @see dtMeshTile
public class dtLink
{
	public dtPoly ref;                    ///< Neighbour reference. (The neighbor that is linked to.)
	public int next;                ///< Index of the next link.
	public char edge;                ///< Index of the polygon edge that owns this link.
	public char side;                ///< If a boundary link, defines on which side the link is.
	public char bmin;                ///< If a boundary link, defines the minimum sub-edge area.
	public char bmax;                ///< If a boundary link, defines the maximum sub-edge area.
}
