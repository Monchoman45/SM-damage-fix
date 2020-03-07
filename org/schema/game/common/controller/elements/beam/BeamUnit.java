// 
// Decompiled by Procyon v0.5.36
// 

package org.schema.game.common.controller.elements.beam;

import org.schema.game.common.controller.elements.ElementCollectionManager;
import javax.vecmath.Tuple3f;
import org.schema.game.common.controller.elements.beam.harvest.SalvageUnit;
import org.schema.game.common.data.element.ShootContainer;
import org.schema.schine.graphicsengine.core.Timer;
import org.schema.game.common.data.player.ControllerStateInterface;
import javax.vecmath.Vector4f;
import org.schema.game.common.controller.damage.DamageDealerType;
import org.schema.game.common.controller.damage.HitType;
import org.schema.game.common.data.element.beam.BeamReloadCallback;
import org.schema.game.common.data.element.CustomOutputUnit;

public abstract class BeamUnit<E extends BeamUnit<E, CM, EM>, CM extends BeamCollectionManager<E, CM, EM>, EM extends BeamElementManager<E, CM, EM>> extends CustomOutputUnit<E, CM, EM> implements BeamReloadCallback
{
	public abstract float getBeamPowerWithoutEffect();
	
	public abstract float getBeamPower();
	
	public abstract float getBaseBeamPower();
	
	@Override
	public abstract float getPowerConsumption();
	
	public boolean isLatchOn() {
		return false;
	}
	
	public abstract HitType getHitType();
	
	@Override
	public float getReloadTimeMs() {
		return this.getCoolDownSec() * 1000.0f;
	}
	
	@Override
	public final boolean isPowerCharging(final long n) {
		return super.isPowerCharging(n) || n - this.elementCollectionManager.lastBeamFired < 300L;
	}
	
	@Override
	public float getInitializationTime() {
		return this.getReloadTimeMs();
	}
	
	public float getMaxEffectiveRange() {
		return 1.0f;
	}
	
	public float getMinEffectiveRange() {
		return 0.0f;
	}
	
	public float getMaxEffectiveValue() {
		return 1.0f;
	}
	
	public float getMinEffectiveValue() {
		return 1.0f;
	}
	
	@Override
	protected DamageDealerType getDamageType() {
		return DamageDealerType.BEAM;
	}
	
	@Override
	public abstract float getDistanceRaw();
	
	public float getTickRate() {
		return this.elementCollectionManager.getElementManager().getTickRate();
	}
	
	public float getCoolDownSec() {
		return this.elementCollectionManager.getElementManager().getCoolDown();
	}
	
	public float getBurstTime() {
		return this.elementCollectionManager.getElementManager().getBurstTime();
	}
	
	public float getInitialTicks() {
		return this.elementCollectionManager.getElementManager().getInitialTicks();
	}
	
	@Override
	public int getEffectBonus() {
		return Math.min(this.size(), (int)(this.size() / (double)this.elementCollectionManager.getTotalSize() * this.elementCollectionManager.getEffectTotal()));
	}
	
	@Override
	public float getExtraConsume() {
		return 1.0f;
	}
	
	@Override
	public float getFiringPower() {
		return this.getBeamPower();
	}
	
	@Override
	public Vector4f getColor() {
		return this.elementCollectionManager.getColor();
	}
	
	@Override
	public void doShot(final ControllerStateInterface controllerStateInterface, final Timer timer, final ShootContainer shootContainer) {
		final boolean b = controllerStateInterface.getPlayerState() != null && controllerStateInterface.getPlayerState().isMouseButtonDown(0) && this.getSegmentController().isClientOwnObject() && this instanceof SalvageUnit;
		controllerStateInterface.getShootingDir(this.getSegmentController(), shootContainer, this.getDistanceFull(), 3000.0f, this.elementCollectionManager.getControllerPos(), this.elementCollectionManager.isInFocusMode(), false);
		if (!this.isAimable()) {
			shootContainer.shootingDirTemp.set((Tuple3f)shootContainer.shootingDirStraightTemp);
		}
		shootContainer.shootingDirTemp.normalize();
		this.elementCollectionManager.getElementManager().doShot((E)this, this.elementCollectionManager, shootContainer, controllerStateInterface.getPlayerState(), controllerStateInterface.getBeamTimeout(), timer, b);
	}
	
	public boolean isFriendlyFire() {
		return false;
	}
	
	public boolean isAimable() {
		return true;
	}
	
	public float getAcidDamagePercentage() {
		return 0.0f;
	}
	
	public boolean isPenetrating() {
		return false;
	}
	
	public boolean isCheckLatchConnection() {
		return false;
	}
}
