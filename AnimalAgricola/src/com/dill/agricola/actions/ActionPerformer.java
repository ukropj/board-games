package com.dill.agricola.actions;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import com.dill.agricola.common.DirPoint;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.ChangeType;
import com.dill.agricola.support.Namer;
import com.dill.agricola.undo.LoggingUndoableEdit;
import com.dill.agricola.undo.TurnUndoableEditSupport;

public class ActionPerformer extends TurnUndoableEditSupport {

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
			postEdit(new StartAction(player));
			edit = action.doo(player, count);
			if (edit != null) {
				count++;
				postEdit(edit);				
			}
			player.spendWorker();
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

	public boolean doFarmAction(DirPoint pos) {
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
		if (action.canUndo(player, pos, count) && action.undo(player, pos, count)) {
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
			player.notifyObservers(ChangeType.ACTION_UNDO);
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
			action.setUsed(true);
			System.out.println("Action done: " + Namer.getName(action));
			action = null;
			player.notifyObservers(ChangeType.ACTION_DONE);
			return true;
		}
		return false;
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

		void stateChanges(Action action);// TODO rename

	}
	
	@SuppressWarnings("serial")
	private class StartAction extends LoggingUndoableEdit {

		private final Player player;
		
		public StartAction(Player player) {
			this.player = player;
		}
		
		public void undo() throws CannotUndoException {
			super.undo();
			player.returnWorker();
		}
		
		public void redo() throws CannotRedoException {
			super.redo();
			player.spendWorker();
		}
		
		
		public String getPresentationName() {
			return Namer.getName(this);
		}
		
	}
	
	@SuppressWarnings("serial")
	private class EndAction extends LoggingUndoableEdit {

//		private final Player player;
		private final Action action;
		
		public EndAction(Player player, Action action) {
//			this.player = player;
			this.action = action;
		}
		
		public void undo() throws CannotUndoException {
			super.undo();
			action.setUsed(false);
		}
		
		public void redo() throws CannotRedoException {
			super.redo();
//			player.setActiveType(null);
			action.setUsed(true);
		}
		
		public String getPresentationName() {
			return Namer.getName(this);
		}
		
	}

}