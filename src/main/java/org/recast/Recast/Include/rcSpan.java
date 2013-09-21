package org.recast.Recast.Include;

public class rcSpan {
    public int smin/* = 13*/;			///< The lower limit of the span. [Limit: < #smax]
    public int smax/* = 13*/;			///< The upper limit of the span. [Limit: <= #RC_SPAN_MAX_HEIGHT]
    public int area/* = 6*/;			///< The area id assigned to the span.
    public rcSpan next;					///< The next span higher up in column.
}
