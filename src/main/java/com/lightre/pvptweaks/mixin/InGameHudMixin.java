package com.lightre.pvptweaks.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "render", at = @At(value = "TAIL"))
    private void renderHealthBars(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        for (LivingEntity entity : client.world.getEntitiesByClass(LivingEntity.class, client.player.getBoundingBox().expand(10), e -> true)) {
            float health = entity.getHealth();
            float maxHealth = entity.getMaxHealth();

            String healthText = String.format("%.1f / %.1f", health, maxHealth);
            Text text = Text.of(healthText);
            Vec3d pos = entity.getPos().add(0, entity.getHeight() + 0.5, 0);

            drawText(client.textRenderer, context, text, pos, client);
        }
    }

    private void drawText(TextRenderer textRenderer, DrawContext context, Text text, Vec3d pos, MinecraftClient client) {
    }

    private void drawText(TextRenderer textRenderer, MatrixStack matrices, Text text, Vec3d pos) {
        matrices.push();
        matrices.translate(pos.x, pos.y, pos.z);
        matrices.scale(-0.025f, -0.025f, 0.025f);

        VertexConsumerProvider.Immediate vertexConsumers = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        textRenderer.draw(text, 0, 0, 0xFFFFFF, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 15728880);
        vertexConsumers.draw();

        matrices.pop();
    }
}
