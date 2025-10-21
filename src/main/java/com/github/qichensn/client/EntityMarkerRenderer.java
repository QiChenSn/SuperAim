package com.github.qichensn.client;

import com.github.qichensn.config.AimConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;

import java.util.List;

import static com.github.qichensn.client.AimModeAdapter.entityModeCheck;

/**
 * 实体标记渲染器
 * 负责渲染实体的浮动文本标签
 */
public class EntityMarkerRenderer {

    /**
     * 渲染所有实体的浮动文本标签
     */
    public static void renderEntityMarkers(RenderLevelStageEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        Player player = mc.player;
        Level level = mc.level;
        PoseStack poseStack = event.getPoseStack();
        float partialTick = event.getPartialTick();
        double range = AimConfig.getSearchRange();

        // 获取范围内的实体
        Vec3 playerPos = player.position();
        AABB searchArea = new AABB(
                playerPos.subtract(range, range, range),
                playerPos.add(range, range, range)
        );

        List<LivingEntity> entities = level.getEntitiesOfClass(
                LivingEntity.class,
                searchArea,
                entity -> entity != player && entity.isAlive()
        );

        // 为每个实体渲染标签
        for (LivingEntity entity : entities) {
            if(!entityModeCheck(entity))continue;
            renderFloatingText(entity, poseStack, partialTick);
        }
    }

    /**
     * 渲染单个实体的浮动文本
     */
    private static void renderFloatingText(LivingEntity entity, PoseStack poseStack, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();

        // 计算标签位置（实体头顶上方 0.5 格）
        Vec3 labelPos = entity.position().add(0, entity.getBbHeight() + 0.5, 0);

        // 计算玩家与实体之间的距离
        double distance = cameraPos.distanceTo(labelPos);

        // 基于距离动态计算文本缩放比例
        float scale = calculateTextScale(distance);

        // OpenGL 状态设置
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // 矩阵变换序列
        poseStack.pushPose();

        // 平移到 3D 世界位置（相对相机）
        poseStack.translate(
                labelPos.x - cameraPos.x,
                labelPos.y - cameraPos.y,
                labelPos.z - cameraPos.z
        );

        // 广告牌效果：应用相机旋转（文本始终面向玩家）
        poseStack.mulPose(camera.rotation());

        // 动态缩放文本
        poseStack.scale(-scale, -scale, scale);

        // 渲染两行文本
        Font font = mc.font;

        // 构建两个文本组件
        Component nameText = Component.literal(entity.getName().getString());
        Component statsText;

        if (entity instanceof Player) {
            int health = (int) entity.getHealth();
            int armor = entity.getArmorValue();
            statsText = Component.literal(String.format("§cHP: %d §7| §9Armor: %d", health, armor));
        } else {
            int health = (int) entity.getHealth();
            statsText = Component.literal(String.format("§cHP: %d", health));
        }

        // 计算文本宽度
        int nameWidth = font.width(nameText);
        int statsWidth = font.width(statsText);

        MultiBufferSource.BufferSource bufferSource =
                MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());

        // 渲染name（1.5倍大小）
        poseStack.pushPose();
        poseStack.scale(1.5f, 1.5f, 1.5f);
        poseStack.translate(-nameWidth / 2.0f, -10.0f, 0.0f);

        font.drawInBatch(
                nameText,
                0.0F, 0.0F,
                0xFFFFFF,  // 白色
                false,
                poseStack.last().pose(),
                bufferSource,
                Font.DisplayMode.SEE_THROUGH,
                0,
                15728880
        );
        poseStack.popPose();

        poseStack.translate(-statsWidth / 2.0f, 5.0f, 0.0f);

        font.drawInBatch(
                statsText,
                0.0F, 0.0F,
                0xFFFF00,  // 黄色
                false,
                poseStack.last().pose(),
                bufferSource,
                Font.DisplayMode.SEE_THROUGH,
                0,
                15728880
        );

        bufferSource.endBatch();

        // 恢复状态
        poseStack.popPose();
        RenderSystem.enableDepthTest();
    }

    /**
     * 根据距离计算文本缩放比例
     */
    private static float calculateTextScale(double distance) {
        float baseScale = 0.025F;
        float scale = (float) (baseScale * (1.0 + distance * 0.05));
        return Math.max(0.01F, Math.min(scale, 0.1F));
    }
}