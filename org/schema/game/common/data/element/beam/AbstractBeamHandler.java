// 
// Decompiled by Procyon v0.5.36
// 

package org.schema.game.common.data.element.beam;

import org.schema.game.common.controller.elements.mines.MineController;
import org.schema.game.common.data.world.Sector;
import org.schema.game.common.data.physics.RayTraceGridTraverser;
import org.schema.game.common.data.element.ColorBeamInterface;
import org.schema.game.common.controller.damage.Damager;
import org.schema.game.common.controller.elements.power.reactor.StabilizerPath;
import com.bulletphysics.collision.dispatch.CollisionObject;
import org.schema.common.util.linAlg.TransformTools;
import org.schema.game.client.data.GameStateInterface;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import org.schema.game.client.controller.GameClientController;
import org.schema.game.common.data.world.SegmentData;
import org.schema.game.common.data.physics.ModifiedDynamicsWorld;
import org.schema.common.util.StringTools;
import org.schema.schine.common.language.Lng;
import org.schema.game.server.data.GameServerState;
import org.schema.schine.network.objects.Sendable;
import org.schema.game.common.controller.elements.ManagerContainer;
import com.bulletphysics.collision.dispatch.CollisionWorld;
import org.schema.game.common.data.physics.RigidDebrisBody;
import org.schema.schine.network.client.ClientController;
import org.schema.schine.graphicsengine.forms.debug.DebugPoint;
import org.schema.schine.graphicsengine.forms.debug.DebugDrawer;
import org.schema.schine.graphicsengine.core.settings.EngineSettings;
import org.schema.game.client.data.GameClientState;
import org.schema.schine.network.Identifiable;
import org.schema.schine.network.client.ClientState;
import java.util.Iterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Collection;
import org.schema.schine.graphicsengine.core.Timer;
import org.schema.game.common.data.element.ElementNotFoundException;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.common.FastMath;
import javax.vecmath.Tuple3f;
import org.schema.common.util.linAlg.Vector3fTools;
import org.schema.schine.graphicsengine.core.MouseButton;
import org.schema.game.common.controller.elements.ShootingRespose;
import org.schema.game.common.controller.elements.beam.BeamCommand;
import org.schema.game.client.view.beam.BeamColors;
import org.schema.game.common.controller.damage.effects.MetaWeaponEffectInterface;
import org.schema.game.common.controller.damage.effects.InterEffectSet;
import org.schema.game.common.controller.damage.DamageDealerType;
import org.schema.schine.network.StateInterface;
import org.schema.game.common.data.physics.SegmentTraversalInterface;
import org.schema.game.common.data.physics.InnerSegmentIterator;
import org.schema.game.common.controller.SegmentController;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.schema.game.common.controller.ArmorValue;
import org.schema.game.common.controller.ArmorCheckTraverseHandler;
import org.schema.game.common.data.physics.RayCubeGridSolver;
import com.bulletphysics.linearmath.Transform;
import org.schema.game.common.data.world.TransformaleObjectTmpVars;
import org.schema.game.common.data.physics.BlockRecorder;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.physics.CubeRayCastResult;
import org.schema.game.client.view.beam.BeamDrawer;
import org.schema.game.common.controller.BeamHandlerContainer;
import org.schema.schine.graphicsengine.forms.BoundingBox;
import javax.vecmath.Vector3f;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.world.Segment;
import java.util.Set;
import org.schema.game.common.controller.elements.BeamState;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.vecmath.Vector4f;
import org.schema.schine.physics.IgnoreBlockRayTestInterface;
import org.schema.game.common.data.physics.NonBlockHitCallback;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;
import org.schema.game.common.controller.elements.VoidElementManager;

public abstract class AbstractBeamHandler<E extends SimpleTransformableSendableObject> implements NonBlockHitCallback, IgnoreBlockRayTestInterface
{
	private static final Vector4f color1Blue;
	private static final Vector4f color1Green;
	private static final Vector4f color1Red;
	private static final Vector4f color1Yellow;
	private static final Vector4f color1White;
	private static final Vector4f color1Purple;
	private final Long2ObjectOpenHashMap<BeamState> beamStates;
	private final Set<Segment> updatedSegments;
	private final Vector3i tmp;
	Vector3f start;
	Vector3f end;
	String[] cannotHitReason;
	Vector3f tmpVal;
	BoundingBox secTmpBB;
	private BeamHandlerContainer<E> owner;
	private boolean lastActive;
	private Vector3f dirTmp;
	private BeamDrawer drawer;
	private final E fromObj;
	private final CubeRayCastResult rayCallback;
	private SegmentPiece tmpHit;
	private Int2ObjectOpenHashMap<BlockRecorder> blockRecorder;
	private final TransformaleObjectTmpVars v;
	private final Transform inFrom;
	private final Transform inTo;
	private final Transform inRes;
	private final Transform outFrom;
	private final Transform outTo;
	private final Transform outRes;
	RayCubeGridSolver raySolver;
	BigTrav bt;
	BigTravMines btMines;
	private final ArmorCheckTraverseHandler pt;
	private CubeRayCastResult rayCallbackTraverse;
	private ArmorValue armorValue;
	
	public AbstractBeamHandler(final BeamHandlerContainer<E> owner, final E fromObj) {
		this.beamStates = (Long2ObjectOpenHashMap<BeamState>)new Long2ObjectOpenHashMap();
		this.updatedSegments = (Set<Segment>)new ObjectOpenHashSet();
		this.tmp = new Vector3i();
		this.start = new Vector3f();
		this.end = new Vector3f();
		this.cannotHitReason = new String[1];
		this.tmpVal = new Vector3f();
		this.secTmpBB = new BoundingBox();
		this.dirTmp = new Vector3f();
		this.tmpHit = new SegmentPiece();
		this.v = new TransformaleObjectTmpVars();
		this.inFrom = new Transform();
		this.inTo = new Transform();
		this.inRes = new Transform();
		this.outFrom = new Transform();
		this.outTo = new Transform();
		this.outRes = new Transform();
		this.raySolver = new RayCubeGridSolver();
		this.bt = new BigTrav();
		this.btMines = new BigTravMines();
		this.pt = new ArmorCheckTraverseHandler();
		this.rayCallbackTraverse = new CubeRayCastResult(new Vector3f(), new Vector3f(), (Object)null, new SegmentController[0]) {
			@Override
			public InnerSegmentIterator newInnerSegmentIterator() {
				return AbstractBeamHandler.this.pt;
			}
		};
		this.armorValue = new ArmorValue();
		this.owner = owner;
		this.fromObj = fromObj;
		this.rayCallback = new CubeRayCastResult(new Vector3f(), new Vector3f(), fromObj, new SegmentController[0]);
	}
	
	public boolean isOnServer() {
		return this.getBeamShooter().isOnServer();
	}
	
	public StateInterface getState() {
		return this.getBeamShooter().getState();
	}
	
	public abstract InterEffectSet getAttackEffectSet(final long p0, final DamageDealerType p1);
	
	public abstract MetaWeaponEffectInterface getMetaWeaponEffect(final long p0, final DamageDealerType p1);
	
	public static Vector4f getColorRange(final BeamColors beamColors) {
		switch (beamColors) {
			case BLUE: {
				return AbstractBeamHandler.color1Blue;
			}
			case GREEN: {
				return AbstractBeamHandler.color1Green;
			}
			case RED: {
				return AbstractBeamHandler.color1Red;
			}
			case PURPLE: {
				return AbstractBeamHandler.color1Purple;
			}
			case YELLOW: {
				return AbstractBeamHandler.color1Yellow;
			}
			case WHITE: {
				return AbstractBeamHandler.color1White;
			}
			default: {
				return AbstractBeamHandler.color1Green;
			}
		}
	}
	
	public SimpleTransformableSendableObject<?> getShootingEntity() {
		return this.getBeamShooter();
	}
	
	public E getBeamShooter() {
		return this.fromObj;
	}
	
	public ShootingRespose addBeam(final BeamCommand beamCommand) {
		final BeamState beamState;
		if ((beamState = (BeamState)this.beamStates.get(beamCommand.identifier)) == null) {
			if (beamCommand.reloadCallback.isInitializing(beamCommand.currentTime)) {
				return ShootingRespose.INITIALIZING;
			}
			if (!beamCommand.reloadCallback.canUse(beamCommand.currentTime, false)) {
				return ShootingRespose.RELOADING;
			}
			if (beamCommand.reloadCallback.isUsingPowerReactors() || beamCommand.reloadCallback.canConsumePower(beamCommand.powerConsumedByTick)) {
				if (beamCommand.lastShot) {
					beamCommand.reloadCallback.setShotReloading((long)(beamCommand.cooldownSec * 1000.0f));
				}
				final BeamState beamState2 = new BeamState(beamCommand.identifier, beamCommand.relativePos, beamCommand.from, beamCommand.to, beamCommand.playerState, beamCommand.tickRate, beamCommand.beamPower, beamCommand.weaponId, beamCommand.beamType, beamCommand.originMetaObject, beamCommand.controllerPos, beamCommand.handheld, beamCommand.latchOn, beamCommand.checkLatchConnection, beamCommand.hitType, this);
				if (beamCommand.beamTimeout < 0.0f) {
					beamState2.timeOutInSecs = this.getBeamTimeoutInSecs();
				}
				else {
					beamState2.timeOutInSecs = beamCommand.beamTimeout;
				}
				beamState2.reloadCallback = beamCommand.reloadCallback;
				beamState2.powerConsumptionPerTick = beamCommand.powerConsumedByTick;
				beamState2.powerConsumptionExtraPerTick = beamCommand.powerConsumedExtraByTick;
				final MouseButton[] values = MouseButton.values();
				for (int i = 0; i < values.length; ++i) {
					beamState2.mouseButton[i] = (beamCommand.playerState != null && beamCommand.playerState.isMouseButtonDown(values[i].button));
				}
				beamState2.timeRunningSinceLastUpdate = 0.0f;
				beamState2.timeRunning = 0.0f;
				beamState2.dontFade = beamCommand.dontFade;
				beamState2.size = beamCommand.beamPower;
				beamState2.burstTime = beamCommand.bursttime;
				beamState2.initialTicks = beamCommand.initialTicks;
				beamState2.railParent = beamCommand.railParent;
				beamState2.railChild = beamCommand.railChild;
				beamState2.ticksToDo = Math.round((beamCommand.bursttime > 0.0f) ? (beamCommand.bursttime / beamCommand.tickRate) : 0.0f);
				beamState2.fireStart = beamCommand.currentTime;
				beamState2.latchOn = beamCommand.latchOn;
				beamState2.checkLatchConnection = beamCommand.checkLatchConnection;
				beamState2.firstLatch = Long.MIN_VALUE;
				beamState2.hitType = beamCommand.hitType;
				beamState2.minEffectiveRange = beamCommand.minEffectiveRange;
				beamState2.minEffectiveValue = beamCommand.minEffectiveValue;
				beamState2.maxEffectiveRange = beamCommand.maxEffectiveRange;
				beamState2.maxEffectiveValue = beamCommand.maxEffectiveValue;
				beamState2.ignoreShield = beamCommand.ignoreShields;
				beamState2.ignoreArmor = beamCommand.ignoreArmor;
				beamState2.friendlyFire = beamCommand.firendlyFire;
				beamState2.penetrating = beamCommand.penetrating;
				beamState2.acidDamagePercent = beamCommand.acidDamagePercent;
				beamState2.beamLength = Vector3fTools.diffLength(beamState2.from, beamState2.to);
				if (beamCommand.beamTimeout < 0.0f) {
					beamCommand.reloadCallback.flagBeamFiredWithoutTimeout();
				}
				this.beamStates.put(beamCommand.identifier, beamState2);
				return ShootingRespose.FIRED;
			}
			return ShootingRespose.NO_POWER;
		}
		else {
			final MouseButton[] values2 = MouseButton.values();
			for (int j = 0; j < values2.length; ++j) {
				beamState.mouseButton[j] = (beamCommand.playerState != null && beamCommand.playerState.isMouseButtonDown(values2[j].button));
			}
			if (beamCommand.reloadCallback.isUsingPowerReactors() || beamCommand.reloadCallback.canConsumePower(beamCommand.powerConsumedByTick)) {
				if (beamCommand.beamTimeout < 0.0f) {
					beamState.timeOutInSecs = this.getBeamTimeoutInSecs();
				}
				else {
					beamState.timeOutInSecs = beamCommand.beamTimeout;
				}
				beamState.from.set((Tuple3f)beamCommand.from);
				if (!beamState.latchOn || beamState.currentHit == null) {
					beamState.to.set((Tuple3f)beamCommand.to);
				}
				if (beamCommand.beamTimeout < 0.0f) {
					beamCommand.reloadCallback.flagBeamFiredWithoutTimeout();
				}
				beamState.timeRunningSinceLastUpdate = 0.0f;
				return ShootingRespose.FIRED;
			}
			beamState.timeOutInSecs = 0.0f;
			return ShootingRespose.NO_POWER;
		}
	}
	
	public int beamHitNonCube(final BeamState beamState) {
		beamState.hitOneSegment += beamState.timeSpent;
		final float beamToHitInSecs = this.getBeamToHitInSecs(beamState);
		int fastFloor = FastMath.fastFloor(beamState.hitOneSegment / beamToHitInSecs);
		beamState.hitOneSegment -= fastFloor * beamToHitInSecs;
		this.cannotHitReason[0] = "";
		if (beamState.initialTicks > 0.0f) {
			fastFloor += (int)beamState.initialTicks;
			beamState.initialTicks = 0.0f;
		}
		return fastFloor;
	}
	
	public abstract boolean canhit(final BeamState p0, final SegmentController p1, final String[] p2, final Vector3i p3);
	
	public void clearStates() {
		this.beamStates.clear();
		if (this.drawer != null) {
			this.drawer.notifyDraw(this, false);
		}
	}
	
	public BeamState getBeam(final Vector3i vector3i) throws ElementNotFoundException {
		return this.getBeam(ElementCollection.getIndex(vector3i));
	}
	
	public synchronized BeamState getBeam(final long n) throws ElementNotFoundException {
		return (BeamState)this.getBeamStates().get(n);
	}
	
	public Long2ObjectOpenHashMap<BeamState> getBeamStates() {
		return this.beamStates;
	}
	
	public abstract float getBeamTimeoutInSecs();
	
	public abstract float getBeamToHitInSecs(final BeamState p0);
	
	public boolean isAnyBeamActiveActive() {
		return !this.beamStates.isEmpty();
	}
	
	public abstract int onBeamHit(final BeamState p0, final int p1, final BeamHandlerContainer<E> p2, final SegmentPiece p3, final Vector3f p4, final Vector3f p5, final Timer p6, final Collection<Segment> p7);
	
	protected abstract boolean onBeamHitNonCube(final BeamState p0, final int p1, final BeamHandlerContainer<E> p2, final Vector3f p3, final Vector3f p4, final CubeRayCastResult p5, final Timer p6, final Collection<Segment> p7);
	
	public void setDrawer(final BeamDrawer drawer) {
		this.drawer = drawer;
	}
	
	public abstract void transform(final BeamState p0);
	
	public void update(final Timer timer) {
		if (this.getBeamStates().isEmpty()) {
			return;
		}
		this.updatedSegments.clear();
		final ObjectIterator iterator = this.getBeamStates().values().iterator();
		final float delta = timer.getDelta();
		while (((Iterator)iterator).hasNext()) {
			final BeamState beamState;
			if (!(beamState = ((Iterator<BeamState>)iterator).next()).isAlive()) {
				((Iterator)iterator).remove();
				if (beamState.latchOn && this.getBeamShooter() instanceof SegmentController && this.getBeamShooter().isOnServer()) {
					((SegmentController)this.getBeamShooter()).sendBeamLatchOn(beamState.identifyerSig, 0, Long.MIN_VALUE);
				}
			}
			else {
				beamState.from.set((Tuple3f)beamState.relativePos);
				this.transform(beamState);
				this.start.set((Tuple3f)beamState.from);
				this.end.set((Tuple3f)beamState.to);
				this.updateBeam(this.start, this.end, beamState, timer, this.updatedSegments, this.getBeamShooter().getState().getUpdateTime());
				if (!beamState.isAlive()) {
					((Iterator)iterator).remove();
					if (beamState.latchOn && this.getBeamShooter() instanceof SegmentController && this.getBeamShooter().isOnServer()) {
						((SegmentController)this.getBeamShooter()).sendBeamLatchOn(beamState.identifyerSig, 0, Long.MIN_VALUE);
					}
				}
			}
			final BeamState beamState2 = beamState;
			beamState2.timeRunningSinceLastUpdate += delta;
			final BeamState beamState3 = beamState;
			beamState3.timeRunning += delta;
		}
		final Iterator<Segment> iterator2 = this.updatedSegments.iterator();
		while (iterator2.hasNext()) {
			final Segment segment;
			if ((segment = iterator2.next()).getSegmentData() != null) {
				segment.getSegmentData().restructBB(true);
			}
		}
		this.updatedSegments.clear();
		if (this.lastActive != this.isAnyBeamActiveActive() && this.drawer != null) {
			this.drawer.notifyDraw(this, this.isAnyBeamActiveActive());
		}
		this.lastActive = this.isAnyBeamActiveActive();
	}
	
	public void updateBeam(final Vector3f vector3f, final Vector3f vector3f2, final BeamState beamState, final Timer timer, final Collection<Segment> collection, final long n) {
		if (this.owner != null && this.owner.getState() instanceof ClientState && this.getBeamShooter() != null && this.getBeamShooter() instanceof Identifiable && !((GameClientState)this.owner.getState()).getCurrentSectorEntities().containsKey(this.getBeamShooter().getId())) {
			beamState.markDeath = true;
			return;
		}
		final SimpleTransformableSendableObject beamShooter = this.getBeamShooter();
		this.rayCallback.setDebug(false);
		if (EngineSettings.P_PHYSICS_DEBUG_ACTIVE.isOn()) {
			final CollisionWorld.ClosestRayResultCallback testRayCollisionPoint;
			if ((testRayCollisionPoint = beamShooter.getPhysics().testRayCollisionPoint(vector3f, vector3f2, false, beamShooter, null, true, true, true)).hasHit() && testRayCollisionPoint.collisionObject != null) {
				DebugDrawer.points.add(new DebugPoint(testRayCollisionPoint.hitPointWorld, this.getBeamShooter().isOnServer() ? new Vector4f(1.0f, 0.0f, 1.0f, 1.0f) : new Vector4f(0.0f, 1.0f, 0.0f, 0.1f)));
			}
			else {
				DebugDrawer.points.add(new DebugPoint(vector3f2, this.getBeamShooter().isOnServer() ? new Vector4f(0.0f, 0.0f, 1.0f, 1.0f) : new Vector4f(1.0f, 0.0f, 1.0f, 0.1f)));
			}
		}
		beamState.timeSpent += timer.getDelta();
		beamState.hitBlockTime += timer.getDelta();
		final CollisionWorld.ClosestRayResultCallback testRayCollisionPoint2;
		if (!beamShooter.isOnServer() && ClientController.hasGraphics(beamShooter.getState()) && !((GameClientState)beamShooter.getState()).getWorldDrawer().getShards().isEmpty() && (testRayCollisionPoint2 = beamShooter.getPhysics().testRayCollisionPoint(vector3f, vector3f2, false, beamShooter, null, this.ignoreNonPhysical(beamState), false, true)).hasHit() && testRayCollisionPoint2.collisionObject != null && testRayCollisionPoint2.collisionObject instanceof RigidDebrisBody) {
			((RigidDebrisBody)testRayCollisionPoint2.collisionObject).shard.kill();
		}
		if (this.isDamagingMines(beamState) && beamShooter.isOnServer()) {
			this.checkMines(vector3f, vector3f2, beamState, (E)beamShooter);
		}
		final long n2 = n - beamState.fireStart;
		final long n3 = (long)(beamState.getTickRate() * 1000.0f);
		if (beamState.initialTicks > 0.0f || n2 > n3) {
			this.doTicks(beamState, n2, n3, vector3f, vector3f2, timer, n, (E)beamShooter);
			return;
		}
		if (!this.getBeamShooter().isOnServer()) {
			this.doGraphicalUpdate(beamState, n2, n3, vector3f, vector3f2, timer, n, (E)beamShooter);
		}
	}
	
	protected boolean isDamagingMines(final BeamState beamState) {
		return false;
	}
	
	private void updateFromNTLatch(final BeamState beamState, final ManagerContainer.ReceivedBeamLatch receivedBeamLatch) {
		if (receivedBeamLatch.blockPos == Long.MIN_VALUE) {
			beamState.currentHit = null;
			beamState.hitPoint = null;
			beamState.cachedLastSegment = null;
			beamState.p1.reset();
			beamState.p2.reset();
			beamState.cachedLastPos.set((byte)(-1), (byte)(-1), (byte)(-1));
			beamState.firstLatch = Long.MIN_VALUE;
			return;
		}
		final Sendable sendable;
		if ((sendable = (Sendable)this.getBeamShooter().getState().getLocalAndRemoteObjectContainer().getLocalObjects().get(receivedBeamLatch.objId)) instanceof SegmentController) {
			(beamState.currentHit = ((SegmentController)sendable).getSegmentBuffer().getPointUnsave(receivedBeamLatch.blockPos, new SegmentPiece())).getWorldPos(beamState.to, this.getBeamShooter().getSectorId());
			if (beamState.hitPoint == null) {
				beamState.hitPoint = new Vector3f();
			}
			beamState.hitPoint.set((Tuple3f)beamState.to);
			beamState.p1.setByReference(beamState.currentHit);
			beamState.currentHit = beamState.p1;
			beamState.p2.setByReference(beamState.currentHit);
			beamState.cachedLastPos.set(beamState.currentHit.x, beamState.currentHit.y, beamState.currentHit.z);
		}
	}
	
	private void doTicks(final BeamState beamState, final long n, final long n2, final Vector3f vector3f, final Vector3f vector3f2, final Timer timer, final long fireStart, final E e) {
		int n3;
		if (beamState.initialTicks > 0.0f) {
			n3 = (int)beamState.initialTicks;
			beamState.initialTicks = 0.0f;
		}
		else {
			n3 = (int)(n / n2);
			beamState.fireStart = fireStart;
			beamState.ticksToDo -= n3;
		}
		beamState.ticksDone += n3;
		if (beamState.oldPower) {
			final float n4 = beamState.powerConsumptionPerTick + beamState.powerConsumptionPerTick * beamState.powerConsumptionExtraPerTick;
			float n5;
			if ((n5 = n3 * n4) > 0.0f) {
				while (n3 > 0 && !beamState.reloadCallback.canConsumePower(n5)) {
					n5 = --n3 * n4;
				}
				if (n3 <= 0) {
					beamState.markDeath = true;
					return;
				}
				beamState.totalConsumedPower += n5;
				if (!beamState.reloadCallback.consumePower(n5)) {
					beamState.markDeath = true;
					return;
				}
			}
		}
		boolean b = false;
		if (beamState.latchOn && beamState.currentHit != null) {
			beamState.currentHit.getWorldPos(beamState.to, this.getBeamShooter().getSectorId());
			vector3f2.set((Tuple3f)beamState.to);
			final short type = beamState.currentHit.getType();
			if (!beamState.currentHit.isValid() || beamState.currentHit.isDead()) {
				this.checkNextLatch(type, beamState, vector3f, vector3f2);
			}
			if (beamState.checkLatchConnection) {
				this.testRayOnOvelappingSectors(vector3f, vector3f2, beamState, e);
				if (beamState.currentHit != null && this.rayCallback.getSegment() != null) {
					if (!beamState.currentHit.equalsSegmentPos(this.rayCallback.getSegment(), this.rayCallback.getCubePos())) {
						beamState.currentHit.setByReference(this.rayCallback.getSegment(), this.rayCallback.getCubePos());
						beamState.firstLatch = beamState.currentHit.getAbsoluteIndex();
						beamState.currentHit.getWorldPos(beamState.to, this.getBeamShooter().getSectorId());
						beamState.hitPoint.set((Tuple3f)beamState.to);
						beamState.p1.setByReference(beamState.currentHit);
						beamState.currentHit = beamState.p1;
						beamState.p2.setByReference(beamState.currentHit);
						beamState.cachedLastPos.set(beamState.currentHit.x, beamState.currentHit.y, beamState.currentHit.z);
						beamState.hitPoint.set((Tuple3f)beamState.to);
						vector3f2.set((Tuple3f)beamState.to);
					}
					beamState.hitPoint.set((Tuple3f)beamState.to);
					beamState.hitSectorId = this.getBeamShooter().getSectorId();
					beamState.hitNormalWorld.set((Tuple3f)this.rayCallback.hitNormalWorld);
					beamState.hitNormalRelative.set((Tuple3f)this.rayCallback.hitNormalWorld);
					this.rayCallback.getSegment().getSegmentController().getWorldTransformInverse().basis.transform((Tuple3f)beamState.hitNormalRelative);
					this.checkNextLatch(type, beamState, vector3f, vector3f2);
				}
			}
			else if (beamState.currentHit != null && Vector3fTools.diffLength(beamState.from, beamState.to) > beamState.beamLength) {
				System.err.println("lost beam due to being too far from latched on target");
				beamState.currentHit = null;
				beamState.hitPoint = null;
				beamState.hitSectorId = -1;
				beamState.cachedLastSegment = null;
				beamState.p1.reset();
				beamState.p2.reset();
				beamState.cachedLastPos.set((byte)(-1), (byte)(-1), (byte)(-1));
				beamState.firstLatch = Long.MIN_VALUE;
			}
			if (beamState.currentHit != null) {
				this.onCubeStructureHit(beamState, n3, vector3f, vector3f2, timer, fireStart, e, beamState.currentHit);
				b = true;
			}
			else {
				b = false;
			}
		}
		if (!b) {
			this.testRayOnOvelappingSectors(vector3f, vector3f2, beamState, e);
			if (this.rayCallback.hasHit()) {
				beamState.hitSectorId = this.getBeamShooter().getSectorId();
				beamState.hitPointCache.set((Tuple3f)this.rayCallback.hitPointWorld);
				beamState.hitNormalWorld.set((Tuple3f)this.rayCallback.hitNormalWorld);
				beamState.hitNormalRelative.set((Tuple3f)this.rayCallback.hitNormalWorld);
				if (this.rayCallback.getSegment() != null && this.rayCallback.getSegment().getSegmentController().getWorldTransformInverse() != null) {
					this.rayCallback.getSegment().getSegmentController().getWorldTransformInverse().basis.transform((Tuple3f)beamState.hitNormalRelative);
				}
				beamState.hitPoint = beamState.hitPointCache;
				if (this.rayCallback.getSegment() != null) {
					final CubeRayCastResult rayCallback;
					final SegmentController segmentController = (rayCallback = this.rayCallback).getSegment().getSegmentController();
					if (this.getBeamShooter().isOnServer()) {
						segmentController.calcWorldTransformRelative(this.getBeamShooter().getSectorId(), ((GameServerState)segmentController.getState()).getUniverse().getSector(this.getBeamShooter().getSectorId()).pos);
						beamState.initalRelativeTranform.set(this.getBeamShooter().getWorldTransformInverse());
						beamState.initalRelativeTranform.mul(segmentController.getClientTransform());
					}
					beamState.p1.setByReference(rayCallback.getSegment(), rayCallback.getCubePos());
					beamState.currentHit = beamState.p1;
					beamState.p2.setByReference(rayCallback.getSegment(), rayCallback.getCubePos());
					beamState.segmentHit = beamState.p2;
					beamState.cachedLastPos.set(rayCallback.getCubePos());
					if (beamState.currentHit != null) {
						beamState.currentHit.getWorldPos(beamState.to, this.getBeamShooter().getSectorId());
					}
					final short type2 = beamState.currentHit.getType();
					b = this.onCubeStructureHit(beamState, n3, vector3f, vector3f2, timer, fireStart, e, beamState.currentHit);
					beamState.firstLatch = beamState.currentHit.getAbsoluteIndex();
					if (beamState.latchOn) {
						this.checkNextLatch(type2, beamState, vector3f, vector3f2);
					}
				}
				else if (this.rayCallback instanceof CubeRayCastResult && this.rayCallback.getSegment() == null) {
					b = this.onBeamHitNonCube(beamState, n3, this.owner, vector3f, vector3f2, this.rayCallback, timer, this.updatedSegments);
					if (!this.getBeamShooter().isOnServer() && beamState.hitPoint != null) {
						beamState.hitPoint.sub((Tuple3f)vector3f);
						this.dirTmp.set((Tuple3f)vector3f2);
						this.dirTmp.sub((Tuple3f)vector3f);
						this.dirTmp.normalize();
						this.dirTmp.scale(beamState.hitPoint.length());
						this.dirTmp.add((Tuple3f)vector3f);
						beamState.hitPoint.set((Tuple3f)this.dirTmp);
					}
				}
			}
			else {
				beamState.reset();
			}
		}
		if (!b) {
			beamState.reset();
		}
	}
	
	private boolean checkNextLatch(final short n, final BeamState beamState, final Vector3f vector3f, final Vector3f vector3f2) {
		beamState.currentHit.refresh();
		if (!beamState.currentHit.isValid() || beamState.currentHit.isDead()) {
			beamState.currentHit = this.getBeamLatchTransitionInterface().selectNextToLatch(beamState, n, beamState.firstLatch, beamState.currentHit.getAbsoluteIndex(), vector3f, vector3f2, (AbstractBeamHandler<SimpleTransformableSendableObject>)this, beamState.currentHit.getSegmentController());
			if (beamState.currentHit != null) {
				beamState.currentHit.getWorldPos(beamState.to, this.getBeamShooter().getSectorId());
				beamState.hitSectorId = this.getBeamShooter().getSectorId();
				beamState.hitPoint.set((Tuple3f)beamState.to);
				beamState.p1.setByReference(beamState.currentHit);
				beamState.currentHit = beamState.p1;
				beamState.p2.setByReference(beamState.currentHit);
				beamState.cachedLastPos.set(beamState.currentHit.x, beamState.currentHit.y, beamState.currentHit.z);
				beamState.hitPoint.set((Tuple3f)beamState.to);
				vector3f2.set((Tuple3f)beamState.to);
				if (this.getBeamShooter() instanceof SegmentController && this.getBeamShooter().isOnServer()) {
					((SegmentController)this.getBeamShooter()).sendBeamLatchOn(beamState.identifyerSig, beamState.currentHit.getSegmentController().getId(), beamState.currentHit.getAbsoluteIndex());
				}
				return true;
			}
			beamState.reset();
		}
		return false;
	}
	
	private void onCubeStructureHitSingle(final BeamState beamState, final int n, final Vector3f vector3f, final Vector3f vector3f2, final Timer timer, final long n2, final E e, final SegmentPiece segmentPiece) {
		final SegmentController segmentController = segmentPiece.getSegmentController();
		final SimpleTransformableSendableObject beamShooter;
		if (this.owner != null && this.owner.getHandler() != null && (beamShooter = this.owner.getHandler().getBeamShooter()) != null && beamShooter instanceof SegmentController) {
			final SegmentController segmentController2;
			if ((segmentController2 = (SegmentController)beamShooter).railController.isChildDock(segmentController)) {
				if (beamState.railChild < 1.0 && !segmentController2.isOnServer()) {
					segmentController2.popupOwnClientMessage(StringTools.format(Lng.ORG_SCHEMA_GAME_COMMON_DATA_ELEMENT_BEAM_ABSTRACTBEAMHANDLER_1, StringTools.formatPointZero(beamState.railChild * 100.0)), 3);
				}
				beamState.setPower((float)(beamState.getPower() * beamState.railChild));
			}
			else if (segmentController2.railController.isParentDock(segmentController)) {
				if (beamState.railParent < 1.0 && !segmentController2.isOnServer()) {
					segmentController2.popupOwnClientMessage(StringTools.format(Lng.ORG_SCHEMA_GAME_COMMON_DATA_ELEMENT_BEAM_ABSTRACTBEAMHANDLER_0, StringTools.formatPointZero(beamState.railParent * 100.0)), 3);
				}
				beamState.setPower((float)(beamState.getPower() * beamState.railParent));
			}
		}
		this.onBeamHit(beamState, n, this.owner, segmentPiece, vector3f, vector3f2, timer, this.updatedSegments);
		if (!beamState.latchOn && !this.getBeamShooter().isOnServer() && beamState.hitPoint != null) {
			beamState.hitPoint.sub((Tuple3f)vector3f);
			this.dirTmp.set((Tuple3f)vector3f2);
			this.dirTmp.sub((Tuple3f)vector3f);
			this.dirTmp.normalize();
			this.dirTmp.scale(beamState.hitPoint.length());
			this.dirTmp.add((Tuple3f)vector3f);
			beamState.hitPoint.set((Tuple3f)this.dirTmp);
		}
	}
	
	private boolean onCubeStructureHit(final BeamState beamState, final int n, final Vector3f vector3f, final Vector3f vector3f2, final Timer timer, final long n2, final E e, final SegmentPiece segmentPiece) {
		final SegmentController segmentController = segmentPiece.getSegmentController();
		beamState.cachedLastSegment = segmentController;
		this.cannotHitReason[0] = "";
		if (this.canhit(beamState, segmentController, this.cannotHitReason, segmentPiece.getAbsolutePos(this.tmp))) {
			if (beamState.penetrating) {
				if (segmentController.getSectorId() != this.owner.getSectorId()) {
					final Vector3i sector;
					if ((sector = segmentController.getSector(new Vector3i())) != null) {
						this.inFrom.origin.set((Tuple3f)vector3f);
						this.inTo.origin.set((Tuple3f)vector3f2);
						SimpleTransformableSendableObject.calcWorldTransformRelative(segmentController.getSectorId(), sector, e.getSectorId(), this.inFrom, e.getState(), e.isOnServer(), this.outFrom, this.v);
						SimpleTransformableSendableObject.calcWorldTransformRelative(segmentController.getSectorId(), sector, e.getSectorId(), this.inTo, e.getState(), e.isOnServer(), this.outTo, this.v);
					}
				}
				else {
					this.outFrom.origin.set((Tuple3f)vector3f);
					this.outTo.origin.set((Tuple3f)vector3f2);
				}
				if (this.blockRecorder == null) {
					this.blockRecorder = (Int2ObjectOpenHashMap<BlockRecorder>)new Int2ObjectOpenHashMap();
				}
				this.testRay(vector3f, vector3f2, beamState, segmentController, this.blockRecorder, 100000, (ModifiedDynamicsWorld)segmentController.getPhysics().getDynamicsWorld());
				if (this.rayCallback.hasHit()) {
					final BlockRecorder blockRecorder;
					if ((blockRecorder = (BlockRecorder)this.blockRecorder.get(segmentController.getId())) != null && blockRecorder.size() > 0) {
						final int size = blockRecorder.size();
						final float power = beamState.getPower();
						beamState.setPower(power / size);
						for (int i = 0; i < size; ++i) {
							final int int1 = blockRecorder.blockLocalIndices.getInt(i);
							this.tmpHit.setByReference(((SegmentData)blockRecorder.datas.get(i)).getSegment(), SegmentData.getPosXFromIndex(int1), SegmentData.getPosYFromIndex(int1), SegmentData.getPosZFromIndex(int1));
							if ((int)beamState.getPower() == 0) {
								break;
							}
							this.onCubeStructureHitSingle(beamState, n, vector3f, vector3f2, timer, n2, e, this.tmpHit);
						}
						beamState.setPower(power);
					}
					else {
						System.err.println("WARNING: No block Recorder when there should be one for id " + segmentController.getId() + ": size: " + this.blockRecorder.size() + "; " + this.blockRecorder);
					}
					this.tmpHit.reset();
				}
				final Iterator iterator = this.blockRecorder.values().iterator();
				while (iterator.hasNext()) {
					((BlockRecorder)iterator.next()).free();
				}
				this.blockRecorder.clear();
			}
			else {
				this.onCubeStructureHitSingle(beamState, n, vector3f, vector3f2, timer, n2, e, segmentPiece);
			}
			return true;
		}
		if (!segmentController.isOnServer()) {
			if (((GameClientState)this.getBeamShooter().getState()).getCurrentPlayerObject() == this.getBeamShooter()) {
				((GameClientController)segmentController.getState().getController()).popupInfoTextMessage(StringTools.format(Lng.ORG_SCHEMA_GAME_COMMON_DATA_ELEMENT_BEAM_ABSTRACTBEAMHANDLER_2, segmentController.toNiceString(), this.cannotHitReason[0]), "BHITTXT", 0.0f);
			}
			else {
				System.err.println(((GameClientState)this.getBeamShooter().getState()).getCurrentPlayerObject() + "; " + this.getBeamShooter());
			}
		}
		return false;
	}
	
	private void doGraphicalUpdate(final BeamState beamState, final long n, final long n2, final Vector3f vector3f, final Vector3f vector3f2, final Timer timer, final long n3, final E e) {
		if (beamState.latchOn && beamState.currentHit != null) {
			beamState.currentHit.getWorldPos(beamState.to, this.getBeamShooter().getSectorId());
			beamState.hitPoint.set((Tuple3f)beamState.to);
			beamState.hitSectorId = this.getBeamShooter().getSectorId();
			return;
		}
		if (beamState.penetrating && beamState.hitPoint != null) {
			beamState.hitPoint.set((Tuple3f)vector3f2);
			beamState.hitSectorId = this.getBeamShooter().getSectorId();
			return;
		}
		if (n3 - beamState.lastCheck > 50L) {
			this.rayCallback.setCubesOnly(this.isHitsCubesOnly());
			this.rayCallback.closestHitFraction = 1.0f;
			this.rayCallback.rayFromWorld.set((Tuple3f)vector3f);
			this.rayCallback.rayToWorld.set((Tuple3f)vector3f2);
			this.rayCallback.collisionObject = null;
			this.rayCallback.setSegment(null);
			this.rayCallback.setDamageTest(false);
			this.rayCallback.setIgnoereNotPhysical(false);
			this.rayCallback.setIgnoreDebris(true);
			this.rayCallback.setZeroHpPhysical(true);
			this.rayCallback.setHasCollidingBlockFilter(false);
			this.rayCallback.setCollidingBlocks(null);
			this.rayCallback.setSimpleRayTest(false);
			((ModifiedDynamicsWorld)e.getPhysics().getDynamicsWorld()).rayTest(vector3f, vector3f2, (CollisionWorld.RayResultCallback)this.rayCallback);
			if (this.rayCallback.hasHit()) {
				beamState.hitPoint = new Vector3f(this.rayCallback.hitPointWorld);
				beamState.hitSectorId = this.getBeamShooter().getSectorId();
				if (this.rayCallback instanceof CubeRayCastResult && this.rayCallback.getSegment() != null) {
					final CubeRayCastResult rayCallback = this.rayCallback;
					beamState.currentHit = new SegmentPiece(rayCallback.getSegment(), rayCallback.getCubePos());
					beamState.segmentHit = new SegmentPiece(rayCallback.getSegment(), rayCallback.getCubePos());
				}
			}
			else {
				beamState.currentHit = null;
				beamState.hitPoint = null;
				beamState.hitSectorId = -1;
				beamState.cachedLastSegment = null;
				beamState.cachedLastPos.set((byte)(-1), (byte)(-1), (byte)(-1));
				beamState.segmentHit = null;
				beamState.hitOneSegment = 0.0f;
				beamState.hitBlockTime = 0.0f;
				beamState.timeSpent = 0.0f;
			}
			beamState.lastCheck = timer.currentTime;
			return;
		}
		if (beamState.hitPoint != null) {
			this.tmpVal.sub((Tuple3f)beamState.hitPoint, (Tuple3f)vector3f);
			this.dirTmp.set((Tuple3f)vector3f2);
			this.dirTmp.sub((Tuple3f)vector3f);
			this.dirTmp.normalize();
			this.dirTmp.scale(this.tmpVal.length());
			this.dirTmp.add((Tuple3f)vector3f);
			beamState.hitPoint.interpolate((Tuple3f)this.dirTmp, timer.getDelta());
		}
	}
	
	private void checkMines(final Vector3f from, final Vector3f to, final BeamState con, final E controller) {
		this.btMines.from = from;
		this.btMines.to = to;
		this.btMines.con = con;
		this.btMines.controller = controller;
		this.btMines.init();
		this.btMines.ownSectorPos.set(((GameServerState)controller.getState()).getUniverse().getSector(controller.getSectorId()).pos);
		final float sectorSize = ((GameStateInterface)controller.getState()).getSectorSize();
		RayCubeGridSolver.sectorInv = 1.0f / sectorSize;
		RayCubeGridSolver.sectorHalf = sectorSize * 0.5f;
		this.raySolver.initializeSectorGranularity(from, to, TransformTools.ident);
		this.raySolver.traverseSegmentsOnRay(this.btMines);
	}
	
	CubeRayCastResult testRayOnOvelappingSectors(final Vector3f from, final Vector3f to, final BeamState con, final E controller) {
		this.testRay(from, to, con, (ModifiedDynamicsWorld)controller.getPhysics().getDynamicsWorld());
		if (!this.rayCallback.hasHit() && controller.isOnServer()) {
			this.bt.from = from;
			this.bt.to = to;
			this.bt.con = con;
			this.bt.controller = controller;
			this.bt.ownSectorPos.set(((GameServerState)controller.getState()).getUniverse().getSector(controller.getSectorId()).pos);
			final float sectorSize = ((GameStateInterface)controller.getState()).getSectorSize();
			RayCubeGridSolver.sectorInv = 1.0f / sectorSize;
			RayCubeGridSolver.sectorHalf = sectorSize * 0.5f;
			this.raySolver.initializeSectorGranularity(from, to, TransformTools.ident);
			this.raySolver.traverseSegmentsOnRay(this.bt);
		}
		return this.rayCallback;
	}
	
	private boolean testRay(final Vector3f vector3f, final Vector3f vector3f2, final BeamState beamState, final ModifiedDynamicsWorld modifiedDynamicsWorld) {
		return this.testRay(vector3f, vector3f2, beamState, null, null, 0, modifiedDynamicsWorld);
	}
	
	private boolean testRay(final Vector3f vector3f, final Vector3f vector3f2, final BeamState beamState, final SegmentController segmentController, final Int2ObjectOpenHashMap<BlockRecorder> int2ObjectOpenHashMap, final int n, final ModifiedDynamicsWorld modifiedDynamicsWorld) {
		this.rayCallback.setCubesOnly(this.isHitsCubesOnly());
		this.rayCallback.closestHitFraction = 1.0f;
		this.rayCallback.rayFromWorld.set((Tuple3f)vector3f);
		this.rayCallback.rayToWorld.set((Tuple3f)vector3f2);
		this.rayCallback.collisionObject = null;
		this.rayCallback.setSegment(null);
		this.rayCallback.setDamageTest(this.affectsTargetBlock());
		this.rayCallback.setIgnoereNotPhysical(this.ignoreNonPhysical(beamState));
		this.rayCallback.setIgnInterface(this);
		this.rayCallback.setIgnoreDebris(true);
		this.rayCallback.setZeroHpPhysical(this.isConsiderZeroHpPhysical());
		this.rayCallback.setHasCollidingBlockFilter(false);
		this.rayCallback.setCollidingBlocks(null);
		this.rayCallback.setSimpleRayTest(true);
		this.rayCallback.setHitNonblockCallback(this);
		this.rayCallback.power = beamState.getPower();
		if (segmentController != null) {
			this.rayCallback.setFilter(segmentController);
		}
		else {
			this.rayCallback.setFilter(new SegmentController[0]);
		}
		if (int2ObjectOpenHashMap != null) {
			this.rayCallback.setRecordAllBlocks(true);
			this.rayCallback.setRecordedBlocks(int2ObjectOpenHashMap, n);
		}
		else {
			this.rayCallback.setRecordAllBlocks(false);
			this.rayCallback.setRecordedBlocks(null, 0);
		}
		modifiedDynamicsWorld.rayTest(vector3f, vector3f2, (CollisionWorld.RayResultCallback)this.rayCallback);
		beamState.armorValue.reset();
		if (this.rayCallback.hasHit() && this.rayCallback.getSegment() != null && !beamState.ignoreArmor) {
			System.err.println("#XXX: hitPointWorld: " + this.rayCallback.hitPointWorld.toString());
			beamState.armorValue.set(this.retrieveArmorInfo(this.rayCallback.getSegment().getSegmentController(), vector3f, vector3f2));
		}
		return this.rayCallback.hasHit();
	}

	private ArmorValue retrieveArmorInfo(final SegmentController segmentController, final Vector3f vector3f, final Vector3f vector3f2) {
		System.err.println("#XXX: retrieveArmorInfo");
		try {throw new Exception();}
		catch(Exception e) {e.printStackTrace();}
		this.rayCallbackTraverse.closestHitFraction = 1.0f;
		this.rayCallbackTraverse.collisionObject = null;
		this.rayCallbackTraverse.setSegment(null);
		this.rayCallbackTraverse.rayFromWorld.set((Tuple3f)vector3f);
		this.rayCallbackTraverse.rayToWorld.set((Tuple3f)vector3f2);
		this.rayCallbackTraverse.setFilter(segmentController);
		this.rayCallbackTraverse.setOwner(null);
		this.rayCallbackTraverse.setIgnoereNotPhysical(false);
		this.rayCallbackTraverse.setIgnoreDebris(false);
		this.rayCallbackTraverse.setRecordAllBlocks(false);
		this.rayCallbackTraverse.setZeroHpPhysical(false);
		this.rayCallbackTraverse.setDamageTest(true);
		this.rayCallbackTraverse.setCheckStabilizerPaths(false);
		this.rayCallbackTraverse.setSimpleRayTest(true);
		this.armorValue.reset();
		this.pt.armorValue = this.armorValue;
		((ModifiedDynamicsWorld)segmentController.getPhysics().getDynamicsWorld()).rayTest(vector3f, vector3f2, (CollisionWorld.RayResultCallback)this.rayCallbackTraverse);
		if (this.armorValue.typesHit.size() > 0) {
			this.armorValue.calculate();
		}
		this.rayCallbackTraverse.collisionObject = null;
		this.rayCallbackTraverse.setSegment(null);
		this.rayCallbackTraverse.setFilter(new SegmentController[0]);
		System.err.println("#XXX: armorValue: " + this.armorValue.totalArmorValue);
		return this.armorValue;
	}
	
	@Override
	public boolean onHit(final CollisionObject collisionObject, final float n) {
		if (this.canHit(collisionObject) && collisionObject.getUserPointer() instanceof StabilizerPath) {
			((StabilizerPath)collisionObject.getUserPointer()).onHit(this.owner, n);
			return false;
		}
		return true;
	}
	
	protected boolean canHit(final CollisionObject collisionObject) {
		return true;
	}
	
	public boolean ignoreBlockType(final short n) {
		return false;
	}
	
	protected boolean isHitsCubesOnly() {
		return false;
	}
	
	public boolean affectsTargetBlock() {
		return true;
	}
	
	protected boolean isConsiderZeroHpPhysical() {
		return true;
	}
	
	protected boolean ignoreNonPhysical(final BeamState beamState) {
		return true;
	}
	
	public boolean drawBlockSalvage() {
		return false;
	}
	
	public final Vector4f getColor(final BeamState beamState) {
		if (this.owner instanceof ColorBeamInterface && ((ColorBeamInterface)this.owner).hasCustomColor()) {
			return ((ColorBeamInterface)this.owner).getColor();
		}
		return this.getDefaultColor(beamState);
	}
	
	protected abstract Vector4f getDefaultColor(final BeamState p0);
	
	@Override
	public boolean ignoreBlock(final short n) {
		return false;
	}
	
	public void sendClientMessage(final String s, final int n) {
		if (this.getBeamShooter() != null) {
			this.getBeamShooter().sendClientMessage(s, n);
		}
	}
	
	public void sendServerMessage(final Object[] array, final int n) {
		if (this.getBeamShooter() != null) {
			this.getBeamShooter().sendServerMessage(array, n);
		}
	}
	
	public float getDamageGivenMultiplier() {
		if (this.getBeamShooter() != null) {
			return this.getBeamShooter().getDamageGivenMultiplier();
		}
		return 1.0f;
	}
	
	public abstract boolean isUsingOldPower();
	
	public abstract BeamLatchTransitionInterface getBeamLatchTransitionInterface();
	
	public boolean handleBeamLatch(final ManagerContainer.ReceivedBeamLatch receivedBeamLatch) {
		final BeamState beamState;
		if ((beamState = (BeamState)this.beamStates.get(receivedBeamLatch.beamId)) != null) {
			this.updateFromNTLatch(beamState, receivedBeamLatch);
			return true;
		}
		return false;
	}
	
	public void onArmorBlockKilled(final BeamState beamState, final float n) {
	}
	
	static {
		color1Blue = new Vector4f(0.0f, 0.0f, 1.0f, 1.0f);
		color1Green = new Vector4f(0.0f, 1.0f, 0.0f, 1.0f);
		color1Red = new Vector4f(0.7f, 0.0f, 0.0f, 1.0f);
		color1Yellow = new Vector4f(1.0f, 1.0f, 0.0f, 1.0f);
		color1White = new Vector4f(0.7f, 0.7f, 0.7f, 1.0f);
		color1Purple = new Vector4f(1.0f, 0.3f, 1.0f, 1.0f);
	}
	
	class BigTrav implements SegmentTraversalInterface<BigTrav>
	{
		public Vector3f from;
		public Vector3f to;
		public BeamState con;
		public E controller;
		private Vector3i secPosTmp;
		private Vector3i ownSectorPos;
		
		public BigTrav() {
			this.secPosTmp = new Vector3i();
			this.ownSectorPos = new Vector3i();
			AbstractBeamHandler.this.inFrom.setIdentity();
			AbstractBeamHandler.this.inTo.setIdentity();
		}
		
		@Override
		public boolean handle(final int a, final int a2, final int a3, final RayTraceGridTraverser rayTraceGridTraverser) {
			assert this.controller.isOnServer();
			if (Math.abs(a) >= 2 || Math.abs(a2) >= 2 || Math.abs(a3) >= 2 || AbstractBeamHandler.this.rayCallback.hasHit()) {
				return false;
			}
			if (a == 0 && a2 == 0 && a3 == 0) {
				return true;
			}
			this.secPosTmp.set(this.ownSectorPos.x + a, this.ownSectorPos.y + a2, this.ownSectorPos.z + a3);
			final Sector sectorWithoutLoading;
			if ((sectorWithoutLoading = ((GameServerState)this.controller.getState()).getUniverse().getSectorWithoutLoading(this.secPosTmp)) != null) {
				AbstractBeamHandler.this.inFrom.origin.set((Tuple3f)this.from);
				AbstractBeamHandler.this.inTo.origin.set((Tuple3f)this.to);
				SimpleTransformableSendableObject.calcWorldTransformRelative(sectorWithoutLoading.getId(), sectorWithoutLoading.pos, this.controller.getSectorId(), AbstractBeamHandler.this.inFrom, this.controller.getState(), true, AbstractBeamHandler.this.outFrom, AbstractBeamHandler.this.v);
				SimpleTransformableSendableObject.calcWorldTransformRelative(sectorWithoutLoading.getId(), sectorWithoutLoading.pos, this.controller.getSectorId(), AbstractBeamHandler.this.inTo, this.controller.getState(), true, AbstractBeamHandler.this.outTo, AbstractBeamHandler.this.v);
				AbstractBeamHandler.this.testRay(AbstractBeamHandler.this.outFrom.origin, AbstractBeamHandler.this.outTo.origin, this.con, (ModifiedDynamicsWorld)sectorWithoutLoading.getPhysics().getDynamicsWorld());
				if (AbstractBeamHandler.this.rayCallback.hasHit()) {
					final Sector sectorWithoutLoading2 = ((GameServerState)this.controller.getState()).getUniverse().getSectorWithoutLoading(this.ownSectorPos);
					AbstractBeamHandler.this.inRes.setIdentity();
					AbstractBeamHandler.this.inRes.origin.set((Tuple3f)AbstractBeamHandler.this.rayCallback.hitPointWorld);
					SimpleTransformableSendableObject.calcWorldTransformRelative(sectorWithoutLoading2.getId(), sectorWithoutLoading2.pos, sectorWithoutLoading.getSectorId(), AbstractBeamHandler.this.inRes, this.controller.getState(), true, AbstractBeamHandler.this.outRes, AbstractBeamHandler.this.v);
					AbstractBeamHandler.this.rayCallback.hitPointWorld.set((Tuple3f)AbstractBeamHandler.this.outRes.origin);
					return false;
				}
			}
			return true;
		}
		
		@Override
		public BigTrav getContextObj() {
			return this;
		}
	}
	
	class BigTravMines implements SegmentTraversalInterface<BigTravMines>
	{
		public Vector3f from;
		public Vector3f to;
		Transform inFrom;
		Transform inTo;
		Transform outFrom;
		Transform outTo;
		public BeamState con;
		public E controller;
		private Vector3i ownSectorPos;
		private TransformaleObjectTmpVars v;
		private MineController mineController;
		private GameServerState state;
		private Vector3i secTmp;
		
		public void init() {
			this.state = (GameServerState)AbstractBeamHandler.this.getBeamShooter().getState();
			this.mineController = this.state.getController().getMineController();
		}
		
		public BigTravMines() {
			this.inFrom = new Transform();
			this.inTo = new Transform();
			this.outFrom = new Transform();
			this.outTo = new Transform();
			this.ownSectorPos = new Vector3i();
			this.v = new TransformaleObjectTmpVars();
			this.secTmp = new Vector3i();
			this.inFrom.setIdentity();
			this.inTo.setIdentity();
		}
		
		@Override
		public boolean handle(final int n, final int n2, final int n3, final RayTraceGridTraverser rayTraceGridTraverser) {
			this.secTmp.set(n, n2, n3);
			this.secTmp.add(this.ownSectorPos);
			final Sector sectorWithoutLoading;
			if ((sectorWithoutLoading = this.state.getUniverse().getSectorWithoutLoading(this.secTmp)) != null) {
				this.mineController.handleHit(sectorWithoutLoading.getId(), this.from, this.to);
			}
			return true;
		}
		
		@Override
		public BigTravMines getContextObj() {
			return this;
		}
	}
}
