// 
// Decompiled by Procyon v0.5.36
// 

package org.schema.game.common.controller.damage.projectile;

import org.schema.game.common.data.world.SegmentData;
import org.schema.common.util.linAlg.Vector3fTools;
import org.schema.game.common.data.physics.RayTraceGridTraverser;
import org.schema.game.common.controller.damage.acid.AcidSetting;
import org.schema.game.common.controller.damage.effects.MetaWeaponEffectInterface;
import com.bulletphysics.util.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import org.schema.game.common.data.physics.RigidBodyExt;
import org.schema.game.common.data.world.SectorNotFoundException;
import org.schema.game.common.controller.damage.HitType;
import org.schema.game.common.controller.elements.ShieldContainerInterface;
import org.schema.game.common.controller.TransientSegmentController;
import org.schema.game.common.controller.damage.acid.AcidDamageFormula;
import com.bulletphysics.collision.dispatch.CollisionWorld;
import org.schema.game.common.data.physics.ModifiedDynamicsWorld;
import javax.vecmath.Tuple3f;
import org.schema.game.common.controller.damage.Damager;
import org.schema.game.client.view.effects.ExplosionDrawer;
import java.util.Iterator;
import org.schema.game.common.data.player.inventory.Inventory;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.controller.damage.DamageDealerType;
import org.schema.game.client.data.GameClientState;
import org.schema.game.common.controller.EditableSendableSegmentController;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;
import org.schema.game.common.controller.elements.cargo.CargoCollectionManager;
import org.schema.game.common.data.ManagedSegmentController;
import org.schema.common.FastMath;
import org.schema.game.common.controller.elements.ArmorDamageCalcStyle;
import org.schema.game.common.controller.damage.effects.InterEffectSet;
import org.schema.game.common.controller.elements.VoidElementManager;
import org.schema.game.common.controller.damage.effects.InterEffectHandler;
import org.schema.game.common.controller.damage.HitReceiverType;
import org.schema.game.common.data.element.ElementInformation;
import org.schema.game.common.data.element.ElementKeyMap;
import com.bulletphysics.linearmath.Transform;
import org.schema.common.util.linAlg.Vector3b;
import org.schema.game.common.data.world.Segment;
import org.schema.game.common.data.physics.SegmentTraversalInterface;
import org.schema.game.common.data.physics.InnerSegmentIterator;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.physics.CubeRayCastResult;
import javax.vecmath.Vector3f;
import org.schema.game.common.data.SegmentPiece;

public class ProjectileHandlerSegmentController extends ProjectileHandler
{
	private final SegmentPiece pTmp;
	private final Vector3f hTmp;
	private final Vector3f dirTmp;
	private ShotHandler shotHandler;
	private CubeRayCastResult rayCallbackTraverse;
	private Vector3f tmpDir;
	private Vector3f tmpWorldPos;
	
	public ProjectileHandlerSegmentController() {
		this.pTmp = new SegmentPiece();
		this.hTmp = new Vector3f();
		this.dirTmp = new Vector3f();
		this.shotHandler = new ShotHandler();
		this.rayCallbackTraverse = new CubeRayCastResult(new Vector3f(), new Vector3f(), (Object)null, new SegmentController[0]) {
			@Override
			public InnerSegmentIterator newInnerSegmentIterator() {
				return new ProjectileTraverseHandler();
			}
		};
		this.tmpDir = new Vector3f();
		this.tmpWorldPos = new Vector3f();
	}
	
	private boolean processRawHitUnshielded(final Segment segment, final int n, final short n2, final Vector3b vector3b, final Vector3f vector3f, final Transform transform) {
		final ElementInformation infoFast = ElementKeyMap.getInfoFast(n2);
		this.shotHandler.positionsHit.add(segment.getAbsoluteIndex(n));
		this.shotHandler.typesHit.add(infoFast);
		this.shotHandler.segmentsHit.add(segment);
		this.shotHandler.segmentsHitSet.add(segment);
		this.shotHandler.infoIndexHit.add(n);
		boolean processHitsUnshielded = true;
		if (!this.isAccumulateShot(infoFast)) {
			processHitsUnshielded = this.processHitsUnshielded(this.shotHandler);
		}
		return processHitsUnshielded;
	}
	
	private boolean processHitsUnshielded(final ShotHandler shotHandler) {
		final int size = shotHandler.positionsHit.size();
		boolean b = false;
		if (size > 1) {
			final HitReceiverType armor = HitReceiverType.ARMOR;
			final InterEffectSet value = shotHandler.hitSegController.getEffectContainer().get(armor);
			assert shotHandler.damager != null;
			assert shotHandler.damageDealerType != null;
			final InterEffectSet attackEffectSet;
			float n;
			if ((attackEffectSet = shotHandler.damager.getAttackEffectSet(shotHandler.weaponId, shotHandler.damageDealerType)) == null) {
				System.err.println(shotHandler.hitSegController.getState() + " WARNING: hit effect set on " + shotHandler.hitSegController + " by " + shotHandler.damager + " is null for weapon " + shotHandler.weaponId);
				n = shotHandler.dmg;
			}
			else {
				n = InterEffectHandler.handleEffects(shotHandler.dmg, attackEffectSet, value, shotHandler.hitType, shotHandler.damageDealerType, armor, (short)(-1));
			}
			short n2 = 0;
			int n3 = 0;
			float n4 = 0.0f;
			float n5 = 0.0f;
			for (int i = 0; i < size; ++i) {
				final ElementInformation elementInformation = (ElementInformation)shotHandler.typesHit.get(i);
				final Segment segment = (Segment)shotHandler.segmentsHit.get(i);
				final int value2 = shotHandler.infoIndexHit.get(i);
				final short id = elementInformation.id;
				if (!elementInformation.isArmor()) {
					break;
				}
				if (n3 == 0) {
					n2 = id;
				}
				++n3;
				n4 += elementInformation.getArmorValue() + elementInformation.getArmorValue() * (n3 * VoidElementManager.ARMOR_THICKNESS_BONUS);
				n5 += segment.getSegmentData().getHitpointsByte(value2) * 0.007874016f;
			}
			if (n3 > 0) {
				shotHandler.dmg = shotHandler.hitSegController.getHpController().onHullDamage(shotHandler.damager, n, n2, shotHandler.damageDealerType);
				b = true;
				shotHandler.totalArmorValue = n4 * (n5 / n3);
				shotHandler.damageToAcidPercent = 0.0f;
				if (n < shotHandler.totalArmorValue * VoidElementManager.ACID_DAMAGE_ARMOR_STOPPED_MARGIN) {
					shotHandler.shotStatus = ShotStatus.STOPPED;
				}
				else if (n < shotHandler.totalArmorValue) {
					shotHandler.shotStatus = ShotStatus.STOPPED_ACID;
					shotHandler.damageToAcidPercent = (n - shotHandler.totalArmorValue * VoidElementManager.ACID_DAMAGE_ARMOR_STOPPED_MARGIN) / (1.0f - VoidElementManager.ACID_DAMAGE_ARMOR_STOPPED_MARGIN);
					this.doDamageReduction(shotHandler, n3);
				}
				else if (shotHandler.shotStatus == ShotStatus.OVER_PENETRATION) {
					if (n > shotHandler.totalArmorValue * VoidElementManager.ARMOR_OVER_PENETRATION_MARGIN_MULTIPLICATOR) {
						shotHandler.shotStatus = ShotStatus.OVER_PENETRATION;
					}
					else {
						shotHandler.shotStatus = ShotStatus.NORMAL;
					}
				}
				if (shotHandler.shotStatus == ShotStatus.NORMAL || shotHandler.shotStatus == ShotStatus.OVER_PENETRATION) {
					this.doDamageReduction(shotHandler, n3);
				}
			}
			else if (shotHandler.blockIndex == 0 && shotHandler.shotStatus == ShotStatus.OVER_PENETRATION) {
				if (shotHandler.dmg > VoidElementManager.NON_ARMOR_OVER_PENETRATION_MARGIN) {
					shotHandler.shotStatus = ShotStatus.OVER_PENETRATION;
				}
				else {
					shotHandler.shotStatus = ShotStatus.NORMAL;
				}
			}
		}
		for (int n6 = 0; n6 < size && shotHandler.dmg > 0.0f; ++n6) {
			final ElementInformation obj = (ElementInformation)shotHandler.typesHit.get(n6);
			final Segment segment2 = (Segment)shotHandler.segmentsHit.get(n6);
			final int value3 = shotHandler.infoIndexHit.get(n6);
			final short id2 = obj.id;
			if (this.rayCallbackTraverse.isDebug()) {
				System.err.println("HANDLING DAMAGE ON BLOCK: " + obj + "; " + new SegmentPiece(segment2, value3));
			}
			shotHandler.dmg = this.doDamageOnBlock(id2, obj, segment2, value3);
			++shotHandler.blockIndex;
			if (b && shotHandler.shotStatus == ShotStatus.STOPPED) {
				shotHandler.dmg = 0.0f;
				break;
			}
		}
		shotHandler.resetHitBuffer();
		return shotHandler.dmg > 0.0f;
	}
	
	private void doDamageReduction(final ShotHandler shotHandler, final int n) {
		if (VoidElementManager.ARMOR_CALC_STYLE == ArmorDamageCalcStyle.EXPONENTIAL) {
			shotHandler.dmg = Math.max(0.0f, FastMath.pow(shotHandler.dmg, VoidElementManager.CANNON_ARMOR_EXPONENTIAL_INCOMING_EXPONENT) / (FastMath.pow(shotHandler.totalArmorValue, VoidElementManager.CANNON_ARMOR_EXPONENTIAL_ARMOR_VALUE_TOTAL_EXPONENT) + FastMath.pow(shotHandler.dmg, VoidElementManager.CANNON_ARMOR_EXPONENTIAL_INCOMING_DAMAGE_ADDED_EXPONENT)));
			return;
		}
		shotHandler.dmg = Math.max(0.0f, shotHandler.dmg - VoidElementManager.CANNON_ARMOR_FLAT_DAMAGE_REDUCTION * shotHandler.dmg);
		shotHandler.dmg = Math.max(0.0f, shotHandler.dmg - Math.min(VoidElementManager.CANNON_ARMOR_THICKNESS_DAMAGE_REDUCTION_MAX, VoidElementManager.CANNON_ARMOR_THICKNESS_DAMAGE_REDUCTION * n * shotHandler.dmg));
	}
	
	private float doDamageOnBlock(final short type, final ElementInformation obj, final Segment segment, final int n) {
		segment.getSegmentData().getOrientation(n);
		float a = 0.0f;
		Label_0289: {
			if (this.shotHandler.blockIndex == 0) {
				final InterEffectSet defenseEffectSetTmp = this.shotHandler.defenseEffectSetTmp;
				final InterEffectSet effect = obj.isArmor() ? this.shotHandler.defenseArmor : this.shotHandler.defenseBlock;
				assert this.shotHandler.damager != null;
				assert this.shotHandler.damageDealerType != null;
				defenseEffectSetTmp.setEffect(effect);
				defenseEffectSetTmp.add(obj.effectArmor);
				final InterEffectSet attackEffectSet = this.shotHandler.damager.getAttackEffectSet(this.shotHandler.weaponId, this.shotHandler.damageDealerType);
				final HitReceiverType hitReceiverType = obj.isArmor() ? HitReceiverType.ARMOR : HitReceiverType.BLOCK;
				if (attackEffectSet != null) {
					a = InterEffectHandler.handleEffects(this.shotHandler.dmg, attackEffectSet, defenseEffectSetTmp, this.shotHandler.hitType, this.shotHandler.damageDealerType, hitReceiverType, type);
					break Label_0289;
				}
				System.err.println(this.shotHandler.hitSegController.getState() + " WARNING: block hit effect set on " + this.shotHandler.hitSegController + " by " + this.shotHandler.damager + " is null for weapon " + this.shotHandler.weaponId);
			}
			a = this.shotHandler.dmg;
		}
		float max = a;
		final int round;
		if ((round = Math.round(a)) == 0) {
			return (float)round;
		}
		if (segment.getSegmentController() instanceof ManagedSegmentController && (obj.isInventory() || type == 689)) {
			final long absoluteIndex = segment.getAbsoluteIndex(n);
			final ManagerContainer managerContainer = ((ManagedSegmentController)segment.getSegmentController()).getManagerContainer();
			Inventory inventory = null;
			if (obj.isInventory()) {
				inventory = managerContainer.getInventory(absoluteIndex);
			}
			else {
				final Iterator iterator = managerContainer.getCargo().getCollectionManagers().iterator();
				while (iterator.hasNext()) {
					final CargoCollectionManager cargoCollectionManager;
					if ((cargoCollectionManager = (CargoCollectionManager)iterator.next()).rawCollection.contains(absoluteIndex)) {
						inventory = managerContainer.getInventory(cargoCollectionManager.getControllerIndex());
					}
				}
			}
			if (inventory != null) {
				if (!inventory.isEmpty()) {
					inventory.getVolume();
					for (double n2 = 0.0; n2 < round && !inventory.isEmpty(); n2 += round) {
						if (this.isOnServer()) {
							inventory.spawnVolumeInSpace(segment.getSegmentController(), round);
						}
						max = Math.max(0.0f, max - round);
					}
					return (float)round;
				}
			}
			else {
				System.err.println("[SERVER][PROJECTILE] Warning: no connected inventory found when hitting " + segment.getAbsoluteElemPos(n, new Vector3i()) + " -> " + obj);
			}
		}
		segment.getSegmentData().getHitpointsByte(n);
		final EditableSendableSegmentController editableSendableSegmentController;
		if ((editableSendableSegmentController = (EditableSendableSegmentController)this.shotHandler.hitSegController).isExtraAcidDamageOnDecoBlocks() && obj.isDecorative()) {
			//#XXX: the if above is what made cannons destroy infinite hull
			//this was fixed with config rather than modifying this file, essentially
			//a new BlockConfig value called ArmorBlock was created (the cousin of
			//SystemBlock, used to flag reactors and such). See ElementInformation
			//for more on the config setup, but basically the issue here was that
			//a block is decorative if it's not armor and isn't a system, meaning
			//it doesn't have the SystemBlock flag in BlockConfig.xml and it has
			//an armor value of 0. this made hull technically decorative, but giving
			//it an armor value would make it benefit from armor stacking and flagging
			//it as a system wouldn't make any sense, so config change instead.
			System.err.println("#XXX: shot a block the game thinks is decorative: " + obj.toString());
			if (this.isOnServer()) {
				editableSendableSegmentController.killBlock(new SegmentPiece(segment, n));
				this.shotHandler.acidFormula.getAcidDamageSetting(type, round, 40, 40, this.shotHandler.totalArmorValue, this.shotHandler.blockIndex, this.shotHandler.projectileWidth, this.shotHandler.penetrationDepth, this.shotHandler.shotStatus, this.shotHandler.acidSetting);
				this.shotHandler.totalDmg += (float)this.shotHandler.acidSetting.damage;
				editableSendableSegmentController.getAcidDamageManagerServer().inputDamage(segment.getAbsoluteIndex(n), this.shotHandler.shootingDirRelative, this.shotHandler.acidSetting.damage, this.shotHandler.acidSetting.maxPropagation, this.shotHandler.damager, this.shotHandler.weaponId, true, true);
			}
			else {
				final ExplosionDrawer explosionDrawer = ((GameClientState)this.shotHandler.hitSegController.getState()).getWorldDrawer().getExplosionDrawer();
				this.pTmp.setByReference(segment, n);
				this.pTmp.getAbsolutePos(this.hTmp);
				final Vector3f hTmp = this.hTmp;
				hTmp.x -= 8.0f;
				final Vector3f hTmp2 = this.hTmp;
				hTmp2.y -= 8.0f;
				final Vector3f hTmp3 = this.hTmp;
				hTmp3.z -= 8.0f;
				explosionDrawer.addExplosion(this.hTmp);
			}
			return max;
		}
		final float damageElement = editableSendableSegmentController.damageElement(type, n, segment.getSegmentData(), round, this.shotHandler.damager, DamageDealerType.PROJECTILE, this.shotHandler.weaponId);
		this.shotHandler.totalDmg += damageElement;
		final short n3 = (short)(segment.isEmpty() ? 0 : segment.getSegmentData().getHitpointsByte(n));
		float n4 = Math.max(0.0f, max - damageElement);
		if (n3 > 0) {
			if (this.isOnServer()) {
				editableSendableSegmentController.sendBlockHpByte(segment.getAbsoluteIndex(n), n3);
				editableSendableSegmentController.onBlockDamage(segment.getAbsoluteIndex(n), type, round, this.shotHandler.damageDealerType, this.shotHandler.damager);
			}
			n4 = 0.0f;
		}
		else {
			if (this.isOnServer()) {
				editableSendableSegmentController.sendBlockKill(segment.getAbsoluteIndex(n));
				final SegmentPiece segmentPiece;
				(segmentPiece = new SegmentPiece(segment, n)).setType(type);
				editableSendableSegmentController.onBlockKill(segmentPiece, this.shotHandler.damager);
			}
			else {
				final ExplosionDrawer explosionDrawer2 = ((GameClientState)this.shotHandler.hitSegController.getState()).getWorldDrawer().getExplosionDrawer();
				this.pTmp.setByReference(segment, n);
				this.pTmp.getAbsolutePos(this.hTmp);
				final Vector3f hTmp4 = this.hTmp;
				hTmp4.x -= 8.0f;
				final Vector3f hTmp5 = this.hTmp;
				hTmp5.y -= 8.0f;
				final Vector3f hTmp6 = this.hTmp;
				hTmp6.z -= 8.0f;
				explosionDrawer2.addExplosion(this.hTmp);
			}
			if (n4 > 0.0f && this.shotHandler.shotStatus != ShotStatus.STOPPED) {
				if (this.shotHandler.shotStatus == ShotStatus.STOPPED_ACID) {
					n4 *= this.shotHandler.damageToAcidPercent;
				}
				this.shotHandler.acidFormula.getAcidDamageSetting(type, round, (int)n4, (int)this.shotHandler.initialDamage, this.shotHandler.totalArmorValue, this.shotHandler.blockIndex, this.shotHandler.projectileWidth, this.shotHandler.penetrationDepth, this.shotHandler.shotStatus, this.shotHandler.acidSetting);
				if (this.isOnServer()) {
					this.shotHandler.totalDmg += (float)this.shotHandler.acidSetting.damage;
					editableSendableSegmentController.getAcidDamageManagerServer().inputDamage(segment.getAbsoluteIndex(n), this.shotHandler.shootingDirRelative, this.shotHandler.acidSetting.damage, this.shotHandler.acidSetting.maxPropagation, this.shotHandler.damager, this.shotHandler.weaponId, true, false);
				}
				n4 = Math.max(n4 - this.shotHandler.acidSetting.damage, 0.0f);
			}
		}
		return n4;
	}
	
	private boolean isOnServer() {
		return this.shotHandler.hitSegController.isOnServer();
	}
	
	private boolean isAccumulateShot(final ElementInformation elementInformation) {
		return elementInformation.isArmor();
	}
	
	@Override
	public ProjectileController.ProjectileHandleState handle(final Damager damager, final ProjectileController projectileController, final Vector3f vector3f, final Vector3f vector3f2, final ProjectileParticleContainer projectileParticleContainer, final int n, final CubeRayCastResult obj) {
		final Segment segment;
		if ((segment = obj.getSegment()) == null) {
			System.err.println(damager + " ERROR: SEGMENT NULL: " + obj);
			return ProjectileController.ProjectileHandleState.PROJECTILE_NO_HIT;
		}
		final SegmentController segmentController;
		if (!((segmentController = segment.getSegmentController()) instanceof ProjectileHittable)) {
			return ProjectileController.ProjectileHandleState.PROJECTILE_NO_HIT;
		}
		if (!segmentController.canBeDamagedBy(damager, DamageDealerType.PROJECTILE)) {
			return ProjectileController.ProjectileHandleState.PROJECTILE_NO_HIT_STOP;
		}
		this.shotHandler.hitSegController = segmentController;
		this.shotHandler.posBeforeUpdate.set((Tuple3f)vector3f);
		this.shotHandler.posAfterUpdate.set((Tuple3f)vector3f);
		this.shotHandler.shootingDir.sub((Tuple3f)vector3f2, (Tuple3f)vector3f);
		FastMath.normalizeCarmack(this.shotHandler.shootingDir);
		this.shotHandler.shootingDirRelative.set((Tuple3f)this.shotHandler.shootingDir);
		segmentController.getWorldTransformInverse().basis.transform((Tuple3f)this.shotHandler.shootingDirRelative);
		this.dirTmp.sub((Tuple3f)vector3f2, (Tuple3f)vector3f);
		FastMath.normalizeCarmack(this.dirTmp);
		this.dirTmp.scale(400.0f);
		vector3f2.add((Tuple3f)vector3f, (Tuple3f)this.dirTmp);
		this.rayCallbackTraverse.closestHitFraction = 1.0f;
		this.rayCallbackTraverse.collisionObject = null;
		this.rayCallbackTraverse.setSegment(null);
		this.rayCallbackTraverse.rayFromWorld.set((Tuple3f)vector3f);
		this.rayCallbackTraverse.rayToWorld.set((Tuple3f)vector3f2);
		this.rayCallbackTraverse.setFilter(segmentController);
		this.rayCallbackTraverse.setOwner(damager);
		this.rayCallbackTraverse.setIgnoereNotPhysical(false);
		this.rayCallbackTraverse.setIgnoreDebris(false);
		this.rayCallbackTraverse.setRecordAllBlocks(false);
		this.rayCallbackTraverse.setZeroHpPhysical(false);
		this.rayCallbackTraverse.setDamageTest(true);
		this.rayCallbackTraverse.setCheckStabilizerPaths(true);
		this.rayCallbackTraverse.setSimpleRayTest(true);
		((ModifiedDynamicsWorld)projectileController.getCurrentPhysics().getDynamicsWorld()).rayTest(vector3f, vector3f2, (CollisionWorld.RayResultCallback)this.rayCallbackTraverse);
		if (this.shotHandler.typesHit.size() > 0) {
			this.processHitsUnshielded(this.shotHandler);
		}
		assert this.shotHandler.typesHit.size() == 0 : "not all hits consumed " + this.shotHandler.typesHit.size();
		if (this.shotHandler.getResult() == ProjectileController.ProjectileHandleState.PROJECTILE_HIT_STOP || this.shotHandler.getResult() == ProjectileController.ProjectileHandleState.PROJECTILE_HIT_CONTINUE || this.shotHandler.getResult() == ProjectileController.ProjectileHandleState.PROJECTILE_HIT_STOP_INVULNERABLE) {
			this.shotHandler.hitSegController.sendHitConfirmToDamager(damager, false);
		}
		if (this.rayCallbackTraverse.hasHit()) {
			if (this.rayCallbackTraverse.isDebug()) {
				System.err.println("UPDATE POSAFTERHIT::: -> " + this.rayCallbackTraverse.hitPointWorld);
			}
			vector3f2.set((Tuple3f)this.rayCallbackTraverse.hitPointWorld);
		}
		return this.shotHandler.getResult();
	}
	
	@Override
	public ProjectileController.ProjectileHandleState handleBefore(final Damager damager, final ProjectileController projectileController, Vector3f unknown_vector, final Vector3f vector3f, final ProjectileParticleContainer projectileParticleContainer, int n, final CubeRayCastResult cubeRayCastResult) {
		if (cubeRayCastResult.getSegment() == null) {
			System.err.println("[PROJECTILE][WARNING] Segment null but collided " + cubeRayCastResult.collisionObject);
			return ProjectileController.ProjectileHandleState.PROJECTILE_NO_HIT_STOP;
		}
		SegmentController segmentController = cubeRayCastResult.getSegment().getSegmentController();
		this.shotHandler.reset();
		this.shotHandler.dmg = projectileParticleContainer.getDamage(n);
		this.shotHandler.penetrationDepth = projectileParticleContainer.getPenetrationDepth(n);
		this.shotHandler.initialDamage = projectileParticleContainer.getDamageInitial(n);
		this.shotHandler.damager = damager;
		this.shotHandler.hitSegController = segmentController;
		this.shotHandler.weaponId = projectileParticleContainer.getWeaponId(n);
		this.shotHandler.blockIndex = projectileParticleContainer.getBlockHitIndex(n);
		this.shotHandler.shotStatus = ShotStatus.values()[projectileParticleContainer.getShotStatus(n)];
		this.shotHandler.projectileWidth = projectileParticleContainer.getWidth(n);
		this.shotHandler.projectileId = projectileParticleContainer.getId(n);
		this.shotHandler.wasFirst = (this.shotHandler.blockIndex == 0);
		this.shotHandler.defenseArmor.setEffect(this.shotHandler.hitSegController.getEffectContainer().get(HitReceiverType.ARMOR));
		this.shotHandler.defenseArmor.add(VoidElementManager.armorEffectConfiguration);
		this.shotHandler.defenseBlock.setEffect(this.shotHandler.hitSegController.getEffectContainer().get(HitReceiverType.BLOCK));
		this.shotHandler.defenseBlock.add(VoidElementManager.basicEffectConfiguration);
		this.shotHandler.defenseShield.setEffect(this.shotHandler.hitSegController.getEffectContainer().get(HitReceiverType.SHIELD));
		this.shotHandler.defenseShield.add(VoidElementManager.shieldEffectConfiguration);
		this.shotHandler.acidFormula = AcidDamageFormula.AcidFormulaType.values()[projectileParticleContainer.getAcidFormulaIndex(n)].formula;
		this.shotHandler.meta = this.shotHandler.damager.getMetaWeaponEffect(this.shotHandler.weaponId, this.shotHandler.damageDealerType);
		if (this.shotHandler.meta != null && projectileParticleContainer.getBlockHitIndex(n) == 0) {
			this.shotHandler.meta.onHit(segmentController);
		}
		if (!this.isOnServer()) {
			((GameClientState)this.shotHandler.hitSegController.getState()).getWorldDrawer().getExplosionDrawer().addExplosion(cubeRayCastResult.hitPointWorld, 10.0f, 10, this.shotHandler.dmg);
		}
		if (!this.shotHandler.hitSegController.checkAttack(damager, true, true) && (this.isOnServer() || !((GameClientState)this.shotHandler.hitSegController.getState()).getPlayer().isInTutorial())) {
			if (!this.isOnServer()) {
				final ExplosionDrawer explosionDrawer = ((GameClientState)this.shotHandler.hitSegController.getState()).getWorldDrawer().getExplosionDrawer();
				this.pTmp.setByReference(cubeRayCastResult.getSegment(), cubeRayCastResult.getCubePos());
				this.pTmp.getAbsolutePos(this.hTmp);
				final Vector3f hTmp = this.hTmp;
				hTmp.x -= 8.0f;
				final Vector3f hTmp2 = this.hTmp;
				hTmp2.y -= 8.0f;
				final Vector3f hTmp3 = this.hTmp;
				hTmp3.z -= 8.0f;
				explosionDrawer.addExplosion(this.hTmp);
			}
			return ProjectileController.ProjectileHandleState.PROJECTILE_HIT_STOP_INVULNERABLE;
		}
		if (segmentController instanceof TransientSegmentController) {
			((TransientSegmentController)segmentController).setTouched(true, true);
		}
		final ManagerContainer managerContainer;
		if (segmentController instanceof ManagedSegmentController && (managerContainer = ((ManagedSegmentController)segmentController).getManagerContainer()) instanceof ShieldContainerInterface) {
			final ShieldContainerInterface shieldContainerInterface = (ShieldContainerInterface)managerContainer;
			final boolean b = ((SegmentController)segmentController).isUsingLocalShields() && (shieldContainerInterface.getShieldAddOn().isUsingLocalShieldsAtLeastOneActive() || ((SegmentController)segmentController).railController.isDockedAndExecuted());
			n = ((shieldContainerInterface.getShieldAddOn().getShields() > 0.0 || ((SegmentController)segmentController).railController.isDockedAndExecuted()) ? 1 : 0);
			if (b || n != 0) {
				//this.shotHandler.dmg; //this statement is a decompiler artifact
				final InterEffectSet defenseEffectSetTmp;
				(defenseEffectSetTmp = this.shotHandler.defenseEffectSetTmp).setEffect(this.shotHandler.defenseShield);
				if (VoidElementManager.individualBlockEffectArmorOnShieldHit) {
					this.pTmp.setByReference(cubeRayCastResult.getSegment(), cubeRayCastResult.getCubePos());
					if (this.pTmp.isValid()) {
						defenseEffectSetTmp.add(this.pTmp.getInfo().effectArmor);
					}
				}
				try {
					this.shotHandler.dmg = (float)shieldContainerInterface.getShieldAddOn().handleShieldHit(damager, defenseEffectSetTmp, cubeRayCastResult.hitPointWorld, projectileController.getSectorId(), DamageDealerType.PROJECTILE, HitType.WEAPON, this.shotHandler.dmg, this.shotHandler.weaponId);
				}
				catch (SectorNotFoundException ex) {
					ex.printStackTrace();
					this.shotHandler.dmg = 0.0f;
				}
				if (this.shotHandler.dmg <= 0.0f) {
					((SegmentController)segmentController).sendHitConfirmToDamager(damager, true);
					return ProjectileController.ProjectileHandleState.PROJECTILE_HIT_STOP;
				}
				return ProjectileController.ProjectileHandleState.PROJECTILE_HIT_CONTINUE;
			}
		}
		return ProjectileController.ProjectileHandleState.PROJECTILE_NO_HIT;
	}
	
	@Override
	public ProjectileController.ProjectileHandleState handleAfterIfNotStopped(final Damager damager, final ProjectileController projectileController, final Vector3f vector3f, final Vector3f vector3f2, final ProjectileParticleContainer projectileParticleContainer, final int n, final CubeRayCastResult cubeRayCastResult) {
		projectileParticleContainer.setBlockHitIndex(n, this.shotHandler.blockIndex);
		projectileParticleContainer.setShotStatus(n, this.shotHandler.shotStatus.ordinal());
		return ProjectileController.ProjectileHandleState.PROJECTILE_IGNORE;
	}
	
	@Override
	public void afterHandleAlways(final Damager damager, final ProjectileController projectileController, final Vector3f vector3f, final Vector3f vector3f2, final ProjectileParticleContainer projectileParticleContainer, final int n, final CubeRayCastResult cubeRayCastResult) {
		if (this.shotHandler.wasFirst && this.shotHandler.blockIndex > 0 && this.shotHandler.hitSegController != null && this.shotHandler.hitSegController.railController.getRoot().getPhysicsDataContainer().getObject() instanceof RigidBodyExt) {
			this.tmpDir.sub((Tuple3f)vector3f2, (Tuple3f)vector3f);
			if (this.tmpDir.lengthSquared() > 0.0f) {
				this.tmpWorldPos.set((Tuple3f)cubeRayCastResult.hitPointWorld);
				this.applyRecoil(this.tmpWorldPos, this.tmpDir, this.shotHandler.initialDamage);
			}
		}
	}
	
	private void applyRecoil(final Vector3f vector3f, final Vector3f vector3f2, final float n) {
		this.shotHandler.hitSegController.railController.getRoot();
		this.shotHandler.hitSegController.railController.getRoot().hitWithPhysicalRecoil(vector3f, vector3f2, n * 0.1f, false);
	}
	
	public class ShotHandler
	{
		public SegmentController hitSegController;
		public long weaponId;
		public Damager damager;
		public Vector3f posBeforeUpdate;
		public Vector3f posAfterUpdate;
		public Vector3f shootingDir;
		public Vector3f shootingDirRelative;
		public HitType hitType;
		public DamageDealerType damageDealerType;
		private float dmg;
		private float totalArmorValue;
		private float totalDmg;
		private LongArrayList positionsHit;
		private ObjectArrayList<ElementInformation> typesHit;
		private ObjectArrayList<Segment> segmentsHit;
		private ObjectOpenHashSet<Segment> segmentsHitSet;
		private IntArrayList infoIndexHit;
		public InterEffectSet defenseArmor;
		public InterEffectSet defenseShield;
		public InterEffectSet defenseBlock;
		public MetaWeaponEffectInterface meta;
		public ShotStatus shotStatus;
		public final AcidSetting acidSetting;
		public AcidDamageFormula acidFormula;
		public int blockIndex;
		public int penetrationDepth;
		public float projectileWidth;
		public float initialDamage;
		public InterEffectSet defenseEffectSetTmp;
		public ProjectileController.ProjectileHandleState forcedResult;
		public boolean wasFirst;
		public float damageToAcidPercent;
		public int projectileId;
		
		public ShotHandler() {
			this.posBeforeUpdate = new Vector3f();
			this.posAfterUpdate = new Vector3f();
			this.shootingDir = new Vector3f();
			this.shootingDirRelative = new Vector3f();
			this.hitType = HitType.WEAPON;
			this.damageDealerType = DamageDealerType.PROJECTILE;
			this.positionsHit = new LongArrayList();
			this.typesHit = (ObjectArrayList<ElementInformation>)new ObjectArrayList();
			this.segmentsHit = (ObjectArrayList<Segment>)new ObjectArrayList();
			this.segmentsHitSet = (ObjectOpenHashSet<Segment>)new ObjectOpenHashSet();
			this.infoIndexHit = new IntArrayList();
			this.defenseArmor = new InterEffectSet();
			this.defenseShield = new InterEffectSet();
			this.defenseBlock = new InterEffectSet();
			this.shotStatus = ShotStatus.OVER_PENETRATION;
			this.acidSetting = new AcidSetting();
			this.defenseEffectSetTmp = new InterEffectSet();
		}
		
		public void reset() {
			this.meta = null;
			this.weaponId = Long.MIN_VALUE;
			this.damager = null;
			this.hitSegController = null;
			this.dmg = 0.0f;
			this.totalDmg = 0.0f;
			this.totalArmorValue = 0.0f;
			this.initialDamage = 0.0f;
			this.acidFormula = null;
			this.forcedResult = null;
			this.shotStatus = ShotStatus.OVER_PENETRATION;
			this.forcedResult = null;
			this.resetHitBuffer();
		}
		
		public void resetHitBuffer() {
			this.positionsHit.clear();
			this.typesHit.clear();
			this.segmentsHit.clear();
			this.segmentsHitSet.clear();
			this.infoIndexHit.clear();
		}
		
		public ProjectileController.ProjectileHandleState getResult() {
			if (this.forcedResult != null) {
				return this.forcedResult;
			}
			if (this.dmg > 0.0f) {
				return ProjectileController.ProjectileHandleState.PROJECTILE_HIT_CONTINUE;
			}
			return ProjectileController.ProjectileHandleState.PROJECTILE_HIT_STOP;
		}
	}
	
	class ProjectileTraverseHandler extends InnerSegmentIterator
	{
		private ProjectileTraverseHandler() {
		}
		
		@Override
		public boolean onOuterSegmentHitTest(final Segment obj, final boolean b) {
			if (this.debug) {
				System.err.println("OUTER HIT::: " + obj);
			}
			boolean access$300 = true;
			if (!b && ProjectileHandlerSegmentController.this.shotHandler.typesHit.size() > 0) {
				access$300 = ProjectileHandlerSegmentController.this.processHitsUnshielded(ProjectileHandlerSegmentController.this.shotHandler);
			}
			return access$300;
		}
		
		@Override
		public boolean handle(final int n, final int n2, final int n3, final RayTraceGridTraverser rayTraceGridTraverser) {
			final SegmentController contextObj = this.getContextObj();
			final int i = n - this.currentSeg.pos.x + 16;
			final int j = n2 - this.currentSeg.pos.y + 16;
			final int k = n3 - this.currentSeg.pos.z + 16;
			if (this.debug) {
				rayTraceGridTraverser.drawDebug(n + 16, n2 + 16, n3 + 16, this.tests, contextObj.getWorldTransform());
			}
			++this.tests;
			final SegmentData segmentData = this.currentSeg.getSegmentData();
			if (i >= 0 && i < 32 && j >= 0 && j < 32 && k >= 0 && k < 32) {
				this.v.elemA.set((byte)i, (byte)j, (byte)k);
				this.v.elemPosA.set((float)(this.v.elemA.x - 16), (float)(this.v.elemA.y - 16), (float)(this.v.elemA.z - 16));
				final Vector3f elemPosA = this.v.elemPosA;
				elemPosA.x += this.currentSeg.pos.x;
				final Vector3f elemPosA2 = this.v.elemPosA;
				elemPosA2.y += this.currentSeg.pos.y;
				final Vector3f elemPosA3 = this.v.elemPosA;
				elemPosA3.z += this.currentSeg.pos.z;
				this.v.nA.set((Tuple3f)this.v.elemPosA);
				this.v.tmpTrans3.set(this.testCubes);
				this.v.tmpTrans3.basis.transform((Tuple3f)this.v.nA);
				this.v.tmpTrans3.origin.add((Tuple3f)this.v.nA);
				this.rayResult.closestHitFraction = Vector3fTools.length(this.fromA.origin, this.v.tmpTrans3.origin) / Vector3fTools.length(this.fromA.origin, this.toA.origin);
				this.rayResult.setSegment(segmentData.getSegment());
				this.rayResult.getCubePos().set(this.v.elemA);
				this.rayResult.hitPointWorld.set((Tuple3f)this.v.tmpTrans3.origin);
				this.rayResult.hitNormalWorld.sub((Tuple3f)this.fromA.origin, (Tuple3f)this.toA.origin);
				FastMath.normalizeCarmack(this.rayResult.hitNormalWorld);
				final int infoIndex;
				final short type;
				if ((type = segmentData.getType(infoIndex = SegmentData.getInfoIndex((byte)i, (byte)j, (byte)k))) > 0 && ElementInformation.isPhysicalRayTests(type, segmentData, infoIndex) && this.isZeroHpPhysical(segmentData, infoIndex)) {
					if (this.rayResult.isDebug()) {
						System.err.println("HIT BLOCK: " + i + ", " + j + "; " + k + "; BLOCK: " + (n - 16) + ", " + (n2 - 16) + ", " + (n3 - 16));
					}
					this.rayResult.collisionObject = this.collisionObject;
					final boolean access$400 = ProjectileHandlerSegmentController.this.processRawHitUnshielded(this.currentSeg, infoIndex, type, this.v.elemA, this.v.elemPosA, this.testCubes);
					if (this.rayResult.isDebug()) {
						System.err.println("HIT BLOCK: " + i + ", " + j + "; " + k + "; BLOCK: " + (n - 16) + ", " + (n2 - 16) + ", " + (n3 - 16) + " -CONTINUE: " + access$400);
					}
					if (!access$400) {
						this.hitSignal = true;
					}
					return access$400;
				}
				if (ProjectileHandlerSegmentController.this.shotHandler.typesHit.size() > 0) {
					this.hitSignal = true;
					final boolean access$401 = ProjectileHandlerSegmentController.this.processHitsUnshielded(ProjectileHandlerSegmentController.this.shotHandler);
					if (this.rayResult.isDebug()) {
						System.err.println("*AIR* HIT BLOCK ACCUMULATED (air block): " + i + ", " + j + "; " + k + "; BLOCK: " + (n - 16) + ", " + (n2 - 16) + ", " + (n3 - 16) + "; type: " + type + "; continue: " + access$401);
					}
					return access$401;
				}
			}
			return true;
		}
	}
	
	public enum ShotStatus
	{
		OVER_PENETRATION, 
		STOPPED, 
		STOPPED_ACID, 
		NORMAL;
	}
}
