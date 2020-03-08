// 
// Decompiled by Procyon v0.5.36
// 

package org.schema.game.common.controller.elements.missile.dumb;

import org.schema.game.common.controller.elements.ElementCollectionManager;
import org.schema.game.common.controller.elements.missile.MissileCollectionManager;
import org.schema.game.common.controller.elements.missile.MissileUnit;
import org.schema.game.client.controller.GameClientController;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.controller.elements.combination.MissileCombiSettings;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.game.common.controller.elements.VoidElementManager;
import org.schema.common.util.StringTools;
import org.schema.game.client.view.gui.structurecontrol.ModuleValueEntry;
import org.schema.game.client.view.gui.structurecontrol.GUIKeyValueEntry;
import org.schema.schine.common.language.Lng;
import org.schema.game.client.data.GameClientState;
import org.schema.game.common.controller.elements.combination.CombinationAddOn;
import org.schema.game.common.controller.elements.combination.modifier.MissileUnitModifier;
import org.schema.game.client.view.gui.structurecontrol.ControllerManagerGUI;
import org.schema.game.common.controller.elements.ControlBlockElementCollectionManager;
import org.schema.game.common.controller.damage.Damager;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;
import javax.vecmath.Vector3f;
import com.bulletphysics.linearmath.Transform;
import org.schema.game.client.data.GameStateInterface;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.elements.combination.MissileCombinationAddOn;
import org.schema.game.common.controller.damage.effects.InterEffectSet;
import org.schema.game.common.controller.elements.UnitCalcStyle;
import org.schema.common.config.ConfigurationElement;
import org.schema.game.common.controller.elements.config.FloatReactorDualConfigElement;
import org.schema.game.common.controller.elements.missile.MissileElementManager;

public class DumbMissileElementManager extends MissileElementManager<DumbMissileUnit, DumbMissileCollectionManager, DumbMissileElementManager>
{
	@ConfigurationElement(name = "Damage")
	public static FloatReactorDualConfigElement BASE_DAMAGE;
	@ConfigurationElement(name = "Distance")
	public static float BASE_DISTANCE;
	@ConfigurationElement(name = "Speed", description = "missile speed in percent of server max speed (1.0 = 100%)")
	public static float BASE_SPEED;
	@ConfigurationElement(name = "ReloadMs")
	public static float BASE_RELOAD;
	@ConfigurationElement(name = "ReactorPowerConsumptionResting")
	public static float REACTOR_POWER_CONSUMPTION_RESTING;
	@ConfigurationElement(name = "AdditionalCapacityUsedPerDamage")
	public static float ADDITIONAL_CAPACITY_USER_PER_DAMAGE;
	@ConfigurationElement(name = "AdditionalCapacityUsedPerDamageMult")
	public static float ADDITIONAL_CAPACITY_USER_PER_DAMAGE_MULT;
	@ConfigurationElement(name = "PercentagePowerUsageCharging")
	public static float PERCENTAGE_POWER_USAGE_CHARGING;
	@ConfigurationElement(name = "PercentagePowerUsageResting")
	public static float PERCENTAGE_POWER_USAGE_RESTING;
	@ConfigurationElement(name = "ReactorPowerConsumptionCharging")
	public static float REACTOR_POWER_CONSUMPTION_CHARGING;
	@ConfigurationElement(name = "PowerConsumption")
	public static float BASE_POWER_CONSUMPTION;
	@ConfigurationElement(name = "AdditionalPowerConsumptionPerUnitMult")
	public static float ADDITIONAL_POWER_CONSUMPTION_PER_UNIT_MULT;
	@ConfigurationElement(name = "ChasingTurnSpeedWithTargetInFront")
	public static float CHASING_TURN_SPEED_WITH_TARGET_IN_FRONT;
	@ConfigurationElement(name = "ChasingTurnSpeedWithTargetInBack")
	public static float CHASING_TURN_SPEED_WITH_TARGET_IN_BACK;
	@ConfigurationElement(name = "BombActivationTimeSec")
	public static float BOMB_ACTIVATION_TIME_SEC;
	@ConfigurationElement(name = "MissileHPCalcStyle")
	public static UnitCalcStyle MISSILE_HP_CALC_STYLE;
	@ConfigurationElement(name = "MissileHPMin")
	public static float MISSILE_HP_MIN;
	@ConfigurationElement(name = "MissileHPPerDamage")
	public static float MISSILE_HP_PER_DAMAGE;
	@ConfigurationElement(name = "MissileHPExp")
	public static float MISSILE_HP_EXP;
	@ConfigurationElement(name = "MissileHPExpMult")
	public static float MISSILE_HP_EXP_MULT;
	@ConfigurationElement(name = "MissileHPLogOffset")
	public static float MISSILE_HP_LOG_OFFSET;
	@ConfigurationElement(name = "MissileHPLogFactor")
	public static float MISSILE_HP_LOG_FACTOR;
	@ConfigurationElement(name = "EffectConfiguration")
	public static InterEffectSet basicEffectConfiguration;
	@ConfigurationElement(name = "LockOnTimeSec")
	public static float LOCK_ON_TIME_SEC;
	@ConfigurationElement(name = "LockedOnExpireTimeSec")
	public static float LOCKED_ON_EXPIRE_TIME_SEC;
	@ConfigurationElement(name = "PossibleZoom")
	public static float POSSIBLE_ZOOM;
	private MissileCombinationAddOn addOn;
	
	public DumbMissileElementManager(final SegmentController segmentController) {
		super((short)38, (short)32, segmentController);
		this.addOn = new MissileCombinationAddOn(this, (GameStateInterface)this.getState());
	}
	
	public void addMissile(final SegmentController segmentController, final Transform transform, final Vector3f vector3f, final float n, final float n2, final float n3, final long n4, final SimpleTransformableSendableObject simpleTransformableSendableObject, final short n5) {
		//System.err.println("#XXX: addMissile");
		//#XXX: lock on as base missile fix
		//there's technically a config for this in blockBehaviorConfig, but it doesn't
		//actually work; i considered trying to make it work instead of just doing this
		//but m/x is a unique case in the code which makes that not super simple, instead
		//this seemed to be the most straightforward solution. it does mean that
		//DumbMissileElementManager now shoots missiles that aren't dumb, but it kinda
		//already did that because it's the element manager for every type of missile?
		//idk, missiles are weird.
		this.getMissileController().addFafoMissile(segmentController, transform, vector3f, n, n2, n3, n4, simpleTransformableSendableObject, n5);
		//#XXX:
	}
	
	@Override
	public boolean isTargetLocking(final DumbMissileCollectionManager dumbMissileCollectionManager) {
		return this.getMissileMode(dumbMissileCollectionManager) == 2;
	}
	
	@Override
	public boolean isHeatSeeking(final DumbMissileCollectionManager dumbMissileCollectionManager) {
		return this.getMissileMode(dumbMissileCollectionManager) == 1;
	}
	
	@Override
	public ControllerManagerGUI getGUIUnitValues(final DumbMissileUnit dumbMissileUnit, final DumbMissileCollectionManager dumbMissileCollectionManager, final ControlBlockElementCollectionManager<?, ?, ?> controlBlockElementCollectionManager, final ControlBlockElementCollectionManager<?, ?, ?> controlBlockElementCollectionManager2) {
		if (controlBlockElementCollectionManager2 != null) {
			dumbMissileCollectionManager.setEffectTotal(controlBlockElementCollectionManager2.getTotalSize());
		}
		else {
			dumbMissileCollectionManager.setEffectTotal(0);
		}
		float n = dumbMissileUnit.getDamage();
		float n2 = dumbMissileUnit.getSpeed();
		float n3 = dumbMissileUnit.getDistance();
		float n4 = dumbMissileUnit.getReloadTimeMs();
		float n5 = dumbMissileUnit.getPowerConsumption();
		float n6 = 1.0f;
		float outputMode = 0.0f;
		float ratio = 0.0f;
		if (controlBlockElementCollectionManager != null) {
			final MissileUnitModifier missileUnitModifier;
			n = (missileUnitModifier = (MissileUnitModifier)this.getAddOn().getGUI(dumbMissileCollectionManager, dumbMissileUnit, controlBlockElementCollectionManager, controlBlockElementCollectionManager2)).outputDamage;
			n2 = missileUnitModifier.outputSpeed;
			n3 = missileUnitModifier.outputDistance;
			n4 = missileUnitModifier.outputReload;
			n5 = missileUnitModifier.outputPowerConsumption;
			n6 = (float)missileUnitModifier.outputSplit;
			outputMode = missileUnitModifier.outputMode;
		}
		if (controlBlockElementCollectionManager2 != null) {
			controlBlockElementCollectionManager2.getElementManager();
			ratio = CombinationAddOn.getRatio(dumbMissileCollectionManager, controlBlockElementCollectionManager2);
		}
		return ControllerManagerGUI.create((GameClientState)this.getState(), Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_MISSILE_DUMB_DUMBMISSILEELEMENTMANAGER_1, dumbMissileUnit, new ModuleValueEntry(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_MISSILE_DUMB_DUMBMISSILEELEMENTMANAGER_0, (outputMode == 0.0f) ? Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_MISSILE_DUMB_DUMBMISSILEELEMENTMANAGER_12 : ((outputMode == 1.0f) ? Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_MISSILE_DUMB_DUMBMISSILEELEMENTMANAGER_21 : Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_MISSILE_DUMB_DUMBMISSILEELEMENTMANAGER_3)), new ModuleValueEntry(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_MISSILE_DUMB_DUMBMISSILEELEMENTMANAGER_4, StringTools.formatPointZero(n / n6)), new ModuleValueEntry(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_MISSILE_DUMB_DUMBMISSILEELEMENTMANAGER_5, StringTools.formatPointZero(VoidElementManager.EXPLOSION_SHIELD_DAMAGE_BONUS)), new ModuleValueEntry(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_MISSILE_DUMB_DUMBMISSILEELEMENTMANAGER_6, StringTools.formatPointZero(VoidElementManager.EXPLOSION_HULL_DAMAGE_BONUS)), new ModuleValueEntry(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_MISSILE_DUMB_DUMBMISSILEELEMENTMANAGER_22, StringTools.formatPointZero(n2)), new ModuleValueEntry(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_MISSILE_DUMB_DUMBMISSILEELEMENTMANAGER_9, StringTools.formatPointZero(n3)), new ModuleValueEntry(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_MISSILE_DUMB_DUMBMISSILEELEMENTMANAGER_10, StringTools.formatPointZero(n6)), new ModuleValueEntry(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_MISSILE_DUMB_DUMBMISSILEELEMENTMANAGER_11, StringTools.formatPointZero(n4)), new ModuleValueEntry(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_MISSILE_DUMB_DUMBMISSILEELEMENTMANAGER_2, StringTools.formatPointZero(n5)), new ModuleValueEntry(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_MISSILE_DUMB_DUMBMISSILEELEMENTMANAGER_8, StringTools.formatPointZero(ratio)));
	}
	
	@Override
	public CombinationAddOn<DumbMissileUnit, DumbMissileCollectionManager, DumbMissileElementManager, MissileCombiSettings> getAddOn() {
		return this.addOn;
	}
	
	@Override
	protected String getTag() {
		return "missile";
	}
	
	@Override
	public DumbMissileCollectionManager getNewCollectionManager(final SegmentPiece segmentPiece, final Class<DumbMissileCollectionManager> clazz) {
		return new DumbMissileCollectionManager(segmentPiece, this.getSegmentController(), this);
	}
	
	@Override
	public String getManagerName() {
		return Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_MISSILE_DUMB_DUMBMISSILEELEMENTMANAGER_13;
	}
	
	protected void playSound(final DumbMissileUnit dumbMissileUnit, final Transform transform) {
		((GameClientController)this.getState().getController()).queueTransformableAudio("0022_spaceship user - missile fire one", transform, 1.0f);
	}
	
	@Override
	public boolean isUsingRegisteredActivation() {
		return true;
	}
	
	static {
		DumbMissileElementManager.BASE_DAMAGE = new FloatReactorDualConfigElement();
		DumbMissileElementManager.BASE_DISTANCE = 1000.0f;
		DumbMissileElementManager.BASE_SPEED = 1.1f;
		DumbMissileElementManager.BASE_RELOAD = 10000.0f;
		DumbMissileElementManager.REACTOR_POWER_CONSUMPTION_RESTING = 10.0f;
		DumbMissileElementManager.ADDITIONAL_CAPACITY_USER_PER_DAMAGE = 10.0f;
		DumbMissileElementManager.ADDITIONAL_CAPACITY_USER_PER_DAMAGE_MULT = 10.0f;
		DumbMissileElementManager.PERCENTAGE_POWER_USAGE_CHARGING = 10.0f;
		DumbMissileElementManager.PERCENTAGE_POWER_USAGE_RESTING = 10.0f;
		DumbMissileElementManager.REACTOR_POWER_CONSUMPTION_CHARGING = 10.0f;
		DumbMissileElementManager.BASE_POWER_CONSUMPTION = 200.0f;
		DumbMissileElementManager.ADDITIONAL_POWER_CONSUMPTION_PER_UNIT_MULT = 0.1f;
		DumbMissileElementManager.CHASING_TURN_SPEED_WITH_TARGET_IN_FRONT = 2.03f;
		DumbMissileElementManager.CHASING_TURN_SPEED_WITH_TARGET_IN_BACK = 1.1f;
		DumbMissileElementManager.BOMB_ACTIVATION_TIME_SEC = 1.1f;
		DumbMissileElementManager.MISSILE_HP_CALC_STYLE = UnitCalcStyle.LINEAR;
		DumbMissileElementManager.MISSILE_HP_MIN = 10.0f;
		DumbMissileElementManager.MISSILE_HP_PER_DAMAGE = 1.0f;
		DumbMissileElementManager.MISSILE_HP_EXP = 1.0f;
		DumbMissileElementManager.MISSILE_HP_EXP_MULT = 1.0f;
		DumbMissileElementManager.MISSILE_HP_LOG_OFFSET = 10.0f;
		DumbMissileElementManager.MISSILE_HP_LOG_FACTOR = 10.0f;
		DumbMissileElementManager.basicEffectConfiguration = new InterEffectSet();
		DumbMissileElementManager.POSSIBLE_ZOOM = 0.0f;
	}
}
