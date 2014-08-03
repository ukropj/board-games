package com.dill.agricola.actions;

import java.util.Stack;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.Main;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.ChangeType;
import com.dill.agricola.model.types.Purchasable;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.TurnUndoableEditSupport;
import com.dill.agricola.undo.UndoableFarmEdit;

public class ActionPerformer extends TurnUndoableEditSupport {

	private Player player = null;
	private Stack<Action> actions = new Stack<Action>();
	private boolean isExtraAction = false;

	private void checkState(int level) throws IllegalStateException {
		Main.asrtPositive(level, "Action level cannot be negative");
		Main.asrtTrue(player != null, "Cannot perform action without player");
		Main.asrtTrue(actions.size() > level, "Cannot perform action without action " + level);
	}

	public void reset() {
		super.reset();
		player = null;
		actions.clear();
		isExtraAction = false;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public boolean hasPlayer() {
		return player != null;
	}

	public Player getPlayer() {
		return player;
	}

	public boolean hasAction() {
		return hasAction(0);
	}

	public boolean hasAction(int level) {
		return level >= 0 && actions.size() > level;
	}

	public boolean hasAction(ActionType type) {
		return !actions.isEmpty() && actions.firstElement().getType() == type;
	}

	public boolean hasExtraAction() {
		return !actions.isEmpty() && isExtraAction;
	}

	public Action getAction(int level) {
		return actions.get(level);
	}

	public Action getTopAction() {
		return actions.peek();
	}

	public boolean canCancel() {
		return !isExtraAction;
	}

	public boolean isFinished() {
		return actions.isEmpty();
	}

	public boolean startAction(Action action) {
		return startAction(action, false);
	}

	public boolean startAction(Action action, boolean extraAction) {
		actions.clear();
		return startAction(action, 0, extraAction);
	}

	private boolean startAction(Action action, int level, boolean extraAction) {
		actions.push(action);
		boolean isSubaction = level != 0;
		if (!isSubaction) {
			// only base action can be extra (for now..)
			isExtraAction = extraAction;
		} else {
			action.addChangeListener(new SubActionStateChangeListener(level));
			action.init();
			action.useAsSubaction(level);
		}
		checkState(level);

		boolean canDo = action.canDo(player);
		if (canDo) {
			UndoableFarmEdit edit = action.doo(player);

			if (action.isCancelled()) {
				actions.pop();
				if (isSubaction) {
					action.removeChangeListeners();
				}
				return false;
			}

			postEdit(new StartAction(player, action, level, extraAction));
			action.setUsed(player.getColor());
			if (!isSubaction && !extraAction) {
				// spend worker only for base, non-extra actions
				player.spendWorker();
			}

			if (!hasAction(level + 1)) {
				Action subAction = action.getSubAction(player, false);
				if (subAction != null) {
					startAction(subAction, level + 1, false);
				}
			}

			if (edit != null) {
				postEdit(edit);
				if (canQuickFinish(level, isExtraAction)) {
					return finishActions();
				}
			}

			player.notifyObservers(ChangeType.ACTION_DO);
			return true;
		} else {
			actions.pop(); // action cannot be started
			return false;
		}
	}

	private boolean canQuickFinish(int level, boolean extraAction) {
		Action action = getAction(level);
		return (((extraAction && action.getType() != ActionType.BREEDING) || !player.hasLooseAnimals())
				&& player.validate()
				&& !action.canDoOnFarm(player)
				&& (!hasAction(level + 1) || canQuickFinish(level + 1, false)));
	}

	public boolean canDoFarmAction(DirPoint pos, Purchasable thing, int level) {
		return hasAction(level) && player.getFarm().getActiveType(level) == thing && getAction(level).canDoOnFarm(player, pos);
	}

	public boolean canUndoFarmAction(DirPoint pos, Purchasable thing, int level) {
		return hasAction(level) && player.getFarm().getActiveType(level) == thing && getAction(level).canUndoOnFarm(player, pos);
	}

	public boolean doOrUndoFarmAction(DirPoint pos, Purchasable thing, int level) {
		boolean done = doFarmAction(pos, thing, level);
		if (!done) {
			done = undoFarmAction(pos, thing, level);
		}
		return done;
	}

	public boolean doFarmAction(DirPoint pos, Purchasable thing, int level) {
		checkState(level);
		if (canDoFarmAction(pos, thing, level)) {
			Action action = getAction(level);
			UndoableFarmEdit edit = action.doOnFarm(player, pos);
			if (edit != null) {
				postEdit(edit);
				if (!hasAction(level + 1)) {
					Action subAction = action.getSubAction(player, true);
					if (subAction != null) {
						startAction(subAction, level + 1, false);
					}
				}
				return true;
			}
		}
		if (hasAction(level + 1)) {
			return doFarmAction(pos, thing, level + 1);
		}
		return false;
	}

	public boolean undoFarmAction(DirPoint pos, Purchasable thing, int level) {
		checkState(level);
		if (canUndoFarmAction(pos, thing, level)) {
			Action action = getAction(level);
			if (undoSpecificAction(player, pos, thing, action.mustUndoSubactions() && hasAction(level + 1))) {
				return true;
			}
		}
		if (hasAction(level + 1)) {
			return undoFarmAction(pos, thing, level + 1);
		}
		return false;
	}

	public boolean canFinish() {
		return player != null && player.validate() && canFinish(0);
	}

	private boolean canFinish(int level) {
		return hasAction(level) && getAction(level).isUsedEnough()
				&& (!hasAction(level + 1) || canFinish(level + 1));
	}

	public boolean finishActions() {
		if (canFinish()) {
			while (!actions.isEmpty()) {
				Action action = actions.pop();
				int level = actions.size();
				postEdit(new EndAction(player, action, level, isExtraAction));
				player.getFarm().setActiveType(null, level);
				if (level != 0) {
					action.removeChangeListeners();					
				}
			}
			isExtraAction = false;
			player.notifyObservers(ChangeType.ACTION_DONE);
			return true;
		}
		return false;
	}

	private class StartAction extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player p;
		private final Action a;
		private final int l;
		private final boolean extraAction;

		public StartAction(Player player, Action action, int level, boolean extraAction) {
			super(!extraAction);
			this.p = player;
			this.a = action;
			this.l = level;
			this.extraAction = extraAction;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			if (l == 0) {
				if (!extraAction) {
					p.returnWorker();
				}
				isExtraAction = false;
			} else {
				a.removeChangeListeners();
			}
			p.getFarm().setActiveType(null, l);
			actions.pop();
			Main.asrtTrue(actions.size() == l, "Broken action stack");
			a.setUsed(null);
		}

		public void redo() throws CannotRedoException {
			super.redo();
			if (l == 0) {
				if (!extraAction) {
					p.spendWorker();
				}
				isExtraAction = extraAction;
			} else {
				a.addChangeListener(new SubActionStateChangeListener(l));
			}
			a.setUsed(p.getColor());
			Main.asrtTrue(actions.size() == l, "Broken action stack");
			actions.push(a);
		}

		public String getPresentationName() {
			return a.getType().shortDesc;
		}

	}

	private class EndAction extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player p;
		private final Action a;
		private final int l;
		private final boolean extraAction;
		private final Purchasable activeType;

		public EndAction(Player player, Action action, int level, boolean extraAction) {
			super(!extraAction);
			this.p = player;
			this.a = action;
			this.l = level;
			this.extraAction = extraAction;
			this.activeType = player.farm.getActiveType(level);
		}

		public void undo() throws CannotUndoException {
			super.undo();
			Main.asrtTrue(actions.size() == l, "Broken action stack");
			actions.push(a);
			if (l == 0) {
				isExtraAction = extraAction;
			} else {
				a.addChangeListener(new SubActionStateChangeListener(l));
			}
			p.getFarm().setActiveType(activeType, l);
			// TODO what about change listeners?
		}

		public void redo() throws CannotRedoException {
			super.redo();
			p.getFarm().setActiveType(null, l);
			actions.pop();
			Main.asrtTrue(actions.size() == l, "Broken action stack");
			if (l == 0) {
				isExtraAction = false;
			} else {
				// only for subactions
				a.removeChangeListeners();
			}
		}

	}

	private class SubActionStateChangeListener implements ActionStateChangeListener {
		private final int level;

		public SubActionStateChangeListener(int level) {
			Main.asrtPositive(level - 1, "Subaction must have level > 0");
			this.level = level;
		}

		public void stateChanges(Action subAction) {
			if (hasAction(level - 1)) {
				// notify parent action
				getAction(level - 1).setChanged();
			}
		}
	}

}