package org.recast.RecastDemo.Include;

/**
 * @author igozha
 * @since 23.09.13 21:31
 */
public abstract class SampleTool
{
//	public abstract ~SampleTool() {}
	public abstract SampleToolType type();
	public abstract void init(Sample sample);
	public abstract void reset();
	public abstract void handleMenu();
	public abstract void handleClick(float[] s, float[] p, boolean shift);
//	public abstract void handleRender();
//	public abstract void handleRenderOverlay(double* proj, double* model, int* view);
//	public abstract void handleToggle();
	public abstract void handleStep();
//	public abstract void handleUpdate(float dt);
}
