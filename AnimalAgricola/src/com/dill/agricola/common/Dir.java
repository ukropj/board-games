package com.dill.agricola.common;

public enum Dir {

	N, E, S, W;
	
	public Dir opposite() {
		switch (this) {
		case N:
			return S;
		case W:
			return E;
		case S:
			return N;
		case E:
			return W;
		default:
			throw new IllegalArgumentException();
		}
	}

}
