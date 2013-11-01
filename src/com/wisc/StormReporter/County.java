package com.wisc.StormReporter;

public class County {

	private int count;
	private String name;
	
	public County(String countyName) {
		this.name = countyName;
		this.count = 1;
	
	}


// Mutators
	public void addCount() {
		this.count++;
	}
	public void resetCount() {
		this.count = 0;
	}
	public void setName(String newName) {
		this.name = newName;
	}
// Getters
	public String getName() {
		return this.name;
	}
	public int getCount() {
		return this.count;
	}
	/**
	 * Compares the count of the two counties, used for sorting
	 * between who has the most. Also checks if the 
	 * objects being compared are actually counties. Throws a ClassCastException
	 * if not.
	 *
	 * @param rhs object to be compared to the first object (movers)
	 * @return int representing if mover 2 has more food than mover 1
	 */
	public int compareTo (Object rhs) throws ClassCastException {

		if (rhs instanceof County) {
			County m2 = (County) rhs;
			if (this.count > m2.count) {
				return -1;
			}
			else if (this.count < m2.count) {
				return 1;
			}
			else {
				return 0;
			}
		}
		else {
			throw new ClassCastException();
		}
	}
}
