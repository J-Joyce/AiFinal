package net.TimeIsWhat.AIFinal;

import com.mojang.logging.LogUtils;
//import net.TimeIsWhat.AIFinal.waves.WaveManager;
import net.TimeIsWhat.AIFinal.waves.WaveManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.nio.file.Path;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(AiFinal.MOD_ID)
public final class AiFinal {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "aifinal";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

   // public static final WaveManager WAVE_MANAGER = new WaveManager();
   public static final WaveManager WAVE_MANAGER = new WaveManager();


    public AiFinal(FMLJavaModLoadingContext context) {
        var modBusGroup = context.getModBusGroup();

        // Register setup
        FMLCommonSetupEvent.getBus(modBusGroup).addListener(this::commonSetup);

        // Creative tab
        BuildCreativeModeTabContentsEvent.BUS.addListener(AiFinal::addCreative);

        // REMOVE THIS FOR NOW — Forge 1.21 config system is different
        // context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        // Data logger
        Path configDir = FMLPaths.CONFIGDIR.get();
        DataLogger.init(configDir);

        // Event handlers
        MinecraftForge.EVENT_BUS.register(new CombatDataEvents());

        // In AiFinal.java or your main mod constructor
//        MinecraftForge.EVENT_BUS.register(WAVE_MANAGER);

//        MinecraftForge.EVENT_BUS.register(new TickHandler());
    }


    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    // Add the example block item to the building blocks tab
    private static void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }
    }


}
