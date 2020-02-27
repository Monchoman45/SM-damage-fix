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
	
	public static float handleEffects(final float n, final InterEffectSet set, final InterEffectSet set2, final HitType hitType, final DamageDealerType damageDealerType, final HitReceiverType hitReceiverType, final short n2) {
		float n3 = 0.0f;
		for (int i = 0; i < InterEffectHandler.EFFECTS.length; ++i) {
			n3 += InterEffectHandler.EFFECTS[i].getOutputDamage(n, InterEffectHandler.EFFECTS[i].getType(), set, set2, hitType, damageDealerType, hitReceiverType, n2);
		}
		return Math.max(0.0f, n3 / InterEffectHandler.EFFECTS.length);
	}
	
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
