package com.dill.agricola.model.enums;

public class DirPoint {

	public Point point;
	public Dir dir;
	
	public DirPoint(Point point, Dir dir) {
		this.point = point;
		this.dir = dir;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dir == null) ? 0 : dir.hashCode());
		result = prime * result + ((point == null) ? 0 : point.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DirPoint other = (DirPoint) obj;
		if (dir != other.dir)
			return false;
		if (point == null) {
			if (other.point != null)
				return false;
		} else if (!point.equals(other.point))
			return false;
		return true;
	}
	
	public String toString() {
		return dir.toString() + " " + point.toString();
	}
	
}
