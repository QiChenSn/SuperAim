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

        // 3. 批量渲染所有实体的标记
        if (!entities.isEmpty()) {
            renderEntitiesBatch(entities, player, crosshairPos, poseStack, camera);
        }
    }

    /**
     * 批量渲染所有实体标记
     */
    private static void renderEntitiesBatch(List<LivingEntity> entities, Player player,
                                           Vec3 crosshairPos, PoseStack poseStack, Camera camera) {
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

        // 开始构建顶点数据
        buffer.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

        // 为每个实体添加顶点数据
        for (LivingEntity entity : entities) {
            if (entity == player) continue; // 跳过玩家自己

            Vec3 entityPos = entity.position();
            float[] color = getEntityColor(entity);

            // 准星到实体的连线
            addLineVertices(buffer, poseStack, crosshairPos, entityPos, color);

            // 实体的三维立方体框架
            addCubeVertices(buffer, poseStack, entity, color);
        }

        // 一次性提交所有几何体
        tessellator.end();

        // 恢复状态
        poseStack.popPose();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    /**
     * 获取实体颜色 - 玩家=白色，其他实体=黄色
     */
    private static float[] getEntityColor(LivingEntity entity) {
        float red = 1.0F;
        float green = 1.0F;
        float blue = (entity instanceof Player) ? 1.0F : 0.0F;
        return new float[]{red, green, blue};
    }

    /**
     * 添加线条顶点数据
     */
    private static void addLineVertices(BufferBuilder buffer, PoseStack poseStack,
                                        Vec3 start, Vec3 end, float[] color) {
        buffer.vertex(poseStack.last().pose(), (float)start.x, (float)start.y, (float)start.z)
                .color(color[0], color[1], color[2], 1.0F)
                .endVertex();
        buffer.vertex(poseStack.last().pose(), (float)end.x, (float)end.y, (float)end.z)
                .color(color[0], color[1], color[2], 1.0F)
                .endVertex();
    }

    /**
     * 添加立方体顶点数据
     */
    private static void addCubeVertices(BufferBuilder buffer, PoseStack poseStack,
                                       LivingEntity entity, float[] color) {
        // 获取实体的包围盒
        AABB boundingBox = entity.getBoundingBox();

        // 计算包围盒的8个顶点
        Vec3[] corners = {
                new Vec3(boundingBox.minX, boundingBox.minY, boundingBox.minZ), // 0: 最小点
                new Vec3(boundingBox.maxX, boundingBox.minY, boundingBox.minZ), // 1
                new Vec3(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ), // 2
                new Vec3(boundingBox.minX, boundingBox.minY, boundingBox.maxZ), // 3
                new Vec3(boundingBox.minX, boundingBox.maxY, boundingBox.minZ), // 4
                new Vec3(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ), // 5
                new Vec3(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ), // 6: 最大点
                new Vec3(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ)  // 7
        };

        // 绘制底面的4条边
        addLineVertices(buffer, poseStack, corners[0], corners[1], color);
        addLineVertices(buffer, poseStack, corners[1], corners[2], color);
        addLineVertices(buffer, poseStack, corners[2], corners[3], color);
        addLineVertices(buffer, poseStack, corners[3], corners[0], color);

        // 绘制顶面的4条边
        addLineVertices(buffer, poseStack, corners[4], corners[5], color);
        addLineVertices(buffer, poseStack, corners[5], corners[6], color);
        addLineVertices(buffer, poseStack, corners[6], corners[7], color);
        addLineVertices(buffer, poseStack, corners[7], corners[4], color);

        // 绘制连接顶面和底面的4条边
        addLineVertices(buffer, poseStack, corners[0], corners[4], color);
        addLineVertices(buffer, poseStack, corners[1], corners[5], color);
        addLineVertices(buffer, poseStack, corners[2], corners[6], color);
        addLineVertices(buffer, poseStack, corners[3], corners[7], color);
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

    }