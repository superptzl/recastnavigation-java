package org.recast.Recast.Include;

/// Represents a span of unobstructed space within a compact heightfield.
public class rcCompactSpan {
    public short y;			///< The lower extent of the span. (Measured from the heightfield's base.)
    public short reg;			///< The id of the region the span belongs to. (Or zero if not in a region.)
    public int con/* = 24*/;		///< Packed neighbor connection data.
    public int h/* = 8*/;			///< The height of the span.  (Measured from #y.)
}
