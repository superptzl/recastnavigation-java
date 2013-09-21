package org.recast.Recast.Include;

public class rcCompactCell {
    private int index/* = 24*/;	///< Index to the first span in the column.
	private int count/* = 8*/;		///< Number of spans in the column.

	public void setIndex(int index) {
//		System.out.println("setIndex");
		this.index = index;
	}

	public int getIndex() {
//		System.out.println("getIndex");
		return index;
	}

	public int getCount() {
//		System.out.println("getCount");
		return count;
	}

	public void setCount(int count) {
//		System.out.println("setCount");
		this.count = count;
	}

	@Override
	public String toString()
	{
		return "rcCompactCell{" +
			"index=" + index +
			", count=" + count +
			'}';
	}
}
