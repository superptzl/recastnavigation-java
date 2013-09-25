package org.recast.RecastDemo.Include;

import org.recast.Detour.Include.DetourCommon;

public abstract class CrowdTool extends SampleTool
{
	public Sample m_sample;
	public CrowdToolState m_state;

	public static enum ToolMode
	{
		TOOLMODE_CREATE,
		TOOLMODE_MOVE_TARGET,
		TOOLMODE_SELECT,
		TOOLMODE_TOGGLE_POLYS,
	}

	public ToolMode m_mode;

//    public abstract void updateAgentParams();
//    void updateTick(const float dt);

//    public:
//    CrowdTool();
//    virtual ~CrowdTool();

	public SampleToolType type()
	{
		return SampleToolType.TOOL_CROWD;
	}

	public abstract void init(Sample sample);

	//    public abstract void reset();
	public abstract void handleMenu();

	public abstract void handleClick(float[] s, float[] p, boolean shift);

	public abstract void handleToggle();

	public abstract void handleStep();

	public abstract void handleUpdate(float dt);
//    public abstract void handleRender();
//    public abstract void handleRenderOverlay(double* proj, double* model, int* view);

	public static void calcVel(float[] vel, float[] pos, float[] tgt, float speed)
	{
		DetourCommon.dtVsub(vel, tgt, pos);
		vel[1] = 0.0f;
		DetourCommon.dtVnormalize(vel);
		DetourCommon.dtVscale(vel, vel, speed);
	}
}
