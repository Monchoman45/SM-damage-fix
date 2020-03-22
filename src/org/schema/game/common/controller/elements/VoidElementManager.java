// 
// Decompiled by Procyon v0.5.36
// 

package org.schema.game.common.controller.elements;

import java.io.File;
import org.schema.schine.graphicsengine.core.Timer;
import org.schema.game.common.data.player.ControllerStateInterface;
import com.bulletphysics.linearmath.Transform;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.client.view.gui.structurecontrol.ControllerManagerGUI;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.HpConditionList;
import org.schema.common.config.ConfigurationElement;
import org.schema.game.common.controller.damage.effects.InterEffectSet;
import org.schema.game.common.data.element.ElementCollection;

public class VoidElementManager<E extends ElementCollection<E, CM, VoidElementManager<E, CM>>, CM extends ElementCollectionManager<E, CM, VoidElementManager<E, CM>>> extends UsableControllableSingleElementManager<E, CM, VoidElementManager<E, CM>>
{
	@ConfigurationElement(name = "ShieldEffectConfiguration")
	public static InterEffectSet shieldEffectConfiguration;
	@ConfigurationElement(name = "BasicEffectConfiguration")
	public static InterEffectSet basicEffectConfiguration;
	@ConfigurationElement(name = "ArmorEffectConfiguration")
	public static InterEffectSet armorEffectConfiguration;
	@ConfigurationElement(name = "IndividualBlockEffectArmorOnShieldHit")
	public static boolean individualBlockEffectArmorOnShieldHit;
	@ConfigurationElement(name = "VolumeMassMultiplier")
	public static float VOLUME_MASS_MULT;
	@ConfigurationElement(name = "DefensiveEffectMaxPercentMassMult", description = "multiplication of mass used as max percent for defensive effects")
	public static float DEVENSIVE_EFFECT_MAX_PERCENT_MASS_MULT;
	@ConfigurationElement(name = "ShieldExtraCapacityMultPerUnit", description = "size of each unit gets multiplied with this value")
	public static float SHIELD_EXTRA_CAPACITY_MULT_PER_UNIT;
	@ConfigurationElement(name = "ShieldExtraRechargeMultPerUnit", description = "size of each unit gets multiplied with this value")
	public static float SHIELD_EXTRA_RECHARGE_MULT_PER_UNIT;
	@ConfigurationElement(name = "ShieldDoInitialWithoutFromCore", description = "")
	public static boolean SHIELD_INITIAL_CORE;
	@ConfigurationElement(name = "ShieldCapacityInitial", description = "Initial Capacity")
	public static double SHIELD_CAPACITY_INITIAL;
	@ConfigurationElement(name = "ShieldRechargeInitial", description = "Initial Recharge")
	public static double SHIELD_RECHARGE_INITIAL;
	@ConfigurationElement(name = "ShieldCapacityPow", description = "((x*pre)^pow)*total")
	public static double SHIELD_CAPACITY_POW;
	@ConfigurationElement(name = "ShieldCapacityPreMul", description = "((x*pre)^pow)*total")
	public static double SHIELD_CAPACITY_PRE_POW_MUL;
	@ConfigurationElement(name = "ShieldCapacityTotalMul", description = "((x*pre)^pow)*total")
	public static double SHIELD_CAPACITY_TOTAL_MUL;
	@ConfigurationElement(name = "ShieldRechargePow", description = "((x*pre)^pow)*total")
	public static double SHIELD_RECHARGE_POW;
	@ConfigurationElement(name = "ShieldRechargePreMul", description = "((x*pre)^pow)*total")
	public static double SHIELD_RECHARGE_PRE_POW_MUL;
	@ConfigurationElement(name = "ShieldRechargeTotalMul", description = "((x*pre)^pow)*total")
	public static double SHIELD_RECHARGE_TOTAL_MUL;
	@ConfigurationElement(name = "ShieldRechargeCycleTime")
	public static double SHIELD_RECHARGE_CYCLE_TIME;
	@ConfigurationElement(name = "ShieldRecoveryTimeAfterOutage", description = "time in seconds for shield to start recharge again after reaching 0")
	public static int SHIELD_RECOVERY_TIME_IN_SEC;
	@ConfigurationElement(name = "ShieldDirectRecoveryTime", description = "")
	public static int SHIELD_DIRECT_RECOVERY_TIME_IN_SEC;
	@ConfigurationElement(name = "ShieldRecoveryMultPerPercent", description = "")
	public static float SHIELD_RECOVERY_NERF_MULT_PER_PERCENT;
	@ConfigurationElement(name = "ShieldRecoveryMult", description = "")
	public static float SHIELD_RECOVERY_NERF_MULT;
	@ConfigurationElement(name = "ShieldRechargePowerConsumption")
	public static int SHIELD_RECHARGE_POWER_CONSUMPTION;
	@ConfigurationElement(name = "ShieldFullPowerConsumption")
	public static int SHIELD_FULL_POWER_CONSUMPTION;
	@ConfigurationElement(name = "ShieldDockTransferLimit", description = "Up to which fill status a rail docked entity up in the chain (towards root) will take the hit")
	public static double SHIELD_DOCK_TRANSFER_LIMIT;
	@ConfigurationElement(name = "PowerDivFactor")
	public static double POWER_DIV_FACTOR;
	@ConfigurationElement(name = "PowerCeiling")
	public static double POWER_CEILING;
	@ConfigurationElement(name = "PowerGrowth")
	public static double POWER_GROWTH;
	@ConfigurationElement(name = "PowerLinearGrowth")
	public static double POWER_LINEAR_GROWTH;
	@ConfigurationElement(name = "PowerRecoveryTime")
	public static long POWER_RECOVERY_TIME;
	@ConfigurationElement(name = "PowerBaseCapacity")
	public static int POWER_FIXED_BASE_CAPACITY;
	@ConfigurationElement(name = "PowerTankCapacityLinear")
	public static float POWER_TANK_CAPACITY_LINEAR;
	@ConfigurationElement(name = "PowerTankCapacityPow")
	public static float POWER_TANK_CAPACITY_POW;
	@ConfigurationElement(name = "PowerBatteryCapacityLinear")
	public static float POWER_BATTERY_CAPACITY_LINEAR;
	@ConfigurationElement(name = "PowerBatteryCapacityPow")
	public static float POWER_BATTERY_CAPACITY_POW;
	@ConfigurationElement(name = "PowerBatteryTransferPercentRatePerSec")
	public static float POWER_BATTERY_TRANSFER_RATE_PER_SEC;
	@ConfigurationElement(name = "PowerBatteryLinearGrowth")
	public static double POWER_BATTERY_LINEAR_GROWTH;
	@ConfigurationElement(name = "PowerBatteryGroupMultiplier")
	public static double POWER_BATTERY_GROUP_MULTIPLIER;
	@ConfigurationElement(name = "PowerBatteryGroupPow")
	public static double POWER_BATTERY_GROUP_POW;
	@ConfigurationElement(name = "PowerBatteryGroupGrowth")
	public static double POWER_BATTERY_GROUP_GROWTH;
	@ConfigurationElement(name = "PowerBatteryGroupCeiling")
	public static double POWER_BATTERY_GROUP_CEILING;
	@ConfigurationElement(name = "PowerBatteryTurnedOnRegenMultiplier")
	public static double POWER_BATTERY_TURNED_ON_MULT;
	@ConfigurationElement(name = "PowerBatteryTurnedOffRegenMultiplier")
	public static double POWER_BATTERY_TURNED_OFF_MULT;
	@ConfigurationElement(name = "PowerBatteryTransferTopOffOnly")
	public static boolean POWER_BATTERY_TRANSFER_TOP_OFF_ONLY;
	@ConfigurationElement(name = "PowerBatteryExplosionsPerSecond")
	public static double POWER_BATTERY_EXPLOSION_RATE;
	@ConfigurationElement(name = "PowerBatteryExplosionRadiusPerBlocksInGroup")
	public static double POWER_BATTERY_EXPLOSION_RADIUS_PER_BLOCKS;
	@ConfigurationElement(name = "PowerBatteryExplosionRadiusMax")
	public static double POWER_BATTERY_EXPLOSION_RADIUS_MAX;
	@ConfigurationElement(name = "PowerBatteryExplosionDamagePerBlocksInGroup")
	public static double POWER_BATTERY_EXPLOSION_DAMAGE_PER_BLOCKS;
	@ConfigurationElement(name = "PowerBatteryExplosionDamageMax")
	public static double POWER_BATTERY_EXPLOSION_DAMAGE_MAX;
	@ConfigurationElement(name = "PowerBatteryExplosionCountPerBlocksInGroup")
	public static double POWER_BATTERY_EXPLOSION_COUNT_PER_BLOCKS;
	@ConfigurationElement(name = "PowerBatteryExplosionCountMax")
	public static int POWER_BATTERY_EXPLOSION_COUNT_MAX;
	@ConfigurationElement(name = "PowerBatteryExplosionCountMaxPercent")
	public static double POWER_BATTERY_EXPLOSION_COUNT_PERCENT;
	@ConfigurationElement(name = "EvadeEffectPowerConsumptionMult")
	public static float EVADE_EFFECT_POWER_CONSUMPTION_MULT;
	@ConfigurationElement(name = "TakeOffEffectPowerConsumptionMult")
	public static float TAKE_OFF_EFFECT_POWER_CONSUMPTION_MULT;
	@ConfigurationElement(name = "PersonalSalvageBeamBonus", description = "bonus mult of raw resources when salvaging with handheld salvage beam")
	public static float PERSONAL_SALVAGE_BEAM_BONUS;
	@ConfigurationElement(name = "RailMassEnhancerFreeMass")
	public static float RAIL_MASS_ENHANCER_FREE_MASS;
	@ConfigurationElement(name = "RailMassEnhancerMassPerEnhancer")
	public static float RAIL_MASS_ENHANCER_MASS_ADDED_PER_ENHANCER;
	@ConfigurationElement(name = "RailMassEnhancerPowerConsumedPerEnhancer")
	public static double RAIL_MASS_ENHANCER_POWER_CONSUMED_PER_ENHANCER;
	@ConfigurationElement(name = "RailMassEnhancerPercentCostPerMassAboveEnhancerProvided")
	public static float RAIL_MASS_ENHANCER_PERCENT_COST_PER_MASS_ABOVE_ENHANCER_PROVIDED;
	@ConfigurationElement(name = "RailMassEnhancerReactorPowerConsumptionResting")
	public static float RAIL_MASS_ENHANCER_REACTOR_POWER_CONSUMPTION_RESTING;
	@ConfigurationElement(name = "RailMassEnhancerReactorPowerConsumptionCharging")
	public static float RAIL_MASS_ENHANCER_REACTOR_POWER_CONSUMPTION_CHARGING;
	@ConfigurationElement(name = "PlanetPowerBaseCapacity")
	public static int POWER_FIXED_PLANET_BASE_CAPACITY;
	@ConfigurationElement(name = "AsteroidPowerBaseCapacity")
	public static int POWER_FIXED_ASTEROID_BASE_CAPACITY;
	@ConfigurationElement(name = "ShipRebootTimeInSecPerMissingHpPercent")
	public static double SHIP_REBOOT_TIME_IN_SEC_PER_MISSING_HP_PERCENT;
	@ConfigurationElement(name = "ShipRebootTimeMultiplierPerMass")
	public static double SHIP_REBOOT_TIME_MULTIPLYER_PER_MASS;
	@ConfigurationElement(name = "ShipRebootTimeMinSec")
	public static double SHIP_REBOOT_TIME_MIN_SEC;
	@ConfigurationElement(name = "HpConditionTriggerList")
	public static final HpConditionList HP_CONDITION_TRIGGER_LIST;
	@ConfigurationElement(name = "HpDeductionLogFactor")
	public static float HP_DEDUCTION_LOG_FACTOR;
	@ConfigurationElement(name = "HpDeductionLogOffset")
	public static float HP_DEDUCTION_LOG_OFFSET;
	@ConfigurationElement(name = "StructureHpBlockMultiplier")
	public static double STRUCTURE_HP_BLOCK_MULTIPLIER;
	@ConfigurationElement(name = "AITurretMinOrientationSpeed")
	public static float AI_TURRET_ORIENTATION_SPEED_MIN;
	@ConfigurationElement(name = "AITurretMaxOrientationSpeed")
	public static float AI_TURRET_ORIENTATION_SPEED_MAX;
	@ConfigurationElement(name = "AITurretOrientationSpeedDivByMass")
	public static float AI_TURRET_ORIENTATION_SPEED_DIV_BY_MASS;
	@ConfigurationElement(name = "ExplosionShieldDamageBonus", description = "")
	public static float EXPLOSION_SHIELD_DAMAGE_BONUS;
	@ConfigurationElement(name = "ExplosionHullDamageBonus", description = "")
	public static float EXPLOSION_HULL_DAMAGE_BONUS;
	@ConfigurationElement(name = "ReactorChamberBlocksPerMainReactor")
	public static float REACTOR_CHAMBER_BLOCKS_PER_MAIN_REACTOR_AND_LEVEL;
	@ConfigurationElement(name = "ReactorConduitPowerConsuptionPerSec")
	public static float POWER_REACTOR_CONDUIT_POWER_CONSUMPTION_PER_SEC;
	@ConfigurationElement(name = "ReactorSwitchCooldownSec")
	public static float REACTOR_SWITCH_COOLDOWN_SEC;
	@ConfigurationElement(name = "ReactorMainCountMultiplier")
	public static float REACTOR_MAIN_COUNT_MULTIPLIER;
	@ConfigurationElement(name = "ReactorPowerCapacityMultiplier")
	public static float REACTOR_POWER_CAPACITY_MULTIPLIER;
	@ConfigurationElement(name = "ReactorRechargePercentPerSecond")
	public static float REACTOR_RECHARGE_PERCENT_PER_SECOND;
	@ConfigurationElement(name = "ReactorRechargeMultiplierWhenEmpty")
	public static float REACTOR_RECHARGE_EMPTY_MULTIPLIER;
	@ConfigurationElement(name = "ReactorStabilizerLinearFalloffOne")
	public static float REACTOR_STABILIZER_LINEAR_FALLOFF_ONE;
	@ConfigurationElement(name = "ReactorStabilizerLinearFalloffZero")
	public static float REACTOR_STABILIZER_LINEAR_FALLOFF_ZERO;
	@ConfigurationElement(name = "ReactorStabilizerFreeMainReactorBlocks")
	public static int REACTOR_STABILIZER_FREE_MAIN_REACTOR_BLOCKS;
	@ConfigurationElement(name = "ReactorStabilizerDistanceTotalMult")
	public static float REACTOR_STABILIZER_DISTANCE_TOTAL_MULT;
	@ConfigurationElement(name = "ReactorStabilizationMultiplier")
	public static float REACTOR_STABILIZATION_MULTIPLIER;
	@ConfigurationElement(name = "ReactorStabilizerDistanceLogLeveledSteps")
	public static boolean REACTOR_STABILIZER_DISTANCE_LOG_LEVELED_STEPS;
	@ConfigurationElement(name = "ReactorStabilizerDistanceLogLeveledMultiplier")
	public static float REACTOR_STABILIZER_DISTANCE_LOG_LEVELED_MULTIPLIER;
	@ConfigurationElement(name = "ReactorStabilizerDistanceLogLeveledExp")
	public static float REACTOR_STABILIZER_DISTANCE_LOG_LEVELED_EXP;
	@ConfigurationElement(name = "ReactorCalcStyle", description = "LINEAR, EXP, LOG, LOG_LEVELED")
	public static UnitCalcStyle REACTOR_CALC_STYLE;
	@ConfigurationElement(name = "ReactorStabilizerDistancePerMainReactorBlock", description = "ReactorStabilizerStartingDistance + blocks * ReactorStabilizerDistancePerMainReactorBlock")
	public static float REACTOR_STABILIZER_DISTANCE_PER_MAIN_REACTOR_BLOCK;
	@ConfigurationElement(name = "ReactorStabilizerDistanceExpMult", description = "ReactorStabilizerStartingDistance + (blocks ^ ReactorStabilizerDistanceExp) * ReactorStabilizerDistanceExp")
	public static float REACTOR_STABILIZER_DISTANCE_EXP_MULT;
	@ConfigurationElement(name = "ReactorStabilizerDistanceExp")
	public static float REACTOR_STABILIZER_DISTANCE_EXP;
	@ConfigurationElement(name = "ReactorStabilizerDistanceExpSoftcapMult")
	public static float REACTOR_STABILIZER_DISTANCE_EXP_SOFTCAP_MULT;
	@ConfigurationElement(name = "ReactorStabilizerDistanceExpSoftcapExp")
	public static float REACTOR_STABILIZER_DISTANCE_EXP_SOFTCAP_EXP;
	@ConfigurationElement(name = "ReactorStabilizerDistanceExpSoftCapBlocksStart")
	public static float REACTOR_STABILIZER_DISTANCE_EXP_SOFTCAP_BLOCKS_START;
	@ConfigurationElement(name = "ReactorStabilizerDistanceLogFactor", description = "ReactorStabilizerStartingDistance + (Log10(blocks) + ReactorStabilizerDistanceLogOffset) * ReactorStabilizerDistanceLogFactor")
	public static float REACTOR_STABILIZER_DISTANCE_LOG_FACTOR;
	@ConfigurationElement(name = "ReactorStabilizerDistanceLogOffset")
	public static float REACTOR_STABILIZER_DISTANCE_LOG_OFFSET;
	@ConfigurationElement(name = "ReactorStabilizerStartingDistance")
	public static float REACTOR_STABILIZER_STARTING_DISTANCE;
	@ConfigurationElement(name = "StabilizerBonusCalc")
	public static StabBonusCalcStyle STABILIZER_BONUS_CALC;
	@ConfigurationElement(name = "ReactorStablizationAngleBonus2Groups")
	public static float STABILIZATION_ANGLE_BONUS_2_GROUPS;
	@ConfigurationElement(name = "ReactorStablizationAngleBonus3Groups")
	public static float STABILIZATION_ANGLE_BONUS_3_GROUPS;
	@ConfigurationElement(name = "ReactorStablizationAngleBonus4Groups")
	public static float STABILIZATION_ANGLE_BONUS_4_GROUPS;
	@ConfigurationElement(name = "ReactorStablizationAngleBonus5Groups")
	public static float STABILIZATION_ANGLE_BONUS_5_GROUPS;
	@ConfigurationElement(name = "ReactorStablizationAngleBonus6Groups")
	public static float STABILIZATION_ANGLE_BONUS_6_GROUPS;
	@ConfigurationElement(name = "ReactorStablizationBonus2")
	public static float STABILIZATION_DIMENSION_BONUS_2;
	@ConfigurationElement(name = "ReactorStablizationBonus3")
	public static float STABILIZATION_DIMENSION_BONUS_3;
	@ConfigurationElement(name = "ReactorStablizationBonus4")
	public static float STABILIZATION_DIMENSION_BONUS_4;
	@ConfigurationElement(name = "ReactorStablizationBonus5")
	public static float STABILIZATION_DIMENSION_BONUS_5;
	@ConfigurationElement(name = "ReactorStablizationBonus6")
	public static float STABILIZATION_DIMENSION_BONUS_6;
	@ConfigurationElement(name = "ReactorLowStabilizationExtraDamageStart")
	public static float REACTOR_LOW_STABILIZATION_EXTRA_DAMAGE_START;
	@ConfigurationElement(name = "ReactorLowStabilizationExtraDamageEnd")
	public static float REACTOR_LOW_STABILIZATION_EXTRA_DAMAGE_END;
	@ConfigurationElement(name = "ReactorLowStabilizationExtraDamageStartDamage")
	public static float REACTOR_LOW_STABILIZATION_EXTRA_DAMAGE_START_DAMAGE;
	@ConfigurationElement(name = "ReactorLowStabilizationExtraDamageEndDamage")
	public static float REACTOR_LOW_STABILIZATION_EXTRA_DAMAGE_END_DAMAGE;
	@ConfigurationElement(name = "ReactorJumpPowerConsumptionRestingPerMass")
	public static float REACTOR_JUMP_POWER_CONSUMPTION_RESTING_PER_MASS;
	@ConfigurationElement(name = "ReactorJumpPowerConsumptionChargingPerMass")
	public static float REACTOR_JUMP_POWER_CONSUMPTION_CHARGING_PER_MASS;
	@ConfigurationElement(name = "ReactorJumpDistanceDefault")
	public static float REACTOR_JUMP_DISTANCE_DEFAULT;
	@ConfigurationElement(name = "ReactorJumpChargeNeededInSecondsDefault")
	public static float REACTOR_JUMP_CHARGE_NEEDED_IN_SEC;
	@ConfigurationElement(name = "ReactorJumpChargeNeededInSecondsExtraPerMass")
	public static float REACTOR_JUMP_CHARGE_NEEDED_IN_SEC_EXTRA_PER_MASS;
	@ConfigurationElement(name = "ReactorJumpChargeNeededInSecondsLogFactor")
	public static float REACTOR_JUMP_CHARGE_NEEDED_IN_SEC_LOG_FACTOR;
	@ConfigurationElement(name = "ReactorJumpChargeNeededInSecondsLogOffset")
	public static float REACTOR_JUMP_CHARGE_NEEDED_IN_SEC_LOG_OFFSET;
	@ConfigurationElement(name = "ReactorStealthChargeNeeded")
	public static float STEALTH_CHARGE_NEEDED;
	@ConfigurationElement(name = "ReactorStealthChargeConsumptionResting")
	public static float STEALTH_CONSUMPTION_RESTING;
	@ConfigurationElement(name = "ReactorStealthChargeConsumptionCharging")
	public static float STEALTH_CONSUMPTION_CHARGING;
	@ConfigurationElement(name = "ReactorStealthChargeConsumptionRestingAddedByMass")
	public static float STEALTH_CONSUMPTION_RESTING_ADDED_BY_MASS;
	@ConfigurationElement(name = "ReactorStealthChargeConsumptionChargingAddedByMass")
	public static float STEALTH_CONSUMPTION_CHARGING_ADDED_BY_MASS;
	@ConfigurationElement(name = "ReactorStealthDurationBasic")
	public static float STEALTH_DURATION_BASIC;
	@ConfigurationElement(name = "ReactorStealthStrengthBasic")
	public static float STEALTH_STRENGTH_BASIC;
	@ConfigurationElement(name = "ReactorScanChargeNeeded")
	public static float SCAN_CHARGE_NEEDED;
	@ConfigurationElement(name = "ReactorScanChargeConsumptionResting")
	public static float SCAN_CONSUMPTION_RESTING;
	@ConfigurationElement(name = "ReactorScanChargeConsumptionCharging")
	public static float SCAN_CONSUMPTION_CHARGING;
	@ConfigurationElement(name = "ReactorScanChargeConsumptionRestingAddedByMass")
	public static float SCAN_CONSUMPTION_RESTING_ADDED_BY_MASS;
	@ConfigurationElement(name = "ReactorScanChargeConsumptionChargingAddedByMass")
	public static float SCAN_CONSUMPTION_CHARGING_ADDED_BY_MASS;
	@ConfigurationElement(name = "ReactorScanDurationBasic")
	public static float SCAN_DURATION_BASIC;
	@ConfigurationElement(name = "ReactorScanStrengthBasic")
	public static float SCAN_STRENGTH_BASIC;
	@ConfigurationElement(name = "ReconDifferenceMinCloaking")
	public static int RECON_DIFFERENCE_MIN_CLOAKING;
	@ConfigurationElement(name = "ReconDifferenceMinJamming")
	public static int RECON_DIFFERENCE_MIN_JAMMING;
	@ConfigurationElement(name = "ReconDifferenceMinReactor")
	public static int RECON_DIFFERENCE_MIN_REACTOR;
	@ConfigurationElement(name = "ReconDifferenceMinChambers")
	public static int RECON_DIFFERENCE_MIN_CHAMBERS;
	@ConfigurationElement(name = "ReconDifferenceMinWeapons")
	public static int RECON_DIFFERENCE_MIN_WEAPONS;
	@ConfigurationElement(name = "ReactorExplosionStabilityMargin")
	public static double REACTOR_EXPLOSION_STABILITY;
	@ConfigurationElement(name = "ReactorExplosionStabilityLossMult")
	public static double REACTOR_EXPLOSION_STABILITY_LOSS_MULT;
	@ConfigurationElement(name = "ReactorExplosionsPerSecond")
	public static double REACTOR_EXPLOSION_RATE;
	@ConfigurationElement(name = "ReactorExplosionRadiusPerBlocksInGroup")
	public static double REACTOR_EXPLOSION_RADIUS_PER_BLOCKS;
	@ConfigurationElement(name = "ReactorExplosionRadiusMax")
	public static double REACTOR_EXPLOSION_RADIUS_MAX;
	@ConfigurationElement(name = "ReactorExplosionDamagePerBlocksInGroup")
	public static double REACTOR_EXPLOSION_DAMAGE_PER_BLOCKS;
	@ConfigurationElement(name = "ReactorExplosionDamageMax")
	public static double REACTOR_EXPLOSION_DAMAGE_MAX;
	@ConfigurationElement(name = "ReactorExplosionCountPerBlocksInGroup")
	public static double REACTOR_EXPLOSION_COUNT_PER_BLOCKS;
	@ConfigurationElement(name = "ReactorExplosionCountMax")
	public static int REACTOR_EXPLOSION_COUNT_MAX;
	@ConfigurationElement(name = "ReactorExplosionCountMaxPercent")
	public static double REACTOR_EXPLOSION_COUNT_PERCENT;
	@ConfigurationElement(name = "ReactorModuleDischargeMargin")
	public static double REACTOR_MODULE_DISCHARGE_MARGIN;
	@ConfigurationElement(name = "ShieldLocalCapacityPerBlock")
	public static float SHIELD_LOCAL_CAPACITY_PER_BLOCK;
	@ConfigurationElement(name = "ShieldLocalRechargePerBlock")
	public static float SHIELD_LOCAL_RECHARGE_PER_BLOCK;
	@ConfigurationElement(name = "ShieldLocalDefaultCapacity")
	public static float SHIELD_LOCAL_DEFAULT_CAPACITY;
	@ConfigurationElement(name = "ShieldLocalRadiusCalcStyle", description = "LINEAR, EXP, LOG")
	public static UnitCalcStyle SHIELD_LOCAL_RADIUS_CALC_STYLE;
	@ConfigurationElement(name = "ReactorLevelCalcStyle", description = "LOG10, LINEAR")
	public static ReactorLevelCalcStyle REACTOR_LEVEL_CALC_STYLE;
	@ConfigurationElement(name = "ReactorLevelCalcLinearBlocksNeededPerLevel")
	public static int REACTOR_LEVEL_CALC_LINEAR_BLOCKS_NEEDED_PER_LEVEL;
	@ConfigurationElement(name = "ShieldLocalDefaultRadius")
	public static float SHIELD_LOCAL_DEFAULT_RADIUS;
	@ConfigurationElement(name = "ShieldUpkeepPerSecondOfTotalCapacity")
	public static float SHIELD_LOCAL_UPKEEP_PER_SECOND_OF_TOTAL_CAPACITY;
	@ConfigurationElement(name = "ShieldLocalPowerConsumptionPerRechargePerSecondResting")
	public static float SHIELD_LOCAL_CONSUMPTION_PER_CURRENT_RECHARGE_PER_SECOND_RESTING;
	@ConfigurationElement(name = "ShieldLocalPowerConsumptionPerRechargePerSecondCharging")
	public static float SHIELD_LOCAL_CONSUMPTION_PER_CURRENT_RECHARGE_PER_SECOND_CHARGING;
	@ConfigurationElement(name = "ShieldLocalRechargeUnderFireModeSec")
	public static float SHIELD_LOCAL_RECHARGE_UNDER_FIRE_MODE_SEC;
	@ConfigurationElement(name = "ShieldLocalRechargeUnderFireMinPercent")
	public static float SHIELD_LOCAL_RECHARGE_UNDER_FIRE_MIN_PERCENT;
	@ConfigurationElement(name = "ShieldLocalRechargeUnderFireStartAtCharged")
	public static float SHIELD_LOCAL_RECHARGE_UNDER_FIRE_START_AT_CHARGED;
	@ConfigurationElement(name = "ShieldLocalRechargeUnderFireEndAtCharged")
	public static float SHIELD_LOCAL_RECHARGE_UNDER_FIRE_END_AT_CHARGED;
	@ConfigurationElement(name = "ShieldLocalOnZeroShieldsRechargePreventionSec")
	public static float SHIELD_LOCAL_ON_ZERO_SHIELDS_RECHARGE_PREVENTION_SEC;
	@ConfigurationElement(name = "ShieldLocalHitAllOverlapping")
	public static boolean SHIELD_LOCAL_HIT_ALL_OVERLAPPING;
	@ConfigurationElement(name = "ShieldLocalMaxCapacityGroupsPerLocalShield")
	public static int SHIELD_LOCAL_MAX_CAPACITY_GROUPS_PER_LOCAL_SHIELD;
	@ConfigurationElement(name = "ShieldLocalRadiusPerRechargeBlock")
	public static float SHIELD_LOCAL_RADIUS_PER_RECHARGE_BLOCK;
	@ConfigurationElement(name = "ShieldLocalRadiusExpMult")
	public static float SHIELD_LOCAL_RADIUS_EXP_MULT;
	@ConfigurationElement(name = "ShieldLocalRadiusExp")
	public static float SHIELD_LOCAL_RADIUS_EXP;
	@ConfigurationElement(name = "ShieldLocalRadiusLogFactor")
	public static float SHIELD_LOCAL_RADIUS_LOG_FACTOR;
	@ConfigurationElement(name = "ShieldLocalRadiusLogOffset")
	public static float SHIELD_LOCAL_RADIUS_LOG_OFFSET;
	@ConfigurationElement(name = "CollectionIntegrityStartValue")
	public static double COLLECTION_INTEGRITY_START_VALUE;
	@ConfigurationElement(name = "CollectionIntegrityBaseTouching0")
	public static double COLLECTION_INTEGRITY_BASE_TOUCHING_0;
	@ConfigurationElement(name = "CollectionIntegrityBaseTouching1")
	public static double COLLECTION_INTEGRITY_BASE_TOUCHING_1;
	@ConfigurationElement(name = "CollectionIntegrityBaseTouching2")
	public static double COLLECTION_INTEGRITY_BASE_TOUCHING_2;
	@ConfigurationElement(name = "CollectionIntegrityBaseTouching3")
	public static double COLLECTION_INTEGRITY_BASE_TOUCHING_3;
	@ConfigurationElement(name = "CollectionIntegrityBaseTouching4")
	public static double COLLECTION_INTEGRITY_BASE_TOUCHING_4;
	@ConfigurationElement(name = "CollectionIntegrityBaseTouching5")
	public static double COLLECTION_INTEGRITY_BASE_TOUCHING_5;
	@ConfigurationElement(name = "CollectionIntegrityBaseTouching6")
	public static double COLLECTION_INTEGRITY_BASE_TOUCHING_6;
	@ConfigurationElement(name = "CollectionIntegrityMargin")
	public static double INTEGRITY_MARGIN;
	@ConfigurationElement(name = "CollectionIntegrityExplosionAmount")
	public static int COLLECTION_INTEGRITY_EXPLOSION_AMOUNT;
	@ConfigurationElement(name = "CollectionIntegrityExplosionRadius")
	public static int COLLECTION_INTEGRITY_EXPLOSION_RADIUS;
	@ConfigurationElement(name = "OverheatTimerMin")
	public static long OVERHEAT_TIMER_MIN;
	@ConfigurationElement(name = "OverheatTimerMax")
	public static long OVERHEAT_TIMER_MAX;
	@ConfigurationElement(name = "OverheatTimerAddedSecondsPerBlock")
	public static float OVERHEAT_TIMER_ADDED_PER_BLOCK;
	@ConfigurationElement(name = "CollectionIntegrityExplosionRate")
	public static long COLLECTION_INTEGRITY_EXPLOSION_RATE;
	@ConfigurationElement(name = "CollectionIntegrityExplosionDamagePerBlocksInGroup")
	public static double COLLECTION_INTEGRITY_DAMAGE_PER_BLOCKS;
	@ConfigurationElement(name = "CollectionIntegrityExplosionDamageMax")
	public static double COLLECTION_INTEGRITY_DAMAGE_MAX;
	@ConfigurationElement(name = "ReactorStabilizerGroupingProximity")
	public static float REACTOR_STABILIZER_GROUPING_PROXIMITY;
	@ConfigurationElement(name = "ReactorStabilizerPathRadiusDefault")
	public static float REACTOR_STABILIZER_PATH_RADIUS_DEFAULT;
	@ConfigurationElement(name = "ReactorStabilizerPathRadiusPerLevel")
	public static float REACTOR_STABILIZER_PATH_RADIUS_PER_LEVEL;
	@ConfigurationElement(name = "ReactorStabilizationPowerEffectiveFull")
	public static float REACTOR_STABILIZATION_POWER_EFFECTIVE_FULL;
	@ConfigurationElement(name = "ReactorStabilizerGroupsMax")
	public static int REACTOR_STABILIZER_GROUPS_MAX;
	public static final String configPath;
	public static final String configPathHOWTO;
	@ConfigurationElement(name = "CollectionIntegrityUnderFireUpdateDelaySec")
	public static float COLLECTION_INTEGRITY_UNDER_FIRE_UPDATE_DELAY_SEC;
	@ConfigurationElement(name = "RepulseMultiplicator")
	public static float REPULSE_MULT;
	@ConfigurationElement(name = "ArmorThicknessBonus")
	public static float ARMOR_THICKNESS_BONUS;
	@ConfigurationElement(name = "ArmorBeamDamageResistance")
	public static float ARMOR_BEAM_DAMAGE_SCALING;
	@ConfigurationElement(name = "ArmorCalcStyle")
	public static ArmorDamageCalcStyle ARMOR_CALC_STYLE;
	@ConfigurationElement(name = "CannonArmorFlatDamageReduction")
	public static float CANNON_ARMOR_FLAT_DAMAGE_REDUCTION;
	@ConfigurationElement(name = "CannonArmorThicknessDamageReduction")
	public static float CANNON_ARMOR_THICKNESS_DAMAGE_REDUCTION;
	@ConfigurationElement(name = "CannonArmorThicknessDamageReductionMax")
	public static float CANNON_ARMOR_THICKNESS_DAMAGE_REDUCTION_MAX;
	@ConfigurationElement(name = "BeamArmorFlatDamageReduction")
	public static float BEAM_ARMOR_FLAT_DAMAGE_REDUCTION;
	@ConfigurationElement(name = "BeamArmorThicknessDamageReduction")
	public static float BEAM_ARMOR_THICKNESS_DAMAGE_REDUCTION;
	@ConfigurationElement(name = "BeamArmorThicknessDamageReductionMax")
	public static float BEAM_ARMOR_THICKNESS_DAMAGE_REDUCTION_MAX;
	@ConfigurationElement(name = "CannonArmorExponentialIncomingExponent")
	public static float CANNON_ARMOR_EXPONENTIAL_INCOMING_EXPONENT;
	@ConfigurationElement(name = "CannonArmorExponentialArmorValueTotalExponent")
	public static float CANNON_ARMOR_EXPONENTIAL_ARMOR_VALUE_TOTAL_EXPONENT;
	@ConfigurationElement(name = "CannonArmorExponentialIncomingDamageAddedExponent")
	public static float CANNON_ARMOR_EXPONENTIAL_INCOMING_DAMAGE_ADDED_EXPONENT;
	@ConfigurationElement(name = "BeamArmorExponentialIncomingExponent")
	public static float BEAM_ARMOR_EXPONENTIAL_INCOMING_EXPONENT;
	@ConfigurationElement(name = "BeamArmorExponentialArmorValueTotalExponent")
	public static float BEAM_ARMOR_EXPONENTIAL_ARMOR_VALUE_TOTAL_EXPONENT;
	@ConfigurationElement(name = "BeamArmorExponentialIncomingDamageAddedExponent")
	public static float BEAM_ARMOR_EXPONENTIAL_INCOMING_DAMAGE_ADDED_EXPONENT;
	@ConfigurationElement(name = "ArmorOverPenetrationMarginMultiplicator")
	public static float ARMOR_OVER_PENETRATION_MARGIN_MULTIPLICATOR;
	@ConfigurationElement(name = "NonArmorOverpenetrationMargin")
	public static float NON_ARMOR_OVER_PENETRATION_MARGIN;
	@ConfigurationElement(name = "ReactorRebootMinCooldownSec")
	public static float REACTOR_REBOOT_MIN_COOLDOWN_SEC;
	@ConfigurationElement(name = "ReactorRebootLogFactor")
	public static float REACTOR_REBOOT_LOG_FACTOR;
	@ConfigurationElement(name = "ReactorRebootLogOffset")
	public static float REACTOR_REBOOT_LOG_OFFSET;
	@ConfigurationElement(name = "ReactorRebootCooldownInSecPerMissingHpPercent")
	public static float REACTOR_REBOOT_SEC_PER_HP_PERCENT;
	@ConfigurationElement(name = "ReactorStabilizationEnergyStreamHitCooldownPerDamageInSec")
	public static float REACTOR_STABILIZATION_ENERGY_STREAM_HIT_COOLDOWN_PER_DAMAGE_IN_SEC;
	@ConfigurationElement(name = "ReactorStabilizationEnergyStreamHitMinCooldownInSec")
	public static float REACTOR_STABILIZATION_ENERGY_STREAM_HIT_MIN_COOLDOWN_IN_SEC;
	@ConfigurationElement(name = "ReactorStabilizationEnergyStreamHitMaxCooldownInSec")
	public static float REACTOR_STABILIZATION_ENERGY_STREAM_HIT_MAX_COOLDOWN_IN_SEC;
	@ConfigurationElement(name = "ReactorStabilizationEnergyStreamDistance")
	public static float REACTOR_STABILIZATION_ENERGY_STREAM_DISTANCE;
	@ConfigurationElement(name = "ReactorStabilizationEnergyStreamHitMaxCooldownReactorEfficiency")
	public static float REACTOR_STABILIZATION_ENERGY_STREAM_HIT_COOLDOWN_REACTOR_EFFICIENCY;
	@ConfigurationElement(name = "AcidDamageArmorStoppedMargin")
	public static float ACID_DAMAGE_ARMOR_STOPPED_MARGIN;
	//#XXX: new armor counter, see ArmorValue.countArmor
	@ConfigurationElement(name = "ArmorRaycastLength")
	public static float ARMOR_RAYCAST_LENGTH;
	//#XXX:
	
	public static double getIntegrityBaseTouching(final int i) {
		switch (i) {
			case 0: {
				return VoidElementManager.COLLECTION_INTEGRITY_BASE_TOUCHING_0;
			}
			case 1: {
				return VoidElementManager.COLLECTION_INTEGRITY_BASE_TOUCHING_1;
			}
			case 2: {
				return VoidElementManager.COLLECTION_INTEGRITY_BASE_TOUCHING_2;
			}
			case 3: {
				return VoidElementManager.COLLECTION_INTEGRITY_BASE_TOUCHING_3;
			}
			case 4: {
				return VoidElementManager.COLLECTION_INTEGRITY_BASE_TOUCHING_4;
			}
			case 5: {
				return VoidElementManager.COLLECTION_INTEGRITY_BASE_TOUCHING_5;
			}
			case 6: {
				return VoidElementManager.COLLECTION_INTEGRITY_BASE_TOUCHING_6;
			}
			default: {
				throw new RuntimeException("Illegal amount of touching " + i);
			}
		}
	}
	
	public VoidElementManager(final SegmentController segmentController, final Class<CM> clazz) {
		super(segmentController, clazz);
	}
	
	@Override
	public void onControllerChange() {
	}
	
	@Override
	public ControllerManagerGUI getGUIUnitValues(final E e, final CM cm, final ControlBlockElementCollectionManager<?, ?, ?> controlBlockElementCollectionManager, final ControlBlockElementCollectionManager<?, ?, ?> controlBlockElementCollectionManager2) {
		assert false;
		throw new IllegalArgumentException();
	}
	
	@Override
	protected String getTag() {
		return "general";
	}
	
	@Override
	public CM getNewCollectionManager(final SegmentPiece segmentPiece, final Class<CM> clazz) {
		try {
			return clazz.getConstructor(SegmentController.class, VoidElementManager.class).newInstance(this.getSegmentController(), this);
		}
		catch (Exception ex) {
			final Exception cause = ex;
			ex.printStackTrace();
			throw new RuntimeException(cause);
		}
	}
	
	@Override
	protected void playSound(final E e, final Transform transform) {
	}
	
	@Override
	public void handle(final ControllerStateInterface controllerStateInterface, final Timer timer) {
	}
	
	public static boolean isUsingReactorDistance() {
		return VoidElementManager.REACTOR_STABILIZER_LINEAR_FALLOFF_ONE <= 1.0E-6f;
	}
	
	public static boolean hasAngleStabBonus() {
		return VoidElementManager.STABILIZER_BONUS_CALC == StabBonusCalcStyle.BY_ANGLE && VoidElementManager.STABILIZATION_ANGLE_BONUS_2_GROUPS + VoidElementManager.STABILIZATION_ANGLE_BONUS_3_GROUPS + VoidElementManager.STABILIZATION_ANGLE_BONUS_4_GROUPS + VoidElementManager.STABILIZATION_ANGLE_BONUS_5_GROUPS + VoidElementManager.STABILIZATION_ANGLE_BONUS_6_GROUPS > 0.0f;
	}
	
	public static boolean hasAngleOrSideStabBonus() {
		return hasAngleStabBonus() || hasSideStabBonus();
	}
	
	public static boolean hasSideStabBonus() {
		return VoidElementManager.STABILIZER_BONUS_CALC == StabBonusCalcStyle.BY_SIDE && VoidElementManager.STABILIZATION_DIMENSION_BONUS_2 + VoidElementManager.STABILIZATION_DIMENSION_BONUS_3 + VoidElementManager.STABILIZATION_DIMENSION_BONUS_4 + VoidElementManager.STABILIZATION_DIMENSION_BONUS_5 + VoidElementManager.STABILIZATION_DIMENSION_BONUS_6 > 0.0f;
	}
	
	static {
		VoidElementManager.shieldEffectConfiguration = new InterEffectSet();
		VoidElementManager.basicEffectConfiguration = new InterEffectSet();
		VoidElementManager.armorEffectConfiguration = new InterEffectSet();
		VoidElementManager.VOLUME_MASS_MULT = 0.0f;
		VoidElementManager.DEVENSIVE_EFFECT_MAX_PERCENT_MASS_MULT = 0.0f;
		VoidElementManager.SHIELD_EXTRA_CAPACITY_MULT_PER_UNIT = 1.0f;
		VoidElementManager.SHIELD_EXTRA_RECHARGE_MULT_PER_UNIT = 1.0f;
		VoidElementManager.SHIELD_INITIAL_CORE = true;
		VoidElementManager.SHIELD_CAPACITY_INITIAL = 5000.0;
		VoidElementManager.SHIELD_RECHARGE_INITIAL = 5000.0;
		VoidElementManager.SHIELD_CAPACITY_POW = 0.66666;
		VoidElementManager.SHIELD_CAPACITY_PRE_POW_MUL = 3.5;
		VoidElementManager.SHIELD_CAPACITY_TOTAL_MUL = 350.0;
		VoidElementManager.SHIELD_RECHARGE_POW = 0.5;
		VoidElementManager.SHIELD_RECHARGE_PRE_POW_MUL = 5.0;
		VoidElementManager.SHIELD_RECHARGE_TOTAL_MUL = 50.0;
		VoidElementManager.SHIELD_RECHARGE_CYCLE_TIME = 1.0;
		VoidElementManager.SHIELD_RECOVERY_TIME_IN_SEC = 15;
		VoidElementManager.SHIELD_DIRECT_RECOVERY_TIME_IN_SEC = 3;
		VoidElementManager.SHIELD_RECOVERY_NERF_MULT_PER_PERCENT = 0.25f;
		VoidElementManager.SHIELD_RECOVERY_NERF_MULT = 0.25f;
		VoidElementManager.SHIELD_RECHARGE_POWER_CONSUMPTION = 1;
		VoidElementManager.SHIELD_FULL_POWER_CONSUMPTION = 1;
		VoidElementManager.SHIELD_DOCK_TRANSFER_LIMIT = 0.0;
		VoidElementManager.POWER_DIV_FACTOR = 0.333;
		VoidElementManager.POWER_CEILING = 1000000.0;
		VoidElementManager.POWER_GROWTH = 1.000696;
		VoidElementManager.POWER_LINEAR_GROWTH = 25.0;
		VoidElementManager.POWER_RECOVERY_TIME = 1000L;
		VoidElementManager.POWER_FIXED_BASE_CAPACITY = 20000;
		VoidElementManager.POWER_TANK_CAPACITY_LINEAR = 1.0f;
		VoidElementManager.POWER_TANK_CAPACITY_POW = 1.75f;
		VoidElementManager.POWER_BATTERY_CAPACITY_LINEAR = 1.0f;
		VoidElementManager.POWER_BATTERY_CAPACITY_POW = 1.75f;
		VoidElementManager.POWER_BATTERY_TRANSFER_RATE_PER_SEC = 1.75f;
		VoidElementManager.POWER_BATTERY_LINEAR_GROWTH = 25.0;
		VoidElementManager.POWER_BATTERY_GROUP_MULTIPLIER = 25.0;
		VoidElementManager.POWER_BATTERY_GROUP_POW = 25.0;
		VoidElementManager.POWER_BATTERY_GROUP_GROWTH = 25.0;
		VoidElementManager.POWER_BATTERY_GROUP_CEILING = 25.0;
		VoidElementManager.POWER_BATTERY_TURNED_ON_MULT = 25.0;
		VoidElementManager.POWER_BATTERY_TURNED_OFF_MULT = 25.0;
		VoidElementManager.POWER_BATTERY_TRANSFER_TOP_OFF_ONLY = false;
		VoidElementManager.POWER_BATTERY_EXPLOSION_RATE = 1.0;
		VoidElementManager.POWER_BATTERY_EXPLOSION_RADIUS_PER_BLOCKS = 25.0;
		VoidElementManager.POWER_BATTERY_EXPLOSION_RADIUS_MAX = 25.0;
		VoidElementManager.POWER_BATTERY_EXPLOSION_DAMAGE_PER_BLOCKS = 25.0;
		VoidElementManager.POWER_BATTERY_EXPLOSION_DAMAGE_MAX = 25.0;
		VoidElementManager.POWER_BATTERY_EXPLOSION_COUNT_PER_BLOCKS = 25.0;
		VoidElementManager.POWER_BATTERY_EXPLOSION_COUNT_MAX = 25;
		VoidElementManager.POWER_BATTERY_EXPLOSION_COUNT_PERCENT = 25.0;
		VoidElementManager.EVADE_EFFECT_POWER_CONSUMPTION_MULT = 1.75f;
		VoidElementManager.TAKE_OFF_EFFECT_POWER_CONSUMPTION_MULT = 1.75f;
		VoidElementManager.PERSONAL_SALVAGE_BEAM_BONUS = 2.0f;
		VoidElementManager.RAIL_MASS_ENHANCER_FREE_MASS = 5.0f;
		VoidElementManager.RAIL_MASS_ENHANCER_MASS_ADDED_PER_ENHANCER = 0.5f;
		VoidElementManager.RAIL_MASS_ENHANCER_POWER_CONSUMED_PER_ENHANCER = 10.0;
		VoidElementManager.RAIL_MASS_ENHANCER_PERCENT_COST_PER_MASS_ABOVE_ENHANCER_PROVIDED = 0.01f;
		VoidElementManager.RAIL_MASS_ENHANCER_REACTOR_POWER_CONSUMPTION_RESTING = 0.0f;
		VoidElementManager.RAIL_MASS_ENHANCER_REACTOR_POWER_CONSUMPTION_CHARGING = 1.0f;
		VoidElementManager.POWER_FIXED_PLANET_BASE_CAPACITY = 500;
		VoidElementManager.POWER_FIXED_ASTEROID_BASE_CAPACITY = 500;
		VoidElementManager.SHIP_REBOOT_TIME_IN_SEC_PER_MISSING_HP_PERCENT = 1.0;
		VoidElementManager.SHIP_REBOOT_TIME_MULTIPLYER_PER_MASS = 1.0E-4;
		VoidElementManager.SHIP_REBOOT_TIME_MIN_SEC = 30.0;
		HP_CONDITION_TRIGGER_LIST = new HpConditionList();
		VoidElementManager.HP_DEDUCTION_LOG_FACTOR = 0.0f;
		VoidElementManager.HP_DEDUCTION_LOG_OFFSET = 0.0f;
		VoidElementManager.STRUCTURE_HP_BLOCK_MULTIPLIER = 1.0;
		VoidElementManager.AI_TURRET_ORIENTATION_SPEED_MIN = 0.5f;
		VoidElementManager.AI_TURRET_ORIENTATION_SPEED_MAX = 3.0f;
		VoidElementManager.AI_TURRET_ORIENTATION_SPEED_DIV_BY_MASS = 30.0f;
		VoidElementManager.EXPLOSION_SHIELD_DAMAGE_BONUS = 0.0f;
		VoidElementManager.EXPLOSION_HULL_DAMAGE_BONUS = 0.0f;
		VoidElementManager.REACTOR_CHAMBER_BLOCKS_PER_MAIN_REACTOR_AND_LEVEL = 0.5f;
		VoidElementManager.POWER_REACTOR_CONDUIT_POWER_CONSUMPTION_PER_SEC = 1.0f;
		VoidElementManager.REACTOR_SWITCH_COOLDOWN_SEC = 1.0f;
		VoidElementManager.REACTOR_MAIN_COUNT_MULTIPLIER = 1.0f;
		VoidElementManager.REACTOR_POWER_CAPACITY_MULTIPLIER = 1.0f;
		VoidElementManager.REACTOR_RECHARGE_PERCENT_PER_SECOND = 0.1f;
		VoidElementManager.REACTOR_RECHARGE_EMPTY_MULTIPLIER = 0.1f;
		VoidElementManager.REACTOR_STABILIZER_LINEAR_FALLOFF_ONE = 1.0f;
		VoidElementManager.REACTOR_STABILIZER_LINEAR_FALLOFF_ZERO = 1.0f;
		VoidElementManager.REACTOR_STABILIZER_FREE_MAIN_REACTOR_BLOCKS = 1;
		VoidElementManager.REACTOR_STABILIZER_DISTANCE_TOTAL_MULT = 2.0f;
		VoidElementManager.REACTOR_STABILIZATION_MULTIPLIER = 1.0f;
		VoidElementManager.REACTOR_STABILIZER_DISTANCE_LOG_LEVELED_STEPS = true;
		VoidElementManager.REACTOR_STABILIZER_DISTANCE_LOG_LEVELED_MULTIPLIER = 10.0f;
		VoidElementManager.REACTOR_STABILIZER_DISTANCE_LOG_LEVELED_EXP = 1.0f;
		VoidElementManager.REACTOR_CALC_STYLE = UnitCalcStyle.LINEAR;
		VoidElementManager.REACTOR_STABILIZER_DISTANCE_PER_MAIN_REACTOR_BLOCK = 1.0f;
		VoidElementManager.REACTOR_STABILIZER_DISTANCE_EXP_MULT = 1.0f;
		VoidElementManager.REACTOR_STABILIZER_DISTANCE_EXP = 1.0f;
		VoidElementManager.REACTOR_STABILIZER_DISTANCE_EXP_SOFTCAP_MULT = 1.0f;
		VoidElementManager.REACTOR_STABILIZER_DISTANCE_EXP_SOFTCAP_EXP = 1.0f;
		VoidElementManager.REACTOR_STABILIZER_DISTANCE_EXP_SOFTCAP_BLOCKS_START = 1.0f;
		VoidElementManager.REACTOR_STABILIZER_DISTANCE_LOG_FACTOR = 1.0f;
		VoidElementManager.REACTOR_STABILIZER_DISTANCE_LOG_OFFSET = 1.0f;
		VoidElementManager.REACTOR_STABILIZER_STARTING_DISTANCE = -20.0f;
		VoidElementManager.STABILIZER_BONUS_CALC = StabBonusCalcStyle.BY_SIDE;
		VoidElementManager.STABILIZATION_ANGLE_BONUS_2_GROUPS = 1.0f;
		VoidElementManager.STABILIZATION_ANGLE_BONUS_3_GROUPS = 1.0f;
		VoidElementManager.STABILIZATION_ANGLE_BONUS_4_GROUPS = 1.0f;
		VoidElementManager.STABILIZATION_ANGLE_BONUS_5_GROUPS = 1.0f;
		VoidElementManager.STABILIZATION_ANGLE_BONUS_6_GROUPS = 1.0f;
		VoidElementManager.STABILIZATION_DIMENSION_BONUS_2 = 1.0f;
		VoidElementManager.STABILIZATION_DIMENSION_BONUS_3 = 1.0f;
		VoidElementManager.STABILIZATION_DIMENSION_BONUS_4 = 1.0f;
		VoidElementManager.STABILIZATION_DIMENSION_BONUS_5 = 1.0f;
		VoidElementManager.STABILIZATION_DIMENSION_BONUS_6 = 1.0f;
		VoidElementManager.REACTOR_LOW_STABILIZATION_EXTRA_DAMAGE_START = 1.0f;
		VoidElementManager.REACTOR_LOW_STABILIZATION_EXTRA_DAMAGE_END = 0.2f;
		VoidElementManager.REACTOR_LOW_STABILIZATION_EXTRA_DAMAGE_START_DAMAGE = 1.0f;
		VoidElementManager.REACTOR_LOW_STABILIZATION_EXTRA_DAMAGE_END_DAMAGE = 2.0f;
		VoidElementManager.REACTOR_JUMP_POWER_CONSUMPTION_RESTING_PER_MASS = 1.0E-4f;
		VoidElementManager.REACTOR_JUMP_POWER_CONSUMPTION_CHARGING_PER_MASS = 0.01f;
		VoidElementManager.REACTOR_JUMP_DISTANCE_DEFAULT = 4.0f;
		VoidElementManager.REACTOR_JUMP_CHARGE_NEEDED_IN_SEC = 10.0f;
		VoidElementManager.REACTOR_JUMP_CHARGE_NEEDED_IN_SEC_EXTRA_PER_MASS = 1.0E-4f;
		VoidElementManager.REACTOR_JUMP_CHARGE_NEEDED_IN_SEC_LOG_FACTOR = 0.5f;
		VoidElementManager.REACTOR_JUMP_CHARGE_NEEDED_IN_SEC_LOG_OFFSET = 0.5f;
		VoidElementManager.STEALTH_CHARGE_NEEDED = 0.0f;
		VoidElementManager.STEALTH_CONSUMPTION_RESTING = 0.0f;
		VoidElementManager.STEALTH_CONSUMPTION_CHARGING = 0.0f;
		VoidElementManager.STEALTH_CONSUMPTION_RESTING_ADDED_BY_MASS = 0.0f;
		VoidElementManager.STEALTH_CONSUMPTION_CHARGING_ADDED_BY_MASS = 0.0f;
		VoidElementManager.STEALTH_DURATION_BASIC = 0.0f;
		VoidElementManager.STEALTH_STRENGTH_BASIC = 0.0f;
		VoidElementManager.SCAN_CHARGE_NEEDED = 0.0f;
		VoidElementManager.SCAN_CONSUMPTION_RESTING = 0.0f;
		VoidElementManager.SCAN_CONSUMPTION_CHARGING = 0.0f;
		VoidElementManager.SCAN_CONSUMPTION_RESTING_ADDED_BY_MASS = 0.0f;
		VoidElementManager.SCAN_CONSUMPTION_CHARGING_ADDED_BY_MASS = 0.0f;
		VoidElementManager.SCAN_DURATION_BASIC = 0.0f;
		VoidElementManager.SCAN_STRENGTH_BASIC = 0.0f;
		VoidElementManager.RECON_DIFFERENCE_MIN_CLOAKING = 0;
		VoidElementManager.RECON_DIFFERENCE_MIN_JAMMING = 0;
		VoidElementManager.RECON_DIFFERENCE_MIN_REACTOR = 0;
		VoidElementManager.RECON_DIFFERENCE_MIN_CHAMBERS = 0;
		VoidElementManager.RECON_DIFFERENCE_MIN_WEAPONS = 0;
		VoidElementManager.REACTOR_EXPLOSION_STABILITY = 1.0;
		VoidElementManager.REACTOR_EXPLOSION_STABILITY_LOSS_MULT = 1.0;
		VoidElementManager.REACTOR_EXPLOSION_RATE = 1.0;
		VoidElementManager.REACTOR_EXPLOSION_RADIUS_PER_BLOCKS = 25.0;
		VoidElementManager.REACTOR_EXPLOSION_RADIUS_MAX = 25.0;
		VoidElementManager.REACTOR_EXPLOSION_DAMAGE_PER_BLOCKS = 25.0;
		VoidElementManager.REACTOR_EXPLOSION_DAMAGE_MAX = 25.0;
		VoidElementManager.REACTOR_EXPLOSION_COUNT_PER_BLOCKS = 25.0;
		VoidElementManager.REACTOR_EXPLOSION_COUNT_MAX = 25;
		VoidElementManager.REACTOR_EXPLOSION_COUNT_PERCENT = 25.0;
		VoidElementManager.REACTOR_MODULE_DISCHARGE_MARGIN = 0.05000000074505806;
		VoidElementManager.SHIELD_LOCAL_CAPACITY_PER_BLOCK = 400.0f;
		VoidElementManager.SHIELD_LOCAL_RECHARGE_PER_BLOCK = 20.0f;
		VoidElementManager.SHIELD_LOCAL_DEFAULT_CAPACITY = 200.0f;
		VoidElementManager.SHIELD_LOCAL_RADIUS_CALC_STYLE = UnitCalcStyle.LINEAR;
		VoidElementManager.REACTOR_LEVEL_CALC_STYLE = ReactorLevelCalcStyle.LOG10;
		VoidElementManager.REACTOR_LEVEL_CALC_LINEAR_BLOCKS_NEEDED_PER_LEVEL = 0;
		VoidElementManager.SHIELD_LOCAL_DEFAULT_RADIUS = 50.0f;
		VoidElementManager.SHIELD_LOCAL_UPKEEP_PER_SECOND_OF_TOTAL_CAPACITY = 0.001f;
		VoidElementManager.SHIELD_LOCAL_CONSUMPTION_PER_CURRENT_RECHARGE_PER_SECOND_RESTING = 1.0f;
		VoidElementManager.SHIELD_LOCAL_CONSUMPTION_PER_CURRENT_RECHARGE_PER_SECOND_CHARGING = 1.0f;
		VoidElementManager.SHIELD_LOCAL_RECHARGE_UNDER_FIRE_MODE_SEC = 1.0f;
		VoidElementManager.SHIELD_LOCAL_RECHARGE_UNDER_FIRE_MIN_PERCENT = 1.0f;
		VoidElementManager.SHIELD_LOCAL_RECHARGE_UNDER_FIRE_START_AT_CHARGED = 1.0f;
		VoidElementManager.SHIELD_LOCAL_RECHARGE_UNDER_FIRE_END_AT_CHARGED = 1.0f;
		VoidElementManager.SHIELD_LOCAL_ON_ZERO_SHIELDS_RECHARGE_PREVENTION_SEC = 1.0f;
		VoidElementManager.SHIELD_LOCAL_HIT_ALL_OVERLAPPING = true;
		VoidElementManager.SHIELD_LOCAL_MAX_CAPACITY_GROUPS_PER_LOCAL_SHIELD = 20;
		VoidElementManager.SHIELD_LOCAL_RADIUS_PER_RECHARGE_BLOCK = 0.5f;
		VoidElementManager.SHIELD_LOCAL_RADIUS_EXP_MULT = 0.5f;
		VoidElementManager.SHIELD_LOCAL_RADIUS_EXP = 0.5f;
		VoidElementManager.SHIELD_LOCAL_RADIUS_LOG_FACTOR = 0.5f;
		VoidElementManager.SHIELD_LOCAL_RADIUS_LOG_OFFSET = 0.5f;
		VoidElementManager.COLLECTION_INTEGRITY_START_VALUE = 100.0;
		VoidElementManager.COLLECTION_INTEGRITY_BASE_TOUCHING_0 = -3.0;
		VoidElementManager.COLLECTION_INTEGRITY_BASE_TOUCHING_1 = -2.0;
		VoidElementManager.COLLECTION_INTEGRITY_BASE_TOUCHING_2 = -1.0;
		VoidElementManager.COLLECTION_INTEGRITY_BASE_TOUCHING_3 = 0.0;
		VoidElementManager.COLLECTION_INTEGRITY_BASE_TOUCHING_4 = 1.0;
		VoidElementManager.COLLECTION_INTEGRITY_BASE_TOUCHING_5 = 2.0;
		VoidElementManager.COLLECTION_INTEGRITY_BASE_TOUCHING_6 = 3.0;
		VoidElementManager.INTEGRITY_MARGIN = 0.0;
		VoidElementManager.COLLECTION_INTEGRITY_EXPLOSION_AMOUNT = 3;
		VoidElementManager.COLLECTION_INTEGRITY_EXPLOSION_RADIUS = 3;
		VoidElementManager.OVERHEAT_TIMER_MIN = 60L;
		VoidElementManager.OVERHEAT_TIMER_MAX = 600L;
		VoidElementManager.OVERHEAT_TIMER_ADDED_PER_BLOCK = 0.001f;
		VoidElementManager.COLLECTION_INTEGRITY_EXPLOSION_RATE = 1L;
		VoidElementManager.COLLECTION_INTEGRITY_DAMAGE_PER_BLOCKS = 25.0;
		VoidElementManager.COLLECTION_INTEGRITY_DAMAGE_MAX = 25.0;
		VoidElementManager.REACTOR_STABILIZER_GROUPING_PROXIMITY = 25.0f;
		VoidElementManager.REACTOR_STABILIZER_PATH_RADIUS_DEFAULT = 1.0f;
		VoidElementManager.REACTOR_STABILIZER_PATH_RADIUS_PER_LEVEL = 0.1f;
		VoidElementManager.REACTOR_STABILIZATION_POWER_EFFECTIVE_FULL = 0.2f;
		VoidElementManager.REACTOR_STABILIZER_GROUPS_MAX = 20;
		configPath = "." + File.separator + "data" + File.separator + "config" + File.separator + "customBlockBehaviorConfigTemplate.xml";
		configPathHOWTO = "." + File.separator + "data" + File.separator + "config" + File.separator + "customBlockBehaviorConfigHOWTO.txt";
		VoidElementManager.REPULSE_MULT = 1.0f;
		VoidElementManager.ARMOR_THICKNESS_BONUS = 0.0f;
		VoidElementManager.ARMOR_BEAM_DAMAGE_SCALING = 0.0f;
		VoidElementManager.ARMOR_CALC_STYLE = ArmorDamageCalcStyle.LINEAR;
		VoidElementManager.CANNON_ARMOR_FLAT_DAMAGE_REDUCTION = 0.0f;
		VoidElementManager.CANNON_ARMOR_THICKNESS_DAMAGE_REDUCTION = 0.0f;
		VoidElementManager.CANNON_ARMOR_THICKNESS_DAMAGE_REDUCTION_MAX = 0.0f;
		VoidElementManager.BEAM_ARMOR_FLAT_DAMAGE_REDUCTION = 0.0f;
		VoidElementManager.BEAM_ARMOR_THICKNESS_DAMAGE_REDUCTION = 0.0f;
		VoidElementManager.BEAM_ARMOR_THICKNESS_DAMAGE_REDUCTION_MAX = 0.0f;
		VoidElementManager.CANNON_ARMOR_EXPONENTIAL_INCOMING_EXPONENT = 0.0f;
		VoidElementManager.CANNON_ARMOR_EXPONENTIAL_ARMOR_VALUE_TOTAL_EXPONENT = 0.0f;
		VoidElementManager.CANNON_ARMOR_EXPONENTIAL_INCOMING_DAMAGE_ADDED_EXPONENT = 0.0f;
		VoidElementManager.BEAM_ARMOR_EXPONENTIAL_INCOMING_EXPONENT = 0.0f;
		VoidElementManager.BEAM_ARMOR_EXPONENTIAL_ARMOR_VALUE_TOTAL_EXPONENT = 0.0f;
		VoidElementManager.BEAM_ARMOR_EXPONENTIAL_INCOMING_DAMAGE_ADDED_EXPONENT = 0.0f;
		VoidElementManager.ARMOR_OVER_PENETRATION_MARGIN_MULTIPLICATOR = 0.0f;
		VoidElementManager.NON_ARMOR_OVER_PENETRATION_MARGIN = 0.0f;
		VoidElementManager.REACTOR_REBOOT_MIN_COOLDOWN_SEC = 10.0f;
		VoidElementManager.REACTOR_REBOOT_LOG_FACTOR = 0.5f;
		VoidElementManager.REACTOR_REBOOT_LOG_OFFSET = 0.5f;
		VoidElementManager.REACTOR_REBOOT_SEC_PER_HP_PERCENT = 30.0f;
		VoidElementManager.REACTOR_STABILIZATION_ENERGY_STREAM_HIT_COOLDOWN_PER_DAMAGE_IN_SEC = 0.01f;
		VoidElementManager.REACTOR_STABILIZATION_ENERGY_STREAM_HIT_MIN_COOLDOWN_IN_SEC = 1.0f;
		VoidElementManager.REACTOR_STABILIZATION_ENERGY_STREAM_HIT_MAX_COOLDOWN_IN_SEC = 100.0f;
		VoidElementManager.REACTOR_STABILIZATION_ENERGY_STREAM_DISTANCE = -1.0f;
		VoidElementManager.REACTOR_STABILIZATION_ENERGY_STREAM_HIT_COOLDOWN_REACTOR_EFFICIENCY = 0.2f;
		VoidElementManager.ACID_DAMAGE_ARMOR_STOPPED_MARGIN = 0.0f;
		//#XXX: new armor counter
		VoidElementManager.ARMOR_RAYCAST_LENGTH = 100.0f;
		//#XXX:
	}
}
