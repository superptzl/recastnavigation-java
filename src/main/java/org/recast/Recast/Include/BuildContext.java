package org.recast.Recast.Include;

import org.recast.Recast.Source.rcContextImpl;

import java.util.HashMap;
import java.util.Map;

public abstract class BuildContext extends rcContextImpl
{


//    public long m_startTime[] = new long[rcTimerLabel.values().length];
    public Map<rcTimerLabel, Long> m_startTime = new HashMap<>();//[rcTimerLabel.values().length];
//    public int m_accTime[] = new int[rcTimerLabel.values().length];
    public Map<rcTimerLabel, Integer> m_accTime = new HashMap<>();//new int[rcTimerLabel.values().length];

    public static final int MAX_MESSAGES = 1000;
    public String m_messages[] = new String[MAX_MESSAGES];
    public int m_messageCount;
    public static final int TEXT_POOL_SIZE = 8000;
    public char m_textPool[] = new char[TEXT_POOL_SIZE];
    public int m_textPoolSize;

    public BuildContext() {}
//    virtual ~BuildContext();

    /// Dumps the log to stdout.
    public abstract void dumpLog(String format, Object ...args);
    /// Returns number of log messages.
    public abstract int getLogCount();
    /// Returns log message text.
    public abstract String getLogText(int i);

//    protected:
    /// Virtual functions for custom implementations.
    ///@{
public  abstract void doResetLog();
    public  abstract void doLog(rcLogCategory category, String msg);
    public abstract void doResetTimers();
    public  abstract void doStartTimer(rcTimerLabel label);
    public  abstract void doStopTimer(rcTimerLabel label);
    public  abstract int doGetAccumulatedTime(rcTimerLabel label) ;
    ///@}
}
