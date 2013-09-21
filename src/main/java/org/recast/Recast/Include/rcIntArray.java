package org.recast.Recast.Include;

/**
 * @author igozha
 * @since 20.09.13 22:19
 */
public abstract class rcIntArray
{
	public int[] m_data;
	public int m_size, m_cap;
//		 rcIntArray(const rcIntArray&);
//		 rcIntArray& operator=(const rcIntArray&);
//	public:

		/// Constructs an instance with an initial array size of zero.
		public  rcIntArray() /*: m_data(0), m_size(0), m_cap(0)*/ {
			m_data = new int[]{};
			m_size = 0;
			m_cap = 0;
		}

		/// Constructs an instance initialized to the specified size.
		///  @param[in]		n	The initial size of the integer array.
		public  rcIntArray(int n) /*: m_data(0), m_size(0), m_cap(0) */{
//			m_c
			resize(n); }
//	public  ~rcIntArray() { rcFree(m_data); }

		/// Specifies the new size of the integer array.
		///  @param[in]		n	The new size of the integer array.
		public abstract void resize(int n);

		/// Push the specified integer onto the end of the array and increases the size by one.
		///  @param[in]		item	The new value.
	public  void push(int item) { resize(m_size+1); m_data[m_size-1] = item; }

		/// Returns the value at the end of the array and reduces the size by one.
		///  @return The value at the end of the array.
	public  int pop() { if (m_size > 0) m_size--; return m_data[m_size]; }

		/// The value at the specified array index.
		/// @warning Does not provide overflow protection.
		///  @param[in]		i	The index of the value.
//	public  const int& operator[](int i) const { return m_data[i]; }

		/// The value at the specified array index.
		/// @warning Does not provide overflow protection.
		///  @param[in]		i	The index of the value.
//	public  int& operator[](int i) { return m_data[i]; }

		/// The current size of the integer array.
	public  int size() { return m_size; }

	public void set(int i, int v) {
		m_data[i] = v;
	}

	public int get(int i) {
		return m_data[i];
	}
}
