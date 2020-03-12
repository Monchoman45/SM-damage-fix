// 
// Decompiled by Procyon v0.5.36
// 

package org.schema.game.client.view.gui.shiphud.newhud;

import org.schema.game.common.controller.elements.ElementCollectionManager;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import org.schema.game.common.util.FastCopyLongOpenHashSet;
import java.util.Set;
import org.schema.game.common.controller.elements.ManagerModuleCollection;
import org.schema.game.common.controller.elements.ShieldLocal;
import org.schema.game.common.controller.elements.ShieldLocalAddOn;
import org.schema.game.common.controller.elements.power.reactor.PowerInterface;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.controller.elements.power.reactor.chamber.ReactorChamberElementManager;
import org.schema.game.common.controller.elements.power.reactor.chamber.ReactorChamberUnit;
import org.schema.game.common.controller.elements.ManagerModuleSingle;
import org.schema.game.common.controller.elements.power.reactor.chamber.ReactorChamberCollectionManager;
import org.schema.game.common.controller.elements.power.reactor.PowerImplementation;
import org.schema.game.common.controller.elements.power.reactor.MainReactorUnit;
import org.schema.game.common.data.element.Element;
import org.schema.game.common.controller.elements.StabBonusCalcStyle;
import org.schema.game.common.controller.elements.power.reactor.StabilizerUnit;
import org.schema.game.common.controller.damage.effects.InterEffectHandler;
import org.schema.game.common.controller.damage.effects.InterEffectSet;
import org.schema.game.common.controller.damage.DamageDealer;
import org.schema.game.common.controller.elements.ControlBlockElementCollectionManager;
import org.schema.game.common.controller.elements.UsableCombinableControllableElementManager;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.game.common.controller.elements.ShieldContainerInterface;
import org.schema.game.common.controller.elements.VoidElementManager;
import org.schema.game.common.data.ManagedSegmentController;
import org.schema.game.common.controller.EditableSendableSegmentController;
import org.schema.game.client.view.BuildModeDrawer;
import org.schema.game.client.controller.manager.ingame.PlayerInteractionControlManager;
import org.schema.game.common.controller.Ship;
import org.schema.game.common.data.player.ControllerStateUnit;
import org.schema.schine.graphicsengine.core.Timer;
import org.schema.game.common.data.blockeffects.config.StatusEffectType;
import obfuscated.t;
import org.schema.game.common.controller.ShopSpaceStation;
import org.schema.common.util.StringTools;
import org.schema.game.client.controller.element.world.ClientSegmentProvider;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.element.ElementInformation;
import org.schema.game.common.data.player.inventory.InventorySlot;
import org.schema.schine.common.language.Lng;
import org.schema.schine.graphicsengine.core.settings.EngineSettings;
import org.schema.schine.graphicsengine.core.settings.ContextGroup;
import org.schema.schine.input.InputState;
import org.schema.schine.input.KeyboardMappings;
import org.schema.schine.graphicsengine.core.settings.ContextFilter;
import org.schema.schine.input.InputType;
import java.util.Iterator;
import org.schema.schine.graphicsengine.core.GLFrame;
import java.util.Collection;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.client.data.GameClientState;
import org.schema.game.common.controller.ArmorValue;
import org.schema.common.util.linAlg.Vector3i;

public class HudContextHelpManager
{
	private final GameClientState state;
	private boolean init;
	private int leftStartPosY;
	private int leftEndPosY;
	private int bottomOffsetY;
	private SegmentPiece currentPiece;
	private HudContextHelperContainer tmpHelper;
	private final Object2ObjectOpenHashMap<HudContextHelperContainer, HudContextHelperContainer> helperCache;
	private List<HudContextHelperContainer> queueMouse;
	private List<HudContextHelperContainer> queueLeft;
	private List<HudContextHelperContainer> queueBlock;
	
	public HudContextHelpManager(final GameClientState state) {
		this.bottomOffsetY = 128;
		this.tmpHelper = new HudContextHelperContainer();
		this.helperCache = (Object2ObjectOpenHashMap<HudContextHelperContainer, HudContextHelperContainer>)new Object2ObjectOpenHashMap();
		this.queueMouse = (List<HudContextHelperContainer>)new ObjectArrayList();
		this.queueLeft = (List<HudContextHelperContainer>)new ObjectArrayList();
		this.queueBlock = (List<HudContextHelperContainer>)new ObjectArrayList();
		this.state = state;
	}
	
	public void draw() {
		if (!this.init) {
			this.onInit();
		}
		this.queueMouse.addAll(this.queueBlock);
		this.drawMouse(this.queueMouse);
		this.drawLeft(this.queueLeft);
	}
	
	private void drawLeft(final List<HudContextHelperContainer> list) {
		if (list.isEmpty()) {
			this.leftStartPosY = GLFrame.getHeight();
			this.leftEndPosY = GLFrame.getHeight();
		}
		else {
			this.leftStartPosY = GLFrame.getHeight() - this.bottomOffsetY;
		}
		int leftStartPosY = this.getLeftStartPosY();
		final Iterator<HudContextHelperContainer> iterator = list.iterator();
		while (iterator.hasNext()) {
			final HudContextHelperContainer hudContextHelperContainer;
			(hudContextHelperContainer = iterator.next()).icon.setPos(0.0f, (float)leftStartPosY, 0.0f);
			hudContextHelperContainer.icon.draw();
			leftStartPosY -= (int)hudContextHelperContainer.icon.getHeight();
		}
		this.leftEndPosY = leftStartPosY;
	}
	
	private void drawMouse(final List<HudContextHelperContainer> list) {
		final int n = GLFrame.getWidth() / 2 + 5;
		int n2 = GLFrame.getHeight() / 2;
		for (final HudContextHelperContainer hudContextHelperContainer : list) {
			n2 += (int)hudContextHelperContainer.icon.getHeight();
			hudContextHelperContainer.icon.setPos((float)n, (float)n2, 0.0f);
			hudContextHelperContainer.icon.draw();
		}
	}
	
	public void addBlock(final SegmentPiece p2, final Object o) {
		this.addHelper(InputType.BLOCK, this.queueBlock.size(), o, HudContextHelperContainer.Hos.BLOCK, ContextFilter.CRUCIAL).p = p2;
	}
	
	public void addInfo(final HudContextHelperContainer.Hos hos, final ContextFilter contextFilter, final Object o) {
		this.addHelper(InputType.BLOCK, this.queueBlock.size(), o, hos, contextFilter);
	}
	
	public void addHelper(final KeyboardMappings keyboardMappings, final String s, final HudContextHelperContainer.Hos hos, final ContextFilter contextFilter) {
		this.addHelper(InputType.KEYBOARD, keyboardMappings.getMapping(), s, hos, contextFilter);
	}
	
	public HudContextHelperContainer addHelper(final InputType inputType, final int n, final Object obj, final HudContextHelperContainer.Hos hos, final ContextFilter contextFilter) {
		try {
			this.tmpHelper.set(inputType, n, obj, hos, contextFilter);
			if (!this.helperCache.containsKey(this.tmpHelper)) {
				final HudContextHelperContainer hudContextHelperContainer;
				(hudContextHelperContainer = new HudContextHelperContainer(inputType, n, obj, hos, contextFilter)).create(this.getState());
				this.helperCache.put(hudContextHelperContainer, hudContextHelperContainer);
			}
			final HudContextHelperContainer hudContextHelperContainer2 = (HudContextHelperContainer)this.helperCache.get((Object)this.tmpHelper);
			if (((ContextGroup)EngineSettings.G_ICON_CONTEXT_FILTER.getCurrentState()).containsFilter(contextFilter)) {
				if (hos == HudContextHelperContainer.Hos.MOUSE) {
					this.queueMouse.add(hudContextHelperContainer2);
				}
				else if (hos == HudContextHelperContainer.Hos.BLOCK) {
					this.queueBlock.add(hudContextHelperContainer2);
				}
				else {
					this.queueLeft.add(hudContextHelperContainer2);
				}
			}
			return hudContextHelperContainer2;
		}
		catch (Exception ex) {
			System.err.println("Exception: HELPER FAILED WITH TEXT: " + obj);
			ex.printStackTrace();
			return new HudContextHelperContainer(inputType, n, "ERROR IN TRANSLATION", hos, contextFilter);
		}
	}
	
	private void addBuildAndTake() {
		if (this.currentPiece != null) {
			final InventorySlot slot;
			if ((slot = this.state.getPlayer().getInventory().getSlot(this.state.getPlayer().getSelectedBuildSlot())) == null) {
				this.addHelper(InputType.MOUSE, EngineSettings.C_MOUSE_BUTTON_SWITCH.isOn() ? 0 : 1, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_3, HudContextHelperContainer.Hos.MOUSE, ContextFilter.IMPORTANT);
				return;
			}
			if (slot.isMultiSlot() || !slot.isMetaItem()) {
				this.addHelper(InputType.MOUSE, EngineSettings.C_MOUSE_BUTTON_SWITCH.isOn() ? 1 : 0, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_1, HudContextHelperContainer.Hos.MOUSE, ContextFilter.IMPORTANT);
				this.addHelper(InputType.MOUSE, EngineSettings.C_MOUSE_BUTTON_SWITCH.isOn() ? 0 : 1, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_0, HudContextHelperContainer.Hos.MOUSE, ContextFilter.IMPORTANT);
			}
		}
	}
	
	private void addAstronautOptions() {
		this.addHelper(KeyboardMappings.SPAWN_SHIP, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_23, HudContextHelperContainer.Hos.LEFT, ContextFilter.NORMAL);
		this.addHelper(KeyboardMappings.SPAWN_SPACE_STATION, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_25, HudContextHelperContainer.Hos.LEFT, ContextFilter.NORMAL);
		if (this.currentPiece != null) {
			this.addHelper(KeyboardMappings.SIT_ASTRONAUT, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_26, HudContextHelperContainer.Hos.LEFT, ContextFilter.TRIVIAL);
		}
		this.addBuildAndTake();
		if (this.currentPiece != null && this.state.getCharacter() != null && (this.state.getCharacter().getGravity() == null || this.state.getCharacter().getGravity().source != this.currentPiece.getSegmentController())) {
			this.addHelper(KeyboardMappings.GRAPPLING_HOOK, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_40, HudContextHelperContainer.Hos.MOUSE, ContextFilter.TRIVIAL);
		}
		this.addBlockOption();
		final InventorySlot slot;
		if ((slot = this.state.getPlayer().getInventory().getSlot(this.state.getPlayer().getSelectedBuildSlot())) != null) {
			slot.isMetaItem();
		}
	}
	
	private boolean isSelectable(final ElementInformation elementInformation) {
		return ElementKeyMap.getFactorykeyset().contains(elementInformation.getId()) || elementInformation.getId() == 120 || elementInformation.getId() == 677 || elementInformation.getControlling().size() > 0;
	}
	
	private void addBlockOption() {
		if (this.currentPiece == null || !ElementKeyMap.isValidType(this.currentPiece.getType())) {
			return;
		}
		final ElementInformation infoFast = ElementKeyMap.getInfoFast(this.currentPiece.getType());
		final InputType keyboard = InputType.KEYBOARD;
		final HudContextHelperContainer.Hos mouse = HudContextHelperContainer.Hos.MOUSE;
		final SegmentPiece selectedBlockByActiveController = this.getPlayerInteractionManager().getSelectedBlockByActiveController();
		if (infoFast.getId() == 670) {
			final long absoluteIndexWithType4 = this.currentPiece.getAbsoluteIndexWithType4();
			String s;
			if ((s = (String)this.currentPiece.getSegmentController().getTextMap().get(absoluteIndexWithType4)) == null) {
				((ClientSegmentProvider)this.currentPiece.getSegmentController().getSegmentProvider()).getSendableSegmentProvider().clientTextBlockRequest(absoluteIndexWithType4);
				s = "";
			}
			this.addHelper(KeyboardMappings.ACTIVATE, StringTools.format(Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_27, s), mouse, ContextFilter.CRUCIAL);
		}
		else if (infoFast.getId() == 347) {
			this.addHelper(KeyboardMappings.ACTIVATE, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_28, mouse, ContextFilter.CRUCIAL);
		}
		else if (infoFast.getId() == 683) {
			this.addHelper(KeyboardMappings.ACTIVATE, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_29, mouse, ContextFilter.CRUCIAL);
		}
		else if (infoFast.getId() == 8) {
			this.addHelper(KeyboardMappings.ACTIVATE, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_30, mouse, ContextFilter.CRUCIAL);
		}
		else if (this.currentPiece != null && this.currentPiece.getSegmentController() instanceof ShopSpaceStation && this.currentPiece.equalsPos(new Vector3i(17, 10, 16))) {
			this.addHelper(KeyboardMappings.ACTIVATE, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_2, mouse, ContextFilter.CRUCIAL);
		}
		else if (infoFast.isEnterable() && !this.getPlayerInteractionManager().isInAnyStructureBuildMode()) {
			this.addHelper(KeyboardMappings.ENTER_SHIP, StringTools.format(Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_4, infoFast.getName()), mouse, ContextFilter.CRUCIAL);
		}
		else if (infoFast.isSignal()) {
			this.addHelper(KeyboardMappings.ACTIVATE, StringTools.format(Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_7, this.currentPiece.isActive() ? Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_5 : Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_6), mouse, ContextFilter.CRUCIAL);
		}
		else if (ElementInformation.isMedical(infoFast.getId())) {
			this.addHelper(KeyboardMappings.ACTIVATE, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_8, mouse, ContextFilter.CRUCIAL);
		}
		else if (infoFast.isInventory()) {
			this.addHelper(KeyboardMappings.ACTIVATE, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_14, mouse, ContextFilter.CRUCIAL);
		}
		else if (infoFast.getId() == 542) {
			if (this.currentPiece.getSegmentController().getConfigManager().apply(StatusEffectType.WARP_FREE_TARGET, false)) {
				this.addHelper(KeyboardMappings.ACTIVATE, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_59, mouse, ContextFilter.CRUCIAL);
			}
		}
		else if (infoFast.canActivate() && infoFast.getControlledBy().size() > 0 && !infoFast.getControlledBy().contains((short)1)) {
			if (this.currentPiece.isActive()) {
				this.addHelper(KeyboardMappings.ACTIVATE, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_42, mouse, ContextFilter.CRUCIAL);
			}
			else {
				this.addHelper(KeyboardMappings.ACTIVATE, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_15, mouse, ContextFilter.CRUCIAL);
			}
		}
		else if (infoFast.getId() == 978 || infoFast.getId() == 2 || infoFast.getId() == 331 || infoFast.getId() == 3 || infoFast.getId() == 478) {
			this.addHelper(KeyboardMappings.ACTIVATE, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_16, mouse, ContextFilter.CRUCIAL);
		}
		else if (infoFast.canActivate()) {
			this.addHelper(KeyboardMappings.ACTIVATE, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_43, mouse, ContextFilter.CRUCIAL);
		}
		if (this.isSelectable(infoFast)) {
			if (selectedBlockByActiveController != null) {
				if (!selectedBlockByActiveController.equals(this.currentPiece)) {
					this.addHelper(KeyboardMappings.SELECT_MODULE, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_11, mouse, ContextFilter.CRUCIAL);
				}
				else {
					this.addHelper(KeyboardMappings.SELECT_MODULE, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_10, mouse, ContextFilter.CRUCIAL);
				}
			}
			else {
				this.addHelper(KeyboardMappings.SELECT_MODULE, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_9, mouse, ContextFilter.CRUCIAL);
			}
		}
		if (selectedBlockByActiveController != null && !selectedBlockByActiveController.equals(this.currentPiece) && ElementKeyMap.isValidType(selectedBlockByActiveController.getType()) && ElementInformation.canBeControlled(selectedBlockByActiveController.getType(), this.currentPiece.getType())) {
			if (selectedBlockByActiveController.getSegmentController().getControlElementMap().isControlling(selectedBlockByActiveController.getAbsoluteIndex(), this.currentPiece.getAbsoluteIndex(), this.currentPiece.getType())) {
				this.addHelper(KeyboardMappings.CONNECT_MODULE, StringTools.format(Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_12, ElementKeyMap.getInfo(selectedBlockByActiveController.getType()).getName()), mouse, ContextFilter.NORMAL);
				return;
			}
			final short type = selectedBlockByActiveController.getType();
			final short type2 = this.currentPiece.getType();
			if (ElementKeyMap.getInfoFast(type).isLightConnect(type2)) {
				this.addHelper(KeyboardMappings.CONNECT_MODULE, StringTools.format(Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_13, ElementKeyMap.getInfo(this.currentPiece.getType()).getName(), ElementKeyMap.getInfo(selectedBlockByActiveController.getType()).getName()), mouse, ContextFilter.CRUCIAL);
				return;
			}
			if ((ElementKeyMap.getInfoFast(type).isMainCombinationControllerB() && ElementKeyMap.getInfoFast(type2).isMainCombinationControllerB()) || (ElementKeyMap.getInfoFast(type).isSupportCombinationControllerB() && ElementKeyMap.getInfoFast(type2).isMainCombinationControllerB())) {
				this.addHelper(KeyboardMappings.CONNECT_MODULE, StringTools.format(Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_35, ElementKeyMap.getInfo(selectedBlockByActiveController.getType()).getName()), mouse, ContextFilter.CRUCIAL);
				return;
			}
			this.addHelper(KeyboardMappings.CONNECT_MODULE, StringTools.format(Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_36, ElementKeyMap.getInfo(selectedBlockByActiveController.getType()).getName()), mouse, ContextFilter.CRUCIAL);
		}
	}
	
	private void addFlightMode(final Timer timer) {
		this.addHelper(KeyboardMappings.SELECT_LOOK_ENTITY, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_64, HudContextHelperContainer.Hos.LEFT, ContextFilter.NORMAL);
		this.addHelper(KeyboardMappings.CHANGE_SHIP_MODE, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_17, HudContextHelperContainer.Hos.LEFT, ContextFilter.NORMAL);
		this.addHelper(KeyboardMappings.ENTER_SHIP, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_18, HudContextHelperContainer.Hos.LEFT, ContextFilter.NORMAL);
		this.addHelper(KeyboardMappings.BRAKE, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_19, HudContextHelperContainer.Hos.LEFT, ContextFilter.NORMAL);
		this.addHelper(KeyboardMappings.ALIGN_SHIP, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_37, HudContextHelperContainer.Hos.LEFT, ContextFilter.NORMAL);
		if (!this.getState().getPlayer().getCockpit().isCore()) {
			this.addHelper(KeyboardMappings.ADJUST_COCKPIT, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_71, HudContextHelperContainer.Hos.LEFT, ContextFilter.NORMAL);
			this.addHelper(KeyboardMappings.ADJUST_COCKPIT_RESET, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_72, HudContextHelperContainer.Hos.LEFT, ContextFilter.NORMAL);
		}
		if (this.getState().getPlayer().getCockpit().isInCockpitAdjustment()) {
			this.addHelper(KeyboardMappings.ADJUST_COCKPIT, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_73, HudContextHelperContainer.Hos.LEFT, ContextFilter.NORMAL);
			this.addHelper(KeyboardMappings.ADJUST_COCKPIT_RESET, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_74, HudContextHelperContainer.Hos.LEFT, ContextFilter.NORMAL);
		}
		if (this.getPlayerInteractionManager().getSelectedEntity() != null) {
			this.addHelper(KeyboardMappings.PIN_AI_TARGET, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_68, HudContextHelperContainer.Hos.LEFT, ContextFilter.NORMAL);
		}
		if (this.getPlayerInteractionManager().getSelectedAITarget() != null) {
			this.addInfo(HudContextHelperContainer.Hos.LEFT, ContextFilter.CRUCIAL, StringTools.format(Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_69, this.getPlayerInteractionManager().getSelectedAITarget().getName()));
		}
		final Ship ship;
		if ((ship = this.getState().getShip()) != null) {
			final Iterator<ControllerStateUnit> iterator = this.getState().getPlayer().getControllerState().getUnits().iterator();
			while (iterator.hasNext()) {
				final ControllerStateUnit controllerStateUnit;
				if ((controllerStateUnit = iterator.next()).playerControllable == ship) {
					ship.getManagerContainer().addHudConext(controllerStateUnit, this, timer);
				}
			}
		}
	}
	
	public PlayerInteractionControlManager getPlayerInteractionManager() {
		return this.state.getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager();
	}
	
	private void addBuildModeOptions() {
		if (this.getPlayerInteractionManager().getSegmentControlManager().getSegmentController() == null) {
			return;
		}
		if (this.getPlayerInteractionManager().getBuildCommandManager().getCurrent() != null) {
			String[] split;
			for (int length = (split = this.getPlayerInteractionManager().getBuildCommandManager().getCurrent().getInstruction().split("\n")).length, i = 0; i < length; ++i) {
				this.addInfo(HudContextHelperContainer.Hos.MOUSE, ContextFilter.NORMAL, split[i]);
			}
			this.addInfo(HudContextHelperContainer.Hos.MOUSE, ContextFilter.NORMAL, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_70);
		}
		final EditableSendableSegmentController segmentController = this.getPlayerInteractionManager().getSegmentControlManager().getSegmentController();
		final short selectedTypeWithSub = this.getPlayerInteractionManager().getSelectedTypeWithSub();
		//#XXX: new armor counter
		if (this.currentPiece != null && ElementKeyMap.isValidType(selectedTypeWithSub) && ElementKeyMap.getInfo(selectedTypeWithSub).isArmor() && BuildModeDrawer.armorValue > 0.0f) {
		//#XXX:
			this.addInfo(HudContextHelperContainer.Hos.MOUSE, ContextFilter.CRUCIAL, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_63);
			this.addInfo(HudContextHelperContainer.Hos.MOUSE, ContextFilter.CRUCIAL, StringTools.format(Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_65, StringTools.formatSeperated((int)this.currentPiece.getInfo().getArmorValue())));
			//#XXX: new armor counter
			this.addInfo(HudContextHelperContainer.Hos.MOUSE, ContextFilter.CRUCIAL, StringTools.format(Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_66, ArmorValue.lastSize));
			this.addInfo(HudContextHelperContainer.Hos.MOUSE, ContextFilter.CRUCIAL, StringTools.format(Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_67, StringTools.formatSeperated((int)BuildModeDrawer.armorValue)));
			//#XXX:
		}
		if (segmentController.getHpController().getHpPercent() < 1.0) {
			this.addHelper(KeyboardMappings.REBOOT_SYSTEMS, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_38, HudContextHelperContainer.Hos.LEFT, ContextFilter.TRIVIAL);
		}
		if (segmentController instanceof Ship) {
			this.addHelper(KeyboardMappings.CHANGE_SHIP_MODE, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_20, HudContextHelperContainer.Hos.LEFT, ContextFilter.NORMAL);
		}
		if (!this.getPlayerInteractionManager().isAdvancedBuildMode()) {
			this.addHelper(KeyboardMappings.BUILD_MODE_FAST_MOVEMENT, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_39, HudContextHelperContainer.Hos.LEFT, ContextFilter.NORMAL);
			this.addHelper(KeyboardMappings.BUILD_MODE_FIX_CAM, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_21, HudContextHelperContainer.Hos.LEFT, ContextFilter.NORMAL);
		}
		this.addBuildAndTake();
		if (segmentController.isUsingPowerReactors() && this.getPlayerInteractionManager().isInAnyStructureBuildMode()) {
			this.addReactorInfo(this.getPlayerInteractionManager().getSelectedTypeWithSub());
		}
		this.addBlockOption();
	}
	
	public void onInit() {
		if (this.init) {
			return;
		}
		this.init = true;
	}
	
	private void addReactorInfo(final short n) {
		final ManagerContainer managerContainer;
		final PowerInterface powerInterface = (managerContainer = ((ManagedSegmentController)this.getPlayerInteractionManager().getSegmentControlManager().getSegmentController()).getManagerContainer()).getPowerInterface();
		if (ElementKeyMap.isReactor(n)) {
			this.addInfo(HudContextHelperContainer.Hos.MOUSE, ContextFilter.CRUCIAL, StringTools.format(Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_44, StringTools.formatPointZero(powerInterface.getRechargeRatePowerPerSec())));
			this.addInfo(HudContextHelperContainer.Hos.MOUSE, ContextFilter.CRUCIAL, StringTools.format(Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_41, StringTools.formatPointZero(powerInterface.getStabilizerEfficiencyTotal() * 100.0)));
		}
		if (VoidElementManager.isUsingReactorDistance() && n == 1009 && this.currentPiece != null) {
			this.addInfo(HudContextHelperContainer.Hos.MOUSE, ContextFilter.CRUCIAL, StringTools.format(Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_45, StringTools.formatPointZero(BuildModeDrawer.currentStabEfficiency * 100.0), StringTools.formatPointZero(BuildModeDrawer.currentStabDist), StringTools.formatPointZero(BuildModeDrawer.currentOptStabDist)));
		}
		if (this.currentPiece != null && managerContainer.isUsingPowerReactors() && managerContainer instanceof ShieldContainerInterface) {
			final ShieldLocalAddOn shieldLocalAddOn = ((ShieldContainerInterface)managerContainer).getShieldAddOn().getShieldLocalAddOn();
			final ShieldLocal containingShield;
			if ((this.currentPiece.getType() == 478 || this.currentPiece.getType() == 3) && (containingShield = shieldLocalAddOn.getContainingShield((ShieldContainerInterface)managerContainer, this.currentPiece.getAbsoluteIndex())) != null) {
				this.addInfo(HudContextHelperContainer.Hos.MOUSE, ContextFilter.CRUCIAL, StringTools.format(Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_54, StringTools.formatSeperated(containingShield.getShieldCapacity()), StringTools.formatSeperated(containingShield.rechargePerSecond - containingShield.getShieldUpkeep())));
				if (this.currentPiece.getType() == 478) {
					this.addInfo(HudContextHelperContainer.Hos.MOUSE, ContextFilter.CRUCIAL, StringTools.format(Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_52, ElementCollection.getPosX(containingShield.outputPos) + ", " + ElementCollection.getPosY(containingShield.outputPos) + ", " + ElementCollection.getPosZ(containingShield.outputPos), StringTools.formatPointZero(containingShield.radius)));
				}
				else if (this.currentPiece.getType() == 3) {
					this.addInfo(HudContextHelperContainer.Hos.MOUSE, ContextFilter.CRUCIAL, StringTools.format(Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_53, containingShield.getPosString()));
				}
				if (containingShield.rechargePerSecond < containingShield.getShieldUpkeep()) {
					this.addInfo(HudContextHelperContainer.Hos.MOUSE, ContextFilter.CRUCIAL, StringTools.format(Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_60, StringTools.formatPointZero(containingShield.getShieldUpkeep() - containingShield.rechargePerSecond)));
				}
			}
		}
		final ManagerModuleCollection<?, ?, ?> value;
		final ControlBlockElementCollectionManager controlBlockElementCollectionManager;
		if (this.currentPiece != null && (value = managerContainer.getModulesControllerMap().get(this.currentPiece.getType())) != null && value.getElementManager() instanceof UsableCombinableControllableElementManager && (controlBlockElementCollectionManager = (ControlBlockElementCollectionManager)value.getElementManager().getCollectionManagersMap().get(this.currentPiece.getAbsoluteIndex())) != null && controlBlockElementCollectionManager instanceof DamageDealer) {
			final InterEffectSet attackEffectSet = ((DamageDealer)controlBlockElementCollectionManager).getAttackEffectSet();
			for (int i = 0; i < InterEffectSet.length; ++i) {
				final InterEffectHandler.InterEffectType interEffectType = InterEffectHandler.InterEffectType.values()[i];
				this.addInfo(HudContextHelperContainer.Hos.MOUSE, ContextFilter.CRUCIAL, interEffectType.shortName.getName(interEffectType) + ": " + Math.round(attackEffectSet.getStrength(interEffectType) / 3.0f * 100.0f) + "%");
			}
		}
		if (this.currentPiece != null && ElementKeyMap.isReactor(this.currentPiece.getType())) {
			final Iterator<StabilizerUnit> iterator = powerInterface.getStabilizerCollectionManager().getElementCollections().iterator();
			while (iterator.hasNext()) {
				final StabilizerUnit stabilizerUnit;
				if ((stabilizerUnit = iterator.next()).contains(this.currentPiece.getAbsoluteIndex())) {
					if (VoidElementManager.STABILIZER_BONUS_CALC == StabBonusCalcStyle.BY_ANGLE) {
						this.addInfo(HudContextHelperContainer.Hos.MOUSE, ContextFilter.CRUCIAL, StringTools.format(Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_75, Math.round(stabilizerUnit.smallestAngle * 57.295776f) + "\u00c2°", StringTools.formatPointZero(stabilizerUnit.getStabilization()), StringTools.formatPointZero(stabilizerUnit.getBonusEfficiency())));
						if (stabilizerUnit.isBonusSlot()) {
							continue;
						}
						this.addInfo(HudContextHelperContainer.Hos.MOUSE, ContextFilter.CRUCIAL, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_76);
					}
					else {
						this.addInfo(HudContextHelperContainer.Hos.MOUSE, ContextFilter.CRUCIAL, StringTools.format(Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_61, Element.getSideString(stabilizerUnit.getReactorSide()), StringTools.formatPointZero(stabilizerUnit.getStabilization()), StringTools.formatPointZero(stabilizerUnit.getBonusEfficiency() * 100.0)));
						if (stabilizerUnit.isBonusSlot()) {
							continue;
						}
						this.addInfo(HudContextHelperContainer.Hos.MOUSE, ContextFilter.CRUCIAL, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_62);
					}
				}
			}
			final Iterator<MainReactorUnit> iterator2 = powerInterface.getMainReactors().iterator();
			while (iterator2.hasNext()) {
				final MainReactorUnit mainReactorUnit;
				if ((mainReactorUnit = iterator2.next()).contains(this.currentPiece.getAbsoluteIndex())) {
					this.addInfo(HudContextHelperContainer.Hos.MOUSE, ContextFilter.CRUCIAL, StringTools.format(Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_46, this.currentPiece.getInfo().getName(), mainReactorUnit.getNeighboringCollection().size()));
					this.addInfo(HudContextHelperContainer.Hos.MOUSE, ContextFilter.CRUCIAL, StringTools.format(Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_57, StringTools.formatPointZero(powerInterface.getChamberCapacity() * 100.0f), "100.0"));
				}
			}
			if (((PowerImplementation)powerInterface).getActiveReactor() != null) {
				final Iterator<ManagerModuleSingle<ReactorChamberUnit, ReactorChamberCollectionManager, ReactorChamberElementManager>> iterator3 = powerInterface.getChambers().iterator();
				while (iterator3.hasNext()) {
					final Iterator<ReactorChamberUnit> iterator4 = iterator3.next().getCollectionManager().getElementCollections().iterator();
					while (iterator4.hasNext()) {
						final ReactorChamberUnit reactorChamberUnit;
						if ((reactorChamberUnit = iterator4.next()).contains(this.currentPiece.getAbsoluteIndex())) {
							final String name = this.currentPiece.getInfo().getName();
							final int minChamberSize = ((PowerImplementation)powerInterface).getActiveReactor().getMinChamberSize();
							final int size;
							this.addInfo(HudContextHelperContainer.Hos.MOUSE, ContextFilter.CRUCIAL, StringTools.format(Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_49, name, ((size = reactorChamberUnit.getNeighboringCollection().size()) >= minChamberSize) ? StringTools.format(Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_47, StringTools.formatSmallAndBig(size)) : StringTools.format(Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_48, StringTools.formatSmallAndBig(minChamberSize - size), StringTools.formatSmallAndBig(size))));
						}
					}
				}
			}
			if (this.currentPiece.getType() == 1010) {
				final Set<ReactorChamberUnit> connectedChambersToConduit;
				String s;
				if ((connectedChambersToConduit = powerInterface.getConnectedChambersToConduit(this.currentPiece.getAbsoluteIndex())) != null) {
					s = Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_50;
					final Iterator<ReactorChamberUnit> iterator5 = connectedChambersToConduit.iterator();
					while (iterator5.hasNext()) {
						final SegmentPiece elementCollectionId;
						(elementCollectionId = iterator5.next().getElementCollectionId()).refresh();
						if (elementCollectionId.isValid()) {
							s += elementCollectionId.getInfo().getName();
						}
						if (iterator5.hasNext()) {
							s += " -> ";
						}
					}
				}
				else {
					s = Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_51;
				}
				this.addInfo(HudContextHelperContainer.Hos.MOUSE, ContextFilter.CRUCIAL, s);
			}
		}
	}
	
	private void addSelectedStats(final SegmentPiece segmentPiece) {
		String s = null;
		if (segmentPiece != null && segmentPiece == this.currentPiece && ElementKeyMap.isValidType(segmentPiece.getType())) {
			final ElementInformation infoFast;
			if ((infoFast = ElementKeyMap.getInfoFast(segmentPiece.getType())).isSignal()) {
				s = Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_31;
			}
			else if (infoFast.isRailTrack()) {
				s = Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_32;
			}
			if (s != null) {
				final FastCopyLongOpenHashSet set;
				this.addBlock(segmentPiece, StringTools.format(Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_34, s, ((set = (FastCopyLongOpenHashSet)segmentPiece.getSegmentController().getControlElementMap().getControllingMap().getAll().get(segmentPiece.getAbsoluteIndex())) != null) ? set.size() : 0));
			}
			if (s == null && infoFast.getControlling().size() > 0) {
				final Iterator<Short> iterator = infoFast.getControlling().iterator();
				while (iterator.hasNext()) {
					final short shortValue;
					if (ElementKeyMap.isValidType(shortValue = iterator.next())) {
						s = ElementKeyMap.getInfoFast(shortValue).getName();
					}
					final Short2ObjectOpenHashMap<FastCopyLongOpenHashSet> value;
					if (s != null && (value = segmentPiece.getSegmentController().getControlElementMap().getControllingMap().get(segmentPiece.getAbsoluteIndex())) != null) {
						final FastCopyLongOpenHashSet set2;
						this.addBlock(segmentPiece, StringTools.format(Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_33, s, ((set2 = (FastCopyLongOpenHashSet)value.get(shortValue)) != null) ? set2.size() : 0));
					}
				}
			}
		}
	}
	
	public void update(final Timer timer) {
		this.queueMouse.clear();
		this.queueLeft.clear();
		this.queueBlock.clear();
		this.addHelper(KeyboardMappings.RADIAL_MENU, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_22, HudContextHelperContainer.Hos.LEFT, ContextFilter.IMPORTANT);
		this.addHelper(KeyboardMappings.MAP_PANEL, Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_GUI_SHIPHUD_NEWHUD_HUDCONTEXTHELPMANAGER_24, HudContextHelperContainer.Hos.LEFT, ContextFilter.IMPORTANT);
		this.currentPiece = BuildModeDrawer.currentPiece;
		final PlayerInteractionControlManager playerInteractionManager;
		if ((playerInteractionManager = this.getPlayerInteractionManager()).isInAnyBuildMode()) {
			this.addSelectedStats(playerInteractionManager.getSelectedBlockByActiveController());
		}
		if (playerInteractionManager.isInAnyStructureBuildMode()) {
			this.addBuildModeOptions();
			return;
		}
		if (playerInteractionManager.getInShipControlManager().getShipControlManager().getShipExternalFlightController().isTreeActive()) {
			this.addFlightMode(timer);
			return;
		}
		if (playerInteractionManager.isInAnyCharacterBuildMode()) {
			this.addAstronautOptions();
		}
	}
	
	public GameClientState getState() {
		return this.state;
	}
	
	private void setLeftStartPosY(final int leftStartPosY) {
		this.leftStartPosY = leftStartPosY;
	}
	
	public int getLeftStartPosY() {
		return this.leftStartPosY;
	}
	
	private void setLeftEndPosY(final int leftEndPosY) {
		this.leftEndPosY = leftEndPosY;
	}
	
	public int getLeftEndPosY() {
		return this.leftEndPosY;
	}
}
