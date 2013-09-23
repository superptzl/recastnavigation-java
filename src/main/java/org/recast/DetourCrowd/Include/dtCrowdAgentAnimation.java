package org.recast.DetourCrowd.Include;

import org.recast.Detour.Include.dtPoly;

public class dtCrowdAgentAnimation {
    public char active;
    public float initPos[] = new float[3], startPos[] = new float[3], endPos[] = new float[3];
    public dtPoly polyRef;
    public float t, tmax;
}
