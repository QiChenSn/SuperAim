package com.github.qichensn.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * SuperAim Mod 配置类
 * 包含所有可配置选项及其默认值
 */
public final class AimConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // 基础瞄准配置
    public static final ForgeConfigSpec.ConfigValue<Integer> SEARCH_RANGE;
    public static final ForgeConfigSpec.DoubleValue FOV_ANGLE;
    public static final ForgeConfigSpec.DoubleValue DISTANCE_WEIGHT;
    public static final ForgeConfigSpec.DoubleValue DELTA;
    public static final ForgeConfigSpec.BooleanValue ALLOW_WALL_PENETRATION;
    public static final ForgeConfigSpec.BooleanValue ALLOW_TARGET_SWITCHING;

    public static final ForgeConfigSpec.DoubleValue SMOOTH_FACTOR;
    public static final ForgeConfigSpec.BooleanValue TARGET_GLOW_ENABLED;

    // 实体渲染配置
    public static final ForgeConfigSpec.BooleanValue ENABLE_ENTITY_RENDERING;
    public static final ForgeConfigSpec.BooleanValue ENABLE_LINE_RENDERING;
    public static final ForgeConfigSpec.BooleanValue ENABLE_SQUARE_RENDERING;
    public static final ForgeConfigSpec.BooleanValue ENABLE_TEXT_RENDERING;
    public static final ForgeConfigSpec.DoubleValue CROSSHAIR_DISTANCE;
    public static final ForgeConfigSpec.DoubleValue SQUARE_SIZE;
    public static final ForgeConfigSpec.DoubleValue TEXT_SCALE;
    public static final ForgeConfigSpec.DoubleValue LINE_WIDTH;

    public static final ForgeConfigSpec SPEC;

    static {
        BUILDER.comment("===========================================")
                .comment("       SuperAim Configuration File")
                .comment("===========================================");

        SEARCH_RANGE = BUILDER
                .comment("Search radius in blocks for detecting entities or items.")
                .comment("Higher values increase detection range but may impact performance.")
                .defineInRange("searchRange", 128, 1, 256);

        FOV_ANGLE = BUILDER
                .comment("Field of View (FOV) angle in degrees.")
                .comment("Determines the width of the entity's vision cone.")
                .defineInRange("fovAngle", 25.0, 0.0, 180.0);

        DISTANCE_WEIGHT = BUILDER
                .comment("Weight applied to distance calculations when making decisions.")
                .comment("Higher values prioritize closer targets.")
                .defineInRange("distanceWeight", 0.2, 0.0, 1.0);

        DELTA = BUILDER
                .comment("AimBot targeting speed factor.")
                .comment("Higher values result in faster targeting but may appear less natural.")
                .defineInRange("delta", 0.3, 0.01, 1.0);

        ALLOW_WALL_PENETRATION = BUILDER
                .comment("Whether the aimbot is allowed to lock onto targets behind walls.")
                .comment("If disabled, only visible targets can be locked.")
                .define("allowWallPenetration", false);

        ALLOW_TARGET_SWITCHING = BUILDER
                .comment("Whether the aimbot is allowed to switch targets dynamically.")
                .comment("If disabled, it will maintain lock on the initial target.")
                .define("allowTargetSwitching", true);

        SMOOTH_FACTOR = BUILDER
                .comment("Smoothing factor for movement or animation.")
                .comment("Lower values result in smoother transitions but may increase lag.")
                .defineInRange("smoothFactor", 0.3, 0.01, 1.0);

        TARGET_GLOW_ENABLED = BUILDER
                .comment("Whether to enable glowing effect on locked targets.")
                .define("targetGlowEnabled", true);

        BUILDER.push("Entity Rendering Settings");

        ENABLE_ENTITY_RENDERING = BUILDER
                .comment("Enable entity rendering functionality.")
                .define("enableEntityRendering", true);

        ENABLE_LINE_RENDERING = BUILDER
                .comment("Enable line rendering.")
                .define("enableLineRendering", true);

        ENABLE_SQUARE_RENDERING = BUILDER
                .comment("Enable square rendering.")
                .define("enableSquareRendering", true);

        ENABLE_TEXT_RENDERING = BUILDER
                .comment("Enable text label rendering.")
                .define("enableTextRendering", true);

        CROSSHAIR_DISTANCE = BUILDER
                .comment("Crosshair distance in blocks.")
                .defineInRange("crosshairDistance", 5.0, 1.0, 20.0);

        SQUARE_SIZE = BUILDER
                .comment("Half side length of the square in blocks.")
                .defineInRange("squareSize", 1.0, 0.5, 5.0);

        TEXT_SCALE = BUILDER
                .comment("Text scale factor.")
                .defineInRange("textScale", 0.025, 0.01, 0.1);

        LINE_WIDTH = BUILDER
                .comment("Line width in pixels.")
                .defineInRange("lineWidth", 2.0, 1.0, 10.0);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    // 基础配置 Getter 方法
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

    // 实体渲染配置 Getter 方法
    public static boolean isEnableEntityRendering() {
        return ENABLE_ENTITY_RENDERING.get();
    }

    public static boolean isEnableLineRendering() {
        return ENABLE_LINE_RENDERING.get();
    }

    public static boolean isEnableSquareRendering() {
        return ENABLE_SQUARE_RENDERING.get();
    }

    public static boolean isEnableTextRendering() {
        return ENABLE_TEXT_RENDERING.get();
    }

    public static double getCrosshairDistance() {
        return CROSSHAIR_DISTANCE.get();
    }

    public static double getSquareSize() {
        return SQUARE_SIZE.get();
    }

    public static double getTextScale() {
        return TEXT_SCALE.get();
    }

    public static double getLineWidth() {
        return LINE_WIDTH.get();
    }
}