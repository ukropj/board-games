package com.dill.agricola.model.types;

import com.dill.agricola.support.Msg;

public enum ActionType {
	STARTING_ONE_WOOD("startWood"),
	THREE_WOOD("wood"),
	ONE_STONE("stone"),
	TWO_STONE("stone2"),
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
	COTTAGE("cottage"),
	BREEDING("breeding"),
	BUILDING_REWARD("buildReward"),
	ONE_TROUGH("oneTrough"),
	ONE_FREE_BORDER("oneBorder"),
	FREE_BORDERS("freeBorders"),
	TRADE_FOR_COW("tradeForCow"),
	TRADE_ANIMALS("tradeAnimals"),
	MOVE_TROUGHS("moveTroughs"), 
	MOVE_STALLS_AND_TROUGHS("moveStallsTroughs"), 
	EMPTY_REARING_STATION("emptyRearingStation"),
	FREE_STABLES(),
	TRADE_REED("tradeReed"), 
	TRADE_MATERIALS("tradeMaterials"), 
	UPGRADE_TROUGH("upgradeTrough"), 
	CARVE_STONE("carveStone"), 
	GIVE_BORDERS("giveBorders");
	
	public final String desc;
	public final String shortDesc;
	public final String farmText;
	
	private ActionType() {
		desc = "";
		shortDesc = "";
		farmText = "";
	}
	
	private ActionType(String code) {
		desc = addStyling(Msg.get(code + "Act"));
		shortDesc = Msg.get(code + "ShortAct");
		farmText = Msg.get(code + "Farm");
	}

	private String addStyling(String string) {
		return string.replaceFirst("^\"", "<html><b>").replaceFirst("\":", ":</b>");
	}
}
