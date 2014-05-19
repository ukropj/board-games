package com.dill.agricola.undo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.common.DirPoint;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.model.types.Purchasable;
import com.dill.agricola.support.Logger;
import com.dill.agricola.support.Msg;
import com.dill.agricola.support.Namer;

public abstract class SimpleEdit extends AbstractUndoableEdit implements UndoableFarmEdit {
	private static final long serialVersionUID = 1L;

	private boolean significant;
	private final PlayerColor player;
	private final DirPoint pos;
	private final Purchasable thing;


	public SimpleEdit() {
		this(false);
	}

	public SimpleEdit(boolean significant) {
		this(significant, null, null, null);
	}

	public SimpleEdit(PlayerColor player, DirPoint pos, Purchasable thing) {
		this(true, player, pos, thing);
	}

	public SimpleEdit(boolean significant, PlayerColor player, DirPoint pos, Purchasable thing) {
		this.significant = significant;
		this.pos = pos;
		this.player = player;
		this.thing = thing;
	}

	public boolean isSignificant() {
		return significant;
	}

	public boolean isFarmEdit() {
		return pos != null;
	}

	public boolean isAnimalEdit() {
		return false;
	}

	public boolean matchesFarmAction(PlayerColor player, DirPoint pos, Purchasable thing) {
		return player.equals(this.player) && pos.equals(this.pos) && thing.equals(this.thing);
	}

	public void undo() throws CannotUndoException {
		Logger.logUndo((isSignificant() ? "" : "-") + "Atomic " + getUndoPresentationName());
		super.undo();
	}

	public void redo() throws CannotRedoException {
		Logger.logUndo((isSignificant() ? "" : "-") + "Atomic " + getRedoPresentationName());
		super.redo();
	}

	public String getPresentationName() {
		return Namer.getName(this);
	}

	public String getUndoPresentationName() {
		String name = getPresentationName();
		return Msg.get("undo") + (!"".equals(name) ? " " + name : "");
	}

	public String getRedoPresentationName() {
		String name = getPresentationName();
		return Msg.get("redo") + (!"".equals(name) ? " " + name : "");
	}

}
