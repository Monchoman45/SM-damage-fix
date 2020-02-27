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
		final int ordinal = interEffectType.ordinal();
		final float[] strength = this.strength;
		final int n = ordinal;
		strength[n] += configEntityManager.apply(statusEffectType, this.strength[ordinal]);
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
