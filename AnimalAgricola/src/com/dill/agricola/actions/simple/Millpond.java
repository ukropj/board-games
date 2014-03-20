package com.dill.agricola.actions.simple;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import com.dill.agricola.common.Animals;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.support.Namer;
import com.dill.agricola.undo.LoggingUndoableEdit;

public class Millpond extends AnimalAction {

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

	public UndoableEdit init() {
		user = null;
		UndoableEdit edit;
		if (materials.isEmpty()) {
			materials.add(REFILL);
			edit = new RefillMaterials(REFILL);
		} else {
			animals.add(other, 1);
			edit = new RefillAnimals(new Animals(other));
		}
		setChanged();
		return edit;
	}

	public boolean canDo(Player player, int doneSoFar) {
		return !materials.isEmpty() || !animals.isEmpty();
	}

	public UndoableEdit doo(Player player, int doneSoFar) {
		if (canDo(player, doneSoFar)) {
			UndoableEdit edit = joinEdits(
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

	public boolean undo(Player player, int doneSoFar) {
		// TODO remove
		return false;
	}

	public Materials getAccumulatedMaterials() {
		return materials;
	}

	@SuppressWarnings("serial")
	public class TakeMaterials extends LoggingUndoableEdit {
		private final Player player;
		private final Materials takenMaterials;

		public TakeMaterials(Player player, Materials materials) {
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

		public String getPresentationName() {
			return Namer.getName(this);
		}
	}
	
	@SuppressWarnings("serial")
	protected class RefillMaterials extends LoggingUndoableEdit {
		
		private final Materials added;
		
		public RefillMaterials(Materials added) {
			super(false);
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
		
		public String getPresentationName() {
			return Namer.getName(this);
		}
	}

}
