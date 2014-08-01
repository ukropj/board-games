package com.dill.agricola.actions.extra;

import java.util.List;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.actions.AbstractAction;
import com.dill.agricola.common.Animals;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.PointUtils;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.Space;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.UndoableFarmEdit;

public class AddAnimalPerPasture extends AbstractAction {
	
	public AddAnimalPerPasture(ActionType type) {
		super(type);
	}
	
	public boolean canDo(Player player) {
		return true;
	}

	public boolean canDoOnFarm(Player player, DirPoint pos) {
		return false;
	}

	public boolean canUndoOnFarm(Player player, DirPoint pos) {
		return false;
	}

	public UndoableFarmEdit doo(Player player) {
		List<DirPoint> range = PointUtils.createGridRange(player.farm.getWidth(), player.farm.getHeight());
		Animals toTake = new Animals();
		for (DirPoint pos : range) {
			Space space = player.farm.getSpace(pos);
			int count = space.getAnimalsPerPasture();
			if (count == 1) {
				Animal type = space.getAnimalTypesPerPasture().iterator().next();
				player.purchaseAnimal(type, count);
				player.farm.putAnimals(pos, type, count);
				toTake.add(type, count);
			}
		}
		UndoableFarmEdit edit = new TakeAnimals(player, new Animals(toTake));
		return joinEdits(true, edit);
	}

	public UndoableFarmEdit doOnFarm(Player player, DirPoint pos) {
		return null;
	}

	private class TakeAnimals extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player player;
		private final Animals takenAnimals;

		public TakeAnimals(Player player, Animals animals) {
			super(true);
			this.player = player;
			this.takenAnimals = animals;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			player.unpurchaseAnimals(takenAnimals);
		}

		public void redo() throws CannotRedoException {
			super.redo();
			player.purchaseAnimals(takenAnimals);
		}

	}

}
