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
	// extra
	BREEDING("breeding"),
	BUILDING_REWARD("buildReward"),
	ONE_TROUGH("oneTrough"),
	ONE_BORDER("oneBorder"),
	SWITCH_FOR_COW("switchForCow"),
	TRADE_ANIMALS("tradeAnimals"),
	MOVE_TROUGHS("moveTroughs"), 
	MOVE_STALLS("moveStalls"), 
	EMPTY_REARING_STATION("emptyRearingStation");
	
	public final String desc;
	public final String shortDesc;
	public final String farmText;
	
	private ActionType() {
		desc = "";
		shortDesc = "";
		farmText = "";
	}
	
	private ActionType(String code) {
		desc = Msg.get(code + "Act");
		shortDesc = Msg.get(code + "ShortAct");
		farmText = Msg.get(code + "Farm");
	}
}
