// 
// Decompiled by Procyon v0.5.36
// 

package org.schema.game.common.controller.damage.beam;

import org.schema.game.common.data.element.ElementInformation;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.controller.EditableSendableSegmentController;
import org.schema.game.common.controller.damage.effects.InterEffectHandler;
import com.bulletphysics.collision.dispatch.CollisionObject;
import org.schema.game.common.controller.elements.ShieldAddOn;
import org.schema.game.common.data.world.SectorNotFoundException;
import org.schema.game.common.controller.elements.ShieldContainerInterface;
import org.schema.game.common.data.ManagedSegmentController;
import org.schema.common.FastMath;
import org.schema.game.common.controller.elements.VoidElementManager;
import org.schema.game.common.controller.damage.HitReceiverType;
import org.schema.game.common.Starter;
import org.schema.game.common.controller.TransientSegmentController;
import org.schema.game.common.data.world.Segment;
import java.util.Collection;
import org.schema.schine.graphicsengine.core.Timer;
import javax.vecmath.Vector3f;
import org.schema.game.common.controller.BeamHandlerContainer;
import org.schema.game.common.controller.elements.BeamState;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.damage.HitType;
import org.schema.game.common.controller.damage.DamageDealerType;
import org.schema.game.common.controller.damage.Damager;
import org.schema.game.common.controller.damage.effects.InterEffectSet;
import org.schema.game.common.data.SegmentPiece;

public class DamageBeamHitHandlerSegmentController implements DamageBeamHitHandler
{
	private SegmentPiece segmentPiece;
	private final InterEffectSet defenseShield;
	private final InterEffectSet defenseBlock;
	private final InterEffectSet defenseArmor;
	private Damager damager;
	private final DamageDealerType damageDealerType;
	private long weaponId;
	private float dam;
	private HitType hitType;
	private SegmentController hitController;
	private final InterEffectSet defense;
	
	public DamageBeamHitHandlerSegmentController() {
		this.segmentPiece = new SegmentPiece();
		this.defenseShield = new InterEffectSet();
		this.defenseBlock = new InterEffectSet();
		this.defenseArmor = new InterEffectSet();
		this.damageDealerType = DamageDealerType.BEAM;
		this.weaponId = Long.MIN_VALUE;
		this.defense = new InterEffectSet();
	}
	
	@Override
	public void reset() {
		this.segmentPiece.reset();
		this.defenseShield.reset();
		this.defenseBlock.reset();
		this.defenseArmor.reset();
		this.damager = null;
		this.hitType = null;
		this.hitController = null;
		this.weaponId = Long.MIN_VALUE;
		this.dam = 0.0f;
	}
	
	public int onBeamDamage(final BeamState beamState, final int n, BeamHandlerContainer<?> container, SegmentPiece segmentPiece, Vector3f vector3f, final Vector3f vector3f2, final Timer timer, final Collection<Segment> collection) {
		System.err.println("#XXX: ");
		System.err.println("#XXX: onBeamDamage");
		this.segmentPiece.setByReference(segmentPiece);
		if (!this.segmentPiece.isValid()) {
			System.err.println(this.segmentPiece.getSegmentController().getState() + " HITTTING INVALID PIECE");
			return 0;
		}
		this.hitController = segmentPiece.getSegmentController();
		if (this.hitController instanceof TransientSegmentController) {
			((TransientSegmentController)this.hitController).setTouched(true, true);
		}
		Starter.modManager.onSegmentControllerHitByBeam(this.hitController);
		this.segmentPiece.getType();
		ElementInformation info = this.segmentPiece.getInfo();
		this.damager = (Damager)beamState.getHandler().getBeamShooter();
		if (!this.hitController.checkAttack(this.damager, true, true)) {
			return 0;
		}
		this.defenseShield.setEffect(this.hitController.getEffectContainer().get(HitReceiverType.SHIELD));
		this.defenseShield.add(VoidElementManager.shieldEffectConfiguration);
		if (VoidElementManager.individualBlockEffectArmorOnShieldHit) {
			this.defenseShield.add(info.effectArmor);
		}
		this.defenseArmor.setEffect(this.hitController.getEffectContainer().get(HitReceiverType.ARMOR));
		this.defenseArmor.add(VoidElementManager.armorEffectConfiguration);
		this.defenseBlock.setEffect(this.hitController.getEffectContainer().get(HitReceiverType.BLOCK));
		this.defenseBlock.add(VoidElementManager.basicEffectConfiguration);
		this.weaponId = beamState.weaponId;
		this.dam = n * beamState.getPowerByBeamLength();
		if (beamState.beamType == 6) {
			this.dam = FastMath.ceil(info.getMaxHitPointsFull() / (float)info.getMaxHitPointsByte()) * (int)(n * beamState.getPowerByBeamLength());
		}
		this.hitType = beamState.hitType;
		this.dam *= this.hitController.getDamageTakenMultiplier(this.damageDealerType);
		if (this.damager != null) {
			this.dam *= this.damager.getDamageGivenMultiplier();
		}
		boolean b = false;
		Label_0661: {
			if (!beamState.ignoreShield && this.hitController instanceof ManagedSegmentController && ((ManagedSegmentController)this.hitController).getManagerContainer() instanceof ShieldContainerInterface) {
				ShieldContainerInterface sci;
				final ShieldAddOn shieldAddOn = (sci = (ShieldContainerInterface)(((ManagedSegmentController)this.hitController).getManagerContainer())).getShieldAddOn();
				if (this.hitController.isUsingLocalShields()) {
					if (!shieldAddOn.isUsingLocalShieldsAtLeastOneActive()) {
						if (!this.hitController.railController.isDockedAndExecuted()) {
							break Label_0661;
						}
					}
					try {
						final float dam = this.dam;
						this.dam = (float)shieldAddOn.handleShieldHit(this.damager, this.defenseShield, beamState.hitPoint, beamState.hitSectorId, this.damageDealerType, this.hitType, this.dam, this.weaponId);
					}
					catch (SectorNotFoundException ex) {
						ex.printStackTrace();
						this.dam = 0.0f;
					}
					if (this.dam <= 0.0f) {
						this.hitController.sendHitConfirmToDamager(this.damager, true);
						return 0;
					}
				}
				else {
					Label_0639: {
						if (shieldAddOn.getShields() <= 0.0) {
							if (!this.hitController.railController.isDockedAndExecuted()) {
								break Label_0639;
							}
						}
						try {
							this.dam = (float)shieldAddOn.handleShieldHit(this.damager, this.defenseShield, beamState.hitPoint, beamState.hitSectorId, this.damageDealerType, this.hitType, this.dam, this.weaponId);
						}
						catch (SectorNotFoundException ex2) {
							ex2.printStackTrace();
							this.dam = 0.0f;
						}
						if (this.dam <= 0.0f) {
							this.hitController.sendHitConfirmToDamager(this.damager, true);
							return 0;
						}
					}
					b = sci.getShieldAddOn().getShields() > 0.0;
				}
			}
		}
		this.hitController.sendHitConfirmToDamager(this.damager, b);
		this.dam = (float)beamState.calcPreviousArmorDamageReduction(this.dam);
		this.dam = this.hitController.getHpController().onHullDamage(this.damager, this.dam, this.segmentPiece.getType(), this.damageDealerType);
		if (this.doDamageOnBlock(this.segmentPiece, beamState) && info.isArmor()) {
			beamState.getHandler().onArmorBlockKilled(beamState, info.getArmorValue());
		}
		final CollisionObject object;
		if ((object = this.hitController.getPhysicsDataContainer().getObject()) != null) {
			object.activate(true);
		}
		Starter.modManager.onSegmentControllerDamageTaken(this.hitController);
		return n;
	}

        private boolean doDamageOnBlock(final SegmentPiece segmentPiece, final BeamState beamState) {
		segmentPiece.getOrientation();
		final short type = segmentPiece.getType();
		final ElementInformation info;
		final HitReceiverType hitReceiverType = (info = segmentPiece.getInfo()).isArmor() ? HitReceiverType.ARMOR : HitReceiverType.BLOCK;
		final InterEffectSet effect = info.isArmor() ? this.defenseArmor : this.defenseBlock;
		assert this.damager != null;
		assert this.damageDealerType != null;
		final InterEffectSet attackEffectSet = this.damager.getAttackEffectSet(this.weaponId, this.damageDealerType);
		this.defense.setEffect(effect);
		this.defense.scaleAdd(info.effectArmor, 1.0f);
		final float handleEffects;
		final int round = Math.round(handleEffects = InterEffectHandler.handleEffects(this.dam, attackEffectSet, this.defense, this.hitType, this.damageDealerType, hitReceiverType, type));
		final int hitpointsFull = segmentPiece.getHitpointsFull();
		final EditableSendableSegmentController editableSendableSegmentController;
		final float damageElement = (editableSendableSegmentController = (EditableSendableSegmentController)this.hitController).damageElement(type, segmentPiece.getInfoIndex(), segmentPiece.getSegment().getSegmentData(), round, this.damager, this.damageDealerType, this.weaponId);
		final int n = (int)(hitpointsFull - damageElement);
		final float max = Math.max(0.0f, handleEffects - damageElement);
		if (n > 0) {
			if (this.isOnServer()) {
				editableSendableSegmentController.sendBlockHpByte(segmentPiece.getAbsoluteIndex(), ElementKeyMap.convertToByteHP(type, n));
				editableSendableSegmentController.onBlockDamage(segmentPiece.getAbsoluteIndex(), type, round, this.damageDealerType, this.damager);
			}
		}
		else {
			if (this.isOnServer()) {
				editableSendableSegmentController.sendBlockKill(segmentPiece.getAbsoluteIndex());
				editableSendableSegmentController.onBlockKill(segmentPiece, this.damager);
			}
			if (this.isOnServer() && beamState.acidDamagePercent > 0.0f) {
				final int n2 = (int)(beamState.acidDamagePercent * max);
				editableSendableSegmentController.getAcidDamageManagerServer().inputDamage(segmentPiece.getAbsoluteIndex(), beamState.hitNormalRelative, n2, 16, (Damager)beamState.getHandler().getBeamShooter(), beamState.weaponId, true, false);
				Math.max(max - n2, 0.0f);
			}
		}
		return n <= 0;
	}
	
	private boolean isOnServer() {
		return this.hitController.isOnServer();
	}
}
