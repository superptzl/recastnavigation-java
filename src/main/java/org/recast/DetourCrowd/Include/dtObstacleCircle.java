package org.recast.DetourCrowd.Include;

public class dtObstacleCircle {
    public float p[] = new float[3];				///< Position of the obstacle
    public float vel[] = new float[3];			///< Velocity of the obstacle
    public float dvel[] = new float[3];			///< Velocity of the obstacle
    public float rad;				///< Radius of the obstacle
    public float dp[] = new float[3], np[] = new float[3];		///< Use for side selection during sampling.
}
