package com.github.qichensn.key;

import com.github.qichensn.SuperAim;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import static com.github.qichensn.client.AimModeAdapter.changeNextMode;

/**
 * 模组按键映射类
 * 定义所有自定义按键绑定
 */
public class ModKeyMapping {
    public static KeyMapping AIM_HELP = new KeyMapping(
                "key.super_aim.aim_help",
                KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_T,
                "key.super_aim.name"
    );

    public static final KeyMapping TOGGLE_RENDERING = new KeyMapping(
            "key.super_aim.toggle_rendering",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "key.super_aim.name"
    );

    public static final KeyMapping SET_RENDERING_ON = new KeyMapping(
            "key.super_aim.set_rendering_on",
            KeyConflictContext.IN_GAME,
            KeyModifier.ALT,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "key.super_aim.name"
    );

    public static final KeyMapping CHANGE_MODE = new KeyMapping(
            "key.super_aim.change_mode",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_L,
            "key.super_aim.name"
    );

    @Mod.EventBusSubscriber(
            modid = SuperAim.MODID,
            value = {Dist.CLIENT},
            bus = Mod.EventBusSubscriber.Bus.MOD
    )
    static class ClientBusEvents{
        @SubscribeEvent
        public static void registerKeyMappingsEvent(RegisterKeyMappingsEvent registerKeyMappingsEvent) {
            registerKeyMappingsEvent.register(AIM_HELP);
            registerKeyMappingsEvent.register(TOGGLE_RENDERING);
            registerKeyMappingsEvent.register(CHANGE_MODE);
            registerKeyMappingsEvent.register(SET_RENDERING_ON);
        }
    }
}
