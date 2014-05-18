package com.dill.agricola.actions.simple;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.common.Animals;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.UndoableFarmEdit;

public class Millpond extends AnimalRefillAction {

	public static final Materials REFILL = new Materials(Material.REED, 1);

	public static final Animal OTHER_ANIMAL = Animal.SHEEP;

	private final Materials materials = new Materials();

	public Millpond() {
		super(ActionType.MILLPOND, null, OTHER_ANIMAL);
	}

	public void reset() {
		super.reset();
		materials.clear();
		setChanged();
	}

	public UndoableFarmEdit init() {
		UndoableFarmEdit initEdit = isUsed() ? new ActionInit(user, useCount) : null;
		user = null;
		useCount = 0;
		UndoableFarmEdit edit;
		if (materials.isEmpty()) {
			materials.add(REFILL);
			edit = new RefillMaterials(REFILL);
		} else {
			animals.add(other, 1);
			edit = new RefillAnimals(new Animals(other));
		}
		setChanged();
		return joinEdits(initEdit, edit);
	}
	
	public boolean canDo(Player player) {
		return !materials.isEmpty() || !animals.isEmpty();
	}

	public UndoableFarmEdit doo(Player player) {
		if (canDo(player)) {
			UndoableFarmEdit edit = joinEdits(true,
					new TakeAnimals(player, new Animals(animals)),
					new TakeMaterials(player, new Materials(materials)));
			player.purchaseAnimals(animals);
			player.addMaterial(materials);
			animals.clear();
			materials.clear();
			setChanged();
			return edit;
		}
		return null;
	}

	public Materials getAccumulatedMaterials() {
		return materials;
	}

	public class TakeMaterials extends SimpleEdit {
		private static final long serialVersionUID = 1L;
		
		private final Player player;
		private final Materials takenMaterials;

		public TakeMaterials(Player player, Materials materials) {
			super(true);
			this.player = player;
			this.takenMaterials = materials;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			player.removeMaterial(takenMaterials);
			materials.add(takenMaterials);
		}

		public void redo() throws CannotRedoException {
			super.redo();
			materials.substract(takenMaterials);
			setChanged();
			player.addMaterial(takenMaterials);
		}

	}
	
	protected class RefillMaterials extends SimpleEdit {
		private static final long serialVersionUID = 1L;
		
		private final Materials added;
		
		public RefillMaterials(Materials added) {
			this.added = added;
		}
		
		public void undo() throws CannotUndoException {
			super.undo();
			materials.substract(added);
			setChanged();
		}
		
		public void redo() throws CannotRedoException {
			super.redo();
			materials.add(added);
			setChanged();
		}
		
	}

}
