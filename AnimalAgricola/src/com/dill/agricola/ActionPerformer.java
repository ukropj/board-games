package com.dill.agricola;

import com.dill.agricola.actions.Action;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.enums.ChangeType;

public class ActionPerformer {

	private Player player = null;
	private Action action = null;
	
	private boolean onceDone = false;
	private int count = 0;
	
	private void checkState() throws IllegalStateException {
		if (player == null || action == null) {
			throw new IllegalStateException("Cannot perform action without action or player");
		}
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public boolean hasAction() {
		return action != null;
	}
	
	public Action getAction() {
		return action;
	}
	
	public boolean doAction(Action action) {
		this.action = action;
		this.onceDone = false;
		this.count = 0;
		checkState();
		
		if (!action.canPerform(player)) {
			return false;
		}
		
		onceDone = action.doOnce(player);
		player.spendWorker();
		player.notifyObservers(ChangeType.ACTION_DO);		

		return onceDone || canDoMore();
	}
	
	public boolean canDoMore() {
		return action.canPerformMore(player, count);
	}
	
	public boolean doActionMore() {
		checkState();
		if (canDoMore()) {
			if (action.doo(player)) {
				count++;
				player.notifyObservers(ChangeType.ACTION_MORE);
				return true;
			}
		}
		return false;
	}

	public boolean canDoLess() {
		return count > 0;
	}
	
	public boolean doActionLess() {
		checkState();
		if (canDoLess()) {
			if (action.undo(player)) {
				count--;
				player.notifyObservers(ChangeType.ACTION_LESS);
				return true;
			}
		}
		return false;
	}

	public boolean undoAction() {
		checkState();
		boolean done = true;
		while (count > 0) {
			done = action.undo(player);
			if (!done) {
				break;
			}
			count--;
		}
		if (onceDone) {
			done = action.undoOnce(player) && done;
		}
		if (done) {
			action = null;
			player.returnWorker();
			player.setActiveType(null);
		}
		player.notifyObservers(ChangeType.ACTION_UNDO);
		return done;
	}

	public boolean finishAction() {
		checkState();
		if (!onceDone && count == 0) {
			// never done
			return false;
		}
		if (player.validate()) {
			player.setActiveType(null);
			action.setUsed();
			System.out.println("Action done: " + Namer.getName(action));
			action = null;
			player.notifyObservers(ChangeType.ACTION_DONE);
			return true;
		} else{
			return false;
		}
	}
	
	
	
}