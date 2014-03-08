package com.dill.agricola.common;

import java.awt.Point;

@SuppressWarnings("serial")
public class DirPoint extends Point {

	public final Dir dir;

	
	public DirPoint(int x, int y) {
		this(x, y, null);
	}
	
	public DirPoint(int x, int y, Dir dir) {
		super(x, y);
		this.dir = dir;
	}

	public DirPoint(DirPoint p) {
		this(p, p.dir);
	}
	
	public DirPoint(DirPoint pos, Dir dir) {
		super(pos);
		this.dir = dir;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dir == null) ? 0 : dir.hashCode());
		result = prime * result + ((this == null) ? 0 : super.hashCode());
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
		return super.equals(other);
	}

	public String toString() {
		return (dir != null ? dir.toString() : "C") + " " + super.toString();
	}

	public Point toPoint() {
		return new Point(x, y);
	}
	
}
