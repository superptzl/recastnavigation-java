package org.recast.RecastDemo.Include;

public abstract class GraphParams {
    public abstract void setRect(int ix, int iy, int iw, int ih, int ipad);
    public abstract void setValueRange(float ivmin, float ivmax, int indiv,  String iunits);

    public int x, y, w, h, pad;
    public float vmin, vmax;
    public int ndiv;
    public String units;//[] = new char[16];

    public abstract void drawGraphBackground( GraphParams p);

    public abstract void drawGraph( GraphParams p,  ValueHistory graph,
                   int idx,  char[] label,  int col);
}
