package com.github.qichensn.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ModConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.ConfigValue<Integer> SEARCH_RANGE;
    public static final ForgeConfigSpec.DoubleValue FOV_ANGLE;
    public static final ForgeConfigSpec.DoubleValue DISTANCE_WEIGHT;
    public static final ForgeConfigSpec.DoubleValue DELTA;
    public static final ForgeConfigSpec.BooleanValue ALLOW_WALL_PENETRATION;
    public static final ForgeConfigSpec.BooleanValue ALLOW_TARGET_SWITCHING;

    public static final ForgeConfigSpec.DoubleValue SMOOTH_FACTOR;
    public static final ForgeConfigSpec.BooleanValue TARGET_GLOW_ENABLED;

    public static final ForgeConfigSpec SPEC;

    static {
        BUILDER.comment("===========================================")
                .comment("       SuperAim 配置文件")
                .comment("   SuperAim Configuration File")
                .comment("===========================================");

        SEARCH_RANGE = BUILDER
                .comment("检测实体或物品的半径（方块数）。数值越大范围越广，但可能影响性能。")
                .comment("Search radius in blocks for detecting entities or items.")
                .comment("Higher values increase detection range but may impact performance.")
                .defineInRange("searchRange", 128, 1, 256);

        FOV_ANGLE = BUILDER
                .comment("实体的视野锥形角度（度）。数值越小瞄准越精准。")
                .comment("Field of View (FOV) angle in degrees.")
                .comment("Determines the width of the entity's vision cone.")
                .defineInRange("fovAngle", 25.0, 0.0, 180.0);

        DISTANCE_WEIGHT = BUILDER
                .comment("计算目标优先级时的距离权重。数值越高越优先近距离目标。")
                .comment("Weight applied to distance calculations when making decisions.")
                .comment("Higher values prioritize closer targets.")
                .defineInRange("distanceWeight", 0.9, 0.0, 1.0);

        DELTA = BUILDER
                .comment("瞄准速度系数。数值越高锁定越快，但可能显得不自然。")
                .comment("AimBot targeting speed factor.")
                .comment("Higher values result in faster targeting but may appear less natural.")
                .defineInRange("delta", 0.3, 0.01, 1.0);

        ALLOW_WALL_PENETRATION = BUILDER
                .comment("是否允许锁定墙后的目标。禁用时仅能瞄准可见目标。")
                .comment("Whether the aimbot is allowed to lock onto targets behind walls.")
                .comment("If disabled, only visible targets can be locked.")
                .define("allowWallPenetration", false);

        ALLOW_TARGET_SWITCHING = BUILDER
                .comment("是否允许自动切换目标。禁用时将锁定初始目标。")
                .comment("Whether the aimbot is allowed to switch targets dynamically.")
                .comment("If disabled, it will maintain lock on the initial target.")
                .define("allowTargetSwitching", true);

        SMOOTH_FACTOR = BUILDER
                .comment("移动或动画的平滑度。数值越低过渡越流畅，但可能增加延迟。")
                .comment("Smoothing factor for movement or animation.")
                .comment("Lower values result in smoother transitions but may increase lag.")
                .defineInRange("smoothFactor", 0.3, 0.01, 1.0);

        TARGET_GLOW_ENABLED = BUILDER
                .comment("是否使锁定的目标发光。")
                .comment("Whether to enable glowing effect on locked targets.")
                .define("targetGlowEnabled", true);

        SPEC = BUILDER.build();
    }

    // 工具方法：快速获取配置值
    public static int getSearchRange() {
        return SEARCH_RANGE.get();
    }

    public static double getFovAngle() {
        return FOV_ANGLE.get();
    }

    public static double getDistanceWeight() {
        return DISTANCE_WEIGHT.get();
    }

    public static double getDelta() {
        return DELTA.get();
    }

    public static double getSmoothFactor() {
        return SMOOTH_FACTOR.get();
    }

    public static boolean isAllowWallPenetration() {
        return ALLOW_WALL_PENETRATION.get();
    }

    public static boolean isAllowTargetSwitching() {
        return ALLOW_TARGET_SWITCHING.get();
    }

    public static boolean isTargetGlowEnabled() {
        return TARGET_GLOW_ENABLED.get();
    }
}