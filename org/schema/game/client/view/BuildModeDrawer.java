// 
// Decompiled by Procyon v0.5.36
// 

package org.schema.game.client.view;

import it.unimi.dsi.fastutil.ints.IntSet;
import org.schema.game.common.controller.elements.ElementCollectionManager;
import org.schema.game.common.data.player.PlayerCharacter;
import org.schema.game.common.data.element.ElementClassNotFoundException;
import org.schema.schine.input.KeyboardMappings;
import org.schema.game.common.data.creature.AICreature;
import org.schema.game.common.data.player.AbstractCharacter;
import org.schema.common.util.StringTools;
import org.schema.schine.common.language.Lng;
import org.schema.schine.input.Keyboard;
import org.schema.game.common.data.physics.PairCachingGhostObjectAlignable;
import org.schema.game.common.data.physics.RigidBodySegmentController;
import com.bulletphysics.collision.dispatch.CollisionWorld;
import org.schema.game.common.data.physics.PhysicsExt;
import org.schema.game.common.data.physics.ModifiedDynamicsWorld;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.schema.game.client.view.gui.shiphud.HudIndicatorOverlay;
import org.schema.schine.graphicsengine.core.Timer;
import org.schema.game.client.controller.manager.ingame.ship.ShipControllerManager;
import org.schema.game.client.controller.manager.ingame.SegmentControlManager;
import org.schema.game.client.controller.manager.ingame.character.PlayerExternalController;
import org.schema.game.client.controller.manager.ingame.SegmentBuildController;
import org.schema.game.client.view.cubes.shapes.BlockStyle;
import org.schema.game.common.data.VoidUniqueSegmentPiece;
import org.schema.game.client.view.cubes.shapes.BlockShapeAlgorithm;
import org.schema.common.FastMath;
import org.schema.schine.graphicsengine.shader.Shader;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import org.schema.game.client.controller.manager.ingame.PlayerInteractionControlManager;
import org.schema.game.common.controller.elements.power.reactor.tree.ReactorTree;
import org.schema.game.client.view.camera.BuildShipCamera;
import org.schema.game.common.controller.PositionControl;
import org.schema.game.common.controller.elements.ManagerModuleCollection;
import java.util.ConcurrentModificationException;
import org.schema.game.common.controller.elements.ControlBlockElementCollectionManager;
import org.schema.game.common.controller.elements.UsableControllableElementManager;
import org.schema.game.common.controller.elements.power.reactor.PowerInterface;
import javax.vecmath.Tuple4f;
import org.schema.game.common.controller.elements.power.reactor.StabilizerUnit;
import org.schema.game.common.controller.elements.StabBonusCalcStyle;
import org.schema.common.util.linAlg.TransformTools;
import java.util.List;
import org.schema.game.common.controller.elements.ShieldLocal;
import org.schema.schine.graphicsengine.forms.debug.DebugLine;
import org.schema.schine.graphicsengine.core.Controller;
import org.schema.common.util.linAlg.Triangle;
import org.schema.game.common.controller.elements.power.reactor.MainReactorUnit;
import org.lwjgl.opengl.GL11;
import org.schema.game.client.controller.manager.ingame.SymmetryPlanes;
import org.schema.game.client.controller.manager.ingame.BuildToolsManager;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.controller.elements.ShieldContainerInterface;
import org.schema.schine.graphicsengine.shader.Shaderable;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.game.common.controller.elements.VoidElementManager;
import org.schema.schine.graphicsengine.shader.ShaderLibrary;
import org.schema.game.common.data.element.Element;
import org.schema.game.client.view.cubes.shapes.orientcube.Oriencube;
import javax.vecmath.Tuple3f;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.controller.Ship;
import org.schema.game.common.data.ManagedSegmentController;
import java.util.Iterator;
import org.schema.game.common.controller.rails.RailRelation;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;
import org.schema.schine.graphicsengine.core.settings.EngineSettings;
import org.schema.schine.graphicsengine.core.GlUtil;
import org.schema.game.common.controller.elements.dockingBlock.DockingBlockCollectionManager;
import org.schema.game.common.data.physics.SegmentTraversalInterface;
import org.schema.game.common.data.physics.InnerSegmentIterator;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.ArmorValue;
import org.schema.game.common.controller.ArmorCheckTraverseHandler;
import org.schema.schine.graphicsengine.util.timer.SinusTimerUtil;
import javax.vecmath.Vector4f;
import org.schema.common.util.linAlg.PolygonToolsVars;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import org.schema.game.common.data.world.GameTransformable;
import org.schema.schine.graphicsengine.forms.Mesh;
import org.schema.game.common.data.world.Segment;
import org.schema.common.util.linAlg.Vector3b;
import org.schema.game.client.view.effects.ConstantIndication;
import org.schema.schine.graphicsengine.util.timer.LinearTimerUtil;
import org.schema.game.common.data.physics.CubeRayCastResult;
import org.schema.game.client.data.GameClientState;
import org.schema.common.util.linAlg.Vector3i;
import com.bulletphysics.linearmath.Transform;
import org.schema.game.client.view.tools.SingleBlockDrawer;
import javax.vecmath.Vector3f;
import org.schema.game.common.data.element.ElementInformation;
import org.schema.game.common.data.SegmentPiece;
import org.schema.schine.graphicsengine.core.Drawable;

public class BuildModeDrawer implements Drawable
{
	public static SegmentPiece currentPiece;
	public static ElementInformation currentInfo;
	public static int currentSide;
	public static SegmentPiece selectedBlock;
	public static ElementInformation selectedInfo;
	public static float currentOptStabDist;
	public static float currentStabDist;
	private final Vector3f dist;
	private final SingleBlockDrawer drawer;
	Transform t;
	Transform tinv;
	Vector3i pp;
	Vector3i posTmp;
	int i;
	private boolean firstDraw;
	private GameClientState state;
	public CubeRayCastResult testRayCollisionPoint;
	private LinearTimerUtil linearTimer;
	private LinearTimerUtil linearTimerC;
	private ConstantIndication indication;
	private Vector3b lastCubePos;
	private Segment lastSegment;
	private Mesh mesh;
	private int blockOrientation;
	private long blockChangedTime;
	private SelectionShader selectionShader;
	private SelectionShader selectionShaderSolid;
	private Vector3i toBuildPos;
	private Vector3i loockingAtPos;
	private Vector3f pTmp;
	private boolean flagUpdate;
	private GameTransformable currentObject;
	private boolean drawDebug;
	StringBuffer touching;
	private final ShortOpenHashSet conDrw;
	private LinearTimerUtil linearTimerSl;
	private final PolygonToolsVars v;
	public static long currentPieceIndexIntegrity;
	public static double currentPieceIntegrity;
	public static double currentStabEfficiency;
	public static boolean inReactorAlignSlider;
	public static boolean inReactorAlignAlwaysVisible;
	public static int inReactorAlignSliderSelectedAxis;
	private final Vector3f tmp;
	private final Vector4f stabColor;
	private final SinusTimerUtil colorMod;
	private int currentSelectedStabSide;
	private final ArmorCheckTraverseHandler pt;
	private CubeRayCastResult rayCallbackTraverse;
	public static ArmorValue armorValue;
	private final Vector3f cPosA;
	private final Vector3f cPosB;
	private long lastArmorCheck;
	
	public BuildModeDrawer(final GameClientState state) {
		this.dist = new Vector3f();
		this.drawer = new SingleBlockDrawer();
		this.t = new Transform();
		this.tinv = new Transform();
		this.pp = new Vector3i();
		this.posTmp = new Vector3i();
		this.i = 0;
		this.firstDraw = true;
		this.lastCubePos = new Vector3b();
		this.lastSegment = null;
		this.blockOrientation = -1;
		this.toBuildPos = new Vector3i();
		this.loockingAtPos = new Vector3i();
		this.pTmp = new Vector3f();
		this.touching = new StringBuffer();
		this.conDrw = new ShortOpenHashSet();
		this.linearTimerSl = new LinearTimerUtil(1.0f);
		this.v = new PolygonToolsVars();
		this.tmp = new Vector3f();
		this.stabColor = new Vector4f();
		this.colorMod = new SinusTimerUtil(7.0f);
		this.currentSelectedStabSide = -1;
		this.pt = new ArmorCheckTraverseHandler();
		this.rayCallbackTraverse = new CubeRayCastResult(new Vector3f(), new Vector3f(), (Object)null, new SegmentController[0]) {
			@Override
			public InnerSegmentIterator newInnerSegmentIterator() {
				return BuildModeDrawer.this.pt;
			}
		};
		this.cPosA = new Vector3f();
		this.cPosB = new Vector3f();
		this.state = state;
		this.linearTimer = new LinearTimerUtil();
		this.linearTimerC = new LinearTimerUtil(6.1f);
		this.indication = new ConstantIndication(new Transform(), "");
	}
	
	public void addBlockedDockIndicator(final SegmentPiece segmentPiece, final SegmentController segmentController, final DockingBlockCollectionManager dockingBlockCollectionManager) {
	}
	
	@Override
	public void cleanUp() {
		this.drawer.cleanUp();
	}
	
	@Override
	public void draw() {
		if (this.firstDraw) {
			this.onInit();
			GlUtil.printGlErrorCritical();
		}
		if (EngineSettings.G_DRAW_NO_OVERLAYS.isOn()) {
			return;
		}
		if (this.touching.length() > 0) {
			this.touching.delete(0, this.touching.length());
		}
		this.drawDebug = WorldDrawer.drawError;
		if (this.drawDebug) {
			GlUtil.printGlErrorCritical();
		}
		final SimpleTransformableSendableObject currentPlayerObject = this.state.getCurrentPlayerObject();
		if (!this.getShipControllerManager().getSegmentBuildController().isTreeActive() && this.getPlayerManager().isActive()) {
			WorldDrawer.insideBuildMode = false;
			this.drawCharacterExternalMode();
			return;
		}
		if (this.drawDebug) {
			GlUtil.printGlErrorCritical();
		}
		if (currentPlayerObject instanceof SegmentController && this.getPlayerIntercationManager().isInAnyStructureBuildMode()) {
			this.drawFor((SegmentController)currentPlayerObject);
		}
	}
	
	public void drawForAll(SegmentController root) {
		root = root.railController.getRoot();
		this.drawForChilds(root);
	}
	
	public void drawForChilds(final SegmentController segmentController) {
		this.drawFor(segmentController);
		final Iterator<RailRelation> iterator = segmentController.railController.next.iterator();
		while (iterator.hasNext()) {
			this.drawForChilds(iterator.next().docked.getSegmentController());
		}
	}
	
	private void drawFor(final SegmentController segmentController) {
		this.currentSelectedStabSide = -1;
		if (segmentController == null) {
			return;
		}
		if (this.drawDebug) {
			GlUtil.printGlErrorCritical();
		}
		final ManagerContainer managerContainer;
		if (segmentController instanceof ManagedSegmentController && (managerContainer = ((ManagedSegmentController)segmentController).getManagerContainer()).isUsingPowerReactors() && this.getPlayerIntercationManager().getSelectedTypeWithSub() == 1009) {
			managerContainer.getStabilizer().drawMesh();
		}
		this.drawPowerHull(segmentController, true);
		if (this.drawDebug) {
			GlUtil.printGlErrorCritical();
		}
		WorldDrawer.insideBuildMode = true;
		if (segmentController instanceof Ship) {
			final BuildToolsManager buildToolsManager;
			final boolean addMode = (buildToolsManager = this.state.getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager().getBuildToolsManager()).isAddMode();
			if (buildToolsManager.getBuildHelper() != null) {
				buildToolsManager.getBuildHelper().draw();
			}
			if (this.drawDebug) {
				GlUtil.printGlErrorCritical();
			}
			GlUtil.glPushMatrix();
			if (this.drawDebug) {
				GlUtil.printGlErrorCritical();
			}
			boolean b = true;
			if (!ElementKeyMap.isValidType(this.getPlayerIntercationManager().getSelectedTypeWithSub())) {
				b = false;
			}
			if (this.drawDebug) {
				GlUtil.printGlErrorCritical();
			}
			final Transform toBuildTransform = this.getToBuildTransform(segmentController);
			if (buildToolsManager.isInCreateDockingMode() && buildToolsManager.getBuildToolCreateDocking().core != null) {
				final Vector3f absolutePos;
				final Vector3f vector3f = absolutePos = buildToolsManager.getBuildToolCreateDocking().core.getAbsolutePos(new Vector3f());
				vector3f.x -= 16.0f;
				final Vector3f vector3f2 = absolutePos;
				vector3f2.y -= 16.0f;
				final Vector3f vector3f3 = absolutePos;
				vector3f3.z -= 16.0f;
				segmentController.getWorldTransform().transform(absolutePos);
				final Transform transform;
				(transform = new Transform((Transform)segmentController.getWorldTransform())).origin.set((Tuple3f)absolutePos);
				buildToolsManager.getBuildToolCreateDocking().core.setOrientation((byte)buildToolsManager.getBuildToolCreateDocking().potentialCoreOrientation);
				final Oriencube oriencube = (Oriencube)buildToolsManager.getBuildToolCreateDocking().core.getAlgorithm((short)662);
				if (this.drawDebug) {
					GlUtil.printGlErrorCritical();
				}
				final Vector3f vector3f4;
				(vector3f4 = new Vector3f(Element.DIRECTIONSf[oriencube.getOrientCubePrimaryOrientationSwitchedLeftRight()])).scale(0.5f);
				segmentController.getWorldTransform().basis.transform((Tuple3f)vector3f4);
				transform.origin.add((Tuple3f)vector3f4);
				this.drawOrientationArrow(transform, oriencube.getOrientCubeSecondaryOrientation());
				if (this.drawDebug) {
					GlUtil.printGlErrorCritical();
				}
			}
			else if (toBuildTransform != null && b) {
				this.drawOrientationArrow(toBuildTransform, this.state.getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager().getBlockOrientation());
			}
			if (this.drawDebug) {
				GlUtil.printGlErrorCritical();
			}
			if (this.isDrawPreview() && !buildToolsManager.isInCreateDockingMode() && this.drawToBuildBox(segmentController, this.drawer, ShaderLibrary.selectionShader, this.selectionShader, addMode) != null && this.getPlayerIntercationManager().getSelectedTypeWithSub() == 1009 && VoidElementManager.isUsingReactorDistance()) {
				this.drawReactorDistance(segmentController, ElementCollection.getIndex(this.toBuildPos));
			}
			if (this.drawDebug) {
				GlUtil.printGlErrorCritical();
			}
			this.mesh.loadVBO(true);
			if (this.drawDebug) {
				GlUtil.printGlErrorCritical();
			}
			GlUtil.glDisable(2884);
			if (EngineSettings.G_BASIC_SELECTION_BOX.getCurrentState() != null) {
				ShaderLibrary.selectionShader.setShaderInterface(this.selectionShader);
				ShaderLibrary.selectionShader.load();
				this.drawToBuildBox(segmentController, null, ShaderLibrary.selectionShader, this.selectionShader, addMode);
				if (this.drawDebug) {
					GlUtil.printGlErrorCritical();
				}
			}
			else {
				ShaderLibrary.solidSelectionShader.setShaderInterface(this.selectionShaderSolid);
				ShaderLibrary.solidSelectionShader.load();
				this.drawToBuildBox(segmentController, null, ShaderLibrary.solidSelectionShader, this.selectionShaderSolid, addMode);
				if (this.drawDebug) {
					GlUtil.printGlErrorCritical();
				}
			}
			if (this.drawDebug) {
				GlUtil.printGlErrorCritical();
			}
			this.drawCreateDock(segmentController);
			if (this.drawDebug) {
				GlUtil.printGlErrorCritical();
			}
			this.drawCameraHighlight(segmentController);
			if (this.drawDebug) {
				GlUtil.printGlErrorCritical();
			}
			ShaderLibrary.selectionShader.setShaderInterface(this.selectionShader);
			ShaderLibrary.selectionShader.load();
			GlUtil.updateShaderVector4f(ShaderLibrary.selectionShader, "selectionColor", 1.0f, 1.0f, 0.0f, 0.65f);
			GlUtil.updateShaderVector4f(ShaderLibrary.selectionShader, "selectionColor", 0.9f, 0.6f, 0.2f, 0.65f);
			this.drawCurrentSelectedElement(segmentController, this.getShipControllerManager().getSegmentBuildController().getSelectedBlock());
			this.drawControlledElements(segmentController, this.getShipControllerManager().getSegmentBuildController().getSelectedBlock());
			GlUtil.glEnable(2884);
			ShaderLibrary.selectionShader.unload();
			this.mesh.unloadVBO(true);
			if (this.drawDebug) {
				GlUtil.printGlErrorCritical();
			}
			final SymmetryPlanes symmetryPlanes;
			if (((symmetryPlanes = this.getActiveBuildController().getSymmetryPlanes()).isXyPlaneEnabled() || symmetryPlanes.isXzPlaneEnabled() || symmetryPlanes.isYzPlaneEnabled()) && EngineSettings.G_SHOW_SYMMETRY_PLANES.isOn()) {
				this.drawCurrentSymetriePlanesElement(segmentController);
			}
			if (this.drawDebug) {
				GlUtil.printGlErrorCritical();
			}
			if (symmetryPlanes.getPlaceMode() > 0) {
				this.drawCurrentSymetriePlanesElement(segmentController, null, symmetryPlanes.getPlaceMode(), this.toBuildPos, symmetryPlanes.getExtraDist() * 0.5f);
			}
			if (this.drawDebug) {
				GlUtil.printGlErrorCritical();
			}
			if (segmentController.getMass() > 0.0f && buildToolsManager.showCenterOfMass) {
				this.drawCenterOfMass(segmentController);
			}
			if (this.drawDebug) {
				GlUtil.printGlErrorCritical();
			}
			if (segmentController instanceof ManagedSegmentController && ((ManagedSegmentController)segmentController).getManagerContainer() instanceof ShieldContainerInterface) {
				this.drawLocalShields((ShieldContainerInterface)((ManagedSegmentController)segmentController).getManagerContainer());
			}
			if (VoidElementManager.isUsingReactorDistance()) {
				this.drawStabilizerOrientation(segmentController);
				this.drawReactorCoordinateSystems(segmentController);
			}
			if (this.drawDebug) {
				GlUtil.printGlErrorCritical();
			}
			if (buildToolsManager.isInCreateDockingMode()) {
				this.drawCreateDockingMode(segmentController);
			}
			this.drawToBuildConnection(segmentController);
			if (this.drawDebug) {
				GlUtil.printGlErrorCritical();
			}
			GlUtil.glPopMatrix();
		}
		else {
			GlUtil.glPushMatrix();
			final BuildToolsManager buildToolsManager2;
			final boolean addMode2 = (buildToolsManager2 = this.state.getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager().getBuildToolsManager()).isAddMode();
			if (buildToolsManager2.getBuildHelper() != null) {
				buildToolsManager2.getBuildHelper().draw();
			}
			if (this.isDrawPreview() && !buildToolsManager2.isInCreateDockingMode() && this.drawToBuildBox(segmentController, this.drawer, ShaderLibrary.selectionShader, this.selectionShader, addMode2) != null && this.getPlayerIntercationManager().getSelectedTypeWithSub() == 1009 && VoidElementManager.isUsingReactorDistance()) {
				this.drawReactorDistance(segmentController, ElementCollection.getIndex(this.toBuildPos));
			}
			this.mesh.loadVBO(true);
			GlUtil.glDisable(2884);
			Transform transform2;
			if (EngineSettings.G_BASIC_SELECTION_BOX.getCurrentState() != null) {
				ShaderLibrary.selectionShader.setShaderInterface(this.selectionShader);
				ShaderLibrary.selectionShader.load();
				GlUtil.updateShaderVector4f(ShaderLibrary.selectionShader, "selectionColor", 0.7f, 0.77f, 0.1f, 0.65f);
				transform2 = this.drawToBuildBox(segmentController, null, ShaderLibrary.selectionShader, this.selectionShader, addMode2);
			}
			else {
				ShaderLibrary.solidSelectionShader.setShaderInterface(this.selectionShaderSolid);
				ShaderLibrary.solidSelectionShader.load();
				GlUtil.updateShaderVector4f(ShaderLibrary.solidSelectionShader, "selectionColor", 0.7f, 0.77f, 0.1f, 0.65f);
				transform2 = this.drawToBuildBox(segmentController, null, ShaderLibrary.solidSelectionShader, this.selectionShaderSolid, addMode2);
			}
			this.drawCreateDock(segmentController);
			this.drawCameraHighlight(segmentController);
			ShaderLibrary.selectionShader.setShaderInterface(this.selectionShader);
			ShaderLibrary.selectionShader.load();
			GlUtil.updateShaderVector4f(ShaderLibrary.selectionShader, "selectionColor", 0.9f, 0.6f, 0.2f, 0.65f);
			this.drawCurrentSelectedElement(segmentController, this.getSegmentControlManager().getSegmentBuildController().getSelectedBlock());
			GlUtil.updateShaderVector4f(ShaderLibrary.selectionShader, "selectionColor", 0.4f, 0.1f, 0.9f, 0.65f);
			this.drawControlledElements(segmentController, this.getSegmentControlManager().getSegmentBuildController().getSelectedBlock());
			GlUtil.glEnable(2884);
			ShaderLibrary.selectionShader.unload();
			this.mesh.unloadVBO(true);
			final SymmetryPlanes symmetryPlanes2;
			if ((symmetryPlanes2 = this.getSegmentControlManager().getSegmentBuildController().getSymmetryPlanes()).isXyPlaneEnabled() || symmetryPlanes2.isXzPlaneEnabled() || symmetryPlanes2.isYzPlaneEnabled()) {
				this.drawCurrentSymetriePlanesElement(segmentController);
			}
			if (symmetryPlanes2.getPlaceMode() > 0) {
				this.drawCurrentSymetriePlanesElement(segmentController, null, symmetryPlanes2.getPlaceMode(), this.toBuildPos, symmetryPlanes2.getExtraDist() * 0.5f);
			}
			if (segmentController instanceof ManagedSegmentController && ((ManagedSegmentController)segmentController).getManagerContainer() instanceof ShieldContainerInterface) {
				this.drawLocalShields((ShieldContainerInterface)((ManagedSegmentController)segmentController).getManagerContainer());
			}
			if (VoidElementManager.isUsingReactorDistance()) {
				this.drawStabilizerOrientation(segmentController);
				this.drawReactorCoordinateSystems(segmentController);
			}
			if (buildToolsManager2.isInCreateDockingMode() && buildToolsManager2.getBuildToolCreateDocking().core != null) {
				final Vector3f absolutePos2;
				final Vector3f vector3f5 = absolutePos2 = buildToolsManager2.getBuildToolCreateDocking().core.getAbsolutePos(new Vector3f());
				vector3f5.x -= 16.0f;
				final Vector3f vector3f6 = absolutePos2;
				vector3f6.y -= 16.0f;
				final Vector3f vector3f7 = absolutePos2;
				vector3f7.z -= 16.0f;
				segmentController.getWorldTransform().transform(absolutePos2);
				final Transform transform3;
				(transform3 = new Transform((Transform)segmentController.getWorldTransform())).origin.set((Tuple3f)absolutePos2);
				buildToolsManager2.getBuildToolCreateDocking().core.setOrientation((byte)buildToolsManager2.getBuildToolCreateDocking().potentialCoreOrientation);
				final Oriencube oriencube2 = (Oriencube)buildToolsManager2.getBuildToolCreateDocking().core.getAlgorithm((short)662);
				final Vector3f vector3f8;
				(vector3f8 = new Vector3f(Element.DIRECTIONSf[oriencube2.getOrientCubePrimaryOrientationSwitchedLeftRight()])).scale(0.5f);
				this.getSegmentControlManager().getEntered().getSegmentController().getWorldTransform().basis.transform((Tuple3f)vector3f8);
				transform3.origin.add((Tuple3f)vector3f8);
				this.drawOrientationArrow(transform3, oriencube2.getOrientCubeSecondaryOrientation());
			}
			else if (transform2 != null) {
				this.drawOrientationArrow(transform2, this.state.getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager().getBlockOrientation());
			}
			GlUtil.glPopMatrix();
		}
		GlUtil.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		BuildModeDrawer.inReactorAlignSlider = false;
	}
	
	private void drawPowerHull(final SegmentController segmentController, final boolean b) {
		if (!this.state.getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager().getBuildToolsManager().reactorHull) {
			return;
		}
		if (b) {
			GL11.glPolygonMode(1032, 6913);
		}
		GlUtil.glPushMatrix();
		GlUtil.glEnable(2896);
		GlUtil.glEnable(3042);
		GlUtil.glBlendFunc(770, 771);
		GlUtil.glBlendFuncSeparate(770, 771, 1, 771);
		GlUtil.glDisable(2903);
		GlUtil.glDisable(2929);
		GlUtil.glColor4f(1.0f, 1.0f, 1.0f, 0.8f);
		if (segmentController != null && segmentController instanceof ManagedSegmentController) {
			GlUtil.glMultMatrix(segmentController.getWorldTransformOnClient());
			final Iterator<MainReactorUnit> iterator = ((ManagedSegmentController)segmentController).getManagerContainer().getPowerInterface().getMainReactors().iterator();
			while (iterator.hasNext()) {
				final MainReactorUnit mainReactorUnit;
				if ((mainReactorUnit = iterator.next()).tris != null) {
					GlUtil.glPushMatrix();
					GL11.glBegin(4);
					GlUtil.glColor4f(0.4f, 0.4f, 0.3f, 0.5f);
					for (int i = 0; i < mainReactorUnit.tris.length; ++i) {
						final Triangle triangle;
						final Vector3f normal = (triangle = mainReactorUnit.tris[i]).getNormal();
						GL11.glVertex3f(triangle.v1.x - 16.0f, triangle.v1.y - 16.0f, triangle.v1.z - 16.0f);
						GL11.glNormal3f(normal.x, normal.y, normal.z);
						GL11.glVertex3f(triangle.v2.x - 16.0f, triangle.v2.y - 16.0f, triangle.v2.z - 16.0f);
						GL11.glNormal3f(normal.x, normal.y, normal.z);
						GL11.glVertex3f(triangle.v3.x - 16.0f, triangle.v3.y - 16.0f, triangle.v3.z - 16.0f);
						GL11.glNormal3f(normal.x, normal.y, normal.z);
					}
					GL11.glEnd();
					GlUtil.glPopMatrix();
				}
			}
		}
		GlUtil.glEnable(2929);
		GlUtil.glDisable(3042);
		GlUtil.glDisable(2896);
		GlUtil.glPopMatrix();
		GlUtil.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GL11.glPolygonMode(1032, 6914);
	}
	
	private void drawCreateDockingMode(final SegmentController segmentController) {
	}
	
	@Override
	public boolean isInvisible() {
		return false;
	}
	
	@Override
	public void onInit() {
		this.mesh = (Mesh)Controller.getResLoader().getMesh("Box").getChilds().get(0);
		this.selectionShader = new SelectionShader(this.mesh.getMaterial().getTexture().getTextureId());
		this.selectionShaderSolid = new SelectionShader(-1);
		this.firstDraw = false;
	}
	
	private void drawCenterOfMass(final SegmentController segmentController) {
		if (segmentController.getPhysicsDataContainer().getShape() != null) {
			final DebugLine[] centerOfMassCross = segmentController.getCenterOfMassCross();
			for (int i = 0; i < centerOfMassCross.length; ++i) {
				centerOfMassCross[i].draw();
			}
		}
	}
	
	public void drawBlock(final long n, final SegmentController segmentController, final LinearTimerUtil linearTimerUtil) {
		this.pTmp.set((float)(ElementCollection.getPosX(n) - 16), (float)(ElementCollection.getPosY(n) - 16), (float)(ElementCollection.getPosZ(n) - 16));
		this.dist.set((float)(ElementCollection.getPosX(n) - 16), (float)(ElementCollection.getPosY(n) - 16), (float)(ElementCollection.getPosZ(n) - 16));
		segmentController.getWorldTransform().transform(this.dist);
		if (Controller.getCamera().isPointInFrustrum(this.dist)) {
			this.dist.sub((Tuple3f)Controller.getCamera().getWorldTransform().origin);
			if (this.dist.length() < 64.0f) {
				GlUtil.glPushMatrix();
				GlUtil.translateModelview(this.pTmp.x, this.pTmp.y, this.pTmp.z);
				final float n2 = 1.05f + linearTimerUtil.getTime() * 0.05f;
				GlUtil.scaleModelview(n2, n2, n2);
				this.mesh.renderVBO();
				GlUtil.glPopMatrix();
			}
		}
	}
	
	public void drawBlock(final Vector3i vector3i, final SegmentController segmentController, final LinearTimerUtil linearTimerUtil) {
		if (vector3i != null) {
			this.pTmp.set((float)(vector3i.x - 16), (float)(vector3i.y - 16), (float)(vector3i.z - 16));
			GlUtil.glPushMatrix();
			GlUtil.translateModelview(this.pTmp.x, this.pTmp.y, this.pTmp.z);
			final float n = 1.05f + linearTimerUtil.getTime() * 0.05f;
			GlUtil.scaleModelview(n, n, n);
			this.mesh.renderVBO();
			GlUtil.glPopMatrix();
		}
	}
	
	public void drawCharacterExternalMode() {
		if (this.firstDraw) {
			this.onInit();
		}
		if (this.blockOrientation != this.state.getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager().getBlockOrientation()) {
			if (this.blockOrientation >= 0) {
				this.blockChangedTime = System.currentTimeMillis();
			}
			this.blockOrientation = this.state.getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager().getBlockOrientation();
		}
		final SegmentPiece selectedBlock;
		if ((selectedBlock = this.getPlayerManager().getSelectedBlock()) != null) {
			GlUtil.glPushMatrix();
			this.mesh.loadVBO(true);
			ShaderLibrary.selectionShader.setShaderInterface(this.selectionShader);
			ShaderLibrary.selectionShader.load();
			GlUtil.updateShaderVector4f(ShaderLibrary.selectionShader, "selectionColor", 0.9f, 0.6f, 0.2f, 0.65f);
			this.drawCurrentSelectedElement(selectedBlock.getSegment().getSegmentController(), selectedBlock);
			GlUtil.updateShaderVector4f(ShaderLibrary.selectionShader, "selectionColor", 0.4f, 0.1f, 0.9f, 0.65f);
			this.drawControlledElements(selectedBlock.getSegment().getSegmentController(), selectedBlock);
			ShaderLibrary.selectionShader.unload();
			this.mesh.unloadVBO(true);
			GlUtil.glPopMatrix();
		}
		if (this.state.getCharacter() != null && System.currentTimeMillis() - this.blockChangedTime < 3000L) {
			this.drawOrientationArrow(this.state.getCharacter().getWorldTransform(), this.state.getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager().getBlockOrientation());
		}
	}
	
	public void drawLocalShields(final ShieldContainerInterface shieldContainerInterface) {
		if (BuildModeDrawer.currentPiece != null && (BuildModeDrawer.currentPiece.getType() == 3 || BuildModeDrawer.currentPiece.getType() == 478)) {
			shieldContainerInterface.getShieldAddOn().getShieldLocalAddOn().markDrawCollectionByBlock(BuildModeDrawer.currentPiece.getAbsoluteIndex());
		}
		if (this.getPlayerIntercationManager().getSelectedTypeWithSub() != 3 && this.getPlayerIntercationManager().getSelectedTypeWithSub() != 478) {
			return;
		}
		if (shieldContainerInterface.getSegmentController().railController.isDocked()) {
			return;
		}
		final List<ShieldLocal> activeShields = shieldContainerInterface.getShieldAddOn().getShieldLocalAddOn().getActiveShields();
		final List<ShieldLocal> inactiveShields = shieldContainerInterface.getShieldAddOn().getShieldLocalAddOn().getInactiveShields();
		if (!activeShields.isEmpty() || !inactiveShields.isEmpty()) {
			GlUtil.glDisable(3553);
			GlUtil.glDisable(2896);
			GlUtil.glEnable(2903);
			GlUtil.glEnable(3042);
			GlUtil.glBlendFunc(770, 1);
			final Mesh mesh = (Mesh)Controller.getResLoader().getMesh("Sphere").getChilds().get(0);
			GlUtil.glPushMatrix();
			this.t.set((Transform)shieldContainerInterface.getSegmentController().getWorldTransform());
			GlUtil.glMultMatrix(this.t);
			final Vector4f vector4f = new Vector4f(1.0f, 1.0f, 1.0f, 0.1f);
			final Vector4f vector4f2 = new Vector4f(1.0f, 0.5f, 0.5f, 0.2f);
			final Vector4f vector4f3 = new Vector4f(1.0f, 1.0f, 0.2f, 0.6f);
			final Vector4f vector4f4 = new Vector4f(1.0f, 0.2f, 1.0f, 0.6f);
			final Vector4f vector4f5 = new Vector4f(1.0f, 0.6f, 1.0f, 0.9f);
			for (final ShieldLocal shieldLocal : activeShields) {
				GlUtil.glPushMatrix();
				this.drawLocalShield(shieldContainerInterface, shieldLocal, vector4f, vector4f3, vector4f4, vector4f5, mesh);
				GlUtil.glPopMatrix();
			}
			for (final ShieldLocal shieldLocal2 : inactiveShields) {
				GlUtil.glPushMatrix();
				this.drawLocalShield(shieldContainerInterface, shieldLocal2, vector4f2, vector4f3, vector4f4, vector4f5, mesh);
				GlUtil.glPopMatrix();
			}
			GlUtil.glPopMatrix();
			GlUtil.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			GlUtil.glDisable(3042);
		}
	}
	
	private void drawLocalShield(final ShieldContainerInterface shieldContainerInterface, final ShieldLocal shieldLocal, final Vector4f vector4f, final Vector4f vector4f2, final Vector4f vector4f3, final Vector4f vector4f4, final Mesh mesh) {
		final int n = ElementCollection.getPosX(shieldLocal.outputPos) - 16;
		final int n2 = ElementCollection.getPosY(shieldLocal.outputPos) - 16;
		final int n3 = ElementCollection.getPosZ(shieldLocal.outputPos) - 16;
		drawPoint(null, (float)n, (float)n2, (float)n3, vector4f2, true);
		if (shieldLocal.active) {
			final Iterator<Long> iterator = shieldLocal.supportCoMIds.iterator();
			while (iterator.hasNext()) {
				final long longValue;
				final int n4 = ElementCollection.getPosX(longValue = iterator.next()) - 16;
				final int n5 = ElementCollection.getPosY(longValue) - 16;
				final int n6 = ElementCollection.getPosZ(longValue) - 16;
				drawPoint(null, (float)n4, (float)n5, (float)n6, vector4f3, true);
				GlUtil.glDisable(2929);
				drawArrow((float)n4, (float)n5, (float)n6, (float)n, (float)n2, (float)n3, shieldContainerInterface.getSegmentController().getWorldTransformOnClient(), vector4f4);
				GlUtil.glEnable(2929);
			}
		}
		GlUtil.glColor4f(vector4f);
		GlUtil.translateModelview((float)n, (float)n2, (float)n3);
		GlUtil.glDepthMask(false);
		if (shieldLocal.isPositionInRadiusWorld(shieldContainerInterface.getSegmentController().getWorldTransformOnClient(), Controller.getCamera().getPos())) {
			GL11.glCullFace(1028);
		}
		else {
			GL11.glCullFace(1029);
		}
		GlUtil.drawSphere(shieldLocal.radius, 20.0f);
		GL11.glPolygonMode(1032, 6913);
		GlUtil.drawSphere(shieldLocal.radius, 20.0f);
		GL11.glPolygonMode(1032, 6914);
		GlUtil.glDepthMask(true);
		GL11.glCullFace(1029);
	}
	
	private static void drawArrow(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final Transform transform, final Vector4f vector4f) {
		final DebugLine[] arrow = DebugLine.getArrow(new Vector3f(n, n2, n3), new Vector3f(n4, n5, n6), vector4f, 0.25f, 3.0f, 0.5f, 50.0f, transform);
		for (int i = 0; i < arrow.length; ++i) {
			arrow[i].drawRaw();
		}
	}
	
	private static void drawPoint(final SegmentController segmentController, final float n, final float n2, final float n3, final Vector4f color, final boolean b) {
		final DebugLine[] cross = DebugLine.getCross((segmentController != null) ? segmentController.getWorldTransformOnClient() : TransformTools.ident, new Vector3f(n, n2, n3), 1.5f, 1.5f, 1.5f, b);
		for (int i = 0; i < cross.length; ++i) {
			final DebugLine debugLine;
			(debugLine = cross[i]).setColor(color);
			debugLine.drawRaw();
		}
	}
	
	public void drawStabilizerOrientation(final SegmentController segmentController) {
		if (VoidElementManager.STABILIZER_BONUS_CALC == StabBonusCalcStyle.BY_ANGLE) {
			return;
		}
		final ManagerContainer managerContainer;
		if (segmentController instanceof ManagedSegmentController && (managerContainer = ((ManagedSegmentController)segmentController).getManagerContainer()).hasActiveReactors() && managerContainer.getStabilizer().getElementCollections().size() > 0) {
			GlUtil.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			GlUtil.glDisable(3553);
			GlUtil.glDisable(2896);
			GlUtil.glEnable(3042);
			GlUtil.glBlendFunc(770, 771);
			GlUtil.glBlendFuncSeparate(770, 771, 1, 771);
			GlUtil.glEnable(2903);
			GlUtil.glDisable(2884);
			GlUtil.glPushMatrix();
			GlUtil.glMultMatrix(segmentController.getWorldTransformOnClient());
			for (int i = 0; i < managerContainer.getStabilizer().getElementCollections().size(); ++i) {
				final StabilizerUnit stabilizerUnit;
				final int reactorSide;
				if ((reactorSide = (stabilizerUnit = managerContainer.getStabilizer().getElementCollections().get(i)).getReactorSide()) >= 0) {
					if (BuildModeDrawer.currentPiece != null && stabilizerUnit.getNeighboringCollection().contains(BuildModeDrawer.currentPiece.getAbsoluteIndex())) {
						this.currentSelectedStabSide = reactorSide;
					}
					final float n = stabilizerUnit.getCoMOrigin().x - 16.0f;
					final float n2 = stabilizerUnit.getCoMOrigin().y - 16.0f;
					final float n3 = stabilizerUnit.getCoMOrigin().z - 16.0f;
					this.tmp.set((Tuple3f)Element.DIRECTIONSf[reactorSide]);
					this.tmp.scale(10.0f);
					managerContainer.getPowerInterface().getActiveReactor().getBonusMatrix().transform((Tuple3f)this.tmp);
					final Vector3f tmp = this.tmp;
					tmp.x += n;
					final Vector3f tmp2 = this.tmp;
					tmp2.y += n2;
					final Vector3f tmp3 = this.tmp;
					tmp3.z += n3;
					if (stabilizerUnit.isBonusSlot()) {
						this.stabColor.set((Tuple4f)Element.SIDE_COLORS[reactorSide]);
						final Vector4f stabColor = this.stabColor;
						stabColor.x += this.colorMod.getTime();
						final Vector4f stabColor2 = this.stabColor;
						stabColor2.y += this.colorMod.getTime();
						final Vector4f stabColor3 = this.stabColor;
						stabColor3.z += this.colorMod.getTime();
						this.stabColor.w = 0.9f;
					}
					else {
						this.stabColor.set(0.5f, 0.5f, 0.5f, 0.5f);
					}
					GlUtil.glDisable(2929);
					drawArrow(n, n2, n3, this.tmp.x, this.tmp.y, this.tmp.z, segmentController.getWorldTransformOnClient(), this.stabColor);
					GlUtil.glEnable(2929);
				}
			}
			GlUtil.glDisable(3042);
			GlUtil.glEnable(2884);
			GlUtil.glPopMatrix();
			GlUtil.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		}
	}
	
	public void drawReactorDistance(final SegmentController segmentController, final long n) {
		final ManagerContainer managerContainer;
		if (segmentController instanceof ManagedSegmentController && (managerContainer = ((ManagedSegmentController)segmentController).getManagerContainer()).isUsingPowerReactors() && managerContainer.getPowerInterface().getMainReactors().size() > 0) {
			final PowerInterface powerInterface = managerContainer.getPowerInterface();
			final Vector3f vector3f = new Vector3f();
			final Vector3f vector3f2 = new Vector3f();
			ElementCollection.getPosFromIndex(n, vector3f2);
			float currentStabDist = Float.POSITIVE_INFINITY;
			final Iterator<MainReactorUnit> iterator = powerInterface.getMainReactors().iterator();
			while (iterator.hasNext()) {
				final float distanceToThis;
				if ((distanceToThis = iterator.next().distanceToThis(n, this.v)) < currentStabDist) {
					currentStabDist = distanceToThis;
					vector3f.set((Tuple3f)this.v.outFrom);
				}
			}
			this.t.set((Transform)segmentController.getWorldTransform());
			GlUtil.glPushMatrix();
			GlUtil.glMultMatrix(this.t);
			GlUtil.glDisable(2896);
			GlUtil.glEnable(3042);
			GlUtil.glBlendFunc(770, 771);
			GlUtil.glBlendFuncSeparate(770, 771, 1, 771);
			GlUtil.glEnable(2903);
			GlUtil.glColor4f(0.5f, 1.0f, 0.5f, 0.9f);
			GlUtil.glDisable(2884);
			GL11.glLineWidth(2.5f);
			final double reactorOptimalDistance = powerInterface.getReactorOptimalDistance();
			BuildModeDrawer.currentStabDist = currentStabDist;
			BuildModeDrawer.currentOptStabDist = (float)reactorOptimalDistance;
			BuildModeDrawer.currentStabEfficiency = powerInterface.calcStabilization(reactorOptimalDistance, currentStabDist);
			if (currentStabDist < reactorOptimalDistance) {
				GlUtil.glColor4f(1.0f, 0.27f, 0.34f, 0.9f);
			}
			GL11.glBegin(1);
			GL11.glVertex3f(vector3f.x - 16.0f, vector3f.y - 16.0f, vector3f.z - 16.0f);
			GL11.glVertex3f(vector3f2.x - 16.0f, vector3f2.y - 16.0f, vector3f2.z - 16.0f);
			if (currentStabDist < reactorOptimalDistance) {
				final Vector3f vector3f3;
				(vector3f3 = new Vector3f()).sub((Tuple3f)vector3f2, (Tuple3f)vector3f);
				final float n2 = (float)(reactorOptimalDistance - currentStabDist);
				vector3f3.normalize();
				vector3f3.scale(n2);
				vector3f3.add((Tuple3f)vector3f2);
				GlUtil.glColor4f(0.5f, 0.5f, 1.0f, 0.9f);
				GL11.glVertex3f(vector3f2.x - 16.0f, vector3f2.y - 16.0f, vector3f2.z - 16.0f);
				GL11.glVertex3f(vector3f3.x - 16.0f, vector3f3.y - 16.0f, vector3f3.z - 16.0f);
			}
			GL11.glEnd();
			GlUtil.glDisable(3042);
			GlUtil.glEnable(2884);
			GlUtil.glPopMatrix();
		}
	}
	
	public void drawControlledElements(final SegmentController segmentController, final SegmentPiece segmentPiece) {
		if (segmentPiece == null) {
			return;
		}
		this.conDrw.clear();
		final Vector4f vector4f = new Vector4f(0.4f, 0.1f, 0.9f, 0.65f);
		final Vector4f vector4f2 = new Vector4f(vector4f);
		try {
			long absoluteIndex = Long.MIN_VALUE;
			if (BuildModeDrawer.currentPiece != null) {
				absoluteIndex = BuildModeDrawer.currentPiece.getAbsoluteIndex();
			}
			final Vector3i absolutePos = segmentPiece.getAbsolutePos(this.posTmp);
			long slaveConnectedElement = Long.MIN_VALUE;
			long effectConnectedElement = Long.MIN_VALUE;
			long lightConnectedElement = Long.MIN_VALUE;
			final ManagerModuleCollection<?, ?, ?> value;
			final Object elementManager;
			final ControlBlockElementCollectionManager controlBlockElementCollectionManager;
			if (segmentController instanceof ManagedSegmentController && (value = ((ManagedSegmentController)segmentController).getManagerContainer().getModulesControllerMap().get(segmentPiece.getType())) != null && (elementManager = value.getElementManager()) instanceof UsableControllableElementManager && (controlBlockElementCollectionManager = (ControlBlockElementCollectionManager)((UsableControllableElementManager)elementManager).getCollectionManagersMap().get(ElementCollection.getIndex(absolutePos))) != null) {
				controlBlockElementCollectionManager.drawnUpdateNumber = this.state.getNumberOfUpdate();
				slaveConnectedElement = controlBlockElementCollectionManager.getSlaveConnectedElement();
				effectConnectedElement = controlBlockElementCollectionManager.getEffectConnectedElement();
				lightConnectedElement = controlBlockElementCollectionManager.getLightConnectedElement();
				final Iterator<ElementCollection> iterator = controlBlockElementCollectionManager.getElementCollections().iterator();
				while (iterator.hasNext()) {
					final ElementCollection collection;
					if ((collection = iterator.next()).contains(absoluteIndex)) {
						BuildModeDrawer.currentPieceIndexIntegrity = absoluteIndex;
						BuildModeDrawer.currentPieceIntegrity = collection.getIntegrity();
						for (int i = 0; i < 7; ++i) {
							this.touching.append("Touch " + i + "/6: " + collection.touching[i] + "; x" + VoidElementManager.getIntegrityBaseTouching(i) + " -> " + Math.round(collection.touching[i] * VoidElementManager.getIntegrityBaseTouching(i)) + "\n");
						}
					}
					collection.markDraw();
					collection.setDrawColor(vector4f.x + 1.0f / (1.0f - vector4f.x) * this.linearTimerSl.getTime() * 0.5f, vector4f.y + 1.0f / (1.0f - vector4f.y) * this.linearTimerSl.getTime() * 0.5f, vector4f.z + 1.0f / (1.0f - vector4f.z) * this.linearTimerSl.getTime() * 0.5f, vector4f.w);
					this.conDrw.add(controlBlockElementCollectionManager.getEnhancerClazz());
				}
			}
			GlUtil.updateShaderVector4f(ShaderLibrary.selectionShader, "selectionColor", vector4f2);
			final PositionControl directControlledElements;
			if (EngineSettings.G_DRAW_SELECTED_BLOCK_WOBBLE.isOn() && segmentPiece != null && (directControlledElements = segmentController.getControlElementMap().getDirectControlledElements((short)32767, absolutePos)) != null) {
				this.prepareBlockDraw(segmentController.getWorldTransform());
				for (final long longValue : directControlledElements.getControlMap()) {
					if (!EngineSettings.F_FRAME_BUFFER.isOn() || EngineSettings.G_DRAW_SELECTED_BLOCK_WOBBLE_ALWAYS.isOn() || !this.conDrw.contains((short)ElementCollection.getType(longValue))) {
						if (longValue == slaveConnectedElement) {
							vector4f2.set(0.1f, 0.5f, 0.8f, 0.65f);
							GlUtil.updateShaderVector4f(ShaderLibrary.selectionShader, "selectionColor", vector4f2);
						}
						if (longValue == effectConnectedElement) {
							vector4f2.set(0.1f, 0.9f, 0.1f, 0.65f);
							GlUtil.updateShaderVector4f(ShaderLibrary.selectionShader, "selectionColor", vector4f2);
						}
						if (longValue == lightConnectedElement) {
							vector4f2.set(0.6f, 0.9f, 0.2f, 0.65f);
							GlUtil.updateShaderVector4f(ShaderLibrary.selectionShader, "selectionColor", vector4f2);
						}
						this.drawBlock(longValue, segmentController, this.linearTimerC);
						if (vector4f2.equals((Tuple4f)vector4f)) {
							continue;
						}
						vector4f2.set((Tuple4f)vector4f);
						GlUtil.updateShaderVector4f(ShaderLibrary.selectionShader, "selectionColor", vector4f2);
					}
				}
				this.endBlockDraw();
			}
		}
		catch (ConcurrentModificationException ex) {
			ex.printStackTrace();
		}
	}
	
	public void drawCurrentCamElement(final SegmentController segmentController) {
		if (Controller.getCamera() instanceof BuildShipCamera) {
			this.t.set((Transform)segmentController.getWorldTransform());
			final Vector3f relativeCubePos = ((BuildShipCamera)Controller.getCamera()).getRelativeCubePos();
			this.t.basis.transform((Tuple3f)relativeCubePos);
			this.t.origin.add((Tuple3f)relativeCubePos);
			GlUtil.glPushMatrix();
			GlUtil.glMultMatrix(this.t);
			GlUtil.scaleModelview(1.01f, 1.01f, 1.01f);
			GlUtil.glDisable(2896);
			GlUtil.glEnable(3042);
			GlUtil.glBlendFunc(770, 771);
			GlUtil.glBlendFuncSeparate(770, 771, 1, 771);
			GlUtil.glEnable(2903);
			GlUtil.glColor4f(0.0f, 0.0f, 1.0f, 0.6f);
			GlUtil.scaleModelview(0.1f, 0.1f, 0.1f);
			this.mesh.renderVBO();
			GlUtil.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			GlUtil.glEnable(2896);
			GlUtil.glDisable(2903);
			GlUtil.glDisable(3042);
			GlUtil.glPopMatrix();
		}
	}
	
	public void drawCurrentSelectedElement(final SegmentController segmentController, final SegmentPiece segmentPiece) {
		if (segmentPiece != null) {
			this.prepareBlockDraw(segmentController.getWorldTransform());
			segmentPiece.refresh();
			this.drawBlock(segmentPiece.getAbsolutePos(this.posTmp), segmentController, this.linearTimer);
			this.endBlockDraw();
		}
	}
	
	public void drawCurrentSymetriePlanesElement(final SegmentController segmentController) {
		final SymmetryPlanes symmetryPlanes = this.getActiveBuildController().getSymmetryPlanes();
		GlUtil.glEnable(2929);
		GlUtil.glDisable(2896);
		GlUtil.glEnable(3042);
		GlUtil.glBlendFunc(770, 771);
		GlUtil.glBlendFuncSeparate(770, 771, 1, 771);
		GlUtil.glEnable(2903);
		GlUtil.glColor4f(1.0f, 1.0f, 1.0f, 0.7f);
		GlUtil.glDisable(2884);
		final Vector3f vector3f = new Vector3f(symmetryPlanes.getYzPlane().x - 16 + symmetryPlanes.getYzExtraDist() * 0.5f, symmetryPlanes.getXzPlane().y - 16 + symmetryPlanes.getXzExtraDist() * 0.5f, symmetryPlanes.getXyPlane().z - 16 + symmetryPlanes.getXyExtraDist() * 0.5f);
		this.t.set((Transform)segmentController.getWorldTransform());
		GlUtil.glPushMatrix();
		GlUtil.glMultMatrix(this.t);
		GL11.glBegin(1);
		if (symmetryPlanes.isXyPlaneEnabled() && symmetryPlanes.isXzPlaneEnabled() && symmetryPlanes.isYzPlaneEnabled()) {
			GL11.glVertex3f((float)(segmentController.getMinPos().x << 5), vector3f.y, vector3f.z);
			GL11.glVertex3f((float)(segmentController.getMaxPos().x << 5), vector3f.y, vector3f.z);
			GL11.glVertex3f(vector3f.x, (float)(segmentController.getMinPos().y << 5), vector3f.z);
			GL11.glVertex3f(vector3f.x, (float)(segmentController.getMaxPos().y << 5), vector3f.z);
			GL11.glVertex3f(vector3f.x, vector3f.y, (float)(segmentController.getMinPos().z << 5));
			GL11.glVertex3f(vector3f.x, vector3f.y, (float)(segmentController.getMaxPos().z << 5));
		}
		else if (symmetryPlanes.isXyPlaneEnabled() && symmetryPlanes.isXzPlaneEnabled()) {
			GL11.glVertex3f((float)(segmentController.getMinPos().x << 5), vector3f.y, vector3f.z);
			GL11.glVertex3f((float)(segmentController.getMaxPos().x << 5), vector3f.y, vector3f.z);
		}
		else if (symmetryPlanes.isXyPlaneEnabled() && symmetryPlanes.isYzPlaneEnabled()) {
			GL11.glVertex3f(vector3f.x, (float)(segmentController.getMinPos().y << 5), vector3f.z);
			GL11.glVertex3f(vector3f.x, (float)(segmentController.getMaxPos().y << 5), vector3f.z);
		}
		else if (symmetryPlanes.isXzPlaneEnabled() && symmetryPlanes.isYzPlaneEnabled()) {
			GL11.glVertex3f(vector3f.x, vector3f.y, (float)(segmentController.getMinPos().z << 5));
			GL11.glVertex3f(vector3f.x, vector3f.y, (float)(segmentController.getMaxPos().z << 5));
		}
		GL11.glEnd();
		GlUtil.glPopMatrix();
		GlUtil.glBindTexture(3553, 0);
		GlUtil.glEnable(2884);
		GlUtil.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GlUtil.glEnable(2896);
		GlUtil.glDisable(2903);
		GlUtil.glDisable(3042);
		if (symmetryPlanes.isXyPlaneEnabled()) {
			this.drawCurrentSymetriePlanesElement(segmentController, null, 1, symmetryPlanes.getXyPlane(), symmetryPlanes.getXyExtraDist() * 0.5f);
		}
		if (symmetryPlanes.isXzPlaneEnabled()) {
			this.drawCurrentSymetriePlanesElement(segmentController, null, 2, symmetryPlanes.getXzPlane(), symmetryPlanes.getXzExtraDist() * 0.5f);
		}
		if (symmetryPlanes.isYzPlaneEnabled()) {
			this.drawCurrentSymetriePlanesElement(segmentController, null, 4, symmetryPlanes.getYzPlane(), symmetryPlanes.getYzExtraDist() * 0.5f);
		}
	}
	
	private void drawReactorCoordinateSystems(final SegmentController segmentController) {
		final PowerInterface powerInterface;
		if (segmentController instanceof ManagedSegmentController && (powerInterface = ((ManagedSegmentController)segmentController).getManagerContainer().getPowerInterface()).getActiveReactor() != null) {
			this.drawReactorCoordinateSystem(segmentController, powerInterface.getActiveReactor());
		}
	}
	
	private void drawReactorCoordinateSystem(final SegmentController segmentController, final ReactorTree reactorTree) {
		final Transform transform;
		(transform = new Transform()).setIdentity();
		transform.basis.set(reactorTree.getBonusMatrix());
		final Vector3i posFromIndex = ElementCollection.getPosFromIndex(reactorTree.getCenterOfMass(), new Vector3i());
		final Vector3f vector3f = new Vector3f(-1.0f, -1.0f, -1.0f);
		final Vector3f vector3f2 = new Vector3f(1.0f, 1.0f, 1.0f);
		this.t.set((Transform)segmentController.getWorldTransform());
		final Vector4f vector4f = new Vector4f(Element.SIDE_COLORS[1]);
		final Vector4f vector4f2 = new Vector4f(Element.SIDE_COLORS[0]);
		final Vector4f vector4f3 = new Vector4f(Element.SIDE_COLORS[2]);
		final Vector4f vector4f4 = new Vector4f(Element.SIDE_COLORS[3]);
		final Vector4f vector4f5 = new Vector4f(Element.SIDE_COLORS[5]);
		final Vector4f vector4f6 = new Vector4f(Element.SIDE_COLORS[4]);
		float n = BuildModeDrawer.inReactorAlignSlider ? 0.2f : 0.0f;
		if (BuildModeDrawer.inReactorAlignAlwaysVisible) {
			n = 0.3f;
		}
		vector4f.w = n;
		vector4f2.w = n;
		vector4f3.w = n;
		vector4f4.w = n;
		vector4f5.w = n;
		vector4f6.w = n;
		Vector4f vector4f7 = null;
		switch (this.currentSelectedStabSide) {
			case 1: {
				vector4f7 = vector4f;
				break;
			}
			case 0: {
				vector4f7 = vector4f2;
				break;
			}
			case 2: {
				vector4f7 = vector4f3;
				break;
			}
			case 3: {
				vector4f7 = vector4f4;
				break;
			}
			case 5: {
				vector4f7 = vector4f5;
				break;
			}
			case 4: {
				vector4f7 = vector4f6;
				break;
			}
		}
		if (vector4f7 != null) {
			vector4f.w = 0.05f;
			vector4f2.w = 0.05f;
			vector4f3.w = 0.05f;
			vector4f4.w = 0.05f;
			vector4f5.w = 0.05f;
			vector4f6.w = 0.05f;
			final Vector4f vector4f8 = vector4f7;
			vector4f8.x += this.colorMod.getTime();
			final Vector4f vector4f9 = vector4f7;
			vector4f9.y += this.colorMod.getTime();
			final Vector4f vector4f10 = vector4f7;
			vector4f10.z += this.colorMod.getTime();
			vector4f7.w = 0.3f + this.colorMod.getTime() * 0.3f;
			this.drawReactoAlignCross(transform, posFromIndex, vector3f, vector3f2, 0.0f);
		}
		else if (BuildModeDrawer.inReactorAlignAlwaysVisible) {
			this.drawReactoAlignCross(transform, posFromIndex, vector3f, vector3f2, 0.0f);
		}
		else if (BuildModeDrawer.inReactorAlignSliderSelectedAxis >= 0) {
			this.drawReactoAlignCross(transform, posFromIndex, vector3f, vector3f2, 0.0f);
		}
		this.drawCurrentSymetriePlanesElement(segmentController, transform, 1, posFromIndex, 0.0f, vector4f, vector4f2, vector3f, vector3f2);
		this.drawCurrentSymetriePlanesElement(segmentController, transform, 2, posFromIndex, 0.0f, vector4f3, vector4f4, vector3f, vector3f2);
		this.drawCurrentSymetriePlanesElement(segmentController, transform, 4, posFromIndex, 0.0f, vector4f5, vector4f6, vector3f, vector3f2);
		BuildModeDrawer.inReactorAlignSliderSelectedAxis = -1;
	}
	
	private void drawReactoAlignCross(final Transform transform, final Vector3i vector3i, final Vector3f vector3f, final Vector3f vector3f2, final float n) {
		GlUtil.glPushMatrix();
		GlUtil.glMultMatrix(this.t);
		GlUtil.glTranslatef(vector3i.x - 16 + n, vector3i.y - 16 + n, vector3i.z - 16 + n);
		GlUtil.glMultMatrix(transform);
		GlUtil.glDisable(3553);
		GlUtil.glEnable(2929);
		GlUtil.glDisable(2896);
		GlUtil.glEnable(3042);
		GlUtil.glBlendFunc(770, 771);
		GlUtil.glBlendFuncSeparate(770, 771, 1, 771);
		GlUtil.glEnable(2903);
		GlUtil.glColor4f(1.0f, 1.0f, 1.0f, 0.6f);
		GlUtil.glDisable(2884);
		GL11.glBegin(1);
		if (BuildModeDrawer.inReactorAlignSliderSelectedAxis < 0 || BuildModeDrawer.inReactorAlignSliderSelectedAxis == 4) {
			GL11.glVertex3f(vector3f.x * 32.0f + n, 0.0f, 0.0f);
			GL11.glVertex3f(vector3f2.x * 32.0f + n, 0.0f, 0.0f);
		}
		if (BuildModeDrawer.inReactorAlignSliderSelectedAxis < 0 || BuildModeDrawer.inReactorAlignSliderSelectedAxis == 2) {
			GL11.glVertex3f(0.0f, vector3f.y * 32.0f + n, 0.0f);
			GL11.glVertex3f(0.0f, vector3f2.y * 32.0f + n, 0.0f);
		}
		if (BuildModeDrawer.inReactorAlignSliderSelectedAxis < 0 || BuildModeDrawer.inReactorAlignSliderSelectedAxis == 1) {
			GL11.glVertex3f(0.0f, 0.0f, vector3f.z * 32.0f + n);
			GL11.glVertex3f(0.0f, 0.0f, vector3f2.z * 32.0f + n);
		}
		GL11.glEnd();
		GlUtil.glPopMatrix();
	}
	
	private void drawCurrentSymetriePlanesElement(final SegmentController segmentController, final Transform transform, final int n, final Vector3i vector3i, final float n2) {
		final Vector3f vector3f = new Vector3f((float)(segmentController.getMinPos().x - 1), (float)(segmentController.getMinPos().y - 1), (float)(segmentController.getMinPos().z - 1));
		final Vector3f vector3f2 = new Vector3f((float)(segmentController.getMaxPos().x + 1), (float)(segmentController.getMaxPos().y + 1), (float)(segmentController.getMaxPos().z + 1));
		final Vector4f vector4f = new Vector4f();
		final Vector4f vector4f2 = new Vector4f();
		if (n == 1) {
			vector4f.set(0.0f, 0.0f, 1.0f, 0.3f);
		}
		else if (n == 2) {
			vector4f.set(0.0f, 1.0f, 0.0f, 0.3f);
		}
		else {
			vector4f.set(1.0f, 0.0f, 0.0f, 0.3f);
		}
		vector4f2.set((Tuple4f)vector4f);
		this.drawCurrentSymetriePlanesElement(segmentController, transform, n, vector3i, n2, vector4f, vector4f2, vector3f, vector3f2);
	}
	
	private void drawCurrentSymetriePlanesElement(final SegmentController segmentController, final Transform transform, final int n, final Vector3i vector3i, float n2, final Vector4f vector4f, final Vector4f vector4f2, final Vector3f vector3f, final Vector3f vector3f2) {
		GlUtil.glDepthMask(false);
		GlUtil.glEnable(2929);
		GlUtil.glEnable(3553);
		GlUtil.glDisable(2896);
		GlUtil.glEnable(3042);
		GlUtil.glBlendFunc(770, 1);
		GlUtil.glBlendFuncSeparate(770, 771, 1, 771);
		GlUtil.glEnable(2903);
		GlUtil.glEnable(2884);
		Controller.getResLoader().getSprite("symm-plane").getMaterial().getTexture().attach(0);
		final Vector3f vector3f3 = new Vector3f(vector3f2.x - vector3f.x, vector3f2.y - vector3f.y, vector3f2.z - vector3f.z);
		this.t.set((Transform)segmentController.getWorldTransform());
		GlUtil.glPushMatrix();
		GlUtil.glMultMatrix(this.t);
		GlUtil.glTranslatef(vector3i.x - 16 + n2, vector3i.y - 16 + n2, vector3i.z - 16 + n2);
		if (transform != null) {
			GlUtil.glMultMatrix(transform);
		}
		final float n3 = vector3f.x * 32.0f;
		final float n4 = vector3f2.x * 32.0f;
		final float n5 = vector3f.y * 32.0f;
		n2 = vector3f2.y * 32.0f;
		final float n6 = vector3f.z * 32.0f;
		final float n7 = vector3f2.z * 32.0f;
		if (n == 1) {
			GL11.glBegin(7);
			GlUtil.glColor4f(vector4f);
			GL11.glTexCoord2f(0.0f, 0.0f);
			GL11.glVertex3f(n3, n5, 0.0f);
			GL11.glTexCoord2f(0.0f, vector3f3.y / 0.07f);
			GL11.glVertex3f(n3, n2, 0.0f);
			GL11.glTexCoord2f(vector3f3.x / 0.07f, vector3f3.y / 0.07f);
			GL11.glVertex3f(n4, n2, 0.0f);
			GL11.glTexCoord2f(vector3f3.x / 0.07f, 0.0f);
			GL11.glVertex3f(n4, n5, 0.0f);
			GlUtil.glColor4f(vector4f2);
			GL11.glTexCoord2f(vector3f3.x / 0.07f, 0.0f);
			GL11.glVertex3f(n4, n5, 0.0f);
			GL11.glTexCoord2f(vector3f3.x / 0.07f, vector3f3.y / 0.07f);
			GL11.glVertex3f(n4, n2, 0.0f);
			GL11.glTexCoord2f(0.0f, vector3f3.y / 0.07f);
			GL11.glVertex3f(n3, n2, 0.0f);
			GL11.glTexCoord2f(0.0f, 0.0f);
			GL11.glVertex3f(n3, n5, 0.0f);
			GL11.glEnd();
		}
		if (n == 2) {
			GL11.glBegin(7);
			GlUtil.glColor4f(vector4f);
			GL11.glTexCoord2f(0.0f, 0.0f);
			GL11.glVertex3f(n3, 0.0f, n6);
			GL11.glTexCoord2f(0.0f, vector3f3.z / 0.07f);
			GL11.glVertex3f(n3, 0.0f, n7);
			GL11.glTexCoord2f(vector3f3.x / 0.07f, vector3f3.z / 0.07f);
			GL11.glVertex3f(n4, 0.0f, n7);
			GL11.glTexCoord2f(vector3f3.x / 0.07f, 0.0f);
			GL11.glVertex3f(n4, 0.0f, n6);
			GlUtil.glColor4f(vector4f2);
			GL11.glTexCoord2f(vector3f3.x / 0.07f, 0.0f);
			GL11.glVertex3f(n4, 0.0f, n6);
			GL11.glTexCoord2f(vector3f3.x / 0.07f, vector3f3.z / 0.07f);
			GL11.glVertex3f(n4, 0.0f, n7);
			GL11.glTexCoord2f(0.0f, vector3f3.z / 0.07f);
			GL11.glVertex3f(n3, 0.0f, n7);
			GL11.glTexCoord2f(0.0f, 0.0f);
			GL11.glVertex3f(n3, 0.0f, n6);
			GL11.glEnd();
		}
		if (n == 4) {
			GL11.glBegin(7);
			GlUtil.glColor4f(vector4f);
			GL11.glTexCoord2f(0.0f, 0.0f);
			GL11.glVertex3f(0.0f, n5, n6);
			GL11.glTexCoord2f(0.0f, vector3f3.z / 0.07f);
			GL11.glVertex3f(0.0f, n5, n7);
			GL11.glTexCoord2f(vector3f3.y / 0.07f, vector3f3.z / 0.07f);
			GL11.glVertex3f(0.0f, n2, n7);
			GL11.glTexCoord2f(vector3f3.y / 0.07f, 0.0f);
			GL11.glVertex3f(0.0f, n2, n6);
			GlUtil.glColor4f(vector4f2);
			GL11.glTexCoord2f(vector3f3.y / 0.07f, 0.0f);
			GL11.glVertex3f(0.0f, n2, n6);
			GL11.glTexCoord2f(vector3f3.y / 0.07f, vector3f3.z / 0.07f);
			GL11.glVertex3f(0.0f, n2, n7);
			GL11.glTexCoord2f(0.0f, vector3f3.z / 0.07f);
			GL11.glVertex3f(0.0f, n5, n7);
			GL11.glTexCoord2f(0.0f, 0.0f);
			GL11.glVertex3f(0.0f, n5, n6);
			GL11.glEnd();
		}
		GlUtil.glPopMatrix();
		GlUtil.glBindTexture(3553, 0);
		GlUtil.glEnable(2884);
		GlUtil.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GlUtil.glEnable(2896);
		GlUtil.glDisable(2903);
		GlUtil.glDisable(3042);
		GlUtil.glDepthMask(true);
	}
	
	private void drawOrientationArrow(Transform transform, final int n) {
		this.state.getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager().getBuildToolsManager();
		GlUtil.glPushMatrix();
		final Mesh mesh = (Mesh)Controller.getResLoader().getMesh("Arrow").getChilds().get(0);
		transform = new Transform(transform);
		SegmentController.setConstraintFrameOrientation((byte)n, transform, GlUtil.getRightVector(new Vector3f(), transform), GlUtil.getUpVector(new Vector3f(), transform), GlUtil.getForwardVector(new Vector3f(), transform));
		final Vector3f vector3f;
		(vector3f = new Vector3f(0.0f, 0.0f, 0.1f)).scale(this.linearTimer.getTime() / 5.0f);
		final Vector3f vector3f2 = vector3f;
		vector3f2.z -= 0.3f;
		transform.basis.transform((Tuple3f)vector3f);
		transform.origin.add((Tuple3f)vector3f);
		GlUtil.glMultMatrix(transform);
		GlUtil.scaleModelview(0.13f, 0.13f, 0.13f);
		GlUtil.glEnable(3042);
		GlUtil.glDisable(2884);
		GlUtil.glEnable(2896);
		GlUtil.glBlendFunc(770, 771);
		GlUtil.glBlendFuncSeparate(770, 771, 1, 771);
		GlUtil.glEnable(2903);
		GlUtil.glColor4f(1.0f, 1.0f, 1.0f, this.linearTimer.getTime() - 0.5f);
		mesh.draw();
		GlUtil.glPopMatrix();
		GlUtil.glDisable(2903);
		GlUtil.glDisable(3042);
		GlUtil.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}
	
	private Transform getToBuildTransform(final SegmentController segmentController) {
		if (this.testRayCollisionPoint == null || !this.testRayCollisionPoint.hasHit() || !(this.testRayCollisionPoint instanceof CubeRayCastResult)) {
			return null;
		}
		final CubeRayCastResult testRayCollisionPoint;
		if ((testRayCollisionPoint = this.testRayCollisionPoint).getSegment() == null) {
			return null;
		}
		assert segmentController != null;
		assert this.t != null;
		assert segmentController.getWorldTransform() != null;
		this.t.set((Transform)segmentController.getWorldTransform());
		final Vector3f vector3f2;
		final Vector3f vector3f = vector3f2 = new Vector3f((float)testRayCollisionPoint.getSegment().pos.x, (float)testRayCollisionPoint.getSegment().pos.y, (float)testRayCollisionPoint.getSegment().pos.z);
		vector3f.x += testRayCollisionPoint.getCubePos().x - 16;
		final Vector3f vector3f3 = vector3f2;
		vector3f3.y += testRayCollisionPoint.getCubePos().y - 16;
		final Vector3f vector3f4 = vector3f2;
		vector3f4.z += testRayCollisionPoint.getCubePos().z - 16;
		final Vector3f vector3f5 = new Vector3f(this.testRayCollisionPoint.hitPointWorld);
		segmentController.getWorldTransformInverse().transform(vector3f5);
		this.pp.set((int)vector3f2.x, (int)vector3f2.y, (int)vector3f2.z);
		this.toBuildPos.set(this.pp.x + 16, this.pp.y + 16, this.pp.z + 16);
		final BuildToolsManager buildToolsManager = this.state.getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager().getBuildToolsManager();
		final Vector3f vector3f6 = new Vector3f((float)buildToolsManager.getWidth(), (float)buildToolsManager.getHeight(), (float)buildToolsManager.getDepth());
		if (!PlayerInteractionControlManager.isAdvancedBuildMode(this.state)) {
			vector3f6.set(1.0f, 1.0f, 1.0f);
		}
		final Vector3f vector3f7 = new Vector3f();
		final IntOpenHashSet set = new IntOpenHashSet();
		for (int i = 0; i < 6; ++i) {
			final Vector3i vector3i = Element.DIRECTIONSi[i];
			final SegmentPiece pointUnsave;
			if ((pointUnsave = segmentController.getSegmentBuffer().getPointUnsave(new Vector3i(this.toBuildPos.x + vector3i.x, this.toBuildPos.y + vector3i.y, this.toBuildPos.z + vector3i.z))) != null && pointUnsave.getType() != 0) {
				((IntSet)set).add(i);
			}
		}
		final SegmentPiece pointUnsave2 = segmentController.getSegmentBuffer().getPointUnsave(this.toBuildPos);
		switch (BuildModeDrawer.currentSide = Element.getSide(vector3f5, (pointUnsave2 == null) ? null : pointUnsave2.getAlgorithm(), this.pp, (short)((pointUnsave2 != null) ? pointUnsave2.getType() : 0), (pointUnsave2 != null) ? pointUnsave2.getOrientation() : 0, (IntCollection)set)) {
			case 4: {
				if (buildToolsManager.isAddMode()) {
					final Vector3f vector3f8 = vector3f2;
					++vector3f8.x;
				}
				final Vector3i toBuildPos = this.toBuildPos;
				++toBuildPos.x;
				vector3f7.set(vector3f6.x, vector3f6.y, vector3f6.z);
				break;
			}
			case 5: {
				if (buildToolsManager.isAddMode()) {
					final Vector3f vector3f9 = vector3f2;
					--vector3f9.x;
				}
				final Vector3i toBuildPos2 = this.toBuildPos;
				--toBuildPos2.x;
				vector3f7.set(-vector3f6.x, vector3f6.y, vector3f6.z);
				break;
			}
			case 2: {
				if (buildToolsManager.isAddMode()) {
					final Vector3f vector3f10 = vector3f2;
					++vector3f10.y;
				}
				final Vector3i toBuildPos3 = this.toBuildPos;
				++toBuildPos3.y;
				vector3f7.set(vector3f6.x, vector3f6.y, vector3f6.z);
				break;
			}
			case 3: {
				if (buildToolsManager.isAddMode()) {
					final Vector3f vector3f11 = vector3f2;
					--vector3f11.y;
				}
				final Vector3i toBuildPos4 = this.toBuildPos;
				--toBuildPos4.y;
				vector3f7.set(vector3f6.x, -vector3f6.y, vector3f6.z);
				break;
			}
			case 0: {
				if (buildToolsManager.isAddMode()) {
					final Vector3f vector3f12 = vector3f2;
					++vector3f12.z;
				}
				final Vector3i toBuildPos5 = this.toBuildPos;
				++toBuildPos5.z;
				vector3f7.set(vector3f6.x, vector3f6.y, vector3f6.z);
				break;
			}
			case 1: {
				if (buildToolsManager.isAddMode()) {
					final Vector3f vector3f13 = vector3f2;
					--vector3f13.z;
				}
				final Vector3i toBuildPos6 = this.toBuildPos;
				--toBuildPos6.z;
				vector3f7.set(vector3f6.x, vector3f6.y, -vector3f6.z);
				break;
			}
		}
		final Vector3f vector3f14 = vector3f2;
		vector3f14.x += vector3f7.x / 2.0f - 0.5f * Math.signum(vector3f7.x);
		final Vector3f vector3f15 = vector3f2;
		vector3f15.y += vector3f7.y / 2.0f - 0.5f * Math.signum(vector3f7.y);
		final Vector3f vector3f16 = vector3f2;
		vector3f16.z += vector3f7.z / 2.0f - 0.5f * Math.signum(vector3f7.z);
		final Vector3f vector3f17 = new Vector3f(vector3f2);
		this.t.basis.transform((Tuple3f)vector3f17);
		this.t.origin.add((Tuple3f)vector3f17);
		return new Transform(this.t);
	}
	
	private boolean isDrawPreview() {
		final BuildToolsManager buildToolsManager = this.state.getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager().getBuildToolsManager();
		return EngineSettings.G_PREVIEW_TO_BUILD_BLOCK.isOn() && buildToolsManager.isAddMode() && !buildToolsManager.isCopyMode();
	}
	
	private Transform drawToBuildBox(final SegmentController segmentController, final SingleBlockDrawer singleBlockDrawer, final Shader shader, final SelectionShader shaderInterface, final boolean b) {
		GlUtil.glEnable(3042);
		GlUtil.glDisable(2896);
		GlUtil.glBlendFunc(770, 771);
		GlUtil.glBlendFuncSeparate(770, 771, 1, 771);
		if (this.drawDebug) {
			GlUtil.printGlErrorCritical();
		}
		if (this.testRayCollisionPoint == null || !this.testRayCollisionPoint.hasHit() || !(this.testRayCollisionPoint instanceof CubeRayCastResult)) {
			return null;
		}
		final CubeRayCastResult testRayCollisionPoint;
		if ((testRayCollisionPoint = this.testRayCollisionPoint).getSegment() == null) {
			return null;
		}
		assert segmentController != null;
		assert this.t != null;
		assert segmentController.getWorldTransform() != null;
		this.t.set((Transform)segmentController.getWorldTransform());
		final Vector3f vector3f2;
		final Vector3f vector3f = vector3f2 = new Vector3f((float)testRayCollisionPoint.getSegment().pos.x, (float)testRayCollisionPoint.getSegment().pos.y, (float)testRayCollisionPoint.getSegment().pos.z);
		vector3f.x += testRayCollisionPoint.getCubePos().x - 16;
		final Vector3f vector3f3 = vector3f2;
		vector3f3.y += testRayCollisionPoint.getCubePos().y - 16;
		final Vector3f vector3f4 = vector3f2;
		vector3f4.z += testRayCollisionPoint.getCubePos().z - 16;
		final BuildToolsManager buildToolsManager;
		if ((buildToolsManager = this.state.getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager().getBuildToolsManager()).getBuildHelper() != null && !buildToolsManager.getBuildHelper().placed) {
			buildToolsManager.getBuildHelper().localTransform.origin.set((Tuple3f)vector3f2);
		}
		if ((buildToolsManager.isInCreateDockingMode() && buildToolsManager.getBuildToolCreateDocking().docker != null) || buildToolsManager.isSelectMode()) {
			return null;
		}
		final Vector3f vector3f5 = new Vector3f(this.testRayCollisionPoint.hitPointWorld);
		segmentController.getWorldTransformInverse().transform(vector3f5);
		if (this.drawDebug) {
			GlUtil.printGlErrorCritical();
		}
		this.pp.set((int)Math.floor(vector3f2.x), (int)Math.floor(vector3f2.y), (int)Math.floor(vector3f2.z));
		this.toBuildPos.set(this.pp.x + 16, this.pp.y + 16, this.pp.z + 16);
		this.loockingAtPos.set(this.toBuildPos);
		if (this.drawDebug) {
			GlUtil.printGlErrorCritical();
		}
		if (this.drawDebug) {
			GlUtil.printGlErrorCritical();
		}
		final Vector3f sizef = buildToolsManager.getSizef();
		if (singleBlockDrawer == null) {
			if (!b) {
				if (buildToolsManager.isCopyMode()) {
					GlUtil.updateShaderVector4f(shader, "selectionColor", 0.6f, 0.6f, 0.04f, 1.0f);
				}
				else if (buildToolsManager.isPasteMode()) {
					GlUtil.updateShaderVector4f(shader, "selectionColor", 0.7f, 0.1f, 0.5f, 1.0f);
				}
				else {
					GlUtil.updateShaderVector4f(shader, "selectionColor", 0.7f, 0.1f, 0.1f, 1.0f);
				}
				sizef.x = -sizef.x;
				sizef.y = -sizef.y;
				sizef.z = -sizef.z;
			}
			else {
				GlUtil.updateShaderVector4f(shader, "selectionColor", 0.6f, 0.6f, 0.04f, 1.0f);
			}
		}
		if (this.drawDebug) {
			GlUtil.printGlErrorCritical();
		}
		if (!PlayerInteractionControlManager.isAdvancedBuildMode(this.state)) {
			sizef.set(1.0f, 1.0f, 1.0f);
		}
		final float max = FastMath.max(Math.abs(sizef.x), Math.abs(sizef.y), Math.abs(sizef.z));
		final Vector3f vector3f6;
		(vector3f6 = new Vector3f(testRayCollisionPoint.hitPointWorld)).sub((Tuple3f)Controller.getCamera().getWorldTransform().origin);
		final float n = Math.min(0.1f, max / 100.0f) + Math.min(0.2f, vector3f6.length() / 500.0f);
		final Vector3f vector3f7 = new Vector3f();
		final IntOpenHashSet set = new IntOpenHashSet();
		for (int i = 0; i < 6; ++i) {
			final Vector3i vector3i = Element.DIRECTIONSi[i];
			final SegmentPiece pointUnsave;
			if ((pointUnsave = segmentController.getSegmentBuffer().getPointUnsave(new Vector3i(this.toBuildPos.x + vector3i.x, this.toBuildPos.y + vector3i.y, this.toBuildPos.z + vector3i.z))) != null && ElementKeyMap.isValidType(pointUnsave.getType())) {
				ElementKeyMap.getInfoFast(pointUnsave.getType());
				((IntSet)set).add(i);
			}
		}
		if (this.drawDebug) {
			GlUtil.printGlErrorCritical();
		}
		final SegmentPiece pointUnsave2 = segmentController.getSegmentBuffer().getPointUnsave(this.toBuildPos);
		final int side;
		switch (side = Element.getSide(vector3f5, (pointUnsave2 == null) ? null : pointUnsave2.getAlgorithm(), this.pp, (short)((pointUnsave2 != null) ? pointUnsave2.getType() : 0), (pointUnsave2 != null) ? pointUnsave2.getOrientation() : 0, (IntCollection)set)) {
			case 4: {
				if (b) {
					final Vector3f vector3f8 = vector3f2;
					++vector3f8.x;
				}
				final Vector3i toBuildPos = this.toBuildPos;
				++toBuildPos.x;
				vector3f7.set(sizef.x, sizef.y, sizef.z);
				break;
			}
			case 5: {
				if (b) {
					final Vector3f vector3f9 = vector3f2;
					--vector3f9.x;
				}
				final Vector3i toBuildPos2 = this.toBuildPos;
				--toBuildPos2.x;
				vector3f7.set(-sizef.x, sizef.y, sizef.z);
				break;
			}
			case 2: {
				if (b) {
					final Vector3f vector3f10 = vector3f2;
					++vector3f10.y;
				}
				final Vector3i toBuildPos3 = this.toBuildPos;
				++toBuildPos3.y;
				vector3f7.set(sizef.x, sizef.y, sizef.z);
				break;
			}
			case 3: {
				if (b) {
					final Vector3f vector3f11 = vector3f2;
					--vector3f11.y;
				}
				final Vector3i toBuildPos4 = this.toBuildPos;
				--toBuildPos4.y;
				vector3f7.set(sizef.x, -sizef.y, sizef.z);
				break;
			}
			case 0: {
				if (b) {
					final Vector3f vector3f12 = vector3f2;
					++vector3f12.z;
				}
				final Vector3i toBuildPos5 = this.toBuildPos;
				++toBuildPos5.z;
				vector3f7.set(sizef.x, sizef.y, sizef.z);
				break;
			}
			case 1: {
				if (b) {
					final Vector3f vector3f13 = vector3f2;
					--vector3f13.z;
				}
				final Vector3i toBuildPos6 = this.toBuildPos;
				--toBuildPos6.z;
				vector3f7.set(sizef.x, sizef.y, -sizef.z);
				break;
			}
		}
		if (this.drawDebug) {
			GlUtil.printGlErrorCritical();
		}
		if (buildToolsManager.isInCreateDockingMode()) {
			buildToolsManager.getBuildToolCreateDocking().potentialCreateDockPos = null;
		}
		if (this.drawDebug) {
			GlUtil.printGlErrorCritical();
		}
		if (buildToolsManager.getCopyArea() != null && buildToolsManager.isPasteMode()) {
			shader.unload();
			this.mesh.unloadVBO(true);
			final Vector3f vector3f14 = new Vector3f(vector3f2);
			final Transform transform;
			(transform = new Transform(this.t)).basis.transform((Tuple3f)vector3f14);
			transform.origin.add((Tuple3f)vector3f14);
			GlUtil.glPushMatrix();
			GlUtil.glMultMatrix(transform);
			buildToolsManager.getCopyArea().draw();
			GlUtil.glPopMatrix();
			this.mesh.loadVBO(true);
			shader.setShaderInterface(shaderInterface);
			shader.load();
			if (singleBlockDrawer == null) {
				if (!b) {
					if (buildToolsManager.isCopyMode()) {
						GlUtil.updateShaderVector4f(shader, "selectionColor", 0.7f, 0.8f, 0.2f, 1.0f);
					}
					else if (buildToolsManager.isPasteMode()) {
						GlUtil.updateShaderVector4f(shader, "selectionColor", 0.7f, 0.1f, 0.5f, 1.0f);
					}
					else {
						GlUtil.updateShaderVector4f(shader, "selectionColor", 0.7f, 0.1f, 0.1f, 1.0f);
					}
				}
				else {
					GlUtil.updateShaderVector4f(shader, "selectionColor", 0.7f, 0.77f, 0.1f, 1.0f);
				}
			}
		}
		GlUtil.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		if (this.drawDebug) {
			GlUtil.printGlErrorCritical();
		}
		if (buildToolsManager.isInCreateDockingMode()) {
			GlUtil.updateShaderVector4f(shader, "selectionColor", 0.7f, 0.1f, 0.1f, 1.0f);
			if (buildToolsManager.getBuildToolCreateDocking().docker == null) {
				final SegmentPiece pointUnsave3;
				if ((pointUnsave3 = segmentController.getSegmentBuffer().getPointUnsave(this.loockingAtPos)) != null && ElementKeyMap.isValidType(pointUnsave3.getType()) && ElementKeyMap.getInfoFast(pointUnsave3.getType()).isRailDockable() && Element.switchLeftRight(((Oriencube)BlockShapeAlgorithm.getAlgo(ElementKeyMap.getInfoFast(pointUnsave3.getType()).getBlockStyle(), pointUnsave3.getOrientation())).getOrientCubePrimaryOrientation()) == side) {
					GlUtil.updateShaderVector4f(shader, "selectionColor", 0.1f, 0.8f, 0.1f, 1.0f);
					buildToolsManager.getBuildToolCreateDocking().potentialCreateDockPos = new VoidUniqueSegmentPiece(pointUnsave3);
				}
			}
			else {
				GlUtil.updateShaderVector4f(shader, "selectionColor", 0.1f, 0.1f, 0.9f, 1.0f);
			}
		}
		if (this.drawDebug) {
			GlUtil.printGlErrorCritical();
		}
		if (EngineSettings.G_BASIC_SELECTION_BOX.getCurrentState() != null) {
			final Vector3f vector3f15 = new Vector3f(vector3f7);
			final Vector3f vector3f16 = new Vector3f(vector3f2);
			final Transform transform2 = new Transform(this.t);
			final Vector3f vector3f17 = vector3f16;
			vector3f17.x += vector3f15.x / 2.0f - 0.5f * Math.signum(vector3f15.x);
			final Vector3f vector3f18 = vector3f16;
			vector3f18.y += vector3f15.y / 2.0f - 0.5f * Math.signum(vector3f15.y);
			final Vector3f vector3f19 = vector3f16;
			vector3f19.z += vector3f15.z / 2.0f - 0.5f * Math.signum(vector3f15.z);
			if (this.drawDebug) {
				GlUtil.printGlErrorCritical();
			}
			final Vector3f vector3f20 = new Vector3f(vector3f16);
			transform2.basis.transform((Tuple3f)vector3f20);
			transform2.origin.add((Tuple3f)vector3f20);
			GlUtil.glPushMatrix();
			GlUtil.glMultMatrix(transform2);
			if (b) {
				vector3f15.scale(0.99993f);
			}
			else {
				vector3f15.scale(1.00003f);
			}
			if (singleBlockDrawer == null) {
				GlUtil.scaleModelview(vector3f15.x, vector3f15.y, vector3f15.z);
			}
			if (this.drawDebug) {
				GlUtil.printGlErrorCritical();
			}
			if (this.drawDebug) {
				GlUtil.printGlErrorCritical();
			}
			if (singleBlockDrawer == null) {
				this.mesh.renderVBO();
			}
			GlUtil.glPopMatrix();
		}
		else {
			final Vector3f vector3f21 = vector3f2;
			vector3f21.x -= n / 2.0f * Math.signum(vector3f7.x);
			final Vector3f vector3f22 = vector3f2;
			vector3f22.y -= n / 2.0f * Math.signum(vector3f7.y);
			final Vector3f vector3f23 = vector3f2;
			vector3f23.z -= n / 2.0f * Math.signum(vector3f7.z);
			for (int j = 0; j < 12; ++j) {
				final Vector3f vector3f24 = new Vector3f(vector3f7);
				final Vector3f vector3f25 = new Vector3f(vector3f2);
				final Transform transform3 = new Transform(this.t);
				switch (j) {
					case 0: {
						final Vector3f vector3f26 = vector3f25;
						vector3f26.x += n * Math.signum(vector3f24.x);
						final Vector3f vector3f27 = vector3f24;
						vector3f27.x -= n * Math.signum(vector3f24.x);
						vector3f24.y = n * Math.signum(vector3f24.y);
						vector3f24.z = n * Math.signum(vector3f24.z);
						break;
					}
					case 1: {
						final Vector3f vector3f28 = vector3f24;
						vector3f28.x -= n * Math.signum(vector3f24.x);
						vector3f24.y = n * Math.signum(vector3f24.y);
						vector3f24.z = n * Math.signum(vector3f24.z);
						break;
					}
					case 2: {
						final Vector3f vector3f29 = vector3f25;
						vector3f29.x += n * Math.signum(vector3f24.x);
						final Vector3f vector3f30 = vector3f24;
						vector3f30.x -= n * Math.signum(vector3f24.x);
						vector3f24.y = n * Math.signum(vector3f24.y);
						vector3f24.z = n * Math.signum(vector3f24.z);
						break;
					}
					case 3: {
						final Vector3f vector3f31 = vector3f25;
						vector3f31.x += n * Math.signum(vector3f24.x);
						final Vector3f vector3f32 = vector3f24;
						vector3f32.x -= n * Math.signum(vector3f24.x);
						vector3f24.y = n * Math.signum(vector3f24.y);
						vector3f24.z = n * Math.signum(vector3f24.z);
						break;
					}
					case 4:
					case 5:
					case 6: {
						final Vector3f vector3f33 = vector3f25;
						vector3f33.z += n * Math.signum(vector3f24.z);
					}
					case 7: {
						final Vector3f vector3f34 = vector3f24;
						vector3f34.z -= n * Math.signum(vector3f24.z);
						vector3f24.y = n * Math.signum(vector3f24.y);
						vector3f24.x = n * Math.signum(vector3f24.x);
						break;
					}
					case 8:
					case 9:
					case 10:
					case 11: {
						final Vector3f vector3f35 = vector3f24;
						vector3f35.y += n * Math.signum(vector3f24.y);
						vector3f24.x = n * Math.signum(vector3f24.x);
						vector3f24.z = n * Math.signum(vector3f24.z);
						break;
					}
				}
				final Vector3f vector3f36 = vector3f25;
				vector3f36.x += vector3f24.x / 2.0f - 0.5f * Math.signum(vector3f24.x);
				final Vector3f vector3f37 = vector3f25;
				vector3f37.y += vector3f24.y / 2.0f - 0.5f * Math.signum(vector3f24.y);
				final Vector3f vector3f38 = vector3f25;
				vector3f38.z += vector3f24.z / 2.0f - 0.5f * Math.signum(vector3f24.z);
				if (j == 1) {
					final Vector3f vector3f39 = vector3f25;
					vector3f39.y += vector3f7.y;
					final Vector3f vector3f40 = vector3f25;
					vector3f40.x += n * Math.signum(vector3f24.x);
				}
				else if (j == 2) {
					final Vector3f vector3f41 = vector3f25;
					vector3f41.z += vector3f7.z;
				}
				else if (j == 3) {
					final Vector3f vector3f42 = vector3f25;
					vector3f42.y += vector3f7.y;
					final Vector3f vector3f43 = vector3f25;
					vector3f43.z += vector3f7.z;
				}
				else if (j == 5) {
					final Vector3f vector3f44 = vector3f25;
					vector3f44.x += vector3f7.x;
				}
				else if (j == 6) {
					final Vector3f vector3f45 = vector3f25;
					vector3f45.y += vector3f7.y;
				}
				else if (j == 7) {
					final Vector3f vector3f46 = vector3f25;
					vector3f46.x += vector3f7.x;
					final Vector3f vector3f47 = vector3f25;
					vector3f47.y += vector3f7.y;
					final Vector3f vector3f48 = vector3f25;
					vector3f48.z += n * Math.signum(vector3f24.z);
				}
				else if (j == 9) {
					final Vector3f vector3f49 = vector3f25;
					vector3f49.x += vector3f7.x;
				}
				else if (j == 10) {
					final Vector3f vector3f50 = vector3f25;
					vector3f50.z += vector3f7.z;
				}
				else if (j == 11) {
					final Vector3f vector3f51 = vector3f25;
					vector3f51.x += vector3f7.x;
					final Vector3f vector3f52 = vector3f25;
					vector3f52.z += vector3f7.z;
				}
				if (this.drawDebug) {
					GlUtil.printGlErrorCritical();
				}
				final Vector3f vector3f53 = new Vector3f(vector3f25);
				transform3.basis.transform((Tuple3f)vector3f53);
				transform3.origin.add((Tuple3f)vector3f53);
				GlUtil.glPushMatrix();
				GlUtil.glMultMatrix(transform3);
				if (b) {
					vector3f24.scale(0.99993f);
				}
				else {
					vector3f24.scale(1.00003f);
				}
				if (singleBlockDrawer == null) {
					GlUtil.scaleModelview(vector3f24.x, vector3f24.y, vector3f24.z);
				}
				if (this.drawDebug) {
					GlUtil.printGlErrorCritical();
				}
				if (this.drawDebug) {
					GlUtil.printGlErrorCritical();
				}
				if (singleBlockDrawer == null) {
					this.mesh.renderVBO();
				}
				GlUtil.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
				GlUtil.glPopMatrix();
			}
			final Vector3f vector3f54 = vector3f2;
			vector3f54.x += n / 2.0f * Math.signum(vector3f7.x);
			final Vector3f vector3f55 = vector3f2;
			vector3f55.y += n / 2.0f * Math.signum(vector3f7.y);
			final Vector3f vector3f56 = vector3f2;
			vector3f56.z += n / 2.0f * Math.signum(vector3f7.z);
		}
		if (this.drawDebug) {
			GlUtil.printGlErrorCritical();
		}
		final Vector3f vector3f57 = vector3f2;
		vector3f57.x += vector3f7.x / 2.0f - 0.5f * Math.signum(vector3f7.x);
		final Vector3f vector3f58 = vector3f2;
		vector3f58.y += vector3f7.y / 2.0f - 0.5f * Math.signum(vector3f7.y);
		final Vector3f vector3f59 = vector3f2;
		vector3f59.z += vector3f7.z / 2.0f - 0.5f * Math.signum(vector3f7.z);
		final Vector3f vector3f60 = new Vector3f(vector3f2);
		this.t.basis.transform((Tuple3f)vector3f60);
		this.t.origin.add((Tuple3f)vector3f60);
		GlUtil.glPushMatrix();
		GlUtil.glMultMatrix(this.t);
		if (b) {
			vector3f7.scale(0.99993f);
		}
		else {
			vector3f7.scale(1.00003f);
		}
		if (this.drawDebug) {
			GlUtil.printGlErrorCritical();
		}
		if (this.drawDebug) {
			GlUtil.printGlErrorCritical();
		}
		if (singleBlockDrawer != null && b) {
			if (this.drawDebug) {
				GlUtil.printGlErrorCritical();
			}
			final boolean vboLoaded = this.mesh.isVboLoaded();
			if (this.mesh.isVboLoaded()) {
				this.mesh.unloadVBO(true);
			}
			if (this.drawDebug) {
				GlUtil.printGlErrorCritical();
			}
			final short selectedTypeWithSub;
			if (ElementKeyMap.isValidType(selectedTypeWithSub = this.getPlayerIntercationManager().getSelectedTypeWithSub())) {
				singleBlockDrawer.alpha = 0.5f;
				if (ElementKeyMap.getInfo(selectedTypeWithSub).getBlockStyle() != BlockStyle.NORMAL) {
					singleBlockDrawer.setSidedOrientation((byte)0);
					singleBlockDrawer.setShapeOrientation24((byte)this.getPlayerIntercationManager().getBlockOrientation());
				}
				else if (ElementKeyMap.getInfo(selectedTypeWithSub).getIndividualSides() > 3) {
					singleBlockDrawer.setShapeOrientation24((byte)0);
					singleBlockDrawer.setSidedOrientation((byte)this.getPlayerIntercationManager().getBlockOrientation());
				}
				else if (ElementKeyMap.getInfo(selectedTypeWithSub).orientatable) {
					singleBlockDrawer.setShapeOrientation24((byte)0);
					singleBlockDrawer.setSidedOrientation((byte)this.getPlayerIntercationManager().getBlockOrientation());
				}
				else {
					singleBlockDrawer.setShapeOrientation24((byte)0);
					singleBlockDrawer.setSidedOrientation((byte)0);
				}
				if (this.drawDebug) {
					GlUtil.printGlErrorCritical();
				}
				singleBlockDrawer.activateBlinkingOrientation(ElementKeyMap.getInfo(selectedTypeWithSub).isOrientatable());
				if (this.drawDebug) {
					GlUtil.printGlErrorCritical();
				}
				GL11.glCullFace(1029);
				singleBlockDrawer.useSpriteIcons = false;
				singleBlockDrawer.drawType(selectedTypeWithSub, this.t);
				singleBlockDrawer.useSpriteIcons = true;
			}
			if (this.drawDebug) {
				GlUtil.printGlErrorCritical();
			}
			if (vboLoaded) {
				this.mesh.loadVBO(true);
			}
		}
		GlUtil.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GlUtil.glEnable(2896);
		GlUtil.glDisable(3042);
		GL11.glCullFace(1029);
		GlUtil.glDisable(2884);
		GlUtil.glPopMatrix();
		return new Transform(this.t);
	}
	
	private void drawCameraHighlight(final SegmentController segmentController) {
		final BuildToolsManager buildToolsManager;
		if ((buildToolsManager = this.state.getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager().getBuildToolsManager()).isSelectMode()) {
			buildToolsManager.getSelectMode().draw(this.state, segmentController, this.mesh, this.selectionShader);
		}
	}
	
	private void drawCreateDock(final SegmentController segmentController) {
		final BuildToolsManager buildToolsManager;
		if ((buildToolsManager = this.state.getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager().getBuildToolsManager()).isInCreateDockingMode() && buildToolsManager.getBuildToolCreateDocking().docker != null) {
			final VoidUniqueSegmentPiece docker = buildToolsManager.getBuildToolCreateDocking().docker;
			GlUtil.glEnable(3042);
			GlUtil.glDisable(2896);
			GlUtil.glBlendFunc(770, 771);
			GlUtil.glBlendFuncSeparate(770, 771, 1, 771);
			final Transform transform = new Transform((Transform)segmentController.getWorldTransform());
			final Vector3f absolutePos;
			final Vector3f vector3f = absolutePos = docker.getAbsolutePos(new Vector3f());
			vector3f.x -= 16.0f;
			final Vector3f vector3f2 = absolutePos;
			vector3f2.y -= 16.0f;
			final Vector3f vector3f3 = absolutePos;
			vector3f3.z -= 16.0f;
			transform.basis.transform((Tuple3f)absolutePos);
			transform.origin.add((Tuple3f)absolutePos);
			GlUtil.glPushMatrix();
			GlUtil.glMultMatrix(transform);
			if (this.drawer == null) {
				GlUtil.updateShaderVector4f(ShaderLibrary.selectionShader, "selectionColor", 0.1f, 0.3f, 0.9f, 0.65f);
				this.mesh.renderVBO();
			}
			else {
				final boolean vboLoaded = this.mesh.isVboLoaded();
				if (this.mesh.isVboLoaded()) {
					this.mesh.unloadVBO(true);
				}
				this.drawer.alpha = 0.5f;
				this.drawer.setSidedOrientation((byte)0);
				BlockShapeAlgorithm.getLocalAlgoIndex(ElementKeyMap.getInfo(docker.getType()).getBlockStyle(), docker.getOrientation());
				this.drawer.setShapeOrientation24(docker.getOrientation());
				this.drawer.useSpriteIcons = false;
				this.drawer.drawType(buildToolsManager.getBuildToolCreateDocking().docker.getType(), transform);
				this.drawer.useSpriteIcons = true;
				if (vboLoaded) {
					this.mesh.loadVBO(true);
				}
				this.drawer.setActive(false);
			}
			GlUtil.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			GlUtil.glEnable(2896);
			GlUtil.glDisable(3042);
			GlUtil.glPopMatrix();
			VoidUniqueSegmentPiece core;
			if (buildToolsManager.getBuildToolCreateDocking().core == null) {
				(core = new VoidUniqueSegmentPiece()).uniqueIdentifierSegmentController = segmentController.getUniqueIdentifier();
				core.setType((short)1);
				final Vector3f vector3f4 = new Vector3f(Controller.getCamera().getPos());
				final Tuple3f forward;
				((Vector3f)(forward = (Tuple3f)Controller.getCamera().getForward(new Vector3f()))).scale(buildToolsManager.getBuildToolCreateDocking().coreDistance);
				vector3f4.add(forward);
				segmentController.getWorldTransformInverse().transform(vector3f4);
				vector3f4.x = (float)(Math.round(vector3f4.x) + 16);
				vector3f4.y = (float)(Math.round(vector3f4.y) + 16);
				vector3f4.z = (float)(Math.round(vector3f4.z) + 16);
				core.voidPos.set(new Vector3i(vector3f4));
				final SegmentPiece pointUnsave;
				if (((pointUnsave = segmentController.getSegmentBuffer().getPointUnsave(core.voidPos)) != null && ElementKeyMap.isValidType(pointUnsave.getType())) || core.voidPos.equals(buildToolsManager.getBuildToolCreateDocking().docker.voidPos)) {
					buildToolsManager.getBuildToolCreateDocking().potentialCore = null;
				}
				else {
					core.setSegmentController(segmentController);
					buildToolsManager.getBuildToolCreateDocking().potentialCore = core;
				}
			}
			else {
				core = buildToolsManager.getBuildToolCreateDocking().core;
			}
			GlUtil.glEnable(3042);
			GlUtil.glDisable(2896);
			GlUtil.glBlendFunc(770, 771);
			GlUtil.glBlendFuncSeparate(770, 771, 1, 771);
			final Transform transform2 = new Transform((Transform)segmentController.getWorldTransform());
			final Vector3f absolutePos2;
			final Vector3f vector3f5 = absolutePos2 = core.getAbsolutePos(new Vector3f());
			vector3f5.x -= 16.0f;
			final Vector3f vector3f6 = absolutePos2;
			vector3f6.y -= 16.0f;
			final Vector3f vector3f7 = absolutePos2;
			vector3f7.z -= 16.0f;
			transform2.basis.transform((Tuple3f)absolutePos2);
			transform2.origin.add((Tuple3f)absolutePos2);
			GlUtil.glPushMatrix();
			GlUtil.glMultMatrix(transform2);
			ShaderLibrary.selectionShader.setShaderInterface(this.selectionShader);
			ShaderLibrary.selectionShader.load();
			GlUtil.updateShaderVector4f(ShaderLibrary.selectionShader, "selectionColor", 0.1f, 0.9f, 0.6f, 0.65f);
			this.mesh.renderVBO();
			GlUtil.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			GlUtil.glEnable(2896);
			GlUtil.glDisable(3042);
			GlUtil.glPopMatrix();
		}
	}
	
	private Transform drawToBuildConnection(final SegmentController segmentController) {
		if (this.testRayCollisionPoint == null || !this.testRayCollisionPoint.hasHit() || !(this.testRayCollisionPoint instanceof CubeRayCastResult)) {
			return null;
		}
		final CubeRayCastResult testRayCollisionPoint;
		if ((testRayCollisionPoint = this.testRayCollisionPoint).getSegment() == null) {
			return null;
		}
		if (PlayerInteractionControlManager.isAdvancedBuildMode(this.state)) {
			return null;
		}
		this.t.set((Transform)segmentController.getWorldTransform());
		final Vector3f vector3f2;
		final Vector3f vector3f = vector3f2 = new Vector3f((float)testRayCollisionPoint.getSegment().pos.x, (float)testRayCollisionPoint.getSegment().pos.y, (float)testRayCollisionPoint.getSegment().pos.z);
		vector3f.x += testRayCollisionPoint.getCubePos().x - 16;
		final Vector3f vector3f3 = vector3f2;
		vector3f3.y += testRayCollisionPoint.getCubePos().y - 16;
		final Vector3f vector3f4 = vector3f2;
		vector3f4.z += testRayCollisionPoint.getCubePos().z - 16;
		final short selectedTypeWithSub = this.state.getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager().getSelectedTypeWithSub();
		final Vector3f vector3f5 = new Vector3f();
		final SegmentPiece selectedBlock;
		if ((selectedBlock = this.getActiveBuildController().getSelectedBlock()) != null && selectedBlock.getType() != 0 && selectedTypeWithSub != 0) {
			final Vector3i absolutePos = selectedBlock.getAbsolutePos(new Vector3i());
			vector3f5.set((float)(absolutePos.x - 16), (float)(absolutePos.y - 16), (float)(absolutePos.z - 16));
			final Vector3f vector3f6 = new Vector3f(this.testRayCollisionPoint.hitPointWorld);
			segmentController.getWorldTransformInverse().transform(vector3f6);
			new Vector3f();
			final IntOpenHashSet set = new IntOpenHashSet();
			for (int i = 0; i < 6; ++i) {
				final Vector3i vector3i = Element.DIRECTIONSi[i];
				final SegmentPiece pointUnsave;
				if ((pointUnsave = segmentController.getSegmentBuffer().getPointUnsave(new Vector3i(this.toBuildPos.x + vector3i.x, this.toBuildPos.y + vector3i.y, this.toBuildPos.z + vector3i.z))) != null && pointUnsave.getType() != 0) {
					((IntSet)set).add(i);
				}
			}
			final SegmentPiece pointUnsave2;
			if ((pointUnsave2 = segmentController.getSegmentBuffer().getPointUnsave(this.toBuildPos)) != null) {
				switch (Element.getSide(vector3f6, (pointUnsave2 == null) ? null : pointUnsave2.getAlgorithm(), this.pp, (short)((pointUnsave2 != null) ? pointUnsave2.getType() : 0), (pointUnsave2 != null) ? pointUnsave2.getOrientation() : 0, (IntCollection)set)) {
					case 4: {
						final Vector3f vector3f7 = vector3f2;
						++vector3f7.x;
						break;
					}
					case 5: {
						final Vector3f vector3f8 = vector3f2;
						--vector3f8.x;
						break;
					}
					case 2: {
						final Vector3f vector3f9 = vector3f2;
						++vector3f9.y;
						break;
					}
					case 3: {
						final Vector3f vector3f10 = vector3f2;
						--vector3f10.y;
						break;
					}
					case 0: {
						final Vector3f vector3f11 = vector3f2;
						++vector3f11.z;
						break;
					}
					case 1: {
						final Vector3f vector3f12 = vector3f2;
						--vector3f12.z;
						break;
					}
				}
			}
			this.pp.set((int)vector3f2.x, (int)vector3f2.y, (int)vector3f2.z);
			GlUtil.glPushMatrix();
			GlUtil.glMultMatrix(this.t);
			if (this.drawDebug) {
				GlUtil.printGlErrorCritical();
			}
			GlUtil.glDisable(3553);
			GlUtil.glEnable(2903);
			GlUtil.glDisable(2896);
			GlUtil.glEnable(3042);
			GlUtil.glBlendFunc(770, 771);
			GlUtil.glBlendFuncSeparate(770, 771, 1, 771);
			GL11.glLineWidth(4.0f);
			if (ElementKeyMap.isValidType(selectedTypeWithSub) && ElementKeyMap.getInfo(selectedTypeWithSub).getControlledBy().contains(selectedBlock.getType())) {
				GlUtil.glColor4f(0.0f, 0.8f, 0.0f, 1.0f);
			}
			else {
				GlUtil.glColor4f(0.8f, 0.0f, 0.0f, 0.6f);
			}
			GL11.glBegin(1);
			GL11.glVertex3f(vector3f5.x, vector3f5.y, vector3f5.z);
			GL11.glVertex3f(vector3f2.x, vector3f2.y, vector3f2.z);
			GL11.glEnd();
			if (this.drawDebug) {
				GlUtil.printGlErrorCritical();
			}
			GL11.glLineWidth(2.0f);
			GlUtil.glDisable(3042);
			GlUtil.glDisable(2903);
			GlUtil.glEnable(2896);
			GlUtil.glEnable(3553);
			GlUtil.glPopMatrix();
			if (this.drawDebug) {
				GlUtil.printGlErrorCritical();
			}
			return new Transform(this.t);
		}
		return null;
	}
	
	private void endBlockDraw() {
		GlUtil.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GlUtil.glEnable(2896);
		GlUtil.glDisable(2903);
		GlUtil.glDisable(3042);
		GlUtil.glPopMatrix();
	}
	
	public void flagControllerSetChanged() {
	}
	
	public void flagUpdate() {
		this.flagUpdate = true;
	}
	
	public SegmentBuildController getActiveBuildController() {
		if (this.getSegmentControlManager().getSegmentBuildController().isTreeActive()) {
			return this.getSegmentControlManager().getSegmentBuildController();
		}
		if (this.getShipControllerManager().getSegmentBuildController().isTreeActive()) {
			return this.getShipControllerManager().getSegmentBuildController();
		}
		return null;
	}
	
	public PlayerInteractionControlManager getPlayerIntercationManager() {
		return this.state.getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager();
	}
	
	public PlayerExternalController getPlayerManager() {
		return this.getPlayerIntercationManager().getPlayerCharacterManager();
	}
	
	public SegmentControlManager getSegmentControlManager() {
		return this.getPlayerIntercationManager().getSegmentControlManager();
	}
	
	public ShipControllerManager getShipControllerManager() {
		return this.getPlayerIntercationManager().getInShipControlManager().getShipControlManager();
	}
	
	private void prepareBlockDraw(final Transform transform) {
		this.t.set(transform);
		GlUtil.glEnable(3042);
		GlUtil.glBlendFunc(770, 771);
		GlUtil.glBlendFuncSeparate(770, 771, 1, 771);
		GlUtil.glDisable(2896);
		GlUtil.glEnable(2903);
		GlUtil.glColor4f(1.0f, 0.0f, 1.0f, 0.6f);
		GlUtil.glPushMatrix();
		GlUtil.glMultMatrix(transform);
	}
	
	private void textPopups() {
	}
	
	public void update(final Timer timer) {
		HudIndicatorOverlay.toDrawTexts.remove(this.indication);
		if (!this.getSegmentControlManager().getSegmentBuildController().isTreeActive() && !this.getShipControllerManager().getSegmentBuildController().isTreeActive() && !this.getPlayerManager().isActive()) {
			return;
		}
		if (this.state.getCharacter() == null) {
			return;
		}
		if (this.flagUpdate) {
			this.lastSegment = null;
			BuildModeDrawer.currentPiece = null;
			BuildModeDrawer.currentInfo = null;
			this.flagUpdate = false;
		}
		this.colorMod.update(timer);
		this.linearTimer.update(timer);
		this.linearTimerC.update(timer);
		this.linearTimerSl.update(timer);
		try {
			final SegmentBuildController activeBuildController = this.getActiveBuildController();
			final Vector3f vector3f = new Vector3f(Controller.getCamera().getPos());
			if (activeBuildController == null && this.state.getCharacter() == this.state.getCurrentPlayerObject()) {
				vector3f.set((Tuple3f)this.state.getCharacter().getHeadWorldTransform().origin);
			}
			final Vector3f vector3f2;
			if (Float.isNaN((vector3f2 = new Vector3f(Controller.getCamera().getForward())).x)) {
				return;
			}
			if (PlayerInteractionControlManager.isAdvancedBuildMode(this.state)) {
				vector3f2.sub((Tuple3f)new Vector3f(this.state.getWorldDrawer().getAbsoluteMousePosition()), (Tuple3f)vector3f);
			}
			vector3f2.normalize();
			vector3f2.scale((activeBuildController != null) ? 300.0f : 6.0f);
			final Vector3f vector3f3;
			(vector3f3 = new Vector3f(vector3f)).add((Tuple3f)vector3f2);
			final PlayerCharacter character = this.state.getCharacter();
			final SegmentController segmentController;
			if ((segmentController = ((this.state.getCurrentPlayerObject() instanceof SegmentController) ? ((SegmentController)this.state.getCurrentPlayerObject()) : null)) != null) {
				this.testRayCollisionPoint = new CubeRayCastResult(vector3f, vector3f3, character, new SegmentController[] { segmentController });
			}
			else {
				this.testRayCollisionPoint = new CubeRayCastResult(vector3f, vector3f3, character, new SegmentController[0]);
			}
			this.testRayCollisionPoint.setDamageTest(false);
			this.testRayCollisionPoint.setIgnoereNotPhysical(true);
			this.testRayCollisionPoint.setIgnoreDebris(true);
			this.testRayCollisionPoint.setZeroHpPhysical(true);
			this.testRayCollisionPoint.setCheckStabilizerPaths(false);
			this.testRayCollisionPoint.setHasCollidingBlockFilter(false);
			this.testRayCollisionPoint.setCollidingBlocks(null);
			((ModifiedDynamicsWorld)((PhysicsExt)this.state.getPhysics()).getDynamicsWorld()).rayTest(vector3f, vector3f3, (CollisionWorld.RayResultCallback)this.testRayCollisionPoint);
			if (this.testRayCollisionPoint.collisionObject != null && !(this.testRayCollisionPoint.collisionObject instanceof RigidBodySegmentController)) {
				this.testRayCollisionPoint.setSegment(null);
			}
			if (this.testRayCollisionPoint != null && this.testRayCollisionPoint.hasHit() && this.testRayCollisionPoint instanceof CubeRayCastResult) {
				final CubeRayCastResult testRayCollisionPoint;
				if ((testRayCollisionPoint = this.testRayCollisionPoint).collisionObject instanceof PairCachingGhostObjectAlignable) {
					this.currentObject = ((PairCachingGhostObjectAlignable)testRayCollisionPoint.collisionObject).getObj();
					BuildModeDrawer.currentPiece = null;
					BuildModeDrawer.currentInfo = null;
				}
				else if (testRayCollisionPoint.getSegment() != null) {
					if ((testRayCollisionPoint.getSegment() != null && this.lastSegment != null && !testRayCollisionPoint.getSegment().equals(this.lastSegment)) || !testRayCollisionPoint.getCubePos().equals(this.lastCubePos) || BuildModeDrawer.currentPiece == null) {
						this.lastCubePos.set(testRayCollisionPoint.getCubePos());
						this.lastSegment = testRayCollisionPoint.getSegment();
						this.currentObject = this.lastSegment.getSegmentController();
						BuildModeDrawer.currentInfo = ElementKeyMap.getInfo((BuildModeDrawer.currentPiece = new SegmentPiece(this.lastSegment, this.lastCubePos)).getType());
					}
				}
				else {
					BuildModeDrawer.currentPiece = null;
					BuildModeDrawer.currentInfo = null;
					this.currentObject = null;
				}
			}
			else {
				this.currentObject = null;
				BuildModeDrawer.currentPiece = null;
				BuildModeDrawer.currentInfo = null;
			}
			if (BuildModeDrawer.currentInfo != null) {
				if (BuildModeDrawer.currentInfo.isArmor()) {
					this.retrieveArmorInfo(BuildModeDrawer.currentPiece.getSegmentController(), BuildModeDrawer.currentPiece, new Vector3f(vector3f), new Vector3f(vector3f2));
				}
				else {
					BuildModeDrawer.armorValue.reset();
				}
				if (Keyboard.isKeyDown(54) && Controller.getCamera().getCameraOffset() < 1.0f) {
					this.indication.setText(StringTools.format(Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_BUILDMODEDRAWER_0, BuildModeDrawer.currentInfo.getId(), BuildModeDrawer.currentPiece, BuildModeDrawer.currentPiece.getSegmentController().getUniqueIdentifier(), StringTools.formatPointZero(BuildModeDrawer.currentPiece.getSegmentController().railController.calculateRailMassIncludingSelf()), this.touching.toString(), BuildModeDrawer.currentPiece.getSegmentController().isUsingOldPower() ? "[OLD POWER]" : "[NEW POWER]"));
					BuildModeDrawer.currentPiece.getTransform(this.indication.getCurrentTransform());
					HudIndicatorOverlay.toDrawTexts.add(this.indication);
				}
				else if (this.state.getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager().getBuildToolsManager().buildInfo) {
					this.indication.setText(StringTools.format(Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_BUILDMODEDRAWER_1, BuildModeDrawer.currentInfo.getName(), Element.getSideString(BuildModeDrawer.currentPiece.getOrientation()), BuildModeDrawer.currentPiece.getAbsolutePos(new Vector3i()).toString(), BuildModeDrawer.currentPiece.getHitpointsFull(), BuildModeDrawer.currentInfo.getMaxHitPointsFull()));
					BuildModeDrawer.currentPiece.getTransform(this.indication.getCurrentTransform());
					HudIndicatorOverlay.toDrawTexts.add(this.indication);
				}
			}
			else if (this.currentObject != null && this.currentObject instanceof AbstractCharacter && this.currentObject instanceof AICreature) {
				this.indication.setText(StringTools.format(Lng.ORG_SCHEMA_GAME_CLIENT_VIEW_BUILDMODEDRAWER_27, KeyboardMappings.ACTIVATE.getKeyChar(), ((AICreature)this.currentObject).getRealName()));
				this.indication.getCurrentTransform().set((Transform)this.currentObject.getWorldTransformOnClient());
				HudIndicatorOverlay.toDrawTexts.add(this.indication);
			}
			if (BuildModeDrawer.currentPiece == null) {
				BuildModeDrawer.armorValue.reset();
			}
			if (activeBuildController == null) {
				return;
			}
			Label_1214: {
				if (activeBuildController.getSelectedBlock() != null) {
					activeBuildController.getSelectedBlock().refresh();
					if (BuildModeDrawer.selectedBlock != activeBuildController.getSelectedBlock() && activeBuildController.getSelectedBlock().getType() != 0) {
						BuildModeDrawer.selectedInfo = ElementKeyMap.getInfo((BuildModeDrawer.selectedBlock = activeBuildController.getSelectedBlock()).getType());
						break Label_1214;
					}
					if (activeBuildController.getSelectedBlock().getType() != 0) {
						break Label_1214;
					}
				}
				BuildModeDrawer.selectedBlock = null;
				BuildModeDrawer.selectedInfo = null;
			}
			final short type;
			if (BuildModeDrawer.selectedBlock != null && (type = BuildModeDrawer.selectedBlock.getType()) != 0 && BuildModeDrawer.currentInfo != null) {
				if (BuildModeDrawer.selectedInfo != null) {
					try {
						BuildModeDrawer.currentInfo.getControlledBy().contains(type);
					}
					catch (ElementClassNotFoundException ex) {
						ex.printStackTrace();
					}
				}
				BuildModeDrawer.currentInfo.isController();
			}
			if (BuildModeDrawer.selectedInfo != null) {
				final ElementInformation currentInfo = BuildModeDrawer.currentInfo;
				final ElementInformation selectedInfo = BuildModeDrawer.selectedInfo;
			}
			this.textPopups();
		}
		catch (Exception ex3) {
			final Exception ex2 = ex3;
			ex3.printStackTrace();
			System.err.println("[BUILDMODEDRAWER] " + ex2.getClass().getSimpleName() + ": " + ex2.getMessage());
		}
	}

	private void retrieveArmorInfo(final SegmentController segmentController, final SegmentPiece segmentPiece, final Vector3f vector3f, final Vector3f vector3f2) {
		final Vector3f vector3f3 = new Vector3f(vector3f);
		vector3f2.normalize();
		vector3f2.scale(400.0f);
		vector3f3.add((Tuple3f)vector3f2);
		if (this.cPosA.epsilonEquals((Tuple3f)vector3f, 0.1f) && this.cPosB.epsilonEquals((Tuple3f)vector3f3, 4.0f) && this.state.getUpdateTime() - this.lastArmorCheck < 1000L) {
			return;
		}
		this.lastArmorCheck = this.state.getUpdateTime();
		this.cPosA.set((Tuple3f)vector3f);
		this.cPosB.set((Tuple3f)vector3f3);
		this.rayCallbackTraverse.closestHitFraction = 1.0f;
		this.rayCallbackTraverse.collisionObject = null;
		this.rayCallbackTraverse.setSegment(null);
		this.rayCallbackTraverse.rayFromWorld.set((Tuple3f)vector3f);
		this.rayCallbackTraverse.rayToWorld.set((Tuple3f)vector3f3);
		this.rayCallbackTraverse.setFilter(segmentController);
		this.rayCallbackTraverse.setOwner(this.state.getCharacter());
		this.rayCallbackTraverse.setIgnoereNotPhysical(false);
		this.rayCallbackTraverse.setIgnoreDebris(false);
		this.rayCallbackTraverse.setRecordAllBlocks(false);
		this.rayCallbackTraverse.setZeroHpPhysical(false);
		this.rayCallbackTraverse.setDamageTest(true);
		this.rayCallbackTraverse.setCheckStabilizerPaths(false);
		this.rayCallbackTraverse.setSimpleRayTest(true);
		this.pt.armorValue = BuildModeDrawer.armorValue;
		BuildModeDrawer.armorValue.reset();
		((ModifiedDynamicsWorld)segmentController.getPhysics().getDynamicsWorld()).rayTest(vector3f, vector3f3, (CollisionWorld.RayResultCallback)this.rayCallbackTraverse);
		if (BuildModeDrawer.armorValue.typesHit.size() > 0) {
			BuildModeDrawer.armorValue.calculate();
		}
		this.rayCallbackTraverse.collisionObject = null;
		this.rayCallbackTraverse.setSegment(null);
		this.rayCallbackTraverse.setFilter(new SegmentController[0]);
	}
	
	static {
		BuildModeDrawer.inReactorAlignSliderSelectedAxis = -1;
		armorValue = new ArmorValue();
	}
}
