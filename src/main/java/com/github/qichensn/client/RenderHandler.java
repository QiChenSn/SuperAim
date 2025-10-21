package com.github.qichensn.client;

import com.github.qichensn.config.AimConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;

import java.util.List;

public class RenderHandler {

    /**
     * Forge 渲染事件处理器
     * 对应 Fabric 的 WorldRenderEvents.END
     */
    public static void onRenderWorld(RenderLevelStageEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        Player player = mc.player;
        Level level = mc.level;
        PoseStack poseStack = event.getPoseStack();
        Camera camera = event.getCamera();
        float partialTick = event.getPartialTick();

        // 1. 计算准星位置
        Vec3 eyePos = player.getEyePosition(partialTick);
        Vec3 viewVec = player.getViewVector(1.0F);
        Vec3 crosshairPos = eyePos.add(viewVec.scale(5.0)); // 视线前方5格

        // 2. AABB 实体检测
        double range = AimConfig.getSearchRange(); // 复用现有配置
        List<LivingEntity> entities = getNearbyEntities(player, level, range);

        // 3. 渲染每个实体的标记
        for (LivingEntity entity : entities) {
            if (entity == player) continue; // 跳过玩家自己

            Vec3 entityPos = entity.position();

            // 颜色编码：玩家=白色，其他实体=黄色
            float red = 1.0F;
            float green = 1.0F;
            float blue = (entity instanceof Player) ? 1.0F : 0.0F;

            // 渲染连线和方框
            renderLine(crosshairPos, entityPos, poseStack, camera, red, green, blue);
            renderSquare(entityPos, poseStack, camera, red, green, blue);
        }
    }

    /**
     * AABB 空间查询 - 获取范围内的实体
     */
    private static List<LivingEntity> getNearbyEntities(Player player, Level level, double range) {
        Vec3 playerPos = player.position();
        AABB searchArea = new AABB(
                playerPos.subtract(range, range, range),
                playerPos.add(range, range, range)
        );

        return level.getEntitiesOfClass(
                LivingEntity.class,
                searchArea,
                entity -> entity != player && entity.isAlive()
        );
    }

    /**
     * 渲染线条 - 从起点到终点
     */
    private static void renderLine(Vec3 start, Vec3 end, PoseStack poseStack,
                                   Camera camera, float red, float green, float blue) {
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();

        // OpenGL 状态设置
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableDepthTest(); // 穿透方块显示
        RenderSystem.lineWidth(2.0F);

        // 矩阵变换
        poseStack.pushPose();
        Vec3 cameraPos = camera.getPosition();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        // 构建顶点数据
        buffer.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

        // 起点
        buffer.vertex(poseStack.last().pose(), (float)start.x, (float)start.y, (float)start.z)
                .color(red, green, blue, 1.0F)
                .endVertex();

        // 终点
        buffer.vertex(poseStack.last().pose(), (float)end.x, (float)end.y, (float)end.z)
                .color(red, green, blue, 1.0F)
                .endVertex();

        // 提交渲染
        tessellator.end();

        // 恢复状态
        poseStack.popPose();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    /**
     * 渲染方框 - 在实体脚底绘制 2x2 正方形
     */
    private static void renderSquare(Vec3 center, PoseStack poseStack,
                                     Camera camera, float red, float green, float blue) {
        double size = 1.0; // 半边长

        // 四个角点
        Vec3[] corners = {
                new Vec3(center.x - size, center.y, center.z - size), // 西北
                new Vec3(center.x + size, center.y, center.z - size), // 东北
                new Vec3(center.x + size, center.y, center.z + size), // 东南
                new Vec3(center.x - size, center.y, center.z + size)  // 西南
        };

        // 绘制4条边
        for (int i = 0; i < 4; i++) {
            Vec3 start = corners[i];
            Vec3 end = corners[(i + 1) % 4];
            renderLine(start, end, poseStack, camera, red, green, blue);
        }
    }
}