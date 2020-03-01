// 
// Decompiled by Procyon v0.5.36
// 

package org.schema.game.common.controller.elements.beam;

import org.schema.schine.graphicsengine.forms.font.FontLibrary;
import org.schema.game.common.controller.elements.UsableControllableElementManager;
import org.schema.schine.input.InputState;
import org.schema.game.common.controller.elements.combination.CombinationSettings;
import org.schema.game.common.controller.elements.UsableControllableFireingElementManager;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.controller.Ship;
import org.schema.game.common.controller.elements.ElementCollectionManager;
import org.schema.game.common.controller.elements.beam.damageBeam.DamageBeamCollectionManager;
import java.io.IOException;
import org.schema.game.common.data.player.ControllerStateInterface;
import org.schema.game.client.view.gui.structurecontrol.ControllerManagerGUI;
import org.schema.game.common.controller.observer.DrawerObservable;
import org.schema.game.client.data.GameClientState;
import org.schema.game.common.controller.elements.ControlBlockElementCollectionManager;
import org.schema.game.common.controller.elements.ManagerModuleCollection;
import org.schema.schine.input.KeyboardMappings;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;
import org.schema.game.common.controller.elements.combination.Combinable;
import org.schema.game.common.controller.elements.combination.CombinationAddOn;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.schine.graphicsengine.core.Timer;
import org.schema.game.common.data.player.PlayerState;
import java.util.Iterator;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.controller.SegmentController;
import javax.vecmath.Vector4f;
import org.schema.schine.graphicsengine.forms.gui.GUITextOverlay;
import org.schema.game.common.data.element.ShootContainer;
import org.schema.game.common.controller.elements.IntegrityBasedInterface;
import org.schema.game.common.controller.elements.BlockActivationListenerInterface;
import org.schema.game.common.controller.elements.combination.BeamCombiSettings;
import org.schema.game.common.controller.elements.UsableCombinableControllableElementManager;
import org.schema.game.common.controller.elements.beam.damageBeam.DamageBeamElementManager;

public abstract class BeamElementManager<E extends BeamUnit<E, CM, EM>, CM extends BeamCollectionManager<E, CM, EM>, EM extends BeamElementManager<E, CM, EM>> extends UsableCombinableControllableElementManager<E, CM, EM, BeamCombiSettings> implements BlockActivationListenerInterface, IntegrityBasedInterface
{
	public final ShootContainer shootContainer;
	private final BeamCombiSettings combiSettings;
	private static GUITextOverlay chargesText;
	public static final Vector4f chargeColor;
	private final DrawReloadListener drawReloadListener;
	
	public BeamElementManager(final short n, final short n2, final SegmentController segmentController) {
		super(n, n2, segmentController);
		this.shootContainer = new ShootContainer();
		this.combiSettings = new BeamCombiSettings();
		this.drawReloadListener = new DrawReloadListener();
	}
	
	@Override
	public BeamCombiSettings getCombiSettings() {
		return this.combiSettings;
	}
	
	@Override
	public int onActivate(final SegmentPiece segmentPiece, final boolean b, final boolean b2) {
		final long absoluteIndex = segmentPiece.getAbsoluteIndex();
		for (int i = 0; i < this.getCollectionManagers().size(); ++i) {
			final Iterator iterator = ((BeamCollectionManager)this.getCollectionManagers().get(i)).getElementCollections().iterator();
			while (iterator.hasNext()) {
				final BeamUnit beamUnit;
				if ((beamUnit = (BeamUnit)iterator.next()).contains(absoluteIndex)) {
					beamUnit.setMainPiece(segmentPiece, b2);
					if (b2) {
						return 1;
					}
					return 0;
				}
			}
		}
		if (b2) {
			return 1;
		}
		return 0;
	}
	
	public void doShot(final E reloadCallback, final CM cm, final ShootContainer shootContainer, final PlayerState playerState, final float n, final Timer timer, final boolean b) {
		ManagerModuleCollection<?, ?, ?> collection = null;
		if (cm.getEffectConnectedElement() != Long.MIN_VALUE) {
			collection = this.getManagerContainer().getModulesControllerMap().get((short)ElementCollection.getType(cm.getEffectConnectedElement()));
		}
		if (cm.getEffectConnectedElement() != Long.MIN_VALUE) {
			collection = this.getManagerContainer().getModulesControllerMap().get((short)ElementCollection.getType(cm.getEffectConnectedElement()));
			final ControlBlockElementCollectionManager<?, ?, ?> effect;
			if ((effect = CombinationAddOn.getEffect(cm.getEffectConnectedElement(), collection, this.getSegmentController())) != null) {
				cm.setEffectTotal(effect.getTotalSize());
			}
		}
		//#XXX: beam effect module dps fix
		//because this used to not be in an else, the game will completely
		//discard any linked effect modules you have just microseconds before
		//it shoots your beam. the game appears to update the collection manager
		//once per frame, so it'll readd your effect modules pretty quickly,
		//but all that really means is that your effect modules only exist if
		//you aren't currently shooting your beam, which is basically the
		//only time you need your effect modules to exist. what makes this
		//especially fun is that if you inspect your gun in the menu, it'll
		//tell you the correct damage number with effect modules included,
		//but it's essentially just lying.
		//i moved this here because while searching for this bug, i found
		//similar classes that looked like this, but i also tried running with
		//this completely removed and i didn't notice any problems.
		else {
			cm.setEffectTotal(0);
		}
		//#XXX:
		if (this.isCombinable() && cm.getSlaveConnectedElement() != Long.MIN_VALUE) {
			this.handleResponse(this.handleAddOn(this, cm, reloadCallback, this.getManagerContainer().getModulesControllerMap().get((short)ElementCollection.getType(cm.getSlaveConnectedElement())), collection, shootContainer, null, playerState, timer, n), (E)reloadCallback, shootContainer.weapontOutputWorldPos);
			return;
		}
		//#XXX: moved up
		//cm.setEffectTotal(0);
		//#XXX:

		//#XXX: effect relink fix
		//so currently when an entity is loaded, weapons with an effect computer
		//linked only use their basic effect spread, and if you shoot them you'll
		//only get basic effect damage. you can fix this by just unlinking the
		//computer and linking it back up again; the correct values are all there,
		//the game just never put them together. rather than digging into the entity
		//loader to fix this at the source, which sounds very scary and difficult, i
		//just made it recalculate here since it's not an expensive calculation and
		//we're already recalculating effect and slave size anyway.
		//i know this class handles all beams, including salvage, astrotech,
		//activation, etc. that don't have anything to do with damage at all, and
		//that this might be better placed in DamageBeamElementManager, but i'd have
		//to recompile that class too and i'd have to copy this entire function over
		//there and override this one, both of which seem a bit excessive for what
		//i can easily fix here with instanceof. there's probably a better way to
		//do this in general, but it might require restructuring these classes and
		//i'm not really here to do that.
		if(cm instanceof DamageBeamCollectionManager) {
			((DamageBeamCollectionManager)cm).updateAttackEffectSet();
		}
		//#XXX:

		final Vector3f vector3f;
		(vector3f = new Vector3f()).set((Tuple3f)shootContainer.weapontOutputWorldPos);
		shootContainer.shootingDirTemp.scale(reloadCallback.getDistance());
		vector3f.add((Tuple3f)shootContainer.shootingDirTemp);
		final BeamCommand beamCommand;
		(beamCommand = new BeamCommand()).minEffectiveRange = reloadCallback.getMinEffectiveRange();
		beamCommand.minEffectiveValue = reloadCallback.getMinEffectiveValue();
		beamCommand.maxEffectiveRange = reloadCallback.getMaxEffectiveRange();
		beamCommand.maxEffectiveValue = reloadCallback.getMaxEffectiveValue();
		beamCommand.currentTime = timer.currentTime;
		beamCommand.identifier = reloadCallback.getSignificator();
		beamCommand.relativePos.set((float)(reloadCallback.getOutput().x - 16), (float)(reloadCallback.getOutput().y - 16), (float)(reloadCallback.getOutput().z - 16));
		beamCommand.reloadCallback = reloadCallback;
		beamCommand.from.set((Tuple3f)shootContainer.weapontOutputWorldPos);
		beamCommand.to.set((Tuple3f)vector3f);
		beamCommand.playerState = playerState;
		beamCommand.beamTimeout = ((reloadCallback.getBurstTime() > 0.0f) ? reloadCallback.getBurstTime() : n);
		beamCommand.tickRate = reloadCallback.getTickRate();
		beamCommand.beamPower = reloadCallback.getBeamPower();
		beamCommand.cooldownSec = reloadCallback.getCoolDownSec();
		beamCommand.bursttime = reloadCallback.getBurstTime();
		beamCommand.initialTicks = reloadCallback.getInitialTicks();
		beamCommand.powerConsumedByTick = reloadCallback.getPowerConsumption();
		beamCommand.latchOn = reloadCallback.isLatchOn();
		beamCommand.checkLatchConnection = reloadCallback.isCheckLatchConnection();
		beamCommand.hitType = reloadCallback.getHitType();
		beamCommand.powerConsumedExtraByTick = 0.0f;
		beamCommand.railParent = this.getRailHitMultiplierParent();
		beamCommand.railChild = this.getRailHitMultiplierChild();
		if (playerState != null && playerState.isKeyDownOrSticky(KeyboardMappings.WALK)) {
			beamCommand.dontFade = true;
		}
		beamCommand.weaponId = cm.getUsableId();
		beamCommand.controllerPos = cm.getControllerPos();
		beamCommand.firendlyFire = reloadCallback.isFriendlyFire();
		beamCommand.penetrating = reloadCallback.isPenetrating();
		beamCommand.acidDamagePercent = reloadCallback.getAcidDamagePercentage();
		this.handleResponse(cm.getHandler().addBeam(beamCommand), (E)reloadCallback, shootContainer.weapontOutputWorldPos);
	}
	
	public void onAddedCollection(final long n, final CM cm) {
		super.onAddedCollection(n, (CM)cm);
		this.notifyBeamDrawer();
	}
	
	public void notifyBeamDrawer() {
		if (!this.getSegmentController().isOnServer()) {
			((GameClientState)this.getSegmentController().getState()).getWorldDrawer().getBeamDrawerManager().update(this, true, null);
		}
	}
	
	public void onConnectionRemoved(final long n, final CM cm) {
		super.onConnectionRemoved(n, (CM)cm);
		this.notifyBeamDrawer();
	}
	
	public ControllerManagerGUI getGUIUnitValues(final E e, final CM cm, final ControlBlockElementCollectionManager<?, ?, ?> controlBlockElementCollectionManager, final ControlBlockElementCollectionManager<?, ?, ?> controlBlockElementCollectionManager2) {
		return null;
	}
	
	@Override
	public void handle(final ControllerStateInterface controllerStateInterface, final Timer timer) {
		if (!controllerStateInterface.isFlightControllerActive()) {
			return;
		}
		if (this.getCollectionManagers().isEmpty()) {
			return;
		}
		try {
			if (!this.convertDeligateControls(controllerStateInterface, this.shootContainer.controlledFromOrig, this.shootContainer.controlledFrom)) {
				return;
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
			return;
		}
		for (int size = this.getCollectionManagers().size(), i = 0; i < size; ++i) {
			final BeamCollectionManager beamCollectionManager = (BeamCollectionManager)this.getCollectionManagers().get(i);
			final boolean selected = controllerStateInterface.isSelected(beamCollectionManager.getControllerElement(), this.shootContainer.controlledFrom);
			final boolean aiSelected = controllerStateInterface.isAISelected(beamCollectionManager.getControllerElement(), this.shootContainer.controlledFrom, (beamCollectionManager instanceof DamageBeamCollectionManager) ? i : Integer.MIN_VALUE, this.getCollectionManagers().size(), beamCollectionManager);
			if (selected && aiSelected && (this.shootContainer.controlledFromOrig.equals(this.shootContainer.controlledFrom) | this.getControlElementMap().isControlling(this.shootContainer.controlledFromOrig, beamCollectionManager.getControllerPos(), this.controllerId)) && beamCollectionManager.allowedOnServerLimit()) {
				if (this.shootContainer.controlledFromOrig.equals(Ship.core)) {
					controllerStateInterface.getControlledFrom(this.shootContainer.controlledFromOrig);
				}
				beamCollectionManager.handleControlShot(controllerStateInterface, timer);
			}
		}
	}
	
	public abstract float getTickRate();
	
	public abstract float getCoolDown();
	
	public abstract float getBurstTime();
	
	public abstract float getInitialTicks();
	
	public abstract double getRailHitMultiplierParent();
	
	public abstract double getRailHitMultiplierChild();
	
	public boolean handleBeamLatch(final ManagerContainer.ReceivedBeamLatch receivedBeamLatch) {
		final Iterator<CM> iterator = this.getCollectionManagers().iterator();
		while (iterator.hasNext()) {
			final boolean handleBeamLatch;
			if (handleBeamLatch = ((BeamCollectionManager)iterator.next()).handleBeamLatch(receivedBeamLatch)) {
				return handleBeamLatch;
			}
		}
		return false;
	}
	
	@Override
	public void drawReloads(final Vector3i vector3i, final Vector3i vector3i2, final long n) {
		this.handleReload(vector3i, vector3i2, n, this.drawReloadListener);
	}
	
	@Override
	public boolean isUsingRegisteredActivation() {
		return true;
	}
	
	static {
		chargeColor = new Vector4f(0.8f, 0.5f, 0.3f, 0.4f);
	}
	
	public class DrawReloadListener implements ReloadListener
	{
		@Override
		public String onDischarged(final InputState inputState, final Vector3i vector3i, final Vector3i vector3i2, final Vector4f vector4f, final boolean b, final float n) {
			UsableControllableElementManager.drawReload(inputState, vector3i, vector3i2, vector4f, b, n);
			return null;
		}
		
		@Override
		public String onReload(final InputState inputState, final Vector3i vector3i, final Vector3i vector3i2, final Vector4f vector4f, final boolean b, final float n) {
			UsableControllableElementManager.drawReload(inputState, vector3i, vector3i2, vector4f, b, n);
			return null;
		}
		
		@Override
		public String onFull(final InputState inputState, final Vector3i vector3i, final Vector3i vector3i2, final Vector4f vector4f, final boolean b, final float n, final long n2) {
			return null;
		}
		
		@Override
		public void drawForElementCollectionManager(final InputState inputState, final Vector3i vector3i, final Vector3i vector3i2, final Vector4f vector4f, final long n) {
			final BeamCollectionManager beamCollectionManager;
			final BeamCombiSettings weaponChargeParams;
			if ((beamCollectionManager = (BeamCollectionManager)BeamElementManager.this.getCollectionManagersMap().get(n)) != null && (weaponChargeParams = beamCollectionManager.getWeaponChargeParams()).chargeTime > 0.0f && beamCollectionManager.beamCharge > 0.0f) {
				if (BeamElementManager.chargesText == null) {
					BeamElementManager.chargesText = new GUITextOverlay(10, 10, FontLibrary.FontSize.MEDIUM.getFont(), (InputState)BeamElementManager.this.getState());
					BeamElementManager.chargesText.onInit();
				}
				UsableControllableElementManager.drawReload(inputState, vector3i, vector3i2, BeamElementManager.chargeColor, false, Math.min(beamCollectionManager.beamCharge / weaponChargeParams.chargeTime, 0.99999f), true, beamCollectionManager.beamCharge, (int)weaponChargeParams.chargeTime, -1L, BeamElementManager.chargesText);
			}
		}
	}
}
