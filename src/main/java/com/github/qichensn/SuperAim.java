package com.github.qichensn;

import com.github.qichensn.client.EntityMarkerRenderer;
import com.github.qichensn.client.RenderHandler;
import com.github.qichensn.config.AimConfig;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SuperAim.MODID)
public class SuperAim
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "super_aim";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public SuperAim(FMLJavaModLoadingContext context)
    {
        LOGGER.info("fuckfuckfuckfucxaifaiodhjoadjoasdoa");

        IEventBus modEventBus = context.getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        context.registerConfig(ModConfig.Type.CLIENT, AimConfig.SPEC);


        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }

    // ========== 新增：客户端渲染事件订阅器 ==========
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientRenderEvents {

        /**
         * 渲染实体标记（线条、方框、文本）
         * 对应 Fabric 的 WorldRenderEvents.END
         */
        @SubscribeEvent
        public static void onRenderLevel(RenderLevelStageEvent event) {
            // 在实体渲染之后执行
            if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {

                // 检查是否启用渲染功能（可通过按键或配置控制）
                if (!AimConfig.isEnableEntityRendering()) {
                    return; // 功能未启用，跳过渲染
                }

                // 渲染线框标记
                RenderHandler.onRenderWorld(event);

                // 渲染浮动文本标签
                EntityMarkerRenderer.renderEntityMarkers(event);
            }
        }
    }
}
