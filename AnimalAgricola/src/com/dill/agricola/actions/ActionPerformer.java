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

	private int count = 0;
	private ActionPerfListener perfListener;

	private void checkState() throws IllegalStateException {
		Main.asrtNotNull(player, "Cannot perform action without player");
		Main.asrtNotNull(action, "Cannot perform action without action");
	}

	public void setPlayer(Player player) {
		this.player = player;
		System.out.println("## AP: " + player);
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
		this.action = action;
		this.count = 0;
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

				if (!action.canDoOnFarm(player, count) && !edit.isAnimalEdit()) {
					return finishAction();
				}
			}

			player.notifyObservers(ChangeType.ACTION_DO);
			setChanged();
			return true;
		} else {
			this.action = null; // action cannot be started
			return false;
		}
	}

	public boolean canDoFarmAction(DirPoint pos) {
		return action.canDoOnFarm(player, pos, count) || action.canUndoOnFarm(player, pos, count);
	}

	public boolean doFarmAction(DirPoint pos, Purchasable thing) {
		checkState();
		if (action.canDoOnFarm(player, pos, count)) {
			UndoableFarmEdit edit = action.doOnFarm(player, pos, count);
			if (edit != null) {
				postEdit(edit);
				count++;
				setChanged();
//				if (!action.canDoOnFarm(player, count) && !edit.isAnimalEdit()) {
//					return finishAction();
//				}
				return true;
			}
		}
		if (undoFarmAction(player, pos, thing)) {
			count--;
			setChanged();
			return true;
		}
		return false;
	}

	public boolean canFinish() {
		return action != null && player != null
				&& count >= action.getMinimalCount() && player.validate();
	}

	public boolean finishAction() {
		if (canFinish()) {
			postEdit(new EndAction());
			player.setActiveType(null);
			action = null;
			player.notifyObservers(ChangeType.ACTION_DONE);
			return true;
		}
		return false;
	}

	public boolean isFinished() {
		return action == null;
	}

	private void setChanged() {
		if (perfListener != null) {
			perfListener.stateChanges(action);
		}
	}

	public void setActionPerfListener(ActionPerfListener perfListener) {
		this.perfListener = perfListener;
	}

	public interface ActionPerfListener {

		void stateChanges(Action action);

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
			p.setActiveType(null);
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