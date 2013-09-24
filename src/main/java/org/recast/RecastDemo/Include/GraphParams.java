package org.recast.RecastDemo.Include;

public class GraphParams {
    void setRect(int ix, int iy, int iw, int ih, int ipad);
    void setValueRange(float ivmin, float ivmax, int indiv, const char* iunits);

    int x, y, w, h, pad;
    float vmin, vmax;
    int ndiv;
    char units[16];

    void drawGraphBackground(const GraphParams* p);

    void drawGraph(const GraphParams* p, const ValueHistory* graph,
                   int idx, const char* label, const unsigned int col);
}
