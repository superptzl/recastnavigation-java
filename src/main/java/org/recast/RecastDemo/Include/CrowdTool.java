package org.recast.RecastDemo.Include;

public abstract class CrowdTool {
    public Sample m_sample;
    public CrowdToolState m_state;

    public static enum ToolMode
    {
        TOOLMODE_CREATE,
        TOOLMODE_MOVE_TARGET,
        TOOLMODE_SELECT,
        TOOLMODE_TOGGLE_POLYS,
    };
    public ToolMode m_mode;

    public abstract void updateAgentParams();
//    void updateTick(const float dt);

//    public:
//    CrowdTool();
//    virtual ~CrowdTool();

    public SampleToolType type() { return SampleToolType.TOOL_CROWD; }
    public abstract void init(Sample sample);
//    public abstract void reset();
//    public abstract void handleMenu();
//    public abstract void handleClick(const float* s, const float* p, bool shift);
//    public abstract void handleToggle();
//    public abstract void handleStep();
//    public abstract void handleUpdate(const float dt);
//    public abstract void handleRender();
//    public abstract void handleRenderOverlay(double* proj, double* model, int* view);
}
