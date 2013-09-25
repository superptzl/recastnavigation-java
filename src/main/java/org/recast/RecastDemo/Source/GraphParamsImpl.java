package org.recast.RecastDemo.Source;

import org.recast.RecastDemo.Include.GraphParams;
import org.recast.RecastDemo.Include.ValueHistory;

public class GraphParamsImpl extends GraphParams
{
	public void setRect(int ix, int iy, int iw, int ih, int ipad)
	{
		x = ix;
		y = iy;
		w = iw;
		h = ih;
		pad = ipad;
	}

	public void setValueRange(float ivmin, float ivmax, int indiv, String iunits)
	{
		vmin = ivmin;
		vmax = ivmax;
		ndiv = indiv;
		units = iunits;
	}

	public void drawGraphBackground(GraphParams p)
	{
		// BG
//        imguiDrawRoundedRect((float)p.x, (float)p.y, (float)p.w, (float)p.h, (float)p.pad, imguiRGBA(64,64,64,128));
//
//        float sy = (p.h-p.pad*2) / (p.vmax-p.vmin);
//        float oy = p.y+p.pad-p.vmin*sy;
//
//        char text[64];
//
//        // Divider Lines
//        for (int i = 0; i <= p.ndiv; ++i)
//        {
//            float u = (float)i/(float)p.ndiv;
//            float v = p.vmin + (p.vmax-p.vmin)*u;
//            snprintf(text, 64, "%.2f %s", v, p.units);
//            float fy = oy + v*sy;
//            imguiDrawText(p.x + p.w - p.pad, (int)fy-4, IMGUI_ALIGN_RIGHT, text, imguiRGBA(0,0,0,255));
//            imguiDrawLine((float)p.x + (float)p.pad, fy, (float)p.x + (float)p.w - (float)p.pad - 50, fy, 1.0f, imguiRGBA(0,0,0,64));
//        }
	}

	public void drawGraph(GraphParams p, ValueHistory graph,
						  int idx, char[] label, int col)
	{
//        float sx = (p.w - p.pad*2) / (float)graph.getSampleCount();
//        float sy = (p.h - p.pad*2) / (p.vmax - p.vmin);
//        float ox = (float)p.x + (float)p.pad;
//        float oy = (float)p.y + (float)p.pad - p.vmin*sy;
//
//        // Values
//        float px=0, py=0;
//        for (int i = 0; i < graph.getSampleCount()-1; ++i)
//        {
//            float x = ox + i*sx;
//            float y = oy + graph.getSample(i)*sy;
//            if (i > 0)
//                imguiDrawLine(px,py, x,y, 2.0f, col);
//            px = x;
//            py = y;
//        }
//
//        // Label
//        int size = 15;
//        int spacing = 10;
//        int ix = p.x + p.w + 5;
//        int iy = p.y + p.h - (idx+1)*(size+spacing);
//
//        imguiDrawRoundedRect((float)ix, (float)iy, (float)size, (float)size, 2.0f, col);
//
//        char text[64];
//        snprintf(text, 64, "%.2f %s", graph.getAverage(), p.units);
//        imguiDrawText(ix+size+5, iy+3, IMGUI_ALIGN_LEFT, label, imguiRGBA(255,255,255,192));
//        imguiDrawText(ix+size+150, iy+3, IMGUI_ALIGN_RIGHT, text, imguiRGBA(255,255,255,128));
	}

}
