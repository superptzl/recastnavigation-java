package org.recast.Recast.Include;

/// Represents a span of unobstructed space within a compact heightfield.
public class rcCompactSpan {
    public int y;			///< The lower extent of the span. (Measured from the heightfield's base.)
    public int reg;			///< The id of the region the span belongs to. (Or zero if not in a region.)
    private int con/* = 24*/;		///< Packed neighbor connection data.
//    private int con/* = 24*/;		///< Packed neighbor connection data.
//    private boolean[] con = new boolean[24]/* = 24*/;		///< Packed neighbor connection data.
    private int h/* = 8*/;			///< The height of the span.  (Measured from #y.)

	/*public boolean[] getCon() {
		return con;
	}

	public void setCon(boolean[] con) {
		this.con = con;
	}*/

	public int getCon() {
		return con;
	}

	public void setCon(int con) {
		this.con = con;
	}

	public void setH(int h) {
//		System.out.println("setH");
		this.h = h;
	}

	public int getH()
	{
//		System.out.println("getH");
		return h;
	}
}
