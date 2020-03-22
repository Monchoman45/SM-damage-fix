// 
// Decompiled by Procyon v0.5.36
// 

package org.schema.game.common.controller.elements.beam.damageBeam;

import org.schema.game.common.data.element.ElementCollection;
import org.schema.game.common.data.element.beam.AbstractBeamHandler;
import org.schema.common.util.StringTools;
import org.schema.schine.input.KeyboardMappings;
import org.schema.schine.graphicsengine.core.settings.ContextFilter;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.input.InputType;
import org.schema.game.client.view.gui.shiphud.newhud.HudContextHelperContainer;
import org.schema.game.client.view.gui.shiphud.newhud.HudContextHelpManager;
import org.schema.game.common.data.player.ControllerStateUnit;
import org.schema.game.common.controller.damage.effects.MetaWeaponEffectInterface;
import org.schema.game.common.controller.elements.combination.BeamCombiSettings;
import org.schema.game.server.ai.AIFireState;
import org.schema.game.common.data.player.ControllerStateInterface;
import org.schema.game.common.controller.damage.DamageDealerType;
import org.schema.schine.common.language.Lng;
import java.util.Iterator;
import org.schema.game.common.controller.elements.effectblock.EffectCollectionManager;
import org.schema.game.common.controller.elements.ControlBlockElementCollectionManager;
import org.schema.game.common.controller.elements.combination.modifier.BeamUnitModifier;
import org.schema.game.common.controller.elements.ElementCollectionManager;
import org.schema.game.client.data.GameClientState;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.controller.damage.effects.InterEffectSet;
import org.schema.game.common.controller.elements.EffectChangeHanlder;
import org.schema.game.common.controller.damage.DamageDealer;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.BeamHandlerContainer;
import org.schema.game.common.controller.elements.beam.BeamCollectionManager;

public class DamageBeamCollectionManager extends BeamCollectionManager<DamageBeamUnit, DamageBeamCollectionManager, DamageBeamElementManager> implements BeamHandlerContainer<SegmentController>, DamageDealer, EffectChangeHanlder
{
	private final DamageBeamHandler handler;
	private float speedMax;
	private float distMax;
	private InterEffectSet effectConfiguration;
	
	public DamageBeamCollectionManager(final SegmentPiece segmentPiece, final SegmentController segmentController, final DamageBeamElementManager damageBeamElementManager) {
		super(segmentPiece, (short)415, segmentController, damageBeamElementManager);
		this.effectConfiguration = new InterEffectSet(DamageBeamElementManager.basicEffectConfiguration);
		this.handler = new DamageBeamHandler(segmentController, this);
	}
	
	@Override
	public DamageBeamHandler getHandler() {
		return this.handler;
	}
	
	@Override
	protected boolean dropShieldsOnCharge() {
		return DamageBeamElementManager.DROP_SHIELDS_ON_CHARGING;
	}
	
	@Override
	public int getMargin() {
		return 0;
	}
	
	@Override
	protected Class<DamageBeamUnit> getType() {
		return DamageBeamUnit.class;
	}
	
	@Override
	public float getPossibleZoomRaw() {
		return DamageBeamElementManager.POSSIBLE_ZOOM;
	}
	
	@Override
	public DamageBeamUnit getInstance() {
		return new DamageBeamUnit();
	}
	
	@Override
	public void onChangedCollection() {
		super.onChangedCollection();
		this.updateInterEffects(DamageBeamElementManager.basicEffectConfiguration, this.effectConfiguration);
		if (!this.getSegmentController().isOnServer()) {
			((GameClientState)this.getSegmentController().getState()).getWorldDrawer().getGuiDrawer().managerChanged(this);
		}
		this.speedMax = 0.0f;
		this.distMax = 0.0f;
		final ControlBlockElementCollectionManager<?, ?, ?> supportCollectionManager = this.getSupportCollectionManager();
		final EffectCollectionManager<?, ?, ?> effectCollectionManager = this.getEffectCollectionManager();
		for (final DamageBeamUnit damageBeamUnit : this.getElementCollections()) {
			if (supportCollectionManager != null) {
				this.distMax = Math.max(this.distMax, ((BeamUnitModifier)this.getElementManager().getAddOn().getGUI(this, damageBeamUnit, supportCollectionManager, effectCollectionManager)).outputDistance);
			}
			else {
				this.distMax = Math.max(this.distMax, damageBeamUnit.getDistance());
			}
		}
	}
	
	@Override
	public float getChargeTime() {
		return DamageBeamElementManager.CHARGE_TIME;
	}
	
	@Override
	public float getWeaponSpeed() {
		return this.speedMax;
	}
	
	@Override
	public float getWeaponDistance() {
		return this.distMax;
	}
	
	@Override
	public String getModuleName() {
		return Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_BEAM_DAMAGEBEAM_DAMAGEBEAMCOLLECTIONMANAGER_0;
	}
	
	@Override
	public DamageDealerType getDamageDealerType() {
		return DamageDealerType.BEAM;
	}
	
	@Override
	public AIFireState getAiFireState(final ControllerStateInterface controllerStateInterface) {
		final BeamCombiSettings weaponChargeParams = this.getWeaponChargeParams();
		final float secondsToExecute;
		if ((secondsToExecute = 0.0f + weaponChargeParams.chargeTime + ((weaponChargeParams.burstTime > 0.0f) ? weaponChargeParams.burstTime : controllerStateInterface.getBeamTimeout())) > 0.0f) {
			final AIFireState aiFireState;
			(aiFireState = new AIFireState()).secondsToExecute = secondsToExecute;
			aiFireState.timeStarted = this.getState().getUpdateTime();
			return aiFireState;
		}
		return null;
	}
	
	@Override
	public InterEffectSet getAttackEffectSet() {
		return this.effectConfiguration;
	}

	//#XXX: effect relink fix
	//this is only here because .updateInterEffects is protected and
	//.effectConfiguration is private, but we need to set both in
	//BeamElementManager
	public void updateAttackEffectSet() {
		this.updateInterEffects(DamageBeamElementManager.basicEffectConfiguration, this.effectConfiguration);
	}
	//#XXX:
	
	@Override
	public MetaWeaponEffectInterface getMetaWeaponEffect() {
		return null;
	}
	
	@Override
	public void addHudConext(final ControllerStateUnit controllerStateUnit, final HudContextHelpManager hudContextHelpManager, final HudContextHelperContainer.Hos hos) {
		hudContextHelpManager.addHelper(InputType.MOUSE, MouseEvent.ShootButton.PRIMARY_FIRE.getButton(), Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_BEAM_DAMAGEBEAM_DAMAGEBEAMCOLLECTIONMANAGER_1, hos, ContextFilter.IMPORTANT);
		if (this.getPossibleZoom() > 1.0f) {
			hudContextHelpManager.addHelper(InputType.MOUSE, MouseEvent.ShootButton.SECONDARY_FIRE.getButton(), Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_BEAM_DAMAGEBEAM_DAMAGEBEAMCOLLECTIONMANAGER_3, hos, ContextFilter.IMPORTANT);
		}
		hudContextHelpManager.addHelper(InputType.KEYBOARD, KeyboardMappings.SWITCH_FIRE_MODE.getMapping(), StringTools.format(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_BEAM_DAMAGEBEAM_DAMAGEBEAMCOLLECTIONMANAGER_2, this.getFireMode().getName()), hos, ContextFilter.CRUCIAL);
	}
}
