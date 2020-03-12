// 
// Decompiled by Procyon v0.5.36
// 

package org.schema.game.common.controller;

import java.util.Collection;
import org.schema.game.common.data.element.ElementInformation;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.schema.game.common.controller.elements.VoidElementManager;
import org.schema.game.common.data.physics.CubeRayCastResult;
import javax.vecmath.Vector3f;
import org.schema.game.common.data.physics.ModifiedDynamicsWorld;
import javax.vecmath.Tuple3f;
import com.bulletphysics.collision.dispatch.CollisionWorld;
import org.schema.game.common.data.physics.InnerSegmentIterator;

public class ArmorValue
{
	public ObjectArrayList<ElementInformation> typesHit;
	public float armorValueAccumulatedRaw;
	public float armorIntegrity;
	public float totalArmorValue;
	
	public ArmorValue() {
		this.typesHit = (ObjectArrayList<ElementInformation>)new ObjectArrayList();
	}
	
	public void reset() {
		this.typesHit.clear();
		this.armorValueAccumulatedRaw = 0.0f;
		this.armorIntegrity = 0.0f;
		this.totalArmorValue = 0.0f;
	}
	
	public void calculate() {
		assert !this.typesHit.isEmpty();
		this.armorIntegrity /= this.typesHit.size();
		System.err.println("#XXX: armorIntegrity: " + this.armorIntegrity);
		System.err.println("#XXX: size: " + this.typesHit.size());
		this.totalArmorValue = this.armorValueAccumulatedRaw * this.armorIntegrity;
	}
	
	public void set(final ArmorValue armorValue) {
		this.reset();
		this.typesHit.addAll((Collection)armorValue.typesHit);
		this.armorValueAccumulatedRaw = armorValue.armorValueAccumulatedRaw;
		this.armorIntegrity = armorValue.armorIntegrity;
		this.totalArmorValue = armorValue.totalArmorValue;
	}

	//#XXX: new armor counter
	//so currently cannons, beams, and build mode all use separate armor counters with separate ray casts
	//that each produce results different enough to be a pain to work with - for example, until i started
	//poking around in here, it was common knowledge that armor only stacked up to 32 blocks due to some
	//hard limit in the  game, which it turns out is only true for cannons and doesn't really have anything
	//to do with depth, instead being a segment boundary thing and a quirk of how the projectile traverser
	//works. build mode will happily count up to 400 blocks (maybe 600 at an angle) and beams will actually
	//count thousands in theory, because their counter raycasts the entire beam up to max range, which can
	//be several kilometers (especially on longer range combos like beam/beam).
	//
	//aside from being difficult to explain to new players, this isn't really conducive to balance; when
	//everything behaves by a completely different set of invisible rules it's hard to tell what's working
	//and what's not - i discovered most of these things by putting my own print statements directly in the
	//code to get the information i wanted, and then spending a lot of time digging through logs.
	//
	//so now there's this: a single unified armor counter. i stole this from AbstractBeamHandler (although
	//BuildModeDrawer was using a pretty similar version) and replaced every armor counting function with
	//this. now there are no more weird differences between beams and cannons and the armor value you see
	//in build mode is actually the armor value a gun will experience when it hits you at that location
	//and angle. the amount of armor counted is configurable in blockBehaviorConfig (ArmorRaycastLength)
	//and is set to 100m by default. this is actually smaller than before for both beams and build mode
	//(beams raycast several kilometers and build mode was raycasting 400m), but because cannons are what
	//people tested this with, they'll likely notice armor stacking better than it did previously.
	//
	//it is worth noting that this does not guarantee a max of 100 blocks (or whatever you set the config
	//to), rather it guarantees a max of 100 meters, which will contain more than 100 blocks if cast at an
	//angle. in practice, at a setting of 100, the most stacking i've observed is a little under 150 blocks.
	//i initially thought i couldn't guarantee a fixed block value before i started digging into traversers
	//and realized i probably could, but when i brought this up with QF everyone seemed to like the idea that
	//hitting armor at an angle makes your shot a bit weaker than normal, so i decided to keep it the way it
	//is.
	//
	//doing a dedicated raycast for this is unlikely to have a significant performance impact on the game;
	//beams and build mode were both already using dedicated raycasts, and although cannons weren't, a single
	//extra raycast is unlikely to impact their performance significantly. also i'm pretty sure if you
	//managed to shoot a single block on a segment border you'd experience no armor at all and get 100% acid
	//regardless of how much armor was supposed to be there so whatever minor performance penalty this comes
	//with is likely less bad than the current state anyway.
	private static ArmorCheckTraverseHandler traverseHandler = new ArmorCheckTraverseHandler();
	private static ArmorValue armorValue = new ArmorValue();
	private static CubeRayCastResult rct = new CubeRayCastResult(new Vector3f(), new Vector3f(), null, new SegmentController[0]) {
		@Override
		public InnerSegmentIterator newInnerSegmentIterator() {
			return ArmorValue.traverseHandler;
		}
	};
	public static int lastSize = 0;
	public static float lastArmor = 0.0f;
	static {
		ArmorValue.traverseHandler.armorValue = ArmorValue.armorValue;
	}
	public static float countArmor(SegmentController segmentController, Vector3f from, Vector3f dir) {
		System.err.println("#XXX: countArmor");
		Vector3f to = new Vector3f(dir);
		to.normalize();
		to.scaleAdd(VoidElementManager.ARMOR_RAYCAST_LENGTH, from);

		System.err.println("#XXX: from: " + from.toString());
		System.err.println("#XXX: dir: " + dir.toString());
		System.err.println("#XXX: to: " + to.toString());
		ArmorValue.rct.rayFromWorld.set(from);
		ArmorValue.rct.rayToWorld.set(to);
		ArmorValue.rct.closestHitFraction = 1.0f;
		ArmorValue.rct.collisionObject = null;
		ArmorValue.rct.setSegment(null);
		ArmorValue.rct.setFilter(segmentController);
		ArmorValue.rct.setOwner(null);
		ArmorValue.rct.setIgnoereNotPhysical(false);
		ArmorValue.rct.setIgnoreDebris(false);
		ArmorValue.rct.setRecordAllBlocks(false);
		ArmorValue.rct.setZeroHpPhysical(false);
		ArmorValue.rct.setDamageTest(true);
		ArmorValue.rct.setCheckStabilizerPaths(false);
		ArmorValue.rct.setSimpleRayTest(true);
		ArmorValue.armorValue.reset();
		((ModifiedDynamicsWorld)segmentController.getPhysics().getDynamicsWorld()).rayTest(from, to, ArmorValue.rct);
		ArmorValue.lastSize = ArmorValue.armorValue.typesHit.size();
		if(ArmorValue.lastSize > 0) {
			ArmorValue.armorValue.calculate();
		}
		ArmorValue.rct.setFilter(new SegmentController[0]);
		ArmorValue.lastArmor = ArmorValue.armorValue.totalArmorValue;
		return ArmorValue.lastArmor;
	}
	public static Vector3f dir(Vector3f from, Vector3f to) {
		Vector3f dir = new Vector3f(to);
		dir.sub(from);
		dir.normalize();
		return dir;
	}
	//#XXX:
}
