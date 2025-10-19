package com.github.qichensn.key;

import com.github.qichensn.SuperAim;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

public class ModKeyMapping {
    public static KeyMapping AIM_HELP=new KeyMapping(
                "key.super_aim.aim_help",
                KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_T,
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
        }
    }
}
