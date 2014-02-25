package com.dill.agricola.model;


public class Pasture extends Space {

	private final static int CAP = 2;
	private final static int SOLO_FEEDER_CAP = 1;
	
	public Pasture() {
	}
	
	public int getMaxCapacity() {
		if (! isEnclosed()) {
			return hasTrough() ? SOLO_FEEDER_CAP : 0;
		}
		int feeders = getTroughCount();
		return feeders == 0 ? CAP : CAP * feeders * FEEDER_MULTI;
	}

	public boolean isAlwaysEnclosed() {
		return false;
	}

	/*public Space unbuild() {
		// cannot be unbuild any more
		return null;
	}*/

}
