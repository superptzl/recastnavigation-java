package org.recast.Recast.Include;

public class rcSpan
{
	private int smin/* = 13*/;            ///< The lower limit of the span. [Limit: < #smax]
	private int smax/* = 13*/;            ///< The upper limit of the span. [Limit: <= #RC_SPAN_MAX_HEIGHT]
	private int area/* = 6*/;            ///< The area id assigned to the span.
	public rcSpan next;                    ///< The next span higher up in column.

	public void setArea(int area)
	{
//		System.out.println("setArea");
		this.area = area;
	}

	public int getArea()
	{
//		System.out.println("getArea");
		return area;
	}

	public void setSmax(int smax)
	{
//		System.out.println("setSmax");
		this.smax = smax;
	}

	public int getSmax()
	{
//		System.out.println("getSmax");
		return smax;
	}

	public void setSmin(int smin)
	{
//		System.out.println("setSmin");
		this.smin = smin;
	}

	public int getSmin()
	{
//		System.out.println("getSmin");
		return smin;
	}
}
