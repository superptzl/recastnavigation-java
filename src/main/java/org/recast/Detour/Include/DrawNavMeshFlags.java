package org.recast.Detour.Include;

public enum DrawNavMeshFlags {
    DU_DRAWNAVMESH_OFFMESHCONS(0x01),
    DU_DRAWNAVMESH_CLOSEDLIST(0x02),
    DU_DRAWNAVMESH_COLOR_TILES(0x04);

    public int v;

    DrawNavMeshFlags(int v) {
        this.v = v;
    }
}
