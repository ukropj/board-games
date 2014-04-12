package com.dill.agricola.model.types;

import com.dill.agricola.support.Msg;

public enum ActionType {
	STARTING_ONE_WOOD(Msg.get("startWoodAct")),
	THREE_WOOD(Msg.get("woodAct")),
	TWO_STONE(Msg.get("stoneAct")),
	ONE_STONE(Msg.get("stoneAct")),
	BUILDING_MATERIAL(Msg.get("materialAct")),
	EXPAND(Msg.get("expandAct")),
	FENCES(Msg.get("fencesAct")),
	WALLS(Msg.get("wallsAct")),
	TROUGHS(Msg.get("troughsAct")),
	STALLS(Msg.get("stallAct")),
	STABLES(Msg.get("stablesAct")),
	SPECIAL(Msg.get("specialAct")),
	SPECIAL2(Msg.get("specialAct")),
	MILLPOND(Msg.get("millpondAct")),
	PIG_AND_SHEEP(Msg.get("pigSheepAct")),
	COW_AND_PIGS(Msg.get("cowPigsAct")),
	HORSE_AND_SHEEP(Msg.get("horseSheepAct"));
	
	public final String desc;
	
	private ActionType(String name) {
		this.desc = name;
	}
}
