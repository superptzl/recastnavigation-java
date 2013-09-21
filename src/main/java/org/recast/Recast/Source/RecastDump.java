package org.recast.Recast.Source;

import org.recast.Recast.Include.rcContext;
import org.recast.Recast.Include.rcLogCategory;
import org.recast.Recast.Include.rcTimerLabel;

public class RecastDump {
    public void duLogBuildTimes(rcContext ctx, int totalTimeUsec)
    {
        float pc = 100.0f / totalTimeUsec;

        ctx.log(rcLogCategory.RC_LOG_PROGRESS, "Build Times");
        logLine(ctx, rcTimerLabel.RC_TIMER_RASTERIZE_TRIANGLES,		"- Rasterize", pc);
        logLine(ctx, rcTimerLabel.RC_TIMER_BUILD_COMPACTHEIGHTFIELD,	"- Build Compact", pc);
        logLine(ctx, rcTimerLabel.RC_TIMER_FILTER_BORDER,				"- Filter Border", pc);
        logLine(ctx, rcTimerLabel.RC_TIMER_FILTER_WALKABLE,			"- Filter Walkable", pc);
        logLine(ctx, rcTimerLabel.RC_TIMER_ERODE_AREA,				"- Erode Area", pc);
        logLine(ctx, rcTimerLabel.RC_TIMER_MEDIAN_AREA,				"- Median Area", pc);
        logLine(ctx, rcTimerLabel.RC_TIMER_MARK_BOX_AREA,				"- Mark Box Area", pc);
        logLine(ctx, rcTimerLabel.RC_TIMER_MARK_CONVEXPOLY_AREA,		"- Mark Convex Area", pc);
        logLine(ctx, rcTimerLabel.RC_TIMER_MARK_CYLINDER_AREA,		"- Mark Cylinder Area", pc);
        logLine(ctx, rcTimerLabel.RC_TIMER_BUILD_DISTANCEFIELD,		"- Build Distance Field", pc);
        logLine(ctx, rcTimerLabel.RC_TIMER_BUILD_DISTANCEFIELD_DIST,	"    - Distance", pc);
        logLine(ctx, rcTimerLabel.RC_TIMER_BUILD_DISTANCEFIELD_BLUR,	"    - Blur", pc);
        logLine(ctx, rcTimerLabel.RC_TIMER_BUILD_REGIONS,				"- Build Regions", pc);
        logLine(ctx, rcTimerLabel.RC_TIMER_BUILD_REGIONS_WATERSHED,	"    - Watershed", pc);
        logLine(ctx, rcTimerLabel.RC_TIMER_BUILD_REGIONS_EXPAND,		"      - Expand", pc);
        logLine(ctx, rcTimerLabel.RC_TIMER_BUILD_REGIONS_FLOOD,		"      - Find Basins", pc);
        logLine(ctx, rcTimerLabel.RC_TIMER_BUILD_REGIONS_FILTER,		"    - Filter", pc);
        logLine(ctx, rcTimerLabel.RC_TIMER_BUILD_LAYERS,				"- Build Layers", pc);
        logLine(ctx, rcTimerLabel.RC_TIMER_BUILD_CONTOURS,			"- Build Contours", pc);
        logLine(ctx, rcTimerLabel.RC_TIMER_BUILD_CONTOURS_TRACE,		"    - Trace", pc);
        logLine(ctx, rcTimerLabel.RC_TIMER_BUILD_CONTOURS_SIMPLIFY,	"    - Simplify", pc);
        logLine(ctx, rcTimerLabel.RC_TIMER_BUILD_POLYMESH,			"- Build Polymesh", pc);
        logLine(ctx, rcTimerLabel.RC_TIMER_BUILD_POLYMESHDETAIL,		"- Build Polymesh Detail", pc);
        logLine(ctx, rcTimerLabel.RC_TIMER_MERGE_POLYMESH,			"- Merge Polymeshes", pc);
        logLine(ctx, rcTimerLabel.RC_TIMER_MERGE_POLYMESHDETAIL,		"- Merge Polymesh Details", pc);
        ctx.log(rcLogCategory.RC_LOG_PROGRESS, "=== TOTAL:\t%.2fms", totalTimeUsec/1000.0f);
    }

    public static void logLine(rcContext ctx, rcTimerLabel label, String name, float pc)
    {
        int t = ctx.getAccumulatedTime(label);
        if (t < 0) return;
        ctx.log(rcLogCategory.RC_LOG_PROGRESS, "%s:\t%.2fms\t(%.1f%%)", name, t/1000.0f, t*pc);
    }
}
