package org.recast.Recast.Include;

public class rcSpanPool {
    public rcSpanPool next;					///< The next span pool.
    public rcSpan items[] = new rcSpan[Recast.RC_SPANS_PER_POOL];	///< Array of spans in the pool.
    public rcSpanPool()
    {
        for (int i = 0; i < items.length; i++) {
            items[i] = new rcSpan();
        }
    }
}
