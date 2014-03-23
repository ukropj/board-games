package com.dill.agricola.undo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.common.DirPoint;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.model.types.Purchasable;
import com.dill.agricola.support.Namer;

public class SimpleEdit extends AbstractUndoableEdit implements UndoableFarmEdit {
	private static final long serialVersionUID = 1L;

	private boolean significant;
	private final DirPoint pos;
	private final Purchasable thing;

	public SimpleEdit() {
		this(false);
	}

	public SimpleEdit(boolean significant) {
		this(significant, null, null);
	}

	public SimpleEdit(DirPoint pos, Purchasable thing) {
		this(true, pos, thing);
	}

	public SimpleEdit(boolean significant, DirPoint pos, Purchasable thing) {
		this.significant = significant;
		this.pos = pos;
		this.thing = thing;
	}

	public boolean isSignificant() {
		return significant;
	}

	public boolean isFarmEdit() {
		return pos != null;
	}

	public boolean matchesFarmAction(PlayerColor player, DirPoint pos, Purchasable thing) {
		return pos.equals(this.pos) && thing.equals(this.thing);
	}

	public void undo() throws CannotUndoException {
		System.out.println("#" + (isSignificant() ? " " : "-") + "Atomic " + getUndoPresentationName());
		super.undo();
	}

	public void redo() throws CannotRedoException {
		System.out.println("#" + (isSignificant() ? " " : "-") + "Atomic " + getRedoPresentationName());
		super.redo();
	}

	public String getPresentationName() {
		return Namer.getName(this);
	}

}
