// 
// Decompiled by Procyon v0.5.36
// 

package org.schema.game.common.data.element.annotation;

import org.schema.game.common.controller.damage.effects.InterEffectHandler;
import org.schema.game.common.facedit.ElementInformationOption;
import javax.vecmath.Tuple4f;
import java.util.Iterator;
import org.schema.game.common.data.element.BlockFactory;
import org.w3c.dom.NamedNodeMap;
import org.schema.game.common.data.element.ElementParser;
import java.util.Locale;
import java.util.ArrayList;
import org.schema.game.common.data.element.FactoryResource;
import javax.vecmath.Vector4f;
import org.w3c.dom.NodeList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import org.schema.game.common.data.element.ElementParserException;
import org.schema.game.common.data.element.ElementInformation;
import org.w3c.dom.Node;

public enum ElemType implements Comparable<ElemType>
{
	CONSISTENCE("Consistence", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			final FactoryResource[] resource = ElemType.parseResource(node);
			for (int i = 0; i < resource.length; ++i) {
				elementInformation.getConsistence().add(resource[i]);
			}
		}
	}), 
	CUBATON_CONSISTENCE("CubatomConsistence", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			final FactoryResource[] resource = ElemType.parseResource(node);
			for (int i = 0; i < resource.length; ++i) {
				elementInformation.cubatomConsistence.add(resource[i]);
			}
		}
	}), 
	CONTROLLED_BY("ControlledBy", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			final NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); ++i) {
				final Node item;
				if ((item = childNodes.item(i)).getNodeType() == 1) {
					if (!item.getNodeName().equals("Element")) {
						throw new ElementParserException("[controlledBy] All child nodes of " + node.getNodeName() + " have to be \"Element\" but is " + item.getNodeName() + " (" + node.getParentNode().getNodeName() + ")");
					}
					final String property;
					if ((property = ElementParser.properties.getProperty(item.getTextContent())) == null) {
						throw new ElementParserException("[controlledBy] The value of " + item.getTextContent() + " has not been found");
					}
					short s;
					try {
						s = (short)Integer.parseInt(property);
					}
					catch (NumberFormatException ex) {
						throw new ElementParserException("[controlledBy] The property " + property + " has to be an Integer value");
					}
					elementInformation.getControlledBy().add(s);
				}
			}
		}
	}), 
	CONTROLLING("Controlling", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			final NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); ++i) {
				final Node item;
				if ((item = childNodes.item(i)).getNodeType() == 1) {
					if (!item.getNodeName().equals("Element")) {
						throw new ElementParserException("All child nodes of " + node.getNodeName() + " have to be \"Element\" but is " + item.getNodeName() + " (" + node.getParentNode().getNodeName() + ")");
					}
					final String property;
					if ((property = ElementParser.properties.getProperty(item.getTextContent())) == null) {
						throw new ElementParserException("[controlling] The value of " + item.getTextContent() + " has not been found");
					}
					short s;
					try {
						s = (short)Integer.parseInt(property);
					}
					catch (NumberFormatException ex) {
						throw new ElementParserException("[controlling] The property " + property + " has to be an Integer value");
					}
					elementInformation.getControlling().add(s);
				}
			}
		}
	}), 
	RECIPE_BUY_RESOURCE("RecipeBuyResource", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			final NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); ++i) {
				final Node item;
				if ((item = childNodes.item(i)).getNodeType() == 1) {
					if (!item.getNodeName().equals("Element")) {
						throw new ElementParserException("All child nodes of " + node.getNodeName() + " have to be \"Element\" but is " + item.getNodeName() + " (" + node.getParentNode().getNodeName() + ")");
					}
					final String property;
					if ((property = ElementParser.properties.getProperty(item.getTextContent())) == null) {
						throw new ElementParserException("The value of " + item.getTextContent() + " has not been found");
					}
					short s;
					try {
						s = (short)Integer.parseInt(property);
					}
					catch (NumberFormatException ex) {
						throw new ElementParserException("The property " + property + " has to be an Integer value");
					}
					elementInformation.getRecipeBuyResources().add(s);
				}
			}
		}
	}), 
	ARMOR_VALUE("ArmorValue", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.setArmorValue(parseFloat(node));
		}
	}), 
	NAME("Name", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
		}
	}), 
	BUILD_ICON("BuildIcon", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
		}
	}), 
	FULL_NAME("FullName", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.setFullName(node.getTextContent());
		}
	}), 
	PRICE("Price", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.setPrice(ElemType.parseInt(node));
			if (elementInformation.getPrice(false) < 0L) {
				throw new ElementParserException("Price for " + node.getParentNode().getNodeName() + " has to be greater or equal zero");
			}
		}
	}), 
	DESCRIPTION("Description", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.setDescription(node.getTextContent().replaceAll("\\r\\n|\\r|\\n", "").replaceAll("\\\\n", "\n").replaceAll("\\\\r", "\r").replaceAll("\\\\t", "\t").replaceAll("\\r", "\r").replaceAll("\r", "").split("-Struct")[0]);
		}
	}), 
	BLOCK_RESOURCE_TYPE("BlockResourceType", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.blockResourceType = ElemType.parseInt(node);
		}
	}), 
	PRODUCED_IN_FACTORY("ProducedInFactory", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.setProducedInFactory(ElemType.parseInt(node));
		}
	}), 
	BASIC_RESOURCE_FACTORY("BasicResourceFactory", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			if (node.getTextContent().trim().length() != 0) {
				final String property;
				if ((property = ElementParser.properties.getProperty(node.getTextContent())) == null) {
					elementInformation.setBasicResourceFactory(parseShort(node));
					return;
				}
				short basicResourceFactory;
				try {
					basicResourceFactory = (short)Integer.parseInt(property);
				}
				catch (NumberFormatException ex) {
					throw new ElementParserException("The property " + property + " has to be an Integer value");
				}
				elementInformation.setBasicResourceFactory(basicResourceFactory);
			}
		}
	}), 
	FACTORY_BAKE_TIME("FactoryBakeTime", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.setFactoryBakeTime(parseFloat(node));
		}
	}), 
	INVENTORY_GROUP("InventoryGroup", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.setInventoryGroup(node.getTextContent().toLowerCase(Locale.ENGLISH));
		}
	}), 
	FACTORY("Factory", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			final ObjectArrayList list = new ObjectArrayList();
			final ObjectArrayList list2 = new ObjectArrayList();
			final BlockFactory factory = new BlockFactory();
			elementInformation.setFactory(factory);
			if (node.getTextContent().toLowerCase(Locale.ENGLISH).equals("input")) {
				return;
			}
			final NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); ++i) {
				final Node item;
				if ((item = childNodes.item(i)).getNodeType() == 1) {
					if (!item.getNodeName().toLowerCase(Locale.ENGLISH).equals("product")) {
						throw new ElementParserException("All child nodes of " + item.getNodeName() + " have to be \"product\" but is " + item.getNodeName() + " (" + node.getParentNode().getNodeName() + ")");
					}
					final NodeList childNodes2 = item.getChildNodes();
					FactoryResource[] resource = null;
					FactoryResource[] resource2 = null;
					for (int j = 0; j < childNodes2.getLength(); ++j) {
						final Node item2;
						if ((item2 = childNodes2.item(j)).getNodeType() == 1) {
							if (!item2.getNodeName().toLowerCase(Locale.ENGLISH).equals("output") && !item2.getNodeName().toLowerCase(Locale.ENGLISH).equals("input")) {
								throw new ElementParserException("All child nodes of " + node.getNodeName() + " have to be \"output\" or \"input\" but is " + item2.getNodeName() + " (" + node.getParentNode().getNodeName() + ")");
							}
							if (item2.getNodeName().toLowerCase(Locale.ENGLISH).equals("input")) {
								resource = ElemType.parseResource(item2);
							}
							if (item2.getNodeName().toLowerCase(Locale.ENGLISH).equals("output")) {
								resource2 = ElemType.parseResource(item2);
							}
						}
					}
					if (resource == null) {
						throw new ElementParserException("No input defined for " + node.getNodeName() + " in (" + node.getParentNode().getNodeName() + ")");
					}
					if (resource2 == null) {
						throw new ElementParserException("No output defined for " + node.getNodeName() + " in (" + node.getParentNode().getNodeName() + ")");
					}
					((List<FactoryResource[]>)list).add(resource);
					((List<FactoryResource[]>)list2).add(resource2);
				}
			}
			if (((List)list).size() != ((List)list2).size()) {
				throw new ElementParserException("Factory Parsing failed for " + node.getNodeName() + " in (" + node.getParentNode().getNodeName() + ")");
			}
			factory.input = new FactoryResource[((List)list).size()][];
			factory.output = new FactoryResource[((List)list2).size()][];
			for (int k = 0; k < factory.input.length; ++k) {
				factory.input[k] = ((List<FactoryResource[]>)list).get(k);
				factory.output[k] = ((List<FactoryResource[]>)list2).get(k);
			}
			if (((List)list).size() == 0 && ((List)list2).size() == 0) {
				elementInformation.setFactory(null);
			}
		}
	}), 
	ANIMATED("Animated", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.setAnimated(parseBoolean(node));
		}
	}), 
	STRUCTURE_HP("StructureHPContribution", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.structureHP = ElemType.parseInt(node);
			if (elementInformation.structureHP < 0) {
				throw new ElementParserException("StructureHP for " + node.getParentNode().getNodeName() + " has to be positive");
			}
		}
	}), 
	TRANSPARENCY("Transparency", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.setBlended(parseBoolean(node));
		}
	}), 
	IN_SHOP("InShop", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.setShoppable(parseBoolean(node));
			elementInformation.setInRecipe(elementInformation.isShoppable());
		}
	}), 
	ORIENTATION("Orientation", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.setOrientatable(parseBoolean(node));
		}
	}), 
	BLOCK_COMPUTER_REFERENCE("BlockComputerReference", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.computerType = ElemType.parseInt(node);
		}
	}), 
	SLAB("Slab", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.slab = ElemType.parseInt(node);
		}
	}), 
	SLAB_IDS("SlabIds", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			if (node.getTextContent() != null && node.getTextContent().trim().length() > 0) {
				try {
					final String[] split = node.getTextContent().split(",");
					final short[] slabIds = { 0, 0, 0 };
					for (int i = 0; i < split.length; ++i) {
						slabIds[i] = Short.parseShort(split[i].trim());
					}
					assert slabIds[0] >= 0;
					elementInformation.slabIds = slabIds;
				}
				catch (NumberFormatException ex) {
					ex.printStackTrace();
					throw new ElementParserException(ElementParser.currentName + ": The value of " + node.getNodeName() + " has to be an Integer value for " + node.getParentNode().getNodeName());
				}
			}
		}
	}), 
	STYLE_IDS("StyleIds", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			if (node.getTextContent() != null && node.getTextContent().trim().length() > 0) {
				try {
					final String[] split;
					final short[] styleIds = new short[(split = node.getTextContent().split(",")).length];
					for (int i = 0; i < split.length; ++i) {
						styleIds[i] = Short.parseShort(split[i].trim());
					}
					assert styleIds[0] >= 0;
					elementInformation.styleIds = styleIds;
				}
				catch (NumberFormatException ex) {
					ex.printStackTrace();
					throw new ElementParserException(ElementParser.currentName + ": The value of " + node.getNodeName() + " has to be an Integer value for " + node.getParentNode().getNodeName());
				}
			}
		}
	}), 
	WILDCARD_IDS("WildcardIds", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			final short[] access$300;
			if ((access$300 = parseShortArray(ElementParser.currentName, node, elementInformation)) != null) {
				assert access$300[0] >= 0;
				elementInformation.wildcardIds = access$300;
			}
		}
	}), 
	SOURCE_REFERENCE("SourceReference", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.setSourceReference(ElemType.parseInt(node));
		}
	}), 
	GENERAL_CHAMBER("GeneralChamber", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.chamberGeneral = parseBoolean(node);
		}
	}), 
	EDIT_REACTOR("Edit Reactor", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
		}
	}), 
	CHAMBER_CAPACITY("ChamberCapacity", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.chamberCapacity = parseFloat(node);
		}
	}), 
	CHAMBER_ROOT("ChamberRoot", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.chamberRoot = ElemType.parseInt(node);
		}
	}), 
	CHAMBER_PARENT("ChamberParent", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.chamberParent = ElemType.parseInt(node);
		}
	}), 
	CHAMBER_UPGRADES_TO("ChamberUpgradesTo", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.chamberUpgradesTo = ElemType.parseInt(node);
		}
	}), 
	CHAMBER_PREREQUISITES("ChamberPrerequisites", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			final short[] access$300;
			if ((access$300 = parseShortArray(ElementParser.currentName, node, elementInformation)) != null) {
				assert access$300[0] >= 0;
				short[] array;
				for (int length = (array = access$300).length, i = 0; i < length; ++i) {
					elementInformation.chamberPrerequisites.add(array[i]);
				}
			}
		}
	}), 
	CHAMBER_MUTUALLY_EXCLUSIVE("ChamberMutuallyExclusive", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			final short[] access$300;
			if ((access$300 = parseShortArray(ElementParser.currentName, node, elementInformation)) != null) {
				assert access$300[0] >= 0;
				short[] array;
				for (int length = (array = access$300).length, i = 0; i < length; ++i) {
					elementInformation.chamberMutuallyExclusive.add(array[i]);
				}
			}
		}
	}), 
	CHAMBER_CHILDREN("ChamberChildren", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			final short[] access$300;
			if ((access$300 = parseShortArray(ElementParser.currentName, node, elementInformation)) != null) {
				assert access$300[0] >= 0;
				short[] array;
				for (int length = (array = access$300).length, i = 0; i < length; ++i) {
					elementInformation.chamberChildren.add(array[i]);
				}
			}
		}
	}), 
	CHAMBER_CONFIG_GROUPS("ChamberConfigGroups", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.chamberConfigGroupsLowerCase.clear();
			final Iterator<String> iterator = parseList(node, "Element").iterator();
			while (iterator.hasNext()) {
				elementInformation.chamberConfigGroupsLowerCase.add(iterator.next().toLowerCase(Locale.ENGLISH));
			}
		}
	}), 
	CHAMBER_APPLIES_TO("ChamberAppliesTo", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.chamberAppliesTo = ElemType.parseInt(node);
		}
	}), 
	REACTOR_HP("ReactorHp", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.reactorHp = ElemType.parseInt(node);
		}
	}), 
	REACTOR_GENERAL_ICON_INDEX("ReactorGeneralIconIndex", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.reactorGeneralIconIndex = ElemType.parseInt(node);
		}
	}), 
	ENTERABLE("Enterable", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.setEnterable(parseBoolean(node));
		}
	}), 
	MASS("Mass", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.mass = parseFloat(node);
		}
	}), 
	HITPOINTS("Hitpoints", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			final int int1;
			if ((int1 = ElemType.parseInt(node)) <= 0) {
				try {
					throw new ElementParserException("Hitpoints for " + elementInformation.getName() + ": " + node.getParentNode().getNodeName() + " has to be more than 0");
				}
				catch (ElementParserException ex) {
					ex.printStackTrace();
					elementInformation.setMaxHitPointsE(100);
					return;
				}
			}
			elementInformation.setMaxHitPointsE(int1);
		}
	}), 
	PLACABLE("Placable", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.setPlacable(parseBoolean(node));
		}
	}), 
	IN_RECIPE("InRecipe", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.setInRecipe(parseBoolean(node));
		}
	}), 
	CAN_ACTIVATE("CanActivate", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.setCanActivate(parseBoolean(node));
		}
	}), 
	INDIVIDUAL_SIDES("IndividualSides", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.setIndividualSides(ElemType.parseInt(node));
			if (elementInformation.getIndividualSides() != 1 && elementInformation.getIndividualSides() != 3 && elementInformation.getIndividualSides() != 6) {
				throw new ElementParserException("Individual Sides for " + node.getParentNode().getNodeName() + " has to be either 1 (default), 3, or 6, but was: " + elementInformation.getIndividualSides());
			}
		}
	}), 
	SIDE_TEXTURE_POINT_TO_ORIENTATION("SideTexturesPointToOrientation", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.sideTexturesPointToOrientation = parseBoolean(node);
		}
	}), 
	HAS_ACTIVE_TEXTURE("HasActivationTexture", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.setHasActivationTexure(parseBoolean(node));
		}
	}), 
	MAIN_COMBINATION_CONTROLLER("MainCombinationController", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.setMainCombinationController(parseBoolean(node));
		}
	}), 
	SUPPORT_COMBINATION_CONTROLLER("SupportCombinationController", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.setSupportCombinationController(parseBoolean(node));
		}
	}), 
	EFFECT_COMBINATION_CONTROLLER("EffectCombinationController", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.setEffectCombinationController(parseBoolean(node));
		}
	}), 
	BEACON("Beacon", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.beacon = parseBoolean(node);
		}
	}), 
	PHYSICAL("Physical", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.setPhysical(parseBoolean(node));
		}
	}), 
	BLOCK_STYLE("BlockStyle", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.setBlockStyle(ElemType.parseInt(node));
		}
	}), 
	LIGHT_SOURCE("LightSource", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.setLightSource(parseBoolean(node));
		}
	}), 
	DOOR("Door", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.setDoor(parseBoolean(node));
		}
	}), 
	SENSOR_INPUT("SensorInput", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.sensorInput = parseBoolean(node);
		}
	}), 
	DEPRECATED("Deprecated", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.setDeprecated(parseBoolean(node));
		}
	}), 
	RESOURCE_INJECTION("ResourceInjection", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.resourceInjection = ElementInformation.ResourceInjectionType.values()[ElemType.parseInt(node)];
		}
	}), 
	LIGHT_SOURCE_COLOR("LightSourceColor", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.getLightSourceColor().set((Tuple4f)parseVector4f(node));
		}
	}), 
	EXTENDED_TEXTURE_4x4("ExtendedTexture4x4", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.extendedTexture = parseBoolean(node);
		}
	}), 
	ONLY_DRAW_IN_BUILD_MODE("OnlyDrawnInBuildMode", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.setDrawOnlyInBuildMode(parseBoolean(node));
		}
	}), 
	LOD_COLLISION_PHYSICAL("LodCollisionPhysical", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.lodCollisionPhysical = parseBoolean(node);
		}
	}), 
	LOD_USE_DETAIL_COLLISION("UseDetailedCollisionForAstronautMode", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.lodUseDetailCollision = parseBoolean(node);
		}
	}), 
	CUBE_CUBE_COLLISION("CubeCubeCollision", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.cubeCubeCollision = parseBoolean(node);
		}
	}), 
	LOD_DETAIL_COLLISION("DetailedCollisionForAstronautMode", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.lodDetailCollision.parse(node);
		}
	}), 
	LOD_COLLISION("CollisionDefault", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.lodCollision.parse(node);
		}
	}), 
	LOD_SHAPE("LodShape", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.lodShapeString = node.getTextContent().trim();
		}
	}), 
	LOD_SHAPE_ACTIVE("LodShapeSwitchStyleActive", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.lodShapeStringActive = node.getTextContent().trim();
		}
	}), 
	LOD_SHAPE_FROM_FAR("LodShapeFromFar", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.lodShapeStyle = ElemType.parseInt(node);
		}
	}), 
	LOD_ACTIVATION_ANIMATION_STYLE("LodActivationAnimationStyle", (NodeSetting)new NodeSettingWithDependency() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.lodActivationAnimationStyle = ElemType.parseInt(node);
		}
		
		@Override
		public final void onSwitch(final ElementInformationOption elementInformationOption, final ElementInformation elementInformation, final Element element) {
			elementInformationOption.editPanel.onSwitchActivationAnimationStyle(elementInformation.lodActivationAnimationStyle);
		}
	}), 
	LOW_HP_SETTING("LowHpSetting", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.lowHpSetting = parseBoolean(node);
		}
	}), 
	OLD_HITPOINTS("OldHitpoints", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.setHpOldByte(parseShort(node));
		}
	}), 
	VOLUME("Volume", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.volume = parseFloat(node);
		}
	}), 
	EXPLOSION_ABSOBTION("ExplosionAbsorbtion", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.setExplosionAbsorbtion(parseFloat(node));
		}
	}), 
	CHAMBER_PERMISSION("ChamberPermission", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.chamberPermission = ElemType.parseInt(node);
		}
	}), 
	ID("ID", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
		}
	}), 
	TEXTURE("Texture", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
		}
	}), 
	EFFECT_ARMOR("EffectArmor", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			final NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); ++i) {
				final Node item;
				if ((item = childNodes.item(i)).getNodeType() == 1) {
					final String lowerCase = item.getNodeName().toLowerCase(Locale.ENGLISH);
					InterEffectHandler.InterEffectType[] values;
					for (int length = (values = InterEffectHandler.InterEffectType.values()).length, j = 0; j < length; ++j) {
						final InterEffectHandler.InterEffectType interEffectType;
						if ((interEffectType = values[j]).id.toLowerCase(Locale.ENGLISH).equals(lowerCase)) {
							try {
								elementInformation.effectArmor.setStrength(interEffectType, Float.parseFloat(item.getTextContent()));
							}
							catch (NumberFormatException ex) {
								ex.printStackTrace();
								throw new ElementParserException("value has to be floating point. " + item.getNodeName() + "; " + node.getNodeName() + "; " + node.getParentNode().getNodeName() + "; " + ElementParser.currentName);
							}
						}
					}
				}
			}
		}
	}),
	//#XXX: this is a direct copy/paste of the SYSTEM_BLOCK parser for the paper hull fix
	//see ProjectileHandlerSegmentController or ElementInformation for more info on this
	ARMOR_BLOCK("ArmorBlock", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.armorBlock = parseBoolean(node);
		}
	}),
	//#XXX:
	SYSTEM_BLOCK("SystemBlock", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.systemBlock = parseBoolean(node);
		}
	}), 
	DRAW_LOGIC_CONNECTION("DrawLogicConnection", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.drawLogicConnection = parseBoolean(node);
		}
	}), 
	LOGIC_BLOCK("LogicBlock", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.signal = parseBoolean(node);
		}
	}), 
	LOGIC_SIGNALED_BY_RAIL("LogicSignaledByRail", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.signaledByRail = parseBoolean(node);
		}
	}), 
	LOGIC_BUTTON("LogicBlockButton", (NodeSetting)new NodeSetting() {
		@Override
		public final void parse(final Node node, final ElementInformation elementInformation) throws ElementParserException {
			elementInformation.button = parseBoolean(node);
		}
	});
	
	public final NodeSetting fac;
	public final String tag;
	
	private static short[] parseShortArray(final String str, final Node node, final ElementInformation elementInformation) {
		if (node.getTextContent() != null && node.getTextContent().trim().length() > 0) {
			try {
				final String replaceAll;
				if ((replaceAll = node.getTextContent().replaceAll("\\{", "").replaceAll("\\}", "")).length() > 0) {
					final String[] split;
					final short[] array = new short[(split = replaceAll.split(",")).length];
					for (int i = 0; i < split.length; ++i) {
						array[i] = Short.parseShort(split[i].trim());
					}
					return array;
				}
				return null;
			}
			catch (NumberFormatException ex) {
				ex.printStackTrace();
				throw new ElementParserException(str + ": The value of " + node.getNodeName() + " has to be an Integer value for " + node.getParentNode().getNodeName());
			}
		}
		return null;
	}
	
	private static List<String> parseList(final Node node, final String s) throws ElementParserException {
		final ObjectArrayList list = new ObjectArrayList();
		final NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			final Node item;
			if ((item = childNodes.item(i)).getNodeType() == 1) {
				if (!item.getNodeName().equals(s)) {
					throw new ElementParserException("All child nodes of " + node.getNodeName() + " have to be \"" + s + "\" but is " + item.getNodeName() + " (" + node.getParentNode().getNodeName() + ")");
				}
				((List<String>)list).add(item.getTextContent());
			}
		}
		return (List<String>)list;
	}
	
	private static boolean parseBoolean(final Node node) throws ElementParserException {
		try {
			return Boolean.parseBoolean(node.getTextContent().trim());
		}
		catch (NumberFormatException ex) {
			ex.printStackTrace();
			throw new ElementParserException("The value of " + node.getNodeName() + " has to be an Boolean value for " + node.getParentNode().getNodeName() + " but was " + node.getTextContent());
		}
	}
	
	private static short parseShort(final Node node) throws ElementParserException {
		try {
			return Short.parseShort(node.getTextContent().trim());
		}
		catch (NumberFormatException ex) {
			ex.printStackTrace();
			throw new ElementParserException("The value of " + node.getNodeName() + " has to be a Short value for " + node.getParentNode().getNodeName());
		}
	}
	
	public static int parseInt(final Node node) throws ElementParserException {
		try {
			return Integer.parseInt(node.getTextContent().trim());
		}
		catch (NumberFormatException ex) {
			ex.printStackTrace();
			throw new ElementParserException("The value of " + node.getNodeName() + " has to be an Integer value for " + node.getParentNode().getNodeName() + " but was: " + node.getTextContent());
		}
	}
	
	private static Vector4f parseVector4f(final Node node) throws ElementParserException {
		final Vector4f vector4f = new Vector4f();
		final String[] split;
		if ((split = node.getTextContent().trim().split(",")).length != 4) {
			throw new ElementParserException("The value of " + node.getNodeName() + " has to be 4 Float values seperated by commas");
		}
		try {
			vector4f.set(Float.parseFloat(split[0].trim()), Float.parseFloat(split[1].trim()), Float.parseFloat(split[2].trim()), Float.parseFloat(split[3].trim()));
		}
		catch (NumberFormatException ex) {
			throw new ElementParserException("The value of " + node.getNodeName() + " has to be a Float value");
		}
		return vector4f;
	}
	
	public static FactoryResource[] parseResource(final Node node) {
		final ArrayList<FactoryResource> list = new ArrayList<FactoryResource>();
		final NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			final Node item;
			if ((item = childNodes.item(i)).getNodeType() == 1) {
				if (!item.getNodeName().toLowerCase(Locale.ENGLISH).equals("item")) {
					throw new ElementParserException("All child nodes of " + node.getNodeName() + " have to be \"item\" but is " + node.getParentNode().getNodeName() + " (" + node.getParentNode().getParentNode().getNodeName() + ")");
				}
				final NamedNodeMap attributes;
				if ((attributes = item.getAttributes()) != null && attributes.getLength() != 1) {
					throw new ElementParserException("Element has wrong attribute count (" + attributes.getLength() + ", but should be 4)");
				}
				final Node type = parseType(item, attributes, "count");
				int int1;
				try {
					int1 = Integer.parseInt(type.getNodeValue());
				}
				catch (NumberFormatException ex) {
					throw new ElementParserException("Cant parse count in " + item.getNodeName() + ", in " + node.getParentNode().getNodeName() + " (" + node.getParentNode().getParentNode().getNodeName() + ")");
				}
				final String property;
				if ((property = ElementParser.properties.getProperty(item.getTextContent())) == null) {
					throw new ElementParserException(node.getParentNode().getParentNode().getParentNode().getNodeName() + " -> " + node.getParentNode().getNodeName() + " -> " + node.getNodeName() + " The value of \"" + item.getTextContent() + "\" has not been found");
				}
				short n;
				try {
					n = (short)Integer.parseInt(property);
				}
				catch (NumberFormatException ex2) {
					throw new ElementParserException("The property " + property + " has to be an Integer value");
				}
				list.add(new FactoryResource(int1, n));
			}
		}
		final FactoryResource[] a = new FactoryResource[list.size()];
		list.toArray(a);
		return a;
	}
	
	private static float parseFloat(final Node node) throws ElementParserException {
		try {
			return Float.parseFloat(node.getTextContent().trim());
		}
		catch (NumberFormatException ex) {
			ex.printStackTrace();
			throw new ElementParserException("The value of " + node.getNodeName() + " has to be a Float value for " + node.getParentNode().getNodeName());
		}
	}
	
	public static Node parseType(final Node node, final NamedNodeMap namedNodeMap, final String str) throws ElementParserException {
		final Node namedItem;
		if ((namedItem = namedNodeMap.getNamedItem(str)) == null) {
			throw new ElementParserException("Obligatory attribute \"" + str + "\" not found in " + node.getNodeName());
		}
		return namedItem;
	}
	
	private ElemType(final String tag, final NodeSetting fac) {
		this.tag = tag;
		this.fac = fac;
	}
}
