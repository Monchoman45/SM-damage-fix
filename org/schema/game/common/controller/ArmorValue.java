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
}
