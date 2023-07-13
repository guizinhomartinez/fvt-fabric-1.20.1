package me.flourick.fvt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import me.flourick.fvt.FVT;
import net.minecraft.entity.player.PlayerInventory;

/**
 * FEATURES: Hotbar Autohide
 * 
 * @author Flourick
 */
@Mixin(PlayerInventory.class)
abstract class PlayerInventoryMixin
{
	@Inject(method = "scrollInHotbar", at = @At("HEAD"))
	private void onScrollInHotbar(CallbackInfo info)
	{
		FVT.VARS.resetHotbarLastInteractionTime();
	}
}
