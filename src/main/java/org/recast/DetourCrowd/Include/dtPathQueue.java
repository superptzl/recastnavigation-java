package org.recast.DetourCrowd.Include;

import org.recast.Detour.Include.*;

public abstract class dtPathQueue {
    public final static int DT_PATHQ_INVALID = 0;

    public static class PathQuery
    {
        public dtPathQueue ref;
        /// Path find start and end location.
        public float startPos[] = new float[3], endPos[] = new float[3];
        public dtPoly startRef, endRef;
        /// Result.
        public dtPoly[] path;
        public int npath;
        /// State.
        public dtStatus status;
        public int keepAlive;
        public dtQueryFilter filter; ///< TODO: This is potentially dangerous!
    }

    public final static int MAX_QUEUE = 8;
    public PathQuery m_queue[] = new PathQuery[MAX_QUEUE];
    public dtPathQueue m_nextHandle;
    public int m_maxPathSize;
    public int m_queueHead;
    public dtNavMeshQuery m_navquery;

    public abstract void purge();

//    public:
//    dtPathQueue();
//    ~dtPathQueue();

    public abstract boolean init(int maxPathSize, int maxSearchNodeCount, dtNavMesh nav);

    /*public abstract void update(int maxIters);

    public abstract dtPathQueue request(dtPoly startRef, dtPoly endRef,
                           float[] startPos, float[] endPos,
                           dtQueryFilter filter);

    public abstract dtStatus getRequestStatus(dtPathQueue ref);

    public abstract dtStatus getPathResult(dtPathQueue ref, dtPoly path, int[] pathSize, int maxPath);

    public dtNavMeshQuery getNavQuery() { return m_navquery; }*/

}
