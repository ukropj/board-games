package com.dill.agricola.model.buildings.more;

import com.dill.agricola.common.Animals;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.model.Space;

public interface ExtraCapacityProvider {

	public Animals getExtraCapacity(DirPoint pos, Space space);
	
}
