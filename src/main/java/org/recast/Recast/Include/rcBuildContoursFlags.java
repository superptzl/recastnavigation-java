package org.recast.Recast.Include;

public enum rcBuildContoursFlags
{
	RC_CONTOUR_TESS_WALL_EDGES(0x01),    ///< Tessellate solid (impassable) edges during contour simplification.
	RC_CONTOUR_TESS_AREA_EDGES(0x02);    ///< Tessellate edges between areas during contour simplification.

	public int v;

	rcBuildContoursFlags(int v)
	{
		this.v = v;
	}
}
