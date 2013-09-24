package org.recast.Detour.Include;

public class dtNode {
    public float pos[] = new float[3];				///< Position of the node.
    public float cost;					///< Cost from previous node to current node.
    public float total;				///< Cost up to the node.
    public int pidx;		///< Index to parent node.
    public int flags;		///< Node flags 0/open/closed.
    public dtPoly id;				///< Polygon ref the node corresponds to.
}
