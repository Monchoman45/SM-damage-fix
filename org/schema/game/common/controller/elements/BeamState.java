// 
// Decompiled by Procyon v0.5.36
// 

package org.schema.game.common.controller.elements;

import org.schema.game.common.data.world.SimpleTransformableSendableObject;
import org.schema.common.FastMath;
import org.schema.common.util.linAlg.Vector3fTools;
import javax.vecmath.Tuple4f;
import javax.vecmath.Tuple3f;
import org.schema.schine.graphicsengine.core.MouseButton;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.controller.ArmorValue;
import org.schema.game.common.controller.damage.HitType;
import com.bulletphysics.linearmath.Transform;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.element.meta.MetaObject;
import org.schema.game.common.data.element.beam.BeamReloadCallback;
import org.schema.common.util.linAlg.Vector3b;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.element.beam.AbstractBeamHandler;
import javax.vecmath.Vector4f;
import org.schema.game.common.data.SegmentPiece;
import javax.vecmath.Vector3f;

public class BeamState
{
	public final Vector3f hitPointCache;
	public final SegmentPiece p1;
	public final SegmentPiece p2;
	public final Vector3f relativePos;
	public final long identifyerSig;
	public final Vector3f from;
	public final Vector3f to;
	public final Vector4f color;
	private final AbstractBeamHandler<?> handler;
	public Vector3f hitPoint;
	public float timeRunningSinceLastUpdate;
	public long lastCheck;
	public float timeSpent;
	public SegmentPiece segmentHit;
	public SegmentPiece currentHit;
	public float hitOneSegment;
	public float hitBlockTime;
	public SegmentController cachedLastSegment;
	public Vector3b cachedLastPos;
	public float camDistStart;
	public float camDistEnd;
	public float size;
	public boolean[] mouseButton;
	public long weaponId;
	public float timeOutInSecs;
	public float burstTime;
	public float initialTicks;
	public float timeRunning;
	public BeamReloadCallback reloadCallback;
	public float powerConsumptionPerTick;
	public float powerConsumptionExtraPerTick;
	public float totalConsumedPower;
	public int beamType;
	public boolean markDeath;
	public MetaObject originMetaObject;
	public Vector3i controllerPos;
	public int ticksToDo;
	public long fireStart;
	public boolean dontFade;
	public int ticksDone;
	private float tickRate;
	private float power;
	public double railParent;
	public double railChild;
	public boolean handheld;
	public Vector3f drawVarsCamPos;
	public Vector3f drawVarsStart;
	public Vector3f drawVarsEnd;
	public float drawVarsAxisAngle;
	public float drawVarsDist;
	public float drawVarsLenDiff;
	public Transform drawVarsDrawTransform;
	public Transform lastHitTrans;
	public Transform lastSegConTrans;
	public Vector3i lastHitPos;
	public boolean oldPower;
	public boolean latchOn;
	public long firstLatch;
	public HitType hitType;
	public Transform initalRelativeTranform;
	public boolean friendlyFire;
	public boolean penetrating;
	public float acidDamagePercent;
	public Vector3f hitNormalWorld;
	public Vector3f hitNormalRelative;
	public boolean checkLatchConnection;
	public Vector3f dirTmp;
	public Vector3f fromInset;
	public Vector3f toInset;
	public float beamLength;
	public float minEffectiveRange;
	public float minEffectiveValue;
	public float maxEffectiveRange;
	public float maxEffectiveValue;
	public int hitSectorId;
	private final Vector3f dd;
	public boolean ignoreShield;
	//#XXX: new armor counter
	public float armorValue;
	//#XXX:
	public boolean ignoreArmor;
	
	public BeamState(final long identifyerSig, final Vector3f vector3f, final Vector3f vector3f2, final Vector3f vector3f3, final PlayerState playerState, final float tickRate, final float power, final long weaponId, final int beamType, final MetaObject originMetaObject, final Vector3i controllerPos, final boolean handheld, final boolean latchOn, final boolean checkLatchConnection, final HitType hitType, final AbstractBeamHandler<?> handler) {
		this.hitPointCache = new Vector3f();
		this.p1 = new SegmentPiece();
		this.p2 = new SegmentPiece();
		this.relativePos = new Vector3f();
		this.from = new Vector3f();
		this.to = new Vector3f();
		this.color = new Vector4f();
		this.timeRunningSinceLastUpdate = 0.0f;
		this.lastCheck = -1L;
		this.cachedLastPos = new Vector3b(-1.0f, -1.0f, -1.0f);
		this.size = 1.0f;
		this.mouseButton = new boolean[MouseButton.values().length];
		this.drawVarsCamPos = new Vector3f();
		this.drawVarsStart = new Vector3f();
		this.drawVarsEnd = new Vector3f();
		this.drawVarsDrawTransform = new Transform();
		this.lastHitTrans = new Transform();
		this.lastSegConTrans = new Transform();
		this.lastHitPos = new Vector3i();
		this.initalRelativeTranform = new Transform();
		this.hitNormalWorld = new Vector3f();
		this.hitNormalRelative = new Vector3f();
		this.checkLatchConnection = true;
		this.dirTmp = new Vector3f();
		this.fromInset = new Vector3f();
		this.toInset = new Vector3f();
		this.hitSectorId = -1;
		this.dd = new Vector3f();
		//#XXX: new armor counter
		this.armorValue = 0.0f;
		//#XXX:
		this.identifyerSig = identifyerSig;
		this.from.set((Tuple3f)vector3f2);
		this.to.set((Tuple3f)vector3f3);
		this.relativePos.set((Tuple3f)vector3f);
		this.setTickRate(tickRate);
		this.setPower(power);
		this.handler = handler;
		this.weaponId = weaponId;
		this.beamType = beamType;
		this.originMetaObject = originMetaObject;
		this.color.set((Tuple4f)handler.getColor(this));
		this.controllerPos = controllerPos;
		this.handheld = handheld;
		this.oldPower = handler.isUsingOldPower();
		this.latchOn = latchOn;
		this.checkLatchConnection = checkLatchConnection;
		this.firstLatch = Long.MIN_VALUE;
		this.hitType = hitType;
	}
	
	public float getTickRate() {
		return this.tickRate;
	}
	
	public void setTickRate(final float tickRate) {
		this.tickRate = tickRate;
	}
	
	@Override
	public int hashCode() {
		return (int)this.identifyerSig * ((SimpleTransformableSendableObject)this.getHandler().getBeamShooter()).hashCode();
	}
	
	@Override
	public boolean equals(final Object o) {
		return ((BeamState)o).getHandler().getBeamShooter() == this.getHandler().getBeamShooter() && ((BeamState)o).identifyerSig == this.identifyerSig;
	}
	
	public float getPower() {
		return this.power;
	}
	
	public void setPower(final float power) {
		this.power = power;
	}
	
	public AbstractBeamHandler<?> getHandler() {
		return this.handler;
	}
	
	public boolean isAlive() {
		return !this.markDeath && (this.dontFade || this.timeRunningSinceLastUpdate <= this.timeOutInSecs) && ((this.burstTime <= 0.0f && this.timeRunningSinceLastUpdate < this.timeOutInSecs) || (this.initialTicks > 0.0f || this.ticksToDo > 0 || this.timeRunning < this.timeOutInSecs));
	}
	
	public boolean isOnServer() {
		return this.handler.isOnServer();
	}
	
	public void reset() {
		this.ignoreArmor = false;
		this.ignoreShield = false;
		this.currentHit = null;
		this.hitPoint = null;
		this.segmentHit = null;
		this.hitSectorId = -1;
		this.hitOneSegment = 0.0f;
		this.hitBlockTime = 0.0f;
		this.timeSpent = 0.0f;
		this.firstLatch = Long.MIN_VALUE;
		this.initalRelativeTranform.setIdentity();
		//#XXX: new armor counter
		this.armorValue = 0.0f;
		//#XXX:
	}
	
	public float getPowerByBeamLength() {
		return this.power * this.getBeamLengthModifier();
	}
	
	public float getBeamLengthModifier() {
		if (this.beamType == 6) {
			return 1.0f;
		}
		if (!this.penetrating && this.beamLength > 0.0f && this.maxEffectiveValue != this.minEffectiveValue) {
			final float n;
			float n2;
			if ((n = Vector3fTools.diffLength(this.to, this.from) / this.beamLength) >= this.maxEffectiveRange) {
				n2 = this.maxEffectiveValue;
			}
			else if (n <= this.minEffectiveRange) {
				n2 = this.minEffectiveValue;
			}
			else {
				final float n3 = (n - this.minEffectiveRange) / Math.abs(this.maxEffectiveRange - this.minEffectiveRange);
				if (this.maxEffectiveValue < this.minEffectiveValue) {
					n2 = this.minEffectiveValue - (this.minEffectiveValue - this.maxEffectiveValue) * n3;
				}
				else {
					n2 = this.minEffectiveValue + (this.maxEffectiveValue - this.minEffectiveValue) * n3;
				}
			}
			return n2;
		}
		return 1.0f;
	}

	//#XXX: beam armor formula
	//the same fixes that went into cannons also apply here
	public float calcPreviousArmorDamageReduction(float damage) {
		System.err.println("#XXX: calcPreviousArmorDamageReduction");
		float reduced = damage;
		if (VoidElementManager.ARMOR_CALC_STYLE == ArmorDamageCalcStyle.EXPONENTIAL) {
			reduced = Math.max(0.0f, FastMath.pow(damage, VoidElementManager.BEAM_ARMOR_EXPONENTIAL_INCOMING_EXPONENT) / (FastMath.pow(this.armorValue, VoidElementManager.BEAM_ARMOR_EXPONENTIAL_ARMOR_VALUE_TOTAL_EXPONENT) + FastMath.pow(damage, VoidElementManager.BEAM_ARMOR_EXPONENTIAL_INCOMING_DAMAGE_ADDED_EXPONENT)));
		}
		else {
			reduced = Math.max(0.0f, damage - VoidElementManager.BEAM_ARMOR_FLAT_DAMAGE_REDUCTION * damage);
			reduced = Math.max(0.0f, reduced - Math.min(VoidElementManager.BEAM_ARMOR_THICKNESS_DAMAGE_REDUCTION_MAX, VoidElementManager.BEAM_ARMOR_THICKNESS_DAMAGE_REDUCTION * ArmorValue.lastSize * reduced));
		}
		return reduced / damage;
	}
	//#XXX:
}
