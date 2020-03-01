// 
// Decompiled by Procyon v0.5.36
// 

package org.schema.game.common.controller.elements.weapon;

import org.schema.game.common.data.element.ElementCollection;
import org.schema.common.util.StringTools;
import org.schema.schine.input.KeyboardMappings;
import org.schema.schine.graphicsengine.core.settings.ContextFilter;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.input.InputType;
import org.schema.game.client.view.gui.shiphud.newhud.HudContextHelperContainer;
import org.schema.game.client.view.gui.shiphud.newhud.HudContextHelpManager;
import org.schema.game.common.data.player.ControllerStateUnit;
import org.schema.game.server.ai.AIFireState;
import org.schema.game.common.controller.damage.acid.AcidDamageFormula;
import org.schema.game.common.controller.damage.effects.MetaWeaponEffectInterface;
import org.schema.game.common.controller.damage.DamageDealerType;
import org.schema.schine.common.language.Lng;
import org.schema.game.common.controller.elements.effectblock.EffectCollectionManager;
import org.schema.game.common.controller.elements.combination.modifier.WeaponUnitModifier;
import org.schema.game.common.controller.elements.ElementCollectionManager;
import org.schema.game.client.data.GameClientState;
import java.util.Iterator;
import org.schema.game.common.controller.elements.combination.WeaponCombiSettings;
import org.schema.schine.graphicsengine.core.Timer;
import org.schema.game.common.data.player.ControllerStateInterface;
import org.schema.game.common.controller.ai.Types;
import org.schema.game.common.controller.ai.AIGameConfiguration;
import org.schema.game.common.controller.ai.SegmentControllerAIInterface;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.controller.damage.effects.InterEffectSet;
import org.schema.game.common.controller.elements.FocusableUsableModule;
import org.schema.game.common.controller.elements.EffectChangeHanlder;
import org.schema.game.common.controller.damage.projectile.ProjectileDamageDealer;
import org.schema.game.common.controller.PlayerUsableInterface;
import org.schema.game.common.controller.elements.ControlBlockElementCollectionManager;

public class WeaponCollectionManager extends ControlBlockElementCollectionManager<WeaponUnit, WeaponCollectionManager, WeaponElementManager> implements PlayerUsableInterface, ProjectileDamageDealer, EffectChangeHanlder, FocusableUsableModule, ZoomableUsableModule
{
	public float damageCharge;
	public float currentDamageMult;
	private float speedMax;
	private float distMax;
	private FireMode mode;
	private InterEffectSet effectConfiguration;
	public float damageProduced;
	
	public WeaponCollectionManager(final SegmentPiece segmentPiece, final SegmentController segmentController, final WeaponElementManager weaponElementManager) {
		super(segmentPiece, (short)16, segmentController, weaponElementManager);
		this.currentDamageMult = 1.0f;
		this.mode = FireMode.getDefault(this.getClass());
		this.effectConfiguration = new InterEffectSet(WeaponElementManager.basicEffectConfiguration);
	}
	
	@Override
	public int getMargin() {
		return 0;
	}
	
	public float getDamageChargeMax() {
		return WeaponElementManager.DAMAGE_CHARGE_MAX;
	}
	
	public float getDamageChargeSpeed() {
		return WeaponElementManager.DAMAGE_CHARGE_SPEED;
	}
	
	@Override
	protected Class<WeaponUnit> getType() {
		return WeaponUnit.class;
	}
	
	@Override
	public boolean needsUpdate() {
		return false;
	}
	
	@Override
	public WeaponUnit getInstance() {
		return new WeaponUnit();
	}
	
	@Override
	public boolean isInFocusMode() {
		return this.mode == FireMode.FOCUSED;
	}
	
	@Override
	public void setFireMode(final FireMode mode) {
		this.mode = mode;
	}
	
	@Override
	public FireMode getFireMode() {
		return this.mode;
	}
	
	@Override
	public boolean isAllowedVolley() {
		return true;
	}
	
	@Override
	public boolean isVolleyShot() {
		return (this.getSegmentController().isAIControlled() && this.getSegmentController() instanceof SegmentControllerAIInterface && ((SegmentControllerAIInterface)this.getSegmentController()).getAiConfiguration().isActiveAI() && ((AIGameConfiguration)((SegmentControllerAIInterface)this.getSegmentController()).getAiConfiguration()).get(Types.FIRE_MODE).getCurrentState().equals("Volley")) || this.mode == FireMode.VOLLEY;
	}
	
	@Override
	public void onSwitched(final boolean b) {
		super.onSwitched(b);
		this.damageCharge = 0.0f;
	}
	
	@Override
	public boolean canUseCollection(final ControllerStateInterface controllerStateInterface, final Timer timer) {
		final WeaponCombiSettings weaponChargeParams = this.getWeaponChargeParams();
		if (controllerStateInterface.isPrimaryShootButtonDown() && weaponChargeParams.damageChargeMax > 0.0f) {
			boolean b = false;
			final Iterator<WeaponUnit> iterator = this.getElementCollections().iterator();
			while (iterator.hasNext()) {
				if (!iterator.next().canUse(timer.currentTime, false)) {
					b = true;
					break;
				}
			}
			if (!b && this.damageCharge < weaponChargeParams.damageChargeMax) {
				this.damageCharge = Math.min(weaponChargeParams.damageChargeMax, this.damageCharge + timer.getDelta() * weaponChargeParams.damageChargeSpeed);
			}
			return false;
		}
		return super.canUseCollection(controllerStateInterface, timer);
	}
	
	public void onNotShootingButtonDown(final ControllerStateInterface controllerStateInterface, final Timer timer) {
		super.onNotShootingButtonDown(controllerStateInterface, timer);
		if (this.damageCharge > 0.0f) {
			this.currentDamageMult = this.damageCharge;
			this.handleControlShot(controllerStateInterface, timer);
			this.damageCharge = 0.0f;
			this.currentDamageMult = 1.0f;
		}
	}
	
	@Override
	public void onChangedCollection() {
		super.onChangedCollection();
		this.updateInterEffects(WeaponElementManager.basicEffectConfiguration, this.effectConfiguration);
		if (!this.getSegmentController().isOnServer()) {
			((GameClientState)this.getSegmentController().getState()).getWorldDrawer().getGuiDrawer().managerChanged(this);
		}
		this.speedMax = 0.0f;
		this.distMax = 0.0f;
		final ControlBlockElementCollectionManager<?, ?, ?> supportCollectionManager = this.getSupportCollectionManager();
		final EffectCollectionManager<?, ?, ?> effectCollectionManager = this.getEffectCollectionManager();
		for (final WeaponUnit weaponUnit : this.getElementCollections()) {
			if (supportCollectionManager != null) {
				final WeaponUnitModifier weaponUnitModifier = (WeaponUnitModifier)this.getElementManager().getAddOn().getGUI(this, weaponUnit, supportCollectionManager, effectCollectionManager);
				this.speedMax = Math.max(this.speedMax, weaponUnitModifier.outputSpeed);
				this.distMax = Math.max(this.distMax, weaponUnitModifier.outputDistance);
			}
			else {
				this.speedMax = Math.max(this.speedMax, weaponUnit.getSpeed());
				this.distMax = Math.max(this.distMax, weaponUnit.getDistance());
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
	public String getModuleName() {
		return Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_WEAPON_WEAPONCOLLECTIONMANAGER_2;
	}
	
	@Override
	public DamageDealerType getDamageDealerType() {
		return DamageDealerType.PROJECTILE;
	}
	
	@Override
	public InterEffectSet getAttackEffectSet() {
		return this.effectConfiguration;
	}
	
	@Override
	public void handleControlShot(final ControllerStateInterface controllerStateInterface, final Timer timer) {
		this.damageProduced = 0.0f;
		super.handleControlShot(controllerStateInterface, timer);
		final WeaponCombiSettings weaponChargeParams;
		if (this.damageProduced > 0.0f && ((weaponChargeParams = this.getWeaponChargeParams()).cursorRecoilX > 0.0f || weaponChargeParams.cursorRecoilY > 0.0f)) {
			this.getElementManager().handleCursorRecoil(this, this.damageProduced, weaponChargeParams);
		}
	}
	
	@Override
	public MetaWeaponEffectInterface getMetaWeaponEffect() {
		return null;
	}
	
	public WeaponCombiSettings getWeaponChargeParams() {
		this.getElementManager().getCombiSettings().acidType = this.getAcidFormula();
		this.getElementManager().getCombiSettings().possibleZoom = this.getPossibleZoomRaw();
		this.getElementManager().getCombiSettings().damageChargeMax = this.getDamageChargeMax();
		this.getElementManager().getCombiSettings().damageChargeSpeed = this.getDamageChargeSpeed();
		this.getElementManager().getCombiSettings().cursorRecoilX = this.getCursorRecoilX();
		this.getElementManager().getCombiSettings().cursorRecoilMinX = this.getCursorRecoilMinX();
		this.getElementManager().getCombiSettings().cursorRecoilMaxX = this.getCursorRecoilMaxX();
		this.getElementManager().getCombiSettings().cursorRecoilDirX = this.getCursorRecoilDirX();
		this.getElementManager().getCombiSettings().cursorRecoilY = this.getCursorRecoilY();
		this.getElementManager().getCombiSettings().cursorRecoilMinY = this.getCursorRecoilMinY();
		this.getElementManager().getCombiSettings().cursorRecoilMaxY = this.getCursorRecoilMaxY();
		this.getElementManager().getCombiSettings().cursorRecoilDirY = this.getCursorRecoilDirY();
		final ControlBlockElementCollectionManager<?, ?, ?> supportCollectionManager;
		if ((supportCollectionManager = this.getSupportCollectionManager()) != null) {
			this.getElementManager().getAddOn().calcCombiSettings(this.getElementManager().getCombiSettings(), this, supportCollectionManager, this.getEffectCollectionManager());
		}
		return this.getElementManager().getCombiSettings();
	}
	
	public float getPossibleZoomRaw() {
		return WeaponElementManager.POSSIBLE_ZOOM;
	}
	
	@Override
	public AcidDamageFormula.AcidFormulaType getAcidFormula() {
		final AcidDamageFormula.AcidFormulaType[] values = AcidDamageFormula.AcidFormulaType.values();
		final ControlBlockElementCollectionManager<?, ?, ?> supportCollectionManager = this.getSupportCollectionManager();
		final int acid_FORMULA_DEFAULT = WeaponElementManager.ACID_FORMULA_DEFAULT;
		if (supportCollectionManager != null) {
			this.getElementManager().getAddOn().calcCombiSettings(this.getElementManager().getCombiSettings(), this, supportCollectionManager, this.getEffectCollectionManager());
			return this.getElementManager().getCombiSettings().acidType;
		}
		assert acid_FORMULA_DEFAULT < values.length && acid_FORMULA_DEFAULT >= 0 : "Invalid Acid formula index: " + acid_FORMULA_DEFAULT;
		return values[acid_FORMULA_DEFAULT];
	}
	
	@Override
	public AIFireState getAiFireState(final ControllerStateInterface controllerStateInterface) {
		float secondsToExecute = 0.0f;
		final WeaponCombiSettings weaponChargeParams;
		if ((weaponChargeParams = this.getWeaponChargeParams()).damageChargeMax > 0.0f && weaponChargeParams.damageChargeSpeed > 0.0f) {
			secondsToExecute = 0.0f + weaponChargeParams.damageChargeMax / weaponChargeParams.damageChargeSpeed;
		}
		if (secondsToExecute > 0.0f) {
			final AIFireState aiFireState;
			(aiFireState = new AIFireState()).secondsToExecute = secondsToExecute;
			aiFireState.timeStarted = this.getState().getUpdateTime();
			return aiFireState;
		}
		return null;
	}
	
	public float getCursorRecoilX() {
		return WeaponElementManager.CURSOR_RECOIL_X;
	}
	
	public float getCursorRecoilMinX() {
		return WeaponElementManager.CURSOR_RECOIL_MIN_X;
	}
	
	public float getCursorRecoilMaxX() {
		return WeaponElementManager.CURSOR_RECOIL_MAX_X;
	}
	
	public float getCursorRecoilDirX() {
		return WeaponElementManager.CURSOR_RECOIL_DIR_X;
	}
	
	public float getCursorRecoilY() {
		return WeaponElementManager.CURSOR_RECOIL_Y;
	}
	
	public float getCursorRecoilMinY() {
		return WeaponElementManager.CURSOR_RECOIL_MIN_Y;
	}
	
	public float getCursorRecoilMaxY() {
		return WeaponElementManager.CURSOR_RECOIL_MAX_Y;
	}
	
	public float getCursorRecoilDirY() {
		return WeaponElementManager.CURSOR_RECOIL_DIR_Y;
	}
	
	@Override
	public void addHudConext(final ControllerStateUnit controllerStateUnit, final HudContextHelpManager hudContextHelpManager, final HudContextHelperContainer.Hos hos) {
		hudContextHelpManager.addHelper(InputType.MOUSE, MouseEvent.ShootButton.PRIMARY_FIRE.getButton(), Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_WEAPON_WEAPONCOLLECTIONMANAGER_3, hos, ContextFilter.IMPORTANT);
		if (this.getPossibleZoom() > 1.0f) {
			hudContextHelpManager.addHelper(InputType.MOUSE, MouseEvent.ShootButton.SECONDARY_FIRE.getButton(), Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_WEAPON_WEAPONCOLLECTIONMANAGER_5, hos, ContextFilter.IMPORTANT);
		}
		hudContextHelpManager.addHelper(InputType.KEYBOARD, KeyboardMappings.SWITCH_FIRE_MODE.getMapping(), StringTools.format(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_WEAPON_WEAPONCOLLECTIONMANAGER_4, this.getFireMode().getName()), hos, ContextFilter.CRUCIAL);
	}
	
	@Override
	public float getPossibleZoom() {
		return this.getWeaponChargeParams().possibleZoom;
	}
}
