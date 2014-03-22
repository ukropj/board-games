package com.dill.agricola.model.types;

public enum ActionType {
	STARTING_ONE_WOOD("Start player and wood"),
	THREE_WOOD("Wood"),
	TWO_STONE("Stone"),
	ONE_STONE("Stone"),
	BUILDING_MATERIAL("Wood/Stone/Reed"),
	EXPAND("Expand"),
	FENCES("Borders"),
	WALLS("Borders"),
	STALLS("Stall"),
	STABLES("Stables"),
	SPECIAL("Special"),
	SPECIAL2("Special"),
	TROUGHS("Troughs"),
	MILLPOND("Reed/Sheep"),
	PIG_AND_SHEEP("Pig/Sheep"),
	COW_AND_PIGS("Cow/Pigs"),
	HORSE_AND_SHEEP("Horse/Sheep");
	
	public final String name;
	
	private ActionType(String name) {
		this.name = name;
	}
}
