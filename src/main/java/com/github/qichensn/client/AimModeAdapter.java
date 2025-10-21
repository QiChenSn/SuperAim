package com.github.qichensn.client;

import com.github.qichensn.config.AimMode;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;

/**
 * 瞄准模式适配器
 * 处理实体模式检查和模式切换逻辑
 */
public class AimModeAdapter {
    public static AimMode currentMode = AimMode.NORMAL;
    public static boolean useRenderingOn = false;

    public static boolean entityModeCheck(LivingEntity entity) {
        return switch (currentMode) {
            case NORMAL -> true;
            case MONSTER -> entity instanceof Monster;
            case PLAYER -> entity instanceof Player;
        };
    }

    public static void changeNextMode(Player player) {
        Component msg = switch (currentMode) {
            case NORMAL -> {
                currentMode = AimMode.MONSTER;
                yield Component.translatable("message.super_aim.mode.monster");
            }
            case MONSTER -> {
                currentMode = AimMode.PLAYER;
                yield Component.translatable("message.super_aim.mode.player");
            }
            case PLAYER -> {
                currentMode = AimMode.NORMAL;
                yield Component.translatable("message.super_aim.mode.normal");
            }
            default -> {
                currentMode = AimMode.NORMAL;
                yield Component.translatable("message.super_aim.mode.normal");
            }
        };
        player.sendSystemMessage(msg);
    }
}
