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

        // 构建显示文本
        Component text = buildEntityLabel(entity);

        // 计算玩家与实体之间的距离
        double distance = cameraPos.distanceTo(labelPos);

        // 基于距离动态计算文本缩放比例
        float scale = calculateTextScale(distance);

        // OpenGL 状态设置
        RenderSystem.disableDepthTest(); // 穿透显示
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // 矩阵变换序列
        poseStack.pushPose();

        // 1. 平移到 3D 世界位置（相对相机）
        poseStack.translate(
                labelPos.x - cameraPos.x,
                labelPos.y - cameraPos.y,
                labelPos.z - cameraPos.z
        );

        // 2. 广告牌效果：应用相机旋转（文本始终面向玩家）
        poseStack.mulPose(camera.rotation());

        // 3. 动态缩放文本
        poseStack.scale(-scale, -scale, scale); // 负值翻转坐标系

        // 4. 文本居中对齐
        Font font = mc.font;
        int width = font.width(text);
        poseStack.translate(-width / 2.0F, 0.0F, 0.0F);

        // 5. 渲染文本
        MultiBufferSource.BufferSource bufferSource =
                MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());

        font.drawInBatch(
                text,                           // 文本内容
                0.0F, 0.0F,                    // 起始坐标
                0xFFFF00,                      // 颜色（黄色）
                false,                         // 是否有阴影
                poseStack.last().pose(),       // 变换矩阵
                bufferSource,                  // 缓冲区源
                Font.DisplayMode.SEE_THROUGH,  // 穿透显示模式
                0,                             // 背景颜色（透明）
                15728880                       // 光照值（全亮）
        );

        bufferSource.endBatch();

        // 恢复状态
        poseStack.popPose();
        RenderSystem.enableDepthTest();
    }

    /**
     * 根据距离计算文本缩放比例
     * @param distance 玩家与实体之间的距离
     * @return 文本缩放比例
     */
    private static float calculateTextScale(double distance) {
        // 基础缩放值
        float baseScale = 0.025F;

        // 根据距离调整缩放比例，确保远距离也能看清
        // 近距离时保持相对较小，远距离时适当放大
        float scale = (float) (baseScale * (1.0 + distance * 0.05));

        // 设置合理的缩放范围，避免过小或过大
        return Math.max(0.01F, Math.min(scale, 0.1F));
    }

    /**
     * 构建实体标签文本
     */
    private static Component buildEntityLabel(LivingEntity entity) {
        String name = entity.getName().getString();
        int health = (int) entity.getHealth();
        int armor = entity.getArmorValue();

        if (entity instanceof Player) {
            // 玩家显示更多信息
            return Component.literal(String.format(
                    "§f%s §7| §cHP: %d §7| §9Armor: %d",
                    name, health, armor
            ));
        } else {
            // 其他实体显示基本信息
            return Component.literal(String.format(
                    "§e%s §7| §cHP: %d",
                    name, health
            ));
        }
    }
}