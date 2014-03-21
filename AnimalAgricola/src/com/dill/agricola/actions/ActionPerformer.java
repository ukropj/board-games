package com.dill.agricola.actions;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import com.dill.agricola.Main;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.ChangeType;
import com.dill.agricola.model.types.Purchasable;
import com.dill.agricola.support.Namer;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.TurnUndoableEditSupport;

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

		boolean canDo = action.canDo(player, count);
		UndoableEdit edit = null;
		if (canDo) {
			beginUpdate(player.getColor(), action.getType());
			postEdit(new StartAction(player, action));
			
			action.setUsed(player.getColor());
			edit = action.doo(player, count);
			if (edit != null) {
				count++;
				postEdit(edit);				
			}
			player.spendWorker();
			
			if (action.isQuickAction()) {
				return finishAction();
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
		return action.canDo(player, pos, count) || action.canUndo(player, pos, count);
	}

	public boolean doFarmAction(DirPoint pos, Purchasable thing) {
		checkState();
		if (action.canDo(player, pos, count)) {
			UndoableEdit edit = action.doo(player, pos, count);
			if (edit != null) {
				postEdit(edit);
				count++;
				setChanged();
				return true;
			}
		}
		if (undoFarmAction(player, pos, thing)) {
//		if (action.canUndo(player, pos, count) && action.undo(player, pos, count)) {
			count--;
			setChanged();
			return true;
		}
		return false;
	}

	public boolean canRevert() {
		return action!= null && player != null;
	}
	
	public boolean revertAction() {
		if (canRevert()) {
			boolean done = true;
			while (count > 0) {
				done = action.undo(player, count);
				if (!done) {
					break;
				}
				count--;
			}
			setChanged();
			if (done) {
				action = null;
				player.returnWorker();
				player.setActiveType(null);
			}
			player.notifyObservers(ChangeType.UNDO);
			return done;			
		}
		return false;
	}

	public boolean canFinish() {
		return action!= null && player != null 
				&& count >= action.getMinimalCount() && player.validate();
	}

	public boolean finishAction() {
		if (canFinish()) {
			postEdit(new EndAction(player, action));
			player.setActiveType(null);
			System.out.println("Action done: " + Namer.getName(action));
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
	
	@SuppressWarnings("serial")
	private class StartAction extends SimpleEdit {

		private final Player p;
		private final Action a;
		
		public StartAction(Player player, Action action) {
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
	
	@SuppressWarnings("serial")
	private class EndAction extends SimpleEdit {

//		private final Player p;
//		private final Action a;
		
		public EndAction(Player player, Action action) {
//			this.p = player;
//			this.a = action;
		}
		
		public void undo() throws CannotUndoException {
			super.undo();
		}
		
		public void redo() throws CannotRedoException {
			super.redo();
		}
		
	}

}