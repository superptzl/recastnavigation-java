package org.recast.Detour.Include;

/**
 * @author igozha
 * @since 22.09.13 20:56
 */
/// Defines an navigation mesh off-mesh connection within a dtMeshTile object.
/// An off-mesh connection is a user defined traversable connection made up to two vertices.
public class dtOffMeshConnection
{
	/// The endpoints of the connection. [(ax, ay, az, bx, by, bz)]
		public float pos[] = new float[6];

		/// The radius of the endpoints. [Limit: >= 0]
		public float rad;

		/// The polygon reference of the connection within the tile.
		public int poly;

		/// Link flags.
		/// @note These are not the connection's user defined flags. Those are assigned via the
		/// connection's dtPoly definition. These are link flags used for internal purposes.
		public int flags;

		/// End point side.
		public char side;

		/// The id of the offmesh connection. (User assigned when the navigation mesh is built.)
		public int userId;
}
