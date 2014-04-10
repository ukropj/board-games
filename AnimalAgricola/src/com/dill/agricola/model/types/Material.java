package com.dill.agricola.model.types;

import com.dill.agricola.support.Msg;

public enum Material {

	WOOD, STONE, REED,
	BORDER;
	
	public String getName() {
		return Msg.get(toString().toLowerCase());
	}
}
