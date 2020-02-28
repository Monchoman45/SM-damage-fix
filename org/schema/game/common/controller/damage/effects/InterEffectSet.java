// 
// Decompiled by Procyon v0.5.36
// 

package org.schema.game.common.controller.damage.effects;

import org.schema.game.common.controller.elements.VoidElementManager;
import org.schema.game.common.data.element.ElementInformation;
import org.schema.game.common.data.blockeffects.config.StatusEffectType;
import org.schema.game.common.data.blockeffects.config.ConfigEntityManager;
import org.w3c.dom.NodeList;
import org.schema.common.config.ConfigParserException;
import java.util.Locale;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.w3c.dom.Node;
import java.util.Arrays;

public class InterEffectSet
{
	public static final int length;
	private float[] strength;
	
	public InterEffectSet() {
		this.strength = new float[InterEffectSet.length];
	}
	
	public InterEffectSet(final InterEffectSet effect) {
		this.strength = new float[InterEffectSet.length];
		this.setEffect(effect);
	}
	
	public float getStrength(final InterEffectHandler.InterEffectType interEffectType) {
		return this.strength[interEffectType.ordinal()];
	}
	
	public void reset() {
		Arrays.fill(this.strength, 0.0f);
	}
	
	public void setEffect(final InterEffectSet set) {
		for (int i = 0; i < InterEffectSet.length; ++i) {
			this.strength[i] = set.strength[i];
		}
	}
	
	public void setStrength(final InterEffectHandler.InterEffectType interEffectType, final float n) {
		this.strength[interEffectType.ordinal()] = n;
	}
	
	public void scaleAdd(final InterEffectSet set, final float n) {
		for (int i = 0; i < InterEffectSet.length; ++i) {
			final float[] strength = this.strength;
			final int n2 = i;
			strength[n2] += set.strength[i] * n;
		}
	}
	
	public boolean hasEffect(final InterEffectHandler.InterEffectType interEffectType) {
		return this.getStrength(interEffectType) > 0.0f;
	}
	
	public void parseXML(final Node node) throws ConfigParserException {
		final ObjectOpenHashSet obj = new ObjectOpenHashSet();
		final InterEffectHandler.InterEffectType[] values = InterEffectHandler.InterEffectType.values();
		final NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			final Node item;
			if ((item = childNodes.item(i)).getNodeType() == 1) {
				final String lowerCase = item.getNodeName().toLowerCase(Locale.ENGLISH);
				boolean b = false;
				InterEffectHandler.InterEffectType[] array;
				for (int length = (array = values).length, j = 0; j < length; ++j) {
					final InterEffectHandler.InterEffectType interEffectType;
					if ((interEffectType = array[j]).id.toLowerCase(Locale.ENGLISH).equals(lowerCase)) {
						try {
							this.strength[interEffectType.ordinal()] = Float.parseFloat(item.getTextContent());
						}
						catch (NumberFormatException ex) {
							throw new ConfigParserException(item.getParentNode().getParentNode().getNodeName() + "->" + item.getParentNode().getNodeName() + "->" + item.getNodeName() + " must be floating point value", ex);
						}
						b = true;
						obj.add((Object)interEffectType);
						break;
					}
				}
				if (!b) {
					throw new ConfigParserException(item.getParentNode().getParentNode().getNodeName() + "->" + item.getParentNode().getNodeName() + "->" + item.getNodeName() + " no effect found for '" + lowerCase + "'; must be one of: " + Arrays.toString(values));
				}
			}
		}
		if (obj.size() < values.length) {
			throw new ConfigParserException(node.getParentNode().getNodeName() + "->" + node.getNodeName() + " missing effects element! set: " + obj + ";. Must contain all of " + Arrays.toString(values));
		}
	}
	
	public boolean isZero() {
		float[] strength;
		for (int length = (strength = this.strength).length, i = 0; i < length; ++i) {
			if (strength[i] != 0.0f) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer();
		final InterEffectHandler.InterEffectType[] values = InterEffectHandler.InterEffectType.values();
		sb.append("EFFECT[");
		for (int i = 0; i < InterEffectSet.length; ++i) {
			sb.append("(");
			sb.append(values[i].id);
			sb.append(" = ");
			sb.append(this.strength[i]);
			sb.append(")");
			if (i < InterEffectSet.length - 1) {
				sb.append(", ");
			}
		}
		sb.append("]");
		return sb.toString();
	}
	
	public void applyAddEffectConfig(final ConfigEntityManager configEntityManager, final StatusEffectType statusEffectType, final InterEffectHandler.InterEffectType interEffectType) {
		int i = interEffectType.ordinal();
		//#XXX: this is the fix to defense chambers
		//previously, this would pass the ConfigEntityManager this.strength[i]
		//instead of 1.0F, but this.strength[i] is always 0 at this point and
		//.apply multiplies the config state with the number you give it, and
		//as a result defense chamber effects were multiplied by 0 before
		//being calculated. passing 1.0 lets us get a result out, but note that
		//the result of this is added to block resistance, not multiplied with
		//it, which puts us in a weird situation: if there's no defense chamber,
		//the returned value is just 1, and as an effect resistance 1 is 100%,
		//but if there is a defense chamber then the returned value is less than
		//1, which means if subtracting 1 from it you get a negative resistance.
		//to fix this the base armor chamber no longer adds a .1 resistance, it
		//sets a 1.1 resistance, which allows us to correctly compute this here,
		//but also means when you mouse over the base armor chamber node it says
		//you're gaining 110% resistance, which is not actually true.
		//the best way to fix this would be to modify ConfigEntityManager and
		//give it an applyAdd function or something of the sort which uses
		//addition rather than multiplication, but i wasn't able to recompile
		//ConfigEntityManager so this is the best i have.
		this.strength[i] += configEntityManager.apply(statusEffectType, 1.0F) - 1.0F;
		//#XXX:
	}
	
	public void mul(final InterEffectSet set) {
		for (int i = 0; i < InterEffectSet.length; ++i) {
			final float[] strength = this.strength;
			final int n = i;
			strength[n] *= set.strength[i];
		}
	}
	
	public void add(final InterEffectSet set) {
		for (int i = 0; i < InterEffectSet.length; ++i) {
			final float[] strength = this.strength;
			final int n = i;
			strength[n] += set.strength[i];
		}
	}
	
	public void setDefenseFromInfo(final ElementInformation elementInformation) {
		this.setAdd(elementInformation.effectArmor, elementInformation.isArmor() ? VoidElementManager.armorEffectConfiguration : VoidElementManager.basicEffectConfiguration);
	}
	
	public void setAdd(final InterEffectSet effect, final InterEffectSet set) {
		this.setEffect(effect);
		this.add(set);
	}
	
	static {
		length = InterEffectHandler.InterEffectType.values().length;
	}
}
