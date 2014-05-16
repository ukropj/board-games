package com.dill.agricola.model.types;

import com.dill.agricola.support.Msg;

public enum ActionType {
	STARTING_ONE_WOOD("startWood"),
	THREE_WOOD("wood"),
	TWO_STONE("stone"),
	ONE_STONE("stone"),
	BUILDING_MATERIAL("material"),
	BORDERS_EXPAND("bordersExpand"),
	FENCES("fences"),
	WALLS("walls"),
	TROUGHS("troughs"),
	STALLS("stall"),
	STABLES("stables"),
	SPECIAL("special"),
	SPECIAL2("special"),
	MILLPOND("millpond"),
	PIG_AND_SHEEP("pigSheep"),
	COW_AND_PIGS("cowPigs"),
	HORSE_AND_SHEEP("horseSheep"),
	//
	BUILDING_REWARD("buildReward"),
	ONE_FREE_TROUGH(),
	SWITCH_FOR_COW("switchForCow");
	
	public final String desc;
	public final String shortDesc;
	
	private ActionType() {
		this.desc = "";
		this.shortDesc = "";
	}
	
	private ActionType(String code) {
		this.desc = Msg.get(code + "Act");
		this.shortDesc = Msg.get(code + "ShortAct");
	}
}
