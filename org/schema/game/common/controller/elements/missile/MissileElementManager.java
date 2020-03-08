// 
// Decompiled by Procyon v0.5.36
// 

package org.schema.game.common.controller.elements.missile;

import org.schema.common.FastMath;
import org.schema.game.common.controller.elements.missile.capacity.MissileCapacityElementManager;
import org.schema.game.common.controller.elements.UsableControllableElementManager;
import org.schema.schine.graphicsengine.forms.font.FontLibrary;
import javax.vecmath.Vector4f;
import org.schema.schine.input.InputState;
import org.schema.schine.graphicsengine.forms.gui.GUITextOverlay;
import org.schema.game.common.controller.elements.combination.CombinationSettings;
import org.schema.game.common.controller.elements.missile.dumb.DumbMissileElementManager;
import org.schema.game.common.controller.elements.effectblock.EffectCollectionManager;
import org.schema.game.common.controller.elements.FocusableUsableModule;
import org.schema.game.common.controller.elements.UsableControllableFireingElementManager;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.Ship;
import org.schema.game.common.controller.elements.ElementCollectionManager;
import java.io.IOException;
import org.schema.game.common.data.player.ControllerStateInterface;
import org.schema.game.client.view.gui.structurecontrol.ModuleValueEntry;
import org.schema.common.util.StringTools;
import org.schema.game.client.view.gui.structurecontrol.GUIKeyValueEntry;
import org.schema.schine.common.language.Lng;
import org.schema.game.client.data.GameClientState;
import org.schema.game.client.view.gui.structurecontrol.ControllerManagerGUI;
import org.schema.game.server.controller.GameServerController;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import java.util.Iterator;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.ManagedSegmentController;
import org.schema.game.common.controller.elements.ControlBlockElementCollectionManager;
import org.schema.game.common.controller.elements.ManagerModuleCollection;
import javax.vecmath.Vector3f;
import javax.vecmath.Tuple3f;
import com.bulletphysics.linearmath.Transform;
import org.schema.game.common.controller.elements.ShootingRespose;
import org.schema.game.common.controller.elements.combination.Combinable;
import org.schema.game.common.controller.elements.combination.CombinationAddOn;
import org.schema.schine.graphicsengine.core.Timer;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;
import org.schema.game.common.controller.elements.UsableElementManager;
import org.schema.game.common.controller.elements.combination.MissileCombinationAddOn;
import org.schema.game.common.controller.elements.missile.dumb.DumbMissileUnit;
import org.schema.game.common.controller.elements.combination.modifier.MissileUnitModifier;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.element.ShootContainer;
import org.schema.game.common.controller.elements.WeaponStatisticsData;
import org.schema.game.common.controller.elements.WeaponElementManagerInterface;
import org.schema.game.common.controller.elements.IntegrityBasedInterface;
import org.schema.game.common.controller.elements.BlockActivationListenerInterface;
import org.schema.game.common.controller.elements.combination.MissileCombiSettings;
import org.schema.game.common.controller.elements.UsableCombinableControllableElementManager;
import org.schema.game.common.controller.elements.missile.dumb.DumbMissileCollectionManager;

public abstract class MissileElementManager<E extends MissileUnit<E, EC, EM>, EC extends MissileCollectionManager<E, EC, EM>, EM extends MissileElementManager<E, EC, EM>> extends UsableCombinableControllableElementManager<E, EC, EM, MissileCombiSettings> implements BlockActivationListenerInterface, IntegrityBasedInterface, WeaponElementManagerInterface
{
	private static boolean debug;
	private final short controlling;
	private final WeaponStatisticsData tmpOutput;
	private final ShootContainer shootContainer;
	private final MissileDrawReloadListener reloadListener;
	private final MissileCombiSettings combiSettings;
	
	public MissileElementManager(final short n, final short controlling, final SegmentController segmentController) {
		super(n, controlling, segmentController);
		this.tmpOutput = new WeaponStatisticsData();
		this.shootContainer = new ShootContainer();
		this.reloadListener = new MissileDrawReloadListener();
		this.combiSettings = new MissileCombiSettings();
		this.controlling = controlling;
	}
	
	@Override
	public MissileCombiSettings getCombiSettings() {
		return this.combiSettings;
	}
	
	protected int getMissileMode(final EC ec) {
		//#XXX: lock on as base missile
		//this just makes isTargetLocking true for default missiles
		int n = 2;
		//#XXX:
		if (this.getAddOn() != null && ec.getSlaveConnectedElement() != Long.MIN_VALUE) {
			switch ((short)ElementCollection.getType(ec.getSlaveConnectedElement())) {
				case 6: {
					n = (int)MissileCombinationAddOn.missileCannonUnitModifier.get(this).modeModifier.getOuput(0.0f);
					break;
				}
				case 414: {
					n = (int)MissileCombinationAddOn.missileBeamUnitModifier.get(this).modeModifier.getOuput(0.0f);
					break;
				}
				case 38: {
					n = (int)MissileCombinationAddOn.missileMissileUnitModifier.get(this).modeModifier.getOuput(0.0f);
					break;
				}
			}
		}
		return n;
	}
	
	public void doShot(final E e, final EC ec, final ShootContainer shootContainer, final float n, final SimpleTransformableSendableObject simpleTransformableSendableObject, final PlayerState playerState, final Timer timer) {
		ManagerModuleCollection<?, ?, ?> collection = null;
		if (ec.getEffectConnectedElement() != Long.MIN_VALUE) {
			collection = this.getManagerContainer().getModulesControllerMap().get((short)ElementCollection.getType(ec.getEffectConnectedElement()));
		}
		if (ec.getEffectConnectedElement() != Long.MIN_VALUE) {
			collection = this.getManagerContainer().getModulesControllerMap().get((short)ElementCollection.getType(ec.getEffectConnectedElement()));
			final ControlBlockElementCollectionManager<?, ?, ?> effect;
			if ((effect = CombinationAddOn.getEffect(ec.getEffectConnectedElement(), collection, this.getSegmentController())) != null) {
				ec.setEffectTotal(effect.getTotalSize());
			}
		}
		//System.err.println("#XXX: missile doShot");
		//System.out.println("#XXX: segmentController.getAttackEffectSet: ", ec.getSegmentController().getAttackEffectSet());
		//System.err.println("#XXX: isTargetLocking: " + this.isTargetLocking(ec));
		//#XXX: effect relink fix
		//so currently when an entity is loaded, weapons with an effect computer
		//linked only use their basic effect spread, and if you shoot them you'll
		//only get basic effect damage. you can fix this by just unlinking the
		//computer and linking it back up again; the correct values are all there,
		//the game just never put them together. rather than digging into the entity
		//loader to fix this at the source, which sounds very scary and difficult, i
		//just made it recalculate here since it's not an expensive calculation and
		//we're already recalculating effect and slave size anyway.
		//missiles are very confusing and i had a hard time finding the right
		//collection manager, but this is the right one and as far as i know it's
		//the only concrete subclass of MissileCollectionManager so this should
		//always be true? idk man, missiles are super weird.
		if(ec instanceof DumbMissileCollectionManager) {
			((DumbMissileCollectionManager)ec).updateAttackEffectSet();
		}
		//#XXX:
		if (this.getAddOn() != null && ec.getSlaveConnectedElement() != Long.MIN_VALUE) {
			this.handleResponse(this.handleAddOn(this, ec, e, this.getManagerContainer().getModulesControllerMap().get((short)ElementCollection.getType(ec.getSlaveConnectedElement())), collection, shootContainer, simpleTransformableSendableObject, playerState, timer, -1.0f), (E)e, shootContainer.weapontOutputWorldPos);
			return;
		}
		if (!e.canUse(timer.currentTime, true)) {
			this.handleResponse(ShootingRespose.RELOADING, (E)e, shootContainer.weapontOutputWorldPos);
			return;
		}
		final float powerConsumption = e.getPowerConsumption();
		if (this.isUsingPowerReactors() || this.consumePower(powerConsumption)) {
			final Transform transform;
			(transform = new Transform()).setIdentity();
			transform.origin.set((Tuple3f)shootContainer.weapontOutputWorldPos);
			e.setStandardShotReloading();
			final long lightConnectedElement = ec.getLightConnectedElement();
			short n2 = 0;
			if (lightConnectedElement != Long.MIN_VALUE) {
				n2 = (short)ElementCollection.getType(lightConnectedElement);
			}
			if (this.getSegmentController().isOnServer()) {
				assert shootContainer.shootingDirTemp.lengthSquared() != 0.0f;
				this.addMissile(this.getSegmentController(), transform, new Vector3f(shootContainer.shootingDirTemp), n, e.getDamage(), e.getDistance(), ec.getUsableId(), simpleTransformableSendableObject, n2);
			}
			this.handleResponse(ShootingRespose.FIRED, (E)e, shootContainer.weapontOutputWorldPos);
			return;
		}
		this.handleResponse(ShootingRespose.NO_POWER, (E)e, shootContainer.weapontOutputWorldPos);
	}
	
	public boolean checkMissileCapacity() {
		final SegmentController root;
		return this.getSegmentController() instanceof SegmentController && (root = this.getSegmentController().railController.getRoot()) instanceof ManagedSegmentController && ((ManagedSegmentController)root).getManagerContainer().getMissileCapacity() >= 1.0f;
	}
	
	protected abstract void addMissile(final SegmentController p0, final Transform p1, final Vector3f p2, final float p3, final float p4, final float p5, final long p6, final SimpleTransformableSendableObject p7, final short p8);
	
	@Override
	public int onActivate(final SegmentPiece segmentPiece, final boolean b, final boolean b2) {
		final long absoluteIndex = segmentPiece.getAbsoluteIndex();
		for (int i = 0; i < this.getCollectionManagers().size(); ++i) {
			final Iterator iterator = ((MissileCollectionManager)this.getCollectionManagers().get(i)).getElementCollections().iterator();
			while (iterator.hasNext()) {
				final MissileUnit missileUnit;
				if ((missileUnit = (MissileUnit)iterator.next()).contains(absoluteIndex)) {
					missileUnit.setMainPiece(segmentPiece, b2);
					if (b2) {
						return 1;
					}
					return 0;
				}
			}
		}
		if (b2) {
			return 1;
		}
		return 0;
	}
	
	@Override
	public void updateActivationTypes(final ShortOpenHashSet set) {
		set.add(this.controlling);
	}
	
	public MissileController getMissileController() {
		return ((GameServerController)this.getSegmentController().getState().getController()).getMissileController();
	}
	
	public abstract boolean isTargetLocking(final EC p0);
	
	public abstract boolean isHeatSeeking(final EC p0);
	
	public boolean isTargetLocking(final SegmentPiece segmentPiece) {
		final MissileCollectionManager missileCollectionManager;
		return segmentPiece != null && (missileCollectionManager = (MissileCollectionManager)this.getCollectionManagersMap().get(segmentPiece.getAbsoluteIndex())) != null && this.isTargetLocking((EC)missileCollectionManager);
	}
	
	public boolean isHeatSeeking(final SegmentPiece segmentPiece) {
		final MissileCollectionManager missileCollectionManager;
		return segmentPiece != null && (missileCollectionManager = (MissileCollectionManager)this.getCollectionManagersMap().get(segmentPiece.getAbsoluteIndex())) != null && this.isHeatSeeking((EC)missileCollectionManager);
	}
	
	public ControllerManagerGUI getGUIUnitValues(final E e, final EC ec, final ControlBlockElementCollectionManager<?, ?, ?> controlBlockElementCollectionManager, final ControlBlockElementCollectionManager<?, ?, ?> controlBlockElementCollectionManager2) {
		this.getStatistics(e, ec, controlBlockElementCollectionManager, controlBlockElementCollectionManager2, this.tmpOutput);
		return ControllerManagerGUI.create((GameClientState)this.getState(), Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_MISSILE_MISSILEELEMENTMANAGER_4, e, new ModuleValueEntry(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_MISSILE_MISSILEELEMENTMANAGER_5, StringTools.formatPointZero(this.tmpOutput.damage)), new ModuleValueEntry(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_MISSILE_MISSILEELEMENTMANAGER_7, StringTools.formatPointZero(this.tmpOutput.speed)), new ModuleValueEntry(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_MISSILE_MISSILEELEMENTMANAGER_8, StringTools.formatPointZero(this.tmpOutput.distance)), new ModuleValueEntry(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_MISSILE_MISSILEELEMENTMANAGER_9, StringTools.formatPointZero(this.tmpOutput.reload)), new ModuleValueEntry(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_MISSILE_MISSILEELEMENTMANAGER_10, e.getPowerConsumedPerSecondResting()), new ModuleValueEntry(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_MISSILE_MISSILEELEMENTMANAGER_12, e.getPowerConsumedPerSecondCharging()));
	}
	
	@Override
	public void handle(final ControllerStateInterface controllerStateInterface, final Timer timer) {
		if (!controllerStateInterface.isFlightControllerActive()) {
			if (MissileElementManager.debug) {
				System.err.println("NO 1");
			}
			return;
		}
		if (this.getCollectionManagers().isEmpty()) {
			if (MissileElementManager.debug) {
				System.err.println("NO 2 " + this.getClass().getSimpleName() + "; " + controllerStateInterface.getClass().getSimpleName());
			}
			return;
		}
		try {
			if (!this.convertDeligateControls(controllerStateInterface, this.shootContainer.controlledFromOrig, this.shootContainer.controlledFrom)) {
				if (MissileElementManager.debug) {
					System.err.println("NO 3");
				}
				return;
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
			return;
		}
		for (int i = 0; i < this.getCollectionManagers().size(); ++i) {
			final MissileCollectionManager missileCollectionManager = (MissileCollectionManager)this.getCollectionManagers().get(i);
			final boolean selected = controllerStateInterface.isSelected(missileCollectionManager.getControllerElement(), this.shootContainer.controlledFrom);
			final boolean aiSelected = controllerStateInterface.isAISelected(missileCollectionManager.getControllerElement(), this.shootContainer.controlledFrom, i, this.getCollectionManagers().size(), missileCollectionManager);
			if (!selected && MissileElementManager.debug) {
				System.err.println("NO 4 " + this.getClass().getSimpleName() + "; " + controllerStateInterface.getClass().getSimpleName() + "; " + missileCollectionManager.getControllerPos() + "; " + this.shootContainer.controlledFrom);
			}
			if (selected && aiSelected) {
				final boolean b;
				if (!(b = (this.shootContainer.controlledFromOrig.equals(this.shootContainer.controlledFrom) | this.getControlElementMap().isControlling(this.shootContainer.controlledFromOrig, missileCollectionManager.getControllerPos(), this.controllerId))) && MissileElementManager.debug) {
					System.err.println("NO 4");
				}
				if (b && missileCollectionManager.allowedOnServerLimit()) {
					if (this.shootContainer.controlledFromOrig.equals(Ship.core)) {
						controllerStateInterface.getControlledFrom(this.shootContainer.controlledFromOrig);
					}
					if (MissileElementManager.debug) {
						System.err.println("FIRE MISSILE " + controllerStateInterface.getClass());
					}
					missileCollectionManager.handleControlShot(controllerStateInterface, timer);
					if (missileCollectionManager.getElementCollections().isEmpty() && this.clientIsOwnShip()) {
						((GameClientState)this.getState()).getController().popupInfoTextMessage(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_MISSILE_MISSILEELEMENTMANAGER_1, 0.0f);
					}
				}
			}
		}
		if (this.getCollectionManagers().isEmpty() && this.clientIsOwnShip()) {
			((GameClientState)this.getState()).getController().popupInfoTextMessage(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_MISSILE_MISSILEELEMENTMANAGER_3, 0.0f);
		}
	}
	
	@Override
	public void drawReloads(final Vector3i vector3i, final Vector3i vector3i2, final long n) {
		this.handleReload(vector3i, vector3i2, n, this.reloadListener);
	}
	
	public WeaponStatisticsData getStatistics(final E e, final EC ec, final ControlBlockElementCollectionManager<?, ?, ?> controlBlockElementCollectionManager, final ControlBlockElementCollectionManager<?, ?, ?> controlBlockElementCollectionManager2, final WeaponStatisticsData weaponStatisticsData) {
		if (controlBlockElementCollectionManager2 != null) {
			ec.setEffectTotal(controlBlockElementCollectionManager2.getTotalSize());
		}
		else {
			ec.setEffectTotal(0);
		}
		weaponStatisticsData.damage = e.getDamage();
		weaponStatisticsData.speed = e.getSpeed();
		weaponStatisticsData.distance = e.getDistance();
		weaponStatisticsData.reload = e.getReloadTimeMs();
		weaponStatisticsData.powerConsumption = e.getPowerConsumption();
		weaponStatisticsData.split = 1.0f;
		weaponStatisticsData.mode = 1.0f;
		weaponStatisticsData.effectRatio = 0.0f;
		if (controlBlockElementCollectionManager != null) {
			final MissileUnitModifier missileUnitModifier = (MissileUnitModifier)this.getAddOn().getGUI(ec, e, controlBlockElementCollectionManager, controlBlockElementCollectionManager2);
			weaponStatisticsData.mode = missileUnitModifier.outputMode;
			weaponStatisticsData.damage = missileUnitModifier.outputDamage;
			weaponStatisticsData.speed = missileUnitModifier.outputSpeed;
			weaponStatisticsData.distance = missileUnitModifier.outputDistance;
			weaponStatisticsData.reload = missileUnitModifier.outputReload;
			weaponStatisticsData.powerConsumption = missileUnitModifier.outputPowerConsumption;
			weaponStatisticsData.split = (float)missileUnitModifier.outputSplit;
		}
		if (controlBlockElementCollectionManager2 != null) {
			controlBlockElementCollectionManager2.getElementManager();
			weaponStatisticsData.effectRatio = CombinationAddOn.getRatio(ec, controlBlockElementCollectionManager2);
		}
		return weaponStatisticsData;
	}
	
	@Override
	public double calculateWeaponDamageIndex() {
		double n = 0.0;
		final Iterator<EC> iterator = this.getCollectionManagers().iterator();
		while (iterator.hasNext()) {
			final FocusableUsableModule focusableUsableModule;
			final ControlBlockElementCollectionManager<?, ?, ?> supportCollectionManager = ((ControlBlockElementCollectionManager)(focusableUsableModule = (MissileCollectionManager)iterator.next())).getSupportCollectionManager();
			final EffectCollectionManager effectCollectionManager = ((ControlBlockElementCollectionManager)focusableUsableModule).getEffectCollectionManager();
			final Iterator<E> iterator2 = ((ElementCollectionManager)focusableUsableModule).getElementCollections().iterator();
			while (iterator2.hasNext()) {
				this.getStatistics(iterator2.next(), (EC)focusableUsableModule, supportCollectionManager, effectCollectionManager, this.tmpOutput);
				n += this.tmpOutput.damage / (this.tmpOutput.reload / 1000.0);
			}
		}
		return n;
	}
	
	@Override
	public double calculateWeaponRangeIndex() {
		double n = 0.0;
		double n2 = 0.0;
		final Iterator<EC> iterator = this.getCollectionManagers().iterator();
		while (iterator.hasNext()) {
			final FocusableUsableModule focusableUsableModule;
			final ControlBlockElementCollectionManager<?, ?, ?> supportCollectionManager = ((ControlBlockElementCollectionManager)(focusableUsableModule = (MissileCollectionManager)iterator.next())).getSupportCollectionManager();
			final EffectCollectionManager effectCollectionManager = ((ControlBlockElementCollectionManager)focusableUsableModule).getEffectCollectionManager();
			final Iterator<E> iterator2 = ((ElementCollectionManager)focusableUsableModule).getElementCollections().iterator();
			while (iterator2.hasNext()) {
				this.getStatistics(iterator2.next(), (EC)focusableUsableModule, supportCollectionManager, effectCollectionManager, this.tmpOutput);
				n += this.tmpOutput.distance;
				++n2;
			}
		}
		if (n2 > 0.0) {
			return n / n2;
		}
		return 0.0;
	}
	
	@Override
	public double calculateWeaponHitPropabilityIndex() {
		double n = 0.0;
		final Iterator<EC> iterator = this.getCollectionManagers().iterator();
		while (iterator.hasNext()) {
			final FocusableUsableModule focusableUsableModule;
			final ControlBlockElementCollectionManager<?, ?, ?> supportCollectionManager = ((ControlBlockElementCollectionManager)(focusableUsableModule = (MissileCollectionManager)iterator.next())).getSupportCollectionManager();
			final EffectCollectionManager effectCollectionManager = ((ControlBlockElementCollectionManager)focusableUsableModule).getEffectCollectionManager();
			final Iterator<E> iterator2 = ((ElementCollectionManager)focusableUsableModule).getElementCollections().iterator();
			while (iterator2.hasNext()) {
				this.getStatistics(iterator2.next(), (EC)focusableUsableModule, supportCollectionManager, effectCollectionManager, this.tmpOutput);
				n += DumbMissileElementManager.BASE_SPEED * this.tmpOutput.split / (this.tmpOutput.reload / 1000.0);
			}
		}
		return n;
	}
	
	@Override
	public double calculateWeaponSpecialIndex() {
		final Iterator<EC> iterator = this.getCollectionManagers().iterator();
		while (iterator.hasNext()) {
			final FocusableUsableModule focusableUsableModule;
			final ControlBlockElementCollectionManager<?, ?, ?> supportCollectionManager = ((ControlBlockElementCollectionManager)(focusableUsableModule = (MissileCollectionManager)iterator.next())).getSupportCollectionManager();
			final EffectCollectionManager effectCollectionManager = ((ControlBlockElementCollectionManager)focusableUsableModule).getEffectCollectionManager();
			final Iterator<E> iterator2 = ((ElementCollectionManager)focusableUsableModule).getElementCollections().iterator();
			while (iterator2.hasNext()) {
				this.getStatistics(iterator2.next(), (EC)focusableUsableModule, supportCollectionManager, effectCollectionManager, this.tmpOutput);
			}
		}
		return 0.0;
	}
	
	@Override
	public double calculateWeaponPowerConsumptionPerSecondIndex() {
		double n = 0.0;
		final Iterator<EC> iterator = this.getCollectionManagers().iterator();
		while (iterator.hasNext()) {
			final FocusableUsableModule focusableUsableModule;
			final ControlBlockElementCollectionManager<?, ?, ?> supportCollectionManager = ((ControlBlockElementCollectionManager)(focusableUsableModule = (MissileCollectionManager)iterator.next())).getSupportCollectionManager();
			final EffectCollectionManager effectCollectionManager = ((ControlBlockElementCollectionManager)focusableUsableModule).getEffectCollectionManager();
			final Iterator<E> iterator2 = ((ElementCollectionManager)focusableUsableModule).getElementCollections().iterator();
			while(iterator2.hasNext()) {
				final E missileUnit = iterator2.next();
				this.getStatistics((E)missileUnit, (EC)focusableUsableModule, supportCollectionManager, effectCollectionManager, this.tmpOutput);
				n += missileUnit.getPowerConsumption();
			}
		}
		return n;
	}
	
	static {
		MissileElementManager.debug = false;
	}
	
	public class MissileDrawReloadListener implements ReloadListener
	{
		private GUITextOverlay chargesText;
		private short lastDraw;
		
		@Override
		public String onDischarged(final InputState inputState, final Vector3i vector3i, final Vector3i vector3i2, final Vector4f vector4f, final boolean b, final float n) {
			if (this.chargesText == null) {
				(this.chargesText = new GUITextOverlay(10, 10, FontLibrary.FontSize.MEDIUM.getFont(), (InputState)MissileElementManager.this.getState())).onInit();
			}
			if (this.lastDraw != inputState.getNumberOfUpdate()) {
				UsableControllableElementManager.drawReload(inputState, vector3i, vector3i2, vector4f, b, n, true, (float)(int)MissileElementManager.this.getSegmentController().getMissileCapacity(), (int)MissileElementManager.this.getSegmentController().getMissileCapacityMax(), -1L, this.chargesText);
				this.lastDraw = inputState.getNumberOfUpdate();
			}
			return null;
		}
		
		@Override
		public String onReload(final InputState inputState, final Vector3i vector3i, final Vector3i vector3i2, final Vector4f vector4f, final boolean b, final float n) {
			UsableControllableElementManager.drawReload(inputState, vector3i, vector3i2, vector4f, b, n);
			return null;
		}
		
		@Override
		public String onFull(final InputState inputState, final Vector3i vector3i, final Vector3i vector3i2, final Vector4f vector4f, final boolean b, final float n, final long n2) {
			return null;
		}
		
		@Override
		public void drawForElementCollectionManager(final InputState inputState, final Vector3i vector3i, final Vector3i vector3i2, final Vector4f vector4f, final long n) {
			if (this.chargesText == null) {
				(this.chargesText = new GUITextOverlay(10, 10, FontLibrary.FontSize.MEDIUM.getFont(), (InputState)MissileElementManager.this.getState())).onInit();
			}
			boolean b = false;
			if (this.lastDraw != inputState.getNumberOfUpdate()) {
				UsableControllableElementManager.drawReload(inputState, vector3i, vector3i2, UsableControllableFireingElementManager.reloadColor, false, 1.0f, true, (float)(int)MissileElementManager.this.getSegmentController().getMissileCapacity(), (int)MissileElementManager.this.getSegmentController().getMissileCapacityMax(), -1L, this.chargesText);
				this.lastDraw = inputState.getNumberOfUpdate();
				b = true;
			}
			if (b && MissileElementManager.this.getSegmentController().getMissileCapacity() < (int)MissileElementManager.this.getSegmentController().getMissileCapacityMax() && MissileCapacityElementManager.MISSILE_CAPACITY_RELOAD_MODE == MissileCapacityElementManager.MissileCapacityReloadMode.ALL) {
				this.chargesText.setTextSimple(StringTools.format(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_MISSILE_MISSILEELEMENTMANAGER_0, (int)FastMath.ceil(((ManagedSegmentController)MissileElementManager.this.getSegmentController()).getManagerContainer().getMissileCapacityTimer())));
				final Vector3f pos = this.chargesText.getPos();
				pos.x -= 10.0f;
				final Vector3f pos2 = this.chargesText.getPos();
				pos2.y -= 20.0f;
				UsableControllableElementManager.drawReloadText(inputState, vector3i, vector3i2, this.chargesText);
			}
		}
	}
}
