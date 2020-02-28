// 
// Decompiled by Procyon v0.5.36
// 

package org.schema.game.common.data.element;

import com.mxgraph.layout.mxIGraphLayout;
import org.schema.game.client.view.tools.SingleBlockDrawer;
import org.schema.game.client.view.cubes.shapes.orientcube.Oriencube;
import org.schema.game.client.view.cubes.shapes.BlockShapeAlgorithm;
import org.w3c.dom.Attr;
import org.w3c.dom.NodeList;
import com.bulletphysics.collision.shapes.CompoundShape;
import org.schema.schine.graphicsengine.forms.AbstractSceneNode;
import javax.vecmath.Matrix3f;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.collision.shapes.CollisionShape;
import org.schema.game.common.data.physics.ConvexHullShapeExt;
import org.schema.schine.graphicsengine.forms.MeshGroup;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;
import org.schema.game.common.data.blockeffects.config.ConfigGroup;
import org.schema.game.common.data.blockeffects.config.ConfigPool;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import org.schema.game.common.facedit.AddElementEntryDialog;
import org.schema.schine.graphicsengine.forms.Mesh;
import org.schema.schine.graphicsengine.core.Controller;
import java.util.Arrays;
import org.schema.game.common.data.element.meta.RecipeInterface;
import org.schema.game.common.controller.elements.ShipManagerContainer;
import org.schema.schine.network.StateInterface;
import org.schema.game.common.controller.Ship;
import org.schema.schine.input.KeyboardMappings;
import org.schema.game.client.data.GameClientState;
import org.schema.schine.graphicsengine.core.settings.EngineSettings;
import org.schema.game.common.controller.damage.effects.InterEffectHandler;
import java.util.Collection;
import java.util.Locale;
import javax.vecmath.Vector3f;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.schema.game.server.data.ServerConfig;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.util.mxConstants;
import java.awt.Container;
import java.awt.Component;
import java.awt.GridBagConstraints;
import com.mxgraph.swing.mxGraphComponent;
import java.awt.LayoutManager;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import com.mxgraph.view.mxGraph;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import org.schema.schine.common.language.Lng;
import org.schema.schine.graphicsengine.forms.gui.graph.GUIGraph;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.game.client.view.gui.GUIBlockSprite;
import org.schema.schine.graphicsengine.forms.gui.GUITextOverlay;
import org.schema.schine.graphicsengine.forms.font.FontLibrary;
import org.schema.schine.graphicsengine.forms.gui.GUIColoredRectangle;
import org.schema.schine.graphicsengine.forms.gui.graph.GUIGraphElement;
import org.schema.schine.input.InputState;
import org.schema.common.util.StringTools;
import org.schema.game.common.data.physics.octree.ArrayOctree;
import org.schema.game.common.data.world.SegmentData;
import org.schema.game.common.data.SegmentPiece;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.schema.game.common.controller.ElementCountMap;
import org.schema.game.client.view.gui.shop.shopnew.ShopItemElement;
import javax.vecmath.Vector4f;
import org.schema.game.client.view.cubes.shapes.BlockStyle;
import org.schema.game.common.controller.damage.effects.InterEffectSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.List;
import org.schema.game.common.data.element.annotation.ElemType;
import org.schema.game.common.data.element.annotation.Element;

public class ElementInformation implements Comparable<ElementInformation>
{
	public static final String[] rDesc;
	public static final int FAC_NONE = 0;
	public static final int FAC_CAPSULE = 1;
	public static final int FAC_MICRO = 2;
	public static final int FAC_BASIC = 3;
	public static final int FAC_STANDARD = 4;
	public static final int FAC_ADVANCED = 5;
	public static final int RT_ORE = 0;
	public static final int RT_PLANT = 1;
	public static final int RT_BASIC = 2;
	public static final int RT_CUBATOM_SPLITTABLE = 3;
	public static final int RT_MANUFACTORY = 4;
	public static final int RT_ADVANCED = 5;
	public static final int RT_CAPSULE = 6;
	public static final int CHAMBER_APPLIES_TO_SELF = 0;
	public static final int CHAMBER_APPLIES_TO_SECTOR = 1;
	@Element(writeAsTag = false, canBulkChange = false, parser = ElemType.ID, cat = EIC.BASICS, order = -1)
	public final short id;
	@Element(writeAsTag = false, canBulkChange = false, parser = ElemType.TEXTURE, cat = EIC.TEXTURES, order = 0)
	private short[] textureId;
	@Element(consistence = true, parser = ElemType.CONSISTENCE, cat = EIC.CRAFTING_ECONOMY, order = 0)
	public final List<FactoryResource> consistence;
	@Element(cubatomConsistence = true, parser = ElemType.CUBATON_CONSISTENCE, cat = EIC.DEPRECATED, order = 0)
	public final List<FactoryResource> cubatomConsistence;
	@Element(collectionElementTag = "Element", elementSet = true, collectionType = "blockTypes", parser = ElemType.CONTROLLED_BY, cat = EIC.FEATURES, order = 10)
	public final ShortSet controlledBy;
	@Element(collectionElementTag = "Element", elementSet = true, collectionType = "blockTypes", parser = ElemType.CONTROLLING, cat = EIC.FEATURES, order = 11)
	public final ShortSet controlling;
	@Element(collectionElementTag = "Element", elementSet = true, collectionType = "blockTypes", parser = ElemType.RECIPE_BUY_RESOURCE, cat = EIC.CRAFTING_ECONOMY, order = 4)
	public final ShortList recipeBuyResources;
	public final ObjectOpenHashSet<String> parsed;
	private final int[] textureLayerMapping;
	private final int[] textureIndexLocalMapping;
	private final int[] textureLayerMappingActive;
	private final int[] textureIndexLocalMappingActive;
	@Element(from = 0, to = 10000000, parser = ElemType.ARMOR_VALUE, cat = EIC.HP_ARMOR, order = 6)
	public float armorValue;
	@Element(writeAsTag = false, canBulkChange = false, parser = ElemType.NAME, cat = EIC.BASICS, order = 0)
	public String name;
	public ElementCategory type;
	@Element(parser = ElemType.EFFECT_ARMOR, canBulkChange = true, cat = EIC.HP_ARMOR, order = 7)
	public InterEffectSet effectArmor;
	@Element(writeAsTag = false, canBulkChange = false, parser = ElemType.BUILD_ICON, cat = EIC.BASICS, order = 4)
	public int buildIconNum;
	@Element(canBulkChange = false, parser = ElemType.FULL_NAME, cat = EIC.BASICS, order = 1)
	public String fullName;
	@Element(from = 0, to = Integer.MAX_VALUE, parser = ElemType.PRICE, cat = EIC.CRAFTING_ECONOMY, order = 1)
	public long price;
	@Element(textArea = true, parser = ElemType.DESCRIPTION, cat = EIC.BASICS, order = 2)
	public String description;
	@Element(states = { "0", "1", "2", "3", "4", "5", "6" }, stateDescs = { "ore", "plant", "basic", "Cubatom-Splittable", "manufactory", "advanced", "capsule" }, parser = ElemType.BLOCK_RESOURCE_TYPE, cat = EIC.CRAFTING_ECONOMY, order = 5)
	public int blockResourceType;
	@Element(states = { "0", "1", "2", "3", "4", "5" }, stateDescs = { "none", "capsule refinery", "micro assembler", "basic factory", "standard factory", "advanced factory" }, parser = ElemType.PRODUCED_IN_FACTORY, cat = EIC.CRAFTING_ECONOMY, order = 6)
	public int producedInFactory;
	@Element(type = true, parser = ElemType.BASIC_RESOURCE_FACTORY, cat = EIC.CRAFTING_ECONOMY, order = 7)
	public short basicResourceFactory;
	@Element(from = 0, to = 1000000, parser = ElemType.FACTORY_BAKE_TIME, cat = EIC.CRAFTING_ECONOMY, order = 8)
	public float factoryBakeTime;
	@Element(inventoryGroup = true, parser = ElemType.INVENTORY_GROUP, cat = EIC.BASICS, order = 3)
	public String inventoryGroup;
	@Element(factory = true, parser = ElemType.FACTORY, cat = EIC.CRAFTING_ECONOMY, order = 9)
	public BlockFactory factory;
	@Element(parser = ElemType.ANIMATED, cat = EIC.CRAFTING_ECONOMY, order = 7)
	public boolean animated;
	@Element(from = 0, to = Integer.MAX_VALUE, parser = ElemType.STRUCTURE_HP, cat = EIC.FEATURES, order = 0)
	public int structureHP;
	@Element(parser = ElemType.TRANSPARENCY, cat = EIC.FEATURES, order = 1)
	public boolean blended;
	@Element(parser = ElemType.IN_SHOP, cat = EIC.CRAFTING_ECONOMY, order = 2)
	public boolean shoppable;
	@Element(parser = ElemType.ORIENTATION, cat = EIC.BASICS, order = 6)
	public boolean orientatable;
	@Element(selectBlock = true, parser = ElemType.BLOCK_COMPUTER_REFERENCE, cat = EIC.BASICS, order = 7)
	public int computerType;
	public static final String[] slabStrings;
	@Element(states = { "0", "1", "2", "3" }, stateDescs = { "full block", "3/4 block", "1/2 block", "1/4 block" }, parser = ElemType.SLAB, cat = EIC.BASICS, order = 8)
	public int slab;
	@Element(canBulkChange = false, parser = ElemType.SLAB_IDS, cat = EIC.BASICS, order = 9)
	public short[] slabIds;
	@Element(canBulkChange = false, parser = ElemType.STYLE_IDS, cat = EIC.BASICS, order = 10)
	public short[] styleIds;
	@Element(canBulkChange = false, parser = ElemType.WILDCARD_IDS, cat = EIC.BASICS, order = 11)
	public short[] wildcardIds;
	public short[] blocktypeIds;
	@Element(canBulkChange = false, editable = false, parser = ElemType.SOURCE_REFERENCE, cat = EIC.BASICS, order = 12)
	public int sourceReference;
	@Element(parser = ElemType.GENERAL_CHAMBER, cat = EIC.POWER_REACTOR, order = 0)
	public boolean chamberGeneral;
	@Element(writeAsTag = false, parser = ElemType.EDIT_REACTOR, cat = EIC.POWER_REACTOR, order = 1)
	public ElementReactorChange change;
	@Element(parser = ElemType.CHAMBER_CAPACITY, cat = EIC.POWER_REACTOR, order = 2)
	public float chamberCapacity;
	@Element(editable = false, selectBlock = true, parser = ElemType.CHAMBER_ROOT, cat = EIC.POWER_REACTOR, order = 3)
	public int chamberRoot;
	@Element(editable = false, selectBlock = true, parser = ElemType.CHAMBER_PARENT, cat = EIC.POWER_REACTOR, order = 4)
	public int chamberParent;
	@Element(editable = false, selectBlock = true, parser = ElemType.CHAMBER_UPGRADES_TO, cat = EIC.POWER_REACTOR, order = 5)
	public int chamberUpgradesTo;
	public static final int CHAMBER_PERMISSION_ANY = 0;
	public static final int CHAMBER_PERMISSION_SHIP = 1;
	public static final int CHAMBER_PERMISSION_STATION = 2;
	public static final int CHAMBER_PERMISSION_PLANET = 4;
	@Element(states = { "0", "1", "6", "2", "4" }, stateDescs = { "Any", "Ship Only", "Station/Planet Only", "Station Only", "Planet Only" }, parser = ElemType.CHAMBER_PERMISSION, cat = EIC.POWER_REACTOR, order = 6)
	public int chamberPermission;
	@Element(editable = false, canBulkChange = false, shortSet = true, parser = ElemType.CHAMBER_PREREQUISITES, cat = EIC.POWER_REACTOR, order = 7)
	public final ShortSet chamberPrerequisites;
	@Element(editable = true, canBulkChange = true, shortSet = true, parser = ElemType.CHAMBER_MUTUALLY_EXCLUSIVE, cat = EIC.POWER_REACTOR, order = 8)
	public final ShortSet chamberMutuallyExclusive;
	@Element(canBulkChange = false, editable = false, shortSet = true, parser = ElemType.CHAMBER_CHILDREN, cat = EIC.POWER_REACTOR, order = 9)
	public final ShortSet chamberChildren;
	@Element(collectionElementTag = "Element", configGroupSet = true, collectionType = "String", stringSet = true, parser = ElemType.CHAMBER_CONFIG_GROUPS, cat = EIC.POWER_REACTOR, order = 10)
	public List<String> chamberConfigGroupsLowerCase;
	@Element(states = { "0", "1" }, stateDescs = { "self", "sector" }, parser = ElemType.CHAMBER_APPLIES_TO, cat = EIC.POWER_REACTOR, order = 11)
	public int chamberAppliesTo;
	@Element(editable = true, canBulkChange = true, parser = ElemType.REACTOR_HP, cat = EIC.POWER_REACTOR, order = 12)
	public int reactorHp;
	@Element(editable = true, canBulkChange = true, parser = ElemType.REACTOR_GENERAL_ICON_INDEX, cat = EIC.POWER_REACTOR, order = 13)
	public int reactorGeneralIconIndex;
	@Element(parser = ElemType.ENTERABLE, cat = EIC.FEATURES, order = 2)
	public boolean enterable;
	@Element(parser = ElemType.MASS, cat = EIC.HP_ARMOR, order = 0)
	public float mass;
	@Element(parser = ElemType.VOLUME, cat = EIC.HP_ARMOR, order = 1)
	public float volume;
	@Element(from = 1, to = Integer.MAX_VALUE, parser = ElemType.HITPOINTS, cat = EIC.HP_ARMOR, order = 2)
	public int maxHitPointsFull;
	@Element(parser = ElemType.PLACABLE, cat = EIC.FEATURES, order = 3)
	public boolean placable;
	@Element(parser = ElemType.IN_RECIPE, cat = EIC.CRAFTING_ECONOMY, order = 3)
	public boolean inRecipe;
	@Element(parser = ElemType.CAN_ACTIVATE, cat = EIC.FEATURES, order = 4)
	public boolean canActivate;
	@Element(states = { "1", "3", "6" }, updateTextures = true, parser = ElemType.INDIVIDUAL_SIDES, cat = EIC.TEXTURES, order = 7)
	public int individualSides;
	@Element(parser = ElemType.SIDE_TEXTURE_POINT_TO_ORIENTATION, cat = EIC.TEXTURES, order = 8)
	public boolean sideTexturesPointToOrientation;
	@Element(parser = ElemType.HAS_ACTIVE_TEXTURE, cat = EIC.TEXTURES, order = 9)
	public boolean hasActivationTexure;
	@Element(parser = ElemType.MAIN_COMBINATION_CONTROLLER, cat = EIC.FEATURES, order = 12)
	public boolean mainCombinationController;
	@Element(parser = ElemType.SUPPORT_COMBINATION_CONTROLLER, cat = EIC.FEATURES, order = 13)
	public boolean supportCombinationController;
	@Element(parser = ElemType.EFFECT_COMBINATION_CONTROLLER, cat = EIC.FEATURES, order = 14)
	public boolean effectCombinationController;
	@Element(editable = true, canBulkChange = true, parser = ElemType.BEACON, cat = EIC.FEATURES, order = 15)
	public boolean beacon;
	@Element(parser = ElemType.PHYSICAL, cat = EIC.FEATURES, order = 5)
	public boolean physical;
	@Element(parser = ElemType.BLOCK_STYLE, cat = EIC.BASICS, order = 5)
	public BlockStyle blockStyle;
	@Element(parser = ElemType.LIGHT_SOURCE, cat = EIC.FEATURES, order = 8)
	public boolean lightSource;
	@Element(parser = ElemType.DOOR, cat = EIC.FEATURES, order = 6)
	public boolean door;
	@Element(parser = ElemType.SENSOR_INPUT, cat = EIC.FEATURES, order = 7)
	public boolean sensorInput;
	@Element(editable = true, canBulkChange = true, parser = ElemType.DRAW_LOGIC_CONNECTION, cat = EIC.FEATURES, order = 18)
	public boolean drawLogicConnection;
	@Element(parser = ElemType.DEPRECATED, cat = EIC.BASICS, order = 13)
	public boolean deprecated;
	public long dynamicPrice;
	@Element(parser = ElemType.RESOURCE_INJECTION, cat = EIC.CRAFTING_ECONOMY, order = 10)
	public ResourceInjectionType resourceInjection;
	@Element(from = 0, to = 100000, parser = ElemType.EXPLOSION_ABSOBTION, cat = EIC.HP_ARMOR, order = 8)
	public float explosionAbsorbtion;
	@Element(vector4f = true, parser = ElemType.LIGHT_SOURCE_COLOR, cat = EIC.TEXTURES, order = 9)
	public final Vector4f lightSourceColor;
	@Element(editable = true, canBulkChange = true, parser = ElemType.EXTENDED_TEXTURE_4x4, cat = EIC.TEXTURES, order = 10)
	public boolean extendedTexture;
	@Element(editable = true, canBulkChange = true, parser = ElemType.ONLY_DRAW_IN_BUILD_MODE, cat = EIC.BASICS, order = 14)
	public boolean drawOnlyInBuildMode;
	@Element(editable = true, canBulkChange = true, parser = ElemType.LOD_SHAPE, modelSelect = true, modelSelectFilter = "lod", cat = EIC.MODELS, order = 2)
	public String lodShapeString;
	@Element(states = { "0", "1", "2" }, stateDescs = { "None", "Switch Model", "Keyframe Animation (not implemented yet)" }, parser = ElemType.LOD_ACTIVATION_ANIMATION_STYLE, cat = EIC.MODELS, order = 3)
	public int lodActivationAnimationStyle;
	@Element(editable = true, canBulkChange = true, parser = ElemType.LOD_SHAPE_ACTIVE, modelSelect = true, modelSelectFilter = "lod", cat = EIC.MODELS, order = 4)
	public String lodShapeStringActive;
	@Element(editable = true, canBulkChange = true, parser = ElemType.LOD_COLLISION_PHYSICAL, modelSelect = true, modelSelectFilter = "lod", cat = EIC.MODELS, order = 5)
	public boolean lodCollisionPhysical;
	@Element(states = { "0", "1", "2" }, stateDescs = { "solid block", "sprite", "invisible" }, parser = ElemType.LOD_SHAPE_FROM_FAR, cat = EIC.MODELS, order = 6)
	public int lodShapeStyle;
	@Element(editable = true, canBulkChange = true, parser = ElemType.LOD_COLLISION, cat = EIC.MODELS, order = 7)
	public LodCollision lodCollision;
	@Element(editable = true, canBulkChange = true, parser = ElemType.CUBE_CUBE_COLLISION, cat = EIC.MODELS, order = 8)
	public boolean cubeCubeCollision;
	@Element(editable = true, canBulkChange = true, parser = ElemType.LOD_USE_DETAIL_COLLISION, cat = EIC.MODELS, order = 9)
	public boolean lodUseDetailCollision;
	@Element(editable = true, canBulkChange = true, parser = ElemType.LOD_DETAIL_COLLISION, cat = EIC.MODELS, order = 10)
	public LodCollision lodDetailCollision;
	@Element(editable = true, canBulkChange = true, parser = ElemType.LOW_HP_SETTING, cat = EIC.DEPRECATED, order = 0)
	public boolean lowHpSetting;
	@Element(from = 1, to = 127, editable = false, parser = ElemType.OLD_HITPOINTS, cat = EIC.DEPRECATED, order = 0)
	public short oldHitpoints;
	@Element(editable = true, canBulkChange = true, parser = ElemType.SYSTEM_BLOCK, cat = EIC.BASICS, order = 15)
	public boolean systemBlock;
	@Element(editable = true, canBulkChange = true, parser = ElemType.LOGIC_BLOCK, cat = EIC.BASICS, order = 16)
	public boolean signal;
	@Element(editable = true, canBulkChange = true, parser = ElemType.LOGIC_SIGNALED_BY_RAIL, cat = EIC.BASICS, order = 17)
	public boolean signaledByRail;
	@Element(editable = true, canBulkChange = true, parser = ElemType.LOGIC_BUTTON, cat = EIC.BASICS, order = 18)
	public boolean button;
	public int wildcardIndex;
	private FixedRecipe productionRecipe;
	private double maxHitpointsInverse;
	private double maxHitpointsByteToFull;
	private double maxHitpointsFullToByte;
	private ShopItemElement shopItemElement;
	private boolean createdTX;
	public String idName;
	private boolean specialBlock;
	private final List<FactoryResource> rawConsistence;
	private final List<FactoryResource> totalConsistence;
	private final ElementCountMap rawBlocks;
	public boolean isSourceBlockTmp;
	private static final float AB = 0.007874016f;
	
	public ElementInformation(final ElementInformation elementInformation, short id, String name) {
		this.consistence = (List<FactoryResource>)new ObjectArrayList();
		this.cubatomConsistence = (List<FactoryResource>)new ObjectArrayList();
		this.controlledBy = (ShortSet)new ShortOpenHashSet();
		this.controlling = (ShortSet)new ShortOpenHashSet();
		this.recipeBuyResources = (ShortList)new ShortArrayList();
		this.parsed = (ObjectOpenHashSet<String>)new ObjectOpenHashSet(2048);
		this.textureLayerMapping = new int[6];
		this.textureIndexLocalMapping = new int[6];
		this.textureLayerMappingActive = new int[6];
		this.textureIndexLocalMappingActive = new int[6];
		this.name = "n/a";
		this.effectArmor = new InterEffectSet();
		this.buildIconNum = 62;
		this.fullName = "";
		this.price = 100L;
		this.description = "undefined description";
		this.blockResourceType = 2;
		this.producedInFactory = 0;
		this.basicResourceFactory = 0;
		this.factoryBakeTime = 5.0f;
		this.inventoryGroup = "";
		this.structureHP = 0;
		this.shoppable = true;
		this.slab = 0;
		this.slabIds = null;
		this.styleIds = null;
		this.wildcardIds = null;
		this.sourceReference = 0;
		this.chamberCapacity = 0.0f;
		this.chamberPermission = 0;
		this.chamberPrerequisites = (ShortSet)new ShortOpenHashSet();
		this.chamberMutuallyExclusive = (ShortSet)new ShortOpenHashSet();
		this.chamberChildren = (ShortSet)new ShortOpenHashSet();
		this.chamberConfigGroupsLowerCase = (List<String>)new ObjectArrayList();
		this.chamberAppliesTo = 0;
		this.mass = 0.1f;
		this.volume = -1.0f;
		this.maxHitPointsFull = 100;
		this.placable = true;
		this.inRecipe = this.shoppable;
		this.individualSides = 1;
		this.sideTexturesPointToOrientation = false;
		this.physical = true;
		this.blockStyle = BlockStyle.NORMAL;
		this.drawLogicConnection = false;
		this.dynamicPrice = -1L;
		this.resourceInjection = ResourceInjectionType.OFF;
		this.lightSourceColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
		this.lodShapeString = "";
		this.lodActivationAnimationStyle = 0;
		this.lodShapeStringActive = "";
		this.lodCollisionPhysical = true;
		this.lodShapeStyle = 0;
		this.lodCollision = new LodCollision();
		this.cubeCubeCollision = true;
		this.lodDetailCollision = new LodCollision();
		this.createdTX = false;
		this.specialBlock = true;
		this.rawConsistence = (List<FactoryResource>)new ObjectArrayList();
		this.totalConsistence = (List<FactoryResource>)new ObjectArrayList();
		this.rawBlocks = new ElementCountMap();
		this.name = new String(name);
		this.type = elementInformation.type;
		this.setTextureId(elementInformation.textureId);
		this.id = id;
		Field[] fields = ElementInformation.class.getFields();
		for (final Field field : fields) {
			try {
				if (field.get(elementInformation) != null && !Modifier.isFinal(field.getModifiers()) && !field.getName().equals("name")) {
					field.set(this, field.get(elementInformation));
				}
			}
			catch (IllegalArgumentException ex) {
				ex.printStackTrace();
			}
			catch (IllegalAccessException ex2) {
				ex2.printStackTrace();
			}
		}
		assert elementInformation.getBlockStyle() == this.getBlockStyle();
	}
	
	public ElementInformation(final short id, final String name, final ElementCategory type, final short[] textureId) {
		this.consistence = (List<FactoryResource>)new ObjectArrayList();
		this.cubatomConsistence = (List<FactoryResource>)new ObjectArrayList();
		this.controlledBy = (ShortSet)new ShortOpenHashSet();
		this.controlling = (ShortSet)new ShortOpenHashSet();
		this.recipeBuyResources = (ShortList)new ShortArrayList();
		this.parsed = (ObjectOpenHashSet<String>)new ObjectOpenHashSet(2048);
		this.textureLayerMapping = new int[6];
		this.textureIndexLocalMapping = new int[6];
		this.textureLayerMappingActive = new int[6];
		this.textureIndexLocalMappingActive = new int[6];
		this.name = "n/a";
		this.effectArmor = new InterEffectSet();
		this.buildIconNum = 62;
		this.fullName = "";
		this.price = 100L;
		this.description = "undefined description";
		this.blockResourceType = 2;
		this.producedInFactory = 0;
		this.basicResourceFactory = 0;
		this.factoryBakeTime = 5.0f;
		this.inventoryGroup = "";
		this.structureHP = 0;
		this.shoppable = true;
		this.slab = 0;
		this.slabIds = null;
		this.styleIds = null;
		this.wildcardIds = null;
		this.sourceReference = 0;
		this.chamberCapacity = 0.0f;
		this.chamberPermission = 0;
		this.chamberPrerequisites = (ShortSet)new ShortOpenHashSet();
		this.chamberMutuallyExclusive = (ShortSet)new ShortOpenHashSet();
		this.chamberChildren = (ShortSet)new ShortOpenHashSet();
		this.chamberConfigGroupsLowerCase = (List<String>)new ObjectArrayList();
		this.chamberAppliesTo = 0;
		this.mass = 0.1f;
		this.volume = -1.0f;
		this.maxHitPointsFull = 100;
		this.placable = true;
		this.inRecipe = this.shoppable;
		this.individualSides = 1;
		this.sideTexturesPointToOrientation = false;
		this.physical = true;
		this.blockStyle = BlockStyle.NORMAL;
		this.drawLogicConnection = false;
		this.dynamicPrice = -1L;
		this.resourceInjection = ResourceInjectionType.OFF;
		this.lightSourceColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
		this.lodShapeString = "";
		this.lodActivationAnimationStyle = 0;
		this.lodShapeStringActive = "";
		this.lodCollisionPhysical = true;
		this.lodShapeStyle = 0;
		this.lodCollision = new LodCollision();
		this.cubeCubeCollision = true;
		this.lodDetailCollision = new LodCollision();
		this.createdTX = false;
		this.specialBlock = true;
		this.rawConsistence = (List<FactoryResource>)new ObjectArrayList();
		this.totalConsistence = (List<FactoryResource>)new ObjectArrayList();
		this.rawBlocks = new ElementCountMap();
		this.name = name;
		this.type = type;
		this.id = id;
		this.setTextureId(textureId);
	}
	
	public static BlockOrientation convertOrientation(final ElementInformation obj, final byte blockOrientation) {
		final BlockOrientation blockOrientation2;
		(blockOrientation2 = new BlockOrientation()).blockOrientation = blockOrientation;
		blockOrientation2.activateBlock = obj.activateOnPlacement();
		assert blockOrientation2.blockOrientation < 24;
		if (blockOrientation2.blockOrientation > 15 && obj.getBlockStyle() == BlockStyle.NORMAL) {
			System.err.println("[CLIENT][PLACEBLOCK] Exception: Block orientation was set over 8 on block style " + obj + ": " + blockOrientation2.blockOrientation);
			blockOrientation2.blockOrientation = 0;
		}
		return blockOrientation2;
	}
	
	public static byte defaultActive(final short n) {
		return (byte)((n != 16 && n != 32 && n != 48 && n != 40 && n != 30 && n != 24) ? 1 : 0);
	}
	
	public static String getKeyId(final short i) {
		final Set<Map.Entry<Object, Object>> entrySet = ElementKeyMap.properties.entrySet();
		String string = null;
		final Iterator<Map.Entry<Object, Object>> iterator = entrySet.iterator();
		while (iterator.hasNext()) {
			final Map.Entry<Object, Object> entry;
			if ((entry = iterator.next()).getValue().equals(String.valueOf(i))) {
				string = entry.getKey().toString();
				break;
			}
		}
		return string;
	}
	
	private static boolean calcIsSignal(final short n) {
		return n == 405 || n == 408 || n == 407 || n == 410 || n == 412 || n == 406 || n == 979 || ElementKeyMap.isButton(n) || n == 667 || n == 668 || n == 670 || n == 409;
	}
	
	public static boolean isPhysical(final SegmentPiece segmentPiece) {
		return isAlwaysPhysical(segmentPiece.getType()) || ElementKeyMap.getInfo(segmentPiece.getType()).isPhysical(segmentPiece.isActive());
	}
	
	public static boolean isAlwaysPhysical(final short n) {
		return !ElementKeyMap.isDoor(n);
	}
	
	public static boolean isPhysical(final short n, final SegmentData segmentData, final int n2) {
		final ElementInformation infoFast = ElementKeyMap.getInfoFast(n);
		return (!ElementKeyMap.isDoor(n) || segmentData.isActive(n2)) && (!infoFast.hasLod() || infoFast.lodShapeStyle != 2);
	}
	
	public static boolean isPhysicalRayTests(final short n, final SegmentData segmentData, final int n2) {
		ElementKeyMap.getInfoFast(n);
		return !ElementKeyMap.isDoor(n) || segmentData.isActive(n2);
	}
	
	public static boolean isPhysicalFast(final short n, final SegmentData segmentData, final int n2) {
		return !ElementKeyMap.infoArray[n].isDoor() || segmentData.isActive(n2);
	}
	
	public static void updatePhysical(final short n, final short n2, final boolean b, final byte b2, final byte b3, final byte b4, final ArrayOctree arrayOctree, final SegmentData segmentData, final int n3) {
		if (n2 != n && ElementKeyMap.isDoor(n)) {
			final boolean active;
			if ((active = segmentData.isActive(n3)) && !b) {
				assert n != 938;
				arrayOctree.insert(b2, b3, b4, n3);
			}
			else if (!active && b) {
				arrayOctree.delete(b2, b3, b4, n3, n);
			}
		}
	}
	
	public static boolean isBlendedSpecial(final short n, final boolean b) {
		return (ElementKeyMap.isDoor(n) && !b) || n == 689;
	}
	
	public static boolean isVisException(final ElementInformation elementInformation, final short n, final boolean b) {
		return elementInformation.isDoor() && n < 0;
	}
	
	public static byte activateOnPlacement(final short n) {
		if (n > 0 && ElementKeyMap.getInfo(n).activateOnPlacement()) {
			return 1;
		}
		return 0;
	}
	
	public static boolean canBeControlledByAny(final short n) {
		return ElementKeyMap.isValidType(n) && (ElementKeyMap.getInfoFast(n).getControlledBy().size() > 0 || n == 679 || n == 689 || n == 66 || ElementKeyMap.getInfoFast(n).isLightSource() || ElementKeyMap.getInfoFast(n).isInventory() || ElementKeyMap.getInfoFast(n).isInventory() || ElementKeyMap.getInfoFast(n).isInventory() || ElementKeyMap.getInfoFast(n).isSensorInput() || n == 405 || n == 408 || n == 409 || n == 405 || ElementKeyMap.getInfoFast(n).isSignal() || n == 685 || n == 120 || n == 120 || ElementKeyMap.getInfo(n).isRailTrack() || n == 405 || ElementKeyMap.getInfo(n).isSignal() || ElementKeyMap.getInfo(n).isSignal() || ElementKeyMap.getInfoFast(n).isSignal() || n == 663 || ElementKeyMap.getInfoFast(n).canActivate() || ElementKeyMap.getInfoFast(n).isRailTrack() || isLightConnectAny(n) || ElementKeyMap.getInfoFast(n).isMainCombinationControllerB() || ElementKeyMap.getInfoFast(n).isMainCombinationControllerB());
	}
	
	public static boolean canBeControlled(final short n, final short n2) {
		return ElementKeyMap.isValidType(n2) && ElementKeyMap.isValidType(n) && ((n == 677 && n2 == 679) || (n == 347 && n2 == 689) || (n == 66 && n2 == 66) || (n == 1 && ElementKeyMap.getInfoFast(n2).isLightSource()) || (ElementKeyMap.isToStashConnectable(n) && ElementKeyMap.getInfoFast(n2).isInventory()) || (ElementKeyMap.getInfoFast(n).isInventory() && ElementKeyMap.getInfoFast(n2).isInventory()) || (ElementKeyMap.getInfoFast(n).isRailTrack() && ElementKeyMap.getInfoFast(n2).isInventory()) || (n == 980 && ElementKeyMap.getInfoFast(n2).isSensorInput()) || (n == 120 && n2 == 405) || (n == 120 && n2 == 408) || (n == 120 && n2 == 409) || (ElementKeyMap.getInfo(n).isRailRotator() && n2 == 405) || (n == 685 && ElementKeyMap.getInfoFast(n2).isSignal()) || (ElementKeyMap.getInfoFast(n).isSignal() && n2 == 685) || (ElementKeyMap.getFactorykeyset().contains(n) && n2 == 120) || (n == 672 && ElementKeyMap.getInfo(n2).isRailTrack()) || (n == 672 && n2 == 405) || (n == 413 && ElementKeyMap.getInfo(n2).isSignal()) || (n == 685 && ElementKeyMap.getInfo(n2).isSignal()) || (ElementKeyMap.getInfoFast(n).isRailDockable() && ElementKeyMap.getInfoFast(n2).isSignal()) || (n == 360 && ElementKeyMap.getInfoFast(n2).isSignal()) || (ElementKeyMap.getInfoFast(n).isSignal() && n2 == 663) || (ElementKeyMap.getInfoFast(n).isSignal() && ElementKeyMap.getInfoFast(n2).canActivate()) || (ElementKeyMap.getInfoFast(n).isSignal() && ElementKeyMap.getInfoFast(n2).isRailTrack()) || ElementKeyMap.getInfoFast(n).isLightConnect(n2) || (ElementKeyMap.getInfoFast(n).isMainCombinationControllerB() && ElementKeyMap.getInfoFast(n2).isMainCombinationControllerB()) || (ElementKeyMap.getInfoFast(n).isSupportCombinationControllerB() && ElementKeyMap.getInfoFast(n2).isMainCombinationControllerB()));
	}
	
	private boolean isSensorInput() {
		return this.isInventory() || this.isDoor() || this.sensorInput;
	}
	
	private static CharSequence getFactoryResourceString(final ElementInformation detailMessage) {
		if (detailMessage.getFactory() == null) {
			return "CANNOT DISPLAY RESOURCES: NOT A FACTORY";
		}
		final StringBuffer sb = new StringBuffer();
		if (detailMessage.getFactory().input != null) {
			sb.append("----------Factory Production--------------\n\n");
			assert detailMessage.getFactory().input != null : detailMessage;
			for (int i = 0; i < detailMessage.getFactory().input.length; ++i) {
				sb.append("----------Product-<" + (i + 1) + ">--------------\n");
				sb.append("--- Required Resources:\n");
				FactoryResource[] array;
				for (int length = (array = detailMessage.getFactory().input[i]).length, j = 0; j < length; ++j) {
					final FactoryResource factoryResource = array[j];
					sb.append(factoryResource.count + "x " + ElementKeyMap.getInfo(factoryResource.type).getName() + "\n");
				}
				sb.append("\n\n--- Produces Resources:\n");
				FactoryResource[] array2;
				for (int length2 = (array2 = detailMessage.getFactory().output[i]).length, k = 0; k < length2; ++k) {
					final FactoryResource factoryResource2 = array2[k];
					sb.append(factoryResource2.count + "x " + ElementKeyMap.getInfo(factoryResource2.type).getName() + "\n");
				}
				sb.append("\n");
			}
			sb.append("\n---------------------------------------------\n\n");
		}
		return sb.toString();
	}
	
	public static boolean allowsMultiConnect(final short n) {
		return ElementKeyMap.isValidType(n) && ElementKeyMap.getInfoFast(n).isMultiControlled() && !ElementKeyMap.getInfoFast(n).isRestrictedMultiControlled();
	}
	
	public static boolean isMedical(final short n) {
		return n == 446 || n == 445;
	}
	
	public boolean isProducedIn(final short n) {
		return !this.deprecated && ((n == 213 && this.producedInFactory == 1) || (n == 215 && this.producedInFactory == 2) || (n == 211 && this.producedInFactory == 3) || (n == 217 && this.producedInFactory == 4) || (n == 259 && this.producedInFactory == 5));
	}
	
	public short getProducedInFactoryType() {
		switch (this.producedInFactory) {
			case 1: {
				return 213;
			}
			case 2: {
				return 215;
			}
			case 3: {
				return 211;
			}
			case 4: {
				return 217;
			}
			case 5: {
				return 259;
			}
			default: {
				return 0;
			}
		}
	}
	
	private String getDivString(final float n) {
		for (float n2 = 0.0f; n2 < 8.0f; ++n2) {
			if (n2 * 0.125f == n) {
				return (int)n2 + "/8";
			}
		}
		if (n - Math.round(n) == 0.0f) {
			return String.valueOf(n);
		}
		return StringTools.formatPointZeroZero(n);
	}
	
	public GUIGraphElement getGraphElement(final InputState inputState, final String s, final int n, final int n2, final int n3, final int n4) {
		return this.getGraphElement(inputState, s, n, n2, n3, n4, new Vector4f(0.17f, 0.27f, 0.37f, 1.0f));
	}
	
	public GUIGraphElement getGraphElement(final InputState inputState, final String textSimple, final int n, final int n2, final int n3, final int n4, final Vector4f vector4f) {
		final GUIColoredRectangle guiColoredRectangle;
		(guiColoredRectangle = new GUIColoredRectangle(inputState, (float)n3, (float)n4, vector4f)).rounded = 4.0f;
		final GUITextOverlay guiTextOverlay;
		(guiTextOverlay = new GUITextOverlay(n3, n4, FontLibrary.getBoldArial12White(), inputState)).setColor(1.0f, 1.0f, 1.0f, 1.0f);
		guiTextOverlay.setTextSimple(textSimple);
		guiTextOverlay.setPos(5.0f, 5.0f, 0.0f);
		final GUIBlockSprite guiBlockSprite;
		(guiBlockSprite = new GUIBlockSprite(inputState, this.getId())).getScale().set(0.5f, 0.5f, 0.5f);
		guiBlockSprite.getPos().x = (float)(n3 - 32);
		guiBlockSprite.getPos().y = (float)(n4 - 32);
		guiColoredRectangle.attach(guiBlockSprite);
		guiColoredRectangle.attach(guiTextOverlay);
		final GUIGraphElement guiGraphElement;
		(guiGraphElement = new GUIGraphElement(inputState, guiColoredRectangle)).setPos((float)n, (float)n2, 0.0f);
		return guiGraphElement;
	}
	
	public ShopItemElement getShopItemElement(final InputState inputState) {
		if (this.shopItemElement == null || this.shopItemElement.getState() != inputState) {
			(this.shopItemElement = new ShopItemElement(inputState, this)).onInit();
		}
		return this.shopItemElement;
	}
	
	public GUIGraph getRecipeGraph(final InputState inputState) {
		final GUIGraph guiGraph = new GUIGraph(inputState);
		String str = this.getName();
		final Vector4f vector4f = new Vector4f(0.17f, 0.27f, 0.37f, 1.0f);
		if (this.getBasicResourceFactory() != 0) {
			if (this.getBasicResourceFactory() == 215) {
				vector4f.set(0.37f, 0.27f, 0.17f, 1.0f);
			}
			else {
				vector4f.set(0.37f, 0.17f, 0.27f, 1.0f);
			}
			str += StringTools.format(Lng.ORG_SCHEMA_GAME_COMMON_DATA_ELEMENT_ELEMENTINFORMATION_2, ElementKeyMap.getInfo(this.getBasicResourceFactory()).getName());
		}
		final GUIGraphElement addVertex = guiGraph.addVertex(this.getGraphElement(inputState, str, 20, 20, 130, 80, vector4f));
		int n = 0;
		final Int2IntOpenHashMap int2IntOpenHashMap = new Int2IntOpenHashMap();
		final Iterator<FactoryResource> iterator = this.consistence.iterator();
		while (iterator.hasNext()) {
			final FactoryResource factoryResource;
			ElementKeyMap.getInfo((factoryResource = iterator.next()).type).addRecipeGraph(int2IntOpenHashMap, 1, n, 130, 80, guiGraph, inputState, addVertex, (float)factoryResource.count, (float)factoryResource.count);
			int2IntOpenHashMap.put(0, 1);
			++n;
		}
		return guiGraph;
	}
	
	private void addRecipeGraph(final Int2IntOpenHashMap int2IntOpenHashMap, final int n, int value, final int n2, final int n3, final GUIGraph guiGraph, final InputState inputState, final GUIGraphElement guiGraphElement, final float n4, final float n5) {
		value = int2IntOpenHashMap.get(n);
		String str = StringTools.format(Lng.ORG_SCHEMA_GAME_COMMON_DATA_ELEMENT_ELEMENTINFORMATION_3, this.getName(), this.getDivString(n4), this.getDivString(n5));
		final Vector4f vector4f = new Vector4f(0.17f, 0.27f, 0.37f, 1.0f);
		if (this.getBasicResourceFactory() != 0) {
			if (this.getBasicResourceFactory() == 215) {
				vector4f.set(0.37f, 0.27f, 0.17f, 1.0f);
			}
			else {
				vector4f.set(0.37f, 0.17f, 0.27f, 1.0f);
			}
			str += StringTools.format(Lng.ORG_SCHEMA_GAME_COMMON_DATA_ELEMENT_ELEMENTINFORMATION_4, ElementKeyMap.getInfo(this.getBasicResourceFactory()).getName());
		}
		final GUIGraphElement addVertex = guiGraph.addVertex(this.getGraphElement(inputState, str, value * (n2 + 10), n * ((n3 << 1) + 10), n2, n3, vector4f));
		guiGraph.addConnection(addVertex, guiGraphElement);
		int2IntOpenHashMap.put(n, value + 1);
		value = 0;
		for (final FactoryResource obj : this.consistence) {
			System.err.println("ADD CONSISTENCE NORMAL " + obj);
			ElementKeyMap.getInfo(obj.type).addRecipeGraph(int2IntOpenHashMap, n + 1, value, n2, n3, guiGraph, inputState, addVertex, (float)obj.count, obj.count * n5);
			++value;
		}
	}
	
	private void addGraph(final Int2IntOpenHashMap int2IntOpenHashMap, final int n, int value, final int n2, final int n3, final mxGraph mxGraph, final Object o, final Object o2, final float n4, final float n5) {
		value = int2IntOpenHashMap.get(n);
		final Object insertVertex = mxGraph.insertVertex(o2, (String)null, (Object)(this.getName() + "\n(x" + this.getDivString(n4) + ")\ntot(x" + this.getDivString(n5) + ")"), (double)(n * ((n2 << 1) + 10)), (double)(value * (n3 + 10)), (double)n2, (double)n3);
		mxGraph.insertEdge(o2, (String)null, (Object)"", insertVertex, o);
		int2IntOpenHashMap.put(n, value + 1);
		value = 0;
		if (this.isCapsule()) {
			System.err.println("CAPSULE " + this + "; Consistence " + this.consistence);
			if (this.consistence.size() > 0) {
				final ElementInformation info = ElementKeyMap.getInfo(this.consistence.get(0).type);
				System.err.println("CAPSULE " + this + "; Consistence " + this.consistence + " split-> " + info.cubatomConsistence);
				final Iterator<FactoryResource> iterator = info.cubatomConsistence.iterator();
				while (iterator.hasNext()) {
					final FactoryResource factoryResource;
					if ((factoryResource = iterator.next()).type == this.getId()) {
						System.err.println("ADDING CUBATOM CONSISTENS FOR " + this + " -> " + ElementKeyMap.getInfo(factoryResource.type));
						info.addGraph(int2IntOpenHashMap, n + 1, 0, n2, n3, mxGraph, insertVertex, o2, 1.0f / factoryResource.count, 1.0f / factoryResource.count * n5);
					}
				}
			}
		}
		else {
			for (final FactoryResource obj : this.consistence) {
				System.err.println("ADD CONSISTENCE NORMAL " + obj);
				ElementKeyMap.getInfo(obj.type).addGraph(int2IntOpenHashMap, n + 1, value, n2, n3, mxGraph, insertVertex, o2, (float)obj.count, obj.count * n5);
				++value;
			}
		}
	}
	
	public JPanel getGraph() {
		final JPanel panel = new JPanel(new GridBagLayout());
		final mxGraph mxGraph;
		final Object defaultParent = (mxGraph = new mxGraph()).getDefaultParent();
		mxGraph.setCellsEditable(false);
		mxGraph.setConnectableEdges(false);
		mxGraph.getModel().beginUpdate();
		try {
			final Object insertVertex = mxGraph.insertVertex(defaultParent, String.valueOf(this.getId()), (Object)this.getName(), 20.0, 20.0, 100.0, 180.0);
			int n = 0;
			final Int2IntOpenHashMap int2IntOpenHashMap = new Int2IntOpenHashMap();
			if (this.isCapsule()) {
				System.err.println("CAPSULE " + this + "; Consistence " + this.consistence);
				if (this.consistence.size() > 0) {
					final ElementInformation info = ElementKeyMap.getInfo(this.consistence.get(0).type);
					System.err.println("CAPSULE " + this + "; Consistence " + this.consistence + " split-> " + info.cubatomConsistence);
					final Iterator<FactoryResource> iterator = info.cubatomConsistence.iterator();
					while (iterator.hasNext()) {
						final FactoryResource factoryResource;
						if ((factoryResource = iterator.next()).type == this.getId()) {
							System.err.println("ADDING CUBATOM CONSISTENS FOR " + this + " -> " + ElementKeyMap.getInfo(factoryResource.type));
							info.addGraph(int2IntOpenHashMap, 1, 0, 100, 180, mxGraph, insertVertex, defaultParent, 1.0f / factoryResource.count, 1.0f / factoryResource.count);
						}
					}
				}
			}
			else {
				final Iterator<FactoryResource> iterator2 = this.consistence.iterator();
				while (iterator2.hasNext()) {
					final FactoryResource factoryResource2;
					ElementKeyMap.getInfo((factoryResource2 = iterator2.next()).type).addGraph(int2IntOpenHashMap, 1, n, 100, 180, mxGraph, insertVertex, defaultParent, (float)factoryResource2.count, (float)factoryResource2.count);
					int2IntOpenHashMap.put(0, 1);
					++n;
				}
			}
		}
		finally {
			mxGraph.getModel().endUpdate();
		}
		final mxGraphComponent comp = new mxGraphComponent(mxGraph);
		final GridBagConstraints constraints;
		(constraints = new GridBagConstraints()).anchor = 12;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.fill = 1;
		//final Container container;
		panel.add((Component)comp, constraints);
		return panel;
	}
	
	private void addChamberGraph(final Int2IntOpenHashMap int2IntOpenHashMap, final int n, int value, final int n2, final int n3, final mxGraph mxGraph, final Object o, final Object o2) {
		value = int2IntOpenHashMap.get(n);
		String str = "[" + this.getName() + "]";
		final Iterator<String> iterator = this.chamberConfigGroupsLowerCase.iterator();
		while (iterator.hasNext()) {
			str = str + "\n" + iterator.next();
		}
		final Object insertVertex = mxGraph.insertVertex(o2, (String)null, (Object)str, (double)(n * (n2 + 35)), (double)(value * (n3 + 10)), (double)n2, (double)n3);
		mxGraph.insertEdge(o2, (String)null, (Object)"", o, insertVertex);
		int2IntOpenHashMap.put(n, value + 1);
		value = 0;
		final Iterator iterator2 = this.chamberChildren.iterator();
		while (iterator2.hasNext()) {
			ElementKeyMap.getInfo((short)iterator2.next()).addChamberGraph(int2IntOpenHashMap, n + 1, value, n2, n3, mxGraph, insertVertex, o2);
			int2IntOpenHashMap.put(0, 1);
			++value;
		}
	}
	
	public JPanel getChamberGraph() {
		assert this.isReactorChamberGeneral();
		final JPanel panel = new JPanel(new GridBagLayout());
		final mxGraph mxGraph;
		final Object defaultParent = (mxGraph = new mxGraph()).getDefaultParent();
		mxGraph.setCellsEditable(false);
		mxGraph.setConnectableEdges(true);
		mxGraph.getModel().beginUpdate();
		mxGraph.setAllowLoops(false);
		mxGraph.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_EDGE, "elbowEdgeStyle");
		try {
			final Object insertVertex = mxGraph.insertVertex(defaultParent, String.valueOf(this.getId()), (Object)this.getName(), 20.0, 20.0, 230.0, 30.0);
			int n = 0;
			final Int2IntOpenHashMap int2IntOpenHashMap = new Int2IntOpenHashMap();
			final Iterator iterator = this.chamberChildren.iterator();
			while (iterator.hasNext()) {
				ElementKeyMap.getInfo((short)iterator.next()).addChamberGraph(int2IntOpenHashMap, 1, n, 230, 30, mxGraph, insertVertex, defaultParent);
				int2IntOpenHashMap.put(0, 1);
				++n;
			}
		}
		finally {
			mxGraph.getModel().endUpdate();
		}
		((mxIGraphLayout)new mxParallelEdgeLayout(mxGraph)).execute(defaultParent);
		final mxGraphComponent comp = new mxGraphComponent(mxGraph);
		final GridBagConstraints constraints;
		(constraints = new GridBagConstraints()).anchor = 12;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.fill = 1;
		//final Container container;
		panel.add((Component)comp, constraints);
		return panel;
	}
	
	public long calculateDynamicPrice() {
		if (this.dynamicPrice < 0L) {
			long price = 0L;
			if (!this.consistence.isEmpty()) {
				for (final FactoryResource factoryResource : this.consistence) {
					price += factoryResource.count * ElementKeyMap.getInfo(factoryResource.type).calculateDynamicPrice();
					if (ElementKeyMap.getInfo(factoryResource.type).consistence.isEmpty()) {
						price *= (long)(float)ServerConfig.DYNAMIC_RECIPE_PRICE_MODIFIER.getCurrentState();
					}
				}
			}
			else {
				price = this.price;
			}
			this.dynamicPrice = price;
		}
		return this.dynamicPrice;
	}
	
	public boolean isCapsule() {
		return this.blockResourceType == 6;
	}
	
	public boolean isOre() {
		return this.blockResourceType == 0;
	}
	
	public void appendXML(final Document document, final org.w3c.dom.Element element) throws CannotAppendXMLException {
		final org.w3c.dom.Element element2 = document.createElement("Block");
		final String keyId;
		if ((keyId = getKeyId(this.getId())) == null) {
			throw new CannotAppendXMLException("Cannot find property key for Block ID " + this.getId() + "; Check your Block properties file");
		}
		element2.setAttribute("type", keyId);
		element2.setAttribute("icon", String.valueOf(this.getBuildIconNum()));
		element2.setAttribute("textureId", String.valueOf(StringTools.getCommaSeperated(this.getTextureIds())));
		element2.setAttribute("name", this.name);
		Field[] fields;
		for (int length = (fields = ElementInformation.class.getFields()).length, i = 0; i < length; ++i) {
			final Field field = fields[i];
			try {
				field.setAccessible(true);
				if (field.get(this) == null) {
					continue;
				}
			}
			catch (IllegalArgumentException ex2) {
				final IllegalArgumentException ex = ex2;
				ex2.printStackTrace();
				throw new CannotAppendXMLException(ex.getMessage());
			}
			catch (IllegalAccessException ex4) {
				final IllegalAccessException ex3 = ex4;
				ex4.printStackTrace();
				throw new CannotAppendXMLException(ex3.getMessage());
			}
			final Element element3;
			if ((element3 = field.getAnnotation(Element.class)) != null && element3.writeAsTag()) {
				final org.w3c.dom.Element element4 = document.createElement(element3.parser().tag);
				try {
					if (element3.factory()) {
						if (this.getFactory().input == null) {
							element4.setTextContent("INPUT");
						}
						else {
							for (int j = 0; j < this.getFactory().input.length; ++j) {
								final org.w3c.dom.Element element5 = document.createElement("Product");
								final org.w3c.dom.Element element6 = document.createElement("Input");
								final org.w3c.dom.Element element7 = document.createElement("Output");
								for (int k = 0; k < this.getFactory().input[j].length; ++k) {
									element6.appendChild(this.getFactory().input[j][k].getNode(document));
								}
								for (int l = 0; l < this.getFactory().output[j].length; ++l) {
									element7.appendChild(this.getFactory().output[j][l].getNode(document));
								}
								element5.appendChild(element6);
								element5.appendChild(element7);
								element4.appendChild(element5);
							}
						}
					}
					else if (element3.type()) {
						element4.setTextContent(String.valueOf(field.getShort(this)));
					}
					else if (!element3.cubatom()) {
						if (element3.consistence()) {
							for (int n = 0; n < this.getConsistence().size(); ++n) {
								element4.appendChild(this.getConsistence().get(n).getNode(document));
							}
						}
						else if (element3.cubatomConsistence()) {
							for (int n2 = 0; n2 < this.cubatomConsistence.size(); ++n2) {
								element4.appendChild(this.cubatomConsistence.get(n2).getNode(document));
							}
						}
						else if (element3.vector3f()) {
							final Vector3f vector3f = (Vector3f)field.get(this);
							element4.setTextContent(vector3f.x + "," + vector3f.y + "," + vector3f.z);
						}
						else if (element3.vector4f()) {
							final Vector4f vector4f = (Vector4f)field.get(this);
							element4.setTextContent(vector4f.x + "," + vector4f.y + "," + vector4f.z + "," + vector4f.w);
						}
						else if (field.getType().equals(short[].class)) {
							final short[] array = (short[])field.get(this);
							final StringBuffer sb = new StringBuffer();
							for (int n3 = 0; n3 < array.length; ++n3) {
								sb.append(array[n3]);
								if (n3 < array.length - 1) {
									sb.append(", ");
								}
							}
							element4.setTextContent(sb.toString());
						}
						else if (field.getType().equals(int[].class)) {
							final int[] array2 = (int[])field.get(this);
							final StringBuffer sb2 = new StringBuffer();
							for (int n4 = 0; n4 < array2.length; ++n4) {
								sb2.append(array2[n4]);
								if (n4 < array2.length - 1) {
									sb2.append(", ");
								}
							}
							element4.setTextContent(sb2.toString());
						}
						else if (element3.collectionType().toLowerCase(Locale.ENGLISH).equals("blocktypes")) {
							final Collection collection;
							if ((collection = (Collection)field.get(this)).isEmpty()) {
								continue;
							}
							Iterator it = collection.iterator();
							for (Short obj = (Short)it.next(); it.hasNext(); obj = (Short)it.next()) {
								final org.w3c.dom.Element element8 = document.createElement(element3.collectionElementTag());
								final String keyId2;
								if ((keyId2 = getKeyId(obj)) == null) {
									throw new CannotAppendXMLException("[BlockSet] " + field.getName() + " Cannot find property key for Block ID " + obj + "; Check your Block properties file");
								}
								element8.setTextContent(keyId2);
								element4.appendChild(element8);
							}
						}
						else if (field.getType().equals(InterEffectSet.class)) {
							final InterEffectSet set = (InterEffectSet)field.get(this);
							InterEffectHandler.InterEffectType[] values;
							for (int length2 = (values = InterEffectHandler.InterEffectType.values()).length, n5 = 0; n5 < length2; ++n5) {
								final InterEffectHandler.InterEffectType interEffectType = values[n5];
								final org.w3c.dom.Element element9 = document.createElement(interEffectType.id);
								element9.setTextContent(String.valueOf(set.getStrength(interEffectType)));
								element4.appendChild(element9);
							}
						}
						else if (element3.collectionType().toLowerCase(Locale.ENGLISH).equals("string")) {
							final Collection collection2;
							if ((collection2 = (Collection)field.get(this)).isEmpty()) {
								continue;
							}
							final Iterator<String> iterator2 = collection2.iterator();
							while (iterator2.hasNext()) {
								final org.w3c.dom.Element element10;
								(element10 = document.createElement(element3.collectionElementTag())).setTextContent(iterator2.next());
								element4.appendChild(element10);
							}
						}
						else if (field.getType() == BlockStyle.class) {
							final BlockStyle blockStyle = (BlockStyle)field.get(this);
							element4.setTextContent(String.valueOf(blockStyle.id));
							element4.appendChild(document.createComment(blockStyle.realName + ": " + blockStyle.desc));
						}
						else if (field.getType() == LodCollision.class) {
							((LodCollision)field.get(this)).write(element4, document);
						}
						else if (field.getType() == ResourceInjectionType.class) {
							final ResourceInjectionType resourceInjectionType = (ResourceInjectionType)field.get(this);
							element4.setTextContent(String.valueOf(resourceInjectionType.ordinal()));
							element4.appendChild(document.createComment(resourceInjectionType.name().toLowerCase(Locale.ENGLISH)));
						}
						else {
							String textContent = field.get(this).toString();
							if (element3.textArea()) {
								textContent = textContent.replace("\n", "\\n\\r").replaceAll("\\r", "\r");
							}
							if (textContent.length() == 0) {
								continue;
							}
							element4.setTextContent(textContent);
						}
					}
				}
				catch (Exception ex6) {
					final Exception ex5 = ex6;
					ex6.printStackTrace();
					throw new CannotAppendXMLException(ex5.getMessage());
				}
				element2.appendChild(element4);
			}
		}
		element.appendChild(element2);
	}
	
	public short[] getTextureIds() {
		return this.textureId;
	}
	
	public boolean canActivate() {
		return this.canActivate;
	}
	
	@Override
	public int compareTo(final ElementInformation elementInformation) {
		return this.name.compareTo(elementInformation.name);
	}
	
	public BlockStyle getBlockStyle() {
		return this.blockStyle;
	}
	
	public void setBlockStyle(final int n) throws ElementParserException {
		this.blockStyle = BlockStyle.getById(n);
	}
	
	public int getBuildIconNum() {
		return this.buildIconNum;
	}
	
	public void setBuildIconNum(final int buildIconNum) {
		this.buildIconNum = buildIconNum;
	}
	
	public Set<Short> getControlledBy() {
		return (Set<Short>)this.controlledBy;
	}
	
	public Set<Short> getControlling() {
		return (Set<Short>)this.controlling;
	}
	
	public String getDescription() {
		final String s;
		if ((s = (String)ElementKeyMap.descriptionTranslations.get(this.id)) != null) {
			return s;
		}
		return this.description;
	}
	
	public void setDescription(final String description) {
		this.description = description;
	}
	
	public byte getExtraOrientation() {
		return 0;
	}
	
	public BlockFactory getFactory() {
		return this.factory;
	}
	
	public void setFactory(final BlockFactory factory) {
		this.factory = factory;
	}
	
	public String getFullName() {
		if (this.fullName == null) {
			return this.getName();
		}
		return this.fullName;
	}
	
	public void setFullName(final String fullName) {
		this.fullName = fullName;
	}
	
	public short getId() {
		return this.id;
	}
	
	public int getIndividualSides() {
		return this.individualSides;
	}
	
	public void setIndividualSides(final int individualSides) {
		this.individualSides = individualSides;
	}
	
	public Vector4f getLightSourceColor() {
		return this.lightSourceColor;
	}
	
	public int getMaxHitPointsFull() {
		return this.maxHitPointsFull;
	}
	
	public String getName() {
		final String s;
		if ((s = (String)ElementKeyMap.nameTranslations.get(this.id)) != null) {
			return s;
		}
		return this.name;
	}
	
	public String getNameUntranslated() {
		return this.name;
	}
	
	public long getPrice(final boolean b) {
		if (!b) {
			return this.price;
		}
		return this.dynamicPrice;
	}
	
	public List<Short> getRecipeBuyResources() {
		return (List<Short>)this.recipeBuyResources;
	}
	
	public short getTextureId(final int n) {
		return this.textureId[n];
	}
	
	public ElementCategory getType() {
		return this.type;
	}
	
	@Override
	public int hashCode() {
		return this.getId();
	}
	
	@Override
	public boolean equals(final Object o) {
		return ((ElementInformation)o).getId() == this.getId();
	}
	
	@Override
	public String toString() {
		return this.getName() + "(" + this.getId() + ")";
	}
	
	public boolean isAnimated() {
		return this.animated;
	}
	
	public void setAnimated(final boolean animated) {
		this.animated = animated;
	}
	
	public boolean isBlended() {
		return this.blended;
	}
	
	public void setBlended(final boolean blended) {
		this.blended = blended;
	}
	
	public boolean isController() {
		return !this.getControlling().isEmpty() || this.controlsAll();
	}
	
	public boolean isOldDockable() {
		return this.getId() == 7 || this.getId() == 289;
	}
	
	public boolean isEnterable() {
		return this.enterable;
	}
	
	public void setEnterable(final boolean enterable) {
		this.enterable = enterable;
	}
	
	public boolean isInRecipe() {
		return this.inRecipe;
	}
	
	public void setInRecipe(final boolean inRecipe) {
		this.inRecipe = inRecipe;
	}
	
	public boolean isLightSource() {
		return this.lightSource;
	}
	
	public void setLightSource(final boolean lightSource) {
		this.lightSource = lightSource;
	}
	
	public boolean isOrientatable() {
		return this.orientatable;
	}
	
	public void setOrientatable(final boolean orientatable) {
		this.orientatable = orientatable;
	}
	
	public boolean isPhysical() {
		return this.physical;
	}
	
	public void setPhysical(final boolean physical) {
		this.physical = physical;
	}
	
	public boolean isPhysical(final boolean b) {
		if (this.isDoor()) {
			return b;
		}
		return this.physical;
	}
	
	public boolean isPlacable() {
		return this.placable;
	}
	
	public void setPlacable(final boolean placable) {
		this.placable = placable;
	}
	
	public boolean isShoppable() {
		return this.shoppable;
	}
	
	public void setShoppable(final boolean shoppable) {
		this.shoppable = shoppable;
	}
	
	public void setCanActivate(final boolean canActivate) {
		this.canActivate = canActivate;
	}
	
	public void setPrice(final long price) {
		this.price = price;
	}
	
	public int getDefaultOrientation() {
		if (this.getId() == 689) {
			return 4;
		}
		if (this.getId() == 688) {
			return 2;
		}
		if (this.getBlockStyle() == BlockStyle.SPRITE || this.getIndividualSides() == 3) {
			return 2;
		}
		if (this.getBlockStyle() == BlockStyle.NORMAL24) {
			return 14;
		}
		if (this.getId() == 56) {
			return 3;
		}
		return 0;
	}
	
	public boolean isBlendBlockStyle() {
		return this.getBlockStyle().blendedBlockStyle || (this.hasLod() && this.lodShapeStyle == 1);
	}
	
	public boolean controlsAll() {
		return this.isSignal();
	}
	
	public boolean isSignal() {
		return this.signal;
	}
	
	public boolean isHasActivationTexure() {
		return this.hasActivationTexure;
	}
	
	public void setHasActivationTexure(final boolean hasActivationTexure) {
		this.hasActivationTexure = hasActivationTexure;
	}
	
	public boolean drawConnection() {
		return EngineSettings.G_DRAW_ALL_CONNECTIONS.isOn() || (EngineSettings.G_DRAW_ANY_CONNECTIONS.isOn() && this.drawLogicConnection);
	}
	
	public boolean isSupportCombinationControllerB() {
		return this.supportCombinationController;
	}
	
	public void setSupportCombinationController(final boolean supportCombinationController) {
		this.supportCombinationController = supportCombinationController;
	}
	
	public boolean isMainCombinationControllerB() {
		return this.mainCombinationController;
	}
	
	public void setMainCombinationController(final boolean mainCombinationController) {
		this.mainCombinationController = mainCombinationController;
	}
	
	public boolean isCombiConnectSupport(final short n) {
		if (ElementKeyMap.isValidType(n)) {
			final ElementInformation info = ElementKeyMap.getInfo(n);
			if (this.isMainCombinationControllerB() && info.isMainCombinationControllerB()) {
				return true;
			}
			if (this.isSupportCombinationControllerB() && info.isMainCombinationControllerB()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isCombiConnectEffect(final short n) {
		final ElementInformation info = ElementKeyMap.getInfo(n);
		return this.isMainCombinationControllerB() && info.isEffectCombinationController();
	}
	
	public boolean isEffectCombinationController() {
		return this.effectCombinationController;
	}
	
	public void setEffectCombinationController(final boolean effectCombinationController) {
		this.effectCombinationController = effectCombinationController;
	}
	
	public boolean isCombiConnectAny(final short n) {
		return this.isCombiConnectSupport(n) || this.isCombiConnectEffect(n) || this.isLightConnect(n);
	}
	
	public static boolean isLightConnectAny(final short n) {
		return ElementKeyMap.isValidType(n) && ElementKeyMap.getInfo(n).isLightSource();
	}
	
	public boolean isLightConnect(final short n) {
		if (ElementKeyMap.isValidType(n)) {
			final ElementInformation info = ElementKeyMap.getInfo(n);
			if (this.isMainCombinationControllerB() && info.isLightSource()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean activateOnPlacement() {
		return !this.isSignal() && (ElementKeyMap.getFactorykeyset().contains(this.id) || this.isLightSource() || this.isDoor() || this.getId() == 120 || this.getId() == 937);
	}
	
	public String[] parseDescription(final GameClientState gameClientState) {
		final String string = Lng.ORG_SCHEMA_GAME_COMMON_DATA_ELEMENT_ELEMENTINFORMATION_6 + this.getMaxHitPointsFull() + " \n" + (Lng.ORG_SCHEMA_GAME_COMMON_DATA_ELEMENT_ELEMENTINFORMATION_5 + "<?> \n\n" + this.getDescription());
		final String org_SCHEMA_GAME_COMMON_DATA_ELEMENT_ELEMENTINFORMATION_7 = Lng.ORG_SCHEMA_GAME_COMMON_DATA_ELEMENT_ELEMENTINFORMATION_7;
		final String[] split = (Lng.ORG_SCHEMA_GAME_COMMON_DATA_ELEMENT_ELEMENTINFORMATION_9 + StringTools.formatPointZeroZero(this.getMass()) + " \n" + (Lng.ORG_SCHEMA_GAME_COMMON_DATA_ELEMENT_ELEMENTINFORMATION_8 + this.structureHP + " \n" + string)).split("\\n");
		for (int i = 0; i < split.length; ++i) {
			split[i] = split[i].replace("$ACTIVATE", KeyboardMappings.ACTIVATE.getKeyChar());
			if (split[i].contains("$RESOURCES")) {
				split[i] = split[i].replace("$RESOURCES", getFactoryResourceString(this));
			}
			if (split[i].contains("$ARMOUR")) {
				split[i] = split[i].replace("$ARMOUR", "");
			}
			if (split[i].contains("$HP")) {
				split[i] = split[i].replace("$HP", String.valueOf(this.getMaxHitPointsFull()));
			}
			if (split[i].contains("$EFFECT")) {
				if (new ShipManagerContainer(gameClientState, new Ship(gameClientState)).getEffect(this.getId()) != null) {
					split[i] = split[i].replace("$EFFECT", StringTools.format(Lng.ORG_SCHEMA_GAME_COMMON_DATA_ELEMENT_ELEMENTINFORMATION_0, KeyboardMappings.SELECT_MODULE.getKeyChar(), KeyboardMappings.CONNECT_MODULE.getKeyChar(), "[PLACEHOLDER]"));
				}
				else {
					split[i] = split[i].replace("$EFFECT", "NO_EFFECT(invalid $ var)");
				}
			}
			if (split[i].contains("$MAINCOMBI")) {
				split[i] = split[i].replace("$MAINCOMBI", StringTools.format(Lng.ORG_SCHEMA_GAME_COMMON_DATA_ELEMENT_ELEMENTINFORMATION_1, KeyboardMappings.SELECT_MODULE.getKeyChar(), KeyboardMappings.CONNECT_MODULE.getKeyChar()));
			}
		}
		return split;
	}
	
	public boolean isDeprecated() {
		return this.deprecated;
	}
	
	public void setDeprecated(final boolean deprecated) {
		this.deprecated = deprecated;
	}
	
	public List<FactoryResource> getConsistence() {
		return this.consistence;
	}
	
	public RecipeInterface getProductionRecipe() {
		if (this.productionRecipe == null) {
			this.productionRecipe = new FixedRecipe();
			this.productionRecipe.costAmount = -1;
			this.productionRecipe.costType = -1;
			if (this.consistence.isEmpty()) {
				this.productionRecipe.recipeProducts = new FixedRecipeProduct[0];
			}
			else if (this.isCapsule()) {
				final ElementInformation info = ElementKeyMap.getInfo(this.consistence.get(0).type);
				(this.productionRecipe.recipeProducts = new FixedRecipeProduct[1])[0] = new FixedRecipeProduct();
				(this.productionRecipe.recipeProducts[0].input = new FactoryResource[1])[0] = new FactoryResource(1, info.getId());
				this.productionRecipe.recipeProducts[0].output = new FactoryResource[info.cubatomConsistence.size()];
				int n = 0;
				final Iterator<FactoryResource> iterator = info.cubatomConsistence.iterator();
				while (iterator.hasNext()) {
					this.productionRecipe.recipeProducts[0].output[n] = iterator.next();
					++n;
				}
			}
			else {
				(this.productionRecipe.recipeProducts = new FixedRecipeProduct[1])[0] = new FixedRecipeProduct();
				this.productionRecipe.recipeProducts[0].input = new FactoryResource[this.consistence.size()];
				int n2 = 0;
				for (final FactoryResource factoryResource : this.consistence) {
					assert factoryResource != null;
					this.productionRecipe.recipeProducts[0].input[n2] = factoryResource;
					++n2;
				}
				(this.productionRecipe.recipeProducts[0].output = new FactoryResource[1])[0] = new FactoryResource(1, this.getId());
			}
		}
		return this.productionRecipe;
	}
	
	public double getMaxHitpointsFullInverse() {
		return this.maxHitpointsInverse;
	}
	
	public double getMaxHitpointsByteToFull() {
		return this.maxHitpointsByteToFull;
	}
	
	public double getMaxHitpointsFullToByte() {
		return this.maxHitpointsFullToByte;
	}
	
	public short getBasicResourceFactory() {
		return this.basicResourceFactory;
	}
	
	public void setBasicResourceFactory(final short basicResourceFactory) {
		this.basicResourceFactory = basicResourceFactory;
	}
	
	public float getExplosionAbsorbtion() {
		return this.explosionAbsorbtion;
	}
	
	public void setExplosionAbsorbtion(final float explosionAbsorbtion) {
		this.explosionAbsorbtion = explosionAbsorbtion;
	}
	
	public float getFactoryBakeTime() {
		return this.factoryBakeTime;
	}
	
	public void setFactoryBakeTime(final float factoryBakeTime) {
		this.factoryBakeTime = factoryBakeTime;
	}
	
	public boolean isMultiControlled() {
		return this.isSignal() || this.isInventory() || this.isRailTrack() || this.isSensorInput() || this.getId() == 479;
	}
	
	public boolean isRestrictedMultiControlled() {
		return this.getId() == 689;
	}
	
	public ShortArrayList getRestrictedMultiControlled() {
		if (this.getId() == 689) {
			return ElementKeyMap.inventoryTypes;
		}
		assert false : this;
		return null;
	}
	
	public String createWikiStub() {
		final StringBuffer sb;
		(sb = new StringBuffer()).append("{{infobox block\n");
		sb.append("  |type=" + this.getName() + "\n");
		sb.append("\t|hp=" + this.getMaxHitPointsFull() + "\n");
		sb.append("\t|armor=-\n");
		sb.append("\t|light=" + (this.isLightSource() ? "yes" : "no") + "\n");
		if (this.isLightSource()) {
			sb.append("\t|lightColor=" + this.getLightSourceColor() + "\n");
		}
		sb.append("\t|dv=" + this.getId() + "\n");
		sb.append("}}\n\n");
		return sb.toString();
	}
	
	public boolean isDoor() {
		return this.door;
	}
	
	public void setDoor(final boolean door) {
		this.door = door;
	}
	
	public int getProducedInFactory() {
		return this.producedInFactory;
	}
	
	public void setProducedInFactory(final int producedInFactory) {
		this.producedInFactory = producedInFactory;
	}
	
	public String getInventoryGroup() {
		return this.inventoryGroup;
	}
	
	public void setInventoryGroup(final String s) {
		this.inventoryGroup = s.trim();
	}
	
	public boolean hasInventoryGroup() {
		return this.inventoryGroup.length() > 0;
	}
	
	public boolean isNormalBlockStyle() {
		return this.blockStyle.cube;
	}
	
	public boolean isRailTrack() {
		return this.blockStyle == BlockStyle.NORMAL24 && !this.hasLod() && this.id != 663 && this.id != 665 && this.id != 679 && this.id != 937 && this.id != 678;
	}
	
	public boolean isRailShipyardCore() {
		return this.id == 679;
	}
	
	public boolean isRailRotator() {
		return this.id == 664 || this.id == 669;
	}
	
	public boolean isRailTurret() {
		return this.id == 665;
	}
	
	public boolean isRailDockable() {
		return this.isRailTrack() || this.isRailRotator() || this.isRailTurret();
	}
	
	void recreateTextureMapping() {
		for (byte b = 0; b < 6; ++b) {
			this.textureLayerMapping[b] = this.calcTextureLayerCode(false, b);
			this.textureIndexLocalMapping[b] = this.calcTextureIndexLocalCode(false, b);
			this.textureLayerMappingActive[b] = this.calcTextureLayerCode(true, b);
			this.textureIndexLocalMappingActive[b] = this.calcTextureIndexLocalCode(true, b);
		}
		this.createdTX = true;
	}
	
	public byte getTextureLayer(final boolean b, final byte b2) {
		assert this.createdTX;
		return (byte)(b ? this.textureLayerMappingActive[b2] : this.textureLayerMapping[b2]);
	}
	
	public short getTextureIndexLocal(final boolean b, final byte b2) {
		assert this.createdTX;
		return (short)(b ? this.textureIndexLocalMappingActive[b2] : this.textureIndexLocalMapping[b2]);
	}
	
	private byte calcTextureLayerCode(final boolean b, final byte b2) {
		return (byte)(Math.abs(this.getTextureId(b, b2)) / 256);
	}
	
	private short calcTextureIndexLocalCode(final boolean b, final byte b2) {
		return (short)(this.getTextureId(b, b2) % 256);
	}
	
	private short getTextureId(final boolean b, final int n) {
		assert !(!this.hasActivationTexure);
		if (this.hasActivationTexure && !b) {
			return (short)(this.textureId[n] + 1);
		}
		return this.textureId[n];
	}
	
	public void setTextureId(final short[] original) {
		this.textureId = Arrays.copyOf(original, original.length);
		this.createdTX = false;
	}
	
	public void setTextureId(final int n, final short n2) {
		this.textureId[n] = n2;
		this.createdTX = false;
	}
	
	public void normalizeTextureIds() {
		final short n = this.textureId[0];
		if (this.individualSides >= 6) {
			for (short n2 = 0; n2 < 6; ++n2) {
				this.textureId[n2] = (short)(n + n2);
			}
		}
		else if (this.individualSides == 3) {
			this.textureId[2] = n;
			this.textureId[3] = (short)(n + 1);
			this.textureId[5] = (short)(n + 2);
			this.textureId[4] = (short)(n + 2);
			this.textureId[0] = (short)(n + 2);
			this.textureId[1] = (short)(n + 2);
		}
		else {
			for (int i = 1; i < 6; ++i) {
				this.textureId[i] = n;
			}
		}
		this.recreateTextureMapping();
	}
	
	public boolean isRailDocker() {
		return this.id == 663;
	}
	
	public boolean isRailSpeedActivationConnect(final short n) {
		return this.id == 672 && n == 405;
	}
	
	public boolean isRailSpeedTrackConnect(final short n) {
		return this.id == 672 && ElementKeyMap.isValidType(n) && ElementKeyMap.getInfo(n).isRailTrack();
	}
	
	public boolean needsCoreConnectionToWorkOnHotbar() {
		return this.id != 663 && this.id != 670 && this.id != 978;
	}
	
	public float getMass() {
		return this.mass;
	}
	
	public int getSlab() {
		return this.slab;
	}
	
	public int getSlab(final int n) {
		if (this.id == 689) {
			return n;
		}
		return this.slab;
	}
	
	public float getVolume() {
		return this.volume;
	}
	
	public boolean isInventory() {
		return this.id == 120 || this.id == 211 || this.id == 677 || this.id == 215 || this.id == 217 || this.id == 259 || this.id == 347 || this.getFactory() != null;
	}
	
	public boolean isSpecialBlock() {
		return this.specialBlock;
	}
	
	public void setSpecialBlock(final boolean specialBlock) {
		this.specialBlock = specialBlock;
	}
	
	public boolean isDrawnOnlyInBuildMode() {
		assert !(!this.blended);
		return this.drawOnlyInBuildMode;
	}
	
	public void setDrawOnlyInBuildMode(final boolean drawOnlyInBuildMode) {
		this.drawOnlyInBuildMode = drawOnlyInBuildMode;
	}
	
	public boolean isInOctree() {
		return this.id != 937 && this.id != 939 && this.id != 938;
	}
	
	public boolean isLightPassOnBlockItself() {
		return EngineSettings.CUBE_LIGHT_NORMALIZER_NEW_M.isOn() && (this.blockStyle.solidBlockStyle || this.slab > 0);
	}
	
	public void onInit() {
		this.calculateDynamicPrice();
	}
	
	public boolean hasLod() {
		return this.lodShapeString.length() > 0;
	}
	
	public int getModelCount(final boolean b) {
		Mesh mesh;
		if (b && this.lodShapeStringActive != null && this.lodShapeStringActive.length() > 0) {
			mesh = Controller.getResLoader().getMesh(this.lodShapeStringActive);
		}
		else {
			mesh = Controller.getResLoader().getMesh(this.lodShapeString);
		}
		return mesh.getChilds().size();
	}
	
	public Mesh getModel(final int n, final boolean b) {
		Mesh mesh;
		if (b && this.lodShapeStringActive != null && this.lodShapeStringActive.length() > 0) {
			mesh = Controller.getResLoader().getMesh(this.lodShapeStringActive);
		}
		else {
			mesh = Controller.getResLoader().getMesh(this.lodShapeString);
		}
		if (mesh == null) {
			throw new RuntimeException("Model " + this.lodShapeString + " not found");
		}
		final Mesh mesh2 = (Mesh)mesh.getChilds().get(n);
		assert mesh2 != null : this.lodShapeString;
		return mesh2;
	}
	
	private void recalcRawConsistenceRec(final FactoryResource factoryResource, final int n) {
		final ElementInformation infoFast;
		if ((infoFast = ElementKeyMap.getInfoFast(factoryResource.type)).getConsistence().isEmpty()) {
			this.rawConsistence.add(factoryResource);
			this.rawBlocks.inc(factoryResource.type, n);
			return;
		}
		for (final FactoryResource factoryResource2 : infoFast.consistence) {
			this.recalcRawConsistenceRec(factoryResource2, factoryResource2.count * n);
		}
	}
	
	private void recalcTotalConsistenceRec(final FactoryResource factoryResource) {
		final ElementInformation infoFast = ElementKeyMap.getInfoFast(factoryResource.type);
		this.getTotalConsistence().add(factoryResource);
		final Iterator<FactoryResource> iterator = infoFast.consistence.iterator();
		while (iterator.hasNext()) {
			this.recalcTotalConsistenceRec(iterator.next());
		}
	}
	
	public void recalcTotalConsistence() {
		this.rawBlocks.checkArraySize();
		this.rawConsistence.clear();
		this.getTotalConsistence().clear();
		List<FactoryResource> list = this.consistence;
		if (this.getSourceReference() != 0 && ElementKeyMap.isValidType(this.getSourceReference())) {
			list = ElementKeyMap.getInfo(this.getSourceReference()).getConsistence();
		}
		for (final FactoryResource factoryResource : list) {
			this.recalcRawConsistenceRec(factoryResource, factoryResource.count);
		}
		final Iterator<FactoryResource> iterator2 = list.iterator();
		while (iterator2.hasNext()) {
			this.recalcTotalConsistenceRec(iterator2.next());
		}
	}
	
	public ElementCountMap getRawBlocks() {
		return this.rawBlocks;
	}
	
	public List<FactoryResource> getRawConsistence() {
		return this.rawConsistence;
	}
	
	public List<FactoryResource> getTotalConsistence() {
		return this.totalConsistence;
	}
	
	public boolean isExtendedTexture() {
		return this.extendedTexture;
	}
	
	public void setBuildIconToFree() {
		int n = 0;
		final ShortOpenHashSet set = new ShortOpenHashSet();
		short[] typeList;
		for (int length = (typeList = ElementKeyMap.typeList()).length, i = 0; i < length; ++i) {
			final Short value = typeList[i];
			n = (short)Math.max(n, ElementKeyMap.getInfo(value).getBuildIconNum());
			set.add((short)ElementKeyMap.getInfo(value).getBuildIconNum());
		}
		for (final Short n2 : AddElementEntryDialog.addedBuildIcons) {
			n = (short)Math.max(n, n2);
			set.add(n2);
		}
		for (short buildIconNum = 0; buildIconNum < n + 2; ++buildIconNum) {
			if (!set.contains(buildIconNum)) {
				this.setBuildIconNum(buildIconNum);
				return;
			}
		}
	}
	
	public boolean isReactorChamberAny() {
		return this.isReactorChamberGeneral() || this.isReactorChamberSpecific();
	}
	
	public boolean isReactorChamberGeneral() {
		return this.chamberGeneral;
	}
	
	public boolean isReactorChamberSpecific() {
		return this.chamberRoot != 0;
	}
	
	public short getComputer() {
		return (short)this.computerType;
	}
	
	public boolean needsComputer() {
		return ElementKeyMap.isValidType(this.computerType);
	}
	
	public ShortSet getChamberChildrenOnLevel(final ShortSet set) {
		set.addAll((ShortCollection)this.chamberChildren);
		final ElementInformation info;
		if (this.chamberParent != 0 && (info = ElementKeyMap.getInfo(this.chamberParent)).chamberUpgradesTo == this.id) {
			info.getChamberChildrenOnLevel(set);
			set.remove((short)info.chamberUpgradesTo);
		}
		return set;
	}
	
	public short getChamberUpgradedRoot() {
		if (this.chamberParent == 0) {
			return this.id;
		}
		final ElementInformation info;
		if ((info = ElementKeyMap.getInfo(this.chamberParent)).chamberUpgradesTo == this.id) {
			return info.getChamberUpgradedRoot();
		}
		return this.id;
	}
	
	public boolean isChamberChildrenUpgradableContains(final short n) {
		final ElementInformation info;
		return this.chamberChildren.contains(n) || (this.chamberParent != 0 && (info = ElementKeyMap.getInfo(this.chamberParent)).chamberUpgradesTo == this.id && info.isChamberChildrenUpgradableContains(n));
	}
	
	public boolean isChamberUpgraded() {
		return ElementKeyMap.isValidType(this.chamberParent) && ElementKeyMap.getInfo(this.chamberParent).chamberUpgradesTo == this.id;
	}
	
	public void sanatizeReactorValues() {
		if (this.chamberParent != 0 && (!ElementKeyMap.isValidType(this.chamberParent) || !ElementKeyMap.isChamber((short)this.chamberParent))) {
			System.err.println("SANATIZED REACTOR chamberParent " + this.getName() + " -> " + ElementKeyMap.toString(this.chamberParent));
			this.chamberParent = 0;
		}
		if (this.chamberRoot != 0 && (!ElementKeyMap.isValidType(this.chamberRoot) || !ElementKeyMap.isChamber((short)this.chamberRoot))) {
			System.err.println("SANATIZED REACTOR chamberRoot " + this.getName() + " -> " + ElementKeyMap.toString(this.chamberRoot));
			this.chamberRoot = 0;
		}
		if (this.chamberUpgradesTo != 0 && (!ElementKeyMap.isValidType(this.chamberUpgradesTo) || !ElementKeyMap.isChamber((short)this.chamberUpgradesTo))) {
			System.err.println("SANATIZED REACTOR chamberUpgradesTo " + this.getName() + " -> " + ElementKeyMap.toString(this.chamberUpgradesTo));
			this.chamberUpgradesTo = 0;
		}
		final ShortIterator iterator = this.chamberPrerequisites.iterator();
		while (iterator.hasNext()) {
			final short nextShort;
			if ((nextShort = iterator.nextShort()) != 0 && (!ElementKeyMap.isValidType(nextShort) || !ElementKeyMap.isChamber(nextShort))) {
				System.err.println("SANATIZED REACTOR chamberPrereq " + this.getName() + " -> " + ElementKeyMap.toString(nextShort));
				iterator.remove();
			}
		}
		final ShortIterator iterator2 = this.chamberChildren.iterator();
		while (iterator2.hasNext()) {
			final short nextShort2;
			if ((nextShort2 = iterator2.nextShort()) != 0 && (!ElementKeyMap.isValidType(nextShort2) || !ElementKeyMap.isChamber(nextShort2))) {
				System.err.println("SANATIZED REACTOR chamberChildren " + this.getName() + " -> " + ElementKeyMap.toString(nextShort2));
				iterator2.remove();
			}
		}
		if (this.isReactorChamberSpecific() || this.getId() == 1008 || this.getId() == 1010 || this.getId() == 1009) {
			if (this.reactorHp == 0) {
				this.reactorHp = 10;
			}
		}
		else {
			this.reactorHp = 0;
		}
	}
	
	public String getChamberEffectInfo(final ConfigPool configPool) {
		if (this.chamberConfigGroupsLowerCase.isEmpty()) {
			return Lng.ORG_SCHEMA_GAME_COMMON_DATA_ELEMENT_ELEMENTINFORMATION_10;
		}
		final StringBuffer sb = new StringBuffer();
		final Iterator<String> iterator = this.chamberConfigGroupsLowerCase.iterator();
		while (iterator.hasNext()) {
			final ConfigGroup configGroup;
			if ((configGroup = configPool.poolMapLowerCase.get(iterator.next())) != null) {
				sb.append(configGroup.getEffectDescription());
			}
		}
		return sb.toString().trim();
	}
	
	private float getChamberCapacityBranchRec(float n) {
		ElementInformation chamber = this;
		while (ElementKeyMap.isValidType(chamber.chamberParent)) {
			final ElementInformation infoFast = ElementKeyMap.getInfoFast(chamber.chamberParent);
			n += infoFast.chamberCapacity;
			chamber = infoFast;
		}
		return n;
	}
	
	public float getChamberCapacityBranch() {
		return this.getChamberCapacityBranchRec(this.chamberCapacity);
	}
	
	public int getSourceReference() {
		if (this.chamberRoot != 0) {
			return this.chamberRoot;
		}
		return this.sourceReference;
	}
	
	public void setSourceReference(final int sourceReference) {
		this.sourceReference = sourceReference;
	}
	
	public float getChamberCapacityWithUpgrades() {
		float chamberCapacity = this.chamberCapacity;
		if (this.isChamberUpgraded() && ElementKeyMap.isValidType(this.chamberParent)) {
			chamberCapacity += ElementKeyMap.getInfo(this.chamberParent).getChamberCapacityWithUpgrades();
		}
		return chamberCapacity;
	}
	
	public boolean isChamberPermitted(final SimpleTransformableSendableObject.EntityType entityType) {
		if (this.chamberPermission == 0) {
			return true;
		}
		switch (entityType) {
			case PLANET_CORE:
			case PLANET_ICO:
			case PLANET_SEGMENT: {
				return (this.chamberPermission & 0x4) == 0x4;
			}
			case SHIP: {
				return (this.chamberPermission & 0x1) == 0x1;
			}
			case SHOP:
			case SPACE_STATION: {
				return (this.chamberPermission & 0x2) == 0x2;
			}
			default: {
				return false;
			}
		}
	}
	
	public String getDescriptionIncludingChamberUpgraded() {
		if (this.isChamberUpgraded()) {
			return ElementKeyMap.getInfo(this.chamberParent).getDescription();
		}
		return this.getDescription();
	}
	
	public boolean isThisOrParentChamberMutuallyExclusive(final short n) {
		return this.chamberMutuallyExclusive.contains(n) || (ElementKeyMap.isValidType(this.chamberParent) && ElementKeyMap.getInfoFast(this.chamberParent).isThisOrParentChamberMutuallyExclusive(n));
	}
	
	public boolean isArmor() {
		return this.armorValue > 0.0f;
	}
	
	public float getMaxHitPointsOneDivByByte() {
		return 0.007874016f;
	}
	
	public byte getMaxHitPointsByte() {
		return 127;
	}
	
	public void setMaxHitPointsE(final int maxHitPointsFull) {
		assert maxHitPointsFull > 0;
		this.maxHitPointsFull = maxHitPointsFull;
		this.maxHitpointsInverse = 1.0 / maxHitPointsFull;
		this.maxHitpointsFullToByte = 127.0 / maxHitPointsFull;
		this.maxHitpointsByteToFull = maxHitPointsFull / 127.0;
	}
	
	public short convertToByteHp(final int n) {
		return (short)Math.max(0L, Math.min(127L, Math.round(n * this.maxHitpointsFullToByte)));
	}
	
	public int convertToFullHp(final short n) {
		return (int)(n * this.maxHitpointsByteToFull);
	}
	
	public void setHpOldByte(final short oldHitpoints) {
		this.oldHitpoints = oldHitpoints;
	}
	
	public short getHpOldByte() {
		return this.oldHitpoints;
	}
	
	public float getArmorValue() {
		return this.armorValue;
	}
	
	public void setArmorValue(final float armorValue) {
		this.armorValue = armorValue;
	}
	
	public boolean isMineAddOn() {
		return this.id == 365 || this.id == 364 || this.id == 366 || this.id == 363;
	}
	
	public boolean isMineType() {
		return this.id == 355 || this.id == 356 || this.id == 358;
	}
	
	public boolean isDecorative() {
		return !this.isArmor() && !this.systemBlock;
	}
	
	public static boolean canBeControlledOld(final short n, final short n2) {
		if (n != 0 && n2 != 0) {
			final ElementInformation info = ElementKeyMap.getInfo(n);
			final ElementInformation info2 = ElementKeyMap.getInfo(n2);
			return info.getId() == 1 && info2.isEffectCombinationController();
		}
		return false;
	}
	
	public boolean isBeacon() {
		return this.beacon;
	}
	
	static {
		rDesc = new String[] { "ore", "plant", "basic", "Cubatom-Splittable", "manufactory", "advanced", "capsule" };
		slabStrings = new String[] { "full block", "3/4 block", "1/2 block", "1/4 block" };
	}
	
	public enum EIC
	{
		BASICS("Basics"), 
		MODELS("Models"), 
		TEXTURES("Textures"), 
		FEATURES("Features"), 
		HP_ARMOR("Hp & Armor"), 
		CRAFTING_ECONOMY("Crafting & Economy"), 
		POWER_REACTOR("Power & Reactor"), 
		DEPRECATED("Deprecated");
		
		public boolean collapsed;
		private final String name;
		
		private EIC(final String name2) {
			this.collapsed = true;
			this.name = name2;
		}
		
		public final String getName() {
			return this.name;
		}
	}
	
	public enum ResourceInjectionType
	{
		OFF(0), 
		ORE(1), 
		FLORA(17);
		
		public final int index;
		
		private ResourceInjectionType(final int index) {
			this.index = index;
		}
	}
	
	public static class LodCollision
	{
		public String modelId;
		public MeshGroup meshGroup;
		public ConvexHullShapeExt[] hulls;
		public CollisionShape shape;
		public boolean valid;
		public LodCollisionType type;
		public BlockStyle blockTypeToEmulate;
		public int colslab;
		private Vector3f localPosTmp;
		private Transform tmpTrans0;
		private Transform tmpTrans1;
		private Matrix3f rotTmp;
		
		public LodCollision() {
			this.valid = true;
			this.type = LodCollisionType.BLOCK_TYPE;
			this.blockTypeToEmulate = BlockStyle.NORMAL;
			this.localPosTmp = new Vector3f();
			this.tmpTrans0 = new Transform();
			this.tmpTrans1 = new Transform();
			this.rotTmp = new Matrix3f();
		}
		
		public void load() {
			if (this.type == LodCollisionType.NONE) {
				return;
			}
			if (this.type == LodCollisionType.BLOCK_TYPE) {
				return;
			}
			this.meshGroup = (MeshGroup)Controller.getResLoader().getMesh(this.modelId);
			if (this.meshGroup == null) {
				throw new RuntimeException("Collision Mesh Not found: '" + this.modelId + "'");
			}
			int n = 0;
			final Iterator<AbstractSceneNode> iterator = this.meshGroup.getChilds().iterator();
			while (iterator.hasNext()) {
				if (iterator.next() instanceof Mesh) {
					++n;
				}
			}
			this.hulls = new ConvexHullShapeExt[n];
			final CompoundShape shape = new CompoundShape();
			int n2 = 0;
			final Iterator<AbstractSceneNode> iterator2 = this.meshGroup.getChilds().iterator();
			while (iterator2.hasNext()) {
				final AbstractSceneNode abstractSceneNode;
				if ((abstractSceneNode = iterator2.next()) instanceof Mesh) {
					final Mesh mesh = (Mesh)abstractSceneNode;
					final ConvexHullShapeExt convexHullShapeExt = new ConvexHullShapeExt(mesh.getVerticesListInstance());
					this.hulls[n2] = convexHullShapeExt;
					shape.addChildShape(mesh.getInitialTransformWithoutScale(new Transform()), (CollisionShape)convexHullShapeExt);
					++n2;
				}
			}
			this.shape = (CollisionShape)shape;
		}
		
		public boolean isValid() {
			return this.valid;
		}
		
		@Override
		public String toString() {
			if (this.type == LodCollisionType.CONVEX_HULL) {
				return "LOD_COLLSISION(Type: " + this.type.type + "; Model: '" + this.modelId + "')";
			}
			return "LOD_COLLSISION(Type: " + this.type.type + "; Model: '" + this.blockTypeToEmulate + "')";
		}
		
		public void parse(Node item) {
			final NodeList childNodes = item.getChildNodes();
			final Node namedItem;
			if ((namedItem = item.getAttributes().getNamedItem("type")) == null) {
				return;
			}
			final String nodeValue = namedItem.getNodeValue();
			for (int i = 0; i < LodCollisionType.values().length; ++i) {
				if (LodCollisionType.values()[i].type.toLowerCase(Locale.ENGLISH).equals(nodeValue.toLowerCase(Locale.ENGLISH))) {
					this.type = LodCollisionType.values()[i];
					break;
				}
			}
			if (this.type == null) {
				throw new RuntimeException("NO TYPE: " + nodeValue);
			}
			switch (this.type) {
				case NONE: {
					break;
				}
				case BLOCK_TYPE: {
					this.colslab = Integer.parseInt(item.getAttributes().getNamedItem("slab").getNodeValue());
					this.blockTypeToEmulate = BlockStyle.parse((org.w3c.dom.Element)item);
					break;
				}
				case CONVEX_HULL: {
					for (int j = 0; j < childNodes.getLength(); ++j) {
						if ((item = childNodes.item(j)).getNodeType() == 1 && item.getNodeName().toLowerCase(Locale.ENGLISH).equals("mesh")) {
							this.modelId = item.getTextContent();
						}
					}
					break;
				}
				default: {
					throw new RuntimeException("UNKNOWN TYPE: " + this.type.name());
				}
			}
			this.valid = true;
		}
		
		public void write(final org.w3c.dom.Element element, final Document document) {
			final Attr attribute;
			(attribute = document.createAttribute("type")).setValue(this.type.type);
			element.getAttributes().setNamedItem(attribute);
			switch (this.type) {
				case NONE: {}
				case BLOCK_TYPE: {
					this.blockTypeToEmulate.write(element, document);
					final Attr attribute2;
					(attribute2 = document.createAttribute("slab")).setNodeValue(String.valueOf(this.colslab));
					element.getAttributes().setNamedItem(attribute2);
				}
				case CONVEX_HULL: {
					final org.w3c.dom.Element element2;
					(element2 = document.createElement("Mesh")).setTextContent(this.modelId);
					element.appendChild(element2);
				}
				default: {
					throw new RuntimeException("UNKNOWN TYPE: " + this.type.name());
				}
			}
		}
		
		public CollisionShape getShape(final short n, final byte b, final Transform transform) {
			switch (this.type) {
				case BLOCK_TYPE: {
					return (CollisionShape)BlockShapeAlgorithm.getShape(this.blockTypeToEmulate, b);
				}
				case CONVEX_HULL: {
					final ElementInformation infoFast = ElementKeyMap.getInfoFast(n);
					transform.set(((Oriencube)BlockShapeAlgorithm.algorithms[5][(infoFast.blockStyle == BlockStyle.SPRITE) ? (b % 6 << 2) : b]).getBasicTransform());
					if (infoFast.getBlockStyle() == BlockStyle.SPRITE) {
						this.rotTmp.setIdentity();
						this.rotTmp.rotX(SingleBlockDrawer.timesR * 1.5707964f);
						transform.basis.mul(this.rotTmp);
					}
					transform.origin.set(0.0f, 0.0f, 0.0f);
					return this.shape;
				}
				case NONE: {
					return null;
				}
				default: {
					throw new RuntimeException("UNKNOWN TYPE: " + this.type.name());
				}
			}
		}
		
		public enum LodCollisionType
		{
			BLOCK_TYPE("BlockType"), 
			CONVEX_HULL("ConvexHull"), 
			NONE("None");
			
			public final String type;
			
			private LodCollisionType(final String type) {
				this.type = type;
			}
			
			@Override
			public final String toString() {
				return this.type;
			}
		}
	}
}
