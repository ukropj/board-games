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
	private boolean isExtraAction = false;

	private void checkState() throws IllegalStateException {
		Main.asrtNotNull(player, "Cannot perform action without player");
		Main.asrtNotNull(action, "Cannot perform action without action");
	}
	
	public void reset() {
		super.reset();
		player = null;
		action = subaction = null;
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
		return action != null;
	}

	public boolean hasAction(ActionType type) {
		return action != null && action.getType() == type;
	}

	public boolean hasExtraAction() {
		return action != null && isExtraAction;
	}
	
	public boolean hasSubAction() {
		return subaction != null;
	}

	public Action getAction() {
		return action;
	}
	
	public Action getSubAction() {
		return subaction;
	}

	public boolean canCancel() {
		return !isExtraAction;
	}

	public boolean isFinished() {
		return action == null;
	}

	public boolean startAction(Action action) {
		return startAction(action, false);
	}

	public boolean startAction(Action action, boolean extraAction) {
		this.action = action;
		this.subaction = null;
		this.isExtraAction = extraAction;
		checkState();

		boolean canDo = action.canDo(player);
		if (canDo) {
			UndoableFarmEdit edit = action.doo(player);

			if (action.isCancelled()) {
				this.action = null;
				return false;
			}

			postEdit(new StartAction(player, action, extraAction));
			action.setUsed(player.getColor());
			if (!extraAction) {
				player.spendWorker();
			}

			if (this.subaction == null) {
				Action subAction = action.getSubAction(player, false);
				if (subAction != null) {
					startSubaction(subAction);
				}
			}

			if (edit != null) {
				postEdit(edit);
				if (((extraAction && action.getType() != ActionType.BREEDING) || !player.hasLooseAnimals())
						&& player.validate()
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
		if (canDo) {
			UndoableFarmEdit edit = subaction.doo(player);

			if (subaction.isCancelled()) {
				return false;
			}

			postEdit(new StartSubAction(player, subaction));
			subaction.setUsed(player.getColor());

			if (edit != null) {
				postEdit(edit);
				if (!player.hasLooseAnimals()
						&& !subaction.canDoOnFarm(player)) {
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
					Action subAction = action.getSubAction(player, true);
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
			postEdit(new EndAction(player, action, isExtraAction));
			player.getFarm().setActiveType(null);
			action = null;
			isExtraAction = false;
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
			postEdit(new EndSubAction(player, subaction));
			player.getFarm().setActiveSubType(null);
			subaction.removeChangeListeners();
			subaction = null;
			return true;
		}
		return false;
	}

	private class StartAction extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player p;
		private final Action a;
		private final boolean extraAction;

		public StartAction(Player player, Action action, boolean extraAction) {
			super(!extraAction);
			this.p = player;
			this.a = action;
			this.extraAction = extraAction;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			if (!extraAction) {
				p.returnWorker();
			}
			action = null;
			isExtraAction = false;
			a.setUsed(null);
		}

		public void redo() throws CannotRedoException {
			super.redo();
			a.setUsed(p.getColor());
			if (!extraAction) {
				p.spendWorker();
			}
			action = a;
			isExtraAction = extraAction;
		}
		
		public String getPresentationName() {
			return a.getType().shortDesc;
		}

	}

	private class StartSubAction extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player p;
		private final Action sa;

		public StartSubAction(Player player, Action subaction) {
			this.p = player;
			this.sa = subaction;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			p.getFarm().setActiveSubType(null);
			subaction = null;
			sa.setUsed(null);
		}

		public void redo() throws CannotRedoException {
			super.redo();
			sa.setUsed(p.getColor());
			subaction = sa;
		}
		
		public String getPresentationName() {
			return sa.getType().shortDesc;
		}

	}

	private class EndAction extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player p;
		private final Action a;
		private final boolean extraAction;
		private final Purchasable activeType;

		public EndAction(Player player, Action action, boolean extraAction) {
			super(!extraAction);
			this.p = player;
			this.a = action;
			this.extraAction = extraAction;
			this.activeType = player.farm.getActiveType();
		}

		public void undo() throws CannotUndoException {
			super.undo();
			action = a;
			isExtraAction = extraAction;
			p.getFarm().setActiveType(activeType);
		}

		public void redo() throws CannotRedoException {
			super.redo();
			p.getFarm().setActiveType(null);
			action = null;
			isExtraAction = false;
		}

	}

	private class EndSubAction extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player p;
		private final Action sa;
		private final Purchasable activeSubType;

		public EndSubAction(Player player, Action subaction) {
			this.p = player;
			this.sa = subaction;
			this.activeSubType = player.farm.getActiveSubType();
		}

		public void undo() throws CannotUndoException {
			super.undo();
			subaction = sa;
			p.getFarm().setActiveSubType(activeSubType);
		}

		public void redo() throws CannotRedoException {
			super.redo();
			p.getFarm().setActiveSubType(null);
			subaction.removeChangeListeners();
			subaction = null;
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