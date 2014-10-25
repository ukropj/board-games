package com.dill.agricola.actions;

/**
 * Interface for actions that can be activated by button anytime 
 * (usually) during player turn.
 */
public interface FeatureAction extends Action {
	
	/**
	 * True when not managed by ActionPerformer
	 */
	boolean isQuickAction();
	
	boolean canDoDuringBreeding();
	
	String getButtonIconName();
	
}
