// 
// Decompiled by Procyon v0.5.36
// 

package org.schema.game.common.controller.elements.missile.dumb;

import org.schema.game.common.data.element.ElementCollection;
import org.schema.common.util.StringTools;
import org.schema.schine.input.KeyboardMappings;
import org.schema.schine.graphicsengine.core.settings.ContextFilter;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.input.InputType;
import org.schema.game.client.view.gui.shiphud.newhud.HudContextHelperContainer;
import org.schema.game.client.view.gui.shiphud.newhud.HudContextHelpManager;
import org.schema.game.common.data.player.ControllerStateUnit;
import org.schema.game.common.controller.damage.effects.MetaWeaponEffectInterface;
import org.schema.game.common.controller.elements.combination.MissileCombiSettings;
import org.schema.game.common.controller.damage.DamageDealerType;
import org.schema.schine.common.language.Lng;
import org.schema.game.client.view.gui.structurecontrol.GUIKeyValueEntry;
import java.util.Iterator;
import org.schema.game.common.controller.elements.effectblock.EffectCollectionManager;
import org.schema.game.common.controller.elements.ControlBlockElementCollectionManager;
import org.schema.game.common.controller.elements.combination.modifier.MissileUnitModifier;
import org.schema.game.common.controller.elements.ElementCollectionManager;
import org.schema.game.client.data.GameClientState;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.controller.damage.effects.InterEffectSet;
import org.schema.game.common.controller.elements.weapon.ZoomableUsableModule;
import org.schema.game.common.controller.elements.EffectChangeHanlder;
import org.schema.game.common.controller.damage.DamageDealer;
import org.schema.game.common.controller.PlayerUsableInterface;
import org.schema.game.common.controller.elements.missile.MissileCollectionManager;

public class DumbMissileCollectionManager extends MissileCollectionManager<DumbMissileUnit, DumbMissileCollectionManager, DumbMissileElementManager> implements PlayerUsableInterface, DamageDealer, EffectChangeHanlder, ZoomableUsableModule
{
	private float speedMax;
	private float distMax;
	private InterEffectSet effectConfiguration;
	
	public DumbMissileCollectionManager(final SegmentPiece segmentPiece, final SegmentController segmentController, final DumbMissileElementManager dumbMissileElementManager) {
		super(segmentPiece, (short)32, segmentController, dumbMissileElementManager);
		this.effectConfiguration = new InterEffectSet(DumbMissileElementManager.basicEffectConfiguration);
	}
	
	@Override
	public int getMargin() {
		return 0;
	}
	
	@Override
	protected Class<DumbMissileUnit> getType() {
		return DumbMissileUnit.class;
	}
	
	@Override
	public boolean needsUpdate() {
		return false;
	}
	
	@Override
	public DumbMissileUnit getInstance() {
		return new DumbMissileUnit();
	}
	
	@Override
	public void onChangedCollection() {
		super.onChangedCollection();
		this.updateInterEffects(DumbMissileElementManager.basicEffectConfiguration, this.effectConfiguration);
		if (!this.getSegmentController().isOnServer()) {
			((GameClientState)this.getSegmentController().getState()).getWorldDrawer().getGuiDrawer().managerChanged(this);
		}
		this.speedMax = 0.0f;
		this.distMax = 0.0f;
		final ControlBlockElementCollectionManager<?, ?, ?> supportCollectionManager = this.getSupportCollectionManager();
		final EffectCollectionManager<?, ?, ?> effectCollectionManager = this.getEffectCollectionManager();
		for (final DumbMissileUnit dumbMissileUnit : this.getElementCollections()) {
			if (supportCollectionManager != null) {
				final MissileUnitModifier missileUnitModifier = (MissileUnitModifier)this.getElementManager().getAddOn().getGUI(this, dumbMissileUnit, supportCollectionManager, effectCollectionManager);
				this.speedMax = Math.max(this.speedMax, missileUnitModifier.outputSpeed);
				this.distMax = Math.max(this.distMax, missileUnitModifier.outputDistance);
			}
			else {
				this.speedMax = Math.max(this.speedMax, dumbMissileUnit.getSpeed());
				this.distMax = Math.max(this.distMax, dumbMissileUnit.getDistance());
			}
		}
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
	public GUIKeyValueEntry[] getGUICollectionStats() {
		return new GUIKeyValueEntry[0];
	}
	
	@Override
	public String getModuleName() {
		return Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_MISSILE_DUMB_DUMBMISSILECOLLECTIONMANAGER_0;
	}
	
	@Override
	public DamageDealerType getDamageDealerType() {
		return DamageDealerType.MISSILE;
	}
	
	@Override
	public InterEffectSet getAttackEffectSet() {
		return this.effectConfiguration;
	}
	
	public MissileCombiSettings getWeaponChargeParams() {
		this.getElementManager().getCombiSettings().lockOnTime = this.getLockOnTimeRaw();
		this.getElementManager().getCombiSettings().possibleZoom = this.getPossibleZoomRaw();
		final ControlBlockElementCollectionManager<?, ?, ?> supportCollectionManager;
		if ((supportCollectionManager = this.getSupportCollectionManager()) != null) {
			this.getElementManager().getAddOn().calcCombiSettings(this.getElementManager().getCombiSettings(), this, supportCollectionManager, this.getEffectCollectionManager());
		}
		return this.getElementManager().getCombiSettings();
	}
	
	@Override
	public MetaWeaponEffectInterface getMetaWeaponEffect() {
		return null;
	}
	
	public float getPossibleZoomRaw() {
		return DumbMissileElementManager.POSSIBLE_ZOOM;
	}
	
	@Override
	public void addHudConext(final ControllerStateUnit controllerStateUnit, final HudContextHelpManager hudContextHelpManager, final HudContextHelperContainer.Hos hos) {
		hudContextHelpManager.addHelper(InputType.MOUSE, MouseEvent.ShootButton.PRIMARY_FIRE.getButton(), "Fire Missile", hos, ContextFilter.IMPORTANT);
		if (this.getPossibleZoom() > 1.0f) {
			hudContextHelpManager.addHelper(InputType.MOUSE, MouseEvent.ShootButton.SECONDARY_FIRE.getButton(), Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_MISSILE_DUMB_DUMBMISSILECOLLECTIONMANAGER_1, hos, ContextFilter.IMPORTANT);
		}
		hudContextHelpManager.addHelper(InputType.KEYBOARD, KeyboardMappings.SWITCH_FIRE_MODE.getMapping(), StringTools.format(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_MISSILE_DUMB_DUMBMISSILECOLLECTIONMANAGER_2, this.getFireMode().getName()), hos, ContextFilter.CRUCIAL);
	}
	
	public float getLockOnTime() {
		return this.getWeaponChargeParams().lockOnTime;
	}
	
	public float getLockOnTimeRaw() {
		return DumbMissileElementManager.LOCK_ON_TIME_SEC;
	}
	
	@Override
	public float getPossibleZoom() {
		return this.getWeaponChargeParams().possibleZoom;
	}
}
