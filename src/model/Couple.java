package model;

import java.io.Serializable;

public class Couple<X, Y> implements Serializable{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 6790244705107202731L;
	private X first;
	private Y second;

	public Couple(X first, Y second) {
		this.first = first;
		this.second = second;
	}

	public X getFirst() {
		return first;
	}

	public Y getSecond() {
		return second;
	}

	public void setFirst(X arg) {
		first = arg;
	}

	public void setSecond(Y arg) {
		second = arg;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Couple<?, ?> other = (Couple<?, ?>) obj;
		if (first == null) {
			if (other.first != null)
				return false;
		} else if (!first.equals(other.first))
			return false;
		if (second == null) {
			if (other.second != null)
				return false;
		} else if (!second.equals(other.second))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Couple [first=" + first + ", second=" + second + "]";
	}	
}
