package com.github.qichensn.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.qichensn.config.AimConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.client.event.RenderGuiEvent.Pre;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import static com.github.qichensn.client.AimModeAdapter.changeNextMode;
import static com.github.qichensn.client.AimModeAdapter.entityModeCheck;
import static com.github.qichensn.key.ModKeyMapping.AIM_HELP;
import static com.github.qichensn.key.ModKeyMapping.CHANGE_MODE;

/**
 * 瞄准处理器
 * 处理自动瞄准逻辑和目标选择
 */
@EventBusSubscriber
public class AimHandler {
    public static LivingEntity TARGET = null;

    @SubscribeEvent
    public static void aimBot(Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player != null && minecraft.level != null && AIM_HELP.isDown()) {
            double SEARCH_RANGE = AimConfig.getSearchRange();
            double FOV_ANGLE = AimConfig.getFovAngle();
            double DISTANCE_WEIGHT = AimConfig.getDistanceWeight();
            double DELTA = AimConfig.getDelta();
            double SMOOTH_FACTOR = AimConfig.getSmoothFactor();
            boolean AllowWallPenetration = AimConfig.isAllowWallPenetration();
            boolean AllowTargetSwitching = AimConfig.isAllowTargetSwitching();

            SearchTarget(player, SEARCH_RANGE, AllowWallPenetration, FOV_ANGLE, DISTANCE_WEIGHT, AllowTargetSwitching);
            LockTarget(player, (float)SMOOTH_FACTOR, DELTA);
        } else {
            TARGET = null;
        }
    }

    private static void LockTarget(LocalPlayer player, float SMOOTH_FACTOR, double DELTA) {
        if (TARGET != null) {

            Vec3 targetPos = new Vec3(TARGET.getX(), TARGET.getEyeY(), TARGET.getZ());
            Vec3 eyePos = player.getEyePosition(player.getEyeHeight());
            Vec3 direction = targetPos.subtract(eyePos).normalize();
            double distanceXZ = Math.hypot(direction.x, direction.z);
            float targetYaw = (float)Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0F;
            float targetPitch = (float)(-Math.toDegrees(Math.atan2(direction.y, distanceXZ)));
            float currentYaw = player.getYRot();
            float currentPitch = player.getXRot();

            while (targetYaw - currentYaw > 180.0F) {
                targetYaw -= 360.0F;
            }

            while (targetYaw - currentYaw < -180.0F) {
                targetYaw += 360.0F;
            }

            float newYaw = currentYaw + (targetYaw - currentYaw) * SMOOTH_FACTOR;
            float newPitch = currentPitch + (targetPitch - currentPitch) * SMOOTH_FACTOR;
            float y = (float)Mth.lerp(DELTA, player.yRotO, newYaw);
            float x = (float)Mth.lerp(DELTA, player.xRotO, newPitch);
            player.setYRot(y);
            player.setXRot(x);
            player.yRotO = y;
            player.xRotO = x;
        }
    }

    private static void SearchTarget(
            LocalPlayer player, double SEARCH_RANGE, boolean AllowWallPenetration, double FOV_ANGLE, double DISTANCE_WEIGHT, boolean AllowTargetSwitching
    ) {
        if (TARGET == null || AllowTargetSwitching) {
            Vec3 lookVec = player.getLookAngle();
            Vec3 eyePos = player.getEyePosition(player.getEyeHeight());
            AABB searchArea = player.getBoundingBox().inflate(SEARCH_RANGE);
            List<LivingEntity> entities = new ArrayList<>(
                    player.level().getEntitiesOfClass(LivingEntity.class, searchArea, entityx -> entityx != player && entityx.isAlive())
            );
            Map<Double, LivingEntity> livingEntityMap = new HashMap<>();

            for (LivingEntity entity : entities) {
                if(!entityModeCheck(entity))continue;
                if (AllowWallPenetration || canSeeEntity(player, entity)) {
                    Vec3 entityPos = entity.getBoundingBox().getCenter();
                    Vec3 toEntity = entityPos.subtract(eyePos);
                    double distance = toEntity.length();
                    if (!(distance > SEARCH_RANGE)) {
                        double angle = Math.acos(lookVec.dot(toEntity) / (lookVec.length() * toEntity.length()));
                        double angleDeg = Math.toDegrees(angle);
                        if (!(angleDeg > FOV_ANGLE)) {
                            double normalizedAngle = angleDeg / FOV_ANGLE;
                            double normalizedDistance = Math.min(distance / SEARCH_RANGE, 1.0);
                            double score = (1.0 - DISTANCE_WEIGHT) * normalizedAngle + DISTANCE_WEIGHT * normalizedDistance;
                            livingEntityMap.put(score, entity);
                            List<Entry<Double, LivingEntity>> sortedEntries = new ArrayList<>(livingEntityMap.entrySet());
                            if (!sortedEntries.isEmpty()) {
                                sortedEntries.sort(Entry.comparingByKey());
                                TARGET = sortedEntries.get(0).getValue();
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean canSeeEntity(Player player, LivingEntity target) {
        Vec3 playerEyes = player.getEyePosition(1.0F);
        Vec3 targetPos = new Vec3(target.getX(), target.getEyeY(), target.getZ());
        HitResult hitResult = player.level().clip(new ClipContext(playerEyes, targetPos, Block.VISUAL, Fluid.NONE, player));
        return hitResult.getType() == Type.MISS;
    }
}
