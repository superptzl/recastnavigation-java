package org.recast.Recast.Source;

import org.recast.Recast.Include.BuildContext;
import org.recast.Recast.Include.rcLogCategory;
import org.recast.Recast.Include.rcTimerLabel;

public class BuildContextImpl extends BuildContext {
//    #define _USE_MATH_DEFINES
//    #include <math.h>
//    #include <stdio.h>
//    #include <stdarg.h>
//    #include "SampleInterfaces.h"
//            #include "Recast.h"
//            #include "RecastDebugDraw.h"
//            #include "DetourDebugDraw.h"
//            #include "PerfTimer.h"
//            #include "SDL.h"
//            #include "SDL_opengl.h"
//
//            #ifdef WIN32
//    #	define snprintf _snprintf
//    #endif

////////////////////////////////////////////////////////////////////////////////////////////////////

    public BuildContextImpl()
    {
//        m_messageCount(0),
//                m_textPoolSize(0)
        resetTimers();
    }

  /*  ~BuildContext()
    {
    }*/

    // Virtual functions for custom implementations.
    public void doResetLog()
    {
        m_messageCount = 0;
        m_textPoolSize = 0;
    }

    public void doLog(rcLogCategory category, String msg)
    {
//        if (!len) return;
//        if (m_messageCount >= MAX_MESSAGES)
//            return;
//        char* dst = &m_textPool[m_textPoolSize];
//        int n = TEXT_POOL_SIZE - m_textPoolSize;
//        if (n < 2)
//            return;
//        char* cat = dst;
//        char* text = dst+1;
//        const int maxtext = n-1;
//        Store category
//        *cat = (char)category;
//        Store message
//        const int count = rcMin(len+1, maxtext);
//        memcpy(text, msg, count);
//        text[count-1] = '\0';
//        m_textPoolSize += 1 + count;
//        m_messages[m_messageCount++] = dst;
        System.out.println("["+category + "]: " + msg);
    }

    public void doResetTimers()
    {
        for (rcTimerLabel label : rcTimerLabel.values())
            m_accTime.put(label, -1);// = -1;
    }

    public void doStartTimer( rcTimerLabel label)
    {
        m_startTime.put(label, getPerfTime());
    }

    public Long getPerfTime() {
        return System.currentTimeMillis();
    }

    public void doStopTimer( rcTimerLabel label)
    {
        long endTime = getPerfTime();
        int deltaTime = (int)(endTime - m_startTime.get(label));
        if (m_accTime.get(label) == -1)
            m_accTime.put(label, deltaTime);
        else
            m_accTime.put(label,  m_accTime.get(label) + deltaTime);
    }

    public int doGetAccumulatedTime( rcTimerLabel label)
    {
        return m_accTime.get(label);
    }

    public void dumpLog(String format, Object ...args)
    {
        // Print header.
//        va_list ap;
//        va_start(ap, format);
//        vprintf(format, ap);
//        va_end(ap);
//        printf("\n");

        // Print messages
        int[] TAB_STOPS = { 28, 36, 44, 52 };
        for (String message : m_messages) {
            System.out.println("message = " + message);
        }
//        for (int i = 0; i < m_messageCount; ++i)
//        {
//            String msg = m_messages[i]+1;
//            int n = 0;
//            while (msg != null)
//            {
//                if (msg.equals("\t"))
//                {
//                    int count = 1;
//                    for (int j = 0; j < 4; ++j)
//                    {
//                        if (n < TAB_STOPS[j])
//                        {
//                            count = TAB_STOPS[j] - n;
//                            break;
//                        }
//                    }
//                    while (--count)
//                    {
//                        putchar(' ');
//                        n++;
//                    }
//                }
//                else
//                {
//                    putchar(*msg);
//                    n++;
//                }
////                msg++;
//            }
////            putchar('\n');
//            System.out.println();
//        }
    }

    public int getLogCount()
    {
        return m_messageCount;
    }

    public String getLogText(int i)
    {
        return m_messages[i]+1;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
//
//    class GLCheckerTexture
//    {
//        unsigned int m_texId;
//        public:
//        GLCheckerTexture() : m_texId(0)
//    {
//    }
//
//        ~GLCheckerTexture()
//    {
//        if (m_texId != 0)
//            glDeleteTextures(1, &m_texId);
//    }
//        void bind()
//        {
//            if (m_texId == 0)
//            {
//                // Create checker pattern.
//                const unsigned int col0 = duRGBA(215,215,215,255);
//                const unsigned int col1 = duRGBA(255,255,255,255);
//                static const int TSIZE = 64;
//                unsigned int data[TSIZE*TSIZE];
//
//                glGenTextures(1, &m_texId);
//                glBindTexture(GL_TEXTURE_2D, m_texId);
//
//                int level = 0;
//                int size = TSIZE;
//                while (size > 0)
//                {
//                    for (int y = 0; y < size; ++y)
//                        for (int x = 0; x < size; ++x)
//                            data[x+y*size] = (x==0 || y==0) ? col0 : col1;
//                    glTexImage2D(GL_TEXTURE_2D, level, GL_RGBA, size,size, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
//                    size /= 2;
//                    level++;
//                }
//
//                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_NEAREST);
//                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
//            }
//            else
//            {
//                glBindTexture(GL_TEXTURE_2D, m_texId);
//            }
//        }
//    };
//    GLCheckerTexture g_tex;
//

//    void DebugDrawGL::depthMask(bool state)
//    {
//        glDepthMask(state ? GL_TRUE : GL_FALSE);
//    }
//
//    void DebugDrawGL::texture(bool state)
//    {
//        if (state)
//        {
//            glEnable(GL_TEXTURE_2D);
//            g_tex.bind();
//        }
//        else
//        {
//            glDisable(GL_TEXTURE_2D);
//        }
//    }
//
//    void DebugDrawGL::begin(duDebugDrawPrimitives prim, float size)
//    {
//        switch (prim)
//        {
//            case DU_DRAW_POINTS:
//                glPointSize(size);
//                glBegin(GL_POINTS);
//                break;
//            case DU_DRAW_LINES:
//                glLineWidth(size);
//                glBegin(GL_LINES);
//                break;
//            case DU_DRAW_TRIS:
//                glBegin(GL_TRIANGLES);
//                break;
//            case DU_DRAW_QUADS:
//                glBegin(GL_QUADS);
//                break;
//        };
//    }
//
//    void DebugDrawGL::vertex(const float* pos, unsigned int color)
//    {
//        glColor4ubv((GLubyte*)&color);
//        glVertex3fv(pos);
//    }
//
//    void DebugDrawGL::vertex(const float x, const float y, const float z, unsigned int color)
//    {
//        glColor4ubv((GLubyte*)&color);
//        glVertex3f(x,y,z);
//    }
//
//    void DebugDrawGL::vertex(const float* pos, unsigned int color, const float* uv)
//    {
//        glColor4ubv((GLubyte*)&color);
//        glTexCoord2fv(uv);
//        glVertex3fv(pos);
//    }
//
//    void DebugDrawGL::vertex(const float x, const float y, const float z, unsigned int color, const float u, const float v)
//    {
//        glColor4ubv((GLubyte*)&color);
//        glTexCoord2f(u,v);
//        glVertex3f(x,y,z);
//    }
//
//    void DebugDrawGL::end()
//    {
//        glEnd();
//        glLineWidth(1.0f);
//        glPointSize(1.0f);
//    }

////////////////////////////////////////////////////////////////////////////////////////////////////




    public int getPerfDeltaTimeUsec(long start, long end)
    {
        long freq = 100;
//        if (freq == 0)
//            QueryPerformanceFrequency((LARGE_INTEGER*)&freq);
        long elapsed = end - start;
        return (int)(elapsed*1000000 / freq);
    }
}
