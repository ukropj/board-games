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
	private Action subAction = null;

	private int count = 0;
	private int subCount = 0;

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

//	private boolean hasSubAction() {
//		return subAction != null;
//	}

	public Action getAction() {
		return action;
	}

	public boolean startAction(Action action) {
		this.action = action;
		this.subAction = null;
		this.count = this.subCount = 0;
		checkState();

		boolean canDo = action.canDo(player);
		UndoableFarmEdit edit = null;
		if (canDo) {
			edit = action.doo(player);

			if (action.isCancelled()) {
				return false;
			}

			beginUpdate(player.getColor(), action.getType()); // start "action edit"
			postEdit(new StartAction(player, action));
			player.spendWorker();
			action.setUsed(player.getColor());

			if (edit != null) {
				count++;
				postEdit(edit);

				if (!action.canDoOnFarm(player, count) && !edit.isAnimalEdit() && !player.hasLooseAnimals()) {
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

	private boolean startSubAction(Action subAction) {
		this.subAction = subAction;
		this.subCount = 0;

		boolean canDo = subAction.canDo(player);
		UndoableFarmEdit edit = null;
		if (canDo) {
			edit = subAction.doo(player);

			if (subAction.isCancelled()) {
				return false;
			}

//			beginUpdate(player.getColor(), subAction.getType()); // start "action edit"
			postEdit(new StartSubAction(player, subAction));

			if (edit != null) {
				subCount++;
				postEdit(edit);

				if (!subAction.canDoOnFarm(player, subCount) && !edit.isAnimalEdit() && !player.hasLooseAnimals()) {
					return finishSubAction();
				}
			}

			player.notifyObservers(ChangeType.ACTION_DO);
			return true;
		} else {
			this.subAction = null; // sub-action cannot be started
			return false;
		}
	}

	public boolean canDoFarmAction(DirPoint pos, Purchasable thing, boolean isSub) {
		return !isSub ? player.getFarm().getActiveType() == thing && action.canDoOnFarm(player, pos, count)
				: subAction != null && player.getFarm().getActiveSubType() == thing && subAction.canDoOnFarm(player, pos, subCount);
	}

	public boolean canUndoFarmAction(DirPoint pos, Purchasable thing, boolean isSub) {
		return !isSub ? player.getFarm().getActiveType() == thing && action.canUndoOnFarm(player, pos, count)
				: subAction != null && player.getFarm().getActiveSubType() == thing && subAction.canUndoOnFarm(player, pos, subCount);
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
			UndoableFarmEdit edit = action.doOnFarm(player, pos, count);
			if (edit != null) {
				postEdit(edit);
				count++;
				if (this.subAction == null) {
					Action subAction = action.getSubAction();
					if (subAction != null) {
						startSubAction(subAction);
					}					
				}
				return true;
			}
		}
		if (canDoFarmAction(pos, thing, true)) {
			UndoableFarmEdit edit = subAction.doOnFarm(player, pos, subCount);
			if (edit != null) {
				postEdit(edit);
				subCount++;
				return true;
			}
		}
		return false;
	}

	public boolean undoFarmAction(DirPoint pos, Purchasable thing) {
		checkState();
		if (canUndoFarmAction(pos, thing, false)) {
			if (undoSpecificAction(player, pos, thing, subAction != null)) {
				count--;
				return true;
			}
		}
		if (canUndoFarmAction(pos, thing, true)) {
			if (undoSpecificAction(player, pos, thing, false)) {
				subCount--;
				return true;
			}
		}
		return false;
	}

	public boolean canFinish() {
		return action != null && player != null
				&& count >= action.getMinimalCount()
				&& canSubFinish()
				&& player.validate();
	}

	public boolean canSubFinish() {
		return subAction == null || subCount >= subAction.getMinimalCount();
	}

	public boolean finishAction() {
		if (canFinish()) {
			postEdit(new EndAction());
			player.getFarm().setActiveType(null);
			action = null;
			if (subAction != null) {
				finishSubAction();
			}
			player.notifyObservers(ChangeType.ACTION_DONE);
			return true;
		}
		return false;
	}

	public boolean finishSubAction() {
		if (canSubFinish()) {
//			postEdit(new EndAction());
			player.getFarm().setActiveSubType(null);
			subAction = null;
			player.notifyObservers(ChangeType.ACTION_DONE);
			return true;
		}
		return false;
	}

	/*private boolean canFinishSub() {
		return subAction != null && player != null
				&& subCount >= subAction.getMinimalCount() && player.validate();
	}

	private boolean finishSubAction() {
		if (canFinishSub()) {
			postEdit(new EndAction());
			player.setActiveType(null);
			subAction = null;
			player.notifyObservers(ChangeType.ACTION_DONE);
			return true;
		}
		return false;
	}*/

	public boolean isFinished() {
		return action == null;
	}

	private class StartAction extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player p;
		private final Action a;

		public StartAction(Player player, Action action) {
			super(true);
			this.p = player;
			this.a = action;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			p.getFarm().setActiveType(null);
			action = null;
			p.returnWorker();
			a.setUsed(null);
		}

		public void redo() throws CannotRedoException {
			super.redo();
			a.setUsed(p.getColor());
			p.spendWorker();
		}

	}

	private class StartSubAction extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player p;
		@SuppressWarnings("unused")
		private final Action a;

		public StartSubAction(Player player, Action action) {
			super(true);
			this.p = player;
			this.a = action;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			p.getFarm().setActiveSubType(null);
			subAction = null;
		}

		public void redo() throws CannotRedoException {
			super.redo();
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

}