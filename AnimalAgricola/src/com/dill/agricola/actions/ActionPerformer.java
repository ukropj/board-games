package com.dill.agricola.actions;

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
	private Action action = null;
	private Action subaction = null;

	private void checkState() throws IllegalStateException {
		Main.asrtNotNull(player, "Cannot perform action without player");
		Main.asrtNotNull(action, "Cannot perform action without action");
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

	public boolean hasAction(ActionType type) {
		return action != null && action.getType() == type;
	}

	public Action getAction() {
		return action;
	}

	public boolean startAction(Action action) {
		return startAction(action, false);
	}

	public boolean startAction(Action action, boolean extraAction) {
		this.action = action;
		this.subaction = null;
		checkState();

		boolean canDo = action.canDo(player);
		UndoableFarmEdit edit = null;
		if (canDo) {
			edit = action.doo(player);

			if (action.isCancelled()) {
				this.action = null;
				return false;
			}

			if (!extraAction) {
				beginUpdate(player.getColor(), action.getType()); // start "action edit"
				player.spendWorker();
			}
			postEdit(new StartAction(player, action, extraAction));
			action.setUsed(player.getColor());

			if (this.subaction == null) {
				Action subAction = action.getSubAction(false);
				if (subAction != null) {
					startSubaction(subAction);
				}
			}

			if (edit != null) {
				postEdit(edit);
				if (!edit.isAnimalEdit() && !player.hasLooseAnimals()
						&& !action.canDoOnFarm(player)
						&& (subaction == null || !subaction.canDoOnFarm(player))) {
					return finishAction();
				}
			}

			player.notifyObservers(ChangeType.ACTION_DO);
			return true;
		} else {
			this.action = null; // action cannot be started
			return false;
		}
	}

	private boolean startSubaction(Action subaction) {
		this.subaction = subaction;
		subaction.addChangeListener(new SubActionStateChangeListener());
		subaction.init();
		subaction.useAsSubaction();

		boolean canDo = subaction.canDo(player);
		UndoableFarmEdit edit = null;
		if (canDo) {
			edit = subaction.doo(player);

			if (subaction.isCancelled()) {
				return false;
			}

			postEdit(new StartSubAction(player, subaction));
			subaction.setUsed(player.getColor());

			if (edit != null) {
				postEdit(edit);
				if (!subaction.canDoOnFarm(player) && !edit.isAnimalEdit() && !player.hasLooseAnimals()) {
					return finishSubaction();
				}
			}

			player.notifyObservers(ChangeType.ACTION_DO);
			return true;
		} else {
			this.subaction = null; // sub-action cannot be started
			return false;
		}
	}

	public boolean canDoFarmAction(DirPoint pos, Purchasable thing, boolean isSub) {
		return !isSub ? player.getFarm().getActiveType() == thing && action.canDoOnFarm(player, pos)
				: subaction != null && player.getFarm().getActiveSubType() == thing && subaction.canDoOnFarm(player, pos);
	}

	public boolean canUndoFarmAction(DirPoint pos, Purchasable thing, boolean isSub) {
		return !isSub ? player.getFarm().getActiveType() == thing && action.canUndoOnFarm(player, pos)
				: subaction != null && player.getFarm().getActiveSubType() == thing && subaction.canUndoOnFarm(player, pos);
	}

	public boolean doOrUndoFarmAction(DirPoint pos, Purchasable thing) {
		boolean done = doFarmAction(pos, thing);
		if (!done) {
			done = undoFarmAction(pos, thing);
		}
		return done;
	}

	public boolean doFarmAction(DirPoint pos, Purchasable thing) {
		checkState();
		if (canDoFarmAction(pos, thing, false)) {
			UndoableFarmEdit edit = action.doOnFarm(player, pos);
			if (edit != null) {
				postEdit(edit);
				if (this.subaction == null) {
					Action subAction = action.getSubAction(true);
					if (subAction != null) {
						startSubaction(subAction);
					}
				}
				return true;
			}
		}
		if (canDoFarmAction(pos, thing, true)) {
			UndoableFarmEdit edit = subaction.doOnFarm(player, pos);
			if (edit != null) {
				postEdit(edit);
				return true;
			}
		}
		return false;
	}

	public boolean undoFarmAction(DirPoint pos, Purchasable thing) {
		checkState();
		if (canUndoFarmAction(pos, thing, false)) {
			if (undoSpecificAction(player, pos, thing, subaction != null)) {
				return true;
			}
		}
		if (canUndoFarmAction(pos, thing, true)) {
			if (undoSpecificAction(player, pos, thing, false)) {
				return true;
			}
		}
		return false;
	}

	public boolean canFinish() {
		return action != null && player != null
				&& action.isUsedEnough()
				&& canSubFinish()
				&& player.validate();
	}

	private boolean canSubFinish() {
		return subaction == null || subaction.isUsedEnough();
	}

	public boolean finishAction() {
		if (canFinish()) {
			postEdit(new EndAction());
			player.getFarm().setActiveType(null);
			action = null;
			if (subaction != null) {
				finishSubaction();
			}
			player.notifyObservers(ChangeType.ACTION_DONE);
			return true;
		}
		return false;
	}

	private boolean finishSubaction() {
		if (canSubFinish()) {
			player.getFarm().setActiveSubType(null);
			subaction.removeChangeListeners();
			subaction = null;
			player.notifyObservers(ChangeType.ACTION_DONE);
			return true;
		}
		return false;
	}

	public boolean isFinished() {
		return action == null;
	}

	private class StartAction extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player p;
		private final Action a;
		private final boolean extraAction;

		public StartAction(Player player, Action action, boolean extraAction) {
			super(true);
			this.p = player;
			this.a = action;
			this.extraAction = extraAction;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			p.getFarm().setActiveType(null);
			action = null;
			if (!extraAction) {
				p.returnWorker();
			}
			a.setUsed(null);
		}

		public void redo() throws CannotRedoException {
			super.redo();
			a.setUsed(p.getColor());
			if (!extraAction) {
				p.spendWorker();
			}
		}

	}

	private class StartSubAction extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player p;
		private final Action a;

		public StartSubAction(Player player, Action action) {
			super(true);
			this.p = player;
			this.a = action;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			p.getFarm().setActiveSubType(null);
			subaction = null;
			a.setUsed(null);
		}

		public void redo() throws CannotRedoException {
			super.redo();
			a.setUsed(p.getColor());
		}

	}

	private class EndAction extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		public void undo() throws CannotUndoException {
			super.undo();
		}

		public void redo() throws CannotRedoException {
			super.redo();
		}

	}

	private class SubActionStateChangeListener implements ActionStateChangeListener {
		public void stateChanges(Action subAction) {
			if (action != null) {
				action.setChanged();
			}
		}
	}

}