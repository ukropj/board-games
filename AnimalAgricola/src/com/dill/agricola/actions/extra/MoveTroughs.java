package com.dill.agricola.actions.extra;

import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Purchasable;

public class MoveTroughs extends MovePurchasable/* implements FeatureAction*/ {

	public MoveTroughs() {
		super(ActionType.MOVE_TROUGHS, Purchasable.TROUGH);
	}

}
