package com.dill.agricola.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.dill.agricola.Game.FarmActionCommand;
import com.dill.agricola.Game.Phase;
import com.dill.agricola.actions.Action;
import com.dill.agricola.actions.ActionPerformer;
import com.dill.agricola.common.Animals;
import com.dill.agricola.common.Dir;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.PointUtils;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Farm;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.Space;
import com.dill.agricola.model.buildings.MultiImaged;
import com.dill.agricola.model.buildings.evenmore.AssemblyHall;
import com.dill.agricola.model.buildings.evenmore.BuildingFirm;
import com.dill.agricola.model.buildings.evenmore.CattleMarket;
import com.dill.agricola.model.buildings.evenmore.StoneCarversWorkshop;
import com.dill.agricola.model.buildings.more.AnimalTrader;
import com.dill.agricola.model.buildings.more.Carpenter;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.BuildingType.BuildingText;
import com.dill.agricola.model.types.ChangeType;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.model.types.Purchasable;
import com.dill.agricola.support.Fonts;
import com.dill.agricola.support.Msg;
import com.dill.agricola.undo.UndoableFarmEdit;
import com.dill.agricola.view.utils.AgriImages;
import com.dill.agricola.view.utils.AgriImages.ImgSize;
import com.dill.agricola.view.utils.Images;
import com.dill.agricola.view.utils.UiFactory;

public class FarmPanel extends JPanel {

	private static enum PurchasableState {
		CAN_DO,
		CAN_UNDO,
		NA
	}

	private static final long serialVersionUID = 1L;

	public final static Color RED = new Color(154, 0, 25);

	public final static int S = 100;
	public final static int M = S / 16, L = S - 2 * M;
	public final static int X1 = S / 2, X2 = S / 2, Y1 = (int) (S * 0.39f);
	static int H = (int) (S * 3.8f);
	static int P = (int) (S * 0.9f);

	private final static Area animalArea = new Area(new Ellipse2D.Float(S / 4 - M, S / 4 - M, S / 2 + 2 * M, S / 2 + 2 * M));
	private final static int AR = S / 6 + M; // 
	private final static DirPoint[][] animalPositions = new DirPoint[][] {
			new DirPoint[] {
					new DirPoint(S / 2, S / 2) },
			new DirPoint[] {
					new DirPoint(S / 2 + AR / 2, S / 2 - AR / 2),
					new DirPoint(S / 2 - AR / 2, S / 2 + AR / 2) },
			new DirPoint[] {
					new DirPoint(S / 2, S / 2 + AR),
					new DirPoint(S / 2 + M + AR / 2, S / 2 - M),
					new DirPoint(S / 2 - M - AR / 2, S / 2 - M) },
			new DirPoint[] {
					new DirPoint(S / 2, S / 2 - AR + M),
					new DirPoint(S / 2, S / 2 + AR),
					new DirPoint(S / 2 + AR, S / 2),
					new DirPoint(S / 2 - AR, S / 2 + M / 2) }
	};
	private final static Line2D[][] animalDividers = new Line2D[][] {
			new Line2D[] {},
			new Line2D[] { new Line2D.Float(new DirPoint(S / 4 + M, S / 4 + M), new DirPoint(3 * S / 4 - M, 3 * S / 4 - M)) },
			new Line2D[] {
					new Line2D.Float(new DirPoint(S / 2, S / 2), new DirPoint(S / 2, S / 4 - M / 2)),
					new Line2D.Float(new DirPoint(S / 2, S / 2), new DirPoint(3 * S / 4 - M, 3 * S / 4 - M)),
					new Line2D.Float(new DirPoint(S / 2, S / 2), new DirPoint(S / 4 + M, 3 * S / 4 - M)) },
			new Line2D[] {
					new Line2D.Float(new DirPoint(S / 4 + M, S / 4 + M), new DirPoint(3 * S / 4 - M, 3 * S / 4 - M)),
					new Line2D.Float(new DirPoint(S / 4 + M, 3 * S / 4 - M), new DirPoint(3 * S / 4 - M, S / 4 + M)) }
	};
	private final static Area[][] animalAreas = new Area[][] {
			new Area[] { animalArea },
			new Area[] {
					intersect(new Area(new Polygon(new int[] { 0, S, S }, new int[] { 0, 0, S }, 3)), animalArea),
					intersect(new Area(new Polygon(new int[] { 0, 0, S }, new int[] { 0, S, S }, 3)), animalArea) },
			new Area[] {
					intersect(new Area(new Polygon(new int[] { 0, S / 2, S }, new int[] { S + 1, S / 2, S }, 3)), animalArea),
					intersect(new Area(new Polygon(new int[] { S / 2, S / 2, S, S }, new int[] { 0, S / 2, S, 0 }, 4)), animalArea),
					intersect(new Area(new Polygon(new int[] { 0, S / 2, S / 2, 0 }, new int[] { 0, 0, S / 2, S + 1 }, 4)), animalArea)
			},
			new Area[] {
					intersect(new Area(new Polygon(new int[] { 0, S / 2, S + 1 }, new int[] { 0, S / 2, 0 }, 3)), animalArea),
					intersect(new Area(new Polygon(new int[] { 0, S / 2, S + 1 }, new int[] { S + 1, S / 2, S }, 3)), animalArea),
					intersect(new Area(new Polygon(new int[] { S + 1, S / 2, S + 1 }, new int[] { 0, S / 2, S }, 3)), animalArea),
					intersect(new Area(new Polygon(new int[] { 0, S / 2, 0 }, new int[] { 0, S / 2, S + 1 }, 3)), animalArea)
			}
	};

	private final static Polygon troughShape = new Polygon(
			new int[] { S - 6 * M, S - 6 * M, S - 4 * M, S - 2 * M, S - 2 * M },
			new int[] { 7 * M, 4 * M, 2 * M, 4 * M, 7 * M },
			5);
	private final static Rectangle buildingRect = new Rectangle(M, M, L, L);
	private final static Rectangle buildingOffRect = new Rectangle(M, M, L / 3, L / 3);
	//	private final static Area buildingSansAnimalRect = subtract(new Area(buildingRect), animalArea);
	private final static Rectangle extRect = new Rectangle(M, M, L, 3 * S - 2 * M);
	private final static Rectangle extOffRect = new Rectangle(M, M, L / 3, L / 3);
	private final static Map<Dir, Rectangle> fenceRects = new EnumMap<Dir, Rectangle>(Dir.class) {
		private static final long serialVersionUID = 1L;
		{
			put(Dir.N, new Rectangle(M, -M, L, 2 * M));
			put(Dir.W, new Rectangle(-M, M, 2 * M, L));
			put(Dir.S, new Rectangle(M, S - M, L, 2 * M));
			put(Dir.E, new Rectangle(S - M, M, 2 * M, L));
		}
	};
	private final static Map<Dir, Rectangle> fenceClickRects = new EnumMap<Dir, Rectangle>(Dir.class) {
		private static final long serialVersionUID = 1L;
		private static final int O = 8;
		{
			for (java.util.Map.Entry<Dir, Rectangle> dirRect : fenceRects.entrySet()) {
				Rectangle r = new Rectangle(dirRect.getValue());
				r.setBounds(r.x - O, r.y - O, r.width + 2 * O, r.height + 2 * O);
				put(dirRect.getKey(), r);
			}
		}
	};
	private final static int R = M / 2;

	private final static Polygon removeShape = new Polygon(
			new int[] { -2 * R, -R, 0, R, 2 * R, R, 2 * R, R, 0, -R, -2 * R, -R },
			new int[] { -R, -2 * R, -R, -2 * R, -R, 0, R, 2 * R, R, 2 * R, R, 0 },
			12);
	private final static Area removeBack = new Area(new Ellipse2D.Float(-4 * R, -4 * R, 8 * R, 8 * R));
	private final static Polygon removeTroughBack = new Polygon(
			new int[] { -2 * M, -2 * M, 0, 2 * M, 2 * M },
			new int[] { (5 * M) / 2, -M / 2, -(5 * M) / 2, -M / 2, (5 * M) / 2 },
			5);

	private final static Stroke NORMAL_STROKE = new BasicStroke();
	private final static Stroke THICK_STROKE = new BasicStroke(2.0f);
	private final static Stroke DASHED_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 10.0f, new float[] { 10, 5 }, 0.0f);
	private final static Color PASTURE_COLOR = new Color(153, 178, 97);

	private final Player player;
	private final Farm farm;
	private boolean active;
	private Phase phase;
	private final ActionPerformer ap;
	private final ActionListener submitListener;

	private JButton finishBtn;
	private JButton cancelBtn;
	private Map<ActionType, JButton> extraBtns = new HashMap<ActionType, JButton>();
	private JLabel msgLabel;
	private JPanel supplyPanel;
	private final JLabel[] workerLabels = new JLabel[Player.MAX_WORKERS + 1];
	private JLabel firstLabel;
	private final Map<Material, JLabel> supply = new EnumMap<Material, JLabel>(Material.class);
	private final Map<Animal, JLabel> animalSupply = new EnumMap<Animal, JLabel>(Animal.class);

	public FarmPanel(Player player, ActionPerformer ap, ActionListener submitListener) {
		this.player = player;
		this.farm = player.getFarm();
		this.ap = ap;
		this.submitListener = submitListener;

		setLayout(null);
		SpaceListener mouseListener = new SpaceListener(farm);
		addMouseListener(mouseListener);
		addMouseWheelListener(mouseListener);

		initMsgPanel();
		initButtons();
		initWorkerPanel();
		initSupplyPanel();
	}

	private void initMsgPanel() {
		msgLabel = new JLabel() {
			private static final long serialVersionUID = 1L;

			public void paintComponent(Graphics g) {
				g.setColor(getBackground());
				g.fillRect(0, 0, getWidth(), getHeight());
				super.paintComponent(g);
			}
		};
		msgLabel.setHorizontalAlignment(JLabel.CENTER);
		msgLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		msgLabel.setAlignmentY(JLabel.CENTER_ALIGNMENT);
		msgLabel.setFont(Fonts.FARM_MESSAGE);
		msgLabel.setBackground(UiFactory.makeTranslucent(msgLabel.getBackground(), 210));
		msgLabel.setBounds(M, M, getWidth() - 2 * M, X1 - 4 * M);
		add(msgLabel);
	}

	private void initButtons() {
		finishBtn = new JButton(AgriImages.getYesIcon());
		finishBtn.setToolTipText(Msg.get("finishActionTip"));
		finishBtn.setMargin(new Insets(0, 0, 0, 0));
		finishBtn.setBounds(X1 + farm.getWidth() * S - S / 3 - 2 * M, Y1 + 3 * S + 3 * M / 2, S / 3 + 3 * M / 2, S / 3);
		finishBtn.setActionCommand(FarmActionCommand.SUBMIT.toString());
		finishBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (ap.hasAction()) {
					if (ap.finishActions()) {
						submitListener.actionPerformed(e);
					}
				}
			}
		});
		finishBtn.setFont(Fonts.ACTION_TEXT);
		finishBtn.setIconTextGap(0);
		finishBtn.setText("");
		finishBtn.setForeground(RED);
		add(finishBtn);

		cancelBtn = new JButton(AgriImages.getNoIcon());
		cancelBtn.setToolTipText(Msg.get("cancelBtnTip"));
		cancelBtn.setMargin(new Insets(0, 0, 0, 0));
		cancelBtn.setBounds(X1 + farm.getWidth() * S, Y1 + 3 * S + 3 * M / 2, S / 3, S / 3);
		cancelBtn.setActionCommand(FarmActionCommand.CANCEL.toString());
		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (ap.hasAction()) {
					submitListener.actionPerformed(e);
				}
			}
		});
		add(cancelBtn);
	}

	private void initWorkerPanel() {
		ImageIcon icon = new ImageIcon(Images.getBestScaledInstance(AgriImages.getFirstTokenImage(player.getColor().ordinal(), ImgSize.BIG), 0.7f));
		firstLabel = UiFactory.createLabel(icon);
		firstLabel.setBounds(M / 2, Y1 + M, 40, 40);
		add(firstLabel);

		JPanel workersP = UiFactory.createVerticalPanel();
		for (int i = 0; i < workerLabels.length; i++) {
			workerLabels[i] = UiFactory.createLabel(AgriImages.getWorkerIcon(player.getColor(), ImgSize.MEDIUM));
//			workerLabels[i].setDisabledIcon(AgriImages.getWorkerIcon(null));
			workersP.add(workerLabels[i]);
			workersP.add(Box.createVerticalStrut(5));
		}
		workersP.setBounds(M / 2, Y1 + S / 2, 40, workerLabels.length * 40);
		add(workersP);
	}

	private void initSupplyPanel() {
		supplyPanel = new JPanel(/* BoxLayout */) {
			private static final long serialVersionUID = 1L;

			public void paintComponent(Graphics g) {
				g.setColor(getBackground());
				g.fillRect(0, 0, getWidth(), getHeight());
				super.paintComponent(g);
			}
		};
		supplyPanel.setLayout(new BoxLayout(supplyPanel, BoxLayout.Y_AXIS));
		supplyPanel.setOpaque(false);
		supplyPanel.setBackground(UiFactory.makeTranslucent(msgLabel.getBackground(), 180));

		JPanel materialsP = new JPanel(new GridLayout(2, 4));
		materialsP.setOpaque(false);
		for (Material m : Material.values()) {
			JLabel l = UiFactory.createMaterialLabel(m, 0, UiFactory.ICON_FIRST);
			l.setToolTipText(m.getName());
			materialsP.add(l);
			supply.put(m, l);
		}
		for (Animal a : Animal.values()) {
			JLabel l = UiFactory.createAnimalLabel(a, 0, UiFactory.ICON_FIRST);
			l.setToolTipText(a.getName(true));
			materialsP.add(l);
			animalSupply.put(a, l);
		}
		supplyPanel.add(materialsP);
		supplyPanel.setBounds(M, H + M, getWidth() - 2 * M, P - 2 * M);
		add(supplyPanel);
	}

	public void updateComponentPosition() {
		finishBtn.setLocation(X1 + farm.getWidth() * S - S / 3 - 2 * M, finishBtn.getY());
		cancelBtn.setLocation(X1 + farm.getWidth() * S, cancelBtn.getY());

		supplyPanel.setLocation((getWidth() - supplyPanel.getWidth()) / 2, supplyPanel.getY());
		msgLabel.setLocation((getWidth() - msgLabel.getWidth()) / 2, msgLabel.getY());
	}

	public void updateControls() {
		firstLabel.setVisible(player.isStarting());
		for (int i = 0; i < workerLabels.length; i++) {
			workerLabels[i].setVisible(player.getWorkers() > i);
//			workerLabels[i].setEnabled(player.getWorkers() > i);
		}
		for (Material m : Material.values()) {
			supply.get(m).setText(String.valueOf(player.getMaterial(m)));
		}
		for (Animal a : Animal.values()) {
			animalSupply.get(a).setText(String.valueOf(player.getAnimal(a)));
		}

		boolean isActivePlayer = active && player.equals(ap.getPlayer());

		if (isActivePlayer && ap.hasAction()) {
			finishBtn.setVisible(true);
			finishBtn.setEnabled(ap.canFinish());

			if (ap.hasAction() && !ap.isFinished() && ap.canCancel()) {
				cancelBtn.setVisible(true);
				cancelBtn.setEnabled(true);
			}
			if (player.hasLooseAnimals()) {
				int n = farm.getLooseAnimals().size();
				finishBtn.setText(String.valueOf(n));
				finishBtn.setToolTipText(Msg.getNum(n, "finishActionRunAwayTip", n));
			} else {
				finishBtn.setText("");
				finishBtn.setToolTipText(Msg.get("finishActionTip"));
			}
		} else {
			finishBtn.setVisible(false);
			cancelBtn.setVisible(false);
		}

		this.msgLabel.setVisible(isActivePlayer);
		if (isActivePlayer) {
			if (ap.hasAction()) {
				Action a = ap.getTopAction();
				this.msgLabel.setText(a.getType().farmText);
			} else {
				this.msgLabel.setText(Msg.get("chooseActionMsg"));
			}
		}
		updateExtraBtns();
	}

	private void initExtraButton(final Action action, ImageIcon icon, boolean quickAction) {
		JButton btn = new JButton(icon);
		btn.setToolTipText(action.getType().desc);
		btn.setMargin(new Insets(1, 1, 1, 1));
		btn.setBounds(X1 + farm.getWidth() * S + (3 * M) / 2, Y1 + M, S / 3 + M / 2, S / 3 + M / 2);
		btn.setCursor(AgriImages.HAND_CURSOR);
		if (quickAction) {
			btn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// this action is not managed by ActionPerformer!
					if (action.canDo(player)) {
						UndoableFarmEdit edit = action.doo(player);
						if (edit != null) {
							ap.postEdit(edit);
							player.notifyObservers(ChangeType.ACTION_DONE);
						}
					}
				}
			});
		} else {
			btn.setActionCommand(FarmActionCommand.START_EXTRA_WORK.toString());
			btn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					submitListener.actionPerformed(e);
					if (ap.startAction(action, true)) {
						if (ap.isFinished()) {
							submitListener.actionPerformed(e);
						}
					}
				}
			});
		}
		add(btn);
		extraBtns.put(action.getType(), btn);
	}

	private void updateExtraBtns() {
		List<ActionType> visibleTypes = new ArrayList<ActionType>();
		if (active && player.equals(ap.getPlayer())) {
			// not dynamic, currently not worth it
			JButton btn;
			if (farm.hasBuilding(BuildingType.ANIMAL_TRADER)) {
				Action action = AnimalTrader.TRADE_ANIMALS;
				ActionType type = action.getType();
				if (!extraBtns.containsKey(type)) {
					initExtraButton(action, AgriImages.getButtonImage("trade_animals"), true);
				}
				btn = extraBtns.get(type);
				btn.setEnabled(action.canDo(player) && !ap.hasAction(ActionType.BREEDING));
				visibleTypes.add(type);
			}

			if (farm.hasBuilding(BuildingType.CATTLE_MARKET)) {
				Action action = CattleMarket.TRADE_REED;
				ActionType type = action.getType();
				if (!extraBtns.containsKey(type)) {
					initExtraButton(action, AgriImages.getButtonImage("trade_reed"), true);
				}
				btn = extraBtns.get(type);
				btn.setEnabled(action.canDo(player));
				visibleTypes.add(type);
			}

			if (farm.hasBuilding(BuildingType.CARPENTER)) {
				Action action = Carpenter.MOVE_TROUGHS;
				ActionType type = action.getType();
				if (!extraBtns.containsKey(type)) {
					initExtraButton(action, AgriImages.getButtonImage("move_troughs"), false);
				}
				btn = extraBtns.get(type);
				btn.setEnabled(action.canDo(player) && !ap.hasAction());
				visibleTypes.add(type);
			}

			if (farm.hasBuilding(BuildingType.CARPENTER)) {
				Action action = Carpenter.MOVE_STALLS;
				ActionType type = action.getType();
				if (!extraBtns.containsKey(type)) {
					initExtraButton(action, AgriImages.getButtonImage("move_stalls"), false);
				}
				btn = extraBtns.get(type);
				btn.setEnabled(action.canDo(player) && !ap.hasAction());
				visibleTypes.add(type);
			}

			if (farm.hasBuilding(BuildingType.BUILDING_FIRM)) {
				Action action = BuildingFirm.UPGRADE_TROUGH;
				ActionType type = action.getType();
				if (!extraBtns.containsKey(type)) {
					initExtraButton(action, AgriImages.getButtonImage("upgrade_trough"), false);
				}
				btn = extraBtns.get(type);
				btn.setEnabled(action.canDo(player) && !ap.hasAction());
				visibleTypes.add(type);
			}

			if (farm.hasBuilding(BuildingType.ASSEMBLY_HALL)) {
				Action action = AssemblyHall.GIVE_BORDER;
				ActionType type = action.getType();
				if (!extraBtns.containsKey(type)) {
					initExtraButton(action, AgriImages.getButtonImage("give_border"), false);
				}
				btn = extraBtns.get(type);
				btn.setEnabled(action.canDo(player) && !ap.hasAction());
				visibleTypes.add(type);
			}
		}

		for (Entry<ActionType, JButton> btnEntry : extraBtns.entrySet()) {
			JButton btn = btnEntry.getValue();
			int index = visibleTypes.indexOf(btnEntry.getKey());
			if (index != -1) {
				btn.setLocation(btn.getX(), Y1 + M + index * (S / 3 + M));
				btn.setVisible(true);
			} else {
				btn.setVisible(false);
			}
		}

	}

	public void setActive(boolean active, Phase phase) {
		this.active = active;
		this.phase = phase;

		updateControls();
	}

	private static Area intersect(Area area, Area intersector) {
		area.intersect(intersector);
		return area;
	}

	private DirPoint rectCenter(Rectangle r) {
		return new DirPoint(r.x + r.width / 2, r.y + r.height / 2);
	}

	private static DirPoint toRealPos(DirPoint farmPos) {
		return new DirPoint(X1 + S * farmPos.x, Y1 + S * farmPos.y);
	}

	private static DirPoint toFarmPos(int x, int y) {
		return new DirPoint(x >= X1 ? (x - X1) / S : -1, y >= Y1 ? (y - Y1) / S : -1);
	}

	private static DirPoint toRealRelativePos(int x, int y) {
		DirPoint pos = toFarmPos(x, y);
		int dx = Math.abs(pos.x * S - (x - X1));
		int dy = Math.abs(pos.y * S - (y - Y1));
		return new DirPoint(dx, dy);
	}

	public int getWidth() {
		return X1 + farm.getWidth() * S + X2;
	}

	public int getHeight() {
		return H + P;
	}

	public Dimension getPreferredSize() {
		return new Dimension(getWidth(), getHeight());
	}

	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	public void paintComponent(Graphics g0) {
		super.paintComponent(g0);
		Graphics2D g = (Graphics2D) g0;
//		g.setClip(getVisibleRect());

		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

		// background (should not be seen)
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());

		// farm
		drawFarm(g);

		// spaces
		List<DirPoint> grid = PointUtils.createGridRange(farm.getWidth(), farm.getHeight());
		for (DirPoint pos : grid) {
			drawSpace(g, pos, farm.getSpace(pos));
		}
		// fences
		for (DirPoint pos : grid) {
			for (Dir d : Dir.values()) {
				drawFence(g, new DirPoint(pos, d));
			}
		}

		// loose animals
		drawLooseAnimals(g);

		drawPad(g);
	}

	private void drawRemoveShape(Graphics2D g, DirPoint pos) {
		drawRemoveShape(g, pos, false);
	}

	private void drawRemoveShape(Graphics2D g, DirPoint pos, boolean trough) {
		AffineTransform tr = AffineTransform.getTranslateInstance(pos.x, pos.y);
		Shape back = tr.createTransformedShape(trough ? removeTroughBack : removeBack);
		Shape front = tr.createTransformedShape(removeShape);

		g.setColor(UiFactory.makeTranslucent(player.getColor().getRealColor(), 150));
		g.fill(back);
		g.setColor(Color.BLACK);
		g.fill(front);
	}

	private void drawPad(Graphics2D g) {
		int count = (farm.getWidth() + 1) / 2 + (farm.getWidth() + 1) % 2;
		int d = (count * 2 * S - getWidth()) / 2;
		for (int i = 0; i < count; i++) {
			BufferedImage img = AgriImages.getFarmPadImage(i % 3);
			g.drawImage(img, -d + i * 2 * S, H, S * 2, P, null);
		}
	}

	private void drawFarm(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.setStroke(DASHED_STROKE);

		// west margin
		BufferedImage img = AgriImages.getFarmMarginImage(Dir.W);
		g.drawImage(img, X1 - S / 2, 0, S / 2, H, null);
		// west extensions
		int westExts = farm.getExtensions(Dir.W).size(), farmCore = 2;
		int i = 0;
		for (Integer id : farm.getExtensions(Dir.W)) {
			img = AgriImages.getExtensionImage(id);
			int x = X1 + (westExts - i - 1) * S;
			g.drawImage(img, x, 0, S, H, null);
			g.drawLine(x + S - 1, Y1 - M, x + S - 1, Y1 + 3 * S + M);
			i++;
		}
		// farm
		img = AgriImages.getFarmImage(player.getColor());
		g.drawImage(img, X1 + westExts * S, 0, farmCore * S, H, null);
		// east extensions
		i = 0;
		for (Integer id : farm.getExtensions(Dir.E)) {
			img = AgriImages.getExtensionImage(id);
			int x = X1 + (westExts + farmCore + i) * S;
			g.drawImage(img, x, 0, S, H, null);
			// marker
			g.drawLine(x, Y1 - M, x, Y1 + 3 * S + M);
			i++;
		}
		// east margin
		img = AgriImages.getFarmMarginImage(Dir.E);
		g.drawImage(img, X1 + S * farm.getWidth(), 0, S / 2, H, null);

		for (DirPoint pos : PointUtils.createGridRange(-1, farm.getWidth() + 1, 0, 1)) {
			switch (getState(pos, Purchasable.EXTENSION)) {
			case CAN_DO:
				g.setColor(UiFactory.makeTranslucent(player.getColor().getRealColor(), 140));
				extRect.translate(X1 + pos.x * S, Y1);
				g.fill(extRect);
				extRect.translate(-(X1 + pos.x * S), -Y1);
				break;
			case CAN_UNDO:
				Rectangle r = extOffRect.getBounds();
				r.translate(X1 + pos.x * S, Y1);
				drawRemoveShape(g, rectCenter(r));
				break;
			default:
				break;
			}
		}
	}

	private void drawSpace(Graphics2D g, DirPoint pos, Space space) {

		Set<Animal> availableAnimals = farm.guessAnimalTypesToPut(pos, true);

		// building
		drawBuilding(g, pos, space, farm.getBuilding(pos), availableAnimals);

		// trough
		drawTrough(g, pos, space);

		// animals
		drawAnimal(g, pos, space, availableAnimals);
	}

	private void drawAnimal(Graphics2D g, DirPoint pos, Space space, Set<Animal> availableAnimals) {
		Animal type = null;
		Set<Animal> types = space.getAnimalTypes();
		types.addAll(space.getRequiredAnimals());

		if (!types.isEmpty()) {
			if (types.size() == 1) {
				type = types.iterator().next();
			} else {
				drawMultiAnimal(g, pos, space, types, availableAnimals);
				return;
			}
		} else {
			Set<Animal> pastureTypes = space.getAnimalTypesPerPasture();
			if (pastureTypes.size() == 1) {
				type = pastureTypes.iterator().next();
			}
		}

		DirPoint realPos = toRealPos(pos);
		int count = type != null ? space.getAnimals(type) : 0;

		drawAnimalCounter(g, realPos, type, count, space.getMaxCapacity(), space.isValid(), 0);

		if (space.getMaxCapacity() > count && availableAnimals.size() > 0) {
			// can add more - show "area"
			AffineTransform tr = AffineTransform.getTranslateInstance(realPos.x, realPos.y);
			Area r = animalArea.createTransformedArea(tr);
			g.setColor(UiFactory.makeTranslucent(player.getColor().getRealColor(), 120));
			g.fill(r);
			if (count == 0) {
				// no animals present - show options
				int typeCount = availableAnimals.size();
				Iterator<Animal> it = availableAnimals.iterator();
				for (int i = 0; i < typeCount; i++) {
					Animal t = it.next();
					DirPoint p = new DirPoint(animalPositions[typeCount - 1][i]);
					p.translate(realPos.x, realPos.y);
					BufferedImage img = AgriImages.getAnimalImage(t, ImgSize.MEDIUM);
					int w = img.getWidth(), h = img.getHeight();
					g.drawImage(img, p.x - w / 2, p.y - h / 2, w, h, null);

					g.setColor(Color.BLACK);
					g.setStroke(NORMAL_STROKE);
					for (Line2D line : animalDividers[typeCount - 1]) {
						g.draw(tr.createTransformedShape(line));
					}
				}
			}
		}
		if (count > 0) {
			// show present animals
			BufferedImage img = AgriImages.getAnimalImage(type, ImgSize.BIG);
			int w = img.getWidth(), h = img.getHeight();
			g.drawImage(img, realPos.x + (S - w) / 2, realPos.y + (S - h) / 2, w, h, null);
		}

	}

	private void drawMultiAnimal(Graphics2D g, DirPoint pos, Space space, Set<Animal> types, Set<Animal> availableAnimals) {
		DirPoint realPos = toRealPos(pos);

		Iterator<Animal> it = types.iterator();
		for (int i = 0; i < types.size(); i++) {
			Animal type = it.next();
			int count = space.getAnimals(type);
			drawAnimalCounter(g, realPos, type, count, space.getMaxCapacity(type), space.isValid(), types.size() - i - 1);
		}

		boolean drawArea = false;
		List<Animal> relevantTypes = new ArrayList<Animal>();
		for (Animal t : types) {
			boolean canAddMore = space.getActualCapacity(t) > 0 && availableAnimals.contains(t);
			if (space.getAnimals(t) > 0 || canAddMore) {
				relevantTypes.add(t);
				drawArea = drawArea || canAddMore;
			}
		}

		AffineTransform tr = AffineTransform.getTranslateInstance(realPos.x, realPos.y);
		// can add more - show "area"
		if (drawArea) {
			Area r = animalArea.createTransformedArea(tr);
			g.setColor(UiFactory.makeTranslucent(player.getColor().getRealColor(), 120));
			g.fill(r);
		}

		int relevantCount = relevantTypes.size();
		int i = 0;
		for (Animal type : relevantTypes) {
			int count = space.getAnimals(type);
			if (space.getMaxCapacity(type) > count && availableAnimals.contains(type)) {
				if (count == 0) {
					// no animals of this present - show option
					DirPoint p = new DirPoint(animalPositions[relevantCount - 1][i]);
					p.translate(realPos.x, realPos.y);
					BufferedImage img = AgriImages.getAnimalImage(type, ImgSize.MEDIUM);
					int w = img.getWidth(), h = img.getHeight();
					g.drawImage(img, p.x - w / 2, p.y - h / 2, w, h, null);

					g.setColor(Color.BLACK);
					g.setStroke(NORMAL_STROKE);
					for (Line2D line : animalDividers[relevantCount - 1]) {
						g.draw(tr.createTransformedShape(line));
					}
				}
			}
			if (count > 0) {
				// show present animals
				DirPoint p = new DirPoint(animalPositions[relevantCount - 1][i]);
				p.translate(realPos.x, realPos.y);
				BufferedImage img = AgriImages.getAnimalImage(type, ImgSize.BIG);
				int w = img.getWidth(), h = img.getHeight();
//				g.drawImage(img, realPos.x + (S - w) / 2, realPos.y + (S - h) / 2, w, h, null);
				g.drawImage(img, p.x - w / 2, p.y - h / 2, w, h, null);
			}
			i++;
		}
	}

	private void drawAnimalCounter(Graphics2D g, DirPoint realPos, Animal type, int count, int max, boolean valid, int index) {
		int k = S / 3 - M / 4;
		int dx = index < 3 ? index * -k : 2 * -k;
		int dy = index < 3 ? 0 : -k;
		if (max > 0 || count > 0) {
			// draw counter circle
			g.setColor(UiFactory.makeTranslucent(type != null ? (valid ? type.getColor() : PASTURE_COLOR) : PASTURE_COLOR, 200));
			Rectangle origClip = g.getClipBounds();
			Rectangle clipRect = new Rectangle(realPos.x + M, realPos.y + M, L + 1, L + 1);
			Area clip = new Area(origClip == null ? clipRect : origClip.createIntersection(clipRect));
			g.setClip(clip);
			if (index < 3) {
				g.fillOval(realPos.x + S - 6 * M + dx, realPos.y + S - 6 * M + dy, 6 * M, 8 * M);
			} else {
				g.fillOval(realPos.x + S - 6 * M + dx - 2 * M, realPos.y + S - 6 * M + dy + M / 2, 8 * M, 5 * M);
			}
			g.setClip(origClip);
			// draw actual/max
			g.setColor(type != null ? (valid ? type.getContrastingColor() : RED) : Color.BLACK);
			g.setFont(Fonts.FARM_FONT);
			FontMetrics fm = g.getFontMetrics();
			int w0 = fm.stringWidth("99");
			String t1 = String.valueOf(count);
			int w1 = fm.stringWidth(t1);
			String t2 = String.valueOf(max);
			int w2 = fm.stringWidth(t2);
			int c = -1;
			g.drawString(t1, realPos.x + S - 5 * M + (w0 - w1) / 2 + c + dx, realPos.y + S - 3 * M + dy);
			g.drawString(t2, realPos.x + S - 3 * M + (w0 - w2) / 2 + c + dx, realPos.y + S - 3 * M / 2 + dy);
			// slash
			g.setStroke(THICK_STROKE);
			g.drawLine(realPos.x + S - 4 * M + c + dx, realPos.y + S - 2 * M + dy, realPos.x + S - 2 * M + c + dx, realPos.y + S - 4 * M + dy);
		}
	}

	private void drawTrough(Graphics2D g, DirPoint pos, Space space) {
		DirPoint realPos = toRealPos(pos);
		if (space.hasTrough()) {
			BufferedImage img = AgriImages.getTroughImage(ImgSize.BIG);
			g.drawImage(img, realPos.x + S - 6 * M, realPos.y + 2 * M + 2, img.getWidth(), img.getHeight(), null);
		}

		switch (getState(pos, Purchasable.TROUGH)) {
		case CAN_DO:
			troughShape.translate(realPos.x, realPos.y);
			g.setColor(UiFactory.makeTranslucent(player.getColor().getRealColor(), 140));
			g.fill(troughShape);
			troughShape.translate(-realPos.x, -realPos.y);
			break;
		case CAN_UNDO:
			Rectangle r = troughShape.getBounds();
			r.translate(realPos.x, realPos.y);
			drawRemoveShape(g, rectCenter(r), true);
			break;
		default:
			break;
		}
	}

	private void drawBuilding(Graphics2D g, DirPoint pos, Space space, Building building, Set<Animal> availableAnimals) {
		DirPoint realPos = toRealPos(pos);
		Rectangle r = new Rectangle(buildingRect);
		r.translate(realPos.x, realPos.y);

		if (building != null) {
			BuildingType type = building.getType();
			BufferedImage img = null;
			switch (type) {
			case COTTAGE:
				img = AgriImages.getCottageImage(player.getColor());
				break;
			case STALL:
				img = AgriImages.getStallImage(((MultiImaged) building).getId());
				break;
			case STABLES:
				img = AgriImages.getStableImage(((MultiImaged) building).getId());
				break;
			default:
				img = AgriImages.getBuildingImage(type);
			}
			if (img != null) {
				g.drawImage(img, r.x, r.y, r.width, r.height, null);
				// name
				g.setColor(Color.BLACK);
				g.setFont(Fonts.FARM_BUILDING);
				int x = 18, y = 31, maxw = 63;
				if (type == BuildingType.COTTAGE) {
					y -= 1;
				}
				Fonts.updateFontToFit(g, type.name, maxw);
				int textW = g.getFontMetrics().stringWidth(type.name);
				g.drawString(type.name, r.x + x + (maxw - textW) / 2, r.y + y);

				// text
				for (BuildingText bText : type.texts) {
					if (bText.text != null) {
						g.setFont(Fonts.FARM_BUILDING);
						Fonts.updateFontToFit(g, bText.text, (int) (r.width * bText.textWidth));
						float ty = r.height * bText.y, textH = g.getFontMetrics().getHeight();
						for (String line : bText.text.split("[\r\n]+")) {
							g.drawString(line, r.x + (r.width * bText.x), r.y + ty);
							ty += textH;
						}
					}
				}
			}
			if (type == BuildingType.STONE_CARVERS_WORKSHOP) {
				// draw carved stones
				int stones = StoneCarversWorkshop.CARVE_STONE.getCarvedStones();
				if (stones > 0) {
					ImageIcon stoneImg = AgriImages.getMaterialMultiIcon(Material.STONE, stones);
					g.drawImage(stoneImg.getImage(), r.x + (r.width - stoneImg.getIconWidth()) / 2, r.y + S / 2,
							stoneImg.getIconWidth(), stoneImg.getIconHeight(), null);
				}
			}
		}

		switch (getState(pos, Purchasable.BUILDING)) {
		case CAN_DO:
			g.setColor(UiFactory.makeTranslucent(player.getColor().getRealColor(), 120));
			g.fill(r);
			break;
		case CAN_UNDO:
			buildingOffRect.translate(realPos.x, realPos.y);
			drawRemoveShape(g, rectCenter(buildingOffRect));
			buildingOffRect.translate(-realPos.x, -realPos.y);
			break;
		default:
			break;
		}
	}

	private void drawFence(Graphics2D g, DirPoint dirPos) {
		if ((dirPos.dir == Dir.N && dirPos.y > 0) || (dirPos.dir == Dir.W && dirPos.x > 0)) {
			// will be brawn by neighouring spaces
			return;
		}
		DirPoint realPos = toRealPos(dirPos);
		boolean hasBorder = farm.has(Purchasable.FENCE, dirPos, false);

		Rectangle r = new Rectangle(fenceRects.get(dirPos.dir));
		r.translate(realPos.x, realPos.y);

		if (hasBorder) {
			BufferedImage img = AgriImages.getFenceImage(dirPos.dir);
			g.drawImage(img, r.x, r.y, r.width, r.height, null);
		}

		switch (getState(dirPos, Purchasable.FENCE)) {
		case CAN_DO:
			g.setColor(UiFactory.makeTranslucent(player.getColor().getRealColor(), 130));
			g.fill(r);
			break;
		case CAN_UNDO:
			drawRemoveShape(g, rectCenter(r));
			break;
		default:
			break;
		}
	}

	private PurchasableState getState(DirPoint pos, Purchasable type) {
		if (active && farm.hasActiveType(type)) {
			int level = farm.getLevelOfActiveType(type);
			if (ap.canDoFarmAction(pos, type, level)) {
				return PurchasableState.CAN_DO;
			}
			if (ap.canUndoFarmAction(pos, type, level)) {
				return PurchasableState.CAN_UNDO;
			}
		}
		return PurchasableState.NA;
	}

	private void drawLooseAnimals(Graphics2D g) {
		Animals loose = farm.getLooseAnimals();
		int total = loose.size();
		if (total > 0) {
			total += 3;
			float maxWidth = 1.0f * farm.getWidth() * S - S / 2;
			float l = Math.min(2.0f * M, maxWidth / total);
			float x = farm.getWidth() * S - S / 3 - 3 * M;
			int y = Y1 + farm.getHeight() * S + S / 2 - 2 * M;
			int j = 0;
			BufferedImage img = null;
			for (Animal type : Animal.values()) {
				int count = loose.get(type);
				if (count > 0) {
					img = AgriImages.getAnimalImage(type, ImgSize.BIG);
					for (int i = 0; i < count; i++) {
						g.drawImage(img, (int) (x - j * l), y - img.getHeight(), img.getWidth(), img.getHeight(), null);
						j++;
					}
					j++;
				}
			}
		}
	}

	private class SpaceListener extends MouseAdapter {

		private final Farm farm;

		public SpaceListener(Farm farm) {
			this.farm = farm;
		}

		private Animal getAnimalType(Set<Animal> types, DirPoint relativeDirPoint) {
			int typeCount = types.size();
			if (typeCount != 0) {
				Iterator<Animal> it = types.iterator();
				for (int i = 0; i < animalAreas[typeCount - 1].length; i++) {
					if (animalAreas[typeCount - 1][i].contains(relativeDirPoint)) {
						return it.next();
					}
					it.next();
				}
			}
			return null;
		}

		public void mouseClicked(MouseEvent e) {
			ChangeType changeType = ChangeType.FARM_CLICK;

			DirPoint pos = toFarmPos(e.getX(), e.getY());
			DirPoint relativeDirPoint = toRealRelativePos(e.getX(), e.getY());

			boolean leftClick = e.getButton() == MouseEvent.BUTTON1;
			boolean multiClick = e.getClickCount() > 1;

			Set<Animal> availableAnimals = null;

			boolean done = false, hadAction = ap.hasAction();
			Map<Integer, Purchasable> activeTypes = farm.getActiveTypes();

			Space space = farm.getSpace(pos);

			// TODO refactor to more functions
			if (active && phase != Phase.BREEDING) {
				for (Entry<Integer, Purchasable> act : activeTypes.entrySet()) {
					int level = act.getKey();
					Purchasable activeType = act.getValue();

					switch (activeType) {
					case EXTENSION:
						Dir extDir = null;
						if (pos.x < 0 ||
								(pos.x == 0 && extOffRect.contains(relativeDirPoint) &&
								farm.isActiveSpot(new DirPoint(0, 0), Purchasable.EXTENSION))) {
							extDir = Dir.W;
						} else if (pos.x >= farm.getWidth() ||
								(pos.x == farm.getWidth() - 1 && extOffRect.contains(relativeDirPoint) &&
								farm.isActiveSpot(new DirPoint(farm.getWidth() - 1, 0), Purchasable.EXTENSION))) {
							extDir = Dir.E;
						}
						if (extDir != null) {
							done = ap.doOrUndoFarmAction(new DirPoint(pos.x, 0), Purchasable.EXTENSION, level);
							changeType = ChangeType.FARM_RESIZE;
						}
						break;
					case FENCE:
						Dir fenceDir = null;
						for (Dir d : Dir.values()) {
							if (fenceClickRects.get(d).contains(relativeDirPoint)) {
								fenceDir = d;
								break;
							}
						}
						if (fenceDir != null) {
							DirPoint fencePoint = new DirPoint(pos, fenceDir);
							if (fenceDir.ordinal() >= 2) {
								// normalize to N/W
								fencePoint = PointUtils.getNext(fencePoint);
							}
							done = ap.doOrUndoFarmAction(fencePoint, Purchasable.FENCE, level);
						}
						break;
					case TROUGH:
						if (troughShape.contains(relativeDirPoint)) {
							done = ap.doOrUndoFarmAction(pos, Purchasable.TROUGH, level);
						}
						break;
					case BUILDING:
						if (ap.canUndoFarmAction(pos, Purchasable.BUILDING, level) && buildingOffRect.contains(relativeDirPoint)) {
							done = ap.undoFarmAction(pos, Purchasable.BUILDING, level);
						} else if (buildingRect.contains(relativeDirPoint)) {
							availableAnimals = farm.guessAnimalTypesToPut(pos, true);
							if (!animalArea.contains(relativeDirPoint) // not clicked in animal area OR
									// no animals available OR no animals present
									|| ((availableAnimals.size() == 0 && leftClick) || (space != null && space.getAnimals() == 0 && !leftClick))) {
								done = ap.doFarmAction(pos, Purchasable.BUILDING, level);
							}
						}
						break;
					}
					if (done) {
						break;
					}
				}
			}

			if (!done && space != null) {
				availableAnimals = availableAnimals != null ? availableAnimals : farm.guessAnimalTypesToPut(pos, true);
				availableAnimals.addAll(space.getAnimalTypes());
				if (leftClick) {
					Animal type = getAnimalType(availableAnimals, relativeDirPoint);
					if (type != null) {
						if (farm.putAnimals(pos, type, multiClick ? Integer.MAX_VALUE : 1) > 0) {
							done = true;
							changeType = ChangeType.FARM_ANIMALS;
						}
					}
				} else {
					Animal type = getAnimalType(availableAnimals, relativeDirPoint);
					if (type != null) {
						if (farm.takeAnimals(pos, type, multiClick ? Integer.MAX_VALUE : 1) > 0) {
							done = true;
							changeType = ChangeType.FARM_ANIMALS;
						}
					}
				}
			}

			if (done) {
				farm.notifyObservers(changeType);
				if (hadAction && ap.isFinished()) {
					ActionEvent evt = new ActionEvent(player.getColor(),
							ActionEvent.ACTION_PERFORMED,
							FarmActionCommand.SUBMIT.toString(),
							e.getWhen(),
							e.getModifiers());
					submitListener.actionPerformed(evt);
				}
			}
		}

		public void mouseWheelMoved(MouseWheelEvent e) {
			DirPoint pos = toFarmPos(e.getX(), e.getY());
			DirPoint relativeDirPoint = toRealRelativePos(e.getX(), e.getY());
			int count = -e.getWheelRotation();
			boolean done = false;

			Space space = farm.getSpace(pos);

			if (space != null) {
				Set<Animal> availableAnimals = farm.guessAnimalTypesToPut(pos, true);
				availableAnimals.addAll(space.getAnimalTypes());
				if (count > 0) {
					Animal type = getAnimalType(availableAnimals, relativeDirPoint);
					if (type != null) {
						if (farm.putAnimals(pos, type, count) > 0) {
							done = true;
						}
					}
				} else {
					Animal type = getAnimalType(availableAnimals, relativeDirPoint);
					if (type != null) {
						if (farm.takeAnimals(pos, type, -count) > 0) {
							done = true;
						}
					}
				}
			}
			if (done) {
				farm.notifyObservers(ChangeType.FARM_ANIMALS);
			}
		}

	}

}
