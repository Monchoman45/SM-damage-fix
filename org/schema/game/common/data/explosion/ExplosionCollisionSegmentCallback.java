// 
// Decompiled by Procyon v0.5.36
// 

package org.schema.game.common.data.explosion;

import org.schema.game.common.data.player.AbstractOwnerState;
import java.util.Arrays;
import org.schema.game.common.data.element.ElementInformation;
import org.schema.game.common.controller.damage.effects.InterEffectHandler;
import org.schema.game.common.controller.damage.HitReceiverType;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.game.common.data.world.SectorNotFoundException;
import java.util.Iterator;
import org.schema.game.common.controller.elements.ShieldContainerInterface;
import org.schema.game.common.data.ManagedSegmentController;
import org.schema.game.common.data.blockeffects.config.StatusEffectType;
import org.schema.game.common.data.blockeffects.config.ConfigManagerInterface;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;
import org.schema.schine.network.objects.Sendable;
import java.util.List;
import javax.vecmath.Tuple3f;
import org.schema.schine.graphicsengine.core.GlUtil;
import com.bulletphysics.linearmath.Transform;
import org.schema.game.common.data.player.AbstractCharacter;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.schema.game.common.controller.elements.VoidElementManager;
import org.schema.game.common.controller.damage.HitType;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import org.schema.game.common.controller.damage.DamageDealerType;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import org.schema.game.common.controller.damage.effects.InterEffectSet;
import org.schema.game.common.controller.damage.effects.InterEffectContainer;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.schema.game.common.controller.SegmentController;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.schema.game.common.data.world.Segment;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import javax.vecmath.Vector3f;

public class ExplosionCollisionSegmentCallback implements ExplosionDamageInterface
{
	public ArmorMode armorMode;
	public final Vector3f centerOfExplosion;
	public final ExplosionDataHandler explosionDataStructure;
	public final IntOpenHashSet sentDamage;
	public final ObjectOpenHashSet<Segment> hitSegments;
	public final Object2ObjectOpenHashMap<SegmentController, Long2ObjectOpenHashMap<ByteArrayList>> hitSendSegments;
	public final Int2IntOpenHashMap railMap;
	public final Int2IntOpenHashMap railRootMap;
	public final Int2ObjectOpenHashMap<ExplosionCubeConvexBlockCallback> shieldHitPosMap;
	public final Int2ObjectOpenHashMap<ExplosionCubeConvexBlockCallback> hitPosMap;
	public final Int2DoubleOpenHashMap shieldMap;
	public final Int2LongOpenHashMap shieldLocalMap;
	public final Int2DoubleOpenHashMap shieldMapPercent;
	public final Int2FloatOpenHashMap appliedDamageMap;
	public final Int2DoubleOpenHashMap shieldMapBef;
	public final Int2ObjectOpenHashMap<InterEffectContainer> interEffectMap;
	public final Int2FloatOpenHashMap environmentalProtectMultMap;
	public final Int2FloatOpenHashMap extraDamageTakenMap;
	private final InterEffectSet defenseTmp;
	private final ShortOpenHashSet closedList;
	private final ShortOpenHashSet cpy;
	public float explosionRadius;
	public DamageDealerType damageType;
	public long weaponId;
	public float shieldDamageBonus;
	public float[] explosionDamageBuffer;
	public ExplosionCubeConvexBlockCallback[] callbackCache;
	public int cubeCallbackPointer;
	public boolean ignoreShieldsGlobal;
	public ObjectOpenHashSet<Segment> ownLockedSegments;
	LongArrayList collidingSegments;
	private Long2IntOpenHashMap explosionBlockMapA;
	private Long2IntOpenHashMap explosionBlockMapB;
	private Long2IntOpenHashMap explosionBlockMapC;
	private Long2IntOpenHashMap explosionBlockMapD;
	private Vector3f tmp;
	public final IntOpenHashSet entitiesToIgnoreShieldsOn;
	public boolean useLocalShields;
	private boolean ignoreShield;
	public HitType hitType;
	public InterEffectSet attack;
	
	public ExplosionCollisionSegmentCallback(final ExplosionDataHandler explosionDataStructure) {
		this.armorMode = ArmorMode.PREEMTIVE;
		this.centerOfExplosion = new Vector3f();
		this.sentDamage = new IntOpenHashSet();
		this.hitSegments = (ObjectOpenHashSet<Segment>)new ObjectOpenHashSet();
		this.hitSendSegments = (Object2ObjectOpenHashMap<SegmentController, Long2ObjectOpenHashMap<ByteArrayList>>)new Object2ObjectOpenHashMap();
		this.railMap = new Int2IntOpenHashMap();
		this.railRootMap = new Int2IntOpenHashMap();
		this.shieldHitPosMap = (Int2ObjectOpenHashMap<ExplosionCubeConvexBlockCallback>)new Int2ObjectOpenHashMap();
		this.hitPosMap = (Int2ObjectOpenHashMap<ExplosionCubeConvexBlockCallback>)new Int2ObjectOpenHashMap();
		this.shieldMap = new Int2DoubleOpenHashMap();
		this.shieldLocalMap = new Int2LongOpenHashMap();
		this.shieldMapPercent = new Int2DoubleOpenHashMap();
		this.appliedDamageMap = new Int2FloatOpenHashMap();
		this.shieldMapBef = new Int2DoubleOpenHashMap();
		this.interEffectMap = (Int2ObjectOpenHashMap<InterEffectContainer>)new Int2ObjectOpenHashMap();
		this.environmentalProtectMultMap = new Int2FloatOpenHashMap();
		this.extraDamageTakenMap = new Int2FloatOpenHashMap();
		this.defenseTmp = new InterEffectSet();
		this.shieldDamageBonus = VoidElementManager.EXPLOSION_SHIELD_DAMAGE_BONUS;
		this.callbackCache = new ExplosionCubeConvexBlockCallback[4096];
		this.collidingSegments = new LongArrayList();
		this.explosionBlockMapA = new Long2IntOpenHashMap(4096);
		this.explosionBlockMapB = new Long2IntOpenHashMap(4096);
		this.explosionBlockMapC = new Long2IntOpenHashMap(4096);
		this.explosionBlockMapD = new Long2IntOpenHashMap(4096);
		this.tmp = new Vector3f();
		this.entitiesToIgnoreShieldsOn = new IntOpenHashSet();
		this.explosionBlockMapA.defaultReturnValue(-1);
		this.explosionBlockMapB.defaultReturnValue(-1);
		this.explosionBlockMapC.defaultReturnValue(-1);
		this.explosionBlockMapD.defaultReturnValue(-1);
		this.extraDamageTakenMap.defaultReturnValue(1.0f);
		this.environmentalProtectMultMap.defaultReturnValue(1.0f);
		this.explosionDataStructure = explosionDataStructure;
		this.explosionDamageBuffer = new float[explosionDataStructure.bigLength * explosionDataStructure.bigLength * explosionDataStructure.bigLength];
		this.closedList = new ShortOpenHashSet(1024);
		this.cpy = new ShortOpenHashSet(1024);
		for (int i = 0; i < this.callbackCache.length; ++i) {
			this.callbackCache[i] = new ExplosionCubeConvexBlockCallback();
		}
	}
	
	public void updateCallbacks() {
		ReentrantReadWriteLock update = null;
		for (int i = 0; i < this.cubeCallbackPointer; ++i) {
			update = this.callbackCache[i].update(update);
		}
		if (update != null) {
			update.readLock().unlock();
		}
	}
	
	public void addCharacterHittable(final AbstractCharacter<?> abstractCharacter) {
		this.addControllerSub(abstractCharacter, -0.3f);
		this.addControllerSub(abstractCharacter, 0.0f);
		this.addControllerSub(abstractCharacter, 0.3f);
	}
	
	private void addControllerSub(final AbstractCharacter<?> abstractCharacter, final float n) {
		final ExplosionCubeConvexBlockCallback explosionCubeConvexBlockCallback;
		(explosionCubeConvexBlockCallback = this.callbackCache[this.cubeCallbackPointer]).type = 1;
		explosionCubeConvexBlockCallback.segEntityId = abstractCharacter.getId();
		explosionCubeConvexBlockCallback.blockHpOrig = (short)((AbstractOwnerState)abstractCharacter.getOwnerState()).getHealth();
		explosionCubeConvexBlockCallback.blockHp = (short)((AbstractOwnerState)abstractCharacter.getOwnerState()).getHealth();
		explosionCubeConvexBlockCallback.boxTransform.set((Transform)abstractCharacter.getWorldTransform());
		GlUtil.getUpVector(this.tmp, explosionCubeConvexBlockCallback.boxTransform);
		this.tmp.scale(n);
		explosionCubeConvexBlockCallback.boxTransform.origin.add((Tuple3f)this.tmp);
		this.tmp.sub((Tuple3f)explosionCubeConvexBlockCallback.boxTransform.origin, (Tuple3f)this.centerOfExplosion);
		explosionCubeConvexBlockCallback.boxPosToCenterOfExplosion.set((Tuple3f)this.tmp);
		++this.cubeCallbackPointer;
	}
	
	public void sortInsertShieldAndArmorValues(final Vector3f vector3f, final int n, final List<Sendable> list) throws SectorNotFoundException {
		this.shieldMap.clear();
		this.shieldLocalMap.clear();
		this.shieldMapBef.clear();
		this.shieldHitPosMap.clear();
		this.hitPosMap.clear();
		this.interEffectMap.clear();
		this.environmentalProtectMultMap.clear();
		this.railMap.clear();
		this.railRootMap.clear();
		this.appliedDamageMap.clear();
		this.extraDamageTakenMap.clear();
		final Iterator<Sendable> iterator = list.iterator();
		while (iterator.hasNext()) {
			final Sendable sendable;
			if ((sendable = iterator.next()) instanceof SimpleTransformableSendableObject) {
				this.interEffectMap.put(sendable.getId(), ((SimpleTransformableSendableObject)sendable).getEffectContainer());
			}
			if (sendable instanceof ConfigManagerInterface) {
				this.environmentalProtectMultMap.put(sendable.getId(), ((SegmentController)sendable).getConfigManager().apply(StatusEffectType.ARMOR_DEFENSE_ENVIRONMENTAL, 1.0f));
			}
			if (sendable instanceof SegmentController) {
				final SegmentController segmentController = (SegmentController)sendable;
				if (!this.ignoreShield && segmentController instanceof ManagedSegmentController && ((ManagedSegmentController)segmentController).getManagerContainer() instanceof ShieldContainerInterface) {
					((ShieldContainerInterface)((ManagedSegmentController)segmentController).getManagerContainer()).getShieldAddOn().fillShieldsMapRecursive(vector3f, n, this.shieldMap, this.shieldMapBef, this.shieldMapPercent, this.railMap, this.railRootMap, this.shieldLocalMap);
				}
				this.extraDamageTakenMap.put(segmentController.getId(), segmentController.getDamageTakenMultiplier(this.damageType));
			}
		}
	}
	
	public void sortAndInsertCallbackCache() {
		this.explosionBlockMapA.clear();
		this.explosionBlockMapB.clear();
		this.explosionBlockMapC.clear();
		this.explosionBlockMapD.clear();
		for (int i = 0; i < this.cubeCallbackPointer; ++i) {
			final ExplosionCubeConvexBlockCallback explosionCubeConvexBlockCallback;
			final long index = ElementCollection.getIndex(Math.round((explosionCubeConvexBlockCallback = this.callbackCache[i]).boxPosToCenterOfExplosion.x), Math.round(explosionCubeConvexBlockCallback.boxPosToCenterOfExplosion.y), Math.round(explosionCubeConvexBlockCallback.boxPosToCenterOfExplosion.z));
			if (!this.explosionBlockMapA.containsKey(index)) {
				this.explosionBlockMapA.put(index, i);
			}
			else if (!this.explosionBlockMapB.containsKey(index)) {
				this.explosionBlockMapB.put(index, i);
			}
			else if (!this.explosionBlockMapC.containsKey(index)) {
				this.explosionBlockMapC.put(index, i);
			}
			else if (!this.explosionBlockMapD.containsKey(index)) {
				this.explosionBlockMapD.put(index, i);
			}
			else {
				assert false;
			}
		}
	}
	
	public void reset() {
		this.cubeCallbackPointer = 0;
		this.ownLockedSegments.clear();
		this.hitSegments.clear();
		this.hitSendSegments.clear();
		this.entitiesToIgnoreShieldsOn.clear();
		this.armorMode = ArmorMode.PREEMTIVE;
	}
	
	public void growCache(final int n) {
		if (n >= this.callbackCache.length) {
			final ExplosionCubeConvexBlockCallback[] callbackCache = new ExplosionCubeConvexBlockCallback[this.callbackCache.length << 1];
			for (int i = 0; i < this.callbackCache.length; ++i) {
				callbackCache[i] = this.callbackCache[i];
			}
			for (int j = this.callbackCache.length; j < callbackCache.length; ++j) {
				callbackCache[j] = new ExplosionCubeConvexBlockCallback();
			}
			this.callbackCache = callbackCache;
		}
	}
	
	private float damageBlock(final int n, int max, final int n2, float b, final ExplosionCubeConvexBlockCallback explosionCubeConvexBlockCallback) {
		System.err.println("#XXX: damageBlock");
		//try {throw new Exception();}
		//catch(Exception e) {e.printStackTrace();}
		if (explosionCubeConvexBlockCallback.blockHp == 0) {
			return b;
		}
		float n3 = 0.0f;
		this.appliedDamageMap.put(explosionCubeConvexBlockCallback.segEntityId, this.appliedDamageMap.get(explosionCubeConvexBlockCallback.segEntityId) + b);
		if (this.damageType == DamageDealerType.PULSE) {
			n3 = (b *= 0.5f);
		}
		if (this.hitShield(b, explosionCubeConvexBlockCallback)) {
			return n3;
		}
		final InterEffectContainer interEffectContainer = (InterEffectContainer)this.interEffectMap.get(explosionCubeConvexBlockCallback.segEntityId);
		if (this.armorMode == ArmorMode.ON_ALL_BLOCKS && explosionCubeConvexBlockCallback.blockId != 0) {
			final ElementInformation infoFast;
			final HitReceiverType hitReceiverType = (infoFast = ElementKeyMap.getInfoFast(explosionCubeConvexBlockCallback.blockId)).isArmor() ? HitReceiverType.ARMOR : HitReceiverType.BLOCK;
			final InterEffectSet value = interEffectContainer.get(hitReceiverType);
			final InterEffectSet defenseTmp;
			(defenseTmp = this.defenseTmp).setDefenseFromInfo(infoFast);
			System.err.println("#XXX: defenseTmp: " + defenseTmp.toString());
			System.err.println("#XXX: value: " + value.toString());
			System.err.println("#XXX: environmentalProtectMultMap: " + this.environmentalProtectMultMap);
			System.err.println("#XXX: environmentalProtectMultMap.get: " + this.environmentalProtectMultMap.get(explosionCubeConvexBlockCallback.segEntityId));
			System.err.println("#XXX: hitType: " + this.hitType.toString());
			if (this.hitType == HitType.ENVIROMENTAL) {
				defenseTmp.scaleAdd(value, this.environmentalProtectMultMap.get(explosionCubeConvexBlockCallback.segEntityId));
			}
			else {
				defenseTmp.add(value);
			}
			System.err.println("#XXX: defenseTmp: " + defenseTmp.toString());
			b = InterEffectHandler.handleEffects(b, this.attack, defenseTmp, this.hitType, this.damageType, hitReceiverType, explosionCubeConvexBlockCallback.blockId);
		}
		final int blockHp = explosionCubeConvexBlockCallback.blockHp;
		max = Math.max(0, blockHp - Math.round(b * this.extraDamageTakenMap.get(explosionCubeConvexBlockCallback.segEntityId)));
		final int max2 = Math.max(0, max);
		if (this.hitPosMap.get(explosionCubeConvexBlockCallback.segEntityId) == null) {
			this.hitPosMap.put(explosionCubeConvexBlockCallback.segEntityId, explosionCubeConvexBlockCallback);
		}
		if (b < blockHp) {
			Math.max(0.0f, b);
			explosionCubeConvexBlockCallback.blockHp = max2;
			return n3;
		}
		b = Math.max(0.0f, b - blockHp);
		explosionCubeConvexBlockCallback.blockHp = 0;
		return b + n3;
	}
	
	@Deprecated
	private int findShieldShare(float n, int n2, int value) {
		double value2;
		while ((value2 = this.shieldMap.get(value)) <= 0.0 || n2 != value) {
			if (n2 != value && this.shieldMapPercent.get(value) > VoidElementManager.SHIELD_DOCK_TRANSFER_LIMIT && n < value2) {
				return value;
			}
			if (!this.railMap.containsKey(value)) {
				return -1;
			}
			final ExplosionCollisionSegmentCallback explosionCollisionSegmentCallback = this;
			final float n3 = n;
			final int n4 = n2;
			value = this.railMap.get(value);
			n2 = n4;
			n = n3;
			//this = explosionCollisionSegmentCallback; //decompiler artifact
		}
		return n2;
	}
	
	private boolean hitShield(final float n, final ExplosionCubeConvexBlockCallback explosionCubeConvexBlockCallback) {
		if (this.ignoreShieldsGlobal || this.entitiesToIgnoreShieldsOn.contains(explosionCubeConvexBlockCallback.segEntityId)) {
			return false;
		}
		int shieldShare;
		if (this.useLocalShields) {
			int n2;
			if ((n2 = this.railRootMap.get(explosionCubeConvexBlockCallback.segEntityId)) == 0) {
				n2 = explosionCubeConvexBlockCallback.segEntityId;
			}
			if (this.shieldMap.get(n2) < n) {
				shieldShare = -1;
			}
			else {
				shieldShare = n2;
			}
		}
		else {
			shieldShare = this.findShieldShare(n, explosionCubeConvexBlockCallback.segEntityId, explosionCubeConvexBlockCallback.segEntityId);
		}
		final double value;
		if (shieldShare >= 0 && (value = this.shieldMap.get(shieldShare)) > 0.0) {
			final HitReceiverType shield = HitReceiverType.SHIELD;
			final ElementInformation infoFast = ElementKeyMap.getInfoFast(explosionCubeConvexBlockCallback.blockId);
			final InterEffectContainer interEffectContainer = (InterEffectContainer)this.interEffectMap.get(explosionCubeConvexBlockCallback.segEntityId);
			final InterEffectSet defenseTmp;
			(defenseTmp = this.defenseTmp).setEffect(VoidElementManager.shieldEffectConfiguration);
			defenseTmp.add(interEffectContainer.get(shield));
			if (VoidElementManager.individualBlockEffectArmorOnShieldHit) {
				defenseTmp.add(infoFast.effectArmor);
			}
			final float handleEffects = InterEffectHandler.handleEffects(n, this.attack, defenseTmp, this.hitType, this.damageType, shield, explosionCubeConvexBlockCallback.blockId);
			final double max = Math.max(0.0, value - (handleEffects + handleEffects * this.shieldDamageBonus));
			this.shieldMap.put(shieldShare, (max == 0.0) ? -1.0 : max);
			if (this.shieldHitPosMap.get(shieldShare) == null) {
				this.shieldHitPosMap.put(shieldShare, explosionCubeConvexBlockCallback);
			}
			return true;
		}
		return false;
	}
	
	@Override
	public float modifyDamageBasedOnBlockArmor(int value, final int n, final int n2, float handleEffects) {
		System.err.println("#XXX: modifyDamageBasedOnBlockArmor");
		if ((value = this.explosionBlockMapA.get(ElementCollection.getIndex(value, n, n2))) >= 0 && ElementKeyMap.isValidType(this.callbackCache[value].blockId)) {
			final ExplosionCubeConvexBlockCallback explosionCubeConvexBlockCallback = this.callbackCache[value];
			final InterEffectContainer interEffectContainer = (InterEffectContainer)this.interEffectMap.get(explosionCubeConvexBlockCallback.segEntityId);
			final ElementInformation infoFast;
			final HitReceiverType hitReceiverType = (infoFast = ElementKeyMap.getInfoFast(explosionCubeConvexBlockCallback.blockId)).isArmor() ? HitReceiverType.ARMOR : HitReceiverType.BLOCK;
			final InterEffectSet defenseTmp;
			(defenseTmp = this.defenseTmp).setDefenseFromInfo(infoFast);
			System.err.println("#XXX: defenseTmp: " + defenseTmp.toString());
			System.err.println("#XXX: IEC.get: " + interEffectContainer.get(hitReceiverType));
			System.err.println("#XXX: environmentalProtectMultMap: " + this.environmentalProtectMultMap);
			System.err.println("#XXX: environmentalProtectMultMap.get: " + this.environmentalProtectMultMap.get(explosionCubeConvexBlockCallback.segEntityId));
			System.err.println("#XXX: hitType: " + this.hitType.toString());
			if (this.hitType == HitType.ENVIROMENTAL) {
				defenseTmp.scaleAdd(interEffectContainer.get(hitReceiverType), this.environmentalProtectMultMap.get(explosionCubeConvexBlockCallback.segEntityId));
			}
			else {
				defenseTmp.add(interEffectContainer.get(hitReceiverType));
			}
			System.err.println("#XXX: defenseTmp: " + defenseTmp.toString());
			handleEffects = InterEffectHandler.handleEffects(handleEffects, this.attack, defenseTmp, this.hitType, this.damageType, hitReceiverType, explosionCubeConvexBlockCallback.blockId);
		}
		return handleEffects;
	}
	
	@Override
	public float damageBlock(final int n, final int n2, final int n3, float n4) {
		System.err.println("#XXX: damageBlock (weird version)");
		System.err.println("#XXX: n=" + n + ", n2=" + n2 + ", n3=" + n3 + ", n4=" + n4);
		final int value;
		if ((value = this.explosionBlockMapA.get(ElementCollection.getIndex(n, n2, n3))) >= 0) {
			System.err.println("#XXX: value: " + value);
			n4 = this.damageBlock(n, n2, n3, n4, this.callbackCache[value]);
			final int value2;
			if ((value2 = this.explosionBlockMapB.get(ElementCollection.getIndex(n, n2, n3))) >= 0) {
				System.err.println("#XXX: value2: " + value2);
				n4 = this.damageBlock(n, n2, n3, n4, this.callbackCache[value2]);
				final int value3;
				if ((value3 = this.explosionBlockMapC.get(ElementCollection.getIndex(n, n2, n3))) >= 0) {
					System.err.println("#XXX: value3: " + value3);
					n4 = this.damageBlock(n, n2, n3, n4, this.callbackCache[value3]);
					final int value4;
					if ((value4 = this.explosionBlockMapD.get(ElementCollection.getIndex(n, n2, n3))) >= 0) {
						System.err.println("#XXX: value4: " + value4);
						n4 = this.damageBlock(n, n2, n3, n4, this.callbackCache[value4]);
					}
				}
			}
		}
		return n4;
	}
	
	@Override
	public void setDamage(int n, int n2, final int n3, final float n4) {
		n += this.explosionDataStructure.bigLengthHalf;
		n2 += this.explosionDataStructure.bigLengthHalf;
		n += (n3 + this.explosionDataStructure.bigLengthHalf) * this.explosionDataStructure.bigLengthQuad + n2 * this.explosionDataStructure.bigLength;
		this.explosionDamageBuffer[n] = n4;
	}
	
	@Override
	public float getDamage(int i, int j, int k) {
		i += this.explosionDataStructure.bigLengthHalf;
		j += this.explosionDataStructure.bigLengthHalf;
		final int n = (k += this.explosionDataStructure.bigLengthHalf) * this.explosionDataStructure.bigLengthQuad + j * this.explosionDataStructure.bigLength + i;
		assert n < this.explosionDamageBuffer.length : i + ", " + j + ", " + k;
		return this.explosionDamageBuffer[n];
	}
	
	@Override
	public void resetDamage() {
		Arrays.fill(this.explosionDamageBuffer, 0.0f);
		this.closedList.clear();
		this.cpy.clear();
	}
	
	@Override
	public ShortOpenHashSet getClosedList() {
		return this.closedList;
	}
	
	@Override
	public ShortOpenHashSet getCpy() {
		return this.cpy;
	}
	
	@Override
	public Vector3f getExplosionCenter() {
		return this.centerOfExplosion;
	}
	
	public enum ArmorMode
	{
		ON_ALL_BLOCKS, 
		PREEMTIVE, 
		NONE;
	}
}
