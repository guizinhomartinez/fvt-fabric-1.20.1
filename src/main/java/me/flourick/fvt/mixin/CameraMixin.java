package me.flourick.fvt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import me.flourick.fvt.FVT;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;

/**
 * FEATURES: Freecam, Freelook
 * 
 * @author Flourick
 */
@Mixin(Camera.class)
abstract class CameraMixin
{
	@Shadow
	private boolean ready;
	@Shadow
	private BlockView area;
	@Shadow
	private float pitch;
	@Shadow
	private float yaw;
	@Shadow
	private Entity focusedEntity;
	@Shadow
	private boolean thirdPerson;
	@Shadow
	private float cameraY;
	@Shadow
	private float lastCameraY;

	private boolean FVT_preFreecam = true;
	private boolean FVT_preFreelook = true;

	@Shadow
	abstract void setRotation(float yaw, float pitch);

	@Shadow
	abstract void moveBy(double x, double y, double z);

	@Shadow
	abstract double clipToSpace(double desiredCameraDistance);

	@Shadow
	abstract void setPos(double x, double y, double z);

	@Inject(method = "update", at = @At("HEAD"), cancellable = true)
	private void onUpdate(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo info)
	{
		// freecam
		if(FVT.OPTIONS.freecam.getValue()) {
			if(FVT_preFreecam) {
				FVT_preFreecam = false;

				FVT.MC.chunkCullingEnabled = false;

				if(FVT.MC.player.getVehicle() instanceof BoatEntity) {
					((BoatEntity)FVT.MC.player.getVehicle()).setInputs(false, false, false, false);
				}

				FVT.VARS.playerVelocity = FVT.MC.player.getVelocity();

				FVT.VARS.freecamPitch = inverseView ? -FVT.MC.player.getPitch() : FVT.MC.player.getPitch();
				FVT.VARS.freecamYaw = inverseView ? FVT.MC.player.getYaw() + 180.0f : FVT.MC.player.getYaw();

				FVT.VARS.freecamX = FVT.VARS.prevFreecamX = FVT.MC.gameRenderer.getCamera().getPos().getX();
				FVT.VARS.freecamY = FVT.VARS.prevFreecamY = FVT.MC.gameRenderer.getCamera().getPos().getY() + (thirdPerson ? 0.0f : 0.7f);
				FVT.VARS.freecamZ = FVT.VARS.prevFreecamZ = FVT.MC.gameRenderer.getCamera().getPos().getZ();
			}

			this.ready = true;
			this.area = area;
			this.focusedEntity = focusedEntity;
			this.thirdPerson = thirdPerson;

			this.setRotation((float)FVT.VARS.freecamYaw, (float)FVT.VARS.freecamPitch);
			this.setPos(MathHelper.lerp((double)tickDelta, FVT.VARS.prevFreecamX, FVT.VARS.freecamX), MathHelper.lerp((double)tickDelta, FVT.VARS.prevFreecamY, FVT.VARS.freecamY), MathHelper.lerp((double)tickDelta, FVT.VARS.prevFreecamZ, FVT.VARS.freecamZ));

			info.cancel();
			return;
		}
		else if(!FVT_preFreecam) {
			FVT_preFreecam = true;

			FVT.MC.chunkCullingEnabled = true;

			FVT.VARS.freecamForwardSpeed = 0.0f;
			FVT.VARS.freecamUpSpeed = 0.0f;
			FVT.VARS.freecamSideSpeed = 0.0f;
		}

		// freelook
		if(FVT.INSTANCE.isFreelookEnabled() && focusedEntity == FVT.MC.player) {
			if(FVT_preFreelook) {
				FVT_preFreelook = false;

				FVT.VARS.freelookPitch = inverseView ? -FVT.MC.player.getPitch() : FVT.MC.player.getPitch();
				FVT.VARS.freelookYaw = inverseView ? FVT.MC.player.getYaw() + 180.0f : FVT.MC.player.getYaw();
			}

			this.ready = true;
			this.area = area;
			this.focusedEntity = focusedEntity;
			this.thirdPerson = thirdPerson;

			this.setRotation((float)FVT.VARS.freelookYaw, (float)FVT.VARS.freelookPitch);
			this.setPos(MathHelper.lerp((double)tickDelta, focusedEntity.prevX, focusedEntity.getX()), MathHelper.lerp((double)tickDelta, focusedEntity.prevY, focusedEntity.getY()) + (double)MathHelper.lerp(tickDelta, this.lastCameraY, this.cameraY), MathHelper.lerp((double)tickDelta, focusedEntity.prevZ, focusedEntity.getZ()));
			this.moveBy(-this.clipToSpace(4.0), 0.0, 0.0);

			info.cancel();
		}
		else if(!FVT_preFreelook) {
			FVT_preFreelook = true;
		}
	}

	// makes you able to see yourself while in freecam
	@Inject(method = "isThirdPerson", at = @At("HEAD"), cancellable = true)
	private void onIsThirdPerson(CallbackInfoReturnable<Boolean> info)
	{
		if(FVT.OPTIONS.freecam.getValue()) {
			info.setReturnValue(true);
		}
	}
}
