package com.dill.agricola.actions;

import com.dill.agricola.common.DirPoint;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ChangeType;
import com.dill.agricola.support.Namer;

public class ActionPerformer {

	private Player player = null;
	private Action action = null;

	private int count = 0;
	private ActionPerfListener perfListener;

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

	public boolean startAction(Action action) {
		this.action = action;
		this.count = 0;
		checkState();

		boolean done = action.activate(player, count);
		if (done) {
			count++;			
		}
		if (done || action.isFarmAction()) {
			player.spendWorker();
			player.notifyObservers(ChangeType.ACTION_DO);
			perfListener.stateChanges();
			return true;
		}
		return false;
	}
	
	public boolean canDoFarmAction(DirPoint pos) {
		return action.canPerform(player, pos, count) || action.canUnperform(player, pos, count);
	}

	public boolean doFarmAction(DirPoint pos) {
		checkState();
		if (action.canPerform(player, pos, count)) {
			action.activate(player, pos, count);
			count++;
			return true;
		} else if (action.canUnperform(player, pos, count)){
			action.undo(player, pos, count);
			count--;						
			return true;
		}
		return false;
	}

//	public boolean canDoMore() {
//		return action.canPerformMore(player, count);
//	}

//	public boolean doActionMore() {
//		checkState();
//		if (canDoMore()) {
//			if (action.doo(player)) {
//				count++;
//				player.notifyObservers(ChangeType.ACTION_MORE);
//				return true;
//			}
//		}
//		return false;
//	}

//	public boolean canDoLess() {
//		return count > 0;
//	}

//	public boolean doActionLess() {
//		checkState();
//		if (canDoLess()) {
//			if (action.undo(player)) {
//				count--;
//				player.notifyObservers(ChangeType.ACTION_LESS);
//				return true;
//			}
//		}
//		return false;
//	}

	public boolean revertAction() {
		checkState();
		boolean done = true;
		while (count > 0) {
			done = action.undo(player, count);
			if (!done) {
				break;
			}
			count--;
		}
		if (done) {
			action = null;
			player.returnWorker();
			player.setActiveType(null);
		}
		player.notifyObservers(ChangeType.ACTION_UNDO);
		return done;
	}

	public boolean canFinish() {
		return count > 0 && player.validate();
	}
	
	public boolean finishAction() {
		checkState();
		if (canFinish()) {
			player.setActiveType(null);
			action.setUsed();
			System.out.println("Action done: " + Namer.getName(action));
			action = null;
			player.notifyObservers(ChangeType.ACTION_DONE);
			return true;
		} else {
			return false;
		}
	}

	public void setActionPerfListener(ActionPerfListener perfListener) {
		this.perfListener = perfListener;
	}
	
	public interface ActionPerfListener {

		void stateChanges();// TODO rename

	}

}