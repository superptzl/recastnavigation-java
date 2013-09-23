package org.recast.Detour.Include;

/**
 * @author igozha
 * @since 22.09.13 20:49
 */
/// Bounding volume node.
/// @note This structure is rarely if ever used by the end user.
/// @see dtMeshTile
public class dtBVNode
{
	public int bmin[] = new int[3];			///< Minimum bounds of the node's AABB. [(x, y, z)]
	public int bmax[] = new int[3];			///< Maximum bounds of the node's AABB. [(x, y, z)]
	public int i;							///< The node's index. (Negative for escape sequence.)
}
