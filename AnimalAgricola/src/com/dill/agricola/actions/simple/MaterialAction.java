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
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.support.Msg;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.UndoableFarmEdit;
import com.dill.agricola.view.utils.AgriImages;
import com.dill.agricola.view.utils.UiFactory;

public class MaterialAction extends AbstractAction {

	public final Materials[] materials;

	public MaterialAction(ActionType type, Materials materials) {
		this(type, new Materials[] { materials });
	}

	public MaterialAction(ActionType type, Materials[] materials) {
		super(type);
		this.materials = materials;
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
		Materials toTake = getMaterials();
		UndoableFarmEdit edit = new TakeMaterials(player, new Materials(toTake));
		player.addMaterial(toTake);
		return joinEdits(true, edit);
	}

	public UndoableFarmEdit doOnFarm(Player player, DirPoint pos) {
		return null;
	}

	protected Materials getMaterials() {
		if (materials.length == 1) {
			return materials[0];
		}
		List<JComponent> opts = new ArrayList<JComponent>();
		for (int i = 0; i < materials.length; i++) {
			JComponent opt = UiFactory.createResourcesPanel(materials[i], null, UiFactory.X_AXIS);
			opt.setPreferredSize(new Dimension(40, 30));
			opts.add(opt);
		}
		Icon icon = AgriImages.getMaterialIcon(null);
		int result = UiFactory.showOptionDialog(null, Msg.get("chooseMaterial"), getType().shortDesc, icon, opts, 0);
		return result != UiFactory.NO_OPTION ? materials[result] : materials[0];
	}

	private class TakeMaterials extends SimpleEdit {
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
			
		}

		public void redo() throws CannotRedoException {
			super.redo();
			player.addMaterial(takenMaterials);
			
		}

	}

}
