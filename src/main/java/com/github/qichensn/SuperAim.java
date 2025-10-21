package com.github.qichensn;

import com.github.qichensn.client.EntityMarkerRenderer;
import com.github.qichensn.client.RenderHandler;
import com.github.qichensn.config.AimConfig;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import static com.github.qichensn.client.AimModeAdapter.changeNextMode;
import static com.github.qichensn.client.AimModeAdapter.useRenderingOn;
import static com.github.qichensn.key.ModKeyMapping.*;

/**
 * SuperAim Mod 主类
 * 实现实体检测、渲染和瞄准功能
 */
@Mod(SuperAim.MODID)
public class SuperAim {
    public static final String MODID = "super_aim";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SuperAim(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::commonSetup);

        context.registerConfig(ModConfig.Type.CLIENT, AimConfig.SPEC);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }

    /**
     * 客户端渲染事件处理器
     * 处理实体标记的渲染（线条、方框、文本）
     */
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientRenderEvents {

        @SubscribeEvent
        public static void onRenderLevel(RenderLevelStageEvent event) {
            if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES && (TOGGLE_RENDERING.isDown() || useRenderingOn)) {
                // 检查是否启用渲染功能
                if (!AimConfig.isEnableEntityRendering()) {
                    return;
                }

                RenderHandler.onRenderWorld(event);
                EntityMarkerRenderer.renderEntityMarkers(event);
            }
        }

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                while (CHANGE_MODE.consumeClick()) {
                    Minecraft minecraft = Minecraft.getInstance();
                    LocalPlayer player = minecraft.player;
                    if (player == null) return;
                    changeNextMode(player);
                }
                while (SET_RENDERING_ON.consumeClick()) {
                    useRenderingOn = !useRenderingOn;
                    Minecraft minecraft = Minecraft.getInstance();
                    LocalPlayer player = minecraft.player;
                    if (player == null) return;
                    if (useRenderingOn)
                        player.sendSystemMessage(Component.translatable("message.super_aim.use_rendering"));
                    else
                        player.sendSystemMessage(Component.translatable("message.super_aim.stop_rendering"));
                }
            }
        }
    }
}
