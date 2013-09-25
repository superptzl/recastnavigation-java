package org.recast.Recast.Include;

public abstract class rcContext
{
//    public:

	public rcContext()
	{
		this(true);
	}

	/// Contructor.
	///  @param[in]		state	TRUE if the logging and performance timers should be enabled.  [Default: true]
	public rcContext(boolean state)
	{
		m_logEnabled = state;
		m_timerEnabled = state;
	}
//    virtual ~rcContext() {}

	/// Enables or disables logging.
	///  @param[in]		state	TRUE if logging should be enabled.
	public void enableLog(boolean state)
	{
		m_logEnabled = state;
	}

	/// Clears all log entries.
	public void resetLog()
	{
		if (m_logEnabled) doResetLog();
	}

	/// Logs a message.
	///  @param[in]		category	The category of the message.
	///  @param[in]		format		The message.
	public abstract void log(rcLogCategory category, String format, Object... args);

	/// Enables or disables the performance timers.
	///  @param[in]		state	TRUE if timers should be enabled.
	public void enableTimer(boolean state)
	{
		m_timerEnabled = state;
	}

	/// Clears all peformance timers. (Resets all to unused.)
	public void resetTimers()
	{
		if (m_timerEnabled) doResetTimers();
	}

	/// Starts the specified performance timer.
	///  @param	label	The category of timer.
	public void startTimer(rcTimerLabel label)
	{
		if (m_timerEnabled) doStartTimer(label);
	}

	/// Stops the specified performance timer.
	///  @param	label	The category of the timer.
	public void stopTimer(rcTimerLabel label)
	{
		if (m_timerEnabled) doStopTimer(label);
	}

	/// Returns the total accumulated time of the specified performance timer.
	///  @param	label	The category of the timer.
	///  @return The accumulated time of the timer, or -1 if timers are disabled or the timer has never been started.
	public int getAccumulatedTime(rcTimerLabel label)
	{
		return m_timerEnabled ? doGetAccumulatedTime(label) : -1;
	}

	/// Clears all log entries.
	public void doResetLog()
	{
	}

	/// Logs a message.
	///  @param[in]		category	The category of the message.
	///  @param[in]		msg			The formatted message.
	///  @param[in]		len			The length of the formatted message.
	public void doLog(rcLogCategory category, String msg)
	{
	}

	/// Clears all timers. (Resets all to unused.)
	public void doResetTimers()
	{
	}

	/// Starts the specified performance timer.
	///  @param[in]		label	The category of timer.
	public void doStartTimer(rcTimerLabel label)
	{
	}

	/// Stops the specified performance timer.
	///  @param[in]		label	The category of the timer.
	public void doStopTimer(rcTimerLabel label)
	{
	}

	/// Returns the total accumulated time of the specified performance timer.
	///  @param[in]		label	The category of the timer.
	///  @return The accumulated time of the timer, or -1 if timers are disabled or the timer has never been started.
	public int doGetAccumulatedTime(rcTimerLabel label)
	{
		return -1;
	}

	/// True if logging is enabled.
	public boolean m_logEnabled;

	/// True if the performance timers are enabled.
	public boolean m_timerEnabled;
}
