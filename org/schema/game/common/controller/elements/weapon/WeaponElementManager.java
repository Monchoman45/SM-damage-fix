// 
// Decompiled by Procyon v0.5.36
// 

package org.schema.game.common.controller.elements.weapon;

import org.schema.game.common.controller.elements.UsableElementManager;
import org.schema.schine.graphicsengine.forms.font.FontLibrary;
import org.schema.game.common.controller.elements.UsableControllableElementManager;
import org.schema.schine.input.InputState;
import org.schema.game.common.controller.elements.combination.CombinationSettings;
import org.schema.game.common.controller.elements.effectblock.EffectCollectionManager;
import org.schema.game.common.controller.elements.combination.modifier.WeaponUnitModifier;
import org.schema.game.common.controller.Ship;
import org.schema.game.common.controller.elements.ElementCollectionManager;
import java.io.IOException;
import org.schema.game.common.data.player.ControllerStateInterface;
import org.schema.game.common.controller.elements.UsableControllableFireingElementManager;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.client.controller.GameClientController;
import com.bulletphysics.linearmath.Transform;
import org.schema.game.client.view.gui.structurecontrol.ModuleValueEntry;
import org.schema.common.util.StringTools;
import org.schema.game.client.view.gui.structurecontrol.GUIKeyValueEntry;
import org.schema.schine.common.language.Lng;
import org.schema.game.client.data.GameClientState;
import org.schema.game.client.view.gui.structurecontrol.ControllerManagerGUI;
import org.schema.schine.network.objects.NetworkObject;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import java.util.Iterator;
import org.schema.game.common.data.SegmentPiece;
import org.schema.schine.graphicsengine.core.Controller;
import org.schema.game.client.view.camera.InShipCamera;
import com.bulletphysics.dynamics.RigidBody;
import javax.vecmath.Vector3f;
import org.schema.game.common.controller.elements.ControlBlockElementCollectionManager;
import org.schema.game.common.controller.elements.ManagerModuleCollection;
import org.schema.schine.network.Identifiable;
import org.schema.game.common.controller.elements.ShootingRespose;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;
import org.schema.game.common.controller.elements.combination.Combinable;
import org.schema.game.common.controller.elements.combination.CombinationAddOn;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.schine.graphicsengine.core.Timer;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.client.data.GameStateInterface;
import org.schema.game.common.controller.SegmentController;
import javax.vecmath.Vector4f;
import org.schema.schine.graphicsengine.forms.gui.GUITextOverlay;
import org.schema.game.common.data.element.ShootContainer;
import org.schema.game.common.controller.elements.combination.WeaponCombinationAddOn;
import org.schema.game.common.controller.elements.WeaponStatisticsData;
import org.schema.game.common.controller.damage.effects.InterEffectSet;
import org.schema.game.common.controller.elements.config.FloatReactorDualConfigElement;
import org.schema.common.config.ConfigurationElement;
import org.schema.game.common.controller.elements.WeaponElementManagerInterface;
import org.schema.game.common.controller.elements.NTSenderInterface;
import org.schema.game.common.controller.elements.NTReceiveInterface;
import org.schema.game.common.controller.elements.IntegrityBasedInterface;
import org.schema.game.common.controller.elements.BlockActivationListenerInterface;
import org.schema.game.common.controller.elements.combination.WeaponCombiSettings;
import org.schema.game.common.controller.elements.UsableCombinableControllableElementManager;

public class WeaponElementManager extends UsableCombinableControllableElementManager<WeaponUnit, WeaponCollectionManager, WeaponElementManager, WeaponCombiSettings> implements BlockActivationListenerInterface, IntegrityBasedInterface, NTReceiveInterface, NTSenderInterface, WeaponElementManagerInterface
{
	static final double[] weightedLookupTable;
	@ConfigurationElement(name = "AcidFormulaDefault")
	public static int ACID_FORMULA_DEFAULT;
	@ConfigurationElement(name = "Damage")
	public static FloatReactorDualConfigElement BASE_DAMAGE;
	@ConfigurationElement(name = "Distance")
	public static float BASE_DISTANCE;
	@ConfigurationElement(name = "Speed")
	public static float BASE_SPEED;
	@ConfigurationElement(name = "ReloadMs")
	public static float BASE_RELOAD;
	@ConfigurationElement(name = "ImpactForce")
	public static float IMPACT_FORCE;
	@ConfigurationElement(name = "Recoil")
	public static float RECOIL;
	@ConfigurationElement(name = "CursorRecoilX")
	public static float CURSOR_RECOIL_X;
	@ConfigurationElement(name = "CursorRecoilMinX")
	public static float CURSOR_RECOIL_MIN_X;
	@ConfigurationElement(name = "CursorRecoilMaxX")
	public static float CURSOR_RECOIL_MAX_X;
	@ConfigurationElement(name = "CursorRecoilDirX")
	public static float CURSOR_RECOIL_DIR_X;
	@ConfigurationElement(name = "CursorRecoilY")
	public static float CURSOR_RECOIL_Y;
	@ConfigurationElement(name = "CursorRecoilMinY")
	public static float CURSOR_RECOIL_MIN_Y;
	@ConfigurationElement(name = "CursorRecoilMaxY")
	public static float CURSOR_RECOIL_MAX_Y;
	@ConfigurationElement(name = "CursorRecoilDirY")
	public static float CURSOR_RECOIL_DIR_Y;
	@ConfigurationElement(name = "CursorRecoilSpeedIn")
	public static float CURSOR_RECOIL_IN;
	@ConfigurationElement(name = "CursorRecoilSpeedInAddMod")
	public static float CURSOR_RECOIL_IN_ADD;
	@ConfigurationElement(name = "CursorRecoilSpeedInPowMult")
	public static float CURSOR_RECOIL_IN_POW_MULT;
	@ConfigurationElement(name = "CursorRecoilSpeedOut")
	public static float CURSOR_RECOIL_OUT;
	@ConfigurationElement(name = "CursorRecoilSpeedOutAddMod")
	public static float CURSOR_RECOIL_OUT_ADD;
	@ConfigurationElement(name = "CursorRecoilSpeedOutPowMult")
	public static float CURSOR_RECOIL_OUT_POW_MULT;
	@ConfigurationElement(name = "PowerConsumption")
	public static float BASE_POWER_CONSUMPTION;
	@ConfigurationElement(name = "ReactorPowerConsumptionResting")
	public static float REACTOR_POWER_CONSUMPTION_RESTING;
	@ConfigurationElement(name = "ReactorPowerConsumptionCharging")
	public static float REACTOR_POWER_CONSUMPTION_CHARGING;
	@ConfigurationElement(name = "AdditionalPowerConsumptionPerUnitMult")
	public static float ADDITIONAL_POWER_CONSUMPTION_PER_UNIT_MULT;
	public static boolean debug;
	@ConfigurationElement(name = "EffectConfiguration")
	public static InterEffectSet basicEffectConfiguration;
	@ConfigurationElement(name = "ProjectileWidth")
	public static float PROJECTILE_WIDTH_MULT;
	@ConfigurationElement(name = "BasicPenetrationDepth")
	public static int PROJECTILE_PENETRATION_DEPTH_BASIC;
	@ConfigurationElement(name = "PenetrationDepthExp")
	public static float PROJECTILE_PENETRATION_DEPTH_EXP;
	@ConfigurationElement(name = "PenetrationDepthExpMult")
	public static float PROJECTILE_PENETRATION_DEPTH_EXP_MULT;
	@ConfigurationElement(name = "AcidDamageMaxPropagation")
	public static int ACID_DAMAGE_MAX_PROPAGATION;
	@ConfigurationElement(name = "AcidDamageFormulaConeStartWideWeight")
	public static float ACID_DAMAGE_FORMULA_CONE_START_WIDE_WEIGHT;
	@ConfigurationElement(name = "AcidDamageFormulaConeEndWideWeight")
	public static float ACID_DAMAGE_FORMULA_CONE_END_WIDE_WEIGHT;
	@ConfigurationElement(name = "AcidDamageMinOverPenModifier")
	public static float ACID_DAMAGE_MIN_OVER_PEN_MOD;
	@ConfigurationElement(name = "AcidDamageMaxOverPenModifier")
	public static float ACID_DAMAGE_MAX_OVER_PEN_MOD;
	@ConfigurationElement(name = "AcidDamageMinOverArmorModifier")
	public static float ACID_DAMAGE_MIN_OVER_ARMOR_MOD;
	@ConfigurationElement(name = "AcidDamageMaxOverArmorModifier")
	public static float ACID_DAMAGE_MAX_OVER_ARMOR_MOD;
	@ConfigurationElement(name = "AcidDamageOverArmorBaseReference")
	public static float ACID_DAMAGE_OVER_ARMOR_BASE;
	@ConfigurationElement(name = "DamageChargeMax")
	public static float DAMAGE_CHARGE_MAX;
	@ConfigurationElement(name = "DamageChargeSpeed")
	public static float DAMAGE_CHARGE_SPEED;
	@ConfigurationElement(name = "PossibleZoom")
	public static float POSSIBLE_ZOOM;
	@ConfigurationElement(name = "Aimable")
	public static int AIMABLE;
	private final WeaponStatisticsData tmpOutput;
	private WeaponCombinationAddOn addOn;
	private final ShootContainer shootContainer;
	private final WeaponCombiSettings combiSettings;
	private static GUITextOverlay chargesText;
	public static final Vector4f chargeColor;
	private final DrawReloadListener drawReloadListener;
	
	public WeaponElementManager(final SegmentController segmentController) {
		super((short)6, (short)16, segmentController);
		this.tmpOutput = new WeaponStatisticsData();
		this.shootContainer = new ShootContainer();
		this.combiSettings = new WeaponCombiSettings();
		this.drawReloadListener = new DrawReloadListener();
		this.addOn = new WeaponCombinationAddOn(this, (GameStateInterface)this.getState());
	}
	
	@Override
	public WeaponCombiSettings getCombiSettings() {
		return this.combiSettings;
	}
	
	void doShot(final WeaponUnit weaponUnit, final WeaponCollectionManager weaponCollectionManager, final ShootContainer shootContainer, final PlayerState playerState, final Timer timer) {
		ManagerModuleCollection<?, ?, ?> value = null;
		weaponCollectionManager.setEffectTotal(0);
		if (weaponCollectionManager.getEffectConnectedElement() != Long.MIN_VALUE) {
			value = this.getManagerContainer().getModulesControllerMap().get((short)ElementCollection.getType(weaponCollectionManager.getEffectConnectedElement()));
			final ControlBlockElementCollectionManager<?, ?, ?> effect;
			if ((effect = CombinationAddOn.getEffect(weaponCollectionManager.getEffectConnectedElement(), value, this.getSegmentController())) != null) {
				weaponCollectionManager.setEffectTotal(effect.getTotalSize());
			}
		}
		if (weaponCollectionManager.getSlaveConnectedElement() != Long.MIN_VALUE) {
			this.handleResponse(this.handleAddOn(this, weaponCollectionManager, weaponUnit, this.getManagerContainer().getModulesControllerMap().get((short)ElementCollection.getType(weaponCollectionManager.getSlaveConnectedElement())), value, shootContainer, null, playerState, timer, -1.0f), weaponUnit, shootContainer.weapontOutputWorldPos);
			return;
		}
		if (!weaponUnit.canUse(timer.currentTime, false)) {
			this.handleResponse(ShootingRespose.RELOADING, weaponUnit, shootContainer.weapontOutputWorldPos);
			return;
		}
		final long usableId = weaponCollectionManager.getUsableId();
		if (this.isUsingPowerReactors() || this.consumePower(weaponUnit.getPowerConsumption())) {
			weaponUnit.setStandardShotReloading();
			this.getParticleController().addProjectile(this.getSegmentController(), shootContainer.weapontOutputWorldPos, shootContainer.shootingDirTemp, weaponUnit.getDamage(), weaponUnit.getDistance(), weaponCollectionManager.getAcidFormula().ordinal(), weaponUnit.getProjectileWidth(), weaponUnit.getPenetrationDepth(weaponUnit.getDamage()), weaponUnit.getImpactForce(), usableId, weaponCollectionManager.getColor());
			this.handleRecoil(weaponCollectionManager, weaponUnit, shootContainer.weapontOutputWorldPos, shootContainer.shootingDirTemp, weaponUnit.getRecoil(), weaponUnit.getDamage());
			weaponCollectionManager.damageProduced += weaponUnit.getDamage();
			this.handleResponse(ShootingRespose.FIRED, weaponUnit, shootContainer.weapontOutputWorldPos);
			return;
		}
		this.handleResponse(ShootingRespose.NO_POWER, weaponUnit, shootContainer.weapontOutputWorldPos);
	}
	
	public void handleRecoil(final WeaponCollectionManager weaponCollectionManager, final WeaponUnit weaponUnit, final Vector3f vector3f, final Vector3f vector3f2, final float n, final float n2) {
		if (this.getSegmentController().railController.getRoot().getPhysicsDataContainer().getObject() instanceof RigidBody) {
			this.getSegmentController().railController.getRoot().getPhysicsDataContainer().getObject();
			if (n * n2 == 0.0f) {
				return;
			}
			final Vector3f vector3f3;
			(vector3f3 = new Vector3f(vector3f2)).negate();
			this.getSegmentController().railController.getRoot().hitWithPhysicalRecoil(vector3f, vector3f3, n * n2, true);
		}
	}
	
	public void handleCursorRecoil(final WeaponCollectionManager weaponCollectionManager, final float n, final WeaponCombiSettings weaponCombiSettings) {
		if (this.getSegmentController().isClientOwnObject() && Controller.getCamera() instanceof InShipCamera) {
			((InShipCamera)Controller.getCamera()).addRecoil(Math.min(weaponCombiSettings.cursorRecoilMaxX, Math.max(weaponCombiSettings.cursorRecoilMinX, n * weaponCombiSettings.cursorRecoilX)), Math.min(weaponCombiSettings.cursorRecoilMaxY, Math.max(weaponCombiSettings.cursorRecoilMinY, n * weaponCombiSettings.cursorRecoilY)), weaponCombiSettings.cursorRecoilDirX, weaponCombiSettings.cursorRecoilDirY, WeaponElementManager.CURSOR_RECOIL_IN, WeaponElementManager.CURSOR_RECOIL_IN_ADD, WeaponElementManager.CURSOR_RECOIL_IN_POW_MULT, WeaponElementManager.CURSOR_RECOIL_OUT, WeaponElementManager.CURSOR_RECOIL_OUT_ADD, WeaponElementManager.CURSOR_RECOIL_OUT_POW_MULT);
		}
	}
	
	@Override
	public int onActivate(final SegmentPiece segmentPiece, final boolean b, final boolean b2) {
		final long absoluteIndex = segmentPiece.getAbsoluteIndex();
		for (int i = 0; i < this.getCollectionManagers().size(); ++i) {
			final Iterator<WeaponUnit> iterator = this.getCollectionManagers().get(i).getElementCollections().iterator();
			while (iterator.hasNext()) {
				final WeaponUnit weaponUnit;
				if ((weaponUnit = iterator.next()).contains(absoluteIndex)) {
					weaponUnit.setMainPiece(segmentPiece, b2);
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
		set.add((short)16);
	}
	
	@Override
	public void updateFromNT(final NetworkObject networkObject) {
	}
	
	@Override
	public void updateToFullNT(final NetworkObject networkObject) {
	}
	
	@Override
	public CombinationAddOn<WeaponUnit, WeaponCollectionManager, WeaponElementManager, WeaponCombiSettings> getAddOn() {
		return this.addOn;
	}
	
	public ControllerManagerGUI getGUIUnitValues(final WeaponUnit weaponUnit, final WeaponCollectionManager weaponCollectionManager, final ControlBlockElementCollectionManager<?, ?, ?> controlBlockElementCollectionManager, final ControlBlockElementCollectionManager<?, ?, ?> controlBlockElementCollectionManager2) {
		this.getStatistics(weaponUnit, weaponCollectionManager, controlBlockElementCollectionManager, controlBlockElementCollectionManager2, this.tmpOutput);
		return ControllerManagerGUI.create((GameClientState)this.getState(), Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_WEAPON_WEAPONELEMENTMANAGER_3, weaponUnit, new ModuleValueEntry(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_WEAPON_WEAPONELEMENTMANAGER_4, StringTools.formatPointZero(this.tmpOutput.damage / this.tmpOutput.split)), new ModuleValueEntry(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_WEAPON_WEAPONELEMENTMANAGER_15, weaponUnit.getPenetrationDepth(this.tmpOutput.damage)), new ModuleValueEntry(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_WEAPON_WEAPONELEMENTMANAGER_24, StringTools.formatPointZero(this.tmpOutput.speed)), new ModuleValueEntry(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_WEAPON_WEAPONELEMENTMANAGER_21, StringTools.formatPointZero(this.tmpOutput.distance)), new ModuleValueEntry(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_WEAPON_WEAPONELEMENTMANAGER_22, StringTools.formatPointZero(this.tmpOutput.split)), new ModuleValueEntry(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_WEAPON_WEAPONELEMENTMANAGER_23, StringTools.formatPointZero(this.tmpOutput.reload)), new ModuleValueEntry(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_WEAPON_WEAPONELEMENTMANAGER_5, weaponUnit.getPowerConsumedPerSecondResting()), new ModuleValueEntry(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_WEAPON_WEAPONELEMENTMANAGER_6, weaponUnit.getPowerConsumedPerSecondCharging()), new ModuleValueEntry(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_WEAPON_WEAPONELEMENTMANAGER_25, StringTools.formatPointZero(this.tmpOutput.effectRatio)));
	}
	
	@Override
	protected String getTag() {
		return "cannon";
	}
	
	@Override
	public WeaponCollectionManager getNewCollectionManager(final SegmentPiece segmentPiece, final Class<WeaponCollectionManager> clazz) {
		return new WeaponCollectionManager(segmentPiece, this.getSegmentController(), this);
	}
	
	@Override
	public String getManagerName() {
		return Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_WEAPON_WEAPONELEMENTMANAGER_20;
	}
	
	protected void playSound(final WeaponUnit weaponUnit, final Transform transform) {
		final float max = Math.max(0.0f, Math.min(1.0f, weaponUnit.size() / 100.0f));
		((GameClientController)this.getState().getController()).queueTransformableAudio("0022_spaceship user - laser gun single fire small", transform, 1.0f * (1.0f - max));
		((GameClientController)this.getState().getController()).queueTransformableAudio("0022_spaceship user - laser gun single fire medium", transform, max, 100.0f);
	}
	
	@Override
	public void drawReloads(final Vector3i vector3i, final Vector3i vector3i2, final long n) {
		this.handleReload(vector3i, vector3i2, n, this.drawReloadListener);
	}
	
	@Override
	public void handle(final ControllerStateInterface controllerStateInterface, final Timer timer) {
		System.currentTimeMillis();
		if (!controllerStateInterface.isFlightControllerActive()) {
			if (WeaponElementManager.debug) {
				System.err.println("NOT ACTIVE");
			}
			return;
		}
		if (this.getCollectionManagers().isEmpty()) {
			if (WeaponElementManager.debug) {
				System.err.println("NO WEAPONS");
			}
			return;
		}
		try {
			if (!this.convertDeligateControls(controllerStateInterface, this.shootContainer.controlledFromOrig, this.shootContainer.controlledFrom)) {
				if (WeaponElementManager.debug) {
					System.err.println("NO SLOT");
				}
				return;
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
			return;
		}
		this.getPowerManager().sendNoPowerHitEffectIfNeeded();
		if (WeaponElementManager.debug) {
			System.err.println("FIREING CONTROLLERS: " + this.getState() + ", " + this.getCollectionManagers().size() + " FROM: " + this.shootContainer.controlledFrom);
		}
		for (int i = 0; i < this.getCollectionManagers().size(); ++i) {
			final WeaponCollectionManager weaponCollectionManager = (WeaponCollectionManager)this.getCollectionManagers().get(i);
			final boolean selected = controllerStateInterface.isSelected(weaponCollectionManager.getControllerElement(), this.shootContainer.controlledFrom);
			final boolean aiSelected = controllerStateInterface.isAISelected(weaponCollectionManager.getControllerElement(), this.shootContainer.controlledFrom, i, this.getCollectionManagers().size(), weaponCollectionManager);
			if (selected && aiSelected) {
				final boolean b = this.shootContainer.controlledFromOrig.equals(this.shootContainer.controlledFrom) | this.getControlElementMap().isControlling(this.shootContainer.controlledFromOrig, weaponCollectionManager.getControllerPos(), this.controllerId);
				if (WeaponElementManager.debug) {
					System.err.println("Controlling " + b + " " + this.getState());
				}
				if (b && weaponCollectionManager.allowedOnServerLimit()) {
					if (this.shootContainer.controlledFromOrig.equals(Ship.core)) {
						controllerStateInterface.getControlledFrom(this.shootContainer.controlledFromOrig);
					}
					if (WeaponElementManager.debug) {
						System.err.println("Controlling " + b + " " + this.getState() + ": " + weaponCollectionManager.getElementCollections().size());
					}
					weaponCollectionManager.handleControlShot(controllerStateInterface, timer);
				}
			}
		}
		if (this.getCollectionManagers().isEmpty() && this.clientIsOwnShip()) {
			((GameClientState)this.getState()).getController().popupInfoTextMessage(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_WEAPON_WEAPONELEMENTMANAGER_2, 0.0f);
		}
	}
	
	@Override
	public boolean isUsingRegisteredActivation() {
		return true;
	}
	
	public WeaponStatisticsData getStatistics(final WeaponUnit weaponUnit, final WeaponCollectionManager weaponCollectionManager, final ControlBlockElementCollectionManager<?, ?, ?> controlBlockElementCollectionManager, final ControlBlockElementCollectionManager<?, ?, ?> controlBlockElementCollectionManager2, final WeaponStatisticsData weaponStatisticsData) {
		if (controlBlockElementCollectionManager2 != null) {
			weaponCollectionManager.setEffectTotal(controlBlockElementCollectionManager2.getTotalSize());
		}
		else {
			weaponCollectionManager.setEffectTotal(0);
		}
		weaponStatisticsData.damage = weaponUnit.getDamage();
		weaponStatisticsData.speed = weaponUnit.getSpeed();
		weaponStatisticsData.distance = weaponUnit.getDistance();
		weaponStatisticsData.reload = weaponUnit.getReloadTimeMs();
		weaponStatisticsData.powerConsumption = weaponUnit.getPowerConsumption();
		weaponStatisticsData.split = 1.0f;
		weaponStatisticsData.mode = 1.0f;
		weaponStatisticsData.effectRatio = 0.0f;
		if (controlBlockElementCollectionManager != null) {
			final WeaponUnitModifier weaponUnitModifier = (WeaponUnitModifier)this.getAddOn().getGUI(weaponCollectionManager, weaponUnit, controlBlockElementCollectionManager, controlBlockElementCollectionManager2);
			weaponStatisticsData.damage = weaponUnitModifier.outputDamage;
			weaponStatisticsData.speed = weaponUnitModifier.outputSpeed;
			weaponStatisticsData.distance = weaponUnitModifier.outputDistance;
			weaponStatisticsData.reload = weaponUnitModifier.outputReload;
			weaponStatisticsData.powerConsumption = weaponUnitModifier.outputPowerConsumption;
		}
		if (controlBlockElementCollectionManager2 != null) {
			controlBlockElementCollectionManager2.getElementManager();
			weaponStatisticsData.effectRatio = CombinationAddOn.getRatio(weaponCollectionManager, controlBlockElementCollectionManager2);
		}
		return weaponStatisticsData;
	}
	
	@Override
	public double calculateWeaponDamageIndex() {
		double n = 0.0;
		final Iterator<WeaponCollectionManager> iterator = this.getCollectionManagers().iterator();
		while (iterator.hasNext()) {
			final WeaponCollectionManager weaponCollectionManager;
			final ControlBlockElementCollectionManager<?, ?, ?> supportCollectionManager = (weaponCollectionManager = iterator.next()).getSupportCollectionManager();
			final EffectCollectionManager<?, ?, ?> effectCollectionManager = weaponCollectionManager.getEffectCollectionManager();
			final Iterator<WeaponUnit> iterator2 = weaponCollectionManager.getElementCollections().iterator();
			while (iterator2.hasNext()) {
				this.getStatistics(iterator2.next(), weaponCollectionManager, supportCollectionManager, effectCollectionManager, this.tmpOutput);
				n += this.tmpOutput.damage / (this.tmpOutput.reload / 1000.0);
			}
		}
		return n;
	}
	
	@Override
	public double calculateWeaponRangeIndex() {
		double n = 0.0;
		double n2 = 0.0;
		final Iterator<WeaponCollectionManager> iterator = this.getCollectionManagers().iterator();
		while (iterator.hasNext()) {
			final WeaponCollectionManager weaponCollectionManager;
			final ControlBlockElementCollectionManager<?, ?, ?> supportCollectionManager = (weaponCollectionManager = iterator.next()).getSupportCollectionManager();
			final EffectCollectionManager<?, ?, ?> effectCollectionManager = weaponCollectionManager.getEffectCollectionManager();
			final Iterator<WeaponUnit> iterator2 = weaponCollectionManager.getElementCollections().iterator();
			while (iterator2.hasNext()) {
				this.getStatistics(iterator2.next(), weaponCollectionManager, supportCollectionManager, effectCollectionManager, this.tmpOutput);
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
		final Iterator<WeaponCollectionManager> iterator = this.getCollectionManagers().iterator();
		while (iterator.hasNext()) {
			final WeaponCollectionManager weaponCollectionManager;
			final ControlBlockElementCollectionManager<?, ?, ?> supportCollectionManager = (weaponCollectionManager = iterator.next()).getSupportCollectionManager();
			final EffectCollectionManager<?, ?, ?> effectCollectionManager = weaponCollectionManager.getEffectCollectionManager();
			final Iterator<WeaponUnit> iterator2 = weaponCollectionManager.getElementCollections().iterator();
			while (iterator2.hasNext()) {
				this.getStatistics(iterator2.next(), weaponCollectionManager, supportCollectionManager, effectCollectionManager, this.tmpOutput);
				n += WeaponElementManager.BASE_SPEED * this.tmpOutput.split / (this.tmpOutput.reload / 1000.0);
			}
		}
		return n;
	}
	
	@Override
	public double calculateWeaponSpecialIndex() {
		final Iterator<WeaponCollectionManager> iterator = this.getCollectionManagers().iterator();
		while (iterator.hasNext()) {
			final WeaponCollectionManager weaponCollectionManager;
			final ControlBlockElementCollectionManager<?, ?, ?> supportCollectionManager = (weaponCollectionManager = iterator.next()).getSupportCollectionManager();
			final EffectCollectionManager<?, ?, ?> effectCollectionManager = weaponCollectionManager.getEffectCollectionManager();
			final Iterator<WeaponUnit> iterator2 = weaponCollectionManager.getElementCollections().iterator();
			while (iterator2.hasNext()) {
				this.getStatistics(iterator2.next(), weaponCollectionManager, supportCollectionManager, effectCollectionManager, this.tmpOutput);
			}
		}
		return 0.0;
	}
	
	@Override
	public double calculateWeaponPowerConsumptionPerSecondIndex() {
		double n = 0.0;
		final Iterator<WeaponCollectionManager> iterator = this.getCollectionManagers().iterator();
		while (iterator.hasNext()) {
			final WeaponCollectionManager weaponCollectionManager;
			final ControlBlockElementCollectionManager<?, ?, ?> supportCollectionManager = (weaponCollectionManager = iterator.next()).getSupportCollectionManager();
			final EffectCollectionManager<?, ?, ?> effectCollectionManager = weaponCollectionManager.getEffectCollectionManager();
			for (final WeaponUnit weaponUnit : weaponCollectionManager.getElementCollections()) {
				this.getStatistics(weaponUnit, weaponCollectionManager, supportCollectionManager, effectCollectionManager, this.tmpOutput);
				n += weaponUnit.getPowerConsumption();
			}
		}
		return n;
	}
	
	static {
		weightedLookupTable = new double[300];
		WeaponElementManager.ACID_FORMULA_DEFAULT = 0;
		WeaponElementManager.BASE_DAMAGE = new FloatReactorDualConfigElement();
		WeaponElementManager.BASE_DISTANCE = 1000.0f;
		WeaponElementManager.BASE_SPEED = 10.0f;
		WeaponElementManager.BASE_RELOAD = 1000.0f;
		WeaponElementManager.IMPACT_FORCE = 0.01f;
		WeaponElementManager.RECOIL = 0.1f;
		WeaponElementManager.CURSOR_RECOIL_X = 1.0E-4f;
		WeaponElementManager.CURSOR_RECOIL_MIN_X = 0.001f;
		WeaponElementManager.CURSOR_RECOIL_MAX_X = 0.1f;
		WeaponElementManager.CURSOR_RECOIL_DIR_X = 0.0f;
		WeaponElementManager.CURSOR_RECOIL_Y = 1.0E-4f;
		WeaponElementManager.CURSOR_RECOIL_MIN_Y = 0.001f;
		WeaponElementManager.CURSOR_RECOIL_MAX_Y = 0.1f;
		WeaponElementManager.CURSOR_RECOIL_DIR_Y = 0.0f;
		WeaponElementManager.CURSOR_RECOIL_IN = 1.0f;
		WeaponElementManager.CURSOR_RECOIL_IN_ADD = 1.0f;
		WeaponElementManager.CURSOR_RECOIL_IN_POW_MULT = 1.0f;
		WeaponElementManager.CURSOR_RECOIL_OUT = 5.0f;
		WeaponElementManager.CURSOR_RECOIL_OUT_ADD = 1.0f;
		WeaponElementManager.CURSOR_RECOIL_OUT_POW_MULT = 1.0f;
		WeaponElementManager.BASE_POWER_CONSUMPTION = 10.0f;
		WeaponElementManager.REACTOR_POWER_CONSUMPTION_RESTING = 10.0f;
		WeaponElementManager.REACTOR_POWER_CONSUMPTION_CHARGING = 10.0f;
		WeaponElementManager.ADDITIONAL_POWER_CONSUMPTION_PER_UNIT_MULT = 0.1f;
		WeaponElementManager.debug = false;
		WeaponElementManager.basicEffectConfiguration = new InterEffectSet();
		WeaponElementManager.PROJECTILE_WIDTH_MULT = 1.0f;
		WeaponElementManager.PROJECTILE_PENETRATION_DEPTH_BASIC = 1;
		WeaponElementManager.PROJECTILE_PENETRATION_DEPTH_EXP = 0.5f;
		WeaponElementManager.PROJECTILE_PENETRATION_DEPTH_EXP_MULT = 0.5f;
		WeaponElementManager.ACID_DAMAGE_MAX_PROPAGATION = 50;
		WeaponElementManager.ACID_DAMAGE_FORMULA_CONE_START_WIDE_WEIGHT = 1.8f;
		WeaponElementManager.ACID_DAMAGE_FORMULA_CONE_END_WIDE_WEIGHT = 0.2f;
		WeaponElementManager.ACID_DAMAGE_MIN_OVER_PEN_MOD = 1.0f;
		WeaponElementManager.ACID_DAMAGE_MAX_OVER_PEN_MOD = 10.0f;
		WeaponElementManager.ACID_DAMAGE_MIN_OVER_ARMOR_MOD = 1.0f;
		WeaponElementManager.ACID_DAMAGE_MAX_OVER_ARMOR_MOD = 3.0f;
		WeaponElementManager.ACID_DAMAGE_OVER_ARMOR_BASE = 250.0f;
		WeaponElementManager.DAMAGE_CHARGE_MAX = 0.05f;
		WeaponElementManager.DAMAGE_CHARGE_SPEED = 0.05f;
		WeaponElementManager.POSSIBLE_ZOOM = 0.0f;
		WeaponElementManager.AIMABLE = 1;
		chargeColor = new Vector4f(0.8f, 0.5f, 0.3f, 0.4f);
	}
	
	public class DrawReloadListener implements ReloadListener
	{
		@Override
		public String onDischarged(final InputState inputState, final Vector3i vector3i, final Vector3i vector3i2, final Vector4f vector4f, final boolean b, final float n) {
			UsableControllableElementManager.drawReload(inputState, vector3i, vector3i2, vector4f, b, n);
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
			final WeaponCollectionManager weaponCollectionManager;
			final WeaponCombiSettings weaponChargeParams;
			if ((weaponCollectionManager = (WeaponCollectionManager)WeaponElementManager.this.getCollectionManagersMap().get(n)) != null && (weaponChargeParams = weaponCollectionManager.getWeaponChargeParams()).damageChargeMax > 0.0f && weaponCollectionManager.damageCharge > 0.0f) {
				if (WeaponElementManager.chargesText == null) {
					WeaponElementManager.chargesText = new GUITextOverlay(10, 10, FontLibrary.FontSize.MEDIUM.getFont(), (InputState)WeaponElementManager.this.getState());
					WeaponElementManager.chargesText.onInit();
				}
				UsableControllableElementManager.drawReload(inputState, vector3i, vector3i2, WeaponElementManager.chargeColor, false, Math.min(weaponCollectionManager.damageCharge / weaponChargeParams.damageChargeMax, 0.99999f), true, weaponCollectionManager.damageCharge, (int)weaponChargeParams.damageChargeMax, -1L, WeaponElementManager.chargesText);
			}
		}
	}
}
