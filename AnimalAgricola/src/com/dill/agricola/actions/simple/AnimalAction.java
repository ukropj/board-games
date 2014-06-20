package com.dill.agricola.actions.simple;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.actions.AbstractAction;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Animals;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.support.Msg;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.UndoableFarmEdit;
import com.dill.agricola.view.utils.AgriImages;
import com.dill.agricola.view.utils.UiFactory;
import com.dill.agricola.view.utils.AgriImages.ImgSize;

public class AnimalAction extends AbstractAction {

	public final Animals[] animals;

	public AnimalAction(ActionType type, Animals animals) {
		this(type, new Animals[] { animals });
	}
	
	public AnimalAction(ActionType type, Animals[] animals) {
		super(type);
		this.animals = animals;
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
		Animals toTake = getAnimals();
		UndoableFarmEdit edit = new TakeAnimals(player, new Animals(toTake));
		player.purchaseAnimals(toTake);
		return joinEdits(true, edit);
	}

	public UndoableFarmEdit doOnFarm(Player player, DirPoint pos) {
		return null;
	}

	protected Animals getAnimals() {
		if (animals.length == 1) {
			return animals[0];
		}
		List<JComponent> opts = new ArrayList<JComponent>();
		for (int i = 0; i < animals.length; i++) {
			JComponent opt = UiFactory.createResourcesPanel(null, animals[i], UiFactory.X_AXIS);
			opt.setPreferredSize(new Dimension(40, 30));
			opts.add(opt);
		}
		Icon icon = AgriImages.getAnimalIcon(null, ImgSize.MEDIUM);
		int result = UiFactory.showOptionDialog(null, Msg.get("chooseAnimal"), getType().shortDesc, icon, opts, 0);
		return result != UiFactory.NO_OPTION ? animals[result] : animals[0];
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
