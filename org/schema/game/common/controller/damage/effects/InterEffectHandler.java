// 
// Decompiled by Procyon v0.5.36
// 

package org.schema.game.common.controller.damage.effects;

import org.schema.schine.common.language.Lng;
import org.schema.schine.common.language.Translatable;
import org.schema.game.common.controller.damage.HitReceiverType;
import org.schema.game.common.controller.damage.DamageDealerType;
import org.schema.game.common.controller.damage.HitType;

public abstract class InterEffectHandler
{
	private static final InterEffectHandler[] EFFECTS;
	
	public abstract InterEffectType getType();

	//#XXX: this is the rebuilt damage formula, essentially attack * (1 - defense) * damage
	//attack and defense are both a percentage between 0 and 1, meaning .5 attack in an effect is 50%
	//in that effect and .25 defense in an effect is a 25% resistance to that effect. these are
	//multiplied together so if you use a 50% effect to shoot a 25% resistance you get 37.5% of listed
	//damage. all the included configs have attack spreads that add up to 1, and defense spreads that
	//add up to 0. this means a weapon that hits neutrally (eg. cannon vs shield) will do exactly its
	//listed damage, and will do more than listed damage vs a good target (eg. cannon vs systems) or 
	//less than listed damage vs a bad target (eg. cannon vs armor).
	public static float handleEffects(float damage, InterEffectSet attack, InterEffectSet defense, HitType hitType, DamageDealerType ddType, HitReceiverType receiverType, short n) {
		if(attack == null || defense == null) {return 0.0F;}

		float modifier = 0.0F;
		System.err.println("#XXX: damage calc: damage: " + damage + ", attack: " + attack.toString() + ", defense: " + defense.toString());
		for (byte b = 0; b < EFFECTS.length; b++) {
			modifier += attack.getStrength(EFFECTS[b].getType()) * (1 - defense.getStrength(EFFECTS[b].getType()));
		}
		damage *= modifier;
		System.err.println("#XXX: final: " + Math.round(modifier * 100) + "% effective, " + damage + " damage");
		return damage;
	}
	//#XXX:
	
	//#XXX: this function is no longer called anywhere
	public float getOutputDamage(final float n, final InterEffectType interEffectType, final InterEffectSet set, final InterEffectSet set2, final HitType hitType, final DamageDealerType damageDealerType, final HitReceiverType hitReceiverType, final short n2) {
		assert set != null;
		return n * Math.max(0.0f, ((set != null) ? set.getStrength(interEffectType) : 0.0f) - ((set2 != null) ? set2.getStrength(interEffectType) : 0.0f));
	}
	
	static {
		EFFECTS = new InterEffectHandler[InterEffectType.values().length];
		for (int i = 0; i < InterEffectType.values().length; ++i) {
			switch (InterEffectType.values()[i]) {
				case HEAT: {
					InterEffectHandler.EFFECTS[i] = new HeatEffectHandler();
					break;
				}
				case KIN: {
					InterEffectHandler.EFFECTS[i] = new KineticEffectHandler();
					break;
				}
				case EM: {
					InterEffectHandler.EFFECTS[i] = new EMEffectHandler();
					break;
				}
				default: {
					assert false;
					break;
				}
			}
			assert InterEffectHandler.EFFECTS[i].getType() == InterEffectType.values()[i] : InterEffectType.values()[i] + "; " + i;
		}
	}
	
	public enum InterEffectType
	{
		HEAT("Heat", (Translatable)new Translatable() {
			@Override
			public final String getName(final Enum enum1) {
				return Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_DAMAGE_EFFECTS_INTEREFFECTHANDLER_1;
			}
		}, (Translatable)new Translatable() {
			@Override
			public final String getName(final Enum enum1) {
				return Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_DAMAGE_EFFECTS_INTEREFFECTHANDLER_0;
			}
		}), 
		KIN("Kinetic", (Translatable)new Translatable() {
			@Override
			public final String getName(final Enum enum1) {
				return Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_DAMAGE_EFFECTS_INTEREFFECTHANDLER_2;
			}
		}, (Translatable)new Translatable() {
			@Override
			public final String getName(final Enum enum1) {
				return Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_DAMAGE_EFFECTS_INTEREFFECTHANDLER_3;
			}
		}), 
		EM("EM", (Translatable)new Translatable() {
			@Override
			public final String getName(final Enum enum1) {
				return Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_DAMAGE_EFFECTS_INTEREFFECTHANDLER_4;
			}
		}, (Translatable)new Translatable() {
			@Override
			public final String getName(final Enum enum1) {
				return Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_DAMAGE_EFFECTS_INTEREFFECTHANDLER_5;
			}
		});
		
		public final Translatable fullName;
		public final Translatable shortName;
		public final String id;
		
		private InterEffectType(final String id, final Translatable shortName, final Translatable fullName) {
			this.id = id;
			this.fullName = fullName;
			this.shortName = shortName;
		}
		
		@Override
		public final String toString() {
			return this.id;
		}
	}
}
