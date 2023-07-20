package me.flourick.fvt.mixin;

import java.util.List;

import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix4f;
import org.joml.Vector2ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import me.flourick.fvt.utils.IScreen;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;

/**
 * FEATURES: Container Buttons
 * 
 * @author Flourick
 */
@Mixin(Screen.class)
abstract class ScreenMixin implements IScreen
{
	@Shadow
    private List<Drawable> drawables;
	@Shadow
    private List<Element> children;
	@Shadow
    private List<Selectable> selectables;

	@Shadow
    protected ItemRenderer itemRenderer;
	@Shadow
    protected TextRenderer textRenderer;

    @Override
	public <T extends Element & Drawable & Selectable> void FVT_addDrawableSelectableChild(T child)
    {
        // cannot use Invoker because for some reason it cannot find those methods
		this.drawables.add(child);
		this.children.add(child);
		this.selectables.add(child);
    }

    // FIXME This function causes problems but doesnt seem to be in use so just disabled it
	// @Inject(method = "renderTooltipFromComponents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void renderTooltipFromComponents(DrawContext context, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner, CallbackInfo info, int i, int j,
                                             int l, int m, Vector2ic vector2ic, int n, int o, int p, Tessellator tessellator, BufferBuilder bufferBuilder, Matrix4f matrix4f, VertexConsumerProvider.Immediate immediate)
	{
		TooltipComponent tooltipComponent2;
		int r = m;

        context.getMatrices().translate(0.0f, 0.0f, 400.0f);

        for(int q = 0; q < components.size(); ++q) {
            tooltipComponent2 = components.get(q);
            tooltipComponent2.drawText(this.textRenderer, l, r, matrix4f, immediate);
            r += tooltipComponent2.getHeight();
        }

        immediate.draw();
        context.getMatrices().pop();

        r = m;
        for(int q = 0; q < components.size(); ++q) {
            tooltipComponent2 = components.get(q);
            tooltipComponent2.drawItems(this.textRenderer, l, r, context);
            r += tooltipComponent2.getHeight();
        }
        // this.itemRenderer.zOffset = f;

		info.cancel();
	}
}